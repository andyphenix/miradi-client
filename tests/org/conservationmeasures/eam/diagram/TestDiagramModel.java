/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.FactorSet;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.DiagramLink;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.views.diagram.LinkCreator;

public class TestDiagramModel extends EAMTestCase
{
	public TestDiagramModel(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
		model = project.getDiagramModel();
		idAssigner = new IdAssigner();
	}

	public void tearDown() throws Exception
	{
		project.close();
		super.tearDown();
	}
	
	public void testGetNodesInChain() throws Exception
	{
		int[] linkagePairs = getLinkagePairs();
		SampleDiagramBuilder.buildNodeGrid(project, 7, linkagePairs);
		int[][] expectedNodesInChain = getExpectedNodesInChain();
		
		for(int threatIndex = 0; threatIndex < expectedNodesInChain.length; ++threatIndex)
		{
			int[] expectedChainNodeIds = expectedNodesInChain[threatIndex];
			String label = Integer.toString(expectedChainNodeIds[0]);
			Factor cmNode = findFactor(label);
			
			FactorSet gotChainNodes = model.getNodesInChain(cmNode);
			FactorSet foundNodes = findNodes(expectedChainNodeIds);
			assertEquals("wrong direct threat chain nodes for " + expectedChainNodeIds[0] + "?", foundNodes, gotChainNodes);
		}
	}
	
	private Factor findFactor(String label)
	{
		DiagramFactor[] diagramFactors = project.getAllDiagramFactors();
		for (int i = 0; i < diagramFactors.length; ++i)
		{
			FactorId factorId = diagramFactors[i].getWrappedId();
			Factor factor = (Factor) project.findObject(ObjectType.FACTOR, factorId);
			if (factor.getLabel().equals(label))
				return factor;
		}
		
		return null;
	}

	private DiagramFactor findDiagramFactor(String label)
	{
		DiagramFactor[] diagramFactors = project.getAllDiagramFactors();
		for (int i = 0; i < diagramFactors.length; ++i)
		{
			FactorId factorId = diagramFactors[i].getWrappedId();
			Factor factor = (Factor) project.findObject(ObjectType.FACTOR, factorId);
			if (factor.getLabel().equals(label))
				return diagramFactors[i];
		}
		
		return null;
	}

	private int[][] getExpectedNodesInChain()
	{
		int[][] expectedNodesInChain = {
				{ 31,  11, 12, 13, 21, 22, 41 },
				{ 32,  13, 23, 41 },
				{ 33,  13, 23, 42, 43 },
				{ 34,  24, 25, 26, 35, 36, 44 },
				{ 35,  25, 26, 34, 36, 45 },
				// 36 is actually an INDIRECT FACTOR!
		};
		return expectedNodesInChain;
		
	}
	
	private int[] getLinkagePairs()
	{
		int[] linkagePairs = {
				11, 21,		21, 31, 	31, 41,
				12, 21, 	22, 31, 	32, 41,
				13, 22, 	23, 32, 	33, 42,
				13, 23, 	23, 33, 	33, 43,
				24, 34, 	34, 44,
				25, 35, 	35, 45,
				26, 36, 	36, 35, 	35, 34,
		};
		return linkagePairs;
	}
	
	
	private int[] getLoopLinkagePairs()
	{
		int[] linkagePairs = {
				11, 21,		21, 31, 	31, 41,		41, 11,  	31,11
		};
		return linkagePairs;
	}
	
	private int[][] getExpectedNodesInLoopChain()
	{
		int[][] expectedNodesInLoopChain = {
				{ 11, 21, 31, 41,}
		};
		return expectedNodesInLoopChain;
	}
	
	public void testLoopChainIds() throws Exception
	{
		int[] linkagePairs = getLoopLinkagePairs();
		SampleDiagramBuilder.buildNodeGrid(project, 1, linkagePairs);
		int[][] expectedNodesInChain = getExpectedNodesInLoopChain();

		for(int nodeIndex = 0; nodeIndex < expectedNodesInChain.length; ++nodeIndex)
		{
			int[] expectedChainNodeIds = expectedNodesInChain[nodeIndex];
			for (int i=0; i<expectedChainNodeIds.length; ++i) 
			{
				String label = Integer.toString(expectedChainNodeIds[i]);
				DiagramFactor diagramFactor = findDiagramFactor(label);
				
				FactorSet gotUpChainNodes = model.getAllUpstreamNodes(diagramFactor);
				Set gotLabels1 = getLabelSet(gotUpChainNodes);
				assertEquals("wrong upstream chain nodes for " + expectedChainNodeIds[i] + "?", toSet(expectedChainNodeIds), gotLabels1);
				
				FactorSet gotDownChainNodes = model.getAllDownstreamNodes(diagramFactor);
				Set gotLabels2 = getLabelSet(gotDownChainNodes);
				assertEquals("wrong downstream chain nodes for " + expectedChainNodeIds[i] + "?", toSet(expectedChainNodeIds), gotLabels2);
			}
		}
	}
	
	
	
	public void testGetChainIds() throws Exception
	{
		int[] linkagePairs = getLinkagePairs();
		SampleDiagramBuilder.buildNodeGrid(project, 7, linkagePairs);
		int[][] expectedNodesInChain = getExpectedNodesInChain();
				
		for(int threatIndex = 0; threatIndex < expectedNodesInChain.length; ++threatIndex)
		{
			int[] expectedChainNodeIds = expectedNodesInChain[threatIndex];
			String label = Integer.toString(expectedChainNodeIds[0]);
			DiagramFactor diagramFactor = findDiagramFactor(label);
			FactorSet gotChainNodes = model.getDirectThreatChainNodes(diagramFactor);
			Set gotLabels = getLabelSet(gotChainNodes);
			assertEquals("wrong direct threat chain nodes for " + expectedChainNodeIds[0] + "?", toSet(expectedChainNodeIds), gotLabels);
		}

		int[][] expectedNodesInFullChain = {
				{ 37 },
				{ 11, 21, 31, 41 },
				{ 13, 22, 23, 31, 32, 33, 41, 42, 43 },
				{ 21, 11, 12, 31, 41 },
				{ 23, 13, 32, 33, 41, 42, 43 },
				{ 41, 11, 12, 13, 21, 22, 23, 31, 32 },
		};

		for(int nodeIndex = 0; nodeIndex < expectedNodesInFullChain.length; ++nodeIndex)
		{
			int[] expectedChainNodeIds = expectedNodesInFullChain[nodeIndex];
			String label = Integer.toString(expectedChainNodeIds[0]);
			DiagramFactor diagramFactor = findDiagramFactor(label);
			FactorSet gotChainNodes = model.getAllUpstreamDownstreamNodes(diagramFactor);
			Set gotLabels = getLabelSet(gotChainNodes);
			assertEquals("wrong chain nodes for " + expectedChainNodeIds[0] + "?", toSet(expectedChainNodeIds), gotLabels);
		}
	}
	
	private Set toSet(int[] ints)
	{
		HashSet set = new HashSet();
		for (int i = 0; i < ints.length; ++i)
		{
			set.add(Integer.toString(ints[i]));
		}
		
		return set;
	}
	
	private Set getLabelSet(FactorSet gotChainNodes)
	{
		Factor[] factors = gotChainNodes.toFactorArray();
		HashSet set = new HashSet();
		for (int i = 0; i < factors.length; ++i)
		{
			set.add(factors[i].getLabel());
		}
		
		return set;
	}

	public FactorSet findNodes(int[] values)
	{
		FactorSet result = new FactorSet();
		for(int i = 0; i < values.length; ++i)
		{	
			String valueAsString = Integer.toString(values[i]);
			result.attemptToAdd(findFactor(valueAsString));
		}
		
		return result;
	}

	public void testIsNode() throws Exception
	{
		FactorCell factor = project.createFactorCell(ObjectType.CAUSE);
		FactorCell target = project.createFactorCell(ObjectType.TARGET);
		assertTrue("factor isn't a node?", factor.isFactor());
		assertTrue("target isn't a node?", target.isFactor());
	}
	
	public void testCounts()throws Exception
	{
		FactorCell factor = project.createFactorCell(ObjectType.CAUSE);
		FactorCell target = project.createFactorCell(ObjectType.TARGET);
		createLinkage(new FactorLinkId(BaseId.INVALID.asInt()), factor.getDiagramFactor(), target.getDiagramFactor());
		assertEquals(2, model.getFactorCount());
		assertEquals(1, model.getFactorLinkCount());
	}
	
	public void testHasLinkage() throws Exception
	{
		DiagramFactorId diagramFactorId = project.createAndAddFactorToDiagram(ObjectType.CAUSE);
		model.removeDiagramFactor(diagramFactorId);
		DiagramFactorId newFactorId = project.createAndAddFactorToDiagram(ObjectType.CAUSE);
		DiagramFactorId targetId = project.createAndAddFactorToDiagram(ObjectType.TARGET);
		assertFalse("already linked?", model.areLinked(newFactorId, targetId));
		
		DiagramFactor newDiagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, newFactorId));
		DiagramFactor target = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, targetId));
		createLinkage(new FactorLinkId(BaseId.INVALID.asInt()), newDiagramFactor, target);
		
		assertTrue("not linked?", model.areLinked(newFactorId, targetId));
		assertTrue("reverse link not detected?", model.areLinked(targetId, newFactorId));
	}
	
	public void testGetLinkages() throws Exception
	{
		FactorCell factor1 = project.createFactorCell(ObjectType.CAUSE);
		FactorCell factor2 = project.createFactorCell(ObjectType.CAUSE);
		FactorCell target = project.createFactorCell(ObjectType.TARGET);
		DiagramLink linkage1 = createLinkage(takeNextLinkageId(), factor1.getDiagramFactor(), target.getDiagramFactor());
		DiagramLink linkage2 = createLinkage(takeNextLinkageId(), factor2.getDiagramFactor(), target.getDiagramFactor());
		Set found = model.getFactorLinks(target);
		assertEquals("Didn't see both links?", 2, found.size());
		assertTrue("missed first?", found.contains(model.findLinkCell(linkage1)));
		assertTrue("missed second?", found.contains(model.findLinkCell(linkage2)));
	}

	private FactorLinkId takeNextLinkageId()
	{
		return new FactorLinkId(idAssigner.takeNextId().asInt());
	}
	
	
	public void testCreateNode() throws Exception
	{
		project.createFactorCell(ObjectType.TARGET);		
		DiagramFactorId diagramFactorId = project.createAndAddFactorToDiagram(ObjectType.CAUSE);
		project.createFactorCell(ObjectType.TARGET);
		FactorCell lastCreated = project.createFactorCell(ObjectType.TARGET);		
		model.removeDiagramFactor(diagramFactorId);
		FactorCell nodeAfterUndo = project.createFactorCell(ObjectType.TARGET);
		
		assertTrue("reused an id?", nodeAfterUndo.getDiagramFactorId().asInt() > lastCreated.getDiagramFactorId().asInt());
	}

	public void testGetAllNodes() throws Exception
	{
		DiagramFactor node1 = project.createDiagramFactorAndAddToDiagram(ObjectType.TARGET);
		DiagramFactor node2 = project.createDiagramFactorAndAddToDiagram(ObjectType.TARGET);		
		createLinkage(new FactorLinkId(BaseId.INVALID.asInt()), node1, node2);
		DiagramFactor node3 = project.createDiagramFactorAndAddToDiagram(ObjectType.TARGET);		
		
		DiagramFactor[] diagramFactors = project.getAllDiagramFactors();
		assertEquals(3, diagramFactors.length);
		assertContains(node1, diagramFactors);
		assertContains(node2, diagramFactors);
		assertContains(node3, diagramFactors);
	}
	
	public void testGetAllLinkages() throws Exception
	{
		FactorCell node1 = project.createFactorCell(ObjectType.TARGET);		
		FactorCell node2 = project.createFactorCell(ObjectType.TARGET);		
		DiagramLink link1 = createLinkage(takeNextLinkageId(), node1.getDiagramFactor(), node2.getDiagramFactor());
		FactorCell node3 = project.createFactorCell(ObjectType.TARGET);		
		DiagramLink link2 = createLinkage(takeNextLinkageId(), node1.getDiagramFactor(), node3.getDiagramFactor());
		
		Vector linkages = model.getAllDiagramFactorLinks();
		assertEquals(2, linkages.size());
		assertContains(link1, linkages);
		assertContains(link2, linkages);
	}
	
	public void testFillFrom() throws Exception
	{
		FactorCell node1 = project.createFactorCell(ObjectType.TARGET);		
		FactorCell node2 = project.createFactorCell(ObjectType.TARGET);		
		DiagramLink link1 = createLinkage(takeNextLinkageId(), node1.getDiagramFactor(), node2.getDiagramFactor());

		DiagramModel copy = new DiagramModel(project);
		copy.fillFrom(model.getDiagramObject());
		
		assertNotNull("missing node1?", copy.getFactorCellById(node1.getDiagramFactorId()));
		assertNotNull("missing node2?", copy.getFactorCellById(node2.getDiagramFactorId()));
		assertNotNull("missing link?", copy.getDiagramFactorLinkById(link1.getDiagramLinkageId()));
	}
	
	public void testActionsFiring() throws Exception
	{
		TestTableModel testModel = new TestTableModel();
		testModel.addListener(model);
		DiagramFactorId diagramFactorId = project.createAndAddFactorToDiagram(ObjectType.CAUSE);
		
		assertEquals("test model wasn't notified of add action?", 1, testModel.nodeAdded);
		assertEquals("node changed already called?", 0, testModel.nodeChanged);
		assertEquals("node deleted already called?", 0, testModel.nodeDeleted);
		assertEquals("node add did a node move notify?", 0, testModel.nodeMoved);
		assertEquals("added already called?", 0, testModel.linkAdded);
		assertEquals("linkage deleted already called?", 0, testModel.linkDeleted);
		
		model.updateDiagramFactor(diagramFactorId);
		assertEquals("update did a node add notify?", 1, testModel.nodeAdded);
		assertEquals("update didn't do a node changed notify?", 1, testModel.nodeChanged);
		assertEquals("test model fired a delete action for a modify?", 0, testModel.nodeDeleted);
		assertEquals("update did a node move notify?", 0, testModel.nodeMoved);
		assertEquals("update did a link add notify?", 0, testModel.linkAdded);
		assertEquals("update did a linkdeleted notify?", 0, testModel.linkDeleted);
		
		model.removeDiagramFactor(diagramFactorId);
		assertEquals("delete node didn't notify?", 1, testModel.nodeDeleted);
		assertEquals("delete node triggered a node add notify?", 1, testModel.nodeAdded);
		assertEquals("test model modify action called for delete?", 1, testModel.nodeChanged);
		assertEquals("node delete did a node move notify?", 0, testModel.nodeMoved);
		assertEquals("delete node did a link add notify?", 0, testModel.linkAdded);
		assertEquals("delete node did a link delete notify?", 0, testModel.linkDeleted);

		FactorCell node2 = project.createFactorCell(ObjectType.TARGET);
		FactorCell node3 = project.createFactorCell(ObjectType.TARGET);
		DiagramLink link1 = createLinkage(new FactorLinkId(BaseId.INVALID.asInt()), node2.getDiagramFactor(), node3.getDiagramFactor());
		assertEquals("didn't do more node add notify's?", 3, testModel.nodeAdded);
		assertEquals("add link did a node delete notify?", 1, testModel.nodeDeleted);
		assertEquals("add link did a node change notify?", 1, testModel.nodeChanged);
		assertEquals("link add did a node move notify?", 0, testModel.nodeMoved);
		assertEquals("test model linkageAdded action not called for addition of a link?", 1, testModel.linkAdded);
		assertEquals("add link did a delete link notify?", 0, testModel.linkDeleted);

		model.deleteDiagramFactorLink(link1);
		assertEquals("link delete did a node add notify?", 3, testModel.nodeAdded);
		assertEquals("link delete did a node delete notify?", 1, testModel.nodeDeleted);
		assertEquals("link delete did a node change notify?", 1, testModel.nodeChanged);
		assertEquals("link delete did a node move notify?", 0, testModel.nodeMoved);
		assertEquals("link delete did a link add notify?", 1, testModel.linkAdded);
		assertEquals("test model linkDeleted action not called?",1, testModel.linkDeleted);
		
		model.factorsWereMoved(new DiagramFactorId[] {node2.getDiagramFactorId(), node3.getDiagramFactorId()});
		assertEquals("move nodes did a node add notify?", 3, testModel.nodeAdded);
		assertEquals("move nodes did a node delete notify?", 1, testModel.nodeDeleted);
		assertEquals("move nodes did a node change notify?", 1, testModel.nodeChanged);
		assertEquals("node move didn't do all node move notify's?", 2, testModel.nodeMoved);
		assertEquals("move nodes did a link add notify?", 1, testModel.linkAdded);
		assertEquals("move nodes did a link delete notify?", 1, testModel.linkDeleted);
		
	}
	
	private DiagramLink createLinkage(FactorLinkId id, DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor) throws Exception
	{
		LinkCreator linkCreator = new LinkCreator(project);
		FactorLinkId factorLinkId = linkCreator.createFactorLinkAndAddToDiagramUsingCommands(project.getDiagramModel(), fromDiagramFactor, toDiagramFactor);
		DiagramLink diagramLink = project.getDiagramModel().getDiagramFactorLinkbyWrappedId(factorLinkId);
		
		return diagramLink;
	}
	
	static class TestTableModel implements DiagramModelListener
	{
		
		public void addListener(DiagramModel model)
		{
			model.addDiagramModelListener(this);
		}

		public void factorAdded(DiagramModelEvent event) 
		{
			nodeAdded++;
		}

		public void factorDeleted(DiagramModelEvent event) 
		{
			nodeDeleted++;
		}

		public void factorChanged(DiagramModelEvent event) 
		{
			nodeChanged++;
		}
		
		public void factorMoved(DiagramModelEvent event) 
		{
			nodeMoved++;
		}

		public void linkAdded(DiagramModelEvent event) 
		{
			linkAdded++;
		}

		public void linkDeleted(DiagramModelEvent event) 
		{
			linkDeleted++;
		}

		int nodeAdded = 0;
		int nodeDeleted = 0;
		int nodeChanged = 0;
		int linkAdded = 0;
		int linkDeleted = 0;
		int nodeMoved = 0;
	}

	ProjectForTesting project;
	DiagramModel model;
	IdAssigner idAssigner;
}
