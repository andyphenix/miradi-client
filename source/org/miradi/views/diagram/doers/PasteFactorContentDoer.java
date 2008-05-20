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
package org.miradi.views.diagram.doers;

import java.util.HashMap;
import java.util.Vector;

import org.miradi.commands.Command;
import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandDeleteObject;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.main.TransferableMiradiList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.views.diagram.DiagramCopyPaster;

public class PasteFactorContentDoer extends AbstractPasteDoer
{
	@Override
	public boolean isAvailable()
	{
		boolean isSuperAvailable = super.isAvailable();
		if (!isSuperAvailable)
			return false;

		if (invalidFactorSelectionCount())
			return false;

		if (hasMoreThanOneFactorInClipboard())
			return false;
		
		return pastingIntoDifferntType(); 
	}

	@Override
	public void doIt() throws CommandFailedException
	{
		if (!isAvailable())
			return;

		if (userCanceled())
			return;
		
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			ORef selectedFactorRef = getSelectedFactor().getWrappedORef();
			DiagramCopyPaster paster = new DiagramCopyPaster(getDiagramPanel(), getDiagramModel(), getTransferableMiradiList());
			paster.pasteFactors(getLocation());
			
			DiagramFactor newlyPastedDiagramFactor = getNewlyPastedFactor(paster);
			Vector<Command> commands = buildCommandsToFill(selectedFactorRef, newlyPastedDiagramFactor.getWrappedFactor());
			getProject().executeCommandsWithoutTransaction(commands);
			
			shallowDeleteDiagramFactorAndUnderlyingFactor(newlyPastedDiagramFactor);
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

	private boolean userCanceled()
	{
		String[] buttons = {PASTE_CONTENTS_BUTTON, CANCEL_BUTTON};
		String title = EAM.text("Paste Content...");
		String[] body = {EAM.text("This will replace all the selected factor's contents with the contents " +
								  "of the factor that was cut/copied earlier. Are you sure you want to do this?")};
	
		String userChoice = EAM.choiceDialog(title, body, buttons);
		return userChoice.equals(CANCEL_BUTTON);
	}
	
	private boolean pastingIntoDifferntType()
	{
		try
		{
			Vector diagramFactorJsons = getClipboardDiagramFactorJsons();
			String diagramFactorAsString = (String) diagramFactorJsons.get(0);
			EnhancedJsonObject json = new EnhancedJsonObject(diagramFactorAsString);
			ORef ref = json.getRef(DiagramFactor.TAG_WRAPPED_REF);
			
			return getSelectedFactor().getWrappedType() == ref.getObjectType(); 
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return false;
		}
	}

	private boolean hasMoreThanOneFactorInClipboard()
	{
		try
		{
			return getClipboardDiagramFactorJsons().size() != 1;
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return false;
		}
	}

	private boolean invalidFactorSelectionCount()
	{
		return getSelectedFactor() == null;
	}

	private FactorCell getSelectedFactor()
	{
		return getDiagramView().getDiagramComponent().getSelectedFactor();
	}

	private void shallowDeleteDiagramFactorAndUnderlyingFactor(DiagramFactor newlyPastedDiagramFactor) throws Exception
	{
		Factor factorToDelete = newlyPastedDiagramFactor.getWrappedFactor();
		CommandSetObjectData removeFromDiagram = CommandSetObjectData.createRemoveIdCommand(getDiagramModel().getDiagramObject(), DiagramObject.TAG_DIAGRAM_FACTOR_IDS, newlyPastedDiagramFactor.getId());
		getProject().executeCommand(removeFromDiagram);
		
		getProject().executeCommandsWithoutTransaction(newlyPastedDiagramFactor.createCommandsToClear());
		getProject().executeCommand(new CommandDeleteObject(newlyPastedDiagramFactor));
		
		getProject().executeCommandsWithoutTransaction(factorToDelete.createCommandsToClear());
		getProject().executeCommand(new CommandDeleteObject(factorToDelete));		
	}

	private DiagramFactor getNewlyPastedFactor(DiagramCopyPaster paster)
	{
		HashMap<ORef, ORef> oldToNewFactorRefMap = paster.getOldToNewObjectRefMap();
		ORef[] pastedRefsArray = oldToNewFactorRefMap.values().toArray(new ORef[0]);
		ORefList pastedRefs = new ORefList(pastedRefsArray);
		ORef diagramFactorRef = pastedRefs.getRefForType(DiagramFactor.getObjectType());
		
		return DiagramFactor.find(getProject(), diagramFactorRef);
	}

	private Vector<Command> buildCommandsToFill(ORef selectedFactorRef, Factor newlyPastedFactor)
	{
		Vector<Command> commands = new Vector();
		String[] allTags = newlyPastedFactor.getFieldTags();
		for (int tagIndex = 0; tagIndex < allTags.length; ++tagIndex)
		{			
			if (newlyPastedFactor.isPseudoField(allTags[tagIndex]))
				continue;
			
			String dataToTransfer = newlyPastedFactor.getData(allTags[tagIndex]);
			commands.add(new CommandSetObjectData(selectedFactorRef, allTags[tagIndex], dataToTransfer));
		}
		
		return commands;
	}

	private Vector getClipboardDiagramFactorJsons() throws Exception
	{
		TransferableMiradiList list = getTransferableMiradiList();
		if (list == null)
			return new Vector();
		
		return list.getDiagramFactorDeepCopies();
	}
	
	protected final String PASTE_CONTENTS_BUTTON = EAM.text("Button|Paste Contents");
}
