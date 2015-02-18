/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.forms.summary;

import org.miradi.forms.FieldPanelSpec;
import org.miradi.forms.FormConstant;
import org.miradi.forms.FormFieldData;
import org.miradi.forms.FormFieldLabel;
import org.miradi.forms.FormRow;
import org.miradi.main.EAM;
import org.miradi.objects.ProjectMetadata;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.views.summary.BiodiversityPanel;

public class ScopeTabBiodiversitySubPanelForm extends FieldPanelSpec
{
	public ScopeTabBiodiversitySubPanelForm()
	{
		setTranslatedTitle(BiodiversityPanel.PANEL_DESCRIPTION);
		
		int type = ProjectMetadataSchema.getObjectType();
		
		FormRow areaRow = new FormRow();
		areaRow.addLeftFormItem(new FormConstant(EAM.text("Label|Biodiversity Area (hectares)")));
		areaRow.addRightFormItem(new FormFieldData(type, ProjectMetadata.TAG_PROJECT_AREA));
		areaRow.addRightFormItem(new FormFieldLabel(type, ProjectMetadata.TAG_PROJECT_AREA_NOTES));
		areaRow.addRightFormItem(new FormFieldData(type, ProjectMetadata.TAG_PROJECT_AREA_NOTES));
		addFormRow(areaRow);
	}
}
