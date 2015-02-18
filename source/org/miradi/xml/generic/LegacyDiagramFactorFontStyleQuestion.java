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

package org.miradi.xml.generic;

import org.miradi.main.EAM;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.StaticChoiceQuestion;

public class LegacyDiagramFactorFontStyleQuestion extends StaticChoiceQuestion
{
	public LegacyDiagramFactorFontStyleQuestion()
	{
		super(getStyleChoices());
	}
	
	static ChoiceItem[] getStyleChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem(PLAIN_CODE, EAM.text("Plain (Default)")),
			new ChoiceItem(BOLD_CODE, EAM.text("Bold")),
			new ChoiceItem(UNDERLINE_CODE, EAM.text("Underline")),
			new ChoiceItem(STRIKE_THROUGH_CODE, EAM.text("Strike through")),
		};
	}
	
	public static final String PLAIN_CODE = "";
	public static final String BOLD_CODE = "<B>";
	public static final String UNDERLINE_CODE = "<U>";
	public static final String STRIKE_THROUGH_CODE = "<S>";
}