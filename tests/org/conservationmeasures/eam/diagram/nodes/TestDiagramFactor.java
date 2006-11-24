/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram.nodes;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandDiagramMove;
import org.conservationmeasures.eam.commands.CommandSetNodeSize;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.cells.DiagramNode;
import org.conservationmeasures.eam.diagram.cells.DiagramTarget;
import org.conservationmeasures.eam.diagram.nodetypes.FactorTypeTarget;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramNodeId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.ids.ModelNodeId;
import org.conservationmeasures.eam.objecthelpers.CreateModelNodeParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Cause;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.testall.EAMTestCase;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;
import org.jgraph.graph.GraphConstants;

public class TestDiagramFactor extends EAMTestCase
{
	public TestDiagramFactor(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
		idAssigner = new IdAssigner();

		Strategy cmIntervention = new Strategy(takeNextModelNodeId());
		Cause cmIndirectFactor = new Cause(takeNextModelNodeId());
		Cause cmDirectThreat = new Cause(takeNextModelNodeId());
		cmDirectThreat.increaseTargetCount();
		CreateModelNodeParameter createTarget = new CreateModelNodeParameter(new FactorTypeTarget());
		BaseId rawTargetId = project.createObject(ObjectType.MODEL_NODE, BaseId.INVALID, createTarget);
		ModelNodeId cmTargetId = new ModelNodeId(rawTargetId.asInt());
		cmTarget = (Target)project.findNode(cmTargetId);
		
		DiagramNodeId interventionNodeId = new DiagramNodeId(44);
		intervention = DiagramNode.wrapConceptualModelObject(interventionNodeId, cmIntervention);
		DiagramNodeId indirectFactorNodeId = new DiagramNodeId(46);
		indirectFactor = DiagramNode.wrapConceptualModelObject(indirectFactorNodeId, cmIndirectFactor);
		DiagramNodeId directThreatNodeId = new DiagramNodeId(43);
		directThreat = DiagramNode.wrapConceptualModelObject(directThreatNodeId, cmDirectThreat);
		DiagramNodeId targetNodeId = new DiagramNodeId(35);
		target = DiagramNode.wrapConceptualModelObject(targetNodeId, cmTarget);
		targetAttributeMap = target.getAttributes();
	}
	
	public void tearDown() throws Exception
	{
		project.close();
		super.tearDown();
	}

	public void testPort()
	{
		assertEquals("port not first child?", target.getPort(), target.getFirstChild());
	}
	
	public void testObjectives()
	{
		assertTrue(directThreat.canHaveObjectives());
		assertTrue(indirectFactor.canHaveObjectives());
		assertTrue(intervention.canHaveObjectives());
		assertFalse(target.canHaveObjectives());
	}

	public void testIndicator()
	{
		IdList indicators = directThreat.getIndicators();
		assertEquals(0, indicators.size());
	}
	
	public void testGoals()
	{
		assertTrue(target.canHaveGoal());
		assertFalse(directThreat.canHaveGoal());
		assertFalse(indirectFactor.canHaveGoal());
		assertFalse(intervention.canHaveGoal());
	}
	
	
	public void testBounds()
	{
		target.setLocation(new Point(123, 456));
		Rectangle2D bounds = GraphConstants.getBounds(targetAttributeMap);
		assertEquals("wrong x?", 123.0, bounds.getX(), TOLERANCE);
		assertEquals("wrong y?", 456.0, bounds.getY(), TOLERANCE);
		assertEquals("wrong width", 120.0, bounds.getWidth(), TOLERANCE);
		assertEquals("wrong height", 60.0, bounds.getHeight(), TOLERANCE);
	}
	
	public void testSize()
	{
		target.setLocation(new Point(3, 4));
		target.setSize(new Dimension(300, 200));
		Rectangle2D bounds = GraphConstants.getBounds(targetAttributeMap);
		assertEquals("wrong x?", 3.0, bounds.getX(), TOLERANCE);
		assertEquals("wrong y?", 4.0, bounds.getY(), TOLERANCE);
		assertEquals("wrong width", 300.0, bounds.getWidth(), TOLERANCE);
		assertEquals("wrong height", 200.0, bounds.getHeight(), TOLERANCE);
		target.setSize(new Dimension(100, 50));
		bounds = GraphConstants.getBounds(targetAttributeMap);
		assertEquals("x changed?", 3.0, bounds.getX(), TOLERANCE);
		assertEquals("y changed?", 4.0, bounds.getY(), TOLERANCE);
		assertEquals("wrong new width", 100.0, bounds.getWidth(), TOLERANCE);
		assertEquals("wrong new height", 50.0, bounds.getHeight(), TOLERANCE);
		assertEquals("node size width incorrect?", 100.0, target.getSize().getWidth(), TOLERANCE);
		assertEquals("node size height incorrect?", 50.0, target.getSize().getHeight(), TOLERANCE);
	}

	public void testFont()
	{
		Font nodeFont = GraphConstants.getFont(targetAttributeMap);
		assertTrue("not bold?", nodeFont.isBold());
	}
	
	public void testBuildCommandsToClear() throws Exception
	{
		Command[] commands = target.buildCommandsToClear();
		assertEquals(3, commands.length);
		int next = 0;
		assertEquals(CommandSetNodeSize.COMMAND_NAME, commands[next++].getCommandName());
		assertEquals(CommandDiagramMove.COMMAND_NAME, commands[next++].getCommandName());
		assertEquals(CommandSetObjectData.COMMAND_NAME, commands[next++].getCommandName());
	}
	
	public void testJson() throws Exception
	{
		target.setLocation(new Point(100, 200));
		target.setSize(new Dimension(50, 75));
		
		DiagramNode got = new DiagramTarget(target.getDiagramNodeId(), cmTarget);
		EnhancedJsonObject json = target.toJson();
		got.fillFrom(json);
		
		assertEquals("location", target.getLocation(), got.getLocation());
		assertEquals("size", target.getSize(), got.getSize());
		assertEquals("id", target.getDiagramNodeId(), got.getDiagramNodeId());
		assertEquals("wrapped id", target.getWrappedId(), got.getWrappedId());
	}

	private ModelNodeId takeNextModelNodeId()
	{
		return new ModelNodeId(idAssigner.takeNextId().asInt());
	}
	

	static final double TOLERANCE = 0.00;
	
	Project project;
	IdAssigner idAssigner;
	Target cmTarget;
	DiagramNode intervention;
	DiagramNode indirectFactor;
	DiagramNode directThreat;
	DiagramNode target;
	Map targetAttributeMap;
}
