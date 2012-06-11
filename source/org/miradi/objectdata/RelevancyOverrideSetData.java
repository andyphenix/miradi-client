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
package org.miradi.objectdata;

import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.RelevancyOverride;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.xml.xmpz2.xmpz2schema.Xmpz2XmlSchemaCreator;

public class RelevancyOverrideSetData extends ObjectData
{
	public RelevancyOverrideSetData(String tagToUse)
	{
		super(tagToUse);
		relevancyOverrideSet = new RelevancyOverrideSet();
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if (! (rawOther instanceof RelevancyOverrideSetData))
			return false;
		
		return rawOther.toString().equals(toString());
	}

	public RelevancyOverrideSet getRawRelevancyOverrideSet()
	{
		return new RelevancyOverrideSet(relevancyOverrideSet);
	}
	
	public ORefSet extractRelevantRefs()
	{
		RelevancyOverrideSet rawRelevancyOverrideSet = getRawRelevancyOverrideSet();
		ORefSet rawRelevantOverrideList = new ORefSet();
		RelevancyOverrideSet relevantOverrides = rawRelevancyOverrideSet;
		for(RelevancyOverride relevancyOverride : relevantOverrides)
		{
			rawRelevantOverrideList.add(relevancyOverride.getRef());
		}
		
		return rawRelevantOverrideList;
	}
	
	@Override
	public String get()
	{
		return relevancyOverrideSet.toString();
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	@Override
	public void set(String newValue) throws Exception
	{
		set(new RelevancyOverrideSet(newValue));	
	}
	
	private void set(RelevancyOverrideSet relevancyOverrideSetToUse)
	{
		relevancyOverrideSet = relevancyOverrideSetToUse;
	}
	 
	@Override
	public String writeAsXmpz2SchemaElement(Xmpz2XmlSchemaCreator creator, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		return creator.writeRelevantSchemaElement(baseObjectSchema, fieldSchema);
	}
	
	private RelevancyOverrideSet relevancyOverrideSet;
}
