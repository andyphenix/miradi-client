/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.icons.FactorLinkIcon;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionInsertFactorLink extends MainWindowAction
{
	public ActionInsertFactorLink(MainWindow mainWindow)
	{
		super(mainWindow, getLabel(), new FactorLinkIcon());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Insert|Link...");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Add a link between two factors");
	}

}

