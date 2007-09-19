/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning.legend;

import java.awt.event.ActionEvent;
import java.util.Vector;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.dialogs.planning.RowColumnProvider;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.questions.ChoiceItem;
import org.conservationmeasures.eam.utils.UiComboBoxWithSaneActionFiring;

abstract public class PlanningViewComboBox extends UiComboBoxWithSaneActionFiring implements RowColumnProvider
{
	public PlanningViewComboBox(Project projectToUse, ChoiceItem[] choices) throws Exception
	{
		super(choices);
		
		project = projectToUse;
		addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent event)
	{		
		try
		{
			saveState();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Error: " + e.getMessage());
		}
	}

	private void saveState() throws Exception
	{	
		Vector commands = new Vector();
		commands.addAll(getComboSaveCommnds());
		commands.addAll(getRadioSaveCommands());
		if (commands.size() == 0)
			return;
		
		project.executeCommand(new CommandBeginTransaction());
		try
		{
			getProject().executeCommands((Command[])commands.toArray(new Command[0]));
		}
		finally
		{
			project.executeCommand(new CommandEndTransaction());
		}
	}

	private Vector getComboSaveCommnds() throws Exception
	{
		if (! needsSave())
			return new Vector();
		
		ChoiceItem selectedItem = (ChoiceItem) getSelectedItem();
		String newValue = selectedItem.getCode();
		Vector comboSaveCommands = new Vector();
		ViewData viewData = getProject().getCurrentViewData();

		comboSaveCommands.add(new CommandSetObjectData(viewData.getRef(), getChoiceTag(), newValue));
		return comboSaveCommands;
	}
	
	private Vector getRadioSaveCommands() throws Exception
	{
		if (! needsSave())
			return new Vector();
		
		Vector radioSaveCommands = new Vector();
		ViewData viewData = getProject().getCurrentViewData();
		String existingStyleChoice = viewData.getData(ViewData.TAG_PLANNING_STYLE_CHOICE);
		if (existingStyleChoice.equals(getRadioChoicTag()))
			return new Vector();

		radioSaveCommands.add(new CommandSetObjectData(viewData.getRef(), ViewData.TAG_PLANNING_STYLE_CHOICE, getRadioChoicTag()));
		return radioSaveCommands;
	}
	
	protected Project getProject()
	{
		return project;
	}

	abstract public String getRadioChoicTag();
	abstract public String getChoiceTag();
	abstract boolean needsSave() throws Exception;
	
	private Project project;
	
}
