/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionHideCellRatings extends MainWindowAction
{
	public ActionHideCellRatings(MainWindow mainWindowToUse)
	{
		//TODO change Icon to somehting that makes sense, this is just for development
		super(mainWindowToUse, getLabel(), "icons/hideRatings.png");
	}
	
	public static String getLabel()
	{
		return EAM.text("Action|Hide Ratings in Cell");
	}
	
	public String getToolTipText()
	{
		return getLabel();
	}
}
