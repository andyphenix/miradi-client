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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.martus.swing.Utilities;
import org.miradi.dialogs.base.MiradiPanel;
import org.miradi.dialogs.fieldComponents.CheckBoxWithChoiceItemProvider;
import org.miradi.dialogs.fieldComponents.ChoiceItemProvider;
import org.miradi.dialogs.fieldComponents.ControlPanelHtmlFormViewer;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.FillerLabel;
import org.miradi.utils.FlexibleWidthHtmlViewer;

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
		MiradiPanel mainRowsPanel = new MiradiPanel(new GridLayoutPlus(0, calculateColumnCount())); 
		mainRowsPanel.setBackground(getTogglePanelBackgroundColor());
		for (int index = 0; index < choices.length; ++index)
		{
			ChoiceItem choiceItem = choices[index];
			JComponent leftColumnComponent = createLeftColumnComponent(choiceItem);
			addComponentToRowPanel(mainRowsPanel, leftColumnComponent, choiceItem);
		}
	
		add(mainRowsPanel);
		revalidate();
		repaint();
	}

	protected JComponent createLeftColumnComponent(ChoiceItem choiceItem)
	{
		JToggleButton toggleButton = createToggleButton(choiceItem);
		toggleButton.setBackground(choiceItem.getColor());
		toggleButton.addActionListener(new ToggleButtonHandler());
		choiceItemToToggleButtonMap.put(choiceItem, toggleButton);
		
		return createLeftColumnComponent(choiceItem, toggleButton);
	}

	protected JComponent createLeftColumnComponent(ChoiceItem choiceItem, JComponent selectableLeftColumnComponent)
	{
		if (choiceItem.isSelectable())
			return selectableLeftColumnComponent;
		
		return new PanelTitleLabel(choiceItem.getTextAsHtmlWrappedLabel());
	}

	protected int calculateColumnCount()
	{
		return getColumnCount() * getNumberOfComponentsPerChoice();
	}

	protected void addComponentToRowPanel(MiradiPanel mainRowsPanel, JComponent leftColumnComponent, ChoiceItem choiceItem)
	{
		mainRowsPanel.add(getSafeIconLabel(choiceItem.getIcon()));
		mainRowsPanel.add(leftColumnComponent);
		mainRowsPanel.add(createDescriptionComponent(choiceItem));
	}

	protected int getNumberOfComponentsPerChoice()
	{
		final int ICON_COMPONENT_COUNT = 1;
		final int TOGGLE_BUTTON_COMPONENT_COUNT = 1;
		final int DESCRIPTION_COMPONENT_COUNT = 1;
		final int TOTAL_COMPONENT_COUNT_PER_CHOICE = ICON_COMPONENT_COUNT + TOGGLE_BUTTON_COMPONENT_COUNT + DESCRIPTION_COMPONENT_COUNT;
		return TOTAL_COMPONENT_COUNT_PER_CHOICE;
	}
	
	protected Component createDescriptionComponent(ChoiceItem choiceItem)
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

	protected PanelTitleLabel getSafeIconLabel(Icon icon)
	{
		if (icon == null)
			return new PanelTitleLabel();
		
		return new PanelTitleLabel(icon);
	}

	protected JToggleButton createToggleButton(ChoiceItem choiceItem)
	{
		return new CheckBoxWithChoiceItemProvider(choiceItem);
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
		toggleButton.setOpaque(true);
		Color fg = EAM.EDITABLE_FOREGROUND_COLOR;
		Color bg = getTogglePanelBackgroundColor();
		if(!isValidObject)
		{
			fg = EAM.READONLY_FOREGROUND_COLOR;
		}
		toggleButton.setForeground(fg);
		toggleButton.setBackground(bg);
	}
	
	public ChoiceQuestion getQuestion()
	{
		return question;
	}
	
	protected int getColumnCount()
	{
		return columnCount;
	}
	
	private class ToggleButtonHandler implements ActionListener
	{
		public ToggleButtonHandler()
		{
		}
		
		public void actionPerformed(ActionEvent event)
		{
			try
			{
				ChoiceItemProvider choiceItemProvider = (ChoiceItemProvider) event.getSource();
				ChoiceItem choiceItem = choiceItemProvider.getChoiceItem();
				toggleButtonStateChanged(choiceItem, choiceItemProvider.isSelected());
			}
			catch (Exception e)
			{
				EAM.alertUserOfNonFatalException(e);
			}			
		}
	}
	
	abstract protected void toggleButtonStateChanged(ChoiceItem choiceItem, boolean isSelected) throws Exception;
	
	private ChoiceQuestion question;
	protected HashMap<ChoiceItem, JToggleButton> choiceItemToToggleButtonMap;
	private int columnCount;
	public static final int SINGLE_COLUMN = 1;
}
