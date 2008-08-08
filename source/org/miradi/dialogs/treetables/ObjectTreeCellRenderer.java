/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.miradi.icons.ActivityIcon;
import org.miradi.icons.ConceptualModelIcon;
import org.miradi.icons.DirectThreatIcon;
import org.miradi.icons.GoalIcon;
import org.miradi.icons.IconManager;
import org.miradi.icons.IndicatorIcon;
import org.miradi.icons.IntermediateResultIcon;
import org.miradi.icons.KeyEcologicalAttributeIcon;
import org.miradi.icons.MeasurementIcon;
import org.miradi.icons.MethodIcon;
import org.miradi.icons.ObjectiveIcon;
import org.miradi.icons.ResultsChainIcon;
import org.miradi.icons.StrategyIcon;
import org.miradi.icons.TargetIcon;
import org.miradi.icons.TaskIcon;
import org.miradi.icons.ThreatReductionResultIcon;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.Factor;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Task;
import org.miradi.utils.HtmlUtilities;

public class ObjectTreeCellRenderer extends VariableHeightTreeCellRenderer
{		
	public ObjectTreeCellRenderer(ObjectTreeTable treeTableToUse)
	{
		super(treeTableToUse);
		
		projectMetaDataRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(projectMetaDataRenderer, IconManager.getImage(ProjectMetadata.getObjectType()), getBoldFont());

		targetRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(targetRenderer, new TargetIcon(), getBoldFont());

		directThreatRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(directThreatRenderer, new DirectThreatIcon(), getPlainFont());
		
		threatReductionResultRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(threatReductionResultRenderer, new ThreatReductionResultIcon(), getPlainFont());
		
		intermediateResultsRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(intermediateResultsRenderer, new IntermediateResultIcon(), getPlainFont());

		strategyRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(strategyRenderer, new StrategyIcon(), getBoldFont());

		objectiveRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(objectiveRenderer, new ObjectiveIcon(), getBoldFont());
		
		indicatorRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(indicatorRenderer, new IndicatorIcon(), getBoldFont());
		
		goalRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(goalRenderer, new GoalIcon(), getBoldFont());
		
		activityRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(activityRenderer, new ActivityIcon(), getPlainFont());

		keyEcologicalAttributeRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(keyEcologicalAttributeRenderer, new KeyEcologicalAttributeIcon(), getPlainFont());
		
		methodRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(methodRenderer, new MethodIcon(), getPlainFont());

		taskRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(taskRenderer, new TaskIcon(), getPlainFont());
		
		conceptualModelRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(conceptualModelRenderer, new ConceptualModelIcon(), getBoldFont());

		resultsChainRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(resultsChainRenderer, new ResultsChainIcon(), getBoldFont());

		stringNoIconRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(stringNoIconRenderer, null, ObjectTreeTable.createFristLevelFont(getPlainFont()));
		
		defaultRenderer = new DefaultTreeCellRenderer();
		defaultRenderer.setFont(getPlainFont());
		
		measurementRenderer = new DefaultTreeCellRenderer();
		setRendererDefaults(measurementRenderer, new MeasurementIcon(), getPlainFont());
	}
	
	public void setRendererDefaults(DefaultTreeCellRenderer renderer, Icon icon, Font font)
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
		else if(node.getType() == ObjectType.CAUSE)
			renderer = directThreatRenderer;
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
			renderer = getTaskRenderer((Task)node.getObject());
		else if(node.getType() == ObjectType.KEY_ECOLOGICAL_ATTRIBUTE)
			renderer = keyEcologicalAttributeRenderer;
		else if(node.getType() == ObjectType.MEASUREMENT)
			renderer = measurementRenderer;
		else if(node.getType() == ProjectMetadata.getObjectType())
			renderer = projectMetaDataRenderer;
		
		value = "<html>" + HtmlUtilities.plainStringWithNewlinesToHtml(value.toString());
		
		DefaultTreeCellRenderer rendererComponent = (DefaultTreeCellRenderer)renderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		return rendererComponent;
	}
	
	private TreeCellRenderer getTaskRenderer(Task task)
	{
		if(task.isActivity())
			return getRendererWithSetSharedTaskItalicFont(activityRenderer, task);
		if(task.isMethod())
			return getRendererWithSetSharedTaskItalicFont(methodRenderer, task);
		
		return taskRenderer;
	}
	
	private DefaultTreeCellRenderer getRendererWithSetSharedTaskItalicFont(DefaultTreeCellRenderer renderer, Task task)
	{
		renderer.setFont(getSharedTaskFont(task));
		return renderer;
	}
	
	protected DefaultTreeCellRenderer getStrategyRenderer(Factor factor)
	{
		return strategyRenderer;
	}
	
	private Font getBoldFont()
	{
		return deriveFont(Font.BOLD);
	}

	protected Font getPlainFont()
	{
		return deriveFont(Font.PLAIN);
	}
	
	private Font getSharedTaskFont(Task task)
	{
		int style = Font.PLAIN;
		if (task.isShared())
			style |= Font.ITALIC;

		if (task.isMethod())
			style |= Font.BOLD;
				
		return deriveFont(style);
	}
	
	private Font deriveFont(int style)
	{
		Font defaultFont = getMainWindow().getUserDataPanelFont();
		return defaultFont.deriveFont(style);
	}
	
	private DefaultTreeCellRenderer projectMetaDataRenderer;
	private DefaultTreeCellRenderer targetRenderer;
	private DefaultTreeCellRenderer strategyRenderer;
	private DefaultTreeCellRenderer objectiveRenderer;
	protected DefaultTreeCellRenderer goalRenderer;
	protected DefaultTreeCellRenderer indicatorRenderer;
	private DefaultTreeCellRenderer activityRenderer;
	private DefaultTreeCellRenderer methodRenderer;
	private DefaultTreeCellRenderer taskRenderer;
	private DefaultTreeCellRenderer conceptualModelRenderer;
	private DefaultTreeCellRenderer resultsChainRenderer;
	private DefaultTreeCellRenderer defaultRenderer;
	private DefaultTreeCellRenderer stringNoIconRenderer;
	private DefaultTreeCellRenderer keyEcologicalAttributeRenderer;
	private DefaultTreeCellRenderer directThreatRenderer;
	private DefaultTreeCellRenderer threatReductionResultRenderer;
	private DefaultTreeCellRenderer intermediateResultsRenderer;
	private	DefaultTreeCellRenderer measurementRenderer;
}