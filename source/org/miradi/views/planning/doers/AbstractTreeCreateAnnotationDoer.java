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

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Factor;
import org.miradi.views.diagram.CreateAnnotationDoer;

abstract public class AbstractTreeCreateAnnotationDoer extends AbstractTreeNodeDoer
{
	@Override
	public boolean isAvailable()
	{
		try
		{
			//FIXME low, look at the FIXME in AbstractCreateTaskNodeDoer.  Sometimes a deleted
			//object will not get removed from the selection hierarchy in time.
			BaseObject selectedObject = getSingleSelectedObject();
			if (selectedObject == null)
				return false;

			if (!isCorrectOwner(selectedObject))
				return false;

			if(!childWouldBeVisible(getObjectName()))
				return false;

			return isAvailableForFactor((Factor) selectedObject);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return false;
		}
	}

	protected boolean isCorrectOwner(BaseObject selectedObject)
	{
		return Factor.isFactor(selectedObject);
	}
	
	@Override
	protected void doIt() throws Exception
	{
		if (!isAvailable())
			return;
		
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			BaseObject selectedObjectAsParent = getSingleSelectedObject();
			CommandCreateObject createAnnotationCommand = new CommandCreateObject(getAnnotationType());
			getProject().executeCommand(createAnnotationCommand);			

			CommandSetObjectData appendCommand = CreateAnnotationDoer.createAppendCommand(selectedObjectAsParent, createAnnotationCommand.getObjectRef(), getAnnotationTag());
			getProject().executeCommand(appendCommand);
			
			CreateAnnotationDoer.ensureObjectVisible(getPicker(), createAnnotationCommand.getObjectRef());
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
		finally 
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}
	
	abstract protected int getAnnotationType();
	
	abstract protected String getAnnotationTag();
	
	abstract protected boolean isAvailableForFactor(Factor factor);
	
	abstract protected String getObjectName();
}
