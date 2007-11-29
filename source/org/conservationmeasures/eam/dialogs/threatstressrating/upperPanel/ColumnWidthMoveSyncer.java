/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.threatstressrating.upperPanel;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class ColumnWidthMoveSyncer implements TableColumnModelListener
{

	public ColumnWidthMoveSyncer(JTable otherTableToUse)
	{
		otherTable = otherTableToUse; 
	}

	public void columnMarginChanged(ChangeEvent event)
	{
		TableColumnModel model = (TableColumnModel) event.getSource();
		syncPreferredColumnWidths(model);
	}

	public void syncPreferredColumnWidths(TableColumnModel model)
	{
		int columnCount = model.getColumnCount();
		for (int i = 0; i < columnCount; ++i)
		{
			TableColumn tableColumn = model.getColumn(i);
			TableColumn columnToAdjust = otherTable.getColumnModel().getColumn(i);
			columnToAdjust.setPreferredWidth(tableColumn.getPreferredWidth());
		}
	}

	public void columnAdded(TableColumnModelEvent event)
	{
	}
	
	public void columnMoved(TableColumnModelEvent event)
	{
		otherTable.getColumnModel().moveColumn(event.getFromIndex(), event.getToIndex());
	}

	public void columnRemoved(TableColumnModelEvent event)
	{
	}

	public void columnSelectionChanged(ListSelectionEvent events)
	{
	}
	
	private JTable otherTable;
}
