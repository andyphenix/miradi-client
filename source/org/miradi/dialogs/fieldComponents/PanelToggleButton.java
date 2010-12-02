/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.fieldComponents;

import java.awt.Insets;

import org.miradi.actions.MiradiAction;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;


public class PanelToggleButton extends UiToggleButton
{
	public PanelToggleButton(MiradiAction action)
	{
		super(action);
		setMargin(new Insets(2,2,2,2));
		setFont(getMainWindow().getUserDataPanelFont());
	}

	//TODO should not use static ref here
	public MainWindow getMainWindow()
	{
		return EAM.getMainWindow();
	}
}
