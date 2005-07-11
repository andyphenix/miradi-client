/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandInsertIntervention;
import org.conservationmeasures.eam.commands.CommandInsertThreat;
import org.conservationmeasures.eam.commands.CommandLinkNodes;
import org.conservationmeasures.eam.diagram.nodes.Linkage;
import org.conservationmeasures.eam.diagram.nodes.Node;
import org.conservationmeasures.eam.diagram.nodes.NodeTypeGoal;
import org.conservationmeasures.eam.diagram.nodes.NodeTypeThreat;
import org.conservationmeasures.eam.main.Project;
import org.conservationmeasures.eam.testall.EAMTestCase;

public class TestLinkage extends EAMTestCase
{
	public TestLinkage(String name)
	{
		super(name);
	}

	public void testBasics()
	{
		Node threat = new Node(new NodeTypeThreat());
		Node goal = new Node(new NodeTypeGoal());
		Linkage linkage = new Linkage(threat, goal);
		assertEquals("didn't remember from?", threat, linkage.getFromNode());
		assertEquals("didn't remember to?", goal, linkage.getToNode());

		assertEquals("source not the port of from?", threat.getPort(), linkage.getSource());
		assertEquals("target not the port of to?", goal.getPort(), linkage.getTarget());
	}
	
	public void testLinkNodes()
	{
		Project project = new Project();
		DiagramModel model = project.getDiagramModel();
		
		Command insertIntervention = new CommandInsertIntervention();
		Command insertThreat = new CommandInsertThreat();
		Node intervention = (Node)insertIntervention.execute(project);
		Node threat = (Node)insertThreat.execute(project);
		int interventionId = model.getNodeId(intervention);
		int threatId = model.getNodeId(threat);
		CommandLinkNodes link = new CommandLinkNodes(interventionId, threatId);
		Linkage linkage = (Linkage)link.execute(project);
		assertTrue("linkage not in model?", model.getLinkageId(linkage) >= 0);
		
		
	}
}
