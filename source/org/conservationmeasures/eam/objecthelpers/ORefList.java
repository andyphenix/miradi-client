/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objecthelpers;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.utils.EnhancedJsonArray;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;
import org.json.JSONArray;
import org.martus.util.UnicodeWriter;

public class ORefList
{
	public ORefList()
	{
		this(new Vector());
	}
	
	public ORefList(ORefList copyFrom)
	{
		this(copyFrom.toJson());
	}
	
	public ORefList(String listAsJsonString) throws ParseException
	{
		this(new EnhancedJsonObject(listAsJsonString));
	}

	public ORefList(EnhancedJsonObject json)
	{
		this();
		EnhancedJsonArray array = json.optJsonArray(TAG_REFERENCES);
		for(int i = 0; i < array.length(); ++i)
			add(new ORef(array.getJson(i)));
	}
	
	public ORefList(ORef[] orefs)
	{
		this();
		for (int i=0; i<orefs.length; ++i)
			add(orefs[i]);
	}
	
	public ORefList(BaseObject[] baseObjects)
	{
		this();
		for (int i = 0; i < baseObjects.length; ++i)
		{
			add(baseObjects[i].getRef());
		}
	}
	
	public ORefList(int objectType, IdList idList)
	{
		this();
		for (int i=0; i<idList.size(); ++i)
			add(new ORef(objectType,idList.get(i)));
	}

	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		JSONArray array = new JSONArray();
		for (int i = 0; i < data.size(); i++)
		{
			array.put(get(i).toJson());
		}
		json.put(TAG_REFERENCES, array);
		return json;
	}
	
	public void add(ORef objectReferenceToUse)
	{
		data.add(objectReferenceToUse);
	}
	
	public void add(int index, ORef objectReferenceToUse)
	{
		data.add(index, objectReferenceToUse);
	}
	
	public void addAll(ORefList otherList)
	{
		for(int i = 0; i < otherList.size(); ++i)
			add(otherList.get(i));
	}
	
	public void addAll(ORef[] otherList)
	{
		addAll(new ORefList(otherList));
	}
	
	public void remove(ORef oRefToRemove)
	{
		data.remove(oRefToRemove);
	}
	
	public ORef get(int index)
	{
		return (ORef)data.get(index);
	}
		
	private ORefList(List listToUse)
	{
		data = new Vector(listToUse);
	}
		
	public ORefList extractByType(int objectTypeToFilterOn)
	{
		ORefList newList = new ORefList();
		for(int i = 0; i < data.size(); ++i)
		{
			if (get(i).getObjectType() == objectTypeToFilterOn)
				newList.add((ORef)data.get(i));
		}
		return newList;
	}
	
	public ORef[] toArray()
	{
		return (ORef[]) data.toArray(new ORef[0]);
	}
	
	public String toString()
	{
		if(size() == 0)
			return "";
		return toJson().toString();
	}
	
	public boolean equals(Object other)
	{	
		if (! (other instanceof ORefList))
			return false;
		
		return other.toString().equals(toString());	
	}
	
	public boolean contains(ORef objectRef)
	{
		return data.contains(objectRef);
	}
	
	public boolean containsAnyOf(ORefList otherList)
	{
		return getOverlappingRefs(otherList).size() > 0;
	}
	
	public ORefList getOverlappingRefs(ORefList otherList)
	{
		ORefList overlappingRefs = new ORefList();
		for (int i = 0; i < data.size(); ++i)
		{
			ORef thisRef = (ORef) data.get(i);
			if (otherList.contains(thisRef))
				overlappingRefs.add((ORef) data.get(i));
		}
		
		return overlappingRefs;		
	}
	
	public int find(ORef oref)
	{
	  for (int i=0; i<data.size(); ++i)
	  {
		  if (data.get(i).equals(oref))
			  return i;
	  }
	  return -1;
	}
	
	public int hashCode()
	{
		return data.hashCode();
	}
		
	public int size()
	{
		return data.size();
	}
	
	public boolean isEmpty()
	{
		return (size() == 0);
	}
	
	public void toXml(UnicodeWriter out) throws IOException
	{
		for(int i = 0; i < size(); ++i)
		{
			if(i > 0)
				out.write(",");
			get(i).toXml(out);
		}
	}
	
	public IdList convertToIdList(int objectType)
	{
		IdList convertedList = new IdList(objectType);
		for (int i = 0; i < data.size(); ++i)
		{
			ORef refToConvert = (ORef) data.get(i);
			if (refToConvert.getObjectType() != objectType)
				throw new RuntimeException("Found wrong type " + refToConvert.getObjectType() + " in ORefList thought to contain " + objectType);
			
			convertedList.add(refToConvert.getObjectId());
		}
		
		return convertedList;
	}

	private Vector data;
	private static final String TAG_REFERENCES = "References";
}
