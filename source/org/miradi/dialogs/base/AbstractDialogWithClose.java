/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JDialog;

import org.martus.swing.UiButton;
import org.miradi.dialogs.fieldComponents.PanelButton;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

abstract public class AbstractDialogWithClose extends DialogWithDisposablePanelAndMainWindowUpdating
{
	public AbstractDialogWithClose(JDialog owner, MainWindow mainWindow)
	{
		super(owner, mainWindow);
		
		setButtons();
	}
	
	protected AbstractDialogWithClose(MainWindow parent)
	{
		super(parent);
		
		setButtons();
	}
	
	protected AbstractDialogWithClose(MainWindow parent, DisposablePanel panel)
	{
		super(parent, panel);
		
		setButtons();
	}

	private void setButtons()
	{
		setButtons(getButtonBarComponents());
	}
	
	protected Vector<Component> getButtonBarComponents()
	{
		UiButton closeButton = new PanelButton(EAM.text("Button|Close"));
		setSimpleCloseButton(closeButton);
		
		Vector<Component> components = new Vector<Component>(); 
		components.add(Box.createHorizontalGlue());
		components.add(closeButton);
		
		return components;
	}
}
