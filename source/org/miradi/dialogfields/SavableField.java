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
package org.miradi.dialogfields;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import org.miradi.main.EAM;

public abstract class SavableField implements FocusListener
{	
	public SavableField()
	{
		fieldSaver = FieldSaver.getSingletonInstance();
	}
	
	public void focusGained(FocusEvent e)
	{
		EAM.logVerbose("focusGained");
		fieldSaver.setSavableField(this);
	}

	public void focusLost(FocusEvent e)
	{
		EAM.logVerbose("focusLost");
		saveIfNeeded();
		fieldSaver.setSavableField(null);
	}

	public static void saveFocusedFieldPendingEdits()
	{
		fieldSaver.savePendingEdits();
	}

	abstract public void saveIfNeeded();

	private static FieldSaver fieldSaver;
}
