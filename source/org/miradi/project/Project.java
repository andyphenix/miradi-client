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
package org.miradi.project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.PrintStream;
import java.util.Vector;

import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.cells.DiagramGroupBoxCell;
import org.miradi.dialogs.planning.upperPanel.WorkPlanTreeTablePanel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.exceptions.UnexpectedNonSideEffectException;
import org.miradi.exceptions.UnexpectedSideEffectException;
import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramFactorId;
import org.miradi.ids.DiagramLinkId;
import org.miradi.ids.IdAssigner;
import org.miradi.ids.IdList;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.main.ResourcesHandler;
import org.miradi.main.VersionConstants;
import org.miradi.objecthelpers.CodeToChoiceMap;
import org.miradi.objecthelpers.CodeToCodeListMap;
import org.miradi.objecthelpers.DashboardStatusMapsCache;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.PlanningPreferencesChangeHandler;
import org.miradi.objecthelpers.ThreatStressRatingEnsurer;
import org.miradi.objecthelpers.TimePeriodCostsMapsCache;
import org.miradi.objectpools.ResourceAssignmentPool;
import org.miradi.objectpools.CausePool;
import org.miradi.objectpools.ConceptualModelDiagramPool;
import org.miradi.objectpools.DiagramFactorPool;
import org.miradi.objectpools.DiagramLinkPool;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objectpools.ExpenseAssignmentPool;
import org.miradi.objectpools.FactorLinkPool;
import org.miradi.objectpools.FundingSourcePool;
import org.miradi.objectpools.GoalPool;
import org.miradi.objectpools.GroupBoxPool;
import org.miradi.objectpools.HumanWelfareTargetPool;
import org.miradi.objectpools.IndicatorPool;
import org.miradi.objectpools.IntermediateResultPool;
import org.miradi.objectpools.KeyEcologicalAttributePool;
import org.miradi.objectpools.ObjectTreeTableConfigurationPool;
import org.miradi.objectpools.ObjectivePool;
import org.miradi.objectpools.RareProjectDataPool;
import org.miradi.objectpools.ResourcePool;
import org.miradi.objectpools.ResultsChainDiagramPool;
import org.miradi.objectpools.ScopeBoxPool;
import org.miradi.objectpools.StrategyPool;
import org.miradi.objectpools.StressPool;
import org.miradi.objectpools.TableSettingsPool;
import org.miradi.objectpools.TaggedObjectSetPool;
import org.miradi.objectpools.TargetPool;
import org.miradi.objectpools.TaskPool;
import org.miradi.objectpools.TextBoxPool;
import org.miradi.objectpools.ThreatReductionResultPool;
import org.miradi.objectpools.ThreatStressRatingPool;
import org.miradi.objectpools.ViewPool;
import org.miradi.objectpools.WcpaProjectDataPool;
import org.miradi.objectpools.WcsProjectDataPool;
import org.miradi.objectpools.WwfProjectDataPool;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.objects.FosProjectData;
import org.miradi.objects.ObjectTreeTableConfiguration;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.RareProjectData;
import org.miradi.objects.RatingCriterion;
import org.miradi.objects.ScopeBox;
import org.miradi.objects.TableSettings;
import org.miradi.objects.TaggedObjectSet;
import org.miradi.objects.TextBox;
import org.miradi.objects.ThreatRatingCommentsData;
import org.miradi.objects.ThreatStressRating;
import org.miradi.objects.TncProjectData;
import org.miradi.objects.ValueOption;
import org.miradi.objects.ViewData;
import org.miradi.objects.WcpaProjectData;
import org.miradi.objects.WcsProjectData;
import org.miradi.objects.WwfProjectData;
import org.miradi.project.threatrating.SimpleThreatFormula;
import org.miradi.project.threatrating.SimpleThreatRatingFramework;
import org.miradi.project.threatrating.StressBasedThreatFormula;
import org.miradi.project.threatrating.StressBasedThreatRatingFramework;
import org.miradi.project.threatrating.ThreatRatingFramework;
import org.miradi.questions.BudgetTimePeriodQuestion;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.DiagramModeQuestion;
import org.miradi.questions.FontFamiliyQuestion;
import org.miradi.questions.FontSizeQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.ThreatRatingModeChoiceQuestion;
import org.miradi.questions.WorkPlanColumnConfigurationQuestion;
import org.miradi.schemas.ConceptualModelDiagramSchema;
import org.miradi.schemas.DashboardSchema;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.utils.CodeList;
import org.miradi.utils.CommandVector;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.HtmlUtilities;
import org.miradi.utils.ProgressInterface;
import org.miradi.utils.Translation;
import org.miradi.views.diagram.DiagramClipboard;
import org.miradi.views.diagram.DiagramPageList;
import org.miradi.views.diagram.DiagramView;
import org.miradi.views.planning.PlanningView;
import org.miradi.views.planning.doers.CreatePlanningViewEmptyConfigurationDoer;
import org.miradi.views.workplan.WorkPlanView;


public class Project
{
	public Project() throws Exception
	{
		commandExecutor = new CommandExecutor(this);
		projectCalendar = new ProjectCalendar(this);
		projectTotalCalculator = new ProjectTotalCalculator(this);
		threatStressRatingEnsurer = new ThreatStressRatingEnsurer(this);
		planningPreferencesChangeHandler = new PlanningPreferencesChangeHandler(this);
		dashboardStatusMapsCache = new DashboardStatusMapsCache(this);
		timePeriodCostsMapsCache = new TimePeriodCostsMapsCache(this);
		enableIsDoNothingCommandOptimization();
		enableListeners();
		
		clear();
	}

	public void clear() throws Exception
	{
		isOpen = false;
		projectInfo = new ProjectInfo();
		objectManager = new ObjectManager(this);
		commandExecutor.clear();
		
		diagramClipboard = new DiagramClipboard(this);
		simpleThreatFramework = new SimpleThreatRatingFramework(this);
		stressBasedThreatFramework = new StressBasedThreatRatingFramework(this);
		
		currentViewName = NO_PROJECT_VIEW_NAME;

		
		projectCalendar.clearDateRanges();
		quarantine = new StringBuilder();
		exceptionLog = new StringBuilder();
		lastModifiedMillis = System.currentTimeMillis();
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// simple getters
	public long getLastModifiedTime()
	{
		return lastModifiedMillis;
	}

	public void setLastModified(long newlastModifiedMillis)
	{
		lastModifiedMillis = newlastModifiedMillis;
	}

	public IdAssigner getNormalIdAssigner()
	{
		return projectInfo.getNormalIdAssigner();
	}
	
	public ObjectManager getObjectManager()
	{
		return objectManager;
	}
	
	public EAMObjectPool getPool(int objectType)
	{
		return objectManager.getPool(objectType);
	}
	
	public ORefList getAllDiagramObjectRefs()
	{
		return objectManager.getAllDiagramObjectRefs();
	}

	public TaggedObjectSetPool getTaggedObjectSetPool()
	{
		return (TaggedObjectSetPool) getPool(TaggedObjectSet.getObjectType());
	}
		
	public WwfProjectDataPool getWwfProjectDataPool()
	{
		return (WwfProjectDataPool) getPool(WwfProjectData.getObjectType());
	}
	
	public RareProjectDataPool getRareProjectDataPool()
	{
		return (RareProjectDataPool) getPool(RareProjectData.getObjectType());
	}
	
	public WcsProjectDataPool getWcsProjectDataPool()
	{
		return (WcsProjectDataPool) getPool(WcsProjectData.getObjectType());
	}
	
	public WcpaProjectDataPool getWcpaProjectDataPool()
	{
		return (WcpaProjectDataPool) getPool(WcpaProjectData.getObjectType());
	}
	
	public ConceptualModelDiagramPool getConceptualModelDiagramPool()
	{
		return (ConceptualModelDiagramPool) getPool(ObjectType.CONCEPTUAL_MODEL_DIAGRAM);
	}
	
	public ResultsChainDiagramPool getResultsChainDiagramPool()
	{
		return (ResultsChainDiagramPool) getPool(ObjectType.RESULTS_CHAIN_DIAGRAM);
	}
	
	public TextBoxPool getTextBoxPool()
	{
		return (TextBoxPool) getPool(ObjectType.TEXT_BOX);
	}
	
	public ScopeBoxPool getScopeBoxPool()
	{
		return (ScopeBoxPool) getPool(ScopeBox.getObjectType());
	}
	
	public CausePool getCausePool()
	{
		return (CausePool) getPool(ObjectType.CAUSE);
	}
	
	public StressPool getStressPool()
	{
		return (StressPool) getPool(ObjectType.STRESS);
	}

	public IntermediateResultPool getIntermediateResultPool()
	{
		return (IntermediateResultPool) getPool(ObjectType.INTERMEDIATE_RESULT);
	}
	
	public ThreatReductionResultPool getThreatReductionResultPool()
	{
		 return (ThreatReductionResultPool) getPool(ObjectType.THREAT_REDUCTION_RESULT);
	}
	
	public ThreatStressRatingPool getThreatStressRatingPool()
	{
		return (ThreatStressRatingPool) getPool(ThreatStressRating.getObjectType());
	}
	
	public StrategyPool getStrategyPool()
	{
		return (StrategyPool) getPool(ObjectType.STRATEGY);
	}
	
	public TargetPool getTargetPool()
	{
		return (TargetPool) getPool(ObjectType.TARGET);
	}
	
	public HumanWelfareTargetPool getHumanWelfareTargetPool()
	{
		return (HumanWelfareTargetPool) getPool(ObjectType.HUMAN_WELFARE_TARGET);
	}
	
	public DiagramFactorPool getDiagramFactorPool()
	{
		return objectManager.getDiagramFactorPool();
	}
	
	public DiagramLinkPool getDiagramFactorLinkPool()
	{
		return objectManager.getDiagramFactorLinkPool();
	}
	
	public FactorLinkPool getFactorLinkPool()
	{
		return objectManager.getLinkagePool();
	}
	
	public TaskPool getTaskPool()
	{
		return objectManager.getTaskPool();
	}
	
	public KeyEcologicalAttributePool getKeyEcologicalAttributePool()
	{
		return objectManager.getKeyEcologicalAttributePool();
	}
	
	public ViewPool getViewPool()
	{
		return objectManager.getViewPool();
	}
	
	public ResourcePool getResourcePool()
	{
		return objectManager.getResourcePool();
	}
	
	public FundingSourcePool getFundingSourcePool()
	{
		return objectManager.getFundingSourcePool();
	}
	
	public IndicatorPool getIndicatorPool()
	{
		return objectManager.getIndicatorPool();
	}

	public ObjectivePool getObjectivePool()
	{
		return objectManager.getObjectivePool();
	}
	
	public GoalPool getGoalPool()
	{
		return objectManager.getGoalPool();
	}
	
	public ResourceAssignmentPool getAssignmentPool()
	{
		return objectManager.getAssignmentPool();
	}
	
	public ExpenseAssignmentPool getExpenseAssignmentPool()
	{
		return objectManager.getExpenseAssignmentPool();
	}
	
	public ObjectTreeTableConfigurationPool getPlanningViewConfigurationPool()
	{
		return objectManager.getPlanningConfigurationPool();
	}
	
	public GroupBoxPool getGroupBoxPool()
	{
		return (GroupBoxPool) getPool(ObjectType.GROUP_BOX);
	}
	
	public TableSettingsPool getTableSettingsPool()
	{
		return (TableSettingsPool) getPool(ObjectType.TABLE_SETTINGS);
	}
	
	public ThreatRatingCommentsData getSingletonThreatRatingCommentsData()
	{
		ORef threatRatingCommentsDataRef = getSingletonObjectRef(ThreatRatingCommentsData.getObjectType());
		return ThreatRatingCommentsData.find(this, threatRatingCommentsDataRef);
	}
	
	public ORef getSafeSingleObjectRef(int objectType)
	{
		if (getPool(objectType).size() == 1)
			return getSingletonObjectRef(objectType);
		
		return ORef.INVALID;
	}
	
	public ORef getSingletonObjectRef(int objectType)
	{
		EAMObjectPool pool = getPool(objectType);
		ORefList objectRefs = pool.getORefList();
		if (objectRefs.size() == 1)
			return objectRefs.get(0);
		
		throw new RuntimeException("Wrong object count (count = " + objectRefs.size() + ") in pool for type:" + objectType);
	}
	
	public String getCurrentView()
	{
		if(!isOpen())
			return NO_PROJECT_VIEW_NAME;
		
		return currentViewName;
	}
	
	public ViewData getDiagramViewData() throws Exception
	{
		return getViewData(DiagramView.getViewName());
	}
	
	public ViewData getWorkPlanViewData() throws Exception
	{
		return getViewData(WorkPlanView.getViewName());
	}
	
	public ViewData getCurrentViewData() throws Exception
	{
		return getViewData(getCurrentView());
	}
	
	public ViewData getViewData(String viewName) throws Exception
	{
		ViewData found = getViewPool().findByLabel(viewName);
		if(found != null)
			return found;
		
		BaseId createdId = createObjectAndReturnId(ObjectType.VIEW_DATA);
		setObjectData(ObjectType.VIEW_DATA, createdId, ViewData.TAG_LABEL, viewName);
		return getViewPool().find(createdId);
	}
	
	public boolean isChainMode()
	{
		try
		{
			String mode = getCurrentViewData().getData(ViewData.TAG_CURRENT_MODE);
			return (mode.equals(DiagramModeQuestion.MODE_STRATEGY_BRAINSTORM));
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
		
		return false;
	}

	public  boolean isNonChainMode()
	{
		return !isChainMode(); 
	}
	
	public SimpleThreatRatingFramework getSimpleThreatRatingFramework()
	{
		return simpleThreatFramework;
	}
	
	public StressBasedThreatRatingFramework getStressBasedThreatRatingFramework()
	{
		return stressBasedThreatFramework;
	}
	
	public ThreatRatingFramework getThreatRatingFramework()
	{
		if (isStressBaseMode())
			return  stressBasedThreatFramework;
		
		return simpleThreatFramework;
	}

	public boolean isSimpleThreatRatingMode()
	{
		return !isStressBaseMode();
	}
	
	public boolean isStressBaseMode()
	{
		return getMetadata().getThreatRatingMode().equals(ThreatRatingModeChoiceQuestion.STRESS_BASED_CODE);
	}
	
	public SimpleThreatFormula getSimpleThreatFormula()
	{
		return getSimpleThreatRatingFramework().getSimpleThreatFormula();
	}
	
	public StressBasedThreatFormula getStressBasedThreatFormula()
	{
		return stressBasedThreatFramework.getStressBasedThreatFormula();
	}
	
	public ProjectCalendar getProjectCalendar()
	{
		return projectCalendar;
	}
	
	public ProjectTotalCalculator getProjectTotalCalculator()
	{
		return projectTotalCalculator;
	}
	
	public CodeToChoiceMap getCachedDashboardEffectiveMap() throws Exception
	{
		return getDashboardStatusMapsCache().getEffectiveMap();
	}
	
	public DashboardStatusMapsCache getDashboardStatusMapsCache()
	{
		return dashboardStatusMapsCache;
	}
	
	public TimePeriodCostsMapsCache getTimePeriodCostsMapsCache()
	{
		return timePeriodCostsMapsCache;
	}

	public BaseObject findObject(ORef ref)
	{
		return objectManager.findObject(ref);
	}

	public BaseObject findObject(int objectType, BaseId objectId)
	{
		return findObject(new ORef(objectType, objectId));
	}
	
	public ProjectInfo getProjectInfo()
	{
		return projectInfo;
	}
	
	public ProjectMetadata getMetadata()
	{
		return (ProjectMetadata)findObject(ObjectType.PROJECT_METADATA, getMetadataId());
	}

	private BaseId getMetadataId()
	{
		return projectInfo.getMetadataId();
	}
	
	public ChoiceQuestion getQuestion(Class questionClass)
	{
		return StaticQuestionManager.getQuestion(questionClass);
	}

	public void appendToQuarantineFile(String textToAppend) throws Exception
	{
		quarantine.append(textToAppend);
	}
	
	public String getQuarantineFileContents() throws Exception
	{
		return quarantine.toString();
	}

	public void appendToExceptionLog(String textToAppend) throws Exception
	{
		exceptionLog.append(textToAppend);
	}
	
	public String getExceptionLog() throws Exception
	{
		return exceptionLog.toString();
	}

	/////////////////////////////////////////////////////////////////////////////////
	// objects
	
	public void setMetadata(String tag, String value) throws Exception
	{
		setObjectData(ObjectType.PROJECT_METADATA, getMetadataId(), tag, value);
	}
	
	public BaseId obtainRealLinkageId(BaseId proposedId)
	{
		return projectInfo.obtainFactorLinkId(proposedId);
	}
	
	public ORef createObject(int objectType) throws Exception
	{
		BaseId createdId = createObjectAndReturnId(objectType);
		return new ORef(objectType, createdId);
	}
	
	public ORef createObject(ORef ref) throws Exception
	{
		BaseId createdId = createObjectAndReturnId(ref.getObjectType(), ref.getObjectId());
		return new ORef(ref.getObjectType(), createdId);
	}
	
	public ORef createObject(int objectType, BaseId objectId) throws Exception
	{
		BaseId createdId = createObjectAndReturnId(objectType, objectId);
		return new ORef(objectType, createdId);
	}
	
	public BaseId createObjectAndReturnId(int objectType) throws Exception
	{
		return createObject(objectType, BaseId.INVALID).getObjectId();
	}
	
	public BaseId createObjectAndReturnId(int objectType, BaseId objectId) throws Exception
	{
		BaseId createdId = objectManager.createObject(objectType, objectId);
		return createdId;
	}
	
	public void deleteObject(BaseObject object) throws Exception
	{
		objectManager.deleteObject(object);
	}
	
	public void setObjectData(int objectType, BaseId objectId, String fieldTag, String dataValue) throws Exception
	{
		setObjectData(new ORef(objectType, objectId), fieldTag, dataValue);
	}
	
	public void setObjectData(BaseObject baseObject, String fieldTag, String dataValue) throws Exception
	{
		setObjectData(baseObject.getRef(), fieldTag, dataValue);
	}
	
	public void setObjectData(ORef objectRef, String fieldTag, String dataValue) throws Exception
	{
		objectManager.setObjectData(objectRef, fieldTag, dataValue);
	}
	
	public String getObjectData(int objectType, BaseId objectId, String fieldTag)
	{
		return objectManager.getObjectData(objectType, objectId, fieldTag);
	}
	
	public String getObjectData(ORef ref, String fieldTag)
	{
		return objectManager.getObjectData(ref.getObjectType(), ref.getObjectId(), fieldTag);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// database
	
	public void createOrOpenWithDefaultObjectsAndDiagramHelp(File projectFile, ProgressInterface progressMeter) throws Exception
	{
		boolean didProjectAlreadyExist = projectFile.exists();
		createOrOpenWithDefaultObjects(projectFile, progressMeter);
		
		if (!didProjectAlreadyExist)
			createDefaultHelpTextBoxDiagramFactor();
	}

	public void createOrOpenWithDefaultObjects(File projectFile, ProgressInterface progressMeter) throws Exception
	{
		rawCreateorOpen();
		createMissingDefaultObjects();
		applyDefaultBehavior();
		finishOpeningAfterLoad(projectFile);
	}

	public void finishOpeningAfterLoad(File projectFile) throws Exception
	{
		String openedProjectName = projectFile.getName().replaceAll(".Miradi$", "");
		finishOpeningAfterLoad(openedProjectName);
	}
	
	public void finishOpeningAfterLoad(String openedProjectName) throws Exception
	{
		createMissingBuiltInObjects();
		projectName = openedProjectName;
		isOpen = true;
	}

	public void rawCreateorOpen() throws Exception
	{
		currentViewName = SUMMARY_VIEW_NAME;
		writeStartingLogEntry();
	}

	private void writeStartingLogEntry() throws Exception
	{
		EAM.logVerbose("Project Opened by Miradi " + VersionConstants.getVersionAndTimestamp());
	}

	private void turnOnBudgetRelatedColumnsInWorkPlan() throws Exception
	{
		if (TableSettings.exists(this, WorkPlanTreeTablePanel.getTabSpecificModelIdentifier()))
			return;
		
		TableSettings newTableSettings = TableSettings.findOrCreate(this, WorkPlanTreeTablePanel.getTabSpecificModelIdentifier());
		CodeList budgetColumnCodes = new CodeList();
		budgetColumnCodes.add(WorkPlanColumnConfigurationQuestion.META_RESOURCE_ASSIGNMENT_COLUMN_CODE);
		budgetColumnCodes.add(WorkPlanColumnConfigurationQuestion.META_EXPENSE_ASSIGNMENT_COLUMN_CODE);
		budgetColumnCodes.add(WorkPlanColumnConfigurationQuestion.META_BUDGET_DETAIL_COLUMN_CODE);
		
		CodeToCodeListMap newTableSettingsMap = new CodeToCodeListMap();
		newTableSettingsMap.putCodeList(TableSettings.WORK_PLAN_BUDGET_COLUMNS_CODELIST_KEY, budgetColumnCodes);
		
		CommandSetObjectData setColumnCodes = new CommandSetObjectData(newTableSettings, TableSettings.TAG_TABLE_SETTINGS_MAP, newTableSettingsMap.toJsonString());
		executeWithoutRecording(setColumnCodes);
	}

	public void ensureAllDiagramFactorsAreVisible() throws Exception
	{
		ORefList allDiagramFactorRefs = getDiagramFactorPool().getORefList();
		for (int i = 0; i < allDiagramFactorRefs.size(); ++i)
		{
			DiagramFactor diagramFactor = DiagramFactor.find(this, allDiagramFactorRefs.get(i));
			Point location = (Point) diagramFactor.getLocation().clone();
			if (!diagramFactor.isGroupBoxFactor() && isOffScreenLocation(location))
			{
				CommandSetObjectData moveToOnScreen = new CommandSetObjectData(diagramFactor.getRef(), DiagramFactor.TAG_LOCATION, EnhancedJsonObject.convertFromPoint(new Point(0, 0)));
				executeWithoutRecording(moveToOnScreen);
			}	
		}
	}
	
	private boolean isOffScreenLocation(Point location)
	{
		if (location.x < 0)
			return true;
		
		if (location.y < 0)
			return true;
		
		return false;
	}

	private void createDefaultPlanningCustomization() throws Exception
	{
		if (getPool(ObjectTreeTableConfiguration.getObjectType()).getORefList().size() > 0)
			return;

		ViewData planningViewData = getViewData(PlanningView.getViewName());
		createDefaultConfigurationObject(planningViewData, ViewData.TAG_TREE_CONFIGURATION_REF);
	}

	private void createDefaultConfigurationObject(BaseObject baseObject, String configurationTag) throws Exception
	{
		ORef configurationRef = ORef.createFromString(baseObject.getData(configurationTag));
		if (configurationRef.isInvalid())
		{
			ORef createPlanningConfiguration = createObject(ObjectTreeTableConfiguration.getObjectType());
			setObjectData(createPlanningConfiguration, ObjectTreeTableConfiguration.TAG_LABEL, CreatePlanningViewEmptyConfigurationDoer.getConfigurationDefaultLabel(this));
			setObjectData(baseObject.getRef(), configurationTag, createPlanningConfiguration.toString());
		}
	}
	
	private void selectDefaultPlanningCustomization() throws Exception
	{
		ORef currentCustomizationRef = getViewData(PlanningView.getViewName()).getORef(ViewData.TAG_TREE_CONFIGURATION_REF);
		if (! currentCustomizationRef.isInvalid())
			return;
		
		ORefList customizationRefs = getPlanningViewConfigurationPool().getORefList();
		if (customizationRefs.isEmpty())
			return;
		
		ViewData planningViewData = getViewData(PlanningView.getViewName());
		setObjectData(planningViewData.getRef(), ViewData.TAG_TREE_CONFIGURATION_REF, customizationRefs.getFirstElement().toString());
	}

	private void createDefaultConceptualModel() throws Exception
	{
		if (getConceptualModelDiagramPool().getORefList().size() > 0)
			return;
		
		createObject(ObjectType.CONCEPTUAL_MODEL_DIAGRAM);
	}

	public void createDefaultHelpTextBoxDiagramFactor() throws Exception
	{
		if (getConceptualModelDiagramPool().getORefList().size() != 1)
			return;
		
		ORef mainDiagramRef = getConceptualModelDiagramPool().getORefList().getRefForType(ConceptualModelDiagramSchema.getObjectType());
		ConceptualModelDiagram mainDiagram = (ConceptualModelDiagram) findObject(mainDiagramRef);
		ORefList diagramFactorRefs = mainDiagram.getAllDiagramFactorRefs();
		if (diagramFactorRefs.size() != 0)
			return;
		
		final String diagramInitialHelpTextFileName = "DiagramInitialHelpText.html";
		String helpText = ResourcesHandler.loadResourceFile(diagramInitialHelpTextFileName);
		int labelLineCount = DiagramGroupBoxCell.getLabelLineCount(helpText);
		final int NUMBER_OF_PIXELS_PER_LINE = 20;
		int height = labelLineCount * NUMBER_OF_PIXELS_PER_LINE; 
		
		ORef textBoxRef = createObject(TextBox.getObjectType());
		ORef diagramFactorRef = createObject(DiagramFactorSchema.getObjectType());
		setObjectData(diagramFactorRef, DiagramFactor.TAG_WRAPPED_REF, textBoxRef.toString());
		
		final int WIDTH = 300;
		setObjectData(diagramFactorRef, DiagramFactor.TAG_SIZE, EnhancedJsonObject.convertFromDimension(new Dimension(WIDTH, height)));
		
		final int X_LOCATION = 105;
		final int Y_LOCATION = 105;
		setObjectData(diagramFactorRef, DiagramFactor.TAG_LOCATION, EnhancedJsonObject.convertFromPoint(new Point(X_LOCATION, Y_LOCATION)));
		String htmlContent = Translation.getHtmlContent(diagramInitialHelpTextFileName);
		htmlContent = HtmlUtilities.removeNonHtmlNewLines(htmlContent);
		setObjectData(textBoxRef, TextBox.TAG_LABEL, htmlContent);
		
		IdList diagramFactorIdList = new IdList(DiagramFactorSchema.getObjectType());
		diagramFactorIdList.add(diagramFactorRef.getObjectId());
		setObjectData(mainDiagramRef, DiagramObject.TAG_DIAGRAM_FACTOR_IDS, diagramFactorIdList.toString());
	}
	
	private void createProjectMetadata() throws Exception
	{
		BaseId createdId = createObjectAndReturnId(ObjectType.PROJECT_METADATA);
		projectInfo.setMetadataId(createdId);
		setObjectData(getMetadata().getRef(), ProjectMetadata.TAG_CURRENCY_SYMBOL, "$");
		setObjectData(getMetadata().getRef(), ProjectMetadata.TAG_WORKPLAN_TIME_UNIT, BudgetTimePeriodQuestion.BUDGET_BY_YEAR_CODE);
		setObjectData(getMetadata().getRef(), ProjectMetadata.TAG_DIAGRAM_FONT_FAMILY, FontFamiliyQuestion.ARIAL_CODE);
		setObjectData(getMetadata().getRef(), ProjectMetadata.TAG_DIAGRAM_FONT_SIZE, FontSizeQuestion.getDefaultSizeCode());
	}
	
	private void createDefaultProjectDataObject(int objectType) throws Exception
	{
		EAMObjectPool pool = getPool(objectType);
		if (pool.getORefList().size() > 0)
			return;
		
		createObject(objectType);
	}
	
	private void ensureAllConceptualModelPagesHaveLabels() throws Exception
	{
		ORefList diagramPageRefs = getConceptualModelDiagramPool().getORefList();
		String defaultDiagramPageLabel = getDefaultDiagramPageName(diagramPageRefs);		
		for (int i = 0; i < diagramPageRefs.size(); ++i)
		{
			ORef diagramPageRef = diagramPageRefs.get(i);
			ConceptualModelDiagram diagramPage = (ConceptualModelDiagram) findObject(diagramPageRef);
			if (diagramPage.toString().length() != 0)
				continue;
			
			setObjectData(diagramPageRef, ConceptualModelDiagram.TAG_LABEL, defaultDiagramPageLabel);
		}
	}

	private String getDefaultDiagramPageName(ORefList diagramPageRefs)
	{
		if (diagramPageRefs.size() > 1)
			return ConceptualModelDiagram.DEFAULT_BLANK_NAME;
		
		return ConceptualModelDiagram.DEFAULT_MAIN_NAME;
	}
	
	protected void enableListeners()
	{
		getProjectCalendar().enable();
		getProjectTotalCalculator().enable();
		enableThreatStressRatingEnsurer();
		planningPreferencesChangeHandler.enable();
		dashboardStatusMapsCache.enable();
		timePeriodCostsMapsCache.enable();
	}

	protected void applyDefaultBehavior() throws Exception
	{
		selectDefaultPlanningCustomization();
		turnOnBudgetRelatedColumnsInWorkPlan();
		
		ensureAllConceptualModelPagesHaveLabels();
		ensureAllDiagramFactorsAreVisible();
		setDefaultDiagramPage(ObjectType.CONCEPTUAL_MODEL_DIAGRAM);
		setDefaultDiagramPage(ObjectType.RESULTS_CHAIN_DIAGRAM);
	}

	protected void createMissingDefaultObjects() throws Exception
	{
		createDefaultConceptualModel();
		createDefaultPlanningCustomization();
	}

	private void createMissingBuiltInObjects() throws Exception
	{
		if(getMetadataId().isInvalid())
			createProjectMetadata();
		
		createMissingSimpleThreatRatingCriteria();
		createMissingSimpleThreatRatingValueOptions();

		createDefaultProjectDataObject(WwfProjectData.getObjectType());
		createDefaultProjectDataObject(RareProjectData.getObjectType());
		createDefaultProjectDataObject(WcsProjectData.getObjectType());
		createDefaultProjectDataObject(TncProjectData.getObjectType());
		createDefaultProjectDataObject(FosProjectData.getObjectType());
		createDefaultProjectDataObject(WcpaProjectData.getObjectType());
		createDefaultProjectDataObject(ThreatRatingCommentsData.getObjectType());
		createDefaultProjectDataObject(DashboardSchema.getObjectType());
	}

	private void createMissingSimpleThreatRatingCriteria() throws Exception
	{
		if(getPool(RatingCriterion.getObjectType()).size() > 0)
			return;
		
		createDefaultRatingCriterion("Scope"); 
		createDefaultRatingCriterion("Severity");
		createDefaultRatingCriterion("Irreversibility");
	}

	private void createDefaultRatingCriterion(String label) throws Exception
	{
		ORef createdRef = createObject(RatingCriterion.getObjectType());
		CommandSetObjectData setLabel = new CommandSetObjectData(createdRef, RatingCriterion.TAG_LABEL, label);
		executeWithoutRecording(setLabel);		
	}

	private void createMissingSimpleThreatRatingValueOptions() throws Exception
	{
		if(getPool(ValueOption.getObjectType()).size() > 0)
			return;
		
		createDefaultValueOption("None", SimpleThreatRatingFramework.NONE_VALUE, Color.WHITE);
		createDefaultValueOption("Very High", SimpleThreatRatingFramework.VERY_HIGH_RATING_VALUE, Color.RED);
		createDefaultValueOption("High", SimpleThreatRatingFramework.HIGH_RATING_VALUE, Color.ORANGE);
		createDefaultValueOption("Medium", SimpleThreatRatingFramework.MEDIUM_RATING_VALUE, Color.YELLOW);
		createDefaultValueOption("Low", SimpleThreatRatingFramework.LOW_RATING_VALUE, Color.GREEN);
	}

	private void createDefaultValueOption(String label, int numericValue, Color color) throws Exception
	{
		ORef createdRef = createObject(ValueOption.getObjectType());
		CommandSetObjectData setLabel = new CommandSetObjectData(createdRef, ValueOption.TAG_LABEL, label);
		executeWithoutRecording(setLabel);
		CommandSetObjectData setNumeric = new CommandSetObjectData(createdRef, ValueOption.TAG_NUMERIC, Integer.toString(numericValue));
		executeWithoutRecording(setNumeric);
		CommandSetObjectData setColor= new CommandSetObjectData(createdRef, ValueOption.TAG_COLOR, Integer.toString(color.getRGB()));
		executeWithoutRecording(setColor);
	}

	private void setDefaultDiagramPage(int objectType) throws Exception
	{
		ViewData viewData = getDiagramViewData();
		ORef currentDiagramObjectRef = DiagramPageList.getCurrentDiagramViewDataRef(viewData, objectType);
		if (currentDiagramObjectRef.isValid())
			return;

		EAMObjectPool pool = getPool(objectType);
		if (pool.size() == 0)
			return;
	
		ORef firstPoolItemRef = pool.getORefList().get(0);
		String currentDiagramViewDataTag = DiagramPageList.getCurrentDiagramViewDataTag(objectType);
		CommandSetObjectData setCurrentDiagramObject = new CommandSetObjectData(viewData.getRef(), currentDiagramViewDataTag, firstPoolItemRef);
		executeWithoutRecording(setCurrentDiagramObject);		
	}
	
	public String getFilename()
	{
		if(isOpen())
			return projectName;
		return EAM.text("[No Project]");
	}

	public boolean isOpen()
	{
		return isOpen;
	}
	
	public void close() throws Exception
	{
		clear();
	}
	
	public void dispose()
	{
		disableListeners();	
	}

	protected void disableListeners()
	{
		getProjectCalendar().disable();
		getProjectTotalCalculator().disable();
		disableThreatStressRatingEnsurer();
		planningPreferencesChangeHandler.disable();
		dashboardStatusMapsCache.disable();
		timePeriodCostsMapsCache.disable();
	}

	public void disableThreatStressRatingEnsurer()
	{
		threatStressRatingEnsurer.disable();
	}
	
	public void enableThreatStressRatingEnsurer()
	{
		threatStressRatingEnsurer.enable();
	}
	
	static public boolean isValidProjectFilename(String candidate)
	{
		return candidate.equals(makeProjectFilenameLegal(candidate));
	}
	
	static public String makeProjectFilenameLegal(String candidate)
	{
		if(candidate.length() < 1)
			return Character.toString(EAM.DASH);
		
		if(candidate.length() > MAX_PROJECT_FILENAME_LENGTH)
			candidate = candidate.substring(0, MAX_PROJECT_FILENAME_LENGTH);
		
		char[] asArray = candidate.toCharArray();
		for(int i = 0; i < candidate.length(); ++i)
		{
			char c = asArray[i];
			if (EAM.isValidProjectNameCharacter(c))
				continue;
			
			asArray[i] = EAM.DASH;
		}

		return new String(asArray);
	}
	
	public int getProjectSummaryThreatRating()
	{
		if (isStressBaseMode())
			return getStressBasedThreatRatingFramework().getOverallProjectRating();
			
		return getSimpleThreatRatingFramework().getOverallProjectRating().getNumericValue();
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// command execution

	public void executeCommand(Command command) throws UnexpectedNonSideEffectException, CommandFailedException
	{
		if(command.isBeginTransaction() || command.isEndTransaction())
			getCommandExecutor().executeSingleCommand(command);
		else
			getCommandExecutor().executeCommand(command);
	}

	public boolean isDoNothingCommand(Command command)	throws CommandFailedException
	{
		return getCommandExecutor().isDoNothingCommand(command);
	}

	public void executeEndTransaction() throws CommandFailedException
	{
		getCommandExecutor().executeEndTransaction();
	}
	
	public void executeBeginTransaction() throws CommandFailedException
	{
		getCommandExecutor().executeBeginTransaction();
	}
	
	public void executeCommands(CommandVector commands) throws CommandFailedException
	{
		getCommandExecutor().executeCommands(commands);
	}
	
	public void executeCommands(Command[] commands) throws CommandFailedException
	{
		getCommandExecutor().executeCommands(commands);
	}
	
	public void undo() throws CommandFailedException, RuntimeException
	{
		getCommandExecutor().undo();
	}
	
	public void redo() throws CommandFailedException, RuntimeException
	{
		getCommandExecutor().redo();
	}

	private void executeWithoutRecording(Command command) throws CommandFailedException
	{
		getCommandExecutor().executeWithoutRecording(command);
	}
	
	public void executeAsSideEffect(CommandVector commands) throws CommandFailedException
	{
		getCommandExecutor().executeAsSideEffect(commands);
	}
	
	public void executeAsSideEffect(Command command) throws UnexpectedSideEffectException, CommandFailedException
	{
		getCommandExecutor().executeAsSideEffect(command);	
	}
	
	public void recordCommand(Command command)
	{
		getCommandExecutor().recordCommand(command);
	}

	public Command getLastExecutedCommand()
	{
		return getCommandExecutor().getLastExecutedCommand();
	}
	
	public boolean isExecutingACommand()
	{
		return getCommandExecutor().isExecutingACommand();
	}
	
	public void addCommandExecutedListener(CommandExecutedListener listener)
	{
		getCommandExecutor().addCommandExecutedListener(listener);
	}
	
	public void removeCommandExecutedListener(CommandExecutedListener listener)
	{
		getCommandExecutor().removeCommandExecutedListener(listener);
	}

	public int getCommandListenerCount()
	{
		return getCommandExecutor().getCommandListenerCount();
	}
	
	public void logCommandListeners(PrintStream out)
	{
		getCommandExecutor().logCommandListeners(out);
	}
	
	public void logDebugCommandListeners()
	{
		getCommandExecutor().logDebugCommandListeners();
	}

	public boolean canUndo()
	{
		return getCommandExecutor().canUndo();
	}
	
	public boolean canRedo()
	{
		return getCommandExecutor().canRedo();
	}
	
	public void internalBeginTransaction() throws CommandFailedException
	{
		getCommandExecutor().beginTransaction();
	}
	
	public void internalEndTransaction() throws CommandFailedException
	{
		getCommandExecutor().endTransaction();
	}
	
	public boolean isInTransaction()
	{
		return getCommandExecutor().isInTransaction();
	}
	
	public void beginCommandSideEffectMode()
	{
		getCommandExecutor().beginCommandSideEffectMode();
	}

	public void endCommandSideEffectMode()
	{
		getCommandExecutor().endCommandSideEffectMode();
	}
	
	public void enableIsDoNothingCommandOptimization()
	{
		getCommandExecutor().enableIsDoNothingCommandOptimization();
	}
	
	public void disableIsDoNothingCommandOptimization()
	{
		getCommandExecutor().disableIsDoNothingCommandOptimization();
	}
	
	public boolean isDoNothingCommandEnabledOptimization()
	{
		return getCommandExecutor().isDoNothingCommandEnabledOptimization();
	}

	public boolean isInCommandSideEffectMode()
	{
		return getCommandExecutor().isInCommandSideEffectMode();
	}
	
	public CommandExecutor getCommandExecutor()
	{
		return commandExecutor;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// views
	
	public void switchToView(String viewName) throws CommandFailedException
	{
		currentViewName = viewName;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// diagram view

	public ORefList findConceptualModelThatContainsBothFactors(ORef fromFactorRef, ORef toFactorRef)
	{
		if(!Factor.isFactor(fromFactorRef))
			throw new RuntimeException("Non-factor passed for from: " + fromFactorRef);
		if(!Factor.isFactor(toFactorRef))
			throw new RuntimeException("Non-factor passed for to: " + toFactorRef);

		ORefList conceptualModels = new ORefList();
		ConceptualModelDiagramPool diagramPool = getConceptualModelDiagramPool();
		ORefList diagramORefs = diagramPool.getORefList();
		for (int i = 0; i < diagramORefs.size(); ++i)
		{
			ORef thisDiagramRef = diagramORefs.get(i);
			ConceptualModelDiagram diagram =  (ConceptualModelDiagram) findObject(thisDiagramRef);
			if (diagram.containsWrappedFactorRef(fromFactorRef) && diagram.containsWrappedFactorRef(toFactorRef))
				conceptualModels.add(thisDiagramRef); 		
		}
		
		return conceptualModels;
	}
	
	public DiagramLink[] getToAndFromLinks(DiagramFactorId diagramFactorId)
	{
		DiagramLinkId[] allLinkIds = getDiagramFactorLinkPool().getallDiagramFactorLinkIds();
		Vector<DiagramLink> fromAndToLinksForFactor = new Vector<DiagramLink>();
		for (int i = 0; i < allLinkIds.length; i++)
		{
			DiagramLink link = (DiagramLink) findObject(new ORef(ObjectType.DIAGRAM_LINK, allLinkIds[i]));
			if ((link.getFromDiagramFactorId().equals(diagramFactorId) || (link.getToDiagramFactorId().equals(diagramFactorId))))
				fromAndToLinksForFactor.add(link);
		}
		
		return fromAndToLinksForFactor.toArray(new DiagramLink[0]);
	}
	
	public DiagramFactorId[] getAllDiagramFactorIds()
	{
		return getDiagramFactorPool().getDiagramFactorIds();
	}
	
	public DiagramFactor[] getAllDiagramFactors()
	{
		DiagramFactorId[] diagramFactorIds = getAllDiagramFactorIds();
		DiagramFactor[] diagramFactors = new DiagramFactor[diagramFactorIds.length];
		
		for (int i = 0; i < diagramFactorIds.length; i++)
		{
			diagramFactors[i] = (DiagramFactor) findObject(new ORef(ObjectType.DIAGRAM_FACTOR, diagramFactorIds[i]));
		}
		
		return diagramFactors;
	}

	public boolean areDiagramFactorsLinked(ORef fromRef, ORef toRef) throws Exception
	{
		DiagramLink diagramLink = getDiagramFactorLinkPool().getDiagramLink(fromRef, toRef);
		return diagramLink != null;
	}
	
	public boolean areLinked(ORef factorRef1, ORef factorRef2)
	{
		Factor factor1 = (Factor)findObject(factorRef1);
		Factor factor2 = (Factor)findObject(factorRef2);
		return areLinked(factor1, factor2);
	}

	public boolean areLinked(Factor factor1, Factor factor2)
	{
		return getFactorLinkPool().areLinked(factor1, factor2);
	}
	
	public int forceNonZeroEvenSnap(int value)
	{
		int gridSize = getGridSize();
		int newValue = (value + gridSize) - (value + gridSize) % (gridSize * 2);
		
		if (newValue != 0)
			return newValue;
		
		return gridSize * 2;
	}
		
	public int getGridSize()
	{
		return DEFAULT_GRID_SIZE;
	}
	
	public Point getSnapped(int x, int y)
	{
		return getSnapped(new Point(x, y));
	}
	
	public Dimension getSnapped(Dimension dimension)
	{
		int gridSize = getGridSize();
		return new Dimension(roundTo(dimension.width, gridSize), roundTo(dimension.height, gridSize));
	}
	
	public Point getSnapped(Point point)
	{
		int gridSize = getGridSize();
		return new Point(roundTo(point.x, gridSize), roundTo(point.y, gridSize));
	}
	
	int roundTo(int valueToRound, int incrementToRoundTo)
	{
		int sign = 1;
		if(valueToRound < 0)
			sign = -1;
		valueToRound = Math.abs(valueToRound);
		
		int half = incrementToRoundTo / 2;
		valueToRound += half;
		valueToRound -= (valueToRound % incrementToRoundTo);
		return valueToRound * sign;
	}
	
	public ProjectResource[] getAllProjectResources()
	{
		IdList allResourceIds = getResourcePool().getIdList();
		return getResources(allResourceIds);
	}

	public ProjectResource[] getResources(IdList resourceIds)
	{
		ProjectResource[] availableResources = new ProjectResource[resourceIds.size()];
		for(int i = 0; i < availableResources.length; ++i)
			availableResources[i] = getResourcePool().find(resourceIds.get(i));
		return availableResources;
	}
	
	public DiagramClipboard getDiagramClipboard()
	{
		return diagramClipboard;
	}

	public CurrencyFormat getCurrencyFormatterWithCommas()
	{
		int currencyDecimalPlaces = getMetadata().getCurrencyDecimalPlaces();
		CurrencyFormat currencyFormatter = new CurrencyFormat();
		currencyFormatter.setMinimumFractionDigits(currencyDecimalPlaces);
		currencyFormatter.setMaximumFractionDigits(currencyDecimalPlaces);
		
		return currencyFormatter;
	}
	
	public CurrencyFormat getCurrencyFormatterWithoutCommas()
	{
		CurrencyFormat currencyFormatter = getCurrencyFormatterWithCommas();
		currencyFormatter.setGroupingUsed(false);
		
		return currencyFormatter;
	}
	
	public int getDiagramFontSize()
	{
		int size = getMetadata().getDiagramFontSize();
		if(size == 0)
			return DEFAULT_DIAGRAM_FONT_SIZE;
		
		return size;

	}

	public void setLocalDataLocation(File parentFile)
	{
		throw new RuntimeException("setLocalDataLocation not yet supported!");
	}

	public static final String LIBRARY_VIEW_NAME = "Library";
	public static final String SCHEDULE_VIEW_NAME = "Schedule";
	public static final String MAP_VIEW_NAME = "Map";
	public static final String THREAT_MATRIX_VIEW_NAME = "ThreatMatrix";
	public static final String NO_PROJECT_VIEW_NAME = "NoProject";
	public static final String DIAGRAM_VIEW_NAME = "Diagram";
	public static final String SUMMARY_VIEW_NAME = "Summary";
	public static final String TARGET_VIABILITY_NAME = "Target Viability";
	public static final String PLANNING_VIEW_NAME = "Planning";
	public static final String WORK_PLAN_VIEW = "WorkPlan";
	public static final String REPORT_VIEW_NAME = "Reports";
	
	public static final String DEFAULT_VIEW_NAME = SUMMARY_VIEW_NAME;
	
	public static final int DEFAULT_GRID_SIZE = 15;
	public static final int DEFAULT_DIAGRAM_FONT_SIZE = 11;
	

	public static final int MAX_PROJECT_FILENAME_LENGTH = 32;
	
	private boolean isOpen;
	private String projectName;
	private ProjectInfo projectInfo;
	private ObjectManager objectManager;
	private long lastModifiedMillis;

	private SimpleThreatRatingFramework simpleThreatFramework;
	private StressBasedThreatRatingFramework stressBasedThreatFramework;
	
	private StringBuilder quarantine;
	private StringBuilder exceptionLog;
	
	private DiagramClipboard diagramClipboard;
	private ProjectCalendar projectCalendar;
	private ThreatStressRatingEnsurer threatStressRatingEnsurer;
	private ProjectTotalCalculator projectTotalCalculator;
	private PlanningPreferencesChangeHandler planningPreferencesChangeHandler;
	private DashboardStatusMapsCache dashboardStatusMapsCache;
	private TimePeriodCostsMapsCache timePeriodCostsMapsCache;

	// FIXME low: This should go away, but it's difficult
	private String currentViewName;
	
	public CommandExecutor commandExecutor;

}
