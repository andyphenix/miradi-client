/*
Copyright 2005-2021, Foundations of Success, Bethesda, Maryland
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
package org.miradi.forms.objects;

import org.miradi.forms.FieldPanelSpec;
import org.miradi.main.EAM;
import org.miradi.objects.Assumption;
import org.miradi.schemas.AssumptionSchema;

public class AssumptionPropertiesForm extends FieldPanelSpec
{
    public AssumptionPropertiesForm()
    {
        int type = AssumptionSchema.getObjectType();
        addLabelAndFieldsWithLabels(EAM.text("Assumption"), type, new String[]{Assumption.TAG_SHORT_LABEL, Assumption.TAG_LABEL});
        addLabelAndField(type, Assumption.TAG_TEXT);
        addLabelAndField(type, Assumption.PSEUDO_TAG_RELEVANT_INDICATOR_REFS);
        addLabelAndField(type, Assumption.TAG_COMMENTS);
        addLabelAndField(type, Assumption.TAG_IMPLICATIONS);
        addLabelAndField(type, Assumption.TAG_FUTURE_INFORMATION_NEEDS);
        addMultipleTaxonomyWithEditButtonFields(AssumptionSchema.getObjectType(), Assumption.TAG_TAXONOMY_CLASSIFICATION_CONTAINER);
    }
}
