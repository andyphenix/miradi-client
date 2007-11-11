/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.tablerenderers;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.conservationmeasures.eam.dialogs.planning.propertiesPanel.PlanningViewAbstractTreeTableSyncedTableModel;
import org.conservationmeasures.eam.dialogs.planning.treenodes.PlanningTreeTaskNode;
import org.conservationmeasures.eam.dialogs.treetables.TreeTableNode;
import org.conservationmeasures.eam.icons.AllocatedCostIcon;
import org.conservationmeasures.eam.objects.Task;

public class PossiblyAllocatedNumericTableCellRenderer extends NumericTableCellRenderer
{
	public PossiblyAllocatedNumericTableCellRenderer(PlanningViewAbstractTreeTableSyncedTableModel providerToUse)
	{
		super(providerToUse);
		allocatedIcon = new AllocatedCostIcon();
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int tableColumn)
	{
		JLabel renderer = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, tableColumn);
		annotateIfAllocated(row, renderer);
		return renderer;
	}

	private void annotateIfAllocated(int row, JLabel labelComponent)
	{
		labelComponent.setIcon(null);
		
		if(labelComponent.getText().length() == 0)
			return;
		
		TreeTableNode node = getNodeForRow(row);
		if(node.getType() != Task.getObjectType())
			return;
		
		PlanningTreeTaskNode taskNode = (PlanningTreeTaskNode) node;
		double nodeCostAlloctionProportion = taskNode.getCostAllocationProportion();
		
		if (Double.compare(nodeCostAlloctionProportion, 1.0) < 0)
			labelComponent.setIcon(allocatedIcon);
		
	}
	
	protected TreeTableNode getNodeForRow(int row)
	{
		PlanningViewAbstractTreeTableSyncedTableModel model = (PlanningViewAbstractTreeTableSyncedTableModel)getObjectProvider();
		return model.getNodeForRow(row);
	}
	
	Icon allocatedIcon;
}
