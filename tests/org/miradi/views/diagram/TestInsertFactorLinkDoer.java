/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.miradi.views.diagram;

import org.miradi.main.EAMTestCase;

public class TestInsertFactorLinkDoer extends EAMTestCase
{
	public TestInsertFactorLinkDoer(String name)
	{
		super(name);
	}
	
	public void testNothing()
	{
		
	}

	// TODO: If we are going to test doers like this, we 
	// really need to have a true main window with real views
	// and everything. None of this half-way stuff.
	// Alternative: Delete this test class.
//	public void setUp() throws Exception
//	{
//		super.setUp();
//		project = new ProjectForTesting(getName());
//		doer = new InsertFactorLinkDoer();
//		doer.setProject(project);
//	}
//	
//	public void tearDown() throws Exception
//	{
//		project.close();
//		super.tearDown();
//	}
//	
//	public void testwouldCreateLinkageLoop() throws Exception
//	{
//		FactorId node1 = project.createNodeAndAddToDiagram(ObjectType.TARGET);
//		FactorId node2 = project.createNodeAndAddToDiagram(ObjectType.TARGET);
//		FactorId node3 = project.createNodeAndAddToDiagram(ObjectType.TARGET);
//		
//		CreateFactorLinkParameter parameter12 = new CreateFactorLinkParameter(node1, node2);
//		project.createObject(ObjectType.FACTOR_LINK, BaseId.INVALID, parameter12);
//		
//		CreateFactorLinkParameter parameter23 = new CreateFactorLinkParameter(node2, node3);
//		project.createObject(ObjectType.FACTOR_LINK, BaseId.INVALID, parameter23);
//		
//		DiagramModel model = project.getDiagramModel();
//		assertTrue("Didnt catch loop?", doer.wouldCreateLinkageLoop(model, node3, node1));
//		assertFalse("Prevented legal Link?", doer.wouldCreateLinkageLoop(model, node1, node3));
//		assertTrue("Didnt catch link to itself?", doer.wouldCreateLinkageLoop(model, node1, node1));
//
//	}
//	
//	public void testIsAvailable() throws Exception
//	{
//		OurMainWindow mainWindow = new OurMainWindow(project);
//		
//		doer.setMainWindow(mainWindow);
//		try
//		{
//			assertFalse("Enabled when no nodes in the system?", doer.isAvailable());
//			project.createNodeAndAddToDiagram(ObjectType.TARGET);
//			assertFalse("Enabled when only 1 node?", doer.isAvailable());
//			project.createNodeAndAddToDiagram(ObjectType.CAUSE);
//			assertTrue("not enabled when 2 nodes?", doer.isAvailable());
//		}
//		finally
//		{
//			project.close();
//		}
//		assertFalse("enabled when no project open?", doer.isAvailable());
//		
//	}
//
//	static class OurMainWindow extends MainWindow
//	{
//		public OurMainWindow(Project projectToUse) throws IOException
//		{
//			super(projectToUse);
//			actions = new Actions(this);
//			diagramComponent = new DiagramComponent(this);
//			diagramComponent.setBounds(new Rectangle(0, 0, DIAGRAM_WIDTH, DIAGRAM_HEIGHT));
//			diagramComponent.setModel(projectToUse.getDiagramModel());
//		}
//
//		public DiagramComponent getDiagramComponent()
//		{
//			return diagramComponent;
//		}
//
//		private DiagramComponent diagramComponent;
//
//		static final int DIAGRAM_WIDTH = 1000;
//		static final int DIAGRAM_HEIGHT = 1000;
//	}
//	
//	private ProjectForTesting project;
//	private InsertFactorLinkDoer doer;
//
}
