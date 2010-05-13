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
package org.miradi.views.umbrella;

import org.miradi.dialogs.base.PreferencesDialog;
import org.miradi.dialogs.base.PreferencesPanel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.views.ViewDoer;

public class PreferencesPopupDoer extends ViewDoer
{
	@Override
	public boolean isAvailable()
	{
		return true;
	}

	@Override
	public void doIt() throws Exception
	{		
		try
		{
			showPreferencesDialog();
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
	
	private void showPreferencesDialog() throws Exception
	{
		PreferencesPanel preferencesPanel = new PreferencesPanel(getMainWindow());
		PreferencesDialog dlg = new PreferencesDialog(getMainWindow(), preferencesPanel, "Miradi Preferences");
		getView().showFloatingPropertiesDialog(dlg);
	}
}
