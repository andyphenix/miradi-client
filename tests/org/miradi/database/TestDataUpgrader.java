/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.miradi.database;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.martus.util.DirectoryUtils;
import org.martus.util.UnicodeReader;
import org.miradi.database.DataUpgrader;
import org.miradi.database.JSONFile;
import org.miradi.database.ObjectManifest;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdAssigner;
import org.miradi.ids.IdList;
import org.miradi.main.EAMTestCase;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.Factor;
import org.miradi.objects.Measurement;
import org.miradi.objects.Stress;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.project.ProjectForTesting;
import org.miradi.utils.CodeList;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.PointList;

public class TestDataUpgrader extends EAMTestCase
{
	public TestDataUpgrader(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		tempDirectory = createTempDirectory();
		project = new ProjectForTesting(getName());
	}
	
	public void tearDown() throws Exception
	{
		DirectoryUtils.deleteEntireDirectoryTree(tempDirectory);
		project.close();
		project = null;
		super.tearDown();
	}
	
	public void testMigrateTooOldProject() throws Exception
	{
		File jsonDirectory = new File(tempDirectory, "json");
		jsonDirectory.mkdirs();
		
		File version = new File(jsonDirectory, "version");
		createFile(version, "{\"Version\":14}");
		DataUpgrader upgrader = new DataUpgrader(tempDirectory);
		try
		{
			upgrader.upgrade();
			fail("Should have thrown for version too old to migrate");
		}
		catch(DataUpgrader.MigrationTooOldException ignoreExpected)
		{
		}
	}
	
	public static EnhancedJsonObject makeOld(EnhancedJsonObject json)
	{
		final String TAG_NAME = "Name";

		json.put(TAG_NAME, json.get(Factor.TAG_LABEL));
		json.remove(Factor.TAG_LABEL);
		return json;

	}
	
	public void testUpgradeTo16WithNoObjects6Directory() throws Exception
	{
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();
		
		File projectFile = new File(jsonDir, "project");
		createFile(projectFile, "{\"HighestUsedNodeId\":90}");

		File diagramsDir =  new File(jsonDir, "diagrams");
		diagramsDir.mkdirs();
		
 		String allFactorInfos = " {\"Nodes\":{\"28\":{\"Size\":{\"Width\":120,\"Height\":60},\"WrappedId\":28,\"Location\":{\"Y\":270,\"X\":120},\"Id\":28},\"29\":{\"Size\":{\"Width\":151,\"Height\":60},\"WrappedId\":29,\"Location\":{\"Y\":15,\"X\":375},\"Id\":29}},\"Type\":\"Diagram\"}  ";
		File diagramMainFile = new File(diagramsDir, "main");
		createFile(diagramMainFile, allFactorInfos);

		DataUpgrader upgraderWithNoObjects6 = new DataUpgrader(tempDirectory);
		upgraderWithNoObjects6.upgradeToVersion16();
	}
	
	public void testUpgradeTo16WithObjects6DirectoryButNoManifest() throws Exception
	{
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();
		
		File projectFile = new File(jsonDir, "project");
		createFile(projectFile, "{\"HighestUsedNodeId\":90}");

		File diagramsDir =  new File(jsonDir, "diagrams");
		diagramsDir.mkdirs();
		
 		String allFactorInfos = " {\"Nodes\":{\"28\":{\"Size\":{\"Width\":120,\"Height\":60},\"WrappedId\":28,\"Location\":{\"Y\":270,\"X\":120},\"Id\":28},\"29\":{\"Size\":{\"Width\":151,\"Height\":60},\"WrappedId\":29,\"Location\":{\"Y\":15,\"X\":375},\"Id\":29}},\"Type\":\"Diagram\"}  ";
		File diagramMainFile = new File(diagramsDir, "main");
		createFile(diagramMainFile, allFactorInfos);

		File objects6Dir = new File(jsonDir, "objects-6");
		objects6Dir.mkdirs();
		
		DataUpgrader upgraderWithNoObjects6 = new DataUpgrader(tempDirectory);
		upgraderWithNoObjects6.upgradeToVersion16();
	}

	private File createObjectsDir(File parentDir, String dirName)
	{
		File objectsDir = new File(parentDir, dirName);
		objectsDir.mkdirs();
		return objectsDir;
	}
	
	public void testDeleteOrphanedTasks() throws Exception
	{
		File jsonDir = createJsonDir();
		
		String indicatorWithTask = "{\"RatingSource\":\"\",\"FutureStatusDetail\":\"\",\"IndicatorThresholds\":\"\",\"FutureStatusSummary\":\"\",\"BudgetCostOverride\":\"\",\"Comment\":\"\",\"ShortLabel\":\"\",\"MeasurementRefs\":\"\",\"Priority\":\"\",\"Status\":\"\",\"Detail\":\"\",\"FutureStatusRating\":\"\",\"TaskIds\":\"{\\\"Ids\\\":[24]}\",\"TimeStampModified\":\"1201628736266\",\"BudgetCostMode\":\"\",\"FutureStatusDate\":\"\",\"Label\":\"\",\"Id\":21,\"FutureStatusComment\":\"\",\"ProgressReportRefs\":\"\",\"ViabilityRatingsComment\":\"\"}";
		String strategyWithTask = "{\"ObjectiveIds\":\"\",\"IndicatorIds\":\"{\\\"Ids\\\":[21]}\",\"Type\":\"Intervention\",\"BudgetCostOverride\":\"\",\"Comment\":\"\",\"TaxonomyCode\":\"\",\"ShortLabel\":\"\",\"ImpactRating\":\"\",\"Status\":\"\",\"Text\":\"\",\"GoalIds\":\"\",\"TimeStampModified\":\"1201630619780\",\"ActivityIds\":\"{\\\"Ids\\\":[23]}\",\"BudgetCostMode\":\"\",\"FeasibilityRating\":\"\",\"KeyEcologicalAttributeIds\":\"\",\"Label\":\"New Strategy\",\"Id\":19,\"ProgressReportRefs\":\"\"}";
		
		String taskWithIndicatorParent = "{\"AssignmentIds\":\"\",\"TimeStampModified\":\"1201628736210\",\"BudgetCostOverride\":\"\",\"BudgetCostMode\":\"\",\"SubtaskIds\":\"\",\"Label\":\"\",\"Id\":24}";
		String taskWithStrategyParent= "{\"AssignmentIds\":\"\",\"TimeStampModified\":\"1201629793352\",\"BudgetCostOverride\":\"\",\"BudgetCostMode\":\"\",\"SubtaskIds\":\"\",\"Label\":\"with subs\",\"Id\":23}";
		String orphanTaskWithTaskChild = "{\"AssignmentIds\":\"\",\"TimeStampModified\":\"1201628740106\",\"BudgetCostOverride\":\"\",\"BudgetCostMode\":\"\",\"SubtaskIds\":\"{\\\"Ids\\\":[26]}\",\"Label\":\"\",\"Id\":25}";
		String taskWithOrphandeTaskParent = "{\"AssignmentIds\":\"\",\"TimeStampModified\":\"1201628740044\",\"BudgetCostOverride\":\"\",\"BudgetCostMode\":\"\",\"SubtaskIds\":\"\",\"Label\":\"\",\"Id\":26}";
		
		int[] indicatorIds = {19, };
		File indicatorDir = DataUpgrader.createObjectsDir(jsonDir, 8);
		File indicatorManifestFile = createManifestFile(indicatorDir, indicatorIds);
		assertTrue(indicatorManifestFile.exists());
		
		int[] strategyIds = {21, };
		File strategyDir = DataUpgrader.createObjectsDir(jsonDir, 4);
		File strategyManifestFile = createManifestFile(strategyDir, strategyIds);
		assertTrue(strategyManifestFile.exists());
		
		int[] taskIds = {23, 24, 25, 26,};
		File taskDir = DataUpgrader.createObjectsDir(jsonDir, 3);
		File taskManifestFile = createManifestFile(taskDir, taskIds);
		assertTrue(taskManifestFile.exists());
		
		createObjectFile(indicatorWithTask, indicatorIds[0], indicatorDir);
		createObjectFile(strategyWithTask, strategyIds[0], strategyDir);
		createObjectFile(taskWithStrategyParent, taskIds[0], taskDir);
		createObjectFile(taskWithIndicatorParent, taskIds[1], taskDir);
		createObjectFile(orphanTaskWithTaskChild, taskIds[2], taskDir);
		createObjectFile(taskWithOrphandeTaskParent, taskIds[3], taskDir);
		
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		dataUpgrader.upgradeToVersion34();
		
		File deletedOrphandTask = new File(taskDir, Integer.toString(taskIds[2]));
		assertFalse("orphan task not deleted?", deletedOrphandTask.exists());
		
		File deletedTaskWithOrphandParent = new File(taskDir, Integer.toString(taskIds[3]));
		assertFalse("task with orphand parent not deleted?", deletedTaskWithOrphandParent.exists());
		
		File taskWithStrategyParentFile = new File(taskDir, Integer.toString(taskIds[0]));
		assertTrue("deleted task with parent?", taskWithStrategyParentFile.exists());
		
		File taskWithIndicatorParentFile = new File(taskDir, Integer.toString(taskIds[1]));
		assertTrue("deleted task with parent?", taskWithIndicatorParentFile.exists());
		
		ObjectManifest taskManifestObject = new ObjectManifest(JSONFile.read(taskManifestFile));
		assertEquals("wrong key count?", 2, taskManifestObject.getAllKeys().length);
		
		final int TASK_TYPE = 3;
		IdList ownedTaskIdList = new IdList(TASK_TYPE, taskManifestObject.getAllKeys());
		assertTrue("manifest does not contain owned task?", ownedTaskIdList.contains(new BaseId(taskIds[0])));
		assertTrue("manifest does not contain owned task?", ownedTaskIdList.contains(new BaseId(taskIds[1])));
	}

	private void createObjectFile(String jsonAsString, int id, File dir) throws Exception
	{
		File objectFile = new File(dir, Integer.toString(id));
		createFile(objectFile, jsonAsString);
		assertTrue(objectFile.exists());
	}
	
	public void testRemoveDuplicateBendPoints() throws Exception
	{
		File jsonDir = createJsonDir();
			
		String noDuplicateBendPointDiagramLink = "{\"FromDiagramFactorId\":1,\"ToDiagramFactorId\":2,\"TimeStampModified\":\"1197311302307\",\"Label\":\"\",\"Id\":90,\"WrappedLinkId\":1,\"BendPoints\":\"{\\\"Points\\\":[\\\"{\\\\\\\"Y\\\\\\\":285,\\\\\\\"X\\\\\\\":390}\\\",\\\"{\\\\\\\"Y\\\\\\\":285,\\\\\\\"X\\\\\\\":585}\\\"]}\"}";
		String duplicateBendPointDiagramLink   = "{\"FromDiagramFactorId\":2,\"ToDiagramFactorId\":3,\"TimeStampModified\":\"1197311519795\",\"Label\":\"\",\"Id\":91,\"WrappedLinkId\":2,\"BendPoints\":\"{\\\"Points\\\":[\\\"{\\\\\\\"Y\\\\\\\":285,\\\\\\\"X\\\\\\\":390}\\\",\\\"{\\\\\\\"Y\\\\\\\":285,\\\\\\\"X\\\\\\\":405}\\\",\\\"{\\\\\\\"Y\\\\\\\":285,\\\\\\\"X\\\\\\\":405}\\\"]}\"}";
		String noBendPointsDiagramLink   = "{\"FromDiagramFactorId\":2,\"ToDiagramFactorId\":3,\"TimeStampModified\":\"1197311519795\",\"Label\":\"\",\"Id\":92,\"WrappedLinkId\":2}";
		File diagramLinkDir = DataUpgrader.createObjectsDir(jsonDir, 13);
		int[] diagramLinkIds = {90, 91, 92};
		File diagramLinkManifestFile = createManifestFile(diagramLinkDir, diagramLinkIds);
		assertTrue(diagramLinkManifestFile.exists());
		
		File noDuplicateDiagramLinkFile = new File(diagramLinkDir, Integer.toString(diagramLinkIds[0]));
		createFile(noDuplicateDiagramLinkFile, noDuplicateBendPointDiagramLink);
		assertTrue(noDuplicateDiagramLinkFile.exists());

		File duplicateBendPointDiagramLinkFile = new File(diagramLinkDir, Integer.toString(diagramLinkIds[1]));
		createFile(duplicateBendPointDiagramLinkFile, duplicateBendPointDiagramLink);
		assertTrue(duplicateBendPointDiagramLinkFile.exists());

		File noBendPointsDiagramLinkFile = new File(diagramLinkDir, Integer.toString(diagramLinkIds[2]));
		createFile(noBendPointsDiagramLinkFile, noBendPointsDiagramLink);
		assertTrue(noBendPointsDiagramLinkFile.exists());
		
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		dataUpgrader.upgradeToVersion27();
				
		File diagramLinkWithOldDuplicateBendPoints = new File(diagramLinkDir, "91");
		assertTrue(" Diagram link with duplicate bend points file does not exist?", diagramLinkWithOldDuplicateBendPoints.exists());
		EnhancedJsonObject noDuplicateDiagramLinkJson = DataUpgrader.readFile(diagramLinkWithOldDuplicateBendPoints);
		PointList bendPoints1 = new PointList(noDuplicateDiagramLinkJson.getString("BendPoints"));
		assertEquals("wrong bendpoints?", 2, bendPoints1.size());
		assertTrue("does not contain bendpoint?", bendPoints1.contains(new Point(390, 285)));
		assertTrue("does not contain bendpoint?", bendPoints1.contains(new Point(405, 285)));
		
		File neverHadDuplicateBendPointsDiagramLink = new File(diagramLinkDir, "90");
		assertTrue(" Diagram link without duplicate bend points file does not exist?", neverHadDuplicateBendPointsDiagramLink.exists());
		EnhancedJsonObject neverHadDuplicateBendPointsLinkJson = DataUpgrader.readFile(neverHadDuplicateBendPointsDiagramLink);
		PointList bendPoints2 = new PointList(neverHadDuplicateBendPointsLinkJson.getString("BendPoints"));
		assertEquals("wrong bendpoints?", 2, bendPoints2.size());
		assertTrue("does not contain bendpoint?", bendPoints2.contains(new Point(585, 285)));
		assertTrue("does not contain bendpoint?", bendPoints2.contains(new Point(390, 285)));
	}

	public void testCopyTncEcoRegionFieldOverToDividedTerrestrailMarineFreshwaterEcoRegions() throws Exception
	{
		String metaDataWithOldEcoRegion = "{\"FiscalYearStart\":\"\",\"OtherOrgRegionalOffice\":\"\",\"BudgetSecuredPercent\":\"\",\"TNC.DatabaseDownloadDate\":\"\",\"Countries\":\"\",\"StartDate\":\"\",\"Municipalities\":\"\",\"BudgetCostMode\":\"\",\"LegislativeDistricts\":\"\",\"DiagramFontFamily\":\"\",\"KeyFundingSources\":\"\",\"TotalBudgetForFunding\":\"\",\"LocationDetail\":\"\",\"TNC.LessonsLearned\":\"\",\"OtherOrgManagingOffice\":\"\",\"ProjectName\":\"\",\"DiagramFontSize\":\"\",\"ProjectLatitude\":\"0.0\",\"CurrencyType\":\"\",\"TNC.Country\":\"\",\"TNC.Ecoregion\":\"Aceh, Amazonia Marine, where, Alaska Range\",\"LocationComments\":\"\",\"ProjectLongitude\":\"0.0\",\"Id\":0,\"ScopeComments\":\"\",\"ExpectedEndDate\":\"\",\"CurrencySymbol\":\"$\",\"StateAndProvinces\":\"\",\"OtherOrgProjectNumber\":\"\",\"CurrencyDecimalPlaces\":\"\",\"FinancialComments\":\"\",\"TNC.PlanningTeamComment\":\"\",\"CurrentWizardScreenName\":\"\",\"WorkPlanEndDate\":\"\",\"ProjectDescription\":\"\",\"ThreatRatingMode\":\"\",\"PlanningComments\":\"\",\"TNC.OperatingUnitsField\":\"{\\\"Codes\\\":[\\\"CHINA\\\",\\\"AUSTR\\\"]}\",\"ProjectURL\":\"\",\"WorkPlanTimeUnit\":\"YEARLY\",\"OtherOrgRelatedProjects\":\"\",\"ProjectScope\":\"\",\"TNC.SizeInHectares\":\"\",\"TNC.WorkbookVersionNumber\":\"\",\"BudgetCostOverride\":\"\",\"DataEffectiveDate\":\"\",\"ShortProjectVision\":\"\",\"ProjectVision\":\"\",\"WorkPlanStartDate\":\"\",\"ShortProjectScope\":\"\",\"TimeStampModified\":\"1199924536523\",\"ProjectAreaNote\":\"\",\"TNC.WorkbookVersionDate\":\"\",\"Label\":\"\",\"ProjectArea\":\"\",\"TNC.OperatingUnits\":\"China, Australia, home,  \"}";
		File jsonDir = createJsonDir();
		
		File projectMetaDataDir = DataUpgrader.createObjectsDir(jsonDir, 11);
		int[] projectMetaDataIds = {0, };
		File projectMetaDataManifestFile = createManifestFile(projectMetaDataDir, projectMetaDataIds);
		assertTrue(projectMetaDataManifestFile.exists());
		
		File metaDataFileWithOldEcoRegion = new File(projectMetaDataDir, Integer.toString(projectMetaDataIds[0]));
		createFile(metaDataFileWithOldEcoRegion, metaDataWithOldEcoRegion);
		assertTrue("project meta data file exists?", metaDataFileWithOldEcoRegion.exists());
		
		EnhancedJsonObject projectMetaDataJson1 = DataUpgrader.readFile(metaDataFileWithOldEcoRegion);	
		String ecoRegionString = projectMetaDataJson1.optString("TNC.Ecoregion");
		assertEquals("wrong number of codes?", "Aceh, Amazonia Marine, where, Alaska Range", ecoRegionString);
		
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		boolean isNonBlankEcoRegionField = dataUpgrader.copyTncEcoRegionFieldOverToDividedTerrestrailMarineFreshwaterEcoRegions();
		assertTrue("had non blank old eco region field?", isNonBlankEcoRegionField);
		
		EnhancedJsonObject projectMetaDataJson = DataUpgrader.readFile(metaDataFileWithOldEcoRegion);
		assertEquals("wrong id?", new BaseId(0), projectMetaDataJson.getId("Id"));
		
		CodeList newTerrestrialEcoRegions= new CodeList(projectMetaDataJson.getString("TNC.TerrestrialEcoRegion"));
		assertEquals("wrong number of terrestrial eco regions?", 1, newTerrestrialEcoRegions.size());
		
		CodeList newMarineEcoRegions= new CodeList(projectMetaDataJson.getString("TNC.MarineEcoRegion"));
		assertEquals("wrong number of marine eco regions?", 1, newMarineEcoRegions.size());
		
		CodeList newFreshwaterEcoRegions= new CodeList(projectMetaDataJson.getString("TNC.FreshwaterEcoRegion"));
		assertEquals("wrong number of freshwater eco regions?", 1, newFreshwaterEcoRegions.size());
	}
	
	public void testWithAndWithoutOperatingUnitsFieldBeingCopiedToNewPickList() throws Exception
	{
		String projectMetaDataWithNoOperatingUnitsField = "{\"FiscalYearStart\":\"\",\"OtherOrgRegionalOffice\":\"\",\"BudgetSecuredPercent\":\"\",\"TNC.DatabaseDownloadDate\":\"\",\"Countries\":\"\",\"StartDate\":\"\",\"Municipalities\":\"\",\"BudgetCostMode\":\"\",\"LegislativeDistricts\":\"\",\"DiagramFontFamily\":\"\",\"KeyFundingSources\":\"\",\"TotalBudgetForFunding\":\"\",\"LocationDetail\":\"\",\"TNC.LessonsLearned\":\"\",\"OtherOrgManagingOffice\":\"\",\"ProjectName\":\"\",\"DiagramFontSize\":\"\",\"ProjectLatitude\":\"0.0\",\"CurrencyType\":\"\",\"TNC.Country\":\"\",\"TNC.Ecoregion\":\"\",\"LocationComments\":\"\",\"ProjectLongitude\":\"0.0\",\"Id\":0,\"ScopeComments\":\"\",\"ExpectedEndDate\":\"\",\"CurrencySymbol\":\"$\",\"StateAndProvinces\":\"\",\"OtherOrgProjectNumber\":\"\",\"CurrencyDecimalPlaces\":\"\",\"FinancialComments\":\"\",\"TNC.PlanningTeamComment\":\"\",\"TNC.FreshwaterEcoRegion\":\"\",\"CurrentWizardScreenName\":\"\",\"TNC.TerrestrialEcoRegion\":\"\",\"WorkPlanEndDate\":\"\",\"ProjectDescription\":\"\",\"ThreatRatingMode\":\"\",\"PlanningComments\":\"\",\"ProjectURL\":\"\",\"WorkPlanTimeUnit\":\"YEARLY\",\"OtherOrgRelatedProjects\":\"\",\"ProjectScope\":\"\",\"TNC.SizeInHectares\":\"\",\"TNC.WorkbookVersionNumber\":\"\",\"BudgetCostOverride\":\"\",\"DataEffectiveDate\":\"\",\"ShortProjectVision\":\"\",\"ProjectVision\":\"\",\"WorkPlanStartDate\":\"\",\"TNC.MarineEcoRegion\":\"\",\"ShortProjectScope\":\"\",\"TimeStampModified\":\"1199980890755\",\"ProjectAreaNote\":\"\",\"TNC.WorkbookVersionDate\":\"\",\"Label\":\"\",\"ProjectArea\":\"\"}";
		verifyCopyTncOperatingUnitsFieldDataOverToNewPickListField(projectMetaDataWithNoOperatingUnitsField, "", false, 0);
		
		String projectMetaDataWithOldTncOperatingUnitsField = "{\"FiscalYearStart\":\"\",\"OtherOrgRegionalOffice\":\"\",\"BudgetSecuredPercent\":\"\",\"TNC.DatabaseDownloadDate\":\"\",\"Countries\":\"\",\"StartDate\":\"\",\"Municipalities\":\"\",\"BudgetCostMode\":\"\",\"LegislativeDistricts\":\"\",\"DiagramFontFamily\":\"\",\"KeyFundingSources\":\"\",\"TotalBudgetForFunding\":\"\",\"LocationDetail\":\"\",\"TNC.LessonsLearned\":\"\",\"OtherOrgManagingOffice\":\"\",\"ProjectName\":\"\",\"DiagramFontSize\":\"\",\"ProjectLatitude\":\"0.0\",\"CurrencyType\":\"\",\"TNC.Country\":\"\",\"TNC.Ecoregion\":\"\",\"LocationComments\":\"\",\"ProjectLongitude\":\"0.0\",\"Id\":0,\"ScopeComments\":\"\",\"ExpectedEndDate\":\"\",\"CurrencySymbol\":\"$\",\"StateAndProvinces\":\"\",\"OtherOrgProjectNumber\":\"\",\"CurrencyDecimalPlaces\":\"\",\"FinancialComments\":\"\",\"TNC.PlanningTeamComment\":\"\",\"CurrentWizardScreenName\":\"\",\"WorkPlanEndDate\":\"\",\"ProjectDescription\":\"\",\"ThreatRatingMode\":\"\",\"PlanningComments\":\"\",\"ProjectURL\":\"\",\"WorkPlanTimeUnit\":\"YEARLY\",\"OtherOrgRelatedProjects\":\"\",\"ProjectScope\":\"\",\"TNC.SizeInHectares\":\"\",\"TNC.WorkbookVersionNumber\":\"\",\"BudgetCostOverride\":\"\",\"DataEffectiveDate\":\"\",\"ShortProjectVision\":\"\",\"ProjectVision\":\"\",\"WorkPlanStartDate\":\"\",\"ShortProjectScope\":\"\",\"TimeStampModified\":\"1199918933796\",\"ProjectAreaNote\":\"\",\"TNC.WorkbookVersionDate\":\"\",\"Label\":\"\",\"ProjectArea\":\"\",\"TNC.OperatingUnits\":\"China, Australia, home,  \"}";
		verifyCopyTncOperatingUnitsFieldDataOverToNewPickListField(projectMetaDataWithOldTncOperatingUnitsField, "China, Australia, home,  ", true, 2);
		
		String projectMetaDataWithBlamkTncOperatingUnitsField = "{\"FiscalYearStart\":\"\",\"OtherOrgRegionalOffice\":\"\",\"BudgetSecuredPercent\":\"\",\"TNC.DatabaseDownloadDate\":\"\",\"Countries\":\"\",\"StartDate\":\"\",\"Municipalities\":\"\",\"BudgetCostMode\":\"\",\"LegislativeDistricts\":\"\",\"DiagramFontFamily\":\"\",\"KeyFundingSources\":\"\",\"TotalBudgetForFunding\":\"\",\"LocationDetail\":\"\",\"TNC.LessonsLearned\":\"\",\"OtherOrgManagingOffice\":\"\",\"ProjectName\":\"\",\"DiagramFontSize\":\"\",\"ProjectLatitude\":\"0.0\",\"CurrencyType\":\"\",\"TNC.Country\":\"\",\"TNC.Ecoregion\":\"\",\"LocationComments\":\"\",\"ProjectLongitude\":\"0.0\",\"Id\":0,\"ScopeComments\":\"\",\"ExpectedEndDate\":\"\",\"CurrencySymbol\":\"$\",\"StateAndProvinces\":\"\",\"OtherOrgProjectNumber\":\"\",\"CurrencyDecimalPlaces\":\"\",\"FinancialComments\":\"\",\"TNC.PlanningTeamComment\":\"\",\"CurrentWizardScreenName\":\"\",\"WorkPlanEndDate\":\"\",\"ProjectDescription\":\"\",\"ThreatRatingMode\":\"\",\"PlanningComments\":\"\",\"ProjectURL\":\"\",\"WorkPlanTimeUnit\":\"YEARLY\",\"OtherOrgRelatedProjects\":\"\",\"ProjectScope\":\"\",\"TNC.SizeInHectares\":\"\",\"TNC.WorkbookVersionNumber\":\"\",\"BudgetCostOverride\":\"\",\"DataEffectiveDate\":\"\",\"ShortProjectVision\":\"\",\"ProjectVision\":\"\",\"WorkPlanStartDate\":\"\",\"ShortProjectScope\":\"\",\"TimeStampModified\":\"1199918933796\",\"ProjectAreaNote\":\"\",\"TNC.WorkbookVersionDate\":\"\",\"Label\":\"\",\"ProjectArea\":\"\",\"TNC.OperatingUnits\":\"\"}";
		verifyCopyTncOperatingUnitsFieldDataOverToNewPickListField(projectMetaDataWithBlamkTncOperatingUnitsField, "", false, 0);
	}
	
	public void verifyCopyTncOperatingUnitsFieldDataOverToNewPickListField(String projectMetaDataWithOldTncOperatingUnitsField, String expectedCodesAsString, boolean expectedNonBlank, int expectedNumberOfOperatingUnits) throws Exception
	{
		File jsonDir = createJsonDir();
		File projectMetaDataDir = DataUpgrader.createObjectsDir(jsonDir, 11);
		int[] projectMetaDataIds = {0, };
		File projectMetaDataManifestFile = createManifestFile(projectMetaDataDir, projectMetaDataIds);
		assertTrue(projectMetaDataManifestFile.exists());
		
		File projectMetaDataFileWithOldTncOperatingUnits = new File(projectMetaDataDir, Integer.toString(projectMetaDataIds[0]));
		createFile(projectMetaDataFileWithOldTncOperatingUnits, projectMetaDataWithOldTncOperatingUnitsField);
		assertTrue("project meta data file exists?", projectMetaDataFileWithOldTncOperatingUnits.exists());
		
		EnhancedJsonObject projectMetaDataJson1 = DataUpgrader.readFile(projectMetaDataFileWithOldTncOperatingUnits);	
		String operatingUnitsAsString = projectMetaDataJson1.optString("TNC.OperatingUnits");
		assertEquals("wrong number of codes?", expectedCodesAsString, operatingUnitsAsString);
	
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		boolean isNonBlank = dataUpgrader.copyTncOperatingUnitsFieldDataOverToNewPickListField();
		assertEquals("had non blank old operating units field?", expectedNonBlank, isNonBlank);
		
		EnhancedJsonObject projectMetaDataJson = DataUpgrader.readFile(projectMetaDataFileWithOldTncOperatingUnits);
		assertEquals("wrong id?", new BaseId(0), projectMetaDataJson.getId("Id"));		
		CodeList newOperatingUnits = new CodeList(projectMetaDataJson.getString("TNC.OperatingUnitsField"));
		assertEquals("wrong number of Operating units?", expectedNumberOfOperatingUnits, newOperatingUnits.size());
	}
	
	public void testCopyTncProjectDataSizeInHectaresFieldOverToProjectMetaDataProjectAreaField() throws Exception
	{
		String projectMetaDataWithTncSizeInHectars = "{\"FiscalYearStart\":\"\",\"BudgetSecuredPercent\":\"1.0\",\"TNC.DatabaseDownloadDate\":\"\",\"Countries\":\"\",\"StartDate\":\"2008-01-04\",\"Municipalities\":\"\",\"BudgetCostMode\":\"\",\"LegislativeDistricts\":\"\",\"DiagramFontFamily\":\"\",\"KeyFundingSources\":\"\",\"TotalBudgetForFunding\":\"1.0\",\"LocationDetail\":\"\",\"TNC.LessonsLearned\":\"\",\"ProjectName\":\"my project name for wwf\",\"DiagramFontSize\":\"\",\"ProjectLatitude\":\"0.0\",\"CurrencyType\":\"EUR\",\"TNC.Country\":\"\",\"TNC.Ecoregion\":\"\",\"LocationComments\":\"\",\"ProjectLongitude\":\"0.0\",\"ScopeComments\":\"\",\"Id\":0,\"ExpectedEndDate\":\"2008-01-04\",\"CurrencySymbol\":\"E\",\"StateAndProvinces\":\"\",\"CurrencyDecimalPlaces\":\"\",\"FinancialComments\":\"\",\"TNC.PlanningTeamComment\":\"\",\"CurrentWizardScreenName\":\"SummaryWizardDefineTeamMembers\",\"WorkPlanEndDate\":\"\",\"ProjectDescription\":\"\",\"ThreatRatingMode\":\"\",\"PlanningComments\":\"\",\"ProjectURL\":\"\",\"WorkPlanTimeUnit\":\"YEARLY\",\"ProjectScope\":\"\",\"TNC.SizeInHectares\":\"99999.0\",\"TNC.WorkbookVersionNumber\":\"\",\"BudgetCostOverride\":\"\",\"DataEffectiveDate\":\"\",\"ShortProjectVision\":\"\",\"ProjectVision\":\"vision text 101\",\"WorkPlanStartDate\":\"\",\"TimeStampModified\":\"1199481834101\",\"ShortProjectScope\":\"\",\"ProjectAreaNote\":\"\",\"TNC.WorkbookVersionDate\":\"\",\"Label\":\"\",\"ProjectArea\":\"\",\"TNC.OperatingUnits\":\"\"}";
		File jsonDir = createJsonDir();
		
		File projectMetaDataDir = DataUpgrader.createObjectsDir(jsonDir, 11);
		int[] projectMetaDataIds = {0, };
		File projectMetaDataManifestFile = createManifestFile(projectMetaDataDir, projectMetaDataIds);
		assertTrue(projectMetaDataManifestFile.exists());
		
		File projectMetaDataFileWithTncSizeInHectars = new File(projectMetaDataDir, Integer.toString(projectMetaDataIds[0]));
		createFile(projectMetaDataFileWithTncSizeInHectars, projectMetaDataWithTncSizeInHectars);
		
		EnhancedJsonObject projectMetaDataJson1 = DataUpgrader.readFile(projectMetaDataFileWithTncSizeInHectars);	
		assertEquals("wrong number project area?", "", projectMetaDataJson1.optString("ProjectArea"));
	
		
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		dataUpgrader.copyTncProjectDataSizeInHectaresFieldOverToProjectMetaDataProjectAreaField();
		
		assertTrue("project meta data file exists?", projectMetaDataFileWithTncSizeInHectars.exists());
		EnhancedJsonObject projectMetaDataJson = DataUpgrader.readFile(projectMetaDataFileWithTncSizeInHectars);
		assertEquals("wrong id?", new BaseId(0), projectMetaDataJson.getId("Id"));		
		assertEquals("wrong number project area?", "99999.0", projectMetaDataJson.optString("ProjectArea"));
	}
	
	public void testIsTncCountryCodeBlank() throws Exception
	{
		String projectMetaDataWithoutTncCountries = "{\"FiscalYearStart\":\"\",\"BudgetSecuredPercent\":\"1.0\",\"TNC.DatabaseDownloadDate\":\"\",\"Countries\":\"\",\"StartDate\":\"2008-01-04\",\"Municipalities\":\"\",\"BudgetCostMode\":\"\",\"LegislativeDistricts\":\"\",\"DiagramFontFamily\":\"\",\"KeyFundingSources\":\"\",\"TotalBudgetForFunding\":\"1.0\",\"LocationDetail\":\"\",\"TNC.LessonsLearned\":\"\",\"ProjectName\":\"my project name for wwf\",\"DiagramFontSize\":\"\",\"ProjectLatitude\":\"0.0\",\"CurrencyType\":\"EUR\",\"TNC.Country\":\"\",\"TNC.Ecoregion\":\"\",\"LocationComments\":\"\",\"ProjectLongitude\":\"0.0\",\"ScopeComments\":\"\",\"Id\":0,\"ExpectedEndDate\":\"2008-01-04\",\"CurrencySymbol\":\"E\",\"StateAndProvinces\":\"\",\"CurrencyDecimalPlaces\":\"\",\"FinancialComments\":\"\",\"TNC.PlanningTeamComment\":\"\",\"CurrentWizardScreenName\":\"SummaryOverviewStep\",\"WorkPlanEndDate\":\"\",\"ProjectDescription\":\"\",\"ThreatRatingMode\":\"\",\"PlanningComments\":\"\",\"ProjectURL\":\"\",\"WorkPlanTimeUnit\":\"YEARLY\",\"ProjectScope\":\"\",\"TNC.SizeInHectares\":\"\",\"TNC.WorkbookVersionNumber\":\"\",\"BudgetCostOverride\":\"\",\"DataEffectiveDate\":\"\",\"ShortProjectVision\":\"\",\"ProjectVision\":\"vision text 101\",\"WorkPlanStartDate\":\"\",\"TimeStampModified\":\"1199476769703\",\"ShortProjectScope\":\"\",\"ProjectAreaNote\":\"\",\"TNC.WorkbookVersionDate\":\"\",\"Label\":\"\",\"ProjectArea\":\"\",\"TNC.OperatingUnits\":\"\"}";
		String projectMetaDataWithTncCountries = "{\"FiscalYearStart\":\"\",\"BudgetSecuredPercent\":\"1.0\",\"TNC.DatabaseDownloadDate\":\"\",\"Countries\":\"\",\"StartDate\":\"2008-01-04\",\"Municipalities\":\"\",\"BudgetCostMode\":\"\",\"LegislativeDistricts\":\"\",\"DiagramFontFamily\":\"\",\"KeyFundingSources\":\"\",\"TotalBudgetForFunding\":\"1.0\",\"LocationDetail\":\"\",\"TNC.LessonsLearned\":\"\",\"ProjectName\":\"my project name for wwf\",\"DiagramFontSize\":\"\",\"ProjectLatitude\":\"0.0\",\"CurrencyType\":\"EUR\",\"TNC.Country\":\"China, Japan, Alaska, Luxenburg,Mars\",\"TNC.Ecoregion\":\"\",\"LocationComments\":\"\",\"ProjectLongitude\":\"0.0\",\"ScopeComments\":\"\",\"Id\":0,\"ExpectedEndDate\":\"2008-01-04\",\"CurrencySymbol\":\"E\",\"StateAndProvinces\":\"\",\"CurrencyDecimalPlaces\":\"\",\"FinancialComments\":\"\",\"TNC.PlanningTeamComment\":\"\",\"CurrentWizardScreenName\":\"SummaryOverviewStep\",\"WorkPlanEndDate\":\"\",\"ProjectDescription\":\"\",\"ThreatRatingMode\":\"\",\"PlanningComments\":\"\",\"ProjectURL\":\"\",\"WorkPlanTimeUnit\":\"YEARLY\",\"ProjectScope\":\"\",\"TNC.SizeInHectares\":\"\",\"TNC.WorkbookVersionNumber\":\"\",\"BudgetCostOverride\":\"\",\"DataEffectiveDate\":\"\",\"ShortProjectVision\":\"\",\"ProjectVision\":\"vision text 101\",\"WorkPlanStartDate\":\"\",\"TimeStampModified\":\"1199476660129\",\"ShortProjectScope\":\"\",\"ProjectAreaNote\":\"\",\"TNC.WorkbookVersionDate\":\"\",\"Label\":\"\",\"ProjectArea\":\"\",\"TNC.OperatingUnits\":\"\"}";
		File jsonDir = createJsonDir();
		
		File projectMetaDataDir = DataUpgrader.createObjectsDir(jsonDir, 11);
		int[] projectMetaDataIds = {0, };
		File projectMetaDataManifestFile = createManifestFile(projectMetaDataDir, projectMetaDataIds);
		assertTrue(projectMetaDataManifestFile.exists());
		
		File projectMetaDataFileWithTncCountries = new File(projectMetaDataDir, Integer.toString(projectMetaDataIds[0]));
		createFile(projectMetaDataFileWithTncCountries, projectMetaDataWithTncCountries);
		
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		assertFalse("country code was blank?", dataUpgrader.isTncCountryCodeBlank());
		
		File projectMetaDataFileWithoutTncCountries = new File(projectMetaDataDir, Integer.toString(projectMetaDataIds[0]));
		createFile(projectMetaDataFileWithoutTncCountries, projectMetaDataWithoutTncCountries);
		
		DataUpgrader dataUpgrader2 = new DataUpgrader(tempDirectory);
		assertTrue("country code was not blank?", dataUpgrader2.isTncCountryCodeBlank());
	}
	
	public void testCopyWwfProjectDataCountriesFieldOverToProjectMetaData() throws Exception
	{
		File jsonDir = createJsonDir();
		String projectMetaDataString = "{\"FiscalYearStart\":\"\",\"BudgetSecuredPercent\":\"1.0\",\"TNC.DatabaseDownloadDate\":\"\",\"Countries\":\"\",\"StartDate\":\"2008-01-04\",\"Municipalities\":\"\",\"BudgetCostMode\":\"\",\"LegislativeDistricts\":\"\",\"DiagramFontFamily\":\"\",\"KeyFundingSources\":\"\",\"TotalBudgetForFunding\":\"1.0\",\"LocationDetail\":\"\",\"TNC.LessonsLearned\":\"\",\"ProjectName\":\"my project name for wwf\",\"DiagramFontSize\":\"\",\"ProjectLatitude\":\"0.0\",\"CurrencyType\":\"EUR\",\"TNC.Country\":\"\",\"TNC.Ecoregion\":\"\",\"LocationComments\":\"\",\"ProjectLongitude\":\"0.0\",\"ScopeComments\":\"\",\"Id\":0,\"ExpectedEndDate\":\"2008-01-04\",\"CurrencySymbol\":\"E\",\"StateAndProvinces\":\"\",\"CurrencyDecimalPlaces\":\"\",\"FinancialComments\":\"\",\"TNC.PlanningTeamComment\":\"\",\"CurrentWizardScreenName\":\"SummaryOverviewStep\",\"WorkPlanEndDate\":\"\",\"ProjectDescription\":\"\",\"ThreatRatingMode\":\"\",\"PlanningComments\":\"\",\"ProjectURL\":\"\",\"WorkPlanTimeUnit\":\"YEARLY\",\"ProjectScope\":\"\",\"TNC.SizeInHectares\":\"\",\"TNC.WorkbookVersionNumber\":\"\",\"BudgetCostOverride\":\"\",\"DataEffectiveDate\":\"\",\"ShortProjectVision\":\"\",\"ProjectVision\":\"vision text 101\",\"WorkPlanStartDate\":\"\",\"TimeStampModified\":\"1199469823600\",\"ShortProjectScope\":\"\",\"ProjectAreaNote\":\"\",\"TNC.WorkbookVersionDate\":\"\",\"Label\":\"\",\"ProjectArea\":\"\",\"TNC.OperatingUnits\":\"\"}";
		String wwfProjectDataString = "{\"Countries\":\"{\\\"Codes\\\":[\\\"AFG\\\",\\\"ALA\\\",\\\"ALB\\\",\\\"DZA\\\"]}\",\"ProjectNumber\":\"\",\"ManagingOffices\":\"\",\"TimeStampModified\":\"1199469576877\",\"BudgetCostOverride\":\"\",\"BudgetCostMode\":\"\",\"Label\":\"\",\"Regions\":\"\",\"Id\":12,\"EcoRegions\":\"\",\"RelatedProjects\":\"\"}";
		
		File projectMetaDataDir = DataUpgrader.createObjectsDir(jsonDir, 11);
		int[] projectMetaDataIds = {0, };
		File projectMetaDataManifestFile = createManifestFile(projectMetaDataDir, projectMetaDataIds);
		assertTrue(projectMetaDataManifestFile.exists());
		
		File projectMetaDataFile = new File(projectMetaDataDir, Integer.toString(projectMetaDataIds[0]));
		createFile(projectMetaDataFile, projectMetaDataString);
		
		File wwfProjectDataDir = DataUpgrader.createObjectsDir(jsonDir, 30);
		int[] wwfProjectDataIds = {12, };
		File wwfProjectDataManifestFile = createManifestFile(wwfProjectDataDir, wwfProjectDataIds);
		assertTrue(wwfProjectDataManifestFile.exists());
		File wwfProjectDataFile = new File(wwfProjectDataDir, Integer.toString(wwfProjectDataIds[0]));
		createFile(wwfProjectDataFile, wwfProjectDataString);
		
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		dataUpgrader.upgradeToVersion29();
		
		assertTrue("project meta data file exists?", projectMetaDataFile.exists());
		EnhancedJsonObject projectMetaDataJson = DataUpgrader.readFile(projectMetaDataFile);
		assertEquals("wrong id?", new BaseId(0), projectMetaDataJson.getId("Id"));
		
		CodeList countryCodes = new CodeList(projectMetaDataJson.optString("Countries"));
		assertEquals("wrong number of countries?", 4, countryCodes.size());
		assertTrue("counrty code not found?", countryCodes.contains("AFG"));
		assertTrue("counrty code not found?", countryCodes.contains("ALA"));
		assertTrue("counrty code not found?", countryCodes.contains("ALB"));
		assertTrue("counrty code not found?", countryCodes.contains("DZA"));
	}
	
	public void testCreateThreatStressRatingsForTargetThreatLinks() throws Exception
	{
		File jsonDir = createJsonDir();
		File projectFile = new File(jsonDir, "project");
		createFile(projectFile, "{\"HighestUsedNodeId\":33}");
		
		String targetCauseLinkStresses = "{\"FromRef\":\"{\\\"ObjectType\\\":20,\\\"ObjectId\\\":17}\",\"TimeStampModified\":\"1196110783203\",\"ToRef\":\"{\\\"ObjectType\\\":22,\\\"ObjectId\\\":15}\",\"ThreatStressRatingRefs\":\"\",\"Label\":\"\",\"Id\":19,\"BidirectionalLink\":\"0\",\"StressLabel\":\"\"}";
		String targetCauseLinkNoStress = "{\"FromRef\":\"{\\\"ObjectType\\\":20,\\\"ObjectId\\\":23}\",\"TimeStampModified\":\"1196110789735\",\"ToRef\":\"{\\\"ObjectType\\\":22,\\\"ObjectId\\\":21}\",\"ThreatStressRatingRefs\":\"\",\"Label\":\"\",\"Id\":25,\"BidirectionalLink\":\"0\",\"StressLabel\":\"\"}";
		String causeCauseLink = "{\"FromRef\":\"{\\\"ObjectType\\\":20,\\\"ObjectId\\\":31}\",\"TimeStampModified\":\"1196111729305\",\"ToRef\":\"{\\\"ObjectType\\\":20,\\\"ObjectId\\\":17}\",\"ThreatStressRatingRefs\":\"\",\"Label\":\"\",\"Id\":33,\"BidirectionalLink\":\"0\",\"StressLabel\":\"\"}";
		File factorLinkDir = DataUpgrader.createObjectsDir(jsonDir, 6);
		int[] factorLinkIds = {19, 25, 33};
		File factorLinkManifestFile = createManifestFile(factorLinkDir, factorLinkIds);
		assertTrue(factorLinkManifestFile.exists());
		
		File factorLink19 = new File(factorLinkDir, Integer.toString(factorLinkIds[0]));
		createFile(factorLink19, targetCauseLinkStresses);
		assertTrue(factorLink19.exists());
		
		File factorLink25 = new File(factorLinkDir, Integer.toString(factorLinkIds[1]));
		createFile(factorLink25, targetCauseLinkNoStress);
		assertTrue(factorLink25.exists());
		
		File factorLink33 = new File(factorLinkDir, Integer.toString(factorLinkIds[2]));
		createFile(factorLink33, causeCauseLink);
		assertTrue(factorLink33.exists());
		

		String TargetWithStresses = "{\"ObjectiveIds\":\"\",\"ViabilityMode\":\"\",\"IndicatorIds\":\"\",\"Type\":\"Target\",\"Comment\":\"\",\"StressRefs\":\"{\\\"References\\\":[{\\\"ObjectType\\\":33,\\\"ObjectId\\\":17},{\\\"ObjectType\\\":33,\\\"ObjectId\\\":18}]}\",\"TargetStatus\":\"\",\"GoalIds\":\"\",\"TimeStampModified\":\"1196110887955\",\"KeyEcologicalAttributeIds\":\"\",\"Id\":15,\"Label\":\"New Target\",\"CurrentStatusJustification\":\"\"}";
		String TargetNoStresses = "{\"ObjectiveIds\":\"\",\"ViabilityMode\":\"\",\"IndicatorIds\":\"\",\"Type\":\"Target\",\"Comment\":\"\",\"StressRefs\":\"\",\"TargetStatus\":\"\",\"GoalIds\":\"\",\"TimeStampModified\":\"1196110895493\",\"KeyEcologicalAttributeIds\":\"\",\"Id\":21,\"Label\":\"New Target\",\"CurrentStatusJustification\":\"\"}";
		File targetDir = DataUpgrader.createObjectsDir(jsonDir, 4);
		int[] targetIds = {15, 21};
		File targetManifestFile = createManifestFile(targetDir, targetIds);
		assertTrue(targetManifestFile.exists());
		
		File target15 = new File(targetDir, Integer.toString(targetIds[0]));
		createFile(target15, TargetWithStresses);
		assertTrue(target15.exists());
		
		File target21 = new File(targetDir, Integer.toString(targetIds[1]));
		createFile(target21, TargetNoStresses);
		assertTrue(target21.exists());

		
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		dataUpgrader.upgradeToVersion25();
		
		File threatStressRatingDir = new File(jsonDir, "objects-34");
		assertTrue(threatStressRatingDir.exists());
		
		File threatStressRatingManifestFile = new File(threatStressRatingDir, "manifest");
		assertTrue("threat stresss rating manifest does not exist?", threatStressRatingManifestFile.exists());
		
		ObjectManifest threatStressRatingObjectManifestFile = new ObjectManifest(JSONFile.read(threatStressRatingManifestFile));
		BaseId[] allThreatStressRatingIds = threatStressRatingObjectManifestFile.getAllKeys();
		assertEquals("wrong threat stress ratings count in dir", 2, allThreatStressRatingIds.length);
		
	
		File threatStressRatingFile1 = new File(threatStressRatingDir, "34");
		assertTrue("threat stress rating object file exists?", threatStressRatingFile1.exists());
		EnhancedJsonObject threatStressRating1Json = DataUpgrader.readFile(threatStressRatingFile1);
		assertEquals("wrong id?", new BaseId(34), threatStressRating1Json.getId("Id"));
		assertEquals("wrong stress ref?", new ORef(33, new BaseId(17)), threatStressRating1Json.getRef("StressRef"));
		
		File threatStressRatingFile2 = new File(threatStressRatingDir, "35");
		assertTrue("threat stress rating object file exists?", threatStressRatingFile2.exists());
		EnhancedJsonObject threatStressRating2Json = DataUpgrader.readFile(threatStressRatingFile2);
		assertEquals("wrong id?", new BaseId(35), threatStressRating2Json.getId("Id"));
		assertEquals("wrong stress ref?", new ORef(33, new BaseId(18)), threatStressRating2Json.getRef("StressRef"));
		
		EnhancedJsonObject factorLinkWithThreatStressRatingJson = DataUpgrader.readFile(factorLink19);
		String threatStressRating1AsString = factorLinkWithThreatStressRatingJson.getString("ThreatStressRatingRefs");
		ORefList threatStressRating1Refs = new ORefList(threatStressRating1AsString);
		assertEquals("wrong number of refs in list?", 2, threatStressRating1Refs.size());		
		assertEquals("wrong first threat stress rating ref?", new ORef(34, new BaseId(34)), threatStressRating1Refs.get(0));
		assertEquals("wrong second threat stress rating ref?", new ORef(34, new BaseId(35)), threatStressRating1Refs.get(1));
		
		EnhancedJsonObject factorLinkWithoutThreatStressRating1Json = DataUpgrader.readFile(factorLink25);
		String threatStressRating2AsString = factorLinkWithoutThreatStressRating1Json.getString("ThreatStressRatingRefs");
		ORefList threatStressRating2Refs = new ORefList(threatStressRating2AsString);
		assertEquals("wrong number of refs in list?", 0, threatStressRating2Refs.size());
			
		EnhancedJsonObject factorLinkWithoutThreatStressRating2Json = DataUpgrader.readFile(factorLink25);
		String threatStressRating3AsString = factorLinkWithoutThreatStressRating2Json.getString("ThreatStressRatingRefs");
		ORefList threatStressRating3Refs = new ORefList(threatStressRating3AsString);
		assertEquals("wrong number of refs in list?", 0, threatStressRating3Refs.size());
			
		int highestId = DataUpgrader.readHighestIdInProjectFile(jsonDir);
		assertEquals("wrong highest Id?", 35, highestId);
	}
	
	public void testUpdateTo24CreateStressesFromFactorLinks() throws Exception
	{
		String factorLinkWithStressLabelData = "{\"FromId\":58,\"ToId\":3,\"ToRef\":{\"ObjectType\":22,\"ObjectId\":15},\"FromRef\":{\"ObjectType\":21,\"ObjectId\":58},\"Label\":\"\",\"StressLabel\":\"someLabel\",\"Id\":13}";
		String factorLinkWithOutStressLabelData = "{\"FromId\":5,\"ToId\":78,\"ToRef\":{\"ObjectType\":22,\"ObjectId\":78},\"FromRef\":{\"ObjectType\":20,\"ObjectId\":5},\"Label\":\"\",\"StressLabel\":\"\",\"Id\":14}";
		File jsonDir = createJsonDir();
		File factorLinkDir = DataUpgrader.createObjectsDir(jsonDir, 6);
		
		File projectFile = new File(jsonDir, "project");
		createFile(projectFile, "{\"HighestUsedNodeId\":15}");
		
		int[] factorLinkIds = {13, 14};
		File factorLinkManifestFile = createManifestFile(factorLinkDir, factorLinkIds);
		assertTrue(factorLinkManifestFile.exists());
		
		File factorLink13WithStressLabel = new File(factorLinkDir, Integer.toString(factorLinkIds[0]));
		createFile(factorLink13WithStressLabel, factorLinkWithStressLabelData);
		assertTrue(factorLink13WithStressLabel.exists());
		
		File factorLink14WithoutStressLabel = new File(factorLinkDir, Integer.toString(factorLinkIds[1]));
		createFile(factorLink14WithoutStressLabel, factorLinkWithOutStressLabelData);
		assertTrue(factorLink14WithoutStressLabel.exists());
		
		File targetDir = DataUpgrader.createObjectsDir(jsonDir, 4);
		int[] targetIds = {15};
		File targetManifestFile = createManifestFile(targetDir, targetIds);
		assertTrue(targetManifestFile.exists());
		
		File target = new File(targetDir, Integer.toString(targetIds[0]));
		String targetData = "{\"ObjectiveIds\":\"\",\"ViabilityMode\":\"TNC\",\"IndicatorIds\":\"\",\"Type\":\"Target\",\"Comment\":\"\",\"StressRefs\":\"{\\\"References\\\":[{\\\"ObjectType\\\":33,\\\"ObjectId\\\":17},{\\\"ObjectType\\\":33,\\\"ObjectId\\\":18},{\\\"ObjectType\\\":33,\\\"ObjectId\\\":27}]}\",\"TargetStatus\":\"\",\"GoalIds\":\"\",\"TimeStampModified\":\"1195497623367\",\"KeyEcologicalAttributeIds\":\"{\\\"Ids\\\":[28,30]}\",\"Id\":15,\"Label\":\"New Target\",\"CurrentStatusJustification\":\"\"}";
		createFile(target, targetData);
		assertTrue(target.exists());
		
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		dataUpgrader.upgradeToVersion24();
		
		File stressDir = new File(jsonDir, "objects-33");
		assertTrue("stress dir does not exist?", stressDir.exists());
		
		File stressManifestFile = new File(stressDir, "manifest");
		assertTrue("stress manifest does not exist?", stressManifestFile.exists());

		ObjectManifest stressObjectManifestFile = new ObjectManifest(JSONFile.read(stressManifestFile));
		BaseId[] allStressIds = stressObjectManifestFile.getAllKeys();
		assertEquals("wrong number of stresses created?", 1, allStressIds.length);
		
		String idAsString = Integer.toString(16);
		File stressFile = new File(stressDir, idAsString);
		assertTrue("stress file does not exist?", stressFile.exists());
		EnhancedJsonObject stressJson = DataUpgrader.readFile(stressFile);
		assertEquals("wrong id?", "16", stressJson.getString("Id"));
		assertEquals("wrong label?", "someLabel", stressJson.getString("Label"));
		
		EnhancedJsonObject targetJson = DataUpgrader.readFile(target);
		String stressRefsAsString = targetJson.getString("StressRefs");
		ORefList stressRefs = new ORefList(stressRefsAsString);
		assertEquals("wrong number of refs in list?", 1, stressRefs.size());
		assertEquals("wrong ref in list?", new ORef(Stress.getObjectType(), new BaseId(16)), stressRefs.get(0));
		
		int highestId = DataUpgrader.readHighestIdInProjectFile(jsonDir);
		assertEquals("wrong highest Id?", 16, highestId);
	}
	
	public void testUpdateTo23CreateMeasurementFromDataInIndicator() throws Exception
	{
		String indicatorWithMeasurementData = "{\"MeasurementDate\":\"2007-10-02\",\"Status\":\"\",\"MeasurementStatus\":\"\",\"RatingSource\":\"\",\"ShortLabel\":\"1111111111\",\"MeasurementDetail\":\"CS detail text\",\"Priority\":\"\",\"FutureStatusRating\":\"\",\"MeasurementRefs\":\"\",\"Label\":\"1111111111\",\"MeasurementTrend\":\"Unknown\",\"FutureStatusSummary\":\"\",\"MeasurementStatusConfidence\":\"RapidAssessment\",\"TimeStampModified\":\"1193620397190\",\"MeasurementSummary\":\"CS summary label\",\"TaskIds\":\"\",\"IndicatorThresholds\":\"\",\"FutureStatusDetail\":\"\",\"FutureStatusDate\":\"\",\"Id\":17}";
		String indicatorWithoutMeasurementData = "{\"MeasurementDate\":\"\",\"Status\":\"\",\"MeasurementStatus\":\"\",\"RatingSource\":\"\",\"ShortLabel\":\"\",\"MeasurementDetail\":\"\",\"Priority\":\"\",\"FutureStatusRating\":\"\",\"MeasurementRefs\":\"\",\"Label\":\"no CS data\",\"MeasurementTrend\":\"\",\"FutureStatusSummary\":\"\",\"MeasurementStatusConfidence\":\"\",\"TimeStampModified\":\"1193620417567\",\"MeasurementSummary\":\"\",\"TaskIds\":\"\",\"IndicatorThresholds\":\"\",\"FutureStatusDetail\":\"\",\"FutureStatusDate\":\"\",\"Id\":18}";
		String indicatorWithoutMeasurementFields = "{\"Status\":\"\",\"RatingSource\":\"\",\"ShortLabel\":\"\",\"Priority\":\"\",\"FutureStatusRating\":\"\",\"Label\":\"no CS data\",\"FutureStatusSummary\":\"\",\"TimeStampModified\":\"1193620417567\",\"TaskIds\":\"\",\"IndicatorThresholds\":\"\",\"FutureStatusDetail\":\"\",\"FutureStatusDate\":\"\",\"Id\":19}";
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();
		
		File projectFile = new File(jsonDir, "project");
		createFile(projectFile, "{\"HighestUsedNodeId\":18}");
		
		File indicatorDir = createObjectsDir(jsonDir, "objects-8");
		indicatorDir.mkdirs();
		
		int[] indicatorIds = {17, 18, 19};
		File indicatorManifestFile = createManifestFile(indicatorDir, indicatorIds);
		assertTrue(indicatorManifestFile.exists());
		
		File indicator17WithMeasurementDataFile = new File(indicatorDir, Integer.toString(indicatorIds[0]));
		createFile(indicator17WithMeasurementDataFile, indicatorWithMeasurementData);
		assertTrue(indicator17WithMeasurementDataFile.exists());
		
		File indicator18WithoutMeasurementDataFile = new File(indicatorDir, Integer.toString(indicatorIds[1]));
		createFile(indicator18WithoutMeasurementDataFile, indicatorWithoutMeasurementData);
		assertTrue(indicator18WithoutMeasurementDataFile.exists());
		
		File indicator19WithoutMeasurementFieldsFile = new File(indicatorDir, Integer.toString(indicatorIds[2]));
		createFile(indicator19WithoutMeasurementFieldsFile, indicatorWithoutMeasurementFields);
		assertTrue(indicator19WithoutMeasurementFieldsFile.exists());

		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		dataUpgrader.upgradeToVersion23();
		
		File measurementDir = new File(jsonDir, "objects-32");
		assertTrue("measurment dir does not exist?", measurementDir.exists());
		
		File measurementManifestFile = new File(measurementDir, "manifest");
		assertTrue("measurement manifest does not exist?", measurementManifestFile.exists());
		ObjectManifest measurementObjectManifestFile = new ObjectManifest(JSONFile.read(measurementManifestFile));
		BaseId[] allMeasurementIds = measurementObjectManifestFile.getAllKeys();
		assertEquals("wrong number of measurements created?", 1, allMeasurementIds.length);
			
		String idAsString = Integer.toString(19);
		File measurementFile = new File(measurementDir, idAsString);
		assertTrue("measurement file does not exist?", measurementFile.exists());
		EnhancedJsonObject measurementJson = DataUpgrader.readFile(measurementFile);
		assertEquals("wrong id?", "19", measurementJson.getString("Id"));
		assertEquals("wrong label?", "", measurementJson.getString("Label"));
		
		assertEquals("wrong trend value?", "Unknown", measurementJson.getString("Trend"));
		assertEquals("wrong status value?", "", measurementJson.getString("Status"));
		assertEquals("wrong date value?", "2007-10-02", measurementJson.getString("Date"));
		assertEquals("wrong summary value?", "CS summary label", measurementJson.getString("Summary"));
		assertEquals("wrong status value?", "CS detail text", measurementJson.getString("Detail"));
		assertEquals("wrong status confidence value?", "RapidAssessment", measurementJson.getString("StatusConfidence"));
		
		EnhancedJsonObject indicatorWithJsonObject = DataUpgrader.readFile(indicator17WithMeasurementDataFile);
		String measurementRefsAsString = indicatorWithJsonObject.getString("MeasurementRefs");
		ORefList measurementRefs = new ORefList(measurementRefsAsString);
		assertEquals("wrong number of refs in list?", 1, measurementRefs.size());
		assertEquals("wrong ref in list?", new ORef(Measurement.getObjectType(), new BaseId(19)), measurementRefs.get(0));
	}
	
	public void testUpgradeTo22ChangeWrappedIdsToRefs() throws Exception
	{
		String strategyString = " {\"Type\":\"Intervention\",\"Status\":\"\",\"FeasibilityRating\":\"\",\"ShortLabel\":\"\",\"ActivityIds\":\"\",\"Comment\":\"\",\"GoalIds\":\"\",\"ImpactRating\":\"\",\"IndicatorIds\":\"\",\"Label\":\"New Strategy\",\"DurationRating\":\"\",\"TimeStampModified\":\"1185205725518\",\"TaxonomyCode\":\"\",\"KeyEcologicalAttributeIds\":\"\",\"CostRating\":\"\",\"Id\":16,\"ObjectiveIds\":\"\"} "; 
		String diagramFactorString = " {\"TimeStampModified\":\"1185205730937\",\"Size\":\"{\\\"Width\\\":120,\\\"Height\\\":60}\",\"Label\":\"\",\"WrappedFactorId\":\"16\",\"Location\":\"{\\\"Y\\\":435,\\\"X\\\":165}\",\"Id\":17} ";
		File jsonDir = createObjectsDir(tempDirectory, "json");
		jsonDir.mkdirs();
		
		File diagramFactorDir = createObjectsDir(jsonDir, "objects-18");
		diagramFactorDir.mkdirs();
		
		File factorDir = createObjectsDir(jsonDir, "objects-4");
		factorDir.mkdirs();
		
		int[] diagramFactorIds = {17};
		File diagramFactorManifest = createManifestFile(diagramFactorDir, diagramFactorIds);
		assertTrue(diagramFactorManifest.exists());
		File diagramFactorFile = new File(diagramFactorDir, Integer.toString(diagramFactorIds[0]));
		createFile(diagramFactorFile, diagramFactorString);
		assertTrue(diagramFactorFile.exists());
		
		int[] factorIds = {16};
		File factorManifest = createManifestFile(factorDir, factorIds);
		assertTrue(factorManifest.exists());

		File strategyFile =  new File(factorDir, Integer.toString(factorIds[0]));
		createFile(strategyFile, strategyString);
		assertTrue(strategyFile.exists());
		
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		dataUpgrader.upgradeToVersion22();
		
		EnhancedJsonObject conceptualModelJson = DataUpgrader.readFile(diagramFactorFile);
		String wrappedRefAsString = conceptualModelJson.getString("WrappedFactorRef");
		ORef wrappedFactorRef = ORef.createFromString(wrappedRefAsString);
		ORef expectedRef = new ORef(ObjectType.STRATEGY, new BaseId(16));
		assertEquals("wrong wrapped factor ref?", expectedRef, wrappedFactorRef);
	}
	
	public void testUpgradeTo21AddLinksInAllDOsWhereNeeded() throws Exception
	{
		File jsonDir = createObjectsDir(tempDirectory, "json");
		
		File projectFile = new File(jsonDir, "project");
		createFile(projectFile, "{\"HighestUsedNodeId\":31}");
		
		File factorDir = createObjectsDir(jsonDir, "objects-4");
		File factorLinksDir = createObjectsDir(jsonDir, "objects-6");
		File diagramLinkDir = createObjectsDir(jsonDir, "objects-13");
		File diagramFactorDir = createObjectsDir(jsonDir, "objects-18");
		File conceptualModelDir = createObjectsDir(jsonDir, "objects-19");
		File resultsChainDir = createObjectsDir(jsonDir, "objects-24");
		
		final int target12 = 12;
		final int target14 = 14;
		final int strat16 = 16;
		int[] factorIds = {target12, target14, strat16, };
		File factorManifest = createManifestFile(factorDir, factorIds);
		String target12str = "{\"Type\":\"Target\",\"CurrentStatusJustification\":\"\",\"ViabilityMode\":\"\",\"Comment\":\"\",\"TimeStampModified\":\"1182273035062\",\"GoalIds\":\"\",\"KeyEcologicalAttributeIds\":\"\",\"TargetStatus\":\"\",\"IndicatorIds\":\"\",\"Label\":\"New Target\",\"Id\":12,\"ObjectiveIds\":\"\"}";
		String target14str = "{\"Type\":\"Target\",\"CurrentStatusJustification\":\"\",\"ViabilityMode\":\"\",\"Comment\":\"\",\"TimeStampModified\":\"1182273035625\",\"GoalIds\":\"\",\"KeyEcologicalAttributeIds\":\"\",\"TargetStatus\":\"\",\"IndicatorIds\":\"\",\"Label\":\"New Target\",\"Id\":14,\"ObjectiveIds\":\"\"}";
		String strategy16str = "{\"Type\":\"Intervention\",\"Status\":\"\",\"FeasibilityRating\":\"\",\"ShortLabel\":\"\",\"ActivityIds\":\"\",\"Comment\":\"\",\"GoalIds\":\"\",\"ImpactRating\":\"\",\"IndicatorIds\":\"\",\"Label\":\"New Strategy\",\"DurationRating\":\"\",\"TimeStampModified\":\"1182273040484\",\"TaxonomyCode\":\"\",\"KeyEcologicalAttributeIds\":\"\",\"CostRating\":\"\",\"Id\":16,\"ObjectiveIds\":\"\"}";
		createObjectFile(factorDir, Integer.toString(target12), target12str);
		createObjectFile(factorDir, Integer.toString(target14), target14str);
		createObjectFile(factorDir, Integer.toString(strat16), strategy16str);
		
		final int factorLink18 = 18;
		final int factorLink24 = 24;
		int[] factorLinkIds = {factorLink18, factorLink24, };
		File factorLinkManifest = createManifestFile(factorLinksDir, factorLinkIds);
		String factorLinkStrat16_target12str = "{\"ToId\":\"12\",\"FromId\":\"16\",\"TimeStampModified\":\"1182273040500\",\"ToRef\":{\"ObjectType\":22,\"ObjectId\":12},\"FromRef\":{\"ObjectType\":21,\"ObjectId\":16},\"StressLabel\":\"\",\"Label\":\"\",\"Id\":18,\"BidirectionalLink\":\"0\"}";
		String factorLinkStrat16_target14str = "{\"ToId\":\"14\",\"FromId\":\"16\",\"TimeStampModified\":\"1182273069421\",\"ToRef\":{\"ObjectType\":22,\"ObjectId\":14},\"FromRef\":{\"ObjectType\":21,\"ObjectId\":16},\"StressLabel\":\"\",\"Label\":\"\",\"Id\":24,\"BidirectionalLink\":\"0\"}";
		createObjectFile(factorLinksDir, Integer.toString(factorLink18), factorLinkStrat16_target12str);
		createObjectFile(factorLinksDir, Integer.toString(factorLink24), factorLinkStrat16_target14str);
		
		final int cmTarget13Wrapper = 13;
		final int cmTarget15Wrapper = 15;
		final int cmStrat17Wrapper = 17;
		final int rcTarget27Wrapper = 27;
		final int rcTarget28Wrapper = 28;
		final int rcStrat29Wrapper = 29;
		int[] diagramFactorIds = {cmTarget13Wrapper, cmTarget15Wrapper, cmStrat17Wrapper, rcTarget27Wrapper, rcTarget28Wrapper, rcStrat29Wrapper, };
		File diagramFactorManifest = createManifestFile(diagramFactorDir, diagramFactorIds);
		String diagramFactorTargetCM13str = "{\"TimeStampModified\":\"1182273035046\",\"Size\":\"{\\\"Width\\\":120,\\\"Height\\\":60}\",\"Label\":\"\",\"WrappedFactorId\":\"12\",\"Location\":\"{\\\"Y\\\":150,\\\"X\\\":945}\",\"Id\":13}";
		String diagramFactorTargetCM15str = "{\"TimeStampModified\":\"1182273035609\",\"Size\":\"{\\\"Width\\\":120,\\\"Height\\\":60}\",\"Label\":\"\",\"WrappedFactorId\":\"14\",\"Location\":\"{\\\"Y\\\":225,\\\"X\\\":945}\",\"Id\":15}";
		String diagramFactorStrategyCM17str = "{\"TimeStampModified\":\"1182273040468\",\"Size\":\"{\\\"Width\\\":120,\\\"Height\\\":60}\",\"Label\":\"\",\"WrappedFactorId\":\"16\",\"Location\":\"{\\\"Y\\\":150,\\\"X\\\":675}\",\"Id\":17}";
		String diagramFactorTargetRC28str = "{\"TimeStampModified\":\"1182273074250\",\"Size\":\"{\\\"Width\\\":120,\\\"Height\\\":60}\",\"Label\":\"\",\"WrappedFactorId\":\"14\",\"Location\":\"{\\\"Y\\\":225,\\\"X\\\":945}\",\"Id\":28}";
		String diagramFactorTargetRC29str = "{\"TimeStampModified\":\"1182273074296\",\"Size\":\"{\\\"Width\\\":120,\\\"Height\\\":60}\",\"Label\":\"\",\"WrappedFactorId\":\"12\",\"Location\":\"{\\\"Y\\\":150,\\\"X\\\":945}\",\"Id\":29}";
		String diagramFactorStrategyRC27str = "{\"TimeStampModified\":\"1182273074218\",\"Size\":\"{\\\"Width\\\":120,\\\"Height\\\":60}\",\"Label\":\"\",\"WrappedFactorId\":\"16\",\"Location\":\"{\\\"Y\\\":150,\\\"X\\\":675}\",\"Id\":27}";
		createObjectFile(diagramFactorDir, Integer.toString(cmTarget13Wrapper), diagramFactorTargetCM13str);
		createObjectFile(diagramFactorDir, Integer.toString(cmTarget15Wrapper), diagramFactorTargetCM15str);
		createObjectFile(diagramFactorDir, Integer.toString(cmStrat17Wrapper), diagramFactorStrategyCM17str);
		createObjectFile(diagramFactorDir, Integer.toString(rcTarget27Wrapper), diagramFactorStrategyRC27str);
		createObjectFile(diagramFactorDir, Integer.toString(rcTarget28Wrapper), diagramFactorTargetRC28str);
		createObjectFile(diagramFactorDir, Integer.toString(rcStrat29Wrapper), diagramFactorTargetRC29str);

		final int diagramLink25 = 25;
		final int diagramLink30 = 30;
		int[] diagramLinkIds = {diagramLink25, diagramLink30, };
		File diagramLinkManifest = createManifestFile(diagramLinkDir, diagramLinkIds);
		String diagramLinkStrat17_target15str = "{\"FromDiagramFactorId\":17,\"TimeStampModified\":\"1182273069437\",\"ToDiagramFactorId\":15,\"BendPoints\":\"\",\"WrappedLinkId\":24,\"Label\":\"\",\"Id\":25}";
		String diagramLinkStrat27_target29str = "{\"FromDiagramFactorId\":27,\"TimeStampModified\":\"1182273074343\",\"ToDiagramFactorId\":29,\"BendPoints\":\"\",\"WrappedLinkId\":18,\"Label\":\"\",\"Id\":30}";
		createObjectFile(diagramLinkDir, Integer.toString(diagramLink25), diagramLinkStrat17_target15str);
		createObjectFile(diagramLinkDir, Integer.toString(diagramLink30), diagramLinkStrat27_target29str);
		
		int [] resultsChainIds = {26};
		String resutlsChainIdAsString = Integer.toString(resultsChainIds[0]);
		File resultsChainManifest = createManifestFile(resultsChainDir, resultsChainIds);
		String resultsChainString ="{\"TimeStampModified\":\"1182273078281\",\"DiagramFactorLinkIds\":\"{\\\"Ids\\\":[30]}\",\"Label\":\"Results Chain\",\"DiagramFactorIds\":\"{\\\"Ids\\\":[29,28,27]}\",\"Id\":26}";
		createObjectFile(resultsChainDir, resutlsChainIdAsString, resultsChainString);


		int[] conceptualModelIds = {10};
		String conceptualModelIdAsString = Integer.toString(conceptualModelIds[0]);
		File conceptualModelManifest = createManifestFile(conceptualModelDir, conceptualModelIds);
		String conceptualModelString = "{\"TimeStampModified\":\"1182273085703\",\"DiagramFactorLinkIds\":\"{\\\"Ids\\\":[25]}\",\"Label\":\"\",\"DiagramFactorIds\":\"{\\\"Ids\\\":[13,15,17]}\",\"Id\":10}";
		createObjectFile(conceptualModelDir, conceptualModelIdAsString, conceptualModelString);
		
		assertTrue("results chain manifest doesnt exist?", resultsChainManifest.exists());
		assertTrue("concpetual model manifest doesnt exist?", conceptualModelManifest.exists());
		assertTrue("factor manifest doesnt exist?", factorManifest.exists());
		assertTrue("factor link manifest doesnt exist?", factorLinkManifest.exists());
		assertTrue("diagram factor manifest doesnt exist?", diagramFactorManifest.exists());
		assertTrue("diagram link manifest doesnt exist?", diagramLinkManifest.exists());

		
		DataUpgrader upgrader = new DataUpgrader(tempDirectory);
		upgrader.upgradeToVersion21();
		
		ObjectManifest diagramLinkManifestObject = new ObjectManifest(JSONFile.read(diagramLinkManifest));
		assertEquals("diagram links not created?", 4, diagramLinkManifestObject.size());
		
		BaseId[] diagramLinks = diagramLinkManifestObject.getAllKeys();
		for (int i = 0; i < diagramLinks.length; ++i)
		{
			BaseId baseId = diagramLinks[i];
			String idAsString = Integer.toString(baseId.asInt());
			EnhancedJsonObject diagramLinkJson = DataUpgrader.readFile(new File(diagramLinkDir, idAsString));
			DiagramLink diagramLink = new DiagramLink(getObjectManager(), baseId.asInt(), diagramLinkJson);
			int fromId = diagramLink.getFromDiagramFactorId().asInt();
			int toId = diagramLink.getToDiagramFactorId().asInt();

			if (toId == rcTarget28Wrapper)
				assertEquals("the from is not correct?", rcTarget27Wrapper, fromId);
			
			if (toId == cmTarget13Wrapper)
				assertEquals("the to is not correct?", cmStrat17Wrapper, fromId);
		}
	
		File resultsChainFile26 = new File(resultsChainDir, resutlsChainIdAsString);
		EnhancedJsonObject resultsChainJSon = DataUpgrader.readFile(resultsChainFile26);
		String resultsChainLinksAsString = resultsChainJSon.getString("DiagramFactorLinkIds");
		IdList resultsChainLinks = new IdList(26, resultsChainLinksAsString);
		assertEquals("wrong results chain link size?", 2, resultsChainLinks.size());
		
		File conceptualModelFile = new File(conceptualModelDir, conceptualModelIdAsString);
		EnhancedJsonObject conceptualModelJson = DataUpgrader.readFile(conceptualModelFile);
		String conceptualModelLinksAsString = conceptualModelJson.getString("DiagramFactorLinkIds");
		IdList conceptualModelLinks = new IdList(19, conceptualModelLinksAsString);
		assertEquals("wrong conceptual model link size?", 2, conceptualModelLinks.size());
	}

	private void createObjectFile(File objectDir, String fileName, String objectString) throws Exception
	{
		File objectFile = new File(objectDir, fileName);
		createFile(objectFile, objectString);
	}
	
	public void testUpgradeTo20AddORefsInFactorLinks() throws Exception
	{
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();
		
		File intermediateResultsObjects = new File(jsonDir, "objects-23");
		intermediateResultsObjects.mkdirs();		
		
		ORef expectedFromRef2 = new ORef(23, new BaseId(115));
		int[] intermerdiateResultsIds = {115};
		File intermediateResultsObjectsManifest = createManifestFile(intermediateResultsObjects, intermerdiateResultsIds);
		assertTrue("factor manifest doesnt exist?", intermediateResultsObjectsManifest.exists());

		String intermediateResult =" {\"Type\":\"Intermediate Result\",\"Comment\":\"\",\"TimeStampModified\":\"1181600089656\",\"GoalIds\":\"\",\"KeyEcologicalAttributeIds\":\"\",\"IndicatorIds\":\"\",\"Label\":\"[ New Factor ]\",\"Id\":115,\"ObjectiveIds\":\"\"} ";
		File intermediateResultsFile = new File(intermediateResultsObjects, "115");
		createFile(intermediateResultsFile, intermediateResult);

		
		File factorObjects = new File(jsonDir, "objects-4");
		factorObjects.mkdirs();
		
		ORef expectedFromRef = new ORef(22, new BaseId(23));
		ORef expectedToRef = new ORef(22, new BaseId(45));
		int[] factorIds = {23, 45};
		File factorObjectsManifest = createManifestFile(factorObjects, factorIds);
		assertTrue("factor manifest doesnt exist?", factorObjectsManifest.exists());
		
		String targetString =" {\"Type\":\"Target\",\"CurrentStatusJustification\":\"\",\"ViabilityMode\":\"\",\"Comment\":\"\",\"TimeStampModified\":\"1181599939359\",\"GoalIds\":\"\",\"KeyEcologicalAttributeIds\":\"\",\"TargetStatus\":\"\",\"IndicatorIds\":\"\",\"Label\":\"New Target\",\"Id\":23,\"ObjectiveIds\":\"\"} ";
		String causeString =" {\"Type\":\"Target\",\"CurrentStatusJustification\":\"\",\"ViabilityMode\":\"\",\"Comment\":\"\",\"TimeStampModified\":\"1181599939359\",\"GoalIds\":\"\",\"KeyEcologicalAttributeIds\":\"\",\"TargetStatus\":\"\",\"IndicatorIds\":\"\",\"Label\":\"New Target\",\"Id\":45,\"ObjectiveIds\":\"\"} ";
		
		File targetFile = new File(factorObjects, "23");
		createFile(targetFile, targetString);
		
		File causeFile = new File(factorObjects, "45");
		createFile(causeFile, causeString);
			
		File linkObjects = new File(jsonDir, "objects-6");
		linkObjects.mkdirs();
		
		int[] linkIds = {2, 3};
		File linkObjectsManifest = createManifestFile(linkObjects, linkIds);
		assertTrue("link manifest doesnt exist?", linkObjectsManifest.exists());
		
		String linkString = " {\"FromId\":\"23\",\"ToId\":\"45\",\"TimeStampModified\":\"1181600089796\",\"Label\":\"\",\"StressLabel\":\"\",\"BidirectionalLink\":\"0\",\"Id\":2}  ";
		String link115to45 = " {\"FromId\":\"115\",\"ToId\":\"45\",\"TimeStampModified\":\"1181600089796\",\"Label\":\"\",\"StressLabel\":\"\",\"BidirectionalLink\":\"0\",\"Id\":3}  ";
		File linkFile = new File(linkObjects, "2");
		createFile(linkFile, linkString);
		
		File linkFile2 = new File(linkObjects, "3");
		createFile(linkFile2, link115to45);

		
		DataUpgrader upgrader = new DataUpgrader(tempDirectory);
		upgrader.changeLinkFromToIdsToORefs();
		
		EnhancedJsonObject json = new EnhancedJsonObject(readFile(linkFile));
		checkNewlyWrittenORef(expectedFromRef, json, "FromRef");		
		checkNewlyWrittenORef(expectedToRef, json, "ToRef");
		
		EnhancedJsonObject json2 = new EnhancedJsonObject(readFile(linkFile2));
		checkNewlyWrittenORef(expectedFromRef2, json2, "FromRef");
	}

	private void checkNewlyWrittenORef(ORef expectedFromRef, EnhancedJsonObject json, String tag)
	{
		try
		{
			EnhancedJsonObject fromRefAsString = json.getJson(tag);
			ORef retreivedFromRef = new ORef(fromRefAsString);
			assertEquals("wrong ref", retreivedFromRef, expectedFromRef);
		}
		catch (NoSuchElementException ignore)
		{
			fail("ref does not exist in link?");	
		}
	}
	
	public void testUpgradeTo19RemovingGoalIdsFromIndicators() throws Exception
	{
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();
		
		File objectsIndicator = new File(jsonDir, "objects-8");
		objectsIndicator.mkdirs();

		int[] indicatorIds = {33};
		File objectsIndicatorManifestFile = createManifestFile(objectsIndicator, indicatorIds);
		assertTrue("indicator manifest doesnt exist?", objectsIndicatorManifestFile.exists());

		File objectsGoals = new File(jsonDir, "objects-10");
		objectsGoals.mkdirs();
		
		int[] rawGoalIds = {44, 55};
		File objectsGoalManifestFile = createManifestFile(objectsGoals, rawGoalIds);
		assertTrue("goal manifest doesnt exist?", objectsGoalManifestFile.exists());

		String indicator33 = " {\"Status\":\"\",\"MeasurementDate\":\"\",\"RatingSource\":\"\",\"MeasurementStatus\":\"\",\"ShortLabel\":\"\",\"GoalIds\":\"{\\\"Ids\\\":[44]}\",\"MeasurementDetail\":\"\",\"Priority\":\"\",\"Label\":\"24\",\"MeasurementTrend\":\"\",\"MeasurementStatusConfidence\":\"\",\"MeasurementSummary\":\"\",\"TaskIds\":\"\",\"IndicatorThresholds\":\"\",\"Id\":33} ";
		File indicator33File = new File(objectsIndicator, "33");
		createFile(indicator33File, indicator33);
		
		String goal44 = "{\"ShortLabel\":\"\",\"FullText\":\"\",\"ByWhen\":\"\",\"DesiredStatus\":\"\",\"DesiredDetail\":\"\",\"Label\":\"\",\"DesiredSummary\":\"\",\"Id\":44}";
		File goal44File = new File(objectsGoals, "44");
		createFile(goal44File, goal44);
		
		DataUpgrader upgrader = new DataUpgrader(tempDirectory);
		upgrader.removeGoalsFromIndicators();
		
		EnhancedJsonObject json = new EnhancedJsonObject(readFile(indicator33File));
		String goalIdsAsString = json.getString("GoalIds");
		IdList goalIds = new IdList(10, goalIdsAsString);
		assertEquals("has no goals", 0, goalIds.size());
		
		File goalDirManifest = new File(objectsGoals, "manifest");
		ObjectManifest manifest10 = new ObjectManifest(JSONFile.read(goalDirManifest));
		BaseId[] allGoalIds = manifest10.getAllKeys();
		assertEquals("failed to delete one goal?", 1, allGoalIds.length);
		assertEquals("failed to delete correct goal?", 55, allGoalIds[0].asInt());
	}
	
	public void testUpgradeTo18AddingLinksToObject19() throws Exception
	{
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();

		File objects13 = new File(jsonDir, "objects-13");
		objects13.mkdirs();
		
		String objects13ManifestContent = " {\"Type\":\"ObjectManifest\",\"20\":true,\"21\":true}";
		File objects13ManifestFile = new File(objects13, "manifest");
		createFile(objects13ManifestFile, objects13ManifestContent);
		assertTrue("manifest doesnt exist?", objects13ManifestFile.exists());
		
		File objects19 = new File(jsonDir, "objects-19");
		objects19.mkdirs();
		
		String objects19Content = " {\"Label\":\"\",\"DiagramFactorIds\":\"{\\\"Ids\\\":[1,2,3]}\",\"Id\":30}";
		File file30 = new File(objects19, "30");
		createFile(file30, objects19Content);
		
		createManifestFile(objects19, new int[] {30});
		
		DataUpgrader upgrader = new DataUpgrader(tempDirectory);
		upgrader.upgradeToVersion18();
		
		File newFile30 = new File(objects19, "30");
		EnhancedJsonObject readIn30 = JSONFile.read(newFile30);

		String factorIdsAsString = readIn30.getString("DiagramFactorIds");
		IdList diagramFactorIds = new IdList(18, factorIdsAsString);
		assertEquals("same size?", 3, diagramFactorIds.size());
		assertContains(1, diagramFactorIds.toIntArray());
		assertContains(2, diagramFactorIds.toIntArray());
		assertContains(3, diagramFactorIds.toIntArray());
		
		String linkIdsAsString = readIn30.getString("DiagramFactorLinkIds");
		IdList diagramFactorLinkIds = new IdList(13, linkIdsAsString);
		assertEquals("same size?", 2, diagramFactorLinkIds.size());
		assertContains(20, diagramFactorLinkIds.toIntArray());
		assertContains(21, diagramFactorLinkIds.toIntArray());
	}
	
	public void testUpgradeTo17creatingObjects19FromDiagramsMainFile() throws Exception
	{
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();

		File diagramsDir = new File(jsonDir, "diagrams");
		diagramsDir.mkdirs();
		
		String diagramFactorIds = " {\"Type\":\"Diagram\",\"DiagramFactorIds\":{\"Ids\":[676,691,664]}} ";
		File diagramMain = new File(diagramsDir, "main");
		createFile(diagramMain, diagramFactorIds);
		
		File projectFile = new File(jsonDir, "project");
		createFile(projectFile, "{\"HighestUsedNodeId\":13}");
		
		DataUpgrader upgraderWithNoObjects19 = new DataUpgrader(tempDirectory);
		upgraderWithNoObjects19.upgradeToVersion17();
		
		File objects19Dir = new File(jsonDir, "objects-19");
		assertTrue("didn't create objects-19 dir?", objects19Dir.exists());
		
		File manifest19File = new File(objects19Dir, "manifest");
		assertTrue("didn't create manifest file?", manifest19File.exists());
		
		String expectedManifestContent = "{\"Type\":\"ObjectManifest\",\"14\":true}";
		String migratedManifestContents = readFile(manifest19File);
		assertEquals("manifest contents wrong?", expectedManifestContent.trim(), migratedManifestContents.trim());
		
		File object14File = new File(objects19Dir, "14");
		assertTrue("didn't create object 14 file?", object14File.exists());
		
		EnhancedJsonObject json = new EnhancedJsonObject(readFile(object14File));
		int id = json.getInt("Id");
		assertEquals("wrong object id?", id, 14);
		ConceptualModelDiagram diagramContents = new ConceptualModelDiagram(getObjectManager(), id, json);
		IdList allDiagramFactorIds = diagramContents.getAllDiagramFactorIds();
		assertEquals("wrong id count?", 3, allDiagramFactorIds.size());
		
		assertTrue("missing 676?", allDiagramFactorIds.contains(new BaseId(676)));
		assertTrue("missing 691?", allDiagramFactorIds.contains(new BaseId(691)));
		assertTrue("missing 664?", allDiagramFactorIds.contains(new BaseId(664)));
	}
	
	public void testCreateDiagramFactorLinksFromRawFactorLinks() throws Exception
	{
		String factorLink1 ="{\"FromId\":\"28\",\"ToId\":\"29\",\"Label\":\"\",\"StressLabel\":\"\",\"Id\":56}";
		String factorLink2 ="{\"FromId\":\"30\",\"ToId\":\"31\",\"Label\":\"\",\"StressLabel\":\"\",\"Id\":57}";
		int[] factorLinkIds = new int[2];
		factorLinkIds[0] = 56;
		factorLinkIds[1] = 57;
		
		HashMap factorToDiamgramFactorIdMap = new HashMap();
		factorToDiamgramFactorIdMap.put(new Integer(28), new Integer(91));
		factorToDiamgramFactorIdMap.put(new Integer(29), new Integer(92));
	
		factorToDiamgramFactorIdMap.put(new Integer(30), new Integer(93));
		factorToDiamgramFactorIdMap.put(new Integer(31), new Integer(94));
		
		
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();
		
		File objects6Dir = new File(jsonDir, "objects-6");
		objects6Dir.mkdirs();
		
		File factor56File = new File(objects6Dir, "56");
		createFile(factor56File, factorLink1);
		assertTrue(factor56File.exists());
		
		File factor57File = new File(objects6Dir, "57");
		createFile(factor57File, factorLink2);
		assertTrue(factor57File.exists());
		
		String manifestContent = buildManifestContents(factorLinkIds);
		File manifestFile = new File(objects6Dir, "manifest");
		createFile(manifestFile, manifestContent);
		assertTrue(manifestFile.exists());
		
		File projectFile = new File(jsonDir, "project");
		createFile(projectFile, "{\"HighestUsedNodeId\":134}");
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		dataUpgrader.createDiagramFactorLinksFromRawFactorLinks(factorToDiamgramFactorIdMap);
		
		File objects13Dir = new File(jsonDir, "objects-13");
		assertTrue("objects-13 dir does not exist?", objects13Dir.exists());
		
		File manifest13File = new File(objects13Dir, "manifest");
		assertTrue("manifest exists?", manifest13File.exists());
		
		File file1 = new File(objects13Dir, "135");
		assertTrue("diagram link file 135 exists?", file1.exists());
		
		EnhancedJsonObject json1 = new EnhancedJsonObject(readFile(file1));
		DiagramLink diagramLink = new DiagramLink(getObjectManager(), 135, json1);
		assertEquals("same wrapped id?", 57, diagramLink.getWrappedId().asInt());
		String fromDiagramLinkId = diagramLink.getData(DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID);
		String toDiagramLinkId = diagramLink.getData(DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID);
		assertEquals("same from diagram link id?", Integer.toString(93), fromDiagramLinkId);
		assertEquals("same from diagram link id?", Integer.toString(94), toDiagramLinkId);
	
		
		File file2 = new File(objects13Dir, "136");
		assertTrue("diagram link file 136 exists?", file2.exists());
		
		EnhancedJsonObject json2 = new EnhancedJsonObject(readFile(file2));
		DiagramLink diagramLink2 = new DiagramLink(getObjectManager(), 136, json2);
		assertEquals("same wrapped id?", 56, diagramLink2.getWrappedId().asInt());
		String fromDiagramLinkId2 = diagramLink2.getData(DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID);
		String toDiagramLinkId2 = diagramLink2.getData(DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID);
		assertEquals("same from diagram link id?", Integer.toString(91), fromDiagramLinkId2);
		assertEquals("same from diagram link id?", Integer.toString(92), toDiagramLinkId2);
	
	
	}
	
	public void testCreateDiagramFactorsFromRawFactors() throws Exception
	{
 		String allFactorInfos = " {\"Nodes\":{\"28\":{\"Size\":{\"Width\":120,\"Height\":60},\"WrappedId\":28,\"Location\":{\"Y\":270,\"X\":120},\"Id\":28},\"29\":{\"Size\":{\"Width\":151,\"Height\":60},\"WrappedId\":29,\"Location\":{\"Y\":15,\"X\":375},\"Id\":29}},\"Type\":\"Diagram\"}  ";
		String expected91Content = "{\"Size\":\"{\\\"Width\\\":120,\\\"Height\\\":60}\",\"WrappedFactorId\":\"28\",\"Location\":\"{\\\"Y\\\":270,\\\"X\\\":120}\",\"Id\":91}";
		String expected92Content = "{\"Size\":\"{\\\"Width\\\":151,\\\"Height\\\":60}\",\"WrappedFactorId\":\"29\",\"Location\":\"{\\\"Y\\\":15,\\\"X\\\":375}\",\"Id\":92}";
		String expectedManifestContent = "{\"Type\":\"ObjectManifest\",\"91\":true,\"92\":true}";
		
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();
		
		File projectFile = new File(jsonDir, "project");
		createFile(projectFile, "{\"HighestUsedNodeId\":90}");
		
		File diagramsDir =  new File(jsonDir, "diagrams");
		diagramsDir.mkdirs();
		
		File diagramMainFile = new File(diagramsDir, "main");
		createFile(diagramMainFile, allFactorInfos);
		
		DataUpgrader dataUpgrader = new DataUpgrader(tempDirectory);
		dataUpgrader.createDiagramFactorsFromRawFactors();
		
		File objects18Dir = new File(jsonDir, "objects-18");
		assertTrue("objects-18 dir does not exist?", objects18Dir.exists());
		
		File file1 = new File(objects18Dir, "91");
		File file2 = new File(objects18Dir, "92");
		assertTrue("file 91 exists?", file1.exists());
		assertTrue("file 92 exists?", file2.exists());
		
		EnhancedJsonObject readIn1 = JSONFile.read(file1);
		String factorIdsAsString = readIn1.getString("WrappedFactorId");
		BaseId wrappedId1 = new BaseId(factorIdsAsString);
		Dimension size1 = EnhancedJsonObject.convertToDimension(readIn1.getString("Size"));
		Point location1 = EnhancedJsonObject.convertToPoint(readIn1.getString("Location")); 
		assertEquals(wrappedId1.asInt(),  28);
		assertEquals(new Dimension(120,60),	size1);
		assertEquals(new Point(120, 270), location1);

		EnhancedJsonObject readIn2 = JSONFile.read(file2);
		String factorIdsAsString2 = readIn2.getString("WrappedFactorId");
		BaseId wrappedId2 = new BaseId(factorIdsAsString2);
		Dimension size2 = EnhancedJsonObject.convertToDimension(readIn2.getString("Size"));
		Point location2 = EnhancedJsonObject.convertToPoint(readIn2.getString("Location")); 
		assertEquals(wrappedId2.asInt(), 29);
		assertEquals(new Dimension(151,60),	size2);
		assertEquals(new Point(375, 15), location2);
		
		String file91Content = readFile(file1);
		String file92Content = readFile(file2);
		assertEquals("file 91 content the same?", new EnhancedJsonObject(expected91Content), new EnhancedJsonObject(file91Content));
		assertEquals("file 92 content the same?", new EnhancedJsonObject(expected92Content), new EnhancedJsonObject(file92Content));
		
		File manifestFile = new File(objects18Dir, "manifest");
		String migratedManifestContents = readFile(manifestFile);
		assertTrue("has manifest file?", manifestFile.exists());
		assertEquals("manifests has same content?", expectedManifestContent.trim(), migratedManifestContents.trim());
	}

	private String readFile(File file) throws IOException
	{
		UnicodeReader reader = new UnicodeReader(file);
		String contents = reader.readAll();
		reader.close();
		return contents;
	}
	
	private File createManifestFile(File parent, int[] ids) throws Exception
	{
		return DataUpgrader.createManifestFile(parent, ids);
	}
	
	private String buildManifestContents(int[] ids)
	{
		return DataUpgrader.buildManifestContents(ids);
	}
	
	private void createFile(File file, String contents) throws Exception
	{
		DataUpgrader.createFile(file, contents);
	}
	
	private File createJsonDir()
	{
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();
		
		return jsonDir;
	}
	
	private Project getProject()
	{
		return project;
	}
	
	private ObjectManager getObjectManager()
	{
		return getProject().getObjectManager();
	}
	
	public static IdAssigner idAssigner = new IdAssigner();
	File tempDirectory;
	ProjectForTesting project;
}
