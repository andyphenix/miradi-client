/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram.renderers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

public class RoundRectangleRenderer extends RectangleRenderer
{
	public void fillShape(Graphics g, Rectangle rect, Color color)
	{
		Graphics2D g2 = (Graphics2D)g;
		Paint oldPaint = g2.getPaint();
		setPaint(g2, rect, color);
		g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, getArcWidth(rect), getArcHeight(rect));
	
		g2.setPaint(oldPaint);
	}

	public void drawBorder(Graphics2D g2, Rectangle rect, Color color)
	{
		Color oldColor = g2.getColor();
		g2.setColor(color);
		g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, getArcWidth(rect), getArcHeight(rect));
		g2.setColor(oldColor);
	}

	int getArcWidth(Rectangle rect)
	{
		return rect.width / 20;
	}
	
	int getArcHeight(Rectangle rect)
	{
		return rect.height / 20;
	}

}
