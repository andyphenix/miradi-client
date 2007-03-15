/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.views.ProjectDoer;

public class CreateBendPointDoer extends ProjectDoer
{
	public boolean isAvailable()
	{
		return false;
	}

	public void doIt() throws CommandFailedException
	{
		if (!isAvailable())
			return;
	}
}
