/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import java.text.ParseException;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.ids.IndicatorId;
import org.conservationmeasures.eam.objectdata.IdListData;
import org.conservationmeasures.eam.objectdata.StringData;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class Indicator extends EAMBaseObject
{
	public Indicator(IndicatorId idToUse)
	{
		super(idToUse);
		shortLabel = new StringData();
		method = new StringData();
		resourceIds = new IdListData();
	}
	
	public Indicator(int idAsInt, EnhancedJsonObject json) throws ParseException
	{
		super(new BaseId(idAsInt), json);
		shortLabel = new StringData(json.optString(TAG_SHORT_LABEL));
		method = new StringData(json.optString(TAG_METHOD));
		resourceIds = new IdListData(json.optString(TAG_RESOURCE_IDS));
	}
	
	public int getType()
	{
		return ObjectType.INDICATOR;
	}

	public String getData(String fieldTag)
	{
		if(fieldTag.equals(TAG_SHORT_LABEL))
			return getShortLabel();
		if(fieldTag.equals(TAG_METHOD))
			return method.get();
		if(fieldTag.equals(TAG_RESOURCE_IDS))
			return resourceIds.get();
		
		return super.getData(fieldTag);
	}

	public void setData(String fieldTag, String dataValue) throws Exception
	{
		if(fieldTag.equals(TAG_SHORT_LABEL))
			shortLabel.set(dataValue);
		else if(fieldTag.equals(TAG_METHOD))
			method.set(dataValue);
		else if(fieldTag.equals(TAG_RESOURCE_IDS))
			resourceIds.set(dataValue);
		else
			super.setData(fieldTag, dataValue);
	}
	
	public String getShortLabel()
	{
		return shortLabel.get();
	}
	
	public IdList getResourceIdList()
	{
		return resourceIds.getIdList();
	}

	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = super.toJson();
		json.put(TAG_SHORT_LABEL, getShortLabel());
		json.put(TAG_METHOD, method.get());
		json.put(TAG_RESOURCE_IDS, resourceIds.get());
		
		return json;
	}
	
	public String toString()
	{
		if(getId().isInvalid())
			return "(None)";
		return shortLabel + ": " + getLabel();
	}

	public static final String TAG_SHORT_LABEL = "ShortLabel";
	public static final String TAG_METHOD = "Method";
	public static final String TAG_RESOURCE_IDS = "ResourceIds";

	StringData shortLabel;
	StringData method;
	IdListData resourceIds;
}
