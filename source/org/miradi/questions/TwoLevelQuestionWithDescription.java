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

import org.miradi.dialogs.dashboard.StaticLongDescriptionProvider;
import org.miradi.objecthelpers.TwoLevelFileLoader;

public class TwoLevelQuestionWithDescription extends MultiSelectTwoLevelQuestion
{
	public TwoLevelQuestionWithDescription(TwoLevelFileLoader twoLevelFileLoaderToUse)
	{
		super(twoLevelFileLoaderToUse);
	}

	@Override
	protected ChoiceItem createChoiceItem(String code, String label, String description, String longDescription) throws Exception
	{
		return new ChoiceItemWithLongDescriptionProvider(code, label, new StaticLongDescriptionProvider(description));
	}

	@Override
	public boolean hasAdditionalText()
	{
		return false;
	}
	
	@Override
	public boolean hasLongDescriptionProvider()
	{
		return false;
	}
}
