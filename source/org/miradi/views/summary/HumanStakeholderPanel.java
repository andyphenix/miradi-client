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
package org.miradi.views.summary;

import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.ProjectMetadata;
import org.miradi.project.Project;

public class HumanStakeholderPanel extends ObjectDataInputPanel
{
	public HumanStakeholderPanel(Project projectToUse, ORef orefToUse) throws Exception
	{
		super(projectToUse, orefToUse);

		ObjectDataInputField humanPopulationField = createNumericField(ProjectMetadata.TAG_HUMAN_POPULATION);
		ObjectDataInputField humanPopulationNotesField = createMultilineField(ProjectMetadata.TAG_HUMAN_POPULATION_NOTES);
		addFieldsOnOneLine(EAM.text("Label|Human Stakeholder Pop Size"), new ObjectDataInputField[]{humanPopulationField, humanPopulationNotesField});
		
		addField(createMultilineField(ProjectMetadata.TAG_SOCIAL_CONTEXT));
		
	}

	@Override
	public String getPanelDescription()
	{
		return PANEL_DESCRIPTION; 
	}

	public static final String PANEL_DESCRIPTION = EAM.text("Human Stakeholders");
}
