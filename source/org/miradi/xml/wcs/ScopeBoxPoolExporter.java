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

import org.martus.util.UnicodeWriter;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ScopeBox;
import org.miradi.schemas.ScopeBoxSchema;
import org.miradi.xml.generic.LegacyScopeBoxTypeQuestion;
import org.miradi.xml.generic.XmlSchemaCreator;

public class ScopeBoxPoolExporter extends FactorPoolExporter
{
	public ScopeBoxPoolExporter(XmpzXmlExporter wcsXmlExporterToUse)
	{
		super(wcsXmlExporterToUse, SCOPE_BOX, ScopeBoxSchema.getObjectType());
	}
	
	@Override
	protected void exportFields(UnicodeWriter writer, BaseObject baseObject) throws Exception
	{
		super.exportFields(writer, baseObject);
		
		writeCodeElement(XmlSchemaCreator.SCOPE_BOX_COLOR_ELEMENT_NAME, baseObject, ScopeBox.TAG_SCOPE_BOX_TYPE_CODE, LegacyScopeBoxTypeQuestion.createScopeBoxTypeQuestion());
	}
}
