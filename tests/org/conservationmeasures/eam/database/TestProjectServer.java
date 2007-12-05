/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.database;

import java.io.File;
import java.io.IOException;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Cause;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.objects.RatingCriterion;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.project.ProjectServerForTesting;
import org.conservationmeasures.eam.project.ThreatRatingBundle;
import org.json.JSONObject;
import org.martus.util.DirectoryUtils;
import org.martus.util.UnicodeWriter;

public class TestProjectServer extends EAMTestCase
{
	public TestProjectServer(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		storage = new ProjectServerForTesting();
		storage.openMemoryDatabase(getName());
		idAssigner = new IdAssigner();
		project = new ProjectForTesting(getName());
	}
	
	public void tearDown() throws Exception
	{
		project.close();
		storage.close();
	}
	
	public void testObjectManifest() throws Exception
	{
		BaseId[] idsToWrite = {new BaseId(19), new BaseId(25), new BaseId(727), };
		for(int i = 0; i < idsToWrite.length; ++i)
		{
			Task task = new Task(idsToWrite[i]);
			storage.writeObject(task);
		}
		RatingCriterion criterion = new RatingCriterion(new BaseId(99));
		storage.writeObject(criterion);

		ObjectManifest manifest = storage.readObjectManifest(ObjectType.TASK);
		assertEquals("wrong number of objects?", idsToWrite.length, manifest.size());
		for(int i = 0; i < idsToWrite.length; ++i)
			assertTrue("missing id " + idsToWrite[i], manifest.has(idsToWrite[i]));
		
		storage.deleteObject(ObjectType.TASK, idsToWrite[0]);
		Manifest afterDelete = storage.readObjectManifest(ObjectType.TASK);
		assertFalse("didn't delete id?", afterDelete.has(idsToWrite[0]));
	}
	
	public void testWriteAndReadNode() throws Exception
	{

		Strategy intervention = new Strategy(takeNextModelNodeId());
		storage.writeObject(intervention);
		Strategy gotIntervention = (Strategy)readNode(intervention.getId());
		assertEquals("not a strategy?", intervention.getNodeType(), gotIntervention.getNodeType());
		assertEquals("wrong id?", intervention.getId(), gotIntervention.getId());

		Cause factor = new Cause(project.getObjectManager(), takeNextModelNodeId());
		
		storage.writeObject(factor);
		Cause gotContributingFactor = (Cause)readNode(factor.getId());
		assertEquals("not indirect factor?", factor.getNodeType(), gotContributingFactor.getNodeType());
		
		Target target = new Target(takeNextModelNodeId());
		storage.writeObject(target);
		Target gotTarget = (Target)readNode(target.getId());
		assertEquals("not a target?", target.getNodeType(), gotTarget.getNodeType());
		
		
		ObjectManifest nodeIds = storage.readObjectManifest(ObjectType.FACTOR);
		assertEquals("not three nodes?", 3, nodeIds.size());
		assertTrue("missing a node?", nodeIds.has(target.getId()));
	}
	
	private FactorId takeNextModelNodeId()
	{
		return new FactorId(idAssigner.takeNextId().asInt());
	}
	
	private Factor readNode(BaseId id) throws Exception
	{
		return (Factor)storage.readObject(project.getObjectManager(), ObjectType.FACTOR, id);
	}
	
	public void testWriteAndReadLinkage() throws Exception
	{
		ORef fromRef = new ORef(ObjectType.FACTOR, new FactorId(2));
		ORef toRef = new ORef(ObjectType.FACTOR, new FactorId(3));
		FactorLink original = new FactorLink(new FactorLinkId(1), fromRef, toRef);
		storage.writeObject(original);
		FactorLink got = (FactorLink)storage.readObject(project.getObjectManager(), original.getType(), original.getId());
		assertEquals("wrong id?", original.getId(), got.getId());
		assertEquals("wrong from?", original.getFromFactorRef(), got.getFromFactorRef());
		assertEquals("wrong to?", original.getToFactorRef(), got.getToFactorRef());
		
		ObjectManifest linkageIds = storage.readObjectManifest(original.getType());
		assertEquals("not one link?", 1, linkageIds.size());
		assertTrue("wrong link id in manifest?", linkageIds.has(original.getId()));
		
		storage.writeObject(original);
		assertEquals("dupe in manifest?", 1, storage.readObjectManifest(original.getType()).size());
		
	}
	
	public void testDeleteLinkage() throws Exception
	{
		ORef fromRef = new ORef(ObjectType.FACTOR, new FactorId(2));
		ORef toRef = new ORef(ObjectType.FACTOR, new FactorId(3));
		FactorLink original = new FactorLink(new FactorLinkId(1), fromRef, toRef);
		storage.writeObject(original);
		storage.deleteObject(original.getType(), original.getId());
		assertEquals("didn't delete?", 0, storage.readObjectManifest(original.getType()).size());
		try
		{
			storage.readObject(project.getObjectManager(), original.getType(), original.getId());
		}
		catch(IOException ignoreExpected)
		{
		}
	}
	
	public void testWriteThreatRatingBundle() throws Exception
	{
		FactorId threatId = new FactorId(68);
		FactorId targetId = new FactorId(99);
		BaseId defaultId = new BaseId(929);
		ThreatRatingBundle bundle = new ThreatRatingBundle(threatId, targetId, defaultId);
		storage.writeThreatRatingBundle(bundle);
		
		ThreatRatingBundle got = storage.readThreatRatingBundle(threatId, targetId);
		assertEquals("didn't read?", defaultId, got.getValueId(new BaseId(38838)));
		
		assertNull("didn't return null for non-existent bundle?", storage.readThreatRatingBundle(new BaseId(282), new BaseId(2995)));
	}
	
	public void testReadAndWriteThreatRatingFramework() throws Exception
	{
		ProjectServerForTesting db = project.getTestDatabase();
		db.writeThreatRatingFramework(project.getSimpleThreatRatingFramework());
		JSONObject got = db.readRawThreatRatingFramework();
		assertEquals(got.toString(), project.getSimpleThreatRatingFramework().toJson().toString());
		project.close();
	}
	
	public void testCreateInNonEmptyDirectory() throws Exception
	{
		File tempDirectory = createTempDirectory();
		File anyFile = new File(tempDirectory, "blah");
		anyFile.mkdirs();
		try
		{
			storage.create(tempDirectory);
			fail("Should have thrown");
		}
		catch (Exception ignoreExpected)
		{
		}
		finally
		{
			DirectoryUtils.deleteEntireDirectoryTree(tempDirectory);
		}
	}
	
	public void testOpenNonProjectDirectory() throws Exception
	{
		File tempDirectory = createTempDirectory();
		try
		{
			File nonProjectFile = new File(tempDirectory, "foo");
			UnicodeWriter writer = new UnicodeWriter(nonProjectFile);
			writer.writeln("nothing");
			writer.close();
			
			assertFalse("project exists?", ProjectServer.isExistingProject(tempDirectory));
			ProjectServer anotherStorage = new FileBasedProjectServer();
			try
			{
				anotherStorage.open(tempDirectory);
				fail("Should have thrown trying to open non-empty, non-project directory");
			}
			catch(IOException ignoreExpected)
			{
			}
			
			File nonExistantProjectDirectory = new File(tempDirectory, "DoesNotExist");
			try
			{
				anotherStorage.open(nonExistantProjectDirectory);
				fail("Should throw when opening a non-existant directory");
			}
			catch (Exception ignoreExpected)
			{
			}
			anotherStorage.close();
		}
		finally
		{
			DirectoryUtils.deleteEntireDirectoryTree(tempDirectory);
		}
	}

	IdAssigner idAssigner;
	private ProjectServerForTesting storage;
	private ProjectForTesting project;
}
