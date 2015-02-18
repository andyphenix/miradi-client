/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 

package org.miradi.xml.generic;

import org.miradi.objects.RareProjectData;
import org.miradi.xml.wcs.Xmpz1XmlConstants;

public class RareProjectDataSchemaElement extends ObjectSchemaElement
{
	public RareProjectDataSchemaElement()
	{
		super(Xmpz1XmlConstants.RARE_PROJECT_DATA);
		
		createOptionalTextField(RareProjectData.TAG_COHORT);
		
		createOptionalTextField(RareProjectData.LEGACY_TAG_THREATS_ADDRESSED_NOTES);
		createOptionalTextField(RareProjectData.TAG_NUMBER_OF_COMMUNITIES_IN_CAMPAIGN_AREA);
		createOptionalTextField(RareProjectData.TAG_BIODIVERSITY_HOTSPOTS);
		createOptionalTextField(RareProjectData.TAG_FLAGSHIP_SPECIES_COMMON_NAME);
		createOptionalTextField(RareProjectData.TAG_FLAGSHIP_SPECIES_SCIENTIFIC_NAME);
		createOptionalTextField(RareProjectData.TAG_FLAGSHIP_SPECIES_DETAIL);
		
		createOptionalTextField(RareProjectData.TAG_CAMPAIGN_THEORY_OF_CHANGE);
		createOptionalTextField(RareProjectData.TAG_CAMPAIGN_SLOGAN);
		createOptionalTextField(RareProjectData.TAG_SUMMARY_OF_KEY_MESSAGES);
		createOptionalTextField(RareProjectData.TAG_MAIN_ACTIVITIES_NOTES);
	}
}
