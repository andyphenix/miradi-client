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
package org.miradi.dialogs.diagram;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.martus.swing.UiLabel;
import org.miradi.diagram.DiagramComponent;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.activity.ActivityListManagementPanel;
import org.miradi.dialogs.base.DisposablePanelWithDescription;
import org.miradi.dialogs.base.ModelessDialogPanel;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.base.ObjectManagementPanel;
import org.miradi.dialogs.fieldComponents.ChoiceItemWithXmlTextRendererComboBox;
import org.miradi.dialogs.fieldComponents.PanelTabbedPane;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.dialogs.goal.GoalListManagementPanel;
import org.miradi.dialogs.goal.GoalListTablePanel;
import org.miradi.dialogs.objective.ObjectiveListManagementPanel;
import org.miradi.dialogs.objective.ObjectiveListTablePanel;
import org.miradi.dialogs.stress.StressListManagementPanel;
import org.miradi.dialogs.subTarget.SubTargetManagementPanel;
import org.miradi.dialogs.viability.AbstractViabilityManagementPanel;
import org.miradi.dialogs.viability.IndicatorViabilityTreeManagementPanel;
import org.miradi.dialogs.viability.TargetKeaViabilityTreeManagementPanel;
import org.miradi.icons.IconManager;
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
import org.miradi.objects.ThreatReductionResult;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.DirectThreatPoolQuestion;
import org.miradi.questions.FilteredDiagramFactorsQuestion;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.ViabilityModeQuestion;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.TargetSchema;

public class FactorPropertiesPanel extends ModelessDialogPanel implements CommandExecutedListener
{
	public FactorPropertiesPanel(MainWindow parent,DiagramComponent diagramToUse)
	{
		mainWindow = parent;
		diagram = diagramToUse;
		currentTabIndex = 0;
		tabs = new FactorPropertiesTabbedPane();

		setBackground(AppPreferences.getDarkPanelBackgroundColor());
		setBorder(BorderFactory.createEmptyBorder(0,3,3,3));
		tabChangeHandler = new TabChangeHandler();
		tabs.addChangeListener(tabChangeHandler);
	}
	
	//TODO: can put a loop of disposable panels and move the code to DisposablePanel passing in list
	@Override
	public void dispose()
	{
		disposeTabs();
		
		super.dispose();
	}

	private void disposeTabs()
	{
		disposePanel(detailsTab);
		detailsTab = null;

		disposePanel(indicatorsTab);
		indicatorsTab = null;

		disposePanel(goalsTab);
		goalsTab = null;

		disposePanel(objectivesTab);
		objectivesTab = null;

		disposePanel(activitiesTab);
		activitiesTab = null;

		disposePanel(viabilityTab);
		viabilityTab = null;

		disposePanel(simpleViabilityTab);
		simpleViabilityTab = null;

		disposePanel(stressTab);
		stressTab = null;

		disposePanel(subTargetTab);
		subTargetTab = null;

		disposePanel(grid);
		grid = null;
	}
	
	@Override
	public void becomeInactive()
	{
		getProject().removeCommandExecutedListener(this);
		deactivateCurrentTab();
		if(grid != null)
			grid.becomeInactive();
		
		super.becomeInactive();
	}
	
	@Override
	public void becomeActive()
	{
		super.becomeActive();
		
		if (grid != null)
			grid.becomeActive();
		
		activateCurrentTab();
		getProject().addCommandExecutedListener(this);
	}
	
	private FactorPropertiesDialog getFactorPropertiesDialog()
	{
		FactorPropertiesDialog dialog = (FactorPropertiesDialog)getTopLevelAncestor();
		return dialog;
	}

	private Component rebuildTabs(DiagramFactor diagramFactor) throws Exception
	{
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

		boolean isKeaViabilityMode = (AbstractTarget.isAbstractTarget(factor) && factor.getData(AbstractTarget.TAG_VIABILITY_MODE).equals(ViabilityModeQuestion.TNC_STYLE_CODE));
		
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

		if (factor.canDirectlyOwnIndicators() && !isKeaViabilityMode && !AbstractTarget.isAbstractTarget(factor))
		{
			indicatorsTab = IndicatorViabilityTreeManagementPanel.createManagementPanel(mainWindow, getCurrentDiagramFactor().getWrappedORef());
			addTab(indicatorsTab);
		}
		
		if ( AbstractTarget.isAbstractTarget(factor) && !isKeaViabilityMode)
		{
			simpleViabilityTab = new SimpleViabilityPanel(mainWindow, getCurrentDiagramFactor().getWrappedORef());
			tabs.addTab(simpleViabilityTab.getPanelDescription(), simpleViabilityTab.getIcon(), simpleViabilityTab);
		}
		
		if(isKeaViabilityMode)
		{
			viabilityTab = TargetKeaViabilityTreeManagementPanel.createManagementPanel(mainWindow, getCurrentDiagramFactor().getWrappedORef());
			addTab(viabilityTab);
		}

		if (factor.isTarget())
		{
			stressTab = StressListManagementPanel.createStressManagementPanelWithVisibilityPanel(mainWindow, getCurrentDiagramFactor().getWrappedORef());
			addTab(stressTab);
		}
			
		if (AbstractTarget.isAbstractTarget(factor))
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

	private void activateCurrentTab()
	{
		DisposablePanelWithDescription panel = getCastedCurrentTab();		
		if(panel != null)
			panel.becomeActive();
	}

	private void deactivateCurrentTab()
	{
		DisposablePanelWithDescription panel = getCastedCurrentTab();
		if(panel != null)
			panel.becomeInactive();
	}

	private DisposablePanelWithDescription getCastedCurrentTab()
	{
		if (tabs.getTabCount() > 0 && currentTabIndex < tabs.getTabCount())
			return (DisposablePanelWithDescription)tabs.getComponent(currentTabIndex);
		
		return null;
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

	public void setCurrentDiagramFactor(DiagramFactor diagramFactor)
	{
		rebuildPanelForDiagramFactor(diagramFactor);
		detailsTab.setFocusOnFirstField();
	}
	
	public void rebuildPanelForDiagramFactor(DiagramFactor diagramFactor)
	{
		currentDiagramFactor = diagramFactor;

		rebuildPanel();
		becomeActive();
	}

	private void setFocusOnFactorChangeComboBox()
	{
		currentFactorChangerComboBox.requestFocusInWindow();
	}

	private void rebuildPanel()
	{
		ignoreTabChanges = true;
		try
		{
			removeAll();
			rebuildTabs(currentDiagramFactor);

			add(createLabelBar(currentDiagramFactor),	BorderLayout.BEFORE_FIRST_LINE);
			add(tabs, BorderLayout.CENTER);
			
			getFactorPropertiesDialog().pack();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Error reading factor information:" + e.getMessage());
		}
		finally
		{
			ignoreTabChanges = false;
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

	private Component createLabelBar(DiagramFactor diagramFactor) throws Exception
	{
		ORef factorORef = diagramFactor.getWrappedORef();
		Factor factor = (Factor) getProject().findObject(factorORef);
		grid = new FactorInputPanel(getProject(), factorORef);
		
		grid.addTopAlignedLabel(createFactorTypeLabel(factor));
		FilteredDiagramFactorsQuestion currentDiagramFactorsQuestion = new FilteredDiagramFactorsQuestion(getDiagramObject());
		currentFactorChangerComboBox = new CurrentFactorChangerComboBox(currentDiagramFactorsQuestion);
		grid.addFieldComponent(currentFactorChangerComboBox);
		
		if (AbstractTarget.isAbstractTarget(factor))
		{
			grid.addField(createTargetStatusField(factor));
			ObjectDataInputField modeField = grid.createChoiceField(factor.getType(), AbstractTarget.TAG_VIABILITY_MODE, new ViabilityModeQuestion());
			grid.addFieldWithCustomLabel(modeField, EAM.text("Viability Analysis Mode"));
		}
		
		if (factor.isThreatReductionResult())
		{
			grid.addField(grid.createChoiceField(ObjectType.THREAT_REDUCTION_RESULT, ThreatReductionResult.TAG_RELATED_DIRECT_THREAT_REF, new DirectThreatPoolQuestion(getProject())));
		}
		
		grid.setObjectRef(factorORef);
		return grid;
	}
	
	private UiLabel createFactorTypeLabel(Factor factor) throws Exception
	{
		Icon factorIcon = IconManager.getImage(factor);
		String factorLabel = EAM.fieldLabel(factor.getType(), factor.getTypeName());
		return new PanelTitleLabel(factorLabel, factorIcon, UiLabel.LEADING);
	}
	
	private ObjectDataInputField createTargetStatusField(Factor factor)
	{
		return grid.createReadOnlyChoiceField(factor.getType(), AbstractTarget.PSEUDO_TAG_TARGET_VIABILITY, new StatusQuestion());
	}
	
	class FactorInputPanel extends ObjectDataInputPanel
	{

		public FactorInputPanel(Project projectToUse, ORef oRefToUse)
		{
			super(projectToUse, oRefToUse);
		}

		@Override
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

	@Override
	public BaseObject getObject()
	{
		return currentDiagramFactor;
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Factor Properties");
	}

	public void commandExecuted(CommandExecutedEvent event)
	{
		//TODO: Refactor entire tab add remove mechanism
		if (shouldRebuildPanel(event))
		{
			becomeInactive();
			rebuildPanel();
			becomeActive();
		}
		
		if (event.isSetDataCommandWithThisTag(Factor.TAG_LABEL)  || event.isSetDataCommandWithThisTag(Factor.TAG_SHORT_LABEL))
			rebuildFactorChangerComboBox();
	}

	private boolean shouldRebuildPanel(CommandExecutedEvent event)
	{
		if (event.isSetDataCommandWithThisTypeAndTag(CauseSchema.getObjectType(), Cause.TAG_IS_DIRECT_THREAT))
			return true;
		
		if (event.isSetDataCommandWithThisTypeAndTag(TargetSchema.getObjectType(), AbstractTarget.TAG_VIABILITY_MODE))
			return true;
		
		return event.isSetDataCommandWithThisTypeAndTag(HumanWelfareTargetSchema.getObjectType(), AbstractTarget.TAG_VIABILITY_MODE);
	}
	
	private class CurrentFactorChangerComboBox extends ChoiceItemWithXmlTextRendererComboBox implements ItemListener 
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
		
			becomeInactive();
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
	
	private class FactorPropertiesTabbedPane extends PanelTabbedPane
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
	
	private class TabChangeHandler implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			int newTab = tabs.getSelectedIndex();
			if (!ignoreTabChanges)
			{
				 deactivateCurrentTab();
				 currentTabIndex = newTab;
				 activateCurrentTab();
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
	private int currentTabIndex;
	private boolean ignoreTabChanges;
	private FactorSummaryScrollablePanel detailsTab;
	private ObjectiveListManagementPanel objectivesTab;
	private AbstractViabilityManagementPanel indicatorsTab;
	private GoalListManagementPanel goalsTab;
	private AbstractViabilityManagementPanel viabilityTab;
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
