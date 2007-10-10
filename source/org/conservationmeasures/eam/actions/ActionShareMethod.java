package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionShareMethod extends MainWindowAction
{
	public ActionShareMethod(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Share Method");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Share an existing Method into this Strategy");
	}
}
