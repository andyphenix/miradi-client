/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

package org.miradi.dialogs.planning.upperPanel;

import java.awt.Color;

import org.miradi.dialogs.base.ChoiceItemTableModel;
import org.miradi.dialogs.planning.propertiesPanel.PlanningViewAbstractTreeTableSyncedTableModel;
import org.miradi.utils.ModelColumnTagProvider;

public class PlanningTreeMultiTableModel extends MultiTableModel implements ModelColumnTagProvider
{
	public PlanningTreeMultiTableModel(String subViewModelIdentifierToUse)
	{
		super(subViewModelIdentifierToUse);
	}

	@Override
	public void addModel(ChoiceItemTableModel modelToAdd)
	{
		throw new RuntimeException(getClass().getName() + ".addModel: Model must be a ColumnTagProvider");
	}
	
	public void addModel(PlanningViewAbstractTreeTableSyncedTableModel modelToAdd)
	{
		// NOTE: We are calling a DIFFERENT variant in super!
		// This is NOT a do-nothing method!!
		super.addModel(modelToAdd);
	}
	
	public String getColumnTag(int modelColumn)
	{
		return getCastedModel(modelColumn).getColumnTag(findColumnWithinSubTable(modelColumn));
	}
	
	public Color getCellBackgroundColor(int row, int column)
	{
		return getCastedModel(column).getCellBackgroundColor(findColumnWithinSubTable(column));
	}
	
	public boolean isCurrencyColumn(int modelColumn)
	{
		return getCastedModel(modelColumn).isCurrencyColumn(findColumnWithinSubTable(modelColumn));
	}

	public boolean isChoiceColumn(int modelColumn)
	{
		return getCastedModel(modelColumn).isChoiceItemColumn(findColumnWithinSubTable(modelColumn));
	}

	public boolean isProgressColumn(int modelColumn)
	{
		return getCastedModel(modelColumn).isProgressColumn(findColumnWithinSubTable(modelColumn));
	}
	
	public boolean isDateUnitColumn(int modelColumn)
	{
		return getCastedModel(modelColumn).isDateUnitColumn(findColumnWithinSubTable(modelColumn));
	}
	
	public boolean isWhenColumn(int modelColumn)
	{
		return getCastedModel(modelColumn).isWhenColumn(findColumnWithinSubTable(modelColumn));
	}
	
	public boolean isFormattedEditableColumn(int modelColumn)
	{
		return getCastedModel(modelColumn).isFormattedColumn(modelColumn);
	}
	
	public Class getCellQuestion(int row, int modelColumn)
	{
		return getCastedModel(modelColumn).getCellQuestion(row, findColumnWithinSubTable(modelColumn));
	}

	public PlanningViewAbstractTreeTableSyncedTableModel getCastedModel(int column)
	{
		return (PlanningViewAbstractTreeTableSyncedTableModel) findModel(column);
	}

	@Override
	public String getColumnGroupCode(int modelColumn)
	{
		return getCastedModel(modelColumn).getColumnGroupCode(findColumnWithinSubTable(modelColumn));
	}
}
