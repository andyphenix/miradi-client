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
package org.miradi.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.martus.swing.UiScrollPane;
import org.miradi.diagram.DiagramComponent;
import org.miradi.icons.AbstractMiradiIcon;
import org.miradi.main.MainWindow;
import org.miradi.objects.DiagramObject;
import org.miradi.views.diagram.DiagramSplitPane;


public  class BufferedImageFactory
{
	public static BufferedImage getImage(AbstractMiradiIcon icon)
	{
		int width = icon.getIconWidth() + 2 * INSET;
		int height = icon.getIconHeight() + 2 * INSET;
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		icon.paintIcon(null, graphics, 0, 0);
		graphics.dispose();
		
		return image;
	}
	
	public static BufferedImage getImage(JComponent swingComponent,  int inset) 
	{
		realizeComponent(swingComponent);

		Rectangle2D bounds = new Rectangle(swingComponent.getPreferredSize());
		if (bounds == null) 
			return null;
		
		toScreen(bounds);
		int width = (int) bounds.getWidth() + 2 * inset;
		int height = (int) bounds.getHeight() + 2 * inset;
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		swingComponent.print(graphics);
		graphics.dispose();
		return image;

	}

	public static void realizeComponent(JComponent swingComponent)
	{
		//TODO: is there a better way to do this
		JFrame frame = new JFrame();
		frame.add(new UiScrollPane(swingComponent));
		frame.pack();
	}
	
	public static BufferedImage createImageFromDiagram(MainWindow mainWindow, DiagramObject diagramObject) throws Exception
	{
		DiagramComponent diagram = BufferedImageFactory.createDiagramComponent(mainWindow, diagramObject);

		Rectangle bounds = new Rectangle(diagram.getTotalBoundsUsed().getBounds());
		diagram.toScreen(bounds);
		diagram.setToDefaultBackgroundColor();
		diagram.setGridVisible(false);

		BufferedImage image = BufferedImageFactory.getImage(diagram, 5);

		int x = Math.max(bounds.x, 0);
		int y = Math.max(bounds.y, 0);
		int imageWidth = image.getWidth() - x; 
		int imageHeight = image.getHeight() - y;

		return image.getSubimage(x, y, imageWidth, imageHeight);
	}
	
	private static void toScreen(Rectangle2D rect) 
	{
		rect.setFrame(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}
	
	public static BufferedImage createImageFromTable(JTable table)
	{
		JScrollPane scrollerToShowHeaders = new JScrollPane(table);
		scrollerToShowHeaders.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollerToShowHeaders.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollerToShowHeaders.getViewport().setPreferredSize(table.getPreferredSize());
		return createImageFromComponent(scrollerToShowHeaders);
	}

	public static BufferedImage createImageFromComponent(JComponent component)
	{
		return getImage(component, INSET);
	}
	
	public static DiagramComponent createDiagramComponent(MainWindow mainWindow, DiagramObject diagramObject) throws Exception
	{
		DiagramComponent diagram =  DiagramSplitPane.createDiagram(mainWindow, diagramObject);
		diagram.setScale(1.0);
		diagram.getDiagramModel().updateVisibilityOfFactorsAndLinks();
		
		// TODO: This is here because setting a factor/link to be visible also has
		// the side effect of selecting it, so the last item added is selected but 
		// shouldn't be. So our quick fix is to clear the selection. 
		// Cleaner fixes ran into strange problems where Windows and Linux systems
		// behaved differently. SEE ALSO DiagramSplitPane.showCard()
		diagram.clearSelection();
		
		return diagram;
	}
	
	private static final int INSET = 5;
}
