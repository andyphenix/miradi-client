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
package org.miradi.views.umbrella;

import java.util.EventObject;

import javax.swing.JComponent;

import org.miradi.exceptions.CommandFailedException;
import org.miradi.utils.HtmlViewPanelWithMargins;
import org.miradi.views.MainWindowDoer;

public class HelpButtonDoer extends MainWindowDoer
{
	public HelpButtonDoer()
	{
	}
	
	@Override
	public boolean isAvailable()
	{
		return true;
	}
	
	@Override
	public void doIt(EventObject event) throws Exception
	{
		try
		{
			JComponent sourceComponent = (JComponent)event.getSource();
			HelpButtonData data = (HelpButtonData)sourceComponent.getClientProperty(HelpButtonData.class);
			
			HtmlViewPanelWithMargins.createFromTextString(getMainWindow(), data.title, data.getHelpContents()).showAsOkDialog();
		}
		catch(Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
	
	@Override
	protected void doIt() throws Exception
	{
		throw new CommandFailedException("Invalid call; use DoIt(event)");
	}
	
}
