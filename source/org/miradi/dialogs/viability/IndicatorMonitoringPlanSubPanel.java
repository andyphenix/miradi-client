/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.dialogs.viability;

import org.miradi.actions.ActionEditMethods;
import org.miradi.actions.ObjectsAction;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Indicator;
import org.miradi.project.Project;
import org.miradi.questions.PriorityRatingQuestion;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.utils.ObjectsActionButton;

public class IndicatorMonitoringPlanSubPanel extends ObjectDataInputPanel
{
	public IndicatorMonitoringPlanSubPanel(Project projectToUse, ORef orefToUse) throws Exception
	{
		super(projectToUse, orefToUse);
		
		addField(createReadonlyTextField(IndicatorSchema.getObjectType(), Indicator.PSEUDO_TAG_FACTOR));
		
		ObjectsAction editMethods = getMainWindow().getActions().getObjectsAction(ActionEditMethods.class);
		editMethodsButton = new ObjectsActionButton(editMethods, getPicker());
		addFieldWithEditButton(EAM.text("Label|Methods"), createReadonlyTextField(IndicatorSchema.getObjectType(), Indicator.PSEUDO_TAG_METHODS), editMethodsButton);

		addField(createRatingChoiceField(IndicatorSchema.getObjectType(), Indicator.TAG_PRIORITY, new PriorityRatingQuestion()));
		
		updateFieldsFromProject();
	}
	
	@Override
	public void dispose()
	{
		editMethodsButton.dispose();
		super.dispose();
	}
	
	@Override
	protected boolean doesSectionContainFieldWithTag(String tag)
	{
		if (tag.equals(Indicator.PSEUDO_TAG_METHODS))
			return true;
		
		return super.doesSectionContainFieldWithTag(tag);
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Monitoring Plan");
	}
		
	private ObjectsActionButton editMethodsButton;
}
