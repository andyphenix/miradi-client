/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.dialogs.subTarget;

import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.icons.SubTargetIcon;
import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objects.SubTarget;
import org.miradi.project.Project;

public class SubTargetPropertiesPanel extends ObjectDataInputPanel
{
	public SubTargetPropertiesPanel(Project projectToUse) throws Exception
	{
		super(projectToUse, SubTarget.getObjectType(), BaseId.INVALID);
			
		ObjectDataInputField shortLabelField = createShortStringField(SubTarget.TAG_SHORT_LABEL);
		ObjectDataInputField labelField = createStringField(SubTarget.TAG_LABEL);
		addFieldsOnOneLine(EAM.text("Nested Target"), new SubTargetIcon(), new ObjectDataInputField[]{shortLabelField, labelField});
	
		addField(createMultilineField(SubTarget.TAG_DETAIL));
		
		updateFieldsFromProject();
	}
	
	public String getPanelDescription()
	{
		return EAM.text("Title|Nested Target Properties");
	}
}
