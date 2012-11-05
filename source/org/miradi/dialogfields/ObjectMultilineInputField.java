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
package org.miradi.dialogfields;


import javax.swing.text.JTextComponent;

import org.miradi.ids.BaseId;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.utils.EditableHtmlPane;
import org.miradi.utils.HtmlEditorRightClickMouseHandler;

public abstract class ObjectMultilineInputField extends ObjectTextInputField
{
	protected ObjectMultilineInputField(MainWindow mainWindowToUse, ORef refToUse, String tagToUse, int initialVisibleRows, int columnsToUse) throws Exception
	{
		this(mainWindowToUse, refToUse.getObjectType(), refToUse.getObjectId(), tagToUse, initialVisibleRows, columnsToUse);
	}
	
	protected ObjectMultilineInputField(MainWindow mainWindowToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse, int initialVisibleRows, int columnsToUse) throws Exception
	{
		super(mainWindowToUse, objectTypeToUse, objectIdToUse, tagToUse, createTextComponent(mainWindowToUse, initialVisibleRows, columnsToUse));
		
		mainWindow = mainWindowToUse;
	}

	private static JTextComponent createTextComponent(MainWindow mainWindow, int initialVisibleRows, int columnsToUse) throws Exception
	{
		return new EditableHtmlPane(mainWindow, columnsToUse, initialVisibleRows);
	}
	
	@Override
	protected void setSaveListener(DocumentEventHandler saveListenerToUse) throws Exception
	{
		
		((EditableHtmlPane)getTextField()).initializeListener(saveListenerToUse);
	}

	@Override
	protected void createRightClickMouseHandler()
	{
		new HtmlEditorRightClickMouseHandler(getActions(), getTextField());
	}	
	
	protected MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	private MainWindow mainWindow;
}
