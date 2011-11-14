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
package org.miradi.legacyprojects.migrations;

import java.io.File;


import org.json.JSONArray;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.legacyprojects.DataUpgrader;
import org.miradi.legacyprojects.JSONFile;
import org.miradi.legacyprojects.ObjectManifest;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.utils.EnhancedJsonObject;

public class ConvertHighLevelEstimatesIntoAssignments
{
	public static int convertToAssignments() throws Exception
	{	
		File jsonDir = DataUpgrader.getTopJsonDir();
		final int TASK_TYPE = 3;
		int convertedHightLevelEstimateCount = convertToAssignments(jsonDir, TASK_TYPE, "Details");
		
		final int INDICATOR_TYPE = 8;
		convertedHightLevelEstimateCount += convertToAssignments(jsonDir, INDICATOR_TYPE, "Detail");
		
		final int STRATEGY_TYPE = 21;
		convertedHightLevelEstimateCount += convertToAssignments(jsonDir, STRATEGY_TYPE, "Text");
		
		return convertedHightLevelEstimateCount;
	}

	private static int convertToAssignments(File jsonDir, final int objectType, String detailsTag) throws Exception
	{
		File objectDir = DataUpgrader.getObjectsDir(jsonDir, objectType);
		if (! objectDir.exists())
			return 0;
		
		File manifestFile = createManifestFile(objectDir);
		if (! manifestFile.exists())
			return 0;
		
		int convertedHightLevelEstimateCount = 0;
		ObjectManifest taskManifestObject = new ObjectManifest(JSONFile.read(manifestFile));
		BaseId[] ids = taskManifestObject.getAllKeys();
		for (int index = 0; index < ids.length; ++index)
		{
			BaseId id = ids[index];
			File objectFile = new File(objectDir, Integer.toString(id.asInt()));
			EnhancedJsonObject objectJson = DataUpgrader.readFile(objectFile);
			if (objectJson.optString("BudgetCostMode").equals("BudgetOverrideMode"))
			{
				createExpenseAssignment(jsonDir, objectFile, objectJson);
				createResourceAssignment(jsonDir, objectFile, objectJson);
				updateDetailsTextWithOverrideData(jsonDir, objectFile, objectJson, detailsTag);
				++convertedHightLevelEstimateCount;
			}
		}
		
		return convertedHightLevelEstimateCount;
	}

	private static void createExpenseAssignment(File jsonDir, File objectFile, EnhancedJsonObject objectJson) throws Exception
	{
		double costOverride = objectJson.optDouble("BudgetCostOverride");
		if (Double.isNaN(costOverride))
			return;
		
		final int EXPENSE_ASSIGNMENT_TYPE = 51;
		File expenseAssignmentDir = DataUpgrader.getObjectsDir(jsonDir, EXPENSE_ASSIGNMENT_TYPE);
		if (! expenseAssignmentDir.exists())
			DataUpgrader.createObjectsDir(jsonDir, EXPENSE_ASSIGNMENT_TYPE);

		EnhancedJsonObject expenseAssignmentJson = new EnhancedJsonObject();
		expenseAssignmentJson.put("Details", createSingleElementDateUnitEffortList(costOverride));
		int newlyCreatedId = createAssignment(jsonDir, expenseAssignmentDir, expenseAssignmentJson);
		
		ORefList currentExpenseAssignmentRefs = objectJson.optRefList("ExpenseRefs");
		currentExpenseAssignmentRefs.add(new ORef(EXPENSE_ASSIGNMENT_TYPE, new BaseId(newlyCreatedId)));
		objectJson.put("ExpenseRefs", currentExpenseAssignmentRefs.toString());
		DataUpgrader.writeJson(objectFile, objectJson);
	}

	private static void createResourceAssignment(File jsonDir, File objectFile, EnhancedJsonObject objectJson) throws Exception
	{
		ORefList whoOverrideRefs = objectJson.optRefList("WhoOverrideRefs");
		if (whoOverrideRefs.isEmpty())
			return;

		final int RESOURCE_ASSIGNMENT_TYPE =  14;
		File resourceAssignmentDir = DataUpgrader.getObjectsDir(jsonDir, RESOURCE_ASSIGNMENT_TYPE);
		if (! resourceAssignmentDir.exists())
			DataUpgrader.createObjectsDir(jsonDir, RESOURCE_ASSIGNMENT_TYPE);

		IdList newlyCreatedResourceAssignmentIds = objectJson.optIdList(RESOURCE_ASSIGNMENT_TYPE, "AssignmentIds");
		for (int index = 0; index < whoOverrideRefs.size(); ++index)
		{
			EnhancedJsonObject resourceAssignmentJson = new EnhancedJsonObject();
			resourceAssignmentJson.put("ResourceId", whoOverrideRefs.get(index).getObjectId().toString());
			resourceAssignmentJson.put("Details", createSingleElementDateUnitEffortList(0.0));
			int newlyCreatedId = createAssignment(jsonDir, resourceAssignmentDir, resourceAssignmentJson);
			newlyCreatedResourceAssignmentIds.add(new BaseId(newlyCreatedId));
		}
		
		objectJson.put("AssignmentIds", newlyCreatedResourceAssignmentIds.toString());
		DataUpgrader.writeJson(objectFile, objectJson);
	}
	
	private static void updateDetailsTextWithOverrideData(File jsonDir,	File objectFile, EnhancedJsonObject objectJson, String detailsTag) throws Exception
	{
		String originalDetailsText = objectJson.optString(detailsTag);
		String migrationDetialsText = EAM.text("Migrated High Level Estimate:");
		final String NEW_LINE = "\n";
		migrationDetialsText += NEW_LINE;
		migrationDetialsText += EAM.substitute(EAM.text("Budget Override was: %s"), getSafeBudgetCostOverride(objectJson));
		migrationDetialsText += NEW_LINE;
		migrationDetialsText += EAM.substitute(EAM.text("When Override was: %s"), createOverrideWhenString(objectJson));
		migrationDetialsText += NEW_LINE;
		migrationDetialsText += EAM.substitute(EAM.text("Who Override was: %s"), createAppendedResourceNames(jsonDir, objectJson));
		migrationDetialsText += NEW_LINE;
		migrationDetialsText += "---------------------------------------------------";
		migrationDetialsText += NEW_LINE;
		migrationDetialsText += originalDetailsText;
		
		objectJson.put(detailsTag, migrationDetialsText);
		DataUpgrader.writeJson(objectFile, objectJson);
	}

	private static String getSafeBudgetCostOverride(EnhancedJsonObject objectJson)
	{
		Double budgetCostOverride = objectJson.optDouble("BudgetCostOverride");
		if (budgetCostOverride.isNaN())
			return "";
		
		return Double.toString(budgetCostOverride);
	}

	private static String createOverrideWhenString(EnhancedJsonObject objectJson) throws Exception
	{
		EnhancedJsonObject whenOverrideJson = new EnhancedJsonObject(objectJson.optString("WhenOverride"));
		String startDateAsString = whenOverrideJson.optString("StartDate");
		String endDateAsString = whenOverrideJson.optString("EndDate");
		if (isEmpty(startDateAsString) || isEmpty(endDateAsString))
			return "";
		
		String overrideWhenDates = startDateAsString + " - " + endDateAsString;
		return overrideWhenDates;
	}

	private static boolean isEmpty(String string)
	{
		return string.length() == 0;
	}

	private static String createAppendedResourceNames(File jsonDir,	EnhancedJsonObject objectJson) throws Exception
	{
		ORefList whoOverrideRefs = objectJson.optRefList("WhoOverrideRefs");
		final int PROJECT_RESOURCE_TYPE = 7;
		File resourceDir = DataUpgrader.getObjectsDir(jsonDir, PROJECT_RESOURCE_TYPE);
		String appendedNames = "";
		for (int index = 0; index < whoOverrideRefs.size(); ++index)
		{
			Integer integer = whoOverrideRefs.get(index).getObjectId().asInt();
			File projectResourceFile = new File(resourceDir, Integer.toString(integer));
			EnhancedJsonObject projectResourceJson = new EnhancedJsonObject(DataUpgrader.readFile(projectResourceFile));
			String name = projectResourceJson.optString("Name");
			String surName = projectResourceJson.optString("SurName");
			String id = projectResourceJson.optString("Initials");
			if (index > 0)
				appendedNames += ", ";

			appendedNames += (id + " " + name + " " +surName);
		}

		return appendedNames;
	}

	private static EnhancedJsonObject getOrCreateExpenseManifestObject(File assignmentDir) throws Exception
	{
		File assignmentManifestFile = createManifestFile(assignmentDir);
		if (assignmentManifestFile.exists())
			return DataUpgrader.readFile(assignmentManifestFile);

		EnhancedJsonObject assignemtnManifestJson = new EnhancedJsonObject();
		assignemtnManifestJson.put("Type", "ObjectManifest");
		
		return assignemtnManifestJson;
	}
	
	private static int createAssignment(File jsonDir, File assignmentDir, EnhancedJsonObject assignmentJsonWithoutIdKey) throws Exception
	{
		int highestId = DataUpgrader.readHighestIdInProjectFile(jsonDir);
		int id = ++highestId;
		
		EnhancedJsonObject assignmentManifestJson = getOrCreateExpenseManifestObject(assignmentDir);
		assignmentManifestJson.put(Integer.toString(id), "true");
		assignmentJsonWithoutIdKey.put("Id", Integer.toString(id));
		
		File expenseAssignmentFile = new File(assignmentDir, Integer.toString(id));
		DataUpgrader.createFile(expenseAssignmentFile, assignmentJsonWithoutIdKey.toString());	
		DataUpgrader.writeHighestIdToProjectFile(jsonDir, id);
		File assignmentManifestFile = createManifestFile(assignmentDir);
		DataUpgrader.writeJson(assignmentManifestFile, assignmentManifestJson);
		
		return id;
	}
	
	private static File createManifestFile(File assignmentDir)
	{
		return new File(assignmentDir, "manifest");
	}
		
	private static EnhancedJsonObject createSingleElementDateUnitEffortList(double cost)
	{
		EnhancedJsonObject dateUnitJson = new EnhancedJsonObject();
		dateUnitJson.put("DateUnitCode", "");
		
		EnhancedJsonObject dateUnitEffortJson = new EnhancedJsonObject();
		dateUnitEffortJson.put("DateUnit", dateUnitJson);
		dateUnitEffortJson.put("NumberOfUnits", cost);
		
		EnhancedJsonObject dateUnitEffortListJson = new EnhancedJsonObject();
		JSONArray array = new JSONArray();
		array.put(dateUnitEffortJson);	
		dateUnitEffortListJson.put("DateUnitEfforts", array);
		
		return dateUnitEffortListJson;
	}
}
