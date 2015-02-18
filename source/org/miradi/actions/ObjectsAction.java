/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
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
package org.miradi.actions;

import java.util.Vector;

import javax.swing.Icon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.miradi.main.MainWindow;
import org.miradi.views.Doer;
import org.miradi.views.umbrella.ObjectPicker;

public class ObjectsAction extends ViewAction implements ListSelectionListener
{
	public ObjectsAction(MainWindow mainWindowToUse, String label)
	{
		super(mainWindowToUse, label);
		initialize();
	}
	
	public ObjectsAction(MainWindow mainWindowToUse, String label, String icon)
	{
		super(mainWindowToUse, label, icon);
		initialize();
	}
	
	public ObjectsAction(MainWindow mainWindowToUse, String label, Icon icon)
	{
		super(mainWindowToUse, label, icon);
		initialize();
	}
	
	private void initialize()
	{
		pickers = new Vector<ObjectPicker>();
	}

	@Override
	public boolean isObjectAction()
	{
		return true;
	}
	
	public void addPicker(ObjectPicker newPicker)
	{
		if(newPicker == null)
			throw new RuntimeException("Cannot add null picker");
		
		pickers.add(newPicker);
		newPicker.addSelectionChangeListener(this);
	}

	public void removePicker(ObjectPicker toRemove)
	{
		toRemove.removeSelectionChangeListener(this);
		pickers.remove(toRemove);
	}

	@Override
	public Doer getDoer()
	{
		Doer doer = super.getDoer();
		if(doer != null)
			doer.setPicker(getPicker());
		return doer;
	}

	public void valueChanged(ListSelectionEvent event)
	{
		updateEnabledState();
	}
	
	public ObjectPicker getPicker()
	{
		return getMostRecentlyAddedActivePicker();
	}

	private ObjectPicker getMostRecentlyAddedActivePicker()
	{
		ObjectPicker currentPicker = null;
		for(ObjectPicker picker : pickers)
		{
			if(picker.isActive())
				currentPicker = picker;
		}
		return currentPicker;
	}

	private Vector<ObjectPicker> pickers;

}
