/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;

public class ActivityPoolTableModel extends ObjectPoolTableModel
{
	public ActivityPoolTableModel(Project projectToUse, ORef parentRefToUse)
	{
		super(projectToUse, ObjectType.TASK, COLUMN_TAGS);
		parentRef = parentRefToUse;
	}
	
	private static final String[] COLUMN_TAGS = new String[] {
		Task.TAG_LABEL,
		//FIXME include associated factor
	};
	
	public IdList getLatestIdListFromProject()
	{
		ORefList filteredTaskRefs = new ORefList();
		Strategy  parentStrategy = (Strategy) getProject().findObject(parentRef);
		ORefList parentActivityRefs = parentStrategy.getActivities();
		ORefList taskRefs = new ORefList(Task.getObjectType(), super.getLatestIdListFromProject());
		for (int i = 0; i < taskRefs.size(); ++i)
		{
			Task task = (Task) project.findObject(taskRefs.get(i));
			if (! task.isActivity())
				continue;
			
			if (isReferedToByDraftStrategyies(task))
				continue;
			
			if (parentActivityRefs.contains(task.getRef()))
				continue;
			
			filteredTaskRefs.add(taskRefs.get(i));
		}
				
		return filteredTaskRefs.convertToIdList(Task.getObjectType());
	}

	private boolean isReferedToByDraftStrategyies(Task task)
	{
		ORefList strategyReferrerRefs = task.findObjectsThatReferToUs(Strategy.getObjectType());
		for (int i = 0; i < strategyReferrerRefs.size(); ++i)
		{
			Strategy strategy = (Strategy) getProject().findObject(strategyReferrerRefs.get(i));
			if (strategy.isStatusDraft())
				return true;
		}
		
		return false;
	}
	
	private ORef parentRef;
}
