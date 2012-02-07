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
package org.miradi.dialogs.base;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.martus.swing.UiButton;
import org.miradi.actions.Actions;
import org.miradi.actions.MiradiAction;
import org.miradi.actions.ObjectsAction;
import org.miradi.dialogs.fieldComponents.PanelButton;
import org.miradi.layout.OneRowPanel;
import org.miradi.main.AppPreferences;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.utils.MiradiScrollPane;
import org.miradi.views.umbrella.ObjectPicker;

abstract public class ObjectCollectionPanel extends DisposablePanel implements CommandExecutedListener
{
	public ObjectCollectionPanel(MainWindow mainWindowToUse, ObjectPicker componentToUse)
	{
		super(new BorderLayout());
		
		mainWindow = mainWindowToUse;
		component = componentToUse;
		
		//FIXME low: Need to pass in a picker and a component to avoid this cast.
		//Another option to review is to not pass in component.
		MiradiScrollPane tableScrollPane = new MiradiScrollPane((JComponent)component);
		tableScrollPane.setBackground(AppPreferences.getDataPanelBackgroundColor());
		tableScrollPane.getViewport().setBackground(AppPreferences.getDataPanelBackgroundColor());
		add(tableScrollPane, BorderLayout.CENTER);
		buttons = new OneRowPanel();
		buttons.setGaps(3);
		buttons.setBackground(AppPreferences.getDataPanelBackgroundColor());

		add(buttons, BorderLayout.BEFORE_FIRST_LINE);
		setFocusCycleRoot(true);

		setBackground(AppPreferences.getDataPanelBackgroundColor());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		getProject().addCommandExecutedListener(this);
	}
	
	@Override
	public void dispose()
	{
		getProject().removeCommandExecutedListener(this);
		super.dispose();
	}
	
	@Override
	public void becomeActive()
	{
		getPicker().becomeActive();
	}

	@Override
	public void becomeInactive()
	{
		getPicker().becomeInactive();
	}
	
	public boolean isActive()
	{
		return getPicker().isActive();
	}
	
	public void setPropertiesPanel(AbstractObjectDataInputPanel panel)
	{
		propertiesPanel = panel;
		ORefList selectionHierarchy = component.getSelectionHierarchy();
		if(selectionHierarchy == null)
			selectionHierarchy = new ORefList();
			
		if (propertiesPanel != null)
			propertiesPanel.setObjectRefs(selectionHierarchy.toArray());
	}

	public AbstractObjectDataInputPanel getPropertiesPanel()
	{
		return propertiesPanel;
	}
	
	protected void addObjectActionButton(Class objectActionClass, ObjectPicker pickerToUse)
	{
		ObjectsAction action = getActions().getObjectsAction(objectActionClass);
		addButton(createObjectsActionButton(action, pickerToUse));
	}
	
	protected void addNonObjectActionButton(Class nonObjectsActionClass)
	{
		MiradiAction action = getActions().get(nonObjectsActionClass);
		addButton(new PanelButton(action));
	}
	
	//TODO Should call specific methods to add OA or NonOA buttons
	protected void addUnknownTypeOfButton(Class actionClass)
	{
		MiradiAction action = getActions().get(actionClass);
		if (action.isObjectAction())
			addObjectActionButton(actionClass, component);
		else
			addNonObjectActionButton(actionClass);
	}
	
	private void addButton(UiButton button)
	{
		buttons.add(button);
	}
	
	private Actions getActions()
	{
		return getMainWindow().getActions();
	}
	
	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	public Project getProject()
	{
		return getMainWindow().getProject();
	}
	
	public ObjectPicker getPicker()
	{
		return component;
	}
	
	final public void commandExecuted(CommandExecutedEvent event)
	{
		if(isActive())
			handleCommandEventImmediately(event);
		else
			handleCommandEventWhileInactive(event);
	}
	
	protected void handleCommandEventWhileInactive(CommandExecutedEvent event)
	{
		// NOTE: Most classes ignore; override to pay attention
	}

	abstract public void handleCommandEventImmediately(CommandExecutedEvent event);
	abstract public BaseObject getSelectedObject();
	
	
	private MainWindow mainWindow;
	private OneRowPanel buttons;
	private ObjectPicker component;
	private AbstractObjectDataInputPanel propertiesPanel;
}
