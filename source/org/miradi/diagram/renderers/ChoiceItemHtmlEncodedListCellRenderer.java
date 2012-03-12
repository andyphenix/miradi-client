/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.diagram.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

import org.miradi.icons.RatingIcon;
import org.miradi.questions.ChoiceItem;

public class ChoiceItemHtmlEncodedListCellRenderer extends DefaultListCellRenderer
{
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
	{
		ChoiceItem thisOption = (ChoiceItem)value;
		thisOption.setLabel(thisOption.getHtmlLabel());
		
		Component cell = super.getListCellRendererComponent(list, thisOption, index, isSelected,	cellHasFocus);
		if (value!=null)
		{
			Icon icon = getOrCreateIcon(thisOption); 
			setIcon(icon);
		}
		return cell;
	}

	private Icon getOrCreateIcon(ChoiceItem thisOption)
	{
		Icon icon = thisOption.getIcon();
		if(icon != null)
			return icon;
		
		Color color = thisOption.getColor();
		if(color != null)
			return new RatingIcon(thisOption);
		
		return null;
	}
}
