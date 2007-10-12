package org.conservationmeasures.eam.views.planning.doers;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.dialogs.ShareableMethodPoolTablePanel;
import org.conservationmeasures.eam.dialogs.diagram.ShareSelectionDialog;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Indicator;

public class ShareMethodDoer extends AbstractShareDoer
{	
	protected boolean canOwnTask(BaseObject object)
	{
		if(object.getType() == Indicator.getObjectType())
			return true;
		
		return false;
	}

	public void doIt() throws CommandFailedException
	{
		if (!isAvailable())
			return;
		
		ORef selectedRef = getSelectedRef();
		ShareSelectionDialog listDialog = new ShareSelectionDialog(getMainWindow(), EAM.text("Share Method"), new ShareableMethodPoolTablePanel(getProject(), selectedRef));
		listDialog.setVisible(true);
		
		appendSelectedObjectAsShared(selectedRef, Indicator.TAG_TASK_IDS, listDialog.getSelectedObject());
	}

	protected void appendSelectedObjectAsShared(ORef parentOfSharedRef, String tag, BaseObject objectToShare) throws CommandFailedException
	{
		if (objectToShare == null)
			return;
		
		try
		{
			BaseObject parentOfShared = getProject().findObject(parentOfSharedRef);
			CommandSetObjectData appendSharedObjectCommand = CommandSetObjectData.createAppendIdCommand(parentOfShared, tag, objectToShare.getId());
			getProject().executeCommand(appendSharedObjectCommand);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
}
