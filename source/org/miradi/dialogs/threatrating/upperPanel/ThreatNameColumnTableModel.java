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
package org.miradi.dialogs.threatrating.upperPanel;

import org.miradi.icons.DirectThreatIcon;
import org.miradi.main.EAM;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.TaglessChoiceItem;

public class ThreatNameColumnTableModel extends AbstractThreatPerRowTableModel
{
	public ThreatNameColumnTableModel(Project projectToUse)
	{
		super(projectToUse);
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}

	public int getColumnCount()
	{
		return COLUMN_COUNT;
	}
	
	@Override
	public String getColumnGroupCode(int column)
	{
		return getColumnName(column);
	}
	
	@Override
	public String getColumnName(int column)
	{
		if (isThreatIconColumn(column))
			return "";
		
		final String DOWN_ARROW = "\u21D3";
		return DOWN_ARROW + EAM.text("Threats") + DOWN_ARROW + " / "+ RIGHT_ARROW + EAM.text("Targets") + RIGHT_ARROW;
	}
	
	public String getColumnTag(int column)
	{
		return "";
	}
	
	public Object getValueAt(int row, int column)
	{
		return getChoiceItemAt(row, column);
	}

	public ChoiceItem getChoiceItemAt(int row, int column)
	{
		if (isThreatIconColumn(column))
			return createTaglessChoiceItemWithThreatIcon();
		
		return new TaglessChoiceItem(getDirectThreat(row).toString());
	}

	public TaglessChoiceItem createTaglessChoiceItemWithThreatIcon()
	{
		return new TaglessChoiceItem(new DirectThreatIcon());
	}

	private boolean isThreatIconColumn(int column)
	{
		return column == THREAT_ICON_COLUMN_INDEX;
	}
	
	public BaseObject getBaseObjectForRowColumn(int row, int column)
	{
		return getDirectThreat(row);
	}

	private static final String UNIQUE_IDENTIFIER = "ThreatNameColumnTableModel";
	
	public static final int THREAT_ICON_COLUMN_INDEX = 0;
	public static final int THREAT_NAME_COLUMN_INDEX = 1;
	public static final int COLUMN_COUNT = 2;
	public static final String RIGHT_ARROW = "\u21D2";
}
