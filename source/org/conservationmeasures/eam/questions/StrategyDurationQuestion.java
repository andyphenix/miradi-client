/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import java.awt.Color;

import org.conservationmeasures.eam.dialogfields.ChoiceItem;
import org.conservationmeasures.eam.dialogfields.ChoiceQuestion;

public class StrategyDurationQuestion extends ChoiceQuestion
{
	public StrategyDurationQuestion(String tag)
	{
		super(tag, "Duration of Impact", getDurationChoices());
	}
	
	static ChoiceItem[] getDurationChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem("", "Not Specified", Color.WHITE),
			new ChoiceItem("1", "Useless", COLOR_1_OF_4),
			new ChoiceItem("2", "Short-Term", COLOR_2_OF_4),
			new ChoiceItem("3", "Long-Term", COLOR_3_OF_4),
			new ChoiceItem("4", "Permanent", COLOR_4_OF_4),
		};
	}

}
