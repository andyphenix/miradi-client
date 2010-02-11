/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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

import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.ids.FactorLinkId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.FactorLink;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;

public class LinkDeletor
{
	public LinkDeletor(Project projectToUse)
	{
		project = projectToUse;
	}

	public void deleteDiagramLinkAndOrphandFactorLink(DiagramLink diagramLink) throws Exception
	{
		Vector<DiagramFactor> noDiagramFactorsToBeDeleted = new Vector();
		if (diagramLink.isGroupBoxLink())
			deleteFactorLinksAndGroupBoxDiagramLinks(noDiagramFactorsToBeDeleted, diagramLink);
		else
			deleteDiagramLinkAndOrphandFactorLink(noDiagramFactorsToBeDeleted, diagramLink);
	}
	
	public void deleteFactorLinkAndAllRefferers(FactorLinkId factorLinkId) throws Exception
	{
		FactorLink factorLink = (FactorLink) project.findObject(new ORef(ObjectType.FACTOR_LINK, factorLinkId));
		deleteDiagramLinkAndReferrers(factorLink);
		deleteFactorLinkIfOrphaned(factorLink);
	}
	
	private void deleteDiagramLinkAndReferrers(FactorLink factorLink) throws Exception
	{
		ORefList diagramLinkreferrers = factorLink.findObjectsThatReferToUs(DiagramLink.getObjectType());	
		for (int referrerIndex = 0; referrerIndex < diagramLinkreferrers.size(); ++referrerIndex)
		{
			DiagramLink diagramLink = (DiagramLink) project.findObject(diagramLinkreferrers.get(referrerIndex));
			deleteOurGroupDiagramLinkParents(diagramLink);
			deleteDiagramLink(diagramLink);
		}
	}

	private void deleteOurGroupDiagramLinkParents(DiagramLink diagramLink) throws Exception
	{
		ORefList groupBoxDiagramLinkReferrers = diagramLink.findObjectsThatReferToUs(DiagramLink.getObjectType());
		for (int groupLinkIndex = 0; groupLinkIndex < groupBoxDiagramLinkReferrers.size(); ++groupLinkIndex)
		{
			DiagramLink groupDiagramLink = DiagramLink.find(getProject(), groupBoxDiagramLinkReferrers.get(groupLinkIndex));
			if (groupDiagramLink.alsoLinksOurFromOrTo(diagramLink))
				deleteDiagramLink(groupDiagramLink);
		}
	}
	
	public void deleteDiagramLinkAndOrphandFactorLink(Vector<DiagramFactor> diagramFactorsAboutToBeDeleted, DiagramLink diagramLink) throws Exception
	{
		FactorLink factorLink = diagramLink.getWrappedFactorLink();
		deleteDiagramLink(diagramLink);
		if(factorLink == null)
		{
			EAM.logWarning(HAS_NO_WRAPPED_LINK_MESSAGE);
			return;
		}
		
		deleteFactorLinkIfOrphaned(factorLink);
	}

	public void deleteFactorLinksAndGroupBoxDiagramLinks(Vector<DiagramFactor> diagramFactorsAboutToBeDeleted, DiagramLink diagramLink) throws Exception
	{
		ORefList groupBoxLinkChildRefs = diagramLink.getGroupedDiagramLinkRefs();
		deleteDiagramLink(diagramLink);
		
		for (int i = 0; i < groupBoxLinkChildRefs.size(); ++i)
		{
			DiagramLink childDiagramLink = DiagramLink.find(getProject(), groupBoxLinkChildRefs.get(i));
			deleteDiagramLinkAndOrphandFactorLink(diagramFactorsAboutToBeDeleted, childDiagramLink);
		}
	}

	public void deleteDiagramLinks(ORefList diagramLinkORefs) throws Exception
	{
		for (int i = 0; i < diagramLinkORefs.size(); ++i)
		{
			DiagramLink diagramLink = (DiagramLink) project.findObject(diagramLinkORefs.get(i));
			deleteDiagramLink(diagramLink);
		}
	}
	
	public void deleteDiagramLink(DiagramLink diagramLink) throws Exception
	{
		BaseObject owner = diagramLink.getOwner();
		DiagramObject diagramObject = (DiagramObject) owner;
		
		removeFromGroupBoxDiagramLinkChildren(diagramLink);
		
		CommandSetObjectData removeDiagramFactorLink = CommandSetObjectData.createRemoveIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, diagramLink.getDiagramLinkId());
		project.executeCommand(removeDiagramFactorLink);

		Vector<Command> commandsToDeleteChildrenAndDiagramLink = diagramLink.createCommandsToDeleteChildrenAndObject();
		getProject().executeCommandsWithoutTransaction(commandsToDeleteChildrenAndDiagramLink);
	}

	private void removeFromGroupBoxDiagramLinkChildren(DiagramLink diagramLink) throws Exception
	{
		ORefList diagramBoxDiagramLinkReferrerRefs = diagramLink.findObjectsThatReferToUs(DiagramLink.getObjectType());
		for (int i = 0; i < diagramBoxDiagramLinkReferrerRefs.size(); ++i)
		{
			DiagramLink groupBoxLink = DiagramLink.find(getProject(), diagramBoxDiagramLinkReferrerRefs.get(i));
			CommandSetObjectData removeDiagramLink = CommandSetObjectData.createRemoveORefCommand(groupBoxLink, diagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS, diagramLink.getRef());
			getProject().executeCommand(removeDiagramLink);
		}
	}
	
	private void deleteFactorLinkIfOrphaned(FactorLink factorLink) throws Exception
	{
		ObjectManager objectManager = project.getObjectManager();
		ORefList diagramLinkReferrers = factorLink.findObjectsThatReferToUs(objectManager, DiagramLink.getObjectType(), factorLink.getRef());
		
		if (diagramLinkReferrers.size() != 0)
			return;
		
		Vector<Command> commandsToDeleteChildrenAndFactorLink = factorLink.createCommandsToDeleteChildrenAndObject();
		getProject().executeCommandsWithoutTransaction(commandsToDeleteChildrenAndFactorLink);		
	}

	private Project getProject()
	{
		return project;
	}
	
	private static final String HAS_NO_WRAPPED_LINK_MESSAGE = "DiagramLink has no wrapped link to delete";
	
	private Project project;
}
