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
package org.miradi.dialogs.diagram;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.martus.swing.UiLabel;
import org.miradi.diagram.DiagramComponent;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.activity.ActivityListManagementPanel;
import org.miradi.dialogs.base.DisposablePanel;
import org.miradi.dialogs.base.DisposablePanelWithDescription;
import org.miradi.dialogs.base.ModelessDialogPanel;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.base.ObjectManagementPanel;
import org.miradi.dialogs.fieldComponents.ChoiceItemComboBox;
import org.miradi.dialogs.fieldComponents.PanelTabbedPane;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.dialogs.goal.GoalListManagementPanel;
import org.miradi.dialogs.goal.GoalListTablePanel;
import org.miradi.dialogs.objective.ObjectiveListManagementPanel;
import org.miradi.dialogs.objective.ObjectiveListTablePanel;
import org.miradi.dialogs.stress.StressListManagementPanel;
import org.miradi.dialogs.subTarget.SubTargetManagementPanel;
import org.miradi.dialogs.viability.FactorPropertiesViabilityTreeManagementPanel;
import org.miradi.dialogs.viability.TargetPropertiesKeaViabilityTreeManagementPanel;
import org.miradi.dialogs.viability.TargetViabilityManagementPanel;
import org.miradi.icons.ContributingFactorIcon;
import org.miradi.icons.DirectThreatIcon;
import org.miradi.icons.GroupBoxIcon;
import org.miradi.icons.HumanWelfareTargetIcon;
import org.miradi.icons.IntermediateResultIcon;
import org.miradi.icons.ScopeBoxIcon;
import org.miradi.icons.StrategyIcon;
import org.miradi.icons.TargetIcon;
import org.miradi.icons.TextBoxIcon;
import org.miradi.icons.ThreatReductionResultIcon;
import org.miradi.main.AppPreferences;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.objects.GroupBox;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.IntermediateResult;
import org.miradi.objects.ScopeBox;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.objects.TextBox;
import org.miradi.objects.ThreatReductionResult;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.DirectThreatQuestion;
import org.miradi.questions.FilteredDiagramFactorsQuestion;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.ViabilityModeQuestion;

public class FactorPropertiesPanel extends ModelessDialogPanel implements CommandExecutedListener
{
	public FactorPropertiesPanel(MainWindow parent,DiagramComponent diagramToUse)
	{
		mainWindow = parent;
		diagram = diagramToUse;
		tabs = new FactorPropertiesTabbedPane();

		setBackground(AppPreferences.getDarkPanelBackgroundColor());
		setBorder(BorderFactory.createEmptyBorder(0,3,3,3));
		tabChangeHandler = new TabChangeHandler();
		tabs.addChangeListener(tabChangeHandler);

		getProject().addCommandExecutedListener(this);
	}
	
	//TODO: can put a loop of disposable panels and move the code to DIsposablePanel passing in list
	public void dispose()
	{
		disposeTabs();
		getProject().removeCommandExecutedListener(this);
		super.dispose();
	}

	private void disposeTabs()
	{
		becomeInactive();

		dispose(detailsTab);
		dispose(indicatorsTab);
		dispose(goalsTab);
		dispose(objectivesTab);
		dispose(activitiesTab);
		dispose(viabilityTab);
		dispose(simpleViabilityTab);
		dispose(stressTab);
		dispose(subTargetTab);
		dispose(grid);
	}
	
	private void dispose(DisposablePanel panelToDispose)
	{
		if (panelToDispose != null)
		{
			panelToDispose.dispose();
			panelToDispose = null;
		}
	}
	
	@Override
	public void becomeInactive()
	{
		deactivateCurrentTab();
		if(grid != null)
			grid.becomeInactive();
		super.becomeInactive();
	}
	
	class FactorPropertiesTabbedPane extends PanelTabbedPane
	{
		@Override
		public void setSelectedIndex(int index)
		{
			super.setSelectedIndex(index);
			ensureButtonsInProperState();
		}

		private void ensureButtonsInProperState()
		{
			// NOTE: This is a hack, but not a horrible one.
			// We are not sure why switching KEA mode would not correctly enable buttons
			// on the Viability tab, so the fix is to make sure that whenever the user 
			// switches tabs, the buttons on that tab will be correctly enabled
			mainWindow.updateActionStates();
			
			// NOTE: This is a weaker hack. We want to update the Directions button
			// in the FactorPropertiesDialog, and there is no easy way to get from here
			// (the only place that knows the update is needed) to there (where the 
			// update can be done
			FactorPropertiesDialog dialog = getFactorPropertiesDialog();
			if(dialog != null)
				dialog.updateDirectionsEnabledState();
		}

	}
	
	private FactorPropertiesDialog getFactorPropertiesDialog()
	{
		FactorPropertiesDialog dialog = (FactorPropertiesDialog)getTopLevelAncestor();
		return dialog;
	}

	private Component rebuildTabs(DiagramFactor diagramFactor) throws Exception
	{
		deactivateAllTabs();
		tabs.setFocusable(false);
		tabs.removeAll();
		disposeTabs();
		detailsTab = new FactorSummaryScrollablePanel(mainWindow, diagramFactor);
		
		tabs.addTab(detailsTab.getPanelDescription(), detailsTab.getIcon(), detailsTab);
		Factor factor = (Factor) getProject().findObject(diagramFactor.getWrappedORef());
		
		ORefList selectedHierarchy = new ORefList();
		selectedHierarchy.add(factor.getRef());
		selectedHierarchy.add(diagramFactor.getRef());
		selectedHierarchy.add(getDiagramObject().getRef());

		boolean isKeaViabilityMode = (AbstractTarget.isTarget(factor) && factor.getData(AbstractTarget.TAG_VIABILITY_MODE).equals(ViabilityModeQuestion.TNC_STYLE_CODE));
		
		if(factor.canHaveGoal())
		{
			GoalListTablePanel goalListPanel = new GoalListTablePanel(mainWindow, getCurrentDiagramFactor().getWrappedORef());
			goalsTab = new GoalListManagementPanel(mainWindow, goalListPanel, getCurrentDiagramFactor().getWrappedORef(), mainWindow.getActions());
			addTab(goalsTab);
		}
		
		if(factor.canHaveObjectives())
		{
			ObjectiveListTablePanel objectListPanel = new ObjectiveListTablePanel(mainWindow, getCurrentDiagramFactor().getWrappedORef());
			objectivesTab = new ObjectiveListManagementPanel(mainWindow, getCurrentDiagramFactor().getWrappedORef(), mainWindow.getActions(), objectListPanel);
			addTab(objectivesTab);
		}
		
		if(factor.isStrategy())
		{
			activitiesTab = ActivityListManagementPanel.create(mainWindow, selectedHierarchy);
			addTab(activitiesTab);
		}

		if (factor.canHaveIndicators() && !isKeaViabilityMode && !AbstractTarget.isTarget(factor))
		{
			indicatorsTab = new FactorPropertiesViabilityTreeManagementPanel(mainWindow, getCurrentDiagramFactor().getWrappedORef(), mainWindow.getActions());
			addTab(indicatorsTab);
		}
		
		if ( AbstractTarget.isTarget(factor) && !isKeaViabilityMode)
		{
			simpleViabilityTab = new SimpleViabilityPanel(mainWindow, getCurrentDiagramFactor().getWrappedORef());
			tabs.addTab(simpleViabilityTab.getPanelDescription(), simpleViabilityTab.getIcon(), simpleViabilityTab);
		}
		
		if(isKeaViabilityMode)
		{
			viabilityTab = new TargetPropertiesKeaViabilityTreeManagementPanel(mainWindow, mainWindow, getCurrentDiagramFactor().getWrappedORef());
			addTab(viabilityTab);
		}

		if (factor.isTarget())
		{
			stressTab = new StressListManagementPanel(mainWindow, getCurrentDiagramFactor().getWrappedORef(), mainWindow.getActions());
			addTab(stressTab);
		}
			
		if (AbstractTarget.isTarget(factor))
		{
			subTargetTab = new SubTargetManagementPanel(mainWindow, getCurrentDiagramFactor().getWrappedORef(), mainWindow.getActions());
			addTab(subTargetTab);
		}
		
		return tabs;
	}
	
	private void addTab(ObjectManagementPanel managementPanel)
	{
		tabs.addTab(managementPanel.getPanelDescription(), managementPanel.getIcon(), managementPanel);
	}

	class TabChangeHandler implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			deactivateAllTabs();
			activateCurrentTab();
		}

	}

	private void activateCurrentTab()
	{
		DisposablePanelWithDescription panel = (DisposablePanelWithDescription)tabs.getSelectedComponent();
		if(panel != null)
			panel.becomeActive();
	}

	
	private void deactivateCurrentTab()
	{
		DisposablePanelWithDescription panel = (DisposablePanelWithDescription)tabs.getSelectedComponent();
		deactivateTab(panel);
	}

	private void deactivateTab(DisposablePanelWithDescription panel)
	{
		if(panel != null)
			panel.becomeInactive();
	}

	private void deactivateAllTabs()
	{
		deactivateTab(detailsTab);
		deactivateTab(indicatorsTab);
		deactivateTab(goalsTab);
		deactivateTab(objectivesTab);
		deactivateTab(activitiesTab);
		deactivateTab(viabilityTab);
		deactivateTab(simpleViabilityTab);
		deactivateTab(stressTab);
		deactivateTab(subTargetTab);
	}
	
	public void selectTab(int tabIdentifier)
	{
		switch(tabIdentifier)
		{
			case TAB_OBJECTIVES:
				tabs.setSelectedComponent(objectivesTab);
				break;
			case TAB_INDICATORS:
			{
				if (indicatorsTab!= null)
					tabs.setSelectedComponent(indicatorsTab);
				else
					tabs.setSelectedComponent(simpleViabilityTab);
				break;
			}
			case TAB_GOALS:
				tabs.setSelectedComponent(goalsTab);
				break;
			case TAB_VIABILITY:
				tabs.setSelectedComponent(viabilityTab);
				break;
			case TAB_STRESS:
				tabs.setSelectedComponent(stressTab);
			case TAB_SUB_TARGET:
				tabs.setSelectedComponent(subTargetTab);
			case TAB_SIMPLE_VIABILITY:
				tabs.setSelectedComponent(simpleViabilityTab);
			default:
				tabs.setSelectedComponent(detailsTab);
				break;
		}
	}

	public void updateAllSplitterLocations()
	{
		if (indicatorsTab != null)
			indicatorsTab.updateSplitterLocation();
		
		if (objectivesTab != null)
			objectivesTab.updateSplitterLocation();
		
		if (goalsTab != null)
			goalsTab.updateSplitterLocation();
		
		if (activitiesTab != null)
			activitiesTab.updateSplitterLocation();
		
		if (viabilityTab != null)
			viabilityTab.updateSplitterLocation();
		
		if (stressTab != null)
			stressTab.updateSplitterLocation();
		
		if (subTargetTab != null)
			subTargetTab.updateSplitterLocation();
		
		if (simpleViabilityTab != null)
			simpleViabilityTab.updateSplitterLocation();
	}
	
	public void setCurrentDiagramFactor(DiagramComponent diagram, DiagramFactor diagramFactor)
	{
		rebuildPanelForDiagramFactor(diagramFactor);
		detailsTab.setFocusOnFirstField();
	}
	
	public void rebuildPanelForDiagramFactor(DiagramFactor diagramFactor)
	{
		currentDiagramFactor = diagramFactor;

		rebuildPanel();
	}

	private void setFocusOnFactorChangeComboBox()
	{
		currentFactorChangerComboBox.requestFocusInWindow();
	}

	private void rebuildPanel()
	{
		try
		{
			removeAll();
			rebuildTabs(currentDiagramFactor);

			add(createLabelBar(currentDiagramFactor),	BorderLayout.BEFORE_FIRST_LINE);
			add(tabs, BorderLayout.CENTER);
			
			getFactorPropertiesDialog().pack();
			becomeActive();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Error reading factor information:" + e.getMessage());
		}
	}

	public DiagramFactor getCurrentDiagramFactor()
	{
		return currentDiagramFactor;
	}

	private void rebuildFactorChangerComboBox()
	{
		currentFactorChangerComboBox.rebuild();
	}

	private Component createLabelBar(DiagramFactor diagramFactor)
	{
		ORef factorORef = diagramFactor.getWrappedORef();
		Factor factor = (Factor) getProject().findObject(factorORef);
		grid = new FactorInputPanel(getProject(), factorORef);
		
		grid.addLabel(createFactorTypeLabel(factor));
		FilteredDiagramFactorsQuestion currentDiagramFactorsQuestion = new FilteredDiagramFactorsQuestion(getDiagramObject());
		currentFactorChangerComboBox = new CurrentFactorChangerComboBox(currentDiagramFactorsQuestion);
		grid.addFieldComponent(currentFactorChangerComboBox);
		
		if (AbstractTarget.isTarget(factor))
		{
			grid.addField(createTargetStatusField(factor));
			PanelTitleLabel modeLabel = new PanelTitleLabel(EAM.text("Viability Analysis Mode"));
			ObjectDataInputField modeField = grid.createChoiceField(ObjectType.TARGET, Target.TAG_VIABILITY_MODE, new ViabilityModeQuestion());
			grid.addFieldWithCustomLabel(modeField, modeLabel);
		}
		
		if (factor.isThreatReductionResult())
		{
			grid.addField(grid.createReadOnlyChoiceField(ObjectType.THREAT_REDUCTION_RESULT, ThreatReductionResult.TAG_RELATED_DIRECT_THREAT_REF, new DirectThreatQuestion(mainWindow.getProject())));
		}
		
		grid.setObjectRef(factorORef);
		return grid;
	}
	
	//FIXME medium: there is much duplicated code between this method and FactorType.getTypeLabel(factor)
	private UiLabel createFactorTypeLabel(Factor factor)
	{
		if(factor.isDirectThreat())
			return new PanelTitleLabel(EAM.fieldLabel(Cause.getObjectType(), Cause.OBJECT_NAME_THREAT), new DirectThreatIcon(), UiLabel.LEADING);
		
		if (factor.isContributingFactor())
			return new PanelTitleLabel(EAM.fieldLabel(Cause.getObjectType(), Cause.OBJECT_NAME_CONTRIBUTING_FACTOR), new ContributingFactorIcon(), UiLabel.LEADING);
		
		if (factor.isStrategy())
			return new PanelTitleLabel(EAM.fieldLabel(Strategy.getObjectType(), Strategy.OBJECT_NAME), new StrategyIcon(), UiLabel.LEADING);
		
		if (factor.isTarget())
			return new PanelTitleLabel(EAM.fieldLabel(Target.getObjectType(), Target.OBJECT_NAME), new TargetIcon(), UiLabel.LEADING);
		
		if (factor.isHumanWelfareTarget())
			return  new PanelTitleLabel(EAM.fieldLabel(HumanWelfareTarget.getObjectType(), HumanWelfareTarget.OBJECT_NAME), new HumanWelfareTargetIcon(), UiLabel.LEADING);
		
		if (factor.isIntermediateResult())
			return new PanelTitleLabel(EAM.fieldLabel(IntermediateResult.getObjectType(), IntermediateResult.OBJECT_NAME), new IntermediateResultIcon(), UiLabel.LEADING);

		if (factor.isThreatReductionResult())
			return new PanelTitleLabel(EAM.fieldLabel(ThreatReductionResult.getObjectType(), ThreatReductionResult.OBJECT_NAME), new ThreatReductionResultIcon(), UiLabel.LEADING);
		
		if (factor.isTextBox())
			return new PanelTitleLabel(EAM.fieldLabel(TextBox.getObjectType(), TextBox.OBJECT_NAME), new TextBoxIcon(), UiLabel.LEADING);
		
		if (factor.isScopeBox())
			return new PanelTitleLabel(EAM.fieldLabel(ScopeBox.getObjectType(), ScopeBox.OBJECT_NAME), new ScopeBoxIcon(), UiLabel.LEADING);

		if (factor.isGroupBox())
			return new PanelTitleLabel(EAM.fieldLabel(GroupBox.getObjectType(), GroupBox.OBJECT_NAME), new GroupBoxIcon(), UiLabel.LEADING);
		
		throw new RuntimeException("Unknown factor type");
	}
	
	private ObjectDataInputField createTargetStatusField(Factor factor)
	{
		ObjectDataInputField field =  grid.createReadOnlyChoiceField(ObjectType.TARGET, Target.PSEUDO_TAG_TARGET_VIABILITY, new StatusQuestion());
		return field;
	}
	
	class FactorInputPanel extends ObjectDataInputPanel
	{

		public FactorInputPanel(Project projectToUse, ORef oRefToUse)
		{
			super(projectToUse, oRefToUse);
		}

		public String getPanelDescription()
		{
			return "";
		}
	}

	private Project getProject()
	{
		return getDiagram().getProject();
	}

	private DiagramComponent getDiagram()
	{
		return diagram;
	}
	
	private DiagramObject getDiagramObject()
	{
		return getDiagram().getDiagramObject();
	}

	public BaseObject getObject()
	{
		return currentDiagramFactor;
	}

	public String getPanelDescription()
	{
		return EAM.text("Factor Properties");
	}

	public void commandExecuted(CommandExecutedEvent event)
	{
		//TODO: refactor entire tab add remove mechisisim
		if (shouldRebuildPanel(event))
			rebuildPanel();
		
		if (event.isSetDataCommandWithThisTag(Factor.TAG_LABEL)  || event.isSetDataCommandWithThisTag(Factor.TAG_SHORT_LABEL))
			rebuildFactorChangerComboBox();
	}

	private boolean shouldRebuildPanel(CommandExecutedEvent event)
	{
		if (event.isSetDataCommandWithThisTypeAndTag(Cause.getObjectType(), Cause.TAG_IS_DIRECT_THREAT))
			return true;
		
		return event.isSetDataCommandWithThisTypeAndTag(ObjectType.TARGET, Target.TAG_VIABILITY_MODE);
	}
	
	class CurrentFactorChangerComboBox extends ChoiceItemComboBox implements ItemListener 
	{
		public CurrentFactorChangerComboBox(ChoiceQuestion question)
		{
			super(question);

			addItemListener(this);
			setSelectedItemToMatchCurrentFactor(question);
		}

		public void rebuild()
		{
			FilteredDiagramFactorsQuestion currentDiagramFactorsQuestion = new FilteredDiagramFactorsQuestion(getDiagramObject());
			reloadComboBox(currentDiagramFactorsQuestion);
			setSelectedItemToMatchCurrentFactor(currentDiagramFactorsQuestion);
		}
		
		private void setSelectedItemToMatchCurrentFactor(ChoiceQuestion question)
		{
			ORef wrappedRef = getCurrentDiagramFactor().getWrappedORef();
			ChoiceItem choiceItemToSelect = question.findChoiceByCode(wrappedRef.toString());
			setSelectedItem(choiceItemToSelect);
		}
		
		public void itemStateChanged(ItemEvent e)
		{
			int selectedIndex = getSelectedIndex();
			if (selectedIndex < 0)
				return;
			
			ChoiceItem selectedChoiceItem = (ChoiceItem) getSelectedItem();
			ORef selectedFactorRef = ORef.createFromString(selectedChoiceItem.getCode());
			ORef currentDiagramFactorRef = getCurrentDiagramFactor().getRef();
			
			DiagramFactor diagramFactorToSelect = getDiagramObject().getDiagramFactor(selectedFactorRef);
			if (currentDiagramFactorRef.equals(diagramFactorToSelect.getRef()))
				return;
		
			rebuildPanelForDiagramFactor(diagramFactorToSelect);
			setFocusOnFactorChangeComboBox();
			selectNewlyChosenDiagramFactor(diagramFactorToSelect.getRef());
		}

		private void selectNewlyChosenDiagramFactor(ORef diagramFactorToSelectRef)
		{
			try
			{
				DiagramModel diagramModel = getDiagram().getDiagramModel();
				FactorCell factorCell = diagramModel.getFactorCellByRef(diagramFactorToSelectRef);
				getDiagram().clearSelection();
				getDiagram().addSelectionCell(factorCell);
			}
			catch(Exception e)
			{
				EAM.logException(e);
			}	
		}
	}

	public static final int TAB_DETAILS = 0;
	public static final int TAB_INDICATORS = 1;
	public static final int TAB_OBJECTIVES = 2;
	public static final int TAB_GOALS = 3;
	public static final int TAB_VIABILITY = 4;
	public static final int TAB_STRESS = 5;
	public static final int TAB_SUB_TARGET = 6;
	public static final int TAB_SIMPLE_VIABILITY = 7;

	protected JTabbedPane tabs;
	private FactorSummaryScrollablePanel detailsTab;
	private ObjectiveListManagementPanel objectivesTab;
	private TargetViabilityManagementPanel indicatorsTab;
	private GoalListManagementPanel goalsTab;
	private TargetViabilityManagementPanel viabilityTab;
	private SimpleViabilityPanel simpleViabilityTab;
	private StressListManagementPanel stressTab;
	private ActivityListManagementPanel activitiesTab;
	private SubTargetManagementPanel subTargetTab;
	private MainWindow mainWindow;
	private DiagramComponent diagram;
	private DiagramFactor currentDiagramFactor;
	private FactorInputPanel grid;
	private CurrentFactorChangerComboBox currentFactorChangerComboBox;
	
	private TabChangeHandler tabChangeHandler;
}
