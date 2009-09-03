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
package org.miradi.xml.conpro.exporter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.martus.util.UnicodeWriter;
import org.martus.util.xml.XmlUtilities;
import org.miradi.diagram.ThreatTargetChainObject;
import org.miradi.exceptions.InvalidICUNSelectionException;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.main.EAM;
import org.miradi.main.VersionConstants;
import org.miradi.objecthelpers.BaseObjectByRefSorter;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectToStringSorter;
import org.miradi.objecthelpers.StringRefMap;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.Desire;
import org.miradi.objects.Factor;
import org.miradi.objects.FactorLink;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.Objective;
import org.miradi.objects.ProgressPercent;
import org.miradi.objects.ProgressReport;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.RatingCriterion;
import org.miradi.objects.Strategy;
import org.miradi.objects.Stress;
import org.miradi.objects.SubTarget;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.ThreatRatingCommentsData;
import org.miradi.objects.ThreatReductionResult;
import org.miradi.objects.ThreatStressRating;
import org.miradi.objects.TncProjectData;
import org.miradi.objects.ValueOption;
import org.miradi.objects.Xenodata;
import org.miradi.project.Project;
import org.miradi.project.threatrating.SimpleThreatRatingFramework;
import org.miradi.project.threatrating.ThreatRatingBundle;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.ResourceRoleQuestion;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.StrategyClassificationQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.DateRange;
import org.miradi.utils.DelimitedFileLoader;
import org.miradi.utils.MiradiMultiCalendar;
import org.miradi.xml.XmlExporter;
import org.miradi.xml.conpro.ConProMiradiCodeMapHelper;
import org.miradi.xml.conpro.ConProMiradiXml;


//FIXME urgent: This class is under construction
public class ConproXmlExporter extends XmlExporter implements ConProMiradiXml
{
	public ConproXmlExporter(Project project) throws Exception
	{
		super(project);
		
		codeMapHelper = new ConProMiradiCodeMapHelper();
	}

	@Override
	public void exportProject(UnicodeWriter out) throws Exception
	{
		out.writeln("<?xml version='1.0' encoding='UTF-8' ?>");
		writeStartElementWithAttribute(out, CONSERVATION_PROJECT, XMLNS, NAME_SPACE);
		
		writeoutProjectSummaryElement(out);
		writeTargets(out);
		writeThreats(out);
		writeStrategies(out);
		writeObjectives(out);
		writeMethods(out);
		writeIndicators(out);
		
		writeEndElement(out, CONSERVATION_PROJECT);
	}

	private void writeIndicators(UnicodeWriter out) throws Exception
	{
		ORefList indicatorRefs = getProject().getIndicatorPool().getRefList();
		indicatorRefs.sort();
		writeStartElement(out, INDICATORS);
		for (int refIndex = 0; refIndex < indicatorRefs.size(); ++refIndex)
		{
			ORef indicatorRef = indicatorRefs.get(refIndex);
			writeIndicator(out, indicatorRef);
		}
		
		writeEndElement(out, INDICATORS);
	}

	private void writeIndicator(UnicodeWriter out, ORef indicatorRef) throws Exception
	{
		Indicator indicator = Indicator.find(getProject(), indicatorRef);
		writeStartElementWithAttribute(out, INDICATOR, ID, indicator.getId().toString());
		writeLabelElement(out, NAME, indicator, Indicator.TAG_LABEL);
		writeElement(out, FACTOR_TYPE, "Indicator");
		writeElement(out, FACTOR_NAME, "Indicator");
		writeOptionalRatingCodeElement(out, PRIORITY, indicator, Indicator.TAG_PRIORITY);
		writeOptionalElement(out, COMMENT, indicator, Indicator.TAG_COMMENTS);
		writeMeasurements(out, indicator.getMeasurementRefs());		
		writeIds(out, indicator.getMethodRefs(), METHODS,	METHOD_ID);
		writeProgressReports(out, indicator.getProgressReportRefs());

		writeEndElement(out, INDICATOR);
	}

	private void writeStrategies(UnicodeWriter out) throws Exception
	{
		ORefList strategyRefs = getProject().getStrategyPool().getRefList();
		strategyRefs.sort();
		writeStartElement(out, STRATEGIES);
		for (int refIndex = 0; refIndex < strategyRefs.size(); ++refIndex)
		{
			Strategy strategy = Strategy.find(getProject(), strategyRefs.get(refIndex));
			writeStartElementWithAttribute(out, STRATEGY, ID, strategy.getId().toString());
			
			ORefSet objectiveRefs = getRelevantObjectiveRefs(strategy);
			writeIds(out, OBJECTIVES, OBJECTIVE_ID, new ORefList(objectiveRefs));
			
			writeLabelElement(out, NAME, strategy, Strategy.TAG_LABEL);
			writeOptionalTaxonomyClassificationCode(out, strategy);
			writeElement(out, SELECTED, Boolean.toString(!strategy.isStatusDraft()));
			writeOptionalElement(out, COMMENT, strategy, Strategy.TAG_COMMENTS);
			writeOptionalElement(out, LEGACY_TNC_STRATEGY_RATING , strategy, Strategy.TAG_LEGACY_TNC_STRATEGY_RANKING);
			writeProgressReports(out, strategy.getProgressReportRefs());
			writeActivities(out, strategy.getActivityRefs());
						
			writeEndElement(out, STRATEGY);
		}
		
		writeEndElement(out, STRATEGIES);
	}
	
	private void writeOptionalTaxonomyClassificationCode(UnicodeWriter out,	Strategy strategy) throws Exception
	{
		String taxonomyCode = strategy.getTaxonomyCode();
		ChoiceQuestion question = new StrategyClassificationQuestion();
		ChoiceItem classificationChoice = question.findChoiceByCode(taxonomyCode);
		if (classificationChoice.isSelectable())
		{
			writeOptionalElement(out, TAXONOMY_CODE, strategy, Strategy.TAG_TAXONOMY_CODE);
			return;
		}
		
		throw new InvalidICUNSelectionException(strategy);
	}

	//NOTE this approach is slow.  Another approach would be to 
	//create an inverse map of strategy objevive list based on objective relavancy list
	public ORefSet getRelevantObjectiveRefs(Strategy strategy) throws Exception
	{
		ORefSet objectiveRefs = new ORefSet();
		ORefList allObjectives = getProject().getObjectivePool().getORefList();
		for (int index = 0; index < allObjectives.size(); ++index)
		{
			Objective objective = Objective.find(getProject(), allObjectives.get(index));
			ORefList relevantStrategyRefs = objective.getRelevantStrategyAndActivityRefs();
			if (relevantStrategyRefs.contains(strategy.getRef()))
				objectiveRefs.add(allObjectives.get(index));
		}
		
		return objectiveRefs;
	}
		
	private void writeActivities(UnicodeWriter out, ORefList activityRefs) throws Exception
	{
		writeStartElement(out, ACTIVITIES);
		for (int refIndex = 0; refIndex < activityRefs.size(); ++refIndex)
		{
			Task activity = Task.find(getProject(), activityRefs.get(refIndex));
			writeStartElement(out, ACTIVITY);
			writeLabelElement(out, NAME, activity, Task.TAG_LABEL);
			DateRange whenTotal = activity.getWhenRollup();
			if (whenTotal != null)
			{
				writeElement(out, ACTIVITY_START_DATE, whenTotal.getStartDate().toString());
				writeElement(out, ACTIVITY_END_DATE, whenTotal.getEndDate().toString());
			}
			writeProgressReports(out, activity.getProgressReportRefs());
			
			writeEndElement(out, ACTIVITY);
		}
		
		writeEndElement(out, ACTIVITIES);
	}
	
	private void writeMethods(UnicodeWriter out) throws Exception
	{
		Vector<Task> methods = getProject().getTaskPool().getAllMethods();
		Collections.sort(methods, new BaseObjectByRefSorter());
		writeStartElement(out, METHODS);
		for (int refIndex = 0; refIndex < methods.size(); ++refIndex)
		{
			Task method = methods.get(refIndex);
			writeStartElementWithAttribute(out, METHOD, ID, method.getId().toString());
			
			writeLabelElement(out, METHOD_NAME, method, Task.TAG_LABEL);
			writeElement(out, METHOD_DETAIL, method, Task.TAG_DETAILS);
			writeElement(out, METHOD_COMMENT, method, Task.TAG_COMMENTS);
			writeEndElement(out, METHOD);
		}
		
		writeEndElement(out, METHODS);
	}

	private void writeProgressReports(UnicodeWriter out, ORefList progressReportRefs) throws Exception
	{
		writeStartElement(out, PROGRESS_REPORTS);
		int sequenceCounter = 0;
		for (int refIndex = 0; refIndex < progressReportRefs.size(); ++refIndex)
		{
			ProgressReport progressReport = ProgressReport.find(getProject(), progressReportRefs.get(refIndex));
			
			String progressStatusCode = progressReport.getData(ProgressReport.TAG_PROGRESS_STATUS);
			String progressReportStatusCode = statusCodeToXmlValue(progressStatusCode);
			String progressReportDate = progressReport.getDateAsString();
			String progressReportDetails = progressReport.getData(ProgressReport.TAG_DETAILS);
			
			String[] fieldsToVerify = new String[]{progressReportStatusCode, progressReportDate, progressReportDetails,};
			if (isAtLeastOneStringNonBlank(fieldsToVerify))
			{
				writeStartElementWithAttribute(out, PROGRESS_REPORT, SEQUENCE, ++sequenceCounter);
			
				writeOptionalElement(out, PROGRESS_REPORT_STATUS, progressReportStatusCode);
				writeOptionalElement(out, PROGRESS_REPORT_DATE, progressReportDate);
				writeOptionalElement(out, PROGRESS_REPORT_COMMENT, progressReportDetails);
				
				writeEndElement(out, PROGRESS_REPORT);
			}
		}
		
		writeEndElement(out, PROGRESS_REPORTS);
	}
	
	private boolean isAtLeastOneStringNonBlank(String[] dataToVerify)
	{
		for (int index = 0; index < dataToVerify.length; ++index)
		{
			if (dataToVerify[index].length() > 0)
				return true;
		}
		
		return false;
	}
	
	private void writeMeasurements(UnicodeWriter out, ORefList measurementRefs) throws Exception
	{
		writeStartElement(out, MEASUREMENTS);
		int sequenceCounter = 0;
		for (int refIndex = 0; refIndex < measurementRefs.size(); ++refIndex)
		{
			Measurement measurement = Measurement.find(getProject(), measurementRefs.get(refIndex));
			String measurementDate = measurement.getData(Measurement.TAG_DATE);
			String measurementStatusConfidenceCode = statusConfidenceToXmlValue(measurement.getData(Measurement.TAG_STATUS_CONFIDENCE));
			String measurementTrendCode = trendToXmlValue(measurement.getData(Measurement.TAG_TREND));
			String measurementRankingCode = rankingCodeToXmlValue(measurement.getData(Measurement.TAG_STATUS));
				
			writeStartElementWithAttribute(out, MEASUREMENT, SEQUENCE, ++sequenceCounter);
			writeLabelElement(out, MEASUREMENT_SUMMARY, measurement, Measurement.TAG_SUMMARY);
			writeOptionalElement(out, MEASUREMENT_DATE, measurementDate);
			writeOptionalElement(out, MEASUREMENT_STATUS_CONFIDENCE,  measurementStatusConfidenceCode);
			writeOptionalElement(out, MEASUREMENT_TREND, measurementTrendCode);
			writeOptionalElement(out, MEASUREMENT_RATING, measurementRankingCode);

			writeEndElement(out, MEASUREMENT);
		}
		
		writeEndElement(out, MEASUREMENTS);
	}	

	private void writeObjectives(UnicodeWriter out) throws Exception
	{
		ORefList objectiveRefs = getProject().getObjectivePool().getRefList();
		objectiveRefs.sort();
		writeStartElement(out, OBJECTIVES);
		for (int refIndex = 0; refIndex < objectiveRefs.size(); ++refIndex)
		{
			ORef objectiveRef = objectiveRefs.get(refIndex);
			writeObjective(out, objectiveRef);
		}
		
		writeTargetGoalsAsObjectives(out);
		
		writeEndElement(out, OBJECTIVES);
	}

	private void writeTargetGoalsAsObjectives(UnicodeWriter out) throws Exception
	{
		ORefList targetRefs = getProject().getTargetPool().getRefList();
		for (int index = 0; index < targetRefs.size(); ++index)
		{
			Target target = Target.find(getProject(), targetRefs.get(index));
			ORefList goalRefs = target.getGoalRefs();
			String targetAnnotationLabel = " (" + EAM.text("Target") + " = " + target.getLabel() + ")";
			writeGoalsAsObjectives(out, goalRefs, targetAnnotationLabel);
		}
	}

	private void writeGoalsAsObjectives(UnicodeWriter out, ORefList goalRefs, String targetName) throws Exception
	{
		for (int index = 0; index < goalRefs.size(); ++index)
		{
			writeObjective(out, goalRefs.get(index), targetName);
		}
	}

	private void writeObjective(UnicodeWriter out, ORef desireRef) throws Exception
	{
		Desire desire = Desire.findDesire(getProject(), desireRef);
		Factor owningFactor = desire.getDirectOrIndirectOwningFactor();
		writeObjective(out, desireRef, createThreatOrThreatReductionResultAnnotationLabel(owningFactor));
	}

	private String createThreatOrThreatReductionResultAnnotationLabel(Factor owningFactor)
	{
		if (owningFactor == null)
			return "";
			
		String threatLabel = EAM.text("Threat");
		if (owningFactor.isDirectThreat())
			return createThreatAnnotationLabel(owningFactor, threatLabel);
		
		if (owningFactor.isThreatReductionResult())
			return createThreatReductionResultsAnnotationLabel(owningFactor, threatLabel);
		
		return "";
	}

	private String createThreatReductionResultsAnnotationLabel(Factor factor, String translatedThreatLabel)
	{
		ThreatReductionResult threatReductionResult = (ThreatReductionResult) factor;
		ORef relatedThreatRef = threatReductionResult.getRelatedThreatRef();
		if (relatedThreatRef.isInvalid())
			return " (" + translatedThreatLabel + ")";
		
		Cause threat = Cause.find(getProject(), relatedThreatRef);
		return createThreatAnnotationLabel(threat, translatedThreatLabel);
	}

	private String createThreatAnnotationLabel(Factor threat, String translatedThreatLabel)
	{
		return " (" + translatedThreatLabel + " = " + threat.getLabel() + ")";
	}

	private void writeObjective(UnicodeWriter out, ORef desireRef, String optionalAnnotationLabel) throws Exception
	{
		Desire desire = Desire.findDesire(getProject(), desireRef);
		writeStartElementWithAttribute(out, OBJECTIVE, ID, desire.getId().toString());

		writeIndicatorIds(out, desire.getRelevantIndicatorRefList());
		writeElement(out, NAME, buildObjectiveExportableName(desire, optionalAnnotationLabel));
		writeOptionalElement(out, COMMENT, desire, Objective.TAG_COMMENTS);
		writeProgressPercents(out, desire.getProgressPercentRefs());
		writeEndElement(out, OBJECTIVE);
	}

	private String buildObjectiveExportableName(Desire desire, String optionalAnnotationLabel)
	{
		String shortLabel = desire.getShortLabel();
		String label = desire.getLabel();
		String fullText = desire.getData(Objective.TAG_FULL_TEXT);		
		
		final String DELIMITER_TAG = "|";
		String name = shortLabel + DELIMITER_TAG + label + DELIMITER_TAG + fullText;
		if (optionalAnnotationLabel.length() > 0)
			name += optionalAnnotationLabel;
		
		return name;
	}
	
	private void writeProgressPercents(UnicodeWriter out, ORefList progressPercentRefs) throws Exception
	{
		progressPercentRefs.sort();
		writeStartElement(out, PROGRESS_PERCENT_REPORTS);
		int sequenceCounter = 0;
		for (int index = 0; index < progressPercentRefs.size(); ++index)
		{
			ProgressPercent progressPercent = ProgressPercent.find(getProject(), progressPercentRefs.get(index));
			String progressPercentComplete = progressPercent.getData(ProgressPercent.TAG_PERCENT_COMPLETE);
			String progressPercentDate = progressPercent.getData(ProgressPercent.TAG_DATE);
			String progressPercentCompleteNotes = progressPercent.getData(ProgressPercent.TAG_PERCENT_COMPLETE_NOTES);
			String[] valuesToVerify = new String[]{progressPercentComplete, progressPercentDate, progressPercentCompleteNotes, }; 
			if (isAtLeastOneStringNonBlank(valuesToVerify))
			{
				writeStartElementWithAttribute(out, PROGRESS_PERCENT_REPORT, SEQUENCE, ++sequenceCounter);
		
				writeOptionalElement(out, PROGRESS_PERCENT_COMPLETE, progressPercentComplete);
				writeOptionalElement(out, PROGRESS_PERCENT_DATE, progressPercentDate);
				writeOptionalElement(out, PROGRESS_PERCENT_COMMENT, progressPercentCompleteNotes);

				writeEndElement(out, PROGRESS_PERCENT_REPORT);
			}
		}
		
		writeEndElement(out, PROGRESS_PERCENT_REPORTS);
	}

	private void writeIndicatorIds(UnicodeWriter out, ORefList indicatorRefs) throws Exception
	{
		writeIds(out, indicatorRefs, INDICATORS,	INDICATOR_ID);
	}
	
	private void writeIds(UnicodeWriter out, ORefList indicatorRefs, String parentElementName, String idElementName) throws Exception
	{
		writeStartElement(out, parentElementName);
		writeIds(out, idElementName, indicatorRefs);		
		writeEndElement(out, parentElementName);
	}

	private void writeIds(UnicodeWriter out, String elementName, ORefList refs) throws Exception
	{
		for (int refIndex = 0; refIndex < refs.size(); ++refIndex)
		{
			writeElement(out, elementName, refs.get(refIndex).getObjectId().toString());
		}
	}

	private void writeIds(UnicodeWriter out, String parentElementName, String elementName, ORefList refs) throws Exception
	{
		out.writeln("<" + parentElementName + ">");
		writeIds(out, elementName, refs);
		out.writeln("</" + parentElementName + ">");
	}
	
	private void writeThreats(UnicodeWriter out) throws Exception
	{
		Factor[] directThreats = getProject().getCausePool().getDirectThreats();
		writeStartElement(out, THREATS);
		for (int index = 0; index < directThreats.length; ++index)
		{
			writeStartElementWithAttribute(out, THREAT, ID, directThreats[index].getId().toString());
			writeLabelElement(out, NAME, directThreats[index], Cause.TAG_LABEL);
			writeOptionalElement(out, THREAT_TAXONOMY_CODE, directThreats[index], Cause.TAG_TAXONOMY_CODE);
			ChoiceItem threatRatingValue = getProject().getThreatRatingFramework().getThreatThreatRatingValue(directThreats[index].getRef());
			if (threatRatingValue != null)
				writeOptionalRatingCodeElement(out, THREAT_TO_PROJECT_RANK, threatRatingValue.getCode());
			
			writeIndicatorIds(out, directThreats[index].getOnlyDirectIndicatorRefs());
			writeEndElement(out, THREAT);
		}
		
		writeEndElement(out, THREATS);
	}

	private void writeKeas(UnicodeWriter out, Target target) throws Exception
	{
		writeStartElement(out, KEY_ATTRIBUTES);
		
		if (target.isViabilityModeTNC())
			writeKeyEcologicalAttributeViability(out, target);
		
		writeEndElement(out, KEY_ATTRIBUTES);
	}

	private void writeKeyEcologicalAttributeViability(UnicodeWriter out, Target target) throws Exception
	{
		ORefList keaRefs = target.getKeyEcologicalAttributeRefs();
		for (int refIndex = 0; refIndex < keaRefs.size(); ++refIndex)
		{
			KeyEcologicalAttribute kea = KeyEcologicalAttribute.find(getProject(), keaRefs.get(refIndex));
			writeKea(out, target, kea);
		}
	}
	
	private void writeKeyEcologicalAttributeIndicatorViability(UnicodeWriter out, Target target, KeyEcologicalAttribute kea) throws Exception
	{
		writeStartElement(out, VIABILITY_ASSESSMENTS);
		ORefList indicatorRefs = kea.getIndicatorRefs();
		indicatorRefs.sort();
		for (int refIndex = 0; refIndex < indicatorRefs.size(); ++refIndex)
		{
			Indicator indicator = Indicator.find(getProject(), indicatorRefs.get(refIndex));
			writeViability(out, target.getRef(), kea, indicator);
		}				
		
		writeEndElement(out, VIABILITY_ASSESSMENTS);
	}

	private void writeViability(UnicodeWriter out, ORef targetRef, KeyEcologicalAttribute kea, Indicator indicator) throws Exception
	{
		writeStartElement(out, VIABILITY_ASSESSMENT);
		writeElement(out, INDICATOR_ID, indicator.getId().toString());
		
		writeThreshold(out, INDICATOR_DESCRIPTION_POOR, indicator, StatusQuestion.POOR);
		writeThreshold(out, INDICATOR_DESCRIPTION_FAIR, indicator, StatusQuestion.FAIR);
		writeThreshold(out, INDICATOR_DESCRIPTION_GOOD, indicator, StatusQuestion.GOOD);
		writeThreshold(out, INDICATOR_DESCRIPTION_VERY_GOOD, indicator, StatusQuestion.VERY_GOOD);
		
		writeOptionalRankingCodeElement(out, DESIRED_VIABILITY_RATING, indicator.getFutureStatusRating());
		writeCodeElement(out, SOURCE_INDICATOR_RATINGS, indicator.getData(Indicator.TAG_RATING_SOURCE), getCodeMapHelper().getMiradiToConProIndicatorRatingSourceMap());
		writeOptionalElement(out, DESIRED_RATING_DATE,  indicator, Indicator.TAG_FUTURE_STATUS_DATE);
		writeOptionalElement(out, KEA_AND_INDICATOR_COMMENT, indicator, Indicator.TAG_DETAIL);
		writeOptionalElement(out, INDICATOR_RATING_COMMENT, indicator, Indicator.TAG_VIABILITY_RATINGS_COMMENT);
		writeOptionalElement(out, DESIRED_RATING_COMMENT, indicator, Indicator.TAG_FUTURE_STATUS_COMMENT);
			
		writeEndElement(out, VIABILITY_ASSESSMENT);
	}

	private void writeThreshold(UnicodeWriter out, String elementName, Indicator indicator, String threshold) throws Exception
	{
		HashMap<String, String> stringMap = indicator.getThreshold().getStringMap().toHashMap();
		String value = stringMap.get(threshold);
		writeOptionalElement(out, elementName, value);
	}

	private void writeKea(UnicodeWriter out, Target target, KeyEcologicalAttribute kea) throws Exception
	{
		writeStartElement(out, KEY_ATTRIBUTE);

		writeLabelElement(out, NAME, kea, KeyEcologicalAttribute.TAG_LABEL);
		writeOptionalElement(out, CATEGORY, keyEcologicalAttributeTypeToXmlValue(kea.getKeyEcologicalAttributeType()));
		
		writeKeyEcologicalAttributeIndicatorViability(out, target, kea);
		
		writeEndElement(out, KEY_ATTRIBUTE);
	}

	private void writeTargets(UnicodeWriter out) throws Exception
	{
		Target[] targets = getProject().getTargetPool().getSortedTargets();
		Arrays.sort(targets, new ObjectToStringSorter());
		writeStartElement(out, TARGETS);
		for (int index = 0; index < targets.length; ++index)
		{
			Target target = targets[index];
			out.write("<" + TARGET + " " + ID + "='" + target.getId().toString() + "' " + SEQUENCE + "='" + index + 1 + "'>");
			
			writeLabelElement(out, TARGET_NAME, target, Target.TAG_LABEL);
			writeOptionalElement(out, TARGET_DESCRIPTION, target, Target.TAG_TEXT);
			writeOptionalElement(out, TARGET_DESCRIPTION_COMMENT, target, Target.TAG_COMMENTS);
			writeOptionalElement(out, TARGET_VIABILITY_COMMENT, target, Target.TAG_CURRENT_STATUS_JUSTIFICATION);
			writeOptionalRank(out, target);
			writeHabitatMappedCodes(out, target);
			writeNestedTargets(out, target);
			writeSimpleTargetLinkRatings(out, target);
			writeStresses(out, target);
			writeStrategyThreatTargetAssociations(out, target);
		
			writeKeas(out, target);
			
			writeEndElement(out, TARGET);
		}
		writeEndElement(out, TARGETS);
	}

	private void writeOptionalRank(UnicodeWriter out, Target target) throws Exception
	{
		String targetStatusCode = target.getTargetViability();
		if (targetStatusCode.length() == 0)
			return;
		
		writeStartElementWithAttribute(out, TARGET_VIABILITY_RANK, TARGET_VIABILITY_MODE, getTargetMode(target));
		writeCodeElement(out, targetStatusCode, getCodeMapHelper().getMiradiToConProRankingMap());
		writeEndElement(out, TARGET_VIABILITY_RANK);
	}

	private String getTargetMode(Target target)
	{
		if (target.isViabilityModeTNC())
			return getConproCode(target.getViabilityMode(), getCodeMapHelper().getMiradiToConProViabilityModeMap());
		
		return ConProMiradiCodeMapHelper.CONPRO_TARGET_SIMPLE_MODE_VALUE;
	}
	
	private void writeStrategyThreatTargetAssociations(UnicodeWriter out, Target target) throws Exception
	{
		writeStartElement(out, STRATEGY_THREAT_TARGET_ASSOCIATIONS);
		
		ThreatTargetChainObject threatTargetChainObject = new ThreatTargetChainObject(getProject());
		ORefSet upstreamThreatRefs = threatTargetChainObject.getUpstreamThreatRefsFromTarget(target);
		for(ORef threatRef : upstreamThreatRefs)
		{
			writeStrategyThreatTargetAssociations(out, target, threatRef);
		}
		
		writeEndElement(out, STRATEGY_THREAT_TARGET_ASSOCIATIONS);
	}

	private void writeStrategyThreatTargetAssociations(UnicodeWriter out, Target target, ORef threatRef) throws Exception
	{
		Cause threat = Cause.find(getProject(), threatRef);
		ORefList factorLinkReferrers = threat.findObjectsThatReferToUs(FactorLink.getObjectType());
		for (int refIndex = 0; refIndex < factorLinkReferrers.size(); ++refIndex)
		{
			FactorLink thisFactorLink = FactorLink.find(getProject(), factorLinkReferrers.get(refIndex));
			ORef strategyRef = getStrategyRef(thisFactorLink);
			if (!strategyRef.isInvalid())
				writeStrategyThreatTargetAssociation(out, threatRef, strategyRef);
		}	
	}

	private ORef getStrategyRef(FactorLink thisFactorLink)
	{
		if (Strategy.is(thisFactorLink.getFromFactorRef()))
			return thisFactorLink.getFromFactorRef();
		
		if (Strategy.is(thisFactorLink.getToFactorRef()))
			return thisFactorLink.getToFactorRef();
		
		return ORef.INVALID;
	}

	private void writeStrategyThreatTargetAssociation(UnicodeWriter out, ORef threatRef, ORef strategyRef) throws Exception
	{
		writeStartElement(out, STRATEGY_THREAT_TARGET_ASSOCIATION);
		
		writeElement(out, STRATEGY_ID, strategyRef.getObjectId().toString());
		writeElement(out, THREAT_ID, threatRef.getObjectId().toString());
		
		writeEndElement(out, STRATEGY_THREAT_TARGET_ASSOCIATION);
	}

	private void writeHabitatMappedCodes(UnicodeWriter out, Target target) throws Exception
	{
		CodeList conProHabitatCodeList = new CodeList();
		HashMap<String, String> habitatCodeMap = getCodeMapHelper().getMiradiToConProHabitatCodeMap();
		CodeList miradiHabitatCodeList = target.getCodeList(Target.TAG_HABITAT_ASSOCIATION);
		for (int codeIndex = 0; codeIndex < miradiHabitatCodeList.size(); ++codeIndex)
		{
			String miradiHabitatCode = miradiHabitatCodeList.get(codeIndex);
			String conProHabitatCode = habitatCodeMap.get(miradiHabitatCode);
			conProHabitatCodeList.add(conProHabitatCode);
		}
		
		writeCodeListElements(out, HABITAT_TAXONOMY_CODES, HABITAT_TAXONOMY_CODE, conProHabitatCodeList);
	}
	
	private void writeSimpleTargetLinkRatings(UnicodeWriter out, Target target) throws Exception
	{		
		writeStartElement(out, THREAT_TARGET_ASSOCIATIONS);

		ThreatTargetChainObject threatTargetChainObejct = new ThreatTargetChainObject(getProject());
		ORefSet upstreamThreats = threatTargetChainObejct.getUpstreamThreatRefsFromTarget(target);
		ORefList sortedUpstreamThreatRefs = new ORefList(upstreamThreats);
		sortedUpstreamThreatRefs.sort();
		for (int index = 0; index < sortedUpstreamThreatRefs.size(); ++index)
		{
			ORef threatRef = sortedUpstreamThreatRefs.get(index);
			writeSimpleTargetLinkRatings(out, threatRef, target.getRef());
		}
		
		writeEndElement(out, THREAT_TARGET_ASSOCIATIONS);
	}

	private void writeSimpleTargetLinkRatings(UnicodeWriter out, ORef threatRef, ORef targetRef) throws Exception, IOException
	{
		SimpleThreatRatingFramework simpleThreatFramework = getProject().getSimpleThreatRatingFramework();
		ThreatRatingBundle bundle = simpleThreatFramework.getBundle((FactorId)threatRef.getObjectId(), (FactorId)targetRef.getObjectId());
				
		int threatTargetRatingValue = simpleThreatFramework.getBundleValue(bundle).getNumericValue();
		
		writeStartElement(out, THREAT_TARGET_ASSOCIATION);
		writeElement(out, THREAT_ID, threatRef.getObjectId().toString());
		writeOptionalRatingCodeElement(out, THREAT_TO_TARGET_RANK, threatTargetRatingValue);
		writeOptionalRatingCodeElement(out, THREAT_SEVERITY, getSeverity(simpleThreatFramework, bundle));
		writeOptionalRatingCodeElement(out, THREAT_SCOPE, getScope(simpleThreatFramework, bundle));
		writeOptionalRatingCodeElement(out, THREAT_IRREVERSIBILITY, getIrreversibility(simpleThreatFramework, bundle));
		
		ThreatRatingCommentsData threatRatingCommentsData = getProject().getSingletonThreatRatingCommentsData();
		String simpleThreatRatingComment = threatRatingCommentsData.getSimpleThreatRatingComment(threatRef, targetRef);
		writeOptionalElement(out, THREAT_TARGET_COMMENT, simpleThreatRatingComment);
		
		writeEndElement(out, THREAT_TARGET_ASSOCIATION);
	}

	private int getIrreversibility(SimpleThreatRatingFramework simpleThreatFramework, ThreatRatingBundle bundle)
	{
		RatingCriterion irreversibilityCriterion = simpleThreatFramework.getIrreversibilityCriterion();
		ValueOption irreversibility = findValueOption(bundle.getValueId(irreversibilityCriterion.getId()));
		return irreversibility.getNumericValue();
	}

	private int getScope(SimpleThreatRatingFramework simpleThreatFramework, ThreatRatingBundle bundle)
	{
		RatingCriterion scopeCriterion = simpleThreatFramework.getScopeCriterion();
		ValueOption scope = findValueOption(bundle.getValueId(scopeCriterion.getId()));
		return scope.getNumericValue();
	}

	private int getSeverity(SimpleThreatRatingFramework simpleThreatFramework, ThreatRatingBundle bundle)
	{
		RatingCriterion severityCriterion = simpleThreatFramework.getSeverityCriterion();
		ValueOption severity = findValueOption(bundle.getValueId(severityCriterion.getId()));
		
		return severity.getNumericValue();
	}

	private ValueOption findValueOption(BaseId valueOptionId)
	{
		return (ValueOption)getProject().findObject(ValueOption.getObjectType(), valueOptionId);
	}
	
	private void writeNestedTargets(UnicodeWriter out, Target target) throws Exception
	{
		ORefList subTargetRefs = target.getSubTargetRefs();
		writeStartElement(out, NESTED_TARGETS);
		for (int refIndex = 0; refIndex < subTargetRefs.size(); ++refIndex)
		{
			SubTarget subTarget = SubTarget.find(getProject(), subTargetRefs.get(refIndex));
			writeStartElementWithAttribute(out, NESTED_TARGET, SEQUENCE, refIndex);
			writeLabelElement(out, NAME, subTarget, SubTarget.TAG_LABEL);
			writeElement(out, COMMENT, subTarget, SubTarget.TAG_DETAIL);
			writeEndElement(out, NESTED_TARGET);
		}
		writeEndElement(out, NESTED_TARGETS);
	}

	private void writeThreatStressRatings(UnicodeWriter out, Stress stress) throws Exception
	{
		ORefList referringRefs = stress.findObjectsThatReferToUs(ThreatStressRating.getObjectType());
		referringRefs.sort(new ThreatStressRatingSorterByThreatRef());
		writeStartElement(out, THREAT_STRESS_RATINGS);
		for (int refIndex = 0; refIndex < referringRefs.size(); ++refIndex)
		{
			
			ORef threatStressRatingRef = referringRefs.get(refIndex);
			ThreatStressRating threatStressRating = ThreatStressRating.find(getProject(), threatStressRatingRef);
			if (!threatStressRating.isActive())
				continue;
			
			writeStartElement(out, THREAT_STRESS_RATING);
			
			writeElement(out, THREAT_ID, threatStressRating.getThreatRef().getObjectId().toString());
			writeOptionalRatingCodeElement(out, CONTRIBUTING_RANK, threatStressRating, ThreatStressRating.TAG_CONTRIBUTION);
			writeOptionalRatingCodeElement(out, IRREVERSIBILITY_RANK, threatStressRating, ThreatStressRating.TAG_IRREVERSIBILITY);
			writeOptionalRatingCodeElement(out, STRESS_THREAT_TO_TARGET_RANK, threatStressRating.calculateThreatRating());
			
			writeEndElement(out, THREAT_STRESS_RATING);
		}
		
		writeEndElement(out, THREAT_STRESS_RATINGS);
	}

	private void writeStresses(UnicodeWriter out, Target target) throws Exception
	{
		ORefList stressRefs = target.getStressRefs();
		stressRefs.sort();
		writeStartElement(out, STRESSES);
		for (int refIndex = 0; refIndex < stressRefs.size(); ++refIndex)
		{
			writeStartElementWithAttribute(out, STRESS, SEQUENCE, refIndex);
			Stress stress = Stress.find(getProject(), stressRefs.get(refIndex));
			writeLabelElement(out, NAME, stress, Stress.TAG_LABEL);
			writeOptionalRatingCodeElement(out, STRESS_SEVERITY, stress.getData(Stress.TAG_SEVERITY));
			writeOptionalRatingCodeElement(out, STRESS_SCOPE, stress.getData(Stress.TAG_SCOPE));
			writeOptionalRatingCodeElement(out, STRESS_TO_TARGET_RANK, stress.getCalculatedStressRating());
			writeOptionalElement(out, STRESS_OVERRIDE_RANK, stress.getData(Stress.TAG_DETAIL));
			writeThreatStressRatings(out, stress);
			
			writeEndElement(out, STRESS);
		}
		writeEndElement(out, STRESSES);
	}

	private void writeoutProjectSummaryElement(UnicodeWriter out) throws Exception
	{
		ORef tncProjectDataRef = getProject().getSingletonObjectRef(TncProjectData.getObjectType());
		String tncProjectSharingCode = getProject().getObjectData(tncProjectDataRef, TncProjectData.TAG_PROJECT_SHARING_CODE);
		writeStartElementWithAttribute(out, PROJECT_SUMMARY, SHARE_OUTSIDE_ORGANIZATION, tncProjectSharingToXmlValue(tncProjectSharingCode));
	
			writeProjectId(out);
						
			writeElement(out, PROJECT_SUMMARY_NAME, getProjectName());
			
			writeOptionalElement(out, START_DATE, getProjectMetadata(), ProjectMetadata.TAG_START_DATE);
			writeOptionalAreaSize(out);
			writeOptionalLocation(out);
			
			writeOptionalElement(out, DESCRIPTION_COMMENT, getConcatenatedProjectDescriptionAndScope());
			writeOptionalElement(out, GOAL_COMMENT, getProjectMetadata(), ProjectMetadata.TAG_PROJECT_VISION);
			writeOptionalElement(out, PLANNING_TEAM_COMMENT, getProjectMetadata(), ProjectMetadata.TAG_TNC_PLANNING_TEAM_COMMENT);
			writeOptionalElement(out, LESSONS_LEARNED, getProjectMetadata(), ProjectMetadata.TAG_TNC_LESSONS_LEARNED);
			writeOptionalElement(out, RELATED_PROJECTS, getProjectMetadata(), ProjectMetadata.TAG_OTHER_ORG_RELATED_PROJECTS);
			writeOptionalElement(out, PARENT_CHILD, getTncProjectData(), TncProjectData.TAG_CON_PRO_PARENT_CHILD_PROJECT_TEXT);
			
			writeThreatRatingMode(out);

			writeOptionalElement(out, PROJECT_THREAT_RANK, getStressBasedOverallProjectRating());
			writeOptionalElement(out, PROJECT_VIABILITY_RANK, getComputedTncViability());
			writeTeamMembers(out);
			writeEcoregionCodes(out);
			writeCodeListElements(out, COUNTRIES, COUNTRY_CODE, getProjectMetadata(), ProjectMetadata.TAG_COUNTRIES);
			writeCodeListElements(out, OUS, OU_CODE, getProjectMetadata(), ProjectMetadata.TAG_TNC_OPERATING_UNITS);
			writeClassifications(out);
			
			writeElement(out, EXPORTER_NAME, MIRADI);
			out.writeln();
			writeElement(out, EXPORTER_VERSION, getMiradiVersionAsToken());
			out.writeln();
			writeElement(out, EXPORT_DATE, new MiradiMultiCalendar().toIsoDateString());
			out.writeln();
			
		writeEndElement(out, PROJECT_SUMMARY);
	}

	private void writeThreatRatingMode(UnicodeWriter out) throws Exception
	{
		String threatRatingMode = getCodeMapHelper().getMiradiToConProThreatRatingModeMap().get(getProjectMetadata().getThreatRatingMode());
		writeOptionalElement(out, STRESSLESS_THREAT_RANKING, threatRatingMode);
	}

	private void writeClassifications(UnicodeWriter out) throws Exception
	{
		writeStartElement(out, CLASSIFICATIONS);
		
		String rawClassifications = getTncProjectData().getClassifications();
		Vector<Vector<String>> classifications = new DelimitedFileLoader().getDelimitedContents(new StringReader(rawClassifications));
		final int CLASSIFICATION_ID_INDEX = 0;
		final int CLASSIFICATION_NAME_INDEX = 1;
		final int CLASSIFICATION_CATEGORY_NAME_INDEX = 2;
		for(Vector<String> row : classifications)
		{
			writeStartElement(out, CLASSIFICATION);
			
			writeOptionalElement(out, CLASSIFICATION_ID, row.get(CLASSIFICATION_ID_INDEX));
			writeOptionalElement(out, CLASSIFICATION_NAME, row.get(CLASSIFICATION_NAME_INDEX));
			writeOptionalElement(out, CLASSIFICATION_CATEGORY_NAME, row.get(CLASSIFICATION_CATEGORY_NAME_INDEX));
			
			writeEndElement(out, CLASSIFICATION);
		}
		
		writeEndElement(out, CLASSIFICATIONS);
	}

	private String getConcatenatedProjectDescriptionAndScope()
	{
		String concatenatedDescriptionAndScope = "";
		String projectDescriptionToConcatenate = concatenate(EAM.text("Project Description:"), getProjectMetadata().getProjectDescription());
		concatenatedDescriptionAndScope += projectDescriptionToConcatenate;
		if (concatenatedDescriptionAndScope.length() > 0)
			concatenatedDescriptionAndScope += "\n\n";
		
		String projectScopeToConcatenate = concatenate(EAM.text("Site/Scope Description:"), getProjectMetadata().getProjectScope());
		concatenatedDescriptionAndScope +=projectScopeToConcatenate;
		
		return concatenatedDescriptionAndScope;
	}

	private String concatenate(final String fieldLabel, String description)
	{
		String concatenated = "";
		if (description.length() > 0)
		{
			concatenated += fieldLabel;
			concatenated += "\n";
			concatenated +=description;
		}
		
		return concatenated;
	}
	
	private TncProjectData getTncProjectData()
	{
		ORef tncProjectDataRef = getProject().getSingletonObjectRef(TncProjectData.getObjectType());
		return TncProjectData.find(getProject(), tncProjectDataRef);
	}
	
	private String getMiradiVersionAsToken() throws Exception
	{
		String versionToken = VersionConstants.getVersionAndTimestamp();
		versionToken = versionToken.replaceAll(" ", "_");
		versionToken = versionToken.replaceAll("\\(", "");
		versionToken = versionToken.replaceAll("\\)", "");
		return versionToken;
	}

	private String getProjectName()
	{
		String projectName = getProjectMetadata().getProjectName();
		if (projectName.length() > 0)
			return projectName;
		
		return getProject().getFilename();
	}

	private void writeProjectId(UnicodeWriter out) throws Exception
	{
		String stringRefMapAsString = getProject().getMetadata().getData(ProjectMetadata.TAG_XENODATA_STRING_REF_MAP);
		StringRefMap stringRefMap = new StringRefMap(stringRefMapAsString);
		Set keys = stringRefMap.getKeys();
		for(Object key: keys)
		{
			if (!key.equals(CONPRO_CONTEXT))
				continue;
			
			ORef xenodataRef = stringRefMap.getValue((String) key);
			if (xenodataRef.isInvalid())
			{
				EAM.logWarning("Invalid Xenodata ref found for key: " + key + " while exporting.");
				continue;
			}

			Xenodata xenodata = Xenodata.find(getProject(), xenodataRef);
			String projectId = xenodata.getData(Xenodata.TAG_PROJECT_ID);

			writeStartElementWithAttribute(out, PROJECT_ID, CONTEXT_ATTRIBUTE, key.toString());
			writeXmlEncodedData(out, projectId);
			writeEndElement(out, PROJECT_ID);
		}
	}

	private String getComputedTncViability()
	{
		String code = Target.computeTNCViability(getProject());
		return rankingCodeToXmlValue(code);
	}

	private String getStressBasedOverallProjectRating()
	{
		int overallProjectRating = getProject().getStressBasedThreatRatingFramework().getOverallProjectRating();
		return ratingCodeToXmlValue(overallProjectRating);
	}
	
	private void writeEcoregionCodes(UnicodeWriter out) throws Exception
	{
		CodeList allTncEcoRegionCodes = new CodeList();
		allTncEcoRegionCodes.addAll(getProjectMetadata().getTncFreshwaterEcoRegion());
		allTncEcoRegionCodes.addAll(getProjectMetadata().getTncMarineEcoRegion());
		allTncEcoRegionCodes.addAll(getProjectMetadata().getTncTerrestrialEcoRegion());
				
		writeCodeListElements(out, ECOREGIONS, ECOREGION_CODE, allTncEcoRegionCodes);
	}
	
	private void writeOptionalAreaSize(UnicodeWriter out) throws IOException
	{
		double sizeInHectaresAsInt = getProjectMetadata().getSizeInHectaresAsDouble();
		if (sizeInHectaresAsInt == 0)
			return;
		
		writeStartElementWithAttribute(out, AREA_SIZE, AREA_SIZE_UNIT, "hectares");
		writeXmlEncodedData(out, Integer.toString((int)sizeInHectaresAsInt));
		writeEndElement(out, AREA_SIZE);
	}
	
	private void writeOptionalLocation(UnicodeWriter out) throws IOException, Exception
	{
		float latitudeAsFloat = getProjectMetadata().getLatitudeAsFloat();
		float longitudeAsFloat = getProjectMetadata().getLongitudeAsFloat();
		if (latitudeAsFloat == 0 && longitudeAsFloat == 0)
			return;
		
		writeStartElementWithAttribute(out, GEOSPATIAL_LOCATION, GEOSPATIAL_LOCATION_TYPE, "point");
		writeOptionalFloatElement(out, LATITUDE, latitudeAsFloat);
		writeOptionalFloatElement(out, LONGITUDE, longitudeAsFloat);
		writeEndElement(out, GEOSPATIAL_LOCATION);
	}
	
	private void writeTeamMembers(UnicodeWriter out) throws Exception
	{
		ORefList teamMemberRefs = getProject().getResourcePool().getTeamMemberRefs();
		writeStartElement(out, TEAM);
		for (int memberIndex = 0; memberIndex < teamMemberRefs.size(); ++memberIndex)
		{
			writeStartElement(out, PERSON);
			ProjectResource member = ProjectResource.find(getProject(), teamMemberRefs.get(memberIndex));
			writeMemberRoles(out, member);
			
			writeLabelElement(out, GIVEN_NAME, member, ProjectResource.TAG_GIVEN_NAME);
			writeLabelElement(out, SUR_NAME, member, ProjectResource.TAG_SUR_NAME);
			writeLabelElement(out, EMAIL, member, ProjectResource.TAG_EMAIL);
			writeOptionalElement(out, PHONE, member, ProjectResource.TAG_PHONE_NUMBER);
			writeOptionalElement(out, ORGANIZATION, member, ProjectResource.TAG_ORGANIZATION);
			writeEndElement(out, PERSON);	
		}
		writeEndElement(out, TEAM);
	}

	private void writeMemberRoles(UnicodeWriter out, ProjectResource member) throws Exception
	{
		ChoiceQuestion question = getProject().getQuestion(ResourceRoleQuestion.class);
		CodeList roleCodes = member.getRoleCodes();
		for(int index = 0; index < roleCodes.size(); ++index)
		{
			ChoiceItem role = question.findChoiceByCode(roleCodes.get(index));
			String roleLabel = getCodeMapHelper().getMiradiToConProTeamRolesMap().get(role.getCode());
			writeElement(out, ROLE, roleLabel);
		}
	}
	
	private void writeOptionalFloatElement(UnicodeWriter out, String elementName, float value) throws Exception
	{
		if (value == 0)
			return;
		
		writeOptionalElement(out, elementName, Float.toString(value));
	}

	private void writeCodeListElements(UnicodeWriter out, String parentElementName, String elementName, BaseObject object, String tag) throws Exception
	{
		CodeList codeList = object.getCodeList(tag);
		writeCodeListElements(out, parentElementName, elementName, codeList);
	}

	protected void writeCodeListElements(UnicodeWriter out, String parentElementName, String elementName, CodeList codeList) throws Exception
	{
		out.writeln("<" + parentElementName + ">");
		writeCodeListElements(out, elementName, codeList);
		out.writeln("</" + parentElementName + ">");
	}
	
	private void writeCodeListElements(UnicodeWriter out, String elementName, CodeList codeList) throws Exception
	{
		for (int codeIndex = 0; codeIndex < codeList.size(); ++codeIndex)
		{
			writeElement(out, elementName, codeList.get(codeIndex));
		}
	}
	
	private void writeElement(UnicodeWriter out, String elementName, String data) throws Exception
	{
		out.write("<" + elementName + ">");
		writeXmlEncodedData(out, data);
		out.writeln("</" + elementName + ">");
	}

	private void writeOptionalElement(UnicodeWriter out, String elementName, String data) throws Exception
	{
		if (data == null || data.length() == 0)
			return;
		
		writeElement(out, elementName, data);
	}
	
	private void writeXmlEncodedData(UnicodeWriter out, String data) throws IOException
	{
		out.write(XmlUtilities.getXmlEncoded(data));
	}
	
	private void writeOptionalElement(UnicodeWriter out, String elementName, BaseObject object, String fieldTag) throws Exception
	{
		writeOptionalElement(out, elementName, object.getData(fieldTag));
	}
	
	private void writeLabelElement(UnicodeWriter out, String elementName, BaseObject object, String tag) throws Exception
	{
		String label = object.getData(tag);
		if (label.length() == 0)
			label = UNSPECIFIED_LABEL;
		
		writeElement(out, elementName, label);
	}

	private void writeElement(UnicodeWriter out, String elementName, BaseObject object, String tag) throws Exception
	{
		writeElement(out, elementName, object.getData(tag));
	}

	private ProjectMetadata getProjectMetadata()
	{
		return getProject().getMetadata();
	}

	private void writeCodeElement(UnicodeWriter out, String elementName, String code, HashMap<String, String> map) throws Exception
	{
		writeStartElement(out, elementName);
		out.write(getConproCode(code, map));
		writeEndElement(out, elementName);
	}
		
	private void writeCodeElement(UnicodeWriter out, String code, HashMap<String, String> map) throws Exception
	{
		out.write(getConproCode(code, map));
	}

	private void writeOptionalRankingCodeElement(UnicodeWriter out, String elementName, String code) throws Exception
	{
		writeOptionalElement(out, elementName, rankingCodeToXmlValue(code));
	}
	
	private void writeOptionalRatingCodeElement(UnicodeWriter out, String elementName, BaseObject object, String tag) throws Exception
	{
		writeOptionalRatingCodeElement(out, elementName, object.getData(tag));
	}
	
	private void writeOptionalRatingCodeElement(UnicodeWriter out, String elementName, String code) throws Exception
	{
		writeOptionalElement(out, elementName, ratingCodeToXmlValue(code));
	}
	
	private void writeOptionalRatingCodeElement(UnicodeWriter out, String elementName, int code) throws Exception
	{
		writeOptionalElement(out, elementName, ratingCodeToXmlValue(code));
	}
	
	private void writeEndElement(UnicodeWriter out, String endElementName) throws IOException
	{
		out.writeln("</" + endElementName + ">");
	}
	
	private void writeStartElement(UnicodeWriter out, String startElementName) throws IOException
	{
		out.writeln("<" + startElementName + ">");
	}

	private void writeStartElementWithAttribute(UnicodeWriter out, String startElementName, String attributeName, int attributeValue) throws IOException
	{
		writeStartElementWithAttribute(out, startElementName, attributeName, Integer.toString(attributeValue));
	}
	
	private void writeStartElementWithAttribute(UnicodeWriter out, String startElementName, String attributeName, String attributeValue) throws IOException
	{
		out.write("<" + startElementName + " " + attributeName + "='" + attributeValue + "'>");
	}
		
	private String ratingCodeToXmlValue(int code)
	{
		return ratingCodeToXmlValue(Integer.toString(code));
	}
	
	private String statusConfidenceToXmlValue(String code)
	{
		HashMap<String, String> statusConfidenceMap = getCodeMapHelper().getMiradiToConProStatusConfidenceMap();
		return getCodeMapHelper().getSafeXmlCode(statusConfidenceMap, code);
	}
	
	private String trendToXmlValue(String code)
	{
		HashMap<String, String> trendMap = getCodeMapHelper().getMiradiToConProTrendMap();
		return getCodeMapHelper().getSafeXmlCode(trendMap, code);
	}

	private String tncProjectSharingToXmlValue(String code)
	{
		HashMap<String, String> tncProjectSharingMap = getCodeMapHelper().getMiradiToConProTncProjectSharingMap();
		return getCodeMapHelper().getSafeXmlCode(tncProjectSharingMap, code);
	}
	
	private String keyEcologicalAttributeTypeToXmlValue(String type)
	{
		HashMap<String, String> keaTypeMap = getCodeMapHelper().getMiradiToConProKeaTypeMap();
		return getCodeMapHelper().getSafeXmlCode(keaTypeMap, type);
	}
	
	private String ratingCodeToXmlValue(String code)
	{
		HashMap<String, String> rankingMap = getCodeMapHelper().getMiradiToConProRatingMap();
		return getCodeMapHelper().getSafeXmlCode(rankingMap, code);
	}
	
	private String rankingCodeToXmlValue(String code)
	{
		HashMap<String, String> rankingMap = getCodeMapHelper().getMiradiToConProRankingMap();
		return getCodeMapHelper().getSafeXmlCode(rankingMap, code);
	}
	
	private String statusCodeToXmlValue(String code)
	{
		HashMap<String, String> progressStatuMap = getCodeMapHelper().getMiradiToConProProgressStatusMap();
		return getCodeMapHelper().getSafeXmlCode(progressStatuMap, code);
	}
	
	private String getConproCode(String code, HashMap<String, String> map)
	{
		return ConProMiradiCodeMapHelper.getSafeXmlCode(map, code);
	}
	
	private ConProMiradiCodeMapHelper getCodeMapHelper()
	{
		return codeMapHelper;
	}
	
	class ThreatStressRatingSorterByThreatRef implements Comparator<ORef>
	{
		public int compare(ORef threatStressRatingRef1, ORef threatStressRatingRef2)
		{
			ThreatStressRating threatStressRating1 = ThreatStressRating.find(getProject(), threatStressRatingRef1);
			ThreatStressRating threatStressRating2 = ThreatStressRating.find(getProject(), threatStressRatingRef2);
			ORef threatRef1 = threatStressRating1.getThreatRef();
			ORef threatRef2 = threatStressRating2.getThreatRef();
			
			return threatRef1.compareTo(threatRef2);
		}	
	}
	
	private ConProMiradiCodeMapHelper codeMapHelper;
	private static final String UNSPECIFIED_LABEL = "[Unspecified]";
}
