/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ProjectForTesting;

public class TestTask extends ObjectTestCase
{
	public TestTask(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
	}

	public void tearDown() throws Exception
	{
		project.close();
		super.tearDown();
	}

	public void testBasics() throws Exception
	{
		verifyFields(ObjectType.TASK);
		BaseId id = new BaseId(5);
		
		Task task = new Task(id);
		assertEquals("bad id?", id, task.getId());
		
		String label = "Name of task";
		task.setData(Task.TAG_LABEL, label);
		assertEquals("bad label?", label, task.getData(Task.TAG_LABEL));
		
		Task sameTask = new Task(id);
		assertEquals("same ids not equal?", task, sameTask);
		Task otherTask = new Task(new BaseId(id.asInt()+1));
		otherTask.setData(Task.TAG_LABEL, label);
		assertNotEquals("different ids are equal?", task, otherTask);
	}
	
	public void testData() throws Exception
	{
		IdList sampleIds = new IdList();
		sampleIds.add(1);
		sampleIds.add(1527);
		String sampleIdData = sampleIds.toString(); 
		Task task = new Task(new BaseId(0));
		task.setData(Task.TAG_SUBTASK_IDS, sampleIdData);
		assertEquals("bad data?", sampleIdData, task.getData(Task.TAG_SUBTASK_IDS));
	}
	
	public void testNesting() throws Exception
	{
		IdAssigner idAssigner = new IdAssigner();
		Task top = new Task(idAssigner.takeNextId());
		Task child1 = new Task(idAssigner.takeNextId());
		Task child2 = new Task(idAssigner.takeNextId());
		Task grandchild21 = new Task(idAssigner.takeNextId());
		
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
		
		Task got = (Task)BaseObject.createFromJson(project.getObjectManager(), parent.getType(), parent.toJson());
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
		Task parent = new Task(new BaseId(1));
		Task child1 = new Task(new BaseId(2));
		Task child2 = new Task(new BaseId(3));
		parent.addSubtaskId(child1.getId());
		parent.addSubtaskId(child2.getId());
		return parent;
	}
	
	ProjectForTesting project;
}
