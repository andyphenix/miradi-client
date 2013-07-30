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

package org.miradi.xml.wcs;

import java.util.Vector;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.martus.util.inputstreamwithseek.StringInputStreamWithSeek;
import org.miradi.exceptions.ValidationException;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.main.TestCaseWithProject;
import org.miradi.objecthelpers.CodeToUserStringMap;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Assignment;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.Strategy;
import org.miradi.project.ProjectForTesting;
import org.miradi.project.TestSimpleThreatRatingFramework;
import org.miradi.project.TestStressBasedThreatRatingFramework;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.ThreatRatingModeChoiceQuestion;
import org.miradi.questions.TncOperatingUnitsQuestion;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.ProjectResourceSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.utils.CodeList;
import org.miradi.utils.DateUnitEffortList;
import org.miradi.utils.NullProgressMeter;
import org.miradi.utils.TestStringUtilities;
import org.miradi.utils.UnicodeXmlWriter;
import org.miradi.xml.TestXmpzXmlImporter;
import org.miradi.xml.generic.XmlSchemaCreator;
import org.miradi.xml.xmpz1.Xmpz1XmlImporter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class TestXmpzExporter extends TestCaseWithProject
{
	public TestXmpzExporter(String name)
	{
		super(name);
	}

	public void testProjectWithInvalidChars() throws Exception
	{
		try
		{
			String valueWithInvalidChar = TestStringUtilities.getIllegalCharAsString();
			getProject().fillObjectUsingCommand(getProject().getMetadata(), ProjectMetadata.TAG_PROJECT_DESCRIPTION, valueWithInvalidChar);
			validateProject();
			fail("project with illegal chars should not validate?");
		}
		catch (Exception ignoreExpectedException)
		{
		}
	}
	
	public void testValidateEmptyProject() throws Exception
	{
		validateProject();
	}
	
	public void testImportWithNoBudgetDetailsElement() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		getProject().addExpenseAssignment(strategy, new DateUnitEffortList());
		validateProject();
	}
	
	public void testEncodedThresholdValue() throws Exception
	{
		KeyEcologicalAttribute kea = getProject().createKea();
		Indicator indicator = getProject().createIndicator(kea);
		
		CodeToUserStringMap threshold = new CodeToUserStringMap();
		threshold.putUserString(StatusQuestion.POOR, "&amp;&lt;&gt;&apos;&quot;");
		getProject().fillObjectUsingCommand(indicator, Indicator.TAG_THRESHOLDS_MAP, threshold.toJsonString());
		validateProject();
	}
	
	public void testExportingOfLegacyOperatingUnits() throws Exception
	{
		CodeList operatingUnitCodes = new CodeList();
		operatingUnitCodes.add("PACIF");
		getProject().fillObjectUsingCommand(getProject().getMetadata(), ProjectMetadata.TAG_TNC_OPERATING_UNITS, operatingUnitCodes.toString());
		
		validateProject();
		
		Xmpz1XmlImporter xmlImporter = createProjectImporter(getProject());
		String pathElements = xmlImporter.generatePath(new String[]                      		                                                            {
				Xmpz1XmlConstants.CONSERVATION_PROJECT,
				Xmpz1XmlConstants.TNC_PROJECT_DATA, 
				Xmpz1XmlConstants.TNC_PROJECT_DATA + XmlSchemaCreator.TNC_OPERATING_UNITS + Xmpz1XmlConstants.CONTAINER_ELEMENT_TAG,
				XmlSchemaCreator.CODE_ELEMENT_NAME,
			   });
			
		Node operatingUnitsCodeNode = xmlImporter.getNode(pathElements);
 		assertEquals("incorrect code for legacy replacement?", "OBSOLETE", operatingUnitsCodeNode.getTextContent());
	}

	public void testGetOpertingUnitsWithoutLegacyCode() throws Exception
	{
		CodeList operatingUnitCodes = new CodeList();
		operatingUnitCodes.add("PACIF");
		operatingUnitCodes.add(TncOperatingUnitsQuestion.TNC_SUPERSEDED_OU_CODE);
		getProject().fillObjectUsingCommand(getProject().getMetadata(), ProjectMetadata.TAG_TNC_OPERATING_UNITS, operatingUnitCodes.toString());
		
		CodeList codeListWithoutLegacyCode = Xmpz1XmlExporter.getOperatingUnitsWithoutLegacyCode(getProject().getMetadata());
		assertEquals("incorrect list size?", 1, codeListWithoutLegacyCode.size());
		assertEquals("legacy code was not replaced?", TncOperatingUnitsQuestion.TNC_SUPERSEDED_OU_CODE, codeListWithoutLegacyCode.get(0));
	}
	
	public void testExtraDataNameSplitChar()
	{
		assertContains("Mismatch? ", ExtraDataExporter.TYPE_ID_TAG_SPLIT_TOKEN, ExtraDataExporter.TYPE_ID_TAG_SPLIT_TOKEN_FOR_REGULAR_EXPRESSION);
	}
	
	public void testIfWeDoBigSchemaChangesWeShouldIncludeMinorChangesToo() throws Exception
	{
		if("73".equals(Xmpz1XmlConstants.NAME_SPACE_VERSION))
			return;

		fail("If the schema version number changes, make sure we also do all the\n" +
				"pending small changes at the same time. Then update this test.");
	}
	
	public void testValidateFilledProject() throws Exception
	{
		getProject().populateEverything();
		getProject().createAndAddFactorToDiagram(HumanWelfareTargetSchema.getObjectType());
		DiagramFactor diagramFactor1 = getProject().createAndPopulateDiagramFactor();
		DiagramFactor diagramFactor2 = getProject().createAndPopulateDiagramFactor();
		getProject().tagDiagramFactor(diagramFactor2.getWrappedORef());
		getProject().createDiagramFactorLinkAndAddToDiagram(diagramFactor1, diagramFactor2);
		getProject().createResourceAssignment();
		validateProject();
	}
	
	public void testProjectWithStressBasedThreatRatingData() throws Exception
	{
		getProject().setMetadata(ProjectMetadata.TAG_THREAT_RATING_MODE, ThreatRatingModeChoiceQuestion.STRESS_BASED_CODE);
		DiagramFactor targetDiagramFactor = getProject().createDiagramFactorAndAddToDiagram(TargetSchema.getObjectType());
		DiagramFactor causeDiagramFactor = getProject().createDiagramFactorAndAddToDiagram(CauseSchema.getObjectType());
		getProject().enableAsThreat((Cause) causeDiagramFactor.getWrappedFactor());
		TestStressBasedThreatRatingFramework.createThreatFactorLink(getProject(), causeDiagramFactor, targetDiagramFactor);
		validateProject();
	}
	
	public void testProjectWithSimpleThreatRatingData() throws Exception
	{
		getProject().setMetadata(ProjectMetadata.TAG_THREAT_RATING_MODE, ThreatRatingModeChoiceQuestion.SIMPLE_BASED_CODE);
		
		DiagramFactor threatDiagramFactor = getProject().createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
		getProject().enableAsThreat(threatDiagramFactor.getWrappedORef());

		DiagramFactor targetDiagramFactor = getProject().createDiagramFactorAndAddToDiagram(TargetSchema.getObjectType());
		getProject().createFactorLink(threatDiagramFactor.getWrappedORef(), targetDiagramFactor.getWrappedORef());
		
		TestSimpleThreatRatingFramework.populateBundle(getProject().getSimpleThreatRatingFramework(), threatDiagramFactor.getWrappedId(), targetDiagramFactor.getWrappedId(), getProject().getSimpleThreatRatingFramework().getValueOptions()[0]);
		
		validateProject();
	}
	
	public void testExpenseTimePeriodCost() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		getProject().setProjectStartDate(2008);
		ExpenseAssignment expense = getProject().createAndPopulateExpenseAssignment();
		getProject().fillObjectUsingCommand(strategy, Strategy.TAG_EXPENSE_ASSIGNMENT_REFS, new ORefList(expense));
		Xmpz1XmlImporter xmlImporter = createProjectImporter(getProject());
		Node timePeriodCostsNode = getTimePeriodCostsNode(xmlImporter); 
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_START_DATE, "2008-01-01");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_END_DATE, "2008-12-31");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_TOTAL_BUDGET_COST, "10");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_EXPENSE_TOTAL, "10");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_WORK_UNITS_TOTAL, "");
		verifyExpensesEntriesNode(xmlImporter, timePeriodCostsNode);
		verifyExpenseEntryDateUnitNode(xmlImporter, timePeriodCostsNode);
		verifyExpenseCategoryValue(xmlImporter, expense, timePeriodCostsNode, ExpenseAssignment.TAG_FUNDING_SOURCE_REF, Xmpz1XmlConstants.FUNDING_SOURCE_ID);
		verifyExpenseCategoryValue(xmlImporter, expense, timePeriodCostsNode, ExpenseAssignment.TAG_ACCOUNTING_CODE_REF, Xmpz1XmlConstants.ACCOUNTING_CODE_ID);
		verifyExpenseCategoryValue(xmlImporter, expense, timePeriodCostsNode, ExpenseAssignment.TAG_CATEGORY_ONE_REF, Xmpz1XmlConstants.BUDGET_CATEGORY_ONE_ID);
		verifyExpenseCategoryValue(xmlImporter, expense, timePeriodCostsNode, ExpenseAssignment.TAG_CATEGORY_TWO_REF, Xmpz1XmlConstants.BUDGET_CATEGORY_TWO_ID);
	}
	
	public void testWorkUnitsTimePeriodCost() throws Exception
	{
		Strategy strategy  = getProject().createStrategy();
		getProject().setProjectStartDate(2007);
		getProject().setProjectEndDate(2008);
		ResourceAssignment assignment = getProject().createAndPopulateResourceAssignment();
		getProject().fillObjectUsingCommand(strategy, Strategy.TAG_RESOURCE_ASSIGNMENT_IDS, new IdList(assignment));
		Xmpz1XmlImporter xmlImporter = createProjectImporter(getProject());
		Node timePeriodCostsNode = getTimePeriodCostsNode(xmlImporter); 
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_START_DATE, "2007-01-01");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_END_DATE, "2008-01-01");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_TOTAL_BUDGET_COST, "110");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_EXPENSE_TOTAL, "");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_WORK_UNITS_TOTAL, "11");
		verifyWorkUnitEntriesNode(xmlImporter, timePeriodCostsNode);
		verifyWorkUnitEntryDateUnitNode(xmlImporter, timePeriodCostsNode);
		verifyWorkUnitCategoryValue(xmlImporter, assignment, timePeriodCostsNode, ResourceAssignment.TAG_FUNDING_SOURCE_ID, Xmpz1XmlConstants.FUNDING_SOURCE_ID);
		verifyWorkUnitCategoryValue(xmlImporter, assignment, timePeriodCostsNode, ResourceAssignment.TAG_ACCOUNTING_CODE_ID, Xmpz1XmlConstants.ACCOUNTING_CODE_ID);
		verifyWorkUnitCategoryValue(xmlImporter, assignment, timePeriodCostsNode, ResourceAssignment.TAG_CATEGORY_ONE_REF, Xmpz1XmlConstants.BUDGET_CATEGORY_ONE_ID);
		verifyWorkUnitCategoryValue(xmlImporter, assignment, timePeriodCostsNode, ResourceAssignment.TAG_CATEGORY_TWO_REF, Xmpz1XmlConstants.BUDGET_CATEGORY_TWO_ID);
	}
	
	public void testWorkUnitsTimePeriodCostWithoutProjectResourceUnitCost() throws Exception
	{
		Strategy strategy  = getProject().createStrategy();
		getProject().setProjectStartDate(2007);
		getProject().setProjectEndDate(2008);
		ResourceAssignment assignment = getProject().createAndPopulateResourceAssignment();
		ORef resourceRef = assignment.getResourceRef();
		getProject().fillObjectUsingCommand(resourceRef, ProjectResource.TAG_COST_PER_UNIT, "");
		getProject().fillObjectUsingCommand(strategy, Strategy.TAG_RESOURCE_ASSIGNMENT_IDS, new IdList(assignment));
		Xmpz1XmlImporter xmlImporter = createProjectImporter(getProject());
		Node timePeriodCostsNode = getTimePeriodCostsNode(xmlImporter); 
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_START_DATE, "2007-01-01");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_END_DATE, "2008-01-01");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_TOTAL_BUDGET_COST, "0");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_EXPENSE_TOTAL, "");
		verifyNodeValue(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_WORK_UNITS_TOTAL, "11");
		verifyResourceIds(xmlImporter, timePeriodCostsNode, Xmpz1XmlConstants.CALCULATED_WHO, new ORefList(resourceRef));
		verifyWorkUnitEntriesNode(xmlImporter, timePeriodCostsNode);
		verifyWorkUnitEntryDateUnitNode(xmlImporter, timePeriodCostsNode);
		verifyWorkUnitCategoryValue(xmlImporter, assignment, timePeriodCostsNode, ResourceAssignment.TAG_FUNDING_SOURCE_ID, Xmpz1XmlConstants.FUNDING_SOURCE_ID);
		verifyWorkUnitCategoryValue(xmlImporter, assignment, timePeriodCostsNode, ResourceAssignment.TAG_ACCOUNTING_CODE_ID, Xmpz1XmlConstants.ACCOUNTING_CODE_ID);
		verifyWorkUnitCategoryValue(xmlImporter, assignment, timePeriodCostsNode, ResourceAssignment.TAG_CATEGORY_ONE_REF, Xmpz1XmlConstants.BUDGET_CATEGORY_ONE_ID);
		verifyWorkUnitCategoryValue(xmlImporter, assignment, timePeriodCostsNode, ResourceAssignment.TAG_CATEGORY_TWO_REF, Xmpz1XmlConstants.BUDGET_CATEGORY_TWO_ID);
	}
	
	private void verifyResourceIds(Xmpz1XmlImporter xmlImporter, Node timePeriodCostsNode, String calculatedWho, ORefList resourceRefs) throws Exception
	{
		XPathExpression expression = xmlImporter.getXPath().compile(xmlImporter.generatePath(new String[]{Xmpz1XmlConstants.CALCULATED_WHO,}));
		Node whoNode = (Node) expression.evaluate(timePeriodCostsNode, XPathConstants.NODE);
		String elementNameToMatch = Xmpz1XmlConstants.RESOURCE_ID;

		Vector<Node> matchingNodes = getMatchingChildElementNodes(whoNode, elementNameToMatch);
		
		assertEquals("Wrong number of resources?", resourceRefs.size(), matchingNodes.size());
		for(int index = 0; index < matchingNodes.size(); ++index)
		{
			ORef nodeRef = new ORef(ProjectResourceSchema.getObjectType(), new BaseId(matchingNodes.get(index).getTextContent()));
			assertEquals("Wrong resource ref?", resourceRefs.get(index), nodeRef);
		}
	}

	private Vector<Node> getMatchingChildElementNodes(Node parentNode,	String elementNameToMatch)
	{
		NodeList resourceNodes = parentNode.getChildNodes();
		Vector<Node> matchingNodes = new Vector<Node>();
		for(int index = 0; index < resourceNodes.getLength(); ++index)
		{
			Node node = resourceNodes.item(index);
			String elementName = node.getLocalName();
			if(elementName != null && elementName.equals(elementNameToMatch))
				matchingNodes.add(node);
		}
		return matchingNodes;
	}

	private void verifyWorkUnitEntriesNode(Xmpz1XmlImporter xmlImporter, Node timePeriodCostsNode) throws Exception
	{
		String value = xmlImporter.getPathData(timePeriodCostsNode, new String[] {
				Xmpz1XmlConstants.CALCULATED_WORK_UNITS_ENTRIES, 
				Xmpz1XmlConstants.WORK_UNITS_ENTRY,  
				Xmpz1XmlConstants.WORK_UNITS_ENTRY + Xmpz1XmlConstants.DETAILS, 
				Xmpz1XmlConstants.DATE_UNIT_WORK_UNITS, 
				Xmpz1XmlConstants.WORK_UNITS, });
		
		assertEquals("Incorrect work units?", "11", value);		
	}
	
	private void verifyWorkUnitEntryDateUnitNode(Xmpz1XmlImporter xmlImporter, Node timePeriodCostsNode) throws Exception
	{
		Node dateUnitNode = xmlImporter.getNode(timePeriodCostsNode, new String[] {
				Xmpz1XmlConstants.CALCULATED_WORK_UNITS_ENTRIES, 
				Xmpz1XmlConstants.WORK_UNITS_ENTRY,  
				Xmpz1XmlConstants.WORK_UNITS_ENTRY + Xmpz1XmlConstants.DETAILS, 
				Xmpz1XmlConstants.DATE_UNIT_WORK_UNITS, 
				Xmpz1XmlConstants.WORK_UNITS_DATE_UNIT, 
				Xmpz1XmlConstants.WORK_UNITS_FULL_PROJECT_TIMESPAN});
		
		assertEquals("Incorrect work units?", "Total", xmlImporter.getAttributeValue(dateUnitNode, Xmpz1XmlConstants.FULL_PROJECT_TIMESPAN));		
	}

	private void verifyExpenseEntryDateUnitNode(Xmpz1XmlImporter xmlImporter, Node timePeriodCostsNode) throws Exception
	{
		Node dateUnitNode = xmlImporter.getNode(timePeriodCostsNode, new String[] {
				Xmpz1XmlConstants.CALCULATED_EXPENSE_ENTRIES, 
				Xmpz1XmlConstants.EXPENSE_ENTRY,  
				Xmpz1XmlConstants.EXPENSE_ENTRY + Xmpz1XmlConstants.DETAILS, 
				Xmpz1XmlConstants.DATE_UNITS_EXPENSE, 
				Xmpz1XmlConstants.EXPENSES_DATE_UNIT, 
				Xmpz1XmlConstants.EXPENSES_YEAR,
				});
		
		assertEquals("Incorrect work units?", "2008", xmlImporter.getAttributeValue(dateUnitNode, "StartYear"));
		assertEquals("Incorrect work units?", "1", xmlImporter.getAttributeValue(dateUnitNode, "StartMonth"));
		
	}
	
	private void verifyWorkUnitCategoryValue(Xmpz1XmlImporter xmlImporter, Assignment assignment, Node timePeriodCostsNode, final String categoryRefTag, final String categoryElementName) throws Exception
	{
		String value = xmlImporter.getPathData(timePeriodCostsNode, new String[] {
				Xmpz1XmlConstants.CALCULATED_WORK_UNITS_ENTRIES, 
				Xmpz1XmlConstants.WORK_UNITS_ENTRY,  
				Xmpz1XmlConstants.WORK_UNITS_ENTRY + categoryElementName,
				categoryElementName,
				});
		
		assertEquals("Incorrect category id?", assignment.getRef(categoryRefTag).getObjectId().toString(), value);
	}
	
	private void verifyExpenseCategoryValue(Xmpz1XmlImporter xmlImporter, Assignment assignment, Node timePeriodCostsNode, final String categoryRefTag, final String categoryElementName) throws Exception
	{
		String value = xmlImporter.getPathData(timePeriodCostsNode, new String[] {
				Xmpz1XmlConstants.CALCULATED_EXPENSE_ENTRIES, 
				Xmpz1XmlConstants.EXPENSE_ENTRY,  
				Xmpz1XmlConstants.EXPENSE_ENTRY + categoryElementName,
				categoryElementName,
				});
		
		assertEquals("Incorrect category id?", assignment.getRef(categoryRefTag).getObjectId().toString(), value);
	}
	
	private void verifyExpensesEntriesNode(Xmpz1XmlImporter xmlImporter, Node timePeriodCostsNode) throws Exception
	{
		String value = xmlImporter.getPathData(timePeriodCostsNode, new String[] {
				Xmpz1XmlConstants.CALCULATED_EXPENSE_ENTRIES, 
				Xmpz1XmlConstants.EXPENSE_ENTRY,  
				Xmpz1XmlConstants.EXPENSE_ENTRY + Xmpz1XmlConstants.DETAILS, 
				Xmpz1XmlConstants.DATE_UNITS_EXPENSE, 
				Xmpz1XmlConstants.EXPENSE, });
		
		assertEquals("Incorrect expense?", "10", value);		
	}

	private Node getTimePeriodCostsNode(Xmpz1XmlImporter xmlImporter)	throws Exception
	{
		String pathElements = xmlImporter.generatePath(new String[]
		                                                            {
			Xmpz1XmlConstants.CONSERVATION_PROJECT, 
			Xmpz1XmlConstants.STRATEGY + Xmpz1XmlConstants.POOL_ELEMENT_TAG, 
			Xmpz1XmlConstants.STRATEGY, 
			Xmpz1XmlConstants.STRATEGY + Xmpz1XmlConstants.TIME_PERIOD_COSTS, 
			Xmpz1XmlConstants.TIME_PERIOD_COSTS, 
		   });
		
		return xmlImporter.getNode(pathElements);
	}

	private void verifyNodeValue(final Xmpz1XmlImporter importer, final Node node, final String elementName, final String expectedValue) throws Exception
	{
		assertEquals("Wrong node value for element " + elementName + "?", expectedValue, importer.getPathData(node, elementName));
	}
	
	private Xmpz1XmlImporter createProjectImporter(final ProjectForTesting projectToExport) throws Exception
	{
		UnicodeXmlWriter writer = TestXmpzXmlImporter.createWriter(projectToExport);		
		ProjectForTesting projectToImportInto = ProjectForTesting.createProjectWithoutDefaultObjects("ProjectToImportInto");
		Xmpz1XmlImporter xmlImporter = new Xmpz1XmlImporter(projectToImportInto, new NullProgressMeter());
		StringInputStreamWithSeek stringInputputStream = new StringInputStreamWithSeek(writer.toString());
		xmlImporter.importProject(stringInputputStream);
		
		return xmlImporter;
	}

	private void validateProject() throws Exception
	{
		UnicodeXmlWriter writer = UnicodeXmlWriter.create();
		new Xmpz1XmlExporter(getProject()).exportProject(writer);
		writer.close();
		String xml = writer.toString();

		// NOTE: Uncomment for debugging only
//		File file = createTempFile();
//		file.createNewFile();
//		UnicodeWriter tempWriter = new UnicodeWriter(System.out);
//		tempWriter.writeln(xml);
//		tempWriter.close();
		
		InputStreamWithSeek inputStream = new StringInputStreamWithSeek(xml);
		if (!new WcsMiradiXmlValidator().isValid(inputStream))
		{
			throw new ValidationException(EAM.text("File to import does not validate."));
		}
	}
}
