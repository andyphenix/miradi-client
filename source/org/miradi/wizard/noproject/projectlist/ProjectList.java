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
package org.miradi.wizard.noproject.projectlist;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


import org.martus.swing.HyperlinkHandler;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.legacyprojects.LegacyProjectUtilities;
import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;
import org.miradi.utils.HyperlinkLabel;
import org.miradi.views.umbrella.CreateProjectDialog;
import org.miradi.wizard.noproject.NoProjectWizardStep;

import com.jhlabs.awt.GridLayoutPlus;

public class ProjectList extends JPanel
{
	public ProjectList(LegacyProjectUtilities databaseToUse, HyperlinkHandler handlerToUse) throws Exception
	{
		handler = handlerToUse;

		int COL_GUTTER = 5;
		int ROW_GUTTER = 0;
		GridLayoutPlus layout = new GridLayoutPlus(0, 2, COL_GUTTER, ROW_GUTTER);
		setLayout(layout);
		
		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setBackground(AppPreferences.getWizardBackgroundColor());
		refresh();
	}
	
	public void refresh() throws Exception
	{
		removeAll();
		add(new TableHeadingText("Project Filename"));
		add(new TableHeadingText("Last Modified"));
		
		Vector<String> projectNames = new Vector<String>(ProjectList.getListOfProjectsIn(""));
		Collections.sort(projectNames);
		for(String name : projectNames)
		{
			String isoDate = EAM.text("(Unknown)");
			add(new HyperlinkLabel(name, NoProjectWizardStep.OPEN_PREFIX+"projects/"+name, handler));
			add(new HtmlLabel("<font size='%100'>" + isoDate + "</font>"));
		}
		
		// NOTE: invalidate() is not strong enough to blank the bottom row after delete
		repaint();
	}

	public File[] getProjectDirectories()
	{
		File home = EAM.getHomeDirectory();
		home.mkdirs();
		return home.listFiles(new CreateProjectDialog.ProjectFilter());

	}
	
	public static Set<String> getListOfProjectsIn(String directory) throws Exception
	{
		File directoryFile = new File(directory);
		String[] projectNames = directoryFile.list();
		if(projectNames == null)
			projectNames = new String[0];
		return new HashSet<String>(Arrays.asList(projectNames));
	}

	class HtmlLabel extends PanelTitleLabel
	{
		public HtmlLabel(String text)
		{
			super("<html><span style='background-color: " + AppPreferences.getWizardBackgroundColorForCss() + ";'" + text + "</span></html>");
		}
	}
	
	class TableHeadingText extends HtmlLabel
	{
		public TableHeadingText(String text)
		{
			super("<strong><font size='+1'>" + text + "</font></strong>");
		}
		
	}

	private HyperlinkHandler handler;
}
