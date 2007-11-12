/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/
package org.conservationmeasures.eam.views.planning.doers;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.dialogs.base.ObjectPoolTablePanel;
import org.conservationmeasures.eam.dialogs.diagram.ShareSelectionDialog;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.BaseObject;


abstract public class AbstractShareDoer extends AbstractTreeNodeCreateTaskDoer
{
	public void doIt() throws CommandFailedException
	{
		if (!isAvailable())
			return;
		
		appendSelectedObjectAsShared();
	}
	
	private void appendSelectedObjectAsShared() throws CommandFailedException
	{
		ORef parentOfSharedRef = getParentRefOfShareableObjects();
		if (parentOfSharedRef.isInvalid())
			return;
	
		ShareSelectionDialog listDialog = new ShareSelectionDialog(getMainWindow(), getShareDialogTitle(), getShareableObjectPoolTablePanel(parentOfSharedRef));
		listDialog.setVisible(true); 
		
		BaseObject objectToShare = listDialog.getSelectedObject();
		if (objectToShare == null)
			return;
		
		try
		{
			BaseObject parentOfShared = getProject().findObject(parentOfSharedRef);
			CommandSetObjectData appendSharedObjectCommand = CommandSetObjectData.createAppendIdCommand(parentOfShared, getParentTaskIdsTag(), objectToShare.getId());
			getProject().executeCommand(appendSharedObjectCommand);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
	
	abstract protected String getShareDialogTitle();
	
	abstract protected ObjectPoolTablePanel getShareableObjectPoolTablePanel(ORef parentOfSharedObjectRefs);
	
	abstract protected ORef getParentRefOfShareableObjects();
	
	abstract protected String getParentTaskIdsTag();
}
