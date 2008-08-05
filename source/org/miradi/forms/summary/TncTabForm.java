/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.forms.summary;

import org.miradi.forms.FieldPanelSpec;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.TncProjectData;



public class TncTabForm extends FieldPanelSpec 
{
	public TncTabForm()
	{
		int projectMetadataType = ProjectMetadata.getObjectType();
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_TNC_DATABASE_DOWNLOAD_DATE);
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_TNC_DATABASE_DOWNLOAD_DATE);
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_XENODATA_STRING_REF_MAP);
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_OTHER_ORG_RELATED_PROJECTS);
		
		addLabelAndField(TncProjectData.getObjectType(), TncProjectData.TAG_ORGANIZATIONAL_PRIORITY);
		
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_TNC_PLANNING_TEAM_COMMENT);
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_TNC_COUNTRY);
		addLabelAndField(projectMetadataType, ProjectMetadata.LEGACY_TAG_TNC_OPERATING_UNITS);
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_TNC_OPERATING_UNITS);
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_TNC_ECOREGION);
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_TNC_TERRESTRIAL_ECO_REGION);
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_TNC_MARINE_ECO_REGION);
		addLabelAndField(projectMetadataType, ProjectMetadata.TAG_TNC_FRESHWATER_ECO_REGION);
	}
}
