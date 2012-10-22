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

package org.miradi.xml.xmpz2.xmpz2schema;

import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import org.miradi.main.Miradi;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.BaseObjectPool;
import org.miradi.objects.Dashboard;
import org.miradi.objects.Desire;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.FactorLink;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.Indicator;
import org.miradi.objects.ObjectTreeTableConfiguration;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.RatingCriterion;
import org.miradi.objects.ReportTemplate;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.Strategy;
import org.miradi.objects.TableSettings;
import org.miradi.objects.TaggedObjectSet;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.ThreatRatingCommentsData;
import org.miradi.objects.ThreatReductionResult;
import org.miradi.objects.ThreatStressRating;
import org.miradi.objects.ViewData;
import org.miradi.objects.Xenodata;
import org.miradi.objects.XslTemplate;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.DashboardFlagsQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.schemas.CostAllocationRuleSchema;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.FosProjectDataSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.RareProjectDataSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TaggedObjectSetSchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.schemas.TncProjectDataSchema;
import org.miradi.schemas.ValueOptionSchema;
import org.miradi.schemas.WcpaProjectDataSchema;
import org.miradi.schemas.WcsProjectDataSchema;
import org.miradi.schemas.WwfProjectDataSchema;
import org.miradi.utils.CodeList;
import org.miradi.utils.StringUtilities;
import org.miradi.utils.Translation;
import org.miradi.utils.Utility;
import org.miradi.xml.xmpz2.Xmpz2TagToElementNameMap;
import org.miradi.xml.xmpz2.Xmpz2XmlConstants;

public class Xmpz2XmlSchemaCreator implements Xmpz2XmlConstants
{
	public static void main(String[] args) throws Exception
	{
		Miradi.addThirdPartyJarsToClasspath();
		Translation.initialize();

		Xmpz2SchemaWriter writer = new Xmpz2SchemaWriter(System.out);
		Xmpz2XmlSchemaCreator creator = new Xmpz2XmlSchemaCreator(writer);
		
		creator.writeRncSchema();
		creator.getSchemaWriter().flush();
	}

	public Xmpz2XmlSchemaCreator(Xmpz2SchemaWriter writerToUse) throws Exception
	{
		writer = writerToUse;
		tagToElementNameMap = new Xmpz2TagToElementNameMap();
		choiceQuestionToSchemaElementNameMap = new ChoiceQuestionToSchemaElementNameMap();
		project = new Project();
		baseObjectSchemaWriters = getTopLevelBaseObjectSchemas();
		codelistSchemaElements = new Vector<String>();
	}

	public void writeRncSchema() throws Exception
	{
		Vector<Xmpz2CustomSchemaDefinitionCreator> creators = new Vector<Xmpz2CustomSchemaDefinitionCreator>(); 
		writeHeader();
		writeConservationProjectElement();
		writeSingletonElements();
		writeBaseObjectSchemaElements();
		writeCodelistSchemaElements();
		writeVocabularyDefinitions();
		writeObjectTypeIdElements();
		writeWrappedByDiagramFactorSchemaElement();
		writeLinkableFactorIds();
		writeHtmlTagSchemaElements();
		writeGeospacialLocationElement();
		writeDiagramPointElement();
		writeDiagramSizeElement();
		writeDateUnitSchemaElements();
		creators.add(creatorThresholdsElementSchemaCreator());
		creators.add(createTimePeriodCostsElementSchemaCreator());
		creators.add(createExpenseEntryElementSchemaCreator());
		creators.add(createWorkUnitsEntryElementSchemaCreator());
		creators.add(createExternaIdSchemaElementSchemaCreator());
		creators.add(createSimpleThreatRatingElementSchemaCreator());
		creators.add(createStressBasedThreatRatingElementSchemaCreator());
		creators.add(createDiagramFactorUiSettingsSchemaCreator());
		creators.add(createDashboardUserChoiceMapSchemaCreator());
		creators.add(writeExtraDataSectionElement());
		creators.add(defineExtraDataItemElement());
		
		for(Xmpz2CustomSchemaDefinitionCreator creator : creators)
		{
			getSchemaWriter().write(creator.createSchemaElement());
		}
		
		writeDashboardFlagsContainer();
	}

	private void writeHeader()
	{
		getSchemaWriter().writeNamespace(NAME_SPACE);
		getSchemaWriter().defineAlias("start", createElementName(CONSERVATION_PROJECT));
	}

	private void writeConservationProjectElement()
	{
		getSchemaWriter().startElementDefinition(CONSERVATION_PROJECT);

		Vector<String> elementNames = new Vector<String>();
		elementNames.add(createElementName(PROJECT_SUMMARY));
		elementNames.add(createElementName(PROJECT_SUMMARY_SCOPE));
		elementNames.add(createElementName(PROJECT_SUMMARY_LOCATION));
		elementNames.add(createElementName(PROJECT_SUMMARY_PLANNING));
		elementNames.add(createElementName(TNC_PROJECT_DATA));
		elementNames.add(createElementName(WWF_PROJECT_DATA));
		elementNames.add(createElementName(WCS_PROJECT_DATA));
		elementNames.add(createElementName(RARE_PROJECT_DATA));
		elementNames.add(createElementName(FOS_PROJECT_DATA));
		elementNames.add(createElementName(EXTRA_DATA));
		for(BaseObjectSchemaWriter baseObjectSchemaWriter : baseObjectSchemaWriters)
		{
			String poolName = baseObjectSchemaWriter.getPoolName();
			elementNames.add(getSchemaWriter().createOptionalSchemaElement(createElementName(poolName)));
		}
		
		elementNames.add(ELEMENT_NAME + PREFIX + DELETED_ORPHANS_ELEMENT_NAME +  "{ text }?");
		getSchemaWriter().defineElements(elementNames);
		
		getSchemaWriter().endElementDefinition(CONSERVATION_PROJECT);
		getSchemaWriter().flush();
	}
	
	private void writeSingletonElements() throws Exception
	{
		writeSingletonObjectSchema(new ProjectSummarySchema());
		writeSingletonObjectSchema(new ProjectSummaryScopeSchema());
		writeSingletonObjectSchema(new ProjectSummaryLocationSchemaWriter(this, new ProjectSummaryLocationSchema()));
		writeSingletonObjectSchema(new ProjectSummaryPlanningSchema());
		writeSingletonObjectSchema(new ProjectSummaryTncProjectDataSchema());
		writeSingletonObjectSchema(new WwfProjectDataSchema());
		writeSingletonObjectSchema(new WcsProjectDataSchema());
		writeSingletonObjectSchema(new FosProjectDataSchema());
		writeSingletonObjectSchema(new RareProjectDataSchema());
	}
	
	private void writeSingletonObjectSchema(BaseObjectSchema baseObjectSchema) throws Exception
	{
		final SingletonSchemaWriter baseObjectSchemaWriter = new SingletonSchemaWriter(this, baseObjectSchema);
		writeSingletonObjectSchema(baseObjectSchemaWriter);
	}

	private void writeSingletonObjectSchema(final SingletonSchemaWriter baseObjectSchemaWriter) throws Exception
	{
		getSchemaWriter().startElementDefinition(baseObjectSchemaWriter.getXmpz2ElementName());
		writeElementContent(baseObjectSchemaWriter);
		getSchemaWriter().endBlock();
	}

	private void writeBaseObjectSchemaElements() throws Exception
	{
		for(BaseObjectSchemaWriter baseObjectSchemaWriter : baseObjectSchemaWriters)
		{
			writeBaseObjectSchema(baseObjectSchemaWriter);
		}		
	}

	private void writeBaseObjectSchema(BaseObjectSchemaWriter baseObjectSchemaWriter) throws Exception
	{
		writeBaseObjectSchemaHeader(baseObjectSchemaWriter);
		getSchemaWriter().startBlock();
		writeElementContent(baseObjectSchemaWriter);
		getSchemaWriter().endBlock();
	}

	private void writeElementContent(BaseObjectSchemaWriter baseObjectSchemaWriter) throws Exception
	{
		Vector<String> schemaFieldElements = baseObjectSchemaWriter.createFieldSchemas();
		getSchemaWriter().defineElements(schemaFieldElements);
	}
	
	private String getRelevantTypeName(AbstractFieldSchema fieldSchema)
	{
		if (fieldSchema.getTag().equals(Desire.TAG_RELEVANT_INDICATOR_SET))
			return INDICATOR;
		
		if (fieldSchema.getTag().equals(RELEVANT_STRATEGY_IDS))
			return STRATEGY;
		
		if (fieldSchema.getTag().equals(RELEVANT_ACTIVITY_IDS))
			return ACTIVITY;
		
		throw new RuntimeException("FieldSchema is not a relevancy field: " + fieldSchema.getTag());
	}
	
	private void writeCodelistSchemaElements()
	{
		for (String codelistSchemaElement : codelistSchemaElements)
		{
			getSchemaWriter().printIndented(codelistSchemaElement);
			getSchemaWriter().println();
		}
	}
	
	public String createDimensionSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		return createSchemaElement(baseObjectSchema, fieldSchema, DIAGRAM_SIZE_ELEMENT_NAME);
	}
	
	public String createDiagramPointSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		return createSchemaElement(baseObjectSchema, fieldSchema, DIAGRAM_POINT_ELEMENT_NAME);
	}
	
	public String createPointListElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		return createSchemaElement(baseObjectSchema, fieldSchema, DIAGRAM_POINT_ELEMENT_NAME);
	}
	
	public String writeCalculatedCostSchemaElement(BaseObjectSchema baseObjectSchema)
	{
		return getSchemaWriter().createOptionalSchemaElement(baseObjectSchema.getXmpz2ElementName() + TIME_PERIOD_COSTS, createElementName(TIME_PERIOD_COSTS));
	}
	
	public String createThresholdsSchemaElement(BaseObjectSchema baseObjectSchema)
	{
		return getSchemaWriter().createOptionalSchemaElement(baseObjectSchema.getXmpz2ElementName() + THRESHOLDS, createElementName(THRESHOLD) +  "*");
	}
	
	public String createStringRefMapSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		return createSchemaElement(baseObjectSchema, fieldSchema, EXTERNAL_PROJECT_ID_ELEMENT_NAME);
	}
	
	public String createElementName(String elementName)
	{
		return getSchemaWriter().createElementName(elementName);
	}
	
	public String createStringSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		return createElementSchema(baseObjectSchema, fieldSchema, TEXT_ELEMENT_TYPE);
	}

	public String createUserTextSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		return createElementSchema(baseObjectSchema, fieldSchema, FORMATTED_TEXT_TYPE);
	}
	
	public String createBooleanSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		return createElementSchema(baseObjectSchema, fieldSchema, getSchemaWriter().createBooleanType());
	}

	public String createNumberSchemaElement(BaseObjectSchema baseObjectSchema,	AbstractFieldSchema fieldSchema)
	{
		return createElementSchema(baseObjectSchema, fieldSchema, getSchemaWriter().createDecimalType());
	}
	
	public String createIntegerSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		return createElementSchema(baseObjectSchema, fieldSchema, getSchemaWriter().createIntegerType());
	}
	
	public String createDateSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		return createElementSchema(baseObjectSchema, fieldSchema, VOCABULARY_DATE);
	}
	
	public String createRefSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		String fieldName = getIdElementName(baseObjectSchema, fieldSchema);
		return createSchemaElement(baseObjectSchema, fieldSchema, fieldName);
	}
	
	public String createBaseIdSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema, int objectType)
	{
		String objectName = getIdElementName(baseObjectSchema, fieldSchema, objectType);
		return createSchemaElement(baseObjectSchema, fieldSchema, createIdName(objectName));
	}

	public String createDateUnitEffortListSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		return createSchemaElement(baseObjectSchema, fieldSchema, DATE_UNIT + getDateUnitTypeName(baseObjectSchema.getType()));
	}
	
	public String createChoiceSchemaElement(BaseObjectSchema baseObjectSchema,	AbstractFieldSchema fieldSchema, ChoiceQuestion choiceQuestion)
	{
		String vocabularyName = getChoiceQuestionToSchemaElementNameMap().findVocabulary(choiceQuestion);
		return createElementSchema(baseObjectSchema, fieldSchema, vocabularyName);
	}
	
	public String createIdListSchemaElement(BaseObjectSchema baseObjectSchema,	AbstractFieldSchema fieldSchema)
	{
		return createRefListSchemaElement(baseObjectSchema, fieldSchema);
	}

	public String createRefListSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		String elementName = getTagToElementNameMap().findElementName(baseObjectSchema.getXmpz2ElementName(), fieldSchema.getTag());
		String reflistElementName = baseObjectSchema.getXmpz2ElementName() + elementName;
		final String idElementName = createIdElementName(baseObjectSchema, fieldSchema, elementName);
		return getSchemaWriter().createZeroOrMoreElementDefinition(reflistElementName, idElementName);
	}

	public String createCodelistSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema, ChoiceQuestion question)
	{
		String elementName = getTagToElementNameMap().findElementName(baseObjectSchema.getXmpz2ElementName(), fieldSchema.getTag());
		String codelistElementName = baseObjectSchema.getXmpz2ElementName() + elementName;
		
		codelistSchemaElements.add(createCodelistSchemaElement(codelistElementName, question));
		
		return createContainerName(codelistElementName) + ".element ?";
	}
	
	private String createCodelistSchemaElement(String codelistElementName, ChoiceQuestion question)
	{
		String vocabularyName = getChoiceQuestionToSchemaElementNameMap().get(question);
		final String createContainerName = createContainerName(codelistElementName);
		String containerElement = getSchemaWriter().createElementDefinition(createContainerName) + StringUtilities.NEW_LINE;
		containerElement += "{" + StringUtilities.NEW_LINE;
		containerElement += getSchemaWriter().createSchemaElement("code", vocabularyName) + " *" + StringUtilities.NEW_LINE;
		containerElement += "}" + StringUtilities.NEW_LINE;
		
		return containerElement;
	}
	
	private String createContainerName(String elementName)
	{
		return elementName + "Container";
	}

	public String createRelevantSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		final String relevantTypeName = createIdName(getRelevantTypeName(fieldSchema));
		final String elementType = getSchemaWriter().createZeroOrMoreDotElement(relevantTypeName);
		return createElementSchema(baseObjectSchema, fieldSchema, elementType);
	}
	
	private void writeVocabularyDefinitions()
	{
		Set<ChoiceQuestion> choiceQuestions = choiceQuestionToSchemaElementNameMap.keySet();
		Vector<ChoiceQuestion> sortedQuestions = new Vector<ChoiceQuestion>(choiceQuestions);
		Collections.sort(sortedQuestions);
		for(ChoiceQuestion question : sortedQuestions)
		{
			String vocabularyName = choiceQuestionToSchemaElementNameMap.get(question);
			defineVocabulary(question, vocabularyName);
		}
		
		getSchemaWriter().println("vocabulary_full_project_timespan = xsd:NMTOKEN { pattern = 'Total' } ");
		getSchemaWriter().println("vocabulary_year = xsd:NMTOKEN { pattern = '[0-9]{4}' } ");
		getSchemaWriter().println("vocabulary_month = xsd:integer { minInclusive='1' maxInclusive='12' } ");
		getSchemaWriter().println("vocabulary_date = xsd:NMTOKEN { pattern = '[0-9]{4}-[0-9]{2}-[0-9]{2}' }");
	}
	
	private void writeObjectTypeIdElements()
	{
		final Vector<String> objectTypeNames = Xmpz2GroupedConstants.getObjectTypeNamesToCreateIdSchemaElements();
		for (String objectTypeName : objectTypeNames)
		{
			writeIdElement(objectTypeName);
		}
	}

	private String createIdElementName(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema, String elementName)
	{
		if (isFieldForType(baseObjectSchema, fieldSchema, TaskSchema.getObjectType(), Task.TAG_SUBTASK_IDS))
			return createIdName(SUB_TASK);
		
		if (isFieldForType(baseObjectSchema, fieldSchema, IndicatorSchema.getObjectType(), Indicator.TAG_METHOD_IDS))
			return createIdName(METHOD );
		
		if (isFieldForType(baseObjectSchema, fieldSchema, StrategySchema.getObjectType(), Strategy.TAG_ACTIVITY_IDS))
			return createIdName(ACTIVITY );
		
		if (isFieldForType(baseObjectSchema, fieldSchema, DiagramLinkSchema.getObjectType(), DiagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS))
			return createIdName(DIAGRAM_LINK);
		
		if (isFieldForType(baseObjectSchema, fieldSchema, DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_GROUP_BOX_CHILDREN_REFS))
			return createIdName(DIAGRAM_FACTOR);
		
		if (isFieldForType(baseObjectSchema, fieldSchema, TaggedObjectSetSchema.getObjectType(), TaggedObjectSet.TAG_TAGGED_OBJECT_REFS))
			return WRAPPED_BY_DIAGRAM_FACTOR_ID_ELEMENT_NAME;
		
		if (DiagramObject.isDiagramObject(baseObjectSchema.getType()) && fieldSchema.getTag().equals(DiagramObject.TAG_SELECTED_TAGGED_OBJECT_SET_REFS))
			return createIdName(TAGGED_OBJECT_SET_ELEMENT_NAME);
				
		return StringUtilities.removeLastChar(elementName);
	}
	
	private boolean isFieldForType(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema, int objectType, String tag)
	{
		if (objectType == baseObjectSchema.getType() && fieldSchema.getTag().equals(tag))
			return true;
		
		return false;
	}
	
	private String getIdElementName(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema)
	{
		if (DiagramFactor.is(baseObjectSchema.getType()) && fieldSchema.getTag().equals(DiagramFactor.TAG_WRAPPED_REF))
			return WRAPPED_BY_DIAGRAM_FACTOR_ID_ELEMENT_NAME;
		
		if (ThreatReductionResult.is(baseObjectSchema.getType()) && fieldSchema.getTag().equals(ThreatReductionResult.TAG_RELATED_DIRECT_THREAT_REF))
			return RELATED_THREAT_ID;
		
		return getTagToElementNameMap().findElementName(baseObjectSchema, fieldSchema);
	}
	
	private String getIdElementName(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema, int objectType)
	{
		if (ResourceAssignment.is(baseObjectSchema.getType()) && fieldSchema.getTag().equals(ResourceAssignment.TAG_RESOURCE_ID))
			return RESOURCE;
		
		if (DiagramLink.is(baseObjectSchema.getType()) && fieldSchema.getTag().equals(DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID))
			return LINKABLE_FACTOR;
		
		if (DiagramLink.is(baseObjectSchema.getType()) && fieldSchema.getTag().equals(DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID))
			return LINKABLE_FACTOR;
	
		return getProject().getObjectManager().getInternalObjectTypeName(objectType);
	}
	
	private void writeIdElement(String baseName)
	{
		getSchemaWriter().writeElement(createIdName(baseName));
	}

	private void writeBaseObjectSchemaHeader(BaseObjectSchemaWriter baseObjectSchemaWriter)
	{
		String baseObjectName = baseObjectSchemaWriter.getXmpz2ElementName();
		String baseObjectPoolName = baseObjectSchemaWriter.getPoolName();
		
		String result = getSchemaWriter().createZeroOrMoreSchemaElement(baseObjectPoolName, createElementName(baseObjectName));
		getSchemaWriter().defineAlias(createElementName(baseObjectPoolName), result);
		getSchemaWriter().defineAlias(createElementName(baseObjectName), ELEMENT_NAME + PREFIX + baseObjectName);
	}

	private void writeLinkableFactorIds()
	{
		String[] factorNames = Xmpz2GroupedConstants.getLinkableFactorNames();
		
		writeOredSchemaElements(LINKABLE_FACTOR_ID, factorNames);
	}

	private void writeWrappedByDiagramFactorSchemaElement()
	{
		String[] factorNames = Xmpz2GroupedConstants.getWrappableFactorNames();

		writeOredSchemaElements(WRAPPED_BY_DIAGRAM_FACTOR_ID_ELEMENT_NAME, factorNames);
	}

	private void defineVocabulary(ChoiceQuestion question, String vocabularyName)
	{
		CodeList codes = question.getCodesAsReadableCodes();
		defineVocabulary(vocabularyName, codes);
	}
	
	private void defineVocabulary(final String elementName, final CodeList allCodesFromDynamicQuestion)
	{
		getSchemaWriter().print(elementName + " = ");
		
		Vector<String> elements = new Vector<String>();
		for(int index = 0; index < allCodesFromDynamicQuestion.size(); ++index)
		{
			String code = allCodesFromDynamicQuestion.get(index);
			elements.add("'" + code + "'");
		}
		
		getSchemaWriter().writeOredElements(elements);
	}

	private void writeOredSchemaElements(final String parentElementName, final String[] elementNamesAsArray)
	{
		Vector<String> elementNames = Utility.convertToVector(elementNamesAsArray);
		getSchemaWriter().startElementDefinition(parentElementName);
		Vector<String> elements = new Vector<String>();
		for (String elementName : elementNames)
		{
			elements.add(createElementName(createIdName(elementName)));
		}
		
		getSchemaWriter().writeOredElements(elements);
		getSchemaWriter().endBlock();
	}

	private void writeHtmlTagSchemaElements()
	{
		String[] tagNames = Xmpz2GroupedConstants.getHtmlTagsAsElementNames();
		getSchemaWriter().write(FORMATTED_TEXT_TYPE + " = ( ");
		
		Vector<String> elements = new Vector<String>();
		elements.add(TEXT_ELEMENT_TYPE);
		for (int index = 0; index < tagNames.length; ++index)
		{
			elements.add("element." + tagNames[index]);
		}
		
		getSchemaWriter().writeOredElements(elements);
		getSchemaWriter().println(")*");
		
		getSchemaWriter().writeSchemaElement("br", "empty");
		getSchemaWriter().writeSchemaElement("b", FORMATTED_TEXT_TYPE);
		getSchemaWriter().writeSchemaElement("i", FORMATTED_TEXT_TYPE);
		getSchemaWriter().writeSchemaElement("u", FORMATTED_TEXT_TYPE);
		getSchemaWriter().writeSchemaElement("strike", FORMATTED_TEXT_TYPE);
		getSchemaWriter().writeSchemaElement("ul", "element.li*");
		getSchemaWriter().writeSchemaElement("ol", "element.li*");
		getSchemaWriter().writeSchemaElement("li", FORMATTED_TEXT_TYPE);
		
		getSchemaWriter().printlnIndented(getSchemaWriter().createSelfNamedElement("a"));
		getSchemaWriter().startBlock();
		getSchemaWriter().printlnIndented(getSchemaWriter().createTextAttributeElement("href") + " &");
		getSchemaWriter().printlnIndented(getSchemaWriter().createTextAttributeElement("name") + "? &");
		getSchemaWriter().printlnIndented(getSchemaWriter().createTextAttributeElement("title") + "? &");
		getSchemaWriter().printlnIndented(getSchemaWriter().createTextAttributeElement("target") + "? &");			  
		getSchemaWriter().printlnIndented(FORMATTED_TEXT_TYPE);
		getSchemaWriter().endBlock();
	}

	private void writeGeospacialLocationElement()
	{
		String[] elementNames = createVector(LATITUDE, LONGITUDE);
		defineElementWithSameType(GEOSPATIAL_LOCATION, elementNames, "decimal");
	}
	
	private void writeDiagramPointElement()
	{
		String[] elementNames = createVector(X_ELEMENT_NAME, Y_ELEMENT_NAME);
		defineElementWithSameType(DIAGRAM_POINT_ELEMENT_NAME, elementNames, "integer");
	}
	
	private void writeDiagramSizeElement()
	{
		String[] elementNames = createVector(WIDTH_ELEMENT_NAME, HEIGHT_ELEMENT_NAME);
		defineElementWithSameType(DIAGRAM_SIZE_ELEMENT_NAME, elementNames, "integer");
	}
	
	private Xmpz2CustomSchemaDefinitionCreator createDashboardUserChoiceMapSchemaCreator()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), DASHBOARD_STATUS_ENTRY);
		creator.addTextAttributeElement(KEY_ATTRIBUTE_NAME);
		creator.addOptionalChildElement(DASHBOARD_PROGRESS, VOCABULARY_DASHBOARD_ROW_PROGRESS);
		creator.addChildElement(DASHBOARD + DASHBOARD_FLAGS + CONTAINER_ELEMENT_TAG + ".element? ");
		creator.addOptionalChildElement(DASHBOARD_COMMENTS, TEXT_ELEMENT_TYPE);
		
		return creator;
	}

	private void writeDashboardFlagsContainer()
	{
		String falgsContainerSchema = createCodelistSchemaElement("DashboardFlags", StaticQuestionManager.getQuestion(DashboardFlagsQuestion.class));
		getSchemaWriter().println(falgsContainerSchema);
	}
	
	private void writeDateUnitSchemaElements() 
	{
		defineDateUnitEfforts();
		defineFullProjectTimeSpanElement(WORK_UNITS_FULL_PROJECT_TIMESPAN);
		defineYearElement(WORK_UNITS_YEAR);
		defineQuarterElement(WORK_UNITS_QUARTER);
		defineMonthElement(WORK_UNITS_MONTH);
		defineDayElement(WORK_UNITS_DAY);
		
		defineDateUnitExpense();
		defineFullProjectTimeSpanElement(EXPENSES_FULL_PROJECT_TIMESPAN);
		defineYearElement(EXPENSES_YEAR);
		defineQuarterElement(EXPENSES_QUARTER);
		defineMonthElement(EXPENSES_MONTH);
		defineDayElement(EXPENSES_DAY);
	}
	
	private void defineDateUnitEfforts()
	{ 		
		final String[] elementTypes = new String[]{
				createElementName(WORK_UNITS_DAY),
				createElementName(WORK_UNITS_MONTH),
				createElementName(WORK_UNITS_QUARTER),
				createElementName(WORK_UNITS_YEAR),
				createElementName(WORK_UNITS_FULL_PROJECT_TIMESPAN),
				};
		
		getSchemaWriter().defineBudgetElements(DATE_UNIT_WORK_UNITS, WORK_UNITS_DATE_UNIT, WORK_UNITS, elementTypes);
	}

	private void defineDateUnitExpense()
	{
		final String[] elementTypes = new String[]{
				createElementName(EXPENSES_DAY),
				createElementName(EXPENSES_MONTH),
				createElementName(EXPENSES_QUARTER),
				createElementName(EXPENSES_YEAR),
				createElementName(EXPENSES_FULL_PROJECT_TIMESPAN),
		};
		
		getSchemaWriter().defineBudgetElements(DATE_UNITS_EXPENSE, EXPENSES_DATE_UNIT, EXPENSE, elementTypes);
	}
	
	private void defineFullProjectTimeSpanElement(String fullProjectTimeSpanElementName)
	{
		final String[] subElements = new String[]{
				getSchemaWriter().createAttributeElement(FULL_PROJECT_TIMESPAN, VOCABULART_FULL_PROJECT_TIMESPAN),
		};
		defineElement(fullProjectTimeSpanElementName, subElements);
	}
	
	private void defineYearElement(String yearElementName)
	{
		defineYearMonthElement(yearElementName, START_YEAR, START_MONTH);
	}

	private void defineQuarterElement(String quarterElementName)
	{
		defineYearMonthElement(quarterElementName, YEAR, START_MONTH);
	}
	
	private void defineMonthElement(String monthElementName)
	{
		defineYearMonthElement(monthElementName, YEAR, MONTH);
	}

	private void defineYearMonthElement(String elementName, final String yearElementName, final String monthElementName)
	{
		String[] subElements = createVector(createAttribute(yearElementName, VOCABULARY_YEAR), createAttribute(monthElementName, VOCABULARY_MONTH));
		defineElement(elementName, subElements);
	}

	private String createAttribute(final String attributeName, final String attributeType)
	{
		return getSchemaWriter().createAttributeElement(attributeName, attributeType);
	}
	
	private void defineDayElement(String dayElementName)
	{
		String[] subElements = new String[]{
				getSchemaWriter().createAttributeElement(DATE, VOCABULARY_DATE),
		};
		defineElement(dayElementName, subElements);
	}
	
	private void defineElement(String elementName, String[] subElements)
	{
		getSchemaWriter().defineAlias(createElementName(elementName), ELEMENT_NAME + PREFIX + elementName);
		getSchemaWriter().startBlock();
		getSchemaWriter().defineElements(Utility.convertToVector(subElements));
		getSchemaWriter().endBlock();
	}
	
	private Xmpz2CustomSchemaDefinitionCreator creatorThresholdsElementSchemaCreator()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), THRESHOLD);
		creator.addOptionalChildElement(STATUS_CODE, VOCABULARY_MEASUREMENT_STATUS);
		creator.addOptionalChildElement(THRESHOLD_VALUE, TEXT_ELEMENT_TYPE);
		creator.addOptionalChildElement(THRESHOLD_DETAILS, TEXT_ELEMENT_TYPE);
		
		return creator;
	}

	private Xmpz2CustomSchemaDefinitionCreator createTimePeriodCostsElementSchemaCreator()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), TIME_PERIOD_COSTS);
		creator.addChildElement(CALCULATED_START_DATE, VOCABULARY_DATE);
		creator.addChildElement(CALCULATED_END_DATE, VOCABULARY_DATE);
		creator.addOptionalDecimalElement(CALCULATED_EXPENSE_TOTAL);
		creator.addOptionalDecimalElement(CALCULATED_WORK_UNITS_TOTAL);
		creator.addOptionalDecimalElement(CALCULATED_TOTAL_BUDGET_COST);
		creator.addOptionalChildElement(CALCULATED_WHO, getSchemaWriter().createZeroOrMoreDotElement(RESOURCE_ID));
		creator.addOptionalChildElement(CALCULATED_EXPENSE_ENTRIES, getSchemaWriter().createZeroOrMoreDotElement(EXPENSE_ENTRY));
		creator.addChildElement(getSchemaWriter().createOptionalSchemaElement(CALCULATED_WORK_UNITS_ENTRIES, getSchemaWriter().createZeroOrMoreDotElement(WORK_UNITS_ENTRY)));
		
		return creator;
	}

	private Xmpz2CustomSchemaDefinitionCreator createExpenseEntryElementSchemaCreator()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), EXPENSE_ENTRY);
		creator.addOptionalSchemaElement(FUNDING_SOURCE_ID);
		creator.addOptionalSchemaElement(ACCOUNTING_CODE_ID);
		creator.addOptionalSchemaElement(BUDGET_CATEGORY_ONE_ID);
		creator.addOptionalSchemaElement(BUDGET_CATEGORY_TWO_ID);
		creator.addChildElement(getSchemaWriter().createOptionalSchemaElement(EXPENSE_ENTRY + DETAILS, getSchemaWriter().createZeroOrMoreDotElement(DATE_UNITS_EXPENSE)));
		
		return creator;
	}

	private Xmpz2CustomSchemaDefinitionCreator createWorkUnitsEntryElementSchemaCreator()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), WORK_UNITS_ENTRY);
		creator.addOptionalSchemaElement(RESOURCE_ID);
		creator.addOptionalSchemaElement(FUNDING_SOURCE_ID);
		creator.addOptionalSchemaElement(ACCOUNTING_CODE_ID);
		creator.addOptionalSchemaElement(BUDGET_CATEGORY_ONE_ID);
		creator.addOptionalSchemaElement(BUDGET_CATEGORY_TWO_ID);
		creator.addChildElement(getSchemaWriter().createOptionalSchemaElement(WORK_UNITS_ENTRY + DETAILS, getSchemaWriter().createZeroOrMoreDotElement(DATE_UNIT_WORK_UNITS)));
		
		return creator;
	}

	private Xmpz2CustomSchemaDefinitionCreator createExternaIdSchemaElementSchemaCreator()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), EXTERNAL_PROJECT_ID_ELEMENT_NAME);
		creator.addChildElement(EXTERNAL_APP_ELEMENT_NAME, TEXT_ELEMENT_TYPE);
		creator.addChildElement(PROJECT_ID, TEXT_ELEMENT_TYPE);
		
		return creator;
	}

	private Xmpz2CustomSchemaDefinitionCreator createSimpleThreatRatingElementSchemaCreator()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), SIMPLE_BASED_THREAT_RATING);
		creator.addOptionalChildElement(SIMPLE_BASED_THREAT_RATING + SCOPE, VOCABULARY_SIMPLE_THREAT_RATING_SCOPE_CODE);
		creator.addOptionalChildElement(SIMPLE_BASED_THREAT_RATING + SEVERITY, VOCABULARY_SIMPLE_THREAT_RATING_SEVERITY_CODE);
		creator.addOptionalChildElement(SIMPLE_BASED_THREAT_RATING + IRREVERSIBILITY, VOCABULARY_SIMPLE_THREAT_RATING_IRREVERSIBILITY_CODE);
		
		return creator;
    }

	private Xmpz2CustomSchemaDefinitionCreator createStressBasedThreatRatingElementSchemaCreator()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), STRESS_BASED_THREAT_RATING);
		creator.addChildElement(STRESS_BASED_THREAT_RATING + STRESS_ID, getSchemaWriter().createDotElement(STRESS_ID));
		creator.addOptionalChildElement(STRESS_BASED_THREAT_RATING + CONTRIBUTION, VOCABULARY_THREAT_STRESS_RATING_CONTRIBUTION_CODE);
		creator.addOptionalChildElement(STRESS_BASED_THREAT_RATING + IRREVERSIBILITY, VOCABULARY_THREAT_STRESS_RATING_IRREVERSIBILITY_CODE);
		creator.addOptionalChildElement(STRESS_BASED_THREAT_RATING + IS_ACTIVE, getSchemaWriter().createBooleanType());
		creator.addOptionalChildElement(STRESS_BASED_THREAT_RATING + STRESS_RATING, VOCABULARY_THREAT_RATING);
		creator.addOptionalChildElement(STRESS_BASED_THREAT_RATING + THREAT_STRESS_RATING, VOCABULARY_THREAT_RATING);
		
		return creator;
	}
	
	private Xmpz2CustomSchemaDefinitionCreator createDiagramFactorUiSettingsSchemaCreator()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), STYLING);
		creator.addOptionalChildElement(DIAGRAM_FACTOR + DIAGRAM_FACTOR_FONT_SIZE_ELEMENT_NAME, VOCABULARY_DIAGRAM_FACTOR_FONT_SIZE);
		creator.addOptionalChildElement(DIAGRAM_FACTOR + DIAGRAM_FACTOR_FONT_STYLE_ELEMENT_NAME, VOCABULARY_DIAGRAM_FACTOR_FONT_STYLE);
		creator.addOptionalChildElement(DIAGRAM_FACTOR + DIAGRAM_FACTOR_FOREGROUND_COLOR_ELEMENT_NAME, VOCABULARY_DIAGRAM_FACTOR_FOREGROUND_COLOR);
		creator.addOptionalChildElement(DIAGRAM_FACTOR + DIAGRAM_FACTOR_BACKGROUND_COLOR_ELEMENT_NAME, VOCABULARY_DIAGRAM_FACTOR_BACKGROUND_COLOR);
		
		return creator;
	}
	
	private Xmpz2CustomSchemaDefinitionCreator writeExtraDataSectionElement()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), EXTRA_DATA_SECTION);
		creator.addTextAttributeElement(EXTRA_DATA_SECTION_OWNER_ATTRIBUTE);
		creator.addZeroOrMoreDotElement(EXTRA_DATA_ITEM);
		
		return creator;
	}

	private Xmpz2CustomSchemaDefinitionCreator defineExtraDataItemElement()
	{
		Xmpz2CustomSchemaDefinitionCreator creator = new Xmpz2CustomSchemaDefinitionCreator(getSchemaWriter(), EXTRA_DATA_ITEM);
		creator.addTextAttributeElement(EXTRA_DATA_ITEM_NAME);
		creator.addOptionalTextSchemaElement(EXTRA_DATA_ITEM_VALUE);
		
		return creator;
	}
	
	private String createSchemaElement(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema, final String elementType)
	{
		return createElementSchema(baseObjectSchema, fieldSchema, getSchemaWriter().createZeroOrMoreDotElement(elementType));
	}

	private String createElementSchema(BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema, String elementType)
	{
		String poolName = baseObjectSchema.getXmpz2ElementName();
		String elementName = getTagToElementNameMap().findElementName(poolName, fieldSchema.getTag());
		return getSchemaWriter().createOptionalSchemaElement(poolName + elementName, elementType);
	}
	
	private void defineElementWithSameType(String parentName, String[] names, String elementType)
	{
		getSchemaWriter().startElementDefinition(parentName);
		Vector<String> elementNames = new Vector<String>();
		for (int index = 0; index < names.length; ++index)
		{
			final String elementName = getSchemaWriter().createXsdSchemaElement(names[index], elementType);
			elementNames.add(elementName);	
		}
		
		getSchemaWriter().defineElements(elementNames);
		getSchemaWriter().endBlock();
	}

	private Vector<BaseObjectSchemaWriter> getTopLevelBaseObjectSchemas()
	{
		Vector<BaseObjectSchemaWriter> schemaWriters = new Vector<BaseObjectSchemaWriter>();
		for(int objectType = ObjectType.FIRST_OBJECT_TYPE; objectType < ObjectType.OBJECT_TYPE_COUNT; ++objectType)
		{
			if (!isPoolDirectlyInXmpz2(objectType))
				continue;

			BaseObjectPool pool = (BaseObjectPool) getProject().getPool(objectType);
			if(pool == null)
				continue;
			
			BaseObjectSchema baseObjectSchema = pool.createBaseObjectSchema(getProject());
			schemaWriters.add(createSchemaWriter(baseObjectSchema));
		}
		
		schemaWriters.add(new ThreatTargetThreatRatingSchemaWriter(this));
		schemaWriters.add(new ExtraDataWriter(this));
		
		return schemaWriters;
	}

	private BaseObjectSchemaWriter createSchemaWriter(BaseObjectSchema baseObjectSchema)
	{
		if (Indicator.is(baseObjectSchema.getType()))
			return new IndicatorSchemaWriter(this, baseObjectSchema);
		
		if (Desire.isDesire(baseObjectSchema.getType()))
			return new DesireSchemaWriter(this, baseObjectSchema);
		
		if (Task.is(baseObjectSchema.getType()))
			return new TaskSchemaWriter(this, baseObjectSchema);
		
		if (Strategy.is(baseObjectSchema.getType()))
			return new StrategySchemaWriter(this, baseObjectSchema);
		
		if (DiagramLink.is(baseObjectSchema.getType()))
			return new DiagramLinkSchemaWriter(this, baseObjectSchema);
		
		if (DiagramFactor.is(baseObjectSchema.getType()))
			return new DiagramFactorSchemaWriter(this, baseObjectSchema);
		
		if (ObjectTreeTableConfiguration.is(baseObjectSchema.getType()))
			return new ObjectTreeTableConfigurationSchemaWriter(this, baseObjectSchema);
		
		if (Target.is(baseObjectSchema.getType()))
			return new TargetSchemaWriter(this, baseObjectSchema);
		
		if (HumanWelfareTarget.is(baseObjectSchema.getType()))
			return new HumanWelfareTargetSchemaWriter(this, baseObjectSchema);
		
		if (Dashboard.is(baseObjectSchema.getType()))
			return new DashboardSchemaWriter(this, baseObjectSchema);
		
		return new BaseObjectSchemaWriter(this, baseObjectSchema);
	}
	
	private boolean isPoolDirectlyInXmpz2(int objectType)
	{
		if (FactorLink.is(objectType))
			return false;
		
		if (ViewData.is(objectType))
			return false;
		
		if (ProjectMetadata.is(objectType))
			return false;

		if (CostAllocationRuleSchema.getObjectType() == objectType)
			return false;

		if (ThreatStressRating.is(objectType))
			return false;

		if (WcpaProjectDataSchema.getObjectType() == objectType)
			return false;

		if (WwfProjectDataSchema.getObjectType() == objectType)
			return false;

		if (RareProjectDataSchema.getObjectType() == objectType)
			return false;

		if (WcsProjectDataSchema.getObjectType() == objectType)
			return false;

		if (TncProjectDataSchema.getObjectType() == objectType)
			return false;

		if (FosProjectDataSchema.getObjectType() == objectType)
			return false;

		if (WwfProjectDataSchema.getObjectType() == objectType)
			return false;

		if (Xenodata.is(objectType))
			return false;

		if (TableSettings.is(objectType))
			return false;

		if (ThreatRatingCommentsData.is(objectType))
			return false;

		if (RatingCriterion.is(objectType))
			return false;

		if (ValueOptionSchema.getObjectType() == objectType)
			return false;

		if (ReportTemplate.is(objectType))
			return false;

		if (XslTemplate.is(objectType))
			return false;

		return true;
	}
	
	private String getDateUnitTypeName(int objectType)
	{
		if (ExpenseAssignment.is(objectType))
			return EXPENSE;
		
		if (ResourceAssignment.is(objectType))
			return "WorkUnits";
		
		throw new RuntimeException("Object type " + objectType + " cannot have a dateunitEffortsList field");
	}
	
	private String[] createVector(String item1, String item2)
	{
		final String[] items = new String[]{
				item1,
				item2,
		};

		return items;
	}
	
	private String createIdName(String objectName)
	{
		return objectName + ID;
	}
	

	public ChoiceQuestionToSchemaElementNameMap getChoiceQuestionToSchemaElementNameMap()
	{
		return choiceQuestionToSchemaElementNameMap;
	}
		
	private Xmpz2TagToElementNameMap getTagToElementNameMap()
	{
		return tagToElementNameMap;
	}
	
	public Xmpz2SchemaWriter getSchemaWriter()
	{
		return writer;
	}

	public Project getProject()
	{
		return project;
	}
	
	private Xmpz2TagToElementNameMap tagToElementNameMap;
	private ChoiceQuestionToSchemaElementNameMap choiceQuestionToSchemaElementNameMap;
	private Project project;
	private Xmpz2SchemaWriter writer;
	private Vector<BaseObjectSchemaWriter> baseObjectSchemaWriters;
	private Vector<String> codelistSchemaElements;
}
