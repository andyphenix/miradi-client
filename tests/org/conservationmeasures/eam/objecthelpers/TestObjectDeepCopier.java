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
		IdList activityIds = new IdList();
		activityIds.add(activityRef.getObjectId());
	
		BaseId taskId = project.createObjectAndReturnId(Task.getObjectType());
		activity.addSubtaskId(taskId);
		
		ORef strategyRef = project.createObject(ObjectType.STRATEGY);
		Strategy strategy = (Strategy) project.findObject(strategyRef);
		assertEquals("owns objects?", 0, strategy.getAllOwnedObjects().size());
		
		strategy.addActivity(activityRef);
		ObjectDeepCopier deepCopier = new ObjectDeepCopier(project);
		Vector deepCopiedNull = deepCopier.createDeepCopy(null);
		assertEquals("deep copied null?", 0, deepCopiedNull.size());
		
		Vector deepCopiedJsonStrings = deepCopier.createDeepCopy(strategy);		
		assertEquals("not all objects copied?", 1, deepCopiedJsonStrings.size());
		 
		IdList deepCopiedFactorIds = extractRefsFromStrings(deepCopiedJsonStrings);
		assertEquals("wrong ref count?", 1, deepCopiedFactorIds.size());
		assertTrue("does not contain strategy?", deepCopiedFactorIds.contains(strategyRef.getObjectId()));
	}
	
	private IdList extractRefsFromStrings(Vector jsonStrings) throws Exception
	{
		IdList idList = new IdList();
		for (int i = 0 ; i < jsonStrings.size(); ++i)
		{
			EnhancedJsonObject json = new EnhancedJsonObject(jsonStrings.get(i).toString());
			idList.add(json.getId(BaseObject.TAG_ID));
		}
		
		return idList;
	}
	
	ProjectForTesting project;
}
