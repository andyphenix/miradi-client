/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.icons;

import java.awt.Color;

import org.conservationmeasures.eam.diagram.renderers.FactorRenderer;
import org.conservationmeasures.eam.diagram.renderers.RectangleRenderer;

public class FundingSourceIcon  extends AbstractShapeIcon 
{

	FactorRenderer getRenderer() 
	{
		return new RectangleRenderer();
	}

	Color getIconColor() 
	{
		return Color.GREEN;
	}
	
	public int getIconHeight()
	{
		return 10;
	}

}