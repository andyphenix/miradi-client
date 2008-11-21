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
package org.miradi.dialogs.planning.treenodes;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Task;
import org.miradi.project.Project;
import org.miradi.utils.CodeList;

public class PlanningTreeTaskNode extends AbstractPlanningTreeNode
{
	public PlanningTreeTaskNode(Project projectToUse, ORef taskRef, CodeList visibleRowsToUse) throws Exception
	{
		super(projectToUse, visibleRowsToUse);
		task = (Task)project.findObject(taskRef);
		
		rebuild();
	}

	public void rebuild() throws Exception
	{
		// NOTE: Speed optimization
		if(!visibleRows.contains(Task.OBJECT_NAME))
			return;
		
		ORefList assignmentRefs = task.getAssignmentRefs();
		for (int index = 0; index < assignmentRefs.size(); ++index)
		{
			children.add(new PlanningTreeAssignmentNode(project, assignmentRefs.get(index), visibleRows));
		}
		
		ORefList subtaskRefs = task.getSubtaskRefs();
		for(int i = 0; i < subtaskRefs.size(); ++i)
		{
			ORef taskRef = subtaskRefs.get(i);
			children.add(new PlanningTreeTaskNode(project, taskRef, visibleRows));
		}
	}

	public BaseObject getObject()
	{
		return task;
	}
	
	public Task getTask()
	{
		return (Task) getObject();
	}
	
	boolean shouldSortChildren()
	{
		return false;
	}
     	
	private Task task;
}
