/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.martus.util.inputstreamwithseek.StringInputStreamWithSeek;
import org.miradi.exceptions.ValidationException;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.Goal;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Objective;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.project.ProjectForTesting;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.GroupBoxSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.utils.NullProgressMeter;
import org.miradi.utils.UnicodeXmlWriter;
import org.miradi.xml.wcs.Xmpz1XmlConstants;
import org.miradi.xml.wcs.Xmpz2XmlValidator;
import org.miradi.xml.xmpz2.Xmpz2XmlConstants;
import org.miradi.xml.xmpz2.Xmpz2XmlExporter;
import org.miradi.xml.xmpz2.Xmpz2XmlImporter;
import org.miradi.xml.xmpz2.Xmpz2XmlSilentValidatorForTesting;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;
import org.miradi.xml.xmpz2.objectImporters.BaseObjectImporter;
import org.miradi.xml.xmpz2.objectImporters.IndicatorImporter;
import org.miradi.xml.xmpz2.objectImporters.StrategyImporter;
import org.miradi.xml.xmpz2.objectImporters.TaskImporter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestXmpz2XmlExporter extends TestCaseForXmpz2ExportAndImport
{
	public TestXmpz2XmlExporter(String name)
	{
		super(name);
	}
	
	public void testNonBlankEmptyExternalProjectData() throws Exception
	{
		verifyEmptyValues("", "");
		verifyEmptyValues("randomCode", "");
		verifyEmptyValues("", "randomProjectId");
		
		verifyNonEmptyValues("randomCode", "randomProjectId");
	}
	
	private void verifyNonEmptyValues(final String externalAppCope,	String xenoDataProjectId) throws Exception
	{
		create(externalAppCope, xenoDataProjectId);
		validateProject();
	}

	private void verifyEmptyValues(final String externalAppCope, String xenoDataProjectId)
	{
		try
		{
			create(externalAppCope, xenoDataProjectId);
			validateProjetSilently();
			fail("empty values should have caused xml to fail validation?");
		}
		catch (Exception expectedExceptionToIgnore)
		{
		}
	}
	
	public void testTaxonomyClassificationContainer() throws Exception
	{
		Target target = getProject().createTarget();
		getProject().populateBaseObject(target);
		verifyRoundTripExportImport();
	}
	
	public void testRelevancyWithKeaIndicators() throws Exception
	{
		Target target = getProject().createKeaModeTarget();
		KeyEcologicalAttribute kea = getProject().createKea(target);
		Indicator indicator = getProject().createIndicator(kea);
		Goal goal = getProject().createGoal(target);
		assertEquals(new ORefList(indicator.getRef()), goal.getRelevantIndicatorRefList());
		verifyRoundTripExportImport();
	}
	
	public void testGroupBoxes() throws Exception
	{
		DiagramFactor groupBoxDiagramFactor = getProject().createAndAddFactorToDiagram(GroupBoxSchema.getObjectType());
		DiagramFactor strategyDiagramFactor = getProject().createAndAddFactorToDiagram(StrategySchema.getObjectType());
		getProject().populateDiagramFactorGroupBox(groupBoxDiagramFactor, strategyDiagramFactor);
		verifyRoundTripExportImport();
	}
	
	public void testRelevancyOverridesWithLinks() throws Exception
	{
		DiagramFactor strategyDiagramFactor = getProject().createAndAddFactorToDiagram(StrategySchema.getObjectType());
		Strategy strategy = Strategy.find(getProject(), strategyDiagramFactor.getWrappedORef());
		Task activity = getProject().createActivity(strategy);
		Indicator indicator = getProject().createIndicator(strategy);
		DiagramFactor causeDiagramFactor = getProject().createAndAddFactorToDiagram(CauseSchema.getObjectType());
		Cause cause = Cause.find(getProject(), causeDiagramFactor.getWrappedORef());
		Objective objective = getProject().createObjective(cause);
		getProject().createDiagramFactorLinkAndAddToDiagram(strategyDiagramFactor, causeDiagramFactor);
		
		assertEquals(new ORefList(strategy.getRef()), objective.getRelevantStrategyRefs());
		assertEquals(new ORefList(activity.getRef()), objective.getRelevantActivityRefs());
		assertEquals(new ORefList(), objective.getRelevantIndicatorRefList());
		verifyRoundTripExportImport();
		
		getProject().executeCommands(objective.createCommandsToEnsureStrategyOrActivityIsIrrelevant(strategy.getRef()));
		getProject().executeCommands(objective.createCommandsToEnsureStrategyOrActivityIsRelevant(activity.getRef()));
		getProject().executeCommands(objective.createCommandsToEnsureIndicatorIsRelevant(indicator.getRef()));
		assertEquals(new ORefList(), objective.getRelevantStrategyRefs());
		assertEquals(new ORefList(activity.getRef()), objective.getRelevantActivityRefs());
		assertEquals(new ORefList(indicator.getRef()), objective.getRelevantIndicatorRefList());
		verifyRoundTripExportImport();
		
	}
	
	public void testRelevancyOverridesWithoutLinks() throws Exception
	{
		Task nearbyActivity = getProject().createActivity();
		Strategy mainStrategy = Strategy.find(getProject(), nearbyActivity.getOwnerRef());
		Indicator nearbyIndicator = getProject().createIndicator(mainStrategy);
		Objective nearbyObjective = getProject().createObjective(mainStrategy);

		Strategy otherStrategy = getProject().createStrategy();
		Indicator otherIndicator = getProject().createIndicator(otherStrategy);
		Task otherActivity = getProject().createActivity();
		
		assertEquals(new ORefList(mainStrategy.getRef()), nearbyObjective.getRelevantStrategyRefs());
		assertEquals(new ORefList(nearbyActivity.getRef()), nearbyObjective.getRelevantActivityRefs());
		assertEquals(new ORefList(nearbyIndicator.getRef()), nearbyObjective.getRelevantIndicatorRefList());

		verifyRoundTripExportImport();

		getProject().executeCommands(nearbyObjective.createCommandsToEnsureStrategyOrActivityIsIrrelevant(mainStrategy.getRef()));
		getProject().executeCommands(nearbyObjective.createCommandsToEnsureStrategyOrActivityIsIrrelevant(nearbyActivity.getRef()));
		getProject().executeCommands(nearbyObjective.createCommandsToEnsureIndicatorIsIrrelevant(nearbyIndicator.getRef()));
		assertEquals(new ORefList(), nearbyObjective.getRelevantStrategyRefs());
		assertEquals(new ORefList(), nearbyObjective.getRelevantActivityRefs());
		assertEquals(new ORefList(), nearbyObjective.getRelevantIndicatorRefList());
		verifyRoundTripExportImport();

		getProject().executeCommands(nearbyObjective.createCommandsToEnsureStrategyOrActivityIsRelevant(otherStrategy.getRef()));
		getProject().executeCommands(nearbyObjective.createCommandsToEnsureStrategyOrActivityIsRelevant(otherActivity.getRef()));
		getProject().executeCommands(nearbyObjective.createCommandsToEnsureIndicatorIsRelevant(otherIndicator.getRef()));
		assertEquals(new ORefList(otherStrategy.getRef()), nearbyObjective.getRelevantStrategyRefs());
		assertEquals(new ORefList(otherActivity.getRef()), nearbyObjective.getRelevantActivityRefs());
		assertEquals(new ORefList(otherIndicator.getRef()), nearbyObjective.getRelevantIndicatorRefList());
		verifyRoundTripExportImport();
	}

	private void create(final String externalAppCope, String xenoDataProjectId) throws Exception
	{
		final String xenodataAsString = getProject().createConproXenodata(externalAppCope, xenoDataProjectId);
		getProject().fillObjectUsingCommand(getProject().getMetadata().getRef(), ProjectMetadata.TAG_XENODATA_STRING_REF_MAP, xenodataAsString);
	}
	
	public void testValidateEmptyProject() throws Exception
	{
		validateProject();
	}
	
	public void testThreeLanguageVocabularyElement() throws Exception
	{
		verifyLanguageCode("zun");
		verifyLanguageCode("plt");
		verifyLanguageCode("es");
	}

	private void verifyLanguageCode(final String languageCode) throws Exception
	{
		getProject().fillObjectUsingCommand(getProject().getMetadata(), ProjectMetadata.TAG_PROJECT_LANGUAGE, languageCode);
		validateProject();
	}
	
	public void testElementsWithCalculatedCostsElement() throws Exception
	{
		getProject().createAndPopulateActivity();
		getProject().createAndPopulateIndicator(getProject().createStrategy());
		getProject().createAndPopulateStrategy();
		verifyCalculatedCostsElement();
	}

	public void verifyCalculatedCostsElement() throws Exception
	{
		ProjectForTesting projectToImportInto = ProjectForTesting.createProjectWithoutDefaultObjects("ProjectToImportInto");
		Xmpz2XmlImporter xmlImporter = new Xmpz2XmlImporter(projectToImportInto, new NullProgressMeter());
		String exportedProjectXml = validateProject();
		StringInputStreamWithSeek stringInputputStream = new StringInputStreamWithSeek(exportedProjectXml);
		try
		{
			xmlImporter.importProject(stringInputputStream);
			verifyCalculatedCostsElement(xmlImporter, new TaskImporter(xmlImporter), 5);
			verifyCalculatedCostsElement(xmlImporter, new IndicatorImporter(xmlImporter), 3);
			verifyCalculatedCostsElement(xmlImporter, new StrategyImporter(xmlImporter), 3);
		}
		finally
		{
			stringInputputStream.close();	
		}
	}

	public void verifyCalculatedCostsElement(Xmpz2XmlImporter xmlImporter, BaseObjectImporter objectImporter, int expectedTaskCount) throws Exception
	{
		final String elementObjectName = objectImporter.getBaseObjectSchema().getXmpz2ElementName();
		final String containerElementName = Xmpz2XmlWriter.createPoolElementName(elementObjectName);
		final Node rootNode = xmlImporter.getRootNode();
		final NodeList baseObjectNodes = xmlImporter.getNodes(rootNode, new String[]{containerElementName, elementObjectName, });
		
		assertEquals("should have one task node?", expectedTaskCount, baseObjectNodes.getLength());
		Node baseObjectNode = baseObjectNodes.item(0);
		
		Node baseObjectCalculatedCostsNode = xmlImporter.getNamedChildNode(baseObjectNode, elementObjectName + Xmpz2XmlConstants.TIME_PERIOD_COSTS);
		assertNotNull("should have object calcualted costs element?", baseObjectCalculatedCostsNode);
		
		Node calculatedCostsNode = xmlImporter.getNamedChildNode(baseObjectCalculatedCostsNode, Xmpz2XmlConstants.TIME_PERIOD_COSTS);
		assertNotNull("should have calcualted costs element?", calculatedCostsNode);
		
		Node calculatedTotalBudgetCostNode = xmlImporter.getNamedChildNode(calculatedCostsNode, Xmpz1XmlConstants.CALCULATED_TOTAL_BUDGET_COST);
		assertEquals("incorrect total budget value for object?", "112", xmlImporter.getSafeNodeContent(calculatedTotalBudgetCostNode));
	}
	
	public void testProjectWithHtmlInQuarantinedContent() throws Exception
	{
		getProject().appendToQuarantineFile("some <br/> random <b>bolded</b> text");
		validateProject();
	}
	
	public void testUserTextFieldWithHtml() throws Exception
	{
		String sampleText = "one <br/> and <b>some bold</b> <br/>" +
				"two <b>spanning lines<br/>" +
				"</b>" +
				"<a href=\"www.miradi.org\">link</a>" +
				"<i>some <u><strike>combining</strike></u></i>" +
				"<ul><li>test one</li><li><b>bold item</b></li></ul>";
		getProject().fillObjectUsingCommand(getProject().getMetadata(), ProjectMetadata.TAG_PROJECT_DESCRIPTION, sampleText);
		validateProject();
	}
	
	public void testValidateFilledProject() throws Exception
	{
		// FIXME urgent: This needs to do a better job of populating everything
		// It should auto-populate any newly created field so they are immediately 
		// flagged as needing to be added to the schema.
		getProject().populateEverything();
		getProject().populateBaseObjectWithSampleData(getProject().getMetadata());
		DiagramFactor diagramFactor1 = getProject().createAndPopulateDiagramFactor();
		DiagramFactor diagramFactor2 = getProject().createAndPopulateDiagramFactor();
		getProject().tagDiagramFactor(diagramFactor2.getWrappedORef());
		getProject().createDiagramFactorLinkAndAddToDiagram(diagramFactor1, diagramFactor2);
		getProject().createResourceAssignment();
		validateProject();
	}
	
	private String validateProject() throws Exception
	{
		return validateProject(new Xmpz2XmlValidator());
	}
	
	private String validateProjetSilently() throws Exception
	{
		return validateProject(new Xmpz2XmlSilentValidatorForTesting());
	}

	private String validateProject(final Xmpz2XmlValidator xmpz2XmlValidator) throws Exception
	{
		final UnicodeXmlWriter writer = UnicodeXmlWriter.create();
		new Xmpz2XmlExporter(getProject()).exportProject(writer);
		writer.close();
		String xml = writer.toString();
				
		InputStreamWithSeek inputStream = new StringInputStreamWithSeek(xml);
		if (!xmpz2XmlValidator.isValid(inputStream))
		{
			throw new ValidationException(EAM.text("File to import does not validate."));
		}
		
		return xml;
	}
}
