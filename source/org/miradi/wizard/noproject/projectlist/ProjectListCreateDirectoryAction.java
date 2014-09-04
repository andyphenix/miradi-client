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
package org.miradi.wizard.noproject.projectlist;

import java.io.File;
import java.io.IOException;

import org.miradi.main.EAM;
import org.miradi.views.noproject.RenameOldProjectDoer;

public class ProjectListCreateDirectoryAction extends ProjectListAction
{
	public ProjectListCreateDirectoryAction(ProjectListTreeTable tableToUse)
	{
		super(tableToUse, EAM.text("Create Folder"));
	}

	@Override
	protected void updateEnabledState()
	{
		try
		{
			boolean newState = true;
			if(isProjectSelected())
				newState = false;
			if(isOldProjectSelected())
				newState = false;
			
			setEnabled(newState);
			if (getSelectedFile() == null)
				setEnabled(false);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			setEnabled(false);
		}
	}
	
	@Override
	protected void doWork() throws Exception
	{	
		String newFolderName = RenameOldProjectDoer.getLegalProjectNameFromUser(getMainWindow(), "New Folder");
		if (newFolderName == null)
			return;
		
		File newDirToCreate = new File(getSelectedFile(), newFolderName);
		if (!newDirToCreate.mkdirs())
			throw new IOException();
	}
}
