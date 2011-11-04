/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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

import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdList;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.RelevancyOverride;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.objecthelpers.RelevancyOverrideSetData;
import org.miradi.utils.CommandVector;

public class TestStrategy extends AbstractObjectWithBudgetDataToDeleteTestCase
{
	public TestStrategy(String name)
	{
		super(name);
	}
		
	@Override
	protected int getType()
	{
		return ObjectType.STRATEGY;
	}
	
	@Override
	protected BaseObject createParentObject() throws Exception
	{
		return getProject().createStrategy();
	}
	
	public void testFields() throws Exception
	{
		verifyFields(getType());
	}
	
	public void testBasics() throws Exception
	{
		FactorId interventionId = new FactorId(17);
		Strategy intervention = new Strategy(getObjectManager(), interventionId);
		assertEquals("already has activities?", 0, intervention.getActivityIds().size());
		
		IdList afterAdd1 = intervention.getActivityIds();
		afterAdd1.add(new BaseId(838));
		intervention.setData(Strategy.TAG_ACTIVITY_IDS, afterAdd1.toString());
		assertEquals("didn't add?", afterAdd1.toString(), intervention.getData(Strategy.TAG_ACTIVITY_IDS));
		
		intervention.setData(Strategy.TAG_ACTIVITY_IDS, "");
		assertEquals("didn't remove?", "", intervention.getData(Strategy.TAG_ACTIVITY_IDS));
	}
	
	public void testActivityIds() throws Exception
	{
		FactorId interventionId = new FactorId(66);
		Strategy intervention = new Strategy(getObjectManager(), interventionId);
		IdList empty = new IdList(Task.getObjectType(), intervention.getData(Strategy.TAG_ACTIVITY_IDS));
		assertEquals("not empty to start?", 0, empty.size());
		
		BaseId activityId = new BaseId(828);
		IdList oneItem = new IdList(Task.getObjectType());
		oneItem.add(activityId);
		intervention.setData(Strategy.TAG_ACTIVITY_IDS, oneItem.toString());
		
		IdList got = new IdList(Task.getObjectType(), intervention.getData(Strategy.TAG_ACTIVITY_IDS));
		assertEquals("round trip failed?", oneItem, got);
	}
	
	public void testStatus() throws Exception
	{
		FactorId interventionId = new FactorId(91);
		Strategy intervention = new Strategy(getObjectManager(), interventionId);
		assertTrue("didn't default to real status?", intervention.isStatusReal());
		assertFalse("defaulted to draft status?", intervention.isStatusDraft());
		intervention.setData(Strategy.TAG_STATUS, Strategy.STATUS_DRAFT);
		assertEquals("set/get didn't work?", Strategy.STATUS_DRAFT, intervention.getData(Strategy.TAG_STATUS));
		assertFalse("didn't unset real status?", intervention.isStatusReal());
		assertTrue("didn't set to draft status?", intervention.isStatusDraft());
		intervention.setData(Strategy.TAG_STATUS, Strategy.STATUS_REAL);
		assertTrue("didn't restore to real status?", intervention.isStatusReal());
		assertFalse("didn't unset draft status?", intervention.isStatusDraft());
		intervention.setData(Strategy.TAG_STATUS, "OIJFW*FJJF");
		assertTrue("didn't treat unknown as real?", intervention.isStatusReal());
		assertFalse("treated unknown as draft?", intervention.isStatusDraft());
		
	}
	
	public void testJson() throws Exception
	{
		FactorId interventionId = new FactorId(17);
		Strategy intervention = new Strategy(getObjectManager(), interventionId);
		intervention.setData(Strategy.TAG_STATUS, Strategy.STATUS_DRAFT);
		IdList activityIds = new IdList(Task.getObjectType());
		activityIds.add(new BaseId(23));
		activityIds.add(new BaseId(37));
		intervention.setData(Strategy.TAG_ACTIVITY_IDS, activityIds.toString());
		
		Strategy got = (Strategy)BaseObject.createFromJson(getProject().getObjectManager(), intervention.getType(), intervention.toJson());
		assertTrue("Didn't restore status?", got.isStatusDraft());
		assertEquals("Didn't read activities?", intervention.getActivityIds(), got.getActivityIds());
	}
	
	public void testGetWorkUnits() throws Exception
	{
		TestIndicator.verifyGetWorkUnits(getProject(), Strategy.getObjectType(), Strategy.TAG_ACTIVITY_IDS);
	}
	
	public void testIsAssignmentDataSuperseded() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		TestTask.verifyIsAssignmentDataSuperseded(getProject(), strategy, Strategy.TAG_ACTIVITY_IDS);
	}
	
	public void testCreateCommandsToDeleteChildren() throws Exception
	{
		Strategy strategy = getProject().createStrategy();		
		TestObjective.verifyAnnotationIsDeletedFromParent(getProject(), strategy, Strategy.TAG_PROGRESS_REPORT_REFS, ProgressReport.getObjectType());
	}
	
	public void testStrategyIsRemovedFromObjectiveRelevancyList() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		Cause cause = getProject().createCause();
		Objective objective = getProject().createObjective(cause);
		verifyRelevancyIsUpdatedAfterActivityIsDeleted(strategy, cause, objective);
	}
	
	public void testStrategyIsRemovedFromGoalRelevancyListWhenDeleted() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		Target goalOwner = getProject().createTarget();
		Goal goal = getProject().createGoal(goalOwner);
		verifyRelevancyIsUpdatedAfterActivityIsDeleted(strategy, goalOwner, goal);
	}
	
	public void testActivityIsRemovedFromObjectiveRelevancyListWhenDeleted() throws Exception
	{
		Task activitiy = getProject().createActivity();
		Strategy objectiveOwner = getProject().createStrategy();
		Objective objective = getProject().createObjective(objectiveOwner);
		verifyRelevancyIsUpdatedAfterActivityIsDeleted(activitiy, objectiveOwner, objective);
	}

	public void testActivityIsRemovedFromGoalRelevancyListWhenDeleted() throws Exception
	{
		Task activitiy = getProject().createActivity();
		Target goalOwner = getProject().createTarget();
		Goal goal = getProject().createGoal(goalOwner);
		verifyRelevancyIsUpdatedAfterActivityIsDeleted(activitiy, goalOwner, goal);
	}

	private void verifyRelevancyIsUpdatedAfterActivityIsDeleted(BaseObject itemInRelevancyListToBeDeleted, Factor desireOwner, Desire desire) throws Exception, CommandFailedException
	{
		RelevancyOverrideSet relevantActivities = new RelevancyOverrideSet();
		relevantActivities.add(new RelevancyOverride(itemInRelevancyListToBeDeleted.getRef(), true));
		getProject().fillObjectUsingCommand(desire, Desire.TAG_RELEVANT_STRATEGY_ACTIVITY_SET, relevantActivities.toString());

		ORefList relevantStrategyAndActivityRefs = new ORefList(getAllStrategyAndActivityRefsFromRelevancyOverrides(desire));
		ORefList relevantActivityRefs = relevantStrategyAndActivityRefs.getFilteredBy(itemInRelevancyListToBeDeleted.getType());
		assertEquals("Desire's activity relevancy list was not updated?", 1, relevantActivityRefs.size());
		
		CommandVector commandsToDelete = itemInRelevancyListToBeDeleted.createCommandsToDeleteChildrenAndObject();
		getProject().executeCommands(commandsToDelete);
		
		ORefList relevantStrategyAndActivityRefsAfterDelete = new ORefList(getAllStrategyAndActivityRefsFromRelevancyOverrides(desire));
		ORefList relevantActivityRefsAfterDelete = relevantStrategyAndActivityRefsAfterDelete.getFilteredBy(itemInRelevancyListToBeDeleted.getType());
		
		assertEquals("Activity was not removed from Desire relevancy list?", 0, relevantActivityRefsAfterDelete.size());
	}

	public ORefSet getAllStrategyAndActivityRefsFromRelevancyOverrides(Desire desire) throws Exception
	{
		return ((RelevancyOverrideSetData)desire.getField(desire.TAG_RELEVANT_STRATEGY_ACTIVITY_SET)).extractRelevantRefs();
	}

	
	static final BaseId criterionId1 = new BaseId(17);
	static final BaseId criterionId2 = new BaseId(952);
	static final BaseId criterionId3 = new BaseId(2833);
	static final BaseId valueId1 = new BaseId(85);
	static final BaseId valueId2 = new BaseId(2398);
	static final BaseId defaultValueId = new BaseId(7272);
}
