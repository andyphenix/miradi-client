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

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.xml.xmpz2.Xmpz2XmlUnicodeWriter;

public class BaseIdData extends ObjectData
{
	public BaseIdData(String tagToUse, int objectTypeToStore)
	{
		super(tagToUse);
		objectType = objectTypeToStore;
		id = BaseId.INVALID;
	}
	
	@Override
	public boolean isBaseIdData()
	{
		return true;
	}
	
	@Override
	public String get()
	{
		if(id.isInvalid())
			return "";
		return id.toString();
	}
	
	@Override
	public ORef getRef()
	{
		return new ORef(objectType, id);
	}
	
	@Override
	public ORefList getRefList()
	{
		ORefList list = new ORefList();
		list.add(getRef());
		return list;
	}
	
	public BaseId getId()
	{
		return id;
	}
	
	@Override
	public void set(String newValue) throws Exception
	{
		if(newValue.length() == 0)
			id = BaseId.INVALID;
		else
			id = new BaseId(Integer.parseInt(newValue));
	}
	
	public void setRef(ORef ref)
	{
		id = ref.getObjectId();
	}

	@Override
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof BaseIdData))
			return false;
		
		BaseIdData other = (BaseIdData)rawOther;
		return id.equals(other.id);
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
	
	@Override
	public void writeAsXmpz2XmlData(Xmpz2XmlUnicodeWriter writer, BaseObjectSchema schema, AbstractFieldSchema fieldSchema) throws Exception
	{
		writer.writeBaseIdData(schema, fieldSchema, get());
	}

	int objectType;
	BaseId id;
}
