/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objectdata.IntegerData;
import org.conservationmeasures.eam.objectdata.ORefData;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class CostAllocationRule extends BaseObject
{
	public CostAllocationRule(ObjectManager objectManager, BaseId id)
	{
		super(objectManager, id);
		clear();
	}
	
	public CostAllocationRule(ObjectManager objectManager, int idAsInt, EnhancedJsonObject jsonObject) throws Exception 
	{
		super(objectManager, new BaseId(idAsInt), jsonObject);
	}
	
	public CostAllocationRule(int idAsInt, EnhancedJsonObject jsonObject) throws Exception 
	{
		super(new BaseId(idAsInt), jsonObject);
	}
	
	public int getType()
	{
		return getObjectType();
	}

	public String getTypeName()
	{
		return OBJECT_NAME;
	}

	public static int getObjectType()
	{
		return ObjectType.COST_ALLOCATION_RULE;
	}
	
	public static boolean canOwnThisType(int type)
	{
		return false;
	}
	
	public static boolean canReferToThisType(int type)
	{
		return false;
	}
	
	void clear()
	{
		super.clear();
		
		parentRef = new ORefData();
		childRef = new ORefData();
		costPercentage = new IntegerData();
		
		addField(TAG_PARENT_REF, parentRef);
		addField(TAG_CHILD_REF, childRef);
		addField(TAG_COST_PERCENTAGE, costPercentage);
	}
	
	public static final String TAG_PARENT_REF = "ParentRef";
	public static final String TAG_CHILD_REF = "ChildRef";
	public static final String TAG_COST_PERCENTAGE = "CostPercentage"; 
	
	public static final String OBJECT_NAME = "CostAllocationRule";
	
	private ORefData parentRef;
	private ORefData childRef;
	private IntegerData costPercentage;
}
