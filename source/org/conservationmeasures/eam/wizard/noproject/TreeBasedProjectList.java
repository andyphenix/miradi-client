/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.wizard.noproject;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JPanel;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class TreeBasedProjectList extends JPanel
{
	public TreeBasedProjectList(MainWindow mainWindow, NoProjectWizardStep handlerToUse) throws Exception
	{
		setLayout(new BorderLayout());
		File home = EAM.getHomeDirectory();
		rootNode = new FileSystemTreeNode(home);
		ProjectListTreeTableModel model = new ProjectListTreeTableModel(rootNode);
		add(new ProjectListTreeTable(model, handlerToUse), BorderLayout.CENTER);
	}

	public void refresh()
	{
		try
		{
			rootNode.rebuild();
		}
		catch(Exception e)
		{
			EAM.panic(e);
		}
		repaint();
	}

	private FileSystemTreeNode rootNode;
}
