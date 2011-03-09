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
package org.miradi.views.umbrella.doers;

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Task;
import org.miradi.views.ObjectsDoer;
import org.miradi.views.umbrella.DeleteActivityDoer;

public class DeleteMethodDoer extends ObjectsDoer
{
	@Override
	public boolean isAvailable()
	{
		if(getSelectedMethod() == null)
			return false;
		
		return true;
	}

	@Override
	protected void doIt() throws Exception
	{
		if(!isAvailable())
			return;
		
		DeleteActivityDoer.deleteTaskWithUserConfirmation(getProject(), getSelectionHierarchy(), getSelectedMethod());

	}

	public Task getSelectedMethod()
	{
		if(getSelectedHierarchies().length != 1)
			return null;
		
		ORef methodRef = getSelectedHierarchies()[0].getRefForType(Task.getObjectType());
		if(methodRef == null || methodRef.isInvalid())
			return null;
		
		return Task.find(getProject(), methodRef);
	}

}
