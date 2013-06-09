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

import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.TncProjectData;
import org.miradi.xml.wcs.Xmpz1XmlConstants;

public class TncProjectDataSchemaElement extends ObjectSchemaElement
{
	public TncProjectDataSchemaElement()
	{
		super(Xmpz1XmlConstants.TNC_PROJECT_DATA);
		
		createOptionalTextField(ProjectMetadata.TAG_TNC_DATABASE_DOWNLOAD_DATE);
		createOptionalTextField(ProjectMetadata.TAG_OTHER_ORG_RELATED_PROJECTS);
		createOptionalTextField(ProjectMetadata.TAG_TNC_PLANNING_TEAM_COMMENTS);
		createOptionalTextField(TncProjectData.TAG_CON_PRO_PARENT_CHILD_PROJECT_TEXT);
		createOptionalTextField(ProjectMetadata.TAG_TNC_LESSONS_LEARNED);
		createOptionalTextField(TncProjectData.TAG_PROJECT_RESOURCES_SCORECARD);
		createOptionalTextField(TncProjectData.TAG_PROJECT_LEVEL_COMMENTS);
		createOptionalTextField(TncProjectData.TAG_PROJECT_CITATIONS);
		createOptionalTextField(TncProjectData.TAG_CAP_STANDARDS_SCORECARD);
		createOptionalCodeListField(XmlSchemaCreator.TNC_PROJECT_PLACE_TYPES);
		createOptionalCodeListField(XmlSchemaCreator.TNC_ORGANIZATIONAL_PRIORITIES);
		createOptionalCodeListField(XmlSchemaCreator.TNC_OPERATING_UNITS);
		createOptionalCodeListField(XmlSchemaCreator.TNC_TERRESTRIAL_ECO_REGION);
		createOptionalCodeListField(XmlSchemaCreator.TNC_MARINE_ECO_REGION);
		createOptionalCodeListField(XmlSchemaCreator.TNC_FRESHWATER_ECO_REGION);
	}
}
