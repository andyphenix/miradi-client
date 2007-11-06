/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import java.awt.Color;

public class StatusQuestion extends StaticChoiceQuestion
{
	public StatusQuestion(String tagToUse)
	{
		super(tagToUse, "Measurement Status", getStatuses());
	}

	static ChoiceItem[] getStatuses()
	{
		return new ChoiceItem[] {
				new ChoiceItem("", "Not Specified", Color.WHITE),
				new ChoiceItem("1", "Poor", COLOR_1_OF_4),
				new ChoiceItem("2", "Fair", COLOR_2_OF_4),
				new ChoiceItem("3", "Good", COLOR_3_OF_4),
				new ChoiceItem("4", "Very Good", COLOR_4_OF_4),
		};
	}
	
	public static ChoiceItem getChoiceItemFromCodeAsString(String intAsString)
	{
		try
		{
			int parsed = Integer.parseInt(intAsString);
			return getStatuses()[parsed];
		}
		catch (Exception e)
		{
			return getStatuses()[0];
		}
	}
	
	public static final String UNSPECIFIED = "";
	public static final String POOR = "1";
	public static final String FAIR = "2";
	public static final String GOOD = "3";
	public static final String VERY_GOOD = "4";

}
