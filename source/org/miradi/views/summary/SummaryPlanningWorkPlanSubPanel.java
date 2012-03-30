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
package org.miradi.views.summary;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogfields.ObjectMultilineDisplayField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.dialogs.planning.propertiesPanel.FillerPanel;
import org.miradi.icons.IconManager;
import org.miradi.main.AppPreferences;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.TimePeriodCostsMap;
import org.miradi.objects.Assignment;
import org.miradi.objects.ProjectMetadata;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.DiagramObjectDataInclusionQuestion;
import org.miradi.questions.FiscalYearStartQuestion;
import org.miradi.questions.QuarterColumnsVisibilityQuestion;
import org.miradi.schemas.ExpenseAssignmentSchema;
import org.miradi.utils.DateRange;
import org.miradi.utils.MiradiTextPane;

public class SummaryPlanningWorkPlanSubPanel extends ObjectDataInputPanel
{
	public SummaryPlanningWorkPlanSubPanel(Project projectToUse, ORef orefToUse) throws Exception
	{
		super(projectToUse, orefToUse);
		
		addHiddenDataWarningLabel();
		
		ObjectDataInputField startDate = createDateChooserField(ProjectMetadata.TAG_START_DATE);
		ObjectDataInputField endDate = createDateChooserField(ProjectMetadata.TAG_EXPECTED_END_DATE);
		ObjectDataInputField[] projectDateFields = new ObjectDataInputField[] {startDate, endDate, };
		addFieldsOnOneLine(EAM.text("Label|Project Dates"), projectDateFields);
		
		ObjectDataInputField workPlanStartDate = createDateChooserField(ProjectMetadata.TAG_WORKPLAN_START_DATE);
		ObjectDataInputField workPlanEndDate = createDateChooserField(ProjectMetadata.TAG_WORKPLAN_END_DATE);
		ObjectDataInputField[] workPlanDateFields = new ObjectDataInputField[] {workPlanStartDate, workPlanEndDate, };
		addFieldsOnOneLine(EAM.text("Label|Work Plan Dates"), workPlanDateFields);
		
		addDataDateRangeTextField();

		addField(createChoiceField(ProjectMetadata.getObjectType(), ProjectMetadata.TAG_FISCAL_YEAR_START, new FiscalYearStartQuestion()));
		addField(createChoiceField(ProjectMetadata.getObjectType(), ProjectMetadata.TAG_WORK_PLAN_DIAGRAM_DATA_INCLUSION, getProject().getQuestion(DiagramObjectDataInclusionQuestion.class)));
		addField(createNumericField(ProjectMetadata.getObjectType(), ProjectMetadata.TAG_FULL_TIME_EMPLOYEE_DAYS_PER_YEAR));
		ChoiceQuestion quarterColumnsVisibilityQuestion = getProject().getQuestion(QuarterColumnsVisibilityQuestion.class);
		quarterColumnVisibilityComponent = addRadioButtonFieldWithCustomLabel(ProjectMetadata.getObjectType(), ProjectMetadata.TAG_QUARTER_COLUMNS_VISIBILITY, quarterColumnsVisibilityQuestion, "");
		addQuarterColumnVisibilityExplanationLabel();
		addField(createMultilineField(ProjectMetadata.TAG_PLANNING_COMMENTS));
		
		updateQuarterColumnVisibilityEnableStatus();
		
		setObjectRefs(new ORef[] {orefToUse, projectToUse.getSingletonObjectRef(ProjectMetadata.getObjectType()), });
		updateFieldsFromProject();
	}
	
	private void addDataDateRangeTextField() throws Exception
	{
		String startDate = getFirstDateWithData();
		String endDate = getLastDateWithData();
		add(new FillerPanel());
		
		String text = EAM.text("Work plan data currently exists for %startDate - %endDate");
		text = EAM.substitute(text, "%startDate", startDate);
		text = EAM.substitute(text, "%endDate", endDate);
		PanelTitleLabel textArea = new PanelTitleLabel(text);
		
		textArea.setBackground(AppPreferences.getDataPanelBackgroundColor());
		add(textArea);
	}

	private void addHiddenDataWarningLabel() throws Exception
	{
		warningLabelFillerReplacement =createAndAddFillerPanel();
		String warningMessage = EAM.text("Some work plan data is currently hidden and not included in " +
										"calculated totals. If the data is outside the planning date range, " +
										"it will be visible again if you set the planning start and end date " +
										"to include the entire date range of the existing data. " +
										"Otherwise, it could be that annual data was entered for a " +
										"different fiscal year setting, in which case it will be visible " +
										"if you set the fiscal year start back to its previous setting.");
		warningPanel = createAndAddWarningPanel(warningMessage);
		updateOutOfRangeDataWarningField();
	}
	
	private void addQuarterColumnVisibilityExplanationLabel() throws Exception
	{	
		quarterVisibilityExplanationFillerReplacement = createAndAddFillerPanel();
		String message = EAM.text("NOTE: Quarter columns cannot be hidden because this project already has data for some quarters.");
		explanationPanel = createAndAddInformationalNotePanel(message);	
		updateQuarterColumnVisibilityEnableStatus();
	}
	
	private FillerPanel createAndAddFillerPanel()
	{
		add(new FillerPanel());
		FillerPanel warningPanelEmptyReplacementPanel = new FillerPanel();
		add(warningPanelEmptyReplacementPanel);
		
		return warningPanelEmptyReplacementPanel;
	}

	private JComponent createAndAddInformationalNotePanel(String message) throws Exception
	{
		MiradiTextPane label = new MiradiTextPane(getMainWindow(), ObjectMultilineDisplayField.DEFAULT_WIDE_FIELD_CHARACTERS, 1);
		label.setText(message);
		label.setEditable(false);
		label.setAlignmentY(TOP_ALIGNMENT);
		label.setForeground(getForeground());
		label.setBackground(getBackground());
		
		add(label);
		
		return label;
	}
	
	private JComponent createAndAddWarningPanel(String message) throws Exception
	{
		MiradiTextPane label = new MiradiTextPane(getMainWindow(), ObjectMultilineDisplayField.DEFAULT_WIDE_FIELD_CHARACTERS, 1);
		label.setText(message);
		label.setEditable(false);
		label.setAlignmentY(TOP_ALIGNMENT);
		
		JLabel icon = new JLabel(IconManager.getWarningIcon());
		icon.setAlignmentY(TOP_ALIGNMENT);

		Box panel = Box.createHorizontalBox();
		panel.setBackground(AppPreferences.getDataPanelBackgroundColor());
		label.setBackground(AppPreferences.getDataPanelBackgroundColor());
		panel.add(icon);
		panel.add(label);
		
		add(panel);
		
		return panel;
	}
	
	@Override
	public void setObjectRefs(ORef[] orefsToUse)
	{
		super.setObjectRefs(orefsToUse);
		updateOutOfRangeDataWarningField();
	}
	
	private void updateOutOfRangeDataWarningField()
	{
		if (warningPanel == null)
			return;
		
		boolean showWarning = hasDataOutsideOfProjectDateRange(getProject());
		warningPanel.setVisible(showWarning);
		warningLabelFillerReplacement.setVisible(!showWarning);
	}

	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		
		if (!event.isSetDataCommand())
			return;
		
		CommandSetObjectData setCommand = (CommandSetObjectData) event.getCommand();
		if (isOneOfOurFields(setCommand.getFieldTag()))
		{
			updateQuarterColumnVisibilityEnableStatus();
			updateOutOfRangeDataWarningField();
			getMainWindow().updatePlanningDateRelatedStatus();
		}
	}
	
	private void updateQuarterColumnVisibilityEnableStatus()
	{
		final boolean enableQuarterVisibilityOption = hasQuarterData() && getProject().getMetadata().areQuarterColumnsVisible();
		
		quarterColumnVisibilityComponent.setEditable(!enableQuarterVisibilityOption);
		explanationPanel.setVisible(enableQuarterVisibilityOption);
		quarterVisibilityExplanationFillerReplacement.setVisible(!enableQuarterVisibilityOption);
	}

	public static boolean hasDataOutsideOfProjectDateRange(Project projectToUse)
	{
		try
		{
			DateRange allDataDateRange = getProjectDataDateRange(projectToUse);
			if (allDataDateRange == null)
				return false;
			
			DateRange projectPlanningDateRange = projectToUse.getProjectCalendar().getProjectPlanningDateRange();
			return !projectPlanningDateRange.contains(allDataDateRange);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return false;
		}
	}
	
	private String getLastDateWithData() throws Exception
	{
		DateRange dateRange = getProjectDataDateRange(getProject());
		if (dateRange == null)
			return "";
		
		return dateRange.getEndDate().toIsoDateString();
	}
	
	private String getFirstDateWithData() throws Exception
	{
		DateRange dateRange = getProjectDataDateRange(getProject());
		if (dateRange == null)
			return "";
		
		return dateRange.getStartDate().toIsoDateString();
	}
	
	private static DateRange getProjectDataDateRange(Project projectToUse) throws Exception
	{
		DateRange projectDateRange = projectToUse.getProjectCalendar().getProjectPlanningDateRange();
		TimePeriodCostsMap tpcm = getTimePeriodCostsMapForAllAssignments(projectToUse);
		
		return tpcm.getRolledUpDateRange(projectDateRange);	
	}

	private static TimePeriodCostsMap getTimePeriodCostsMapForAllAssignments(Project projectToUse) throws Exception
	{
		ORefList assignmentRefs = new ORefList();
		assignmentRefs.addAll(projectToUse.getAssignmentPool().getORefList());
		assignmentRefs.addAll(projectToUse.getPool(ExpenseAssignmentSchema.getObjectType()).getRefList());
		
		TimePeriodCostsMap tpcm = new TimePeriodCostsMap();
		for (int index = 0; index < assignmentRefs.size(); ++index)
		{
			Assignment assignment = Assignment.findAssignment(projectToUse, assignmentRefs.get(index));
			tpcm.mergeAll(assignment.convertAllDateUnitEffortList());
		}
		
		return tpcm;
	}
	
	private boolean hasQuarterData()
	{
		try
		{
			TimePeriodCostsMap tpcm = getTimePeriodCostsMapForAllAssignments(getProject());
			return tpcm.containsQuarterDateUnit();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.unexpectedErrorDialog(e);
			return false;
		}
	}
	
	@Override
	public String getPanelDescription()
	{
		return EAM.text("Work Plan Settings");
	}
	
	private FillerPanel warningLabelFillerReplacement;
	private FillerPanel quarterVisibilityExplanationFillerReplacement;
	private JComponent warningPanel;
	private JComponent explanationPanel;
	private ObjectDataInputField quarterColumnVisibilityComponent;
}
