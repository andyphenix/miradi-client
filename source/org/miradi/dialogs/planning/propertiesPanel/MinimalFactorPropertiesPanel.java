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
package org.miradi.dialogs.planning.propertiesPanel;

import javax.swing.Icon;

import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.objects.Factor;
import org.miradi.project.Project;

public class MinimalFactorPropertiesPanel extends ObjectDataInputPanel
{
	public MinimalFactorPropertiesPanel(Project projectToUse, int objectType)
	{
		super(projectToUse, objectType);
	}
	
	protected void createAndAddFields(String translatedNameLabel, Icon icon)
	{
		ObjectDataInputField shortLabelField = createShortStringField(Factor.TAG_SHORT_LABEL);
		ObjectDataInputField labelField = createExpandableField(Factor.TAG_LABEL);
		addFieldsOnOneLine(translatedNameLabel, icon, new ObjectDataInputField[]{shortLabelField, labelField,});

		addField(createMultilineField(Factor.TAG_TEXT));
		addField(createMultilineField(Factor.TAG_COMMENT));
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Factor Properties");
	}

}
