/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.diagram.renderers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

public class RectangleWithRatingRenderer extends RectangleRenderer
{
	public void fillShape(Graphics g, Rectangle rect, Color color)
	{
		super.fillShape(g, rect, color);
		
		Graphics2D g2 = (Graphics2D)g;
		Paint oldPaint = g2.getPaint();
		drawPriority(g, rect, g2);
		g2.setPaint(oldPaint);
	}
	
	Dimension getInsetDimension()
	{
		return new Dimension(0, PRIORITY_HEIGHT);
	}

	void drawPriority(Graphics g, Rectangle rect, Graphics2D g2) 
	{		
		if (priority == null)
			return;
		
		if (priority.getCode().length() == 0)
			return;
		
		if (priority.getCode().equals("0"))
			return;

		drawRatingBubble(g2, rect, priority.getColor(), priority.getLabel().substring(0,1));
	}
}
