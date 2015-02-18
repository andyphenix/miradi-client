/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
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
package org.miradi.xml.conpro.importer;

import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.martus.util.MultiCalendar;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.ChainWalker;
import org.miradi.diagram.ThreatTargetChainWalker;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.exceptions.CpmzVersionTooOldException;
import org.miradi.exceptions.UnsupportedNewVersionSchemaException;
import org.miradi.exceptions.ValidationException;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.migrations.RawProject;
import org.miradi.objectdata.BooleanData;
import org.miradi.objecthelpers.CodeToUserStringMap;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.RelevancyOverride;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.objecthelpers.StringRefMap;
import org.miradi.objecthelpers.ThreatTargetVirtualLinkHelper;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.Factor;
import org.miradi.objects.FactorLink;
import org.miradi.objects.Indicator;
import org.miradi.objects.IntermediateResult;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.Objective;
import org.miradi.objects.ProgressPercent;
import org.miradi.objects.ProgressReport;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Strategy;
import org.miradi.objects.Stress;
import org.miradi.objects.SubTarget;
import org.miradi.objects.TaggedObjectSet;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.ThreatRatingCommentsData;
import org.miradi.objects.ThreatStressRating;
import org.miradi.objects.TncProjectData;
import org.miradi.objects.ValueOption;
import org.miradi.objects.Xenodata;
import org.miradi.project.Project;
import org.miradi.project.ProjectInterface;
import org.miradi.project.threatrating.SimpleThreatRatingFramework;
import org.miradi.project.threatrating.ThreatRatingBundle;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.StrategyStatusQuestion;
import org.miradi.questions.ThreatRatingQuestion;
import org.miradi.questions.TncFreshwaterEcoRegionQuestion;
import org.miradi.questions.TncMarineEcoRegionQuestion;
import org.miradi.questions.TncTerrestrialEcoRegionQuestion;
import org.miradi.questions.ViabilityModeQuestion;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.ExpenseAssignmentSchema;
import org.miradi.schemas.FactorLinkSchema;
import org.miradi.schemas.FutureStatusSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.IntermediateResultSchema;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.schemas.ObjectiveSchema;
import org.miradi.schemas.ProgressPercentSchema;
import org.miradi.schemas.ProgressReportSchema;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.schemas.ProjectResourceSchema;
import org.miradi.schemas.ResourceAssignmentSchema;
import org.miradi.schemas.ResultsChainDiagramSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.StressSchema;
import org.miradi.schemas.SubTargetSchema;
import org.miradi.schemas.TaggedObjectSetSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.schemas.ThreatStressRatingSchema;
import org.miradi.schemas.TncProjectDataSchema;
import org.miradi.schemas.XenodataSchema;
import org.miradi.utils.CodeList;
import org.miradi.utils.DateRange;
import org.miradi.utils.DateUnitEffort;
import org.miradi.utils.DateUnitEffortList;
import org.miradi.utils.DoubleUtilities;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.HtmlUtilities;
import org.miradi.utils.ProgressInterface;
import org.miradi.utils.XmlUtilities2;
import org.miradi.xml.conpro.ConProMiradiCodeMapHelper;
import org.miradi.xml.conpro.ConProMiradiXml;
import org.miradi.xml.conpro.exporter.ConProMiradiXmlValidator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ConproXmlImporter implements ConProMiradiXml
{
	public ConproXmlImporter(Project projectToFill, ProgressInterface progressIndicatorToUse) throws Exception
	{
		project = projectToFill;
		progressIndicator = progressIndicatorToUse;
		
		codeMapHelper = new ConProMiradiCodeMapHelper();

		wrappedToDiagramMap = new HashMap<ORef, ORef>();
	}
		
	public void importConProProject(InputStreamWithSeek projectAsInputStream) throws Exception
	{
		loadXml(projectAsInputStream);
		importXml();
	}
	
	public void importConProProjectNumbers(InputStreamWithSeek projectAsInputStream, RawProject rawProjectToLoadInto) throws Exception
	{
		loadXml(projectAsInputStream);
		Node projectSumaryNode = getNode(getRootNode(), PROJECT_SUMMARY);
		ORef metadataRef = new ORef(ProjectMetadataSchema.getObjectType(), new BaseId(rawProjectToLoadInto.getProjectMetadataId()));
		importProjectId(rawProjectToLoadInto, projectSumaryNode, metadataRef);
		
		String parentChildData = getPathDataAsHtml(projectSumaryNode, new String[]{PARENT_CHILD});
		ORef tncProjectDataRef = rawProjectToLoadInto.getSingletonRef(TncProjectDataSchema.getObjectType());
		rawProjectToLoadInto.setObjectData(tncProjectDataRef, TncProjectData.TAG_CON_PRO_PARENT_CHILD_PROJECT_TEXT, parentChildData);
		progressIndicator.incrementProgress();
	}
	
	private void importProjectId(ProjectInterface projectInterface, Node projectSumaryNode, ORef metadataRef) throws Exception
	{
		NodeList projectIdNodes = getNodes(projectSumaryNode, new String[]{PROJECT_ID});
		StringRefMap stringRefMap = new StringRefMap();
		for (int nodeIndex = 0; nodeIndex < projectIdNodes.getLength(); ++nodeIndex) 
		{
			Node projectIdNode = projectIdNodes.item(nodeIndex);
			
			String projectId = getSafeNodeContent(projectIdNode);
			ORef xenodataRef = findOrCreateXenodataObject();
			projectInterface.setObjectData(xenodataRef, Xenodata.TAG_PROJECT_ID, projectId);

			String contextAttributeValue = getAttributeValue(projectIdNode, CONTEXT_ATTRIBUTE);
			stringRefMap.add(contextAttributeValue, xenodataRef);
		}
		
		projectInterface.setObjectData(metadataRef, ProjectMetadata.TAG_XENODATA_STRING_REF_MAP, stringRefMap.toJsonString());
	}

	private void loadXml(InputStreamWithSeek projectAsInputStream) throws Exception
	{
		InputSource inputSource = new InputSource(projectAsInputStream);
		document = createDocument(inputSource);
				
		String nameSpaceUri = document.getDocumentElement().getNamespaceURI();
		if (!isSameNameSpace(nameSpaceUri))
		{
			throw new Exception("Name space mismatch should be: " + PARTIAL_NAME_SPACE + " <br> however it is: " + nameSpaceUri); 
		}
				
		if (isUnsupportedNewVersion(nameSpaceUri))
		{
			throw new UnsupportedNewVersionSchemaException(nameSpaceUri);
		}
		
		if (isUnsupportedOldVersion(nameSpaceUri))
		{
			throw new CpmzVersionTooOldException();
		}
		
		projectAsInputStream.seek(0);			
		if (!new ConProMiradiXmlValidator().isValid(projectAsInputStream))
		{
			throw new ValidationException(EAM.text("File to import does not validate."));
		}
		
		xPath = createXPath();
	}

	private void importXml() throws Exception
	{
		importProjectSummaryElement();
		progressIndicator.incrementProgress();

		// NOTE: Must import methods before strategies, because methods have specific ids
		// which could conflict with activities that had been imported already
		// TODO: Should assign all our own ids and keep a map of theirs to ours,
		// to avoid any possible conflicts
		importMethods();
		progressIndicator.incrementProgress();

		importStrategies();
		progressIndicator.incrementProgress();
		
		importThreats();
		progressIndicator.incrementProgress();
		
		importIndicators();
		progressIndicator.incrementProgress();
		
		importObjectives();
		progressIndicator.incrementProgress();
		
		importTargets();
		progressIndicator.incrementProgress();
		
		setDiagramFactorDefaultLocations();
		progressIndicator.incrementProgress();
		
		createDefaultObjects();
		progressIndicator.incrementProgress();
		
		attachObjectivesToHolder();
		progressIndicator.incrementProgress();
		
		attachIndicatorsToHolder();
		progressIndicator.incrementProgress();
		
		updateObjectiveRelevantStrategyList();
		progressIndicator.incrementProgress();
		
		createTaggedThreatChains();
		progressIndicator.incrementProgress();
	}

	private void importStrategies() throws Exception
	{
		NodeList strategyNodeList = getNodes(getRootNode(), STRATEGIES, STRATEGY);
		for (int nodeIndex = 0; nodeIndex < strategyNodeList.getLength(); ++nodeIndex) 
		{
			Node strategyNode = strategyNodeList.item(nodeIndex);
			String strategyId = getAttributeValue(strategyNode, ID);
			ORef strategyRef = getProject().createObject(StrategySchema.getObjectType(), new BaseId(strategyId));
			importField(strategyNode, NAME, strategyRef, Strategy.TAG_LABEL);
			importField(strategyNode, TAXONOMY_CODE, strategyRef, Strategy.TAG_TAXONOMY_CODE);
			
			importStrategyStatus(strategyNode, strategyRef);
			importField(strategyNode, COMMENT, strategyRef, Strategy.TAG_COMMENTS);
			importField(strategyNode, LEGACY_TNC_STRATEGY_RATING, strategyRef, Strategy.TAG_LEGACY_TNC_STRATEGY_RANKING);
			importProgressReports(strategyNode, strategyRef, Strategy.TAG_PROGRESS_REPORT_REFS);
			importActivities(strategyNode, strategyRef);
			importTotalCost(strategyNode, STRATEGY_TOTAL_COST, strategyRef);
			
			createDiagramFactorAndAddToDiagram(strategyRef);
		}
	}

	private void importStrategyStatus(Node strategyNode, ORef strategyRef) throws Exception
	{
		String data = getNodeContent(strategyNode, SELECTED);
		String draftStatusValue = StrategyStatusQuestion.STATUS_REAL_CODE;
		if (isDraft(data))
			draftStatusValue = StrategyStatusQuestion.STATUS_DRAFT_CODE;
		
		setData(strategyRef, Strategy.TAG_STATUS, draftStatusValue);
	}

	private boolean isDraft(String data)
	{
		return !isTrue(data);
	}
	
	private boolean isTrue(String value)
	{
		if (value.length() == 0)
			return false;
		
		if (value.equals(BooleanData.BOOLEAN_TRUE))
			return true;
		
		if (value.equals(BooleanData.BOOLEAN_FALSE))
			return false;
		
		return Boolean.parseBoolean(value);
	}

	private void importActivities(Node strategyNode, ORef strategyRef) throws Exception
	{
		ORefList activityRefs = new ORefList();
		NodeList activityNodeList = getNodes(strategyNode, ACTIVITIES, ACTIVITY);
		for (int nodeIndex = 0; nodeIndex < activityNodeList.getLength(); ++nodeIndex) 
		{
			Node activityNode = activityNodeList.item(nodeIndex);
			ORef activityRef = getProject().createObject(TaskSchema.getObjectType());
			activityRefs.add(activityRef);
					
			importField(activityNode, NAME, activityRef, Task.TAG_LABEL);
			importWhenOverride(activityNode, activityRef);
			importTotalCost(activityNode, ACTIVITY_TOTAL_COST, activityRef);
			importField(activityNode, ACTIVITY_DETAILS, activityRef, Task.TAG_DETAILS);
			importProgressReports(activityNode, activityRef, Task.TAG_PROGRESS_REPORT_REFS);
		}
		
		setIdListFromRefListData(strategyRef, Strategy.TAG_ACTIVITY_IDS, activityRefs, TaskSchema.getObjectType());
	}

	private void importWhenOverride(Node activityNode, ORef activityRef) throws Exception
	{
		String startDateAsString = getNodeContent(activityNode, ACTIVITY_START_DATE);
		String endDateAsString = getNodeContent(activityNode, ACTIVITY_END_DATE);
		if (startDateAsString.length() > 0 && endDateAsString.length() > 0)
		{
			ORef resourceAssignmentRef = getProject().createObject(ResourceAssignmentSchema.getObjectType());
			DateUnitEffortList dateUnitEffortList = createDateUnitEffortList(startDateAsString, endDateAsString);
			setData(resourceAssignmentRef, ResourceAssignment.TAG_DATEUNIT_EFFORTS, dateUnitEffortList.toString());
			setIdListFromRefListData(activityRef, ResourceAssignment.TAG_RESOURCE_ASSIGNMENT_IDS, new ORefList(resourceAssignmentRef), ResourceAssignmentSchema.getObjectType());
		}
	}

	private void importTotalCost(Node node, String totalCostElementName, ORef ref) throws Exception
	{
		String totalCostAsString = getNodeContent(node, totalCostElementName);
		if (totalCostAsString.length() == 0)
			return;
		
		double totalCost = DoubleUtilities.toDoubleFromDataFormat(totalCostAsString);
		ORef expenseAssignmentRef = getProject().createObject(ExpenseAssignmentSchema.getObjectType());
		DateUnitEffortList dateUnitEffortList = createDateUnitEffortList(new DateUnit(), totalCost);
		setData(expenseAssignmentRef, ExpenseAssignment.TAG_DATEUNIT_EFFORTS, dateUnitEffortList.toString());
		setData(ref, BaseObject.TAG_EXPENSE_ASSIGNMENT_REFS, new ORefList(expenseAssignmentRef));
	}

	private DateUnitEffortList createDateUnitEffortList(String startDateAsString, String endDateAsString) throws Exception
	{
		MultiCalendar startDate = MultiCalendar.createFromIsoDateString(startDateAsString);
		MultiCalendar endDate = MultiCalendar.createFromIsoDateString(endDateAsString);
		DateRange dateRange = new DateRange(startDate, endDate);
		DateUnit dateUnit = DateUnit.createFromDateRange(dateRange);
		final double unitQuantity = 0.0;
		
		return createDateUnitEffortList(dateUnit, unitQuantity);
	}

	private DateUnitEffortList createDateUnitEffortList(DateUnit dateUnit, final double unitQuantity)
	{
		DateUnitEffort dateUnitEffort = new DateUnitEffort(dateUnit, unitQuantity);
		DateUnitEffortList dateUnitEffortList = new DateUnitEffortList();
		dateUnitEffortList.add(dateUnitEffort);
		
		return dateUnitEffortList;
	}
	
	private void importObjectives() throws Exception
	{
		NodeList objectiveNodeList = getNodes(getRootNode(), OBJECTIVES, OBJECTIVE);
		for (int nodeIndex = 0; nodeIndex < objectiveNodeList.getLength(); ++nodeIndex) 
		{
			Node objectiveNode = objectiveNodeList.item(nodeIndex);
			String objectiveId = getAttributeValue(objectiveNode, ID);
			ORef objectiveRef = getProject().createObject(ObjectiveSchema.getObjectType(), new BaseId(objectiveId));
			
			importRelevantIndicators(objectiveNode, objectiveRef);
			importField(objectiveNode, NAME, objectiveRef, Objective.TAG_LABEL);
			importField(objectiveNode, COMMENT, objectiveRef, Objective.TAG_COMMENTS);
			importProgressPercents(objectiveNode, objectiveRef);
		}
	}

	private void importProgressPercents(Node objectiveNode, ORef objectiveRef) throws Exception
	{
		ORefList progressPercentRefs = new ORefList();
		NodeList progressPercentNodeList = getNodes(objectiveNode, PROGRESS_PERCENT_REPORTS, PROGRESS_PERCENT_REPORT);
		for (int nodeIndex = 0; nodeIndex < progressPercentNodeList.getLength(); ++nodeIndex) 
		{
			Node progressPercentNode = progressPercentNodeList.item(nodeIndex);			
			ORef progressPercentRef = getProject().createObject(ProgressPercentSchema.getObjectType());
			
			importField(progressPercentNode, PROGRESS_PERCENT_COMPLETE, progressPercentRef, ProgressPercent.TAG_PERCENT_COMPLETE);
			importField(progressPercentNode, PROGRESS_PERCENT_DATE, progressPercentRef, ProgressPercent.TAG_DATE);
			importField(progressPercentNode, PROGRESS_PERCENT_COMMENT, progressPercentRef, ProgressPercent.TAG_PERCENT_COMPLETE_NOTES);

			progressPercentRefs.add(progressPercentRef);
		}
			
		setData(objectiveRef, Objective.TAG_PROGRESS_PERCENT_REFS, progressPercentRefs);
	}

	private void importRelevantIndicators(Node objectiveNode, ORef objectiveRef) throws Exception
	{
		RelevancyOverrideSet relevantIndicators = new RelevancyOverrideSet();
		ORefSet indicatorRefs = getIndicators(objectiveNode);
		for(ORef indicatorRef : indicatorRefs)
		{
			relevantIndicators.add(new RelevancyOverride(indicatorRef, true));
		}
		
		setData(objectiveRef, Objective.TAG_RELEVANT_INDICATOR_SET, relevantIndicators.toString());
	}
	
	private ORefSet getIndicators(Node objectNodeWithIndicators) throws Exception
	{
		ORefSet indicatorRefs = new ORefSet();
		NodeList indicatorIdNodes = getNodes(objectNodeWithIndicators, INDICATORS, INDICATOR_ID);
		for (int nodeIndex = 0; nodeIndex < indicatorIdNodes.getLength(); ++nodeIndex) 
		{
			Node indicatorNode = indicatorIdNodes.item(nodeIndex);
			BaseId indicatorId = new BaseId(indicatorNode.getTextContent());
			ORef indicatorRef = new ORef(IndicatorSchema.getObjectType(), indicatorId);
			indicatorRefs.add(indicatorRef);
		}
		
		return indicatorRefs;
	}

	private void importMethods() throws Exception
	{
		NodeList methodNodeList = getNodes(getRootNode(), METHODS, METHOD);
		for (int nodeIndex = 0; nodeIndex < methodNodeList.getLength(); ++nodeIndex) 
		{
			Node methodNode = methodNodeList.item(nodeIndex);
			String methodId = getAttributeValue(methodNode, ID);
			ORef methodRef = getProject().createObject(TaskSchema.getObjectType(), new BaseId(methodId));
			
			importField(methodNode, METHOD_NAME, methodRef, Task.TAG_LABEL);
			importField(methodNode, METHOD_DETAIL, methodRef, Task.TAG_DETAILS);
			importField(methodNode, METHOD_COMMENT, methodRef, Task.TAG_COMMENTS);
		}
	}
	
	private void importIndicators() throws Exception
	{
		NodeList indicatorNodeList = getNodes(getRootNode(), INDICATORS, INDICATOR);
		for (int nodeIndex = 0; nodeIndex < indicatorNodeList.getLength(); ++nodeIndex) 
		{
			Node indicatorNode = indicatorNodeList.item(nodeIndex);
			String indicatorId = getAttributeValue(indicatorNode, ID);
			ORef indicatorRef = getProject().createObject(IndicatorSchema.getObjectType(), new BaseId(indicatorId));
			
			importField(indicatorNode, NAME, indicatorRef, Indicator.TAG_LABEL);
			updateIndicatorMethodList(indicatorNode, indicatorRef);
			importCodeField(indicatorNode, PRIORITY, indicatorRef, Indicator.TAG_PRIORITY, getCodeMapHelper().getConProToMiradiRatingMap());
			importProgressReports(indicatorNode, indicatorRef, Indicator.TAG_PROGRESS_REPORT_REFS);
			importMeasurements(indicatorNode, indicatorRef);
			importField(indicatorNode, COMMENT, indicatorRef, Indicator.TAG_COMMENTS);
		}
	}
	
	private void updateIndicatorMethodList(Node indicatorNode, ORef indicatorRef) throws Exception
	{
		ORefList methodRefs = new ORefList();
		NodeList methodNodeList = getNodes(indicatorNode, METHODS, METHOD_ID);
		for (int nodeIndex = 0; nodeIndex < methodNodeList.getLength(); ++nodeIndex) 
		{
			Node methodNode = methodNodeList.item(nodeIndex);
			String methodId = methodNode.getTextContent();
			ORef methodRef = new ORef(TaskSchema.getObjectType(), new BaseId(methodId));
			methodRefs.add(methodRef);
		}
			
		setIdListFromRefListData(indicatorRef, Indicator.TAG_METHOD_IDS, methodRefs, TaskSchema.getObjectType());
	}
	
	private void importMeasurements(Node indicatorNode, ORef indicatorRef) throws Exception
	{
		ORefList measurementRefs = new ORefList();
		NodeList measurementNodeList = getNodes(indicatorNode, MEASUREMENTS, MEASUREMENT);
		for (int nodeIndex = 0; nodeIndex < measurementNodeList.getLength(); ++nodeIndex) 
		{
			Node measurementNode = measurementNodeList.item(nodeIndex);			
			ORef measurementRef = getProject().createObject(MeasurementSchema.getObjectType());
			
			importField(measurementNode, MEASUREMENT_SUMMARY, measurementRef, Measurement.TAG_SUMMARY);
			importField(measurementNode, MEASUREMENT_COMMENT, measurementRef, Measurement.TAG_COMMENTS);
			importField(measurementNode, MEASUREMENT_DATE, measurementRef, Measurement.TAG_DATE);
			importCodeField(measurementNode, MEASUREMENT_STATUS_CONFIDENCE, measurementRef, Measurement.TAG_STATUS_CONFIDENCE, getCodeMapHelper().getConProToMiradiStatusConfidenceMap());
			importCodeField(measurementNode, MEASUREMENT_TREND, measurementRef, Measurement.TAG_TREND, getCodeMapHelper().getConProToMiradiTrendMap());
			importCodeField(measurementNode, MEASUREMENT_RATING, measurementRef, Measurement.TAG_STATUS, getCodeMapHelper().getConProToMiradiRankingMap());


			measurementRefs.add(measurementRef);
		}
			
		setData(indicatorRef, Indicator.TAG_MEASUREMENT_REFS, measurementRefs);
	}

	private void importProgressReports(Node parentNode, ORef parentRef, String tag) throws Exception
	{
		ORefList progressReportRefs = new ORefList();
		NodeList progressReportNodeList = getNodes(parentNode, PROGRESS_REPORTS, PROGRESS_REPORT);
		for (int nodeIndex = 0; nodeIndex < progressReportNodeList.getLength(); ++nodeIndex) 
		{
			ORef progressReportRef = getProject().createObject(ProgressReportSchema.getObjectType());
			
			Node progressReportNode = progressReportNodeList.item(nodeIndex);
			importField(progressReportNode, PROGRESS_REPORT_DATE, progressReportRef, ProgressReport.TAG_PROGRESS_DATE);
			importCodeField(progressReportNode, PROGRESS_REPORT_STATUS, progressReportRef, ProgressReport.TAG_PROGRESS_STATUS, getCodeMapHelper().getConProToMiradiProgressStatusMap());
			importField(progressReportNode, PROGRESS_REPORT_COMMENT, progressReportRef, ProgressReport.TAG_DETAILS);

			progressReportRefs.add(progressReportRef);
		}
			
		setData(parentRef, tag, progressReportRefs);
	}

	private void importThreats() throws Exception
	{
		NodeList threatNodeList = getNodes(getRootNode(), THREATS, THREAT);
		for (int nodeIndex = 0; nodeIndex < threatNodeList.getLength(); ++nodeIndex) 
		{
			Node threatNode = threatNodeList.item(nodeIndex);
			String threatId = getAttributeValue(threatNode, ID);
			ORef threatRef = getProject().createObject(CauseSchema.getObjectType(), new BaseId(threatId));
			setData(threatRef, Cause.TAG_IS_DIRECT_THREAT, BooleanData.BOOLEAN_TRUE);
			
			importField(threatNode, NAME, threatRef, Cause.TAG_LABEL);
			importField(threatNode, THREAT_TAXONOMY_CODE, threatRef, Cause.TAG_TAXONOMY_CODE);
			ORefSet indicatorRefs = getIndicators(threatNode);
			setData(threatRef, Cause.TAG_INDICATOR_IDS, indicatorRefs.toIdList(IndicatorSchema.getObjectType()).toString());
			
			createDiagramFactorAndAddToDiagram(threatRef);
		}
	}

	private void importViability(Node keaNode, ORef targetRef, ORef keaRef) throws Exception
	{
		NodeList keaNodeList = getNodes(keaNode, VIABILITY_ASSESSMENTS, VIABILITY_ASSESSMENT);
		for (int nodeIndex = 0; nodeIndex < keaNodeList.getLength(); ++nodeIndex) 
		{
			Node viabilityAssessmentNode = keaNodeList.item(nodeIndex);
			ORef indicatorRef = getNodeAsRef(viabilityAssessmentNode, INDICATOR_ID, IndicatorSchema.getObjectType());
			ORefSet allKeaIndicatorRefsTobeImported = new ORefSet(indicatorRef);
			KeyEcologicalAttribute kea = KeyEcologicalAttribute.find(getProject(), keaRef);
			ORefList currentKeaIndicators = kea.getIndicatorRefs();
			allKeaIndicatorRefsTobeImported.addAllRefs(currentKeaIndicators);
			ORefList indicatorRefList = new ORefList(allKeaIndicatorRefsTobeImported);
			setData(keaRef, KeyEcologicalAttribute.TAG_INDICATOR_IDS, indicatorRefList.convertToIdList(IndicatorSchema.getObjectType()).toString());
			
			importIndicatorThresholds(viabilityAssessmentNode, indicatorRef);		
			
			
			importCodeField(viabilityAssessmentNode, SOURCE_INDICATOR_RATINGS, indicatorRef, Indicator.TAG_RATING_SOURCE, getCodeMapHelper().getConProToMiradiIndicatorRatingSourceMap());
			importField(viabilityAssessmentNode, KEA_AND_INDICATOR_COMMENT, indicatorRef, Indicator.TAG_DETAIL);
			importField(viabilityAssessmentNode, INDICATOR_RATING_COMMENT, indicatorRef, Indicator.TAG_VIABILITY_RATINGS_COMMENTS);
			
			importFutureStatusValues(viabilityAssessmentNode, indicatorRef);
		}
	}

	private void importFutureStatusValues(Node viabilityAssessmentNode, ORef indicatorRef) throws Exception
	{
		ORef futureStatusRef = getProject().createObject(FutureStatusSchema.getObjectType());
		setData(indicatorRef, Indicator.TAG_FUTURE_STATUS_REFS, new ORefList(futureStatusRef));
		
		importCodeField(viabilityAssessmentNode, DESIRED_VIABILITY_RATING, futureStatusRef, FutureStatusSchema.TAG_FUTURE_STATUS_RATING, getCodeMapHelper().getConProToMiradiRankingMap());
		importField(viabilityAssessmentNode, DESIRED_RATING_DATE, futureStatusRef, FutureStatusSchema.TAG_FUTURE_STATUS_DATE);
		importField(viabilityAssessmentNode, DESIRED_RATING_COMMENT, futureStatusRef, FutureStatusSchema.TAG_FUTURE_STATUS_COMMENTS);
	}

	private void importIndicatorThresholds(Node viabilityAssessmentNode, ORef indicatorRef) throws Exception
	{
		CodeToUserStringMap thresholds = new CodeToUserStringMap();
		String poorThreshold = getEncodedNodeContent(viabilityAssessmentNode, INDICATOR_DESCRIPTION_POOR);
		String fairThreshold = getEncodedNodeContent(viabilityAssessmentNode, INDICATOR_DESCRIPTION_FAIR);
		String goodThreshold = getEncodedNodeContent(viabilityAssessmentNode, INDICATOR_DESCRIPTION_GOOD);
		String veryGoodThreshold = getEncodedNodeContent(viabilityAssessmentNode, INDICATOR_DESCRIPTION_VERY_GOOD);
		thresholds.putUserString(StatusQuestion.POOR, poorThreshold);
		thresholds.putUserString(StatusQuestion.FAIR, fairThreshold);
		thresholds.putUserString(StatusQuestion.GOOD, goodThreshold);
		thresholds.putUserString(StatusQuestion.VERY_GOOD, veryGoodThreshold);
		setData(indicatorRef, Indicator.TAG_THRESHOLDS_MAP, thresholds.toJsonString());
	}

	private void importKeyEcologicalAttributes(Node targetNode, ORef targetRef) throws Exception
	{
		NodeList keaNodeList = getNodes(targetNode, KEY_ATTRIBUTES, KEY_ATTRIBUTE);
		for (int nodeIndex = 0; nodeIndex < keaNodeList.getLength(); ++nodeIndex) 
		{
			Node keaNode = keaNodeList.item(nodeIndex);
			ORef keaRef = getProject().createObject(KeyEcologicalAttributeSchema.getObjectType());
			importField(keaNode, NAME, keaRef, KeyEcologicalAttribute.TAG_LABEL);
			importCodeField(keaNode, CATEGORY, keaRef, KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE, getCodeMapHelper().getConProToMiradiKeaTypeMap());
			
			setData(targetRef, Target.TAG_VIABILITY_MODE, ViabilityModeQuestion.TNC_STYLE_CODE);
			String existingKeaIdsAsString = getProject().getObjectData(targetRef, Target.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS);
			IdList keaIds = new IdList(KeyEcologicalAttributeSchema.getObjectType(), existingKeaIdsAsString);
			if(!keaIds.contains(keaRef))
				keaIds.add(keaRef.getObjectId());
			
			setData(targetRef, Target.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS, keaIds.toString());
			importViability(keaNode, targetRef, keaRef);
		}
	}

	private Document createDocument(InputSource inputSource) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		documentFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		
		return documentBuilder.parse(inputSource);
	}

	private XPath createXPath()
	{
		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath  thisXPath = xPathFactory.newXPath();
		thisXPath.setNamespaceContext(new ConProMiradiNameSpaceContext());
		
		return thisXPath;
	}

	private void importProjectSummaryElement() throws Exception
	{
		ORef metadataRef = getProject().getMetadata().getRef();
		Node projectSumaryNode = getNode(getRootNode(), PROJECT_SUMMARY);
		
		String tncProjectSharingXmlValue = getAttributeValue(projectSumaryNode, SHARE_OUTSIDE_ORGANIZATION);
		ORef tncProjectDataRef = getSingletonTncProjectDataRef();
		final String booleanAsString = Boolean.toString(isTrue(tncProjectSharingXmlValue));
		importCodeField(tncProjectDataRef, TncProjectData.TAG_PROJECT_SHARING_CODE, getCodeMapHelper().getConProToMiradiTncProjectSharingMap(), booleanAsString);
		
		importField(projectSumaryNode, NAME, metadataRef,ProjectMetadata.TAG_PROJECT_NAME);
		importProjectId(getProject(), projectSumaryNode, metadataRef);
		importField(projectSumaryNode, START_DATE, metadataRef, ProjectMetadata.TAG_START_DATE);
		importField(projectSumaryNode, DATA_EFFECTIVE_DATE, metadataRef, ProjectMetadata.TAG_DATA_EFFECTIVE_DATE);
		importField(projectSumaryNode, AREA_SIZE, metadataRef, ProjectMetadata.TAG_PROJECT_AREA);	
		importField(projectSumaryNode, new String[]{GEOSPATIAL_LOCATION, LATITUDE}, metadataRef, ProjectMetadata.TAG_PROJECT_LATITUDE);
		importField(projectSumaryNode, new String[]{GEOSPATIAL_LOCATION, LONGITUDE}, metadataRef, ProjectMetadata.TAG_PROJECT_LONGITUDE);
		importField(projectSumaryNode, DESCRIPTION_COMMENT, metadataRef, ProjectMetadata.TAG_PROJECT_SCOPE);
		importField(projectSumaryNode, GOAL_COMMENT, metadataRef, ProjectMetadata.TAG_PROJECT_VISION);
		importField(projectSumaryNode, PLANNING_TEAM_COMMENT, metadataRef, ProjectMetadata.TAG_TNC_PLANNING_TEAM_COMMENTS);
		importField(projectSumaryNode, LESSONS_LEARNED, metadataRef, ProjectMetadata.TAG_TNC_LESSONS_LEARNED);
		importField(projectSumaryNode, RELATED_PROJECTS, metadataRef, ProjectMetadata.TAG_OTHER_ORG_RELATED_PROJECTS);
		importParentChildField(projectSumaryNode, tncProjectDataRef);
		importThreatRatingValueInMode(projectSumaryNode, metadataRef);
		importTeamMembers(projectSumaryNode, metadataRef);
		
		String[] ecoRegionElementHierarchy = new String[] {CONSERVATION_PROJECT, PROJECT_SUMMARY, ECOREGIONS, ECOREGION_CODE}; 
		String[] allEcoregionCodes = extractNodesAsList(generatePath(ecoRegionElementHierarchy));
		setData(metadataRef, ProjectMetadata.TAG_TNC_TERRESTRIAL_ECO_REGION, extractEcoregions(allEcoregionCodes, TncTerrestrialEcoRegionQuestion.class).toString());
		setData(metadataRef, ProjectMetadata.TAG_TNC_MARINE_ECO_REGION, extractEcoregions(allEcoregionCodes, TncMarineEcoRegionQuestion.class).toString());
		setData(metadataRef, ProjectMetadata.TAG_TNC_FRESHWATER_ECO_REGION, extractEcoregions(allEcoregionCodes, TncFreshwaterEcoRegionQuestion.class).toString());
		
		importClassificationCodes(projectSumaryNode);
		
		importField(projectSumaryNode, EXPORT_DATE, metadataRef, ProjectMetadata.TAG_TNC_DATABASE_DOWNLOAD_DATE);
		
		importCodeListField(generatePath(new String[] {CONSERVATION_PROJECT, PROJECT_SUMMARY, COUNTRIES, COUNTRY_CODE}), metadataRef, ProjectMetadata.TAG_COUNTRIES);
		importCodeListField(generatePath(new String[] {CONSERVATION_PROJECT, PROJECT_SUMMARY, OUS, OU_CODE}), metadataRef, ProjectMetadata.TAG_TNC_OPERATING_UNITS);
	}

	private void importParentChildField(Node projectSumaryNode, ORef tncProjectDataRef) throws Exception
	{
		importField(projectSumaryNode, PARENT_CHILD, tncProjectDataRef, TncProjectData.TAG_CON_PRO_PARENT_CHILD_PROJECT_TEXT);
	}

	private void importClassificationCodes(Node projectSumaryNode) throws Exception
	{
		CodeList classificationCodesToImport = extractAllClassificationCodes(projectSumaryNode);
		if (classificationCodesToImport.size() > 0)
			throw new RuntimeException("Not all classification Codes could imported." + classificationCodesToImport.toString());
	}

	private CodeList extractAllClassificationCodes(Node projectSumaryNode) throws Exception
	{
		CodeList extractedCodes = new CodeList();		
		NodeList classficiationIdNodes = getNodes(projectSumaryNode, CLASSIFICATIONS, CLASSIFICATION_ID);
		for (int nodeIndex = 0; nodeIndex < classficiationIdNodes.getLength(); ++nodeIndex) 
		{
			Node classficiationIdNode = classficiationIdNodes.item(nodeIndex);
			String classificationId = getSafeNodeContent(classficiationIdNode).trim();
			extractedCodes.add(classificationId);
		}
		
		return extractedCodes;
	}
	
	private ORef getSingletonTncProjectDataRef()
	{
		return getProject().getSingletonObjectRef(TncProjectDataSchema.getObjectType());
	}
	
	private ORef findOrCreateXenodataObject() throws Exception
	{
		ORefList xenodataRefs = getProject().getPool(XenodataSchema.getObjectType()).getRefList();
		if (xenodataRefs.isEmpty())
			return getProject().createObject(XenodataSchema.getObjectType());
			
		StringRefMap stringRefMap = getProject().getMetadata().getXenodataStringRefMap();
		Set keys = stringRefMap.getKeys();
		if (keys.size() == 0 && xenodataRefs.hasRefs())
			throw new RuntimeException("All Xenodata objects are orphans. Count =" + xenodataRefs.size());
		
		return xenodataRefs.getRefForType(XenodataSchema.getObjectType());
	}

	private void importTeamMembers(Node projectSumaryNode, ORef metadataRef) throws Exception
	{	
		NodeList teamMemberNodeList = getNodes(projectSumaryNode, TEAM, PERSON);
		for (int nodeIndex = 0; nodeIndex < teamMemberNodeList.getLength(); ++nodeIndex) 
		{
			Node teamMemberNode = teamMemberNodeList.item(nodeIndex);
			ORef projectResourceRef = getProject().createObject(ProjectResourceSchema.getObjectType());
			
			importProjectResourceRoles(teamMemberNode, projectResourceRef);		
			importField(teamMemberNode, GIVEN_NAME, projectResourceRef, ProjectResource.TAG_GIVEN_NAME);
			importField(teamMemberNode, SUR_NAME, projectResourceRef, ProjectResource.TAG_SUR_NAME);
			importField(teamMemberNode, EMAIL, projectResourceRef, ProjectResource.TAG_EMAIL);
			importField(teamMemberNode, PHONE, projectResourceRef, ProjectResource.TAG_PHONE_NUMBER);
			importField(teamMemberNode, ORGANIZATION, projectResourceRef, ProjectResource.TAG_ORGANIZATION);
		}
	}

	private void importProjectResourceRoles(Node teamMemberNode, ORef projectResourceRef) throws Exception
	{
		CodeList roleCodes = new CodeList();
		NodeList roleNodeList = getNodes(teamMemberNode, new String[]{ROLE});
		for (int nodeIndex = 0; nodeIndex < roleNodeList.getLength(); ++nodeIndex)
		{
			Node roleNode = roleNodeList.item(nodeIndex);
			String roleLabel = roleNode.getTextContent();
			String roleCode = getCodeMapHelper().getConProToMiradiTeamRolesMap().get(roleLabel);
			roleCodes.add(roleCode);		
		}
	
		setData(projectResourceRef, ProjectResource.TAG_ROLE_CODES, roleCodes.toString());
	}

	public void importTargets() throws Exception
	{
		NodeList targetNodeList = getNodes(getRootNode(), TARGETS, TARGET);
		for (int nodeIndex = 0; nodeIndex < targetNodeList.getLength(); ++nodeIndex) 
		{
			Node targetNode = targetNodeList.item(nodeIndex);
			String targetId = getAttributeValue(targetNode, ID);
			ORef targetRef = getProject().createObject(TargetSchema.getObjectType(), new BaseId(targetId));
			
			importField(targetNode, TARGET_NAME, targetRef, Target.TAG_LABEL);
			importField(targetNode, TARGET_DESCRIPTION, targetRef, Target.TAG_TEXT);
			importField(targetNode, TARGET_DESCRIPTION_COMMENT, targetRef, Target.TAG_COMMENTS);
			importField(targetNode, TARGET_VIABILITY_COMMENT, targetRef	, Target.TAG_CURRENT_STATUS_JUSTIFICATION);
			importCodeField(targetNode, TARGET_VIABILITY_RANK, targetRef, Target.TAG_TARGET_STATUS, getCodeMapHelper().getConProToMiradiRankingMap());
			importOptionalTargetViabilityRankElement(targetNode, targetRef);
			importCodeListField(targetNode, HABITAT_TAXONOMY_CODES, HABITAT_TAXONOMY_CODE, targetRef, Target.TAG_HABITAT_ASSOCIATION, getCodeMapHelper().getConProToMiradiHabitiatCodeMap());
			
			importSubTargets(targetNode, targetRef);
			createDiagramFactorAndAddToDiagram(targetRef);
			importThreatToTargetAssociations(targetNode, targetRef);
			importStrategyThreatAssociations(targetNode, targetRef);
			importStresses(targetNode, targetRef);
			
			importKeyEcologicalAttributes(targetNode, targetRef);
		}
	}

	private void importOptionalTargetViabilityRankElement(Node targetNode, ORef targetRef) throws Exception
	{
		Node node = getNode(targetNode, TARGET_VIABILITY_RANK);
		if (node == null)
			return;
		
		String targetViabilityMode = getAttributeValue(node, TARGET_VIABILITY_MODE);
		importCodeField(targetRef, Target.TAG_VIABILITY_MODE, getCodeMapHelper().getConProToMiradiViabilityModeMap(), targetViabilityMode);
	}
		
	private void importStrategyThreatAssociations(Node targetNode, ORef targetRef) throws Exception
	{
		NodeList strategyThreatAssociations = getNodes(targetNode, STRATEGY_THREAT_ASSOCIATIONS, STRATEGY_THREAT_ASSOCIATION);
		for (int nodeIndex = 0; nodeIndex < strategyThreatAssociations.getLength(); ++nodeIndex)
		{
			Node strategyThreatAssociationNode = strategyThreatAssociations.item(nodeIndex);
			ORef threatRef = getNodeAsRef(strategyThreatAssociationNode, THREAT_ID, CauseSchema.getObjectType());
			ORef strategyRef = getNodeAsRef(strategyThreatAssociationNode, STRATEGY_ID, StrategySchema.getObjectType());
			createFactorLinkAndAddToDiagram(strategyRef, threatRef);
		}
	}

	private void importThreatToTargetAssociations(Node targetNode, ORef targetRef) throws Exception
	{
		SimpleThreatRatingFramework framework = getProject().getSimpleThreatRatingFramework();
		NodeList threatTargetAssociations = getNodes(targetNode, THREAT_TARGET_ASSOCIATIONS, THREAT_TARGET_ASSOCIATION);
		ThreatRatingCommentsData threatRatingCommentsData = getProject().getSingletonThreatRatingCommentsData();
		for (int nodeIndex = 0; nodeIndex < threatTargetAssociations.getLength(); ++nodeIndex)
		{
			Node threatTargetAssociationNode = threatTargetAssociations.item(nodeIndex);
			ORef threatRef = getNodeAsRef(threatTargetAssociationNode, THREAT_ID, CauseSchema.getObjectType());
			createFactorLinkAndAddToDiagram(threatRef, targetRef);
			
			ThreatRatingBundle bundle = framework.getBundle(threatRef, targetRef);
			importThreatRatingField(threatTargetAssociationNode, THREAT_SCOPE, framework, bundle, Stress.TAG_SCOPE);
			importThreatRatingField(threatTargetAssociationNode, THREAT_SEVERITY, framework, bundle, Stress.TAG_SEVERITY);
			importThreatRatingField(threatTargetAssociationNode, THREAT_IRREVERSIBILITY, framework, bundle, ThreatStressRating.TAG_IRREVERSIBILITY);			
			framework.saveBundle(bundle);
			
			String threatTargetKey = ThreatRatingCommentsData.createKey(threatRef, targetRef);
			importThreatRatingCommentsField(threatTargetAssociationNode, THREAT_TARGET_COMMENT, threatRatingCommentsData, threatTargetKey);
		}
	}
	
	private void importThreatRatingField(Node threatTargetAssociationNode, String element, SimpleThreatRatingFramework framework, ThreatRatingBundle bundle, String criterionLabel) throws Exception
	{
		String rawCode = getNodeContent(threatTargetAssociationNode, element);
		if (rawCode.length() == 0)
			return;
		
		String convertedCode = getCodeMapHelper().getConProToMiradiRatingMap().get(rawCode);
		ValueOption valueOption = framework.findValueOptionByNumericValue(Integer.parseInt(convertedCode));
		BaseId criterionId = framework.findCriterionByLabel(criterionLabel).getId();
		bundle.setValueId(criterionId, valueOption.getId());
	}

	private void importSubTargets(Node targetNode, ORef targetRef) throws Exception
	{
		ORefList subTargetRefs = new ORefList();
		NodeList subTargetNodes = getNodes(targetNode, NESTED_TARGETS, NESTED_TARGET);
		for (int nodeIndex = 0; nodeIndex < subTargetNodes.getLength(); ++nodeIndex)
		{
			ORef subTargetRef = getProject().createObject(SubTargetSchema.getObjectType());
			Node subTargetNode = subTargetNodes.item(nodeIndex);
			
			importField(subTargetNode, NAME, subTargetRef, SubTarget.TAG_LABEL);
			importField(subTargetNode, COMMENT, subTargetRef, SubTarget.TAG_DETAIL);
			
			subTargetRefs.add(subTargetRef);
		}
		
		setData(targetRef, Target.TAG_SUB_TARGET_REFS, subTargetRefs);
	}

	private void importStresses(Node targetNode, ORef targetRef) throws Exception
	{
		ORefList stressRefs = new ORefList();
		NodeList stressNodes = getNodes(targetNode, STRESSES, STRESS);
		for (int nodeIndex = 0; nodeIndex < stressNodes.getLength(); ++nodeIndex)
		{
			ORef stressRef = getProject().createObject(StressSchema.getObjectType());
			Node stressNode = stressNodes.item(nodeIndex);
			
			importField(stressNode, NAME, stressRef, Stress.TAG_LABEL); 
			importCodeField(stressNode, STRESS_SEVERITY, stressRef, Stress.TAG_SEVERITY, getCodeMapHelper().getConProToMiradiRatingMap());
			importCodeField(stressNode, STRESS_SCOPE, stressRef, Stress.TAG_SCOPE, getCodeMapHelper().getConProToMiradiRatingMap());
			stressRefs.add(stressRef);
			setData(targetRef, Target.TAG_STRESS_REFS, stressRefs);
			
			createThreatStressRatings(targetRef, stressRef);
			importField(stressNode, STRESS_OVERRIDE_RANK, stressRef, Stress.TAG_DETAIL);
			importField(stressNode, STRESS_COMMENT, stressRef, Stress.TAG_COMMENTS);
			populateThreatStressRatings(stressNode, targetRef, stressRef);
		}
	}
	
	// FIXME low: Can we use the ThreatStressRatingEnsurer instead of manually creating?
	private void createThreatStressRatings(ORef targetRef, ORef stressRef) throws Exception
	{
		ThreatTargetChainWalker threatTargetChainObject = new ThreatTargetChainWalker(getProject());
		Target target = Target.find(getProject(), targetRef);
		ORefSet upstreatThreatRefs = threatTargetChainObject.getUpstreamThreatRefsFromTarget(target);
		for(ORef threatRef : upstreatThreatRefs)
		{
			ORef threatStressRatingRef = getProject().createObject(ThreatStressRatingSchema.getObjectType());
			getProject().setObjectData(threatStressRatingRef, ThreatStressRating.TAG_STRESS_REF, stressRef.toString());
			getProject().setObjectData(threatStressRatingRef, ThreatStressRating.TAG_THREAT_REF, threatRef.toString());
		}
	}

	private void populateThreatStressRatings(Node stressNode, ORef targetRef, ORef stressRef) throws Exception
	{
		ThreatTargetVirtualLinkHelper threatTargetVirtualLink = new ThreatTargetVirtualLinkHelper(getProject());
		NodeList threatStressRatingNodes = getNodes(stressNode, THREAT_STRESS_RATINGS, THREAT_STRESS_RATING);
		for (int nodeIndex = 0; nodeIndex < threatStressRatingNodes.getLength(); ++nodeIndex)
		{
			Node threatStressRatingNode = threatStressRatingNodes.item(nodeIndex);
			
			ORef threatRef = getNodeAsRef(threatStressRatingNode, THREAT_ID, CauseSchema.getObjectType());			
			ORef threatStressRatingRef = threatTargetVirtualLink.findThreatStressRatingReferringToStress(threatRef, targetRef, stressRef);
			
			importCodeField(threatStressRatingNode, CONTRIBUTING_RANK, threatStressRatingRef, ThreatStressRating.TAG_CONTRIBUTION, getCodeMapHelper().getConProToMiradiRatingMap());
			importCodeField(threatStressRatingNode, IRREVERSIBILITY_RANK, threatStressRatingRef, ThreatStressRating.TAG_IRREVERSIBILITY, getCodeMapHelper().getConProToMiradiRatingMap());
			
			setData(threatStressRatingRef, ThreatStressRating.TAG_IS_ACTIVE, BooleanData.BOOLEAN_TRUE);
		}		
	}

	private CodeList extractEcoregions(String[] allEcoregionCodes, Class questionClass)
	{
		ChoiceQuestion question = StaticQuestionManager.getQuestion(questionClass);
		CodeList ecoregionCodes = new CodeList();
		for (int i= 0; i < allEcoregionCodes.length; ++i)
		{
			ChoiceItem choiceItem = question.findChoiceByCode(allEcoregionCodes[i]);
			if (choiceItem != null)
				ecoregionCodes.add(allEcoregionCodes[i]);
		}
		
		return ecoregionCodes;
	}

	private void importCodeListField(String path, ORef objectRef, String tag) throws Exception
	{
		CodeList codes = new CodeList(extractNodesAsList(path));
		setData(objectRef, tag, codes.toString());
	}

	private void importThreatRatingValueInMode(Node projectSummaryNode, ORef metadataRef) throws Exception
	{
		String booleanAsString = getBooleanValue(projectSummaryNode, new String[]{STRESSLESS_THREAT_RANKING, });
		String threatRatingModeCode = getCodeMapHelper().getConProToMiradiThreatRatingModeMap().get(booleanAsString);
		setData(metadataRef, ProjectMetadata.TAG_THREAT_RATING_MODE, threatRatingModeCode);
	}

	private void importThreatRatingCommentsField(Node node, String path, ThreatRatingCommentsData threatRatingCommentsData, String threatTargetKey) throws Exception
	{
		String threatRatingComments = getPathData(node, new String[]{path,});
		threatRatingComments = XmlUtilities2.getXmlEncoded(threatRatingComments);
		
		CodeToUserStringMap threatRatingCommentsMap = threatRatingCommentsData.getThreatRatingCommentsMap();
		threatRatingCommentsMap.putUserString(threatTargetKey, threatRatingComments);
		String threatRatingCommentsTag = threatRatingCommentsData.getThreatRatingCommentsMapTag();
		importField(threatRatingCommentsData.getRef(), threatRatingCommentsTag, threatRatingCommentsMap.toJsonString());
	}
	
	private void importField(Node node, String path, ORef ref, String tag) throws Exception
	{
		importField(node, new String[]{path,}, ref, tag);
	}
	
	private void importField(Node node, String[] elements, ORef ref, String tag) throws Exception 
	{
		String data = getPathDataAsHtml(node, elements);
		importField(ref, tag, data);
	}

	private String getPathDataAsHtml(Node node, String[] elements) throws Exception
	{
		String data = getPathData(node, elements);
		data = XmlUtilities2.getXmlEncoded(data);
		data = HtmlUtilities.replaceNonHtmlNewlines(data);
		
		return data;
	}

	private String getPathData(Node node, String[] elements) throws XPathExpressionException
	{
		String generatedPath = generatePath(elements);
		return getXPath().evaluate(generatedPath, node);
	}

	private void importField(ORef ref, String tag, String data)	throws Exception
	{
		setData(ref, tag, data);
	}
		
	private void importCodeField(Node node, String element, ORef ref, String tag, HashMap<String, String> map) throws Exception
	{
		importCodeField(node, new String[]{element}, ref, tag, map);
	}
	
	private void importCodeField(Node node, String[] elements, ORef ref, String tag, HashMap<String, String> map) throws Exception
	{
		String trimmedRawCode = getPathData(node, elements).trim();
		importCodeField(ref, tag, map, trimmedRawCode);
	}

	private void importCodeField(ORef ref, String tag, HashMap<String, String> map, String rawCode) throws Exception
	{
		String safeCode = getCodeMapHelper().getSafeXmlCode(map, rawCode);
		importField(ref, tag, safeCode);
	}
	
	private void importCodeListField(Node node, String parentElement, String childElement, ORef ref, String tag, HashMap<String, String> conProToMiradiCodeMap) throws Exception
	{
		CodeList codes = new CodeList();
		NodeList nodes = getNodes(node, new String[]{parentElement, childElement});
		for (int nodeIndex = 0; nodeIndex < nodes.getLength(); ++nodeIndex)
		{
			Node thisNode = nodes.item(nodeIndex);
			String conProCode = thisNode.getTextContent();
			String miradiCode = conProToMiradiCodeMap.get(conProCode);
			if (miradiCode != null)
				codes.add(miradiCode);
		}
		
		setData(ref, tag, codes.toString());
	}
	
	private void setIdListFromRefListData(ORef ref, String tag, ORefList refList, int type) throws Exception
	{
		setData(ref, tag, refList.convertToIdList(type).toString());
	}
	
	private void setData(ORef ref, String tag, String data) throws Exception
	{
		getProject().setObjectData(ref, tag, data.trim());
	}
	
	private void setData(ORef ref, String tag, ORefList refList) throws Exception
	{
		getProject().setObjectData(ref, tag, refList.toString());
	}

	public String generatePath(String[] pathElements)
	{
		String path = "";
		for (int index = 0; index < pathElements.length; ++index)
		{
			path += getPrefixedElement(pathElements[index]);
			if ((index + 1) < pathElements.length)
				path += "/";
		}
		return path;
	}
	
	private String getBooleanValue(Node node, String[] path) throws XPathExpressionException
	{
		String rawValue = getPathData(node, path);
		
		return Boolean.toString(isTrue(rawValue));
	}
	
	public String[] extractNodesAsList(String path) throws Exception
	{
		XPathExpression expression = getXPath().compile(path);
		NodeList nodeList = (NodeList) expression.evaluate(getDocument(), XPathConstants.NODESET);
		Vector<String> nodes = new Vector<String>();
		for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); ++nodeIndex) 
		{
			nodes.add(nodeList.item(nodeIndex).getTextContent());
		}
		
		return nodes.toArray(new String[0]);
	}
	
	private String getAttributeValue(Node elementNode, String attributeName)
	{
		NamedNodeMap attributes = elementNode.getAttributes();
		Node attributeNode = attributes.getNamedItem(attributeName);
		return attributeNode.getNodeValue();
	}
	
	private ORef getNodeAsRef(Node node, String element, int type) throws Exception
	{
		String idAsString = getNodeContent(node, element);
		return new ORef(type, new BaseId(idAsString));
	}
	
	private Node getNode(Node node, String element) throws Exception
	{
		String path = generatePath(new String[]{element});
		XPathExpression expression = getXPath().compile(path);
		return (Node) expression.evaluate(node, XPathConstants.NODE);
	}
	
	private Node getRootNode() throws Exception
	{
		return getNode(generatePath(new String[]{CONSERVATION_PROJECT}));
	}
	
	private Node getNode(String path) throws Exception
	{
		XPathExpression expression = getXPath().compile(path);
		return (Node) expression.evaluate(getDocument(), XPathConstants.NODE);
	}
	
	private String getEncodedNodeContent(Node node, String element) throws Exception
	{
		String nodeConent = getNodeContent(node, element);
		nodeConent = XmlUtilities2.getXmlEncoded(nodeConent);
		
		return nodeConent;
	}
	
	private String getNodeContent(Node node, String element) throws Exception
	{
		Node foundNode = getNode(node, element);
		return getSafeNodeContent(foundNode);
	}

	private String getSafeNodeContent(Node foundNode)
	{
		if (foundNode == null)
			return "";
		
		return foundNode.getTextContent();
	}
	
	private NodeList getNodes(Node node, String[] pathElements) throws Exception
	{
		String path = generatePath(pathElements);
		XPathExpression expression = getXPath().compile(path);
		
		return (NodeList) expression.evaluate(node, XPathConstants.NODESET);
	}
	
	private NodeList getNodes(Node node, String containerName, String contentName) throws Exception
	{
		return getNodes(node, new String[]{containerName, contentName});
	}
	
	private String getPrefixedElement(String elementName)
	{
		return PREFIX + elementName;
	}
	
	private Project getProject()
	{
		return project;
	}
	
	public Document getDocument()
	{
		return document;
	}

	public XPath getXPath()
	{
		return xPath;
	}
	
	private ConProMiradiCodeMapHelper getCodeMapHelper()
	{
		return codeMapHelper;
	}
	
	private void createDiagramFactorAndAddToDiagram(ORef factorRef) throws Exception
	{
		ORef diagramFactorRef = getProject().createObject(DiagramFactorSchema.getObjectType());
		getProject().setObjectData(diagramFactorRef, DiagramFactor.TAG_WRAPPED_REF, factorRef.toString());
		
		wrappedToDiagramMap.put(factorRef, diagramFactorRef);
		appendRefToDiagramObject(DiagramObject.TAG_DIAGRAM_FACTOR_IDS, diagramFactorRef);
	}
	
	private ORef createFactorLinkAndAddToDiagram(ORef fromRef, ORef toRef) throws Exception
	{
		ORef foundFactorLinkRef = getExistingLink(fromRef, toRef);
		if (!foundFactorLinkRef.isInvalid())
			return foundFactorLinkRef;
		
		ORef createdFactorLinkRef = getProject().createObject(FactorLinkSchema.getObjectType());
		project.setObjectData(createdFactorLinkRef, FactorLink.TAG_FROM_REF, fromRef.toString());
		project.setObjectData(createdFactorLinkRef, FactorLink.TAG_TO_REF, toRef.toString());
		
		ORef fromDiagramFactorRef = wrappedToDiagramMap.get(fromRef);
		ORef toDiagramFactorRef = wrappedToDiagramMap.get(toRef);
			
		ORef diagramLinkRef = getProject().createObject(DiagramLinkSchema.getObjectType());
		
		project.setObjectData(diagramLinkRef, DiagramLink.TAG_WRAPPED_ID, createdFactorLinkRef.getObjectId().toString());
    	project.setObjectData(diagramLinkRef, DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID, fromDiagramFactorRef.getObjectId().toString());
    	project.setObjectData(diagramLinkRef, DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID, toDiagramFactorRef.getObjectId().toString());
	
		wrappedToDiagramMap.put(createdFactorLinkRef, diagramLinkRef);
		appendRefToDiagramObject(DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, diagramLinkRef);
		
		return createdFactorLinkRef;
	}
	
	private ORef getExistingLink(ORef fromRef, ORef toRef)
	{
		Factor fromFactor = Factor.findFactor(getProject(), fromRef);
		Factor toFactor = Factor.findFactor(getProject(), toRef);
		return getProject().getFactorLinkPool().getLinkedRef(fromFactor, toFactor);
	}

	private void appendRefToDiagramObject(String tag, ORef refToAdd) throws Exception
	{
		ORef conceptualModelRef = getFirstAndOnlyDiagramObjectRef();

		IdList idList = new IdList(refToAdd.getObjectType(), getProject().getObjectData(conceptualModelRef, tag));
		idList.add(refToAdd.getObjectId());
		
		setData(conceptualModelRef, tag, idList.toString());
	}

	private ORef getFirstAndOnlyDiagramObjectRef()
	{
		ORefList conceptualModelRefs = getProject().getConceptualModelDiagramPool().getRefList();
		ORef conceptualModelRef = conceptualModelRefs.get(0);
		return conceptualModelRef;
	}
	
	private boolean isUnsupportedNewVersion(String nameSpaceUri) throws Exception
	{
		return getSchemaVersionToImport(nameSpaceUri).compareTo(NAME_SPACE_VERSION) > 0;
	}
	
	private boolean isUnsupportedOldVersion(String nameSpaceUri) throws Exception
	{
		return getSchemaVersionToImport(nameSpaceUri).compareTo(NAME_SPACE_VERSION) < 0;
	}

	private boolean isSameNameSpace(String nameSpaceUri) throws Exception
	{
		return nameSpaceUri.startsWith(PARTIAL_NAME_SPACE);
	}
	
	private String getSchemaVersionToImport(String nameSpaceUri) throws Exception
	{
		int lastSlashIndexBeforeVersion = nameSpaceUri.lastIndexOf("/");
		String versionAsString = nameSpaceUri.substring(lastSlashIndexBeforeVersion + 1, nameSpaceUri.length());
		
		return versionAsString;
	}
	
	private void setDiagramFactorDefaultLocations() throws Exception
	{
		ORefList nonDraftStrategyRefs = new ORefList(getProject().getStrategyPool().getNonDraftStrategies());
		setDiagramFactorLocation(nonDraftStrategyRefs, NON_DRAFT_STRATEGY_X_COLUMN);
		
		ORefList draftStrategyRefs = new ORefList(getProject().getStrategyPool().getDraftAndNonDraftStrategies());
		setDiagramFactorLocation(draftStrategyRefs, DRAFT_STRATEGY_X_COLUMN);
		
		setDiagramFactorLocation(getProject().getPool(CauseSchema.getObjectType()).getORefList(), CAUSE_X_COLUMN);
		setDiagramFactorLocation(getProject().getPool(TargetSchema.getObjectType()).getORefList(), TARGET_X_COLUMN);
	}
			
	private void setDiagramFactorLocation(ORefList factorRefs, int xPosition) throws Exception
	{
		DiagramObject diagramObject = ConceptualModelDiagram.find(getProject(), getFirstAndOnlyDiagramObjectRef());
		int VERTICAL_SPACE_INBETWEEN = 90;
		for (int index = 0; index < factorRefs.size(); ++index)
		{
			int rowIndex = (index + 1);
			int y = rowIndex * VERTICAL_SPACE_INBETWEEN;
			Point location = new Point(xPosition, y);
			
			DiagramFactor diagramFactor = diagramObject.getDiagramFactor(factorRefs.get(index));
			CommandSetObjectData setLocation = new CommandSetObjectData(diagramFactor.getRef(), DiagramFactor.TAG_LOCATION, EnhancedJsonObject.convertFromPoint(location));
			getProject().executeCommand(setLocation);
		}
	}
	
	private void createDefaultObjects() throws Exception
	{
		CommandCreateObject createResultsChain = new CommandCreateObject(ResultsChainDiagramSchema.getObjectType());
		getProject().executeCommand(createResultsChain);
		
		String DEFAUL_RESULTS_CHAIN_LABEL = EAM.text("Placeholder - From ConPro");
		CommandSetObjectData setResultsChainLabel = new CommandSetObjectData(createResultsChain.getObjectRef(), ResultsChainDiagram.TAG_LABEL, DEFAUL_RESULTS_CHAIN_LABEL);
		getProject().executeCommand(setResultsChainLabel);
		
		final String OBJECTIVE_CONTAINER_LABEL = EAM.text("Objective Holder");
		final Point OBJECTIVE_CONTAINER_LOCATION = new Point(30, 30);
		ORef objectiveHolderDiagramFactoRef = createIntermediateResultAsContainer(OBJECTIVE_CONTAINER_LABEL, OBJECTIVE_CONTAINER_LOCATION);
		DiagramFactor objectiveHolderDiagramFactor = DiagramFactor.find(getProject(), objectiveHolderDiagramFactoRef);
		objectiveHolderRef = objectiveHolderDiagramFactor.getWrappedORef();
		
		final String INDICATOR_CONTAINER_LABEL = EAM.text("Indicator Holder");
		final Point INDICATOR_CONTAINER_LOCATION = new Point(30, 100);
		ORef indicatorHolderDiagramFactorRef = createIntermediateResultAsContainer(INDICATOR_CONTAINER_LABEL, INDICATOR_CONTAINER_LOCATION);
		DiagramFactor indicatorHolderDiagramFactor = DiagramFactor.find(getProject(), indicatorHolderDiagramFactorRef);
		indicatorHolderRef = indicatorHolderDiagramFactor.getWrappedORef();
		
		IdList idList = new IdList(DiagramFactorSchema.getObjectType());
		idList.addRef(objectiveHolderDiagramFactoRef);
		idList.addRef(indicatorHolderDiagramFactorRef);
		CommandSetObjectData addDiagramFactor = new CommandSetObjectData(createResultsChain.getObjectRef(), ResultsChainDiagram.TAG_DIAGRAM_FACTOR_IDS, idList.toString());
		getProject().executeCommand(addDiagramFactor);
	}

	private ORef createIntermediateResultAsContainer(String label, Point location)	throws CommandFailedException
	{
		CommandCreateObject createIntermediateResults = new CommandCreateObject(IntermediateResultSchema.getObjectType());
		getProject().executeCommand(createIntermediateResults);
		
		CommandSetObjectData setName = new CommandSetObjectData(createIntermediateResults.getObjectRef(), IntermediateResult.TAG_LABEL, label);
		getProject().executeCommand(setName);
			
		CommandCreateObject createDiagramFactor = new CommandCreateObject(DiagramFactorSchema.getObjectType());
		getProject().executeCommand(createDiagramFactor);
		
		final CommandSetObjectData setWrappedRefCommand = new CommandSetObjectData(createDiagramFactor.getObjectRef(), DiagramFactor.TAG_WRAPPED_REF, createIntermediateResults.getObjectRef().toString());
		getProject().executeCommand(setWrappedRefCommand);
		
		CommandSetObjectData setLocation = new CommandSetObjectData(createDiagramFactor.getObjectRef(), DiagramFactor.TAG_LOCATION, EnhancedJsonObject.convertFromPoint(location));
		getProject().executeCommand(setLocation);
		
		return createDiagramFactor.getObjectRef();
	}
	
	private void attachObjectivesToHolder() throws Exception
	{
		IdList objectiveIds = new IdList(ObjectiveSchema.getObjectType());
		NodeList objectiveNodeList = getNodes(getRootNode(), OBJECTIVES, OBJECTIVE);
		for (int nodeIndex = 0; nodeIndex < objectiveNodeList.getLength(); ++nodeIndex) 
		{
			Node objectiveNode = objectiveNodeList.item(nodeIndex);
			String objectiveId = getAttributeValue(objectiveNode, ID);
			objectiveIds.add(new BaseId(objectiveId));
		}
		
		CommandSetObjectData setIndicatorIds = new CommandSetObjectData(getObjectiveHolderRef(), IntermediateResult.TAG_OBJECTIVE_IDS, objectiveIds.toString());
		getProject().executeCommand(setIndicatorIds);	
	}
	
	private void attachIndicatorsToHolder() throws Exception
	{
		IdList indicatorIds = new IdList(IndicatorSchema.getObjectType());
		NodeList indicatorNodeList = getNodes(getRootNode(), INDICATORS, INDICATOR);
		for (int nodeIndex = 0; nodeIndex < indicatorNodeList.getLength(); ++nodeIndex) 
		{
			Node indicatorNode = indicatorNodeList.item(nodeIndex);
			String indicatorId = getAttributeValue(indicatorNode, ID);
			ORef indicatorRef = new ORef(IndicatorSchema.getObjectType(), new BaseId(indicatorId));
			Indicator indicator = Indicator.find(getProject(), indicatorRef);
			if (indicator.findAllObjectsThatReferToUs().size() > 1)
				throw new Exception("Indicator has more than one referrer: " + indicator.getRef());
			
			if (!indicator.hasReferrers())
				indicatorIds.addRef(indicatorRef);
		}
		
		CommandSetObjectData setIndicatorIds = new CommandSetObjectData(getIndicatorHolderRef(), IntermediateResult.TAG_INDICATOR_IDS, indicatorIds.toString());
		getProject().executeCommand(setIndicatorIds);
	}
	
	private void updateObjectiveRelevantStrategyList() throws Exception
	{
		NodeList strategyNodeList = getNodes(getRootNode(), STRATEGIES, STRATEGY);
		for (int nodeIndex = 0; nodeIndex < strategyNodeList.getLength(); ++nodeIndex) 
		{
			Node strategyNode = strategyNodeList.item(nodeIndex);
			String strategyIdAsString = getAttributeValue(strategyNode, ID);
			BaseId strategyId = new BaseId(strategyIdAsString);
		
			NodeList objectiveNodeList = getNodes(strategyNode, OBJECTIVES, OBJECTIVE_ID);
			updateObjectiveRelevancyList(new ORef(StrategySchema.getObjectType(), strategyId), objectiveNodeList);
		}
	}
	
	private void updateObjectiveRelevancyList(ORef strategyRef, NodeList objectiveNodeList) throws Exception
	{
		for (int nodeIndex = 0; nodeIndex < objectiveNodeList.getLength(); ++nodeIndex) 
		{
			Node objectiveNode = objectiveNodeList.item(nodeIndex);
			String objectiveId = objectiveNode.getTextContent();
			ORef objectiveRef = new ORef(ObjectiveSchema.getObjectType(), new BaseId(objectiveId));
			
			RelevancyOverrideSet relevancyOverrideSet = new RelevancyOverrideSet();
			Objective objective = Objective.find(getProject(), objectiveRef);
			relevancyOverrideSet.addAll(objective.getStrategyActivityRelevancyOverrideSet());
			relevancyOverrideSet.add(new RelevancyOverride(strategyRef, true));
			CommandSetObjectData addStrategy = new CommandSetObjectData(objectiveRef, Objective.TAG_RELEVANT_STRATEGY_ACTIVITY_SET, relevancyOverrideSet.toString());
			getProject().executeCommand(addStrategy);
		}
	}
	
	private void createTaggedThreatChains() throws Exception
	{
		highOrAboveRankedThreatsTag = createLabeledTag(EAM.text("High/Very-High Threat Chains"));
		ORef mediumOrBelowRankedThreatsTag = createLabeledTag(EAM.text("Low/Medium/Unrated Threat Chains"));
		
		CodeList highOrAboveRatingCodes = createHighAndAboveThreatRatingCodeList();
		addThreatChainsToTag(highOrAboveRatingCodes, highOrAboveRankedThreatsTag, mediumOrBelowRankedThreatsTag);
		addHighOrAboveTagToConceptualModel(new ORefList(highOrAboveRankedThreatsTag));
	}

	private void addThreatChainsToTag(CodeList highOrAboveCodes, ORef highOrAboveRankedThreatsTagToUse, ORef mediumOrBelowRankedThreatsTag) throws Exception
	{
		ORefSet mediumOrBelowRankedThreatRefs = new ORefSet();
		ORefSet highOrAboveRankedThreatRefs = new ORefSet();
		
		ChainWalker chainWalker = new ChainWalker();
		Cause[] threats = getProject().getCausePool().getDirectThreats();
		for (int index = 0; index < threats.length; ++index)
		{
			Cause threat = threats[index];
			ChoiceItem threatRatingChoice = getProject().getThreatRatingFramework().getThreatThreatRatingValue(threat.getRef());			
			ORefSet factorRefsInChain = chainWalker.buildNormalChainAndGetFactors(threat).getFactorRefs();
			if (threatRatingChoice != null && highOrAboveCodes.contains(threatRatingChoice.getCode()))
				highOrAboveRankedThreatRefs.addAll(factorRefsInChain);
			else
				mediumOrBelowRankedThreatRefs.addAll(factorRefsInChain);
		}
		
		ORefSet allLeftOverStrategyTargetRefs = getAllNonChainedStrategyAndTargets(mediumOrBelowRankedThreatRefs, highOrAboveRankedThreatRefs);
		highOrAboveRankedThreatRefs.addAll(allLeftOverStrategyTargetRefs);
		mediumOrBelowRankedThreatRefs.addAll(allLeftOverStrategyTargetRefs);
		
		tagFactors(highOrAboveRankedThreatsTagToUse, highOrAboveRankedThreatRefs);
		tagFactors(mediumOrBelowRankedThreatsTag, mediumOrBelowRankedThreatRefs);
	}

	private ORefSet getAllNonChainedStrategyAndTargets(ORefSet mediumOrBelowRankedThreatRefs, ORefSet highOrAboveRankedThreatRefs)
	{
		ORefList targetRefs = getProject().getTargetPool().getORefList();
		ORefList strategyRefs = getProject().getStrategyPool().getORefList();
		ORefSet allLeftOverStrategyTargetRefs = new ORefSet();
		allLeftOverStrategyTargetRefs.addAllRefs(targetRefs);
		allLeftOverStrategyTargetRefs.addAllRefs(strategyRefs);
		
		ORefSet allChainedRefs = new ORefSet();
		allChainedRefs.addAll(highOrAboveRankedThreatRefs);
		allChainedRefs.addAll(mediumOrBelowRankedThreatRefs);
		
		allLeftOverStrategyTargetRefs.removeAll(allChainedRefs);
		
		return allLeftOverStrategyTargetRefs;
	}

	private void tagFactors(ORef tagRef, ORefSet factorRefs) throws CommandFailedException
	{
		CommandSetObjectData tagFactors = new CommandSetObjectData(tagRef, TaggedObjectSet.TAG_TAGGED_OBJECT_REFS, factorRefs.toRefList().toString());
		getProject().executeCommand(tagFactors);
	}
	
	private void addHighOrAboveTagToConceptualModel(ORefList tagRefs) throws Exception
	{
		CommandSetObjectData addTags = new CommandSetObjectData(getFirstAndOnlyDiagramObjectRef(), ConceptualModelDiagram.TAG_SELECTED_TAGGED_OBJECT_SET_REFS, tagRefs.toString());
		getProject().executeCommand(addTags);
	}

	private CodeList createHighAndAboveThreatRatingCodeList()
	{
		return new CodeList(new String[]{ThreatRatingQuestion.HIGH_RATING_CODE, ThreatRatingQuestion.VERY_HIGH_RATING_CODE});
	}
	
	private ORef createLabeledTag(String tagLabel) throws CommandFailedException
	{
		CommandCreateObject createCommand = new CommandCreateObject(TaggedObjectSetSchema.getObjectType());
		getProject().executeCommand(createCommand);
		
		ORef tagRef = createCommand.getObjectRef();
		CommandSetObjectData setLabel = new CommandSetObjectData(tagRef, TaggedObjectSet.TAG_LABEL, tagLabel);
		getProject().executeCommand(setLabel);
		
		return tagRef;
	}
		
	private ORef getObjectiveHolderRef()
	{
		return objectiveHolderRef;
	}
	
	private ORef getIndicatorHolderRef()
	{
		return indicatorHolderRef;
	}
	
	public ORef getHighOrAboveRankedThreatsTag()
	{
		return highOrAboveRankedThreatsTag;
	}

	private Project project;
	private XPath xPath;
	private Document document;
	private ConProMiradiCodeMapHelper codeMapHelper;
	private HashMap<ORef, ORef> wrappedToDiagramMap;
	private ORef objectiveHolderRef;
	private ORef indicatorHolderRef;
	private ORef highOrAboveRankedThreatsTag;
	private ProgressInterface progressIndicator;
	
	public static final String PREFIX = "cp:";
	
	private static final int NON_DRAFT_STRATEGY_X_COLUMN = 30;
	private static final int DRAFT_STRATEGY_X_COLUMN = 270;
	private static final int CAUSE_X_COLUMN = 510;
	private static final int TARGET_X_COLUMN = 750;
}
