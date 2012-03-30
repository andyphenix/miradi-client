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
package org.miradi.views.diagram;

import java.util.Vector;

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Target;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.views.targetviability.doers.AbstractKeyEcologicalAttributeDoer;

public class CreateViabilityIndicatorDoer extends AbstractKeyEcologicalAttributeDoer
{	
	@Override
	public boolean isAvailable()
	{
		boolean superIsAvailable = super.isAvailable();
		if (!superIsAvailable)
			return false;
		
		ORefList[] selectedHierarchies = getSelectedHierarchies();
		if (selectedHierarchies.length != 1)
			return false;
		
		ORefList selectedHierarchy = selectedHierarchies[0];
		ORef keaRef = selectedHierarchy.getRefForType(KeyEcologicalAttributeSchema.getObjectType());
		if (!keaRef.isInvalid())
			return true;
		
		ORef targetRef = selectedHierarchy.getRefForType(TargetSchema.getObjectType());
		if (targetRef.isInvalid())
			return false;
		
		Target target = Target.find(getProject(), targetRef);
		if (target.isViabilityModeTNC())
			return false;
		
		return true;
	}
	
	@Override
	public Vector<Integer> getRequiredObjectTypes()
	{
		Vector<Integer> types = new Vector<Integer>();
		types.add(TargetSchema.getObjectType());
		types.add(KeyEcologicalAttributeSchema.getObjectType());

		return types;
	}

	@Override
	protected void doIt() throws Exception
	{
		if(!isAvailable())
			return;

		getProject().executeCommand(new CommandBeginTransaction());
		try
		{		
			BaseObject parentOfNewIndicator = getObjects()[0];
			CommandCreateObject create = new CommandCreateObject(ObjectType.INDICATOR);
			getProject().executeCommand(create);
			
			ORef createdRef = create.getObjectRef();
			CommandSetObjectData addChild = CommandSetObjectData.createAppendIdCommand(parentOfNewIndicator, getIndicatorListTag(parentOfNewIndicator), createdRef.getObjectId());
			getProject().executeCommand(addChild);
			
			getPicker().ensureOneCopyOfObjectSelectedAndVisible(createdRef);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}
}
