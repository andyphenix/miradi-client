/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.datanet;

public class RecordKey
{
	public RecordKey(String typeNameToUse, int idToUse)
	{
		typeName = typeNameToUse;
		id = idToUse;
	}
	
	@Override
	public String toString()
	{
		return typeName + ":" + id;
	}
	
	public String getRecordTypeName()
	{
		return typeName;
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if(! (rawOther instanceof RecordKey))
			return false;
		
		RecordKey other = (RecordKey)rawOther;
		if(id != other.id)
			return false;
		return (typeName.equals(other.typeName));
	}
	
	@Override
	public int hashCode()
	{
		return id;
	}
	
	private String typeName;
	private int id;
}
