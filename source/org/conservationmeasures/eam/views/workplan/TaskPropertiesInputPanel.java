/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.workplan;

import javax.swing.BorderFactory;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.dialogs.ObjectDataInputPanel;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;

public class TaskPropertiesInputPanel extends ObjectDataInputPanel
{
	
	public TaskPropertiesInputPanel(Project projectToUse, Actions actions, BaseId idToEdit, AssignmentEditorComponent editorComponentToUse) throws Exception
	{
		this(projectToUse, actions, idToEdit);
		editorComponent = editorComponentToUse;
	}
	
	public TaskPropertiesInputPanel(Project projectToUse, Actions actions, BaseId idToEdit) throws Exception
	{
		super(projectToUse, ObjectType.TASK, idToEdit);
		project = projectToUse;
		setBorder(BorderFactory.createEtchedBorder());
		
		addCommonFields();
	}
	
	public void dispose()
	{
		super.dispose();
	}

	private void addCommonFields()
	{
		addField(createStringField(Task.TAG_LABEL));
		
		updateFieldsFromProject();
	}
	
	public void setObjectId(BaseId id)
	{
		super.setObjectId(id);
		if (editorComponent == null)
			return;
	
		editorComponent.setTaskId(id);
	}
	
	public String getPanelDescription()
	{
		return EAM.text("Title|Task Properties");
	}
		
	public void updateTable()
	{
		if (editorComponent == null)
			return;
		
		editorComponent.dataWasChanged();
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		if (event.getCommandName().equals(CommandSetObjectData.COMMAND_NAME))
			updateTable();
	}
	
	public void commandUndone(CommandExecutedEvent event)
	{
		super.commandUndone(event);
		if (event.getCommandName().equals(CommandSetObjectData.COMMAND_NAME))
			updateTable();
	}
	
	public void commandFailed(Command command, CommandFailedException e)
	{
		super.commandFailed(command, e);
	}
		
	Project project;
	AssignmentEditorComponent editorComponent;
	
}