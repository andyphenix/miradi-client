/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogfields;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.conservationmeasures.eam.dialogs.fieldComponents.PanelCheckBox;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.questions.ChoiceItem;
import org.conservationmeasures.eam.questions.ChoiceQuestion;
import org.conservationmeasures.eam.utils.CodeList;

import com.jhlabs.awt.BasicGridLayout;

public class CodeListComponent extends JPanel implements ItemListener
{
	public CodeListComponent(ChoiceQuestion questionToUse, ListSelectionListener listener)
	{
		this(questionToUse, 3, listener);
	}
	
	public CodeListComponent(ChoiceQuestion questionToUse, int columnCount, ListSelectionListener listener)
	{
		setLayout(new BasicGridLayout(0,columnCount));
		listSelectionListener = listener;
		ChoiceItem[] choices = questionToUse.getChoices();
		choiceItems = new ChoiceItem[choices.length];
		checkBoxes = new PanelCheckBox[choices.length];
		
		for (int i=0; i<choices.length; ++i)
		{
			JCheckBox checkBox = new PanelCheckBox(choices[i].getLabel());
			checkBox.addItemListener(this);
			choiceItems[i] = choices[i];
			checkBoxes[i] = checkBox;
			add(checkBox);
		}
	}
	
	public void itemStateChanged(ItemEvent e) 
	{
	    if (e.getStateChange() == ItemEvent.SELECTED || 
	    	e.getStateChange() == ItemEvent.DESELECTED)
	    {
	    	valueChanged();
	    }
	}
	
	public void valueChanged()
	{
		if (!skipNotice)
		{
			ListSelectionEvent event = new ListSelectionEvent("DUMMY EVENT",0,0, false);
			listSelectionListener.valueChanged(event);
		}
	}
	
	public String getText()
	{
		CodeList codes = new CodeList();
		for (int checkBoxIndex = 0; checkBoxIndex<checkBoxes.length; ++checkBoxIndex )
		{
			JCheckBox checkBox = checkBoxes[checkBoxIndex];
			if (checkBox.isSelected())
			{
				ChoiceItem choiceItem = choiceItems[checkBoxIndex];
				codes.add(choiceItem.getCode());
			}
		}
		return codes.toString();
	}
	
	
	public void setText(String codesToUse)
	{
		skipNotice=true;
		try
		{
			CodeList codes = new CodeList(codesToUse);

			for (int choiceIndex = 0; choiceIndex<choiceItems.length; ++choiceIndex)
			{
				checkBoxes[choiceIndex].setSelected(false);
				ChoiceItem choiceItem = choiceItems[choiceIndex];
				boolean isChecked  = codes.contains(choiceItem.getCode());
				checkBoxes[choiceIndex].setSelected(isChecked);
			}
		}
		catch(Exception e)
		{
			EAM.errorDialog(EAM.text("Internal Error"));
			EAM.logException(e);
		}
		finally
		{
			skipNotice=false;
		}
	}
	
	public void setEnabled(boolean isValidObject)
	{
		super.setEnabled(isValidObject);
		for (int checkBoxIndex = 0; checkBoxIndex<checkBoxes.length; ++checkBoxIndex)
		{
			updateEditableState(checkBoxes[checkBoxIndex],isValidObject);
		}
	}
	
	public void updateEditableState(JCheckBox checkBox, boolean isValidObject)
	{
			checkBox.setEnabled(isValidObject);
			Color fg = EAM.EDITABLE_FOREGROUND_COLOR;
			Color bg = EAM.EDITABLE_BACKGROUND_COLOR;
			if(!isValidObject)
			{
				fg = EAM.READONLY_FOREGROUND_COLOR;
				bg = EAM.READONLY_BACKGROUND_COLOR;
			}
			checkBox.setForeground(fg);
			checkBox.setBackground(bg);

	}

	private JCheckBox checkBoxes[];
	private ChoiceItem choiceItems[];
	private ListSelectionListener listSelectionListener;
	private boolean skipNotice;
}
