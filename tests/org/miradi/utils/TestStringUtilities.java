/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.utils;

import org.miradi.main.MiradiTestCase;

public class TestStringUtilities extends MiradiTestCase
{
	public TestStringUtilities(String name)
	{
		super(name);
	}
	
	public void testSubStringAfter()
	{
		verifySubstringAfter("", "something");
		verifySubstringAfter("something", "=something");
		verifySubstringAfter("", "something=");
		verifySubstringAfter("something", "tag=something");
		verifySubstringAfter("something=somethingelse", "tag=something=somethingelse");
	}
	
	private void verifySubstringAfter(final String expectedValue, final String testString)
	{
		assertEquals("did not split correctly?", expectedValue, StringUtilities.substringAfter(testString, "="));
	}
}
