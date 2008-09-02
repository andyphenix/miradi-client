/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.martus.util.UnicodeWriter;
import org.martus.util.xml.XmlUtilities;
import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.DiagramChainObject;
import org.miradi.diagram.factortypes.FactorTypeCause;
import org.miradi.diagram.factortypes.FactorTypeStrategy;
import org.miradi.diagram.factortypes.FactorTypeTarget;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.objectdata.ChoiceData;
import org.miradi.objectdata.DateRangeData;
import org.miradi.objectdata.NumberData;
import org.miradi.objectdata.ORefListData;
import org.miradi.objectdata.ObjectData;
import org.miradi.objectdata.StringData;
import org.miradi.objecthelpers.CreateObjectParameter;
import org.miradi.objecthelpers.FactorSet;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.project.ProjectChainObject;
import org.miradi.questions.BudgetCostModeQuestion;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.ProgressReportStatusQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.DateRange;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.InvalidNumberException;

abstract public class BaseObject
{

	public BaseObject(ObjectManager objectManagerToUse, BaseId idToUse)
	{
		objectManager = objectManagerToUse;
		setId(idToUse);
		clear();
	}
	
	BaseObject(ObjectManager objectManagerToUse, BaseId idToUse, EnhancedJsonObject json) throws Exception
	{
		this(objectManagerToUse, idToUse);
		loadFromJson(json);
	}
	
	public void loadFromJson(EnhancedJsonObject json) throws Exception
	{
		Iterator iter = fields.keySet().iterator();
		while(iter.hasNext())
		{
			String tag = (String)iter.next();
			if (!getField(tag).isPseudoField())
			{
				String value = json.optString(tag);
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
	}
	
	public Command[] createCommandsToLoadFromJson(EnhancedJsonObject json) throws Exception
	{
		Vector commands = new Vector();
		Iterator iter = fields.keySet().iterator();
		while(iter.hasNext())
		{
			String tag = (String)iter.next();
			if (getField(tag).isPseudoField() || nonClearedFieldTags.contains(tag))
				continue;
			
			CommandSetObjectData setDataCommand = new CommandSetObjectData(getRef(), tag, json.optString(tag));
			commands.add(setDataCommand);
		}
		
		return (Command[]) commands.toArray(new Command[0]);
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
		return false;
	}
	
	public boolean isRefList(String tag)
	{
		if (tag.equals(TAG_WHO_OVERRIDE_REFS))
			return true;
		
		return false;
	}
	
	public boolean isRelevancyOverrideSet(String tag)
	{
		return false;
	}
			
	public int getAnnotationType(String tag)
	{
		if (tag.equals(TAG_WHO_OVERRIDE_REFS))
			return ProjectResource.getObjectType();
		
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
	
	public ProjectChainObject getProjectChainBuilder()
	{
		return getObjectManager().getProjectChainBuilder();
	}
	
	public DiagramChainObject getDiagramChainBuilder()
	{
		return getObjectManager().getDiagramChainBuilder();
	}
		
	public static BaseObject createFromJson(ObjectManager objectManager, int type, EnhancedJsonObject json) throws Exception
	{
		int idAsInt = json.getInt(TAG_ID);
		switch(type)
		{
			case ObjectType.SLIDESHOW:
				return new SlideShow(objectManager, idAsInt, json);
				
			case ObjectType.SLIDE:
				return new Slide(objectManager, idAsInt, json);
				
			case ObjectType.RATING_CRITERION:
				return new RatingCriterion(objectManager, idAsInt, json);
				
			case ObjectType.VALUE_OPTION:
				return new ValueOption(objectManager, idAsInt, json);
				
			case ObjectType.TASK:
				return new Task(objectManager, new FactorId(idAsInt), json);
			
			case ObjectType.STRESS:
				return new Stress(objectManager, new FactorId(idAsInt), json);

			case ObjectType.GROUP_BOX:
				return new GroupBox(objectManager, new FactorId(idAsInt), json);

			case ObjectType.TEXT_BOX:
				return new TextBox(objectManager, new FactorId(idAsInt), json);

			case ObjectType.THREAT_REDUCTION_RESULT:
				return new ThreatReductionResult(objectManager, new FactorId(idAsInt), json);

			case ObjectType.INTERMEDIATE_RESULT:	
				return new IntermediateResult(objectManager, new FactorId(idAsInt), json);

			case ObjectType.CAUSE:
			case ObjectType.STRATEGY:
			case ObjectType.TARGET:
			case ObjectType.FACTOR:
				return createFactorFromJson(objectManager, json, idAsInt);

			case ObjectType.VIEW_DATA:
				return new ViewData(objectManager, idAsInt, json);
				
			case ObjectType.FACTOR_LINK:
				return new FactorLink(objectManager, idAsInt, json);
				
			case ObjectType.PROJECT_RESOURCE:
				return new ProjectResource(objectManager, idAsInt, json);
				
			case ObjectType.INDICATOR:
				return new Indicator(objectManager, idAsInt, json);
				
			case ObjectType.OBJECTIVE:
				return new Objective(objectManager, idAsInt, json);
				
			case ObjectType.GOAL:
				return new Goal(objectManager, idAsInt, json);
				
			case ObjectType.PROJECT_METADATA:
				return new ProjectMetadata(objectManager, idAsInt, json);
				
			case ObjectType.DIAGRAM_LINK:
				return new DiagramLink(objectManager, idAsInt, json);
				
			case ObjectType.ASSIGNMENT:
				return new Assignment(objectManager, idAsInt, json);
				
			case ObjectType.ACCOUNTING_CODE:
				return new AccountingCode(objectManager, idAsInt, json);
				
			case ObjectType.FUNDING_SOURCE:
				return new FundingSource(objectManager, idAsInt, json);
				
			case ObjectType.KEY_ECOLOGICAL_ATTRIBUTE:
				return new KeyEcologicalAttribute(objectManager, idAsInt, json);
			
			case ObjectType.DIAGRAM_FACTOR:
				return new DiagramFactor(objectManager, idAsInt, json);
				
			case ObjectType.CONCEPTUAL_MODEL_DIAGRAM:
				return new ConceptualModelDiagram(objectManager, idAsInt, json);
			
			case ObjectType.RESULTS_CHAIN_DIAGRAM:
				return new ResultsChainDiagram(objectManager, idAsInt, json);
				
			case ObjectType.PLANNING_VIEW_CONFIGURATION:
				return new PlanningViewConfiguration(objectManager, idAsInt, json);
				
			case ObjectType.WWF_PROJECT_DATA:
				return new WwfProjectData(objectManager, idAsInt, json);
			
			case ObjectType.COST_ALLOCATION_RULE:
				return new CostAllocationRule(objectManager, idAsInt, json);
				
			case ObjectType.MEASUREMENT:
				return new Measurement(objectManager, idAsInt, json);
			
			case ObjectType.THREAT_STRESS_RATING:
				return new ThreatStressRating(objectManager, idAsInt, json);
			
			case ObjectType.SUB_TARGET:
				return new SubTarget(objectManager, idAsInt, json);
			
			case ObjectType.PROGRESS_REPORT:
				return new ProgressReport(objectManager, idAsInt, json);
			
			case ObjectType.RARE_PROJECT_DATA:
				return new RareProjectData(objectManager, idAsInt, json);
				
			case ObjectType.WCS_PROJECT_DATA:
				return new WcsProjectData(objectManager, idAsInt, json);	
			
			case ObjectType.TNC_PROJECT_DATA:
				return new TncProjectData(objectManager, idAsInt, json);
				
			case ObjectType.FOS_PROJECT_DATA:
				return new FosProjectData(objectManager, idAsInt, json);
			
			case ObjectType.ORGANIZATION:
				return new Organization(objectManager, idAsInt, json);
				
			case ObjectType.WCPA_PROJECT_DATA:
				return new WcpaProjectData(objectManager, idAsInt, json);
	
			case ObjectType.XENODATA:
				return new Xenodata(objectManager, idAsInt, json);
			
			case ObjectType.PROGRESS_PERCENT:
				return new ProgressPercent(objectManager, idAsInt, json);
				
			case ObjectType.REPORT_TEMPLATE:
				return new ReportTemplate(objectManager, idAsInt, json);
				
			case ObjectType.TAGGED_OBJECT_SET:
				return new TaggedObjectSet(objectManager, idAsInt, json);
				
			default:
				throw new RuntimeException("Attempted to create unknown EAMObject type " + type);
		}
	}

	private static Factor createFactorFromJson(ObjectManager objectManager, EnhancedJsonObject json, int idAsInt) throws Exception
	{
		String typeString = json.optString(Factor.TAG_NODE_TYPE);

		if(typeString.equals(FactorTypeStrategy.STRATEGY_TYPE))
			return new Strategy(objectManager, new FactorId(idAsInt), json);
		
		if(typeString.equals(FactorTypeCause.CAUSE_TYPE))
			return new Cause(objectManager, new FactorId(idAsInt), json);
		
		if(typeString.equals(FactorTypeTarget.TARGET_TYPE))
			return new Target(objectManager, new FactorId(idAsInt), json);

		
		throw new RuntimeException("Read unknown node type: " + typeString);
	}
	
	abstract public int getType();
	abstract public String getTypeName();
	
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof BaseObject))
			return false;
		
		BaseObject other = (BaseObject)rawOther;
		return other.getId().equals(getId());
	}
	
	public String getSafeLabel(BaseObject baseObject)
	{
		if (baseObject == null)
			return "";
		
		return baseObject.getLabel();
	}
		
	public String getLabel()
	{
		return label.get();
	}
	
	public String getShortLabel()
	{
		return "";
	}
	
	public String getFullName()
	{
		return combineShortLabelAndLabel(getShortLabel(), getLabel());
	}
	
	public void setLabel(String newLabel) throws Exception
	{
		label.set(newLabel);
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
		return getField(fieldTag).isPseudoField();
	}
	
	public String getData(String fieldTag)
	{
		if(TAG_ID.equals(fieldTag))
			return id.toString();
		
		if (TAG_EMPTY.equals(fieldTag))
			return "";
		
		if(!doesFieldExist(fieldTag))
			throw new RuntimeException("Attempted to get data for bad field: " + fieldTag + " in object type: " + getClass().getSimpleName());

		return getField(fieldTag).get();
	}

	public boolean doesFieldExist(String fieldTag)
	{
		return fields.containsKey(fieldTag);
	}
	
	public boolean isEmpty()
	{
		Vector<String> fieldTags = getStoredFieldTags();
		for (int index = 0; index < fieldTags.size(); ++index)
		{
			String tag = fieldTags.get(index);
			if (getNonClearedFieldTags().contains(tag))
				continue;
			
			if (!getField(tag).isEmpty())
				return false;
		}
		
		return true;
	}
	

	public BaseId getId()
	{
		return id;
	}
	
	private void setId(BaseId newId)
	{
		id = newId;
	}
	
	
	public double getProportionalBudgetCost() throws Exception
	{
		return getProportionalBudgetCost(null);
	}

	public double getProportionalBudgetCost(DateRange dateRange) throws Exception
	{
		return getBudgetCost(dateRange) * getBudgetCostAllocation();
	}
	
	public double getBudgetCost(DateRange dateRange) throws Exception
	{
		if (isBudgetOverrideMode() && !isWholeProjectDateRange(dateRange))
			return 0;
		
		if (isBudgetOverrideMode())
			return getBudgetCostOverrideValue();
	
		return getBudgetCostRollup(dateRange);
	}
	
	public double getTasksBudgetCostRollUp(DateRange dateRangeToUse, ORefList taskRefs) throws Exception
	{
		double total = 0.0;
		for(int i = 0; i < taskRefs.size(); ++i)
		{
			Task task = Task.find(getProject(), taskRefs.get(i));
			total += task.getProportionalBudgetCost(dateRangeToUse);
		}

		return total;
	}
	
	public double getBudgetCostRollup(DateRange dateRangeToUse) throws Exception
	{
		return 0;
	}
	
	public double getBudgetCostAllocation() throws Exception
	{
		return 1.0;
	}
	
	private boolean isWholeProjectDateRange(DateRange dateRange) throws Exception
	{
		if (dateRange == null)
			return true;
		
		if (dateRange.contains(getProject().getProjectCalendar().combineStartToEndProjectRange()))
			return true;
		
		return false;
	}

	private double getBudgetCostOverrideValue() throws Exception
	{
		String override = budgetCostOverride.get();
		if (override.length() == 0)
			return 0;
		
		return Double.parseDouble(override);
	}
	
	
	public String getProportionalBudgetCostAsString()
	{
		try
		{
			return formatCurrency(getProportionalBudgetCost());
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.logWarning("Error occurred while calculating budget total for strategy");
			return "";
		}
	}
	
	public String getWhoRollupAsString()
	{
		try
		{
			return new ORefList(getWhoRollup()).toString();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}
	
	public String getWhoTotalAsString()
	{		
		try
		{
			return ProjectResource.getResourcesAsString(getProject(), getWhoTotal());
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}

	public ORefSet getWhoTotal() throws Exception
	{
		if (isBudgetOverrideMode())
			return getOverridenWho();
		
		return getWhoRollup();
	}
	
	public ORefSet getAllResources(ORefList taskRefs) throws Exception
	{
		ORefSet resourceRefs = new ORefSet();
		for (int i = 0; i < taskRefs.size(); ++i)
		{
			Task thisTask = Task.find(getProject(), taskRefs.get(i));
			resourceRefs.addAll(thisTask.getWhoTotal());
		}
		
		return resourceRefs;		
	}
	
	public ORefSet getWhoRollup() throws Exception
	{
		return new ORefSet();
	}

	public String getBudgetCostRollupAsString()
	{
		try
		{
			return formatCurrency(getBudgetCostRollup(null));
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.logWarning("Error occurred while calculating budget total");
			return "";
		}
	}

	public String formatCurrency(double cost)
	{
		if(cost == 0.0)
			return "";
		
		DecimalFormat formater = objectManager.getProject().getCurrencyFormatterWithCommas();
		return formater.format(cost);
	}
	
	public String getWhenTotalAsString()
	{
		try
		{
			return convertToSafeString(getWhenTotal());
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return "";
		} 
	}
	
	public String getWhenRollupAsString()
	{
		try
		{
			return convertToSafeString(getWhenRollup());
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}
	
	public DateRange getWhenTotal() throws Exception
	{
		if (isBudgetOverrideMode())
			return getWhenEstimate();
		
		return getWhenRollup();
	}
	
	public DateRange getWhenRollup() throws Exception
	{
		return null;
	}

	public DateRange combineSubtaskEffortListDateRanges(ORefList taskRefs) throws Exception
	{
		DateRange combinedDateRange = null;
		for (int i = 0; i < taskRefs.size(); ++i)
		{
			Task thisTask = Task.find(getProject(), taskRefs.get(i));
			DateRange thisCombineEffortListDateRanges = thisTask.getWhenTotal();
			if (thisCombineEffortListDateRanges == null)
				continue;
			
			DateRange thisDateRange = new DateRange(thisCombineEffortListDateRanges);
			combinedDateRange = DateRange.combine(combinedDateRange, thisDateRange);
		}
		
		return combinedDateRange;		
	}

	public DateRange combineAssignmentEffortListDateRanges(Task task) throws Exception
	{	
		return task.getWhenRollup();
	}

	public String convertToSafeString(DateRange combinedDateRange)
	{
		if (combinedDateRange == null)
			return "";
		
		return  getProject().getProjectCalendar().getDateRangeName(combinedDateRange);
	}
	
	public DateRange getWhenEstimate() throws Exception
	{
		return DateRange.createFromJson(new EnhancedJsonObject(getData(TAG_WHEN_OVERRIDE)));
	}
	
	public ORefSet getOverridenWho()throws Exception
	{
		return new ORefSet(whoOverrideRefs.getORefList());
	}
	
	void clear()
	{
		label = new StringData(TAG_LABEL);
		budgetTotal = new PseudoStringData(PSEUDO_TAG_BUDGET_TOTAL);
		budgetCostRollup = new PseudoStringData(PSEUDO_TAG_BUDGET_COST_ROLLUP);
		budgetCostOverride = new NumberData(TAG_BUDGET_COST_OVERRIDE);
		budgetCostMode = new ChoiceData(TAG_BUDGET_COST_MODE, getQuestion(BudgetCostModeQuestion.class));
		whenTotal = new PseudoStringData(PSEUDO_TAG_WHEN_TOTAL);
		whenRollup = new PseudoStringData(PSEUDO_TAG_WHEN_ROLLUP);
		whenOverride = new DateRangeData(TAG_WHEN_OVERRIDE);
		
		whoTotal = new PseudoStringData(PSEUDO_TAG_WHO_TOTAL);
		whoRollupRefs = new PseudoORefListData(PSEUDO_TAG_WHO_ROLLUP); 
		whoOverrideRefs = new ORefListData(TAG_WHO_OVERRIDE_REFS);
		latestProgressReport = new PseudoQuestionData(PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE, new ProgressReportStatusQuestion());
		latestProgressReportDetails = new PseudoStringData(PSEUDO_TAG_LATEST_PROGRESS_REPORT_DETAILS);

		fields = new HashMap();
		nonClearedFieldTags = new Vector();
		addField(TAG_LABEL, label);
		
		addField(PSEUDO_TAG_BUDGET_TOTAL, budgetTotal);
		addField(PSEUDO_TAG_BUDGET_COST_ROLLUP, budgetCostRollup);
		addField(TAG_BUDGET_COST_OVERRIDE, budgetCostOverride);
		addField(TAG_BUDGET_COST_MODE, budgetCostMode);
		addField(PSEUDO_TAG_WHEN_TOTAL, whenTotal);
		addField(PSEUDO_TAG_WHEN_ROLLUP, whenRollup);
		addField(TAG_WHEN_OVERRIDE, whenOverride);
		addField(PSEUDO_TAG_WHO_TOTAL, whoTotal);
		addField(PSEUDO_TAG_WHO_ROLLUP, whoRollupRefs);
		addField(TAG_WHO_OVERRIDE_REFS, whoOverrideRefs);
		addField(PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE, latestProgressReport);
		addField(PSEUDO_TAG_LATEST_PROGRESS_REPORT_DETAILS, latestProgressReportDetails);
	}
	
	protected ChoiceQuestion getQuestion(Class questionClass)
	{
		return getProject().getQuestion(questionClass);
	}
	
	void addField(ObjectData data)
	{
		addField(data.getTag(), data);
	}

	void addField(String tag, ObjectData data)
	{
		if(!data.getTag().equals(tag))
			throw new RuntimeException("Wrong tag: " + tag + " in " + data.getTag() + " for " + getRef());
		fields.put(tag, data);
	}
	
	
	void addNoClearField(String tag, ObjectData data)
	{
		nonClearedFieldTags.add(tag);
		fields.put(tag, data);
	}
	
	public String[] getFieldTags()
	{
		return fields.keySet().toArray(new String[0]);
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

	public ObjectData getField(String fieldTag)
	{
		ObjectData data = fields.get(fieldTag);
		return data;
	}
	
	public CreateObjectParameter getCreationExtraInfo()
	{
		return null;
	}
	
	public Collection<CommandSetObjectData> createCommandsToClearAsList()
	{
		return Arrays.asList(createCommandsToClear());
	}
	
	public CommandSetObjectData[] createCommandsToClear()
	{
		Vector commands = new Vector();
		Iterator iter = fields.keySet().iterator();
		while(iter.hasNext())
		{
			String tag = (String)iter.next();
			if (nonClearedFieldTags.contains(tag))
				continue;
			if(isPseudoField(tag))
				continue;

			commands.add(new CommandSetObjectData(getType(), getId(), tag, ""));
		}
		return (CommandSetObjectData[])commands.toArray(new CommandSetObjectData[0]);
	}
	
	
	//Note this method does not clone referenced or owned objects
	public CommandSetObjectData[] createCommandsToClone(BaseId baseId)
	{
		Vector commands = new Vector();
		Iterator iter = fields.keySet().iterator();
		while(iter.hasNext())
		{
			String tag = (String)iter.next();
			if (nonClearedFieldTags.contains(tag))
				continue;
			if(isPseudoField(tag))
				continue;
			if(isIdListField(tag))
				continue;
			if(isRefList(tag))
				continue;
			
			commands.add(new CommandSetObjectData(getType(), baseId, tag, getData(tag)));
		}
		return (CommandSetObjectData[])commands.toArray(new CommandSetObjectData[0]);
	}

	private boolean isIdListField(String tag)
	{
		return tag.indexOf("_IDS")>0;
	}
	
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		json.put(TAG_TIME_STAMP_MODIFIED, Long.toString(new Date().getTime()));
		json.put(TAG_ID, id.asInt());
		Iterator iter = fields.keySet().iterator();
		while(iter.hasNext())
		{
			String tag = (String)iter.next();
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
			result.append(XmlUtilities.getXmlEncoded(resources[i].toString()));
		}
		result.append("</html>");
		
		return result.toString();
	}

	public Vector<String> getNonClearedFieldTags()
	{
		return nonClearedFieldTags;
	}

	public Factor[] getUpstreamDownstreamFactors()
	{
		Factor owner = getDirectOrIndirectOwningFactor();
		if(owner == null)
			return new Factor[0];
		
		ProjectChainObject chainObject = getProjectChainBuilder();
		return chainObject.buildUpstreamDownstreamChainAndGetFactors(owner).toFactorArray();
	}
	
	public Factor[] getUpstreamFactors()
	{
		Factor owner = getDirectOrIndirectOwningFactor();
		if(owner == null)
			return new Factor[0];
		
		ProjectChainObject chainObject = getProjectChainBuilder();
		return chainObject.buildUpstreamChainAndGetFactors(owner).toFactorArray();
	}
	
	public Factor[] getUpstreamFactors(DiagramObject diagram)
	{
		Factor owner = getDirectOrIndirectOwningFactor();
		if(owner == null)
			return new Factor[0];
		
		ORefList diagramFactorRefsThatWrapOwner = owner.findObjectsThatReferToUs(DiagramFactor.getObjectType());
		ORefList diagramFactorRefsOnThisPage = diagram.getAllDiagramFactorRefs();
		ORefList theOneOnThisPage = diagramFactorRefsOnThisPage.getOverlappingRefs(diagramFactorRefsThatWrapOwner);
		if(theOneOnThisPage.size() == 0)
		{
			return new Factor[0];
		}
		
		if(theOneOnThisPage.size() > 1)
		{
			EAM.logWarning("Found multiple wrapping DF's on this page: " + theOneOnThisPage);
			return new Factor[0];
		}
		DiagramFactor diagramFactor = DiagramFactor.find(objectManager, theOneOnThisPage.get(0));
		DiagramChainObject chainObject = getDiagramChainBuilder();
		return chainObject.buildUpstreamChainAndGetFactors(diagram, diagramFactor).toFactorArray();
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
		StringBuffer result = new StringBuffer();
		Iterator iter = factors.iterator();
		while(iter.hasNext())
		{
			if(result.length() > 0)
				result.append("\n");
			
			Factor factor = (Factor)iter.next();
			result.append(factor.getLabel());
		}
		
		return result.toString();
	}
	
	public String combineShortLabelAndLabel(String shortLabel, String Longlabel)
	{
		if (shortLabel.length() <= 0)
			return Longlabel;
		
		if (Longlabel.length() <= 0)
			return shortLabel;
		
		return shortLabel + ". " + Longlabel;
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
		if(isCachedOwnerValid)
			return cachedOwnerRef;

		cachedOwnerRef = ORef.INVALID;
		int[] objectTypes = getTypesThatCanOwnUs(getType());
		for (int i=0; i<objectTypes.length; ++i)
		{
			ORef oref = findObjectWhoOwnsUs(objectManager, objectTypes[i], getRef());
			if (oref != null)
			{
				cachedOwnerRef = oref;
				break;
			}
		}
		
		isCachedOwnerValid = true;
		return cachedOwnerRef;
	}

	
	static public ORef findObjectWhoOwnsUs(ObjectManager objectManager, int objectType, ORef oref)
	{
		ORefList orefsInPool = objectManager.getPool(objectType).getORefList();
		for (int i=0; i<orefsInPool.size(); ++i)
		{
			BaseObject objectInPool = objectManager.findObject(orefsInPool.get(i));
			ORefList children = objectInPool.getOwnedObjects(oref.getObjectType());
			for (int childIdx=0; childIdx<children.size(); ++childIdx)
			{
				if (children.get(childIdx).getObjectId().equals(oref.getObjectId()))
					return objectInPool.getRef();
			}
		}
		return null;
	}

	public ORefList findObjectsThatReferToUs()
	{
		return new ORefList(getObjectManager().getReferringObjects(getRef()));
	}
	
	public ORefList findObjectsThatReferToUs(int objectType)
	{
		return findObjectsThatReferToUs().filterByType(objectType);
	}
	
	
	static public ORefList findObjectsThatReferToUs(ObjectManager objectManager, int objectType, ORef oref)
	{
		BaseObject object = objectManager.findObject(oref);
		return object.findObjectsThatReferToUs(objectType);
	}
	
	public boolean hasReferrers()
	{
		ORefList referrers = findObjectsThatReferToUs();
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
		for(ObjectData field : fields.values())
		{
			ORefList refList = getRefListForField(field);
			list.addAllRefs(refList);
		}
		return list;
	}

	protected ORefList getRefListForField(ObjectData field)
	{
		ORefList refList = field.getRefList();
		return refList;
	}
	
	public ORefList getOwnedObjects(int objectType)
	{
		return new ORefList();
	}
	
	public ORefList getAllObjectsToDeepCopy()
	{
		return getAllOwnedObjects();
	}
	
	public ORefList getAllOwnedObjects()
	{
		ORefList allOwnedObjects = new ORefList();
		for (int objectTypeIndex = 0; objectTypeIndex < ObjectType.OBJECT_TYPE_COUNT; ++objectTypeIndex)
		{
			ORefList ownedObjects = getOwnedObjects(objectTypeIndex);
			allOwnedObjects.addAll(ownedObjects);
		}
		
		return allOwnedObjects;
	}
	
	static public int[] getTypesThatCanOwnUs(int type)
	{
		// TODO: get rid of static number
		int[] objectTypes = new int[300];
		int i = 0;

		if (RatingCriterion.canOwnThisType(type))
			objectTypes[i++] = RatingCriterion.getObjectType();

		if (ValueOption.canOwnThisType(type))
			objectTypes[i++] = ValueOption.getObjectType();

		if (Cause.canOwnThisType(type))
			objectTypes[i++] = Cause.getObjectType();
		
		if (Strategy.canOwnThisType(type))
			objectTypes[i++] = Strategy.getObjectType();
		
		if (Target.canOwnThisType(type))
			objectTypes[i++] = Target.getObjectType();
		
		if (ViewData.canOwnThisType(type))
			objectTypes[i++] = ViewData.getObjectType();

		if (FactorLink.canOwnThisType(type))
			objectTypes[i++] = FactorLink.getObjectType();

		if (ProjectResource.canOwnThisType(type))
			objectTypes[i++] = ProjectResource.getObjectType();

		if (Indicator.canOwnThisType(type))
			objectTypes[i++] = Indicator.getObjectType();

		if (Objective.canOwnThisType(type))
			objectTypes[i++] = Objective.getObjectType();

		if (Goal.canOwnThisType(type))
			objectTypes[i++] = Goal.getObjectType();

		if (ProjectMetadata.canOwnThisType(type))
			objectTypes[i++] = ProjectMetadata.getObjectType();
		
		if (DiagramLink.canOwnThisType(type))
			objectTypes[i++] = DiagramLink.getObjectType();

		if (Assignment.canOwnThisType(type))
			objectTypes[i++] = Assignment.getObjectType();

		if (AccountingCode.canOwnThisType(type))
			objectTypes[i++] = AccountingCode.getObjectType();
		
		if (FundingSource.canOwnThisType(type))
			objectTypes[i++] = FundingSource.getObjectType();

		if (KeyEcologicalAttribute.canOwnThisType(type))
			objectTypes[i++] = KeyEcologicalAttribute.getObjectType();

		if (DiagramFactor.canOwnThisType(type))
			objectTypes[i++] = DiagramFactor.getObjectType();

		if (ConceptualModelDiagram.canOwnThisType(type))
			objectTypes[i++] = ConceptualModelDiagram.getObjectType();
		
		if (ResultsChainDiagram.canOwnThisType(type))
			objectTypes[i++] = ResultsChainDiagram.getObjectType();

		if (IntermediateResult.canOwnThisType(type))
			objectTypes[i++] = IntermediateResult.getObjectType();
		
		if (ThreatReductionResult.canOwnThisType(type))
			objectTypes[i++] = ThreatReductionResult.getObjectType();
		
		if (Slide.canOwnThisType(type))
			objectTypes[i++] = Slide.getObjectType();
		
		if (SlideShow.canOwnThisType(type))
			objectTypes[i++] = SlideShow.getObjectType();
		
		if (Task.canOwnThisType(type))
			objectTypes[i++] = Task.getObjectType();
		
		int[] outArray = new int[i];
		System.arraycopy(objectTypes, 0, outArray, 0, i);
		return outArray;
	}

	public String getPseudoData(String fieldTag)
	{
		if(fieldTag.equals(PSEUDO_TAG_BUDGET_TOTAL))
			return getProportionalBudgetCostAsString();
		
		if (fieldTag.equals(PSEUDO_TAG_BUDGET_COST_ROLLUP))
			return getBudgetCostRollupAsString();
				
		if (fieldTag.equals(PSEUDO_TAG_WHEN_TOTAL))
			return getWhenTotalAsString();
		
		if (fieldTag.equals(PSEUDO_TAG_WHEN_ROLLUP))
			return getWhenRollupAsString();
		
		if (fieldTag.equals(PSEUDO_TAG_WHO_TOTAL))
			return getWhoTotalAsString();
						
		if (fieldTag.equals(PSEUDO_TAG_WHO_ROLLUP))
			return getWhoRollupAsString();
		
		if(fieldTag.equals(PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
			return getLatestProgressReportDate();
		
		if(fieldTag.equals(PSEUDO_TAG_LATEST_PROGRESS_REPORT_DETAILS))
			return getLatestProgressReportDetails();
		
		ObjectData field = getField(fieldTag);
		if(field.isPseudoField())
		{
			EAM.logError("BaseObject.getPseudoData called for: " + fieldTag);
			return "";
		}
		
		return getData(fieldTag);
	}
	
	public String  getLatestProgressReportDate()
	{
		ProgressReport progressReport = getLatestProgressReport();
		if (progressReport == null)
			return "";
		
		return progressReport.getProgressStatusChoice().getCode();
	}

	protected String getLatestProgressReportDetails()
	{
		ProgressReport progressReport = getLatestProgressReport();
		if (progressReport == null)
			return "";
		
		return progressReport.getData(ProgressReport.TAG_DETAILS);
	}

	public ProgressReport getLatestProgressReport()
	{
		return null;
	}

	protected static BaseObject getLatestObject(ObjectManager objectManagerToUse, ORefList objectRefs, String dateTag)
	{
		BaseObject latestObjectToFind = null; 
		for (int i = 0; i < objectRefs.size(); ++i)
		{
			BaseObject thisObject = objectManagerToUse.findObject(objectRefs.get(i));
			if (i == 0)
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

	public ORefList getUpstreamObjectives(DiagramObject diagram)
	{
		ORefList objectiveRefs = new ORefList();
		
		Factor[] upstreamFactors = getUpstreamFactors(diagram);
		for(int i = 0; i < upstreamFactors.length; ++i)
		{
			IdList objectiveIds = upstreamFactors[i].getObjectiveIds();
			for(int idIndex = 0; idIndex < objectiveIds.size(); ++idIndex)
			{
				BaseId objectiveId = objectiveIds.get(idIndex);
				if(objectiveId.isInvalid())
					continue;
	
				objectiveRefs.add(new ORef(Objective.getObjectType(), objectiveId));
			}
		}
		return objectiveRefs;
	}

	public boolean isBudgetOverrideMode()
	{
		BudgetCostModeQuestion question = new BudgetCostModeQuestion();
		ChoiceItem choice = question.findChoiceByCode(budgetCostMode.get());
		
		return choice.getCode().equals(BudgetCostModeQuestion.OVERRIDE_MODE_CODE);
	}
	

	//FIXME move these classes into their own class in order to avoid dup code and inner classes
	public class PseudoQuestionData  extends ObjectData
	{
	
		public PseudoQuestionData(String tagToUse, ChoiceQuestion questionToUse)
		{
			super(tagToUse);
			question = questionToUse;
		}
		
		public boolean isPseudoField()
		{
			return true;
		}
		
		public void set(String newValue) throws Exception
		{
		}

		public String get()
		{
			return getPseudoData(getTag());
		}
		
		public ChoiceItem getChoiceItem()
		{
			return question.findChoiceByCode(getPseudoData(getTag()));
		}

		public boolean equals(Object rawOther)
		{
			if(!(rawOther instanceof PseudoQuestionData))
				return false;
			
			PseudoQuestionData other = (PseudoQuestionData)rawOther;
			return get().equals(other.get());
		}

		public int hashCode()
		{
			return get().hashCode();
		}
		
		private ChoiceQuestion question;
	}
	
	
	public class PseudoStringData  extends StringData
	{

		public PseudoStringData(String tag)
		{
			super(tag);
		}

		public boolean isPseudoField()
		{
			return true;
		}
		
		public void set(String newValue) throws Exception
		{
			if (newValue.length()!=0)
				throw new RuntimeException("Set not allowed in a pseuod field");
		}

		public String get()
		{
			return getPseudoData(getTag());
		}
		
		public boolean equals(Object rawOther)
		{
			if(!(rawOther instanceof StringData))
				return false;
			
			StringData other = (StringData)rawOther;
			return get().equals(other.get());
		}

		public int hashCode()
		{
			return get().hashCode();
		}
	}

	public class PseudoORefListData extends ORefListData
	{
		public PseudoORefListData(String tag)
		{
			super(tag);
		}

		public boolean isPseudoField()
		{
			return true;
		}

		public void set(String newValue) throws Exception
		{
			if (newValue.length()!=0)
				throw new RuntimeException("Set not allowed in a pseuod field");
		}

		public String get()
		{
			return getPseudoData(getTag());
		}
		
		public void toXml(UnicodeWriter out) throws Exception
		{
			startTagToXml(out);
			new ORefList(get()).toXml(out);
			endTagToXml(out);
		}

		public boolean equals(Object rawOther)
		{
			if(!(rawOther instanceof StringData))
				return false;

			StringData other = (StringData)rawOther;
			return get().equals(other.get());
		}

		public int hashCode()
		{
			return get().hashCode();
		}
	}

	public static final String TAG_TIME_STAMP_MODIFIED = "TimeStampModified";
	public static final String TAG_ID = "Id";
	public static final String TAG_LABEL = "Label";
	public static final String TAG_EMPTY = "EMPTY";
	
	public static final String DEFAULT_LABEL = "";
	
	public final static String PSEUDO_TAG_BUDGET_TOTAL = "PseudoTaskBudgetTotal";
	public final static String PSEUDO_TAG_BUDGET_COST_ROLLUP = "PseudoBudgetRollupCost";
	public static final String TAG_BUDGET_COST_OVERRIDE = "BudgetCostOverride";
	public static final String TAG_BUDGET_COST_MODE = "BudgetCostMode";
	
	public final static String PSEUDO_TAG_WHEN_TOTAL = "EffortDatesTotal";
	public final static String PSEUDO_TAG_WHEN_ROLLUP = "PseudoWhenRollupAsString";
	public final static String TAG_WHEN_OVERRIDE = "WhenOverride";
	
	public final static String PSEUDO_TAG_WHO_TOTAL = "Who";
	public final static String PSEUDO_TAG_WHO_ROLLUP = "WhoRollupRefs";
	public final static String TAG_WHO_OVERRIDE_REFS = "WhoOverrideRefs";
	
	public static final String PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE = "PseudoLatestProgressReportCode";
	public static final String PSEUDO_TAG_LATEST_PROGRESS_REPORT_DETAILS = "PseudoLatestProgressReportDetails";

	protected BaseId id;
	protected StringData label;
	
	private PseudoStringData budgetTotal;
	private PseudoStringData budgetCostRollup;
	protected PseudoStringData whenTotal;
	private PseudoStringData whenRollup;
	protected DateRangeData whenOverride;

	private PseudoStringData whoTotal;
	protected PseudoORefListData whoRollupRefs;
	protected ORefListData whoOverrideRefs;
	

	private boolean isCachedOwnerValid;
	private ORef cachedOwnerRef;
	protected ObjectManager objectManager;
	private HashMap<String, ObjectData> fields;
	private Vector<String> nonClearedFieldTags;
	protected NumberData budgetCostOverride;
	protected ChoiceData budgetCostMode;
	
	private PseudoQuestionData latestProgressReport;
	private PseudoStringData latestProgressReportDetails;
}
