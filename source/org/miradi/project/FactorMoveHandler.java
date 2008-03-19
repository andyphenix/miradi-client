/* 
 * Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
 * (on behalf of the Conservation Measures Partnership, "CMP") and 
 * Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
 */ 
package org.miradi.project;

import java.awt.Point;
import java.util.HashSet;
import java.util.Vector;

import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.DiagramComponent;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.diagram.cells.LinkCell;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.DiagramFactorId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.GroupBox;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.PointList;

public class FactorMoveHandler
{
	public FactorMoveHandler(Project projectToUse, DiagramModel modelToUse)
	{
		project = projectToUse;
		model = modelToUse;
	}

	public void factorsWereMovedOrResized(DiagramFactorId[] ids) throws CommandFailedException
	{
		try 
		{
			model.factorsWereMoved(ids);
			
			ORefList diagramFactorRefs = new ORefList(DiagramFactor.getObjectType(), new IdList(DiagramFactor.getObjectType(), ids));
			Vector commandsToExecute = new Vector();
			for(int i = 0 ; i < diagramFactorRefs.size(); ++i)
			{
				FactorCell factorCell = model.getFactorCellById((DiagramFactorId) diagramFactorRefs.get(i).getObjectId());
				if(factorCell.hasMoved())
				{
					commandsToExecute.add(buildMoveCommand(factorCell));
					commandsToExecute.addAll(buildGroupBoxRelatedMoveCommands(diagramFactorRefs, factorCell));
				}
				
				if(factorCell.sizeHasChanged())
				{
					commandsToExecute.add(buildResizeCommand(factorCell));
				}
			}

			if(commandsToExecute.size() > 0)
			{
				for(int i=0; i < commandsToExecute.size(); ++i)
				{
					getProject().executeCommand((Command)commandsToExecute.get(i));
				}
			}
			
			//TODO remove cluster related code below
			/*
			 * NOTE: The following chunk of code works around a weird bug deep in jgraph
			 * If you click on a cluster, then click on a member, then drag the member out,
			 * part of jgraph still thinks the cluster has a member that is selected.
			 * So when you drag the node back in, it doesn't become a member because jgraph 
			 * won't return the cluster, because it thinks the cluster has something selected.
			 * The workaround is to re-select what is selected, so the cached values inside 
			 * jgraph get reset to their proper values.
			 */
			MainWindow mainWindow = EAM.getMainWindow();
			if(mainWindow != null)
			{
				DiagramComponent diagram = mainWindow.getDiagramComponent();
				diagram.setSelectionCells(diagram.getSelectionCells());
			}

		} 
		catch (Exception e) 
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}

	}

	public void ensureLevelSegementToFirstBendPoint(DiagramFactorId[] ids) throws Exception
	{
		for(int i = 0 ; i < ids.length; ++i)
		{
			FactorCell factorCell = model.getFactorCellById(ids[i]);
			if (areBothFactorsLinked(ids, factorCell))
				continue;

			if(factorCell.hasMoved() || factorCell.sizeHasChanged())
			{
				ensureLevelSegementToFirstBendPoint(factorCell);
			}
		}
	}

	private void ensureLevelSegementToFirstBendPoint(FactorCell factorCell) throws Exception
	{
		HashSet<LinkCell> factorRelatedLinks = model.getFactorRelatedLinks(factorCell);
		for(LinkCell linkCell : factorRelatedLinks)
		{
			PointList bendPoints = new PointList(linkCell.getDiagramLink().getBendPoints());
			if (bendPoints.size() < 1)
				continue;
			
			if (wasHorizontal(factorCell, linkCell, bendPoints) && wasVertical(factorCell, linkCell, bendPoints))
				continue;

			if (wasHorizontal(factorCell, linkCell, bendPoints))
				moveFirstBendHorizontally(factorCell, linkCell, bendPoints);
			
			if (wasVertical(factorCell, linkCell, bendPoints))
				moveFirstBendPointVertically(factorCell, linkCell, bendPoints);
		}
	}

	private void moveFirstBendHorizontally(FactorCell factorCell, LinkCell cell, PointList bendPoints) throws Exception
	{
		int deltaX = 0;
		int deltaY = factorCell.getPortLocation(model.getGraphLayoutCache()).y - factorCell.getPreviousPortLocation().y;
		moveAndSaveClosestBendPoint(factorCell, cell, deltaX, deltaY, bendPoints);
	}

	private void moveFirstBendPointVertically(FactorCell factorCell, LinkCell cell, PointList bendPoints) throws Exception
	{
		int deltaX = factorCell.getPortLocation(model.getGraphLayoutCache()).x - factorCell.getPreviousPortLocation().x;
		int deltaY = 0;
		moveAndSaveClosestBendPoint(factorCell, cell, deltaX, deltaY, bendPoints);
	}
	
	private void moveAndSaveClosestBendPoint(FactorCell factorCell, LinkCell linkCell, int deltaX, int deltaY, PointList bendPoints) throws Exception
	{
		moveFirstBendPointInPlace(factorCell, linkCell, bendPoints, deltaX, deltaY);
		CommandSetObjectData bendPointMoveCommand =	CommandSetObjectData.createNewPointList(linkCell.getDiagramLink(), DiagramLink.TAG_BEND_POINTS, bendPoints);
		project.executeCommand(bendPointMoveCommand);
	}

	private void moveFirstBendPointInPlace(FactorCell factorCell, LinkCell linkCell, PointList bendPoints, int deltaX, int deltaY)
	{
		Point bendPointToTranslate = getBendPointToTranslate(factorCell, linkCell, bendPoints);
		bendPointToTranslate.translate(deltaX, deltaY);
		bendPointToTranslate.setLocation(getProject().getSnapped(bendPointToTranslate));
	}
	
	private boolean wasHorizontal(FactorCell factorCell, LinkCell linkCell, PointList bendPoints)
	{	
		int deltaY = factorCell.getPreviousPortLocation().y - factorCell.getPortLocation(model.getGraphLayoutCache()).y;
		Point portLocation = getPortLocation(factorCell, linkCell);
		int originalLocation = portLocation.y + deltaY;
		
		return getBendPointToTranslate(factorCell, linkCell, bendPoints).y == originalLocation;
	}

	private boolean wasVertical(FactorCell factorCell, LinkCell linkCell, PointList bendPoints)
	{
		int deltaX = factorCell.getPreviousPortLocation().x - factorCell.getPortLocation(model.getGraphLayoutCache()).x;
		Point portLocation = getPortLocation(factorCell, linkCell);
		int originalLocation = portLocation.x + deltaX;
		
		return getBendPointToTranslate(factorCell, linkCell, bendPoints).x == originalLocation;
	}

	private Point getPortLocation(FactorCell factorCell, LinkCell linkCell)
	{ 
		if (isFromThisFactorCell(factorCell, linkCell))
			return linkCell.getSourceLocation(model.getGraphLayoutCache());
		
		return linkCell.getTargetLocation(model.getGraphLayoutCache());
	}

	private boolean isFromThisFactorCell(FactorCell factorCell, LinkCell linkCell)
	{
		return linkCell.getFrom().getDiagramFactor().getRef().equals(factorCell.getDiagramFactor().getRef());
	}
	
	private Point getBendPointToTranslate(FactorCell factorCell, LinkCell linkCell, PointList bendPoints)
	{
		Point bendPointToTranslate = bendPoints.get(bendPoints.size() - 1);
		if (isFromThisFactorCell(factorCell, linkCell))
			bendPointToTranslate = bendPoints.get(0);

		return bendPointToTranslate;
	}
	
	private boolean areBothFactorsLinked(DiagramFactorId[] ids, FactorCell factorCell) throws Exception
	{
		for(int i = 0 ; i < ids.length; ++i)
		{
			if (model.areLinked(ids[i], factorCell.getDiagramFactorId()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private Vector<Command> buildGroupBoxRelatedMoveCommands(ORefList diagramFactorRefs, FactorCell factorCell)
	{
		int deltaX = factorCell.getLocation().x - factorCell.getDiagramFactor().getLocation().x;
		int deltaY = factorCell.getLocation().y - factorCell.getDiagramFactor().getLocation().y;
		Vector<Command> commandsToMove = new Vector();
		if (factorCell.getWrappedType() != GroupBox.getObjectType())
			return new Vector();
		
		ORefList groupChildRefs = factorCell.getDiagramFactor().getGroupBoxChildrenRefs();
		for (int i = 0; i < groupChildRefs.size(); ++i)
		{
			ORef groupChildRef = groupChildRefs.get(i);
			if (diagramFactorRefs.contains(groupChildRef))
				continue;
			
			DiagramFactor groupChild = DiagramFactor.find(getProject(), groupChildRef);
			Point currentLocation = (Point) groupChild.getLocation().clone();
			currentLocation.translate(deltaX, deltaY);
			Point snappedLocation = getProject().getSnapped(currentLocation);
			String snappedLocationAsJson = EnhancedJsonObject.convertFromPoint(snappedLocation);
			//FIXME DO not use new DiagramFactorId(ref.asInt);
			commandsToMove.add(new CommandSetObjectData(DiagramFactor.getObjectType(), new DiagramFactorId(groupChildRef.getObjectId().asInt()), DiagramFactor.TAG_LOCATION, snappedLocationAsJson));
		}
				
		return commandsToMove;
	}

	private Object buildMoveCommand(FactorCell node)
	{
		Point location = node.getLocation();
		Point snappedLocation = getProject().getSnapped(location);
		String currentLocation = EnhancedJsonObject.convertFromPoint(snappedLocation);
		return new CommandSetObjectData(ObjectType.DIAGRAM_FACTOR, node.getDiagramFactorId(), DiagramFactor.TAG_LOCATION, currentLocation);
	}

	private Command buildResizeCommand(FactorCell node)
	{
		String currentSize = EnhancedJsonObject.convertFromDimension(node.getSize());
		return new CommandSetObjectData(ObjectType.DIAGRAM_FACTOR, node.getDiagramFactorId(), DiagramFactor.TAG_SIZE, currentSize);
	}

	private Project getProject()
	{
		return project;
	}

	private Project project;
	private DiagramModel model;
}
