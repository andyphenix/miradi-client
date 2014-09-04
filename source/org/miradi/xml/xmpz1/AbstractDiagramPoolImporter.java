/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.DiagramObject;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.TaggedObjectSetSchema;
import org.miradi.xml.wcs.Xmpz1XmlConstants;
import org.w3c.dom.Node;

abstract public class AbstractDiagramPoolImporter extends AbstractBaseObjectPoolImporter
{
	public AbstractDiagramPoolImporter(Xmpz1XmlImporter importerToUse, String poolNameToUse, int diagramTypeToImport)
	{
		super(importerToUse, poolNameToUse, diagramTypeToImport);
	}
	
	@Override
	protected void importFields(Node node, ORef destinationRef)	throws Exception
	{
		super.importFields(node, destinationRef);
		
		importField(node, destinationRef, ConceptualModelDiagram.TAG_SHORT_LABEL);
		importField(node, destinationRef, ConceptualModelDiagram.TAG_DETAIL);
		importField(node, destinationRef, ConceptualModelDiagram.TAG_ZOOM_SCALE);
		importCodeListField(node, destinationRef, DiagramObject.TAG_HIDDEN_TYPES);
		importIds(node, destinationRef, DiagramObject.TAG_DIAGRAM_FACTOR_IDS, DiagramFactorSchema.getObjectType(), Xmpz1XmlConstants.DIAGRAM_FACTOR);
		importIds(node, destinationRef, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, DiagramLinkSchema.getObjectType(), Xmpz1XmlConstants.DIAGRAM_LINK);		
		importRefs(node, Xmpz1XmlConstants.SELECTED_TAGGED_OBJECT_SET_IDS, destinationRef, DiagramObject.TAG_SELECTED_TAGGED_OBJECT_SET_REFS, TaggedObjectSetSchema.getObjectType(), Xmpz1XmlConstants.TAGGED_OBJECT_SET_ELEMENT_NAME);
	}
}
