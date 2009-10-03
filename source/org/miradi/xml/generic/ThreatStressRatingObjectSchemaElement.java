/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

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

import org.miradi.objects.ThreatStressRating;
	
public class ThreatStressRatingObjectSchemaElement extends BaseObjectSchemaElement
{
	public ThreatStressRatingObjectSchemaElement()
	{
		super("ThreatStressRating");
		
		createIdField(ThreatStressRating.TAG_THREAT_REF, XmlSchemaCreator.THREAT_ID_ELEMENT_NAME);
		createIdField(ThreatStressRating.TAG_STRESS_REF, XmlSchemaCreator.STRESS_ID_ELEMENT_NAME);
		createBooleanField(ThreatStressRating.TAG_IS_ACTIVE);
		createCodeField(ThreatStressRating.TAG_CONTRIBUTION, XmlSchemaCreator.VOCABULARY_THREAT_STRESS_RATING_CONTRIBUTION_CODE);
		createCodeField(ThreatStressRating.TAG_IRREVERSIBILITY, XmlSchemaCreator.VOCABULARY_THREAT_STRESS_RATING_IRREVERSIBILITY_CODE);
	}
}
