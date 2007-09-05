/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.planning;

import org.conservationmeasures.eam.exceptions.CommandFailedException;

public class RenamePlanningViewConfigurationDoer extends AbstractPlanningViewConfigurationDoer
{
	public void doIt() throws CommandFailedException
	{
		if (! isAvailable())
			return;
	}
}
