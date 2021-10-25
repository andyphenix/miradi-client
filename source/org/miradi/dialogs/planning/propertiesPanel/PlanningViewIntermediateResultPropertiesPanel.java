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
package org.miradi.dialogs.planning.propertiesPanel;

import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ObjectType;

public class PlanningViewIntermediateResultPropertiesPanel extends ResultFactorPropertiesPanel
{
	public PlanningViewIntermediateResultPropertiesPanel(MainWindow mainWindowToUse) throws Exception
	{
		super(mainWindowToUse, mainWindowToUse.getProject().getObjectManager().getSchemas().get(ObjectType.INTERMEDIATE_RESULT));
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Intermediate Result Properties");
	}

}
