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
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.Factor;
import org.miradi.objects.FundingSource;
import org.miradi.objects.Goal;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.Objective;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Task;
import org.miradi.utils.MiradiResourceImageIcon;
 
public class IconManager
{
	public static void initialize()
	{
		iconMap = new HashMap();
		
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
	}
	
	private static void addIcon(Icon icon)
	{
		iconMap.put(icon.getClass().getSimpleName(), icon);
	}
	
	private static Icon getIcon(Class iconClass)
	{
		String iconName = iconClass.getSimpleName();
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
			return new AssignmentIcon();
		
		if (ExpenseAssignment.is(type))
			return new ExpenseAssignmentIcon();
		
		if (ProjectResource.is(type))
			return new ProjectResourceIcon();
		
		if (FundingSource.is(type))
			return new FundingSourceIcon();
		
		if (AccountingCode.is(type))
			return new AccountingCodeIcon();
		
		throw new RuntimeException("Could not find icon for type:" + type);
	}

	private static Icon getFactorIcon(Factor factor)
	{
		if (factor.isDirectThreat())
			return getThreatIcon();
		
		if (factor.isContributingFactor())
			return new ContributingFactorIcon();
		
		if (factor.isStrategy())
			return new StrategyIcon();
		
		if (factor.isTarget())
			return new TargetIcon();
		
		if (factor.isStress())
			return new StressIcon();
		
		if (factor.isActivity())
			return new ActivityIcon();
		
		if (factor.isIntermediateResult())
			return new IntermediateResultIcon();
		
		if (factor.isThreatReductionResult())
			return new ThreatReductionResultIcon();
		
		if (factor.isTextBox())
			return new TextBoxIcon();
		
		if (factor.isGroupBox())
			return new GroupBoxIcon();
		
		if (factor.isHumanWelfareTarget())
			return new HumanWelfareTargetIcon();	

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
		if(expandIcon == null)
			expandIcon = new MiradiResourceImageIcon(EXPAND_ICON_FILE_NAME);
		return expandIcon;
	}

	public static Icon getCollapseIcon()
	{
		if(collapseIcon == null)
			collapseIcon = new MiradiResourceImageIcon(COLLAPSE_ICON_FILE_NAME);
		return collapseIcon;
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
	
	public static Icon getWarningIcon()
	{
		return getIcon(WarningIcon.class);
	}

	public static Icon getCancelIcon()
	{
		return getIcon(CancelIcon.class);
	}

	private static Icon expandIcon;
	private static Icon collapseIcon;
	private static HashMap<String, Icon> iconMap;
	private static final String EXPAND_ICON_FILE_NAME = "icons/expand.png";
	private static final String COLLAPSE_ICON_FILE_NAME = "icons/collapse.png";
}
