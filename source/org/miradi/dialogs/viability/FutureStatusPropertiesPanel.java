/* 
Copyright 2005-2013, Foundations of Success, Bethesda, Maryland 
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

import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.forms.objects.FutureStatusPropertiesForm;
import org.miradi.main.EAM;
import org.miradi.project.Project;
import org.miradi.schemas.FutureStatusSchema;

public class FutureStatusPropertiesPanel extends ObjectDataInputPanel
{
	public FutureStatusPropertiesPanel(Project projectToUse) throws Exception
	{
		super(projectToUse, FutureStatusSchema.getObjectType(), new FutureStatusPropertiesForm());
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Future Statuss");
	}
}
