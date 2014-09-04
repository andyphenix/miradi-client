/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.ObjectTreeTableConfiguration;
import org.miradi.objects.ViewData;
import org.miradi.project.Project;
import org.miradi.schemas.ObjectTreeTableConfigurationSchema;
import org.miradi.utils.CodeList;
import org.miradi.views.ViewDoer;

abstract public class AbstractCreatePlanningViewConfigurationDoer extends ViewDoer
{
	@Override
	public boolean isAvailable()
	{	
		if (! isPlanningView())
			return false;
		
		return true;
	}

	@Override
	protected void doIt() throws Exception
	{
		if (! isAvailable())
			return;
		
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			createPlanningViewConfiguration();
			PlanningCustomizeDialogPopupDoer.showCustomizeDialog(getMainWindow());
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

	private void createPlanningViewConfiguration() throws Exception
	{
		CodeList visibleRowCodes = getVisibleRowCodes();
		CodeList visibleColumnCodes = getVisibleColumnCodes();
		
		createPlanningViewConfiguration(getProject(), visibleRowCodes, visibleColumnCodes);
	}

	public static void createPlanningViewConfiguration(Project projectToUse, CodeList visibleRowCodes, CodeList visibleColumnCodes)	throws Exception
	{
		String visibleRowsAsString = visibleRowCodes.toString();
		String visibleColsAsString = visibleColumnCodes.toString();
		
		CommandCreateObject createConfiguration = new CommandCreateObject(ObjectTreeTableConfigurationSchema.getObjectType());
		projectToUse.executeCommand(createConfiguration);
		
		ORef newConfigurationRef = createConfiguration.getObjectRef();
		CommandSetObjectData setVisibleRowsCommand = new CommandSetObjectData(newConfigurationRef, ObjectTreeTableConfiguration.TAG_ROW_CONFIGURATION, visibleRowsAsString);
		projectToUse.executeCommand(setVisibleRowsCommand);
		
		CommandSetObjectData setVisibleColsCommand = new CommandSetObjectData(newConfigurationRef, ObjectTreeTableConfiguration.TAG_COL_CONFIGURATION, visibleColsAsString);
		projectToUse.executeCommand(setVisibleColsCommand);
	
		ViewData viewData = projectToUse.getCurrentViewData();
		CommandSetObjectData selectCurrentConfiguration = new CommandSetObjectData(viewData.getRef(), ViewData.TAG_TREE_CONFIGURATION_REF, newConfigurationRef);
		projectToUse.executeCommand(selectCurrentConfiguration);
		
		CommandSetObjectData setConfigurationLabel = new CommandSetObjectData(newConfigurationRef, ObjectTreeTableConfiguration.TAG_LABEL, getConfigurationDefaultLabel(projectToUse));
		projectToUse.executeCommand(setConfigurationLabel);
	}

	public static String getConfigurationDefaultLabel(Project project)
	{
		return "[" + EAM.text("PlanningSubViewName|Custom") + " " + project.getPlanningViewConfigurationPool().size() + "]"; 
	}
	
	abstract protected CodeList getVisibleRowCodes() throws Exception;
	
	abstract protected CodeList getVisibleColumnCodes() throws Exception;
}
