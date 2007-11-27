/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objecthelpers;

import java.util.HashMap;

import org.conservationmeasures.eam.ids.FactorId;

public class CreateDiagramFactorParameter extends CreateObjectParameter
{
	public CreateDiagramFactorParameter(ORef factorRefToUse)
	{
		factorRef = factorRefToUse;
	}
	
	public FactorId getFactorId()
	{
		return new FactorId(factorRef.getObjectId().asInt());
	}

	public ORef getFactorRef()
	{
		return factorRef;
	}
	
	public String getFormatedDataString()
	{
		HashMap dataPairs = new HashMap();
		dataPairs.put(ORef.class.getSimpleName(), factorRef);
		
		return formatDataString(dataPairs);
	}
	
	private ORef factorRef;
}
