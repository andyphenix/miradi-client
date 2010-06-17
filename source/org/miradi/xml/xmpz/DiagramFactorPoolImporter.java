/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml.xmpz;

import java.awt.Dimension;
import java.awt.Point;

import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.CreateDiagramFactorParameter;
import org.miradi.objecthelpers.CreateObjectParameter;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.GroupBox;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.IntermediateResult;
import org.miradi.objects.ScopeBox;
import org.miradi.objects.Strategy;
import org.miradi.objects.Stress;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.TextBox;
import org.miradi.objects.ThreatReductionResult;
import org.miradi.questions.DiagramFactorBackgroundQuestion;
import org.miradi.questions.DiagramFactorFontColorQuestion;
import org.miradi.questions.DiagramFactorFontSizeQuestion;
import org.miradi.questions.DiagramFactorFontStyleQuestion;
import org.miradi.questions.TextBoxZOrderQuestion;
import org.miradi.utils.EnhancedJsonObject;
import org.w3c.dom.Node;

public class DiagramFactorPoolImporter extends AbstractBaseObjectPoolImporter
{
	public DiagramFactorPoolImporter(XmpzXmlImporter importerToUse)
	{
		super(importerToUse, DIAGRAM_FACTOR, DiagramFactor.getObjectType());
	}
	
	@Override
	protected void importFields(Node node, ORef destinationRef)	throws Exception
	{
		super.importFields(node, destinationRef);
		
		importDiagramFactorLocation(node, destinationRef);
		importDiagramFactorSize(node, destinationRef);
		importRefs(node, GROUP_BOX_CHILDREN_IDS, destinationRef, DiagramFactor.TAG_GROUP_BOX_CHILDREN_REFS, DiagramFactor.getObjectType(), DIAGRAM_FACTOR);
		importCodeField(node, getPoolName(), destinationRef, DiagramFactor.TAG_TEXT_BOX_Z_ORDER_CODE, new TextBoxZOrderQuestion());
		importFontStylingElements(node, destinationRef);
	}
	
	private void importFontStylingElements(Node node, ORef destinationRef) throws Exception
	{
		Node diagramFactorSyleNode = getImporter().getNode(node, getPoolName() + STYLING);
		Node style = getImporter().getNode(diagramFactorSyleNode, STYLING);
		importCodeField(style, DIAGRAM_FACTOR, destinationRef, DiagramFactor.TAG_FONT_SIZE, new DiagramFactorFontSizeQuestion());
		importCodeField(style, DIAGRAM_FACTOR, destinationRef, DiagramFactor.TAG_FONT_STYLE, new DiagramFactorFontStyleQuestion());
		importCodeField(style, DIAGRAM_FACTOR, destinationRef, DiagramFactor.TAG_FOREGROUND_COLOR, new DiagramFactorFontColorQuestion());
		importCodeField(style, DIAGRAM_FACTOR, destinationRef, DiagramFactor.TAG_BACKGROUND_COLOR, new DiagramFactorBackgroundQuestion());
	}
	
	@Override
	protected CreateObjectParameter getExtraInfo(Node parentNode) throws Exception
	{
		ORef wrappedRef = importWrappedRef(parentNode);
		
		return new CreateDiagramFactorParameter(wrappedRef);
	}
	
	private ORef importWrappedRef(Node parentNode) throws Exception
	{
		Node wrappedFactorIdNode = getImporter().getNode(parentNode, getPoolName() + WRAPPED_FACTOR_ID_ELEMENT_NAME);
		Node wrappedByDiagamFactorIdNode = getImporter().getNode(wrappedFactorIdNode, WRAPPED_BY_DIAGRAM_FACTOR_ID_ELEMENT_NAME);
		Node typedIdNode = wrappedByDiagamFactorIdNode.getFirstChild();
		
		BaseId wrappedId = new BaseId(typedIdNode.getTextContent());
		
		return new ORef(getObjectTypeOfNode(typedIdNode), wrappedId);
	}

	private int getObjectTypeOfNode(Node typedIdNode)
	{
		String nodeName = typedIdNode.getNodeName();
		String objectTypeName = removeAppendedId(nodeName);
		
		if (objectTypeName.equals(SCOPE_BOX))
			return ScopeBox.getObjectType();
		
		if (objectTypeName.equals(BIODIVERSITY_TARGET))
			return Target.getObjectType();
		
		if (objectTypeName.equals(HUMAN_WELFARE_TARGET))
			return HumanWelfareTarget.getObjectType();
		
		if (objectTypeName.equals(CAUSE) || objectTypeName.equals(THREAT))
			return Cause.getObjectType();
		
		if (objectTypeName.equals(STRATEGY))
			return Strategy.getObjectType();
		
		if (objectTypeName.equals(INTERMEDIATE_RESULTS))
			return IntermediateResult.getObjectType();
		
		if (objectTypeName.equals(THREAT_REDUCTION_RESULTS))
			return ThreatReductionResult.getObjectType();
		
		if (objectTypeName.equals(TEXT_BOX))
			return TextBox.getObjectType();
		
		if (objectTypeName.equals(GROUP_BOX))
			return GroupBox.getObjectType();
		
		if (objectTypeName.equals(TASK))
			return Task.getObjectType();
		
		if (objectTypeName.equals(STRESS))
			return Stress.getObjectType();
		
		EAM.logError("Could not find type for node: " + objectTypeName);
		return ObjectType.FAKE;
	}

	private String removeAppendedId(String nodeName)
	{
		return nodeName.replaceFirst(ID, "");
	}

	private void importDiagramFactorSize(Node node, ORef destinationRef) throws Exception
	{
		Node diagramFactorSizeNode = getImporter().getNode(node, getPoolName()+ SIZE);
		Node sizNode = getImporter().getNode(diagramFactorSizeNode, DIAGRAM_SIZE_ELEMENT_NAME);
		Node widthNode = getImporter().getNode(sizNode, WIDTH_ELEMENT_NAME);
		Node heightNode = getImporter().getNode(sizNode, HEIGHT_ELEMENT_NAME);
		int width = extractNodeTextContentAsInt(widthNode);
		int height = extractNodeTextContentAsInt(heightNode);
		String dimensionAsString = EnhancedJsonObject.convertFromDimension(new Dimension(width, height));
		getImporter().setData(destinationRef, DiagramFactor.TAG_SIZE, dimensionAsString);
	}
	
	private void importDiagramFactorLocation(Node node, ORef destinationRef) throws Exception
	{
		Node locationNode = getImporter().getNode(node, getPoolName()+ LOCATION);
		Node pointNode = getImporter().getNode(locationNode, DIAGRAM_POINT_ELEMENT_NAME);
		Point point = extractPointFromNode(pointNode);
		String pointAsString = EnhancedJsonObject.convertFromPoint(point);
		getImporter().setData(destinationRef, DiagramFactor.TAG_LOCATION, pointAsString);
	}
}
