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
package org.miradi.dialogs.treeRelevancy;

import org.miradi.dialogs.base.EditableObjectTableModel;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.RelevancyOverride;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Objective;
import org.miradi.project.Project;

public class StrategyActivityRelevancyTableModel extends EditableObjectTableModel
{
	public StrategyActivityRelevancyTableModel(Project projectToUse, RowColumnBaseObjectProvider providerToUse, Objective objectiveAsParentToUse)
	{
		super(projectToUse);
		
		objectiveAsParent = objectiveAsParentToUse;
		rowColumnBaseObjectProvider = providerToUse;
	}

    public Class getColumnClass(int columnIndex) 
    {
    	return Boolean.class;
    }
	
	public boolean isCellEditable(int row, int column)
	{
		return true;
	}
    
	public String getColumnName(int column)
	{
		return EAM.text("Is Relevant");
	}	
	
	@Override
	public void setObjectRefs(ORef[] hierarchyToSelectedRef)
	{
		//FIXME should this do something
	}

	public String getColumnTag(int column)
	{
		return "";
	}

	public BaseObject getBaseObjectForRowColumn(int row, int column)
	{
		return rowColumnBaseObjectProvider.getBaseObjectForRowColumn(row, column);
	}

	public int getRowCount()
	{
		return rowColumnBaseObjectProvider.getRowCount();
	}

	public int getColumnCount()
	{
		return 1;
	}

	public Object getValueAt(int row, int column)
	{
		RelevancyOverrideSet strategyRefSet = objectiveAsParent.getStrategyRelevancyOverrideSet();
		ORef ref = getBaseObjectForRowColumn(row, column).getRef();
		RelevancyOverride override = strategyRefSet.find(ref);
		if (override == null)
			return new Boolean(false);
		
		return new Boolean(override.isOverride());
	}
	
	public void setValueAt(Object value, int row, int column)
	{
		if (value == null)
			return;
		
		ORef ref = getBaseObjectForRowColumn(row, column).getRef();
		Boolean valueAsBoolean = (Boolean)value;
		RelevancyOverrideSet strategyRefSet = objectiveAsParent.getStrategyRelevancyOverrideSet();
		RelevancyOverride override = new RelevancyOverride(ref, valueAsBoolean.booleanValue());
		strategyRefSet.add(override);
		setValueUsingCommand(objectiveAsParent.getRef(), Objective.TAG_RELEVANT_STRATEGY_SET, strategyRefSet.toString());
	}
	
	private RowColumnBaseObjectProvider rowColumnBaseObjectProvider;
	private Objective objectiveAsParent;
}
