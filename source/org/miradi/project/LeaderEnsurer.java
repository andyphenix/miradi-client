/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.project;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.ids.IdList;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ResourceAssignment;
import org.miradi.schemas.ResourceAssignmentSchema;

public class LeaderEnsurer implements CommandExecutedListener
{
	public LeaderEnsurer(Project projectToUse)
	{
		project = projectToUse;
	}
	
	public void disable()
	{
		getProject().removeCommandExecutedListener(this);
	}

	public void enable()
	{
		getProject().addCommandExecutedListener(this);
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		try
		{
			if (event.isSetDataCommandWithThisTypeAndTag(ResourceAssignmentSchema.getObjectType(), ResourceAssignment.TAG_RESOURCE_ID))
				possiblyClearResourceLeaderDueToResourceAssignmentResourceUpdate(event);
			
			if (event.isSetDataCommandWithThisTag(BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS))
				possiblyClearResourceLeaderDueToResourceAssignmentDeletion(event);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new RuntimeException(e);
		}
	}

	private void possiblyClearResourceLeaderDueToResourceAssignmentDeletion(CommandExecutedEvent event) throws Exception
	{
		CommandSetObjectData setCommand = event.getSetCommand();
		ORefList currentList = new ORefList(new IdList(ResourceAssignmentSchema.getObjectType(), setCommand.getDataValue()));
		ORefList previousList = new ORefList(new IdList(ResourceAssignmentSchema.getObjectType(), setCommand.getPreviousDataValue()));
		if (previousList.size() <= currentList.size())
			return;
		
		ORefList changedRefList = ORefList.subtract(previousList, currentList);
		if (changedRefList.size() != 1)
			return;
		
		clearResourceLeader(setCommand.getObjectORef());
	}

	private void possiblyClearResourceLeaderDueToResourceAssignmentResourceUpdate(CommandExecutedEvent event) throws Exception
	{
		CommandSetObjectData setCommand = event.getSetCommand();
		final String previousDataValue = setCommand.getPreviousDataValue();
		if (previousDataValue.length() == 0)
			return;
		
		ResourceAssignment resourceAssignment = ResourceAssignment.find(getProject(), setCommand.getObjectORef());
		ORefList referrers = resourceAssignment.findAllObjectsThatReferToUs();
		if (referrers.isEmpty())
			return;
		
		if (referrers.size() > 1)
			throw new Exception("ResourceAssignments cannot be shared");
		
		clearResourceLeader(referrers.getFirstElement());
	}

	public void clearResourceLeader(ORef leaderOwnerRef) throws Exception
	{
		BaseObject leaderOwner = BaseObject.find(getProject(), leaderOwnerRef);
		ORef currentLeaderRef = leaderOwner.getLeaderResourceRef();
		ORefSet resourceRefs = leaderOwner.getTotalTimePeriodCostsMap().getAllProjectResourceRefs();
		if (!resourceRefs.contains(currentLeaderRef))
			clearLeaderResourceRef(leaderOwner);
	}

	private void clearLeaderResourceRef(BaseObject objectToClearLeaderFrom) throws Exception
	{
		CommandSetObjectData clearCommand = new CommandSetObjectData(objectToClearLeaderFrom, BaseObject.TAG_LEADER_RESOURCE, "");
		getProject().executeAsSideEffect(clearCommand);
	}

	private Project getProject()
	{
		return project;
	}
	
	private Project project;
}
