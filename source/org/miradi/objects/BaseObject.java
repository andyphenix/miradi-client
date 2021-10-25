/* 
Copyright 2005-2021, Foundations of Success, Bethesda, Maryland
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
package org.miradi.objects;

import org.martus.util.MultiCalendar;
import org.miradi.commands.CommandDeleteObject;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.ChainWalker;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.objectdata.*;
import org.miradi.objecthelpers.*;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.project.ProjectTotalCalculatorStrategy;
import org.miradi.questions.*;
import org.miradi.schemas.*;
import org.miradi.utils.*;
import org.miradi.utils.OptionalDouble;

import java.awt.*;
import java.text.ParseException;
import java.util.*;

abstract public class BaseObject
{
	public BaseObject(final ObjectManager objectManagerToUse, final BaseId idToUse, final BaseObjectSchema schemaToUse)
	{
		objectManager = objectManagerToUse;
		schema = schemaToUse;
		setId(idToUse);
		createFieldsFromBaseObjectSchema();
	}

	public String getData(String fieldTag)
	{
		if(TAG_ID.equals(fieldTag))
			return id.toString();
		
		if (TAG_EMPTY.equals(fieldTag))
			return "";
		
		if(!doesFieldExist(fieldTag))
			throw new RuntimeException("Attempted to get data for bad field: " + fieldTag + " in object type: " + getClass().getSimpleName());

		String data = getField(fieldTag).get();
		if(data == null)
			throw new RuntimeException("BaseObject " + getRef() + " tag " + fieldTag + " was null");
		return data;
	}
	
	public String getStringData(String tag)
	{
		StringData data = (StringData)getField(tag);
		return data.get();
	}
	
	public ChoiceItem getChoiceItemData(String tag)
	{
		ChoiceData data = (ChoiceData)getField(tag);
		ChoiceQuestion question = data.getChoiceQuestion();
		String code = data.get();
		return question.findChoiceByCode(code);
	}

	public BaseId getBaseIdData(String tag)
	{
		return ((BaseIdData)getField(tag)).getId();
	}
	
	public int getIntegerData(String tag)
	{
		IntegerData data = (IntegerData)getField(tag);
		return data.asInt();
	}
	
	public double getSafeNumberData(String tag)
	{
		NumberData data = (NumberData)getField(tag);
		return data.getSafeValue();
	}
	
	public double getFloatData(String tag)
	{
		FloatData data = (FloatData)getField(tag);
		return data.asFloat();
	}
	
	public boolean getBooleanData(String tag)
	{
		BooleanData data = (BooleanData)getField(tag);
		return data.asBoolean();
	}
	
	public MultiCalendar getDateData(String tag)
	{
		DateData data = (DateData)getField(tag);
		return data.getDate();
	}
	
	public Dimension getDimensionData(String tag)
	{
		DimensionData data = (DimensionData)getField(tag);
		return data.getDimension();
	}
	
	public void setDimensionData(String tag, Dimension value)
	{
		DimensionData data = (DimensionData)getField(tag);
		data.setDimension(value);
	}
	
	public Point getPointData(String tag)
	{
		DiagramPointData data = (DiagramPointData)getField(tag);
		return data.getPoint();
	}
	
	public PointList getPointListData(String tag)
	{
		PointListData data = (PointListData)getField(tag);
		return data.getPointList();
	}
	
	public void setPointData(String tag, Point point)
	{
		DiagramPointData data = (DiagramPointData)getField(tag);
		data.setPoint(point);
	}
	
	public CodeList getCodeListData(String tag)
	{
		CodeListData data = (CodeListData)getField(tag);
		return data.getCodeList();
	}
	
	public CodeList getTagListData(String tag)
	{
		TagListData data = (TagListData)getField(tag);
		return data.getCodeList();
	}
	
	public IdList getSafeIdListData(String fieldTag)
	{
		//NOTE: BaseObject used to always have these fields
		if (!doesFieldExist(fieldTag))
		{
			final int objectType = getAnnotationType(fieldTag);
			return new IdList(objectType);
		}

		ObjectData field = getField(fieldTag);
		if(field.isIdListData())
			return ((IdListData)field).getIdList().createClone();

		throw new RuntimeException("Attempted to get IdList data from non-IdList field " + fieldTag);
	}
	
	public ORefList getSafeRefListData(String fieldTag)
	{
		if (doesFieldExist(fieldTag))
			return getRefList(fieldTag);
		
		//NOTE: BaseObject used to always have these fields
		return new ORefList();
	}

	private ORefList getRefList(String fieldTag)
	{
		ObjectData field = getField(fieldTag);
		if(field.isRefListData())
		{
			RefListData refListField = (RefListData)field;
			ORefList refList = refListField.getRefList();
			return new ORefList(refList);
		}
		
		if(field.isIdListData())
		{
			IdList isList = getSafeIdListData(fieldTag);
			ORefList refList = new ORefList(isList);
			return new ORefList(refList);
		}
			
		throw new RuntimeException("Attempted to get RefList data from non-RefList field " + fieldTag);
	}
	
	public ORef getRefData(String fieldTag)
	{
		ObjectData field = getField(fieldTag);
		if(field.isRefData())
			return new ORef(((ORefData)field).getRef());
		
		if(field.isBaseIdData())
			return new ORef(((BaseIdData)field).getRef());
		
		throw new RuntimeException("Attempted to get Ref data from non-Ref field " + fieldTag);
	}
	
	public void setRefData(String fieldTag, ORef ref)
	{
		ObjectData field = getField(fieldTag);
		if(field.isRefData())
			((ORefData)field).set(ref);
		else if(field.isBaseIdData())
			((BaseIdData)field).setRef(ref);
		else
			throw new RuntimeException("Attempted to set Ref data on non-Ref field " + fieldTag);
	}

	public CodeToCodeMap getCodeToCodeMapData(String tag)
	{
		return ((CodeToCodeMapData)getField(tag)).getCodeToCodeMap();
	}
	
	public CodeToUserStringMap getCodeToUserStringMapData(String tag)
	{
		return ((CodeToUserStringMapData)getField(tag)).getCodeToUserStringMap();
	}
	
	public CodeToChoiceMap getCodeToChoiceMapData(String tag)
	{
		return ((CodeToChoiceMapData)getField(tag)).getStringToChoiceMap();
	}
	
	public CodeToCodeListMap getCodeToCodeListMapData(String tag)
	{
		return ((CodeToCodeListMapData)getField(tag)).getStringToCodeListMap();
	}
	
	public StringRefMap getStringRefMapData(String tag)
	{
		StringRefMapData data = (StringRefMapData)getField(tag);
		return data.getStringRefMap();
	}
	
	public Vector<DateUnit> getDateUnitListData(String tag)
	{
		DateUnitListData data = (DateUnitListData)getField(tag);
		return data.getDateUnits();
	}
	
	public Vector<ORefList> getRefListListData(String tag) throws Exception
	{
		RefListListData data = (RefListListData)getField(tag);
		return data.convertToRefListVector();
	}
	
	public RelevancyOverrideSet getRawRelevancyOverrideData(String tag)
	{
		return ((RelevancyOverrideSetData)getField(tag)).getRawRelevancyOverrideSet();
	}

	public void loadFromJson(EnhancedJsonObject json) throws Exception
	{
		Set<String> tags = getTags();
		for (String tag : tags)
		{
			if (isPseudoField(tag))
				continue;
			
			final String value = getHtmlEncodedValue(json, tag);
			try
			{
				setData(tag, value);
			}
			catch(InvalidNumberException e)
			{
				String newValue = value.replaceAll("[^0-9\\-\\.,]", "");
				EAM.logWarning("Fixing bad numeric data in " + tag + " from " + value + " to " + newValue);
				setData(tag, newValue);
			}
		}
	}

	public String getHtmlEncodedValue(EnhancedJsonObject json, String tag) throws Exception
	{
		String value = json.optString(tag);
		ObjectData field = getField(tag);
		if (field.isUserText())
		{
			value = getHtmlDataFromNonHtml(tag, value);
		}
		else if(field.isCodeToUserStringMapData())
		{
			value = encodeIndividualMapValues(value);
		}
		
		return value;
	}

	private String encodeIndividualMapValues(String mapAsString) throws ParseException
	{
		CodeToUserStringMap map = new CodeToUserStringMap();

		EnhancedJsonObject json = new EnhancedJsonObject(mapAsString);
		EnhancedJsonObject innerJson = json.optJson("StringMap");
		Iterator it = innerJson.keys();
		while(it.hasNext())
		{
			String key = (String)it.next();
			String value = innerJson.getString(key);
			String encoded = XmlUtilities2.getXmlEncoded(value);
			map.putUserString(key, encoded);
		}
		
		return map.toJsonString();
	}

	private Set<String> getTags()
	{
		return getFields().keySet();
	}

	private HashMap<String, ObjectData> getFields()
	{
		return fields;
	}
	
	public ORef getORef(String tag) throws Exception
	{
		return ORef.createFromString(getData(tag));
	}
	
	public CodeList getCodeList(String tag) throws Exception
	{
		return new CodeList(getData(tag));
	}
	
	public boolean isIdListTag(String tag)
	{
		return getField(tag).isIdListData();
	}
	
	public boolean isRefList(String tag)
	{
		return getField(tag).isRefListData();
	}
	
	public ORef getRef(String tag)
	{
		return (getField(tag).getRef());
	}
	
	public boolean isRelevancyOverrideSet(String tag)
	{
		return false;
	}
			
	public int getAnnotationType(String tag)
	{
		if (tag.equals(TAG_RESOURCE_ASSIGNMENT_IDS))
			return ResourceAssignmentSchema.getObjectType();
		
		if (tag.equals(TAG_TIMEFRAME_IDS))
			return TimeframeSchema.getObjectType();

		if (tag.equals(TAG_PROGRESS_REPORT_REFS))
			return ProgressReportSchema.getObjectType();
		
		if (tag.equals(TAG_EXTENDED_PROGRESS_REPORT_REFS))
			return ExtendedProgressReportSchema.getObjectType();

		if (tag.equals(TAG_RESULT_REPORT_REFS))
			return ResultReportSchema.getObjectType();

		if (tag.equals(TAG_OUTPUT_REFS))
			return OutputSchema.getObjectType();

		throw new RuntimeException("Cannot find annotation type for " + tag);
	}
	
	public ORef getRef()
	{
		return new ORef(getType(), getId());
	}
	
	public ObjectManager getObjectManager()
	{
		return objectManager;
	}
	
	public Project getProject()
	{
		return objectManager.getProject();
	}
	
	public ChainWalker getNonDiagramChainWalker()
	{
		return getObjectManager().getDiagramChainWalker();
	}
	
	public ChainWalker getDiagramChainWalker()
	{
		return getObjectManager().getDiagramChainWalker();
	}
		
	@Override
	public int hashCode()
	{
		return getRef().hashCode();
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof BaseObject))
			return false;
		
		BaseObject other = (BaseObject)rawOther;
		return other.getRef().equals(getRef());
	}
	
	public String getSafeLabel(BaseObject baseObject)
	{
		if (baseObject == null)
			return "";
		
		return baseObject.getLabel();
	}
		
	public String getUUID()
	{
		return getData(TAG_UUID);
	}
	
	public String getLabel()
	{
		return getData(TAG_LABEL);
	}

	public String getShortLabel()
	{
		return "";
	}
	
	public String getFullName()
	{
		return combineShortLabelAndLabel();
	}
	
	protected String toFullNameWithCode(String codeToUse)
	{
		String result = "";
		if(codeToUse.length() > 0)
			result += codeToUse + ": ";
		
		result += combineShortLabelAndLabel();
		
		return result;
	}
	
	protected String toString(String defaultValue)
	{
		String result = getFullName();
		if(result.length() > 0)
			return result;
		
		return defaultValue;
	}
	
	public String getHtmlDataFromNonHtml(final String fieldTag, String nonHtmlDataValue) throws Exception
	{
		if (isNavigationField(fieldTag))
			throw new RuntimeException("Cannot convert non user data to html and set. Tag = " + fieldTag);
		
		return HtmlUtilities.convertPlainTextToHtmlText(nonHtmlDataValue);
	}

	public void setData(String fieldTag, String dataValue) throws Exception
	{
		if(TAG_ID.equals(fieldTag))
		{
			id = new BaseId(Integer.parseInt(dataValue));
			return;
		} 
		
		if(!doesFieldExist(fieldTag))
			throw new RuntimeException("Object Ref = " + getRef() + ". Attempted to set data for bad field: " + fieldTag);

		ORefSet oldReferrals = getAllReferencedObjects();
		getField(fieldTag).set(dataValue);
		ORefSet newReferrals = getAllReferencedObjects();
		if(getObjectManager() != null)
		{
			getObjectManager().updateReferrerCache(getRef(), oldReferrals, newReferrals);
		}
	}
	
	public boolean isPseudoField(String fieldTag)
	{
		return getSchema().isPseudoField(fieldTag);
	}
	
	public boolean isUUIDField(String fieldTag)
	{
		return getSchema().isUUIDField(fieldTag);
	}

	public boolean doesFieldExist(String fieldTag)
	{
		return getFields().containsKey(fieldTag);
	}

	public boolean isEmpty()
	{
		Vector<String> fieldTags = getStoredFieldTags();
		for (int index = 0; index < fieldTags.size(); ++index)
		{
			String tag = fieldTags.get(index);

			if (isUUIDField(tag))
				continue;

			if (!getField(tag).isEmpty())
				return false;
		}
		
		return true;
	}

	public static boolean canOwnPlanningObjects(ORef ref)
	{
		if (Strategy.is(ref))
			return true;

		if (Task.is(ref))
			return true;

		return false;
	}

	public BaseId getId()
	{
		return id;
	}
	
	private void setId(BaseId newId)
	{
		id = newId;
	}
	
	public boolean hasAnyChildTaskResourceData(DateUnit dateUnit) throws Exception
	{
		return hasAnyChildTaskAssignmentData(dateUnit, TAG_RESOURCE_ASSIGNMENT_IDS);
	}
	
	public boolean hasAnyChildTaskExpenseData(DateUnit dateUnit) throws Exception
	{	
		return hasAnyChildTaskAssignmentData(dateUnit, TAG_EXPENSE_ASSIGNMENT_REFS);
	}

	private boolean hasAnyChildTaskAssignmentData(DateUnit dateUnit, String assignmentRefsTag) throws Exception
	{
		TimePeriodCostsMap subTaskTimePeriodCostsMap = getTotalTimePeriodCostsMapForChildTasks(assignmentRefsTag);
		
		return subTaskTimePeriodCostsMap.hasDateUnitsContained(dateUnit);
	}
	
	public OptionalDouble getTotalBudgetCost() throws Exception
	{
		TimePeriodCostsMap totalTimePeriodCostsMap = getTotalTimePeriodCostsMapForAssignments();
		return totalTimePeriodCostsMap.calculateTotalBudgetCost(getProject());
	}

	public TimePeriodCosts calculateTimePeriodCostsForAssignments(DateUnit dateUnitToUse)throws Exception
	{
		return getTotalTimePeriodCostsMapForAssignments().calculateTimePeriodCosts(dateUnitToUse);
	}
	
	public TimePeriodCostsMap getTotalTimePeriodCostsForAssignmentsWithoutRollup() throws Exception
	{
		TimePeriodCostsMap assignmentTimePeriodCostsMap = getTotalTimePeriodCostsMapForTag(TAG_RESOURCE_ASSIGNMENT_IDS);
		TimePeriodCostsMap expenseTimePeriodCostsMap = getTotalTimePeriodCostsMapForTag(TAG_EXPENSE_ASSIGNMENT_REFS);
		
		TimePeriodCostsMap mergedTimePeriodCostsMap = new TimePeriodCostsMap();
		mergedTimePeriodCostsMap.merge(expenseTimePeriodCostsMap);
		mergedTimePeriodCostsMap.merge(assignmentTimePeriodCostsMap);

		return mergedTimePeriodCostsMap;
	}

	public TimePeriodCostsMap getTotalTimePeriodCostsMapForAssignments() throws Exception
	{
		TimePeriodCostsMap expenseAssignmentsTimePeriodCostsMap = getExpenseAssignmentsTimePeriodCostsMap();
		TimePeriodCostsMap resourceAssignmentsTimePeriodCostsMap = getResourceAssignmentsTimePeriodCostsMap();
		
		TimePeriodCostsMap mergedTimePeriodCostsMap = new TimePeriodCostsMap();
		mergedTimePeriodCostsMap.mergeAll(expenseAssignmentsTimePeriodCostsMap);
		mergedTimePeriodCostsMap.mergeAll(resourceAssignmentsTimePeriodCostsMap);
		
		return mergedTimePeriodCostsMap;
	}

	public TimePeriodCostsMap getExpenseAssignmentsTimePeriodCostsMap() throws Exception
	{
		return getTimePeriodCostsMap(TAG_EXPENSE_ASSIGNMENT_REFS);
	}

	public TimePeriodCostsMap getResourceAssignmentsTimePeriodCostsMap() throws Exception
	{
		return getTimePeriodCostsMap(TAG_RESOURCE_ASSIGNMENT_IDS);
	}

	public TimePeriodCostsMap getTotalTimePeriodCostsForPlansWithoutRollup() throws Exception
	{
		TimePeriodCostsMap planTimePeriodCostsMap = getTimePeriodCostsMap(TAG_TIMEFRAME_IDS);

		TimePeriodCostsMap mergedTimePeriodCostsMap = new TimePeriodCostsMap();
		mergedTimePeriodCostsMap.merge(planTimePeriodCostsMap);

		return mergedTimePeriodCostsMap;
	}

	public TimePeriodCostsMap getTotalTimePeriodCostsMapForPlans() throws Exception
	{
		TimePeriodCostsMap planTimePeriodCostsMap = getTimeframesTimePeriodCostsMap();

		TimePeriodCostsMap mergedTimePeriodCostsMap = new TimePeriodCostsMap();
		mergedTimePeriodCostsMap.mergeAll(planTimePeriodCostsMap);

		return mergedTimePeriodCostsMap;
	}

	public TimePeriodCostsMap getTimeframesTimePeriodCostsMap() throws Exception
	{
		return getTimePeriodCostsMap(TAG_TIMEFRAME_IDS);
	}

	protected TimePeriodCostsMap getTimePeriodCostsMap(String tag) throws Exception
	{
		TimePeriodCostsMap subTaskTimePeriodCosts = getTotalTimePeriodCostsMapForChildTasks(tag);
		TimePeriodCostsMap totalTimePeriodCostsMapForTag = getTotalTimePeriodCostsMapForTag(tag);
		
		TimePeriodCostsMap mergedTimePeriodCostsMap = new TimePeriodCostsMap();
		mergedTimePeriodCostsMap.merge(subTaskTimePeriodCosts);
		mergedTimePeriodCostsMap.merge(totalTimePeriodCostsMapForTag);
		
		return mergedTimePeriodCostsMap;	
	}
	
	public TimePeriodCostsMap getTotalTimePeriodCostsMapForChildTasks(String tag) throws Exception
	{
		TimePeriodCostsMap timePeriodCostsMap = new TimePeriodCostsMap();
		ProjectTotalCalculatorStrategy projectTotalCalculatorStrategy = getProject().getTimePeriodCostsMapsCache().getProjectTotalCalculator().getProjectTotalCalculatorStrategy();
		ORefList childTaskRefs = projectTotalCalculatorStrategy.getChildTaskRefs(this);
		for (int index = 0; index < childTaskRefs.size(); ++index)
		{
			Task task = Task.find(getProject(), childTaskRefs.get(index));
			timePeriodCostsMap.mergeAll(task.getTimePeriodCostsMap(tag));
		}
		
		return timePeriodCostsMap;
	}

	protected TimePeriodCostsMap getTotalTimePeriodCostsMapForTag(String tag) throws Exception
	{
		TimePeriodCostsMap timePeriodCostsMap = new TimePeriodCostsMap();
		ORefList refs = getSafeRefListData(tag);
		for(int i = 0; i < refs.size(); ++i)
		{
			BaseObject baseObject = BaseObject.find(getObjectManager(), refs.get(i));
			timePeriodCostsMap.mergeAll(baseObject.getTimePeriodCostsMap(tag));
		}
		
		return timePeriodCostsMap;
	}

	public boolean hasTimeframesWithDifferentDateUnitEffortLists() throws Exception
	{
		ORefList timeframeRefs = getTimeframeRefs();
		HashSet<DateUnitEffortList> dateUnitEffortLists = new HashSet<DateUnitEffortList>();
		for (int index = 0; index < timeframeRefs.size(); ++index)
		{
			Timeframe timeframe = Timeframe.find(getProject(), timeframeRefs.get(index));
			dateUnitEffortLists.add(timeframe.getDateUnitEffortList());
			if (dateUnitEffortLists.size() > 1)
				return true;
		}

		return false;
	}

	public boolean hasTimeframesWithUsableNumberOfDateUnitEfforts() throws Exception
	{
		ORefList timeframeRefs = getTimeframeRefs();
		if (timeframeRefs != null && !timeframeRefs.isEmpty())
		{
			Timeframe timeframe = Timeframe.find(getProject(), timeframeRefs.getFirstElement());

			TimePeriodCostsMap timePeriodCostsMap = timeframe.getTimeframesTimePeriodCostsMap();
			OptionalDouble totalWorkUnits = timePeriodCostsMap.calculateTimePeriodCosts(new DateUnit()).getTotalWorkUnits();
			if (totalWorkUnits.hasNonZeroValue())
				return false;

			DateUnitEffortList effortList = timeframe.getDateUnitEffortList();
			if (effortList.size() > 2)
				return false;
		}

		return true;
	}

	public ORefList getChildTaskRefs()
	{
		return new ORefList();
	}
					
	public static Vector<ProjectResource> toProjectResources(Project project, ORefSet resourcesRefs) throws Exception
	{
		Vector<ProjectResource> resources = new Vector<ProjectResource>();
		for(ORef resourceRef : resourcesRefs)
		{
			if (!resourceRef.isValid())
				continue;

			final ProjectResource projectResource = ProjectResource.find(project, resourceRef);
			if (projectResource == null)
			{
				EAM.logError("Could not find Project Resource object for ref:" + resourceRef);
				continue;
			}

			resources.add(projectResource);
		}

		return resources;
	}

	public static String createAppendedResourceNames(Vector<String> sortedNames)
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

	public static Vector<String> getResourceNames(Project project, Iterable<ORef> filteredResources, ORef leaderResourceRef)
	{
		Vector<String> names = new Vector<String>();
		for(ORef resourceRef : filteredResources)
		{
			names.add(getWhoName(project, resourceRef, leaderResourceRef));
		}

		return names;
	}

	private static String getWhoName(Project project, ORef resourceRef, ORef leaderResourceRef)
	{
		if (resourceRef.isInvalid())
			return Translation.getNotSpecifiedText();

		ProjectResource projectResource = ProjectResource.find(project, resourceRef);
		final String who = projectResource.getWho();
		if (leaderResourceRef.equals(resourceRef))
			return who + "*";

		return who;
	}

	public boolean isTimeframeEditable()
	{
		try
		{
			if (!canOwnPlanningObjects(getRef()))
				return false;

			if (hasTimeframesWithDifferentDateUnitEffortLists())
				return false;

			if (hasTimeframesWithUsableNumberOfDateUnitEfforts())
				return true;

			return false;
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return false;
		}
	}

	public String getTimeframeRollupAsString()
	{
		try
		{
			return getProject().getProjectCalendar().convertToSafeString(getTimeframeRollup());
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}

	private DateRange getTimeframeRollup() throws Exception
	{
		final DateRange projectStartEndDateRange = getProject().getProjectCalendar().getProjectPlanningDateRange();
		return getTotalTimePeriodCostsMapForPlans().getRolledUpDateRange(projectStartEndDateRange);
	}

	public CodeList getAssignedWhoResourcesAsCodeList()
	{
		try
		{
			ORefList resourceAssignmentRefs = getResourceAssignmentRefs();

			ORefSet resourceRefs = new ORefSet();
			for(ORef resourceAssignmentRef : resourceAssignmentRefs)
			{
				ResourceAssignment resourceAssignment = ResourceAssignment.find(getProject(), resourceAssignmentRef);
				ORef resourceRef = resourceAssignment.getResourceRef();
				if (resourceRef.isValid())
					resourceRefs.add(resourceRef);
			}

			return buildResourceCodeList(resourceRefs);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return new CodeList();
		}
	}

	public String getAssignedWhoRollupResourcesAsString(ORefSet resourcesFilter)
	{
		try
		{
			ORefSet allResourceRefs = getTotalTimePeriodCostsMapForAssignments().getAllProjectResourceRefs();

			if (!resourcesFilter.isEmpty())
			{
				ORefSet refsToRemove = ORefSet.subtract(allResourceRefs, resourcesFilter);
				allResourceRefs.removeAll(refsToRemove);
			}

			return getAssignedProjectResourceNames(BaseObject.TAG_ASSIGNED_LEADER_RESOURCE, allResourceRefs);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}

	public ChoiceItem getAssignedProjectResourcesAsChoiceItem(TimePeriodCosts timePeriodCosts, String leaderResourceTag, ORefSet resourcesFilter) throws Exception
	{
		String appendedResources = getAssignedProjectResourcesAsString(timePeriodCosts, leaderResourceTag, resourcesFilter);
		return new TaglessChoiceItem(appendedResources);
	}

	private String getAssignedProjectResourcesAsString(TimePeriodCosts timePeriodCosts, String leaderResourceTag, ORefSet resourcesFilter) throws Exception
	{
		timePeriodCosts.retainWorkUnitDataRelatedToAnyOf(resourcesFilter);
		ORefSet filteredResources = new ORefSet(timePeriodCosts.getWorkUnitsRefSetForType(ProjectResourceSchema.getObjectType()));

		return getAssignedProjectResourceNames(leaderResourceTag, filteredResources);
	}

	private String getAssignedProjectResourceNames(String leaderResourceTag, ORefSet resourceRefs) throws Exception
	{
		ORefSet unspecifiedBaseObjectRefs = ORefSet.getInvalidRefs(resourceRefs);
		resourceRefs.removeAll(unspecifiedBaseObjectRefs);
		Vector<ProjectResource> sortedProjectResources = BaseObject.toProjectResources(getProject(), resourceRefs);
		ORef leaderResourceRef = ORef.INVALID;
		if (doesFieldExist(leaderResourceTag))
		{
			leaderResourceRef = getRef(leaderResourceTag);
			Collections.sort(sortedProjectResources, new ProjectResourceLeaderAtTopSorter(leaderResourceRef));
		}

		final ORefList sortedProjectResourceRefs = new ORefList(sortedProjectResources);
		sortedProjectResourceRefs.addAll(new ORefList(unspecifiedBaseObjectRefs));
		Vector<String> sortedNames = BaseObject.getResourceNames(getProject(), sortedProjectResourceRefs, leaderResourceRef);
		return BaseObject.createAppendedResourceNames(sortedNames);
	}

	private CodeList buildResourceCodeList(Iterable<ORef> resourcesRefs)
	{
		CodeList projectResourceCodes = new CodeList();
		for(ORef resourceRef : resourcesRefs)
		{
			projectResourceCodes.add(resourceRef.toString());
		}

		return projectResourceCodes;
	}

	public String getAssignedWhenRollupAsString()
	{
		try
		{
			return getProject().getProjectCalendar().convertToSafeString(getAssignedWhenRollup());
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}

	public DateRange getAssignedWhenRollup() throws Exception
	{
		final DateRange projectStartEndDateRange = getProject().getProjectCalendar().getProjectPlanningDateRange();
		return getTotalTimePeriodCostsMapForAssignments().getRolledUpDateRange(projectStartEndDateRange);
	}

	protected ORefList calculateRelevantRefList(ORefSet relevantRefList, RelevancyOverrideSet relevantOverrides)
	{
		for(RelevancyOverride override : relevantOverrides)
		{
			if (override.getRef().isInvalid())
			{
				EAM.logWarning("An invalid ref was found inside the relevancy list for Desire with ref = " + getRef());
				continue;
			}
			
			if (getProject().findObject(override.getRef()) == null)
				continue;
			
			if (override.isOverride())
				relevantRefList.add(override.getRef());
			else
				relevantRefList.remove(override.getRef());
		}
		return new ORefList(relevantRefList);
	}
	
	protected RelevancyOverrideSet computeRelevancyOverrides(ORefList refList1, ORefList refList2, boolean relevancyValue)
	{
		RelevancyOverrideSet relevantOverrides = new RelevancyOverrideSet();
		ORefList overrideRefs = ORefList.subtract(refList1, refList2);
		for (int i = 0; i < overrideRefs.size(); ++i)
		{
			RelevancyOverride thisOverride = new RelevancyOverride(overrideRefs.get(i), relevancyValue);
			relevantOverrides.add(thisOverride);
		}
		
		return relevantOverrides;
	}

	public RelevancyOverrideSet getCalculatedRelevantIndicatorOverrides(ORefList all) throws Exception
	{
		RelevancyOverrideSet relevantOverrides = new RelevancyOverrideSet();
		ORefList defaultRelevantRefList = getIndicatorsOnSameFactor();
		relevantOverrides.addAll(computeRelevancyOverrides(all, defaultRelevantRefList, true));
		relevantOverrides.addAll(computeRelevancyOverrides(defaultRelevantRefList, all , false));

		return relevantOverrides;
	}

	private ORefSet indicatorsOnSameFactorAsRefSet()
	{
		ORefSet indicatorsOnSameFactor = new ORefSet();
		ORef[] indicators = getIndicatorsOnSameFactor().toArray();
		indicatorsOnSameFactor.addAll(Arrays.asList(indicators));

		return indicatorsOnSameFactor;
	}

	public ORefList getIndicatorsOnSameFactor()
	{
		return new ORefList();
	}

	public ORefList getRelevantIndicatorRefList() throws Exception
	{
		ORefSet relevantRefList = getDefaultRelevantIndicatorRefs();
		RelevancyOverrideSet relevantOverrides = getIndicatorRelevancyOverrideSet();

		return calculateRelevantRefList(relevantRefList, relevantOverrides);
	}

	protected ORefSet getDefaultRelevantIndicatorRefs()
	{
		ORefSet relevantRefList = indicatorsOnSameFactorAsRefSet();
		return relevantRefList;
	}

	protected RelevancyOverrideSet getIndicatorRelevancyOverrideSet()
	{
		return new RelevancyOverrideSet();
	}

	public ORefList getRelevantActivityRefs() throws Exception
	{
		return new ORefList();
	}

	protected String getRelevantActivityRefsAsString()
	{
		ORefList refList;
		try
		{
			refList = getRelevantActivityRefs();
			return refList.toString();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}

	protected final void clear()
	{
		createFieldsFromBaseObjectSchema();
	}
	
	protected static ChoiceQuestion getQuestion(Class questionClass)
	{
		return StaticQuestionManager.getQuestion(questionClass);
	}

	protected void setIsNavigationField(String tag)
	{
		getField(tag).setNavigationField(true);
	}
	
	public boolean isNavigationField(String tag)
	{
		return getField(tag).isNavigationField();
	}
	
	public String[] getFieldTags()
	{
		return getTags().toArray(new String[0]);
	}
	
	public Vector<String> getStoredFieldTags()
	{
		Vector<String> storedFieldTags = new Vector<String>();
		String[] fieldTags = getFieldTags();
		for (int index = 0; index < fieldTags.length; ++index)
		{
			if (!isPseudoField(fieldTags[index]))
				storedFieldTags.add(fieldTags[index]);
		}
		
		return storedFieldTags;
	}

	public Vector<String> getFieldTagsToSkipOnCopy()
	{
		Vector<String> tagsToSkipOnCopy = new Vector<String>();
		tagsToSkipOnCopy.add(BaseObject.TAG_UUID);
		return tagsToSkipOnCopy;
	}

	public ObjectData getField(String fieldTag)
	{
		ObjectData data = getFields().get(fieldTag);
		if(data == null)
			EAM.logWarning("BaseObject.getField called for unknown tag " + fieldTag + " in " + getRef());
		return data;
	}

	public Vector<CommandSetObjectData> createCommandsToClear()
	{
		Vector<CommandSetObjectData> commands = new Vector<CommandSetObjectData>();
		Set<String> tags = getTags();
		for (String tag : tags)
		{
			if(isPseudoField(tag))
				continue;

			commands.add(new CommandSetObjectData(getType(), getId(), tag, ""));
		}
		
		return commands;
	}
	
	public CommandVector createCommandsToDeleteChildrenAndObject() throws Exception
	{
		CommandVector commandsToDeleteChildrenAndObject = new CommandVector();
		commandsToDeleteChildrenAndObject.addAll(createCommandsToClear());
		commandsToDeleteChildrenAndObject.addAll(createCommandsToDeleteChildren());
		commandsToDeleteChildrenAndObject.addAll(createCommandsToDereferenceObject());
		commandsToDeleteChildrenAndObject.add(new CommandDeleteObject(this));
		
		return commandsToDeleteChildrenAndObject;
	}
	
	protected CommandVector createCommandsToDereferenceObject() throws Exception
	{
		return new CommandVector();
	}

	public CommandVector createCommandsToDeleteChildren() throws Exception
	{
		return new CommandVector();
	}
	
	protected CommandVector createCommandsToDeleteBudgetChildren() throws Exception
	{
		CommandVector commandToDeleteChildren = new CommandVector();
		commandToDeleteChildren.addAll(createCommandsToDeleteRefs(TAG_EXPENSE_ASSIGNMENT_REFS));
		commandToDeleteChildren.addAll(createCommandsToDeleteRefs(TAG_RESOURCE_ASSIGNMENT_IDS));
		commandToDeleteChildren.addAll(createCommandsToDeleteRefs(TAG_TIMEFRAME_IDS));

		return commandToDeleteChildren;
	}

	protected CommandVector createCommandsToDeleteRefs(String tag) throws Exception
	{
		ORefList refsToDelete = getSafeRefListData(tag);
		CommandVector commandsToDeleteRefList = new CommandVector();
		commandsToDeleteRefList.add(new CommandSetObjectData(this, tag, ""));
		commandsToDeleteRefList.addAll(createDeleteCommands(refsToDelete));
		
		return commandsToDeleteRefList;
	}

	private CommandVector createDeleteCommands(ORefList refs)
	{
		CommandVector deleteCommands = new CommandVector();
		for (int index = 0; index < refs.size(); ++index)
		{
			BaseObject objectToDelete = BaseObject.find(getProject(), refs.get(index));
			deleteCommands.addAll(objectToDelete.createCommandsToShallowDelete());
		}
		
		return deleteCommands;
	}
	
	public CommandVector createCommandsToShallowDelete()
	{
		CommandVector commandsToShallowDelete = new CommandVector();
		commandsToShallowDelete.addAll(createCommandsToClear());
		commandsToShallowDelete.add(new CommandDeleteObject(this));
		
		return commandsToShallowDelete;
	}

	public static CommandVector buildRemoveObjectFromRelevancyListCommands(Project project, int typeWithRelevancyOverrideSetList, String relevancyTag, ORef relevantObjectRefToRemove) throws Exception
	{
		CommandVector removeFromRelevancyListCommands = new CommandVector();
		ORefList objectRefsWithRelevancyOverrides = project.getPool(typeWithRelevancyOverrideSetList).getORefList();
		for (int index = 0; index < objectRefsWithRelevancyOverrides.size(); ++index)
		{
			BaseObject objectWithRelevancyOverrides = BaseObject.find(project, objectRefsWithRelevancyOverrides.get(index));
			String relevancySetAsString = objectWithRelevancyOverrides.getData(relevancyTag);
			RelevancyOverrideSet relevancyOverrideSet = new RelevancyOverrideSet(relevancySetAsString);
			if (relevancyOverrideSet.contains(relevantObjectRefToRemove))
			{
				relevancyOverrideSet.remove(relevantObjectRefToRemove);
				CommandSetObjectData removeFromRelevancyListCommand = new CommandSetObjectData(objectWithRelevancyOverrides.getRef(), relevancyTag, relevancyOverrideSet.toString());
				removeFromRelevancyListCommands.add(removeFromRelevancyListCommand);
			}
		}

		return removeFromRelevancyListCommands;
	}

	//Note this method does not clone referenced or owned objects
	public CommandSetObjectData[] createCommandsToClone(BaseId baseId)
	{
		Vector<CommandSetObjectData> commands = new Vector<CommandSetObjectData>();
		Set<String> tags = getTags();
		for (String tag : tags)
		{
			if(isPseudoField(tag))
				continue;
			if (isUUIDField(tag))
				continue;
			if(isIdListTag(tag))
				continue;
			if(isRefList(tag))
				continue;
			
			commands.add(new CommandSetObjectData(getType(), baseId, tag, getData(tag)));
		}
		return commands.toArray(new CommandSetObjectData[0]);
	}

	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		json.put(TAG_TIME_STAMP_MODIFIED, Long.toString(new Date().getTime()));
		json.put(TAG_ID, id.asInt());
		Set<String> tags = getTags();
		for (String tag : tags)
		{
			if(isPseudoField(tag))
				continue;
			ObjectData data = getField(tag);
			json.put(tag, data.get());
		}
		
		return json;
	}
	
	public static String toHtml(BaseObject[] resources)
	{
		StringBuffer result = new StringBuffer();
		result.append("<html>");
		for(int i = 0; i < resources.length; ++i)
		{
			if(i > 0)
				result.append("; ");
			result.append(XmlUtilities2.getXmlEncoded(resources[i].toString()));
		}
		result.append("</html>");
		
		return result.toString();
	}

	public Factor[] getUpstreamDownstreamFactors()
	{
		Factor owner = getDirectOrIndirectOwningFactor();
		if(owner == null)
			return new Factor[0];
		
		ChainWalker chainObject = getNonDiagramChainWalker();
		return chainObject.buildNormalChainAndGetFactors(owner).toFactorArray();
	}
	
	public String getRelatedLabelsAsMultiLine(FactorSet filterSet)
	{
		try
		{
			Factor[] upstreamDownstreamFactors = getUpstreamDownstreamFactors();
			filterSet.attemptToAddAll(upstreamDownstreamFactors);
			return getLabelsAsMultiline(filterSet);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}
	
	public String getLabelsAsMultiline(FactorSet factors)
	{
		ORefList refs = new ORefList(factors.getFactorRefs());
		return getLabelsAsMultiline(refs);
	}
	
	public String getLabelsAsMultiline(ORefList refs)
	{
		Vector<String> labels = new Vector<String>();
		for(int i = 0; i < refs.size(); ++i)
		{
			ORef ref = refs.get(i);
			BaseObject baseObject = BaseObject.find(getObjectManager(), ref);
			labels.add(baseObject.getLabel());
		}
		
		return HtmlUtilities.createHtmlBulletList(labels);
	}

	public String getCleanedLabel()
	{
		return HtmlUtilities.replaceHtmlTags(getLabel(), HtmlUtilities.DIV_TAG_NAME, "");
	}

	public String combineShortLabelAndLabel(String shortLabel, String longLabel)
	{
		String cleanedLongLabel = HtmlUtilities.replaceHtmlTags(longLabel, HtmlUtilities.DIV_TAG_NAME, "");

		if (shortLabel.length() <= 0)
			return cleanedLongLabel;
		
		if (longLabel.length() <= 0)
			return shortLabel;

		return shortLabel + ". " + cleanedLongLabel;
	}
	
	public String combineShortLabelAndLabel()
	{
		return combineShortLabelAndLabel(getShortLabel(), getLabel());
	}
	
	public BaseObject getOwner()
	{
		ORef oref = getOwnerRef();
		if (oref==null || oref.isInvalid())
			return null;
		return objectManager.findObject(oref);
	}

	public ORef getOwnerRef()
	{
		int[] possibleOwningTypes = getTypesThatCanOwnUs();
		if(possibleOwningTypes.length == 0)
			return ORef.INVALID;
		
		ORefList possibleOwners = findAllObjectsThatReferToUs();
		ORef ownerRef = possibleOwners.getRefForTypes(possibleOwningTypes);
		if(ownerRef.isInvalid())
			EAM.logVerbose("getOwnerRef didn't find owner for: " + getRef());
		
		return ownerRef;
	}
	
	abstract public int[] getTypesThatCanOwnUs();

	public ORefList findAllObjectsThatReferToUs()
	{
		return new ORefList(getObjectManager().getReferringObjects(getRef()));
	}
	
	public ORefList findObjectsThatReferToUs(int[] objectTypes)
	{
		ORefList referrers = new ORefList();
		for (int index = 0; index < objectTypes.length; ++index)
		{
			referrers.addAll(findObjectsThatReferToUs(objectTypes[index]));
		}
		
		return referrers; 
	}
	
	public ORefList findObjectsThatReferToUs(int objectType)
	{
		return findAllObjectsThatReferToUs().getFilteredBy(objectType);
	}
	
	static public ORefList findObjectsThatReferToUs(ObjectManager objectManager, int objectType, ORef oref)
	{
		BaseObject object = objectManager.findObject(oref);
		return object.findObjectsThatReferToUs(objectType);
	}
	
	public boolean hasReferrers()
	{
		ORefList referrers = findAllObjectsThatReferToUs();
		if (referrers.size() > 0)
			return true;

		return false;
	}
	
	public ORefList getReferencedObjects(int objectType)
	{
		ORefList referenced = new ORefList();
		ORefSet all = getAllReferencedObjects();
		for(ORef ref : all)
		{
			if(ref.getObjectType() == objectType)
				referenced.add(ref);
		}
		return referenced;
	}
	
	public ORefSet getAllReferencedObjects()
	{
		ORefSet list = new ORefSet();
		for(ObjectData field : getFields().values())
		{
			ORefList refList = field.getRefList();
			list.addAllRefs(refList);
		}
		return list;
	}

	public ORefList createRefList(String tag, String unknonwListTypeAsString) throws ParseException 
	{
		if (isRefList(tag))
		{
			return new ORefList(unknonwListTypeAsString);
		}
		
		if (isIdListTag(tag))
		{
			final int type = getType();
			IdList idList = new IdList(type, unknonwListTypeAsString);

			return new ORefList(type, idList);
		}
		
		throw new RuntimeException("List as string is not a known list type: " + unknonwListTypeAsString + " for ref:" + getRef());
	}
	
	public ORefList getExpenseAssignmentRefs()
	{
		return getSafeRefListData(TAG_EXPENSE_ASSIGNMENT_REFS);
	}
	
	public ORefList getTimeframeRefs()
	{
		return new ORefList(TimeframeSchema.getObjectType(), getSafeIdListData(TAG_TIMEFRAME_IDS));
	}
	
	public ORefList getResourceAssignmentRefs()
	{
		return new ORefList(ResourceAssignmentSchema.getObjectType(), getSafeIdListData(TAG_RESOURCE_ASSIGNMENT_IDS));
	}

	public ORefList getOwnedAndRelatedObjectRefs(ORefList deepCopiedFactorRefs)
	{
		ORefList objectsToDeepCopy = new ORefList();
		objectsToDeepCopy.addAll(getOwnedObjectRefs());
		objectsToDeepCopy.addAll(getNonOwnedObjectsToDeepCopy(deepCopiedFactorRefs));
		
		return objectsToDeepCopy;
	}
	
	protected ORefList getNonOwnedObjectsToDeepCopy(ORefList deepCopiedFactorRefs)
	{
		return new ORefList();
	}
	
	public ORefList getOwnedObjectRefs()
	{
		ORefList allOwnedObjects = new ORefList();
		Vector<String> tags = getStoredFieldTags();
		for (String tag : tags)
		{
			if (isOwnedField(tag))
			{
				allOwnedObjects.addAll(getSafeRefListData(tag));
			}
		}
		
		return allOwnedObjects;
	}

	public boolean isOwnedField(String tag)
	{
		return getSchema().getFieldSchema(tag).isOwned();
	}
	
	public String getPseudoData(String fieldTag)
	{
		if (fieldTag.equals(PSEUDO_TAG_TIMEFRAME_TOTAL))
			return getTimeframeRollupAsString();

		if (fieldTag.equals(PSEUDO_TAG_ASSIGNED_WHEN_TOTAL))
			return getAssignedWhenRollupAsString();

		if(fieldTag.equals(PSEUDO_TAG_LATEST_PROGRESS_REPORT_DATE))
			return getLatestProgressReportDate();
		
		if(fieldTag.equals(PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
			return getLatestProgressReportStatusCode();

		if(fieldTag.equals(PSEUDO_TAG_LATEST_PROGRESS_REPORT_DETAILS))
			return getLatestProgressReportDetails();

		if(fieldTag.equals(PSEUDO_TAG_LATEST_PROGRESS_REPORT_NEXT_STEPS))
			return getLatestProgressReportNextSteps();

		if(fieldTag.equals(PSEUDO_TAG_LATEST_PROGRESS_REPORT_LESSONS_LEARNED))
			return getLatestProgressReportLessonsLearned();

		if(fieldTag.equals(PSEUDO_TAG_LATEST_RESULT_REPORT_DATE))
			return getLatestResultReportDate();

		if(fieldTag.equals(PSEUDO_TAG_LATEST_RESULT_REPORT_CODE))
			return getLatestResultReportStatusCode();

		if(fieldTag.equals(PSEUDO_TAG_LATEST_RESULT_REPORT_DETAILS))
			return getLatestResultReportDetails();

		if(isPseudoField(fieldTag))
		{
			EAM.logError("BaseObject.getPseudoData called for: " + fieldTag);
			return "";
		}
		
		return getData(fieldTag);
	}
	
	private String getLatestProgressReportDate()
	{
		AbstractProgressReport progressReport = getLatestProgressReport();
		if (progressReport == null)
			return "";
		
		return progressReport.getDateAsString();
	}

	private String getLatestProgressReportStatusCode()
	{
		AbstractProgressReport progressReport = getLatestProgressReport();
		if (progressReport == null)
			return "";

		return progressReport.getProgressStatusChoice().getCode();
	}

	private String getLatestProgressReportDetails()
	{
		AbstractProgressReport progressReport = getLatestProgressReport();
		if (progressReport == null)
			return "";
		
		return progressReport.getDetails();
	}

	private String getLatestProgressReportNextSteps()
	{
		AbstractProgressReport progressReport = getLatestProgressReport();
		if (progressReport == null)
			return "";

		if (!(progressReport instanceof ExtendedProgressReport))
			return "";

		return ((ExtendedProgressReport) progressReport).getNextSteps();
	}

	private String getLatestProgressReportLessonsLearned()
	{
		AbstractProgressReport progressReport = getLatestProgressReport();
		if (progressReport == null)
			return "";

		if (!(progressReport instanceof ExtendedProgressReport))
			return "";

		return ((ExtendedProgressReport) progressReport).getLessonsLearned();
	}

	private String getLatestResultReportDate()
	{
		ResultReport resultReport = getLatestResultReport();
		if (resultReport == null)
			return "";

		return resultReport.getDateAsString();
	}

	private String getLatestResultReportStatusCode()
	{
		ResultReport resultReport = getLatestResultReport();
		if (resultReport == null)
			return "";

		return resultReport.getResultStatusChoice().getCode();
	}

	private String getLatestResultReportDetails()
	{
		ResultReport resultReport = getLatestResultReport();
		if (resultReport == null)
			return "";

		return resultReport.getDetails();
	}
	
	public String getProgressReportRefsTag()
	{
		if (ProjectMetadata.is(this))
			return TAG_EXTENDED_PROGRESS_REPORT_REFS;

		if (ConceptualModelDiagram.is(this))
			return TAG_EXTENDED_PROGRESS_REPORT_REFS;

		if (ResultsChainDiagram.is(this))
			return TAG_EXTENDED_PROGRESS_REPORT_REFS;

		return TAG_PROGRESS_REPORT_REFS;
	}

	private AbstractProgressReport getLatestProgressReport()
	{
		return (AbstractProgressReport) getLatestObject(getObjectManager(), getSafeRefListData(getProgressReportRefsTag()), AbstractProgressReport.TAG_PROGRESS_DATE);
	}

	private ResultReport getLatestResultReport()
	{
		return (ResultReport) getLatestObject(getObjectManager(), getSafeRefListData(TAG_RESULT_REPORT_REFS), ResultReport.TAG_RESULT_DATE);
	}

	protected static BaseObject getLatestObject(ObjectManager objectManagerToUse, ORefList objectRefs, String dateTag)
	{
		BaseObject latestObjectToFind = null; 
		for (int i = objectRefs.size() - 1; i >= 0; i--)
		{
			BaseObject thisObject = objectManagerToUse.findObject(objectRefs.get(i));
			if (i == objectRefs.size() - 1)
				latestObjectToFind = thisObject;
			
			String thisDateAsString = thisObject.getData(dateTag);
			String latestDateAsString = latestObjectToFind.getData(dateTag);
			if (thisDateAsString.compareTo(latestDateAsString) > 0)
			{
				latestObjectToFind = thisObject;
			}
		}
		
		return latestObjectToFind;		
	}
	
	public Factor getDirectOrIndirectOwningFactor()
	{
		ORef ownerRef = getRef();
		int AVOID_INFINITE_LOOP = 10000;
		for(int i = 0; i < AVOID_INFINITE_LOOP; ++i)
		{
			if(ownerRef.isInvalid())
				return null;
			
			BaseObject owner = getObjectManager().findObject(ownerRef);
			if(Factor.isFactor(owner.getType()))
				return (Factor)owner;
			
			ownerRef = owner.getOwnerRef();
		}
		return null;
	}
	
	public ORef getAssignedLeaderResourceRef()
	{
		if (getTags().contains(TAG_ASSIGNED_LEADER_RESOURCE))
			return getRef(TAG_ASSIGNED_LEADER_RESOURCE);

		return ORef.INVALID;
	}

	public static BaseObject find(ObjectManager objectManager, ORef objectRef)
	{
		return objectManager.findObject(objectRef);
	}
	
	public static BaseObject find(Project project, ORef objectRef)
	{
		return find(project.getObjectManager(), objectRef);
	}

	public boolean isType(int typeToUse)
	{
		return getType() == typeToUse;
	}
	
	private void createFieldsFromBaseObjectSchema()
	{
		fields = new HashMap<String, ObjectData>();
		final Vector<ObjectData> fieldSchemas = schema.createFields(this);
		for(ObjectData objectData : fieldSchemas)
		{
			fields.put(objectData.getTag(), objectData);
		}
	}
	
	public BaseObjectSchema getSchema()
	{
		return schema;
	}
	
	public int getType()
	{
		return getSchema().getType();
	}

	public String getTypeName()
	{
		return getSchema().getObjectName();
	}

	public static final String TAG_TIME_STAMP_MODIFIED = "TimeStampModified";
	public static final String TAG_ID = "Id";
	public static final String TAG_UUID = "UUID";
	public static final String TAG_LABEL = "Label";
	public static final String TAG_COMMENTS = "Comments";
	public static final String TAG_EMPTY = "EMPTY";
	public static final String TAG_TIMEFRAME_IDS = "TimeframeIds";
	public static final String TAG_RESOURCE_ASSIGNMENT_IDS = "AssignmentIds";
	public static final String TAG_EXPENSE_ASSIGNMENT_REFS = "ExpenseRefs";
	public static final String TAG_PROGRESS_REPORT_REFS = "ProgressReportRefs";
	public static final String TAG_EXTENDED_PROGRESS_REPORT_REFS = "ExtendedProgressReportRefs";
	public static final String TAG_RESULT_REPORT_REFS = "ResultReportRefs";
	public static final String TAG_OUTPUT_REFS = "OutputRefs";
	public static final String TAG_ASSIGNED_LEADER_RESOURCE = "AssignedLeaderResource";
	public static final String TAG_TAXONOMY_CLASSIFICATION_CONTAINER = "TaxonomyClassificationContainer";
	public static final String TAG_EVIDENCE_NOTES = "EvidenceNotes";
	public static final String TAG_EVIDENCE_CONFIDENCE = "EvidenceConfidence";

	public final static String PSEUDO_TAG_TIMEFRAME_TOTAL = "TimeframeDatesTotal";
	public final static String PSEUDO_TAG_ASSIGNED_WHEN_TOTAL = "AssignedEffortDatesTotal";
	public static final String PSEUDO_TAG_LATEST_PROGRESS_REPORT_DATE = "PseudoLatestProgressReportDate";
	public static final String PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE = "PseudoLatestProgressReportCode";
	public static final String PSEUDO_TAG_LATEST_PROGRESS_REPORT_DETAILS = "PseudoLatestProgressReportDetails";
	public static final String PSEUDO_TAG_LATEST_PROGRESS_REPORT_NEXT_STEPS = "PseudoLatestProgressReportNextSteps";
	public static final String PSEUDO_TAG_LATEST_PROGRESS_REPORT_LESSONS_LEARNED = "PseudoLatestProgressReportLessonsLearned";
	public static final String PSEUDO_TAG_LATEST_RESULT_REPORT_DATE = "PseudoLatestResultReportDate";
	public static final String PSEUDO_TAG_LATEST_RESULT_REPORT_CODE = "PseudoLatestResultReportCode";
	public static final String PSEUDO_TAG_LATEST_RESULT_REPORT_DETAILS = "PseudoLatestResultReportDetails";

	protected static final int[] NO_OWNERS = new int[] {};
	
	public static final String DEFAULT_LABEL = "";
	
	protected BaseId id;

	protected ObjectManager objectManager;
	private HashMap<String, ObjectData> fields; 
	private BaseObjectSchema schema;
}
