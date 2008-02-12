/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.datanet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class RecordType
{
	static public class FieldAlreadyExistsException extends Exception
	{
		public FieldAlreadyExistsException(String fieldName)
		{
			super(fieldName);
		}
	}
	
	static public class UnknownFieldTypeException extends Exception
	{
		public UnknownFieldTypeException(String fieldType)
		{
			super(fieldType);
		}
	}
	
	public RecordType(String nameToUse)
	{
		name = nameToUse;
		fields = new HashMap<String, String>();
	}

	public String getName()
	{
		return name;
	}
	
	public void addField(String fieldName, String fieldType) throws Exception
	{
		if(fieldExists(fieldName))
			throw new FieldAlreadyExistsException(fieldName);
		if(!isLegalFieldType(fieldType))
			throw new UnknownFieldTypeException(fieldType);
		fields.put(fieldName, fieldType);
	}
	
	public int getFieldCount()
	{
		return fields.size();
	}
	
	public boolean fieldExists(String fieldName)
	{
		return fields.containsKey(fieldName);
	}
	
	public String getFieldType(String fieldName)
	{
		return fields.get(fieldName);
	}
	
	public static boolean isLegalFieldType(String fieldType)
	{
		return new Vector(Arrays.asList(LEGAL_FIELD_TYPES)).contains(fieldType);
	}
	
	public static final String STRING = "String";
	public static final String SHORT_STRING = "ShortString";
	public static final String LONG_STRING = "LongString";
	public static final String INTEGER = "Integer";
	public static final String FLOAT = "Float";
	public static final String DATE = "Date";
	
	public static final String[] LEGAL_FIELD_TYPES = {
		STRING,
		SHORT_STRING,
		LONG_STRING,
		INTEGER,
		FLOAT,
		DATE,
	};

	private String name;
	private Map<String, String> fields;
}
