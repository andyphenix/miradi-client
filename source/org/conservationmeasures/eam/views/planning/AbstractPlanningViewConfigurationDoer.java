/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.planning;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.views.ViewDoer;

abstract public class AbstractPlanningViewConfigurationDoer extends ViewDoer
{
	public boolean isAvailable()
	{
		if (!isPlanningView())
			return false;
		
		if (!isValidConfigurarionChoice())
			return false;
			
		return true;
	}
	
	private boolean isValidConfigurarionChoice()
	{
		try
		{
			ViewData viewData = getProject().getCurrentViewData();
			String currentStyle = viewData.getData(ViewData.TAG_PLANNING_STYLE_CHOICE);
			if (! currentStyle.equals(PlanningView.CUSTOMIZABLE_RADIO_CHOICE))
				return false;
			
			String orefAsString = viewData.getData(ViewData.TAG_PLANNING_CUSTOM_PLAN_REF);
			ORef currentStyleRef = ORef.createFromString(orefAsString);
			if (! currentStyleRef.getObjectId().isInvalid())
				return true;
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return false;
		}
		
		return false;
	}
}
