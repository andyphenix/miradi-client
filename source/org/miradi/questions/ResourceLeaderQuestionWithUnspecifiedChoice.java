/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.questions;

import java.util.Vector;

import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.TimePeriodCostsMap;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;

public class ResourceLeaderQuestionWithUnspecifiedChoice extends ObjectQuestion
{
	public ResourceLeaderQuestionWithUnspecifiedChoice(Project projectToUse)
	{
		super(new BaseObject[0]);
		
		project = projectToUse;
		leaderReferrerRef = ORef.INVALID;
	}
	
	public void setObjectContainingLeaderRef(ORef refToUse)
	{
		leaderReferrerRef = refToUse;
	}
	
	@Override
	public ChoiceItem[] getChoices()
	{
		reloadObjects();
		return createChoiceItemListWithUnspecifiedItem(super.getChoices());
	}
	
	private void reloadObjects()
	{
		try
		{
			if (leaderReferrerRef.isValid())
			{			
				BaseObject baseObject = BaseObject.find(getProject(), leaderReferrerRef);
				TimePeriodCostsMap timePeriodCostsMap = baseObject.getTotalTimePeriodCostsWithoutRollup();
				ORefSet projectResourceRefs = timePeriodCostsMap.getAllProjectResourceRefs();
				setObjects(getBaseObjects(projectResourceRefs));
			}
			else
			{
				setObjects(new BaseObject[0]);
			}
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}		

	private BaseObject[] getBaseObjects(ORefSet projectResourceRefs)
	{
		Vector<BaseObject> baseObjects = new Vector<BaseObject>();
		for (ORef ref : projectResourceRefs)
		{
			//if (ref.isValid())
				baseObjects.add(BaseObject.find(getProject(), ref));
		}
		
		return baseObjects.toArray(new BaseObject[0]);
	}
	
	private Project getProject()
	{
		return project;
	}

	private Project project;
	private ORef leaderReferrerRef;
}
