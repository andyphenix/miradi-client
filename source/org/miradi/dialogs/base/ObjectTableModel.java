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

import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.utils.CodeList;

abstract public class ObjectTableModel extends EditableObjectTableModel
{
	public ObjectTableModel(Project projectToUse, int listedItemType, String[] tableColumnTags)
	{
		super(projectToUse);
		
		columnTags = tableColumnTags;
		rowObjectType = listedItemType;
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		if (isCodeListColumn(column))
			return false;
		
		if (isPseudoFieldColumn(column))
			return false;
		
		return true;
	}
	
	public boolean isPseudoFieldColumn(int column)
	{
		return false;
	}
	
	public int getRowCount()
	{
		return getRowObjectRefs().size();
	}
	
	public void resetRows()
	{
		setRowObjectRefs(getLatestRefListFromProject());
	}
	
	public BaseObject getObjectFromRow(int row) throws RuntimeException
	{
		try
		{
			ORef rowObjectRef = getRowObjectRefs().get(row);
			BaseObject rowObject = getProject().findObject(rowObjectRef);
			if(rowObject == null)
			{
				EAM.logWarning("ObjectTableModel.getObjectFromRow: Missing object: " + rowObjectRef);
				EAM.logStackTrace();
			}
			return rowObject;
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new RuntimeException("TeamModel.getObjectFromRow error. row = " + row);
		}
	}
	
	public int findRowObject(BaseId id)
	{
		for(int row = 0; row < getRowCount(); ++row)
		{
			if(getObjectFromRow(row).getId().equals(id))
				return row;
		}
		
		return -1;
	}

	public int getRowObjectType()
	{
		return rowObjectType;
	}
	
	public Object getValueAt(int row, int column)
	{
		try
		{
			ORef rowObjectRef = getRowObjectRefs().get(row);
			String valueToDisplay = getValueToDisplay(rowObjectRef, getColumnTag(column));
			if (isCodeListColumn(column))
				return new CodeList(valueToDisplay);

			if (isChoiceItemColumn(column))
				return getChoiceItem(column, valueToDisplay);
			
			return valueToDisplay;
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return EAM.text("(Error)");
		}
	}
	
	@Override
	public void setValueAt(Object value, int row, int column)
	{
		super.setValueAt(value, row, column);
		
		final ORef rowObjectRef = getRowObjectRefs().get(row);
		final String columnTag = getColumnTag(column);
		String valueToSave = value.toString();
		if (isChoiceItemColumn(column))
		{
			final ChoiceItem choiceItem = (ChoiceItem) value;
			valueToSave = choiceItem.getCode().toString();
		}
		
		setValueUsingCommand(rowObjectRef, columnTag, valueToSave);
	}
	
	public String getValueToDisplay(ORef rowObjectRef, String tag)
	{
		return getProject().getObjectData(rowObjectRef, tag);
	}

	public void rowsWereAddedOrRemoved()
	{
		//NOTE: Assumes one row at a time insert or delete
		ORefList availableRefs = getLatestRefListFromProject();
		ORefList newList = new ORefList();
		int deletedRowIndex = 0;
		for(int row = 0; row < getRowObjectRefs().size(); ++row)
		{
			ORef thisRef = getRowObjectRefs().get(row);
			if(availableRefs.contains(thisRef))
			{
				newList.add(thisRef);
				availableRefs.remove(thisRef);
			}
			else
			{
				deletedRowIndex = row;
			}
		}
		for(int i = 0; i < availableRefs.size(); ++i)
		{
			newList.add(availableRefs.get(i));
		}
		
		int priorCount = getRowObjectRefs().size();
		setRowObjectRefs(newList);
		
		if (newList.size() > priorCount)
			fireTableRowsInserted(newList.size()-1, newList.size()-1);
		else if (newList.size() < priorCount)
			fireTableRowsDeleted(deletedRowIndex, deletedRowIndex);
	}

	public String getColumnTag(int column)
	{
		return columnTags[column];
	}
	
	public int getColumnCount()
	{
		return columnTags.length;
	}

	@Override
	public String getColumnName(int column)
	{
		return EAM.fieldLabel(rowObjectType, getColumnTag(column));
	}
	
	@Override
	public void setRowObjectRefs(ORefList rowObjectRefsToUse)
	{
		rowObjectRefs = rowObjectRefsToUse;
	}

	@Override
	protected ORefList getRowObjectRefs()
	{
		if (rowObjectRefs == null)
			resetRows();
			
		return rowObjectRefs;
	}

	public ChoiceItem getChoiceItem(int column, String dataToDisplay)
	{
		return getColumnQuestion(column).findChoiceByCode(dataToDisplay);
	}
		
	public BaseObject getBaseObjectForRowColumn(int row, int column)
	{
		return getObjectFromRow(row);
	}
	
	@Override
	public void setObjectRefs(ORefList hierarchyToSelectedRef)
	{
		throw new RuntimeException("Method should not be called!");
	}
	
	abstract public ORefList getLatestRefListFromProject();
	
	private int rowObjectType;
	private ORefList rowObjectRefs;
	private String[] columnTags;
}
