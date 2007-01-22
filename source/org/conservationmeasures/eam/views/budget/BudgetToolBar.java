/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.budget;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.actions.views.ActionViewBudget;
import org.conservationmeasures.eam.main.EAMToolBar;

public class BudgetToolBar extends EAMToolBar
{
	public BudgetToolBar(Actions actions)
	{
		super(actions, ActionViewBudget.class);
	}
}
