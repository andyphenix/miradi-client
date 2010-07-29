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

import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Task;


abstract public class AbstractTreeNodeTaskDoer extends AbstractTreeNodeDoer
{
	@Override
	public boolean isAvailable()
	{
		if(!super.isAvailable())
			return false;
		
		try
		{
			ORefList selecionHiearchy = getSelectionHierarchy();
			BaseObject parent = extractParentFromSelectionHiearchy(selecionHiearchy);
			if (parent == null)
				return false;

			if(!canBeParentOfTask(parent))
				return false;

			if(!childWouldBeVisible(Task.getChildTaskTypeCode(parent.getType())))
				return false;
			
			return true;
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return false;
		}
	}
	
	protected BaseObject extractParentFromSelectionHiearchy(ORefList selectionHiearchy)
	{
		for (int index = 0; index < selectionHiearchy.size(); ++index)
		{
			ORef objectRef = selectionHiearchy.get(index);
			if (objectRef.isInvalid())
				continue;
			
			BaseObject objectInHieararchy = BaseObject.find(getProject(), objectRef);
			//FIXME low, look at the FIXME in AbstractCreateTaskNodeDoer.  Sometimes a deleted
			//object will not get removed from the selection hiearchy in time.
			if (objectInHieararchy == null)
				continue;
			
			if (objectInHieararchy.getType() == getParentType())
				return objectInHieararchy;
		}
		
		return null;
	}
	
	protected boolean canBeParentOfTask(BaseObject selectedObject) throws Exception
	{
		return selectedObject.isType(getParentType());
	}
	
	abstract protected int getParentType();
}
