/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */

package org.conservationmeasures.eam.diagram;

import java.awt.Dimension;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandSetNodeSize;
import org.conservationmeasures.eam.diagram.cells.DiagramNode;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.ModelNodeId;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.martus.util.TestCaseEnhanced;

public class TestSetNodeSize extends TestCaseEnhanced 
{
	public TestSetNodeSize(String name)
	{
		super(name);
	}

	public void testSetNodeSize() throws Exception
	{
		ProjectForTesting project = new ProjectForTesting(getName());
		DiagramModel model = project.getDiagramModel();

		ModelNodeId modelNodeId = project.createNodeAndAddToDiagram(Factor.TYPE_TARGET, BaseId.INVALID);
		DiagramNode found = model.getNodeById(modelNodeId);
		Dimension newSize = new Dimension(200,300);
		Command setNodeSize = new CommandSetNodeSize(found.getDiagramNodeId(), newSize, found.getPreviousSize());
		setNodeSize.execute(project);

		Dimension foundSize = found.getSize();
		assertEquals("wrong size?", newSize, foundSize);
		
		project.close();
	}

}
