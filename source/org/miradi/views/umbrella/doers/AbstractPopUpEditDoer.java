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
package org.miradi.views.umbrella.doers;

import org.martus.swing.Utilities;
import org.miradi.dialogs.base.MiradiDialog;
import org.miradi.dialogs.base.ModalDialogWithClose;
import org.miradi.dialogs.base.ModelessDialogWithDirections;
import org.miradi.dialogs.base.ObjectListManagementPanel;
import org.miradi.dialogs.base.ObjectManagementPanel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.views.ObjectsDoer;

abstract public class AbstractPopUpEditDoer extends ObjectsDoer
{
	public AbstractPopUpEditDoer(int objectTypeToUse, String dialogTitleToUse)
	{
		objectType = objectTypeToUse;
		dialogTitle = dialogTitleToUse;
	}
	
	@Override
	public boolean isAvailable()
	{
		if(getSelectedHierarchies().length != 1)
			return false;
		
		ORef ref = getSelectedHierarchies()[0].getRefForType(getTypeToFilterOn());
		if(ref == null || ref.isInvalid())
			return false;
		
		return true;
	}

	@Override
	protected void doIt() throws Exception
	{
		if(!isAvailable())
			return;
		
		try
		{
			showManagementDialog(getMainWindow(), createManagementPanel(), getDialogTitle());			
		}
		catch(Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
	
	public static void showManagementDialogWithInstructionsButton(MainWindow mainWindow, ObjectManagementPanel managementPanel, String dialogTitle) throws Exception
	{
		showDialog(new ModelessDialogWithDirections(mainWindow, managementPanel, dialogTitle), managementPanel);
	}

	public static void showManagementDialog(MainWindow mainWindow, ObjectManagementPanel managementPanel, String dialogTitle) throws Exception
	{
		showDialog(new ModalDialogWithClose(mainWindow, managementPanel, dialogTitle), managementPanel);
	}

	private static void showDialog(MiradiDialog dialog, ObjectManagementPanel managementPanel)
	{
		Utilities.centerDlg(dialog);

		managementPanel.becomeActive();
		dialog.setVisible(true);
	}

	protected int getTypeToFilterOn()
	{
		return objectType;
	}

	protected String getDialogTitle()
	{
		return dialogTitle;
	}

	abstract protected ObjectListManagementPanel createManagementPanel() throws Exception;
	
	private int objectType;
	private String dialogTitle;
}
