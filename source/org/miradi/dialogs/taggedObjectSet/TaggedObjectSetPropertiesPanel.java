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
package org.miradi.dialogs.taggedObjectSet;

import org.miradi.actions.ActionEditTaggedObjectSet;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.icons.TaggedObjectSetIcon;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objects.TaggedObjectSet;
import org.miradi.schemas.TaggedObjectSetSchema;
import org.miradi.views.umbrella.ObjectPicker;

public class TaggedObjectSetPropertiesPanel extends ObjectDataInputPanel
{
	public TaggedObjectSetPropertiesPanel(MainWindow mainWindowToUse, ObjectPicker picker) throws Exception
	{
		super(mainWindowToUse.getProject(), TaggedObjectSetSchema.getObjectType());
			
		ObjectDataInputField shortLabelField = createStringField(TaggedObjectSet.TAG_SHORT_LABEL, 10);
		ObjectDataInputField labelField = createExpandableField(TaggedObjectSet.TAG_LABEL);
		addFieldsOnOneLine(EAM.text("Tag"), new TaggedObjectSetIcon(), new ObjectDataInputField[]{shortLabelField, labelField,});
		
		addFieldWithEditButton(EAM.text("Tagged Items"), createReadOnlyObjectList(TaggedObjectSetSchema.getObjectType(), TaggedObjectSet.TAG_TAGGED_OBJECT_REFS), createObjectsActionButton(mainWindowToUse.getActions().getObjectsAction(ActionEditTaggedObjectSet.class), picker));
		
		addField(createMultilineField(TaggedObjectSet.TAG_COMMENTS));
		updateFieldsFromProject();
	}
	
	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Tag Properties Panel");
	}
}
