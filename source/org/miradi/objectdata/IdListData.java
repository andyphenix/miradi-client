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

import java.text.ParseException;

import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.schemas.FieldSchemaIdList;
import org.miradi.xml.xmpz2.Xmpz2XmlImporter;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;
import org.miradi.xml.xmpz2.xmpz2schema.Xmpz2XmlSchemaCreator;
import org.w3c.dom.Node;

public class IdListData extends ObjectData
{
	public IdListData(String tagToUse, int objectTypeToStore)
	{
		super(tagToUse);
		ids = new IdList(objectTypeToStore);
	}
	
	@Override
	public void set(String newValue) throws ParseException
	{
		set(new IdList(ids.getObjectType(), newValue));
	}
	
	@Override
	public String get()
	{
		return ids.toString();
	}
	
	public void set(IdList newIds)
	{
		ids = newIds;
	}
	
	public IdList getIdList()
	{
		return ids;
	}
	
	@Override
	public ORefList getRefList()
	{
		return new ORefList(ids.getObjectType(), ids);
	}
	
	public int size()
	{
		return ids.size();
	}
	
	public BaseId get(int index)
	{
		return ids.get(index);
	}
	
	public void add(BaseId id)
	{
		ids.add(id);
	}
	
	public void insertAt(BaseId id, int index)
	{
		ids.insertAt(id, index);
	}
	
	public void removeId(BaseId id)
	{
		ids.removeId(id);
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof IdListData))
			return false;
		
		IdListData other = (IdListData)rawOther;
		return ids.equals(other.ids);
	}

	@Override
	public int hashCode()
	{
		return ids.hashCode();
	}
	
	@Override
	public boolean isIdListData()
	{
		return true;
	}
	
	@Override
	public void writeAsXmpz2XmlData(Xmpz2XmlWriter writer, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		writer.writeIdListData(baseObjectSchema, fieldSchema, getRefList());
	}
	
	@Override
	public void readAsXmpz2XmlData(Xmpz2XmlImporter importer, Node node, ORef destinationRefToUse, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		importer.importIds(node, destinationRefToUse, baseObjectSchema, fieldSchema, ((FieldSchemaIdList)fieldSchema).getIdListType());
	}
	
	@Override
	public void writeAsXmpz2SchemaElement(Xmpz2XmlSchemaCreator creator, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		creator.writeIdListSchemaElement(baseObjectSchema, fieldSchema);
	}
	
	private IdList ids;
}
