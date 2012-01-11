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
package org.miradi.xml.conpro.importer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Set;

import org.martus.util.UnicodeReader;
import org.martus.util.UnicodeWriter;
import org.martus.util.inputstreamwithseek.FileInputStreamWithSeek;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.martus.util.inputstreamwithseek.StringInputStreamWithSeek;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.main.TestCaseWithProject;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.StringRefMap;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.Objective;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.ThreatStressRating;
import org.miradi.objects.TncProjectData;
import org.miradi.objects.Xenodata;
import org.miradi.project.ProjectForTesting;
import org.miradi.questions.ProjectSharingQuestion;
import org.miradi.questions.ResourceRoleQuestion;
import org.miradi.questions.TrendQuestion;
import org.miradi.questions.ViabilityModeQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.HtmlUtilities;
import org.miradi.utils.NullProgressMeter;
import org.miradi.xml.conpro.ConProMiradiXml;
import org.miradi.xml.conpro.exporter.ConproXmlExporter;

public class TestConproXmlImporter extends TestCaseWithProject
{
	public TestConproXmlImporter(String name)
	{
		super(name);
	}
	
	public void testDataWithHtmlTags() throws Exception
	{
		Target target = getProject().createTarget();
		getProject().fillObjectUsingCommand(target, Target.TAG_LABEL, "<b>Target</b> with <br/>2 lines of text");
		
		File beforeXmlOutFile = createTempFile();
		ProjectForTesting projectToFill = ProjectForTesting.createProjectWithDefaultObjects(getName());
		try
		{
			exportProject(beforeXmlOutFile, getProject());
			importProject(beforeXmlOutFile, projectToFill);
			
			ORefList targetRefs = projectToFill.getTargetPool().getRefList();
			Target importedTarget = Target.find(projectToFill, targetRefs.getFirstElement());
			assertEquals("html tags not stripped?", "Target with <br/>2 lines of text", importedTarget.getLabel());
		}
		finally
		{
			beforeXmlOutFile.delete();
			projectToFill.close();
		}
	}
	
	public void testOnlyIndicatorsForCurrentTargetMode() throws Exception
	{
		Target target = getProject().createTarget();
		getProject().turnOnTncMode(target);
		getProject().createIndicator(target);
		KeyEcologicalAttribute kea = getProject().createKea();
		
		IdList indicatorIds = new IdList(Indicator.getObjectType());
		final Indicator keaIndicator = getProject().createIndicator(kea);
		indicatorIds.add(keaIndicator.getId());
		getProject().fillObjectUsingCommand(kea, KeyEcologicalAttribute.TAG_INDICATOR_IDS, indicatorIds);
		
		IdList keaIds = new IdList(KeyEcologicalAttribute.getObjectType());
		keaIds.addRef(kea.getRef());
		getProject().fillObjectUsingCommand(target, Target.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS, keaIds.toString());
		assertEquals("Incorrect indicator pool count?", 2, getProject().getIndicatorPool().size());
		
		File beforeXmlOutFile = createTempFileFromName("$$$exportOnlyActiveIndictorsTest.xml");
		ProjectForTesting projectToFill = ProjectForTesting.createProjectWithDefaultObjects("ProjectToFill");
		try
		{
			exportProject(beforeXmlOutFile, getProject());
			importProject(beforeXmlOutFile, projectToFill);
			
			ORefList indicatorRefs = projectToFill.getIndicatorPool().getRefList();
			assertEquals("Incorrect indictor pool count?", 1, indicatorRefs.size());
			assertEquals("Incorrect indicator imported?", keaIndicator.getRef(), indicatorRefs.getFirstElement());
		}
		finally
		{
			beforeXmlOutFile.delete();
			projectToFill.close();
		}
	}
	
	public void testEmptyMetaNoXeno() throws Exception
	{
		ProjectForTesting projectToExport = createProjectWithConproProjectId();
		ProjectForTesting projectToImportInto = createProjectWithNoXenodata(PROJECT_FOR_IMPORTING_NAME_TAG);
		
		exportImportInto(projectToExport, projectToImportInto);
		
		verifyMetaRefersToConproXeno(projectToImportInto);
	}

	public void testEmptyMetaOneXeno() throws Exception
	{
		ProjectForTesting projectToImportInto = createProjectWithNoXenodata(PROJECT_FOR_IMPORTING_NAME_TAG);
		projectToImportInto.createAndPopulateXenodata("55555");
		verifyImportIntoProjectWithXeno(projectToImportInto);
	}

	public void testEmptyMetaTwoXenos() throws Exception
	{
		ProjectForTesting projectToImportInto = createProjectWithNoXenodata(PROJECT_FOR_IMPORTING_NAME_TAG);
		projectToImportInto.createAndPopulateXenodata("55555");
		projectToImportInto.createAndPopulateXenodata("666666");
		verifyImportIntoProjectWithXeno(projectToImportInto);
	}
	
	public void testMetaWithSingleXeno() throws Exception
	{
		ProjectForTesting projectToExport = createProjectWithConproProjectId();
		ProjectForTesting projectToImportInto = createProjectWithNoXenodata("ForImporting");
		projectToImportInto.createConproXenodataReferredToByMetadata("55555");
		
		exportImportInto(projectToExport, projectToImportInto);
		verifyMetaRefersToConproXeno(projectToImportInto);
	}
	
	public void testMetaWithXenoAndOrphanXenos() throws Exception
	{
		ProjectForTesting projectToExport = createProjectWithConproProjectId();
		ProjectForTesting projectToImportInto = createProjectWithNoXenodata(PROJECT_FOR_IMPORTING_NAME_TAG);
		projectToImportInto.createConproXenodataReferredToByMetadata("55555");
		projectToImportInto.createAndPopulateXenodata("666666");
		
		exportImportInto(projectToExport, projectToImportInto);
		verifyMetaRefersToConproXeno(projectToImportInto);
	}
	
	public void testMetaPointingToMissingXeno() throws Exception
	{
		ProjectForTesting projectToExport = createProjectWithNoXenodata(PROJECT_FOR_EXPORTING_NAME_TAG);
		StringRefMap refMap = new StringRefMap();
		ORef nonExistingXenodataObjectRef = new ORef(Xenodata.getObjectType(), new BaseId(999999));
		refMap.add(ConProMiradiXml.CONPRO_CONTEXT, nonExistingXenodataObjectRef);
		projectToExport.fillObjectUsingCommand(projectToExport.getMetadata().getRef(), ProjectMetadata.TAG_XENODATA_STRING_REF_MAP, refMap.toString());
		try
		{
			ProjectForTesting projectToImportInto = createProjectWithNoXenodata(PROJECT_FOR_IMPORTING_NAME_TAG);
			exportImportInto(projectToExport, projectToImportInto);
			fail("should not be able to import xenodata that does not exist?");
		}
		catch (Exception ignoreExpectedException)
		{
		}
	}
	
	private void verifyMetaRefersToConproXeno(ProjectForTesting projectToImportInto) throws ParseException
	{
		String stringRefMapAsString = projectToImportInto.getMetadata().getData(ProjectMetadata.TAG_XENODATA_STRING_REF_MAP);
		StringRefMap stringRefMap = new StringRefMap(stringRefMapAsString);
		Set keys = stringRefMap.getKeys();
		assertEquals("incorrect number of xeno refs?", 1, keys.size());
		ORef xenodataRef = stringRefMap.getValue(ConProMiradiXml.CONPRO_CONTEXT);
		Xenodata xenodataToVerify = Xenodata.find(projectToImportInto, xenodataRef);
		assertEquals("wrong project id imported?", CONPRO_PROJECT_ID, xenodataToVerify.getData(Xenodata.TAG_PROJECT_ID));
	}
	
	private void verifyImportIntoProjectWithXeno(ProjectForTesting projectToImportInto) throws Exception
	{
		ProjectForTesting projectToExport = createProjectWithConproProjectId();
		try
		{
			exportImportInto(projectToExport, projectToImportInto);
			fail("Should have failed to import a project with empty metadata and atleast one xenos?");
		}
		catch (Exception ignoreExpectedException)
		{
		}
	}
	
	private ProjectForTesting createProjectWithConproProjectId() throws Exception
	{
		ProjectForTesting projectToSetup = createProjectWithNoXenodata(PROJECT_FOR_EXPORTING_NAME_TAG);
		
		projectToSetup.createConproXenodataReferredToByMetadata(CONPRO_PROJECT_ID);
		
		return projectToSetup;
	}

	private ProjectForTesting createProjectWithNoXenodata(String projectNameTag) throws Exception
	{
		ProjectForTesting emptyProject = ProjectForTesting.createProjectWithDefaultObjects(getName() + projectNameTag);
		verifyProjectHasNoXenodata(emptyProject);
		
		return emptyProject;
	}
	
	private void verifyProjectHasNoXenodata(ProjectForTesting projectToImportInto)
	{
		assertEquals("metadata xeno field is not empty?", 0, projectToImportInto.getMetadata().getData(ProjectMetadata.TAG_XENODATA_STRING_REF_MAP).length());
		assertEquals("should not have any xenodata objects?", 0, projectToImportInto.getPool(Xenodata.getObjectType()).size());
	}

	public void testAvoidConflictingActivityAndMethodIds() throws Exception
	{
		getProject().createActivity();
		Task method = getProject().createMethod();
		
		ProjectForTesting firstTry = ProjectForTesting.createProjectWithDefaultObjects(getName());
		String xml = exportImportInto(firstTry);

		ORefList taskRefs = firstTry.getPool(Task.getObjectType()).getORefList();
		taskRefs.remove(method.getRef());
		ORef conflictingRef = taskRefs.get(0);
		
		String oldMethodId = "<method id=\"" + method.getId() + "\"";
		assertTrue("Couldn't find old method id in xml", xml.contains(oldMethodId));
		String newMethodId = "<method id=\"" + conflictingRef.getObjectId() + "\"";
		xml = xml.replace(oldMethodId, newMethodId);
		
		ProjectForTesting secondTry = ProjectForTesting.createProjectWithDefaultObjects(getName());
		importXmlStringIntoProject(xml, secondTry);
	}

	private String exportImportInto(ProjectForTesting firstTry)	throws IOException, Exception, UnsupportedEncodingException
	{
		return exportImportInto(getProject(), firstTry);
	}

	private String exportImportInto(ProjectForTesting projectToExport, ProjectForTesting projectToImportInto) throws Exception
	{
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		UnicodeWriter writer = new UnicodeWriter(bytes);
		
		new ConproXmlExporter(projectToExport).exportProject(writer);
		writer.flush();
		bytes.close();
		String xml = new String(bytes.toByteArray(), "UTF-8");
		
		importXmlStringIntoProject(xml, projectToImportInto);
		return xml;
	}

	private void importXmlStringIntoProject(String xml, ProjectForTesting firstTry) throws UnsupportedEncodingException, Exception, IOException
	{
		InputStreamWithSeek in = new StringInputStreamWithSeek(xml);
		try
		{
			new ConproXmlImporter(firstTry, new NullProgressMeter()).importConProProject(in);
		}
		finally
		{
			in.close();
		}
	}
	
	public void testImportConProProject() throws Exception
	{
		getProject().populateEverything();
		getProject().createIndicatorContainingWhiteSpacePaddedCode();
		
		ORef tncProjectDataRef = getProject().getSingletonObjectRef(TncProjectData.getObjectType());
		
		getProject().fillObjectUsingCommand(tncProjectDataRef, TncProjectData.TAG_PROJECT_SHARING_CODE, ProjectSharingQuestion.SHARE_WITH_ANYONE);
		verifyImport();
		
		getProject().fillObjectUsingCommand(tncProjectDataRef, TncProjectData.TAG_PROJECT_SHARING_CODE, ProjectSharingQuestion.SHARE_ONLY_INSIDE_ORGANIZATION);
		verifyImport();
	}

	private void verifyImport() throws IOException, Exception
	{
		File beforeXmlOutFile = createTempFileFromName("conproVersion2BeforeImport.xml");
		
		File afterXmlOutFile = createTempFileFromName("conproVersion2AfterFirstImport.xml");
		ProjectForTesting projectToFill1 = ProjectForTesting.createProjectWithDefaultObjects("ProjectToFill1");
		try
		{
			exportProject(beforeXmlOutFile, getProject());
			String firstExport = convertFileContentToString(beforeXmlOutFile);
			
			importProject(beforeXmlOutFile, projectToFill1);
			verifyThreatStressRatingPoolContents(getProject(), projectToFill1);
			verifyObjectiveLabelsAndUnsplitLabel(projectToFill1);
			unsplitStrategyLabels(projectToFill1);
			verifyConcatenatedProjectScopeAndDescription(projectToFill1);
			setValuesAgainThatWereLostDuringImport(projectToFill1);
			stripDelimiterTagFromObjectiveNames(projectToFill1);
	
			workAroundCpmzMissingEndDateField(projectToFill1);
			
			exportProject(afterXmlOutFile, projectToFill1);
			String secondExport = convertFileContentToString(afterXmlOutFile);
			assertEquals("incorrect project values after first import?", firstExport, secondExport);
		}
		finally
		{
			beforeXmlOutFile.delete();
			afterXmlOutFile.delete();
			projectToFill1.close();
		}
	}

	private void setValuesAgainThatWereLostDuringImport(ProjectForTesting projectToFill1) throws Exception
	{
		projectToFill1.fillObjectUsingCommand(projectToFill1.getMetadata(), ProjectMetadata.TAG_PROJECT_SCOPE, "Some project scope");
		projectToFill1.fillObjectUsingCommand(projectToFill1.getMetadata(), ProjectMetadata.TAG_PROJECT_DESCRIPTION, "Some project description");
	}

	private void workAroundCpmzMissingEndDateField(ProjectForTesting projectToFill1) throws Exception
	{
		assertEquals("Project End Date exists, so might be time to delete this test?", "", projectToFill1.getMetadata().getExpectedEndDate());
		String expectedEndDate = getProject().getMetadata().getExpectedEndDate();
		projectToFill1.fillObjectUsingCommand(projectToFill1.getMetadata(), ProjectMetadata.TAG_EXPECTED_END_DATE, expectedEndDate);
	}
	
	private void verifyObjectiveLabelsAndUnsplitLabel(ProjectForTesting projectToFill1) throws Exception
	{
		ORefList objectiveRefs = projectToFill1.getObjectivePool().getORefList();
		for (int index = 0; index < objectiveRefs.size(); ++index)
		{
			Objective objective = Objective.find(projectToFill1, objectiveRefs.get(index));
			String rawLabel = objective.getLabel();
			String expectedLabel = "123|Some Objective label|Some objective full text data";
			assertEquals("wrong objective label?", expectedLabel, rawLabel);
		}
		
		String[] tags = new String[]{Objective.TAG_SHORT_LABEL, Objective.TAG_LABEL, Objective.TAG_FULL_TEXT, };
		unsplitLabels(projectToFill1, Objective.getObjectType(), tags);
	}
	
	private void unsplitStrategyLabels(ProjectForTesting projectToFill1) throws Exception
	{
		String[] tags = new String[]{Strategy.TAG_SHORT_LABEL, Strategy.TAG_LABEL, Strategy.TAG_TEXT, };
		unsplitLabels(projectToFill1, Strategy.getObjectType(), tags);
	}

	private void unsplitLabels(ProjectForTesting projectToFill1, int objectType, String[] tags) throws Exception
	{
		ORefList refs = projectToFill1.getPool(objectType).getORefList();
		for (int index = 0; index < refs.size(); ++index)
		{
			BaseObject baseObject = BaseObject.find(projectToFill1, refs.get(index));
			String rawLabel = baseObject.getLabel();
			
			String[] splittedFields = rawLabel.split("\\|");
			setSafeFieldValue(baseObject, splittedFields, tags, 0);
			setSafeFieldValue(baseObject, splittedFields, tags, 1);
			setSafeFieldValue(baseObject, splittedFields, tags, 2);
		}
	}
	
	private void setSafeFieldValue(BaseObject baseObject, String[] splittedFields, String[] tags, int tagIndex) throws Exception
	{
		String thirdField = "";
		if (splittedFields.length > tagIndex)
			thirdField = splittedFields[tagIndex];
		
		baseObject.setData(tags[tagIndex], thirdField);
	}
	
	private void verifyConcatenatedProjectScopeAndDescription(ProjectForTesting projectToFill1) throws Exception
	{
		final ProjectMetadata projectMetadata = projectToFill1.getMetadata();
		final String projectScope = projectMetadata.getProjectScope();
		final String expectedProjectDescription = HtmlUtilities.replaceNonHtmlNewlines(ConproXmlExporter.createProjectDescription("Some project description"));
		final String expectedSiteScopeDescription = HtmlUtilities.replaceNonHtmlNewlines(ConproXmlExporter.createSiteScopeDescription("Some project scope"));
		final String expectedProjectScopeValue = HtmlUtilities.replaceNonHtmlNewlines(ConproXmlExporter.getConcatenatedWithNewlines(expectedProjectDescription, expectedSiteScopeDescription));
		
		assertEquals("wrong project scope?", expectedProjectScopeValue, projectScope);
	}

	private void stripDelimiterTagFromObjectiveNames(ProjectForTesting projectToFill1) throws Exception
	{
		ORefList objectiveRefs = projectToFill1.getObjectivePool().getORefList();
		for (int index = 0; index < objectiveRefs.size(); ++index)
		{
			Objective objective = Objective.find(projectToFill1, objectiveRefs.get(index));
			String rawLabel = objective.getLabel();
			String strippedLabel = rawLabel.replaceAll("\\|", "");
			projectToFill1.setObjectData(objectiveRefs.get(index), Objective.TAG_LABEL, strippedLabel);
		}
	}

	private void verifyThreatStressRatingPoolContents(ProjectForTesting project, ProjectForTesting filledProject)
	{
		int originalProjectObjectCount =  getProject().getPool(ThreatStressRating.getObjectType()).getRefList().size();	
		int filledProjectObjectCount =  filledProject.getPool(ThreatStressRating.getObjectType()).getRefList().size();
		assertEquals("not same Threat stress rating object count?", originalProjectObjectCount, filledProjectObjectCount);
	}

	private void importProject(File beforeXmlOutFile, ProjectForTesting projectToFill1) throws Exception
	{		
		ConproXmlImporter conProXmlImporter = new ConproXmlImporter(projectToFill1, new NullProgressMeter());
		FileInputStreamWithSeek fileInputStream = new FileInputStreamWithSeek(beforeXmlOutFile); 
		try
		{
			//FIXME urgent: this is a temp method to output the xml into a file. remove when class is done
			// maybe use a runtime flag to decide whether to putput for debugging or not?
			//The first time this is needed again while debugging conpro use the current debugging flag and make 
			//sure the .xml file is placed in the project dir.
			//OutputToFileForDevelopment(fileInputStream);
			conProXmlImporter.importConProProject(fileInputStream);
		}
		finally
		{
			fileInputStream.close();	
		}
	}

//	private void OutputToFileForDevelopment(FileInputStreamWithSeek fileInputStream) throws FileNotFoundException, IOException
//	{
//		File outFile = new File("c:\\conproImportTempFile.xml");
//		FileOutputStream out = new FileOutputStream(outFile);
//		byte buf[]=new byte[1024];
//		int len;
//		while((len=fileInputStream.read(buf))>0)
//		{
//			out.write(buf,0,len);
//		}
//		out.close();
//		
//		fileInputStream.seek(0);
//	}

	private void exportProject(File afterXmlOutFile, ProjectForTesting projectToExport) throws Exception
	{
		new ConproXmlExporter(projectToExport).export(afterXmlOutFile);
	}

	private String convertFileContentToString(File fileToConvert) throws Exception
	{
	    return new UnicodeReader(fileToConvert).readAll();
	}
	
	public void testGenereratXPath() throws Exception
	{
		String expectedPath = "cp:SomeElement/cp:SomeOtherElement";
		
		String[] pathElements = new String[]{"SomeElement", "SomeOtherElement"}; 
		String generatedPath = new ConproXmlImporter(getProject(), new NullProgressMeter()).generatePath(pathElements);
		assertEquals("xpaths are not same?", expectedPath, generatedPath);
	}
	
	public void testHighestId() throws Exception
	{
		getProject().createObject(Target.getObjectType(), new BaseId(400));
		int highestId1 = getProject().getNormalIdAssigner().getHighestAssignedId();
		assertEquals("wrong highest greater than current highest id?", 400, highestId1);
		
		int highestId2 = getProject().getNormalIdAssigner().getHighestAssignedId();
		ORef newTargetRef = getProject().createObject(Target.getObjectType(), new BaseId(20));
		assertEquals("wrong id?", 20, newTargetRef.getObjectId().asInt());

		int highestId3 = getProject().getNormalIdAssigner().getHighestAssignedId();
		assertEquals("wrong id less than current highest id?", highestId2, highestId3);
	}
	
	public void testProjectWithSharedIndicator() throws Exception
	{
		File xmlFile = createTempFileFromName("$$$$conproVersion2BeforeImport.xml");
		ProjectForTesting projectToFill = ProjectForTesting.createProjectWithDefaultObjects("ProjectToFill");
		
		DiagramFactor diagramFactorThreat = projectToFill.createDiagramFactorAndAddToDiagram(Cause.getObjectType());
		Cause threat = (Cause) diagramFactorThreat.getWrappedFactor();
		Indicator indicatorToBeShared = projectToFill.createIndicator(threat);
		projectToFill.enableAsThreat(threat);
		
		DiagramFactor diagramFactorTarget = projectToFill.createDiagramFactorAndAddToDiagram(Target.getObjectType());
		projectToFill.createDiagramLink(diagramFactorThreat, diagramFactorTarget);
		
		Target target = (Target) diagramFactorTarget.getWrappedFactor();
		projectToFill.turnOnTncMode(target);
		projectToFill.populateTarget(target);
		ORefList keaRefs = target.getKeyEcologicalAttributeRefs();
		KeyEcologicalAttribute kea = KeyEcologicalAttribute.find(projectToFill, keaRefs.get(0));
		IdList indicatorIds = (kea.getIndicatorIds());
		indicatorIds.add(indicatorToBeShared.getId());
		projectToFill.fillObjectUsingCommand(kea, KeyEcologicalAttribute.TAG_INDICATOR_IDS, indicatorIds.toString());
		try
		{
			exportProject(xmlFile, projectToFill);
			importProject(xmlFile, ProjectForTesting.createProjectWithDefaultObjects("ImportedProject"));
			fail("Project import should have failed since the indicator is shared between the cause and kea");
		}
		catch (Exception ignoreExpectedException)
		{
		}
		finally
		{
			xmlFile.delete();
			projectToFill.close();
		}
	}
	
	public void testEmptyProject() throws Exception
	{
		File beforeXmlOutFile = createTempFileFromName("conproVersion2BeforeImport.xml");
		ProjectForTesting projectToFill = ProjectForTesting.createProjectWithDefaultObjects("ProjectToFill");
		try
		{
			verifyEmpyProject(beforeXmlOutFile);
			verifyImportEmptyProject(beforeXmlOutFile, projectToFill);
		}
		finally
		{
			beforeXmlOutFile.delete();
			projectToFill.close();
		}
	}

	private void verifyImportEmptyProject(File beforeXmlOutFile, ProjectForTesting projectToFill)
	{
		try
		{
			importProject(beforeXmlOutFile, projectToFill);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			fail("Emty project could not be imported?");
		}
	}

	private void verifyEmpyProject(File beforeXmlOutFile)
	{
		try
		{
			new ConproXmlExporter(getProject()).export(beforeXmlOutFile);
		}
		catch (Exception e)
		{
			fail("Emty project could not be exported?");	
		}
	}
	
	public void testProjectResource() throws Exception
	{
		ProjectResource teamMember = getProject().createProjectResource();
		CodeList roleCodes = new CodeList();
		roleCodes.add(ResourceRoleQuestion.TEAM_MEMBER_ROLE_CODE);
		getProject().fillObjectUsingCommand(teamMember, ProjectResource.TAG_ROLE_CODES, roleCodes.toString());
		assertEquals("wrong project resource count?", 1, getProject().getResourcePool().getRefSet().size());
		
		File beforeXmlOutFile = createTempFileFromName("conproVersion2BeforeImport.xml");
		ProjectForTesting projectToFill = ProjectForTesting.createProjectWithDefaultObjects("ProjectToFill");
		try
		{
			verifyEmpyProject(beforeXmlOutFile);
			importProject(beforeXmlOutFile, projectToFill);
			ORefSet resourceRefs = projectToFill.getResourcePool().getRefSet();
			assertEquals("wrong project resource count?", 1, resourceRefs.size());
			for(ORef resourceRef : resourceRefs)
			{
				ProjectResource resource = ProjectResource.find(projectToFill, resourceRef);
				assertEquals("wrong given name?", "[Unspecified]", resource.getGivenName());
				assertEquals("wrong sur name?", "[Unspecified]", resource.getSurName());
				assertEquals("wrong email?", "[Unspecified]", resource.getEmail());
			}
		}
		finally
		{
			beforeXmlOutFile.delete();
			projectToFill.close();
		}
	}
	
	public void testMeasurementTrendCode() throws Exception
	{
		Measurement measurement = getProject().createMeasurement();
		getProject().fillObjectUsingCommand(measurement, Measurement.TAG_TREND, TrendQuestion.STRONG_DECREASE_CODE);
		
		Indicator indicator = getProject().createIndicatorWithCauseParent();
		getProject().fillObjectUsingCommand(indicator, Indicator.TAG_MEASUREMENT_REFS, new ORefList(measurement));
		
		File beforeXmlOutFile = createTempFileFromName("conproVersion2BeforeImport.xml");
		ProjectForTesting projectToFill = ProjectForTesting.createProjectWithDefaultObjects("ProjectToFill");
		try
		{
			exportProject(beforeXmlOutFile, getProject());
			importProject(beforeXmlOutFile, projectToFill);
			
			ORefList measurementRefs = projectToFill.getPool(Measurement.getObjectType()).getORefList();
			assertEquals("incorrect measurement count?", 1, measurementRefs.size());
			ORef measurementRef = measurementRefs.get(0);
			Measurement importedMeasurement = Measurement.find(projectToFill, measurementRef);
			assertEquals("wrong trend?", TrendQuestion.STRONG_DECREASE_CODE, importedMeasurement.getData(Measurement.TAG_TREND));
		}
		finally
		{
			beforeXmlOutFile.delete();
			projectToFill.close();
		}
	}
	
	public void testImportingKeaWithoutIndicators() throws Exception
	{
		Target target = getProject().createTarget();
		getProject().fillObjectUsingCommand(target, Target.TAG_VIABILITY_MODE, ViabilityModeQuestion.TNC_STYLE_CODE);
		KeyEcologicalAttribute kea = getProject().createKea();
		getProject().fillObjectUsingCommand(target, Target.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS, new IdList(kea));
		
		File beforeXmlOutFile = createTempFileFromName("conproVersion2BeforeImport.xml");
		ProjectForTesting projectToFill = ProjectForTesting.createProjectWithDefaultObjects("ProjectToFill");
		try
		{
			exportProject(beforeXmlOutFile, getProject());
			importProject(beforeXmlOutFile, projectToFill);
			
			ORefList targetRefs = projectToFill.getTargetPool().getORefList();
			assertEquals("Incorrect target count?", 1, targetRefs.size());
			
			Target importedTarget = Target.find(projectToFill, targetRefs.get(0));
			assertTrue("Incorrect viability mode?", importedTarget.isViabilityModeTNC());
			assertEquals("Incorrect kea children count?", 1, importedTarget.getKeyEcologicalAttributeRefs().size());
		}
		finally
		{
			beforeXmlOutFile.delete();
			projectToFill.close();
		}
	}
	
	public void testStrategyLabel() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		getProject().fillObjectUsingCommand(strategy, Strategy.TAG_SHORT_LABEL, "SomeShortLabel");
		getProject().fillObjectUsingCommand(strategy, Strategy.TAG_LABEL, "SomeLabel");
		getProject().fillObjectUsingCommand(strategy, Strategy.TAG_TEXT, "SomeDetailsText");
		
		File beforeXmlOutFile = createTempFileFromName("conproVersion2BeforeImport.xml");
		ProjectForTesting projectToFill = ProjectForTesting.createProjectWithDefaultObjects("ProjectToFill");
		try
		{
			exportProject(beforeXmlOutFile, getProject());
			importProject(beforeXmlOutFile, projectToFill);
			
			ORefList strategyRefs = projectToFill.getStrategyPool().getORefList();
			assertEquals("Incorrect strategy count?", 1, strategyRefs.size());
			
			Strategy importedStrategy = Strategy.find(projectToFill, strategyRefs.getFirstElement());
			String EXPECTED_LABEL = "SomeShortLabel|SomeLabel|SomeDetailsText";
			assertEquals("Incorrect strategy label?", EXPECTED_LABEL, importedStrategy.getLabel());
		}
		finally
		{
			beforeXmlOutFile.delete();
			projectToFill.close();
		}
	}
	
	private static final String CONPRO_PROJECT_ID = "4444";
	private static final String PROJECT_FOR_IMPORTING_NAME_TAG = "ForImporting";
	private static final String PROJECT_FOR_EXPORTING_NAME_TAG = "ForExporting";
}
