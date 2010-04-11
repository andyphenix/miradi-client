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
import org.miradi.main.EAM;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.questions.DateUnitTypeQuestion;
import org.miradi.utils.FillerLabel;

public class DayDateUnitStartAndEndCard extends DateUnitStartAndEndCard
{
	public DayDateUnitStartAndEndCard(StartEndDateUnitProvider dateUnitRange)
	{
		startDayPanel = new DayPanel(dateUnitRange.getStartDateUnit(), getStartText());
		endDayPanel = new DayPanel(dateUnitRange.getEndDateUnit(), getEndText());

		add(new PanelTitleLabel(EAM.text("Day Selection: ")));
		add(new FillerLabel());
		add(startDayPanel);
		add(endDayPanel);
	}
	
	@Override
	public void dispose()
	{
		disposePanel(startDayPanel);
		disposePanel(endDayPanel);
		
		super.dispose();
	}
	
	@Override
	protected DateUnit getEndDate()
	{
		return endDayPanel.getDateUnit();
	}

	@Override
	protected DateUnit getStartDate()
	{
		return startDayPanel.getDateUnit();
	}
	
	@Override
	public String getPanelDescription()
	{
		return DateUnitTypeQuestion.DAY_CODE;
	}
	
	private DayPanel startDayPanel;
	private DayPanel endDayPanel;
}
