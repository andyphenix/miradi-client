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

import java.io.File;

import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.views.MainWindowDoer;
import org.miradi.views.umbrella.CreateProjectDialog;
import org.miradi.wizard.noproject.projectlist.ProjectListTreeTable;

public class SaveProjectAsDoer extends MainWindowDoer
{
	@Override
	public boolean isAvailable()
	{
		return (getProject().isOpen());
	}

	@Override
	protected void doIt() throws Exception
	{
		if (!isAvailable())
			return;
		
		CreateProjectDialog saveDialog = new CreateProjectDialog(getMainWindow(), EAM.text("Save As..."), getProject().getFilename());
		if(!saveDialog.showSaveAsDialog())
			return;

		File chosenFile = saveDialog.getSelectedFile();
		try
		{
			File newProjectDir = saveAs(chosenFile);
			getMainWindow().closeProject();
			ProjectListTreeTable.doProjectOpen(getMainWindow(), newProjectDir);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException("Unexpected error during Save As: " + e);
		}
	}

	private File saveAs(File chosenFile) throws Exception
	{
		throw new RuntimeException("Save As is not supported yet");
	}

	private String getTrimmedFileName(File chosenFile)
	{
		return chosenFile.getName().trim();
	}
}
