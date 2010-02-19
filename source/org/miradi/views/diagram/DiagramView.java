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
package org.miradi.views.diagram;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.martus.swing.Utilities;
import org.miradi.actions.ActionActivityMoveDown;
import org.miradi.actions.ActionActivityMoveUp;
import org.miradi.actions.ActionArrangeConceptualModel;
import org.miradi.actions.ActionCloneGoal;
import org.miradi.actions.ActionCloneIndicator;
import org.miradi.actions.ActionCloneObjective;
import org.miradi.actions.ActionCloneStress;
import org.miradi.actions.ActionConfigureLayers;
import org.miradi.actions.ActionCopy;
import org.miradi.actions.ActionCreateActivity;
import org.miradi.actions.ActionCreateBendPoint;
import org.miradi.actions.ActionCreateConceptualModel;
import org.miradi.actions.ActionCreateDiagramMargin;
import org.miradi.actions.ActionCreateGoal;
import org.miradi.actions.ActionCreateIncomingJunction;
import org.miradi.actions.ActionCreateIndicator;
import org.miradi.actions.ActionCreateIndicatorMeasurement;
import org.miradi.actions.ActionCreateKeyEcologicalAttribute;
import org.miradi.actions.ActionCreateKeyEcologicalAttributeIndicator;
import org.miradi.actions.ActionCreateKeyEcologicalAttributeMeasurement;
import org.miradi.actions.ActionCreateNamedTaggedObjectSet;
import org.miradi.actions.ActionCreateObjective;
import org.miradi.actions.ActionCreateOrShowResultsChain;
import org.miradi.actions.ActionCreateOutgoingJunction;
import org.miradi.actions.ActionCreateResultsChain;
import org.miradi.actions.ActionCreateStress;
import org.miradi.actions.ActionCreateStressFromKea;
import org.miradi.actions.ActionCreateSubTarget;
import org.miradi.actions.ActionCreateTaggedObjectSet;
import org.miradi.actions.ActionCut;
import org.miradi.actions.ActionDelete;
import org.miradi.actions.ActionDeleteActivity;
import org.miradi.actions.ActionDeleteBendPoint;
import org.miradi.actions.ActionDeleteConceptualModel;
import org.miradi.actions.ActionDeleteGoal;
import org.miradi.actions.ActionDeleteGroupBox;
import org.miradi.actions.ActionDeleteIndicator;
import org.miradi.actions.ActionDeleteIndicatorMeasurement;
import org.miradi.actions.ActionDeleteKeyEcologicalAttribute;
import org.miradi.actions.ActionDeleteKeyEcologicalAttributeIndicator;
import org.miradi.actions.ActionDeleteKeyEcologicalAttributeMeasurement;
import org.miradi.actions.ActionDeleteObjective;
import org.miradi.actions.ActionDeletePlanningViewTreeNode;
import org.miradi.actions.ActionDeleteResultsChain;
import org.miradi.actions.ActionDeleteStress;
import org.miradi.actions.ActionDeleteSubTarget;
import org.miradi.actions.ActionDeleteTaggedObjectSet;
import org.miradi.actions.ActionDiagramProperties;
import org.miradi.actions.ActionEditTaggedObjectSet;
import org.miradi.actions.ActionGroupBoxAddFactor;
import org.miradi.actions.ActionGroupBoxRemoveFactor;
import org.miradi.actions.ActionHideActivityBubble;
import org.miradi.actions.ActionHideStressBubble;
import org.miradi.actions.ActionInsertContributingFactor;
import org.miradi.actions.ActionInsertDirectThreat;
import org.miradi.actions.ActionInsertDraftStrategy;
import org.miradi.actions.ActionInsertFactorLink;
import org.miradi.actions.ActionInsertGroupBox;
import org.miradi.actions.ActionInsertHumanWelfareTarget;
import org.miradi.actions.ActionInsertIntermediateResult;
import org.miradi.actions.ActionInsertScopeBox;
import org.miradi.actions.ActionInsertStrategy;
import org.miradi.actions.ActionInsertTarget;
import org.miradi.actions.ActionInsertTextBox;
import org.miradi.actions.ActionInsertThreatReductionResult;
import org.miradi.actions.ActionManageFactorTags;
import org.miradi.actions.ActionManageFactorTagsFromMenu;
import org.miradi.actions.ActionManageStresses;
import org.miradi.actions.ActionNudgeDown;
import org.miradi.actions.ActionNudgeLeft;
import org.miradi.actions.ActionNudgeRight;
import org.miradi.actions.ActionNudgeUp;
import org.miradi.actions.ActionPaste;
import org.miradi.actions.ActionPasteFactorContent;
import org.miradi.actions.ActionPasteWithoutLinks;
import org.miradi.actions.ActionProperties;
import org.miradi.actions.ActionRenameConceptualModel;
import org.miradi.actions.ActionRenameResultsChain;
import org.miradi.actions.ActionSelectAll;
import org.miradi.actions.ActionSelectChain;
import org.miradi.actions.ActionShareActivity;
import org.miradi.actions.ActionShowActivityBubble;
import org.miradi.actions.ActionShowConceptualModel;
import org.miradi.actions.ActionShowFullModelMode;
import org.miradi.actions.ActionShowResultsChain;
import org.miradi.actions.ActionShowSelectedChainMode;
import org.miradi.actions.ActionShowStressBubble;
import org.miradi.actions.ActionTreeCreateActivity;
import org.miradi.actions.ActionZoomIn;
import org.miradi.actions.ActionZoomOut;
import org.miradi.actions.ActionZoomToFit;
import org.miradi.actions.EAMAction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.DiagramComponent;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.EAMGraphSelectionModel;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.diagram.cells.LinkCell;
import org.miradi.dialogs.base.ModelessDialogWithClose;
import org.miradi.dialogs.diagram.ConceptualModelDiagramPanel;
import org.miradi.dialogs.diagram.DiagramPanel;
import org.miradi.dialogs.diagram.FactorPropertiesDialog;
import org.miradi.dialogs.diagram.FactorPropertiesPanel;
import org.miradi.dialogs.diagram.ResultsChainDiagramPanel;
import org.miradi.ids.IdList;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.FactorSet;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.objects.GroupBox;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Strategy;
import org.miradi.objects.Stress;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.ViewData;
import org.miradi.project.Project;
import org.miradi.utils.DiagramCorruptionDetector;
import org.miradi.utils.PointList;
import org.miradi.views.TabbedView;
import org.miradi.views.diagram.doers.ArrangeConceptualModelDoer;
import org.miradi.views.diagram.doers.CloneStressDoer;
import org.miradi.views.diagram.doers.CreateIncomingJunctionDoer;
import org.miradi.views.diagram.doers.CreateMarginDoer;
import org.miradi.views.diagram.doers.CreateNamedTaggedObjectSetDoer;
import org.miradi.views.diagram.doers.CreateOutgoingJunctionDoer;
import org.miradi.views.diagram.doers.CreateStressDoer;
import org.miradi.views.diagram.doers.CreateStressFromKeaDoer;
import org.miradi.views.diagram.doers.CreateTaggedObjectSetDoer;
import org.miradi.views.diagram.doers.DeleteGroupBoxDoer;
import org.miradi.views.diagram.doers.DeleteStressDoer;
import org.miradi.views.diagram.doers.DeleteTaggedObjectSetDoer;
import org.miradi.views.diagram.doers.DiagramPropertiesShowDoer;
import org.miradi.views.diagram.doers.EditTaggedObjectSetDoer;
import org.miradi.views.diagram.doers.GroupBoxAddDiagramFactorDoer;
import org.miradi.views.diagram.doers.GroupBoxRemoveDiagramFactorDoer;
import org.miradi.views.diagram.doers.HideActivityBubbleDoer;
import org.miradi.views.diagram.doers.HideStressBubbleDoer;
import org.miradi.views.diagram.doers.InsertGroupBoxDoer;
import org.miradi.views.diagram.doers.InsertHumanWelfareTargetDoer;
import org.miradi.views.diagram.doers.InsertScopeBoxDoer;
import org.miradi.views.diagram.doers.ManageFactorTagsDoer;
import org.miradi.views.diagram.doers.ManageFactorTagsFromMenuDoer;
import org.miradi.views.diagram.doers.PasteFactorContentDoer;
import org.miradi.views.diagram.doers.SelectChainDoer;
import org.miradi.views.diagram.doers.ShowActityBubbleDoer;
import org.miradi.views.diagram.doers.ShowStressBubbleDoer;
import org.miradi.views.diagram.doers.ZoomToFitDoer;
import org.miradi.views.planning.doers.TreeNodeCreateActivityDoer;
import org.miradi.views.planning.doers.TreeNodeDeleteDoer;
import org.miradi.views.targetviability.doers.CreateKeyEcologicalAttributeMeasurementDoer;
import org.miradi.views.targetviability.doers.DeleteKeyEcologicalAttributeMeasurementDoer;
import org.miradi.views.threatmatrix.doers.ManageStressesDoer;
import org.miradi.views.umbrella.DeleteActivityDoer;
import org.miradi.views.umbrella.UmbrellaView;
import org.miradi.views.umbrella.doers.TaskMoveDownDoer;
import org.miradi.views.umbrella.doers.TaskMoveUpDoer;
import org.miradi.views.umbrella.doers.TreeNodeShareActivityDoer;


public class DiagramView extends TabbedView implements CommandExecutedListener
{
	public DiagramView(MainWindow mainWindowToUse) throws Exception
	{
		super(mainWindowToUse);
		mode = ViewData.MODE_DEFAULT;
		
		addDiagramViewDoersToMap();
	}

	
	private void updateToolBar()
	{
		getMainWindow().updateToolBar();
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
	public JToolBar createToolBar()
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
		addDoerToMap(ActionInsertDirectThreat.class, new InsertDirectThreatDoer());
		addDoerToMap(ActionInsertStrategy.class, new InsertStrategyDoer());
		addDoerToMap(ActionInsertDraftStrategy.class, new InsertDraftStrategyDoer());
		addDoerToMap(ActionInsertFactorLink.class, new InsertFactorLinkDoer());
		addDoerToMap(ActionCreateBendPoint.class, new CreateBendPointDoer());
		addDoerToMap(ActionDeleteBendPoint.class, new DeleteBendPointDoer());
		addDoerToMap(ActionRenameResultsChain.class, new RenameResultsChainDoer());
		addDoerToMap(ActionRenameConceptualModel.class, new RenameConceptualModelDoer());
		addDoerToMap(ActionArrangeConceptualModel.class, new ArrangeConceptualModelDoer());
		addDoerToMap(ActionCopy.class, new CopyDoer());
		addDoerToMap(ActionSelectAll.class, new SelectAllDoer());
		addDoerToMap(ActionCut.class, new Cut());
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
		addDoerToMap(ActionShareActivity.class, new TreeNodeShareActivityDoer());
		addDoerToMap(ActionTreeCreateActivity.class, new TreeNodeCreateActivityDoer());
		addDoerToMap(ActionDeletePlanningViewTreeNode.class, new TreeNodeDeleteDoer());

		addDoerToMap(ActionCreateObjective.class, new CreateObjectiveDoer());
		addDoerToMap(ActionCloneObjective.class, new CloneObjectiveDoer());
		addDoerToMap(ActionDeleteObjective.class, new DeleteObjective());
		
		addDoerToMap(ActionCreateIndicator.class, new CreateIndicator());
		addDoerToMap(ActionCloneIndicator.class, new CloneIndicatorDoer());
		addDoerToMap(ActionDeleteIndicator.class, new DeleteIndicator());
		
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
		addDoerToMap(ActionDeleteKeyEcologicalAttributeMeasurement.class, new DeleteKeyEcologicalAttributeMeasurementDoer());
		
		addDoerToMap(ActionCreateIndicatorMeasurement.class, new CreateKeyEcologicalAttributeMeasurementDoer());
		addDoerToMap(ActionDeleteIndicatorMeasurement.class, new DeleteKeyEcologicalAttributeMeasurementDoer());
		
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
		
		addDoerToMap(ActionShowActivityBubble.class, new ShowActityBubbleDoer());
		addDoerToMap(ActionHideActivityBubble.class, new HideActivityBubbleDoer());
		
		addDoerToMap(ActionCreateTaggedObjectSet.class, new CreateTaggedObjectSetDoer());
		addDoerToMap(ActionDeleteTaggedObjectSet.class, new DeleteTaggedObjectSetDoer());
		addDoerToMap(ActionEditTaggedObjectSet.class, new EditTaggedObjectSetDoer());
		addDoerToMap(ActionManageFactorTagsFromMenu.class, new ManageFactorTagsFromMenuDoer());
		addDoerToMap(ActionManageFactorTags.class, new ManageFactorTagsDoer());
		addDoerToMap(ActionCreateNamedTaggedObjectSet.class, new CreateNamedTaggedObjectSetDoer());
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

	private void switchToFullMode() throws Exception
	{
		if (!isStategyBrainstormMode())
			return;
	
		EAMAction actionShowFullModelMode = getActions().get(ActionShowFullModelMode.class);
		actionShowFullModelMode.doAction();
	}

	@Override
	public void prepareForTabSwitch()
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
			ensureDiagramIsSelected(ConceptualModelDiagram.getObjectType(), ViewData.TAG_CURRENT_CONCEPTUAL_MODEL_REF);
			ensureDiagramIsSelected(ResultsChainDiagram.getObjectType(), ViewData.TAG_CURRENT_RESULTS_CHAIN_REF);
			
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
	
	private void updateLegendPanelCheckBoxes()
	{
		getMainWindow().preventActionUpdates();
		try
		{
			getDiagramPanel().getDiagramLegendPanel().resetCheckBoxes();
		}
		finally
		{
			getMainWindow().allowActionUpdates();
			getMainWindow().updateActionsAndStatusBar();
		}
	}

	public void setTabToConceptualModel()
	{
		final int CONCEPTUAL_MODEL_INDEX = 0;
		setTab(CONCEPTUAL_MODEL_INDEX);
	}
	
	public void setDiagramTab(ORef resultsChainRef)
	{
		setTab(getTabIndex(resultsChainRef));
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
		if (newMode.equals(ViewData.MODE_STRATEGY_BRAINSTORM))
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
		
		EAMGraphSelectionModel selectionModel = (EAMGraphSelectionModel) diagramComponent.getSelectionModel();
		// TODO: Find a way to avoid the need to test for null here
		if(selectionModel != null)
			selectionModel.clearSelection();
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
		visibleFactorORefs.addAll(getRelatedDraftInterventions(diagramModel, visibleFactorORefs));
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
	
	private static ORefList getRelatedDraftInterventions(DiagramModel diagramModel, ORefList factorORefs) throws Exception
	{
		ORefList draftsToAdd = new ORefList();
		
		for(int i = 0; i < factorORefs.size(); ++i)
		{
			DiagramFactor diagramFactor = diagramModel.getFactorCellByWrappedRef(factorORefs.get(i)).getDiagramFactor();
			FactorSet possibleDraftStrategies = diagramModel.getDirectlyLinkedUpstreamNodes(diagramFactor);
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
			if (event.isSetDataCommandWithThisTypeAndTag(DiagramFactor.getObjectType(), DiagramFactor.TAG_TEXT_BOX_Z_ORDER_CODE))
				handleTextBoxZOrderChanged(setCommand.getObjectORef());
			
			if(event.isSetDataCommandWithThisTypeAndTag(GroupBox.getObjectType(), GroupBox.TAG_LABEL))
				getDiagramModel().updateGroupBoxCells();
			
			forceDiagramComponentRepaint();
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}

	private void handleTextBoxZOrderChanged(ORef diagramFactorRef) throws Exception
	{
		getDiagramModel().sortLayers();
		
		FactorCell factorCell = getDiagramModel().getFactorCellByRef(diagramFactorRef);
		getDiagramModel().updateCell(factorCell);
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
					
		DiagramFactor diagramFactorRefForPropertiesDialog = nodePropertiesPanel.getCurrentDiagramFactor();
		if (diagramFactorRefForPropertiesDialog == null)
			return true;
		
		IdList currentList = new IdList(DiagramFactor.getObjectType(), setCommand.getDataValue());
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
		ORefList groupBoxLinkReferrerRefs = diagramLink.findObjectsThatReferToUs(DiagramLink.getObjectType());
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

	private void setModeIfRelevant(CommandSetObjectData cmd, String newMode)
	{
		String fieldTag = cmd.getFieldTag();
		try
		{
			ViewData ourViewData = getViewData();
			if(cmd.getObjectType() != ourViewData.getType())
				return;
			if(cmd.getObjectId() != ourViewData.getId())
				return;
			boolean modeChange = fieldTag.equals(ViewData.TAG_CURRENT_MODE);
			boolean cmChange = fieldTag.equals(ViewData.TAG_CURRENT_CONCEPTUAL_MODEL_REF);
			boolean rcChange = fieldTag.equals(ViewData.TAG_CURRENT_RESULTS_CHAIN_REF);
			if(modeChange || cmChange || rcChange)
			{
				setMode(newMode);
			}
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Unknown error prevented this operation");
		}
	}
	
	private void refreshIfNeeded(DiagramComponent diagramComponent, CommandSetObjectData cmd)
	{
		// may have added or removed a stress, modified an Annotation short label, etc.
		diagramComponent.repaint(diagramComponent.getBounds());
	}
	
	@Override
	public void showFloatingPropertiesDialog(ModelessDialogWithClose newDialog)
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
		newPropertiesPanel.setCurrentDiagramFactor(getCurrentDiagramComponent(), node);
		newPropertiesPanel.selectTab(startingTabIdentifier);
		newPropertiesDialog.pack();
		newPropertiesDialog.updatePreferredSize();
		Utilities.centerDlg(newPropertiesDialog);
		
		getCurrentDiagramComponent().selectFactor(node.getWrappedORef());
		nodePropertiesPanel = newPropertiesPanel;
		nodePropertiesDlg = newPropertiesDialog;

		nodePropertiesPanel.becomeActive();
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

	public boolean isStategyBrainstormMode()
	{
		return getCurrentMode().equals(ViewData.MODE_STRATEGY_BRAINSTORM);
	}
	
	public static boolean is(UmbrellaView view)
	{
		return view.cardName().equals(getViewName());
	}

	private PropertiesDoer propertiesDoer;
	private String mode;
	
	private ModelessDialogWithClose nodePropertiesDlg;
	private FactorPropertiesPanel nodePropertiesPanel;
	
	private ConceptualModelDiagramPanel conceptualDiagramPanel;
	private ResultsChainDiagramPanel resultsChainPanel;
}
