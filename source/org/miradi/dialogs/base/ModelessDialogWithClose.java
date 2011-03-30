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

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.miradi.main.MainWindow;

public class ModelessDialogWithClose extends AbstractDialogWithClose
{
	public ModelessDialogWithClose(JDialog owner, MainWindow mainWindow, String title)
	{
		super(owner, mainWindow);
		
		setTitle(title);
		disableAsModalDialog();
	}
	
	public ModelessDialogWithClose(MainWindow parent, String title)
	{
		super(parent);
		
		setTitle(title);
		disableAsModalDialog();
	}
	
	public ModelessDialogWithClose(MainWindow parent, DisposablePanel panel, String title)
	{
		super(parent, panel);
		
		setTitle(title);
		disableAsModalDialog();
	}

	private void disableAsModalDialog()
	{
		setModal(false);
	}
	
	protected DisposablePanel getMainPanel()
	{
		return (DisposablePanel)getWrappedPanel();
	}

	public ModelessDialogPanel safeGetWrappedModelessDialogPanel()
	{
		JPanel wrappedPanel = getWrappedPanel();
		if(wrappedPanel instanceof ModelessDialogPanel)
			return (ModelessDialogPanel)wrappedPanel;
		
		return null;
	}
}
