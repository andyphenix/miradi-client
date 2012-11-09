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
package org.miradi.views.umbrella;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.martus.swing.UiLabel;
import org.martus.swing.Utilities;
import org.miradi.actions.ActionAbout;
import org.miradi.actions.ActionAboutBenetech;
import org.miradi.actions.ActionAboutCMP;
import org.miradi.actions.ActionAssignResource;
import org.miradi.actions.ActionClose;
import org.miradi.actions.ActionCollapseAllRows;
import org.miradi.actions.ActionConfigureExport;
import org.miradi.actions.ActionCreateAudience;
import org.miradi.actions.ActionCreateExpense;
import org.miradi.actions.ActionCreateIucnRedlistSpecies;
import org.miradi.actions.ActionCreateMethod;
import org.miradi.actions.ActionCreateOtherNotableSpecies;
import org.miradi.actions.ActionCreateProgressPercent;
import org.miradi.actions.ActionDatabasesDemo;
import org.miradi.actions.ActionDeleteAudience;
import org.miradi.actions.ActionDeleteExpense;
import org.miradi.actions.ActionDeleteIucnRedlistSpecies;
import org.miradi.actions.ActionDeleteLegacyTncStrategyRanking;
import org.miradi.actions.ActionDeleteMethod;
import org.miradi.actions.ActionDeleteOtherNotableSpecies;
import org.miradi.actions.ActionDeleteProgressPercent;
import org.miradi.actions.ActionDeleteProgressReport;
import org.miradi.actions.ActionEditActivityGoalRelevancyList;
import org.miradi.actions.ActionEditActivityObjectiveRelevancyList;
import org.miradi.actions.ActionEditEstimatedResource;
import org.miradi.actions.ActionEditGoalIndicatorRelevancyList;
import org.miradi.actions.ActionEditGoalStrategyActivityRelevancyList;
import org.miradi.actions.ActionEditMethods;
import org.miradi.actions.ActionEditObjectiveIndicatorRelevancyList;
import org.miradi.actions.ActionEditObjectiveStrategyActivityRelevancyList;
import org.miradi.actions.ActionEditStrategyGoalRelevancyList;
import org.miradi.actions.ActionEditStrategyObjectiveRelevancyList;
import org.miradi.actions.ActionExit;
import org.miradi.actions.ActionExpandAllRows;
import org.miradi.actions.ActionExportCpmz;
import org.miradi.actions.ActionExportMpz;
import org.miradi.actions.ActionExportRtf;
import org.miradi.actions.ActionExportTable;
import org.miradi.actions.ActionExportWcsProjectZip;
import org.miradi.actions.ActionExportXmpz;
import org.miradi.actions.ActionExportXmpz2;
import org.miradi.actions.ActionHelpAdaptiveManagement;
import org.miradi.actions.ActionHelpAgileSoftware;
import org.miradi.actions.ActionHelpButtonExamples;
import org.miradi.actions.ActionHelpButtonMoreInfo;
import org.miradi.actions.ActionHelpButtonSupport;
import org.miradi.actions.ActionHelpButtonWorkshop;
import org.miradi.actions.ActionHelpCMPStandards;
import org.miradi.actions.ActionHelpComingAttractions;
import org.miradi.actions.ActionHelpCredits;
import org.miradi.actions.ActionHowToSave;
import org.miradi.actions.ActionImportCpmz;
import org.miradi.actions.ActionImportMpf;
import org.miradi.actions.ActionImportMpz;
import org.miradi.actions.ActionImportXmpz;
import org.miradi.actions.ActionImportXmpz2;
import org.miradi.actions.ActionInvokeDashboard;
import org.miradi.actions.ActionPreferences;
import org.miradi.actions.ActionPrint;
import org.miradi.actions.ActionRedo;
import org.miradi.actions.ActionRemoveAssignment;
import org.miradi.actions.ActionReportsDemo;
import org.miradi.actions.ActionSaveImageJPEG;
import org.miradi.actions.ActionSaveImagePng;
import org.miradi.actions.ActionSaveProjectAs;
import org.miradi.actions.ActionShareMethod;
import org.miradi.actions.ActionShowCurrentWizardFileName;
import org.miradi.actions.ActionToggleSpellChecker;
import org.miradi.actions.ActionUndo;
import org.miradi.actions.ActionViewLegacyTncStrategtyRanking;
import org.miradi.actions.ActionWizardNext;
import org.miradi.actions.ActionWizardPrevious;
import org.miradi.actions.Actions;
import org.miradi.actions.jump.ActionJumpActivitiesAndActionPlan;
import org.miradi.actions.jump.ActionJumpAdaptAndMonitorPlans;
import org.miradi.actions.jump.ActionJumpAnalyzeData;
import org.miradi.actions.jump.ActionJumpAnalyzeProjectCapacity;
import org.miradi.actions.jump.ActionJumpAnalyzeResourcesFeasibilityAndRisk;
import org.miradi.actions.jump.ActionJumpAnalyzeStrategies;
import org.miradi.actions.jump.ActionJumpArticulateCoreAssumptions;
import org.miradi.actions.jump.ActionJumpAssessStakeholders;
import org.miradi.actions.jump.ActionJumpBudgetFutureDemo;
import org.miradi.actions.jump.ActionJumpBudgetWizardAccountingAndFunding;
import org.miradi.actions.jump.ActionJumpCloseTheLoop;
import org.miradi.actions.jump.ActionJumpCommunicateResults;
import org.miradi.actions.jump.ActionJumpCreate;
import org.miradi.actions.jump.ActionJumpDefineAudiences;
import org.miradi.actions.jump.ActionJumpDefineTasks;
import org.miradi.actions.jump.ActionJumpDevelopDraftStrategiesStep;
import org.miradi.actions.jump.ActionJumpDiagramOverviewStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardCreateInitialModelStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardDefineAudienceStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardDefineTargetsStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardHumanWelfareTargetsStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardIdentifyDirectThreatStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardIdentifyIndirectThreatStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardLinkDirectThreatsToTargetsStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardProjectScopeStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardResultsChainSelectStrategyStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardReviewAndModifyTargetsStep;
import org.miradi.actions.jump.ActionJumpDiagramWizardReviewModelAndAdjustStep;
import org.miradi.actions.jump.ActionJumpDocument;
import org.miradi.actions.jump.ActionJumpEditAllStrategiesStep;
import org.miradi.actions.jump.ActionJumpFinancialOverviewStep;
import org.miradi.actions.jump.ActionJumpGroundTruthRevise;
import org.miradi.actions.jump.ActionJumpImplementWorkPlan;
import org.miradi.actions.jump.ActionJumpMonitoringWizardDefineIndicatorsStep;
import org.miradi.actions.jump.ActionJumpMonitoringWizardEditIndicatorsStep;
import org.miradi.actions.jump.ActionJumpMonitoringWizardSelectMethodsStep;
import org.miradi.actions.jump.ActionJumpPlanDataStorage;
import org.miradi.actions.jump.ActionJumpPlanningOverviewStep;
import org.miradi.actions.jump.ActionJumpPlanningWizardFinalizeMonitoringPlanStep;
import org.miradi.actions.jump.ActionJumpPlanningWizardFinalizeStrategicPlanStep;
import org.miradi.actions.jump.ActionJumpRankDraftStrategiesStep;
import org.miradi.actions.jump.ActionJumpRefinePlans;
import org.miradi.actions.jump.ActionJumpScheduleOverviewStep;
import org.miradi.actions.jump.ActionJumpSelectAppropriateMethods;
import org.miradi.actions.jump.ActionJumpSelectChainStep;
import org.miradi.actions.jump.ActionJumpShare;
import org.miradi.actions.jump.ActionJumpShorttermPlans;
import org.miradi.actions.jump.ActionJumpStrategicPlanDevelopGoalStep;
import org.miradi.actions.jump.ActionJumpStrategicPlanDevelopObjectivesStep;
import org.miradi.actions.jump.ActionJumpStrategicPlanHowToConstructStep;
import org.miradi.actions.jump.ActionJumpStrategicPlanViewAllGoals;
import org.miradi.actions.jump.ActionJumpStrategicPlanViewAllObjectives;
import org.miradi.actions.jump.ActionJumpSummaryWizardDefineProjecScope;
import org.miradi.actions.jump.ActionJumpSummaryWizardDefineProjectLeader;
import org.miradi.actions.jump.ActionJumpSummaryWizardDefineProjectVision;
import org.miradi.actions.jump.ActionJumpSummaryWizardDefineTeamMembers;
import org.miradi.actions.jump.ActionJumpSummaryWizardRolesAndResponsibilities;
import org.miradi.actions.jump.ActionJumpTargetStressesStep;
import org.miradi.actions.jump.ActionJumpTargetViability3Step;
import org.miradi.actions.jump.ActionJumpTargetViabilityMethodChoiceStep;
import org.miradi.actions.jump.ActionJumpTeamRoles;
import org.miradi.actions.jump.ActionJumpThreatMatrixOverviewStep;
import org.miradi.actions.jump.ActionJumpThreatRatingWizardCheckTotalsStep;
import org.miradi.actions.jump.ActionJumpWorkPlanAssignResourcesStep;
import org.miradi.actions.jump.ActionJumpWorkPlanDevelopActivitiesAndTasksStep;
import org.miradi.actions.jump.ActionJumpWorkPlanDevelopMethodsAndTasksStep;
import org.miradi.actions.views.ActionViewDiagram;
import org.miradi.actions.views.ActionViewImages;
import org.miradi.actions.views.ActionViewMap;
import org.miradi.actions.views.ActionViewPlanning;
import org.miradi.actions.views.ActionViewReports;
import org.miradi.actions.views.ActionViewSchedule;
import org.miradi.actions.views.ActionViewSummary;
import org.miradi.actions.views.ActionViewTargetViability;
import org.miradi.actions.views.ActionViewThreatMatrix;
import org.miradi.actions.views.ActionViewWorkPlan;
import org.miradi.commands.Command;
import org.miradi.commands.CommandDeleteObject;
import org.miradi.dialogs.base.ModelessDialogPanel;
import org.miradi.dialogs.base.ModelessDialogWithClose;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.main.MiradiToolBar;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ViewData;
import org.miradi.project.Project;
import org.miradi.rtf.RtfWriter;
import org.miradi.utils.TableExporter;
import org.miradi.views.Doer;
import org.miradi.views.MiradiTabContentsPanelInterface;
import org.miradi.views.NullDoer;
import org.miradi.views.diagram.Print;
import org.miradi.views.diagram.doers.CreateProgressReportDoer;
import org.miradi.views.diagram.doers.DeleteProgressReportDoer;
import org.miradi.views.diagram.doers.EditEstimatedResourceDoer;
import org.miradi.views.umbrella.doers.CollapseAllRowsDoer;
import org.miradi.views.umbrella.doers.CreateAssignmentDoer;
import org.miradi.views.umbrella.doers.CreateAudienceDoer;
import org.miradi.views.umbrella.doers.CreateExpenseDoer;
import org.miradi.views.umbrella.doers.CreateIucnRedlistSpeciesDoer;
import org.miradi.views.umbrella.doers.CreateMethodDoer;
import org.miradi.views.umbrella.doers.CreateOtherNotableSpeciesDoer;
import org.miradi.views.umbrella.doers.CreateProgressPercentDoer;
import org.miradi.views.umbrella.doers.DashboardDoer;
import org.miradi.views.umbrella.doers.DeleteAudienceDoer;
import org.miradi.views.umbrella.doers.DeleteExpenseDoer;
import org.miradi.views.umbrella.doers.DeleteIucnRedlistSpeciesDoer;
import org.miradi.views.umbrella.doers.DeleteLegacyTncStrategyRankingDoer;
import org.miradi.views.umbrella.doers.DeleteMethodDoer;
import org.miradi.views.umbrella.doers.DeleteOtherNotableSpeciesDoer;
import org.miradi.views.umbrella.doers.DeleteProgressPercentDoer;
import org.miradi.views.umbrella.doers.EditActivityGoalRelevancyListDoer;
import org.miradi.views.umbrella.doers.EditActivityObjectiveRelevancyListDoer;
import org.miradi.views.umbrella.doers.EditGoalIndicatorRelevancyListDoer;
import org.miradi.views.umbrella.doers.EditGoalStrategyActivityRelevacyListDoer;
import org.miradi.views.umbrella.doers.EditMethodsDoer;
import org.miradi.views.umbrella.doers.EditObjectiveStrategyActivityRelevacyListDoer;
import org.miradi.views.umbrella.doers.EditStrategyGoalRelevancyListDoer;
import org.miradi.views.umbrella.doers.EditStrategyObjectiveRelevancyListDoer;
import org.miradi.views.umbrella.doers.ExpandAllRowsDoer;
import org.miradi.views.umbrella.doers.ExportRtfDoer;
import org.miradi.views.umbrella.doers.ExportXmpzDoer;
import org.miradi.views.umbrella.doers.HowToSaveDoer;
import org.miradi.views.umbrella.doers.ImportCpmzDoer;
import org.miradi.views.umbrella.doers.ImportMpzDoer;
import org.miradi.views.umbrella.doers.RemoveResourceAssignmentDoer;
import org.miradi.views.umbrella.doers.SaveProjectAsDoer;
import org.miradi.views.umbrella.doers.ShowCurrentWizardFileNameDoer;
import org.miradi.views.umbrella.doers.SwitchToReportViewDoer;
import org.miradi.views.umbrella.doers.SwitchToWorkPlanViewDoer;
import org.miradi.views.umbrella.doers.TabDelimitedTableExportDoer;
import org.miradi.views.umbrella.doers.TreeNodeShareMethodDoer;
import org.miradi.views.umbrella.doers.ViewLegacyTncStrategyRankingDoer;
import org.miradi.views.umbrella.doers.WcsXmlExportDoer;
import org.miradi.views.umbrella.doers.Xmpz2ProjectExportDoer;
import org.miradi.wizard.SkeletonWizardStep;

abstract public class UmbrellaView extends JPanel implements CommandExecutedListener
{
	public UmbrellaView(MainWindow mainWindowToUse)
	{
		super(new BorderLayout());
		mainWindow = mainWindowToUse;
		nullDoer = new NullDoer();
		actionToDoerMap = new HashMap<Class, Doer>();
		addUmbrellaDoersToMap();
	}
	
	abstract public String cardName();

	abstract public MiradiTabContentsPanelInterface getCurrentTabPanel();

	public void setTabForStep(SkeletonWizardStep step)
	{
	}
	
	public void refresh() throws Exception
	{
		becomeInactive();
		becomeActive();
	}
	
	public void becomeActive() throws Exception
	{
		if(isActive)
			EAM.logWarning("UmbrellaView.becomeActive was already active: " + getClass().getName());
		getProject().addCommandExecutedListener(this);
		isActive = true;
		getMainWindow().clearStatusBar();
	}
	
	public void becomeInactive() throws Exception
	{
		if(!isActive)
			EAM.logWarning("UmbrellaView.becomeActive was not active: " + getClass().getName());
		
		closeActivePropertiesDialog();
		getProject().removeCommandExecutedListener(this);
		isActive = false;
		removeAll();
	}
	
	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	public Project getProject()
	{
		return getMainWindow().getProject();
	}
	
	public Actions getActions()
	{
		return getMainWindow().getActions();
	}
	
	abstract public MiradiToolBar createToolBar();
	
	public boolean isImageAvailable()
	{
		return false;
	}
	
	public boolean isExportableTableAvailable()
	{
		return false;
	}
	
	public boolean isPrintable()
	{
		return false;
	}
	
	public boolean isRtfExportable()
	{
		return false;
	}
	
	public TableExporter getTableExporter() throws Exception
	{
		throw new RuntimeException("This view doesn't support getExportableTable");
	}
	
	public BufferedImage getImage(int scalePercent) throws Exception
	{
		throw new RuntimeException("This view doesn't support getImage");
	}
	
	public JComponent getPrintableComponent() throws Exception
	{
		throw new RuntimeException("This view doesn't support getPrintableComponent");
	}
	
	public void exportRtf(RtfWriter writer) throws Exception
	{
		throw new RuntimeException("This view doesn't support exportRtf");
	}
	
	public BaseObject getSelectedObject()
	{
		return null;
	}
	
	public void showFloatingPropertiesDialog(ModelessDialogWithClose newDialog)
	{
		closeActivePropertiesDialog();
		
		activePropertiesDlg = newDialog;
		activePropertiesDlg.becomeActive();
		activePropertiesDlg.pack();
		Utilities.centerDlg(activePropertiesDlg);
		activePropertiesDlg.setVisible(true);
	}

	protected UiLabel createScreenShotLabel()
	{
		UiLabel label = new PanelTitleLabel("Demo Screen Shot");
		label.setBorder(new LineBorder(Color.BLACK));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	////////////////////////////////////////////////////////////
	// these doers are available in this class
	
	private void addUmbrellaDoersToMap()
	{
		addDoerToMap(ActionAbout.class, new AboutDoer());
		addDoerToMap(ActionAboutBenetech.class, new HelpButtonDoer());
		addDoerToMap(ActionAboutCMP.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpComingAttractions.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpAdaptiveManagement.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpAgileSoftware.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpCMPStandards.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpCredits.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpButtonExamples.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpButtonMoreInfo.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpButtonWorkshop.class, new HelpButtonDoer());
		addDoerToMap(ActionHelpButtonSupport.class, new HelpButtonDoer());
		
		addDoerToMap(ActionSaveProjectAs.class, new SaveProjectAsDoer());
		addDoerToMap(ActionHowToSave.class, new HowToSaveDoer());
		addDoerToMap(ActionClose.class, new Close());
		addDoerToMap(ActionExit.class, new Exit());
		addDoerToMap(ActionUndo.class, new UndoDoer());
		addDoerToMap(ActionRedo.class, new RedoDoer());
		addDoerToMap(ActionPreferences.class, new PreferencesPopupDoer());
		addDoerToMap(ActionToggleSpellChecker.class, new ToggleSpellCheckerDoer());
		addDoerToMap(ActionImportMpz.class, new ImportMpzDoer());
		addDoerToMap(ActionImportMpf.class, new ImportMpfProjectDoer());
		addDoerToMap(ActionExportMpz.class, new ExportMpzDoer());
		addDoerToMap(ActionImportCpmz.class, new ImportCpmzDoer());
		addDoerToMap(ActionExportCpmz.class, new ExportCpmzDoer());
		addDoerToMap(ActionImportXmpz.class, new ImportXmpzDoer());
		addDoerToMap(ActionExportXmpz.class, new ExportXmpzDoer());
		addDoerToMap(ActionExportXmpz2.class, new Xmpz2ProjectExportDoer());
		addDoerToMap(ActionImportXmpz2.class, new ImportXmpz2Doer());
		addDoerToMap(ActionExportWcsProjectZip.class, new WcsXmlExportDoer());
		addDoerToMap(ActionExportRtf.class, new ExportRtfDoer());
		addDoerToMap(ActionExportTable.class, new TabDelimitedTableExportDoer());
		addDoerToMap(ActionSaveImageJPEG.class, new SaveImageJPEGDoer());
		addDoerToMap(ActionSaveImagePng.class, new SaveImagePngDoer());
		addDoerToMap(ActionConfigureExport.class, new HelpButtonDoer());
		addDoerToMap(ActionDatabasesDemo.class, new HelpButtonDoer());
		addDoerToMap(ActionReportsDemo.class, new HelpButtonDoer());
		
		addDoerToMap(ActionEditMethods.class, new EditMethodsDoer());
		addDoerToMap(ActionCreateMethod.class, new CreateMethodDoer());
		addDoerToMap(ActionDeleteMethod.class, new DeleteMethodDoer());
		addDoerToMap(ActionShareMethod.class, new TreeNodeShareMethodDoer());
		
		addDoerToMap(ActionCreateProgressReport.class, new CreateProgressReportDoer());
		addDoerToMap(ActionDeleteProgressReport.class, new DeleteProgressReportDoer());
		
		addDoerToMap(ActionCreateProgressPercent.class, new CreateProgressPercentDoer());
		addDoerToMap(ActionDeleteProgressPercent.class, new DeleteProgressPercentDoer());
		
		addDoerToMap(ActionCreateProgressPercent.class, new CreateProgressPercentDoer());
		addDoerToMap(ActionDeleteProgressPercent.class, new DeleteProgressPercentDoer());
		
		addDoerToMap(ActionEditEstimatedResource.class, new EditEstimatedResourceDoer());
		
		addDoerToMap(ActionViewSummary.class, new SwitchToSummaryViewDoer());
		addDoerToMap(ActionViewDiagram.class, new SwitchToDiagramViewDoer());
		addDoerToMap(ActionViewThreatMatrix.class, new SwitchToThreatRatingViewDoer());
		addDoerToMap(ActionViewPlanning.class, new SwitchToPlanningViewDoer());
		addDoerToMap(ActionViewMap.class, new SwitchToMapViewDoer());
		addDoerToMap(ActionViewImages.class, new SwitchToLibraryViewDoer());
		addDoerToMap(ActionViewSchedule.class, new SwitchToScheduleViewDoer());
		addDoerToMap(ActionViewTargetViability.class, new SwitchToTargetViabilityViewDoer());
		addDoerToMap(ActionViewReports.class, new SwitchToReportViewDoer());
		addDoerToMap(ActionViewWorkPlan.class, new SwitchToWorkPlanViewDoer());
		
		addDoerToMap(ActionEditObjectiveIndicatorRelevancyList.class, new EditObjectiveIndicatorRelevancyListDoer());
		addDoerToMap(ActionEditGoalIndicatorRelevancyList.class, new EditGoalIndicatorRelevancyListDoer());
		addDoerToMap(ActionEditObjectiveStrategyActivityRelevancyList.class, new EditObjectiveStrategyActivityRelevacyListDoer());
		addDoerToMap(ActionEditGoalStrategyActivityRelevancyList.class, new EditGoalStrategyActivityRelevacyListDoer());
		
		addDoerToMap(ActionEditStrategyObjectiveRelevancyList.class, new EditStrategyObjectiveRelevancyListDoer());
		addDoerToMap(ActionEditStrategyGoalRelevancyList.class, new EditStrategyGoalRelevancyListDoer());
		addDoerToMap(ActionEditActivityObjectiveRelevancyList.class, new EditActivityObjectiveRelevancyListDoer());
		addDoerToMap(ActionEditActivityGoalRelevancyList.class, new EditActivityGoalRelevancyListDoer());
		
		addDoerToMap(ActionViewLegacyTncStrategtyRanking.class, new ViewLegacyTncStrategyRankingDoer());
		addDoerToMap(ActionDeleteLegacyTncStrategyRanking.class, new DeleteLegacyTncStrategyRankingDoer());
		
		addDoerToMap(ActionExpandAllRows.class, new ExpandAllRowsDoer());
		addDoerToMap(ActionCollapseAllRows.class, new CollapseAllRowsDoer());
		
		addDoerToMap(ActionAssignResource.class, new CreateAssignmentDoer());
		addDoerToMap(ActionRemoveAssignment.class, new RemoveResourceAssignmentDoer());
		addDoerToMap(ActionCreateExpense.class, new CreateExpenseDoer());
		addDoerToMap(ActionDeleteExpense.class, new DeleteExpenseDoer());
		
		addDoerToMap(ActionCreateIucnRedlistSpecies.class, new CreateIucnRedlistSpeciesDoer());
		addDoerToMap(ActionDeleteIucnRedlistSpecies.class, new DeleteIucnRedlistSpeciesDoer());
		
		addDoerToMap(ActionCreateOtherNotableSpecies.class, new CreateOtherNotableSpeciesDoer());
		addDoerToMap(ActionDeleteOtherNotableSpecies.class, new DeleteOtherNotableSpeciesDoer());
		
		addDoerToMap(ActionCreateAudience.class, new CreateAudienceDoer());
		addDoerToMap(ActionDeleteAudience.class, new DeleteAudienceDoer());
				
		addDoerToMap(ActionPrint.class, new Print());
		addDoerToMap(ActionShowCurrentWizardFileName.class, new ShowCurrentWizardFileNameDoer());
		addDoerToMap(ActionInvokeDashboard.class, new DashboardDoer());
		
		addDoerToMap(ActionWizardNext.class, new WizardNextDoer());
		addDoerToMap(ActionWizardPrevious.class, new WizardPreviousDoer());
		
		addJumpDoerToMap(ActionJumpSummaryWizardDefineTeamMembers.class);
		addJumpDoerToMap(ActionJumpSummaryWizardDefineProjectLeader.class);
		addJumpDoerToMap(ActionJumpSummaryWizardRolesAndResponsibilities.class);
		
		addJumpDoerToMap(ActionJumpSummaryWizardDefineProjecScope.class);
		addJumpDoerToMap(ActionJumpSummaryWizardDefineProjectVision.class);
		addJumpDoerToMap(ActionJumpDiagramWizardDefineTargetsStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardProjectScopeStep.class);
		addJumpDoerToMap(ActionJumpTargetViabilityMethodChoiceStep.class);
		addJumpDoerToMap(ActionJumpTargetViability3Step.class);
		addJumpDoerToMap(ActionJumpTargetStressesStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardHumanWelfareTargetsStep.class);
		
		addJumpDoerToMap(ActionJumpDiagramWizardIdentifyDirectThreatStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardLinkDirectThreatsToTargetsStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardCreateInitialModelStep.class);
		addJumpDoerToMap(ActionJumpThreatMatrixOverviewStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardIdentifyIndirectThreatStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardReviewAndModifyTargetsStep.class);
		addJumpDoerToMap(ActionJumpAssessStakeholders.class);
		addJumpDoerToMap(ActionJumpAnalyzeProjectCapacity.class);
		
		addJumpDoerToMap(ActionJumpArticulateCoreAssumptions.class);
		addJumpDoerToMap(ActionJumpDiagramWizardReviewModelAndAdjustStep.class);
		addJumpDoerToMap(ActionJumpGroundTruthRevise.class);
		
		addJumpDoerToMap(ActionJumpStrategicPlanDevelopGoalStep.class);
		addJumpDoerToMap(ActionJumpSelectChainStep.class);
		addJumpDoerToMap(ActionJumpStrategicPlanDevelopObjectivesStep.class);
		
		addJumpDoerToMap(ActionJumpRankDraftStrategiesStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardResultsChainSelectStrategyStep.class);
		addJumpDoerToMap(ActionJumpEditAllStrategiesStep.class);
		addJumpDoerToMap(ActionJumpActivitiesAndActionPlan.class);
		addJumpDoerToMap(ActionJumpAnalyzeResourcesFeasibilityAndRisk.class);
		
		addJumpDoerToMap(ActionJumpDiagramWizardDefineAudienceStep.class);
		addJumpDoerToMap(ActionJumpDefineAudiences.class);
		
		addJumpDoerToMap(ActionJumpMonitoringWizardDefineIndicatorsStep.class);
		addJumpDoerToMap(ActionJumpMonitoringWizardEditIndicatorsStep.class);
		addJumpDoerToMap(ActionJumpSelectAppropriateMethods.class);
		addJumpDoerToMap(ActionJumpPlanDataStorage.class);
		
		addJumpDoerToMap(ActionJumpMonitoringWizardSelectMethodsStep.class);
		
		addJumpDoerToMap(ActionJumpBudgetWizardAccountingAndFunding.class);
		
		addJumpDoerToMap(ActionJumpPlanningOverviewStep.class);
		addJumpDoerToMap(ActionJumpPlanningWizardFinalizeStrategicPlanStep.class);
		addJumpDoerToMap(ActionJumpPlanningWizardFinalizeMonitoringPlanStep.class);
		addJumpDoerToMap(ActionJumpShorttermPlans.class);
		addJumpDoerToMap(ActionJumpScheduleOverviewStep.class);
		addJumpDoerToMap(ActionJumpDefineTasks.class);
		addJumpDoerToMap(ActionJumpTeamRoles.class);
		addJumpDoerToMap(ActionJumpRefinePlans.class);
		addJumpDoerToMap(ActionJumpImplementWorkPlan.class);
		
		addJumpDoerToMap(ActionJumpAnalyzeData.class);
		addJumpDoerToMap(ActionJumpAnalyzeStrategies.class);
		addJumpDoerToMap(ActionJumpCommunicateResults.class);
		
		addJumpDoerToMap(ActionJumpAdaptAndMonitorPlans.class);
		
		addJumpDoerToMap(ActionJumpDocument.class);
		addJumpDoerToMap(ActionJumpShare.class);
		addJumpDoerToMap(ActionJumpCreate.class);
		
		addJumpDoerToMap(ActionJumpCloseTheLoop.class);
		
		addJumpDoerToMap(ActionJumpStrategicPlanViewAllGoals.class);
		addJumpDoerToMap(ActionJumpStrategicPlanViewAllObjectives.class);
		addJumpDoerToMap(ActionJumpStrategicPlanHowToConstructStep.class);
		
		addJumpDoerToMap(ActionJumpDevelopDraftStrategiesStep.class);
		
		
		addJumpDoerToMap(ActionJumpWorkPlanAssignResourcesStep.class);
		addJumpDoerToMap(ActionJumpDiagramWizardReviewModelAndAdjustStep.class);
		addJumpDoerToMap(ActionJumpThreatRatingWizardCheckTotalsStep.class);

		addJumpDoerToMap(ActionJumpWorkPlanDevelopActivitiesAndTasksStep.class);
		addJumpDoerToMap(ActionJumpWorkPlanDevelopMethodsAndTasksStep.class);
		addJumpDoerToMap(ActionJumpWorkPlanAssignResourcesStep.class);
		addJumpDoerToMap(ActionJumpScheduleOverviewStep.class);
		addJumpDoerToMap(ActionJumpFinancialOverviewStep.class);
		addJumpDoerToMap(ActionJumpBudgetFutureDemo.class);
		addJumpDoerToMap(ActionJumpDiagramOverviewStep.class);
	}
	
	public void addJumpDoerToMap(Class actionClass)
	{
		addDoerToMap(actionClass, new JumpDoer(actionClass));
	}
	
	public void addDoerToMap(Class actionClass, Doer doer)
	{
		actionToDoerMap.put(actionClass, doer);
	}
	
	public Doer getDoer(Class actionClass)
	{
		Doer doer = actionToDoerMap.get(actionClass);
		if(doer == null)
			doer = nullDoer;
		
		doer.setView(this);
		doer.setProject(getProject());
		return doer;
	}
	
	protected ViewData getViewData() throws Exception
	{
		ViewData ourViewData = getProject().getViewData(cardName());
		return ourViewData;
	}

	public void commandExecuted(CommandExecutedEvent event)
	{
		closeActivePropertiesDialogIfWeDeletedItsObject(event.getCommand());
	}

	void closeActivePropertiesDialogIfWeDeletedItsObject(Command rawCommand)
	{
		if(!rawCommand.getCommandName().equals(CommandDeleteObject.COMMAND_NAME))
			return;
		
		CommandDeleteObject cmd = (CommandDeleteObject)rawCommand;
		
		if(isActivePropertiesDialogEditing(cmd.getObjectRef()))
			closeActivePropertiesDialog();
	}
	
	private boolean isActivePropertiesDialogEditing(ORef ref)
	{
		if(activePropertiesDlg == null)
			return false;
		
		ModelessDialogPanel panel = activePropertiesDlg.safeGetWrappedModelessDialogPanel();
		if(panel == null)
			return false;
		
		BaseObject objectBeingEdited = panel.getObject();
		if(objectBeingEdited == null)
			return false;
		
		return (ref.equals(objectBeingEdited.getRef()));
	}
	
	public void closeActivePropertiesDialog()
	{
		if(activePropertiesDlg != null && activePropertiesDlg.isDisplayable())
		{
			activePropertiesDlg.setVisible(false);
			activePropertiesDlg.dispose();
		}
		activePropertiesDlg = null;
	}
	
	protected void forceLayoutSoSplittersWork()
	{
		getTopLevelAncestor().validate();
	}

	private MainWindow mainWindow;
	private NullDoer nullDoer;
	private HashMap<Class, Doer> actionToDoerMap;
	private boolean isActive;
	
	private ModelessDialogWithClose activePropertiesDlg;
 
}
