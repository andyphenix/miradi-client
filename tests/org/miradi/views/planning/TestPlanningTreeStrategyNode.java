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
package org.miradi.views.planning;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.Task;

public class TestPlanningTreeStrategyNode extends TestPlanningTree
{
	public TestPlanningTreeStrategyNode(String name)
	{
		super(name);
	}
	
	public void testPlanningTreeStrategyNode() throws Exception
	{
		ORefList activityRefs = getStrategy().getActivityRefs();
		assertEquals("wrong activity count?", 1, activityRefs.size());
		assertTrue("wrong type returned?", isActivity(activityRefs.get(0)));
	}

	private boolean isActivity(ORef ref)
	{
		Task task = (Task) project.findObject(ref);
		return task.isActivity();
	}
}
