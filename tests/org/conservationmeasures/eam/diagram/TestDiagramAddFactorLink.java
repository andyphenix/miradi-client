/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram;

import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandDiagramAddFactorLink;
import org.conservationmeasures.eam.diagram.cells.DiagramFactor;
import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.CreateFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.DiagramFactorLink;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.project.ProjectForTesting;

public class TestDiagramAddFactorLink extends EAMTestCase
{
	public TestDiagramAddFactorLink(String name)
	{
		super(name);
	}

	public void testLinkNodes() throws Exception
	{
		ProjectForTesting project = new ProjectForTesting(getName());
		DiagramModel model = project.getDiagramModel();

		FactorId interventionId = project.createNodeAndAddToDiagram(Factor.TYPE_STRATEGY, BaseId.INVALID);
		DiagramFactor intervention = model.getDiagramFactorByWrappedId(interventionId);
		FactorId factorId = project.createNodeAndAddToDiagram(Factor.TYPE_CAUSE, BaseId.INVALID);
		DiagramFactor factor = model.getDiagramFactorByWrappedId(factorId);

		CreateFactorLinkParameter extraInfo = new CreateFactorLinkParameter(interventionId, factorId);
		CommandCreateObject createModelLinkage = new CommandCreateObject(ObjectType.FACTOR_LINK, extraInfo);
		project.executeCommand(createModelLinkage);
		FactorLinkId modelLinkageId = (FactorLinkId)createModelLinkage.getCreatedId();
		CommandDiagramAddFactorLink command = new CommandDiagramAddFactorLink(modelLinkageId);
		project.executeCommand(command);

		DiagramFactorLink linkage = model.getDiagramFactorLinkById(command.getDiagramFactorLinkId());
		LinkCell cell = project.getDiagramModel().findLinkCell(linkage);

		assertEquals("not from intervention?", intervention, cell.getFrom());
		assertEquals("not to target?", factor, cell.getTo());
		
		project.close();
	}
}
