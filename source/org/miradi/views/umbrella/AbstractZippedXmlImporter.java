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

package org.miradi.views.umbrella;

import java.io.File;

import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.project.Project;
import org.miradi.utils.GenericMiradiFileFilter;
import org.miradi.utils.MiradiZipFile;
import org.miradi.utils.ProgressInterface;

abstract public class AbstractZippedXmlImporter extends AbstractProjectImporter
{
	public AbstractZippedXmlImporter(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}
	
	protected Project importProjectFromXmlEntry(MiradiZipFile zipFile, ProgressInterface progressIndicator) throws Exception
	{
		Project projectToFill = createProjectToFill();

		InputStreamWithSeek projectAsInputStream = getProjectAsInputStream(zipFile);
		if (projectAsInputStream.available() == 0)
			throw new Exception(ExportCpmzDoer.PROJECT_XML_FILE_NAME + EAM.text(" was empty"));

		try
		{
			importProjectXml(projectToFill, zipFile, projectAsInputStream, progressIndicator);
		}
		finally
		{
			projectAsInputStream.close();
		}
		
		return projectToFill;
	}

	protected Project createProjectToFill() throws Exception
	{
		Project projectToFill = new Project();
		projectToFill.finishOpeningAfterLoad("[Imported]");
		return projectToFill;
	}
	
	@Override
	public GenericMiradiFileFilter[] getFileFilters()
	{
		return new GenericMiradiFileFilter[] {createFileFilter()};
	}

	abstract protected GenericMiradiFileFilter createFileFilter();
	
	abstract protected void createOrOpenProject(Project projectToFill, File projectFile) throws Exception;

	abstract protected void importProjectXml(Project projectToFill, MiradiZipFile zipFile, InputStreamWithSeek projectAsInputStream, ProgressInterface progressIndicator) throws Exception;
}
