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

import java.util.Vector;

import org.miradi.ids.IdList;
import org.miradi.ids.IndicatorId;
import org.miradi.objectdata.CodeToUserStringMapData;
import org.miradi.objecthelpers.CodeToUserStringMap;
import org.miradi.objecthelpers.DirectThreatSet;
import org.miradi.objecthelpers.NonDraftStrategySet;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.TargetSet;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.GoalSchema;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.IntermediateResultSchema;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.schemas.ObjectiveSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.schemas.ThreatReductionResultSchema;
import org.miradi.utils.CommandVector;

public class Indicator extends BaseObject
{
	public Indicator(final ObjectManager objectManager, final IndicatorId idToUse)
	{
		super(objectManager, idToUse, createSchema());
	}

	public static IndicatorSchema createSchema()
	{
		return new IndicatorSchema();
	}
	
	public IdList getMethodIds()
	{
		return getSafeIdListData(TAG_METHOD_IDS);
	}
	
	public ORefList getTaskRefs()
	{
		return new ORefList(TaskSchema.getObjectType(), getMethodIds());
	}
	
	public CodeToUserStringMapData getThresholdsMap()
	{
		return (CodeToUserStringMapData)getField(TAG_THRESHOLDS_MAP);
	}
	
	public CodeToUserStringMap getThresholdDetailsMap()
	{
		return getCodeToUserStringMapData(TAG_THRESHOLD_DETAILS_MAP);
	}
	
	@Override
	protected CommandVector createCommandsToDereferenceObject() throws Exception
	{
		CommandVector commandsToDereferences = super.createCommandsToDereferenceObject();
		commandsToDereferences.addAll(buildRemoveIndicatorFromRelevancyListCommands(getRef()));
		
		return commandsToDereferences;
	}
	
	private CommandVector buildRemoveIndicatorFromRelevancyListCommands(ORef relevantIndicatorRefToRemove) throws Exception
	{
		CommandVector removeFromRelevancyListCommands = new CommandVector();
		removeFromRelevancyListCommands.addAll(Desire.buildRemoveObjectFromRelevancyListCommands(getProject(), ObjectiveSchema.getObjectType(), Objective.TAG_RELEVANT_INDICATOR_SET, relevantIndicatorRefToRemove));
		removeFromRelevancyListCommands.addAll(Desire.buildRemoveObjectFromRelevancyListCommands(getProject(), GoalSchema.getObjectType(), Goal.TAG_RELEVANT_INDICATOR_SET, relevantIndicatorRefToRemove));
		
		return removeFromRelevancyListCommands;
	}
		
	@Override
	public CommandVector createCommandsToDeleteChildren() throws Exception
	{
		CommandVector commandsToDeleteChildren  = super.createCommandsToDeleteChildren();
		commandsToDeleteChildren.addAll(createCommandsToDeleteRefs(TAG_PROGRESS_REPORT_REFS));
		commandsToDeleteChildren.addAll(createCommandsToDeleteBudgetChildren());
		commandsToDeleteChildren.addAll(createCommandsToDeleteMethods());
		commandsToDeleteChildren.addAll(createCommandsToDeleteMeasurements());
		
		return commandsToDeleteChildren;
	}

	private CommandVector createCommandsToDeleteMeasurements() throws Exception
	{
		return createCommandsToDeleteAnnotation(getMeasurementRefs());
	}
	
	private CommandVector createCommandsToDeleteMethods() throws Exception
	{		
		return createCommandsToDeleteAnnotation(getMethodRefs());
	}
	
	private CommandVector createCommandsToDeleteAnnotation(ORefList annotationRefs) throws Exception
	{
		CommandVector commandsToDeleteAnnotation = new CommandVector();
		for (int index = 0; index < annotationRefs.size(); ++index)
		{
			BaseObject annotationToDelete = BaseObject.find(getProject(), annotationRefs.get(index));
			ORefList referrers = annotationToDelete.findObjectsThatReferToUs(IndicatorSchema.getObjectType());
			if (referrers.size() == 1)
				commandsToDeleteAnnotation.addAll(annotationToDelete.createCommandsToDeleteChildrenAndObject());
		}
		
		return commandsToDeleteAnnotation;
	}

	//TODO: several pseudo fields are shared between Indicator and Desires; this may indicate a need for a common super class
	@Override
	public String getPseudoData(String fieldTag)
	{
		if(fieldTag.equals(PSEUDO_TAG_TARGETS))
			return getRelatedLabelsAsMultiLine(new TargetSet());
		
		if(fieldTag.equals(PSEUDO_TAG_DIRECT_THREATS))
			return getRelatedLabelsAsMultiLine(new DirectThreatSet());
		
		if(fieldTag.equals(PSEUDO_TAG_STRATEGIES))
			return getRelatedLabelsAsMultiLine(new NonDraftStrategySet());
		
		if(fieldTag.equals(PSEUDO_TAG_FACTOR))
			return getSafeLabel(getDirectOrIndirectOwningFactor());
		
		if(fieldTag.equals(PSEUDO_TAG_METHODS))
			return getIndicatorMethodsSingleLine();
		
		if (fieldTag.equals(PSEUDO_TAG_RELATED_METHOD_OREF_LIST))
			return getMethodRefs().toString();
		
		if (fieldTag.equals(PSEUDO_TAG_LATEST_MEASUREMENT_REF))
			return getLatestMeasurementRef().toString();
		
		if(fieldTag.equals(PSEUDO_TAG_STATUS_VALUE))
			return getCurrentStatus();
		
		return super.getPseudoData(fieldTag);
	}

	public boolean isActive()
	{
		ORef ownerRef = getOwnerRef();
		if(KeyEcologicalAttribute.is(ownerRef))
			return isOwningKeyEcologicalAttributeActive(ownerRef);
		
		if(AbstractTarget.isAbstractTarget(ownerRef))
			return isOwningTargetInSimpleMode(ownerRef);
		
		return true;
	}

	private boolean isOwningKeyEcologicalAttributeActive(ORef keaRef)
	{
		KeyEcologicalAttribute kea = KeyEcologicalAttribute.find(getObjectManager(), keaRef);
		return kea.isActive();
	}

	private boolean isOwningTargetInSimpleMode(ORef targetRef)
	{
		AbstractTarget target = AbstractTarget.findTarget(getProject(), targetRef);
		return !target.isViabilityModeTNC();
	}

	public String getCurrentStatus()
	{
		ORef measurementRef = getLatestMeasurementRef();
		if(measurementRef == null || measurementRef.isInvalid())
			return "";
		
		Measurement measurement = (Measurement)getProject().findObject(measurementRef);
		String statusCode = measurement.getData(Measurement.TAG_STATUS);
		return statusCode;
	}

	private String getIndicatorMethodsSingleLine()
	{
		return getLabelsAsMultiline(getMethodRefs());
	}

	@Override
	public ORefList getSubTaskRefs()
	{
		return getMethodRefs();
	}
	
	public ORefList getMethodRefs()
	{
		return new ORefList(TaskSchema.getObjectType(), getMethodIds());
	}
	
	public Vector<Task> getMethods()
	{
		Vector<Task> methods = new Vector<Task>();
		ORefList methodRefs = getMethodRefs();
		for (int index = 0; index < methodRefs.size(); ++index)
		{
			Task method = Task.find(getProject(), methodRefs.get(index));
			methods.add(method);
		}
		
		return methods;
	}
	
	public ORef getLatestMeasurementRef()
	{
		BaseObject latestObject = getLatestObject(getObjectManager(), getMeasurementRefs(), Measurement.TAG_DATE);
		if (latestObject == null)
			return ORef.INVALID;
		
		return latestObject.getRef();
	}
	
	public ORefList getMeasurementRefs()
	{
		return getSafeRefListData(TAG_MEASUREMENT_REFS);
	}
	
	@Override
	public int getAnnotationType(String tag)
	{
		if (tag.equals(TAG_METHOD_IDS))
			return TaskSchema.getObjectType();
		
		if (tag.equals(TAG_MEASUREMENT_REFS))
			return MeasurementSchema.getObjectType();
		
		return super.getAnnotationType(tag);
	}

	@Override
	public boolean isIdListTag(String tag)
	{
		if (tag.equals(TAG_METHOD_IDS))
			return true;
		
		if (tag.equals(TAG_RESOURCE_ASSIGNMENT_IDS))
			return true;
		
		return super.isIdListTag(tag);
	}
	
	@Override
	public boolean isRefList(String tag)
	{
		if (tag.equals(TAG_MEASUREMENT_REFS))
			return true;
		
		if (tag.equals(TAG_EXPENSE_ASSIGNMENT_REFS))
			return true;
		
		if (tag.equals(TAG_PROGRESS_REPORT_REFS))
			return true;
		
		return super.isRefList(tag);
	}
	
	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return new int[] {
			StrategySchema.getObjectType(),
			CauseSchema.getObjectType(),
			IntermediateResultSchema.getObjectType(),
			ThreatReductionResultSchema.getObjectType(),
			TargetSchema.getObjectType(),
			HumanWelfareTargetSchema.getObjectType(),
			KeyEcologicalAttributeSchema.getObjectType(),
		};
	}
	
	public String getFutureStatusRating()
	{
		return getData(TAG_FUTURE_STATUS_RATING);
	}
	
	public String getFutureStatusSummary()
	{
		return getStringData(TAG_FUTURE_STATUS_SUMMARY);
	}
		
	public ORefList getRelevantDesireRefs() throws Exception
	{
		ORefList relevantDesireRefs = new ORefList();
		relevantDesireRefs.addAll(extractRelevantDesireRefs(getProject().getGoalPool().getRefSet()));
		relevantDesireRefs.addAll(extractRelevantDesireRefs(getProject().getObjectivePool().getRefSet()));
		return relevantDesireRefs;
	}

	private ORefList extractRelevantDesireRefs(ORefSet desireRefs) throws Exception
	{
		ORefList relevantDesireRefs = new ORefList();
		for(ORef desireRef : desireRefs)
		{
			Desire goal = Desire.findDesire(getProject(), desireRef);
			if(goal.getRelevantIndicatorRefList().contains(getRef()))
				relevantDesireRefs.add(desireRef);
		}
		
		return relevantDesireRefs;
	}

	@Override
	protected ORefList getNonOwnedObjectsToDeepCopy(ORefList deepCopiedFactorRefs)
	{
		ORefList deepObjectRefsToCopy = super.getNonOwnedObjectsToDeepCopy(deepCopiedFactorRefs);
		deepObjectRefsToCopy.addAll(getMethodRefs());
		
		return deepObjectRefsToCopy;
	}

	@Override
	public String getShortLabel()
	{
		return getData(TAG_SHORT_LABEL);
	}
	
	@Override
	public String toString()
	{
		if(getId().isInvalid())
			return "(None)";
		return combineShortLabelAndLabel(getShortLabel(), getLabel());
	}
	
	public boolean isViabilityIndicator()
	{
		ORefList keaReferrerRefs = findObjectsThatReferToUs(KeyEcologicalAttributeSchema.getObjectType());
		return keaReferrerRefs.size() > 0;
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
		return objectType == IndicatorSchema.getObjectType();
	}
	
	public static Indicator find(ObjectManager objectManager, ORef indicatorRef)
	{
		return (Indicator) objectManager.findObject(indicatorRef);
	}
	
	public static Indicator find(Project project, ORef indicatorRef)
	{
		return find(project.getObjectManager(), indicatorRef);
	}
	
	public static final String TAG_SHORT_LABEL = "ShortLabel";
	public static final String TAG_PRIORITY = "Priority";
	
	public final static String TAG_METHOD_IDS = "TaskIds";
	public static final String TAG_THRESHOLDS_MAP = "IndicatorThresholds";
	public static final String TAG_THRESHOLD_DETAILS_MAP = "ThresholdDetails";
	public static final String TAG_RATING_SOURCE = "RatingSource";
	public static final String TAG_MEASUREMENT_REFS = "MeasurementRefs";
	public static final String TAG_DETAIL = "Detail";
	public static final String TAG_COMMENTS = "Comments";
	public static final String TAG_VIABILITY_RATINGS_COMMENTS = "ViabilityRatingsComment";

	public static final String TAG_FUTURE_STATUS_RATING  = "FutureStatusRating";
	public static final String TAG_FUTURE_STATUS_DATE = "FutureStatusDate";
	public static final String TAG_FUTURE_STATUS_SUMMARY = "FutureStatusSummary";
	public static final String TAG_FUTURE_STATUS_DETAIL = "FutureStatusDetail";
	public static final String TAG_FUTURE_STATUS_COMMENTS = "FutureStatusComment";

	public static final String PSEUDO_TAG_FACTOR = "PseudoTagFactor";
	
	// NOTE: Can't change the following tags unless we recompile the jasper reports
	public static final String PSEUDO_TAG_TARGETS = "Targets";
	public static final String PSEUDO_TAG_DIRECT_THREATS = "DirectThreats";
	public static final String PSEUDO_TAG_STRATEGIES = "Strategies";
	public static final String PSEUDO_TAG_METHODS = "Methods";
	public static final String PSEUDO_TAG_RATING_SOURCE_VALUE = "RatingSourceValue";
	public static final String PSEUDO_TAG_PRIORITY_VALUE = "PriorityValue";
	public static final String PSEUDO_TAG_FUTURE_STATUS_RATING_VALUE  = "FutureStatusRatingValue";
	public static final String PSEUDO_TAG_STATUS_VALUE  = "StatusValue";
	public static final String PSEUDO_TAG_LATEST_MEASUREMENT_REF = "LatestMeasurementRef";
	
	public static final String PSEUDO_TAG_RELATED_METHOD_OREF_LIST = "PseudoTagRelatedMethodORefList";

	public static final String META_COLUMN_TAG = "IndicatorMetaColumnTag";
}
