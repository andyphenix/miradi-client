/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.views.targetviability.doers;

import java.util.Vector;

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Indicator;
import org.miradi.schemas.IndicatorSchema;

public class CreateKeyEcologicalAttributeMeasurementDoer extends AbstractKeyEcologicalAttributeDoer
{
	@Override
	public Vector<Integer> getRequiredObjectTypes()
	{
		Vector<Integer> types = new Vector<Integer>(1);
		types.add(IndicatorSchema.getObjectType());

		return types;
	}

	@Override
	protected void doIt() throws Exception
	{
		if (!isAvailable())
			return;
		
		try
		{
			Indicator indicator = (Indicator)getObjects()[0];
			insertMeasurement(indicator);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}

	private void insertMeasurement(Indicator indicator) throws Exception
	{
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			CommandCreateObject createMeasurement = new CommandCreateObject(ObjectType.MEASUREMENT);
			getProject().executeCommand(createMeasurement);
			ORef newMeasurementRef = createMeasurement.getObjectRef();
	
			CommandSetObjectData addMeasurement = CommandSetObjectData.createAppendORefCommand(indicator, Indicator.TAG_MEASUREMENT_REFS, newMeasurementRef);
			getProject().executeCommand(addMeasurement);
			
			getPicker().ensureOneCopyOfObjectSelectedAndVisible(newMeasurementRef);
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}	
	}
}
