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

package org.miradi.dialogs.dashboard;

import org.miradi.dialogs.base.OpenStandardsPlanActionsAndMonitoringQuestionPanel;
import org.miradi.main.MainWindow;
import org.miradi.objects.Dashboard;
import org.miradi.questions.OpenStandardsPlanActionsAndMonitoringQuestion;

public class OpenStandarsPlanActionsAndMonitoringTab extends OpenStandardsDashboardTab
{
	private OpenStandarsPlanActionsAndMonitoringTab(MainWindow mainWindowToUse, OpenStandardsPlanActionsAndMonitoringQuestionPanel leftPanelToUse) throws Exception
	{
		super(mainWindowToUse, leftPanelToUse);
	}
	
	public static OpenStandarsPlanActionsAndMonitoringTab createPanel(MainWindow mainWindowToUse) throws Exception
	{
		OpenStandardsPlanActionsAndMonitoringQuestionPanel leftPanelToUse = new OpenStandardsPlanActionsAndMonitoringQuestionPanel(mainWindowToUse.getProject());
		return new OpenStandarsPlanActionsAndMonitoringTab(mainWindowToUse, leftPanelToUse);
	}
	
	@Override
	protected AbstractLongDescriptionProvider getDefaultDescriptionProvider() throws Exception
	{
		return new HtmlResourceLongDescriptionProvider(OpenStandardsPlanActionsAndMonitoringQuestion.MAIN_DESCRIPTION_FILENAME);
	}

	@Override
	public String getPanelDescription()
	{
		return new OpenStandardsPlanActionsAndMonitoringQuestion().getPlanActionsAndMonitoringHeaderLabel();
	}
	
	@Override
	public String getTabCode()
	{
		return Dashboard.DASHBOARD_PLAN_TAB_CODE;
	}
}
