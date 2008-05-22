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
package org.miradi.objects;

import org.martus.util.MultiCalendar;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdAssigner;
import org.miradi.ids.IdList;
import org.miradi.objecthelpers.DateRangeEffortList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.utils.DateRange;
import org.miradi.utils.DateRangeEffort;

public class TestTask extends ObjectTestCase
{
	public TestTask(String name)
	{
		super(name);
	}
	
	public void testBasics() throws Exception
	{
		verifyFields(ObjectType.TASK);
		BaseId id = new BaseId(5);
		
		Task task = new Task(getObjectManager(), id);
		assertEquals("bad id?", id, task.getId());
		
		String label = "Name of task";
		task.setData(Task.TAG_LABEL, label);
		assertEquals("bad label?", label, task.getData(Task.TAG_LABEL));
		
		Task sameTask = new Task(getObjectManager(), id);
		assertEquals("same ids not equal?", task, sameTask);
		Task otherTask = new Task(getObjectManager(), new BaseId(id.asInt()+1));
		otherTask.setData(Task.TAG_LABEL, label);
		assertNotEquals("different ids are equal?", task, otherTask);
	}
	
	public void testData() throws Exception
	{
		IdList sampleIds = new IdList(Task.getObjectType());
		sampleIds.add(1);
		sampleIds.add(1527);
		String sampleIdData = sampleIds.toString(); 
		Task task = new Task(getObjectManager(), new BaseId(0));
		task.setData(Task.TAG_SUBTASK_IDS, sampleIdData);
		assertEquals("bad data?", sampleIdData, task.getData(Task.TAG_SUBTASK_IDS));
	}
	
	public void testNesting() throws Exception
	{
		IdAssigner idAssigner = new IdAssigner();
		Task top = new Task(getObjectManager(), idAssigner.takeNextId());
		Task child1 = new Task(getObjectManager(), idAssigner.takeNextId());
		Task child2 = new Task(getObjectManager(), idAssigner.takeNextId());
		Task grandchild21 = new Task(getObjectManager(), idAssigner.takeNextId());
		
		top.addSubtaskId(child1.getId());
		top.addSubtaskId(child2.getId());
		child2.addSubtaskId(grandchild21.getId());
		
		assertEquals("wrong subtask count?", 2, top.getSubtaskCount());
		assertEquals("not zero subtasks?", 0, child1.getSubtaskCount());
		assertEquals("wrong child1?", child1.getId(), top.getSubtaskId(0));
		assertEquals("wrong child2?", child2.getId(), top.getSubtaskId(1));
	}
	
	public void testSubtaskIdList() throws Exception
	{
		Task parent = createBasicTree();
		
		IdList ids = parent.getSubtaskIdList();
		assertEquals("wrong count?", 2, ids.size());
		assertEquals("wrong 1?", parent.getSubtaskId(0), ids.get(0));
		assertEquals("wrong 2?", parent.getSubtaskId(1), ids.get(1));
		
		IdList shouldBeCopy = parent.getSubtaskIdList();
		shouldBeCopy.add(2727);
		assertEquals("modified the actual list?", 2, parent.getSubtaskIdList().size());
	}
	
	public void testJson() throws Exception
	{
		Task parent = createBasicTree();
		
		Task got = (Task)BaseObject.createFromJson(getObjectManager(), parent.getType(), parent.toJson());
		assertEquals("wrong count?", parent.getSubtaskCount(), got.getSubtaskCount());
	}
	
	public void testGetChildTaskTypeCode()
	{
		assertEquals(Task.ACTIVITY_NAME, Task.getChildTaskTypeCode(Strategy.getObjectType()));
		assertEquals(Task.METHOD_NAME, Task.getChildTaskTypeCode(Indicator.getObjectType()));
		assertEquals(Task.OBJECT_NAME, Task.getChildTaskTypeCode(Task.getObjectType()));
		assertEquals(Task.OBJECT_NAME, Task.getChildTaskTypeCode(AccountingCode.getObjectType()));
	}

	private Task createBasicTree() throws Exception
	{
		Task parent = new Task(getObjectManager(), new BaseId(1));
		Task child1 = new Task(getObjectManager(), new BaseId(2));
		Task child2 = new Task(getObjectManager(), new BaseId(3));
		parent.addSubtaskId(child1.getId());
		parent.addSubtaskId(child2.getId());
		return parent;
	}
	
	public void testGetCombinedEffortDates() throws Exception
	{
		Task taskWithNoSubtasksNoAssignment = createTask(); 
		DateRange combinedDateRange = taskWithNoSubtasksNoAssignment.getWhenRollup();
		assertEquals("combined date range is not null?", null, combinedDateRange);
		
		Task taskWithNoSubTasksWithAssignment = createTask();
		addAssignment(taskWithNoSubTasksWithAssignment, 1.0, 1000, 3000);
		assertEquals("assignment was not added?", 1, taskWithNoSubTasksWithAssignment.getAssignmentIdList().size());
		assertEquals("wrong combined date range?", createDateRangeEffort(1000, 3000).getDateRange(), taskWithNoSubTasksWithAssignment.getWhenRollup());
		
		Task taskWithoutUnits = createTask();
		addAssignment(taskWithoutUnits, 0, 1000, 1001);
		assertEquals("assignment was not added?", 1, taskWithoutUnits.getAssignmentIdList().size());
		assertEquals("wrong combined date range?", null, taskWithoutUnits.getWhenRollup());
		
		Task taskWithSubtasks = createTask();
		Task subTask = createTask();
		IdList subTaskIds = new IdList(Task.getObjectType());
		subTaskIds.add(subTask.getId());
		taskWithSubtasks.setData(Task.TAG_SUBTASK_IDS, subTaskIds.toString());
		assertEquals("sub task combined date range was not null?", null, taskWithSubtasks.getWhenRollup());
		
		addAssignment(subTask, 1.0, 2000, 2010);
		addAssignment(subTask, 1.0, 10, 20);
		addAssignment(subTask, 0, 9998, 9999);
		assertEquals("wrong sub task combined date range?", createDateRangeEffort(10, 2010).getDateRange(), taskWithSubtasks.getWhenRollup());
	}

	private void addAssignment(Task task, double units, int startYear, int endYear) throws Exception
	{
		Assignment assignment = createAssignment();
		DateRangeEffortList dateRangeEffortList = new DateRangeEffortList();
		DateRangeEffort dateRangeEffort = createDateRangeEffort(startYear, endYear);
		dateRangeEffort.setUnitQuantity(units);
		dateRangeEffortList.add(dateRangeEffort);
		assignment.setData(Assignment.TAG_DATERANGE_EFFORTS, dateRangeEffortList.toString());
		IdList currentAssignmentIdList = task.getAssignmentIdList();
		currentAssignmentIdList.add(assignment.getId());
		task.setData(Task.TAG_ASSIGNMENT_IDS, currentAssignmentIdList.toString());
	}

	private Task createTask() throws Exception
	{
		ORef taskRef = getProject().createObject(Task.getObjectType());
		return Task.find(getProject(), taskRef);
	}

	private Assignment createAssignment() throws Exception
	{
		ORef assignmentRef = getProject().createObject(Assignment.getObjectType());
		return Assignment.find(getProject(), assignmentRef);
	}
	
	public static DateRangeEffort createDateRangeEffort(int startYear, int endYear) throws Exception
	{
		MultiCalendar startDate = MultiCalendar.createFromGregorianYearMonthDay(startYear, 1, 1);
		MultiCalendar endDate = MultiCalendar.createFromGregorianYearMonthDay(endYear, 1, 1);
		DateRange dateRange = new DateRange(startDate, endDate);
		
		return new DateRangeEffort("", 0, dateRange);
	}
}

