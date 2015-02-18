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
package org.miradi.forms.objects;

import org.miradi.forms.FieldPanelSpec;
import org.miradi.icons.KeyEcologicalAttributeIcon;
import org.miradi.main.EAM;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.schemas.KeyEcologicalAttributeSchema;

public class KeyEcologicalAttributePropertiesForm extends FieldPanelSpec
{
	public KeyEcologicalAttributePropertiesForm()
	{
		int type = KeyEcologicalAttributeSchema.getObjectType();
		addStandardNameRow(new KeyEcologicalAttributeIcon(), EAM.text("KEA"), type, new String[]{KeyEcologicalAttribute.TAG_SHORT_LABEL, KeyEcologicalAttribute.TAG_LABEL});
		
		addLabelAndField(type, KeyEcologicalAttribute.TAG_DETAILS);
		addLabelAndField(type, KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE);
		addLabelAndField(type, KeyEcologicalAttribute.TAG_DESCRIPTION);
		addMultipleTaxonomyWithEditButtonFields(type, KeyEcologicalAttribute.TAG_TAXONOMY_CLASSIFICATION_CONTAINER);
	}
}
