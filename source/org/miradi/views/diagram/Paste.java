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

import java.text.ParseException;

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.main.TransferableMiradiList;
import org.miradi.objecthelpers.ORef;
import org.miradi.views.diagram.doers.AbstractPasteDoer;

public class Paste extends AbstractPasteDoer
{
	public void doIt() throws CommandFailedException
	{
		getProject().executeCommand(new CommandBeginTransaction());
		try 
		{	
			TransferableMiradiList list = getTransferableMiradiList();
			if (list == null)
				return;
			
			final String usersChoice = getUsersChoice(list);
			if (usersChoice.equals(CANCEL_BUTTON))
				return;
				
			DiagramPaster diagramPaster = createDiagramPasterBaseOnUserChoice(list, usersChoice);
			if (! diagramPaster.canPaste())
			{
				EAM.notifyDialog(EAM.text("<HTML>These factors cannot be pasted as shared into this diagram.<BR><BR>" +
										  "Contributing Factors and Direct Threats cannot be pasted as shared into a Results Chain.<BR>" +
										  "Intermediate Results and Threat Reduction Results cannot be pasted as shared into a Conceptual Model page.</HTML>"));
				return;
			}
			
			if (pastingBetweenProjectsInDifferentDiagramType(list, diagramPaster))
			{
				EAM.notifyDialog(EAM.text("<HTML>When pasting between projects, can't paste from CM to RC or vice versa</HTML>"));
				return;
			}

			getProject().getDiagramClipboard().incrementPasteCount();
			paste(diagramPaster);
			possiblyNotitfyUserIfDataWasLost(diagramPaster);
		} 
		catch (Exception e) 
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		} 
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}

	private String getUsersChoice(TransferableMiradiList list) throws ParseException
	{
		if (! list.atleastOneFactorExists())
			return AS_COPY_BUTTON;

		if (isPastingInSameDiagramAsCopiedFrom(list))
			return AS_COPY_BUTTON;
		
		if (!isPasteInSameProject(list))
			return AS_COPY_BUTTON;
		
		String[] buttons = {AS_COPY_BUTTON, AS_ALIAS_BUTTON, CANCEL_BUTTON};
		String title = EAM.text("Paste As...");
		String[] body = {EAM.text("Do you want to paste full new copies of the factors, or share the existing factors? " +
								"If you paste new copies, any changes will not affect the originals. " +
								"If you share, any changes will automatically affect all the diagrams.")};
	
		return EAM.choiceDialog(title, body, buttons);
	}

	private boolean isPastingInSameDiagramAsCopiedFrom(TransferableMiradiList list)
	{
		ORef diagramObjecRefCopiedFrom = list.getDiagramObjectRefCopiedFrom();
		ORef diagramObjectRefBeingPastedInto = getDiagramPanel().getDiagramObject().getRef();
		
		boolean pasteInSameDiagram = diagramObjecRefCopiedFrom.equals(diagramObjectRefBeingPastedInto);
		return pasteInSameDiagram && isPasteInSameProject(list);
	}

	private boolean isPasteInSameProject(TransferableMiradiList list)
	{
		return list.getProjectFileName().equals(getProject().getFilename());
	}
	
	private DiagramPaster createDiagramPasterBaseOnUserChoice(TransferableMiradiList list, String usersChoice) throws Exception
	{		
		if (usersChoice.equals(AS_ALIAS_BUTTON))
			return new DiagramAliasPaster(getDiagramPanel(), getDiagramModel(), list);
		
		return new DiagramCopyPaster(getDiagramPanel(), getDiagramModel(), list);
	}

	private void possiblyNotitfyUserIfDataWasLost(DiagramPaster diagramPaster) throws Exception
	{
		if (!diagramPaster.wasAnyDataLost())
			return;
		
		EAM.notifyDialog(EAM.text("Some of the data could not be moved to this project because " +
								  "it refers to other data that only exists in the old project"));
	}
	
	private boolean pastingBetweenProjectsInDifferentDiagramType(TransferableMiradiList list, DiagramPaster diagramPaster)
	{
		if (isPasteInSameProject(list))
			return false;
		
		if (diagramPaster.isPastingInSameDiagramType())
			return false;
		
		return true;
	}

	protected void paste(DiagramPaster diagramPaster) throws Exception
	{
		diagramPaster.pasteFactorsAndLinks(getLocation());
		diagramPaster.wrapExistingLinksForDiagramFactorsInAllDiagramObjects();
	}
	
	private final String AS_COPY_BUTTON = EAM.text("Button|As Copy");
	private final String AS_ALIAS_BUTTON = EAM.text("Button|Shared");
}
