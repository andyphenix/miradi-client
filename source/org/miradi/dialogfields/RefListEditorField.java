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
package org.miradi.dialogfields;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.MiradiScrollPane;

public class RefListEditorField extends ObjectDataInputField implements ListSelectionListener
{
	public RefListEditorField(Project projectToUse, int objectTypeToUse, BaseId objectIdToUse, String tagToUse, ChoiceQuestion questionToUse)
	{
		super(projectToUse, new ORef(objectTypeToUse, objectIdToUse), tagToUse);
		
		refListEditor = new RefListComponent(questionToUse);
		refListEditor.addListSelectionListener(this);
		//TODO Panels that use this component are still needing to place the component into a scroll pane.
		//IF they dont, the list will not be scrollable.  
		refListScroller = new MiradiScrollPane(refListEditor);
	}
	
	public void refreshRefs()
	{
		refListEditor.getQuestion().reloadQuestion();
		refListEditor.rebuildToggleButtonsBoxes();
	}
	
	@Override
	public JComponent getComponent()
	{
		return refListScroller;
	}

	@Override
	public String getText()
	{
		return getComponentText();
	}

	protected String getComponentText()
	{
		return refListEditor.getText();
	}
	
	protected ORefList getComponentRefList()
	{
		return refListEditor.getRefList();
	}

	@Override
	public void setText(String codes)
	{	
		refListEditor.setText(codes);
	}
	
	@Override
	protected boolean shouldBeEditable()
	{
		return isValidObject();
	}
	
	public void valueChanged(ListSelectionEvent arg0)
	{
		forceSave();
	}
	
	protected RefListComponent refListEditor;
	private MiradiScrollPane refListScroller;
}
