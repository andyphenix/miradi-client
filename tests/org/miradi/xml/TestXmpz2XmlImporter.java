/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml;

import org.martus.util.UnicodeStringWriter;
import org.martus.util.inputstreamwithseek.StringInputStreamWithSeek;
import org.miradi.main.TestCaseWithProject;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.Goal;
import org.miradi.objects.Indicator;
import org.miradi.objects.Strategy;
import org.miradi.objects.Task;
import org.miradi.project.ProjectForTesting;
import org.miradi.utils.NullProgressMeter;
import org.miradi.utils.PointList;
import org.miradi.views.diagram.TestLinkBendPointsMoveHandler;
import org.miradi.xml.xmpz2.Xmpz2XmlExporter;
import org.miradi.xml.xmpz2.Xmpz2XmlImporter;

public class TestXmpz2XmlImporter extends TestCaseWithProject
{
	public TestXmpz2XmlImporter(String name)
	{
		super(name);
	}
	
	public void testImportEmptyProject() throws Exception
	{
		validateUsingStringWriter();
	}
	
	public void testImportFilledProject() throws Exception
	{
		getProject().populateEverything();
		
		AbstractTarget target = getProject().createAndPopulateHumanWelfareTarget();
		Strategy strategy = getProject().createStrategy();
		Indicator indicator = getProject().createAndPopulateIndicator(strategy);
		Task task = getProject().createAndPopulateTask(indicator, "TASK");
		Goal goal = getProject().createAndPopulateGoal(target);
		getProject().addProgressReport(task);
		getProject().addProgressReport(indicator);
		getProject().addProgressReport(strategy);
		getProject().addProgressPercent(goal);
		getProject().addExpenseWithValue(strategy);
		getProject().addResourceAssignment(strategy);
		getProject().createandpopulateThreatReductionResult();
		
		PointList bendPointList = TestLinkBendPointsMoveHandler.createBendPointList();
		getProject().createLinkCellWithBendPoints(bendPointList);
		getProject().createAndPopulateIndicator(strategy);
		validateUsingStringWriter();
	}
	
	private ProjectForTesting validateUsingStringWriter() throws Exception
	{
		UnicodeStringWriter projectWriter = createWriter(getProject());
		
		ProjectForTesting projectToImportInto = ProjectForTesting.createProjectWithoutDefaultObjects("ProjectToImportInto");
		Xmpz2XmlImporter xmlImporter = new Xmpz2XmlImporter(projectToImportInto, new NullProgressMeter());
		
		String exportedProjectXml = projectWriter.toString();
		StringInputStreamWithSeek stringInputputStream = new StringInputStreamWithSeek(exportedProjectXml);
		try
		{
			xmlImporter.importProject(stringInputputStream);
		}
		finally
		{
			stringInputputStream.close();	
		}
		
		UnicodeStringWriter secondWriter = createWriter(projectToImportInto);
		assertEquals("Exports from projects do not match?", exportedProjectXml, secondWriter.toString());
		
		return projectToImportInto;
	}

	private UnicodeStringWriter createWriter(ProjectForTesting projectToUse) throws Exception
	{
		Xmpz2XmlExporter exporter = new Xmpz2XmlExporter(projectToUse);
		UnicodeStringWriter writer = UnicodeStringWriter.create();
		exporter.exportProject(writer);
		writer.flush();
		
		return writer;
	}
}
