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

package org.miradi.xml.wcs;

import org.martus.util.UnicodeWriter;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ProgressPercent;
import org.miradi.schemas.ProgressPercentSchema;

public class ProgressPercentPoolExporter extends BaseObjectPoolExporter
{
	public ProgressPercentPoolExporter(Xmpz1XmlExporter wcsXmlExporterToUse)
	{
		super(wcsXmlExporterToUse, PROGRESS_PERCENT, ProgressPercentSchema.getObjectType());
	}
	
	@Override
	protected void exportFields(UnicodeWriter writer, BaseObject baseObject) throws Exception
	{
		writeOptionalElementWithSameTag(baseObject, ProgressPercent.TAG_DATE);
		writeOptionalElementWithSameTag(baseObject, ProgressPercent.TAG_PERCENT_COMPLETE);
		writeOptionalElementWithSameTag(baseObject, ProgressPercent.TAG_PERCENT_COMPLETE_NOTES);
	}
}
