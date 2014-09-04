/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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

package org.miradi.views.planning.doers;

import org.miradi.dialogs.base.ModelessDialogWithClose;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.ViewData;
import org.miradi.views.ObjectsDoer;
import org.miradi.views.planning.PlanningCustomizePanel;
import org.miradi.views.planning.PlanningView;

public class PlanningCustomizeDialogPopupDoer extends ObjectsDoer
{
	@Override
	public boolean isAvailable()
	{
		if(!super.isAvailable())
			return false;

		return isPlanningView();
	}

	@Override
	protected void doIt() throws Exception
	{
		if(!isAvailable())
			return;
		
		showCustomizeDialog(getMainWindow());
	}

	public static void showCustomizeDialog(MainWindow mainWindowToUse) throws Exception
	{
		ORef currentViewDataRef = mainWindowToUse.getProject().getCurrentViewData().getRef();
		ViewData viewData = ViewData.find(mainWindowToUse.getProject(), currentViewDataRef);
		ORef planningConfigurationRef = viewData.getORef(ViewData.TAG_TREE_CONFIGURATION_REF);
		
		ModelessDialogWithClose dialog = new ModelessDialogWithClose(mainWindowToUse, EAM.text("Customize..."));
		ObjectDataInputPanel editor = new PlanningCustomizePanel(mainWindowToUse.getProject(), dialog, planningConfigurationRef);
		dialog.setScrollableMainPanel(editor);
		mainWindowToUse.getView(PlanningView.getViewName()).showFloatingPropertiesDialog(dialog);
	}
}
