/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.nodes.EAMGraphCell;
import org.conservationmeasures.eam.diagram.nodes.Linkage;
import org.conservationmeasures.eam.diagram.nodes.Node;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphModel;

public class DiagramModel extends DefaultGraphModel
{
	public DiagramModel()
	{
		cellInventory = new CellInventory();
	}
	
	public void clear()
	{
		while(getRootCount() > 0)
			remove(new Object[] {getRootAt(0)});
		cellInventory.clear();
	}

	public Node createNode(int nodeType) throws Exception
	{
		return createNodeAtId(nodeType, Node.INVALID_ID);
	}
	
	public Node createNodeAtId(int nodeType, int id) throws Exception
	{
		Node node = new Node(nodeType);
		Object[] nodes = new Object[] {node};
		Hashtable nestedAttributeMap = node.getNestedAttributeMap();
		insert(nodes, nestedAttributeMap, null, null, null);
		cellInventory.add(node, id);
		DiagramModelEvent nodeAdded = new DiagramModelEvent(this, node, getIndexByCell(node));
		fireNodeAction(nodeAdded, new ModelEventNotifierNodeAdded());
		return node;
	}
	
	public void addDiagramModelListener(DiagramModelListener listener)
	{
		diagramModelListenerList.add(listener);
	}
	
    public void removeMyEventListener(DiagramModelListener listener) 
    {
    	diagramModelListenerList.remove(listener);
    }	
    
    void fireNodeAction(DiagramModelEvent event, ModelEventNotifier eventNotifier) 
    {
        for (int i=0; i<diagramModelListenerList.size(); ++i) 
        {
        	eventNotifier.fileAction((DiagramModelListener)diagramModelListenerList.get(i), event);
        }                
    }
	
    public void deleteNode(Node nodeToDelete) throws Exception
	{
		DiagramModelEvent nodeDeleted = new DiagramModelEvent(this, nodeToDelete, getIndexByCell(nodeToDelete));
		fireNodeAction(nodeDeleted, new ModelEventNotifierNodeDeleted());

		Object[] nodes = new Object[]{nodeToDelete};
		remove(nodes);
		cellInventory.remove(nodeToDelete);
	}
	
	public Linkage createLinkage(int linkageId, int linkFromId, int linkToId) throws Exception
	{
		Node fromNode = getNodeById(linkFromId);
		Node toNode = getNodeById(linkToId);

		Linkage linkage = new Linkage(fromNode, toNode);
		Object[] linkages = new Object[]{linkage};
		Map nestedMap = linkage.getNestedAttributeMap();
		ConnectionSet cs = linkage.getConnectionSet();
		insert(linkages, nestedMap, cs, null, null);
		cellInventory.add(linkage, linkageId);
		return linkage;
	}
	
	public void deleteLinkage(Linkage linkageToDelete)
	{
		Object[] linkages = new Object[]{linkageToDelete};
		remove(linkages);
		cellInventory.remove(linkageToDelete);
	}
	
	public boolean hasLinkage(Node fromNode, Node toNode)
	{
		for(int i=0; i < cellInventory.size(); ++i)
		{
			EAMGraphCell cell = cellInventory.getByIndex(i);
			if(!cell.isLinkage())
				continue;
			
			Linkage linkage = (Linkage)cell;
			Node thisFromNode = linkage.getFromNode();
			Node thisToNode = linkage.getToNode();
			if(thisFromNode.equals(fromNode) && thisToNode.equals(toNode))
				return true;
			
			if(thisFromNode.equals(toNode) && thisToNode.equals(fromNode))
				return true;
		}
		
		return false;
	}
	
	public int getCellCount()
	{
		return cellInventory.size();
	}
	
	public Set getLinkages(Node node)
	{
		return getEdges(this, new Object[] {node});
	}
	
	public void updateCell(EAMGraphCell nodeToUpdate) throws Exception
	{
		edit(nodeToUpdate.getNestedAttributeMap(), null, null, null);
		DiagramModelEvent nodeUpdated = new DiagramModelEvent(this, nodeToUpdate, getIndexByCell(nodeToUpdate));
		fireNodeAction(nodeUpdated, new ModelEventNotifierNodeChanged());
	}
	
	public boolean isNode(EAMGraphCell cell)
	{
		return cell.isNode();
	}
	
	public Node getNodeById(int id) throws Exception
	{
		return (Node)getCellById(id);
	}

	public Linkage getLinkageById(int id) throws Exception
	{
		return (Linkage)getCellById(id);
	}
	
	public boolean isCellInProject(EAMGraphCell cell)
	{
		try
		{
			getCellById(cell.getId());
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public EAMGraphCell getCellById(int id) throws Exception
	{
		EAMGraphCell cell = cellInventory.getById(id);
		if(cell == null)
			throw new Exception("Cell doesn't exist, id: " + id);
		return cell;
	}
	
	public EAMGraphCell getCellByIndex(int index) throws Exception
	{
		EAMGraphCell cell = cellInventory.getByIndex(index);
		if(cell == null)
			throw new Exception("Cell doesn't exist, index: " + index);
		return cell;
	}
	
	public int getIndexByCell(EAMGraphCell cell)throws Exception
	{
		int index = cellInventory.getIndex(cell);
		if(index == -1)
			throw new Exception("Cell doesn't exist id:"+cell.getId());
		return index;
	}
	
	public Vector getAllNodes()
	{
		Vector nodes = new Vector();
		for(int i=0; i < cellInventory.size(); ++i)
		{
			EAMGraphCell cell = cellInventory.getByIndex(i);
			if(cell.isNode())
				nodes.add(cell);
		}	
		return nodes;
	}
	

	CellInventory cellInventory;
	protected List diagramModelListenerList = new ArrayList();
	
}

class CellInventory
{
	public CellInventory()
	{
		cells = new Vector();
	}
	
	void add(EAMGraphCell cell, int id)
	{
		if(id == Node.INVALID_ID)
		{
			id = nextId++;
		}
		else
		{
			if(nextId < id+1)
				nextId = id + 1;
		}
		
		if(getById(id) != null)
			throw new RuntimeException("Can't add over existing id " + id);
		
		cell.setId(id);
		cells.add(cell);
	}
	
	public int getIndex(EAMGraphCell cellToFind)
	{
		for (int index = 0; index < cells.size(); ++index) 
		{
			EAMGraphCell cell = (EAMGraphCell) cells.get(index);
			if(cell.getId() == cellToFind.getId())
				return index;
		}
		return -1;
	}

	public int size()
	{
		return cells.size();
	}
	
	public EAMGraphCell getByIndex(int index)
	{
		return (EAMGraphCell)cells.get(index);
	}
	
	public EAMGraphCell getById(int id)
	{
		for (Iterator iter = cells.iterator(); iter.hasNext();) 
		{
			EAMGraphCell cell = (EAMGraphCell) iter.next();
			if(cell.getId() == id)
				return cell;
		}
		return null;
	}
	
	public void remove(EAMGraphCell cell)
	{
		cells.remove(cell);
	}
	
	public void clear()
	{
		nextId = 0;
		cells.clear();
	}

	private int nextId;
	Vector cells;
}
