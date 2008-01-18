/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.ids;

public class FactorId extends ObjectId
{

	public FactorId(int idToUse)
	{
		super(idToUse);
	}

	public static FactorId createFromBaseId(BaseId id)
	{
		return new FactorId(id.asInt());
	}
}
