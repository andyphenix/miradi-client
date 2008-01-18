/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionAboutCMP extends MainWindowAction
{
	public ActionAboutCMP(MainWindow mainWindow)
	{
		super(mainWindow, getLabel(), "icons/cmp16.png");
	}

	private static String getLabel()
	{
		return EAM.text("Action|About the CMP");
	}

}
