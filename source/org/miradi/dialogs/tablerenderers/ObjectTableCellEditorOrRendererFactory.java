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
package org.miradi.dialogs.tablerenderers;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.miradi.main.EAM;
import org.miradi.objects.BaseObject;

abstract public class ObjectTableCellEditorOrRendererFactory extends BasicTableCellEditorOrRendererFactory  implements TableCellPreferredHeightProvider
{
	public ObjectTableCellEditorOrRendererFactory(RowColumnBaseObjectProvider providerToUse, FontForObjectProvider fontProviderToUse)
	{
		objectProvider = providerToUse;
		fontProvider = fontProviderToUse;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int tableColumn)
	{
		if(isEditor)
			EAM.logError("Factory used for both editor and renderer: " + getClass().getName());
		isRenderer = true;
		JComponent renderer = (JComponent)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, tableColumn);
		Font font = getCellFont(row, tableColumn);
		renderer.setFont(font);
		return renderer;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int tableColumn)
	{
		if(isRenderer)
			EAM.logError("Factory used for both editor and renderer: " + getClass().getName());
		isEditor = true;
		JComponent editor = (JComponent)super.getTableCellRendererComponent(table, value, isSelected, false, row, tableColumn);
		Font font = getCellFont(row, tableColumn);
		editor.setFont(font);
		final boolean EDITOR_CELL_IS_ALWAYS_SELECTED = true;
		updateBorderAndColors(editor, table, row, tableColumn, EDITOR_CELL_IS_ALWAYS_SELECTED);
		return editor;
	}
	
	protected Font getCellFont(int row, int column)
	{
		BaseObject object = getBaseObjectForRow(row, column);
		if(object == null)
			return getFontProvider().getPlainFont();
		
		Font font = getFontProvider().getFont(object);
		return getAdjustedFont(font, row, column);
	}

	protected Font getAdjustedFont(Font font, int row, int column)
	{
		return font;
	}		

	protected BaseObject getBaseObjectForRow(int row, int column)
	{
		return getObjectProvider().getBaseObjectForRowColumn(row, column);
	}
	
	protected RowColumnBaseObjectProvider getObjectProvider()
	{
		return objectProvider;
	}
	
	private FontForObjectProvider getFontProvider()
	{
		return fontProvider;
	}
	
	private RowColumnBaseObjectProvider objectProvider;
	private FontForObjectProvider fontProvider;

	private boolean isEditor;
	private boolean isRenderer;
}
