/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogs;

import java.text.ParseException;

import javax.swing.ListSelectionModel;

import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAM;

public class ObjectListTable extends ObjectTable
{
	public ObjectListTable(ObjectListTableModel modelToUse)
	{
		super(modelToUse);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resizeTable(4);
	}
	
	public ObjectListTableModel getObjectListTableModel()
	{
		return (ObjectListTableModel)getModel();
	}
	
	void updateTableAfterCommand(CommandSetObjectData cmd)
	{
		super.updateTableAfterCommand(cmd);
		
		String oldData = cmd.getPreviousDataValue();
		String newData = cmd.getDataValue();
		updateTableIfRowWasAddedOrDeleted(cmd.getObjectType(), cmd.getObjectId(), cmd.getFieldTag(), oldData, newData);
	}
	
	void updateTableAfterUndo(CommandSetObjectData cmd)
	{
		super.updateTableAfterUndo(cmd);
		
		String oldData = cmd.getDataValue();
		String newData = cmd.getPreviousDataValue();
		updateTableIfRowWasAddedOrDeleted(cmd.getObjectType(), cmd.getObjectId(), cmd.getFieldTag(), oldData, newData);
	}
	
	void updateTableIfRowWasAddedOrDeleted(int type, BaseId id, String tag, String oldData, String newData)
	{
		if(type != getObjectListTableModel().getContainingObjectType())
			return;
		
		if(!id.equals(getObjectListTableModel().getContainingObjectId()))
			return;
		
		if(!tag.equals(getObjectListTableModel().getFieldTag()))
			return;
		
		int desiredSelectionRow = getSelectedRow();
		try
		{
			IdList oldList = new IdList(oldData);
			IdList newList = new IdList(newData);
			if(newList.size() > oldList.size())
				desiredSelectionRow = newList.size() - 1;
		}
		catch(ParseException nothingWeCanDoAboutIt)
		{
			EAM.logException(nothingWeCanDoAboutIt);
		}

		getObjectListTableModel().rowsWereAddedOrRemoved();
		desiredSelectionRow = Math.min(desiredSelectionRow, getRowCount() - 1);
		if(desiredSelectionRow >= 0)
			setRowSelectionInterval(desiredSelectionRow, desiredSelectionRow);
	}
	

}