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

import org.martus.util.UnicodeWriter;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.migrations.MigrationResult;
import org.miradi.migrations.RawProject;
import org.miradi.migrations.RawProjectLoader;
import org.miradi.migrations.VersionRange;
import org.miradi.migrations.forward.MigrationManager;
import org.miradi.project.Project;
import org.miradi.project.ProjectSaver;
import org.miradi.project.RawProjectSaver;
import org.miradi.utils.GenericMiradiFileFilter;
import org.miradi.utils.ProgressInterface;
import org.miradi.utils.Xmpz2FileFilter;
import org.miradi.xml.AbstractXmlImporter;
import org.miradi.xml.generic.AbstractProjectImporter;
import org.miradi.xml.xmpz2.Xmpz2XmlImporter;

import java.io.File;

public class Xmpz2ProjectImporter extends AbstractProjectImporter
{
	public Xmpz2ProjectImporter(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}

	@Override
	protected AbstractXmlImporter createXmpzXmlImporter(Project projectToFill,	ProgressInterface progressIndicator) throws Exception
	{
		return new Xmpz2XmlImporter(projectToFill, progressIndicator);
	}

	@Override
	protected void createProject(File importFile, File newProjectFile, ProgressInterface progressIndicator) throws Exception
	{
		ImportXmlProjectResult importResult = importProject(importFile, progressIndicator);

		if (importResult.getProjectRequiresMigration())
			reverseMigrateProject(importResult.getProject(), newProjectFile);
		else
			ProjectSaver.saveProject(importResult.getProject(), newProjectFile);
	}

	private void reverseMigrateProject(Project project, File newProjectFile) throws Exception
	{
		String projectMpfSnapShot = ProjectSaver.createSnapShot(project);
		RawProject rawProjectToMigrate = RawProjectLoader.loadProject(projectMpfSnapShot);
		MigrationManager migrationManager = new MigrationManager();

		// reverse back to last migration prior to this version of Miradi...subsequent open project process will then forward migrate as necessary
		MigrationResult migrationResult = migrationManager.migrate(rawProjectToMigrate, new VersionRange(MigrationManager.LAST_MIGRATION_PRIOR_TO_CURRENT_RELEASE));

		if (migrationResult.cannotMigrate())
		{
			final String message = EAM.substituteSingleString(EAM.text("Unable to complete this migration.\n\n" +
					"Issues encountered:\n" +
					"%s"), migrationResult.getUserFriendlyGroupedCannotMigrateMessagesAsString());

			EAM.errorDialog(message);
		}
		if (migrationResult.didLoseData())
		{
			final String message = EAM.substituteSingleString(EAM.text("Unable to complete this migration.\n\n" +
					"Issues encountered:\n" +
					"%s"), migrationResult.getUserFriendlyGroupedDataLossMessagesAsString());

			EAM.errorDialog(message);
		}

		if (migrationResult.didFail())
		{
			EAM.errorDialog(EAM.text("Could not migrate!"));
		}

		String migratedRawProjectAsString = RawProjectSaver.saveProject(rawProjectToMigrate);
		UnicodeWriter fileWriter = new UnicodeWriter(newProjectFile);
		fileWriter.write(migratedRawProjectAsString);
		fileWriter.close();
	}

	@Override
	protected GenericMiradiFileFilter createFileFilter()
	{
		return new Xmpz2FileFilter();
	}
}
