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
package org.miradi.dialogs.base;

import java.text.ParseException;

import javax.swing.ListSelectionModel;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;

public class ObjectListTable extends UpperPanelBaseObjectTable
{
	public ObjectListTable(MainWindow mainWindowToUse, UpperPanelBaseObjectTableModel modelToUse)
	{
		super(mainWindowToUse, modelToUse);
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resizeTable(4);
	}
	
	public ObjectListTable(MainWindow mainWindowToUse, UpperPanelBaseObjectTableModel modelToUse, int sortColumn)
	{
		this(mainWindowToUse, modelToUse);
	}
	
	public ObjectListTableModel getObjectListTableModel()
	{
		return (ObjectListTableModel)getModel();
	}
	
	@Override
	public ORefList[] getSelectedHierarchies()
	{
		ORefList[] superHierarchies = super.getSelectedHierarchies();
		if (superHierarchies.length == 0)
			return new ORefList[0];

		ORefList containingRefs = getObjectListTableModel().getSelectedHierarchy();
		for (int i = 0; i < superHierarchies.length; ++i)
		{
			superHierarchies[i].addAll(containingRefs);
		}
		
		return superHierarchies;
	}
	
	@Override
	public void updateTableAfterCommand(CommandSetObjectData cmd)
	{
		super.updateTableAfterCommand(cmd);
		
		String oldData = cmd.getPreviousDataValue();
		String newData = cmd.getDataValue();
		updateTableIfRowWasAddedOrDeleted(cmd.getObjectORef(), cmd.getFieldTag(), oldData, newData);
	}
	
	private void updateTableIfRowWasAddedOrDeleted(ORef ref, String tag, String oldData, String newData)
	{
		if (!getObjectListTableModel().getContainingRef().equals(ref))
			return;
		
		if(!tag.equals(getObjectListTableModel().getFieldTag()))
			return;
		
		int desiredSelectionRow = getSelectedRow();
		try
		{
			BaseObject baseObject = BaseObject.find(getProject(), ref);
			ORefList oldList = baseObject.createRefList(getObjectListTableModel().getFieldTag(), oldData);
			ORefList newList = baseObject.createRefList(getObjectListTableModel().getFieldTag(), newData);
			if(newList.size() > oldList.size())
				desiredSelectionRow = newList.size() - 1;
		}
		catch(ParseException nothingWeCanDoAboutIt)
		{
			EAM.alertUserOfNonFatalException(nothingWeCanDoAboutIt);
		}

		getObjectListTableModel().rowsWereAddedOrRemoved();
		desiredSelectionRow = Math.min(desiredSelectionRow, getRowCount() - 1);
		if(desiredSelectionRow >= 0)
			setRowSelectionInterval(desiredSelectionRow, desiredSelectionRow);
	}
}