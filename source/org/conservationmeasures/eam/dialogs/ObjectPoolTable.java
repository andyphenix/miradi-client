/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import javax.swing.ListSelectionModel;

import org.conservationmeasures.eam.objecthelpers.ORef;

public class ObjectPoolTable extends ObjectTable
{
	public ObjectPoolTable(ObjectPoolTableModel modelToUse)
	{
		super(modelToUse);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resizeTable(4);
	}
	
	public ObjectPoolTableModel getObjectPoolTableModel()
	{
		return (ObjectPoolTableModel)getModel();
	}
	
	public void updateTableAfterObjectCreated(ORef newObjectRef)
	{
		super.updateTableAfterObjectCreated(newObjectRef);
		getObjectPoolTableModel().rowsWereAddedOrRemoved();
	}
	
	public void updateTableAfterObjectDeleted(ORef deletedObjectRef)
	{
		super.updateTableAfterObjectDeleted(deletedObjectRef);
		getObjectPoolTableModel().rowsWereAddedOrRemoved();
	}
}
