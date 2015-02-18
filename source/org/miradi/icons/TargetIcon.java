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
package org.miradi.icons;

import java.awt.Color;

import javax.swing.Icon;

import org.miradi.diagram.renderers.EllipseRenderer;
import org.miradi.diagram.renderers.FactorRenderer;
import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;

public class TargetIcon extends AbstractShapeIcon
{
	@Override
	FactorRenderer getRenderer()
	{
		return new EllipseRenderer();
	}
	
	@Override
	Color getIconColor()
	{
		return EAM.getMainWindow().getColorPreference(AppPreferences.TAG_COLOR_TARGET);
	}
	
	static public Icon createDisabledIcon()
	{
		return new TargetIconDisabledIcon();
	}
	
	private static final class TargetIconDisabledIcon extends TargetIcon
	{
		@Override
		Color getIconColor()
		{
			return Color.LIGHT_GRAY;
		}
	}
}