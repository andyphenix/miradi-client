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

package org.miradi.dialogs.treetables;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.miradi.dialogs.tablerenderers.PlanningViewFontProvider;
import org.miradi.icons.AccountingCodeIcon;
import org.miradi.icons.ActivityIcon;
import org.miradi.icons.AssignmentIcon;
import org.miradi.icons.ConceptualModelIcon;
import org.miradi.icons.ContributingFactorIcon;
import org.miradi.icons.DirectThreatIcon;
import org.miradi.icons.ExpenseAssignmentIcon;
import org.miradi.icons.FundingSourceIcon;
import org.miradi.icons.GoalIcon;
import org.miradi.icons.GroupBoxIcon;
import org.miradi.icons.HumanWelfareTargetIcon;
import org.miradi.icons.IconManager;
import org.miradi.icons.IndicatorIcon;
import org.miradi.icons.IntermediateResultIcon;
import org.miradi.icons.KeyEcologicalAttributeIcon;
import org.miradi.icons.MeasurementIcon;
import org.miradi.icons.MethodIcon;
import org.miradi.icons.ObjectiveIcon;
import org.miradi.icons.ProjectResourceIcon;
import org.miradi.icons.ResultsChainIcon;
import org.miradi.icons.StrategyIcon;
import org.miradi.icons.StressIcon;
import org.miradi.icons.TargetIcon;
import org.miradi.icons.TaskIcon;
import org.miradi.icons.TextBoxIcon;
import org.miradi.icons.ThreatReductionResultIcon;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.AccountingCode;
import org.miradi.objects.Cause;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.Factor;
import org.miradi.objects.FundingSource;
import org.miradi.objects.GroupBox;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Task;
import org.miradi.objects.TextBox;

public class ObjectTreeCellRenderer extends VariableHeightTreeCellRenderer
{		
	public ObjectTreeCellRenderer(ObjectTreeTable treeTableToUse)
	{
		super(treeTableToUse);
		
		projectMetaDataRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(projectMetaDataRenderer, IconManager.getImage(ProjectMetadata.getObjectType()), getBoldFont());

		targetRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(targetRenderer, new TargetIcon(), getBoldFont());
		
		humanWelfareTargetRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(humanWelfareTargetRenderer, new HumanWelfareTargetIcon(), getBoldFont());

		directThreatRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(directThreatRenderer, new DirectThreatIcon(), getPlainFont());
		
		threatReductionResultRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(threatReductionResultRenderer, new ThreatReductionResultIcon(), getPlainFont());
		
		intermediateResultsRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(intermediateResultsRenderer, new IntermediateResultIcon(), getPlainFont());

		strategyRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(strategyRenderer, new StrategyIcon(), getBoldFont());

		objectiveRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(objectiveRenderer, new ObjectiveIcon(), getBoldFont());
		
		indicatorRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(indicatorRenderer, new IndicatorIcon(), getBoldFont());
		
		goalRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(goalRenderer, new GoalIcon(), getBoldFont());
		
		activityRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(activityRenderer, new ActivityIcon(), getPlainFont());

		stressRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(stressRenderer, new StressIcon(), getPlainFont());
		
		keyEcologicalAttributeRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(keyEcologicalAttributeRenderer, new KeyEcologicalAttributeIcon(), getPlainFont());
		
		methodRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(methodRenderer, new MethodIcon(), getPlainFont());

		taskRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(taskRenderer, new TaskIcon(), getPlainFont());
		
		conceptualModelRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(conceptualModelRenderer, new ConceptualModelIcon(), getBoldFont());

		resultsChainRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(resultsChainRenderer, new ResultsChainIcon(), getBoldFont());

		stringNoIconRenderer = createRenderer(treeTableToUse);
		setRendererDefaults(stringNoIconRenderer, null, ObjectTreeTable.createFristLevelFont(getPlainFont()));
		
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
	}
	
	VariableHeightTreeCellRenderer createRenderer(ObjectTreeTable treeTableToUse)
	{
		return new VariableHeightTreeCellRenderer(treeTableToUse);
	}
	
	public void setRendererDefaults(VariableHeightTreeCellRenderer renderer, Icon icon, Font font)
	{
		renderer.setClosedIcon(icon);
		renderer.setOpenIcon(icon);
		renderer.setLeafIcon(icon);
		renderer.setFont(font);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocusToUse)
	{
		TreeCellRenderer renderer = defaultRenderer;
		
		TreeTableNode node = (TreeTableNode) value;
		if (node.getType() == ObjectType.FAKE)
			renderer  = stringNoIconRenderer;
		else if(node.getType() == ConceptualModelDiagram.getObjectType())
			renderer = conceptualModelRenderer;
		else if(node.getType() == ResultsChainDiagram.getObjectType())
			renderer = resultsChainRenderer;
		else if(node.getType() == ObjectType.TARGET)
			renderer = targetRenderer;
		else if (node.getType() == ObjectType.HUMAN_WELFARE_TARGET)
			renderer = humanWelfareTargetRenderer;
		else if(node.getType() == ObjectType.CAUSE)
			renderer = getCauseRenderer((Cause)node.getObject());
		else if(node.getType() == ObjectType.THREAT_REDUCTION_RESULT)
			renderer = threatReductionResultRenderer;
		else if(node.getType() == ObjectType.INTERMEDIATE_RESULT)
			renderer = intermediateResultsRenderer;
		else if(node.getType() == ObjectType.INDICATOR)
			renderer = indicatorRenderer;
		else if(node.getType() == ObjectType.STRATEGY)
			renderer = getStrategyRenderer((Factor)node.getObject());
		else if(node.getType() == ObjectType.OBJECTIVE)
			renderer = objectiveRenderer;
		else if(node.getType() == ObjectType.GOAL)
			renderer = goalRenderer;
		else if(node.getType() == ObjectType.TASK)
			renderer = getTaskRenderer((Task)node.getObject(), node.getProportionShares());
		else if(node.getType() == ObjectType.KEY_ECOLOGICAL_ATTRIBUTE)
			renderer = keyEcologicalAttributeRenderer;
		else if(node.getType() == ObjectType.STRESS)
			renderer = stressRenderer;
		else if(node.getType() == ObjectType.MEASUREMENT)
			renderer = measurementRenderer;
		else if(node.getType() == ProjectMetadata.getObjectType())
			renderer = projectMetaDataRenderer;
		else if(node.getType() == TextBox.getObjectType())
			renderer = textBoxRenderer;
		else if(node.getType() == GroupBox.getObjectType())
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
		
		Component rendererComponent = renderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocusToUse);
		return rendererComponent;
	}

	private VariableHeightTreeCellRenderer getCauseRenderer(Cause cause)
	{
		if (cause.isDirectThreat())
			return directThreatRenderer;
		
		return contributingFactorRenderer;
	}
	
	private TreeCellRenderer getTaskRenderer(Task task, int proportionShares)
	{
		if(task.isActivity())
			return getRendererWithSetSharedTaskItalicFont(activityRenderer, task, proportionShares);
		if(task.isMethod())
			return getRendererWithSetSharedTaskItalicFont(methodRenderer, task, proportionShares);
		
		return taskRenderer;
	}
	
	private VariableHeightTreeCellRenderer getRendererWithSetSharedTaskItalicFont(VariableHeightTreeCellRenderer renderer, Task task, int proportionShares)
	{
		Font taskFont = new PlanningViewFontProvider(getMainWindow()).deriveTaskFont(task, proportionShares);
		renderer.setFont(taskFont);
		return renderer;
	}
	
	protected VariableHeightTreeCellRenderer getStrategyRenderer(Factor factor)
	{
		return strategyRenderer;
	}
	
	private Font getBoldFont()
	{
		return deriveFont(getMainWindow(), Font.BOLD);
	}

	protected Font getPlainFont()
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
	protected VariableHeightTreeCellRenderer goalRenderer;
	protected VariableHeightTreeCellRenderer indicatorRenderer;
	private VariableHeightTreeCellRenderer activityRenderer;
	private VariableHeightTreeCellRenderer stressRenderer;
	private VariableHeightTreeCellRenderer methodRenderer;
	private VariableHeightTreeCellRenderer taskRenderer;
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
	private VariableHeightTreeCellRenderer assignmentRenderer;
	private VariableHeightTreeCellRenderer expenseAssignmentRenderer;
	private VariableHeightTreeCellRenderer projectResourceRenderer;
	private VariableHeightTreeCellRenderer fundingSourceRenderer;
	private VariableHeightTreeCellRenderer accountingCodeRenderer;
	
}