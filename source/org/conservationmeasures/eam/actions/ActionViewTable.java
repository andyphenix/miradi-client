/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */

package org.conservationmeasures.eam.actions;

import java.awt.event.ActionEvent;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.views.umbrella.UmbrellaView;

public class ActionViewTable extends MainWindowAction 
{
	public ActionViewTable(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Table View");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Switch to the Table View");
	}
	
	public String toString()
	{
		return getLabel();
	}

	public void doAction(UmbrellaView view, ActionEvent event) throws CommandFailedException
	{
		view.getViewTable().doIt();
	}

	public boolean shouldBeEnabled(UmbrellaView view)
	{
		return view.getViewTable().isAvailable();
	}
}
