/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.miradi.exceptions.ValidationException;
import org.miradi.main.MainWindow;
import org.miradi.project.Project;
import org.miradi.project.ProjectSaver;
import org.miradi.utils.ProgressInterface;
import org.miradi.views.umbrella.AbstractZippedXmlImporter;

abstract public class AbstractXmpzProjectImporter extends AbstractZippedXmlImporter
{
	public AbstractXmpzProjectImporter(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}
	
	@Override
	protected void importProjectXml(Project projectToFill, ZipFile zipFile, InputStreamWithSeek projectAsInputStream, ProgressInterface progressIndicator) throws Exception
	{
		AbstractXmlImporter xmpzImporter = createXmpzXmlImporter(projectToFill, progressIndicator);
		xmpzImporter.importProject(projectAsInputStream);
	}

	@Override
	protected void createOrOpenProject(Project projectToFill, File projectFile) throws Exception
	{
		projectToFill.rawCreateorOpen();
	}
	
	@Override
	protected void createProject(File importFile, File homeDirectory, File newProjectFile, ProgressInterface progressIndicator) throws Exception
	{
		Project project = importProject(importFile, progressIndicator);
		ProjectSaver.saveProject(project, newProjectFile);
	}
	
	private Project importProject(File zipFileToImport, ProgressInterface progressIndicator) throws ZipException, IOException, Exception, ValidationException
	{
		ZipFile zipFile = new ZipFile(zipFileToImport);
		try
		{
			return importProjectFromXmlEntry(zipFile, progressIndicator);
		}
		finally
		{
			zipFile.close();
		}
	}
	
	abstract protected AbstractXmlImporter createXmpzXmlImporter(Project projectToFill,	ProgressInterface progressIndicator) throws Exception;
}
