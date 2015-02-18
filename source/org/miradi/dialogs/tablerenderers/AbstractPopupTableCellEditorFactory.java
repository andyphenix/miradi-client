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

package org.miradi.dialogs.tablerenderers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.miradi.dialogs.base.DisposablePanel;
import org.miradi.dialogs.base.ModelessDialogWithClose;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.dialogs.treetables.ObjectTreeTable;
import org.miradi.main.AppPreferences;
import org.miradi.main.MainWindow;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;

abstract public class AbstractPopupTableCellEditorFactory extends AbstractCellEditor implements TableCellEditor
{
	public AbstractPopupTableCellEditorFactory(MainWindow mainWindowToUse, RowColumnSelectionProvider tableToUse)
	{
		this(mainWindowToUse, tableToUse, new MultiLineObjectTableCellRendererOnlyFactory(mainWindowToUse, tableToUse, new DefaultFontProvider(mainWindowToUse)));
	}
	
	public AbstractPopupTableCellEditorFactory(MainWindow mainWindowToUse, RowColumnSelectionProvider tableToUse, MultiLineObjectTableCellRendererOnlyFactory rendererFactoryToUse)
	{
		mainWindow = mainWindowToUse;
		table = tableToUse;
		rendererFactory = rendererFactoryToUse;
		rendererFactory.setCellBackgroundColor(AppPreferences.RESOURCE_TABLE_BACKGROUND);
		
		JComponent rendererComponent = rendererFactory.getRendererComponent();
		rendererComponent.addMouseListener(new LeftClickHandler());
	}
	
	public Component getTableCellEditorComponent(JTable tableToUse, Object value, boolean isSelected, int row, int column) 
	{
		return rendererFactory.getTableCellEditorComponent(tableToUse, value, true, row, column);
	}

	public Object getCellEditorValue()
	{
		return null;
	}

	public Project getProject()
	{
		return getMainWindow().getProject();
	}
	
	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	abstract protected DisposablePanel createEditorComponenet(BaseObject baseObjectForRow);
	
	abstract protected String getDialogTitle();
	
	private class LeftClickHandler extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			table.stopCellEditing();

			final BaseObject baseObjectForRow = table.getBaseObjectForRowColumn(table.getSelectedRow(), table.getSelectedColumn());
			DisposablePanel codeListEditor = createEditorComponenet(baseObjectForRow);
			ModelessDialogWithClose dialog = new ModelessDialogWithClose(getMainWindow(), getDialogTitle());
			String dialogObjectDescription = ObjectTreeTable.getToolTipString(baseObjectForRow);
			dialog.add(new PanelTitleLabel(dialogObjectDescription), BorderLayout.BEFORE_FIRST_LINE);
			dialog.setScrollableMainPanel(codeListEditor);
			dialog.pack();
			getMainWindow().getCurrentView().showFloatingPropertiesDialog(dialog);
		}
	}
	
	private RowColumnSelectionProvider table;
	private MainWindow mainWindow;
	private MultiLineObjectTableCellRendererOnlyFactory rendererFactory;
}
