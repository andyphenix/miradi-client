/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import java.awt.Color;

import org.conservationmeasures.eam.dialogfields.ChoiceItem;
import org.conservationmeasures.eam.dialogfields.ChoiceQuestion;

public class StrategyRatingSummary extends ChoiceQuestion
{
	public StrategyRatingSummary(String tag)
	{
		super(tag, "Priority", getStrategyRatingChoices());
	}
	
	static ChoiceItem[] getStrategyRatingChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem("", "Unknown", Color.WHITE),
			new ChoiceItem("1", "Completely Ineffective", COLOR_1_OF_4),
			new ChoiceItem("2", "Ineffective", COLOR_2_OF_4),
			new ChoiceItem("3", "Effective", COLOR_3_OF_4),
			new ChoiceItem("4", "Very Effective", COLOR_4_OF_4),
		};
	}

	public ChoiceItem getResult(ChoiceItem impact, ChoiceItem duration, ChoiceItem feasibility, ChoiceItem cost)
	{
		String impactCode = impact.getCode();
		String durationCode = duration.getCode();
		String feasibilityCode = feasibility.getCode();
		String costCode = cost.getCode();
		
		int impactValue = codeToInt(impactCode);
		int durationValue = codeToInt(durationCode);
		int feasibilityValue = codeToInt(feasibilityCode);
		int costValue = codeToInt(costCode);
		
		int average = 0;
		if(impactValue * durationValue * feasibilityValue * costValue == 0)
			average = 0;
		else if(impactValue == 1 || durationValue == 1 || feasibilityValue == 1 || costValue == 1)
			average = 1;
		else
		{
			int total = impactValue + durationValue + feasibilityValue + costValue;
			average = total / 4;
		}
		
		if(average == 0)
			return findChoiceByCode("");
		return findChoiceByCode(Integer.toString(average));
	}

	private int codeToInt(String code)
	{
		if(code.length() == 0)
			return 0;
		return Integer.parseInt(code);
	}
}
