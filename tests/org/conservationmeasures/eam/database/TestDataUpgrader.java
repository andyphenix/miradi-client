/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.database;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.ConceptualModelDiagram;
import org.conservationmeasures.eam.objects.DiagramLink;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;
import org.martus.util.DirectoryUtils;
import org.martus.util.UnicodeReader;

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
	}
	
	public void tearDown() throws Exception
	{
		DirectoryUtils.deleteEntireDirectoryTree(tempDirectory);
	
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
	
	public void testUpdateTo23CreateMeasurementFromDataInIndicator() throws Exception
	{
		String indicatorWithMeasurementData = "{\"MeasurementDate\":\"2007-10-02\",\"Status\":\"\",\"MeasurementStatus\":\"\",\"RatingSource\":\"\",\"ShortLabel\":\"1111111111\",\"MeasurementDetail\":\"CS detail text\",\"Priority\":\"\",\"FutureStatusRating\":\"\",\"MeasurementRefs\":\"\",\"Label\":\"1111111111\",\"MeasurementTrend\":\"Unknown\",\"FutureStatusSummary\":\"\",\"MeasurementStatusConfidence\":\"RapidAssessment\",\"TimeStampModified\":\"1193620397190\",\"MeasurementSummary\":\"CS summary label\",\"TaskIds\":\"\",\"IndicatorThresholds\":\"\",\"FutureStatusDetail\":\"\",\"FutureStatusDate\":\"\",\"Id\":17}";
		String indicatorWithoutMeasurementData = "{\"MeasurementDate\":\"\",\"Status\":\"\",\"MeasurementStatus\":\"\",\"RatingSource\":\"\",\"ShortLabel\":\"\",\"MeasurementDetail\":\"\",\"Priority\":\"\",\"FutureStatusRating\":\"\",\"MeasurementRefs\":\"\",\"Label\":\"no CS data\",\"MeasurementTrend\":\"\",\"FutureStatusSummary\":\"\",\"MeasurementStatusConfidence\":\"\",\"TimeStampModified\":\"1193620417567\",\"MeasurementSummary\":\"\",\"TaskIds\":\"\",\"IndicatorThresholds\":\"\",\"FutureStatusDetail\":\"\",\"FutureStatusDate\":\"\",\"Id\":18}";
		File jsonDir = new File(tempDirectory, "json");
		jsonDir.mkdirs();
		
		File projectFile = new File(jsonDir, "project");
		createFile(projectFile, "{\"HighestUsedNodeId\":18}");
		
		File indicatorDir = createObjectsDir(jsonDir, "objects-8");
		indicatorDir.mkdirs();
		
		int[] indicatorIds = {17, 18};
		File indicatorManifestFile = createManifestFile(indicatorDir, indicatorIds);
		assertTrue(indicatorManifestFile.exists());
		
		File indicator17WithMeasurementDataFile = new File(indicatorDir, Integer.toString(indicatorIds[0]));
		createFile(indicator17WithMeasurementDataFile, indicatorWithMeasurementData);
		assertTrue(indicator17WithMeasurementDataFile.exists());
		
		File indicator18WithoutMeasurementDataFile = new File(indicatorDir, Integer.toString(indicatorIds[1]));
		createFile(indicator18WithoutMeasurementDataFile, indicatorWithoutMeasurementData);
		assertTrue(indicator18WithoutMeasurementDataFile.exists());

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
			DiagramLink diagramLink = new DiagramLink(baseId.asInt(), diagramLinkJson);
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
		IdList resultsChainLinks = new IdList(resultsChainLinksAsString);
		assertEquals("wrong results chain link size?", 2, resultsChainLinks.size());
		
		File conceptualModelFile = new File(conceptualModelDir, conceptualModelIdAsString);
		EnhancedJsonObject conceptualModelJson = DataUpgrader.readFile(conceptualModelFile);
		String conceptualModelLinksAsString = conceptualModelJson.getString("DiagramFactorLinkIds");
		IdList conceptualModelLinks = new IdList(conceptualModelLinksAsString);
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
		IdList goalIds = new IdList(goalIdsAsString);
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
		IdList diagramFactorIds = new IdList(factorIdsAsString);
		assertEquals("same size?", 3, diagramFactorIds.size());
		assertContains(1, diagramFactorIds.toIntArray());
		assertContains(2, diagramFactorIds.toIntArray());
		assertContains(3, diagramFactorIds.toIntArray());
		
		String linkIdsAsString = readIn30.getString("DiagramFactorLinkIds");
		IdList diagramFactorLinkIds = new IdList(linkIdsAsString);
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
		ConceptualModelDiagram diagramContents = new ConceptualModelDiagram(id, json);
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
		DiagramLink diagramLink = new DiagramLink(135, json1);
		assertEquals("same wrapped id?", 57, diagramLink.getWrappedId().asInt());
		String fromDiagramLinkId = diagramLink.getData(DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID);
		String toDiagramLinkId = diagramLink.getData(DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID);
		assertEquals("same from diagram link id?", Integer.toString(93), fromDiagramLinkId);
		assertEquals("same from diagram link id?", Integer.toString(94), toDiagramLinkId);
	
		
		File file2 = new File(objects13Dir, "136");
		assertTrue("diagram link file 136 exists?", file2.exists());
		
		EnhancedJsonObject json2 = new EnhancedJsonObject(readFile(file2));
		DiagramLink diagramLink2 = new DiagramLink(136, json2);
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

	public static IdAssigner idAssigner = new IdAssigner();
	File tempDirectory;
}
