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

package org.miradi.diagram.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.miradi.dialogs.fieldComponents.PanelComboBox;
import org.miradi.dialogs.tablerenderers.TableCellPreferredHeightProvider;

public class ComboBoxRenderer extends PanelComboBox implements TableCellRenderer, TableCellPreferredHeightProvider
{
	public ComboBoxRenderer(Object[] items) 
	{
		super(items);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) 
	{
		if (!isSelected)  
			setColors(table.getBackground(), table.getForeground());

		setSelectedItem(value);
		return this;
	}

	private void setColors(Color background, Color foreground)
	{
		setBackground(background);
		setForeground(foreground);
	}

	public int getPreferredHeight(JTable table, int row, int column, Object value)
	{
		return getPreferredSize().height;
	}
}
