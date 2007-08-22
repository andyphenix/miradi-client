/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.diagram;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.objects.DiagramLink;
import org.conservationmeasures.eam.utils.IntVector;
import org.conservationmeasures.eam.utils.PointList;

public class BendPointSelectionHelper
{
	public BendPointSelectionHelper(LinkCell linkCellToUse)
	{
		linkCell = linkCellToUse;
		clearSelection();
	}
	
	public void clearSelection()
	{
		selectionIndexes = new IntVector();
	}
	
	public void mouseClicked(MouseEvent event, int controlPointIndex)
	{	
		int bendPointIndex = getBendPointIndexForControlPointIndex(controlPointIndex);
		
		if (bendPointIndex < 0)
			return;
		
		if (event.isControlDown())
			return;
		
		if (event.isShiftDown())
			return;
		
		if (! selectionIndexes.contains(bendPointIndex))
			return;

		clearSelection();
		selectionIndexes.add(bendPointIndex);
	}

	public void mouseWasPressed(MouseEvent mouseEvent, int controlPointIndex)
	{	
		int bendPointIndex = getBendPointIndexForControlPointIndex(controlPointIndex);
		if (bendPointIndex < 0)
			return;
		
		updateSelectionList(mouseEvent, bendPointIndex);
	}
	
	private int getBendPointIndexForControlPointIndex(int currentIndex)
	{
		return currentIndex - 1;
	}
	
	public void updateSelectionList(MouseEvent mouseEvent, int bendPointIndex)
	{	
		boolean contains = selectionIndexes.contains(bendPointIndex);
		
		if (contains)
			updateSelectionWasAlreadySelected(bendPointIndex, mouseEvent);
		else
			updateSelectionWasNotAlreadySelected(bendPointIndex, mouseEvent);
	}
	
	private void updateSelectionWasNotAlreadySelected(int bendPointIndex, MouseEvent event)
	{
		if (!event.isControlDown() && !event.isShiftDown())
			clearSelection();
		
		selectionIndexes.add(bendPointIndex);
	}

	private void updateSelectionWasAlreadySelected(int bendPointIndex, MouseEvent event)
	{
		if (! event.isControlDown())
			return;
		
		removeSelectionIndex(bendPointIndex);
	}

	public void removeSelectionIndex(int bendPointIndex)
	{
		selectionIndexes.remove(bendPointIndex);
	}

	public void addToSelectionIndexList(int bendPointIndex)
	{
		if (selectionIndexes.contains(bendPointIndex))
			return;
		
		selectionIndexes.add(bendPointIndex);
	}
	
	public void addToSelection(Point pointToSelect)
	{
		PointList bendPoints = linkCell.getDiagramLink().getBendPoints();
		for (int i = 0; i < bendPoints.size(); ++i)
		{
			Point point = bendPoints.get(i);
			if (point.equals(pointToSelect))
				addToSelectionIndexList(i);
		}
	}
	
	//TODO move this method to IntVector and name it toIntArray
	public int[] getSelectedIndexes()
	{
		int[] selection = new int[selectionIndexes.size()];
		for (int i = 0; i < selectionIndexes.size(); ++i)
		{
			selection[i] = selectionIndexes.get(i);
		}
	
		return selection;
	}
	
	public void selectAll()
	{
		clearSelection();
		DiagramLink diagramLink = linkCell.getDiagramLink();
		PointList allBendPoints = diagramLink.getBendPoints();
		for (int i = 0; i < allBendPoints.size(); ++i)
		{
			selectionIndexes.add(i);
		}
	}
	
	IntVector selectionIndexes;
	LinkCell linkCell;
}
