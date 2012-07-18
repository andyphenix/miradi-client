/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml.xmpz2.xmpz2schema;

import org.miradi.objects.ProjectMetadata;
import org.miradi.schemas.ProjectMetadataSchema;

public class ProjectSummaryLocationSchema extends AbstractProjectSummarySchema
{
	@Override
	protected void fillFieldSchemas()
	{
		ProjectMetadataSchema schema = new ProjectMetadataSchema();
		addFieldSchema(schema.getFieldSchema(ProjectMetadata.TAG_COUNTRIES));
		addFieldSchema(schema.getFieldSchema(ProjectMetadata.TAG_STATE_AND_PROVINCES));
		addFieldSchema(schema.getFieldSchema(ProjectMetadata.TAG_MUNICIPALITIES));
		addFieldSchema(schema.getFieldSchema(ProjectMetadata.TAG_LEGISLATIVE_DISTRICTS));
		addFieldSchema(schema.getFieldSchema(ProjectMetadata.TAG_LOCATION_DETAIL));
		addFieldSchema(schema.getFieldSchema(ProjectMetadata.TAG_SITE_MAP_REFERENCE));
		addFieldSchema(schema.getFieldSchema(ProjectMetadata.TAG_LOCATION_COMMENTS));
	}
	
	@Override
	public String getObjectName()
	{
		return PROJECT_SUMMARY_LOCATION;
	}
}
