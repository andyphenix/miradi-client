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
package org.miradi.views.diagram.doers;

import org.miradi.objects.ProgressReport;
import org.miradi.objects.Strategy;
import org.miradi.views.diagram.CreateAnnotationDoer;

public class CreateStrategyProgressReportDoer extends CreateAnnotationDoer
{
	public String getAnnotationListTag()
	{
		return Strategy.TAG_PROGRESS_REPORT_REFS;
	}

	public int getAnnotationType()
	{
		return ProgressReport.getObjectType();
	}
}
