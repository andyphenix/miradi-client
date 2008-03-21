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
package org.miradi.dialogs.planning.upperPanel;

import java.awt.Color;

import javax.swing.table.TableCellRenderer;

import org.miradi.dialogs.tablerenderers.BasicTableCellRenderer;
import org.miradi.dialogs.tablerenderers.ChoiceItemTableCellRenderer;
import org.miradi.dialogs.tablerenderers.FontForObjectTypeProvider;
import org.miradi.main.AppPreferences;
import org.miradi.utils.TableWithTreeTableNodes;

public class PlanningViewMeasurementTable extends TableWithTreeTableNodes
{
	public PlanningViewMeasurementTable(PlanningViewMeasurementTableModel model, FontForObjectTypeProvider fontProvider)
	{
		super(model);	
		otherRenderer = new BasicTableCellRenderer();
		otherRenderer.setCellBackgroundColor(getBackgroundColor());
		statusQuestionRenderer = new ChoiceItemTableCellRenderer(model, fontProvider, getBackgroundColor());
	}

	// TODO: This code is copied from ObjectTable--should be combined somehow
	public TableCellRenderer getCellRenderer(int row, int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		if (getMeasurementModel().isChoiceItemColumn(modelColumn))
		{
			return statusQuestionRenderer;
		}
	
		return otherRenderer;
	}
	
	public Color getColumnBackGroundColor(int column)
	{
		return getBackgroundColor();
	}
	
	public String getUniqueTableIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}
	
	public PlanningViewMeasurementTableModel getMeasurementModel()
	{
		return (PlanningViewMeasurementTableModel)getModel();
	}
	
	private static Color getBackgroundColor()
	{
		return AppPreferences.MEASUREMENT_COLOR_BACKGROUND;
	}
	
	public static final String UNIQUE_IDENTIFIER = "PlanningViewMeasurementTable";

	private BasicTableCellRenderer otherRenderer;
	private ChoiceItemTableCellRenderer statusQuestionRenderer;
}
