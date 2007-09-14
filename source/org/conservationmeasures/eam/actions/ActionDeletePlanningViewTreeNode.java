package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionDeletePlanningViewTreeNode extends ObjectsAction
{
	public ActionDeletePlanningViewTreeNode(MainWindow mainWindow)
	{
		super(mainWindow, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Tree|Delete Item");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Delete the selected item");
	}

}
