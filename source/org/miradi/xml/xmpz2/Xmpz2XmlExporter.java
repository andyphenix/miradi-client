/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

import org.martus.util.UnicodeWriter;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Dashboard;
import org.miradi.objects.TableSettings;
import org.miradi.project.Project;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.xml.XmlExporter;
import org.miradi.xml.wcs.XmpzXmlConstants;

public class Xmpz2XmlExporter extends XmlExporter implements XmpzXmlConstants
{
	public Xmpz2XmlExporter(Project projectToExport, final Xmpz2XmlUnicodeWriter outToUse)
	{
		super(projectToExport);
		
		out = outToUse;
	}

	@Override
	public void exportProject(UnicodeWriter outToUse) throws Exception
	{
		getWriter().writeXmlHeader();
		getWriter().writeMainElementStart();
		exportPools();
		getWriter().writeMainElementEnd();
	}

	private void exportPools() throws Exception
	{
		for(int objectType = ObjectType.FIRST_OBJECT_TYPE; objectType < ObjectType.OBJECT_TYPE_COUNT; ++objectType)
		{
			EAMObjectPool pool = getBaseObjectPoolToExport(objectType);
			if(pool != null)
			{
				ORefList sortedRefList = pool.getSortedRefList();
				exportBaseObjects(sortedRefList);
			}
		}
	}

	private EAMObjectPool getBaseObjectPoolToExport(final int objectType)
	{
		if (objectType == ObjectType.RATING_CRITERION)
			return null;
		
		if (objectType ==  ObjectType.VALUE_OPTION)
			return null;
		
		if (TableSettings.is(objectType))
			return null;

		return getProject().getPool(objectType);
	}
	
	private void exportBaseObjects(ORefList sortedRefList) throws Exception
	{
		for(ORef ref : sortedRefList)
		{
			BaseObject baseObject = BaseObject.find(getProject(), ref);
			if (Dashboard.is(baseObject))
			{
				getWriter().writeDashboardSchemaElement((Dashboard)baseObject);
			}	
			else
			{
			writeBaseObjectDataSchemaElement(baseObject);
			}
		}
	}

	protected void writeBaseObjectDataSchemaElement(final BaseObject baseObject) throws Exception
	{
		BaseObjectSchema baseObjectSchema = baseObject.getSchema();
		getWriter().writeObjectElementStart(baseObjectSchema);
		for(AbstractFieldSchema fieldSchema : baseObjectSchema)
		{
			getWriter().writeFieldElement(baseObject, fieldSchema);
		}
		
		getWriter().writeObjectElementEnd(baseObjectSchema);
	}
	
	private Xmpz2XmlUnicodeWriter getWriter()
	{
		return out;
	}

	private Xmpz2XmlUnicodeWriter out;
}
