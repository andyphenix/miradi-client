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
package org.miradi.views.planning.doers;

import java.text.ParseException;
import java.util.Vector;

import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Factor;
import org.miradi.objects.Indicator;
import org.miradi.objects.Measurement;
import org.miradi.objects.Objective;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.Task;
import org.miradi.project.Project;
import org.miradi.views.umbrella.DeleteActivityDoer;
import org.miradi.views.umbrella.doers.AbstractDeleteDoer;

public class TreeNodeDeleteDoer extends AbstractDeleteDoer
{
	@Override
	public boolean isAvailable()
	{
		BaseObject selected = getSingleSelectedObject();
		if(selected == null)
			return false;
		
		return canDelete(selected);
	}

	private boolean canDelete(BaseObject selected)
	{
		if (Indicator.is(selected))
			return true;
		
		if (Objective.is(selected))
			return true;
		
		if (Measurement.is(selected))
			return true;
		
		if (ResourceAssignment.is(selected))
			return true;
		
		return Task.is(selected.getType());
	}

	@Override
	public void doIt() throws Exception
	{
		if (!isAvailable())
			return;
		
		BaseObject selected = getSingleSelectedObject();
		
		//TODO this might be a redundant test since isAvailable is testing same thing.  why is it here
		if(!canDelete(selected))
			return;

		EAM.logVerbose("Deleting: " + selected.getRef()); 
		try
		{
			if (Task.is(selected))
				deleteTask(selected);
			
			if (Indicator.is(selected))
				deleteAnnotation(selected, Factor.TAG_INDICATOR_IDS);
			
			if (Objective.is(selected))
				deleteAnnotation(selected, Factor.TAG_OBJECTIVE_IDS);
			
			if (Measurement.is(selected))
				deleteAnnotation(selected, Indicator.TAG_MEASUREMENT_REFS);
			
			if (ResourceAssignment.is(selected))
				deleteAnnotation(selected, BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}

	private void deleteAnnotation(BaseObject selected, String annotationListTag) throws Exception
	{
		getProject().executeCommandsAsTransaction(buildCommandsToDeleteAnnotation(getProject(), selected, annotationListTag));
	}
	
	public static Vector<Command> buildCommandsToDeleteAnnotation(Project project, BaseObject objectToRemove, String annotationListTag) throws Exception
	{
		Vector commands = new Vector();
		ORefList ownerRefs = objectToRemove.findObjectsThatReferToUs();
		for (int refIndex = 0; refIndex < ownerRefs.size(); ++refIndex)
		{
			ORef ownerRef = ownerRefs.get(refIndex);
			BaseObject owner = project.findObject(ownerRef);
			if (owner.doesFieldExist(annotationListTag))
			{
				if (owner.isIdListTag(annotationListTag))
					commands.addAll(createCommandToRemoveId(owner, objectToRemove, annotationListTag));
				
				if (owner.isRefList(annotationListTag))
					commands.addAll(createCommandToRemoveRef(owner, objectToRemove, annotationListTag));
			}
		}
		
		commands.addAll(objectToRemove.createCommandsToDeleteChildrenAndObject());
	
		return commands;
	}

	private static Vector<Command> createCommandToRemoveId(BaseObject owner, BaseObject selected, String annotationListTag) throws ParseException
	{
		Vector commands = new Vector();
		IdList idsToRemoveFrom = new IdList(selected.getType(), owner.getData(annotationListTag));
		if (idsToRemoveFrom.contains(selected.getId()))
			commands.add(CommandSetObjectData.createRemoveIdCommand(owner, annotationListTag, selected.getId()));
		
		return commands;
	}
	
	private static Vector<Command> createCommandToRemoveRef(BaseObject owner, BaseObject selected, String annotationListTag) throws ParseException
	{
		Vector commands = new Vector();
		ORefList refsToRemoveFrom = new ORefList(owner.getData(annotationListTag));
		if (refsToRemoveFrom.contains(selected.getRef()))
			commands.add(CommandSetObjectData.createRemoveORefCommand(owner, annotationListTag, selected.getRef()));
		
		return commands;
	}

	private void deleteTask(BaseObject selected) throws CommandFailedException
	{		
		Task selectedTaskToDelete = (Task) selected;
		if (shouldDeleteFromParentOnly(selectedTaskToDelete))
			DeleteActivityDoer.deleteTaskWithUserConfirmation(getProject(), getSelectionHierarchy(), selectedTaskToDelete);
		else
			DeleteActivityDoer.deleteTaskWithUserConfirmation(getProject(), selectedTaskToDelete.findObjectsThatReferToUs(), selectedTaskToDelete);
	}
	
	private boolean shouldDeleteFromParentOnly(Task selectedTaskToDelete)
	{
		ORefList referrers = selectedTaskToDelete.findObjectsThatReferToUs();
		ORefList selectionHierarchy = getSelectionHierarchy();
		
		return selectionHierarchy.containsAnyOf(referrers);
	}
}
