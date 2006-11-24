/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */

package org.conservationmeasures.eam.diagram.cells;

import java.awt.Point;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.cells.DiagramFactor;
import org.conservationmeasures.eam.diagram.cells.FactorDataHelper;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.testall.EAMTestCase;

public class TestFactorDataHelper extends EAMTestCase 
{

	public TestFactorDataHelper(String name)
	{
		super(name);
	}
	
	
	public void setUp() throws Exception 
	{
		DiagramFactorId nodeId1 = new DiagramFactorId(44);
		DiagramFactorId nodeId2 = new DiagramFactorId(25);
		DiagramFactorId nodeId3 = new DiagramFactorId(6346);

		Target cmTarget1 = new Target(originalNodeId1);
		node1 = DiagramFactor.wrapConceptualModelObject(nodeId1, cmTarget1);
		nodeLocation1 = new Point(nodeLocation1x,nodeLocation1y);
		
		Target cmTarget2 = new Target(originalNodeId2);
		node2 = DiagramFactor.wrapConceptualModelObject(nodeId2, cmTarget2);
		nodeLocation2 = new Point(nodeLocation2x,nodeLocation2y);

		Target cmTarget3 = new Target(originalNodeId3);
		node3 = DiagramFactor.wrapConceptualModelObject(nodeId3, cmTarget3);
		
		nodes = new Vector();
		nodes.add(node1);
		nodes.add(node2);
		nodes.add(node3);
		
		super.setUp();
	}


	public void testBasics()
	{
		FactorDataHelper dataHelper = new FactorDataHelper(nodes);
		assertEquals(node1.getDiagramNodeId(), dataHelper.getNewId(node1.getDiagramNodeId()));
		assertEquals(node2.getDiagramNodeId(), dataHelper.getNewId(node2.getDiagramNodeId()));
		assertEquals(node3.getDiagramNodeId(), dataHelper.getNewId(node3.getDiagramNodeId()));
		assertEquals(BaseId.INVALID, dataHelper.getNewId(unknownDiagramId));
	}
	
	public void testSetNewId()
	{
		FactorDataHelper dataHelper = new FactorDataHelper(nodes);
		dataHelper.setNewId(node1.getDiagramNodeId(), newNodeId1);
		dataHelper.setNewId(node2.getDiagramNodeId(), newNodeId2);
		dataHelper.setNewId(node3.getDiagramNodeId(), newNodeId3);
		assertEquals(newNodeId1, dataHelper.getNewId(node1.getDiagramNodeId()));
		assertEquals(newNodeId2, dataHelper.getNewId(node2.getDiagramNodeId()));
		assertEquals(newNodeId3, dataHelper.getNewId(node3.getDiagramNodeId()));
	}
	
	public void testSetGetLocation()
	{
		FactorDataHelper dataHelper = new FactorDataHelper(nodes);
		dataHelper.setOriginalLocation(node1.getDiagramNodeId(), nodeLocation1);
		int insertX = 0;
		int insertY = 0;
		Point insertionPoint = new Point(insertX, insertY);
		
		Point newNode1Location = dataHelper.getNewLocation(node1.getDiagramNodeId(), insertionPoint);
		assertEquals(insertX, newNode1Location.x);
		assertEquals(insertY, newNode1Location.y);
		
		dataHelper.setOriginalLocation(node2.getDiagramNodeId(), nodeLocation2);
		newNode1Location = dataHelper.getNewLocation(node1.getDiagramNodeId(), insertionPoint);
		Point newNode2Location = dataHelper.getNewLocation(node2.getDiagramNodeId(), insertionPoint);
		assertEquals(insertX+(nodeLocation1x-nodeLocation2x), newNode1Location.x);
		assertEquals(insertY+(nodeLocation1y-nodeLocation2y), newNode1Location.y);
		assertEquals(insertX, newNode2Location.x);
		assertEquals(insertY, newNode2Location.y);

		insertX = 50;
		insertY = 50;
		insertionPoint.setLocation(insertX, insertY); 
		FactorDataHelper dataHelper2 = new FactorDataHelper(nodes);
		dataHelper2.setOriginalLocation(node1.getDiagramNodeId(), nodeLocation1);
		newNode1Location = dataHelper2.getNewLocation(node1.getDiagramNodeId(), insertionPoint);
		assertEquals(insertX, newNode1Location.x);
		assertEquals(insertY, newNode1Location.y);
		
		dataHelper2.setOriginalLocation(node2.getDiagramNodeId(), nodeLocation2);
		newNode1Location = dataHelper2.getNewLocation(node1.getDiagramNodeId(), insertionPoint);
		int deltaX = 45;
		int deltaY = 40;
		assertEquals(nodeLocation1x+deltaX, newNode1Location.x);
		assertEquals(nodeLocation1y+deltaY, newNode1Location.y);

		newNode2Location = dataHelper2.getNewLocation(node2.getDiagramNodeId(), insertionPoint);
		assertEquals(nodeLocation2x+deltaX, newNode2Location.x);
		assertEquals(nodeLocation2y+deltaY, newNode2Location.y);
		
	}
	

	final FactorId originalNodeId1 = new FactorId(1);
	final FactorId originalNodeId2 = new FactorId(2);
	final FactorId originalNodeId3 = new FactorId(3);
	final DiagramFactorId newNodeId1 = new DiagramFactorId(5);
	final DiagramFactorId newNodeId2 = new DiagramFactorId(6);
	final DiagramFactorId newNodeId3 = new DiagramFactorId(7);
	final FactorId unknownModelId = new FactorId(10);
	final DiagramFactorId unknownDiagramId = new DiagramFactorId(11);
	final int nodeLocation1x = 20;
	final int nodeLocation1y = 50;
	final int nodeLocation2x = 5;
	final int nodeLocation2y = 10;
	
	DiagramFactor node1;
	DiagramFactor node2;
	DiagramFactor node3;

	Vector nodes;
	Point nodeLocation1;
	Point nodeLocation2;
}
