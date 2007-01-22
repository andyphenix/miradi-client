/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.umbrella.PrintDoer;

public class Print extends PrintDoer 
{
	public boolean isAvailable() 
	{
		Project project = getMainWindow().getProject();
		if(!project.isOpen())
			return false;
		return project.getDiagramModel().getFactorCount() > 0;
	}
}
