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

import org.miradi.objects.Measurement;
import org.miradi.project.Project;
import org.miradi.schemas.FutureStatusSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.utils.CodeList;

public class IndicatorViabilityRowColumnProvider extends AbstractViabilityRowColumnProvider
{
	public IndicatorViabilityRowColumnProvider(Project projectToUse)
	{
		super(projectToUse);
	}

	public CodeList getRowCodesToShow() throws Exception
	{
		return new CodeList(new String[] {
				IndicatorSchema.OBJECT_NAME,
				MeasurementSchema.OBJECT_NAME,
				FutureStatusSchema.OBJECT_NAME,
		});
	}

	@Override
	public CodeList getColumnCodesToShow() throws Exception
	{
		return new CodeList(new String[] {
				 Measurement.TAG_SUMMARY,
				 Measurement.TAG_STATUS_CONFIDENCE,
		});
	}
}
