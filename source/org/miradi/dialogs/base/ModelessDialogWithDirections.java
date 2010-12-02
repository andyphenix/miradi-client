/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.base;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;

import org.martus.swing.UiButton;
import org.miradi.actions.MiradiAction;
import org.miradi.actions.MainWindowAction;
import org.miradi.dialogs.fieldComponents.PanelButton;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

public class ModelessDialogWithDirections extends ModelessDialogWithClose
{
	public ModelessDialogWithDirections(MainWindow parent, String headingText)
	{
		super(parent, headingText);
	}

	public ModelessDialogWithDirections(MainWindow parent, DisposablePanel panel, String headingText)
	{
		super(parent, panel, headingText);
	}

	@Override
	protected Vector<Component> getButtonBarComponents()
	{
		actionDirections = new ActionDirections(getMainWindow());
		UiButton  help = new PanelButton(actionDirections);

		Vector<Component> components = new Vector<Component>(); 
		components.add(help);
		components.addAll(super.getButtonBarComponents());
		
		return components;
	}

	public void updateDirectionsEnabledState()
	{
		actionDirections.updateEnabledState();
	}

	protected Class getJumpAction()
	{
		return getMainPanel().getJumpActionClass();
	}
	
	
	
	protected class ActionDirections extends MainWindowAction
	{

		public ActionDirections(MainWindow mainWindowToUse)
		{
			super(mainWindowToUse, EAM.text("Instructions"), "icons/directions.png");
		}
		
		@Override
		public void doAction() throws Exception
		{
			MiradiAction action = getRealJumpAction();
			if(action == null)
				return;
			
			action.doAction();
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				doAction();
			}
			catch(Exception e1)
			{
				EAM.logException(e1);
			}
		}
		
		@Override
		public boolean shouldBeEnabled()
		{
			MiradiAction action = getRealJumpAction();
			if(action == null)
				return false;
			
			return action.shouldBeEnabled();
		}
		
		private MiradiAction getRealJumpAction()
		{
			Class jumpActionClass = getJumpAction();
			if (jumpActionClass == null)
				return null;
			
			return getMainWindow().getActions().get(jumpActionClass);
		}
	}

	@Override
	public void setVisible(boolean isVisible)
	{
		if(isVisible)
			updateDirectionsEnabledState();
		super.setVisible(isVisible);
	}

	private ActionDirections actionDirections;
}
