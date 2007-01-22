/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.noproject.wizard;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.Date;

import javax.swing.JPanel;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.utils.HyperlinkLabel;
import org.conservationmeasures.eam.views.umbrella.CreateProjectDialog;
import org.martus.swing.HyperlinkHandler;
import org.martus.swing.UiLabel;
import org.martus.util.MultiCalendar;

import com.jhlabs.awt.BasicGridLayout;

public class ProjectList extends JPanel
{
	public ProjectList(HyperlinkHandler handlerToUse)
	{
		super(new BasicGridLayout(0, 2));
		handler = handlerToUse;
		
		setBackground(Color.WHITE);
		refresh();
	}
	
	public void refresh()
	{
		removeAll();
		add(new UiLabel("Project Filename"));
		add(new UiLabel("Last Modified"));
		
		File[] projectDirectories = getProjectDirectories();
		for(int i = 0; i < projectDirectories.length; ++i)
		{
			File projectFile = projectDirectories[i];
			String name = projectFile.getName();
			MultiCalendar date = new MultiCalendar(new Date(projectFile.lastModified()));
			String isoDate = date.toIsoDateString();
			add(new HyperlinkLabel(name, NoProjectWizardPanel.OPEN_PREFIX+name, handler));
			add(new TableHeadingText(isoDate));
		}
		
		// NOTE: invalidate() is not strong enough to blank the bottom row after delete
		repaint();
	}

	public File[] getProjectDirectories()
	{
		File home = EAM.getHomeDirectory();
		home.mkdirs();
		return home.listFiles(new CreateProjectDialog.DirectoryFilter());

	}
	
	class TableHeadingText extends UiLabel
	{
		public TableHeadingText(String text)
		{
			super(text);
			setFont(getFont().deriveFont(Font.BOLD));
		}
		
	}

	HyperlinkHandler handler;
}
