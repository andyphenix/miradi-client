/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.umbrella;

import java.text.ParseException;
import java.util.Vector;

import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.ObjectsDoer;

public class DeleteActivity extends ObjectsDoer
{
	public boolean isAvailable()
	{
		if (getObjects() == null)
			return false;
		
		if ((getObjects().length != 1))
			return false;
		
		if (!(getSelectedObjectType() == ObjectType.TASK))
			return false;
		
		return true;
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
	
		Task selectedTask = (Task)getObjects()[0];
		deleteTask(getProject(), getSelectionHierarchy(), selectedTask);
	}

	public static void deleteTask(Project project, ORefList selectionHierarchy, Task selectedTask) throws CommandFailedException
	{
		String[] buttons = {EAM.text("Button|Delete"), EAM.text("Button|Retain"), };
		String[] confirmText = {EAM.text("This will delete any subtasks too. Are you sure you want to delete?")};
		if(!EAM.confirmDialog(EAM.text("Title|Delete"), confirmText, buttons))
			return;
		
		project.executeCommand(new CommandBeginTransaction());
		try
		{
			deleteTaskTree(project, selectionHierarchy, selectedTask);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
		finally
		{
			project.executeCommand(new CommandEndTransaction());
		}
	}

	public static void deleteTaskTree(Project project, ORefList selectionHierarchy, Task selectedTask) throws Exception
	{
		Vector commandToDeleteTasks = createDeleteCommands(project, selectionHierarchy, selectedTask); 
		executeDeleteCommands(project, commandToDeleteTasks);
		
		if (! isOrphandTask(selectedTask))
			return;
		
		Vector commandsToDeletTask = selectedTask.getDeleteSelfAndSubtasksCommands(project);
		project.executeCommandsWithoutTransaction(commandsToDeletTask);
	}
	
	private static boolean isOrphandTask(Task selectedTask) throws Exception
	{
		ORefList referrers = selectedTask.findObjectThatReferToUs();
		if (referrers.size() > 0)
			return false;
		
		return true;
	}

	private static void executeDeleteCommands(Project project, Vector commands) throws ParseException, CommandFailedException
	{
		project.executeCommandsWithoutTransaction(commands);
	}

	private static Vector createDeleteCommands(Project project, ORefList selectionHierarchy, Task task) throws Exception
	{
		
		//FIXME need to consider parent hierachy when creating commands.  first refactor dup code.  
		Vector commandsToDeleteTasks = new Vector();
		commandsToDeleteTasks.addAll(buildRemoveCommandsForActivityIds(project, selectionHierarchy, task));
		commandsToDeleteTasks.addAll(buildRemoveCommandsForMethodIds(project, selectionHierarchy, task));
		commandsToDeleteTasks.addAll(buildRemoveCommandsForTaskIds(project, task));
		
		return commandsToDeleteTasks;
	}
	
	private static Vector buildRemoveCommandsForActivityIds(Project project, ORefList selectionHierarchy, Task task) throws Exception
	{
		if (! task.isActivity())
			return new Vector();
		
		return buildRemoveCommands(project, Strategy.getObjectType(), selectionHierarchy, Strategy.TAG_ACTIVITY_IDS, task);
	}
	
	private static Vector buildRemoveCommandsForMethodIds(Project project, ORefList selectionHierarchy, Task task) throws Exception
	{
		if (! task.isMethod())
			return new Vector();
		
		return buildRemoveCommands(project, Indicator.getObjectType(), selectionHierarchy, Indicator.TAG_TASK_IDS, task);
	}
	
	private static Vector buildRemoveCommandsForTaskIds(Project project, Task task) throws ParseException
	{
		if (! task.isTask())
			return new Vector();
		
		Vector removeCommands = new Vector();
		BaseObject parentObject = task.getOwner();
		removeCommands.add(CommandSetObjectData.createRemoveIdCommand(parentObject,	Task.TAG_SUBTASK_IDS, task.getId()));
		
		return removeCommands;
	}
	
	private static Vector buildRemoveCommands(Project project, int parentType, ORefList selectionHierarchy, String tag, Task task) throws Exception
	{
		Vector removeCommands = new Vector();
		ORefList referrerRefs = task.findObjectsThatReferToUs(parentType);
		for (int i = 0; i < referrerRefs.size(); ++i)
		{
			BaseObject referrer = project.findObject(referrerRefs.get(i));
			if (selectionHierarchy.contains(referrer.getRef()))
				removeCommands.add(CommandSetObjectData.createRemoveIdCommand(referrer, tag, task.getId()));
		}
		
		return removeCommands;		
	}
}
