/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogfields.editors;

import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.layout.OneRowPanel;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.project.ProjectCalendar;

public class YearPanel extends OneRowPanel
{
	public YearPanel(ProjectCalendar projectCalendar, DateUnit dateUnit)
	{
		this(projectCalendar, dateUnit, "");
	}
	
	public YearPanel(ProjectCalendar projectCalendar, DateUnit dateUnit, String panelTitle)
	{
		yearChooser = new YearComboBox(projectCalendar, dateUnit);
		
		add(new PanelTitleLabel(panelTitle));
		add(yearChooser);
	}

	public int getYear()
	{
		return getDate().getYearYear();
	}
	
	public DateUnit getDate()
	{
		return yearChooser.getYear();
	}
	
	private YearComboBox yearChooser;
}
