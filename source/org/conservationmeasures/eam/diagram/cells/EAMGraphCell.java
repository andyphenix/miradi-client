/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.diagram.cells;

import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.jgraph.graph.DefaultGraphCell;

public class EAMGraphCell extends DefaultGraphCell
{
	public EAMGraphCell()
	{
	}

	public boolean isFactor()
	{
		return false;
	}
	
	public boolean isProjectScope()
	{
		return false;
	}
	
	public boolean isFactorLink()
	{
		return false;
	}
	
	public DiagramFactorLink getDiagramFactorLink()
	{
		return null;
	}
}
