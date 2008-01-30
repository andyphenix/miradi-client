package org.conservationmeasures.eam.objects;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.conservationmeasures.eam.database.ProjectServer;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.ids.KeyEcologicalAttributeId;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.CreateFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objectpools.EAMObjectPool;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.questions.KeyEcologicalAttributeTypeQuestion;
import org.conservationmeasures.eam.questions.ViabilityModeQuestion;
import org.martus.util.DirectoryUtils;


public class TestObjectManager extends EAMTestCase
{
	public TestObjectManager(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		project = new ProjectForTesting(getName());
		manager = project.getObjectManager();		
		db = project.getDatabase();
	}
	
	public void tearDown() throws Exception
	{
		project.close();
		project = null;
	}

	public void testObjectLifecycles() throws Exception
	{
		int[] types = new int[] {
			ObjectType.RATING_CRITERION, 
			ObjectType.VALUE_OPTION,  
			ObjectType.VIEW_DATA, 
			ObjectType.PROJECT_RESOURCE,
			ObjectType.INDICATOR,
			ObjectType.OBJECTIVE,
			ObjectType.GOAL,
			ObjectType.PROJECT_METADATA,
		};
		
		for(int i = 0; i < types.length; ++i)
		{
			verifyObjectLifecycle(types[i]);
		}
		
		verifyObjectLifecycle(ObjectType.CAUSE);
		
		DiagramFactor cause = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
		DiagramFactor target = project.createDiagramFactorAndAddToDiagram(ObjectType.TARGET);		
		CreateFactorLinkParameter link = new CreateFactorLinkParameter(cause.getWrappedORef(), target.getWrappedORef());
		verifyBasicObjectLifecycle(ObjectType.FACTOR_LINK, link);
	}

	public void testPseudoTagTargetViability() throws Exception
	{
		String NOT_SPECIFIED = "";
		String FAIR = "2";
		String sampleStatusCode = FAIR;

		FactorId targetId = project.createFactorAndReturnId(ObjectType.TARGET);
		Target target = (Target)project.findNode(targetId);
		target.setData(Target.TAG_TARGET_STATUS, sampleStatusCode);
		
		String simple = project.getObjectData(target.getRef(), Target.PSEUDO_TAG_TARGET_VIABILITY);
		assertEquals("Didn't return simple viability?", sampleStatusCode, simple);
		
		target.setData(Target.TAG_VIABILITY_MODE, ViabilityModeQuestion.TNC_STYLE_CODE);
		String notRated = project.getObjectData(target.getRef(), Target.PSEUDO_TAG_TARGET_VIABILITY);
		assertEquals("Didn't return detailed viability?", NOT_SPECIFIED, notRated);
		
		Indicator condition1Indicator = createIndicator(FAIR);
		KeyEcologicalAttribute conditionKea = createKEA(new Indicator[] {condition1Indicator});

		IdList keas = new IdList(KeyEcologicalAttribute.getObjectType());
		keas.add(conditionKea.id);
		target.setData(Target.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS, keas.toString());

		String keaWithoutCategory = project.getObjectData(target.getRef(), Target.PSEUDO_TAG_TARGET_VIABILITY);
		assertEquals("Included uncategorized KEA?", NOT_SPECIFIED, keaWithoutCategory);

		conditionKea.setData(KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE, KeyEcologicalAttributeTypeQuestion.CONDITION);
		String fair = project.getObjectData(target.getRef(), Target.PSEUDO_TAG_TARGET_VIABILITY);
		assertEquals("Didn't compute for one kea one indicator?", FAIR, fair);
		
	}

	private Indicator createIndicator(String status) throws Exception
	{
		ORef indicatorRef = project.createObject(Indicator.getObjectType());
		ORef measurementRef = project.createObject(Measurement.getObjectType());
		project.setObjectData(measurementRef, Measurement.TAG_STATUS, status);
		ORefList measurementRefs = new ORefList();
		measurementRefs.add(measurementRef);
		
		project.setObjectData(indicatorRef, Indicator.TAG_MEASUREMENT_REFS, measurementRefs.toString());
		return (Indicator)project.findObject(indicatorRef);
	}

	private KeyEcologicalAttribute createKEA(Indicator[] indicators) throws Exception
	{
		KeyEcologicalAttributeId keaId1 = (KeyEcologicalAttributeId)project.createObjectAndReturnId(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE);
		KeyEcologicalAttribute kea = (KeyEcologicalAttribute)project.findObject(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, keaId1);

		IdList indicatorIds = new IdList(Indicator.getObjectType());
		for(int i = 0; i < indicators.length; ++i)
			indicatorIds.add(indicators[i].getId());
		kea.setData(KeyEcologicalAttribute.TAG_INDICATOR_IDS, indicatorIds.toString());
		
		return kea;
	}
	
	public void testComputeTNCViabilityOfKEA() throws Exception
	{
		String FAIR = "2";
		String GOOD = "3";
		String VERY_GOOD = "4";

		Indicator fair = createIndicator(FAIR);
		Indicator veryGood = createIndicator(VERY_GOOD);
		KeyEcologicalAttribute kea = createKEA(new Indicator[] {fair, veryGood});
		assertEquals(GOOD, kea.getData(KeyEcologicalAttribute.PSEUDO_TAG_VIABILITY_STATUS));
	}
	
	private void verifyObjectLifecycle(int type) throws Exception
	{
		verifyBasicObjectLifecycle(type, null);
		verifyObjectWriteAndRead(type, null);
		verifyGetPool(type);
	}
	
	private void verifyBasicObjectLifecycle(int type, CreateObjectParameter parameter) throws Exception, IOException, ParseException
	{
		BaseId createdId = manager.createObject(type, BaseId.INVALID, parameter);
		assertNotEquals(type + " Created with invalid id", BaseId.INVALID, createdId);
		db.readObject(manager, type, createdId);
		
		String tag = RatingCriterion.TAG_LABEL;
		manager.setObjectData(type, createdId, tag, "data");
		BaseObject withData = db.readObject(manager, type, createdId);
		assertEquals(type + " didn't write/read data for " + tag + "?", "data", withData.getData(tag));
		assertEquals(type + " can't get data from project?", "data", manager.getObjectData(type, createdId, tag));
		
		manager.deleteObject(withData);
		try
		{
			manager.getObjectData(type, createdId, tag);
			fail(type + " Should have thrown getting data from deleted object");
		}
		catch(Exception ignoreExpected)
		{
		}
		
		try
		{
			db.readObject(manager, type, createdId);
			fail(type + " Should have thrown reading deleted object");
		}
		catch(Exception ignoreExpected)
		{
		}
		
		BaseId desiredId = new BaseId(2323);
		assertEquals(type + " didn't use requested id?", desiredId, manager.createObject(type, desiredId, parameter));
	}

	private void verifyObjectWriteAndRead(int type, CreateObjectParameter parameter) throws IOException, Exception
	{
		File tempDirectory = createTempDirectory();
		try
		{
			Project projectToWrite = new Project();
			projectToWrite.createOrOpen(tempDirectory);
			BaseId idToReload = projectToWrite.createObject(type, BaseId.INVALID, parameter);
			projectToWrite.close();
			
			Project projectToRead = new Project();
			projectToRead.createOrOpen(tempDirectory);
			try
			{
				projectToRead.getObjectData(type, idToReload, BaseObject.TAG_LABEL);
			}
			catch (NullPointerException e)
			{
				fail("Didn't reload object from disk, type: " + type + " (did the pool get loaded?)");
			}
			projectToRead.close();
		}
		finally
		{
			DirectoryUtils.deleteEntireDirectoryTree(tempDirectory);
		}
	}
	
	private void verifyGetPool(int type) throws Exception
	{
		CreateObjectParameter cop = null;
		if(type == ObjectType.FACTOR_LINK)
		{
			DiagramFactor cause1 = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
			DiagramFactor cause2 = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
			cop = new CreateFactorLinkParameter(cause1.getWrappedORef(), cause2.getWrappedORef());			
		}
			
		BaseId createdId = manager.createObject(type, BaseId.INVALID, cop);
		EAMObjectPool pool = manager.getPool(type);
		assertNotNull("Missing pool type " + type, pool);
		BaseObject created = (BaseObject)pool.getRawObject(createdId);
		assertNotNull("Pool doesn't have object type " + created);
	}
	
	ProjectForTesting project;
	ObjectManager manager;
	ProjectServer db;
}
