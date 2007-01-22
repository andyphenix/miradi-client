/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objecthelpers;

public class CreateTaskParameter extends CreateObjectParameter
{
	public CreateTaskParameter(ORef parentRefToUse)
	{
		parentRef = parentRefToUse;
	}

	public ORef getParentRef()
	{
		return parentRef;
	}
	
	ORef parentRef;
}
