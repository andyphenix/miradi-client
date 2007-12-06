/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import java.awt.Color;


public class StrategyDurationQuestion extends StaticChoiceQuestion
{
	public StrategyDurationQuestion(String tag)
	{
		super(tag, "Duration of Impact", getDurationChoices());
	}
	
	static ChoiceItem[] getDurationChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem("", "Not Specified", Color.WHITE),
			new ChoiceItem("1", "Useless", COLOR_ALERT),
			new ChoiceItem("2", "Short-Term", COLOR_CAUTION),
			new ChoiceItem("3", "Long-Term", OK),
			new ChoiceItem("4", "Permanent", GREAT),
		};
	}

}
