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
package org.miradi.project;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.main.TestCaseWithProject;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Factor;
import org.miradi.objects.Indicator;
import org.miradi.objects.Task;
import org.miradi.project.ProjectRepairer;

public class TestProjectRepairer extends TestCaseWithProject
{
	public TestProjectRepairer(String name)
	{
		super(name);
	}
	
	public void testDeleteOrphanObjectives() throws Exception
	{
		int annotationType = ObjectType.OBJECTIVE;
		String nodeTagForAnnotationList = Factor.TAG_OBJECTIVE_IDS;

		verifyDeleteOrphanAnnotations(annotationType, ObjectType.CAUSE, nodeTagForAnnotationList);
		
	}

	public void testDeleteOrphanGoals() throws Exception
	{
		int annotationType = ObjectType.GOAL;
		String nodeTagForAnnotationList = Factor.TAG_GOAL_IDS;

		verifyDeleteOrphanAnnotations(annotationType, ObjectType.TARGET, nodeTagForAnnotationList);
		
	}

	public void testDeleteOrphanIndicators() throws Exception
	{
		int annotationType = ObjectType.INDICATOR;
		String nodeTagForAnnotationList = Factor.TAG_INDICATOR_IDS;

		verifyDeleteOrphanAnnotations(annotationType, ObjectType.CAUSE, nodeTagForAnnotationList);
		
	}

	private void verifyDeleteOrphanAnnotations(int annotationType, int nodeType, String nodeTagForAnnotationList) throws Exception
	{
		//BaseId orphan = project.createObject(annotationType);
		BaseId nonOrphan = getProject().createObjectAndReturnId(annotationType);
		FactorId nodeId = (FactorId)getProject().createObjectAndReturnId(nodeType, BaseId.INVALID);
		IdList annotationIds = new IdList(annotationType);
		annotationIds.add(nonOrphan);
		getProject().setObjectData(ObjectType.FACTOR, nodeId, nodeTagForAnnotationList, annotationIds.toString());

		EAM.setLogToString();

//		TODO: removed orphan deletion code until a solution can be found to general extentions of having annoations having annoations
		ProjectRepairer.repairAnyProblems(getProject());
		//assertContains("Deleting orphan", EAM.getLoggedString());
		//assertNull("Didn't delete orphan?", project.findObject(annotationType, orphan));
		assertEquals("Deleted non-orphan?", nonOrphan, getProject().findObject(annotationType, nonOrphan).getId());
	}
	
	public void testScanForCorruptedObjects() throws Exception
	{
		ProjectRepairer repairer = new ProjectRepairer(getProject());

		ORef indicatorRef = getProject().createObject(Indicator.getObjectType());
		Indicator indicator = (Indicator) getProject().findObject(indicatorRef);
		BaseId nonExistantId = new BaseId(500);
		CommandSetObjectData appendTask = CommandSetObjectData.createAppendIdCommand(indicator, Indicator.TAG_TASK_IDS, nonExistantId);
		getProject().executeCommand(appendTask);
		assertEquals("task not appended?", 1, indicator.getMethodRefs().size());		
		assertEquals("Didn't detect non-existant method?", 1, repairer.findAllMissingObjects().size());
		
		ORef taskRef = getProject().createObject(Task.getObjectType());
		Task task = (Task) getProject().findObject(taskRef);
		CommandSetObjectData appendSubTask = CommandSetObjectData.createAppendIdCommand(task, Task.TAG_SUBTASK_IDS, nonExistantId);
		getProject().executeCommand(appendSubTask);
		assertEquals("subtask not appended?", 1, task.getSubtaskCount());
		assertEquals("Didn't detect second instance?", 2, repairer.findAllMissingObjects().size());
	}
}
