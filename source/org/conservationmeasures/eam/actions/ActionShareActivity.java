package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionShareActivity extends ObjectsAction
{
	public ActionShareActivity(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Share Activity");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Share an existing Activity into this Strategy");
	}
}
