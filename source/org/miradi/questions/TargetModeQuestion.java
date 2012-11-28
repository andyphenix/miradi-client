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

import org.miradi.icons.HumanWelfareTargetIcon;
import org.miradi.icons.TargetIcon;
import org.miradi.main.EAM;

public class TargetModeQuestion extends StaticChoiceQuestion
{
	public TargetModeQuestion()
	{
		super(getChoiceItems());
	}

	public static ChoiceItem[] getChoiceItems()
	{
		return new ChoiceItem[] {
				new ChoiceItem(DEFAULT_CODE, EAM.text("Default Mode"), new TargetIcon()),
				new ChoiceItem(HUMAN_WELFARE_TARGET_CODE, EAM.text("Human Wellbeing Target Mode"), new HumanWelfareTargetIcon()),
		};
	}
	
	public static final String DEFAULT_CODE = "";
	public static final String HUMAN_WELFARE_TARGET_CODE = "HumanWelfareTargetMode";
}
