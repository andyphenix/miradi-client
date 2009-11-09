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
package org.miradi.dialogs.planning.propertiesPanel;

import org.miradi.main.MainWindow;
import org.miradi.objectpools.ResourcePool;
import org.miradi.objects.ProjectResource;

public class ResourceAssignmentMainTable extends AbstractAssignmentDetailsMainTable
{
	public ResourceAssignmentMainTable(MainWindow mainWindowToUse, AbstractSummaryTableModel modelToUse) throws Exception
	{
		super(mainWindowToUse, modelToUse, getUniqueIdentifier());
	}
	
	public void rebuildColumnEditorsAndRenderers() throws Exception
	{
		for (int tableColumn = 0; tableColumn < getColumnCount(); ++tableColumn)
		{
			createResourceCombo(tableColumn);
		}
		
		super.rebuildColumnEditorsAndRenderers();
	}
	
	private void createResourceCombo(int tableColumn) throws Exception
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		if (! getAbstractSummaryTableModel().isResourceColumn(modelColumn))
			return;
		
		ProjectResource[] resources = getAllProjectResources();
		ProjectResource invalidResource = ResourceAssignmentMainTableModel.createInvalidResource(getObjectManager());
		createComboColumn(resources, tableColumn, invalidResource);
	}
	
	private ProjectResource[] getAllProjectResources()
	{
		return  getResourcePool().getAllProjectResources();
	}
	
	private ResourcePool getResourcePool()
	{
		return getObjectManager().getResourcePool();
	}
	
	static private String getUniqueIdentifier()
	{
		return "ResourceAssignmentMainTable"; 
	} 
}
