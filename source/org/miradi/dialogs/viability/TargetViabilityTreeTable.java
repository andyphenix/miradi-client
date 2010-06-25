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
package org.miradi.dialogs.viability;


import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.miradi.actions.ActionExpandToGoal;
import org.miradi.actions.ActionExpandToHumanWelfareTarget;
import org.miradi.actions.ActionExpandToIndicator;
import org.miradi.actions.ActionExpandToKeyEcologicalAttribute;
import org.miradi.actions.ActionExpandToMeasurement;
import org.miradi.actions.ActionExpandToMenu;
import org.miradi.actions.ActionExpandToTarget;
import org.miradi.dialogs.tablerenderers.ChoiceItemTableCellRendererFactory;
import org.miradi.dialogs.tablerenderers.FontForObjectProvider;
import org.miradi.dialogs.tablerenderers.MultiLineObjectTableCellRendererOnlyFactory;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.dialogs.tablerenderers.ViabilityViewFontProvider;
import org.miradi.dialogs.treetables.ObjectTreeCellRenderer;
import org.miradi.dialogs.treetables.ObjectTreeTable;
import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.dialogs.treetables.TreeTableWithStateSaving;
import org.miradi.main.MainWindow;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Goal;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.Target;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.StatusQuestion;

public class TargetViabilityTreeTable extends TreeTableWithStateSaving implements RowColumnBaseObjectProvider 
{
	public TargetViabilityTreeTable(MainWindow mainWindowToUse, GenericViabilityTreeModel targetViabilityModelToUse) throws Exception
	{
		super(mainWindowToUse, targetViabilityModelToUse);
		FontForObjectProvider fontProvider = new ViabilityViewFontProvider(getMainWindow());
		setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		getTree().setShowsRootHandles(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		getTree().setCellRenderer(new ViabilityTreeCellRenderer(this));
		setColumnHeaderRenderers();
		statusQuestionRenderer = new ChoiceItemTableCellRendererFactory(this, fontProvider);
		multiLineRenderer = new MultiLineObjectTableCellRendererOnlyFactory(this, fontProvider);
		rebuildTableCompletely();
	}
	
	@Override
	public int getProportionShares(int row)
	{
		return getNodeForRow(row).getProportionShares();
	}
	
	@Override
	public boolean areBudgetValuesAllocated(int row)
	{
		return getNodeForRow(row).areBudgetValuesAllocated();
	}

	@Override
	public BaseObject getBaseObjectForRowColumn(int row, int column)
	{
		return getNodeForRow(row).getObject();
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int tableColumn)
	{
		if(tableColumn == 0)
			return super.getCellRenderer(row, tableColumn);
		
		int modelColumn = convertColumnIndexToModel(tableColumn);
		if (isChoiceItemCell(row, modelColumn))
			return statusQuestionRenderer;
		
		return multiLineRenderer;
	}
	
	public boolean isChoiceItemCell(int row, int modelColumn)
	{
		String columnTag = getColumnTag(modelColumn);
		boolean isChoiceItemColumn =
			columnTag == Target.TAG_VIABILITY_MODE || 
			columnTag == ViabilityTreeModel.VIRTUAL_TAG_STATUS ||
			columnTag == BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE ||
			columnTag == AbstractTarget.PSEUDO_TAG_TARGET_VIABILITY || 
			columnTag == KeyEcologicalAttribute.PSEUDO_TAG_VIABILITY_STATUS || 
			columnTag == Measurement.TAG_STATUS_CONFIDENCE ||
			columnTag == KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE;
	
		if (isChoiceItemColumn)
			return true;
		
		return isMeasurementValueCell(row, modelColumn);
	}

	public boolean isMeasurementValueCell(int row, int modelColumn)
	{
		TreeTableNode node = (TreeTableNode)getRawObjectForRow(row);
		boolean isMeasurementNode = node.getType() == Measurement.getObjectType();
		boolean isFutureStatusNode = node.getType() == Goal.getObjectType();
		
		String columnTag = getColumnTag(modelColumn);
		return (isMeasurementNode || isFutureStatusNode) && isValueColumn(columnTag);
	}
	
	public boolean isTextCell(int row, int modelColumn)
	{
		TreeTableNode node = (TreeTableNode)getRawObjectForRow(row);
		boolean isIndicatorNode = Indicator.is(node.getType());
		
		String columnTag = getColumnTag(modelColumn);
		return isIndicatorNode && isValueColumn(columnTag);
	}

	public boolean isValueColumn(String columnTag)
	{
		return getViabilityModel().isChoiceItemColumn(columnTag);
	}
	
	private String getColumnTag(int modelColumn)
	{
		return getViabilityModel().getColumnTag(modelColumn);
	}
	
	private void setColumnHeaderRenderers()
	{
		ColumnHeaderRenderer headerRenderer = new ColumnHeaderRenderer();
		for (int i = 0; i < getModel().getColumnCount(); ++i)
		{
			getTableHeader().setDefaultRenderer(headerRenderer);
		}
	}
	
	@Override
	protected Set<Class> getRelevantActions()
	{
		HashSet<Class> relevantActions = new HashSet<Class>();
		relevantActions.addAll(super.getRelevantActions());
		relevantActions.add(ActionExpandToMenu.class);
		relevantActions.add(ActionExpandToTarget.class);
		relevantActions.add(ActionExpandToHumanWelfareTarget.class);
		relevantActions.add(ActionExpandToKeyEcologicalAttribute.class);
		relevantActions.add(ActionExpandToIndicator.class);
		relevantActions.add(ActionExpandToGoal.class);
		relevantActions.add(ActionExpandToMeasurement.class);
		
		return relevantActions;
	}

	public static class ColumnHeaderRenderer extends DefaultTableCellRenderer
	{
		public ColumnHeaderRenderer()
		{
			statusQuestion = new StatusQuestion();
		}

		 @Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
		 {
			 JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			 renderer.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			 renderer.setHorizontalAlignment(JLabel.CENTER);
			 renderer.setBackground(table.getTableHeader().getBackground());
			 renderer.setForeground(table.getTableHeader().getForeground());
			
			 ChoiceItem choice = statusQuestion.findChoiceByLabel(renderer.getText());
			 if (choice != null)
			 {
				renderer.setBackground(choice.getColor());
			 	renderer.setForeground(Color.BLACK);
			 }
			
			 return renderer;
		 }

		 private StatusQuestion statusQuestion;
	}
	
	public class ViabilityTreeCellRenderer extends ObjectTreeCellRenderer
	{
		public ViabilityTreeCellRenderer(ObjectTreeTable treeTableToUse)
		{
			super(treeTableToUse);
			indicatorRenderer.setFont(getPlainFont());
			goalRenderer.setFont(getPlainFont());
		}
	}
	
	public GenericViabilityTreeModel getViabilityModel()
	{
		return (GenericViabilityTreeModel)getTreeTableModel();
	}

	private ChoiceItemTableCellRendererFactory statusQuestionRenderer;
	private MultiLineObjectTableCellRendererOnlyFactory multiLineRenderer;
}
