/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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

import org.martus.util.UnicodeWriter;
import org.miradi.objecthelpers.ORefList;

public class ORefListData extends ObjectData
{
	public ORefListData(String tagToUse)
	{
		super(tagToUse);
		objectReferenceList = new ORefList();
	}
	
	public boolean equals(Object rawOther)
	{
		if (! (rawOther instanceof ORefListData))
			return false;
		
		return rawOther.toString().equals(toString());
	}

	public String get()
	{
		return objectReferenceList.toString();
	}

	public ORefList getORefList()
	{
		return objectReferenceList;
	}
	
	public ORefList getRefList()
	{
		return getORefList();
	}
	
	public ORefList getORefList(int objectTypeToFilterOn)
	{
		return objectReferenceList.filterByType(objectTypeToFilterOn);
	}
	
	public int hashCode()
	{
		return toString().hashCode();
	}

	public void set(String newValue) throws Exception
	{
		set(new ORefList(newValue));	
	}
	
	public void toXml(UnicodeWriter out) throws Exception
	{
		startTagToXml(out);
		objectReferenceList.toXml(out);
		endTagToXml(out);
	}

	private void set(ORefList objectReferenceToUse)
	{
		objectReferenceList = objectReferenceToUse;
	}
	
	public boolean isORefListData()
	{
		return true;
	}

	private ORefList objectReferenceList;
}
