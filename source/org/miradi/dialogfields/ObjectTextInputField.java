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


import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.miradi.actions.Actions;
import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.utils.HtmlUtilities;

import com.inet.jortho.SpellChecker;

public class ObjectTextInputField extends ObjectDataInputField
{
	public ObjectTextInputField(MainWindow mainWindowToUse, int objectType, BaseId objectId, String tag, JTextComponent componentToUse)
	{
		this(mainWindowToUse, objectType, objectId, tag, componentToUse, componentToUse.getDocument());
	}
	
	public ObjectTextInputField(MainWindow mainWindowToUse, int objectType, BaseId objectId, String tag, JTextComponent componentToUse, Document document)
	{
		super(mainWindowToUse.getProject(), objectType, objectId, tag);
		
		field = componentToUse;
		field.setDocument(document);
		addFocusListener();
		setEditable(true);
		field.getDocument().addDocumentListener(new DocumentEventHandler());
		createRightClickMouseHandler();
		field.addKeyListener(new UndoRedoKeyHandler(getActions()));
		
		setDefaultFieldBorder();
	}

	protected void createRightClickMouseHandler()
	{
		new TextAreaRightClickMouseHandler(getActions(), field);
	}	

	@Override
	public JComponent getComponent()
	{
		return field;
	}

	@Override
	public String getText()
	{
		final String text = field.getText();
		return HtmlUtilities.replaceNonHtmlNewlines(text);
	}

	@Override
	public void setText(String newValue)
	{
		newValue = HtmlUtilities.replaceHtmlNewlines(newValue);
		setTextWithoutScrollingToMakeFieldVisible(newValue);
		clearNeedsSave();
	}
	
	private void setTextWithoutScrollingToMakeFieldVisible(String newValue)
	{
		DefaultCaret caret = (DefaultCaret)field.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		field.setText(newValue);
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

	@Override
	public void updateEditableState()
	{
		boolean editable = allowEdits() && isValidObject();
		field.setEditable(editable);
		Color fg = EAM.EDITABLE_FOREGROUND_COLOR;
		Color bg = EAM.EDITABLE_BACKGROUND_COLOR;
		if(!editable)
		{
			fg = EAM.READONLY_FOREGROUND_COLOR;
			bg = EAM.READONLY_BACKGROUND_COLOR;
		}
		field.setForeground(fg);
		field.setBackground(bg);

		if(EAM.getMainWindow().isSpellCheckerActive())
			SpellChecker.register(field, false, false, true);
		else
			SpellChecker.unregister(field);
	}

	protected Actions getActions()
	{
		return EAM.getMainWindow().getActions();
	}
	
	protected JTextComponent getTextField()
	{
		return field;
	}

	JTextComponent field;
}
