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

package org.miradi.xml.xmpz2.objectExporters;

import org.miradi.objects.BaseObject;
import org.miradi.objects.ObjectTreeTableConfiguration;
import org.miradi.questions.CustomPlanningAllRowsQuestion;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.schemas.ObjectTreeTableConfigurationSchema;
import org.miradi.xml.xmpz2.BaseObjectExporter;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;

public class ObjectTreeTableConfigurationExporter extends BaseObjectExporter
{
	public ObjectTreeTableConfigurationExporter(Xmpz2XmlWriter writerToUse)
	{
		super(writerToUse, ObjectTreeTableConfigurationSchema.getObjectType());
	}
	
	@Override
	protected boolean doesFieldRequireSpecialHandling(String tag)
	{
		if (tag.equals(ObjectTreeTableConfiguration.TAG_ROW_CONFIGURATION))
			return true;
		
		return super.doesFieldRequireSpecialHandling(tag);
	}
	
	@Override
	protected void writeFields(BaseObject baseObject, BaseObjectSchema baseObjectSchema) throws Exception
	{
		super.writeFields(baseObject, baseObjectSchema);
		
		final String selectedRowCodes = baseObject.getCodeList(ObjectTreeTableConfiguration.TAG_ROW_CONFIGURATION).toJsonString();
		final AbstractFieldSchema rowFieldSchema = baseObjectSchema.getFieldSchema(ObjectTreeTableConfiguration.TAG_ROW_CONFIGURATION);
		getWriter().writeCodeListData(baseObjectSchema, rowFieldSchema, new CustomPlanningAllRowsQuestion(), selectedRowCodes);
	}
}
