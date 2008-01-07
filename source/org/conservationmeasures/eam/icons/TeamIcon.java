/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.icons;

import java.awt.Color;

import org.conservationmeasures.eam.diagram.renderers.FactorRenderer;
import org.conservationmeasures.eam.diagram.renderers.RectangleRenderer;

public class TeamIcon extends AbstractShapeIcon
{
	Color getIconColor()
	{
		return LITE_BROWN;
	}

	FactorRenderer getRenderer()
	{
		return new RectangleRenderer();
	}
	
	public int getIconWidth()
	{
		return 6;
	}

	final private static Color LITE_BROWN = new Color(193,142,23);
}
