/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.testall;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.conservationmeasures.eam.main.MainWindow;


public class TestMainMenu extends EAMTestCase
{
	public TestMainMenu(String name)
	{
		super(name);
	}
	
	public void testFileMenu()
	{
		MainWindow mainWindow = new MainWindow();
		JMenuBar menuBar = mainWindow.getJMenuBar();
		JMenu fileMenu = menuBar.getMenu(0);
		JMenuItem exitItem = fileMenu.getItem(0);
		Action exitAction = exitItem.getAction();
		assertEquals("Exit", exitAction.getValue(Action.NAME));
	}
}
