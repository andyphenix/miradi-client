/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import java.awt.BorderLayout;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.dialogfields.BudgetTableEditorComponent;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.budget.BudgetPropertiesInputPanel;
import org.conservationmeasures.eam.views.budget.BudgetTreeTablePanel;

public class BudgetPropertiesPanel extends DisposablePanel
{
	public BudgetPropertiesPanel(Project projectToUse, Actions actions, BudgetTreeTablePanel treeTableComponent) throws Exception
	{
		this(projectToUse, actions, BaseId.INVALID, treeTableComponent);
	}
	
	public BudgetPropertiesPanel(Project projectToUse, Actions actions, BaseId idToShow, BudgetTreeTablePanel treeTableComponent) throws Exception
	{
		super(new BorderLayout());
		tableEditorComponent = new BudgetTableEditorComponent(projectToUse, actions, treeTableComponent);
		inputPanel = new BudgetPropertiesInputPanel(projectToUse, actions,idToShow,tableEditorComponent);
		
		add(inputPanel, BorderLayout.PAGE_START);
		add(tableEditorComponent, BorderLayout.CENTER);
	}
	
	public void dispose()
	{
		inputPanel.dispose();
		inputPanel = null;
		
		super.dispose();
	}

	public BudgetPropertiesInputPanel getInputPanel()
	{
		return inputPanel;
	}
	
	public String getPanelDescription()
	{
		return EAM.text("Text|Budget properties");
	}
		
	BudgetPropertiesInputPanel inputPanel;
	BudgetTableEditorComponent tableEditorComponent;
	
}
