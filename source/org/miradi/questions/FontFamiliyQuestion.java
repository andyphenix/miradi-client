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

import org.miradi.main.EAM;

public class FontFamiliyQuestion extends StaticChoiceQuestion
{
	public FontFamiliyQuestion()
	{
		super(getFamilyChoices());
	}
	
	static ChoiceItem[] getFamilyChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem("", EAM.text("sans-serif")),
			new ChoiceItem(SERIF_CODE, EAM.text("serif")),
		};
	}

	public String getFontsString(ChoiceItem fontFamilyChoice)
	{
		if(fontFamilyChoice.getCode().equals(SERIF_CODE))
			return "'Times New Roman', serif";
		
		return "'Verdana', sans-serif";
	}
	
	private static final String SERIF_CODE = "serif";
}
