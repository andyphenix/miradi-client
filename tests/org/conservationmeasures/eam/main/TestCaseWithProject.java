package org.conservationmeasures.eam.main;

import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.project.ProjectForTesting;

public class TestCaseWithProject extends EAMTestCase
{
	public TestCaseWithProject(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
	}

	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
		project = null;
	}

	public ProjectForTesting getProject()
	{
		return project;
	}
	
	public ObjectManager getObjectManager()
	{
		return getProject().getObjectManager();
	}
	
	private ProjectForTesting project;
}
