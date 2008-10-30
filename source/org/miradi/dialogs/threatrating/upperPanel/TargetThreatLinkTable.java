/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.miradi.main.AppPreferences;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.Factor;
import org.miradi.objects.Target;
import org.miradi.views.threatmatrix.CreateDeletePopupMouseAdapter;

public class TargetThreatLinkTable extends AbstractTableWithChoiceItemRenderer
{
	public TargetThreatLinkTable(MainWindow mainWindowToUse, TargetThreatLinkTableModel tableModel)
	{
		super(mainWindowToUse, tableModel);
		setBackground(AppPreferences.getDataPanelBackgroundColor());

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setColumnWidths();
		addMouseListener(new CreateDeletePopupMouseAdapter(tableModel.getProject(), tableModel));
	}
	
	private void setColumnWidths()
	{
		for (int i = 0; i < getColumnCount(); ++i)
		{
			setColumnWidth(i, 100);
		}
	}
	
	public boolean shouldSaveColumnSequence()
	{
		return false;
	}

	public TargetThreatLinkTableModel getTargetThreatLinkTableModel()
	{
		return (TargetThreatLinkTableModel) getModel();
	}
	
	public ORefList[] getSelectedHierarchies()
	{
		int threatIndex = getSelectedRow();
		if(threatIndex < 0)
			return new ORefList[0];
		
		Factor directThreat = getTargetThreatLinkTableModel().getDirectThreat(threatIndex);
		
		int tableColumn = getSelectedColumn();
		int modelColumn = convertColumnIndexToModel(tableColumn);
		ORefList hierarchyRefs = new ORefList();
		if (modelColumn < 0)
			return new ORefList[0];
		
		Target target = getTargetThreatLinkTableModel().getTarget(modelColumn);
		if (getTargetThreatLinkTableModel().areLinked(directThreat, target))
		{
			ORef linkRef = getTargetThreatLinkTableModel().getLinkRef(directThreat, target);
			hierarchyRefs.add(linkRef);
		}
		hierarchyRefs.add(target.getRef());
		hierarchyRefs.add(directThreat.getRef());
		
		return new ORefList[]{hierarchyRefs};
	}
	
	public String getUniqueTableIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}

	public static final String UNIQUE_IDENTIFIER = "TargetThreatLinkTable";
	public static final int PREFERRED_VIEWPORT_WIDTH = 500;
	public static final int PREFERRED_VIEWPORT_SUMMARY_COLUMN_WIDTH = 130;
	public static final int PREFERRED_VIEWPORT_HEIGHT = 100;
}
