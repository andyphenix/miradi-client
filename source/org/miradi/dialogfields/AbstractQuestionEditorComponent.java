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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

import org.martus.swing.Utilities;
import org.miradi.dialogs.base.MiradiPanel;
import org.miradi.dialogs.fieldComponents.ControlPanelHtmlFormViewer;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.FillerLabel;
import org.miradi.utils.FlexibleWidthHtmlViewer;
import org.miradi.utils.MiradiScrollPane;

import com.jhlabs.awt.BasicGridLayout;
import com.jhlabs.awt.GridLayoutPlus;

abstract public class AbstractQuestionEditorComponent extends SavebleComponent
{	
	public AbstractQuestionEditorComponent(ChoiceQuestion questionToUse, int columnCountToUse)
	{
		question = questionToUse;
		columnCount = columnCountToUse;
	
		setLayout(new BasicGridLayout(0, columnCount));
		choiceItemToToggleButtonMap = new HashMap<ChoiceItem, JToggleButton>();
		
		rebuildToggleButtonsBoxes();
	}
	
	@Override
	public void dispose()
	{
		removeAll();
		clearChoiceItemToToggleButtonMap();
		
		super.dispose();
	}
	
	private void clearChoiceItemToToggleButtonMap()
	{
		choiceItemToToggleButtonMap.clear();
	}
	
	protected void rebuildToggleButtonsBoxes()
	{
		removeAll();
		addAdditionalComponent();
		ChoiceItem[] choices = getQuestion().getChoices();
		clearChoiceItemToToggleButtonMap();
		MiradiPanel toggleButtonsPanel = new MiradiPanel(new GridLayoutPlus(0, getColumnCount())); 
		toggleButtonsPanel.setBackground(getTogglePanelBackgroundColor());
		for (int index = 0; index < choices.length; ++index)
		{
			ChoiceItem choiceItem = choices[index];
			JToggleButton toggleButton = createToggleButton(choiceItem.getLabel());
			toggleButton.setBackground(choiceItem.getColor());
			toggleButton.addActionListener(new ToggleButtonHandler());
			choiceItemToToggleButtonMap.put(choiceItem, toggleButton);

			MiradiPanel rowPanel = createRowPanel(toggleButton, choiceItem);
			rowPanel.setBackground(getTogglePanelBackgroundColor());
			toggleButtonsPanel.add(rowPanel);
		}
	
		add(new MiradiScrollPane(toggleButtonsPanel));
		revalidate();
		repaint();
	}

	protected MiradiPanel createRowPanel(JToggleButton toggleButton, ChoiceItem choiceItem)
	{
		int gridColumnCount = getColumnCount() * getNumberOfComponentsPerChoice();
		MiradiPanel rowPanel = new MiradiPanel(new GridLayoutPlus(0, gridColumnCount));
		Icon icon = choiceItem.getIcon();
		rowPanel.add(getSafeIconLabel(icon));
		if (choiceItem.isSelectable())
			rowPanel.add(toggleButton);
		else
			rowPanel.add(new PanelTitleLabel(choiceItem.getLabel()));
		
		rowPanel.add(createDescriptionComponent(choiceItem));
		
		return rowPanel;
	}

	private int getNumberOfComponentsPerChoice()
	{
		final int ICON_COMPONENT_COUNT = 1;
		final int TOGGLE_BUTTON_COMPONENT_COUNT = 1;
		final int DESCRIPTION_COMPONENT_COUNT = 1;
		final int TOTAL_COMPONENT_COUNT_PER_CHOICE = ICON_COMPONENT_COUNT + TOGGLE_BUTTON_COMPONENT_COUNT + DESCRIPTION_COMPONENT_COUNT;
		return TOTAL_COMPONENT_COUNT_PER_CHOICE;
	}
	
	private Component createDescriptionComponent(ChoiceItem choiceItem)
	{
		String description = choiceItem.getDescription();
		if (description.length() == 0)
			return new FillerLabel();

		ControlPanelHtmlFormViewer descriptionHtmlPanel = new ControlPanelHtmlFormViewer(EAM.getMainWindow(), description);
		FlexibleWidthHtmlViewer.setFixedWidth(descriptionHtmlPanel, getTwoThirdsOfTheScreenWidth());
		
		return descriptionHtmlPanel;
	}
	
	private int getTwoThirdsOfTheScreenWidth()
	{
		int screenWidth = Utilities.getViewableScreenSize().width;
		
		return (screenWidth * 2) / 3;
	}

	protected Color getTogglePanelBackgroundColor()
	{
		return AppPreferences.getDataPanelBackgroundColor();
	}

	private PanelTitleLabel getSafeIconLabel(Icon icon)
	{
		if (icon == null)
			return new PanelTitleLabel();
		
		return new PanelTitleLabel(icon);
	}

	protected JToggleButton createToggleButton(String label)
	{
		return new JCheckBox(label);
	}

	protected void addAdditionalComponent()
	{
	}

	@Override
	public void setEnabled(boolean isValidObject)
	{
		super.setEnabled(isValidObject);
		Set<ChoiceItem> choices = choiceItemToToggleButtonMap.keySet();
		for(ChoiceItem choiceItem : choices)
		{
			JToggleButton toggleButton = choiceItemToToggleButtonMap.get(choiceItem);
			updateEditableState(toggleButton,isValidObject);
		}
	}

	public void updateEditableState(JToggleButton toggleButton, boolean isValidObject)
	{
		toggleButton.setEnabled(isValidObject);
		Color fg = EAM.EDITABLE_FOREGROUND_COLOR;
		Color bg = EAM.EDITABLE_BACKGROUND_COLOR;
		if(!isValidObject)
		{
			fg = EAM.READONLY_FOREGROUND_COLOR;
			bg = EAM.READONLY_BACKGROUND_COLOR;
		}
		toggleButton.setForeground(fg);
		toggleButton.setBackground(bg);
	}
	
	protected ChoiceQuestion getQuestion()
	{
		return question;
	}
	
	private int getColumnCount()
	{
		return columnCount;
	}
	
	protected class ToggleButtonHandler implements ActionListener
	{
		public ToggleButtonHandler()
		{
		}
		
		public void actionPerformed(ActionEvent event)
		{
			try
			{
				JToggleButton item = (JToggleButton) event.getSource();
				ChoiceItem choiceItem = getQuestion().findChoiceByLabel(item.getText());
				toggleButtonStateChanged(choiceItem, item.isSelected());
			}
			catch (Exception e)
			{
				EAM.logException(e);
				EAM.unexpectedErrorDialog(e);
			}			
		}
	}
	
	abstract protected void toggleButtonStateChanged(ChoiceItem choiceItem, boolean isSelected) throws Exception;
	
	private ChoiceQuestion question;
	protected HashMap<ChoiceItem, JToggleButton> choiceItemToToggleButtonMap;
	private int columnCount;
	protected static final int SINGLE_COLUMN = 1;
}
