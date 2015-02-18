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

package org.miradi.dialogfields.editors;

import javax.swing.DefaultComboBoxModel;

import org.miradi.dialogs.fieldComponents.PanelComboBox;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.project.ProjectCalendar;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;

abstract public class DateUnitComboBox extends PanelComboBox
{
	public DateUnitComboBox(ProjectCalendar projectCalendarToUse, DateUnit dateUnit)
	{
		projectCalendar = projectCalendarToUse;
		
		ChoiceItem[] choices = createChoices();
		setModel(new DefaultComboBoxModel(choices));
		
		setSelectedDateUnit(dateUnit);
	}
	
	public DateUnit getDateUnit()
	{
		ChoiceItem selectedItem = (ChoiceItem) getSelectedItem();
		if (selectedItem == null)
			return null;
		
		return new DateUnit(selectedItem.getCode());
	}

	protected ProjectCalendar getProjectCalendar()
	{
		return projectCalendar;
	}

	public void setSelectedDateUnit(DateUnit dateUnit)
	{
		if (dateUnit != null && isType(dateUnit))
		{
			ChoiceItem choiceItem = createQuestion().findChoiceByCode(dateUnit.getDateUnitCode());
			setSelectedItem(choiceItem);
		}
	}
	
	abstract protected ChoiceItem[] createChoices();
	
	abstract protected ChoiceQuestion createQuestion();
	
	abstract protected boolean isType(DateUnit dateUnit);
	
	private ProjectCalendar projectCalendar;
}
