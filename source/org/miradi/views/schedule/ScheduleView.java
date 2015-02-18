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
package org.miradi.views.schedule;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import org.martus.swing.UiScrollPane;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.main.MiradiToolBar;
import org.miradi.project.Project;
import org.miradi.utils.MiradiResourceImageIcon;
import org.miradi.views.TabbedView;

public class ScheduleView extends TabbedView
{
	public ScheduleView(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		add(createScreenShotLabel(), BorderLayout.BEFORE_FIRST_LINE);
	}

	@Override
	public String cardName() 
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return Project.SCHEDULE_VIEW_NAME;
	}

	@Override
	public MiradiToolBar createToolBar()
	{
		return new ScheduleToolBar(getActions());
	}

	@Override
	public void createTabs() throws Exception
	{
		UiScrollPane calendarPanel = new UiScrollPane(new ScheduleComponent("images/Calendar.png"));
		UiScrollPane ganttPanel = new UiScrollPane(new ScheduleComponent("images/Tasks.png"));

		addTab(EAM.text("Calendar"),calendarPanel);
		addTab(EAM.text("Tasks"),ganttPanel);
	}


	@Override
	public void deleteTabs() throws Exception
	{
		// lightweight tabs...nothing to dispose yet
		
		super.deleteTabs();
	}

	
}

class ScheduleComponent extends JLabel
{
	public ScheduleComponent(String file)
	{
		super(new MiradiResourceImageIcon(file));
	}
}