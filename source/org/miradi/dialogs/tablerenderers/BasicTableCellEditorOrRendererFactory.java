/* 
Copyright 2005-2022, Foundations of Success, Bethesda, Maryland
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
package org.miradi.dialogs.tablerenderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import static org.miradi.main.Miradi.isWindows;

abstract public class BasicTableCellEditorOrRendererFactory extends AbstractCellEditor implements TableCellRenderer, TableCellPreferredHeightProvider, TableCellEditor 
{
	public BasicTableCellEditorOrRendererFactory()
	{
		backgroundColor = Color.WHITE;
	}
	
	public void dispose()
	{
	}
	
	/**
	 * TODO: Currently, about half of the subclasses override this method, 
	 * and the other half override getTableCellRendererComponent directly.
	 * At some point we should unify them, although that will require 
	 * resolving issues about the sequence of when different font changes 
	 * are applied.
	 */
	public abstract JComponent getRendererComponent(JTable table, boolean isSelected, boolean hasFocus, int row, int tableColumn, Object value);
	
	public Object getCellEditorValue()
	{
		throw new RuntimeException("Editable cell must override getCellEditorValue");
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		throw new RuntimeException("Editable cell must override getTableCEllEditorComponent");
	}

	public void setCellBackgroundColor(Color backgroundColorToUse)
	{
		backgroundColor = backgroundColorToUse;
	}

	/**
	 * TODO: Currently, about half of the subclasses override this method, 
	 * and the other half override getRendererComponent.
	 * At some point we should unify them, although that will require 
	 * resolving issues about the sequence of when different font changes 
	 * are applied.
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int tableColumn)
	{
		JComponent renderer = getRendererComponent(table, isSelected, hasFocus, row, tableColumn, value);

		updateBorderAndColors(renderer, table, row, tableColumn, isSelected);
			
		return renderer;
	}

	protected void updateBorderAndColors(JComponent renderer, JTable table, int row, int tableColumn, boolean isSelected)
	{
		renderer.setBorder(getCellBorder(row));
		if(isSelected)
		{
			Color fg = table.getSelectionForeground();
			Color bg = table.getSelectionBackground();
			setColors(renderer, fg, bg);
		}
		else
		{
			Color fg = getCellForegroundColor(table, row, tableColumn);
			Color bg = getCellBackgroundColor();
			setColors(renderer, fg, bg);
		}
	}

	protected void setColors(JComponent renderer, Color fg, Color bg)
	{
		renderer.setForeground(fg);
		renderer.setBackground(bg);
	}

	public Border getCellBorder(int row)
	{
		int cellTopBorderWidth = getCellBorderWidth(row);
		int cellLeftBorderWidth = getCellBorderWidth();
		Color cellBorderColor = getCellBorderColor();
		Border line = BorderFactory.createMatteBorder(cellTopBorderWidth, cellLeftBorderWidth, 0, 0, cellBorderColor);
		Border margin = BorderFactory.createEmptyBorder(CELL_MARGIN, CELL_MARGIN, CELL_MARGIN, CELL_MARGIN);
		return BorderFactory.createCompoundBorder(line, margin);
	}

	private int getCellBorderWidth()
	{
		if (isWindows())
			return 0;

		return DEFAULT_CELL_WIDTH;
	}

	private int getCellBorderWidth(int row)
	{
		if (isWindows())
			return row == 0 ? DEFAULT_CELL_WIDTH : 0;

		return DEFAULT_CELL_WIDTH;
	}

	public static Color getCellBorderColor()
	{
		if (isWindows())
			return Color.lightGray;

		return Color.black;
	}

	public Color getCellForegroundColor(JTable table, int row, int tableColumn)
	{
		if(table.isCellEditable(row, tableColumn))
			return Color.BLUE.darker();
		return Color.BLACK;
	}
	
	public Color getCellBackgroundColor()
	{
		return backgroundColor;
	}
	
	public static final int CELL_MARGIN = 2;
	private static final int DEFAULT_CELL_WIDTH = 1;

	private Color backgroundColor;
}
