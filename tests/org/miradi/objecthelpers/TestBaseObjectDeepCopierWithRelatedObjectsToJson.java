/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.objecthelpers;

import java.util.Vector;

import org.miradi.commands.TestCommandSetObjectData;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.MiradiTestCase;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Strategy;
import org.miradi.objects.Task;
import org.miradi.project.ProjectForTesting;
import org.miradi.schemas.TaskSchema;
import org.miradi.utils.EnhancedJsonObject;

public class TestBaseObjectDeepCopierWithRelatedObjectsToJson extends MiradiTestCase
{
	public TestBaseObjectDeepCopierWithRelatedObjectsToJson(String name)
	{
		super(name);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		project = ProjectForTesting.createProjectWithDefaultObjects(getName());
	}

	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
		project = null;
	}

	public void testDeepCopy() throws Exception
	{
		ORef activityRef = project.createObject(TaskSchema.getObjectType());
		Task activity = (Task) project.findObject(activityRef);
		IdList activityIds = new IdList(TaskSchema.getObjectType());
		activityIds.add(activityRef.getObjectId());
	
		BaseId taskId = project.createObjectAndReturnId(TaskSchema.getObjectType());
		TestCommandSetObjectData.addSubtaskId(activity, taskId);
		
		ORef strategyRef = project.createObject(ObjectType.STRATEGY);
		Strategy strategy = (Strategy) project.findObject(strategyRef);
		assertEquals("wrong initial number of objects to deep copy?", 0, strategy.getOwnedAndRelatedObjectRefs(new ORefList()).size());
		
		strategy.setData(Strategy.TAG_ACTIVITY_IDS, activityIds.toString());
		assertEquals("wrong number of objects to deep copy?", 1, strategy.getOwnedAndRelatedObjectRefs(new ORefList()).size());
		BaseObjectDeepCopierWithRelatedObjectsToJson deepCopier = new BaseObjectDeepCopierWithRelatedObjectsToJson(project);
		Vector deepCopiedNull = deepCopier.createDeepCopy(null);
		assertEquals("deep copied null?", 0, deepCopiedNull.size());
		
		Vector deepCopiedJsonStrings = deepCopier.createDeepCopy(strategy);		
		assertEquals("not all objects copied?", 3, deepCopiedJsonStrings.size());
		 
		IdList deepCopiedFactorIds = extractRefsFromStrings(deepCopiedJsonStrings);
		assertEquals("wrong ref count?", 3, deepCopiedFactorIds.size());
		assertTrue("does not contain strategy?", deepCopiedFactorIds.contains(strategyRef.getObjectId()));
	}
	
	private IdList extractRefsFromStrings(Vector jsonStrings) throws Exception
	{
		IdList idList = new IdList(0);
		for (int i = 0 ; i < jsonStrings.size(); ++i)
		{
			EnhancedJsonObject json = new EnhancedJsonObject(jsonStrings.get(i).toString());
			idList.add(json.getId(BaseObject.TAG_ID));
		}
		
		return idList;
	}
	
	ProjectForTesting project;
}
