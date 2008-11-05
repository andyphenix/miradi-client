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
package org.miradi.dialogs.diagram;

import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.DiagramObject;
import org.miradi.project.Project;

//FIXME this needs to be renamed to ConceptualModelPropertiesPanel
public class ConceptualModelPropertiesPanel extends ObjectDataInputPanel
{
	public ConceptualModelPropertiesPanel(Project projectToUse, ORef diagramObjectRef)
	{
		super(projectToUse, diagramObjectRef);
		
		ObjectDataInputField shortLabelField = createShortStringField(DiagramObject.TAG_SHORT_LABEL);
		ObjectDataInputField labelField = createMediumStringField(DiagramObject.TAG_LABEL);
		addFieldsOnOneLine(EAM.text("Page:"), new ObjectDataInputField[]{shortLabelField, labelField});
	
		addField(createMultilineField(DiagramObject.TAG_DETAIL));

		updateFieldsFromProject();
	}

	public String getPanelDescription()
	{
		return EAM.text("Title|Diagram Properties");
	}
}
