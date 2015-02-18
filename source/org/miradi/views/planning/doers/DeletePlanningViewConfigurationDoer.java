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
package org.miradi.views.planning.doers;

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.ObjectTreeTableConfiguration;
import org.miradi.objects.ViewData;
import org.miradi.utils.CommandVector;

public class DeletePlanningViewConfigurationDoer extends AbstractPlanningViewConfigurationDoer
{
	@Override
	public boolean isAvailable()
	{
		if(getProject().getPlanningViewConfigurationPool().getORefList().size() < 2)
			return false;
		
		return super.isAvailable();
	}

	@Override
	protected void doIt() throws Exception
	{
		if (! isAvailable())
			return;
		
		getProject().executeCommand(new CommandBeginTransaction());
		try 
		{
			deleteConfiguration();
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
	}
	
	private void deleteConfiguration()
	{
		try
		{
			ViewData viewData = getProject().getCurrentViewData();
			ORef configurationRef = viewData.getORef(ViewData.TAG_TREE_CONFIGURATION_REF);
			
			selectOtherConfiguration(configurationRef);
			
			ObjectTreeTableConfiguration configuration = (ObjectTreeTableConfiguration) getProject().findObject(configurationRef);
			CommandVector commandsToDeleteChildrenAndConfiguration = configuration.createCommandsToDeleteChildrenAndObject();
			getProject().executeCommands(commandsToDeleteChildrenAndConfiguration);
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}
	
	protected void selectOtherConfiguration(ORef itemNotToSelect) throws Exception
	{
		ViewData viewData = getProject().getCurrentViewData();
		ORefList existing = getProject().getPlanningViewConfigurationPool().getORefList();
		int next = (existing.find(itemNotToSelect) + 1) % existing.size();
		ORef refAsSelection = existing.get(next);
		CommandSetObjectData setCurrentCustomPlanRef = new CommandSetObjectData(viewData.getRef(), ViewData.TAG_TREE_CONFIGURATION_REF, refAsSelection.toString());
		getProject().executeCommand(setCurrentCustomPlanRef);
	}
}
