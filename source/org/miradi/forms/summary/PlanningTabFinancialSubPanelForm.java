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
import org.miradi.main.EAM;
import org.miradi.objects.ProjectMetadata;
import org.miradi.schemas.ProjectMetadataSchema;

public class PlanningTabFinancialSubPanelForm extends FieldPanelSpec
{
	public PlanningTabFinancialSubPanelForm()
	{
		setTranslatedTitle(EAM.text("Financial"));

		int type = ProjectMetadataSchema.getObjectType();
		
		addLabelAndFieldsWithLabels(EAM.text("Label|Currency"), type, 
				new String[] {ProjectMetadata.TAG_CURRENCY_TYPE, ProjectMetadata.TAG_CURRENCY_SYMBOL});
		
		addLabelAndField(type, ProjectMetadata.TAG_CURRENCY_DECIMAL_PLACES);
		addCurrencyField(type, ProjectMetadata.TAG_TOTAL_BUDGET_FOR_FUNDING);
		addLabelAndField(type, ProjectMetadata.TAG_BUDGET_SECURED_PERCENT);
		addLabelAndField(type, ProjectMetadata.TAG_KEY_FUNDING_SOURCES);
		addLabelAndField(type, ProjectMetadata.TAG_FINANCIAL_COMMENTS);
		
	}
}
