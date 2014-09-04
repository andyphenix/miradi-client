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
package org.miradi.objectpools;

import java.util.Vector;

import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdAssigner;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Task;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.schemas.TaskSchema;

public class TaskPool extends FactorPool
{
	public TaskPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.TASK);
	}
	
	public void put(Task task) throws Exception
	{
		put(task.getId(), task);
	}
	
	public Task find(BaseId id)
	{
		return (Task)getRawObject(id);
	}

	@Override
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId) throws Exception
	{
		return new Task(objectManager, new FactorId(actualId.asInt()));
	}

	private Task[] getAllRawTasks()
	{
		BaseId[] allIds = getIds();
		Task[] allTasks = new Task[allIds.length];
		for (int i = 0; i < allTasks.length; i++)
			allTasks[i] = find(allIds[i]);
			
		return allTasks;
	}

	public Vector<Task> getAllActivities()
	{
		return getTasks(TaskSchema.ACTIVITY_NAME);
	}
	
	public Vector<Task> getAllMethods()
	{
		return getTasks(TaskSchema.METHOD_NAME);
	}
	
	public Vector<Task> getAllTasks()
	{
		return getTasks(TaskSchema.OBJECT_NAME);
	}
	
	public Vector<Task> getTasks(String taskTypeName)
	{
		Vector<Task> allTypedTasks = new Vector<Task>();
		Task[] allTasks = getAllRawTasks();
		for (int index = 0; index < allTasks.length; ++index)
		{	
			if (taskTypeName.equals(allTasks[index].getTypeName()))
				allTypedTasks.add(allTasks[index]);
		}
		
		return allTypedTasks;
	}
	
	@Override
	public BaseObjectSchema createBaseObjectSchema(Project projectToUse)
	{
		return Task.createSchema();
	}
}
