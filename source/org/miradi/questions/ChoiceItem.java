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
package org.miradi.questions;

import java.awt.Color;
import java.util.Vector;

import javax.swing.Icon;

import org.miradi.dialogs.dashboard.AbstractLongDescriptionProvider;
import org.miradi.dialogs.dashboard.StaticLongDescriptionProvider;
import org.miradi.utils.XmlUtilities2;

public class ChoiceItem implements Comparable<Object>
{
	public ChoiceItem(int codeAsInt, String labelToUse, Icon iconToUse)
	{
		this(Integer.toString(codeAsInt), labelToUse, iconToUse);
	}
	
	public ChoiceItem(int codeAsInt, String labelToUse)
	{
		this(Integer.toString(codeAsInt), labelToUse);
	}
	
	public ChoiceItem(String codeToUse, String labelToUse)
	{
		this(codeToUse, labelToUse, (Color)null);
	}
	
	public ChoiceItem(String codeToUse, String labelToUse, String description)
	{
		this(codeToUse, labelToUse, (Color)null, description);
	}
	
	public ChoiceItem(String codeToUse, String labelToUse, boolean isSelectableToUse)
	{
		this(codeToUse, labelToUse);
		
		setSelectable(isSelectableToUse);
	}
	
	public ChoiceItem(String codeToUse, String labelToUse, Color colorToUse)
	{
		this(codeToUse, labelToUse, colorToUse, "");
	}
	
	public ChoiceItem(String codeToUse, String labelToUse, Icon iconToUse)
	{
		this(codeToUse, labelToUse, (Color)null, "");
		
		icon = iconToUse;
	}
	
	public ChoiceItem(String codeToUse, String labelToUse, Color colorToUse, String descriptionToUse)
	{
		this(codeToUse, labelToUse, colorToUse, new StaticLongDescriptionProvider(descriptionToUse));
	}
	
	public ChoiceItem(String codeToUse, String labelToUse, AbstractLongDescriptionProvider longDescriptionProviderToUse)
	{
		this(codeToUse, labelToUse, (Color)null, longDescriptionProviderToUse);
	}
	
	public ChoiceItem(String codeToUse, String labelToUse, Color colorToUse, AbstractLongDescriptionProvider descriptionProviderToUse)
	{
		code = codeToUse;
		label = labelToUse;
		color = colorToUse;
		descriptionProvider = descriptionProviderToUse;
		selectable = true;
		
		XmlUtilities2.ensureValidXmlLabel(label);
	}

	public String getCode()
	{
		return code;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public String getXmlLabel()
	{
		return XmlUtilities2.getXmlDecoded(getLabel());
	}
	
	public String getHtmlLabel()
	{
		return XmlUtilities2.convertXmlTextToHtml(getLabel());
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public Icon getIcon()
	{
		return icon;
	}
	
	public String getDescription()
	{
		return descriptionProvider.getDescription();
	}
	
	public void setLabel(final String labelToUse)
	{
		label = labelToUse;
	}
	
	public void setIcon(Icon iconToUse)
	{
		icon = iconToUse;
	}
	
	@Override
	public String toString()
	{
		//TODO urgent - uncomment and fix all cases that call toString();
		//EAM.logWarning("ChoiceItem.toString should not be called,  please use a specific label getter.")
		return getLabel();
	}
	
	public boolean isSelectable()
	{
		return selectable;
	}
	
	public void setSelectable(boolean selectableToUse)
	{
		selectable = selectableToUse;
	}
	
	public int getChildrenCount()
	{
		return 0;
	}
	
	public boolean hasChildren()
	{
		return !getChildren().isEmpty();
	}
	
	public Vector<ChoiceItem> getChildren()
	{
		return new Vector<ChoiceItem>();
	}
	
	public ChoiceItem getChild(int childIndex)
	{
		throw new RuntimeException("ChoiceItem does not support getChild");
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (! (other instanceof ChoiceItem))
			return false;
		
		ChoiceItem otherChoiceItem = (ChoiceItem) other;
		if (! otherChoiceItem.getCode().equals(getCode()))
			return false;
		
		if (! otherChoiceItem.getLabel().equalsIgnoreCase(getLabel()))
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return code.hashCode();
	}

	public int compareTo(Object rawObject)
	{
		String thisString = toString();
		String otherString = rawObject.toString();
		return thisString.compareToIgnoreCase(otherString);
	}
	
	public String getAdditionalText()
	{
		return null;
	}
	
	public AbstractLongDescriptionProvider getLongDescriptionProvider()
	{
		return descriptionProvider;
	}

	private boolean selectable;
	private String code;
	private String label;
	private Color color;
	private Icon icon;
	private AbstractLongDescriptionProvider descriptionProvider;
}
