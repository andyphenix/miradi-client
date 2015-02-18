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

package org.miradi.dialogs.planning.propertiesPanel;

import java.awt.Dimension;

import org.miradi.dialogs.base.EditableBaseObjectTable;
import org.miradi.dialogs.treetables.MultiTreeTablePanel.ScrollPaneWithHideableScrollBar;

public class ComponentTableScrollPane extends ScrollPaneWithHideableScrollBar
{
	public ComponentTableScrollPane(EditableBaseObjectTable contents)
	{
		super(contents);
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
		setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
		widthSetter = new PersistentWidthSetterComponent(contents.getMainWindow(), this, contents.getUniqueTableIdentifier(), getPreferredSize().width);
		widthSetter.setForeground(contents.getTableHeader().getForeground());
		widthSetter.setBackground(contents.getTableHeader().getBackground());
	}
	
	public PersistentWidthSetterComponent getWidthSetterComponent()
	{
		return widthSetter;
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		final Dimension size = super.getPreferredSize();
		if(widthSetter != null)
			size.width = widthSetter.getControlledWidth();
		return size;
	}
	
	@Override
	public Dimension getSize()
	{
		final Dimension size = super.getSize();
		if(widthSetter != null)
			size.width = widthSetter.getControlledWidth();
		return size;
	}
	
	private PersistentWidthSetterComponent widthSetter;
}