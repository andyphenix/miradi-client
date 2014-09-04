/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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

import org.miradi.main.EAM;

public class TaxonomyMultiSelectModeQuestion extends StaticChoiceQuestion
{
	public TaxonomyMultiSelectModeQuestion()
	{
		super(getChoiceItems());
	}

	private static ChoiceItem[] getChoiceItems()
	{
		return new ChoiceItem[] {
				new ChoiceItem(SINGLE_SELECT_CODE, EAM.text("Single Selection")),
				new ChoiceItem(MULTI_SELECT_CODE, EAM.text("Multi Selection")),
		};
	}
	
	@Override
	protected boolean hasReadableAlternativeDefaultCode()
	{
		return true;
	}
	
	@Override
	protected String getReadableAlternativeDefaultCode()
	{
		return "SingleSelect";
	}
	
	public static final String SINGLE_SELECT_CODE = "";
	public static final String MULTI_SELECT_CODE = "MultiSelect";
}
