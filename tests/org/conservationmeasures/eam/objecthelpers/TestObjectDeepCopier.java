/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objecthelpers;

import java.util.Vector;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class TestObjectDeepCopier extends EAMTestCase
{
	public TestObjectDeepCopier(String name)
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
		super.tearDown();
		project.close();
	}

	public void testDeepCopy() throws Exception
	{
		ORef activityRef = project.createObject(Task.getObjectType());
		Task activity = (Task) project.findObject(activityRef);
		IdList activityIds = new IdList(Task.getObjectType());
		activityIds.add(activityRef.getObjectId());
	
		BaseId taskId = project.createObjectAndReturnId(Task.getObjectType());
		activity.addSubtaskId(taskId);
		
		ORef strategyRef = project.createObject(ObjectType.STRATEGY);
		Strategy strategy = (Strategy) project.findObject(strategyRef);
		assertEquals("wrong initial number of objects to deep copy?", 0, strategy.getAllObjectsToDeepCopy().size());
		
		strategy.addActivity(activityRef);
		assertEquals("wrong number of objects to deep copy?", 1, strategy.getAllObjectsToDeepCopy().size());
		ObjectDeepCopier deepCopier = new ObjectDeepCopier(project);
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
