/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objecthelpers;

import java.util.HashMap;

public class CreateThreatStressRatingParameter extends CreateObjectParameter
{
	public CreateThreatStressRatingParameter(ORef stressRefToUse)
	{
		stressRef = stressRefToUse;
	}
	
	public ORef getStressRef()
	{
		return stressRef;
	}
	
	public String getFormatedDataString()
	{
		HashMap dataPairs = new HashMap();
		dataPairs.put("StressRef", stressRef);
		
		return formatDataString(dataPairs);
	}

	private ORef stressRef;
}
