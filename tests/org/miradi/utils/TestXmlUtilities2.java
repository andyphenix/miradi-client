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

public class TestXmlUtilities2 extends MiradiTestCase
{
	public TestXmlUtilities2(String name)
	{
		super(name);
	}
	
	public void testGetUnescapedNumericValues() throws Exception
	{
		verifyUnescapedNumericValue("�", "&#162;");
	}
		
	private void verifyUnescapedNumericValue(String expectedValue, String actualValue) throws Exception
	{
		assertEquals("incorrect unespcaping of numeric value?", expectedValue, XmlUtilities2.getWithUnescapedNumericEntities(actualValue));
	}

	public void testIsValidXml()
	{
		verifyValidXml(null);
		verifyValidXml("AB");
		verifyValidXml("A&amp;B");
		verifyValidXml("A&lt;B");
		verifyValidXml("A&gt;B");
		verifyValidXml("A&apos;B");
		verifyValidXml("A&quot;B");
		
		verifyInvalidXml("A&B");
		verifyInvalidXml("A<B");
		verifyInvalidXml("A>B");
		verifyInvalidXml("A'B");
		verifyInvalidXml("A\"B");
	}

	private void verifyValidXml(final String value)
	{
		try
		{
			XmlUtilities2.throwIfInvalidXmlWithHtmlTags(value);
		}
		catch (Exception e)
		{
			fail("Value should have been a valid xml: " + value);
		}
	}

	private void verifyInvalidXml(final String value)
	{
		try
		{
			XmlUtilities2.throwIfInvalidXmlWithHtmlTags(value);
			fail("Value is not a valid xml: " + value);
		}
		catch (Exception ignoreException)
		{
		}
	}

	public void testGetXmlDecoded()
	{
		verifyDecoding("<", "&lt;");
		verifyDecoding(">", "&gt;");
		verifyDecoding("\"", "&quot;");
		verifyDecoding("'", "&apos;");
		verifyDecoding("&", "&amp;");
		
		verifyDecoding("<", "&#x3C;");
		verifyDecoding(">", "&#x3E;");
		verifyDecoding("\"", "&#x22;");
		verifyDecoding("'", "&#x27;");
		verifyDecoding("&", "&#x26;");
		
		verifyDecoding("<", "&#x3c;");
		verifyDecoding(">", "&#x3e;");

		verifyDecoding("<", "&#60;");
		verifyDecoding(">", "&#62;");
		verifyDecoding("\"", "&#34;");
		verifyDecoding("'", "&#39;");
		verifyDecoding("&", "&#38;");
		verifyDecoding("\u2013", "&#8211;");
	}
	
	public void testGetXmlEncoded()
	{
		verifyEncoding("&lt;", "<");
		verifyEncoding("&gt;", ">");
		verifyEncoding("&quot;", "\"");
		verifyEncoding("&apos;", "'");
		verifyEncoding("&amp;", "&");
	}

	private void verifyDecoding(String expectedValue, String sampleValue)
	{
		assertEquals("did not decode?", expectedValue, XmlUtilities2.getXmlDecoded(sampleValue));
	}
	
	private void verifyEncoding(String expectedValue, String sampleValue)
	{
		assertEquals("did not encode?", expectedValue, XmlUtilities2.getXmlEncoded(sampleValue));
	}
}
