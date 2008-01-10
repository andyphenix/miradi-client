/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import org.conservationmeasures.eam.objecthelpers.TncMarineEcoRegionFileLoader;
import org.conservationmeasures.eam.objecthelpers.TwoLevelFileLoader;

public class TncMarineEcoRegionQuestion extends TwoLevelQuestion
{
	public TncMarineEcoRegionQuestion(String tagToUse)
	{
		super(tagToUse, "Marine Eco Region", new TncMarineEcoRegionFileLoader(TwoLevelFileLoader.TNC_MARINE_ECO_REGION_FILE));
	}
}
