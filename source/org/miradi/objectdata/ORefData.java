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

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.xml.xmpz2.Xmpz2XmlUnicodeWriter;

public class ORefData extends ObjectData
{
	public ORefData(String tagToUse)
	{
		super(tagToUse);
		ref = ORef.INVALID;
	}
	
	@Override
	public ORef getRef()
	{
		return ref;
	}
	
	@Override
	public ORefList getRefList()
	{
		return new ORefList(new ORef[] {ref});
	}
	
	public boolean isValid()
	{
		return !ref.equals(ORef.INVALID);
	}
	
	@Override
	public boolean isRefData()
	{
		return true;
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof ORefData))
			return false;
		
		ORefData other = (ORefData)rawOther;
		return ref.equals(other.ref);
	}

	public EnhancedJsonObject toJson()
	{
		return ref.toJson();
	}
	
	@Override
	public String get()
	{
		return ref.toString();
	}

	@Override
	public int hashCode()
	{
		return ref.hashCode();
	}
   
	public void set(ORef refToUse)
	{
		ref = refToUse;
	}
	 
	@Override
	public void set(String newValue) throws Exception
	{
		if(newValue.length() == 0)
			ref = ORef.INVALID;
		else
			ref = new ORef(new EnhancedJsonObject(newValue));
	}
	
	@Override
	public void writeAsXmpz2XmlData(Xmpz2XmlUnicodeWriter writer, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		writer.writeORefData(baseObjectSchema, fieldSchema, get());
	}

	ORef ref;
}
