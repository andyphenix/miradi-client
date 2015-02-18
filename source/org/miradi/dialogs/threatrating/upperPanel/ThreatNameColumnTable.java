/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
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
package org.miradi.dialogs.threatrating.upperPanel;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;

import org.miradi.dialogs.tablerenderers.DefaultFontProvider;
import org.miradi.dialogs.tablerenderers.FontForObjectProvider;
import org.miradi.dialogs.tablerenderers.MultiLineObjectTableCellRendererOnlyFactory;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.main.MainWindow;


public class ThreatNameColumnTable extends TableWhoseScrollPaneAlwaysExactlyFits
{
	public ThreatNameColumnTable(MainWindow mainWindowToUse, AbstractThreatPerRowTableModel tableModel)
	{
		super(mainWindowToUse, tableModel, tableModel.getUniqueTableModelIdentifier());

		textCellRendererFactory = new BorderlessMultilineCellRendererFactory(mainWindowToUse, tableModel, new DefaultFontProvider(getMainWindow()));
		getColumnModel().getColumn(ThreatNameColumnTableModel.THREAT_NAME_COLUMN_INDEX).setCellRenderer(textCellRendererFactory);
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		getTableHeader().setReorderingAllowed(false);
		setShowVerticalLines(false);
	}

	@Override
	public boolean shouldSaveColumnSequence()
	{
		return false;
	}

	private BorderlessMultilineCellRendererFactory textCellRendererFactory;
}

class BorderlessMultilineCellRendererFactory extends MultiLineObjectTableCellRendererOnlyFactory
{
	public BorderlessMultilineCellRendererFactory(
			MainWindow mainWindowToUse, 
			RowColumnBaseObjectProvider providerToUse,
			FontForObjectProvider fontProviderToUse)
	{
		super(mainWindowToUse, providerToUse, fontProviderToUse);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int tableColumn)
	{
		final Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, tableColumn);
		((JComponent)component).setBorder(BorderFactory.createEmptyBorder());
		return component;
	}
	
}

