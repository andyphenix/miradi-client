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
package org.miradi.views.planning.doers;

import org.miradi.main.EAM;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.FundingSource;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.BaseObject;
import org.miradi.views.umbrella.doers.DeletePoolObjectDoer;

public class DeleteFundingSourceDoer extends DeletePoolObjectDoer
{
	@Override
	protected String getCustomText()
	{
		return EAM.text("Funding Source");
	}
	
	@Override
	protected void doWork(BaseObject objectToDelete) throws Exception
	{
		removeResourceAssignmentReferenceToObject(objectToDelete, ResourceAssignment.TAG_FUNDING_SOURCE_ID);
		removeExpenseAssignmentReferenceToObject(objectToDelete, ExpenseAssignment.TAG_FUNDING_SOURCE_REF);
	}

	@Override
	protected boolean canDelete(BaseObject singleSelectedObject)
	{
		return FundingSource.is(singleSelectedObject);
	}
}

