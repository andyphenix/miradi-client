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

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.ScopeBox;
import org.miradi.schemas.ScopeBoxSchema;
import org.miradi.xml.generic.LegacyScopeBoxTypeQuestion;
import org.miradi.xml.wcs.Xmpz1XmlConstants;
import org.w3c.dom.Node;

public class ScopeBoxPoolImporter extends FactorPoolImporter
{
	public ScopeBoxPoolImporter(Xmpz1XmlImporter importerToUse)
	{
		super(importerToUse, Xmpz1XmlConstants.SCOPE_BOX, ScopeBoxSchema.getObjectType());
	}
	
	@Override
	protected void importFields(Node node, ORef destinationRef)	throws Exception
	{
		super.importFields(node, destinationRef);
		
		importCodeField(node, destinationRef, ScopeBox.TAG_SCOPE_BOX_TYPE_CODE, LegacyScopeBoxTypeQuestion.createScopeBoxTypeQuestion());
	}
}
