/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionNewProject extends MainWindowAction
{
	public ActionNewProject(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel(), "icons/new.gif");
	}

	public static String getLabel()
	{
		return EAM.text("Action|New Project");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Create a new project");
	}

}
