/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram;

import java.awt.geom.Point2D;

import org.conservationmeasures.eam.diagram.nodes.NodeTypeThreat;
import org.jgraph.graph.DefaultGraphModel;

public class DiagramModel extends DefaultGraphModel
{
	public DiagramModel()
	{
	}

	public void createThreatNode(Point2D snappedPoint, String text)
	{
		Node node = new Node(new NodeTypeThreat(), snappedPoint, text);
		insertNode(node);
	}
	
	public void insertNode(Node nodeToInsert)
	{
		Object[] nodes = new Object[] {nodeToInsert};
		insert(nodes, nodeToInsert.getNestedAttributeMap(), null, null, null);
	}

	public int getNodeId(Node node)
	{
		Object[] allNodes = getAll(this);
		for(int i = 0; i < allNodes.length; ++i)
			if(allNodes[i].equals(node))
				return i;
		
		return -1;
	}
	
	public Node getNodeById(int id)
	{
		return (Node)getAll(this)[id];
	}
}
