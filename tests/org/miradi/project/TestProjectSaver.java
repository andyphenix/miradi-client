/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.project;

import org.martus.util.UnicodeStringWriter;
import org.miradi.main.TestCaseWithProject;

public class TestProjectSaver extends TestCaseWithProject
{
	public TestProjectSaver(String name)
	{
		super(name);
	}
	
	public void testBasics() throws Exception
	{
		getProject().populateEverything();
		getProject().populateSimpleThreatRatingValues();
		
		UnicodeStringWriter writer = UnicodeStringWriter.create();
		try
		{
			long millis = ProjectSaver.saveProject(getProject(), writer);
			writer.flush();
			
			System.out.println(writer.toString());
			System.out.println("Speed = " + millis + "ms");
		}
		finally 
		{
			writer.close();
		}
	}
}
