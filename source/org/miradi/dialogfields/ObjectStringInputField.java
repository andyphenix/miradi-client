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

import java.awt.event.FocusEvent;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.martus.swing.UiTextArea;
import org.miradi.dialogs.fieldComponents.PanelTextArea;
import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.utils.HtmlUtilities;
import org.miradi.utils.StringUtilities;

public class ObjectStringInputField extends ObjectTextInputField
{
	public ObjectStringInputField(MainWindow mainWindowToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse, int columnsToUse)
	{
		this(mainWindowToUse, objectTypeToUse, objectIdToUse, tagToUse, new PanelTextArea(0, columnsToUse));		
	}
	
	public ObjectStringInputField(MainWindow mainWindowToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse, int columnsToUse, Document document)
	{
		this(mainWindowToUse, objectTypeToUse, objectIdToUse, tagToUse, new PanelTextArea(0, columnsToUse), document);
	}
	
	private ObjectStringInputField(MainWindow mainWindowToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse, PanelTextArea componentToUse)
	{
		this(mainWindowToUse, objectTypeToUse, objectIdToUse, tagToUse, componentToUse, componentToUse.getDocument());
	}

	private ObjectStringInputField(MainWindow mainWindowToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse, PanelTextArea componentToUse, Document document)
	{
		super(mainWindowToUse, objectTypeToUse, objectIdToUse, tagToUse, componentToUse, document);
		
		DocumentEventHandler handler = new DocumentEventHandler();
		((JTextComponent)getComponent()).getDocument().addUndoableEditListener(handler);
		((UiTextArea)getComponent()).setWrapStyleWord(true);
		((UiTextArea)getComponent()).setLineWrap(true);		
	}

	@Override
	public void setText(String newValue)
	{
		newValue.replaceAll(StringUtilities.NEW_LINE, StringUtilities.EMPTY_SPACE);
		newValue = HtmlUtilities.convertHtmlToPlainText(newValue);
		
		super.setText(newValue);
	}
	
	@Override
	public String getText()
	{
		String text = super.getText();
		text = HtmlUtilities.convertPlainTextToHtmlText(text);
		
		return text;
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		super.focusGained(e);
		
		if (!wasFocusLostTemporarily())
			selectAll();
	}

	//FIXME medium - this handler is here to remove new lines entered by the user.
	// this mechanism should be replaced with a Document that does not allow new lines.
	private class DocumentEventHandler implements UndoableEditListener
	{
		public void undoableEditHappened(UndoableEditEvent e)
		{
			Document document = (Document)e.getSource();
			try
			{
				if (document.getLength()==0)
					return;
			
				String text = document.getText(0, document.getLength());
				int index = text.indexOf('\n');
				if (index>=0)
				{
					e.getEdit().undo();
				}
			}
			catch(BadLocationException e1)
			{
				EAM.logException(e1);
			}
		}
	}
}

