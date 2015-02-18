/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.dialogs.planning.propertiesPanel;

import java.util.Vector;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.miradi.dialogs.planning.RightClickActionProvider;
import org.miradi.main.MiradiHtmlMenuItem;
import org.miradi.main.MainWindow;
import org.miradi.utils.AbstractTableRightClickHandler;

public class PlanningRightClickHandler extends AbstractTableRightClickHandler
{
	public PlanningRightClickHandler(MainWindow mainWindow, JTable tableToUse, RightClickActionProvider rightClickProvider)
	{
		super(mainWindow, tableToUse);
		actionProvider = rightClickProvider;
	}
	
	@Override
	protected void populateMenu(JPopupMenu popupMenu)
	{
		Vector<Action> rightClickActions = actionProvider.getActionsForRightClickMenu(getSelectedRow(), getSelectedColumn());
		for(Action action : rightClickActions)
		{
			if(action == null)
				popupMenu.addSeparator();
			else
				popupMenu.add(new MiradiHtmlMenuItem(action));
		}
	}
	
	private RightClickActionProvider actionProvider;
}
