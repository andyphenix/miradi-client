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

package org.miradi.dialogs.planning.upperPanel.rebuilder;

import org.miradi.objects.Cause;
import org.miradi.schemas.GroupBoxSchema;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.IntermediateResultSchema;
import org.miradi.schemas.ScopeBoxSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.StressSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.schemas.TextBoxSchema;
import org.miradi.schemas.ThreatReductionResultSchema;

public class FactorNoderSorter extends NodeSorter
{
	@Override
	protected String[] getNodeSortOrder()
	{
		return new String[] {
			ScopeBoxSchema.OBJECT_NAME,
			TargetSchema.OBJECT_NAME,
			HumanWelfareTargetSchema.OBJECT_NAME,
			StressSchema.OBJECT_NAME,
			Cause.OBJECT_NAME_THREAT,
			Cause.OBJECT_NAME_CONTRIBUTING_FACTOR,
			ThreatReductionResultSchema.OBJECT_NAME,
			IntermediateResultSchema.OBJECT_NAME,
			StrategySchema.OBJECT_NAME,
			TaskSchema.ACTIVITY_NAME,
			GroupBoxSchema.OBJECT_NAME,
			TextBoxSchema.OBJECT_NAME,
		};
	}
}
