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

import org.miradi.dialogs.dashboard.StaticLongDescriptionProvider;
import org.miradi.objecthelpers.TaxonomyFileLoader;


public abstract class TaxonomyClassificationQuestion extends TwoLevelQuestion
{
	public TaxonomyClassificationQuestion(String fileName)
	{
		super(new TaxonomyFileLoader(fileName));
	}
	
	@Override
	protected ChoiceItem createChoiceItem(String code, String label, String description, String longDescription) throws Exception
	{
		return new ChoiceItemWithLongDescriptionProvider(code, label, description, new StaticLongDescriptionProvider(longDescription));
	}
	
	@Override
	public boolean hasLongDescriptionProvider()
	{
		return true;
	}
	
	@Override
	public boolean canSelectMultiple()
	{
		return false;
	}
}
