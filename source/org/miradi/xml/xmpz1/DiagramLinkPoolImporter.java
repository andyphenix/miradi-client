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

package org.miradi.xml.xmpz1;

import java.awt.Point;

import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramFactorId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.FactorLink;
import org.miradi.questions.DiagramLinkColorQuestion;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.FactorLinkSchema;
import org.miradi.utils.PointList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DiagramLinkPoolImporter extends AbstractBaseObjectPoolImporter
{
	public DiagramLinkPoolImporter(Xmpz1XmlImporter importerToUse)
	{
		super(importerToUse, DIAGRAM_LINK, DiagramLinkSchema.getObjectType());
	}
	
	@Override
	protected void importFields(Node node, ORef destinationRef) throws Exception
	{
		super.importFields(node, destinationRef);
		
		importRefs(node, GROUP_BOX_DIAGRAM_LINK_CHILDREN_ID, destinationRef, DiagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS, DiagramLinkSchema.getObjectType(), DIAGRAM_LINK);
		importCodeField(node, destinationRef, DiagramLink.TAG_COLOR, new DiagramLinkColorQuestion());
		DiagramLink diagramLink = DiagramLink.find(getProject(), destinationRef);
		if (!diagramLink.isGroupBoxLink())
			importBidirectionalCode(node, diagramLink);
		
		importBendPoints(node, destinationRef);
	}

	private void importBidirectionalCode(Node node, DiagramLink diagramLink) throws Exception
	{
		Node bidirectionalNode = getImporter().getNamedChildNode(node, getPoolName() + DiagramLink.TAG_IS_BIDIRECTIONAL_LINK);
		String bidirectionalNodeValue = bidirectionalNode.getTextContent();
		if (getImporter().isTrue(bidirectionalNodeValue))
			getImporter().setData(diagramLink, DiagramLink.TAG_IS_BIDIRECTIONAL_LINK, DiagramLink.BIDIRECTIONAL_LINK);
	}
	
	private void importBendPoints(Node node, ORef destinationRef) throws Exception
	{
		Node bendPointsNode = getImporter().getNamedChildNode(node, getPoolName() + BEND_POINTS_ELEMENT_NAME);
		NodeList bendPointNodes = getImporter().getNodes(bendPointsNode, new String[]{DIAGRAM_POINT_ELEMENT_NAME, });
		PointList bendPoints = new PointList();
		for (int index = 0; index < bendPointNodes.getLength(); ++index)
		{
			Node bendPointNode = bendPointNodes.item(index);
			Point bendPoint = extractPointFromNode(bendPointNode);
			bendPoints.add(bendPoint);
		}
		
		getImporter().setData(destinationRef, DiagramLink.TAG_BEND_POINTS, bendPoints.toString());
	}
	
	@Override
	protected void postCreateFix(ORef ref, Node node) throws Exception
	{
		Node fromDiagramFactorIdNode = getImporter().getNamedChildNode(node, getPoolName() + FROM_DIAGRAM_FACTOR_ID);
		Node toDiagramFactorIdNode = getImporter().getNamedChildNode(node, getPoolName() + TO_DIAGRAM_FACTOR_ID);
		
		DiagramFactorId fromId = new DiagramFactorId(fromDiagramFactorIdNode.getTextContent().trim());
		DiagramFactorId toId = new DiagramFactorId(toDiagramFactorIdNode.getTextContent().trim());
		
		ORef fromDiagramFactorRef = new ORef(DiagramFactorSchema.getObjectType(), fromId);
		ORef toDiagramFactorRef = new ORef(DiagramFactorSchema.getObjectType(), toId);
		DiagramFactor fromDiagramFactor = DiagramFactor.find(getProject(), fromDiagramFactorRef);
		DiagramFactor toDiagramFactor = DiagramFactor.find(getProject(), toDiagramFactorRef);	
		ORef factorLinkRef = new ORef(ObjectType.FAKE, BaseId.INVALID);
		if (!fromDiagramFactor.isGroupBoxFactor() && !toDiagramFactor.isGroupBoxFactor())
		{
			factorLinkRef = getProject().createObject(FactorLinkSchema.getObjectType());
			getProject().setObjectData(factorLinkRef, FactorLink.TAG_FROM_REF, fromDiagramFactor.getWrappedORef().toString());
			getProject().setObjectData(factorLinkRef, FactorLink.TAG_TO_REF, toDiagramFactor.getWrappedORef().toString());
		}
		
		getProject().setObjectData(ref, DiagramLink.TAG_WRAPPED_ID, factorLinkRef.getObjectId().toString());
		getProject().setObjectData(ref, DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID, fromId.toString());
		getProject().setObjectData(ref, DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID, toId.toString());
	}
}
