/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.main.AbstractTransferableMiradiList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.project.FactorDeleteHelper;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.utils.CommandVector;
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

		if (notExactlyOneFactorSelected())
			return false;

		if (hasMoreThanOneFactorInClipboard())
			return false;
		
		return pastingIntoSameType(); 
	}

	@Override
	protected void doIt() throws Exception
	{
		if (!isAvailable())
			return;

		if (!userWantsToContinue())
			return;
		
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{	
			ORef selectedFactorRefToPasteContentInto = getSingleSelectedFactor().getWrappedFactorRef();
			Factor selectedFactorToPasteContentInto = Factor.findFactor(getProject(), selectedFactorRefToPasteContentInto);
			
			DiagramCopyPaster paster = new DiagramCopyPaster(getDiagramPanel(), getDiagramModel(), getTransferableMiradiList());
			paster.pasteFactors(getLocation());
			
			FactorDeleteHelper factorDeleteHelper = FactorDeleteHelper.createFactorDeleteHelper(getDiagramPanel().getCurrentDiagramComponent());
			factorDeleteHelper.deleteAnnotations(selectedFactorToPasteContentInto);
			CommandVector commandsToClear = new CommandVector(selectedFactorToPasteContentInto.createCommandsToClear());
			getProject().executeCommands(commandsToClear);
			
			DiagramFactor newlyPastedDiagramFactor = getNewlyPastedFactor(paster);
			CommandVector commands = buildCommandsToFill(selectedFactorRefToPasteContentInto, newlyPastedDiagramFactor.getWrappedFactor());
			getProject().executeCommands(commands);
			
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

	private boolean userWantsToContinue()
	{
		String[] buttons = {PASTE_CONTENTS_BUTTON, CANCEL_BUTTON};
		String title = EAM.text("Paste Content...");
		String[] body = {EAM.text("This will replace all the selected factor's contents with the contents " +
								  "of the factor that was cut/copied earlier. Are you sure you want to do this?")};
	
		String userChoice = EAM.choiceDialog(title, body, buttons);
		return userChoice.equals(PASTE_CONTENTS_BUTTON);
	}
	
	private boolean pastingIntoSameType()
	{
		try
		{
			Vector diagramFactorJsons = getClipboardDiagramFactorJsons();
			String diagramFactorAsString = (String) diagramFactorJsons.get(0);
			EnhancedJsonObject json = new EnhancedJsonObject(diagramFactorAsString);
			ORef wrappedRef = json.getRef(DiagramFactor.TAG_WRAPPED_REF);
			
			return getSingleSelectedFactor().getWrappedType() == wrappedRef.getObjectType(); 
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

	private boolean notExactlyOneFactorSelected()
	{
		return getSingleSelectedFactor() == null;
	}

	private FactorCell getSingleSelectedFactor()
	{
		return getDiagramView().getCurrentDiagramComponent().getSingleSelectedFactor();
	}

	private void shallowDeleteDiagramFactorAndUnderlyingFactor(DiagramFactor newlyPastedDiagramFactor) throws Exception
	{
		Factor factorToDelete = newlyPastedDiagramFactor.getWrappedFactor();
		CommandSetObjectData removeFromDiagram = CommandSetObjectData.createRemoveIdCommand(getDiagramModel().getDiagramObject(), DiagramObject.TAG_DIAGRAM_FACTOR_IDS, newlyPastedDiagramFactor.getId());
		getProject().executeCommand(removeFromDiagram);
		
		getProject().executeCommands(newlyPastedDiagramFactor.createCommandsToShallowDelete());
		getProject().executeCommands(factorToDelete.createCommandsToShallowDelete());		
	}

	private DiagramFactor getNewlyPastedFactor(DiagramCopyPaster paster)
	{
		HashMap<ORef, ORef> oldToNewFactorRefMap = paster.getOldToNewObjectRefMap();
		ORef[] pastedRefsArray = oldToNewFactorRefMap.values().toArray(new ORef[0]);
		ORefList pastedRefs = new ORefList(pastedRefsArray);
		ORef diagramFactorRef = pastedRefs.getRefForType(DiagramFactorSchema.getObjectType());
		
		return DiagramFactor.find(getProject(), diagramFactorRef);
	}

	private CommandVector buildCommandsToFill(ORef selectedFactorRef, Factor newlyPastedFactor)
	{
		CommandVector commands = new CommandVector();
		Vector<String> allTags = newlyPastedFactor.getStoredFieldTags();
		for (int tagIndex = 0; tagIndex < allTags.size(); ++tagIndex)
		{			
			String tag = allTags.get(tagIndex);
			String dataToTransfer = newlyPastedFactor.getData(tag);
			commands.add(new CommandSetObjectData(selectedFactorRef, tag, dataToTransfer));
		}
		
		return commands;
	}

	private Vector getClipboardDiagramFactorJsons() throws Exception
	{
		AbstractTransferableMiradiList list = getTransferableMiradiList();
		if (list == null)
			return new Vector();
		
		return list.getDiagramFactorDeepCopies();
	}
	
	protected final String PASTE_CONTENTS_BUTTON = EAM.text("Button|Paste Contents");
}
