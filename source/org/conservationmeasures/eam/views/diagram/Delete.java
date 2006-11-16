/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.diagram;

import java.text.ParseException;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.commands.CommandDiagramRemoveLinkage;
import org.conservationmeasures.eam.commands.CommandDiagramRemoveNode;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.EAMGraphCell;
import org.conservationmeasures.eam.diagram.nodes.DiagramCluster;
import org.conservationmeasures.eam.diagram.nodes.DiagramLinkage;
import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.DiagramLinkageId;
import org.conservationmeasures.eam.ids.DiagramNodeId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.ConceptualModelCluster;
import org.conservationmeasures.eam.objects.ConceptualModelNode;
import org.conservationmeasures.eam.objects.EAMBaseObject;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.ProjectDoer;

public class Delete extends ProjectDoer
{
	public Delete()
	{
		super();
	}
	
	public Delete(Project project)
	{
		setProject(project);
	}
	
	public boolean isAvailable()
	{
		if(!getProject().isOpen())
			return false;

		EAMGraphCell[] selected = getProject().getSelectedAndRelatedCells();
		return (selected.length > 0);
	}

	public void doIt() throws CommandFailedException
	{
		EAMGraphCell[] selectedRelatedCells = getProject().getSelectedAndRelatedCells();
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			for(int i=0; i < selectedRelatedCells.length; ++i)
			{
				EAMGraphCell cell = selectedRelatedCells[i];
				if(cell.isLinkage())
					deleteLinkage((DiagramLinkage)cell);	
			}
			
			for(int i=0; i < selectedRelatedCells.length; ++i)
			{
				EAMGraphCell cell = selectedRelatedCells[i];
				if(cell.isNode())
					deleteNode((DiagramNode)cell);
			}
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}

	private void deleteLinkage(DiagramLinkage linkageToDelete) throws CommandFailedException
	{
		DiagramLinkageId id = linkageToDelete.getDiagramLinkageId();
		CommandDiagramRemoveLinkage removeCommand = new CommandDiagramRemoveLinkage(id);
		getProject().executeCommand(removeCommand);
		CommandDeleteObject deleteLinkage = new CommandDeleteObject(ObjectType.MODEL_LINKAGE, linkageToDelete.getWrappedId());
		getProject().executeCommand(deleteLinkage);
	}

	// TODO: This method should be inside Project and should have unit tests
	private void deleteNode(DiagramNode nodeToDelete) throws Exception
	{
		DiagramNodeId id = nodeToDelete.getDiagramNodeId();

		removeFromView(id);
		removeFromCluster(nodeToDelete, id);
		removeNodeFromDiagram(nodeToDelete, id);

		ConceptualModelNode underlyingNode = nodeToDelete.getUnderlyingObject();
		deleteAnnotations(underlyingNode);
		deleteUnderlyingNode(underlyingNode);
	}

	private void removeFromCluster(DiagramNode nodeToDelete, DiagramNodeId id) throws ParseException, CommandFailedException
	{
		DiagramCluster cluster = (DiagramCluster)nodeToDelete.getParent();
		if(cluster != null)
		{
			CommandSetObjectData removeFromCluster = CommandSetObjectData.createRemoveIdCommand(
					cluster.getUnderlyingObject(),
					ConceptualModelCluster.TAG_MEMBER_IDS, 
					id);
			getProject().executeCommand(removeFromCluster);
		}
	}

	private void removeFromView(DiagramNodeId id) throws ParseException, Exception, CommandFailedException
	{
		Command[] commandsToRemoveFromView = getProject().getCurrentViewData().buildCommandsToRemoveNode(id);
		for(int i = 0; i < commandsToRemoveFromView.length; ++i)
			getProject().executeCommand(commandsToRemoveFromView[i]);
	}

	private void removeNodeFromDiagram(DiagramNode nodeToDelete, DiagramNodeId id) throws CommandFailedException
	{
		Command[] commandsToClear = nodeToDelete.buildCommandsToClear();
		getProject().executeCommands(commandsToClear);
		
		getProject().executeCommand(new CommandDiagramRemoveNode(id));
	}

	private void deleteUnderlyingNode(ConceptualModelNode nodeToDelete) throws CommandFailedException
	{
		Command[] commandsToClear = nodeToDelete.createCommandsToClear();
		getProject().executeCommands(commandsToClear);
		
		getProject().executeCommand(new CommandDeleteObject(nodeToDelete.getType(), nodeToDelete.getModelNodeId()));
	}
	
	private void deleteAnnotations(ConceptualModelNode nodeToDelete) throws Exception
	{
		deleteAnnotations(nodeToDelete, ObjectType.GOAL, nodeToDelete.TAG_GOAL_IDS);
		deleteAnnotations(nodeToDelete, ObjectType.OBJECTIVE, nodeToDelete.TAG_OBJECTIVE_IDS);
		deleteAnnotations(nodeToDelete, ObjectType.INDICATOR, nodeToDelete.TAG_INDICATOR_IDS);
	}

	private void deleteAnnotations(ConceptualModelNode nodeToDelete, int annotationType, String annotationListTag) throws Exception
	{
		IdList ids = new IdList(nodeToDelete.getData(annotationListTag));
		for(int annotationIndex = 0; annotationIndex < ids.size(); ++annotationIndex)
		{
			EAMBaseObject thisAnnotation = (EAMBaseObject)getProject().findObject(annotationType, ids.get(annotationIndex));
			Command[] commands = DeleteAnnotationDoer.buildCommandsToDeleteAnnotation(getProject(), nodeToDelete, annotationListTag, thisAnnotation);
			
			for(int commandIndex = 0; commandIndex < commands.length; ++commandIndex)
				getProject().executeCommand(commands[commandIndex]);
		}
		
	}
}
