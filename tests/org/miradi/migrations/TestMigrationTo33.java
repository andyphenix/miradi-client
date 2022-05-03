/* 
Copyright 2005-2022, Foundations of Success, Bethesda, Maryland
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

package org.miradi.migrations;

import org.martus.util.MultiCalendar;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.migrations.forward.MigrationTo33;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.Strategy;
import org.miradi.project.Project;
import org.miradi.schemas.ExpenseAssignmentSchema;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.schemas.ResourceAssignmentSchema;
import org.miradi.schemas.TimeframeSchema;
import org.miradi.utils.DateUnitEffort;
import org.miradi.utils.DateUnitEffortList;


public class TestMigrationTo33 extends AbstractTestMigration
{
	public TestMigrationTo33(String name)
	{
		super(name);
	}

	public void testFieldsRemovedAfterReverseMigration() throws Exception
	{
		RawProject rawProject = reverseMigrate(new VersionRange(MigrationTo33.VERSION_TO));
		ORef metadataRef = new ORef(ProjectMetadataSchema.getObjectType(), new BaseId(rawProject.getProjectMetadataId()));

		RawObject rawMetadata = rawProject.findObject(metadataRef);
		assertNotNull(rawMetadata);
		assertFalse("Field should have been removed during reverse migration?", rawMetadata.containsKey(MigrationTo33.TAG_DAY_COLUMNS_VISIBILITY));
	}

	public void testFieldAddedAfterForwardMigrationNoAssignmentsHideDays() throws Exception
	{
		RawProject rawProject = reverseMigrate(new VersionRange(MigrationTo33.VERSION_TO));
		migrateProject(rawProject, new VersionRange(Project.VERSION_HIGH));

		ORef metadataRef = new ORef(ProjectMetadataSchema.getObjectType(), new BaseId(rawProject.getProjectMetadataId()));

		verifyObjectsHaveNoDayData(rawProject, ResourceAssignmentSchema.getObjectType());
		verifyObjectsHaveNoDayData(rawProject, ExpenseAssignmentSchema.getObjectType());
		verifyObjectsHaveNoDayData(rawProject, TimeframeSchema.getObjectType());

		RawObject rawMetadata = rawProject.findObject(metadataRef);
		assertNotNull(rawMetadata);
		assertEquals("Field should have been added during forward migration with (default) hide days setting?", rawMetadata.getData(MigrationTo33.TAG_DAY_COLUMNS_VISIBILITY), MigrationTo33.HIDE_DAY_COLUMNS_CODE);
	}

	public void testFieldAddedAfterForwardMigrationAssignmentsWithNoDaysHideDays() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		ResourceAssignment resourceAssignment = getProject().createAndPopulateResourceAssignment();

		DateUnitEffortList dateUnitEffortList = new DateUnitEffortList();
		dateUnitEffortList.add(getProject().createDateUnitEffort(2007, 2007, 0.0));

		getProject().fillObjectUsingCommand(resourceAssignment, ResourceAssignment.TAG_DATEUNIT_DETAILS, dateUnitEffortList.toJson().toString());
		IdList idList = new IdList(ResourceAssignmentSchema.getObjectType(), new BaseId[]{resourceAssignment.getId()});
		getProject().fillObjectUsingCommand(strategy, BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS, idList.toJson().toString());

		RawProject rawProject = reverseMigrate(new VersionRange(MigrationTo33.VERSION_TO));
		migrateProject(rawProject, new VersionRange(Project.VERSION_HIGH));

		ORef metadataRef = new ORef(ProjectMetadataSchema.getObjectType(), new BaseId(rawProject.getProjectMetadataId()));

		verifyObjectsHaveNoDayData(rawProject, ResourceAssignmentSchema.getObjectType());
		verifyObjectsHaveNoDayData(rawProject, ExpenseAssignmentSchema.getObjectType());
		verifyObjectsHaveNoDayData(rawProject, TimeframeSchema.getObjectType());

		RawObject rawMetadata = rawProject.findObject(metadataRef);
		assertNotNull(rawMetadata);
		assertEquals("Field should have been added during forward migration with (default) hide days setting?", rawMetadata.getData(MigrationTo33.TAG_DAY_COLUMNS_VISIBILITY), MigrationTo33.HIDE_DAY_COLUMNS_CODE);
	}

	public void testFieldAddedAfterForwardMigrationResourceAssignmentsWithDaysShowDays() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		ResourceAssignment resourceAssignment = getProject().createAndPopulateResourceAssignment();

		DateUnitEffortList dateUnitEffortList = new DateUnitEffortList();
		MultiCalendar cal = MultiCalendar.createFromGregorianYearMonthDay(2007, 12, 1);
		DateUnit dateUnit = new DateUnit(cal.toIsoDateString());
		dateUnitEffortList.add(new DateUnitEffort(dateUnit, 0.0));

		getProject().fillObjectUsingCommand(resourceAssignment, ResourceAssignment.TAG_DATEUNIT_DETAILS, dateUnitEffortList.toJson().toString());
		IdList idList = new IdList(ResourceAssignmentSchema.getObjectType(), new BaseId[]{resourceAssignment.getId()});
		getProject().fillObjectUsingCommand(strategy, BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS, idList.toJson().toString());

		RawProject rawProject = reverseMigrate(new VersionRange(MigrationTo33.VERSION_TO));
		migrateProject(rawProject, new VersionRange(Project.VERSION_HIGH));

		ORef metadataRef = new ORef(ProjectMetadataSchema.getObjectType(), new BaseId(rawProject.getProjectMetadataId()));

		verifyObjectsHaveDayData(rawProject, ResourceAssignmentSchema.getObjectType());
		verifyObjectsHaveNoDayData(rawProject, ExpenseAssignmentSchema.getObjectType());
		verifyObjectsHaveDayData(rawProject, TimeframeSchema.getObjectType());

		RawObject rawMetadata = rawProject.findObject(metadataRef);
		assertNotNull(rawMetadata);
		assertEquals("Field should have been added during forward migration with show days setting?", rawMetadata.getData(MigrationTo33.TAG_DAY_COLUMNS_VISIBILITY), MigrationTo33.SHOW_DAY_COLUMNS_CODE);
	}

	public void testFieldAddedAfterForwardMigrationExpenseAssignmentsWithDaysShowDays() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		ExpenseAssignment expenseAssignment = getProject().createAndPopulateExpenseAssignment();

		DateUnitEffortList dateUnitEffortList = new DateUnitEffortList();
		MultiCalendar cal = MultiCalendar.createFromGregorianYearMonthDay(2007, 12, 1);
		DateUnit dateUnit = new DateUnit(cal.toIsoDateString());
		dateUnitEffortList.add(new DateUnitEffort(dateUnit, 0.0));

		getProject().fillObjectUsingCommand(expenseAssignment, ExpenseAssignment.TAG_DATEUNIT_DETAILS, dateUnitEffortList.toJson().toString());
		IdList idList = new IdList(ExpenseAssignmentSchema.getObjectType(), new BaseId[]{expenseAssignment.getId()});
		getProject().fillObjectUsingCommand(strategy, BaseObject.TAG_EXPENSE_ASSIGNMENT_REFS, idList.toJson().toString());

		RawProject rawProject = reverseMigrate(new VersionRange(MigrationTo33.VERSION_TO));
		migrateProject(rawProject, new VersionRange(Project.VERSION_HIGH));

		ORef metadataRef = new ORef(ProjectMetadataSchema.getObjectType(), new BaseId(rawProject.getProjectMetadataId()));

		verifyObjectsHaveNoDayData(rawProject, ResourceAssignmentSchema.getObjectType());
		verifyObjectsHaveDayData(rawProject, ExpenseAssignmentSchema.getObjectType());
		verifyObjectsHaveNoDayData(rawProject, TimeframeSchema.getObjectType());

		RawObject rawMetadata = rawProject.findObject(metadataRef);
		assertNotNull(rawMetadata);
		assertEquals("Field should have been added during forward migration with show days setting?", rawMetadata.getData(MigrationTo33.TAG_DAY_COLUMNS_VISIBILITY), MigrationTo33.SHOW_DAY_COLUMNS_CODE);
	}

	private void verifyObjectsHaveNoDayData(RawProject rawProject, int objectType) throws Exception
	{
		RawPool rawPool = rawProject.getRawPoolForType(objectType);
		if (rawPool != null)
		{
			for(ORef ref : rawPool.keySet())
			{
				RawObject rawObject = rawPool.get(ref);
				if (rawObject != null && rawObject.containsKey(MigrationTo33.TAG_DATEUNIT_EFFORTS))
				{
					DateUnitEffortList rawObjectDateUnitEffortList = new DateUnitEffortList(rawObject.get(MigrationTo33.TAG_DATEUNIT_EFFORTS));

					for (int index = 0; index < rawObjectDateUnitEffortList.size(); ++index)
					{
						DateUnitEffort rawObjectDateUnitEffort = rawObjectDateUnitEffortList.getDateUnitEffort(index);
						assertFalse("Did not expect object to have day data", rawObjectDateUnitEffort.getDateUnit().isDay());
					}
				}
			}
		}
	}

	private void verifyObjectsHaveDayData(RawProject rawProject, int objectType) throws Exception
	{
		RawPool rawPool = rawProject.getRawPoolForType(objectType);
		if (rawPool != null)
		{
			for(ORef ref : rawPool.keySet())
			{
				RawObject rawObject = rawPool.get(ref);
				if (rawObject != null && rawObject.containsKey(MigrationTo33.TAG_DATEUNIT_EFFORTS))
				{
					DateUnitEffortList rawObjectDateUnitEffortList = new DateUnitEffortList(rawObject.get(MigrationTo33.TAG_DATEUNIT_EFFORTS));

					for (int index = 0; index < rawObjectDateUnitEffortList.size(); ++index)
					{
						DateUnitEffort rawObjectDateUnitEffort = rawObjectDateUnitEffortList.getDateUnitEffort(index);
						assertTrue("Did expect object to have day data", rawObjectDateUnitEffort.getDateUnit().isDay());
					}
				}
			}
		}
	}

	@Override
	protected int getFromVersion()
	{
		return MigrationTo33.VERSION_FROM;
	}
	
	@Override
	protected int getToVersion()
	{
		return MigrationTo33.VERSION_TO;
	}
}
