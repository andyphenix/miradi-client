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
package org.miradi.questions;

import javax.swing.Icon;

import org.miradi.icons.ActivityIcon;
import org.miradi.icons.AssignmentIcon;
import org.miradi.icons.ConceptualModelIcon;
import org.miradi.icons.ContributingFactorIcon;
import org.miradi.icons.DirectThreatIcon;
import org.miradi.icons.ExpenseAssignmentIcon;
import org.miradi.icons.GoalIcon;
import org.miradi.icons.HumanWelfareTargetIcon;
import org.miradi.icons.IndicatorIcon;
import org.miradi.icons.IntermediateResultIcon;
import org.miradi.icons.MeasurementIcon;
import org.miradi.icons.MethodIcon;
import org.miradi.icons.ObjectiveIcon;
import org.miradi.icons.ResultsChainIcon;
import org.miradi.icons.StrategyIcon;
import org.miradi.icons.TargetIcon;
import org.miradi.icons.TaskIcon;
import org.miradi.icons.ThreatReductionResultIcon;
import org.miradi.main.EAM;
import org.miradi.objects.Cause;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.Goal;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.Indicator;
import org.miradi.objects.IntermediateResult;
import org.miradi.objects.Measurement;
import org.miradi.objects.Objective;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.ThreatReductionResult;

public class RowConfigurationQuestion extends StaticChoiceQuestion
{
	public RowConfigurationQuestion()
	{
		super(getRowChoices());
	}

	private static ChoiceItem[] getRowChoices()
	{	
		return new ChoiceItem[]
		{
				createChoiceItem(ConceptualModelDiagram.getObjectType(), ConceptualModelDiagram.OBJECT_NAME, new ConceptualModelIcon()),
				createChoiceItem(ResultsChainDiagram.getObjectType(), ResultsChainDiagram.OBJECT_NAME, new ResultsChainIcon()),
				createChoiceItem(Target.getObjectType(), Target.OBJECT_NAME, new TargetIcon()),
				createChoiceItem(HumanWelfareTarget.getObjectType(), HumanWelfareTarget.OBJECT_NAME, new HumanWelfareTargetIcon()),
				createChoiceItem(Goal.getObjectType(), Goal.OBJECT_NAME, new GoalIcon()),
				createChoiceItem(Objective.getObjectType(), Objective.OBJECT_NAME, new ObjectiveIcon()),
				createChoiceItem(Cause.getObjectType(), Cause.OBJECT_NAME_THREAT, new DirectThreatIcon()),
				createChoiceItem(Cause.getObjectType(), Cause.OBJECT_NAME_CONTRIBUTING_FACTOR, new ContributingFactorIcon()), 
				createChoiceItem(ThreatReductionResult.getObjectType(), ThreatReductionResult.OBJECT_NAME, new ThreatReductionResultIcon()),
				createChoiceItem(IntermediateResult.getObjectType(), IntermediateResult.OBJECT_NAME, new IntermediateResultIcon()),
				createChoiceItem(Strategy.getObjectType(), Strategy.OBJECT_NAME, new StrategyIcon()),
				createChoiceItem(Task.getObjectType(), Task.ACTIVITY_NAME, new ActivityIcon()),
				createChoiceItem(Indicator.getObjectType(), Indicator.OBJECT_NAME, new IndicatorIcon()),
				createChoiceItem(Task.getObjectType(), Task.METHOD_NAME, new MethodIcon()),
				createChoiceItem(Task.getObjectType(), Task.OBJECT_NAME, new TaskIcon()),
				createChoiceItem(Measurement.getObjectType(), Measurement.OBJECT_NAME, new MeasurementIcon()),
				createChoiceItem(ResourceAssignment.getObjectType(), ResourceAssignment.OBJECT_NAME, new AssignmentIcon()),
				createChoiceItem(ExpenseAssignment.getObjectType(), ExpenseAssignment.OBJECT_NAME, new ExpenseAssignmentIcon()),
		};
	}

	private static ChoiceItem createChoiceItem(int objectType, String objectName, Icon iconToUse)
	{
		return new ChoiceItem(objectName, EAM.fieldLabel(objectType, objectName), iconToUse);
	}
}
