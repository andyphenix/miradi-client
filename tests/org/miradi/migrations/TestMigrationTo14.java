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

package org.miradi.migrations;

import org.miradi.migrations.forward.MigrationTo14;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.ViewData;
import org.miradi.project.ProjectForTesting;

public class TestMigrationTo14 extends AbstractTestMigration
{
	public TestMigrationTo14(String name)
	{
		super(name);
	}
	
	public void testClearingOfLegacyChoices() throws Exception
	{
		verifyMigratingSingleChoiceField("", MigrationTo14.LEGACY_RESOURCE_CHOICE);
		verifyMigratingSingleChoiceField("", MigrationTo14.LEGACY_FUTURE_STATUS_CHOICE);
		verifyMigratingSingleChoiceField("", "");
		verifyMigratingSingleChoiceField("RandomCode", "RandomCode");
	}

	private void verifyMigratingSingleChoiceField(final String expectedData, final String dataToMigrate) throws Exception
	{
		ORefList viewDataRefs = getProject().getViewPool().getRefList();
		ViewData viewData = ViewData.find(getProject(), viewDataRefs.getFirstElement());
		getProject().fillObjectUsingCommand(viewData, ViewData.TAG_PLANNING_SINGLE_LEVEL_CHOICE, dataToMigrate);
		final VersionRange fromRange = new VersionRange(getFromVersion());
		ProjectForTesting migratedProject = migrateProject(fromRange);
		String data = migratedProject.getObjectData(viewData.getRef(), ViewData.TAG_PLANNING_SINGLE_LEVEL_CHOICE);
		assertEquals("Incorrect data after migration?", expectedData, data);
	}

	@Override
	protected int getToVersion()
	{
		return MigrationTo14.VERSION_TO;
	}

	@Override
	protected int getFromVersion()
	{
		return MigrationTo14.VERSION_FROM;
	}
}
