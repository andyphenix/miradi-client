/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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

package org.miradi.dialogs.progressPercent;

import org.miradi.dialogs.base.EditableObjectTableModel;
import org.miradi.dialogs.base.DynamicWidthEditableObjectTable;
import org.miradi.main.MainWindow;

public class ProgressPercentTable extends DynamicWidthEditableObjectTable
{
	public ProgressPercentTable(MainWindow mainWindowToUse, EditableObjectTableModel modelToUse) throws Exception
	{
		super(mainWindowToUse, modelToUse);		
	}
	
	@Override
	public void rebuildColumnEditorsAndRenderers()
	{
		ProgressPercentTableModel progressPercentTableModel = (ProgressPercentTableModel) getModel();
		for (int tableColumn = 0; tableColumn < progressPercentTableModel.getColumnCount(); ++tableColumn)
		{
			int modelColumn = convertColumnIndexToModel(tableColumn);
			if (progressPercentTableModel.isDateColumn(modelColumn))
				createDateColumn(tableColumn);
				
			if (progressPercentTableModel.isPercentCompleteColumn(modelColumn))
				createFloatingPointRestrictedColumn(tableColumn);
			
			if (progressPercentTableModel.isPercentCompleteNotesColumn(modelColumn))
				createWrappableTextFieldColumn(tableColumn);
		}
	}
}
