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

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;

import org.miradi.ids.BaseId;
import org.miradi.main.MainWindow;

public class ObjectStringInputFieldWithLengthLimit extends ObjectStringInputField
{
	public ObjectStringInputFieldWithLengthLimit(MainWindow mainWindowToUse, int objectType, BaseId objectId, String tag, int columnsToUse, Document document)
	{
		super(mainWindowToUse, objectType, objectId, tag, columnsToUse, document);
		
		UndoableEditHandler handler = new UndoableEditHandler();
		getTextField().getDocument().addUndoableEditListener(handler);
		columns = columnsToUse;
	}

	//FIXME medium - this handler is here to remove characters exceeding column count
	// this mechanism should be replaced with a Document with a defined length
	class UndoableEditHandler implements  UndoableEditListener
	{
		public void undoableEditHappened(UndoableEditEvent e)
		{
			Document document = (Document)e.getSource();
			if (document.getLength()<=columns)
				return;
			e.getEdit().undo();
		}
	}
	
	int columns;
}
