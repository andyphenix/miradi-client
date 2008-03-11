/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.dialogs.task;


import java.awt.Component;

import org.miradi.actions.Actions;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogs.base.AbstractObjectDataInputPanel;
import org.miradi.dialogs.diagram.ForecastOverrideSubPanel;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.ids.BaseId;
import org.miradi.layout.OneColumnGridLayout;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Task;
import org.miradi.project.Project;

public class TaskPropertiesInputPanel extends AbstractObjectDataInputPanel
{
	public TaskPropertiesInputPanel(Project projectToUse, Actions actions) throws Exception
	{
		this(projectToUse, actions, BaseId.INVALID);
	}
	
	public TaskPropertiesInputPanel(Project projectToUse, Actions actions, BaseId idToEdit) throws Exception
	{
		super(projectToUse, ObjectType.TASK, idToEdit);
		setLayout(new OneColumnGridLayout());
		
		addSubPanelWithTitledBorder(new TaskDetailsPanel(projectToUse, actions, idToEdit));
		
		hasBothSubTaskAssignmentsWarningLabel = new PanelTitleLabel(EAM.text("NOTE: The budget total for this task is the sum of the budget totals of its subtasks. The resource assignments below are not included in this value."));
		ForecastOverrideSubPanel budgetSubPanel = new ForecastOverrideSubPanel(getProject(), actions, new ORef(Task.getObjectType(), BaseId.INVALID));
		addSubPanelWithTitledBorder(budgetSubPanel);
		
		addFieldComponent(hasBothSubTaskAssignmentsWarningLabel);
		updateFieldsFromProject();
	}
	
	public void dispose()
	{
		super.dispose();
	}

	public void setObjectRefs(ORef[] orefsToUse)
	{
		super.setObjectRefs(orefsToUse);
		updatedWarningMessageVisiblity(orefsToUse);
	}

	
	public String getPanelDescription()
	{
		return EAM.text("Title|Task Properties");
	}
		
	public void updateTable()
	{
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		if (event.getCommandName().equals(CommandSetObjectData.COMMAND_NAME))
			updateTable();
	}
	
	private void updatedWarningMessageVisiblity(ORef[] orefsToUse)
	{
		hasBothSubTaskAssignmentsWarningLabel.setVisible(isVisible(orefsToUse));
	}
			
	private boolean isVisible(ORef[] orefsToUse)
	{
		if (orefsToUse.length == 0)
			return false;
		
		ORef firstRef = orefsToUse[0];
		if(firstRef.isInvalid())
			return false;
		
		BaseObject foundObject = getProject().findObject(firstRef);
		if (foundObject.getType() != Task.getObjectType())
			return false;
		
		Task task = (Task) foundObject;
		if (task.getSubtaskCount() == 0 || task.getAssignmentRefs().size() == 0)
			return false;
		
		return true;
	}
	
	@Override
	public void addFieldComponent(Component component)
	{
		add(component);
	}

	
	private PanelTitleLabel hasBothSubTaskAssignmentsWarningLabel;
}