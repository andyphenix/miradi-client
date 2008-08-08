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
package org.miradi.utils;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JTable;

import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

public class TableRowHeightSaver implements MouseListener, MouseMotionListener
{
	public TableRowHeightSaver()
	{
	}
	
	public void manage(MainWindow mainWindowToUse, TableWithRowHeightManagement tableToManage, String uniqueTableIdentifierToUse)
	{
		mainWindow = mainWindowToUse;
		table = tableToManage.asTable();
		tableWithRowManagement = tableToManage;
		uniqueTableIdentifier = uniqueTableIdentifierToUse;

		if(tableWithRowManagement.allowUserToSetRowHeight())
		{
			table.addMouseListener(this);
			table.addMouseMotionListener(this);
		}

		restoreRowHeight();
	}
	
	public void setMultiTableRowHeightController(MultiTableRowHeightController controller)
	{
		multiTableController = controller;
	}

	public void rowHeightChanged(int newRowHeight)
	{
		saveRowHeight();

		if(multiTableController == null)
			return;
		
		multiTableController.rowHeightChanged(newRowHeight);
	}
	
	public void rowHeightChanged(int row, int newRowHeight)
	{
		if(multiTableController == null)
			return;
		
		multiTableController.rowHeightChanged(row, newRowHeight);
	}
	
	private void restoreRowHeight()
	{
		if(isRowHeightAutomatic())
			return;
		
		int rowHeight = getPreferences().getTaggedInt(getKey());
		if(rowHeight > 0)
		{
			table.setRowHeight(rowHeight);
			EAM.logVerbose("restoreRowHeight " + getKey() + ": " + table.getRowHeight());
		}
	}
	
	private void saveRowHeight()
	{
		EAM.logVerbose("saveRowHeight " + getKey() + ": " + table.getRowHeight());
		getPreferences().setTaggedInt(getKey(), table.getRowHeight());
	}

	private AppPreferences getPreferences()
	{
		return EAM.getMainWindow().getAppPreferences();
	}
	
	private String getKey()
	{
		return "RowHeight." + uniqueTableIdentifier;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		if(resizeInProgress)
			e.consume();
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		if(isRowHeightAutomatic())
			return;
		
		if(!inRowResizeArea(e))
			return;
		
		beginResizing(e);
		e.consume();
	}

	public void mouseReleased(MouseEvent e)
	{
		endResizing();
	}

	public void mouseDragged(MouseEvent e)
	{
		if(isRowHeightAutomatic())
			return;
		
		if(!resizeInProgress)
			return;
		
		sizeDeltaY = e.getY() - dragStartedY;
		table.setRowHeight(rowBeingResized, getNewRowHeight());
		e.consume();
	}

	public void mouseMoved(MouseEvent event)
	{
		if(isRowHeightAutomatic())
			return;
		
		if(resizeInProgress)
			return;
		
		if(inRowResizeArea(event))
			setResizeCursor();
		else
			restoreDefaultCursor();
	}

	private boolean isRowHeightAutomatic()
	{
		return mainWindow.isRowHeightModeAutomatic();
	}

	private boolean inRowResizeArea(MouseEvent event)
	{
		Point point = event.getPoint();
		
		int y = event.getY();
		int row = table.rowAtPoint(point);

		int height = table.getRowHeight();
		int rowStartY = row * height;
		int withinRowY = y - rowStartY;
		int border = ROW_RESIZE_MARGIN;
		
		boolean inBorderChangeArea = (withinRowY >= height - border);
		return inBorderChangeArea;
	}
	
	void beginResizing(MouseEvent event)
	{
		dragStartedY = event.getY();
		originalRowHeight = table.getRowHeight();
		rowBeingResized = table.rowAtPoint(event.getPoint());
		resizeInProgress = true;
		setResizeCursor();
	}

	void endResizing()
	{
		if(!resizeInProgress)
			return;
		
		resizeInProgress = false;
		restoreDefaultCursor();
		int newHeight = getNewRowHeight();
		table.setRowHeight(newHeight);
		Point point = new Point(0, rowBeingResized * newHeight);
		Rectangle resized = new Rectangle(point, new Dimension(1, newHeight));
		table.scrollRectToVisible(resized);
		table.getSelectionModel().setSelectionInterval(rowBeingResized, rowBeingResized);
		table.getTopLevelAncestor().repaint();
	}

	private int getNewRowHeight()
	{
		int newRowHeight = originalRowHeight + sizeDeltaY;
		newRowHeight = Math.max(newRowHeight, 10);
		return newRowHeight;
	}

	private void setResizeCursor()
	{
		if(oldCursor != null)
			return;
		
		oldCursor = table.getCursor();
		table.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
	}
	
	private void restoreDefaultCursor()
	{
		table.setCursor(oldCursor);
		oldCursor = null;
	}
	
    public final static int ROW_RESIZE_MARGIN = 2;

    private MainWindow mainWindow;
    private TableWithRowHeightManagement tableWithRowManagement;
    private JTable table;
	private String uniqueTableIdentifier;
	
	private boolean resizeInProgress;
	private int dragStartedY;
	private int rowBeingResized;
	private int originalRowHeight;
	private int sizeDeltaY;
	private Cursor oldCursor;
	
	private MultiTableRowHeightController multiTableController;
}

