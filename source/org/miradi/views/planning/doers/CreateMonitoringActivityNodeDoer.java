/* 
Copyright 2005-2022, Foundations of Success, Bethesda, Maryland
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

package org.miradi.views.planning.doers;

import org.miradi.objecthelpers.ORef;
import org.miradi.questions.WorkPlanVisibleRowsQuestion;
import org.miradi.schemas.TaskSchema;

public class CreateMonitoringActivityNodeDoer extends AbstractCreateActivityNodeDoer
{
	@Override
	public boolean isAvailable()
	{
		if (isWorkPlanView())
			if (getWorkPlanBudgetMode().equals(WorkPlanVisibleRowsQuestion.SHOW_ACTION_RELATED_ROWS_CODE))
				return false;

		return super.isAvailable();
	}

	@Override
	protected boolean isMonitoringActivity()
	{
		return true;
	}

	@Override
	protected String getChildRowTypeCode(ORef parentRef)
	{
		return TaskSchema.MONITORING_ACTIVITY_NAME;
	}
}
