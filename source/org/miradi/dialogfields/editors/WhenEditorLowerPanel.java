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

package org.miradi.dialogfields.editors;

import java.awt.CardLayout;

import org.miradi.dialogfields.FieldSaver;
import org.miradi.dialogs.base.DisposablePanel;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.project.ProjectCalendar;

public class WhenEditorLowerPanel extends DisposablePanel
{
	public WhenEditorLowerPanel(ProjectCalendar projectCalendar, StartEndDateUnitProvider dateUnitRange)
	{
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		
		noneCard = new NoneCard();
		projectTotalCard = new ProjectTotalDateUnitStartAndEndCard();
		yearCard = new CalendarYearDateUnitStartAndEndCard(projectCalendar, dateUnitRange);
		quarterCard = new QuarterDateUnitStartAndEndCard(projectCalendar, dateUnitRange);
		monthCard = new MonthDateUnitStartAndEndCard(projectCalendar, dateUnitRange);
		dayCard = new DayDateUnitStartAndEndCard(dateUnitRange);
		
		add(noneCard, noneCard.getPanelDescription());
		add(projectTotalCard, projectTotalCard.getPanelDescription());
		add(yearCard, yearCard.getPanelDescription());
		add(quarterCard, quarterCard.getPanelDescription());
		add(monthCard, monthCard.getPanelDescription());
		add(dayCard, dayCard.getPanelDescription());
		
		currentCard = noneCard;
	}

	@Override
	public void dispose()
	{
		FieldSaver.savePendingEdits();
		disposePanel(dayCard);
		dayCard = null;
		
		super.dispose();
	}
	
	public DateUnit getStartDateUnit()
	{
		return currentCard.getStartDate();
	}
	
	public DateUnit getEndDateUnit()
	{
		return currentCard.getEndDate();
	}
	
	public void showCard(String cardName)
	{
		currentCard = findPanel(cardName);
		cardLayout.show(this, currentCard.getPanelDescription());
	}
	
	private DateUnitStartAndEndCard findPanel(String cardName)
	{
		if (cardName.equals(noneCard.getPanelDescription()))
			return noneCard;
		
		if (cardName.equals(projectTotalCard.getPanelDescription()))
			return projectTotalCard;
		
		if (cardName.equals(yearCard.getPanelDescription()))
			return yearCard;
		
		if (cardName.equals(quarterCard.getPanelDescription()))
			return quarterCard;
		
		if (cardName.equals(monthCard.getPanelDescription()))
			return monthCard;
		
		if (cardName.equals(dayCard.getPanelDescription()))
			return dayCard;
		
		throw new RuntimeException(cardName + " card could not be found");
	}
	
	private CardLayout cardLayout;
	private DateUnitStartAndEndCard currentCard;
	
	private NoneCard noneCard;
	private ProjectTotalDateUnitStartAndEndCard projectTotalCard;
	private CalendarYearDateUnitStartAndEndCard yearCard;
	private QuarterDateUnitStartAndEndCard quarterCard;
	private MonthDateUnitStartAndEndCard monthCard;
	private DayDateUnitStartAndEndCard dayCard;
}
