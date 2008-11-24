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
import org.miradi.objects.Assignment;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ProjectResource;
import org.miradi.project.Project;
import org.miradi.utils.CodeList;

public class PlanningTreeAssignmentNode extends AbstractPlanningTreeNode
{
	public PlanningTreeAssignmentNode(Project projectToUse, ORef assignmentRef, CodeList visibleRowsToUse) throws Exception
	{
		super(projectToUse, visibleRowsToUse);
		assignment = Assignment.find(projectToUse, assignmentRef);
		projectResource = ProjectResource.find(project, assignment.getResourceRef());
	}

	public void rebuild() throws Exception
	{
	}

	public BaseObject getObject()
	{
		return assignment;
	}
	
	public String toRawString()
	{
		if (projectResource != null)
			return projectResource.getFullName();
		
		return null;
	}
	
     	
	private Assignment assignment;
	private ProjectResource projectResource;
}
