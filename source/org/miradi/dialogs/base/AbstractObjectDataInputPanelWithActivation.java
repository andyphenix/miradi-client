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

package org.miradi.dialogs.base;

import org.miradi.objecthelpers.ORef;
import org.miradi.project.Project;


abstract public class AbstractObjectDataInputPanelWithActivation extends AbstractObjectDataInputPanel
{
	public AbstractObjectDataInputPanelWithActivation(Project projectToUse,	ORef refToUse)
	{
		this(projectToUse, new ORef[] {refToUse});
	}
	
	public AbstractObjectDataInputPanelWithActivation(Project projectToUse,	ORef[] orefsToUse)
	{
		super(projectToUse, orefsToUse);
	}
	
	@Override
	public void becomeActive()
	{
		super.becomeActive();
		for(AbstractObjectDataInputPanel panel : getSubPanels())
		{
			panel.becomeActive();
		}
		getPicker().becomeActive();
	}
	
	@Override
	public void becomeInactive()
	{
		getPicker().becomeInactive();
		for(AbstractObjectDataInputPanel panel : getSubPanels())
		{
			panel.becomeInactive();
		}
		
		super.becomeInactive();
	}
}
