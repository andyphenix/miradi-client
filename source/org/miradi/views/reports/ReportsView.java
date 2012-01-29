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
package org.miradi.views.reports;

import java.awt.BorderLayout;

import org.miradi.actions.ActionCreateReportTemplate;
import org.miradi.actions.ActionDeleteReportTemplate;
import org.miradi.actions.ActionRunReportTemplate;
import org.miradi.dialogs.reportTemplate.ReportTemplateManagementPanel;
import org.miradi.dialogs.reportTemplate.StandardReportPanel;
import org.miradi.dialogs.reportTemplate.XsltReportPanel;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.main.MiradiToolBar;
import org.miradi.project.Project;
import org.miradi.utils.MiradiScrollPane;
import org.miradi.views.TabbedView;
import org.miradi.views.reports.doers.CreateReportTemplateDoer;
import org.miradi.views.reports.doers.DeleteReportTemplateDoer;
import org.miradi.views.reports.doers.RunReportTemplateDoer;

public class ReportsView extends TabbedView
{
	public ReportsView(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		addDoersToMap();
		add(createScreenShotLabel(), BorderLayout.BEFORE_FIRST_LINE);
	}
	
	@Override
	public void becomeActive() throws Exception
	{
		super.becomeActive();
		reportTemplateManagementPanel.becomeActive();
	}
	
	@Override
	public void becomeInactive() throws Exception
	{
		reportTemplateManagementPanel.becomeInactive();
		super.becomeInactive();
	}
	
	private void addDoersToMap()
	{
		addDoerToMap(ActionCreateReportTemplate.class, new CreateReportTemplateDoer());
		addDoerToMap(ActionDeleteReportTemplate.class, new DeleteReportTemplateDoer());
		addDoerToMap(ActionRunReportTemplate.class, new RunReportTemplateDoer());
	}

	@Override
	public String cardName() 
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return Project.REPORT_VIEW_NAME;
	}

	@Override
	public MiradiToolBar createToolBar()
	{
		return new ReportToolBar(getActions());
	}

	@Override
	public void createTabs() throws Exception
	{
		reportTemplateManagementPanel = new ReportTemplateManagementPanel(getMainWindow());
		standardReportPanel = new StandardReportPanel(getMainWindow());
		xsltReportPanel = new XsltReportPanel(getMainWindow());

		addTab(EAM.text("Standard Reports"), new MiradiScrollPane(standardReportPanel));
		addTab(EAM.text("Custom Reports"), reportTemplateManagementPanel);
		addTab(EAM.text("XSLT"), xsltReportPanel);

	}
	
	@Override
	public void deleteTabs() throws Exception
	{
		reportTemplateManagementPanel.dispose();
		reportTemplateManagementPanel = null;
		
		standardReportPanel = null;
		
		xsltReportPanel = null;
		
		super.deleteTabs();
	}

	private ReportTemplateManagementPanel reportTemplateManagementPanel;
	private StandardReportPanel standardReportPanel;
	private XsltReportPanel xsltReportPanel;
}
