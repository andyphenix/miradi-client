/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.views.ViewDoer;

public class ToggleSlideShowPanelDoer extends ViewDoer
{
	public boolean isAvailable()
	{
		return isDiagramView();
	}

	public void doIt()
	{
		if(!isAvailable())
			return;
		
		try
		{
			if (getDiagramView().isSidePanelVisible())
				getDiagramView().hideSidePanel();
			else
				getDiagramView().showSidePanel();
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}
}
		
