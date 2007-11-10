/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning.propertiesPanel;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.table.TableCellRenderer;

import org.conservationmeasures.eam.dialogs.treetables.AstrickRenderer;
import org.conservationmeasures.eam.dialogs.treetables.TreeTableNode;
import org.conservationmeasures.eam.dialogs.treetables.TreeTableWithIcons;
import org.conservationmeasures.eam.main.AppPreferences;
import org.conservationmeasures.eam.utils.TableWithTreeTableNodes;


public class PlanningViewBudgetAnnualTotalsTable extends TableWithTreeTableNodes
{
	public PlanningViewBudgetAnnualTotalsTable(PlanningViewBudgetAnnualTotalTableModel model)
	{
		super(model);	
	}
	
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		return new AstrickRenderer(this, getColumnBackGroundColor(getColumnCount(), column));	
	}
	
	protected int getColumnWidth(int column)
	{
		return 125;
	}	
	
	public Color getColumnBackGroundColor(int columnCount, int column)
	{
		final int TOTALS_COLUMN = columnCount - 1;
		if (column == TOTALS_COLUMN)
			return AppPreferences.BUDGET_TOTAL_TABLE_BACKGROUND;
		
		return AppPreferences.BUDGET_TABLE_BACKGROUND;
	}
	
	public Font getRowFont(int row)
	{
		PlanningViewBudgetAnnualTotalTableModel model = (PlanningViewBudgetAnnualTotalTableModel) getModel();
		TreeTableNode node = model.getNodeForRow(row);
		return TreeTableWithIcons.getSharedTaskFont(node);
	}
	
	public int getColumnAlignment()
	{
		return JLabel.RIGHT;
	}
	
	public String getUniqueTableIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}
	
	public static final String UNIQUE_IDENTIFIER = "PlanningViewBudgetAnnualTotalsTable";
}
