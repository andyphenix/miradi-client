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
package org.miradi.project;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Vector;

import org.miradi.commands.Command;
import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.database.DataUpgrader;
import org.miradi.database.FileBasedProjectServer;
import org.miradi.database.ProjectServer;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.exceptions.FutureVersionException;
import org.miradi.exceptions.OldVersionException;
import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramFactorId;
import org.miradi.ids.DiagramFactorLinkId;
import org.miradi.ids.FactorId;
import org.miradi.ids.FactorLinkId;
import org.miradi.ids.IdAssigner;
import org.miradi.ids.IdList;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.main.VersionConstants;
import org.miradi.objecthelpers.CreateDiagramFactorParameter;
import org.miradi.objecthelpers.CreateObjectParameter;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.AssignmentPool;
import org.miradi.objectpools.CausePool;
import org.miradi.objectpools.ConceptualModelDiagramPool;
import org.miradi.objectpools.DiagramFactorLinkPool;
import org.miradi.objectpools.DiagramFactorPool;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objectpools.FactorLinkPool;
import org.miradi.objectpools.GoalPool;
import org.miradi.objectpools.GroupBoxPool;
import org.miradi.objectpools.IndicatorPool;
import org.miradi.objectpools.IntermediateResultPool;
import org.miradi.objectpools.KeyEcologicalAttributePool;
import org.miradi.objectpools.ObjectivePool;
import org.miradi.objectpools.PlanningViewConfigurationPool;
import org.miradi.objectpools.RareProjectDataPool;
import org.miradi.objectpools.ResourcePool;
import org.miradi.objectpools.ResultsChainDiagramPool;
import org.miradi.objectpools.StrategyPool;
import org.miradi.objectpools.TargetPool;
import org.miradi.objectpools.TaskPool;
import org.miradi.objectpools.TextBoxPool;
import org.miradi.objectpools.ThreatReductionResultPool;
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
import org.miradi.objects.PlanningViewConfiguration;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.RareProjectData;
import org.miradi.objects.TextBox;
import org.miradi.objects.TncProjectData;
import org.miradi.objects.ViewData;
import org.miradi.objects.WcpaProjectData;
import org.miradi.objects.WcsProjectData;
import org.miradi.objects.WwfProjectData;
import org.miradi.questions.BudgetTimePeriodQuestion;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.QuestionManager;
import org.miradi.questions.ThreatRatingModeChoiceQuestion;
import org.miradi.resources.ResourcesHandler;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.views.diagram.DiagramClipboard;
import org.miradi.views.diagram.DiagramPageList;
import org.miradi.views.diagram.DiagramView;
import org.miradi.views.diagram.LayerManager;
import org.miradi.views.planning.PlanningView;
import org.miradi.views.planning.doers.CreatePlanningViewConfigurationDoer;
import org.miradi.views.summary.SummaryView;


public class Project
{
	public Project() throws Exception
	{
		this(new FileBasedProjectServer());
	}
	
	public Project(ProjectServer databaseToUse) throws Exception
	{
		database = databaseToUse;
		commandExecutedListeners = new Vector();
		projectCalendar = new ProjectCalendar(this);

		clear();
	}

	protected void clear() throws Exception
	{
		projectInfo = new ProjectInfo();
		objectManager = new ObjectManager(this);
		undoRedoState = new UndoRedoState();
		
		diagramClipboard = new DiagramClipboard(this);
		layerManager = new LayerManager();
		simpleThreatFramework = new SimpleThreatRatingFramework(this);
		stressBasedThreatFramework = new StressBasedThreatRatingFramework(this);
		
		currentViewName = SummaryView.getViewName();
		
		projectCalendar.clearDateRanges();
	}
	
	static public void validateNewProject(String newName) throws Exception
	{
		File newFile = new File(EAM.getHomeDirectory(),newName);
		if(ProjectServer.isExistingProject(newFile))
			throw new Exception(EAM.text(" A project by this name already exists: ") + newName);
		
		if (!EAM.getMainWindow().getProject().isValidProjectFilename(newName))
			throw new Exception(EAM.text("Invalid project name:") + newName);
		
		if(newFile.exists())
			throw new Exception(EAM.text("A file or directory exist by the same name:") + newName);
		
	}
	/////////////////////////////////////////////////////////////////////////////////
	// simple getters
	
	public IdAssigner getNodeIdAssigner()
	{
		return projectInfo.getFactorAndLinkIdAssigner();
	}
	
	public IdAssigner getAnnotationIdAssigner()
	{
		return projectInfo.getNormalIdAssigner();
	}
	
	public ProjectServer getDatabase()
	{
		return database;
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
	
	public CausePool getCausePool()
	{
		return (CausePool) getPool(ObjectType.CAUSE);
	}
	
	public IntermediateResultPool getIntermediateResultPool()
	{
		return (IntermediateResultPool) getPool(ObjectType.INTERMEDIATE_RESULT);
	}
	
	public ThreatReductionResultPool getThreatReductionResultPool()
	{
		 return (ThreatReductionResultPool) getPool(ObjectType.THREAT_REDUCTION_RESULT);
	}
	
	public StrategyPool getStrategyPool()
	{
		return (StrategyPool) getPool(ObjectType.STRATEGY);
	}
	
	public TargetPool getTargetPool()
	{
		return (TargetPool) getPool(ObjectType.TARGET);
	}
	
	public DiagramFactorPool getDiagramFactorPool()
	{
		return objectManager.getDiagramFactorPool();
	}
	
	public DiagramFactorLinkPool getDiagramFactorLinkPool()
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
	
	public AssignmentPool getAssignmentPool()
	{
		return objectManager.getAssignmentPool();
	}
	
	public PlanningViewConfigurationPool getPlanningViewConfigurationPool()
	{
		return objectManager.getPlanningConfigurationPool();
	}
	
	public GroupBoxPool getGroupBoxPool()
	{
		return (GroupBoxPool) getPool(ObjectType.GROUP_BOX);
	}
	
	public ORef getSingletonObjectRef(int objectType)
	{
		EAMObjectPool pool = getPool(objectType);
		ORefList objectRefs = pool.getORefList();
		if (objectRefs.size() == 1)
			return objectRefs.get(0);
		
		throw new RuntimeException("Wrong object count (count = " + objectRefs.size() + ") in pool for type:" + objectType);
	}
	
	public LayerManager getLayerManager()
	{
		return layerManager;
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

	public BaseObject findObject(ORef ref)
	{
		return findObject(ref.getObjectType(), ref.getObjectId());
	}

	public BaseObject findObject(int objectType, BaseId objectId)
	{
		return objectManager.findObject(new ORef(objectType, objectId));
	}
	
	public Factor findNode(FactorId nodeId)
	{
		return objectManager.findNode(nodeId);
	}
	
	public Factor findFactor(ORef factorRef)
	{
		return objectManager.findFactor(factorRef);
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
		return QuestionManager.getQuestion(questionClass);
	}


	/////////////////////////////////////////////////////////////////////////////////
	// objects
	
	public void setMetadata(String tag, String value) throws Exception
	{
		setObjectData(ObjectType.PROJECT_METADATA, getMetadataId(), tag, value);
	}
	
	public FactorLinkId obtainRealLinkageId(BaseId proposedId)
	{
		return projectInfo.obtainRealLinkId(proposedId);
	}
	
	public BaseId obtainRealNodeId(BaseId proposedId)
	{
		return projectInfo.obtainRealFactorId(proposedId);
	}
	
	public ORef createObject(int objectType) throws Exception
	{
		BaseId createdId = createObjectAndReturnId(objectType);
		return new ORef(objectType, createdId);
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
		return createObject(objectType, objectId, null);
	}
	
	public ORef createObject(int objectType, CreateObjectParameter extraInfo) throws Exception
	{
		return new ORef(objectType, createObjectAndReturnId(objectType, extraInfo));
	}
	
	public BaseId createObjectAndReturnId(int objectType, CreateObjectParameter extraInfo) throws Exception
	{
		return createObject(objectType, BaseId.INVALID, extraInfo);
	}
	
	public BaseId createObject(int objectType, BaseId objectId, CreateObjectParameter extraInfo) throws Exception
	{
		BaseId createdId = objectManager.createObject(objectType, objectId, extraInfo);
		saveProjectInfo();
		return createdId;
	}
	
	public void deleteObject(BaseObject object) throws IOException, ParseException
	{
		objectManager.deleteObject(object);
	}
	
	public void setObjectData(int objectType, BaseId objectId, String fieldTag, String dataValue) throws Exception
	{
		setObjectData(new ORef(objectType, objectId), fieldTag, dataValue);
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
	
	public int createOrOpen(File projectDirectory) throws Exception
	{
		clear();
		
		int projectAction;
		if(ProjectServer.isExistingProject(projectDirectory))
			projectAction = openProject(projectDirectory);
		else
			projectAction = createProject(projectDirectory);
		
		writeStartingLogEntry();
	
		finishOpening();
		
		return projectAction;
	}

	//TODO: need to remvoe duplicate code after test code fixed as to not need to be tested for
	// between writeStartingLogEntry and writeLogLine...the two new File() lines
	private void writeStartingLogEntry() throws IOException
	{
		File thisProjectDirectory = new File(EAM.getHomeDirectory(), getFilename());
		File commandLogFile = new File(thisProjectDirectory, COMMAND_LOG_FILE_NAME);
		if (commandLogFile.exists())
			commandLogFile.delete();
		writeLogLine("Project Opened by Miradi " + VersionConstants.VERSION_STRING);
	}

	public void writeLogLine(String logLine) throws IOException
	{
		File thisProjectDirectory = new File(EAM.getHomeDirectory(), getFilename());
		
		//TODO: this line is here to support test code
		if (!thisProjectDirectory.exists())
			return;
		
		File commandLogFile = new File(thisProjectDirectory, COMMAND_LOG_FILE_NAME);
		FileOutputStream os = new FileOutputStream(commandLogFile, true);
		PrintStream logPrintStream = new PrintStream(os);
		logPrintStream.println(logLine);
		EAM.logVerbose("Command Executed: " +logLine);
		os.close();
	}
	
	private void applyDefaultBehavior() throws Exception
	{
		simpleThreatFramework.createDefaultObjectsIfNeeded();
		createDefaultConceptualModel();
		createDefaultPlanningCustomization();
		selectDefaultPlanningCustomization();
		selectPlanningViewStrategicRadioButton();
		createDefaultProjectDataObject(WwfProjectData.getObjectType());
		createDefaultProjectDataObject(RareProjectData.getObjectType());
		createDefaultProjectDataObject(WcsProjectData.getObjectType());
		createDefaultProjectDataObject(TncProjectData.getObjectType());
		createDefaultProjectDataObject(FosProjectData.getObjectType());
		createDefaultProjectDataObject(WcpaProjectData.getObjectType());
		ensureAllConceptualModelPagesHaveLabels();
		ensureAllDiagramFactorsAreVisible();
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
		if(getPlanningViewConfigurationPool().getORefList().size() > 0)
			return;
		
		ORef createPlanningConfiguration = createObject(PlanningViewConfiguration.getObjectType());
		setObjectData(createPlanningConfiguration, PlanningViewConfiguration.TAG_LABEL, CreatePlanningViewConfigurationDoer.getConfigurationDefaultLabel(this));
		
		ViewData planningViewData = getViewData(PlanningView.getViewName());
		setObjectData(planningViewData.getRef(), ViewData.TAG_PLANNING_CUSTOM_PLAN_REF, createPlanningConfiguration.toString());
	}
	
	private void selectDefaultPlanningCustomization() throws Exception
	{
		ORef currentCustomizationRef = getViewData(PlanningView.getViewName()).getORef(ViewData.TAG_PLANNING_CUSTOM_PLAN_REF);
		if (! currentCustomizationRef.isInvalid())
			return;
		
		ORefList contomizationRefs = getPlanningViewConfigurationPool().getORefList();
		ViewData planningViewData = getViewData(PlanningView.getViewName());
		setObjectData(planningViewData.getRef(), ViewData.TAG_PLANNING_CUSTOM_PLAN_REF, contomizationRefs.get(0).toString());
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
		
		ORef mainDiagramRef = getConceptualModelDiagramPool().getORefList().getRefForType(ConceptualModelDiagram.getObjectType());
		ConceptualModelDiagram mainDiagram = (ConceptualModelDiagram) findObject(mainDiagramRef);
		ORefList diagramFactorRefs = mainDiagram.getAllDiagramFactorRefs();
		if (diagramFactorRefs.size() != 0)
			return;
		
		String text = EAM.loadResourceFile(ResourcesHandler.class, "DiagramInitialHelpText.txt");
		int indexOfNewLineForSize = text.indexOf("\n");
		String size = text.substring(0, indexOfNewLineForSize);
			
		String restAfterSize = text.substring(indexOfNewLineForSize + 1, text.length());
		int indexOfNewLineForLocation = restAfterSize.indexOf("\n");
		String location = restAfterSize.substring(0, indexOfNewLineForLocation);

		String restAfterLocation = restAfterSize.substring(indexOfNewLineForLocation, restAfterSize.length());
			
		ORef textBoxRef = createObject(TextBox.getObjectType());
		CreateDiagramFactorParameter extraInfo = new CreateDiagramFactorParameter(textBoxRef);
		ORef diagramFactorRef = createObject(DiagramFactor.getObjectType(), extraInfo);
		
		setObjectData(diagramFactorRef, DiagramFactor.TAG_SIZE, size);
		setObjectData(diagramFactorRef, DiagramFactor.TAG_LOCATION, location);
		setObjectData(textBoxRef, TextBox.TAG_LABEL, restAfterLocation);
		
		IdList diagramFactorIdList = new IdList(DiagramFactor.getObjectType());
		diagramFactorIdList.add(diagramFactorRef.getObjectId());
		setObjectData(mainDiagramRef, DiagramObject.TAG_DIAGRAM_FACTOR_IDS, diagramFactorIdList.toString());
	}
	
	private void createProjectMetadata() throws Exception
	{
		BaseId createdId = createObjectAndReturnId(ObjectType.PROJECT_METADATA);
		projectInfo.setMetadataId(createdId);
		setObjectData(getMetadata().getRef(), ProjectMetadata.TAG_CURRENCY_SYMBOL, "$");
		setObjectData(getMetadata().getRef(), ProjectMetadata.TAG_WORKPLAN_TIME_UNIT, BudgetTimePeriodQuestion.BUDGET_BY_YEAR_CODE);

		getDatabase().writeProjectInfo(projectInfo);
	}
	
	private void selectPlanningViewStrategicRadioButton() throws Exception
	{
		ViewData planningViewData = getViewData(PlanningView.getViewName());
		if (planningViewData.getData(ViewData.TAG_PLANNING_STYLE_CHOICE).length() == 0)
			setObjectData(planningViewData.getRef(), ViewData.TAG_PLANNING_STYLE_CHOICE, PlanningView.STRATEGIC_PLAN_RADIO_CHOICE);
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
	
	public int openProject(File projectDirectory) throws Exception
	{
		if(getDatabase().readDataVersion(projectDirectory) > ProjectServer.DATA_VERSION)
			throw new FutureVersionException();

		if(getDatabase().readDataVersion(projectDirectory) < ProjectServer.DATA_VERSION)
			DataUpgrader.attemptUpgrade(projectDirectory);
		
		if(getDatabase().readDataVersion(projectDirectory) < ProjectServer.DATA_VERSION)
			throw new OldVersionException();

		ProjectServer db = getDatabase();
		db.open(projectDirectory);
		try
		{
			loadProjectInfo();
			objectManager.loadFromDatabase();
			EAM.logVerbose("Highest Factor Id: " + getNodeIdAssigner().getHighestAssignedId());
			EAM.logVerbose("Highest Normal Id: " + getAnnotationIdAssigner().getHighestAssignedId());
		}
		catch(Exception e)
		{
			close();
			throw e;
		}
		
		return PROJECT_WAS_OPENED;
	}
	
	private int createProject(File projectDirectory) throws Exception
	{
		getDatabase().create(projectDirectory);
		return PROJECT_WAS_CREATED;
		
	}
	
	private void loadProjectInfo() throws IOException, ParseException
	{
		getDatabase().readProjectInfo(projectInfo);
	}
	
	private void saveProjectInfo() throws IOException
	{
		getDatabase().writeProjectInfo(projectInfo);
	}

	private void loadThreatRatingFramework() throws Exception
	{
		getSimpleThreatRatingFramework().load();
	}
	
	protected void finishOpening() throws Exception
	{
		if(getMetadataId().isInvalid())
			createProjectMetadata();
		
		loadThreatRatingFramework();
		
		applyDefaultBehavior();
		setDefaultDiagramPage(ObjectType.CONCEPTUAL_MODEL_DIAGRAM);
		setDefaultDiagramPage(ObjectType.RESULTS_CHAIN_DIAGRAM);
		database.writeVersion();

	}

	protected void setDefaultDiagramPage(int objectType) throws Exception
	{
		EAMObjectPool pool = getPool(objectType);
		if (pool.size() == 0)
			return;
	
		ViewData viewData = getCurrentViewData();
		ORef currentDiagramObjectRef = DiagramPageList.getCurrentDiagramViewDataRef(viewData, objectType);
		if (!currentDiagramObjectRef.isInvalid())
			return;

		ORef firstPoolItemRef = pool.getORefList().get(0);
		String currentDiagramViewDataTag = DiagramPageList.getCurrentDiagramViewDataTag(objectType);
		CommandSetObjectData setCurrentDiagramObject = new CommandSetObjectData(viewData.getRef(), currentDiagramViewDataTag, firstPoolItemRef);
		executeCommand(setCurrentDiagramObject);		
	}
	
	public String getFilename()
	{
		if(isOpen())
			return getDatabase().getName();
		return EAM.text("[No Project]");
	}

	public boolean isOpen()
	{
		return getDatabase().isOpen();
	}
	
	public void close() throws Exception
	{
		if(!isOpen())
			return;
		
		try
		{
			getDatabase().close();
			clear();
		}
		catch (IOException e)
		{
			EAM.logException(e);
		}
		
	}
	
	static public boolean isValidProjectFilename(String candidate)
	{
		return candidate.equals(makeProjectFilenameLegal(candidate));
	}
	
	static public String makeProjectFilenameLegal(String candidate)
	{
		if(candidate.length() < 1)
			return "-";
		
		if(candidate.length() > MAX_PROJECT_FILENAME_LENGTH)
			candidate = candidate.substring(0, MAX_PROJECT_FILENAME_LENGTH);
		
		char[] asArray = candidate.toCharArray();
		for(int i = 0; i < candidate.length(); ++i)
		{
			char c = asArray[i];
			if(c >= 128)
				continue;
			if(Character.isLetterOrDigit(c))
				continue;
			if(c == ' ' || c == '.' || c == '-')
				continue;
			
			asArray[i] = '-';
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

	public void executeCommand(Command command) throws CommandFailedException
	{
		if(command.isDoNothingCommand(this))
			return;
		
		isExecuting = true;
		try
		{
			executeWithoutRecording(command);
			recordCommand(command);
		}
		finally
		{
			isExecuting = false;
		}
	}
	
	public void executeCommandsWithoutTransaction(Command[] commands) throws CommandFailedException
	{
		for(int i = 0; i < commands.length; ++i)
		{
			executeCommand(commands[i]);
		}
	}

	public void executeCommandsWithoutTransaction(Vector commands) throws CommandFailedException
	{
		executeCommandsWithoutTransaction((Command[]) commands.toArray(new Command[0]));
	}
	
	public void executeCommandsAsTransaction(Command[] commands) throws CommandFailedException
	{
		executeCommand(new CommandBeginTransaction());
		try
		{
			executeCommandsWithoutTransaction(commands);
		}
		finally
		{
			executeCommand(new CommandEndTransaction());
		}
	}
	
	public Command undo() throws CommandFailedException
	{
		Command cmd = undoRedoState.popCommandToUndo();
		try
		{
			isExecuting = true;
			executeWithoutRecording(cmd.getReverseCommand());
			fireCommandExecuted(cmd.getReverseCommand());
			return cmd;
		}
		finally
		{
			isExecuting = false;
		}
	}
	
	public Command redo() throws CommandFailedException
	{
		Command cmd = undoRedoState.popCommandToRedo();
		try
		{
			EAM.logVerbose("Redoing: " + cmd.toString());
			isExecuting = true;
			executeWithoutRecording(cmd);
			fireCommandExecuted(cmd);
			return cmd;
		}
		finally
		{
			isExecuting = false;
		}
	}

	private void executeWithoutRecording(Command command) throws CommandFailedException
	{
		try 
		{
			EAM.logVerbose("Executing: " + command.toString());
			command.executeAndLog(this);
			EAM.logVerbose("Finished : " + command.toString());
		} 
		catch (CommandFailedException e) 
		{
			throw(e);
		}
	}
	
	public void executeInsideListener(Command command) throws CommandFailedException
	{
		if(!firingCommandExecutedEvents)
		{
			EAM.internalError(EAM.text("Attempt to execute command from outside command listener"));
		}
		
		executeWithoutRecording(command);
	}
	
	public void recordCommand(Command command)
	{
		Command lastCommand = undoRedoState.getLastRecordedCommand();
		if(firingCommandExecutedEvents)
		{
			EAM.internalError(
					EAM.text("Attempt to execute command from command listener: " + command.getCommandName() +
					EAM.text(" within ") + lastCommand.getCommandName())
					);
		}
		
		try
		{
			if(command.isEndTransaction() && lastCommand != null && lastCommand.isBeginTransaction())
			{
				undoRedoState.discardLastUndoableCommand();
			}
			else
			{
				undoRedoState.pushUndoableCommand(command);
			}
			fireCommandExecuted(command);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}
	
	public boolean isExecutingACommand()
	{
		return isExecuting;
	}

	public void addCommandExecutedListener(CommandExecutedListener listener)
	{
		if(commandExecutedListeners.contains(listener))
			throw new RuntimeException("Attempted to add listener twice: " + listener.getClass());
		EAM.logVerbose("addCommandExecutedListener: " + listener.getClass());
		commandExecutedListeners.add(listener);
	}
	
	public void removeCommandExecutedListener(CommandExecutedListener listener)
	{
		EAM.logVerbose("removeCommandExecutedListener: " + listener.getClass());
		if(!commandExecutedListeners.contains(listener))
			EAM.logWarning("removeCommandExecutedListener not in list: " + listener.getClass());
		commandExecutedListeners.remove(listener);
	}

	void fireCommandExecuted(Command command)
	{
		EAM.logVerbose("fireCommandExecuted: " + command.toString());
		firingCommandExecutedEvents = true;
		try
		{
			CommandExecutedEvent event = new CommandExecutedEvent(command);
			for(int i=0; i < getCommandListenerCount(); ++i)
			{
				CommandExecutedListener listener = (CommandExecutedListener)commandExecutedListeners.get(i);
				listener.commandExecuted(event);
			}
		}
		finally
		{
			firingCommandExecutedEvents = false;
		}
	}
	
	public int getCommandListenerCount()
	{
		return commandExecutedListeners.size();
	}
	
	public void logCommandListeners(PrintStream out)
	{
		for(int i=0; i < getCommandListenerCount(); ++i)
		{
			CommandExecutedListener listener = (CommandExecutedListener)commandExecutedListeners.get(i);
			out.println(listener.getClass());
		}
	}

	public boolean canUndo()
	{
		if(!isOpen())
			return false;
		
		return undoRedoState.canUndo();
	}
	
	public boolean canRedo()
	{
		if(!isOpen())
			return false;
		
		return undoRedoState.canRedo();
	}
	
	public void beginTransaction() throws CommandFailedException
	{
		if(inTransaction)
			throw new CommandFailedException("Attempted to nest transactions");
		inTransaction = true;
	}
	
	public void endTransaction()
	{
		inTransaction = false;
	}
	
	public boolean isInTransaction()
	{
		return inTransaction;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// views
	
	public void switchToView(String viewName) throws CommandFailedException
	{
		currentViewName = viewName;
	}

	public boolean isLegalViewName(String viewName)
	{
		return Arrays.asList(getLegalViewNames()).contains(viewName);
	}
	
	public String[] getLegalViewNames()
	{
		return new String[] {
			SUMMARY_VIEW_NAME,
			DIAGRAM_VIEW_NAME,
			NO_PROJECT_VIEW_NAME,
			THREAT_MATRIX_VIEW_NAME,
			BUDGET_VIEW_NAME,
			WORK_PLAN_VIEW_NAME,
			MAP_VIEW_NAME,
			SCHEDULE_VIEW_NAME,
			LIBRARY_VIEW_NAME,
			STRATEGIC_PLAN_VIEW_NAME,
			MONITORING_VIEW_NAME,
			TARGET_VIABILITY_NAME,
			
		};
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// diagram view

	public ORefList findConceptualModelThatContainsBothFactors(FactorId fromFactorId, FactorId toFactorId)
	{
		ORefList conceptualModels = new ORefList();
		ConceptualModelDiagramPool diagramPool = getConceptualModelDiagramPool();
		ORefList diagramORefs = diagramPool.getORefList();
		for (int i = 0; i < diagramORefs.size(); ++i)
		{
			ORef thisDiagramRef = diagramORefs.get(i);
			ConceptualModelDiagram diagram =  (ConceptualModelDiagram) findObject(thisDiagramRef);
			if (diagram.containsWrappedFactor(fromFactorId) && diagram.containsWrappedFactor(toFactorId))
				conceptualModels.add(thisDiagramRef); 		
		}
		
		return conceptualModels;
	}
	
	public DiagramLink[] getToAndFromLinks(DiagramFactorId diagramFactorId)
	{
		DiagramFactorLinkId[] allLinkIds = getDiagramFactorLinkPool().getallDiagramFactorLinkIds();
		Vector fromAndToLinksForFactor = new Vector();
		for (int i = 0; i < allLinkIds.length; i++)
		{
			DiagramLink link = (DiagramLink) findObject(new ORef(ObjectType.DIAGRAM_LINK, allLinkIds[i]));
			if ((link.getFromDiagramFactorId().equals(diagramFactorId) || (link.getToDiagramFactorId().equals(diagramFactorId))))
				fromAndToLinksForFactor.add(link);
		}
		
		return (DiagramLink[]) fromAndToLinksForFactor.toArray(new DiagramLink[0]);
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

	protected void writeFactor(FactorId factorId) throws IOException, ParseException
	{
		Factor cmNode = findNode(factorId);
		database.writeObject(cmNode);
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
		//TODO this null check is here for test code
		if (getObjectManager() == null)
			return value;
		
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

	public DecimalFormat getDecimalFormatter()
	{
		DecimalFormat formatter = new DecimalFormat("##0.##");
		formatter.setDecimalSeparatorAlwaysShown(false);
		
		return formatter;
	}
	
	public DecimalFormat getCurrencyFormatterWithCommas()
	{
		int currencyDecimalPlaces = getMetadata().getCurrencyDecimalPlaces();
		DecimalFormat formatter = new DecimalFormat();
		formatter.setMinimumFractionDigits(currencyDecimalPlaces);
		formatter.setMaximumFractionDigits(currencyDecimalPlaces);
		
		return formatter;
	}
	
	public DecimalFormat getCurrencyFormatterWithoutCommas()
	{
		DecimalFormat formatter = getCurrencyFormatterWithCommas();
		formatter.setGroupingUsed(false);
		
		return formatter;
	}
	
	public int getDiagramFontSize()
	{
		int size = getMetadata().getDiagramFontSize();
		if(size == 0)
			return DEFAULT_DIAGRAM_FONT_SIZE;
		
		return size;

	}


	public static final String MONITORING_VIEW_NAME = "Monitoring Plan";
	public static final String STRATEGIC_PLAN_VIEW_NAME = "Strategic Plan";
	public static final String LIBRARY_VIEW_NAME = "Library";
	public static final String SCHEDULE_VIEW_NAME = "Schedule";
	public static final String MAP_VIEW_NAME = "Map";
	public static final String WORK_PLAN_VIEW_NAME = "Work Plan";
	public static final String BUDGET_VIEW_NAME = "Financial";
	public static final String THREAT_MATRIX_VIEW_NAME = "ThreatMatrix";
	public static final String NO_PROJECT_VIEW_NAME = "NoProject";
	public static final String DIAGRAM_VIEW_NAME = "Diagram";
	public static final String SUMMARY_VIEW_NAME = "Summary";
	public static final String TARGET_VIABILITY_NAME = "Target Viability";
	public static final String PLANNING_VIEW_NAME = "Planning";
	public static final String REPORT_VIEW_NAME = "Reports";
	
	public static final String DEFAULT_VIEW_NAME = SUMMARY_VIEW_NAME;
	
	public static final int DEFAULT_GRID_SIZE = 15;
	public static final int DEFAULT_DIAGRAM_FONT_SIZE = 11;
	

	private static final int MAX_PROJECT_FILENAME_LENGTH = 32;
	
	private static final String COMMAND_LOG_FILE_NAME = "command.log";
	
	ProjectInfo projectInfo;
	ObjectManager objectManager;
	UndoRedoState undoRedoState;
	boolean isExecuting;
	boolean firingCommandExecutedEvents;

	SimpleThreatRatingFramework simpleThreatFramework;
	StressBasedThreatRatingFramework stressBasedThreatFramework;
	
	ProjectServer database;
	DiagramClipboard diagramClipboard;
	private ProjectCalendar projectCalendar;

	Vector commandExecutedListeners;
	
	LayerManager layerManager;
	boolean inTransaction;
	
	// FIXME: This should go away, but it's difficult
	String currentViewName;
	
	public static final int PROJECT_WAS_CREATED = 100;
	public static final int PROJECT_WAS_OPENED = 200;
}

