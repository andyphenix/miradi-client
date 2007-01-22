/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.GoalId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Goal;
import org.conservationmeasures.eam.project.Project;

public class GoalPropertiesPanel extends ObjectDataInputPanel
{
	public GoalPropertiesPanel(Project projectToUse, Actions actions) throws Exception
	{
		this(projectToUse, actions, new GoalId(BaseId.INVALID.asInt()));
	}
	
	public GoalPropertiesPanel(Project projectToUse, Actions actions, Goal goal) throws Exception
	{
		this(projectToUse, actions, (GoalId)goal.getId());
	}
	
	public GoalPropertiesPanel(Project projectToUse, Actions actions, GoalId idToShow) throws Exception
	{
		super(projectToUse, ObjectType.GOAL, idToShow);
		
		
		addField(createStringField(Goal.TAG_SHORT_LABEL));
		addField(createStringField(Goal.TAG_LABEL));
		addField(createMultilineField(Goal.TAG_FULL_TEXT));
		addField(createReadonlyTextField(Goal.PSEUDO_TAG_FACTOR));
		addField(createReadonlyTextField(Goal.PSEUDO_TAG_STRATEGIES));
		addField(createReadonlyTextField(Goal.PSEUDO_TAG_DIRECT_THREATS));
		
		updateFieldsFromProject();
	}

	public String getPanelDescription()
	{
		return EAM.text("Title|Goal Properties");
	}

}
