/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.questions;


public class DiagramFactorFontSizeQuestion extends StaticChoiceQuestion
{
	public DiagramFactorFontSizeQuestion()
	{
		super(getFontChoices());
	}
	
	static ChoiceItem[] getFontChoices()
	{
		return new ChoiceItem[] {
				new ChoiceItem("", "Medium (Default)"),
				new ChoiceItem("0.5", "Smallest"),
				new ChoiceItem("0.75", "Very Small"),
				new ChoiceItem("0.9", "Small"),
				new ChoiceItem("1.25", "Large"),
				new ChoiceItem("1.50", "Very Large"),
				new ChoiceItem("2.5", "Largest"),
		};
	}
}
