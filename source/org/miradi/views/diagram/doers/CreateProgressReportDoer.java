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
package org.miradi.views.diagram.doers;

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Indicator;
import org.miradi.objects.Strategy;
import org.miradi.objects.Task;
import org.miradi.schemas.ProgressReportSchema;

public class CreateProgressReportDoer extends AbstractCreateProgressDoer
{
	@Override
	public boolean isAvailable()
	{
		if(!super.isAvailable())
			return false;
		
		final ORef actualSelectedRef = getSelectedRef();
		return canHaveProgressReports(actualSelectedRef);
	}

	private boolean canHaveProgressReports(ORef selectedRef)
	{
		if (Strategy.is(selectedRef))
			return true;
		
		if (Indicator.is(selectedRef))
			return true;
		
		return Task.is(selectedRef);
	}
	
	@Override
	public String getAnnotationListTag()
	{
		return BaseObject.TAG_PROGRESS_REPORT_REFS;
	}

	@Override
	public int getAnnotationType()
	{
		return ProgressReportSchema.getObjectType();
	}
}
