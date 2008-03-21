/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.views.diagram;

import java.util.Vector;

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.EAMGraphCell;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.diagram.cells.LinkCell;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.FactorLink;
import org.miradi.project.FactorDeleteHelper;
import org.miradi.views.ViewDoer;

public class DeleteSelectedItemDoer extends ViewDoer
{
	public boolean isAvailable()
	{
		if(!getProject().isOpen())
			return false;

		if (! isInDiagram())
			return false;
		
		EAMGraphCell[] selected = getDiagramView().getDiagramPanel().getSelectedAndRelatedCells();
		return (selected.length > 0);
	}

	public void doIt() throws CommandFailedException
	{
		EAMGraphCell[] selectedRelatedCells = getDiagramView().getDiagramPanel().getSelectedAndRelatedCells();
		if (! confirmIfReferringLinksBeingDeleted(selectedRelatedCells))
			return;
		
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{			
			deleteSelectedLinks(selectedRelatedCells);
			deleteSelectedFactors(selectedRelatedCells);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}

	private void deleteSelectedFactors(EAMGraphCell[] selectedRelatedCells) throws Exception
	{
		for(int i = 0; i < selectedRelatedCells.length; ++i)
		{
			deleteFactor(selectedRelatedCells[i]);
		}
	}

	private void deleteSelectedLinks(EAMGraphCell[] selectedRelatedCells) throws Exception
	{
		ORefList factorRefsAboutToBeDeleted = extractFactors(selectedRelatedCells);
		for(int i = 0; i < selectedRelatedCells.length; ++i)
		{
			deleteLink(selectedRelatedCells[i], factorRefsAboutToBeDeleted);
		}
	}

	private void deleteFactor(EAMGraphCell cell) throws Exception
	{
		if(!cell.isFactor())
			return;
		
		DiagramModel model = getDiagramView().getDiagramModel();
		FactorCell factorCell = (FactorCell)cell;
		new FactorDeleteHelper(model).deleteFactor(factorCell.getDiagramFactor());
	}

	private void deleteLink(EAMGraphCell cell, ORefList factorRefsAboutToBeDeleted) throws Exception
	{
		if(!cell.isFactorLink())
			return;
		
		LinkDeletor linkDeletor = new LinkDeletor(getProject());
		DiagramLink diagramLink = cell.getDiagramLink();
		DiagramLink found = DiagramLink.find(getProject(), diagramLink.getRef());
		if (found == null)
			return;
		
		if (diagramLink.isGroupBoxLink())
			linkDeletor.deleteFactorLinksAndGroupBoxDiagramLinks(factorRefsAboutToBeDeleted, diagramLink);
		else
			linkDeletor.deleteFactorLinkAndDiagramLink(factorRefsAboutToBeDeleted, diagramLink);
	}	

	private boolean confirmIfReferringLinksBeingDeleted(EAMGraphCell[] selectedRelatedCells)
	{
		Vector<DiagramLink> diagramLinks = extractDiagramLinks(selectedRelatedCells);
		Vector<DiagramLink> diagramLinksWithGroupBoxes = getDiagramLinksAndGroupboxChildrenLinks(diagramLinks);
		Vector diagramNames = getDiagramNamesAffectedByThisDelete(diagramLinksWithGroupBoxes, extractDiagramFactors(selectedRelatedCells));
		if (diagramNames.size() <= 1)
			return true;

		String notifyDiaglogText = LINK_DELETE_NOTIFY_TEXT; 
		for (int i = 0 ; i < diagramNames.size(); ++i)
		{
			notifyDiaglogText += " \n - " + diagramNames.get(i);
		}
		
		return EAM.confirmDeletRetainDialog(new String[]{notifyDiaglogText});
	}

	private Vector getDiagramNamesAffectedByThisDelete(Vector<DiagramLink> diagramLinks, ORefList diagramFactorRefs)
	{	
		Vector diagramNames = new Vector(); 
		for (int i = 0; i < diagramLinks.size(); ++i)
		{
			DiagramLink diagramLink = diagramLinks.get(i);
			FactorLink factorLink = diagramLink.getUnderlyingLink();
			ORef fromDiagramFactorRef =  diagramLink.getFromDiagramFactorRef();
			ORef toDiagramFactorRef = diagramLink.getToDiagramFactorRef();
			ORefList diagramLinkRefs = factorLink.findObjectsThatReferToUs(DiagramLink.getObjectType());
			boolean containsBothFromAndTo = !diagramFactorRefs.contains(fromDiagramFactorRef) && !diagramFactorRefs.contains(toDiagramFactorRef);
			boolean hasMoreThanOneRefferer = diagramLinkRefs.size() > 1;
			if (hasMoreThanOneRefferer && containsBothFromAndTo)
				diagramNames.addAll(getAllDiagramsThatRefer(diagramNames, factorLink));
		}
		
		return diagramNames;
	}
	
	private Vector getAllDiagramsThatRefer(Vector existingDiagramNames, FactorLink factorLink)
	{
		Vector diagramNames = new Vector();
		ORefList diagramRefs = DiagramObject.getDiagramRefsContainingLink(getProject(), factorLink.getRef());
		for (int i = 0; i < diagramRefs.size(); ++i)
		{
			DiagramObject diagramObject = (DiagramObject) getProject().findObject(diagramRefs.get(i));
			String diagramObjectLabel = diagramObject.toString();
			if (existingDiagramNames.contains(diagramObjectLabel))
				continue;
	
			diagramNames.add(diagramObjectLabel);
		}
		
		return diagramNames;
	}

	private ORefList extractFactors(EAMGraphCell[] selectedRelatedCells)
	{
		ORefList factorRefList = new ORefList();
		for (int i = 0; i < selectedRelatedCells.length; ++i)
		{
			EAMGraphCell cell = selectedRelatedCells[i];
			if (!cell.isFactor())
				continue;
			
			FactorCell factorCell = (FactorCell) cell;
			factorRefList.add(factorCell.getWrappedORef());
		}
		
		return factorRefList;
	}
	
	private ORefList extractDiagramFactors(EAMGraphCell[] selectedRelatedCells)
	{
		ORefList diagramFactorRefList = new ORefList();
		for (int i = 0; i < selectedRelatedCells.length; ++i)
		{
			EAMGraphCell cell = selectedRelatedCells[i];
			if (!cell.isFactor())
				continue;
			
			FactorCell factorCell = (FactorCell) cell;
			diagramFactorRefList.add(factorCell.getDiagramFactorRef());
		}
		
		return diagramFactorRefList;
	}
	
	private Vector<DiagramLink> getDiagramLinksAndGroupboxChildrenLinks(Vector<DiagramLink> diagramLinks)
	{
		Vector<DiagramLink> diagramLinksWithPossibleGroupBoxLinks = new Vector();
		for (int i = 0; i < diagramLinks.size(); ++i)
		{
			DiagramLink diagramLink = diagramLinks.get(i);
			ORefList selfOrChildren = diagramLink.getSelfOrChildren();
			if (selfOrChildren.contains(diagramLink.getRef()))
				diagramLinksWithPossibleGroupBoxLinks.add(diagramLink);
			else 
				diagramLinksWithPossibleGroupBoxLinks.addAll(convertToDiagramLinks(selfOrChildren));
		}
		
		return diagramLinksWithPossibleGroupBoxLinks;
	}
	
	private Vector<DiagramLink> convertToDiagramLinks(ORefList diagramLinkRefs)
	{
		Vector<DiagramLink> diagramLinks = new Vector();
		for (int i = 0; i < diagramLinkRefs.size(); ++i)
		{
			diagramLinks.add(DiagramLink.find(getProject(), diagramLinkRefs.get(i)));
		}
		
		return diagramLinks;
	}
	
	private Vector<DiagramLink> extractDiagramLinks(EAMGraphCell[] selectedRelatedCells)
	{
		Vector<DiagramLink> diagramLinks = new Vector();
		for (int i = 0; i < selectedRelatedCells.length; ++i)
		{
			EAMGraphCell cell = selectedRelatedCells[i];
			if (!cell.isFactorLink())
				continue;
			
			LinkCell linkCell = (LinkCell) cell;
			diagramLinks.add(linkCell.getDiagramLink());
		}
		
		return diagramLinks;
	}
	
	public static final String LINK_DELETE_NOTIFY_TEXT = EAM.text("The link(s) will be deleted from all Conceptual Model pages" +
	  															  " and Results Chains, not just this one. ");
}
