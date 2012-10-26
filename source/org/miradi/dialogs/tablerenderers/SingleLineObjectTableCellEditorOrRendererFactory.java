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

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.miradi.utils.HtmlUtilities;

public class SingleLineObjectTableCellEditorOrRendererFactory extends ObjectTableCellEditorOrRendererFactory
{
	public SingleLineObjectTableCellEditorOrRendererFactory(RowColumnBaseObjectProvider providerToUse, FontForObjectProvider fontProviderToUse)
	{
		super(providerToUse, fontProviderToUse);
		
		rendererComponent = new HtmlEncodedCellRenderer();
		editorComponent = new HtmlEncodedCellEditor();
	}
	
	@Override
	public JComponent getRendererComponent(JTable table, boolean isSelected, boolean hasFocus, int row, int tableColumn, Object value)
	{
		JLabel renderer = (JLabel)rendererComponent.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, tableColumn);
		renderer.setVerticalAlignment(SwingConstants.TOP);
		return renderer;
	}

	public int getPreferredHeight(JTable table, int row, int column, Object value)
	{
		Component component = rendererComponent.getTableCellRendererComponent(table, value, false, false, row, column);
		return component.getPreferredSize().height;
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int tableColumn)
	{
		updateBorderAndColors(editorComponent.getJComponent(), table, row, tableColumn, isSelected);
		editorComponent.getComponent().setFont(getCellFont(row, tableColumn));
		editorComponent.setText(value.toString());
		
		return editorComponent.getComponent();
	}
	
	@Override
	public Object getCellEditorValue()
	{
		return editorComponent.getText();
	}
	
	private class HtmlEncodedCellEditor extends DefaultCellEditor
	{
		public HtmlEncodedCellEditor()
		{
			super(new JTextField());
			
			setClickCountToStart(1);
		}

		public JComponent getJComponent()
		{
			return getTextEditorComponent();
		}

		private JTextField getTextEditorComponent()
		{
			return (JTextField)getComponent();
		}

		public void setText(String text)
		{
			text = HtmlUtilities.convertHtmlToPlainText(text);
			
			getTextEditorComponent().setText(text);
		}
		
		public String getText()
		{
			String text = getTextEditorComponent().getText();
			text = HtmlUtilities.convertPlainTextToHtmlText(text);
			
			return text;
		}
	}
	
	private class HtmlEncodedCellRenderer extends DefaultTableCellRenderer
	{
		@Override
		public void setText(String text)
		{
			text = HtmlUtilities.replaceHtmlBullets(text);
			text = HtmlUtilities.convertHtmlToPlainText(text);

			super.setText(text);
		}
	}

	private DefaultTableCellRenderer rendererComponent;
	private HtmlEncodedCellEditor editorComponent;
}
