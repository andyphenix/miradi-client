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
package org.miradi.forms.objects;

import org.miradi.forms.FieldPanelSpec;
import org.miradi.objects.Indicator;
import org.miradi.questions.ProgressReportShortStatusQuestion;
import org.miradi.schemas.IndicatorSchema;

public class IndicatorMonitoringPlanSubForm extends FieldPanelSpec
{
	public IndicatorMonitoringPlanSubForm()
	{
		int type = IndicatorSchema.getObjectType();
		addLabelAndField(type, Indicator.PSEUDO_TAG_FACTOR);
		addLabelAndField(type, Indicator.PSEUDO_TAG_METHODS);
		addLabelAndField(type, Indicator.TAG_PRIORITY);
		addChoiceField(type, Indicator.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE, new ProgressReportShortStatusQuestion());
	}
}
