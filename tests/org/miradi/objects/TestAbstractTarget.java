/* 
Copyright 2005-2013, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.objects;

import org.miradi.objecthelpers.ORef;
import org.miradi.questions.StatusQuestion;


abstract public class TestAbstractTarget extends ObjectTestCase
{
	public TestAbstractTarget(String name)
	{
		super(name);
	}
	
	public void testFields() throws Exception
	{
		verifyFields(getTargetType());
	}
	
	public void testOverallProjectViabilityRating() throws Exception
	{
		verifyOverallViabilityRating("");
		
		ORef abstractTargetRef = getProject().createObject(getTargetType());
		AbstractTarget target = AbstractTarget.findTarget(getProject(), abstractTargetRef);
		getProject().fillObjectUsingCommand(target, AbstractTarget.TAG_TARGET_STATUS, StatusQuestion.VERY_GOOD);
		verifyOverallViabilityRating(StatusQuestion.VERY_GOOD);
	}

	private void verifyOverallViabilityRating(String expectedRating)
	{
		String actualRating = AbstractTarget.computeTNCViability(getProject());
		assertEquals("Incorrect overall viability calculated?", expectedRating, actualRating);
	}
	
	abstract protected int getTargetType();
}
