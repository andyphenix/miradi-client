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
package org.miradi.dialogs.planning.upperPanel;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

import org.miradi.commands.Command;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogs.planning.AssignmentDateUnitsTableModel;
import org.miradi.dialogs.planning.propertiesPanel.PlanningViewAbstractTreeTableSyncedTableModel;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.ProjectResourceLeaderAtTopSorter;
import org.miradi.objecthelpers.TimePeriodCosts;
import org.miradi.objecthelpers.TimePeriodCostsMap;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.Desire;
import org.miradi.objects.Factor;
import org.miradi.objects.Goal;
import org.miradi.objects.Indicator;
import org.miradi.objects.Measurement;
import org.miradi.objects.Objective;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Strategy;
import org.miradi.objects.SubTarget;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.ThreatReductionResult;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.CustomPlanningColumnsQuestion;
import org.miradi.questions.EmptyChoiceItem;
import org.miradi.questions.PriorityRatingQuestion;
import org.miradi.questions.ProgressReportShortStatusQuestion;
import org.miradi.questions.ResourceTypeQuestion;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.TaglessChoiceItem;
import org.miradi.questions.WorkPlanColumnConfigurationQuestion;
import org.miradi.schemas.ProjectResourceSchema;
import org.miradi.schemas.ResourceAssignmentSchema;
import org.miradi.utils.CodeList;
import org.miradi.utils.CommandVector;
import org.miradi.utils.DateRange;
import org.miradi.utils.DateUnitEffort;
import org.miradi.utils.DateUnitEffortList;
import org.miradi.utils.OptionalDouble;
import org.miradi.utils.Translation;
import org.miradi.views.planning.doers.TreeNodeDeleteDoer;
import org.miradi.views.summary.SummaryPlanningWorkPlanSubPanel;

public class PlanningViewMainTableModel extends PlanningViewAbstractTreeTableSyncedTableModel
{
	public PlanningViewMainTableModel(Project projectToUse, RowColumnBaseObjectProvider providerToUse, PlanningTreeRowColumnProvider rowColumnProviderToUse) throws Exception
	{
		super(projectToUse, providerToUse);
		
		rowColumnProvider = rowColumnProviderToUse;
		
		updateColumnsToShow();
	}

	public void updateColumnsToShow() throws Exception
	{
		columnsToShow = new CodeList(getVisibleColumnCodes(project));
		omitColumnTagsRepresentedByColumnTables();
		fireTableStructureChanged();
	}

	@Override
	public Color getCellBackgroundColor(int column)
	{
		String columnTag = getColumnTag(column);
		
		if (isWhoColumn(columnTag))
			return AppPreferences.RESOURCE_TABLE_BACKGROUND;
		
		if(isWhenColumn(columnTag))
			return AppPreferences.getWorkUnitsBackgroundColor();
		
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int modelColumn)
	{
		String columnTag = getColumnTag(modelColumn);
		if (isWhoColumn(columnTag))
			return isWhoCellEditable(row, modelColumn);
		
		if (isWhenColumn(columnTag))
			return isWhenCellEditable(row, modelColumn);
		
		if (isCodeListColumn(modelColumn))
			return false;
		
		BaseObject baseObject = getBaseObjectForRow(row);
		String tagForCell = getTagForCell(baseObject.getType(), modelColumn);
		if (!baseObject.doesFieldExist(tagForCell))
			return false;
		
		if (baseObject.isPseudoField(tagForCell))
			return false;
		
		return true;
	}

	private boolean isWhoColumn(String columnTag)
	{
		return columnTag.equals(CustomPlanningColumnsQuestion.META_WHO_TOTAL);
	}
	
	private boolean isWhenColumn(String columnTag)
	{
		return columnTag.equals(CustomPlanningColumnsQuestion.META_WHEN_TOTAL);
	}
	
	@Override
	public boolean isWhenColumn(int modelColumn)
	{
		return isWhenColumn(getColumnTag(modelColumn));
	}
	
	private boolean isWhenCellEditable(int row, int modelColumn)
	{
		BaseObject baseObjectForRow = getBaseObjectForRowColumn(row, modelColumn);
		return isWhenEditable(baseObjectForRow);
	}
	
	public static boolean isWhenEditable(BaseObject baseObject)
	{
		try
		{
			if (!AssignmentDateUnitsTableModel.canOwnAssignments(baseObject.getRef()))
				return false;
			
			if (hasSubTasksWithData(baseObject))
				return false;
			
			if (baseObject.getResourceAssignmentRefs().isEmpty())
				return true;
			
			if (hasDifferentDateUnitEffortLists(baseObject))
				return false;

			if (hasUsableNumberOfDateUnitEfforts(baseObject))
				return true;
				
			return false;
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return false;
		}
	}

	private static boolean hasSubTasksWithData(BaseObject baseObject) throws Exception
	{
		ORefList subTaskRefs = baseObject.getSubTaskRefs();
		for (int index = 0; index < subTaskRefs.size(); ++index)
		{
			Task task = Task.find(baseObject.getProject(), subTaskRefs.get(index));
			if (task.getResourceAssignmentRefs().hasRefs())
				return true;
		}
		
		return false;
	}
	
	private static boolean hasDifferentDateUnitEffortLists(BaseObject baseObject) throws Exception
	{
		ORefList resourceAssignmentRefs = baseObject.getResourceAssignmentRefs();
		HashSet<DateUnitEffortList> dateUnitEffortLists = new HashSet<DateUnitEffortList>();
		for (int index = 0; index < resourceAssignmentRefs.size(); ++index)
		{			
			ResourceAssignment resourceAssignment = ResourceAssignment.find(baseObject.getProject(), resourceAssignmentRefs.get(index));
			dateUnitEffortLists.add(resourceAssignment.getDateUnitEffortList());
			if (dateUnitEffortLists.size() > 1)
				return true;
		}	
		
		return false;
	}

	private static boolean hasUsableNumberOfDateUnitEfforts(BaseObject baseObject) throws Exception
	{
		ORefList assignmentRefs = baseObject.getResourceAssignmentRefs();
		ResourceAssignment assignment = ResourceAssignment.find(baseObject.getProject(), assignmentRefs.getFirstElement());
		DateUnitEffortList effortList = assignment.getDateUnitEffortList();
		
		TimePeriodCostsMap timePeriodCostsMap = assignment.getResourceAssignmentsTimePeriodCostsMap();
		OptionalDouble totalWorkUnits = timePeriodCostsMap.calculateTimePeriodCosts(new DateUnit()).getTotalWorkUnits();
		if (totalWorkUnits.hasNonZeroValue())
			return false;
		
		if (effortList.size() > 2)
			return false;
		
		return true;
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		try
		{
			final BaseObject baseObjectForRow = getBaseObjectForRow(row);
			if (baseObjectForRow == null || baseObjectForRow.getRef().isInvalid())
				return;
			
			if (value == null)
				return;
			
			if (isWhenColumn(column))
			{
				setWhenValue(baseObjectForRow, createCodeList(value));
			}
			else if (isChoiceItemColumn(column))
			{
				ChoiceItem choiceItem = (ChoiceItem) value;
				setValueUsingCommand(baseObjectForRow.getRef(), getTagForCell(baseObjectForRow.getType(), column), choiceItem.getCode());
			}
			else
			{
				setValueUsingCommand(baseObjectForRow.getRef(), getTagForCell(baseObjectForRow.getType(), column), value.toString());
			}
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
		
 		super.setValueAt(value, row, column);	
	}

	private CodeList createCodeList(Object rawValue) throws Exception
	{
		return new CodeList(rawValue.toString());
	}

	private void setWhenValue(BaseObject baseObjectForRow, CodeList datesAsCodeList) throws Exception
	{
		getProject().executeBeginTransaction();
		try
		{
			clearDateUnitEfforts(baseObjectForRow);
			ORefList resourceAssignmentRefs = baseObjectForRow.getResourceAssignmentRefs();
			if (datesAsCodeList.hasData() && resourceAssignmentRefs.isEmpty())
				createResourceAssignment(baseObjectForRow, datesAsCodeList);
			
			if (datesAsCodeList.hasData() && resourceAssignmentRefs.hasRefs())
				updateResourceAssignments(resourceAssignmentRefs, datesAsCodeList);
			
			if (datesAsCodeList.isEmpty() && resourceAssignmentRefs.size() == 1)
				deleteEmptyResourceAssignment(resourceAssignmentRefs.getFirstElement());
		}
		finally
		{
			getProject().executeEndTransaction();
		}
	}

	private void updateResourceAssignments(ORefList resourceAssignmentRefs, CodeList datesAsCodeList) throws Exception
	{
		DateUnitEffortList dateUnitEffortList = createDateUnitEffortList(datesAsCodeList);
		for (int index = 0; index < resourceAssignmentRefs.size(); ++index)
		{			
			setDateUnitEffortList(resourceAssignmentRefs.get(index), dateUnitEffortList);
		}
	}

	private void clearDateUnitEfforts(BaseObject baseObjectForRow) throws Exception
	{
		ORefList resourceAssignmentRefs = baseObjectForRow.getResourceAssignmentRefs();
		for (int index = 0; index < resourceAssignmentRefs.size(); ++index)
		{			
			ResourceAssignment resourceAssignment = ResourceAssignment.find(getProject(), resourceAssignmentRefs.get(index));
			Command clearDateUnitEffortList = new CommandSetObjectData(resourceAssignment, ResourceAssignment.TAG_DATEUNIT_EFFORTS, new DateUnitEffortList().toString());
			getProject().executeCommand(clearDateUnitEffortList);
		}
	}

	private void deleteEmptyResourceAssignment(ORef resourceAssignmentRef) throws Exception
	{	
		ResourceAssignment resourceAssignment = ResourceAssignment.find(getProject(), resourceAssignmentRef);
		if (resourceAssignment.isEmpty())
		{
			CommandVector removeAssignmentCommands = TreeNodeDeleteDoer.buildCommandsToDeleteAnnotation(getProject(), resourceAssignment, BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS);
			getProject().executeCommands(removeAssignmentCommands);
		}
	}

	private void createResourceAssignment(BaseObject baseObjectForRow, CodeList datesAsCodeList) throws Exception
	{
		CommandCreateObject createResourceAssignment = new CommandCreateObject(ResourceAssignmentSchema.getObjectType());
		getProject().executeCommand(createResourceAssignment);

		ORef resourceAssignmentRef = createResourceAssignment.getObjectRef();
		DateUnitEffortList dateUnitEffortList = createDateUnitEffortList(datesAsCodeList);
		setDateUnitEffortList(resourceAssignmentRef, dateUnitEffortList);

		CommandSetObjectData appendResourceAssignment = CommandSetObjectData.createAppendIdCommand(baseObjectForRow, BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS, resourceAssignmentRef);
		getProject().executeCommand(appendResourceAssignment);
	}

	private void setDateUnitEffortList(ORef resourceAssignmentRef, DateUnitEffortList dateUnitEffortList) throws Exception
	{
		CommandSetObjectData addEffortList = new CommandSetObjectData(resourceAssignmentRef, ResourceAssignment.TAG_DATEUNIT_EFFORTS, dateUnitEffortList.toString());
		getProject().executeCommand(addEffortList);
	}

	private DateUnitEffortList createDateUnitEffortList(CodeList datesAsCodeList)
	{
		final int NO_VALUE = 0;
		DateUnitEffortList dateUnitEffortList = new DateUnitEffortList();
		for (int index = 0; index < datesAsCodeList.size(); ++index)
		{
			DateUnit dateUnit = new DateUnit(datesAsCodeList.get(index));
			if (dateUnitEffortList.getDateUnitEffortForSpecificDateUnit(dateUnit) == null)
				dateUnitEffortList.add(new DateUnitEffort(dateUnit, NO_VALUE));
		}

		return dateUnitEffortList;
	}

	private boolean isWhoCellEditable(int row, int modelColumn)
	{
		try
		{
			BaseObject baseObjectForRow = getBaseObjectForRowColumn(row, modelColumn);
			if (!AssignmentDateUnitsTableModel.canOwnAssignments(baseObjectForRow.getRef()))
				return false;

			if (doAnySubtasksHaveAnyWorkUnitData(baseObjectForRow))
				return false;

			return doAllResourceAssignmentsHaveIdenticalWorkUnits(row, modelColumn);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return false;		
		}
	}

	private boolean doAnySubtasksHaveAnyWorkUnitData(BaseObject baseObjectForRow) throws Exception
	{
		TimePeriodCostsMap timePeriodCostsMap = baseObjectForRow.getTotalTimePeriodCostsMapForSubTasks(baseObjectForRow.getSubTaskRefs(), BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS);
		TimePeriodCosts wholeProjectTimePeriodCosts = timePeriodCostsMap.calculateTimePeriodCosts(new DateUnit());
		OptionalDouble totalSubtaskWorkUnitsForAllTimePeriods = wholeProjectTimePeriodCosts.getTotalWorkUnits();

		return totalSubtaskWorkUnitsForAllTimePeriods.hasValue();
	}
	
	private boolean doAllResourceAssignmentsHaveIdenticalWorkUnits(int row, int modelColumn) throws Exception
	{
			BaseObject baseObjectForRow = getBaseObjectForRowColumn(row, modelColumn);
			ORefList resourceAssignments = baseObjectForRow.getResourceAssignmentRefs();
			DateUnitEffortList expectedDateUnitEffortList = null;
			for (int index = 0; index < resourceAssignments.size(); ++index)
			{
				ResourceAssignment resourceAssignment = ResourceAssignment.find(getProject(), resourceAssignments.get(index));
				DateUnitEffortList thisDateUnitEffortList = resourceAssignment.getDateUnitEffortList();
				if (expectedDateUnitEffortList == null)
					expectedDateUnitEffortList = thisDateUnitEffortList;
				
				if (!expectedDateUnitEffortList.equals(thisDateUnitEffortList))
					return false;
			}
			
			return true;
	}
	
	@Override
	public Class getCellQuestion(int row, int modelColumn)
	{
		if (isProjectResourceTypeColumn(modelColumn))
			return ResourceTypeQuestion.class;
		
		if (isPriortyColumn(modelColumn))
			return PriorityRatingQuestion.class;
		
		return null;
	}
	
	@Override
	public boolean isChoiceItemColumn(int column)
	{
		String columnTag = getColumnTag(column);
		if(isPriortyColumn(column))
			return true;
		
		if(columnTag.equals(CustomPlanningColumnsQuestion.META_CURRENT_RATING))
			return true;
		
		if(isProjectResourceTypeColumn(column))
			return true;
		
		return false;
	}
	
	private boolean isProjectResourceTypeColumn(int column)
	{
		return getColumnTag(column).equals(ProjectResource.TAG_RESOURCE_TYPE);
	}
	
	private boolean isPriortyColumn(int column)
	{
		return getColumnTag(column).equals(Indicator.TAG_PRIORITY);
	}
	
	@Override
	public boolean isProgressColumn(int column)
	{
		String columnTag = getColumnTag(column);
		if(columnTag.equals(Strategy.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
			return true;
		if(columnTag.equals(Objective.PSEUDO_TAG_LATEST_PROGRESS_PERCENT_COMPLETE))
			return true;
		
		return false;
	}

	private void omitColumnTagsRepresentedByColumnTables()
	{
		String[] codesToOmit = new String[]{
											Measurement.META_COLUMN_TAG,
											Indicator.META_COLUMN_TAG,
											WorkPlanColumnConfigurationQuestion.META_RESOURCE_ASSIGNMENT_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_PROJECT_RESOURCE_WORK_UNITS_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_PROJECT_RESOURCE_BUDGET_DETAILS_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_EXPENSE_ASSIGNMENT_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_FUNDING_SOURCE_EXPENSE_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_BUDGET_DETAIL_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_FUNDING_SOURCE_BUDGET_DETAILS_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_ACCOUNTING_CODE_BUDGET_DETAILS_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_ACCOUNTING_CODE_EXPENSE_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_ANALYSIS_WORK_UNITS_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_ANALYSIS_EXPENSES_CODE,
											WorkPlanColumnConfigurationQuestion.META_ANALYSIS_BUDGET_DETAILS_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_ONE_WORK_UNITS_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_ONE_EXPENSE_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_ONE_BUDGET_DETAILS_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_TWO_WORK_UNITS_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_TWO_EXPENSE_COLUMN_CODE,
											WorkPlanColumnConfigurationQuestion.META_BUDGET_CATEGORY_TWO_BUDGET_DETAILS_COLUMN_CODE,
		};
		
		columnsToShow.subtract(new CodeList(codesToOmit));
	}
	
	public int getColumnCount()
	{
		return columnsToShow.size();
	}

	public String getColumnTag(int modelColumn)
	{
		return columnsToShow.get(modelColumn);
	}
	
	@Override
	public String getColumnName(int column)
	{
		String columnTag = getColumnTag(column);
		String columnName = EAM.fieldLabel(ObjectType.FAKE, columnTag);
		if (doesColumnHeaderNeedAsterisk(columnTag))
			columnName += HAS_DATA_OUTSIDE_OF_PROJECT_DATE_ASTERISK;
		
		return columnName;
	}
	
	private boolean doesColumnHeaderNeedAsterisk(String columnTag)
	{
		boolean isAsteriskColumn = isWhenColumn(columnTag) || isWhoColumn(columnTag);
		if (!isAsteriskColumn)
			return false;
		
		return SummaryPlanningWorkPlanSubPanel.hasDataOutsideOfProjectDateRange(getProject());
	}
	
	public ChoiceItem getChoiceItemAt(int row, int column)
	{
		BaseObject baseObject = getBaseObjectForRow(row);
		if(baseObject == null)
			return new EmptyChoiceItem();
		
		try
		{	
			String columnTag = getTagForCell(baseObject.getType(), column);
			if(isWhoColumn(columnTag))
				return appendedProjectResources(baseObject);
			if (columnTag.equals(CustomPlanningColumnsQuestion.META_CURRENT_RATING))
				return getRatingChoiceItem(baseObject);
			
			if (! baseObject.doesFieldExist(columnTag))
				return new EmptyChoiceItem();

			String rawValue = "";
			if(columnTag.equals(BaseObject.PSEUDO_TAG_WHEN_TOTAL))
				rawValue = getProject().getTimePeriodCostsMapsCache().getWhenTotalAsString(baseObject);
			else if (baseObject.isPseudoField(columnTag))
				rawValue = baseObject.getPseudoData(columnTag);
			else
				rawValue = baseObject.getData(columnTag);
			
			if (rawValue == null)
				return new EmptyChoiceItem();
			
			if(columnTag.equals(Indicator.TAG_PRIORITY))
				return new PriorityRatingQuestion().findChoiceByCode(rawValue);
			
			if(columnTag.equals(ProjectResource.TAG_RESOURCE_TYPE))
				return getProject().getQuestion(ResourceTypeQuestion.class).findChoiceByCode(rawValue);
			
			if(columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
				return new ProgressReportShortStatusQuestion().findChoiceByCode(rawValue);
			
			if (Desire.isDesire(baseObject.getRef()) && columnTag.equals(Desire.PSEUDO_TAG_RELEVANT_INDICATOR_REFS))
				return createAppendedRelevantIndicatorLabels((Desire)baseObject);
			
			if (Desire.isDesire(baseObject.getRef()) && columnTag.equals(Desire.PSEUDO_TAG_RELEVANT_ACTIVITY_REFS))
				return createAppendedRelevantActivityLabels((Desire)baseObject);
			
			if(isWhenColumn(columnTag))
				return getFilteredWhen(baseObject);
			
			return new TaglessChoiceItem(rawValue);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return new TaglessChoiceItem(EAM.text("[Error]"));
		}
	}
	
	private ChoiceItem createAppendedRelevantActivityLabels(Desire desire) throws Exception
	{
		return createLabelsOnSingleLine(desire, desire.getRelevantActivityRefs());
	}

	private ChoiceItem createAppendedRelevantIndicatorLabels(Desire desire) throws Exception
	{
		return createLabelsOnSingleLine(desire, desire.getRelevantIndicatorRefList());
	}
	
	private ChoiceItem createLabelsOnSingleLine(Desire desire, ORefList refs)
	{
		String labelsOnASingleLine = desire.getLabelsAsMultiline(refs);
		
		return new TaglessChoiceItem(labelsOnASingleLine);
	}

	private ChoiceItem getRatingChoiceItem(BaseObject baseObject) throws Exception
	{
		if (Cause.is(baseObject))
			return getThreatRatingChoiceItem((Cause) baseObject);
		
		if (Target.is(baseObject))
			return getTargetViabilityRating((AbstractTarget) baseObject);
		
		if (Strategy.is(baseObject))
			return getStrategyRating((Strategy) baseObject);
		
		return new EmptyChoiceItem();
	}

	private ChoiceItem getStrategyRating(Strategy strategy)
	{
		return strategy.getStrategyRating();
	}

	private ChoiceItem getThreatRatingChoiceItem(Cause threat) throws Exception
	{
		if (threat.isContributingFactor())
			return new EmptyChoiceItem();
		
		return getProject().getThreatRatingFramework().getThreatThreatRatingValue(threat.getRef());
	}
	
	private ChoiceItem getTargetViabilityRating(AbstractTarget abstractTarget)
	{
		ChoiceQuestion question = getProject().getQuestion(StatusQuestion.class);
		
		return question.findChoiceByCode(abstractTarget.getTargetViability());
	}

	private ChoiceItem getFilteredWhen(BaseObject baseObject) throws Exception
	{
		TimePeriodCostsMap totalTimePeriodCostsMap = calculateTimePeriodCostsMap(baseObject, getRowColumnProvider().getWorkPlanBudgetMode());
		DateRange projectStartEndDateRange = getProject().getProjectCalendar().getProjectPlanningDateRange();
		DateRange rolledUpResourceAssignmentsDateRange = totalTimePeriodCostsMap.getRolledUpDateRange(projectStartEndDateRange, getResourcesFilter());
		String rolledUpResourceAssignmentsWhen = getProject().getProjectCalendar().convertToSafeString(rolledUpResourceAssignmentsDateRange);
		
		return new TaglessChoiceItem(rolledUpResourceAssignmentsWhen);
	}
	
	@Override
	protected TimePeriodCostsMap getTotalTimePeriodCostsMap(BaseObject baseObject) throws Exception
	{		
		return baseObject.getResourceAssignmentsTimePeriodCostsMap();
	}
	
	private ChoiceItem appendedProjectResources(BaseObject baseObject) throws Exception
	{
		TimePeriodCosts timePeriodCosts = calculateTimePeriodCosts(baseObject, new DateUnit(), getRowColumnProvider().getWorkPlanBudgetMode());
		timePeriodCosts.retainWorkUnitDataRelatedToAnyOf(getResourcesFilter());
		ORefSet filteredResources = new ORefSet(timePeriodCosts.getWorkUnitsRefSetForType(ProjectResourceSchema.getObjectType()));
		if (baseObject.getLeaderResourceRef().isValid())
			filteredResources.add(baseObject.getLeaderResourceRef());
		ORefSet unspecifiedBaseObjectRefs = getInvalidRefs(filteredResources);
		filteredResources.removeAll(unspecifiedBaseObjectRefs);
		Vector<ProjectResource> sortedProjectResources = toProjectResources(filteredResources);
		if (baseObject.doesFieldExist(BaseObject.TAG_LEADER_RESOURCE))
		{
			final ORef leaderResourceRef = baseObject.getRef(BaseObject.TAG_LEADER_RESOURCE);
			Collections.sort(sortedProjectResources, new ProjectResourceLeaderAtTopSorter(leaderResourceRef));
		}
	
		final ORefList sortedProjectResourceRefs = new ORefList(sortedProjectResources);
		sortedProjectResourceRefs.addAll(new ORefList(unspecifiedBaseObjectRefs));
		Vector<String> sortedNames = getResourceNames(sortedProjectResourceRefs, baseObject);		
		String appendedResources = createAppendedResourceNames(sortedNames);
		
		return new TaglessChoiceItem(appendedResources);
	}

	public ORefSet getInvalidRefs(ORefSet filteredResources)
	{
		ORefSet invalidRefs = new ORefSet();
		for(ORef ref : filteredResources)
		{
			if (ref.isInvalid())
				invalidRefs.add(ref);
		}
		
		return invalidRefs;
	}
	
	private Vector<ProjectResource> toProjectResources(ORefSet resourcesRefs) throws Exception
	{
		Vector<ProjectResource> resources = new Vector<ProjectResource>();
		for(ORef resourceRef : resourcesRefs)
		{
			resources.add(ProjectResource.find(getProject(), resourceRef));
		}
		
		return resources;
	}

	private String createAppendedResourceNames(Vector<String> sortedNames)
	{
		boolean isFirstIteration = true; 
		String appendedResources = "";
		for(String resourceName : sortedNames)
		{
			if (!isFirstIteration)
				appendedResources += ", ";
			
			appendedResources += resourceName;
			isFirstIteration = false;	
		}
		return appendedResources;
	}

	private Vector<String> getResourceNames(ORefList filteredResources, BaseObject parentBaseObject)
	{
		Vector<String> names = new Vector<String>();
		for(ORef resourceRef : filteredResources)
		{
			names.add(getWhoName(resourceRef, parentBaseObject));
		}
		
		return names;
	}
	
	private String getWhoName(ORef resourceRef, BaseObject parentBaseObject)
	{
		if (resourceRef.isInvalid())
			return Translation.getNotSpecifiedText();

		ProjectResource projectResource = ProjectResource.find(getProject(), resourceRef);
		final String who = projectResource.getWho();
		if (parentBaseObject.getLeaderResourceRef().equals(resourceRef))
			return who + "*";
		
		return who;	
	}

	public Object getValueAt(int row, int column)
	{
		return getChoiceItemAt(row, column);
	}

	@Override
	public String getTagForCell(int nodeType, int column)
	{
		String columnTag = getColumnTag(column);
		if (columnTag.equals(WorkPlanColumnConfigurationQuestion.COMMENTS_COLUMN_CODE))
		{
			columnTag = Factor.TAG_COMMENTS;
		}
		
		if(ProjectMetadata.is(nodeType))
		{
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
				return "";
		}
		if(ConceptualModelDiagram.is(nodeType))
		{
			if(isDetailsColumn(column))
				return ConceptualModelDiagram.TAG_DETAIL;
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
				return "";
		}
		if(ResultsChainDiagram.is(nodeType))
		{
			if(isDetailsColumn(column))
				return ResultsChainDiagram.TAG_DETAIL;
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
				return "";
		}
		if(AbstractTarget.isAbstractTarget(nodeType))
		{
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
				return "";
		}
		if(Target.is(nodeType))
		{
			if (columnTag.equals(Factor.PSEUDO_TAG_TAXONOMY_CODE_VALUE))
				return Target.PSEUDO_TAG_HABITAT_ASSOCIATION_VALUE;
		}		
		if(Desire.isDesire(nodeType))
		{
			if (columnTag.equals(Factor.TAG_COMMENTS))
				return Desire.TAG_COMMENTS;
			
			if (columnTag.equals(Factor.PSEUDO_TAG_INDICATORS))
				return Desire.PSEUDO_TAG_RELEVANT_INDICATOR_REFS;
			
			if (columnTag.equals(Strategy.PSEUDO_TAG_ACTIVITIES))
				return Desire.PSEUDO_TAG_RELEVANT_ACTIVITY_REFS;
		}
		if(Goal.is(nodeType))
		{
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
				return Goal.PSEUDO_TAG_LATEST_PROGRESS_PERCENT_COMPLETE;
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_DETAILS))
				return Goal.PSEUDO_TAG_LATEST_PROGRESS_PERCENT_DETAILS;
		}
		if(Cause.is(nodeType))
		{
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
				return "";
		}
		if(ThreatReductionResult.is(nodeType))
		{
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
				return "";
		}
		if(Objective.is(nodeType))
		{
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
				return Objective.PSEUDO_TAG_LATEST_PROGRESS_PERCENT_COMPLETE;
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_DETAILS))
				return Objective.PSEUDO_TAG_LATEST_PROGRESS_PERCENT_DETAILS;
		}
		if(Task.is(nodeType))
		{
			if(isDetailsColumn(column))
				return Task.TAG_DETAILS;
		}
		if(Indicator.is(nodeType))
		{
			if (isDetailsColumn(column))
				return Indicator.TAG_DETAIL;
		}
		if(Measurement.is(nodeType))
		{
			if (isDetailsColumn(column))
				return Measurement.TAG_DETAIL;
			if (columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
				return "";
		}
		if(ResourceAssignment.is(nodeType))
		{
			if (isWhoColumn(columnTag))
				return ResourceAssignment.PSEUDO_TAG_PROJECT_RESOURCE_LABEL;
			if (columnTag.equals(Indicator.PSEUDO_TAG_FACTOR))
				return ResourceAssignment.PSEUDO_TAG_OWNING_FACTOR_NAME;
		}
		if(Factor.isFactor(nodeType))
		{
			if (isDetailsColumn(column))
				return Factor.TAG_TEXT;
		}
		if (SubTarget.is(nodeType))
		{
			if (isDetailsColumn(column))
				return SubTarget.TAG_DETAIL;
		}
		
		return columnTag;
	}

	private boolean isDetailsColumn(int column)
	{
		if (getColumnTag(column).equals(WorkPlanColumnConfigurationQuestion.DETAILS_COLUMN_CODE))
			return true;
		
		return getColumnTag(column).equals(Desire.TAG_FULL_TEXT);
	}
	
	private CodeList getVisibleColumnCodes(Project projectToUse) throws Exception
	{
		return getRowColumnProvider().getColumnCodesToShow();
	}
	
	private PlanningTreeRowColumnProvider getRowColumnProvider()
	{
		return rowColumnProvider;
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return UNIQUE_MODEL_IDENTIFIER;
	}
				
	private static final String UNIQUE_MODEL_IDENTIFIER = "PlanningViewMainTableModel";
	
	private static final String HAS_DATA_OUTSIDE_OF_PROJECT_DATE_ASTERISK = "*";

	private CodeList columnsToShow;
	private PlanningTreeRowColumnProvider rowColumnProvider;
}
