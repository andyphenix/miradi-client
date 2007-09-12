/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.icons;

import java.awt.Color;

import org.conservationmeasures.eam.diagram.renderers.FactorRenderer;
import org.conservationmeasures.eam.diagram.renderers.RoundRectangleRenderer;

public class MethodIcon extends AbstractShapeIcon
{
	FactorRenderer getRenderer()
	{
		return new RoundRectangleRenderer();
	}
	
	Color getIconColor()
	{
		return FactorRenderer.INDICATOR_COLOR;
	}
}
