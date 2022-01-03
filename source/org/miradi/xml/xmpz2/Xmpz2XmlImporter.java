/* 
Copyright 2005-2021, Foundations of Success, Bethesda, Maryland
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

package org.miradi.xml.xmpz2;

import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.miradi.exceptions.XmlValidationException;
import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objectdata.AbstractUserTextDataWithHtmlFormatting;
import org.miradi.objectdata.BooleanData;
import org.miradi.objecthelpers.*;
import org.miradi.objectpools.BaseObjectPool;
import org.miradi.objects.*;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.schemas.*;
import org.miradi.utils.*;
import org.miradi.xml.AbstractXmlImporter;
import org.miradi.xml.AbstractXmlNamespaceContext;
import org.miradi.xml.MiradiXmlValidator;
import org.miradi.xml.xmpz2.objectImporters.*;
import org.miradi.xml.xmpz2.xmpz2schema.Xmpz2NameSpaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.util.*;

public class Xmpz2XmlImporter extends AbstractXmlImporter implements Xmpz2XmlConstants
{
	public Xmpz2XmlImporter(Project projectToFill, ProgressInterface progressIndicatorToUse) throws Exception
	{
		super(projectToFill);
		
		tagToElementNameMap = new Xmpz2TagToElementNameMap();
		progressIndicator = progressIndicatorToUse;

		uuidList = new HashSet<String>();
		invalidXmlFileMessage =
				(EAM.text("An error is preventing this project from importing correctly. " +
						"Most likely, the project has been corrupted. Please contact " +
						"the Miradi team for help and advice. We recommend that you do not " +
						"make any changes to this project until this problem has been resolved."));
	}
	
	@Override
	protected Xmpz2MigrationResult migrate(InputStreamWithSeek projectAsInputStream) throws Exception
	{
		Xmpz2ForwardMigration migration = new Xmpz2ForwardMigration();
		return migration.migrate(projectAsInputStream);
	}
		
	@Override
	protected void importXml() throws Exception
	{
		LinkedHashMap<Integer, BaseObjectImporter> typeToImporterMap = fillTypeToImporterMap();
		elementNameToTypeMap = createIdElementNameToTypeMap();
		progressIndicator.setStatusMessage(EAM.text("Importing XML..."),  12 + typeToImporterMap.size());
		
		importSummaryData();
		incrementProgress();
		
		importTncProjectData();
		incrementProgress();
		
		importSingletonObject(new WwfProjectDataSchema());
		incrementProgress();
		
		importSingletonObject(new WcsProjectDataSchema());
		incrementProgress();
		
		importSingletonObject(new FosProjectDataSchema());
		incrementProgress();
		
		importSingletonObject(new RareProjectDataSchema());
		incrementProgress();
		
		importSingletonObject(new MiradiShareProjectDataSchema());
		incrementProgress();
		
		importPools(typeToImporterMap);
		incrementProgress();
		
		importThreatTargetRatings();
		incrementProgress();
		
		importDashboardData();
		incrementProgress();
		
		importExtraData();
		incrementProgress();
	}
	
	private LinkedHashMap<Integer, BaseObjectImporter> fillTypeToImporterMap()
	{
		LinkedHashMap<Integer, BaseObjectImporter> typeToImporterMap = new LinkedHashMap<Integer, BaseObjectImporter>();		
		addImporterToMap(typeToImporterMap, new ConceptualModelDiagramImporter(this));
		addImporterToMap(typeToImporterMap, new ResultsChainDiagramImporter(this));
		addImporterToMap(typeToImporterMap, new DiagramFactorImporter(this));
		addImporterToMap(typeToImporterMap, new DiagramLinkImporter(this));
		addImporterToMap(typeToImporterMap, new StrategyImporter(this));

		// NOTE: Must import all factor types before importing objectives, for relevancy
		addImporterToMap(typeToImporterMap, new BaseObjectImporter(this, new CauseSchema()));
		addImporterToMap(typeToImporterMap, new BaseObjectImporter(this, new IntermediateResultSchema()));
		addImporterToMap(typeToImporterMap, new ThreatReductionResultsImporter(this));
		addImporterToMap(typeToImporterMap, new BaseObjectImporter(this, new TargetSchema()));
		addImporterToMap(typeToImporterMap, new BaseObjectImporter(this, new BiophysicalFactorSchema()));
		addImporterToMap(typeToImporterMap, new BaseObjectImporter(this, new BiophysicalResultSchema()));

		// NOTE: Must import all KEA's before importing objectives/goals, for relevancy
		addImporterToMap(typeToImporterMap, new BaseObjectImporter(this, new KeyEcologicalAttributeSchema()));

		addImporterToMap(typeToImporterMap, new IndicatorImporter(this));
		addImporterToMap(typeToImporterMap, new MethodImporter(this, new MethodSchema()));
		addImporterToMap(typeToImporterMap, new TimeframeImporter(this));
		addImporterToMap(typeToImporterMap, new ResourceAssignmentImporter(this));
		addImporterToMap(typeToImporterMap, new ExpenseAssignmentImporter(this));
		addImporterToMap(typeToImporterMap, new ObjectiveImporter(this));
		addImporterToMap(typeToImporterMap, new GoalImporter(this));
		addImporterToMap(typeToImporterMap, new TaskImporter(this));
		addImporterToMap(typeToImporterMap, new TaggedObjectSetImporter(this));
		addImporterToMap(typeToImporterMap, new TaxonomyImporter(this));
		addImporterToMap(typeToImporterMap, new ObjectTreeTableConfigurationImporter(this));

		addImporterToMap(typeToImporterMap, new AnalyticalQuestionImporter(this));
		addImporterToMap(typeToImporterMap, new AssumptionImporter(this));

		for(int objectType = ObjectType.FIRST_OBJECT_TYPE; objectType < ObjectType.OBJECT_TYPE_COUNT; ++objectType)
		{
			if (isCustomImport(objectType))
				continue;
			
			BaseObjectPool pool = getProject().getPool(objectType);
			if (pool == null)
				continue;
			
			if (typeToImporterMap.containsKey(objectType))
				continue;
			
			BaseObjectSchema baseObjectSchema = pool.createBaseObjectSchema(getProject());
			addImporterToMap(typeToImporterMap, new BaseObjectImporter(this, baseObjectSchema));
		}
		
		return typeToImporterMap;
	}

	private void addImporterToMap(LinkedHashMap<Integer, BaseObjectImporter> typeToImporterMap, BaseObjectImporter importer)
	{
		typeToImporterMap.put(importer.getBaseObjectSchema().getType(), importer);
	}

	private void importPools(LinkedHashMap<Integer, BaseObjectImporter> typeToImporterMap) throws Exception
	{
		Set<Integer> objectTypesAsKeys = typeToImporterMap.keySet();
		for(Integer objectType : objectTypesAsKeys)
		{
			BaseObjectImporter importer = typeToImporterMap.get(objectType);
			importBaseObjects(importer);
			importTaxonomyAssociationsForType(objectType);

			if (ResourceAssignment.is(objectType) || (ExpenseAssignment.is(objectType)))
				importAccountingClassificationAssociationsForType(objectType);

			incrementProgress();
		}
	}
	
	private void importTaxonomyAssociationsForType(final int objectType) throws Exception
	{
		Vector<String> poolNamesForType = TaxonomyHelper.getTaxonomyAssociationPoolNamesForType(objectType);
		for (String poolNameForType : poolNamesForType)
		{
			TaxonomyAssociationImporter importer = new TaxonomyAssociationImporter(this, poolNameForType, objectType);
			importBaseObjects(importer, poolNameForType);
		}
	}

	private void importAccountingClassificationAssociationsForType(final int objectType) throws Exception
	{
		Vector<String> poolNamesForType = TaxonomyHelper.getAccountingClassificationAssociationPoolNamesForType(objectType);
		for (String poolNameForType : poolNamesForType)
		{
			AccountingClassificationAssociationImporter importer = new AccountingClassificationAssociationImporter(this, poolNameForType, objectType);
			importBaseObjects(importer, poolNameForType);
		}
	}

	public static boolean isCustomImport(int objectType)
	{
		if (RatingCriterion.is(objectType))
			return true;
		
		if (ValueOptionSchema.getObjectType() == objectType)
			return true;
		
		if (Dashboard.is(objectType))
			return true;
		
		if (ReportTemplate.is(objectType))
			return true;
		
		if (XslTemplate.is(objectType))
			return true;
		
		return false;
	}

	private void importBaseObjects(final BaseObjectImporter importer) throws Exception
	{
		importBaseObjects(importer, importer.createPoolElementName());
	}

	private void importBaseObjects(final BaseObjectImporter importer, final String containerElementName) throws Exception
	{
		final String elementObjectName = importer.getBaseObjectSchema().getXmpz2ElementName();
		final Node rootNode = getRootNode();
		final NodeList baseObjectNodes = getNodes(rootNode, new String[]{containerElementName, elementObjectName, });
		for (int index = 0; index < baseObjectNodes.getLength(); ++index)
		{
			Node baseObjectNode = baseObjectNodes.item(index);
			ORef ref = importer.createBaseObject(importer, baseObjectNode);
			importer.importFields(baseObjectNode, ref);
			importer.postCreateFix(ref, baseObjectNode);
		}
	}

	private void importTncProjectData() throws Exception
	{
		importSingletonObject(new TncProjectDataImporter(this), new TncProjectDataSchema());
	}

	private void importSingletonObject(BaseObjectSchema baseObjectSchema) throws Exception
	{
		importSingletonObject(new SingletonObjectImporter(this, baseObjectSchema), baseObjectSchema);
	}
	
	private void importSingletonObject(final SingletonObjectImporter importer, final BaseObjectSchema baseObjectSchema) throws Exception
	{
		final Node singletonNode = getNamedChildNode(getRootNode(), baseObjectSchema.getXmpz2ElementName());
		if (singletonNode == null)
			return;
		
		importer.importFields(singletonNode, getSingletonObjectRef(baseObjectSchema.getType()));
	}
	
	private ORef getSingletonObjectRef(int objectType)
	{
		return getProject().getSingletonObjectRef(objectType);
	}

	public void importRefs(Node node, ORef destinationRef, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema, String reflistTypeName) throws Exception
	{
		ORefList importedRefs = extractRefs(node, baseObjectSchema.getXmpz2ElementName(), fieldSchema.getTag(), reflistTypeName);
		setData(destinationRef, fieldSchema.getTag(), importedRefs);
	}
	
	public void importIds(Node baseObjectNode, ORef destinationRef,	BaseObjectSchema baseObjectSchema, String idsElementName, String idElementName, int idListType) throws Exception
	{
		ORefList importedRefs = extractRefs(baseObjectNode, baseObjectSchema.getXmpz2ElementName(), idsElementName, idElementName);
		setData(destinationRef, idsElementName, importedRefs.convertToIdList(idListType));
	}
	
	public void importIds(Node node, ORef destinationRefToUse,	BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema,	int idListType) throws Exception
	{
		ORefList importedRefs = extractRefs(node, baseObjectSchema.getXmpz2ElementName(), fieldSchema);
		setData(destinationRefToUse, fieldSchema.getTag(), importedRefs.convertToIdList(idListType));
	}
	
	private ORefList extractRefs(Node node, String baseObjectElementName, AbstractFieldSchema fieldSchema) throws Exception
	{
		String elementName = findElementName(baseObjectElementName, fieldSchema.getTag());
		String idElementName = convertIdsToIdString(elementName);
		return extractRefsFromNodes(node, baseObjectElementName, elementName, idElementName);
	}
	
	public ORefList extractRefs(Node node, String baseObjectElementName, String idsElementName, String idElementName) throws Exception
	{
		String elementName = findElementName(baseObjectElementName, idsElementName);
		return extractRefsFromNodes(node, baseObjectElementName, elementName,  idElementName + ID);
	}
	
	private ORefList extractRefsFromNodes(Node node, final String baseObjectElementName, final String elementName, final String idElementName) throws Exception
	{
		final String idsContainerName = baseObjectElementName + elementName;
		NodeList idNodes = getNodes(node, new String[]{idsContainerName, idElementName});
		ORefList importedRefs = new ORefList();
		for (int index = 0; index < idNodes.getLength(); ++index)
		{
			Node idNode = idNodes.item(index);
			String id = getSafeNodeContent(idNode);
			int idsType = getObjectTypeOfNode(idNode);
			importedRefs.add(new ORef(idsType, new BaseId(id)));
		}
		
		return importedRefs;
	}
	
	private String convertIdsToIdString(String elementName)
	{
		return StringUtilities.removeLastChar(elementName);
	}

	public void importCodeListField(Node node, String elementContainerName, ORef destinationRef, String destinationTag, ChoiceQuestion question) throws Exception
	{
		String elementName = findElementName(elementContainerName, destinationTag);
		String containerElementName = Xmpz2XmlWriter.createContainerElementName(elementContainerName + elementName);
		CodeList readableCodesToImport = getCodeList(node, containerElementName);
		CodeList internalCodes = convertToInternalCodes(question, readableCodesToImport);
		
		setData(destinationRef, destinationTag, internalCodes.toString());
	}

	private CodeList convertToInternalCodes(ChoiceQuestion question, CodeList readableCodesToImport)
	{
		CodeList internalCodes = new CodeList();
		for (String readableCode : readableCodesToImport)
		{
			internalCodes.add(question.convertToInternalCode(readableCode));
		}
		
		return internalCodes;
	}

	public CodeList getCodeList(Node node, String containerElementName) throws Exception
	{
		NodeList codeNodes = getNodes(node, new String[]{containerElementName, CODE_ELEMENT_NAME});
		CodeList codesToImport = new CodeList();
		for (int index = 0; index < codeNodes.getLength(); ++index)
		{
			Node codeNode = codeNodes.item(index);
			String code = getSafeNodeContent(codeNode);
			codesToImport.add(code);
		}
		
		return codesToImport;
	}
	
	public void importCodeField(Node node, String containerName, ORef destinationRef, String destinationTag, ChoiceQuestion question) throws Exception
	{
		Xmpz2TagToElementNameMap map = new Xmpz2TagToElementNameMap();
		String elementName = map.findElementName(containerName, destinationTag);
		final String containerElementName = containerName  + elementName;
		importCodeFieldWithoutElementNameSubstitute(node, containerElementName, destinationRef,	destinationTag, question);
	}

	public void importCodeFieldWithoutElementNameSubstitute(Node node, final String containerElementName, ORef destinationRef, String destinationTag, ChoiceQuestion question) throws XPathExpressionException, Exception
	{
		String importedReadableCode = getPathData(node, new String[]{containerElementName, });
		String internalCode = question.convertToInternalCode(importedReadableCode);		
		importField(destinationRef, destinationTag, internalCode);
	}

    public void importFieldQualifiedByContainerName(Node node, String containerName, ORef destinationRef, String destinationTag) throws Exception
    {
        Xmpz2TagToElementNameMap map = new Xmpz2TagToElementNameMap();
        String elementName = map.findElementName(containerName, destinationTag);
        final String containerElementName = containerName  + elementName;
        String fieldDataToImport = getPathData(node, new String[]{containerElementName, });
        importField(destinationRef, destinationTag, fieldDataToImport);
    }

	public int getObjectTypeOfNode(Node typedIdNode)
	{
		String nodeName = typedIdNode.getNodeName();
		String objectTypeNameWithNamespace = removeAppendedId(nodeName);
		String objectTypeName = removeNamespacePrefix(objectTypeNameWithNamespace);
		if (elementNameToTypeMap.containsKey(objectTypeName))
			return elementNameToTypeMap.get(objectTypeName);
		
		EAM.logError("Could not find type for node: " + objectTypeName);
		return ObjectType.FAKE;
	}

	private HashMap<String, Integer> createIdElementNameToTypeMap()
	{
		HashMap<String, Integer> elementNameToType = new HashMap<String, Integer>();
		for(int objectType = ObjectType.FIRST_OBJECT_TYPE; objectType < ObjectType.OBJECT_TYPE_COUNT; ++objectType)
		{
			BaseObjectPool pool = getProject().getPool(objectType);
			if (pool == null)
				continue;
			
			BaseObjectSchema baseObjectSchema = pool.createBaseObjectSchema(getProject());
			elementNameToType.put(baseObjectSchema.getXmpz2ElementName(), baseObjectSchema.getType());
			if (Cause.is(objectType))
			{
				elementNameToType.put(THREAT, baseObjectSchema.getType());
			}
			if (Task.is(objectType))
			{
				elementNameToType.put(TaskSchema.ACTIVITY_NAME, TaskSchema.getObjectType());
				elementNameToType.put(SUB_TASK, TaskSchema.getObjectType());
			}
		}
		
		return elementNameToType;
	}
	
	private String removeNamespacePrefix(String objectTypeNameWithNamespace)
	{
		int indexOfPrefix = objectTypeNameWithNamespace.indexOf(":");
		return objectTypeNameWithNamespace.substring(indexOfPrefix + 1, objectTypeNameWithNamespace.length());
	}

	private static String removeAppendedId(String nodeName)
	{
		return nodeName.replaceFirst(ID, "");
	}
	
	public void importBooleanField(Node node, ORef destinationRefToUse,	BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node booleanNode = getNamedChildNode(node, baseObjectSchema.getXmpz2ElementName() + fieldSchema.getTag());
		String isValue = BooleanData.BOOLEAN_FALSE;
		if (booleanNode != null && isTrue(booleanNode.getTextContent()))
			isValue = BooleanData.BOOLEAN_TRUE;

		setData(destinationRefToUse, fieldSchema.getTag(), isValue);
	}
	
	public void importChoiceField(Node node, String parentElementName, ORef destinationRefToUse, String tag, ChoiceQuestion question) throws Exception
	{
		String choiceElementName = findElementName(parentElementName, tag);
		String importedReadableCode = getPathData(node, new String[]{parentElementName + choiceElementName, });
		String internalCode = question.convertToInternalCode(importedReadableCode);
		importField(destinationRefToUse, tag, internalCode);
	}
	
	public void importStringField(Node node, String poolName, ORef destinationRef, String destinationTag) throws Exception
	{
		String elementName = findElementName(poolName, destinationTag);
		importField(node, poolName + elementName, destinationRef, destinationTag);
	}
	
	public void importUUIDField(Node node, String poolName, ORef destinationRef, String destinationTag) throws Exception
	{
		String elementName = findElementName(poolName, destinationTag);
		String path = poolName + elementName;
		String[] elements = new String[]{path,};

		String data = getPathData(node, elements);
		data = escapeDueToParserUnescaping(data);

		if (uuidList.contains(data))
			throw new XmlValidationException(invalidXmlFileMessage);

		importField(destinationRef, destinationTag, data);
		uuidList.add(data);
	}

	public void importFormattedStringField(Node node, String poolName, ORef destinationRef, String destinationTag) throws Exception
	{
		try
		{
			String elementName = findElementName(poolName, destinationTag);
			importFormattedField(node, poolName + elementName, destinationRef, destinationTag);
		} 
		catch (TransformerException e) 
		{
			EAM.alertUserOfNonFatalException(e);
		}
	}
	
	private void importFormattedField(Node parentNode, String childNodeName, ORef ref, String destinationTag) throws Exception
	{
		Node node = getNamedChildNode(parentNode, childNodeName);
		if (node == null)
			return;

		String nodeTreeAsString = getFormattedNodeContent(node);
		importField(ref, destinationTag, nodeTreeAsString);
	}

	public static String getFormattedNodeContent(Node node)	throws Exception
	{
		Document document = createDomDocument();
		return getFormattedNodeContent(node, document);
	}

	public static String getFormattedNodeContent(Node node, Document documentToUse)	throws Exception
	{
		final Node clonedNode = node.cloneNode(true);

		Node adoptedNode = documentToUse.adoptNode(clonedNode);
		Element rootElement = documentToUse.getDocumentElement();
		Node appendedNode = rootElement != null ?  rootElement.appendChild(adoptedNode) : documentToUse.appendChild(adoptedNode);
		String nodeTreeAsString = nodeToString(appendedNode);
		nodeTreeAsString = HtmlUtilitiesRelatedToShef.getNormalizedAndSanitizedHtmlText(nodeTreeAsString, AbstractUserTextDataWithHtmlFormatting.getAllowedHtmlTags());

		return nodeTreeAsString;
	}

	private static Document createDomDocument() throws Exception
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		return documentBuilder.newDocument();
	}

	private static String nodeToString(Node node) throws Exception
	{
		return HtmlUtilities.toXmlString(new DOMSource(node));
	}
	
	public void importDimensionField(Node node, ORef destinationRef, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node diagramFactorSizeNode = getNamedChildNode(node, baseObjectSchema.getXmpz2ElementName() + SIZE);
		Node sizNode = getNamedChildNode(diagramFactorSizeNode, DIAGRAM_SIZE_ELEMENT_NAME);
		Node widthNode = getNamedChildNode(sizNode, WIDTH_ELEMENT_NAME);
		Node heightNode = getNamedChildNode(sizNode, HEIGHT_ELEMENT_NAME);
		int width = extractNodeTextContentAsInt(widthNode);
		int height = extractNodeTextContentAsInt(heightNode);
		String dimensionAsString = EnhancedJsonObject.convertFromDimension(new Dimension(width, height));
		setData(destinationRef, DiagramFactor.TAG_SIZE, dimensionAsString);
	}
	
	public void importDiagramPointField(Node node, ORef destinationRef, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node locationNode = getNamedChildNode(node, baseObjectSchema.getXmpz2ElementName() + LOCATION);
		Node pointNode = getNamedChildNode(locationNode, DIAGRAM_POINT_ELEMENT_NAME);
		Point point = extractPointFromNode(pointNode);
		String pointAsString = EnhancedJsonObject.convertFromPoint(point);
		setData(destinationRef, DiagramFactor.TAG_LOCATION, pointAsString);
	}
	
	public void importIdField(Node node, ORef destinationRefToUse, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		String poolName = baseObjectSchema.getXmpz2ElementName();
		String elementName = findElementName(poolName, fieldSchema.getTag());
		ORef refToImport = getRefToImport(node, poolName + elementName, elementName);
		setData(destinationRefToUse, fieldSchema.getTag(), refToImport.getObjectId().toString());
	}
	
	public void importRefField(Node node, ORef destinationRefToUse,	BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		String tag = fieldSchema.getTag();
		String poolName = baseObjectSchema.getXmpz2ElementName();
		
		String elementName = findElementName(poolName, tag);
		importRef(node, destinationRefToUse, poolName, tag, elementName, elementName);
	}

	public void importRefField(Node node, ORef destinationRefToUse, final String poolName, final String tag, final String idElementName) throws Exception
	{
		String elementName = findElementName(poolName, tag);
		importRef(node, destinationRefToUse, poolName, tag, elementName, idElementName);
	}

	private void importRef(Node node, ORef destinationRefToUse, final String poolName, final String tag, String elementName, final String idElementName) throws Exception
	{
		importRef(node, destinationRefToUse, tag, poolName + elementName, idElementName);
	}

	private void importRef(Node node, ORef destinationRefToUse, final String tag, final String parentElementName, final String idElementName) throws Exception
	{
		ORef refToImport = getRefToImport(node, parentElementName, idElementName);
		if (refToImport.isValid())
			setData(destinationRefToUse, tag, refToImport.toString());
	}
	
	private ORef getRefToImport(Node node, String parentElementName, String elementName) throws Exception
	{
		Node idParentNode = getNamedChildNode(node, parentElementName);
		if (idParentNode == null)
			return ORef.INVALID;
		
		Node idNode = getNamedChildNode(idParentNode, elementName);
		if (idNode == null)
			return ORef.INVALID;
		
		String trimmedIdAsString = getSafeNodeContent(idNode);
		
		return new ORef(getObjectTypeOfNode(idNode), new BaseId(trimmedIdAsString));
	}
	
	public void importPointListField(Node node, ORef destinationRef, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node bendPointsNode = getNamedChildNode(node, baseObjectSchema.getXmpz2ElementName() + BEND_POINTS_ELEMENT_NAME);
		if (bendPointsNode == null)
			return;
		
		NodeList bendPointNodes = getNodes(bendPointsNode, new String[]{DIAGRAM_POINT_ELEMENT_NAME, });
		PointList bendPoints = new PointList();
		for (int index = 0; index < bendPointNodes.getLength(); ++index)
		{
			Node bendPointNode = bendPointNodes.item(index);
			Point bendPoint = extractPointFromNode(bendPointNode);
			bendPoints.add(bendPoint);
		}

		setData(destinationRef, DiagramLink.TAG_BEND_POINTS, bendPoints.toString());
	}

	private void importSummaryData() throws Exception
	{
		new Xmpz2ProjectSummaryImporter(this).importFields();
		new Xmpz2ProjectScopeImporter(this).importFields();
		new Xmpz2ProjectLocationImporter(this).importFields();
		new Xmpz2ProjectPlanningImporter(this).importFields();
	}

	private void importThreatTargetRatings() throws Exception
	{
		new SimpleThreatRatingImporter(this).importFields();
		new StressBasedThreatTargetThreatRatingImporter(this).importFields();
	}
	
	private void importDashboardData() throws Exception
	{
		new DashboardImporter(this).importFields();
	}

	private void importExtraData() throws Exception
	{
		new Xmpz2ExtraDataImporter(this).importFields();
	}
	
	public void importTaxonomyElementList(Node node, ORef destinationRef, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node taxonomyElementListNode = getNamedChildNode(node, TAXONOMY_ELEMENTS);
		Set<Node> taxonomyElementCodes = getNamedChildNodes(taxonomyElementListNode, TAXONOMY_ELEMENT);
		TaxonomyElementList taxonomyElements =  new TaxonomyElementList();
		for(Node taxonomyElementNode : taxonomyElementCodes)
		{
			TaxonomyElement taxonomyElement = new TaxonomyElement();
			taxonomyElement.setCode(getAttributeValue(taxonomyElementNode, TAXONOMY_ELEMENT_CODE));
			taxonomyElement.setLabel(getNamedChildNodeContent(taxonomyElementNode, TAXONOMY_ELEMENT_LABEL));
			taxonomyElement.setDescription(getNamedChildNodeContent(taxonomyElementNode, TAXONOMY_ELEMENT_DESCRIPTION));
			taxonomyElement.setUserCode(getNamedChildNodeContent(taxonomyElementNode, TAXONOMY_ELEMENT_USER_CODE));
			final CodeList childCodes = getCodeList(taxonomyElementNode, Xmpz2XmlWriter.createContainerElementName(TAXONOMY_ELEMENT_CHILD_CODE));
			taxonomyElement.setChildCodes(childCodes);
			
			taxonomyElements.add(taxonomyElement);
		} 
		
		setData(destinationRef, MiradiShareTaxonomySchema.TAG_TAXONOMY_ELEMENTS, taxonomyElements.toJsonString());
	}
	
	public void importTaxonomyClassificationList(Node node, ORef destinationRef, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node taxonomyClassificationContainerNode = getNamedChildNode(node, TAXONOMY_CLASSIFICATION_CONTAINER);
		if (taxonomyClassificationContainerNode == null)
			return;
		
		NodeList taxonomyClassificationNodeList = getNodes(taxonomyClassificationContainerNode, new String[]{TAXONOMY_CLASSIFICATION, });
		TaxonomyClassificationMap taxonomyClassificationsList = new TaxonomyClassificationMap();
		for (int index = 0; index < taxonomyClassificationNodeList.getLength(); ++index)
		{
			Node taxonomyClassificationNode = taxonomyClassificationNodeList.item(index);
			Node taxonomyClassificationTaxonomyCodeNode = getNamedChildNode(taxonomyClassificationNode, TAXONOMY_CLASSIFICATION_TAXONOMY_CODE);
			final String taxonomyCode = taxonomyClassificationTaxonomyCodeNode.getTextContent();
			String containerElementName = Xmpz2XmlWriter.createContainerElementName(TAXONOMY_CLASSIFICATION_TAXONOMY_ELEMENT_CODE);
			final CodeList taxonomyElementCodes = getCodeList(taxonomyClassificationNode, containerElementName);
			taxonomyClassificationsList.putCodeList(taxonomyCode, taxonomyElementCodes);
		}

		setData(destinationRef, BaseObject.TAG_TAXONOMY_CLASSIFICATION_CONTAINER, taxonomyClassificationsList.toJsonString());
	}

	public void importAccountingClassificationList(Node node, ORef destinationRef, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node AccountingClassificationContainerNode = getNamedChildNode(node, ACCOUNTING_CLASSIFICATION_CONTAINER);
		if (AccountingClassificationContainerNode == null)
			return;
		
		NodeList accountingClassificationNodeList = getNodes(AccountingClassificationContainerNode, new String[]{ACCOUNTING_CLASSIFICATION, });
		TaxonomyClassificationMap accountingClassificationsList = new TaxonomyClassificationMap();
		for (int index = 0; index < accountingClassificationNodeList.getLength(); ++index)
		{
			Node accountingClassificationNode = accountingClassificationNodeList.item(index);
			Node accountingClassificationTaxonomyCodeNode = getNamedChildNode(accountingClassificationNode, ACCOUNTING_CLASSIFICATION_TAXONOMY_CODE);
			final String taxonomyCode = accountingClassificationTaxonomyCodeNode.getTextContent();
			String containerElementName = Xmpz2XmlWriter.createContainerElementName(ACCOUNTING_CLASSIFICATION_TAXONOMY_ELEMENT_CODE);
			final CodeList taxonomyElementCodes = getCodeList(accountingClassificationNode, containerElementName);
			accountingClassificationsList.putCodeList(taxonomyCode, taxonomyElementCodes);
		}

		setData(destinationRef, AbstractAssignmentSchema.TAG_ACCOUNTING_CLASSIFICATION_CONTAINER, accountingClassificationsList.toJsonString());
	}

	@Override
	protected String getNameSpaceVersion()
	{
		return NAME_SPACE_VERSION;
	}

	@Override
	protected String getPartialNameSpace()
	{
		return PARTIAL_NAME_SPACE;
	}

	@Override
	protected String getRootNodeName()
	{
		return CONSERVATION_PROJECT;
	}
	
	@Override
	public AbstractXmlNamespaceContext getNamespaceContext()
	{
		return new Xmpz2NameSpaceContext();
	}
	
	@Override
	protected MiradiXmlValidator createXmlValidator()
	{
		return new Xmpz2XmlValidator();
	}

	private String findElementName(String poolName, String destinationTag)
	{
		return getTagToElementNameMap().findElementName(poolName, destinationTag);
	}
	
	private Xmpz2TagToElementNameMap getTagToElementNameMap()
	{
		return tagToElementNameMap;
	}
	
	private void incrementProgress() throws Exception
	{
		progressIndicator.incrementProgress();
	}
	
	private Xmpz2TagToElementNameMap tagToElementNameMap;
	protected ProgressInterface progressIndicator;
	private HashMap<String, Integer> elementNameToTypeMap;
	private HashSet<String> uuidList;
	String invalidXmlFileMessage;
}
