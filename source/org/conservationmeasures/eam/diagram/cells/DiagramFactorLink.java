/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram.cells;

import java.awt.Color;

import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.ids.DiagramFactorLinkId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.objects.FactorLink;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphConstants;

public class DiagramFactorLink extends EAMGraphCell implements Edge
{
	public DiagramFactorLink(DiagramModel model, FactorLink cmLinkage) throws Exception
	{
		underlyingObject = cmLinkage;
		from = model.getNodeById(cmLinkage.getFromNodeId());
		to = model.getNodeById(cmLinkage.getToNodeId());
		String label = "";
		fillConnectorAttributeMap(label);
	}
	
	public boolean isLinkage()
	{
		return true;
	}
	
	public FactorLinkId getWrappedId()
	{
		return (FactorLinkId)underlyingObject.getId();
	}
	
	public DiagramFactor getFromNode()
	{
		return from;
	}
	
	public DiagramFactor getToNode()
	{
		return to;
	}
	
	public String getStressLabel()
	{
		return underlyingObject.getStressLabel();
	}
	
	private void fillConnectorAttributeMap(String label)
	{
	    GraphConstants.setLineEnd(getAttributes(), GraphConstants.ARROW_SIMPLE);
	    GraphConstants.setValue(getAttributes(), label);
	    GraphConstants.setOpaque(getAttributes(), true);
	    GraphConstants.setBackground(getAttributes(), Color.BLACK);
	    GraphConstants.setForeground(getAttributes(), Color.BLACK);
	    GraphConstants.setGradientColor(getAttributes(), Color.BLACK); //Windows 2000 quirk required to see line.
		int arrow = GraphConstants.ARROW_CLASSIC;
		GraphConstants.setLineEnd(getAttributes(), arrow);
		GraphConstants.setEndFill(getAttributes(), true);
	}

	public Object getSource()
	{
		return getFromNode().getPort();
	}

	public Object getTarget()
	{
		return getToNode().getPort();
	}

	public void setSource(Object source)
	{
		// not allowed--ignore attempts to reset the source
	}

	public void setTarget(Object target)
	{
		// not allowed--ignore attempts to reset the target
	}

	public DiagramFactorLinkId getDiagramLinkageId()
	{
		return new DiagramFactorLinkId(underlyingObject.getId().asInt());
	}
	
	public FactorId getFromModelNodeId()
	{
		return underlyingObject.getFromNodeId();
	}
	
	public FactorId getToModelNodeId()
	{
		return underlyingObject.getToNodeId();
	}
	
	public FactorLinkDataMap createLinkageDataMap() throws Exception
	{
		FactorLinkDataMap dataMap = new FactorLinkDataMap();
		dataMap.setId(getDiagramLinkageId());
		dataMap.setFromId(from.getDiagramNodeId());
		dataMap.setToId(to.getDiagramNodeId());
		return dataMap;
	}
	
	private FactorLink underlyingObject;
	private DiagramFactor from;
	private DiagramFactor to;
}
