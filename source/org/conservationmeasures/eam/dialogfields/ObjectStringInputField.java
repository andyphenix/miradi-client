/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogfields;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.conservationmeasures.eam.dialogs.fieldComponents.PanelTextArea;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.Project;
import org.martus.swing.UiTextArea;

public class ObjectStringInputField extends ObjectTextInputField
{
	public ObjectStringInputField(Project projectToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse, int columnsToUse)
	{
		super(projectToUse, objectTypeToUse, objectIdToUse, tagToUse, new PanelTextArea(0, columnsToUse));
		DocumentEventHandler handler = new DocumentEventHandler();
		((JTextComponent)getComponent()).getDocument().addUndoableEditListener(handler);
		((UiTextArea)getComponent()).setWrapStyleWord(true);
		((UiTextArea)getComponent()).setLineWrap(true);
	}


	class DocumentEventHandler implements  UndoableEditListener
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
					
					// FIXME: Avoid beeping when loading legacy projects
					// Also similar code in ObjectStringInputField, 
					// ObjectAdjustableStringInputField, and 
					// UiTextFieldWithLengthLimit
					//Toolkit.getDefaultToolkit().beep();
				}
			}
			catch(BadLocationException e1)
			{
				EAM.logException(e1);
			}
		}
	}

	public void setText(String newValue)
	{
		newValue.replaceAll("\n", " ");
		super.setText(newValue);
	}
}

