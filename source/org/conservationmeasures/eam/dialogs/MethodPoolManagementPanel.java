/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import javax.swing.Icon;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.icons.MethodIcon;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.Project;

public class MethodPoolManagementPanel extends ObjectPoolManagementPanel
{
	public MethodPoolManagementPanel(Project projectToUse, Actions actions) throws Exception
	{
		super(new MethodPoolTablePanel(projectToUse),
				new TaskPropertiesPanel(projectToUse, actions, BaseId.INVALID));
	}
	
	public String getPanelDescription()
	{
		return EAM.text("Tab|Methods");
	}
	
	public Icon getIcon()
	{
		return new MethodIcon();
	}
}