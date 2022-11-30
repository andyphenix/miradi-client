/* 
Copyright 2005-2022, Foundations of Success, Bethesda, Maryland
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

package org.miradi.dialogs.treetables;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.miradi.dialogs.tablerenderers.PlanningViewFontProvider;
import org.miradi.icons.*;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.*;
import org.miradi.schemas.ConceptualModelDiagramSchema;
import org.miradi.schemas.GroupBoxSchema;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.schemas.ResultsChainDiagramSchema;
import org.miradi.schemas.TextBoxSchema;

public class ObjectTreeCellRenderer extends VariableHeightTreeCellRenderer
{		
	public ObjectTreeCellRenderer(ObjectTreeTable treeTableToUse)
	{
		super(treeTableToUse);
		
		projectMetaDataRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(projectMetaDataRenderer, IconManager.getImage(ProjectMetadataSchema.getObjectType()), getBoldFont());

		targetRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(targetRenderer, new TargetIcon(), getBoldFont());
		
		humanWelfareTargetRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(humanWelfareTargetRenderer, new HumanWelfareTargetIcon(), getBoldFont());

		biophysicalFactorRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(biophysicalFactorRenderer, new BiophysicalFactorIcon(), getPlainFont());

		biophysicalResultRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(biophysicalResultRenderer, new BiophysicalResultIcon(), getPlainFont());

		directThreatRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(directThreatRenderer, new DirectThreatIcon(), getPlainFont());
		
		threatReductionResultRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(threatReductionResultRenderer, new ThreatReductionResultIcon(), getPlainFont());
		
		intermediateResultsRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(intermediateResultsRenderer, new IntermediateResultIcon(), getPlainFont());

		strategyRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(strategyRenderer, IconManager.getStrategyIcon(), getBoldFont());

		objectiveRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(objectiveRenderer, new ObjectiveIcon(), getBoldFont());
		
		indicatorRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(indicatorRenderer, IconManager.getIndicatorIcon(), getBoldFont());
		
		goalRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(goalRenderer, new GoalIcon(), getBoldFont());
		
		futureStatusRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(futureStatusRenderer, new FutureStatusIcon(), getPlainFont());
		
		activityRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(activityRenderer, new ActivityIcon(), getPlainFont());

		monitoringActivityRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(monitoringActivityRenderer, new MonitoringActivityIcon(), getPlainFont());

		stressRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(stressRenderer, new StressIcon(), getPlainFont());
		
		subTargetRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(subTargetRenderer, new SubTargetIcon(), getPlainFont());
		
		keyEcologicalAttributeRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(keyEcologicalAttributeRenderer, new KeyEcologicalAttributeIcon(), getPlainFont());
		
		methodRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(methodRenderer, new MethodIcon(), getPlainFont());

		taskRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(taskRenderer, new TaskIcon(), getPlainFont());
		
		outputRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(outputRenderer, new OutputIcon(), getPlainFont());

		conceptualModelRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(conceptualModelRenderer, new ConceptualModelIcon(), getBoldFont());

		resultsChainRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(resultsChainRenderer, new ResultsChainIcon(), getBoldFont());

		stringNoIconRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(stringNoIconRenderer, null, ObjectTreeTable.createFirstLevelFont(getPlainFont()));
		
		defaultRenderer = createRenderer(treeTableToUse);
		defaultRenderer.setFont(getPlainFont());
		
		measurementRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(measurementRenderer, new MeasurementIcon(), getPlainFont());
		
		textBoxRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(textBoxRenderer, new TextBoxIcon(), getPlainFont());
		
		groupBoxRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(groupBoxRenderer, new GroupBoxIcon(), getPlainFont());
		
		contributingFactorRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(contributingFactorRenderer, new ContributingFactorIcon(), getPlainFont());
		
		assignmentRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(assignmentRenderer, new AssignmentIcon(), getPlainFont());
		
		expenseAssignmentRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(expenseAssignmentRenderer, new ExpenseAssignmentIcon(), getPlainFont());
		
		projectResourceRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(projectResourceRenderer, new ProjectResourceIcon(), getPlainFont());
		
		fundingSourceRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(fundingSourceRenderer, new FundingSourceIcon(), getPlainFont());
		
		accountingCodeRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(accountingCodeRenderer, new AccountingCodeIcon(), getPlainFont());
		
		categoryOneRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(categoryOneRenderer, new BudgetCategoryOneIcon(), getPlainFont());
		
		categoryTwoRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(categoryTwoRenderer, new BudgetCategoryTwoIcon(), getPlainFont());
		
		scopeBoxRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(scopeBoxRenderer, new ScopeBoxIcon(), getPlainFont());

		analyticalQuestionRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(analyticalQuestionRenderer, new AnalyticalQuestionIcon(), getPlainFont());

		subAssumptionRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(subAssumptionRenderer, new SubAssumptionIcon(), getPlainFont());
	}
	
	private VariableHeightTreeCellRenderer createRenderer(ObjectTreeTable treeTableToUse)
	{
		return new VariableHeightTreeCellRenderer(treeTableToUse);
	}
	
	private void setRendererDefaults(VariableHeightTreeCellRenderer renderer, Icon icon, Font font)
	{
		renderer.setClosedIcon(icon);
		renderer.setOpenIcon(icon);
		renderer.setLeafIcon(icon);
		renderer.setFont(font);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocusToUse)
	{
		TreeCellRenderer renderer = defaultRenderer;
		
		TreeTableNode node = (TreeTableNode) value;
		if (node.getType() == ObjectType.FAKE)
			renderer  = stringNoIconRenderer;
		else if(node.getType() == ConceptualModelDiagramSchema.getObjectType())
			renderer = conceptualModelRenderer;
		else if(node.getType() == ResultsChainDiagramSchema.getObjectType())
			renderer = resultsChainRenderer;
		else if(node.getType() == ObjectType.TARGET)
			renderer = targetRenderer;
		else if (node.getType() == ObjectType.HUMAN_WELFARE_TARGET)
			renderer = humanWelfareTargetRenderer;
		else if (node.getType() == ObjectType.BIOPHYSICAL_FACTOR)
			renderer = biophysicalFactorRenderer;
		else if (node.getType() == ObjectType.BIOPHYSICAL_RESULT)
			renderer = biophysicalResultRenderer;
		else if(node.getType() == ObjectType.CAUSE)
			renderer = getCauseRenderer((Cause)node.getObject());
		else if(node.getType() == ObjectType.THREAT_REDUCTION_RESULT)
			renderer = threatReductionResultRenderer;
		else if(node.getType() == ObjectType.INTERMEDIATE_RESULT)
			renderer = intermediateResultsRenderer;
		else if(node.getType() == ObjectType.INDICATOR)
			renderer = indicatorRenderer;
		else if(node.getType() == ObjectType.METHOD)
			renderer = methodRenderer;
		else if(node.getType() == ObjectType.STRATEGY)
			renderer = getStrategyRenderer();
		else if(node.getType() == ObjectType.OBJECTIVE)
			renderer = objectiveRenderer;
		else if(node.getType() == ObjectType.GOAL)
			renderer = goalRenderer;
		else if(node.getType() == ObjectType.TASK)
			renderer = getTaskRenderer((Task)node.getObject());
		else if(node.getType() == ObjectType.OUTPUT)
			renderer = outputRenderer;
		else if(node.getType() == ObjectType.KEY_ECOLOGICAL_ATTRIBUTE)
			renderer = keyEcologicalAttributeRenderer;
		else if(node.getType() == ObjectType.STRESS)
			renderer = stressRenderer;
		else if(SubTarget.is(node.getType()))
			renderer = subTargetRenderer;
		else if(node.getType() == ObjectType.MEASUREMENT)
			renderer = measurementRenderer;
		else if(node.getType() == ProjectMetadataSchema.getObjectType())
			renderer = projectMetaDataRenderer;
		else if(node.getType() == TextBoxSchema.getObjectType())
			renderer = textBoxRenderer;
		else if(node.getType() == GroupBoxSchema.getObjectType())
			renderer = groupBoxRenderer;
		else if(ResourceAssignment.is(node.getType()))
			renderer = assignmentRenderer;
		else if(ExpenseAssignment.is(node.getType()))
			renderer = expenseAssignmentRenderer;
		else if(ProjectResource.is(node.getType()))
			renderer = projectResourceRenderer;
		else if(FundingSource.is(node.getType()))
			renderer = fundingSourceRenderer;
		else if (AccountingCode.is(node.getType()))
			renderer = accountingCodeRenderer;
		else if (BudgetCategoryOne.is(node.getType()))
			renderer = categoryOneRenderer;
		else if (BudgetCategoryTwo.is(node.getType()))
			renderer = categoryTwoRenderer;
		else if (ScopeBox.is(node.getType()))
			renderer = scopeBoxRenderer;
		else if (FutureStatus.is(node.getType()))
			renderer = futureStatusRenderer;
		else if (AnalyticalQuestion.is(node.getType()))
			renderer = analyticalQuestionRenderer;
		else if (SubAssumption.is(node.getType()))
			renderer = subAssumptionRenderer;

		Component rendererComponent = renderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocusToUse);
		return rendererComponent;
	}

	private VariableHeightTreeCellRenderer getCauseRenderer(Cause cause)
	{
		if (cause.isDirectThreat())
			return directThreatRenderer;

		return contributingFactorRenderer;
	}

	private TreeCellRenderer getTaskRenderer(Task task)
	{
		if(task.isMonitoringActivity())
			return getTaskRenderer(monitoringActivityRenderer, task);
		if(task.isActivity())
			return getTaskRenderer(activityRenderer, task);

		return taskRenderer;
	}
	
	private VariableHeightTreeCellRenderer getTaskRenderer(VariableHeightTreeCellRenderer renderer, Task task)
	{
		Font taskFont = new PlanningViewFontProvider(getMainWindow()).getFont(task);
		renderer.setFont(taskFont);
		return renderer;
	}
	
	private VariableHeightTreeCellRenderer getStrategyRenderer()
	{
		return strategyRenderer;
	}
	
	private Font getBoldFont()
	{
		return deriveFont(getMainWindow(), Font.BOLD);
	}

	private Font getPlainFont()
	{
		return deriveFont(getMainWindow(), Font.PLAIN);
	}
	
	private static Font deriveFont(MainWindow mainWindow, int style)
	{
		Font defaultFont = mainWindow.getUserDataPanelFont();
		return defaultFont.deriveFont(style);
	}
	
	private VariableHeightTreeCellRenderer projectMetaDataRenderer;
	private VariableHeightTreeCellRenderer targetRenderer;
	private VariableHeightTreeCellRenderer humanWelfareTargetRenderer;
	private VariableHeightTreeCellRenderer strategyRenderer;
	private VariableHeightTreeCellRenderer objectiveRenderer;
	private VariableHeightTreeCellRenderer goalRenderer;
	private VariableHeightTreeCellRenderer indicatorRenderer;
	private VariableHeightTreeCellRenderer activityRenderer;
	private VariableHeightTreeCellRenderer monitoringActivityRenderer;
	private VariableHeightTreeCellRenderer stressRenderer;
	private VariableHeightTreeCellRenderer subTargetRenderer;
	private VariableHeightTreeCellRenderer methodRenderer;
	private VariableHeightTreeCellRenderer taskRenderer;
	private VariableHeightTreeCellRenderer outputRenderer;
	private VariableHeightTreeCellRenderer conceptualModelRenderer;
	private VariableHeightTreeCellRenderer resultsChainRenderer;
	private VariableHeightTreeCellRenderer defaultRenderer;
	private VariableHeightTreeCellRenderer stringNoIconRenderer;
	private VariableHeightTreeCellRenderer keyEcologicalAttributeRenderer;
	private VariableHeightTreeCellRenderer directThreatRenderer;
	private VariableHeightTreeCellRenderer threatReductionResultRenderer;
	private VariableHeightTreeCellRenderer intermediateResultsRenderer;
	private	VariableHeightTreeCellRenderer measurementRenderer;
	private	VariableHeightTreeCellRenderer textBoxRenderer;
	private	VariableHeightTreeCellRenderer groupBoxRenderer;
	private VariableHeightTreeCellRenderer contributingFactorRenderer;
	private VariableHeightTreeCellRenderer biophysicalFactorRenderer;
	private VariableHeightTreeCellRenderer biophysicalResultRenderer;
	private VariableHeightTreeCellRenderer assignmentRenderer;
	private VariableHeightTreeCellRenderer expenseAssignmentRenderer;
	private VariableHeightTreeCellRenderer projectResourceRenderer;
	private VariableHeightTreeCellRenderer fundingSourceRenderer;
	private VariableHeightTreeCellRenderer accountingCodeRenderer;
	private VariableHeightTreeCellRenderer categoryOneRenderer;
	private VariableHeightTreeCellRenderer categoryTwoRenderer;
	private	VariableHeightTreeCellRenderer scopeBoxRenderer;
	private VariableHeightTreeCellRenderer futureStatusRenderer;
	private VariableHeightTreeCellRenderer analyticalQuestionRenderer;
	private VariableHeightTreeCellRenderer subAssumptionRenderer;

}