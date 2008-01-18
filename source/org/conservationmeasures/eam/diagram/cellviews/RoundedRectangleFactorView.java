/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.diagram.cellviews;

import java.awt.geom.Point2D;

import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.diagram.renderers.RoundRectangleRenderer;
import org.conservationmeasures.eam.main.EAM;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCellEditor;

public class RoundedRectangleFactorView extends FactorView
{
	public RoundedRectangleFactorView(FactorCell factor)
	{
		super(factor);
	}

    public CellViewRenderer getRenderer() 
    {
        return roundedRectangleRenderer;
    }

    public GraphCellEditor getEditor() 
    {
    	EAM.logDebug("WARNING: RectangleNodeView.getEditor not implemented");
        return null;
    }
    
	/**
	 * Returns the intersection of the bounding rectangle and the
	 * straight line between the source and the specified point p.
	 * The specified point is expected not to intersect the bounds.
	 */
	public Point2D getPerimeterPoint(EdgeView arg0, Point2D source, Point2D p)
	{
		return ((RoundRectangleRenderer)getRenderer()).getPerimeterPoint(p, getBounds());
	}
	
	protected static RoundRectangleRenderer roundedRectangleRenderer = new RoundRectangleRenderer(40);

}
