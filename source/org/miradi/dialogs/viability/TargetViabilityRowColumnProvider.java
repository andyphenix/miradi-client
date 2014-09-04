/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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

package org.miradi.dialogs.viability;

import org.miradi.project.Project;
import org.miradi.schemas.FutureStatusSchema;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.utils.CodeList;

public class TargetViabilityRowColumnProvider extends AbstractViabilityRowColumnProvider
{
	public TargetViabilityRowColumnProvider(Project projectToUse)
	{
		super(projectToUse);
	}

	public CodeList getRowCodesToShow() throws Exception
	{
		return new CodeList(new String[] {
				ProjectMetadataSchema.OBJECT_NAME, 
				TargetSchema.OBJECT_NAME,
				HumanWelfareTargetSchema.OBJECT_NAME,
				KeyEcologicalAttributeSchema.OBJECT_NAME,
				IndicatorSchema.OBJECT_NAME,
				MeasurementSchema.OBJECT_NAME,
				FutureStatusSchema.OBJECT_NAME,
		});
	}
}
