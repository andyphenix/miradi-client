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
package org.miradi.icons;

import java.util.HashMap;

import javax.swing.Icon;

import org.miradi.objects.AccountingCode;
import org.miradi.objects.BaseObject;
import org.miradi.objects.BudgetCategoryOne;
import org.miradi.objects.BudgetCategoryTwo;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.Factor;
import org.miradi.objects.FundingSource;
import org.miradi.objects.FutureStatus;
import org.miradi.objects.Goal;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.Objective;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.SubTarget;
import org.miradi.objects.Task;
import org.miradi.utils.MiradiResourceImageIcon;
 
public class IconManager
{
	public static void initialize()
	{
		iconMap = new HashMap<String, Icon>();
		
		addIcon(new WarningIcon());
		addIcon(new CancelIcon());
		addIcon(new StrategicPlanIcon());
		addIcon(new DeleteIcon());
		addIcon(new ProjectResourceIcon());
		addIcon(new DirectThreatIcon());
		addIcon(new ReportTemplateIcon());
		addIcon(new WorkPlanIcon());
		addIcon(new PlanningIcon());
		addIcon(new AllocatedCostIcon());
		addIcon(new ResultsChainIcon());
		addIcon(new ConceptualModelIcon());
		addIcon(new ActivityIcon());
		addIcon(new MethodIcon());
		addIcon(new TaskIcon());
		addIcon(new DraftStrategyIcon());
		addIcon(new StrategyIcon());
		addIcon(new TargetIcon());
		addIcon(new MiradiApplicationIcon());
		addIcon(new IndicatorIcon());
		addIcon(new ObjectiveIcon());
		addIcon(new GoalIcon());
		addIcon(new MeasurementIcon());
		addIcon(new KeyEcologicalAttributeIcon());
		addIcon(new ContributingFactorIcon());
		addIcon(new HumanWelfareTargetIcon());
		addIcon(new StressIcon());
		addIcon(new GroupBoxIcon());
		addIcon(new TextBoxIcon());
		addIcon(new ThreatReductionResultIcon());
		addIcon(new IntermediateResultIcon());
		addIcon(new AccountingCodeIcon());
		addIcon(new FundingSourceIcon());
		addIcon(new BudgetCategoryOneIcon());
		addIcon(new BudgetCategoryTwoIcon());
		addIcon(new ExpenseAssignmentIcon());
		addIcon(new AssignmentIcon());
		addIcon(new ProgressReportIcon());
		addIcon(new ProgressPercentIcon());
		addIcon(new SubTargetIcon());
		addIcon(new HyphenIcon());
		addIcon(new OpenStandardsAutomaticIcon());
		addIcon(new OpenStandardsNotApplicableIcon());
		addIcon(new OpenStandardsInProgressIcon());
		addIcon(new OpenStandardsCompleteIcon());
		addIcon(new FutureStatusIcon());
		addResourceImageIcon(COLLAPSE_ICON_FILE_NAME);
		addResourceImageIcon(EXPAND_ICON_FILE_NAME);
		addResourceImageIcon(SPELLCHECK_ICON_FILE_NAME);
		addResourceImageIcon(ANALYSIS_ICON_FILE_NAME);
		addResourceImageIcon(COLLAPSE_ALL_ICON_FILE_NAME);
		addResourceImageIcon(EXPAND_ALL_ICON_FILE_NAME);
		addResourceImageIcon(NEEDS_ATTENTION_ICON_FILE_NAME);
		addResourceImageIcon(DOCUMENT_ICON_FILE_NAME);
	}
	
	private static void addIcon(Icon icon)
	{
		iconMap.put(icon.getClass().getSimpleName(), icon);
	}
	
	private static void addResourceImageIcon(String iconFileName)
	{
		iconMap.put(iconFileName, new MiradiResourceImageIcon(iconFileName));
	}
	
	private static Icon getIcon(Class iconClass)
	{
		return getIcon(iconClass.getSimpleName());
	}

	private static Icon getIcon(String iconName)
	{
		if(iconMap == null)
			initialize();
		
		Icon icon = iconMap.get(iconName);
		if(icon == null)
			throw new RuntimeException("Unknown icon: " + iconName);
		
		return icon;
	}
		
	//TODO: not all Icons are AbstractMiradiIcon.  But all icons should be.
	public static Icon getImage(BaseObject baseObject)
	{
		int type = baseObject.getType();

		if (Task.is(type))
			return getTaskIcon((Task) baseObject);
		
		if (Factor.isFactor(type))
			return getFactorIcon((Factor) baseObject);
		
		return getImage(type);
	}

	public static Icon getImage(int type)
	{
		if (ProjectMetadata.is(type))
			return getMiradiApplicationIcon();
		
		if (Indicator.is(type))
			return getIndicatorIcon();
		
		if (Measurement.is(type))
			return getMeasurementIcon();
		
		if (Goal.is(type))
			return getGoalIcon();
		
		if (Objective.is(type))
			return getObjectiveIcon();
		
		if (KeyEcologicalAttribute.is(type))
			return getKeyEcologicalAttributeIcon();

		if (ConceptualModelDiagram.is(type))
			return getConceptualModelIcon();
		
		if (ResultsChainDiagram.is(type))
			return getResultsChainIcon();
		
		if (ResourceAssignment.is(type))
			return getAssignmentIcon();
		
		if (ExpenseAssignment.is(type))
			return getExpenseIcon();
		
		if (ProjectResource.is(type))
			return getResourceIcon();
		
		if (FundingSource.is(type))
			return getFundingSourceIcon();
		
		if (AccountingCode.is(type))
			return getAccountingCodeIcon();
		
		if (BudgetCategoryOne.is(type))
			return getCategoryOneIcon();
		
		if (BudgetCategoryTwo.is(type))
			return getCategoryTwoIcon();
		
		if (SubTarget.is(type))
			return getSubTargetIcon();
		
		if (FutureStatus.is(type))
			return getGoalIcon();
		
		throw new RuntimeException("Could not find icon for type:" + type);
	}

	private static Icon getFactorIcon(Factor factor)
	{
		if (factor.isDirectThreat())
			return getThreatIcon();
		
		if (factor.isContributingFactor())
			return getContributingFactorIcon();
		
		if (factor.isStrategy())
			return getStrategyIcon();
		
		if (factor.isTarget())
			return getTargetIcon();
		
		if (factor.isStress())
			return getStressIcon();
		
		if (factor.isActivity())
			return getActivityIcon();
		
		if (factor.isIntermediateResult())
			return getIntermediateResultIcon();
		
		if (factor.isThreatReductionResult())
			return getThreatReductionResultIcon();
		
		if (factor.isTextBox())
			return getTextBoxIcon();
		
		if (factor.isGroupBox())
			return getGroupBoxIcon();
		
		if (factor.isHumanWelfareTarget())
			return getHumanWelfareTargetIcon();	

		throw new RuntimeException("type is factor but there is no icon for it was found:"  + factor.getType());
	}

	private static Icon getTaskIcon(Task task)
	{
		if (task.isMethod())
			return getMethodIcon();
		
		if (task.isActivity())
			return getActivityIcon();
		
		return getTaskIcon();
	}
	
	private static Icon getContributingFactorIcon()
	{
		return getIcon(ContributingFactorIcon.class);
	}
	
	private static Icon getHumanWelfareTargetIcon()
	{
		return getIcon(HumanWelfareTargetIcon.class);
	}

	private static Icon getStressIcon()
	{
		return getIcon(StressIcon.class);
	}

	private static Icon getGroupBoxIcon()
	{
		return getIcon(GroupBoxIcon.class);
	}

	private static Icon getTextBoxIcon()
	{
		return getIcon(TextBoxIcon.class);
	}

	private static Icon getThreatReductionResultIcon()
	{
		return getIcon(ThreatReductionResultIcon.class);
	}

	private static Icon getIntermediateResultIcon()
	{
		return getIcon(IntermediateResultIcon.class);
	}
	
	private static Icon getAccountingCodeIcon()
	{
		return getIcon(AccountingCodeIcon.class);
	}

	private static Icon getFundingSourceIcon()
	{
		return getIcon(FundingSourceIcon.class);
	}
	
	private static Icon getCategoryOneIcon()
	{
		return getIcon(BudgetCategoryOneIcon.class);
	}
	
	private static Icon getCategoryTwoIcon()
	{
		return getIcon(BudgetCategoryTwoIcon.class);
	}

	private static Icon getExpenseIcon()
	{
		return getIcon(ExpenseAssignmentIcon.class);
	}

	private static Icon getAssignmentIcon()
	{
		return getIcon(AssignmentIcon.class);
	}
	
	public static Icon getKeyEcologicalAttributeIcon()
	{
		return getIcon(KeyEcologicalAttributeIcon.class);
	}
	
	public static Icon getMeasurementIcon()
	{
		return getIcon(MeasurementIcon.class);
	}
	
	public static Icon getGoalIcon()
	{
		return getIcon(GoalIcon.class);
	}
	
	public static Icon getObjectiveIcon()
	{
		return getIcon(ObjectiveIcon.class);
	}
	
	public static Icon getIndicatorIcon()
	{
		return getIcon(IndicatorIcon.class);
	}
	
	public static Icon getHyphenIcon()
	{
		return getIcon(HyphenIcon.class);
	}
	
	public static Icon getOpenStandardsAutomaticIcon()
	{
		return getIcon(OpenStandardsAutomaticIcon.class);
	}
	
	public static Icon getOpenStandardsNotApplicableIcon()
	{
		return getIcon(OpenStandardsNotApplicableIcon.class);
	}
	
	public static Icon getOpenStandardsInProgressIcon()
	{
		return getIcon(OpenStandardsInProgressIcon.class);
	}
	
	public static Icon getOpenStandardsCompleteIcon()
	{
		return getIcon(OpenStandardsCompleteIcon.class);
	}
	
	public static Icon getMiradiApplicationIcon()
	{
		return getIcon(MiradiApplicationIcon.class);
	}
	
	public static Icon getTargetIcon()
	{
		return getIcon(TargetIcon.class);
	}

	public static Icon getStrategyIcon()
	{
		return getIcon(StrategyIcon.class);
	}
	
	public static Icon getDraftStrategyIcon()
	{
		return getIcon(DraftStrategyIcon.class);
	}

	public static Icon getTaskIcon()
	{
		return getIcon(TaskIcon.class);
	}

	public static Icon getMethodIcon()
	{
		return getIcon(MethodIcon.class);
	}

	public static Icon getActivityIcon()
	{
		return getIcon(ActivityIcon.class);
	}
	
	public static Icon getConceptualModelIcon()
	{
		return getIcon(ConceptualModelIcon.class);
	}
	
	public static Icon getResultsChainIcon()
	{
		return getIcon(ResultsChainIcon.class);
	}

	public static Icon getAllocatedCostIcon()
	{
		return getIcon(AllocatedCostIcon.class);
	}
	
	public static Icon getPlanningIcon()
	{
		return getIcon(PlanningIcon.class);
	}
	
	public static Icon getWorkPlanIcon()
	{
		return getIcon(WorkPlanIcon.class);
	}

	public static Icon getReportIcon()
	{
		return getIcon(ReportTemplateIcon.class);
	}

	public static Icon getThreatIcon()
	{
		return getIcon(DirectThreatIcon.class);
	}

	public static Icon getExpandIcon()
	{
		return getIcon(EXPAND_ICON_FILE_NAME);
	}

	public static Icon getCollapseIcon()
	{
		return getIcon(COLLAPSE_ICON_FILE_NAME);
	}
	
	public static Icon getSpellCheckIcon()
	{
		return getIcon(SPELLCHECK_ICON_FILE_NAME);
	}
	
	public static Icon getAnalysisIcon()
	{
		return getIcon(ANALYSIS_ICON_FILE_NAME);
	}
	
	public static Icon getCollapseAllIcon()
	{
		return getIcon(COLLAPSE_ALL_ICON_FILE_NAME);
	}

	public static Icon getExpandAllIcon()
	{
		return getIcon(EXPAND_ALL_ICON_FILE_NAME);
	}
	
	public static Icon getNeedsAttentionIcon()
	{
		return getIcon(NEEDS_ATTENTION_ICON_FILE_NAME);
	}

	public static Icon getResourceIcon()
	{
		return getIcon(ProjectResourceIcon.class);
	}

	public static Icon getDeleteIcon()
	{
		return getIcon(DeleteIcon.class);
	}

	public static Icon getStrategicPlanIcon()
	{
		return getIcon(StrategicPlanIcon.class);
	}
	
	public static Icon getProgressReportIcon()
	{
		return getIcon(ProgressReportIcon.class);
	}

	public static Icon getProgressPercentIcon()
	{
		return getIcon(ProgressPercentIcon.class);
	}

	public static Icon getWarningIcon()
	{
		return getIcon(WarningIcon.class);
	}

	public static Icon getCancelIcon()
	{
		return getIcon(CancelIcon.class);
	}
	
	private static Icon getSubTargetIcon()
	{
		return new SubTargetIcon();
	}
	
	public static Icon getDocumentIcon()
	{
		return getIcon(DOCUMENT_ICON_FILE_NAME);
	}

	public static Icon getFutureStatusIcon()
	{
		return getIcon(FutureStatusIcon.class);
	}
	
	private static HashMap<String, Icon> iconMap;
	private static final String EXPAND_ICON_FILE_NAME = "icons/expand.png";
	private static final String COLLAPSE_ICON_FILE_NAME = "icons/collapse.png";
	private static final String SPELLCHECK_ICON_FILE_NAME = "icons/spellcheck.png";
	private static final String ANALYSIS_ICON_FILE_NAME = "icons/analysis.png";
	private static final String COLLAPSE_ALL_ICON_FILE_NAME = "icons/collapseAll.png";
	private static final String EXPAND_ALL_ICON_FILE_NAME = "icons/expandAll.png";
	private static final String NEEDS_ATTENTION_ICON_FILE_NAME = "icons/needsAttentionIcon.png";
	private static final String DOCUMENT_ICON_FILE_NAME = "icons/document16.png";
}
