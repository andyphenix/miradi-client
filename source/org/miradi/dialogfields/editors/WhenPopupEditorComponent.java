/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogfields.editors;

import org.martus.swing.Utilities;
import org.miradi.dialogfields.FieldSaver;
import org.miradi.dialogs.base.ModalDialogWithClose;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objects.BaseObject;
import org.miradi.utils.AbstractPopupEditorComponent;
import org.miradi.utils.CodeList;

/*
 * FIXME: This code currently retrieves dialog data after the dialog has been disposed
 */
public class WhenPopupEditorComponent extends AbstractPopupEditorComponent
{
	@Override
	protected void invokePopupEditor()
	{
		try
		{
			MainWindow mainWindow = EAM.getMainWindow();
			String title = EAM.substitute(EAM.text("When - %s"), getBaseObjectForRowLabel());
			ModalDialogWithClose dialog = new ModalDialogWithClose(mainWindow, title);
			whenEditorPanel = new WhenEditorComponent(mainWindow.getProject().getProjectCalendar(), baseObjectForRow);
			dialog.setMainPanel(whenEditorPanel);
			Utilities.centerFrame(dialog);
			dialog.pack();
			dialog.setVisible(true);
			FieldSaver.savePendingEdits();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.unexpectedErrorDialog(e);
		}
	}
	
	@Override
	public String getText()
	{
		try
		{
			if (whenEditorPanel != null)
				return whenEditorPanel.getStartEndCodes().toString();
			
			return null;
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.unexpectedErrorDialog(e);
			return new CodeList().toString();
		}
	}
	
	private String getBaseObjectForRowLabel()
	{
		return baseObjectForRow.combineShortLabelAndLabel();
	}
	
	public void setBaseObjectForRowLabel(BaseObject baseObjectForRowToUse)
	{
		baseObjectForRow = baseObjectForRowToUse;
	}
	
	private WhenEditorComponent whenEditorPanel;
	private BaseObject baseObjectForRow;
}
