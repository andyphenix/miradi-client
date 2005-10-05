/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.main;

import java.io.File;

import org.conservationmeasures.eam.testall.EAMTestCase;
import org.martus.util.DirectoryUtils;

public class TestRealProject extends EAMTestCase
{
	public TestRealProject(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		projectDirectory = createTempDirectory();
	}
	
	public void tearDown() throws Exception
	{
		DirectoryUtils.deleteEntireDirectoryTree(projectDirectory);
		super.tearDown();
	}

	public void testIsOpen() throws Exception
	{
		FileStorage storage = new FileStorage();
		storage.setDirectory(projectDirectory);
		storage.createEmpty();
		
		RealProject project = new RealProject();
		assertFalse("already open?", project.isOpen());
		project.load(projectDirectory);
		assertTrue("not open?", project.isOpen());
		project.close();
		assertFalse("still open?", project.isOpen());
	}
	
	File projectDirectory;
}
