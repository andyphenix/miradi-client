/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.icons.TaskIcon;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionTreeCreateTask extends ObjectsAction
{
	public ActionTreeCreateTask(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel(), new TaskIcon());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Manage|Create Task");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Create a Task or Subtask for the selected Item");
	}
}
