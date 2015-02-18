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
package org.miradi.objecthelpers;

import org.miradi.objects.Factor;

public class NonDraftStrategySet extends FactorSet
{
	public NonDraftStrategySet()
	{
		super();
	}
	
	public NonDraftStrategySet(FactorSet nodesToAttemptToAdd)
	{
		attemptToAddAll(nodesToAttemptToAdd);
	}
	
	public NonDraftStrategySet(Factor[] nodesToAttemptToAdd)
	{
		attemptToAddAll(nodesToAttemptToAdd);
	}

	@Override
	public boolean isLegal(Factor node)
	{
		return (node.isStrategy() && !node.isStatusDraft());
	}
}
