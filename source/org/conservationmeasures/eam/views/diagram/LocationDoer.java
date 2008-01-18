/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.awt.Point;

import org.conservationmeasures.eam.views.ViewDoer;

abstract public class LocationDoer extends ViewDoer
{
	public void setLocation(Point locationToUse)
	{
		location = locationToUse;
	}
	
	public Point getLocation()
	{
		return location;
	}

	Point location;
}
