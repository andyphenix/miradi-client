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
package org.miradi.dialogs.threatrating.upperPanel;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import org.miradi.dialogs.tablerenderers.DefaultFontProvider;
import org.miradi.dialogs.tablerenderers.PlainTextTableHeaderRenderer;
import org.miradi.dialogs.tablerenderers.ThreatTargetTableCellRendererFactory;
import org.miradi.main.AppPreferences;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.Factor;
import org.miradi.objects.Target;

public class TargetThreatLinkTable extends AbstractTableWithChoiceItemRenderer
{
	public TargetThreatLinkTable(MainWindow mainWindowToUse, TargetThreatLinkTableModel tableModel)
	{
		super(mainWindowToUse, tableModel, tableModel.getUniqueTableModelIdentifier());

		setBackground(AppPreferences.getDataPanelBackgroundColor());
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setColumnWidths();
	}
	
	@Override
	protected void setColumnRenderers()
	{
		AbstractThreatPerRowTableModel model = (AbstractThreatPerRowTableModel) getModel();
		ThreatTargetTableCellRendererFactory renderer = new ThreatTargetTableCellRendererFactory(getMainWindow().getAppPreferences(), model, new DefaultFontProvider(getMainWindow()));
		PlainTextTableHeaderRenderer plainTextTableHeaderRenderer = new PlainTextTableHeaderRenderer();
		for (int columnIndex = 0; columnIndex < getColumnCount(); ++columnIndex)
		{
			final TableColumn tableColumn = getColumnModel().getColumn(columnIndex);
			tableColumn.setCellRenderer(renderer);
			tableColumn.setHeaderRenderer(plainTextTableHeaderRenderer);
		}
	}

	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}
	
	@Override
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}
	
	@Override
	public String getColumnGroupCode(int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		String tableColumnSequenceKey = getTargetThreatLinkTableModel().getColumnGroupCode(modelColumn);
		
		return tableColumnSequenceKey;
	}

	private void setColumnWidths()
	{
		for (int i = 0; i < getColumnCount(); ++i)
		{
			setColumnWidth(i, 100);
		}
	}
	
	public TargetThreatLinkTableModel getTargetThreatLinkTableModel()
	{
		return (TargetThreatLinkTableModel) getModel();
	}
	
	public ORefList[] getSelectedHierarchies()
	{
		ORefList[] nothingSelected = new ORefList[] {new ORefList()};
		int threatIndex = getSelectedRow();
		if(threatIndex < 0)
			return nothingSelected;
		
		Factor directThreat = getTargetThreatLinkTableModel().getDirectThreat(threatIndex);
		
		int tableColumn = getSelectedColumn();
		int modelColumn = convertColumnIndexToModel(tableColumn);
		ORefList hierarchyRefs = new ORefList();
		if (modelColumn < 0)
			return nothingSelected;
		
		Target target = getTargetThreatLinkTableModel().getTarget(modelColumn);
		hierarchyRefs.add(target.getRef());
		hierarchyRefs.add(directThreat.getRef());
		
		return new ORefList[]{hierarchyRefs};
	}

	@Override
	public String getColumnIdentifier(int tableColumn)
	{
		final int modelColumn = convertColumnIndexToModel(tableColumn);
		return getTargetThreatLinkTableModel().getColumnIdentifier(modelColumn);
	}
	
	public static final int PREFERRED_VIEWPORT_WIDTH = 500;
	public static final int PREFERRED_VIEWPORT_SUMMARY_COLUMN_WIDTH = 130;
	public static final int PREFERRED_VIEWPORT_HEIGHT = 100;
}
