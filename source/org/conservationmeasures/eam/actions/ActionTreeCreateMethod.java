/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.icons.MethodIcon;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionTreeCreateMethod extends ObjectsAction
{
	public ActionTreeCreateMethod(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel(), new MethodIcon());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Manage|Create Method");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Create a Method for the selected Indicator");
	}
}
