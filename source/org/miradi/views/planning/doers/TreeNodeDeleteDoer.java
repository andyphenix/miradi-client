/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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

import java.util.Arrays;
import java.util.Vector;

import org.miradi.commands.CommandDeleteObject;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Factor;
import org.miradi.objects.Indicator;
import org.miradi.objects.Task;
import org.miradi.views.diagram.DeleteAnnotationDoer;
import org.miradi.views.umbrella.DeleteActivity;

public class TreeNodeDeleteDoer extends AbstractTreeNodeDoer
{
	public boolean isAvailable()
	{
		BaseObject selected = getSingleSelectedObject();
		if(selected == null)
			return false;
		
		return canDelete(selected);
	}

	private boolean canDelete(BaseObject selected)
	{
		if (Indicator.is(selected.getType()))
			return true;
		
		return selected.getType() == Task.getObjectType();
	}

	public void doIt() throws CommandFailedException
	{
		if (!isAvailable())
			return;
		
		BaseObject selected = getSingleSelectedObject();
		
		//TODO this might be a redundant test since isAvailable is testing same thing.  why is it here
		if(!canDelete(selected))
			return;
		
		try
		{
			deleteTask(selected);
			deleteIndicator(selected);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}

	private void deleteIndicator(BaseObject selected) throws Exception
	{
		if (!Indicator.is(selected.getType()))
			return;
		
		Vector commands = new Vector();
		ORefList ownerRefs = selected.findObjectsThatReferToUs();
		for (int refIndex = 0; refIndex < ownerRefs.size(); ++refIndex)
		{
			ORef ownerRef = ownerRefs.get(refIndex);
			BaseObject owner = getProject().findObject(ownerRef);
			if (Factor.isFactor(ownerRef))
				commands.add(DeleteAnnotationDoer.buildCommandToRemoveAnnotationFromObject(owner, Factor.TAG_INDICATOR_IDS, selected.getRef()));
			
			commands.addAll(DeleteAnnotationDoer.buildCommandsToDeleteKEAIndicators(getProject(), ownerRef));
		}
		
		commands.addAll(Arrays.asList(selected.createCommandsToClear()));
		commands.add(new CommandDeleteObject(selected.getRef()));
		getProject().executeCommandsAsTransaction(commands);
	}

	private void deleteTask(BaseObject selected) throws CommandFailedException
	{
		if (!Task.is(selected.getType()))
			return;
		
		Task selectedTaskToDelete = (Task) selected;
		if (shouldDeleteFromParentOnly(selectedTaskToDelete))
			DeleteActivity.deleteTaskWithUserConfirmation(getProject(), getSelectionHierarchy(), selectedTaskToDelete);
		else
			DeleteActivity.deleteTaskWithUserConfirmation(getProject(), selectedTaskToDelete.findObjectsThatReferToUs(), selectedTaskToDelete);
	}
	
	private boolean shouldDeleteFromParentOnly(Task selectedTaskToDelete)
	{
		ORefList referrers = selectedTaskToDelete.findObjectsThatReferToUs();
		ORefList selectionHierarchy = getSelectionHierarchy();
		
		return selectionHierarchy.containsAnyOf(referrers);
	}
}
