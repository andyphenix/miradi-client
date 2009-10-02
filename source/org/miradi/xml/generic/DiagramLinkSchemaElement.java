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

package org.miradi.xml.generic;

import org.miradi.objects.DiagramLink;

public class DiagramLinkSchemaElement extends BaseObjectSchemaElement
{
	public DiagramLinkSchemaElement()
	{
		super("DiagramLink");

		createWrappedByDiagramLinkIdField(DiagramLink.TAG_WRAPPED_ID);
		createLinkableFactorIdField(DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID);
		createLinkableFactorIdField(DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID);
		createDiagramPointListField(DiagramLink.TAG_BEND_POINTS);
		createIdListField(DiagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS, "DiagramLink");
		createCodeListField(XmlSchemaCreator.DIAGRAM_LINK_COLOR_ELEMENT_NAME);
	}
}
