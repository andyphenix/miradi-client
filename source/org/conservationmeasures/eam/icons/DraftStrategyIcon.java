/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.icons;

import java.awt.Color;

import org.conservationmeasures.eam.diagram.DiagramConstants;
import org.conservationmeasures.eam.diagram.renderers.IconHexagonRenderer;
import org.conservationmeasures.eam.diagram.renderers.FactorRenderer;

public class DraftStrategyIcon extends AbstractShapeIcon
{
	FactorRenderer getRenderer()
	{
		return new IconHexagonRenderer(true);
	}
	
	Color getIconColor()
	{
		return DiagramConstants.COLOR_DRAFT_STRATEGY;
	}
}
