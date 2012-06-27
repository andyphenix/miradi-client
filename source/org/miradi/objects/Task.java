/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.UnknownTaskParentTypeException;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.objecthelpers.TimePeriodCostsMap;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.GoalSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.ObjectiveSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.utils.CommandVector;
import org.miradi.utils.OptionalDouble;

public class Task extends Factor
{
	public Task(ObjectManager objectManager, FactorId idToUse)
	{
		super(objectManager, idToUse, createSchema());
	}

	public static TaskSchema createSchema()
	{
		return new TaskSchema();
	}
	
	public CommandVector getDeleteSelfAndSubtasksCommands(Project project) throws Exception
	{
		CommandVector deleteIds = new CommandVector();
		deleteIds.add(new CommandSetObjectData(getType(), getId(), Task.TAG_SUBTASK_IDS, ""));
		int subTaskCount = getSubtaskCount();
		for (int index = 0; index < subTaskCount; index++)
		{
			BaseId subTaskId = getSubtaskId(index);
			Task  subTask = (Task)project.findObject(ObjectType.TASK, subTaskId);
			deleteIds.addAll(subTask.createCommandsToDeleteChildrenAndObject());
		}
		
		return deleteIds;
	}
	
	@Override
	public CommandVector createCommandsToDeleteChildren() throws Exception
	{
		CommandVector commandsToDeleteChildren  = super.createCommandsToDeleteChildren();
		commandsToDeleteChildren.addAll(createCommandsToDeleteBudgetChildren());
		commandsToDeleteChildren.addAll(createCommandsToDeleteRefs(TAG_PROGRESS_REPORT_REFS));
		commandsToDeleteChildren.addAll(getDeleteSelfAndSubtasksCommands(getProject()));
		
		return commandsToDeleteChildren;
	}
	
	@Override
	protected CommandVector createCommandsToDereferenceObject() throws Exception
	{
		CommandVector commandsToDereferences = super.createCommandsToDereferenceObject();
		commandsToDereferences.addAll(buildRemoveFromRelevancyListCommands(getRef()));
		
		return commandsToDereferences;
	}
	
	@Override
	public int getAnnotationType(String tag)
	{
		if (tag.equals(TAG_SUBTASK_IDS))
			return TaskSchema.getObjectType();
		
		return super.getAnnotationType(tag);
	}

	@Override
	public boolean isIdListTag(String tag)
	{
		if (tag.equals(TAG_SUBTASK_IDS))
			return true;
		
		if (tag.equals(TAG_RESOURCE_ASSIGNMENT_IDS))
			return true;
		
		return super.isIdListTag(tag);
	}

	@Override
	public boolean isRefList(String tag)
	{
		if (tag.equals(TAG_EXPENSE_ASSIGNMENT_REFS))
			return true;
		
		if (tag.equals(TAG_PROGRESS_REPORT_REFS))
			return true;

		return super.isRefList(tag);
	}

	@Override
	public String getTypeName()
	{
		ensureCachedTypeStringIsValid();
		return cachedObjectTypeName;
	}
	
	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return new int[] {
			StrategySchema.getObjectType(),
			IndicatorSchema.getObjectType(),
			TaskSchema.getObjectType(),
		};
	}
	
	@Override
	public String getDetails()
	{
		return getData(TAG_DETAILS);
	}
	
	@Override
	public boolean canDirectlyOwnIndicators()
	{
		return false;
	}
	
	@Override
	public ORefList getOwnedObjects(int objectType)
	{
		ORefList list = super.getOwnedObjects(objectType);
		
		switch(objectType)
		{
			case ObjectType.TASK: 
				list.addAll(new ORefList(objectType, getSubtaskIdList()));
				break;
			case ObjectType.RESOURCE_ASSIGNMENT: 
				list.addAll(getResourceAssignmentRefs());
				break;
			case ObjectType.EXPENSE_ASSIGNMENT:
				list.addAll(getExpenseAssignmentRefs());
				break;
			case ObjectType.PROGRESS_REPORT:
				list.addAll(getSafeRefListData(TAG_PROGRESS_REPORT_REFS));
				break;
		}
		return list;
	}
	
	//NOTE: this is not testing if this is a Task object...
	//but if it is a user level task as opposed to a method or an activity
	public boolean isTask()
	{
		return is(TaskSchema.OBJECT_NAME);
	}

	@Override
	public boolean isActivity()
	{
		return is(TaskSchema.ACTIVITY_NAME);
	}

	public boolean isMethod()
	{
		return is(TaskSchema.METHOD_NAME);
	}
	
	private boolean is(final String taskObjectTypeName)
	{
		ensureCachedTypeStringIsValid();
		return (taskObjectTypeName.equals(cachedObjectTypeName));
	}
	
	@Override
	public boolean mustBeDeletedBecauseParentIsGone()
	{
		boolean isSuperShared = super.mustBeDeletedBecauseParentIsGone();
		if (isSuperShared)
			return true;
		
		ORefList referrers = findObjectsThatReferToUs(StrategySchema.getObjectType());
		
		return referrers.size() > 0;
	}
	
	private void ensureCachedTypeStringIsValid()
	{
		ORefList strategyReferrers = findObjectsThatReferToUs(StrategySchema.getObjectType());
		if(strategyReferrers.size() > 0)
		{
			cachedObjectTypeName = TaskSchema.ACTIVITY_NAME;
			return;
		}
		
		ORefList indicatorReferrers = findObjectsThatReferToUs(IndicatorSchema.getObjectType());
		if(indicatorReferrers.size() > 0)
		{
			cachedObjectTypeName = TaskSchema.METHOD_NAME;
			return;
		}

		// NOTE: We should be able to do this test first, but in Marine Example 1.0.7
		// there are Activities that somehow have owners
		ORef ownerRef = getOwnerRef();
		if (ownerRef != null && !ownerRef.isInvalid())
		{
			cachedObjectTypeName = TaskSchema.OBJECT_NAME;
			return;
		}
		
		EAM.logVerbose("Task with no owner: " + getRef());
	}
	
	public boolean isOrphandTask()
	{
		return !hasReferrers();
	}
	
	public boolean isPartOfASharedTaskTree()
	{
		return getTotalShareCount() > 1;
	}

	public int getSubtaskCount()
	{
		return getSubtaskIdList().size();
	}
	
	public BaseId getSubtaskId(int index)
	{
		return getSubtaskIdList().get(index);
	}
	
	public IdList getSubtaskIdList()
	{
		return getSafeIdListData(TAG_SUBTASK_IDS);
	}
	
	@Override
	public String toString()
	{
		return getLabel();
	}
	
	@Override
	public String getPseudoData(String fieldTag)
	{
		if(fieldTag.equals(PSEUDO_TAG_STRATEGY_LABEL))
			return getLabelOfTaskParent();
		
		if(fieldTag.equals(PSEUDO_TAG_INDICATOR_LABEL))
			return getLabelOfTaskParent();
		
		if (fieldTag.equals(PSEUDO_TAG_RELEVANT_OBJECTIVE_REFS))
			return getRelevantDesireRefsAsString(ObjectiveSchema.getObjectType());
		
		if (fieldTag.equals(PSEUDO_TAG_RELEVANT_GOAL_REFS))
			return getRelevantDesireRefsAsString(GoalSchema.getObjectType());
		
		return super.getPseudoData(fieldTag);
	}
	
	private String getRelevantDesireRefsAsString(int desireType)
	{
		try
		{
			return getRelevantDesireRefs(desireType).toString();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}
	
	public ORefList getRelevantDesireRefs(int desireType) throws Exception
	{
		ORefSet relevantObjectives = new ORefSet(Desire.findAllRelevantDesires(getProject(), getRef(), desireType));
		RelevancyOverrideSet relevantOverrides = new RelevancyOverrideSet();
		return calculateRelevantRefList(relevantObjectives, relevantOverrides);
	}

	public boolean hasSubTasks()
	{
		return getSubtaskCount() > 0;
	}
		
	public ORefSet getTaskResources()
	{
		ORefSet resourceRefs = new ORefSet();
		ORefList assignmentRefs = getResourceAssignmentRefs();
		for (int i = 0; i < assignmentRefs.size(); ++i)
		{
			ResourceAssignment assignment = (ResourceAssignment.find(getProject(), assignmentRefs.get(i)));
			resourceRefs.add(assignment.getResourceRef());	
		}
		
		return resourceRefs;
	}
	
	public String getParentTypeCode()
	{
		if(isActivity())
			return StrategySchema.OBJECT_NAME;
		
		if(isMethod())
			return IndicatorSchema.OBJECT_NAME;
		
		Task owner = (Task)getOwner();
		if(owner.isActivity())
			return TaskSchema.ACTIVITY_NAME;
		if(owner.isMethod())
			return TaskSchema.METHOD_NAME;
		
		return TaskSchema.OBJECT_NAME;
	}
	
	@Override
	public ORefList getSubTaskRefs()
	{
		return new ORefList(TaskSchema.getObjectType(), getSubtaskIdList());
	}
	
	@Override
	public int getTotalShareCount()
	{
		ORefList parentRefs = getParentRefs();
		if (parentRefs.isEmpty())
			return 1;
			
		if(isTask())
		{
			ORef parentRef = parentRefs.get(0);
			Task parentTask = Task.find(getObjectManager(), parentRef);
			return parentTask.getTotalShareCount();
		}
		
		return parentRefs.size();
	}

	public ORefList getParentRefs()
	{
		try
		{
			int parentType = getTypeOfParent();
			ORefList parentRefs = findObjectsThatReferToUs(parentType);
		
			return parentRefs;
		}
		catch (UnknownTaskParentTypeException e)
		{
			EAM.logVerbose(getRef() + " " + EAM.text("Task does not have a parent"));
			return new ORefList();
		}
	}
	
	@Override
	protected TimePeriodCostsMap getTotalTimePeriodCostsMapForAssignments(String tag) throws Exception
	{
		TimePeriodCostsMap timePeriodCostsMap = super.getTotalTimePeriodCostsMapForAssignments(tag);
		double totalShareCount = getTotalShareCount();
		if (totalShareCount > 1)
			timePeriodCostsMap.divideBy(new OptionalDouble(totalShareCount));
		
		return timePeriodCostsMap;
	}
	
	public int getTypeOfParent() throws UnknownTaskParentTypeException
	{
		if(isTask())
			return TaskSchema.getObjectType();
		
		if(isMethod())
			return IndicatorSchema.getObjectType();
		
		if(isActivity())
			return StrategySchema.getObjectType();
		
		throw new UnknownTaskParentTypeException();
	}
	
	public static boolean canOwnTask(int selectedType)
	{
		if (Indicator.is(selectedType))
			return true;
		
		if (Strategy.is(selectedType))
			return true;
		
		return Task.is(selectedType);
	}

	private String getLabelOfTaskParent()
	{

		BaseObject parent = getOwner();
		if(parent == null)
		{
			EAM.logWarning("Parent of task " + getId() + " not found: " + getOwnerRef());
			return "(none)";
		}
		return parent.getData(BaseObject.TAG_LABEL);
	}
	
	public static String getChildTaskTypeCode(int parentType)
	{
		if(parentType == StrategySchema.getObjectType())
			return TaskSchema.ACTIVITY_NAME;
		
		if(parentType == IndicatorSchema.getObjectType())
			return TaskSchema.METHOD_NAME;
		
		return TaskSchema.OBJECT_NAME;
	}
	
	public static String getTaskIdsTag(BaseObject container) throws Exception
	{
		int type = container.getType();
		switch(type)
		{
			case ObjectType.TASK:
				return Task.TAG_SUBTASK_IDS;
				
			case ObjectType.STRATEGY:
				return Strategy.TAG_ACTIVITY_IDS;
				
			case ObjectType.INDICATOR:
				return Indicator.TAG_METHOD_IDS;
		}
		
		throw new Exception("getTaskIdsTag called for non-task container type " + type);
	}
	
	public static boolean isMethod(Project projectToUse, ORef ref)
	{
		return is(projectToUse, ref, TaskSchema.METHOD_NAME);	
	}
	
	public static boolean isActivity(Project projectToUse, ORef ref)
	{
		return is(projectToUse, ref, TaskSchema.ACTIVITY_NAME);
	}
	
	private static boolean is(Project projectToUse, ORef ref, String objectTypeName)
	{
		if (!is(ref))
			return false;
		
		Task task = Task.find(projectToUse, ref);
		return task.is(objectTypeName);
	}

	public static boolean isActivity(BaseObject baseObject)
	{
		if (Task.is(baseObject))
			return ((Task) baseObject).isActivity();
			
		return false;
	}
	
	public static boolean is(BaseObject object)
	{
		if(object == null)
			return false;
		return is(object.getRef());
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == TaskSchema.getObjectType();
	}
	
	public static Task find(ObjectManager objectManager, ORef taskRef)
	{
		return (Task) objectManager.findObject(taskRef);
	}
	
	public static Task find(Project project, ORef taskRef)
	{
		return find(project.getObjectManager(), taskRef);
	}
	
	public final static String TAG_SUBTASK_IDS = "SubtaskIds";
	public final static String TAG_DETAILS = "Details";
	
	public final static String PSEUDO_TAG_STRATEGY_LABEL = "StrategyLabel";
	public final static String PSEUDO_TAG_INDICATOR_LABEL = "IndicatorLabel";
	public static final String PSEUDO_TAG_RELEVANT_OBJECTIVE_REFS = "PseudoTaskRelevantObjectiveRefs";
	public static final String PSEUDO_TAG_RELEVANT_GOAL_REFS = "PseudoTaskRelevantGoalRefs";
	
	private String cachedObjectTypeName;
}
