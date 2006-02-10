/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.annotations;

import org.conservationmeasures.eam.project.IdAssigner;
import org.conservationmeasures.eam.project.ObjectPool;

public class GoalPool extends ObjectPool
{
	public void put(Goal goal)
	{
		put(goal.getId(), goal);
	}
	
	public Goal find(int id)
	{
		return (Goal)getRawObject(id);
	}

	public static GoalPool createSampleGoals(IdAssigner assigner)
	{
		//TODO: These will be replaced by real user entered data from a wizard
		GoalPool goals = new GoalPool();
	
		goals.put(new Goal(IdAssigner.INVALID_ID, Goal.ANNOTATION_NONE_STRING));
		goals.put(new Goal(assigner.takeNextId(), "Goal 1"));
		goals.put(new Goal(assigner.takeNextId(), "Goal 2"));
		goals.put(new Goal(assigner.takeNextId(), "Goal 3"));
		return goals;
	}
}
