/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.resource;

import org.conservationmeasures.eam.actions.ActionCreateResource;
import org.conservationmeasures.eam.actions.ActionDeleteResource;
import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.dialogs.base.ObjectTablePanelWithCreateAndDelete;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.Project;

public class ResourcePoolTablePanel extends ObjectTablePanelWithCreateAndDelete
{
	public ResourcePoolTablePanel(Project project, Actions actions)
	{
		super(project, ObjectType.PROJECT_RESOURCE, 
				new ResourcePoolTable(new ResourcePoolTableModel(project)),
				actions, buttons);
	}
	
	static Class[] buttons = new Class[] {
		ActionCreateResource.class,
		ActionDeleteResource.class
	};
}
