/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/
package org.miradi.diagram;

import java.awt.geom.Rectangle2D;

import org.jgraph.graph.GraphConstants;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.main.MiradiTestCase;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.project.ProjectForTesting;

public class TestDiagramAddFactor extends MiradiTestCase
{
	public TestDiagramAddFactor(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		project = new ProjectForTesting(getName());
	}
	
	public void tearDown() throws Exception
	{
		project.close();
		project = null;
	}
	
	public void testInsertTarget() throws Exception
	{
		project.createNodeAndAddToDiagram(ObjectType.TARGET);
		DiagramModel model = project.getTestingDiagramModel();
		FactorCell insertedNode = (FactorCell)model.getAllFactorCells().get(0);
		Rectangle2D bounds = GraphConstants.getBounds(insertedNode.getAttributes());
		assertEquals("wrong x?", 0, (int)bounds.getX());
		assertEquals("wrong y?", 0, (int)bounds.getY());
		assertContains("wrong text?", "", insertedNode.getLabel());
		ORef diagramFactorRef = insertedNode.getDiagramFactorRef();
		FactorCell foundNode = model.getFactorCellByRef(diagramFactorRef);
		assertEquals("can't find node?", insertedNode, foundNode);
		assertTrue("not a target?", foundNode.isTarget());
		assertTrue(Target.is(foundNode.getWrappedFactorRef()));
	}

	public void testInsertFactor() throws Exception
	{
		project.createNodeAndAddToDiagram(ObjectType.CAUSE);
		DiagramModel model = project.getTestingDiagramModel();
		FactorCell insertedNode = (FactorCell)model.getAllFactorCells().get(0);
		Rectangle2D bounds = GraphConstants.getBounds(insertedNode.getAttributes());
		assertEquals("wrong x?", 0, (int)bounds.getX());
		assertEquals("wrong y?", 0, (int)bounds.getY());
		assertContains("wrong text?", "", insertedNode.getLabel());
		ORef diagramFactorRef = insertedNode.getDiagramFactorRef();
		FactorCell foundNode = model.getFactorCellByRef(diagramFactorRef);
		assertEquals("can't find node?", insertedNode, foundNode);
		assertTrue("not a contributing factor?", foundNode.isContributingFactor());
		assertEquals(ObjectType.CAUSE, foundNode.getWrappedType());
	}

	public void testInsertIntervention() throws Exception
	{
		project.createNodeAndAddToDiagram(ObjectType.STRATEGY);
		DiagramModel model = project.getTestingDiagramModel();
		FactorCell insertedNode = (FactorCell)model.getAllFactorCells().get(0);
		Rectangle2D bounds = GraphConstants.getBounds(insertedNode.getAttributes());
		assertEquals("wrong x?", 0, (int)bounds.getX());
		assertEquals("wrong y?", 0, (int)bounds.getY());
		assertContains("wrong text?", "", insertedNode.getLabel());
		ORef diagramFactorRef = insertedNode.getDiagramFactorRef();
		FactorCell foundNode = model.getFactorCellByRef(diagramFactorRef);
		assertEquals("can't find node?", insertedNode, foundNode);
		assertTrue("not a strategy?", foundNode.isStrategy());
		assertTrue(Strategy.is(foundNode.getWrappedFactorRef()));
	}
	
	ProjectForTesting project;
}
