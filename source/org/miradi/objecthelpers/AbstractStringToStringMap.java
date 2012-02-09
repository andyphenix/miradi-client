/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.objecthelpers;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.miradi.utils.EnhancedJsonObject;

abstract public class AbstractStringToStringMap
{
	public AbstractStringToStringMap()
	{
		this(new HashMap<String, String>());
	}

	public AbstractStringToStringMap(AbstractStringToStringMap copyFrom)
	{
		this(new HashMap<String, String>(copyFrom.data));
	}

	public AbstractStringToStringMap(EnhancedJsonObject json)
	{
		this();
		copyFromJson(json);
	}
	
	public AbstractStringToStringMap(String mapAsJsonString) throws ParseException
	{
		this(new EnhancedJsonObject(mapAsJsonString));
	}
	
	private AbstractStringToStringMap(Map<String, String> dataToUse)
	{
		data = new HashMap<String, String>(dataToUse);
	}
	
	protected void copyFromJson(EnhancedJsonObject json)
	{
		data.clear();
		EnhancedJsonObject array = json.optJson(getMapTag());
		if(array == null)
			array = new EnhancedJsonObject();
		Iterator iterator = array.keys();
		while (iterator.hasNext())
		{
			String key = (String)iterator.next();
			put(key, (String)array.get(key));
		}
	}
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		EnhancedJsonObject array = new EnhancedJsonObject();
		
		Iterator<String> iterator = data.keySet().iterator();
		while (iterator.hasNext())
		{
			String key = iterator.next();
			array.put(key, get(key));
		}
		json.put(getMapTag(), array);
		return json;
	}
	
	public String get(String code)
	{
		String value = data.get(code);
		if (value==null)
			return "";
	
		return value;
	}

	public int size()
	{
		return data.size();
	}

	public void rawPutForLegacyMigrationsAndTestCases(String code, String object)
	{
		data.put(code, object);
	}
	
	public void put(String code, String object)
	{
		rawPutForLegacyMigrationsAndTestCases(code, object);
	}

	@Override
	public int hashCode()
	{
		return data.hashCode();
	}
	
	@Override
	public String toString()
	{
		if(size() == 0)
			return "";

		return toJson().toString();
	}
	
	public HashMap<String, String> toHashMap()
	{
		return data;
	}
	
	public String get()
	{
		if(size() == 0)
			return "";
		return toJson().toString();
	}

	public void set(String newValue) throws Exception
	{
		copyFromJson(new EnhancedJsonObject(newValue));
	}

	public boolean contains(String code)
	{
		return data.containsKey(code);
	}
	
	public String find(String keyToFind)
	{
		Iterator<String> iterator = data.keySet().iterator();
		while (iterator.hasNext())
		{
			String key = iterator.next();
			if (keyToFind.equals(data.get(key)))
				return key;
		}
		return null;
	}

	public void removeCode(String code)
	{
		if(!data.containsKey(code))
			throw new RuntimeException(
					"Attempted to remove non-existant code: " + code
							+ " from: " + toString());
		data.remove(code);
	}
	
	abstract protected String getMapTag();
	
	protected HashMap<String, String> data;
}
