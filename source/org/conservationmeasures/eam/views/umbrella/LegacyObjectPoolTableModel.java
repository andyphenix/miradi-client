/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.umbrella;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objectpools.EAMObjectPool;
import org.conservationmeasures.eam.objects.EAMObject;
import org.martus.swing.UiTableModel;
import org.martus.util.xml.XmlUtilities;

public class LegacyObjectPoolTableModel extends UiTableModel
{
	public LegacyObjectPoolTableModel(EAMObjectPool resourcePool, String[] columnTagsToUse)
	{
		pool = resourcePool;
		columnTags = columnTagsToUse;
		resetObjects();
	}
	
	public boolean isEnabled(int row)
	{
		return false;
	}

	public int getColumnCount()
	{
		return columnTags.length;
	}

	public int getRowCount()
	{
		return getEAMObjectRows().length;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		EAMObject object = getEAMObjectRows()[rowIndex];
		String data = object.getData(columnTags[columnIndex]);
		//FIXME, under windows look and feel, the html tag causes unwanted 
		//behavior.  white on white.  This happens when all the cells in a row contian
		// the same data.
		return "<html>" + XmlUtilities.getXmlEncoded(data) + "</html>";
	}

	public EAMObject getObjectFromRow(int rowIndex)
	{
		return getEAMObjectRows()[rowIndex];
	}

	public int getRowIndex(EAMObject object)
	{
		for (int i=0; i<getEAMObjectRows().length; ++i)
		{
			if (object.equals(getEAMObjectRows()[i]))
				return i;
		}
		return -1;
	}
	
	public String getColumnName(int column)
	{
		return EAM.fieldLabel(pool.getObjectType(), columnTags[column]);
	}
	
	public EAMObjectPool getPool()
	{
		return pool;
	}
	
	public String getColumnTag(int column) 
	{
		return columnTags[column];
	}
	
	public void resetObjects() 
	{
		eamObjectRows = new EAMObject[pool.size()];
		BaseId baseIds[] = pool.getIds();
		for (int i=0; i<baseIds.length; ++i) 
		{
			eamObjectRows[i] = (EAMObject)pool.getRawObject(baseIds[i]);
		}
	}
	
	public void setEAMObjectRows(EAMObject objectRowsToUse[]) 
	{
		eamObjectRows = objectRowsToUse;
	}
	
	
	public EAMObject[] getEAMObjectRows() 
	{
		return eamObjectRows;
	}
	
	private EAMObject eamObjectRows[];
	private EAMObjectPool pool;
	private String[] columnTags;
}