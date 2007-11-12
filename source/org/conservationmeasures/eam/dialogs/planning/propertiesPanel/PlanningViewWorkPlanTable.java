/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning.propertiesPanel;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.conservationmeasures.eam.dialogs.fieldComponents.PanelTextField;
import org.conservationmeasures.eam.dialogs.tablerenderers.BasicTableCellRenderer;
import org.conservationmeasures.eam.dialogs.tablerenderers.DefaultFontProvider;
import org.conservationmeasures.eam.dialogs.tablerenderers.NumericTableCellRenderer;
import org.conservationmeasures.eam.main.AppPreferences;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.SingleClickAutoSelectCellEditor;

public class PlanningViewWorkPlanTable extends PlanningViewAbstractTableWithPreferredScrollableViewportSize
{
	public PlanningViewWorkPlanTable(Project projectToUse, PlanningViewAbstractBudgetTableModel modelToUse) throws Exception
	{
		super(modelToUse);
		setBackground(getColumnBackGroundColor(0));	
		setSingleCellEditor();
		renderer = new NumericTableCellRenderer(modelToUse, new DefaultFontProvider());
	}
	
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		renderer.setCellBackgroundColor(getColumnBackGroundColor(column));
		return renderer;	
	}
	
	public Color getColumnBackGroundColor(int column)
	{
		return AppPreferences.WORKPLAN_TABLE_BACKGROUND;
	}

	private void setSingleCellEditor()
	{
		int colCount = getColumnCount();
		for (int i = 0; i < colCount; i++)
		{
			int modelColumn = convertColumnIndexToModel(i);
			TableColumn column = getColumnModel().getColumn(modelColumn);
			column.setCellEditor(new SingleClickAutoSelectCellEditor(new PanelTextField()));
		}
	}
	
	protected int getColumnWidth(int column)
	{
		return getColumnHeaderWidth(column);
	}
	
	public int getColumnAlignment()
	{
		return JLabel.RIGHT;
	}
	
	public String getUniqueTableIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}
	
	public static final String UNIQUE_IDENTIFIER = "PlanningViewWorkPlanTable";

	private BasicTableCellRenderer renderer;
}
