/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.workplan;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.objects.Task;

public class TreeNodeDown extends AbstractTaskTreeDoer
{
	public boolean isAvailable()
	{
		EAMObject[] selected = getObjects();
		if(selected == null || selected.length != 1)
			return false;
	
		if(selected[0].getType() != ObjectType.TASK)
			return false;
		
		Task task = (Task)selected[0];
		IdList siblings = getTaskIds(task.getParentRef());
		if(!siblings.contains(task.getId()))
			return false;
		int at = siblings.find(task.getId());
		return (at < siblings.size() - 1);
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		Task selected = (Task)getObjects()[0];
		
		ORef parentRef = selected.getParentRef();
		String tag = getTaskIdsTag(parentRef);
		IdList siblings = getTaskIds(parentRef);
		BaseId id = selected.getId();
		int wasAt = siblings.find(id);

		IdList newSiblings = new IdList(siblings);
		newSiblings.removeId(id);
		newSiblings.insertAt(id, wasAt + 1);
		CommandSetObjectData cmd = new CommandSetObjectData(parentRef.getObjectType(), parentRef.getObjectId(), tag, newSiblings.toString());
		getProject().executeCommand(cmd);
		getPanel().selectObject(selected);
	}
}
