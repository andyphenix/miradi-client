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

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.miradi.questions.TaglessChoiceItem;
import org.miradi.utils.DateEditorComponent;

public class DateTableCellEditorAndRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer
{
	public DateTableCellEditorAndRenderer(DateEditorComponent dateEditorComponentToUse) 
	{
	    super();
	    
	    dateEditorComponent = dateEditorComponentToUse;
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c)
	{
		TaglessChoiceItem choiceItem = (TaglessChoiceItem) value;
		dateEditorComponent.setText(choiceItem.getLabel());
		
		return dateEditorComponent;
	}

	public Object getCellEditorValue()
	{
		return dateEditorComponent.getDateAsString();
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		TaglessChoiceItem choiceItem = (TaglessChoiceItem) value;
		dateEditorComponent.setText(choiceItem.getLabel());
		
		return dateEditorComponent;
	}
	
	private DateEditorComponent dateEditorComponent;
}