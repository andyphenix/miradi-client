package org.conservationmeasures.eam.actions;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;

public class ActionCreateKeyEcologicalAttributeMeasurement extends ObjectsAction
{
	public ActionCreateKeyEcologicalAttributeMeasurement(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Manage|Create Measurement");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Create a new Measurement");
	}
}
