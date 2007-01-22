/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.actions.ActionCreateActivity;
import org.conservationmeasures.eam.actions.ActionDeleteActivity;
import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.actions.MainWindowAction;
import org.conservationmeasures.eam.actions.ObjectsAction;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.Project;

public class ActivityListTablePanel extends ObjectListTablePanel
{
	public ActivityListTablePanel(Project projectToUse, Actions actions, FactorId nodeId)
	{
		super(projectToUse, ObjectType.TASK, 
				new ActivityListTableModel(projectToUse, nodeId), 
				(MainWindowAction)actions.get(ActionCreateActivity.class), 
				(ObjectsAction)actions.get(ActionDeleteActivity.class));
	}
}
