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

import java.util.Vector;

public class StaticChoiceQuestion extends ChoiceQuestion
{
	public StaticChoiceQuestion()
	{
		this("");
	}
	
	public StaticChoiceQuestion(String questionDescriptionToUse)
	{
		super(questionDescriptionToUse);
		
		choices = createChoices();
	}
	
	public StaticChoiceQuestion(ChoiceItem[] choicesToUse)
	{
		this(choicesToUse, "");
	}
	
	public StaticChoiceQuestion(ChoiceItem[] choicesToUse, String questionDescription)
	{
		super(questionDescription);
		
		choices = choicesToUse;
	}
	
	public StaticChoiceQuestion(Vector<ChoiceItem> choicesToUse, String questionDescription)
	{
		this(choicesToUse.toArray(new ChoiceItem[0]), questionDescription);
	}
	
	public StaticChoiceQuestion(Vector<ChoiceItem> choicesToUse)
	{
		this(choicesToUse.toArray(new ChoiceItem[0]));
	}

	@Override
	public ChoiceItem[] getChoices()
	{
		return choices;
	}
	
	protected ChoiceItem[] createChoices()
	{
		throw new RuntimeException("The overriding class calling StaticChoiceQuestion() needs to override this method.");
	}
	
	@Override
	public boolean canSelectMultiple()
	{
		return false;
	}
	
	private ChoiceItem[] choices;
}
