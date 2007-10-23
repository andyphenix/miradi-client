/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.project;

import java.awt.Dimension;
import java.awt.Point;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class FactorCommandHelper
{
	public FactorCommandHelper(Project projectToUse, DiagramModel modelToUse)
	{
		project = projectToUse;
		currentModel = modelToUse;
	}
	
	public CommandCreateObject createFactorAndDiagramFactor(int objectType, Point insertionLocation, Dimension size, String label) throws Exception
	{
		CommandCreateObject createObjectCommand = createFactorAndDiagramFactor(objectType);
		DiagramFactorId diagramFactorId = (DiagramFactorId) createObjectCommand.getCreatedId();
		DiagramFactor diagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, diagramFactorId));
		setLocationSizeLabel(diagramFactor, insertionLocation, size, label);
		
		return createObjectCommand;
	}

	public CommandCreateObject createFactorAndDiagramFactor(int objectType) throws Exception
	{
		FactorId factorId = createFactor(objectType);
		return createDiagramFactor(currentModel.getDiagramObject(), objectType, factorId);
	}
	
	public CommandCreateObject createDiagramFactor(DiagramObject diagramObject, int objectType, FactorId factorId) throws Exception
	{
		CreateDiagramFactorParameter extraDiagramFactorInfo = new CreateDiagramFactorParameter(new ORef(objectType, factorId));
		CommandCreateObject createDiagramFactor = new CommandCreateObject(ObjectType.DIAGRAM_FACTOR, extraDiagramFactorInfo);
		executeCommand(createDiagramFactor);
		
		DiagramFactorId diagramFactorId = (DiagramFactorId) createDiagramFactor.getCreatedId();
		CommandSetObjectData addDiagramFactor = CommandSetObjectData.createAppendIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_IDS, diagramFactorId);
		executeCommand(addDiagramFactor);
		
		Factor factor = project.findNode(factorId);
		Command[] commandsToAddToView = getProject().getCurrentViewData().buildCommandsToAddNode(factor.getRef());
		for(int i = 0; i < commandsToAddToView.length; ++i)
			executeCommand(commandsToAddToView[i]);
		
		return createDiagramFactor;
	}

	private FactorId createFactor(int objectType) throws CommandFailedException
	{
		CommandCreateObject createFactorCommand = new CommandCreateObject(objectType);
		executeCommand(createFactorCommand);
		
		return (FactorId) createFactorCommand.getCreatedId();
	}

	private void setLocationSizeLabel(DiagramFactor diagramFactor, Point insertionLocation, Dimension size, String label) throws CommandFailedException, Exception
	{
		setDiagramFactorSize(diagramFactor.getDiagramFactorId(), size);
		setDiagramFactorLocation(diagramFactor.getDiagramFactorId(), insertionLocation);
		setDiagramFactorLabel(diagramFactor.getWrappedId(), label);
	}
	
	private void setDiagramFactorSize(DiagramFactorId diagramFactorId, Dimension originalSize) throws CommandFailedException
	{
		String currentSize = EnhancedJsonObject.convertFromDimension(originalSize);
		CommandSetObjectData setSizeCommand = new CommandSetObjectData(ObjectType.DIAGRAM_FACTOR, diagramFactorId, DiagramFactor.TAG_SIZE, currentSize);
		executeCommand(setSizeCommand);
	}

	private void setDiagramFactorLabel(FactorId factorId, String label) throws CommandFailedException
	{
		CommandSetObjectData setLabel = new CommandSetObjectData(ObjectType.FACTOR, factorId, Factor.TAG_LABEL, label); 
		executeCommand(setLabel);
	}
	
	private void setDiagramFactorLocation(DiagramFactorId diagramFactorId, Point newNodeLocation) throws Exception
	{
		String newMoveLocation = EnhancedJsonObject.convertFromPoint(new Point(newNodeLocation.x, newNodeLocation.y));
		CommandSetObjectData moveCommand = new CommandSetObjectData(ObjectType.DIAGRAM_FACTOR, diagramFactorId, DiagramFactor.TAG_LOCATION, newMoveLocation);
		executeCommand(moveCommand);
	}

	public static CommandSetObjectData createSetLabelCommand(ORef ref, String newLabel)
	{
		return new CommandSetObjectData(ref, Factor.TAG_LABEL, newLabel);
	}

	private Project getProject()
	{
		return project;
	}
	
	private void executeCommand(Command cmd) throws CommandFailedException
	{
		getProject().executeCommand(cmd);
	}
			
	Project project;
	DiagramModel currentModel;
}
