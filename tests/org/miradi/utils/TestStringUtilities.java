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
	
	public void testEscapeQuotesWithBackslash()
	{
		verifyEscapeQuotesWithBackslash("", "");
		verifyEscapeQuotesWithBackslash("\\\"", "\"");
	}
	
	public void testUnescapeQuotesWithBackslash()
	{
		verifyUnescapeQuotesWithBackslash("", "");
		verifyUnescapeQuotesWithBackslash("\\\"", "\\\\\"");
	}
	
	private void verifyEscapeQuotesWithBackslash(String expectedValue, String valueToEscape)
	{
		assertEquals("Incorrectly escaped?", expectedValue, StringUtilities.escapeQuotesWithBackslash(valueToEscape));
	}
	
	private void verifyUnescapeQuotesWithBackslash(String expectedValue, String valueToUnescape)
	{
		assertEquals("Incorrectly unescaped?", expectedValue, StringUtilities.unescapeQuotesWithBackslash(valueToUnescape));
	}

	public void testEmptySpaceClassMember()
	{
		assertEquals("Blank space should never change?", " ", StringUtilities.EMPTY_SPACE);
	}
		
	public void testConcatenateWithOr()
	{
		verifyConcatenate("", new String[]{""});
		verifyConcatenate("first", new String[]{"first"});
		verifyConcatenate("first|second", new String[]{"first", "second"});
	}
	
	private void verifyConcatenate(final String expected, final String[] strings)
	{
		assertEquals("did not contcatenate correctly?", expected, StringUtilities.joinWithOr(strings));
	}
	
	public void testSubstringBetween()
	{
		verifySubstringBetween("one", "-one-two", "-", "-");
		verifySubstringBetween("two", "one-two-three", "-", "-");
		verifySubstringBetween("two", "one-two-", "-", "-");
	}

	public void testSubStringAfter()
	{
		verifySubstringAfter("", "something");
		verifySubstringAfter("something", "=something");
		verifySubstringAfter("", "something=");
		verifySubstringAfter("something", "tag=something");
		verifySubstringAfter("something=somethingelse", "tag=something=somethingelse");
	}
	
	public void testGetLabelLineCount()
	{
		verifyLineCount(1, "line");
		verifyLineCount(2, "line <br/> second line");
		verifyLineCount(3, "line <br/> second line <br/>");
	}
		
	private void verifyLineCount(int expectedLineCount, String label)
	{
		assertEquals("Incorrect line count?", expectedLineCount, HtmlUtilities.getLabelLineCount(label));
	}
	
	private void verifySubstringAfter(final String expectedValue, final String testString)
	{
		assertEquals("did not split correctly?", expectedValue, StringUtilities.substringAfter(testString, "="));
	}
	
	private void verifySubstringBetween(final String expectedValue, final String testString, final String fromChar, final String toChar)
	{
		assertEquals("did not split correctly?", expectedValue, StringUtilities.substringBetween(testString, fromChar, toChar));
	}
}
