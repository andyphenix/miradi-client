/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.base;

import javax.swing.table.AbstractTableModel;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.ColumnTagProvider;

abstract public class ObjectTableModel extends AbstractTableModel implements ColumnTagProvider
{
	public ObjectTableModel(Project projectToUse, int listedItemType, String[] tableColumnTags)
	{
		columnTags = tableColumnTags;
		project = projectToUse;
		rowObjectType = listedItemType;
	}
	
	abstract public ORefList getLatestRefListFromProject();
	
	private IdList getLatestIdListFromProject()
	{
		return getLatestRefListFromProject().convertToIdList(getRowObjectType());
	}
	
	public int getRowCount()
	{
		return getRowObjectIds().size();
	}
	
	void setNewRowOrder(Integer[] existingRowIndexesInNewOrder)
	{
		IdList newList = new IdList();
		for(int i = 0; i < existingRowIndexesInNewOrder.length; ++i)
		{
			int nextExistingRowIndex = existingRowIndexesInNewOrder[i].intValue();
			newList.add(getRowObjectIds().get(nextExistingRowIndex));
		}
		setRowObjectIds(newList);
	}

	public void resetRows()
	{
		setRowObjectIds(getLatestIdListFromProject());
	}
	
	public BaseObject getObjectFromRow(int row) throws RuntimeException
	{
		try
		{
			BaseId rowObjectId = getRowObjectIds().get(row);
			BaseObject rowObject = project.findObject(rowObjectType, rowObjectId);
			if(rowObject == null)
				EAM.logDebug("ObjectTableModel.getObjectFromRow: Missing object: " + new ORef(rowObjectType, rowObjectId));
			return rowObject;
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new RuntimeException("TeamModel.getObjectFromRow error");
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
			ORef rowObjectRef = new ORef(rowObjectType, getRowObjectIds().get(row));
			return getValueToDisplay(rowObjectRef, getColumnTag(column));
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return "(Error)";
		}
	}

	public String getValueToDisplay(ORef rowObjectRef, String tag)
	{
		return project.getObjectData(rowObjectRef.getObjectType(), rowObjectRef.getObjectId(), tag);
	}

	public void rowsWereAddedOrRemoved()
	{
		//NOTE: Assumes one row at a time insert or delete
		IdList availableIds = getLatestIdListFromProject();
		IdList newList = new IdList();
		int deletedRowIndex = 0;
		for(int row = 0; row < getRowObjectIds().size(); ++row)
		{
			BaseId thisId = getRowObjectIds().get(row);
			if(availableIds.contains(thisId))
			{
				newList.add(thisId);
				availableIds.removeId(thisId);
			}
			else
			{
				deletedRowIndex = row;
			}
		}
		for(int i = 0; i < availableIds.size(); ++i)
		{
			newList.add(availableIds.get(i));
		}
		
		int priorCount = getRowObjectIds().size();
		setRowObjectIds(newList);
		
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

	public String getColumnName(int column)
	{
		return EAM.fieldLabel(rowObjectType, getColumnTag(column));
	}
	
	public Project getProject()
	{
		return project;
	}

	void setRowObjectIds(IdList rowObjectIdsToUse)
	{
		rowObjectIds = rowObjectIdsToUse;
	}

	private IdList getRowObjectIds()
	{
		if (rowObjectIds == null)
			resetRows();
			
		return rowObjectIds;
	}

	protected Project project;
	private int rowObjectType;
	private IdList rowObjectIds;
	private String[] columnTags;
}
