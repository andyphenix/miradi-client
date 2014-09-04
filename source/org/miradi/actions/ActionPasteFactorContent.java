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
package org.miradi.actions;

import java.awt.Point;

import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

public class ActionPasteFactorContent extends LocationAction
{
	public ActionPasteFactorContent(MainWindow mainWindow)
	{
		this(mainWindow, new Point(0,0));
	}
	
	public ActionPasteFactorContent(MainWindow mainWindow, Point startPointToUse)
	{
		super(mainWindow, getLabel(), "icons/pasteFactorContent.gif");
		setInvocationPoint(startPointToUse);	
	}

	private static String getLabel()
	{
		return EAM.text("Action|Paste Content");
	}

	@Override
	public String getToolTipText()
	{
		return EAM.text("TT|Paste the Single Factor's content from the clipboard");
	}
}
