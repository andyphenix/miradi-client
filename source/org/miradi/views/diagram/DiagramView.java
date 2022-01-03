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
package org.miradi.views.diagram;

import org.martus.swing.Utilities;
import org.miradi.actions.*;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.*;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.diagram.cells.LinkCell;
import org.miradi.dialogs.base.AbstractDialogWithClose;
import org.miradi.dialogs.base.ModelessDialogWithClose;
import org.miradi.dialogs.diagram.*;
import org.miradi.ids.IdList;
import org.miradi.main.*;
import org.miradi.objecthelpers.FactorSet;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objects.*;
import org.miradi.project.Project;
import org.miradi.questions.DiagramModeQuestion;
import org.miradi.schemas.*;
import org.miradi.utils.CommandVector;
import org.miradi.utils.DiagramCorruptionDetector;
import org.miradi.utils.PointList;
import org.miradi.views.TabbedView;
import org.miradi.views.diagram.doers.*;
import org.miradi.views.planning.doers.CreateRelevancyActivityDoer;
import org.miradi.views.planning.doers.CreateRelevancyMonitoringActivityDoer;
import org.miradi.views.planning.doers.TreeNodeDeleteDoer;
import org.miradi.views.planning.doers.TreeNodeMoveActivityDoer;
import org.miradi.views.targetviability.doers.CreateKeyEcologicalAttributeMeasurementDoer;
import org.miradi.views.targetviability.doers.ExpandToIndicatorDoer;
import org.miradi.views.targetviability.doers.ExpandToMeasurementDoer;
import org.miradi.views.targetviability.doers.ExpandToMenuDoer;
import org.miradi.views.threatmatrix.doers.ManageStressesDoer;
import org.miradi.views.umbrella.DeleteActivityDoer;
import org.miradi.views.umbrella.UmbrellaView;
import org.miradi.views.umbrella.doers.TaskMoveDownDoer;
import org.miradi.views.umbrella.doers.TaskMoveUpDoer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;


public class DiagramView extends TabbedView implements CommandExecutedListener
{
	public DiagramView(MainWindow mainWindowToUse) throws Exception
	{
		super(mainWindowToUse);
		mode = DiagramModeQuestion.MODE_DEFAULT;
		
		addDiagramViewDoersToMap();
	}

	private void updateToolBar()
	{
		getMainWindow().rebuildToolBar();
	}

	public void addDiagramViewListener(DiagramViewListener listener)
	{
		diagramViewListenerList.add(listener);
	}

	public void removeDiagramViewListener(DiagramViewListener listener)
	{
		diagramViewListenerList.remove(listener);
	}

	public DiagramComponent getCurrentDiagramComponent()
	{
		if(getCurrentDiagramPanel() == null)
			return null;
		
		return getCurrentDiagramPanel().getCurrentDiagramComponent();
	}
	
	public DiagramObject getCurrentDiagramObject()
	{
		if (getCurrentDiagramComponent() == null)
			return null;
		
		return getCurrentDiagramComponent().getDiagramModel().getDiagramObject();
	}

	@Override
	public String cardName()
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return Project.DIAGRAM_VIEW_NAME;
	}
	
	@Override
	public MiradiToolBar createToolBar()
	{
		return new DiagramToolBar(getActions(), this);
	}
	
	@Override
	public BaseObject getSelectedObject()
	{
		FactorCell node = getCurrentDiagramComponent().getSingleSelectedFactor();
		if(node == null)
			return null;
		return node.getWrappedFactor();
	}
	
	public PropertiesDoer getPropertiesDoer()
	{
		return propertiesDoer;
	}

	private void addDiagramViewDoersToMap()
	{
		propertiesDoer = new PropertiesDoer();

		addDoerToMap(ActionInsertIntermediateResult.class, new InsertIntermediateResultDoer());
		addDoerToMap(ActionInsertThreatReductionResult.class, new InsertThreatReductionResultDoer());
		addDoerToMap(ActionInsertTarget.class, new InsertTargetDoer());
		addDoerToMap(ActionInsertHumanWelfareTarget.class, new InsertHumanWelfareTargetDoer());
		addDoerToMap(ActionInsertContributingFactor.class, new InsertContributingFactorDoer());
		addDoerToMap(ActionInsertBiophysicalFactor.class, new InsertBiophysicalFactorDoer());
		addDoerToMap(ActionInsertBiophysicalResult.class, new InsertBiophysicalResultDoer());
		addDoerToMap(ActionInsertDirectThreat.class, new InsertDirectThreatDoer());
		addDoerToMap(ActionInsertStrategy.class, new InsertStrategyDoer());
		addDoerToMap(ActionInsertDraftStrategy.class, new InsertDraftStrategyDoer());
		addDoerToMap(ActionInsertLink.class, new InsertLinkDoer());
		addDoerToMap(ActionCreateBendPoint.class, new CreateBendPointDoer());
		addDoerToMap(ActionDeleteBendPoint.class, new DeleteBendPointDoer());
		addDoerToMap(ActionRenameResultsChain.class, new RenameResultsChainDoer());
		addDoerToMap(ActionRenameConceptualModel.class, new RenameConceptualModelDoer());
		addDoerToMap(ActionArrangeConceptualModel.class, new ArrangeConceptualModelDoer());
		addDoerToMap(ActionCopy.class, new CopyDoer());
		addDoerToMap(ActionSelectAll.class, new SelectAllDoer());
		addDoerToMap(ActionClearAll.class, new ClearAllDoer());
		addDoerToMap(ActionCut.class, new CutDoer());
		addDoerToMap(ActionDelete.class, new DeleteSelectedItemDoer());
		addDoerToMap(ActionPaste.class, new PasteDoer());
		addDoerToMap(ActionPasteFactorContent.class, new PasteFactorContentDoer());
		addDoerToMap(ActionPasteWithoutLinks.class, new PasteWithoutLinks());
		addDoerToMap(ActionSelectChain.class, new SelectChainDoer());
		addDoerToMap(ActionProperties.class, propertiesDoer);
		addDoerToMap(ActionConfigureLayers.class, new ConfigureLayers());
		addDoerToMap(ActionShowSelectedChainMode.class, new ShowSelectedChainModeDoer());
		addDoerToMap(ActionShowFullModelMode.class, new ShowFullModelModeDoer());
		addDoerToMap(ActionZoomIn.class, new ZoomIn());
		addDoerToMap(ActionZoomOut.class, new ZoomOut());
		addDoerToMap(ActionZoomToFit.class, new ZoomToFitDoer());
		addDoerToMap(ActionCreateDiagramMargin.class, new CreateMarginDoer());
		addDoerToMap(ActionNudgeUp.class, new NudgeDoer(KeyEvent.VK_UP)); 
		addDoerToMap(ActionNudgeDown.class, new NudgeDoer(KeyEvent.VK_DOWN));
		addDoerToMap(ActionNudgeLeft.class, new NudgeDoer(KeyEvent.VK_LEFT));
		addDoerToMap(ActionNudgeRight.class, new NudgeDoer(KeyEvent.VK_RIGHT));
		
		addDoerToMap(ActionCreateActivity.class, new CreateActivityDoer());
		addDoerToMap(ActionDeleteActivity.class, new DeleteActivityDoer());
		addDoerToMap(ActionMoveActivity.class, new TreeNodeMoveActivityDoer());
		addDoerToMap(ActionTreeCreateRelevancyActivity.class, new CreateRelevancyActivityDoer());
		addDoerToMap(ActionTreeCreateRelevancyMonitoringActivity.class, new CreateRelevancyMonitoringActivityDoer());
		addDoerToMap(ActionDeletePlanningViewTreeNode.class, new TreeNodeDeleteDoer());

		addDoerToMap(ActionCreateObjective.class, new CreateObjectiveDoer());
		addDoerToMap(ActionCloneObjective.class, new CloneObjectiveDoer());
		addDoerToMap(ActionDeleteObjective.class, new DeleteObjective());
		
		addDoerToMap(ActionCreateIndicator.class, new CreateIndicator());
		addDoerToMap(ActionCloneIndicator.class, new CloneIndicatorDoer());
		
		addDoerToMap(ActionCreateGoal.class, new CreateGoal());
		addDoerToMap(ActionCloneGoal.class, new CloneGoalDoer());
		addDoerToMap(ActionDeleteGoal.class, new DeleteGoal());
		
		addDoerToMap(ActionCreateStress.class, new CreateStressDoer());
		addDoerToMap(ActionDeleteStress.class, new DeleteStressDoer());
		addDoerToMap(ActionCloneStress.class, new CloneStressDoer());
		addDoerToMap(ActionCreateStressFromKea.class, new CreateStressFromKeaDoer());
		
		addDoerToMap(ActionCreateKeyEcologicalAttribute.class, new CreateKeyEcologicalAttributeDoer());
		addDoerToMap(ActionDeleteKeyEcologicalAttribute.class, new DeleteKeyEcologicalAttributeDoer());
		addDoerToMap(ActionCreateKeyEcologicalAttributeIndicator.class, new CreateViabilityIndicatorDoer());
		addDoerToMap(ActionDeleteKeyEcologicalAttributeIndicator.class, new DeleteViabilityIndicatorDoer());
		addDoerToMap(ActionCreateKeyEcologicalAttributeMeasurement.class, new CreateKeyEcologicalAttributeMeasurementDoer());
		
		addDoerToMap(ActionCreateIndicatorMeasurement.class, new CreateKeyEcologicalAttributeMeasurementDoer());
		addDoerToMap(ActionCreateFutureStatus.class, new CreateFutureStatusDoer());
		
		addDoerToMap(ActionCreateAssumption.class, new CreateAssumptionDoer());
		addDoerToMap(ActionDeleteAssumption.class, new DeleteAssumptionDoer());
		
		addDoerToMap(ActionExpandToMenu.class, new ExpandToMenuDoer());
		addDoerToMap(ActionExpandToIndicator.class, new ExpandToIndicatorDoer());
		addDoerToMap(ActionExpandToMeasurement.class, new ExpandToMeasurementDoer());
		addDoerToMap(ActionExpandToFutureStatus.class, new ExpandToFutureStatusDoer());
		
		addDoerToMap(ActionCreateResultsChain.class, new CreateResultsChainDoer());
		addDoerToMap(ActionShowResultsChain.class, new ShowResultsChainDoer());
		addDoerToMap(ActionDeleteResultsChain.class, new DeleteResultsChainDoer());
		addDoerToMap(ActionShowConceptualModel.class, new ShowConceptualModelDoer());
		addDoerToMap(ActionCreateOrShowResultsChain.class, new CreateOrShowResultsChainDoer());
		addDoerToMap(ActionInsertTextBox.class, new InsertTextBoxDoer());
		addDoerToMap(ActionInsertScopeBox.class, new InsertScopeBoxDoer());
		addDoerToMap(ActionInsertGroupBox.class, new InsertGroupBoxDoer());
		addDoerToMap(ActionCreateConceptualModel.class, new CreateConceptualModelPageDoer());
		addDoerToMap(ActionDeleteConceptualModel.class, new DeleteConceptualModelPageDoer());
		addDoerToMap(ActionGroupBoxAddFactor.class, new GroupBoxAddDiagramFactorDoer());
		addDoerToMap(ActionGroupBoxRemoveFactor.class, new GroupBoxRemoveDiagramFactorDoer());
		addDoerToMap(ActionDeleteGroupBox.class, new DeleteGroupBoxDoer());
		
		addDoerToMap(ActionManageStresses.class, new ManageStressesDoer());
		
		addDoerToMap(ActionCreateSubTarget.class, new CreateSubTargetDoer());
		addDoerToMap(ActionDeleteSubTarget.class, new DeleteSubTargetDoer());
		addDoerToMap(ActionDiagramProperties.class, new DiagramPropertiesShowDoer());
		
		addDoerToMap(ActionActivityMoveUp.class, new TaskMoveUpDoer());
		addDoerToMap(ActionActivityMoveDown.class, new TaskMoveDownDoer());
		
		addDoerToMap(ActionCreateIncomingJunction.class, new CreateIncomingJunctionDoer());
		addDoerToMap(ActionCreateOutgoingJunction.class, new CreateOutgoingJunctionDoer());
		
		addDoerToMap(ActionShowStressBubble.class, new ShowStressBubbleDoer());
		addDoerToMap(ActionHideStressBubble.class, new HideStressBubbleDoer());
		
		addDoerToMap(ActionShowActivityBubble.class, new ShowActivityBubbleDoer());
		addDoerToMap(ActionHideActivityBubble.class, new HideActivityBubbleDoer());
		
		addDoerToMap(ActionShowAssumptionBubble.class, new ShowAssumptionBubbleDoer());
		addDoerToMap(ActionHideAssumptionBubble.class, new HideAssumptionBubbleDoer());
		
		addDoerToMap(ActionCreateTaggedObjectSet.class, new CreateTaggedObjectSetDoer());
		addDoerToMap(ActionDeleteTaggedObjectSet.class, new DeleteTaggedObjectSetDoer());
		addDoerToMap(ActionEditTaggedObjectSet.class, new EditTaggedObjectSetDoer());
		addDoerToMap(ActionManageFactorTagsFromMenu.class, new ManageFactorTagsFromMenuDoer());
		addDoerToMap(ActionManageFactorTags.class, new ManageFactorTagsDoer());
		addDoerToMap(ActionCreateNamedTaggedObjectSet.class, new CreateNamedTaggedObjectSetDoer());

		addDoerToMap(ActionCopyDiagramFactorFormat.class, new CopyDiagramFactorFormatDoer());
		addDoerToMap(ActionPasteDiagramFactorFormat.class, new PasteDiagramFactorFormatDoer());
	}
	
	@Override
	public void tabWasSelected()
	{
		getMainWindow().preventActionUpdates();
		try
		{
			super.tabWasSelected();
			getCurrentDiagramPanel().showCurrentDiagram();
			updateVisibilityOfFactorsAndClearSelectionModel();
			if (getCurrentDiagramComponent()!=null)
			{
				getCurrentDiagramComponent().updateDiagramZoomSetting();
			}
			tabWasSelected(new DiagramViewEvent(this));
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog(EAM.text("Unknown error displaying diagram"));
		}
		finally
		{
			getMainWindow().allowActionUpdates();
			getMainWindow().updateActionsAndStatusBar();
		}
	}

	private void tabWasSelected(DiagramViewEvent evt)
	{
		for (DiagramViewListener diagramViewListener : diagramViewListenerList)
		{
			diagramViewListener.tabWasSelected(evt);
		}
	}

	public void diagramWasSelected()
	{
		DiagramViewEvent evt = new DiagramViewEvent(this);
		for (DiagramViewListener diagramViewListener : diagramViewListenerList)
		{
			diagramViewListener.diagramWasSelected(evt);
		}
	}

	private void switchToFullMode() throws Exception
	{
		if (!isStrategyBrainstormMode())
			return;
	
		CommandVector commands = ShowFullModelModeDoer.createCommandsToSwithToDefaultMode(getViewData().getRef());
		getProject().executeAsSideEffect(commands);
	}

	@Override
	protected void prepareForTabSwitch()
	{
		super.prepareForTabSwitch();
		try
		{
			switchToFullMode();
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}

	@Override
	public void createTabs() throws Exception
	{
		getMainWindow().preventActionUpdates();
		try
		{
			createConceptualModelDiagramTab();
			createResultsChainTab();
			
			//TODO get tag using object type, diagram splitter has this info.  
			ensureDiagramIsSelected(ConceptualModelDiagramSchema.getObjectType(), ViewData.TAG_CURRENT_CONCEPTUAL_MODEL_REF);
			ensureDiagramIsSelected(ResultsChainDiagramSchema.getObjectType(), ViewData.TAG_CURRENT_RESULTS_CHAIN_REF);
			
			conceptualDiagramPanel.restoreSavedLocation();
			resultsChainPanel.restoreSavedLocation();
		}
		finally
		{
			getMainWindow().allowActionUpdates();
			getMainWindow().updateActionsAndStatusBar();
		}
	}
	
	@Override
	public void becomeActive() throws Exception
	{
		super.becomeActive();
		setMode(getViewData().getData(ViewData.TAG_CURRENT_MODE));
		
		Vector<String> errorMessages = DiagramCorruptionDetector.getCorruptedDiagrams(getProject());
		if (errorMessages.size() > 0)
		{
			EAM.errorDialog(getDiagramCorruptionMessage(errorMessages));
		}
		
		DiagramCorruptionDetector.warnUserAboutGroupBoxLinkCorruption(getMainWindow());
	}

	private String getDiagramCorruptionMessage(Vector<String> errorMessages)
	{
		StringBuffer corruptedDiagramNames = new StringBuffer();
		corruptedDiagramNames.append("<HTML>" + EAM.text("Found Corrupted Data:"));
		for (int index = 0; index < errorMessages.size(); ++index)
		{
			corruptedDiagramNames.append("<HR></HR>");
			corruptedDiagramNames.append(errorMessages.get(index));
		}
		
		corruptedDiagramNames.append("</HTML>");
		
		return corruptedDiagramNames.toString();
	}
	
	private void createResultsChainTab() throws Exception
	{
		resultsChainPanel = new ResultsChainDiagramPanel(getMainWindow());
		addNonScrollingTab(resultsChainPanel);
	}

	private void createConceptualModelDiagramTab() throws Exception
	{
		conceptualDiagramPanel = new ConceptualModelDiagramPanel(getMainWindow());
		addNonScrollingTab(conceptualDiagramPanel);
	}
	
	private void ensureDiagramIsSelected(int objectType, String tag) throws Exception
	{
		ViewData viewData = getViewData();
		String orefAsJsonString = viewData.getData(tag);
		ORef currentDiagramRef = ORef.createFromString(orefAsJsonString);
		if (!currentDiagramRef.isInvalid())
			return;
			
		EAMObjectPool objectPool = getProject().getPool(objectType);
		if (objectPool.size() == 0)
			return;
		
		ORefList orefList = objectPool.getORefList();
		ORef firstRef = orefList.get(0);
		
		//NOTE: Since we are inside commandExecuted, we can't execute another command here,
		// which is ok, because we are switching away from the absence of a diagram, 
		// so there would be no requirement for undo to restore it
		getProject().setObjectData(viewData.getRef(), tag, firstRef.toString());
	}
	
	public DiagramPanel getDiagramPanel()
	{
		return getCurrentDiagramPanel();
	}

	public DiagramPanel getCurrentDiagramPanel()
	{
		return (DiagramPanel) getCurrentTabContents();
	}

	public boolean isResultsChainTab()
	{
		int index = getSelectedTabIndex();
		if (index  < 0)
			return false;
		
		DiagramPanel panel = (DiagramPanel)getTabContents(index);
		DiagramSplitPane diagramSplitPane = panel.getDiagramSplitPane();
		return diagramSplitPane.getDiagramPageList().isResultsChainPageList();
	}
		
	public int getTabIndex(ORef ref)
	{
		for (int i = 0; i < getTabCount(); ++i)
		{
			DiagramPanel panel = (DiagramPanel)getTabContents(i);
			int diagramObjectType = panel.getDiagramSplitPane().getDiagramPageList().getManagedDiagramType();
			if (diagramObjectType == ref.getObjectType())
				return i;
		}
		
		return 0;
	}
	
	private void updateLegendPanelCheckBoxes() throws Exception
	{
		getMainWindow().preventActionUpdates();
		try
		{
			getDiagramPanel().getDiagramLegendPanel().rebuild();
		}
		finally
		{
			getMainWindow().allowActionUpdates();
			getMainWindow().updateActionsAndStatusBar();
		}
	}

	public DiagramModel getDiagramModel()
	{
		DiagramComponent diagramComponent = getCurrentDiagramComponent();
		if(diagramComponent == null)
			return null;
		return diagramComponent.getDiagramModel();
	}

	@Override
	public void deleteTabs() throws Exception
	{
		// TODO: This should completely tear down the view
		disposeOfNodePropertiesDialog();
		
		for(int i = 0; i < getTabCount(); ++i)
		{
			disposeOfTabPriorToRemovingIt(i);
		}
		
		super.deleteTabs();
	}

	private void disposeOfTabPriorToRemovingIt(int i)
	{
		DiagramPanel panel = (DiagramPanel)getTabContents(i);
		panel.dispose();
	}
	
	private void setMode(String newMode) throws Exception
	{
		mode = newMode;
		DiagramComponent diagramComponent = getCurrentDiagramComponent();
		if (diagramComponent != null)
		{
			hideFactorsForMode(diagramComponent, newMode);
			diagramComponent.clearSelection();
		}
		
		updateToolBar();
		getMainWindow().updateStatusBar();
		updateLegendPanelCheckBoxes();
		updateVisibilityOfFactorsAndClearSelectionModel();
	}

	public static void hideFactorsForMode(DiagramComponent diagramComponent, String newMode) throws Exception
	{
		if (diagramComponent.getDiagramObject().isResultsChain())
			return;
		
		diagramComponent.getDiagramModel().updateGroupBoxCells();
		ORefList hiddenORefs = new ORefList();
		diagramComponent.setToDefaultBackgroundColor();
		if (newMode.equals(DiagramModeQuestion.MODE_STRATEGY_BRAINSTORM))
		{
			hiddenORefs = getORefsToHide(diagramComponent.getDiagramModel());
			diagramComponent.setBackground(Color.LIGHT_GRAY);
		}
			
		LayerManager manager = diagramComponent.getDiagramModel().getLayerManager();
		manager.setHiddenORefs(hiddenORefs);
		manager.setMode(newMode);
	}
	
	public void updateVisibilityOfFactorsAndLinks()
	{
		if (!isCurrentViewDiagramView())
			return;
	
		try
		{
			DiagramModel model = getDiagramModel();
			if(model == null)
				return;
			model.updateVisibilityOfFactorsAndLinks();
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}
	
	public void updateVisibilityOfFactorsAndClearSelectionModel()
	{
		if (!isCurrentViewDiagramView())
			return;
		
		updateVisibilityOfFactorsAndLinks();
		DiagramComponent diagramComponent = getCurrentDiagramComponent();
		if (diagramComponent == null)
			return;

		if (getCurrentDiagramObject().isTaggingEnabled())
		{
			EAMGraphSelectionModel selectionModel = (EAMGraphSelectionModel) diagramComponent.getSelectionModel();
			if(selectionModel != null)
				selectionModel.clearSelection();
		}
	}

	private boolean isCurrentViewDiagramView()
	{
		return DiagramView.is(getMainWindow().getCurrentView());
	}


	private static ORefList getORefsToHide(DiagramModel diagramModel) throws Exception
	{
		ORefList oRefsToHide = new ORefList();
		ViewData viewData = diagramModel.getProject().getDiagramViewData();
		ORefList visibleFactorORefs = new ORefList(viewData.getData(ViewData.TAG_CHAIN_MODE_FACTOR_REFS));
		visibleFactorORefs.addAll(getRelatedDraftStrategies(diagramModel, visibleFactorORefs));
		DiagramFactor[] allDiagramFactors = diagramModel.getProject().getAllDiagramFactors();
		for (int i = 0; i < allDiagramFactors.length; ++i)
		{
			DiagramFactor diagramFactor = allDiagramFactors[i];
			ORef ref = diagramFactor.getWrappedORef();
			if (!visibleFactorORefs.contains(ref))
				oRefsToHide.add(ref);
		}
		return oRefsToHide;
	}
	
	private static ORefList getRelatedDraftStrategies(DiagramModel diagramModel, ORefList factorORefs) throws Exception
	{
		ORefList draftsToAdd = new ORefList();
		
		for(int i = 0; i < factorORefs.size(); ++i)
		{
			DiagramFactor diagramFactor = diagramModel.getFactorCellByWrappedRef(factorORefs.get(i)).getDiagramFactor();
			ChainWalker chainObject = diagramModel.getDiagramChainWalker();
			FactorSet possibleDraftStrategies = chainObject.buildDirectlyLinkedUpstreamChainAndGetFactors(diagramFactor);
			Iterator iter = possibleDraftStrategies.iterator();
			while(iter.hasNext())
			{
				ORef possibleStrategyORef = ((Factor)iter.next()).getRef();
				if(factorORefs.contains(possibleStrategyORef))
					continue;
				Factor possibleIntervention = Factor.findFactor(diagramModel.getProject(), possibleStrategyORef);
				if(possibleIntervention.isStrategy() && possibleIntervention.isStatusDraft())
					draftsToAdd.add(possibleIntervention.getRef());
			}
		}
		
		return draftsToAdd;
	}
	
	@Override
	public JPopupMenu getTabPopupMenu()
	{
		DiagramTabMouseMenuHandler handler = new DiagramTabMouseMenuHandler(this);
		return handler.getPopupMenu();
	}

	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);

		if (!event.isSetDataCommand())
			return;

		CommandSetObjectData setCommand = (CommandSetObjectData) event.getCommand();
		try
		{
			if(isDeleteVisibleDiagramFactorCommand(setCommand))
				disposeOfNodePropertiesDialog();
		
			updateAllTabs(setCommand);
			setToDefaultMode(setCommand);
			if (event.isSetDataCommandWithThisTypeAndTag(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_TEXT_BOX_Z_ORDER_CODE))
				handleTextBoxZOrderChanged(setCommand.getObjectORef());
			
			if(event.isSetDataCommandWithThisTypeAndTag(GroupBoxSchema.getObjectType(), GroupBox.TAG_LABEL) ||
               event.isSetDataCommandWithThisTypeAndTag(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_HEADER_HEIGHT))
				getDiagramModel().updateGroupBoxCells();

			if (setCommand.isTypeAndTag(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_TAGGED_OBJECT_SET_REFS))
				updateVisibilityOfFactorsAndClearSelectionModel();

			forceDiagramComponentRepaint();
		}
		catch (Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
		}
	}

	private void handleTextBoxZOrderChanged(ORef diagramFactorRef) throws Exception
	{
		if (getDiagramModel().containsDiagramFactor(diagramFactorRef))
		{
			getDiagramModel().sortLayers();
			FactorCell factorCell = getDiagramModel().getFactorCellByRef(diagramFactorRef);
			getDiagramModel().updateCell(factorCell);
		}
	}

	private void forceDiagramComponentRepaint()
	{
		DiagramComponent currentDiagramComponent = getCurrentDiagramComponent();
		if (currentDiagramComponent != null)
			currentDiagramComponent.forceRepaint();
	}

	private boolean isDeleteVisibleDiagramFactorCommand(CommandSetObjectData setCommand) throws Exception
	{		
		if (!DiagramObject.isDiagramObject(setCommand.getObjectORef()))
			return false;

		if (!setCommand.getFieldTag().equals(DiagramObject.TAG_DIAGRAM_FACTOR_IDS))
			return false;
			
		if (nodePropertiesPanel == null)
			return false;

		if  ((this.getDiagramModel().isResultsChain() && !ResultsChainDiagram.is(setCommand.getObjectORef())) ||
			 (this.getDiagramModel().isConceptualModelDiagram() && !ConceptualModelDiagram.is(setCommand.getObjectORef())))
			return false;

		DiagramFactor diagramFactorRefForPropertiesDialog = nodePropertiesPanel.getCurrentDiagramFactor();
		if (diagramFactorRefForPropertiesDialog == null)
			return true;
		
		IdList currentList = new IdList(DiagramFactorSchema.getObjectType(), setCommand.getDataValue());
		return !currentList.contains(diagramFactorRefForPropertiesDialog.getRef());
	}

	private void setToDefaultMode(CommandSetObjectData cmd) throws Exception
	{
		if (cmd.getObjectType() != ObjectType.VIEW_DATA)
			return;
		
		if (! isDiagramObjectTag(cmd.getFieldTag()))
			return;
		
		ViewData viewData = getProject().getCurrentViewData();
		String currentDiagramPage = viewData.getData(ViewData.TAG_CURRENT_CONCEPTUAL_MODEL_REF);
		if (cmd.getDataValue().equals(currentDiagramPage))
			return;
		
		String currentResultsChainPageRef = viewData.getData(ViewData.TAG_CURRENT_RESULTS_CHAIN_REF); 
		if (cmd.getDataValue().equals(currentResultsChainPageRef))
			return;
				
		switchToFullMode();
	}

	private boolean isDiagramObjectTag(String tag)
	{
		return (tag.equals(ViewData.TAG_CURRENT_CONCEPTUAL_MODEL_REF)) && (tag.equals(ViewData.TAG_CURRENT_RESULTS_CHAIN_REF));
	}

	private void updateAllTabs(CommandSetObjectData cmd) throws Exception
	{
		String newValue = cmd.getDataValue();
		setModeIfRelevant(cmd, newValue);
		
		DiagramComponent[] diagramComponents = getAllDiagramComponents();
		for (int i = 0; i < diagramComponents.length; ++i)
		{
			DiagramModel model = diagramComponents[i].getDiagramModel();
			updateFactorBoundsIfRelevant(model, cmd);
			updateFactorLinkIfRelevant(model, cmd);
			refreshIfNeeded(diagramComponents[i], cmd);
		}
	}

	private DiagramComponent[] getAllDiagramComponents()
	{
		int tabCount = getTabCount();
		Vector<DiagramComponent> allDiagramComponents = new Vector<DiagramComponent>();
		for (int i = 0; i < tabCount; ++i)
		{
			DiagramPanel panel = (DiagramPanel) getTabContents(i);
			DiagramComponent[] panelDiagramComponents = panel.getAllSplitterDiagramComponents();
			allDiagramComponents.addAll(Arrays.asList(panelDiagramComponents));
		}

		return allDiagramComponents.toArray(new DiagramComponent[0]);
	}

	private void updateFactorLinkIfRelevant(DiagramModel model, CommandSetObjectData cmd) throws Exception
	{
		ORef diagramLinkRef = extractDiagramLinkRef(cmd);
		if(diagramLinkRef.isInvalid())
			return;
		
		ORef diagramLinkRefToUpdate = getParentOrSelf(diagramLinkRef);
		LinkCell cell = model.updateCellFromDiagramFactorLink(diagramLinkRefToUpdate);
		if(cell == null)
			return;

		clearBendPointSelectionList(cell, cmd);
		
		cell.update(getCurrentDiagramComponent());
		model.updateCell(cell);
	}
	
	private ORef extractDiagramLinkRef(CommandSetObjectData cmd) throws Exception
	{
		if(cmd.getObjectType() == ObjectType.DIAGRAM_LINK)
		{
			return cmd.getObjectORef();
		}
		if(cmd.getObjectType() == ObjectType.FACTOR_LINK)
		{
			return getDiagramLinkRefFromFactorLinkRef(cmd.getObjectORef());
		}
		
		return ORef.INVALID;
	}
	
	private ORef getParentOrSelf(ORef diagramLinkRef)
	{
		DiagramLink diagramLink = DiagramLink.find(getProject(), diagramLinkRef);		
		ORefList groupBoxLinkReferrerRefs = diagramLink.findObjectsThatReferToUs(DiagramLinkSchema.getObjectType());
		if (groupBoxLinkReferrerRefs.size() > 0)
			return groupBoxLinkReferrerRefs.get(0);
		
		return diagramLinkRef;
	}

	private void clearBendPointSelectionList(LinkCell cell, CommandSetObjectData cmd) throws Exception
	{
		if (! cmd.getFieldTag().equals(DiagramLink.TAG_BEND_POINTS))
			return;
		
		PointList newPointList = new PointList(cmd.getDataValue());
		PointList oldPointList = new PointList(cmd.getPreviousDataValue());
		
		if (newPointList.size() == oldPointList.size())
			return;
			
		cell.getBendPointSelectionHelper().clearSelection();
	}

	private ORef getDiagramLinkRefFromFactorLinkRef(ORef factorLinkRef) throws Exception
	{
		if(!getDiagramModel().doesDiagramLinkExist(factorLinkRef))
			return ORef.INVALID;
		
		DiagramLink link = getDiagramModel().getDiagramLinkByWrappedRef(factorLinkRef);
		if(link == null)
			return ORef.INVALID;
		
		return link.getRef();
	}

	private void updateFactorBoundsIfRelevant(DiagramModel model, CommandSetObjectData cmd) throws Exception
	{
		if (cmd.getObjectType() != ObjectType.DIAGRAM_FACTOR)
			return;
		
		ORef diagramFactorRef = cmd.getObjectORef();
		model.updateCellFromDiagramFactor(diagramFactorRef);
	}

	private void setModeIfRelevant(CommandSetObjectData cmd, String newMode) throws Exception
	{
		String fieldTag = cmd.getFieldTag();
		ViewData ourViewData = getViewData();
		if (!cmd.getObjectORef().equals(ourViewData.getRef()))
			return;

		boolean modeChange = fieldTag.equals(ViewData.TAG_CURRENT_MODE);
		boolean cmChange = fieldTag.equals(ViewData.TAG_CURRENT_CONCEPTUAL_MODEL_REF);
		boolean rcChange = fieldTag.equals(ViewData.TAG_CURRENT_RESULTS_CHAIN_REF);
		if(modeChange || cmChange || rcChange)
		{
			setMode(newMode);
		}
	}
	
	private void refreshIfNeeded(DiagramComponent diagramComponent, CommandSetObjectData cmd)
	{
		// may have added or removed a stress, modified an Annotation short label, etc.
		diagramComponent.repaint(diagramComponent.getBounds());
	}
	
	@Override
	public void showFloatingPropertiesDialog(AbstractDialogWithClose newDialog)
	{
		if(nodePropertiesDlg != null)
			disposeOfNodePropertiesDialog();
		super.showFloatingPropertiesDialog(newDialog);
	}

    public void showNodeProperties(DiagramFactor node, int startingTabIdentifier)
    {
        closeActivePropertiesDialog();
        disposeOfNodePropertiesDialog();

        getCurrentDiagramComponent().requestFocusInWindow();
        FactorPropertiesPanel newPropertiesPanel = new FactorPropertiesPanel(getMainWindow(), getCurrentDiagramComponent());
        String title = EAM.text("Title|Factor Properties");
        FactorPropertiesDialog newPropertiesDialog = new FactorPropertiesDialog(getMainWindow(), newPropertiesPanel, title);
        newPropertiesPanel.setCurrentDiagramFactor(node);
        newPropertiesDialog.pack();
        newPropertiesDialog.updatePreferredSize();
        newPropertiesDialog.pack();
        newPropertiesPanel.selectTab(startingTabIdentifier);
        Utilities.centerDlg(newPropertiesDialog);

        getCurrentDiagramComponent().selectFactor(node.getWrappedORef());
        nodePropertiesPanel = newPropertiesPanel;
        nodePropertiesDlg = newPropertiesDialog;

        nodePropertiesDlg.setVisible(true);
    }

	private void disposeOfNodePropertiesDialog()
	{
		if(nodePropertiesDlg != null)
			nodePropertiesDlg.dispose();
		nodePropertiesDlg = null;
		nodePropertiesPanel = null;
	}

	public void selectionWasChanged()
	{
		closeActivePropertiesDialog();
		if(nodePropertiesDlg == null)
			return;
		
		if(isRelatedStressSelected())
			return;

		if(isRelatedActivitySelected())
			return;
		
		if(wasDifferentFactorSelected())
			disposeOfNodePropertiesDialog();
	}

	private boolean wasDifferentFactorSelected()
	{
		FactorCell selectedCell = getCurrentDiagramComponent().getSingleSelectedFactor();
		ORef factorRefForPropertiesDialog = nodePropertiesPanel.getCurrentDiagramFactor().getRef();
		if (selectedCell == null)
			return false;
		
		return !selectedCell.getDiagramFactorRef().equals(factorRefForPropertiesDialog);
	}

	public boolean isRelatedStressSelected()
	{
		FactorCell selectedNode = getCurrentDiagramComponent().getSingleSelectedFactor();
		if(selectedNode == null)
			return false;
		
		if(!Stress.is(selectedNode.getWrappedType()))
			return false;
		
		if(!Target.is(nodePropertiesPanel.getCurrentDiagramFactor().getWrappedType()))
			return false;
		
		ORef stressRef = selectedNode.getWrappedFactorRef();
		Target target = Target.find(getProject(),nodePropertiesPanel.getCurrentDiagramFactor().getWrappedORef());
		return(target.getStressRefs().contains(stressRef));
	}
	
	public boolean isRelatedActivitySelected()
	{
		FactorCell selectedNode = getCurrentDiagramComponent().getSingleSelectedFactor();
		if(selectedNode == null)
			return false;
		
		if(!Task.is(selectedNode.getWrappedType()))
			return false;
		
		if(!Strategy.is(nodePropertiesPanel.getCurrentDiagramFactor().getWrappedType()))
			return false;
		
		ORef activityRef = selectedNode.getWrappedFactorRef();
		Strategy strategy = Strategy.find(getProject(),nodePropertiesPanel.getCurrentDiagramFactor().getWrappedORef());
		return(strategy.getActivityRefs().contains(activityRef));
	}
	
	public String getCurrentMode()
	{
		return mode;
	}

	public boolean isStrategyBrainstormMode()
	{
		return getCurrentMode().equals(DiagramModeQuestion.MODE_STRATEGY_BRAINSTORM);
	}
	
	public static boolean is(UmbrellaView view)
	{
		return view.cardName().equals(getViewName());
	}

	private PropertiesDoer propertiesDoer;
	private String mode;

	private List<DiagramViewListener> diagramViewListenerList = new ArrayList<DiagramViewListener>();

	private ModelessDialogWithClose nodePropertiesDlg;
	private FactorPropertiesPanel nodePropertiesPanel;
	
	private ConceptualModelDiagramPanel conceptualDiagramPanel;
	private ResultsChainDiagramPanel resultsChainPanel;
}
