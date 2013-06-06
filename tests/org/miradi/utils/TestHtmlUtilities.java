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

public class TestHtmlUtilities extends MiradiTestCase
{
	public TestHtmlUtilities(String name)
	{
		super(name);
	}
	
	public void testIllegaleAnchorElements() throws Exception
	{
		verifyAnchoElements("a b c  ", "a b c  ");
		verifyAnchoElements("<a href=\"http://www.miradi.org\">miradi</a>", "<a href=\"http://www.miradi.org\">miradi</a>");
		verifyAnchoElements("<a href=\"\" target=\"noTarget\">miradi</a>", "<a target=\"noTarget\">miradi</a>");
		verifyAnchoElements("<a href=\"\">miradi</a>", "<a>miradi</a>");
		verifyAnchoElements("<a href=\"http://www.miradi.org\">miradi</a>", "<a href=\"http://www.miradi.org\" language=\"DE\">miradi</a>");
		verifyAnchoElements("<i>sample with space<a href=\"\" name=\"_msocom_1\">x</a> </i>", "<i>sample with space<a href=\"\" name=\"_msocom_1\">x</a> </i>");
	}
	
	private void verifyAnchoElements(String expectedValue, String actualValue) throws Exception
	{
		assertEquals("Anchor element was not fixed?", expectedValue, HtmlUtilities.fixAnchorElementsSaftley(actualValue));
	}
	
	public void testStripHtmlComments()
	{
		verifyStrippingComments("without comments", "without comments");
		verifyStrippingComments("", "<!---->");
		verifyStrippingComments("", "<!--some Random comment-->");
		verifyStrippingComments("", "<!-- some random comment -->");
		verifyStrippingComments(" with comments", "<!-- comment --> with comments");
		verifyStrippingComments("with  comments", "with <!-- comment --> comments");
		verifyStrippingComments("with comments ", "with comments <!-- comment -->");
		verifyStrippingComments("", "<!--line1 \n line2-->");
	}
	
	private void verifyStrippingComments(String expectedValue, String actualValue)
	{
		assertEquals("Comments were not stripped?", expectedValue, HtmlUtilities.stripHtmlComments(actualValue));
	}

	public void testStripAttributesFromNonAnchorElements()
	{
		verifyStrippingOfAttribute("sampleData", "sampleData");
		verifyStrippingOfAttribute("<style>sampleData", "<style>sampleData");
		verifyStrippingOfAttribute("sampleData</style>", "sampleData</style>");
		verifyStrippingOfAttribute("<style>sampleData</style>", "<style>sampleData</style>");
		verifyStrippingOfAttribute("<style>sampleData</style>", "<style size=3>sampleData</style>");
		verifyStrippingOfAttribute("<style>sampleData</style>", "<style size=3 color=\"blue\" width=40>sampleData</style>");
		
		verifyStrippingOfAttribute("<a href=\"www.miradi.org\">miradi</a>", "<a href=\"www.miradi.org\">miradi</a>");
		verifyStrippingOfAttribute("<A href=\"www.miradi.org\">miradi</a>", "<A href=\"www.miradi.org\">miradi</a>");
	}
	
	private void verifyStrippingOfAttribute(String expectedValue, String actualValue)
	{
		assertEquals("Html tag attirbutes not removed?", expectedValue, HtmlUtilities.stripAttributesFromNonAnchorElements(actualValue));
	}

	public void testRemoveStartToEndTagAndItsContent()
	{
		verifyRemoveStartToEndTagAndItsContent("", "<style>some stuff</style>");
		verifyRemoveStartToEndTagAndItsContent("<style>some stuff", "<style>some stuff");
		verifyRemoveStartToEndTagAndItsContent("", "<head>some stuff</HEAD>");
		verifyRemoveStartToEndTagAndItsContent("", "<head><meta name=Random></HEAD>");
		verifyRemoveStartToEndTagAndItsContent("<b>this is left behind</b>", "<b>this is<style>removed</style> left behind</b>");
		verifyRemoveStartToEndTagAndItsContent("", "<style>this is<b>removed</b> left behind</style>");
		verifyRemoveStartToEndTagAndItsContent("", "<style>\n</style>");
		verifyRemoveStartToEndTagAndItsContent("", "< style >ABC< / style >");
	}
	
	private void verifyRemoveStartToEndTagAndItsContent(String expectedValue, String actualValue)
	{
		assertEquals("Html tags and content was not removed?", expectedValue, HtmlUtilities.removeStartToEndTagAndItsContent(actualValue, new String[]{"style", "head"}));
	}

	public void testReplaceHtmlBullets() throws Exception
	{
		verifyReplaceHtmlBullets("", "");
		verifyReplaceHtmlBullets("randomText", "randomText");
		verifyReplaceHtmlBullets("- item<br/>", "<ul><li>item</li></ul>");
		verifyReplaceHtmlBullets("- item1<br/>- item2<br/>", "<ul><li>item1</li><li>item2</li></ul>");
	}
	
	private void verifyReplaceHtmlBullets(String expected, String actual)
	{
		assertEquals("Didnt replace bullets?", expected, HtmlUtilities.replaceHtmlBullets(actual));
	}

	public void testGetFirstLineWithTruncationIndicated()
	{
		verifyGetFirstLineWithTruncationIndicated("", "");
		verifyGetFirstLineWithTruncationIndicated("single line", "single line");
		verifyGetFirstLineWithTruncationIndicated("Line 1 ...", "Line 1 <br/> line 2");
	}

	public void testGetNormalizedAndSanitizedHtmlText()
	{
		String htmlText = 
		  "<html>\n" +
		  "<head>\n" +
		  "	</head>\n" +
		  "\t<body>\n" +
		  "		<div>\n" +
		  "	 	 text on\tline 1 \n" +
		  "	 	 text on line 2\t\n" +
		  "     </div>\n" +
		  "     <div>\n" +
		  "\t\ttext on line\t 3\n" +
		  "		</div>\n" +
		  "\t</body>\n" +
		  "	</html>\n";

		assertEquals("wrong new lines inserted?", "text on line 1 text on line 2<br/>text on line 3", HtmlUtilitiesRelatedToShef.getNormalizedAndSanitizedHtmlText(htmlText, getAllowedHtmlTags()));
	}
	
	public void testIncorrectEncodingOfApostrophe()
	{
		String htmlText = "we'll";
		assertEquals("wrong new lines inserted?", "we&apos;ll", HtmlUtilitiesRelatedToShef.getNormalizedAndSanitizedHtmlText(htmlText, getAllowedHtmlTags()));
	}
	
	public void testSpacesAroundElementShouldNotBeCompletelyRemoved()
	{
		verifySpaceRemovalAroundElement("X<b/>Y", "X<b/>Y");
		verifySpaceRemovalAroundElement("X<b/> Y", "X<b/> Y");
		verifySpaceRemovalAroundElement("X<b/> Y", "X<b/>   Y");
		verifySpaceRemovalAroundElement("X <b/>Y", "X    <b/>Y");
		verifySpaceRemovalAroundElement("X <b/> Y", "X    <b/>   Y");
		verifySpaceRemovalAroundElement("X<b/>", "X<b/>");
		verifySpaceRemovalAroundElement("X<b/>", "X<b/>   ");
		verifySpaceRemovalAroundElement("X <b/>", "X    <b/>   ");
		verifySpaceRemovalAroundElement("X<br/>Y", "X <br/>Y");
		verifySpaceRemovalAroundElement("X<br/>Y", "X <br/> Y");
	}

	private void verifySpaceRemovalAroundElement(final String expectedValue, final String htmlText)
	{
		assertEquals("incorrect spacing around element?", expectedValue, HtmlUtilitiesRelatedToShef.getNormalizedAndSanitizedHtmlText(htmlText, getAllowedHtmlTags()));
	}
	
	public void testGetWithoutSpacesAfterXmlElements()
	{
		final String htmlText = "<br/> sample value <br/>";
		final String expectedValue = "<br/>sample value<br/>";

		assertEquals("Trailing white space was not removed?", expectedValue, HtmlUtilities.getWithoutSpacesAfterXmlElements(htmlText));
	}
	
	private static String[] getAllowedHtmlTags()
	{
		return new String[] {"br", "b", "i", "ul", "ol", "li", "u", "strike", "a", };
	}

	public void testReplaceNonEmptyBrTags()
	{
		verifyStartBrTagsReplacedWithEmptyBrTags("line1", "line1");
		verifyStartBrTagsReplacedWithEmptyBrTags("line1<br/>line2", "line1<br>line2");
	}

	public void testEnsureNoCloseBrTags()
	{
		try
		{
			HtmlUtilities.ensureNoCloseBrTags("line1</br>line2");
			fail("method did not fail due to invalid br tag?");
		}
		catch (Exception expectedExceptionToIgnore)
		{
		}
	}
	
	private void verifyStartBrTagsReplacedWithEmptyBrTags(final String expectedValue, String testValue)
	{
		assertEquals("didnt replace invalid tag?", expectedValue, HtmlUtilities.replaceStartBrTagsWithEmptyBrTags(testValue));
	}
	
	public void testWrapInHtmlTags()
	{
		assertEquals("didnt wrap with html tags?", "<html></html>", HtmlUtilities.wrapInHtmlTags(""));
		assertEquals("didnt wrap with html tags?", "<html>someValue</html>", HtmlUtilities.wrapInHtmlTags("someValue"));
		assertEquals("didnt wrap with html tags?", "<html><html>someValue</html></html>", HtmlUtilities.wrapInHtmlTags("<html>someValue</html>"));
	}
		
	public void testRemoveAllExcept()
	{
		verifyNothingStripped("text");
		verifyRemoveAllExcept("text<br/>", "<font>text<br/>");
		verifyRemoveAllExcept("text<br/ >", "<font>text<br/ >");
		verifyRemoveAllExcept("text<br />", "<font>text<br />");
		verifyRemoveAllExcept("text< br/>", "<font>text< br/>");
		verifyNothingStripped("<b>text");
		verifyNothingStripped("<b>text</b>");
		verifyNothingStripped("<b>text</b >");
		verifyNothingStripped("<b>text< /b>");
		verifyNothingStripped("<b>text</ b>");
		verifyRemoveAllExcept("<b>text", "<body><b>text");
		verifyRemoveAllExcept("<b>text</b>", "<body><b>text</b></body>");
		verifyRemoveAllExcept("<b>text</b>", "<html><body><b>text</b></htm></body>");
		verifyRemoveAllExcept("<b>text", "<font size=\"5\"><b>text</font>");
		verifyRemoveAllExcept("<b someAttribute=\"x\">text", "<b someAttribute=\"x\">text</font>");
		verifyRemoveAllExcept("text", "<html><head></head><body><p style=\"margin-top: 0\">text</p></body></html>");
	}
	
	private void verifyNothingStripped(String text)
	{
		assertEquals("tags were removed?", text, HtmlUtilities.removeAllExcept(text, getTagsToKeep()));
	}

	private void verifyRemoveAllExcept(String expected, String htmlText)
	{
		assertEquals("tags were removed?", expected, HtmlUtilities.removeAllExcept(htmlText, getTagsToKeep()));
	}
	
	private String[] getTagsToKeep()
	{
		return new String[] {"br", "b", "i", "ul", "ol", "li", "u", "strike", };
	}
	
	public void testAppendNewlineToEndDivTags()
	{
		verifyDivWasAppendedWithNewline("<div>sometext</div>\n", "<div>sometext</div>");
		verifyDivWasAppendedWithNewline("sometext<div/>\nmore", "sometext<div/>more");
		verifyDivWasAppendedWithNewline("<div>sometext</div>\n", "<div>sometext</div>");
	}

	protected void verifyDivWasAppendedWithNewline( String expectedValue, String htmlText)
	{
		final String actualValue = HtmlUtilities.appendNewlineToEndDivTags(htmlText);
		assertEquals("div tag was not found and appended with newline, new line missing?", expectedValue, actualValue);
	}
	
	public void testReplaceHtmlTags()
	{
		verifyReplacingTags("sometext<br>", "br", "", "sometext");
		verifyReplacingTags("sometext<br >", "br", "", "sometext");
		verifyReplacingTags("<br>sometext</br>", "br", "", "sometext");
		verifyReplacingTags("<br>sometext</br >", "br", "", "sometext");
		verifyReplacingTags("sometext<br/>", "br", "", "sometext");
		verifyReplacingTags("sometext<br />", "br", "", "sometext");
		verifyReplacingTags("sometext<font size=\"4\">", "font", "", "sometext");
		verifyReplacingTags("sometext<font size=\"4\" >", "font", "", "sometext");
	}
	
	public void testStripAllHtmlTags()
	{
		verifyStringHtmlTags("", "");
		verifyStringHtmlTags("<html>", "");
		verifyStringHtmlTags("<html><body><h1><br/>someText</body></html>", "someText");
		verifyStringHtmlTags("<html><body><h1><font size=\"5\">someText</body></html>", "someText");
	}

	private void verifyStringHtmlTags(String textWithHtmlTags, String expectedValue)
	{
		assertEquals("html tags were not stripped correctly?", expectedValue, HtmlUtilities.stripAllHtmlTags(textWithHtmlTags));
	}
	
	private void verifyReplacingTags(final String htmlText, final String tagToReplace, final String replacement, final String expectedValue)
	{
		assertEquals("html tag was not replaced?", expectedValue, HtmlUtilities.replaceHtmlTags(htmlText, tagToReplace, replacement));
	}
	
	private void verifyGetFirstLineWithTruncationIndicated(String expctedValue,	String actualValue)
	{
		assertEquals("Incorrect turncation of lines?", expctedValue, HtmlUtilities.getFirstLineWithTruncationIndicated(actualValue));
	}
}
