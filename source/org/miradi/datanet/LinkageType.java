/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.datanet;

public class LinkageType
{
	public LinkageType(String nameToUse, RecordType ownerTypeToUse, RecordType memberTypeToUse, String membershipToUse)
	{
		name = nameToUse;
		ownerType = ownerTypeToUse;
		memberType = memberTypeToUse;
		membershipType = membershipToUse;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getOwnerClassName()
	{
		return ownerType.getName();
	}

	public String getMemberClassName()
	{
		return memberType.getName();
	}
	
	public String getMembershipType()
	{
		return membershipType;
	}
	
	public static String CONTAINS = "Contains";
	
	private String name;
	private RecordType ownerType;
	private RecordType memberType;
	private String membershipType;
}
