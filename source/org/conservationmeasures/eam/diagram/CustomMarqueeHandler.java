/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.diagram;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.objects.DiagramLink;
import org.conservationmeasures.eam.utils.PointList;
import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;

public class CustomMarqueeHandler extends BasicMarqueeHandler
{
	public CustomMarqueeHandler(DiagramComponent diagramToUse)
	{
		diagram = diagramToUse;
	}
	
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		diagram.setMarquee(true);
	}
	
	public void mouseReleased(MouseEvent arg0)
	{
		super.mouseReleased(arg0);
		diagram.setMarquee(false);
	}

	public void handleMarqueeEvent(MouseEvent e, JGraph graph, Rectangle2D bounds)
	{
		super.handleMarqueeEvent(e, graph, bounds);
		selectAllBendPointsInBouds();
	}

	private void selectAllBendPointsInBouds()
	{
		DiagramModel model = diagram.getDiagramModel();
		DiagramLink[] allLinks = model.getAllDiagramLinksAsArray();
		
		for (int i = 0 ; i < allLinks.length; ++i)
		{
			selectBendPointsAndLinksInBounds(model, allLinks[i]);
		}
	}

	private void selectBendPointsAndLinksInBounds(DiagramModel model, DiagramLink diagramLink)
	{
		LinkCell linkCell = model.getDiagramFactorLink(diagramLink);
		Rectangle2D.Double scaledBounds = diagram.getScaledBounds(linkCell);
		if (! marqueeBounds.intersects(scaledBounds))
				return;
		
		PointList bendPoints = diagramLink.getBendPoints();
		for (int i = 0; i < bendPoints.size(); ++i)
		{
			Point bendPoint = bendPoints.get(i);
			Point2D.Double scaledBendPoint = diagram.getScaledPoint(bendPoint);
			if (marqueeBounds.contains(scaledBendPoint))
			{
				diagram.addSelectionCell(linkCell);
				linkCell.getBendPointSelectionHelper().addToSelectionIndexList(i);
			}
		}
	}

	private DiagramComponent diagram;
}
