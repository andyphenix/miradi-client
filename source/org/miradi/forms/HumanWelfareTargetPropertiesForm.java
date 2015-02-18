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

package org.miradi.forms;

import org.miradi.forms.objects.AbstractTargetPropertiesForm;
import org.miradi.icons.AbstractMiradiIcon;
import org.miradi.icons.HumanWelfareTargetIcon;
import org.miradi.main.EAM;
import org.miradi.schemas.HumanWelfareTargetSchema;

public class HumanWelfareTargetPropertiesForm extends AbstractTargetPropertiesForm
{
	@Override
	protected String getTargetLabel()
	{
		return EAM.text("Human Wellbeing Target");
	}
	
	@Override
	protected AbstractMiradiIcon createTargetIcon()
	{
		return new HumanWelfareTargetIcon();
	}
	
	@Override
	protected int getTargetType()
	{
		return HumanWelfareTargetSchema.getObjectType();
	}

	@Override
	protected  void addCustomFields()
	{
	}
}
