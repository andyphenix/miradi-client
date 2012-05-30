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

package org.miradi.xml.xmpz2;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Collection;
import java.util.LinkedHashMap;

import javax.xml.namespace.NamespaceContext;

import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objectdata.BooleanData;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.BaseObjectPool;
import org.miradi.objects.Dashboard;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.RatingCriterion;
import org.miradi.objects.ReportTemplate;
import org.miradi.objects.XslTemplate;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.AccountingCodeSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.schemas.BudgetCategoryOneSchema;
import org.miradi.schemas.BudgetCategoryTwoSchema;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.ExpenseAssignmentSchema;
import org.miradi.schemas.FosProjectDataSchema;
import org.miradi.schemas.FundingSourceSchema;
import org.miradi.schemas.GoalSchema;
import org.miradi.schemas.GroupBoxSchema;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.IntermediateResultSchema;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.schemas.ObjectiveSchema;
import org.miradi.schemas.ProgressPercentSchema;
import org.miradi.schemas.ProgressReportSchema;
import org.miradi.schemas.ProjectResourceSchema;
import org.miradi.schemas.RareProjectDataSchema;
import org.miradi.schemas.ResourceAssignmentSchema;
import org.miradi.schemas.ScopeBoxSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.StressSchema;
import org.miradi.schemas.SubTargetSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.schemas.TextBoxSchema;
import org.miradi.schemas.ThreatReductionResultSchema;
import org.miradi.schemas.TncProjectDataSchema;
import org.miradi.schemas.ValueOptionSchema;
import org.miradi.schemas.WcsProjectDataSchema;
import org.miradi.schemas.WwfProjectDataSchema;
import org.miradi.utils.CodeList;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.PointList;
import org.miradi.utils.ProgressInterface;
import org.miradi.xml.AbstractXmlImporter;
import org.miradi.xml.MiradiXmlValidator;
import org.miradi.xml.wcs.Xmpz2XmlValidator;
import org.miradi.xml.xmpz.XmpzNameSpaceContext;
import org.miradi.xml.xmpz2.objectImporters.BaseObjectImporter;
import org.miradi.xml.xmpz2.objectImporters.ConceptualModelDiagramImporter;
import org.miradi.xml.xmpz2.objectImporters.DashboardImporter;
import org.miradi.xml.xmpz2.objectImporters.DiagramFactorImporter;
import org.miradi.xml.xmpz2.objectImporters.DiagramLinkImporter;
import org.miradi.xml.xmpz2.objectImporters.ExpenseAssignmentImporter;
import org.miradi.xml.xmpz2.objectImporters.GoalImporter;
import org.miradi.xml.xmpz2.objectImporters.IndicatorImporter;
import org.miradi.xml.xmpz2.objectImporters.ObjectiveImporter;
import org.miradi.xml.xmpz2.objectImporters.ResourceAssignmentImporter;
import org.miradi.xml.xmpz2.objectImporters.ResultsChainDiagramImporter;
import org.miradi.xml.xmpz2.objectImporters.SingletonObjectImporter;
import org.miradi.xml.xmpz2.objectImporters.StrategyImporter;
import org.miradi.xml.xmpz2.objectImporters.TaskImporter;
import org.miradi.xml.xmpz2.objectImporters.ThreatReductionResultsImporter;
import org.miradi.xml.xmpz2.objectImporters.ThreatTargetRatingImporter;
import org.miradi.xml.xmpz2.objectImporters.TncProjectDataImporter;
import org.miradi.xml.xmpz2.objectImporters.Xmpz2ExtraDataImporter;
import org.miradi.xml.xmpz2.objectImporters.Xmpz2ProjectLocationImporter;
import org.miradi.xml.xmpz2.objectImporters.Xmpz2ProjectPlanningImporter;
import org.miradi.xml.xmpz2.objectImporters.Xmpz2ProjectScopeImporter;
import org.miradi.xml.xmpz2.objectImporters.Xmpz2ProjectSummaryImporter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Xmpz2XmlImporter extends AbstractXmlImporter implements Xmpz2XmlConstants
{
	public Xmpz2XmlImporter(Project projectToFill, ProgressInterface progressIndicatorToUse) throws Exception
	{
		super(projectToFill);
		
		tagToElementNameMap = new Xmpz2TagToElementNameMap();
		progressIndicator = progressIndicatorToUse;
		progressIndicator.setStatusMessage(EAM.text("Importing XML..."), ObjectType.OBJECT_TYPE_COUNT);
	}
		
	@Override
	protected void importXml() throws Exception
	{
		LinkedHashMap<Integer, BaseObjectImporter> typeToImporterMap = fillTypeToImporterMap();
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
		
		importPools(typeToImporterMap);
		
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
		addImporterToMap(typeToImporterMap, new ThreatReductionResultsImporter(this));
		addImporterToMap(typeToImporterMap, new IndicatorImporter(this));
		addImporterToMap(typeToImporterMap, new ResourceAssignmentImporter(this));
		addImporterToMap(typeToImporterMap, new ExpenseAssignmentImporter(this));
		addImporterToMap(typeToImporterMap, new ObjectiveImporter(this));
		addImporterToMap(typeToImporterMap, new GoalImporter(this));
		addImporterToMap(typeToImporterMap, new TaskImporter(this));
		
		for(int objectType = ObjectType.FIRST_OBJECT_TYPE; objectType < ObjectType.OBJECT_TYPE_COUNT; ++objectType)
		{
			if (isCustomImport(objectType))
				continue;
			
			BaseObjectPool pool = (BaseObjectPool) getProject().getPool(objectType);
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
		Collection<BaseObjectImporter> importers = typeToImporterMap.values();
		for(BaseObjectImporter importer : importers)
		{
			importBaseObjects(importer);
			incrementProgress();
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
		final String elementObjectName = importer.getBaseObjectSchema().getXmpz2ElementName();
		final String containerElementName = elementObjectName + POOL_ELEMENT_TAG;
		final Node rootNode = getRootNode();
		final NodeList baseObjectNodes = getNodes(rootNode, new String[]{containerElementName, elementObjectName, });
		for (int index = 0; index < baseObjectNodes.getLength(); ++index)
		{
			Node baseObjectNode = baseObjectNodes.item(index);
			String intIdAsString = getAttributeValue(baseObjectNode, ID);
			ORef ref = getProject().createObject(importer.getBaseObjectSchema().getType(), new BaseId(intIdAsString));
			
			importer.importFields(baseObjectNode, ref);
			importer.postCreateFix(ref, baseObjectNode);
		}
	}
	
	private void importTncProjectData() throws Exception
	{
		final Node singletonNode = getNode(getRootNode(), new TncProjectDataSchema().getXmpz2ElementName());
		new TncProjectDataImporter(this).importFields(singletonNode, getSingletonObject(new TncProjectDataSchema().getType()));
	}
	
	private void importSingletonObject(BaseObjectSchema baseObjectSchema) throws Exception
	{
		final Node singletonNode = getNode(getRootNode(), baseObjectSchema.getXmpz2ElementName());
		new SingletonObjectImporter(this, baseObjectSchema).importFields(singletonNode, getSingletonObject(baseObjectSchema.getType()));
	}
	
	private ORef getSingletonObject(int objectType)
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
	
	private ORefList extractRefs(Node node, String poolName, AbstractFieldSchema fieldSchema) throws Exception
	{
		String elementName = findElementName(poolName, fieldSchema.getTag());
		String idElementName = convertIdsToIdString(elementName);
		return extractRefsFromNodes(node, poolName, elementName, idElementName);
	}
	
	public ORefList extractRefs(Node node, String poolName, String idsElementName, String idElementName) throws Exception
	{
		String elementName = findElementName(poolName, idsElementName);
		return extractRefsFromNodes(node, poolName, elementName,  idElementName + ID);
	}
	
	private ORefList extractRefsFromNodes(Node node, final String poolName, final String elementName, final String idElementName) throws Exception
	{
		final String idsContainerName = poolName + elementName;
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
		return removeLastChar(elementName);
	}

	private String removeLastChar(String elementName)
	{
		return elementName.substring(0, elementName.length() - 1);
	}

	public void importCodeListField(Node node, String elementContainerName, ORef destinationRef, String destinationTag) throws Exception
	{
		String elementName = findElementName(elementContainerName, destinationTag);
		String containerElementName = elementContainerName + elementName + CONTAINER_ELEMENT_TAG;
		CodeList codesToImport = getCodeList(node, containerElementName);
		
		setData(destinationRef, destinationTag, codesToImport.toString());
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
	
	public void importDateField(Node node, ORef destinationRefToUse, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		importStringField(node, baseObjectSchema.getXmpz2ElementName(), destinationRefToUse, fieldSchema.getTag());
	}

	public int getObjectTypeOfNode(Node typedIdNode)
	{
		String nodeName = typedIdNode.getNodeName();
		String objectTypeNameWithNamespace = removeAppendedId(nodeName);
		String objectTypeName = removeNamepsacePrefix(objectTypeNameWithNamespace);
		
		if (objectTypeName.equals(SCOPE_BOX))
			return ScopeBoxSchema.getObjectType();
		
		if (objectTypeName.equals(BIODIVERSITY_TARGET))
			return TargetSchema.getObjectType();
		
		if (objectTypeName.equals(HUMAN_WELFARE_TARGET))
			return HumanWelfareTargetSchema.getObjectType();
		
		if (objectTypeName.equals(CAUSE) || objectTypeName.equals(THREAT))
			return CauseSchema.getObjectType();
		
		if (objectTypeName.equals(STRATEGY))
			return StrategySchema.getObjectType();
		
		if (objectTypeName.equals(INTERMEDIATE_RESULTS))
			return IntermediateResultSchema.getObjectType();
		
		if (objectTypeName.equals(THREAT_REDUCTION_RESULTS))
			return ThreatReductionResultSchema.getObjectType();
		
		if (objectTypeName.equals(TEXT_BOX))
			return TextBoxSchema.getObjectType();
		
		if (objectTypeName.equals(GROUP_BOX))
			return GroupBoxSchema.getObjectType();
		
		if (isTask(objectTypeName))
			return TaskSchema.getObjectType();
		
		if (objectTypeName.equals(STRESS))
			return StressSchema.getObjectType();
		
		if (objectTypeName.equals(PROGRESS_PERCENT))
			return ProgressPercentSchema.getObjectType();
		
		if (objectTypeName.equals(BIODIVERSITY_TARGET))
			return TargetSchema.getObjectType();
		
		if (objectTypeName.equals(DIAGRAM_FACTOR))
			return DiagramFactorSchema.getObjectType();
		
		if (objectTypeName.equals(DIAGRAM_LINK))
			return DiagramLinkSchema.getObjectType();
		
		if (objectTypeName.equals(INDICATOR))
			return IndicatorSchema.getObjectType();
		
		if (objectTypeName.equals(RESOURCE_ASSIGNMENT))
			return ResourceAssignmentSchema.getObjectType();
		
		if (objectTypeName.equals(EXPENSE_ASSIGNMENT))
			return ExpenseAssignmentSchema.getObjectType();
		
		if (objectTypeName.equals(KEY_ECOLOGICAL_ATTRIBUTE))
			return KeyEcologicalAttributeSchema.getObjectType();
		
		if (objectTypeName.equals(MEASUREMENT))
			return MeasurementSchema.getObjectType();
		
		if (objectTypeName.equals(OBJECTIVE))
			return ObjectiveSchema.getObjectType();
		
		if (objectTypeName.equals(SUB_TASK))
			return TaskSchema.getObjectType();
		
		if (objectTypeName.equals(PROGRESS_REPORT))
			return ProgressReportSchema.getObjectType();
		
		if (objectTypeName.equals(SUB_TARGET))
			return SubTargetSchema.getObjectType();
		
		if (objectTypeName.equals(BUDGET_CATEGORY_ONE))
			return BudgetCategoryOneSchema.getObjectType();
		
		if (objectTypeName.equals(BUDGET_CATEGORY_TWO))
			return BudgetCategoryTwoSchema.getObjectType();
		
		if (objectTypeName.equals(ACCOUNTING_CODE))
			return AccountingCodeSchema.getObjectType();
		
		if (objectTypeName.equals(FUNDING_SOURCE))
			return FundingSourceSchema.getObjectType();
		
		if (objectTypeName.equals(RESOURCE))
			return ProjectResourceSchema.getObjectType();
		
		if (objectTypeName.equals(GOAL))
			return GoalSchema.getObjectType();
		
		EAM.logError("Could not find type for node: " + objectTypeName);
		return ObjectType.FAKE;
	}
	
	private String removeNamepsacePrefix(String objectTypeNameWithNamespace)
	{
		return objectTypeNameWithNamespace.replaceFirst(getPrefix(), "");
	}

	private static String removeAppendedId(String nodeName)
	{
		return nodeName.replaceFirst(ID, "");
	}
	
	public void importBooleanField(Node node, ORef destinationRefToUse,	BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node booleanNode = getNode(node, baseObjectSchema.getXmpz2ElementName() + fieldSchema.getTag());
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
	
	public void importDimensionField(Node node, ORef destinationRef, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node diagramFactorSizeNode = getNode(node, baseObjectSchema.getXmpz2ElementName() + SIZE);
		Node sizNode = getNode(diagramFactorSizeNode, DIAGRAM_SIZE_ELEMENT_NAME);
		Node widthNode = getNode(sizNode, WIDTH_ELEMENT_NAME);
		Node heightNode = getNode(sizNode, HEIGHT_ELEMENT_NAME);
		int width = extractNodeTextContentAsInt(widthNode);
		int height = extractNodeTextContentAsInt(heightNode);
		String dimensionAsString = EnhancedJsonObject.convertFromDimension(new Dimension(width, height));
		setData(destinationRef, DiagramFactor.TAG_SIZE, dimensionAsString);
	}
	
	public void importDiagramPointField(Node node, ORef destinationRef, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node locationNode = getNode(node, baseObjectSchema.getXmpz2ElementName() + LOCATION);
		Node pointNode = getNode(locationNode, DIAGRAM_POINT_ELEMENT_NAME);
		Point point = extractPointFromNode(pointNode);
		String pointAsString = EnhancedJsonObject.convertFromPoint(point);
		setData(destinationRef, DiagramFactor.TAG_LOCATION, pointAsString);
	}
	
	public void importRefField(Node node, ORef destinationRefToUse,	BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		importRefField(node, destinationRefToUse, baseObjectSchema.getXmpz2ElementName(), fieldSchema.getTag());
	}

	public void importRefField(Node node, ORef destinationRefToUse, final String xmpz2ElementName, final String tag) throws Exception
	{
		ORef refToImport = getRefToImport(node, xmpz2ElementName, tag);
		if (refToImport.isValid())
			setData(destinationRefToUse, tag, refToImport.toString());
	}
	
	public void importIdField(Node node, ORef destinationRefToUse, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		ORef refToImport = getRefToImport(node, baseObjectSchema.getXmpz2ElementName(), fieldSchema.getTag());
		setData(destinationRefToUse, fieldSchema.getTag(), refToImport.getObjectId().toString());
	}

	private ORef getRefToImport(Node node, String poolName, String idElementName) throws Exception
	{
		String elementName = findElementName(poolName, idElementName);
		String idParentElement = poolName + elementName;
		Node idParentNode = getNode(node, idParentElement);
		if (idParentNode == null)
			return ORef.INVALID;
		
		Node idNode = getNode(idParentNode, elementName);
		if (idNode == null)
			return ORef.INVALID;
		
		String trimmedIdAsString = getSafeNodeContent(idNode);
		
		return new ORef(getObjectTypeOfNode(idNode), new BaseId(trimmedIdAsString));
	}
	
	public void importPointListField(Node node, ORef destinationRef, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		Node bendPointsNode = getNode(node, baseObjectSchema.getXmpz2ElementName() + BEND_POINTS_ELEMENT_NAME);
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
		new ThreatTargetRatingImporter(this).importFields();
	}
	
	private void importDashboardData() throws Exception
	{
		new DashboardImporter(this).importFields();
	}

	private void importExtraData() throws Exception
	{
		new Xmpz2ExtraDataImporter(this).importFields();
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
	protected String getPrefix()
	{
		return PREFIX;
	}
	
	@Override
	protected NamespaceContext getNamespaceContext()
	{
		return new XmpzNameSpaceContext();
	}
	
	@Override
	protected MiradiXmlValidator createXmlValidator()
	{
		return new Xmpz2XmlValidator();
	}

	@Override
	protected String getNamespaceURI()
	{
		return getDocument().lookupNamespaceURI("miradi");
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
}
