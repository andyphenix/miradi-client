/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.diagram.renderers;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

public class HexagonRenderer extends FactorRenderer
{
	Dimension getInsetDimension()
	{
		return getInsetDimension(getSize().width);
	}
	
	static Dimension getInsetDimension(int totalWidth)
	{
		return new Dimension(totalWidth / 5, 0);
	}
	
	@Override
	public Shape getShape(Rectangle rect)
	{
		return buildHexagon(rect);
	}
	
	@Override
	Stroke getStroke()
	{
		Stroke stroke = super.getStroke();
		if(isDraft())
			stroke = getDashedStroke((BasicStroke)stroke);

		return stroke;
	}
	
	boolean isDraft()
	{
		return node.isStatusDraft();
	}

	BasicStroke getDashedStroke(BasicStroke originalStroke)
	{
		BasicStroke defaultStroke = new BasicStroke();
		float[] dashes = {2, 7};
		BasicStroke dashedStroke = new BasicStroke(
				originalStroke.getLineWidth(), defaultStroke.getEndCap(), 
				defaultStroke.getLineJoin(), defaultStroke.getMiterLimit(),
				dashes, 0);
		return dashedStroke;
	}

	public static Polygon buildHexagon(Rectangle rect)
	{
		int left = rect.x;
		int top = rect.y;
		int totalWidth = rect.width;
		int height = rect.height;
		int right = left + totalWidth;
		int bottom = top + height;
		int endInset = getInsetDimension(totalWidth).width;
		int verticalMiddle = top + (height / 2);
		
		Polygon hex = new Polygon();
		hex.addPoint(left, verticalMiddle);
		hex.addPoint(left + endInset, top);
		hex.addPoint(right - endInset, top);
		hex.addPoint(right, verticalMiddle);
		hex.addPoint(right - endInset, bottom);
		hex.addPoint(left + endInset, bottom);
		hex.addPoint(left, verticalMiddle);
		return hex;
	}

}

