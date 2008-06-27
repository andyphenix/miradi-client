/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.views.diagram;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashSet;

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.diagram.DiagramComponent;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.diagram.cells.LinkCell;
import org.miradi.dialogs.diagram.DiagramPanel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.DiagramFactorId;
import org.miradi.main.EAM;
import org.miradi.project.FactorMoveHandler;
import org.miradi.utils.PointList;

public class NudgeDoer extends LocationDoer
{

	public NudgeDoer(int directionToNudge)
	{
		direction = directionToNudge;
	}
	
	public boolean isAvailable()
	{
		if (!isInDiagram())
			return false;
		
		return isAnythingSelectedInDiagram();
	}

	private boolean isAnythingSelectedInDiagram()
	{
		DiagramPanel diagramPanel = getDiagramView().getDiagramPanel();
		return diagramPanel.getOnlySelectedFactorCells().length > 0 || diagramPanel.getOnlySelectedLinkCells().size() > 0;
	}

	public void doIt() throws CommandFailedException
	{
		if (! isAvailable())
			return;

		try
		{
			int deltaX = 0;
			int deltaY = 0;
			switch(direction)
			{
				case KeyEvent.VK_UP:
					deltaY = -getProject().getGridSize();
					break;
				case KeyEvent.VK_DOWN:
					deltaY = getProject().getGridSize();
					break;
				case KeyEvent.VK_LEFT:
					deltaX = -getProject().getGridSize();
					break;
				case KeyEvent.VK_RIGHT:
					deltaX = getProject().getGridSize();
					break;
			}
			EAM.logVerbose("NudgeNodes ("+deltaX + ","+deltaY+")");
			moveSelectedItems(deltaX, deltaY);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}

	private boolean isFutureCellLocationInsideDiagramBounds(Point currentLocation, int deltaX, int deltaY)
	{
		int futureX = currentLocation.x + deltaX;
		int futureY = currentLocation.y + deltaY;
		
		if (futureX < 0 || futureY < 0)
			return false;
	
		return true;
	}
	
	private void moveSelectedItems(int deltaX, int deltaY) throws Exception
	{
		DiagramPanel diagramPanel = getDiagramView().getDiagramPanel();
		FactorCell[] factorCells = diagramPanel.getOnlySelectedFactorCells();
		HashSet<FactorCell> selectedFactorAndChildren = diagramPanel.getOnlySelectedFactorAndGroupChildCells();
		
		HashSet<LinkCell> allLinkCells = new HashSet();
		DiagramComponent diagramComponent = diagramPanel.getDiagramComponent();
		diagramComponent.selectAllLinksAndThierBendPointsInsideGroupBox(selectedFactorAndChildren);
		allLinkCells.addAll(diagramPanel.getOnlySelectedLinkCells());
		
		DiagramFactorId[] ids = new DiagramFactorId[factorCells.length];
		for(int i = 0; i < factorCells.length; ++i)
		{
			ids[i] = factorCells[i].getDiagramFactorId();
			if (!isFutureCellLocationInsideDiagramBounds(factorCells[i].getLocation(), deltaX, deltaY))
				return;	
			
			factorCells[i].setPreviousLocation(factorCells[i].getLocation());
			factorCells[i].setPreviousPortLocation(factorCells[i].getPortLocation(diagramComponent.getGraphLayoutCache()));
		}
		
		if (wouldMoveBendPointsOutOfBounds(allLinkCells.toArray(new LinkCell[0]), deltaX, deltaY))
			return;
		
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			diagramPanel.moveFactors(deltaX, deltaY, ids);
			FactorMoveHandler factorMoveHandler = new FactorMoveHandler(getProject(), getDiagramView().getDiagramModel());
			factorMoveHandler.factorsWereMovedOrResized(ids);
			moveBendPoints(allLinkCells.toArray(new LinkCell[0]), deltaY, deltaX);
			factorMoveHandler.ensureLevelSegementToFirstBendPoint(ids);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException("Unable to move selected items");
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}

	private void moveBendPoints(LinkCell[] links, int deltaY, int deltaX) throws Exception
	{
		LinkBendPointsMoveHandler bendPointsMoveHandler = new LinkBendPointsMoveHandler(getProject());
		bendPointsMoveHandler.moveLinkBendPoints(links, deltaX, deltaY);
	}

	private boolean wouldMoveBendPointsOutOfBounds(LinkCell[] links, int deltaX, int deltaY)
	{
		for (int i = 0; i < links.length; ++i)
		{
			LinkCell linkCell = links[i];
			if (wouldMoveBendPointsOutOfBounds(linkCell, deltaX, deltaY))
				return true;
		}
		return false;
	}

	private boolean wouldMoveBendPointsOutOfBounds(LinkCell linkCell, int deltaX, int deltaY)
	{
		PointList bendPoints = linkCell.getDiagramLink().getBendPoints();
		int[] selectedIndexes = linkCell.getSelectedBendPointIndexes();
		for (int i = 0; i < selectedIndexes.length; ++i)
		{
			int selectionIndex = selectedIndexes[i];
			Point selectedBendPoint = bendPoints.get(selectionIndex);
			if (!isFutureCellLocationInsideDiagramBounds(selectedBendPoint, deltaX, deltaY))
				return true;
		}
		return false;
	}

	private int direction;
}
