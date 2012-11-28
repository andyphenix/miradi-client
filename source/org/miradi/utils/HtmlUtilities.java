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
package org.miradi.utils;

import java.awt.Color;
import java.util.Collections;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.text.html.StyleSheet;

import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;

public class HtmlUtilities
{
	public static String wrapInHtmlTags(String value)
	{
		return "<html>" + value + "</html>";
	}
	
	public static void addRuleFontSize(StyleSheet style, final int defaultFontSize, final int fontSize)
	{
		int size = fontSize;
		if (fontSize == 0)
			size = defaultFontSize;
		
		style.addRule(HtmlUtilities.makeSureRuleHasRightPrefix("body {font-size:"+size+"pt;}"));
	}
	
	public static void addRuleFontFamily(StyleSheet style, final String fontFamily)
	{
		style.addRule(HtmlUtilities.makeSureRuleHasRightPrefix("body {font-family:" + fontFamily + ";}"));
	}
	
	public static void addFontColor(StyleSheet style, Color color)
	{
		style.addRule(HtmlUtilities.makeSureRuleHasRightPrefix("body {color:" + AppPreferences.convertToHexString(color) + ";}"));
	}
	
	public static String makeSureRuleHasRightPrefix(String rule)
	{
		if (cssDotPrefixWorksCorrectly())
			return rule;

		return replaceDotWithPoundSign(rule);
	}
	
	public static boolean cssDotPrefixWorksCorrectly()
	{
		String javaVersion = EAM.getJavaVersion();
		if (javaVersion.startsWith("1.4"))
			return false;
		return true;
	}
	
	private static String replaceDotWithPoundSign(String rule)
	{
		if (rule.trim().startsWith("."))
			return rule.trim().replaceFirst(".", "#");

		return rule;
	}

	public static String convertPlainTextToHtmlText(String nonHtmlText)
	{
		nonHtmlText = XmlUtilities2.getXmlEncoded(nonHtmlText);
		nonHtmlText = replaceNonHtmlNewlines(nonHtmlText);
		
		return nonHtmlText;
	}
	
	public static String convertHtmlToPlainText(String htmlDataValue)
	{
		htmlDataValue = replaceHtmlBrsWithNewlines(htmlDataValue);
		htmlDataValue = XmlUtilities2.convertXmlTextToPlainText(htmlDataValue);
		htmlDataValue = HtmlUtilities.replaceHtmlBullets(htmlDataValue);
		htmlDataValue = stripAllHtmlTags(htmlDataValue);
		
		return htmlDataValue;
	}

	public static void ensureNoCloseBrTags(String text)
	{
		if (text.contains("</br>"))
			throw new RuntimeException("Text contains </br> tag(s)");		
	}

	public static String replaceStartBrTagsWithEmptyBrTags(String text)
	{
		return replaceHtmlTags(text, "br", BR_TAG);
	}

	public static String getNewlineRegex()
	{
		return "\\r?\\n";
	}
	
	public static String replaceHtmlBrsWithNewlines(String text)
	{
		return replaceHtmlTags(text, "br", StringUtilities.NEW_LINE);
	}

	public static String replaceNonHtmlNewlines(String formatted)
	{
		return formatted.replaceAll(getNewlineRegex(), BR_TAG);
	}
	
	public static String removeNonHtmlNewLines(String htmlText)
	{
		return htmlText.replaceAll(getNewlineRegex(), StringUtilities.EMPTY_STRING);
	}

	public static String stripAllHtmlTags(String text)
	{
		final String ANY = "<.*?>";
		return replaceAll(ANY, text, StringUtilities.EMPTY_STRING);
	}

	public static String replaceHtmlTags(String text, String tagToReplace, final String replacement)
	{
		final String START = createStartTagRegex(tagToReplace);
		final String START_WITH_ATRIBUTE = createStartTagWithAttributeRegex(tagToReplace);
		final String END = createEndTagRegex(tagToReplace);
		final String EMPTY = createEmptyTagRegex(tagToReplace);
		final String regex = START + "|" + EMPTY + "|" + END + "|" + START_WITH_ATRIBUTE; 
		
		return replaceAll(regex, text, replacement);
	}

	public static String replaceStartHtmlTags(String text, String tagToReplace, final String replacement)
	{
		final String START = createStartTagRegex(tagToReplace);
		final String START_WITH_ATRIBUTE = createStartTagWithAttributeRegex(tagToReplace);
		final String regex = START + "|" + START_WITH_ATRIBUTE;
		
		return replaceAll(regex, text, replacement);
	}
	
	private static String createStartTagWithAttributeRegex(String tagToReplace)
	{
		return "<" + tagToReplace + "\\s+.*?>";
	}

	private static String createStartTagRegex(String tagToReplace)
	{
		return "<" + tagToReplace + "\\s*>";
	}

	private static String createEmptyTagRegex(String tagToReplace)
	{
		return "<" + tagToReplace + "\\s*/\\s*>";
	}

	private static String createEndTagRegex(String tag)
	{
		return "<\\/\\s*" + tag + "\\s*>";
	}
	
	private static String replaceAll(final String regex, final String text, final String replacement)
	{
		final Pattern compiledRegex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		
		return compiledRegex.matcher(text).replaceAll(replacement);
	}
	
	public static String appendNewlineToEndDivTags(String text)
	{
		final String END_DIV_REGEX = createEndTagRegex(DIV_TAG_NAME);
		text = replaceAll(END_DIV_REGEX, text, DIV_CLOSING_TAG + StringUtilities.NEW_LINE);
		final String EMPTY_DIV_REGEX = createEmptyTagRegex(DIV_TAG_NAME);
		text = replaceAll(EMPTY_DIV_REGEX, text, DIV_EMPTY_TAG + StringUtilities.NEW_LINE);
		return text;
	}
	
	public static String removeAllExcept(String text, String[] tagsToKeep)
	{
		String tagsSeperatedByOr = StringUtilities.joinWithOr(tagsToKeep);
		
		String regex = "<\\/*?(?![^>]*?\\b(?:" + tagsSeperatedByOr + ")\\b)[^>]*?>";;
		final Pattern compiledRegex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		
		return compiledRegex.matcher(text).replaceAll(StringUtilities.EMPTY_STRING);
	}
	
	public static String getWithoutSpacesAfterXmlElements(final String text)
	{
		String result = replaceAll(" +(<.+>)", text, " $1");
		result = replaceAll("(<.+>) +", result, "$1 ");
		result = replaceAll("(<br/>) +", result, "$1");
		result = replaceAll(" +(<br/>)", result, "$1");
		
		return result;
	}

	public static String getNormalizedAndSanitizedHtmlText(String text, String[] allowedHtmlTags)
	{
		text = replaceAllEmptyDivsWithBrs(text);
		String trimmedText = "";
		final String[] lines = text.split(getNewlineRegex());
		for (int index = 0; index < lines.length; ++index)
		{
			//NOTE: Shef editor never splits text between lines, so we can safely ignore the text\ntext case
			String line = lines[index];
			String leadingSpacesRemoved = line.replaceAll("^[ \\t]+", "");
			trimmedText += leadingSpacesRemoved;
		}
		
		// NOTE: The Java HTML parser compresses all whitespace to a single space
		// (http://java.sun.com/products/jfc/tsc/articles/bookmarks/)
		trimmedText = removeNonHtmlNewLines(trimmedText);
		trimmedText = appendNewlineToEndDivTags(trimmedText);
		trimmedText = removeAllExcept(trimmedText, allowedHtmlTags);
		trimmedText = trimmedText.replaceAll("\\t", " ");
		trimmedText = trimmedText.replaceAll(" +", " ");
		trimmedText = trimmedText.trim();		
		trimmedText = replaceNonHtmlNewlines(trimmedText);
		//NOTE: Third party library  uses <br> instead of <br/>.  If we don't replace <br> then 
		//save method thinks there was a change and attempts to save.
		trimmedText = replaceStartBrTagsWithEmptyBrTags(trimmedText);
		trimmedText = getWithoutSpacesAfterXmlElements(trimmedText);
		// NOTE: Shef does not encode/decode apostrophes as we need for proper XML
		trimmedText = XmlUtilities2.getXmlEncodedApostrophes(trimmedText);
		ensureNoCloseBrTags(trimmedText);
		
		return trimmedText;
	}

	private static String replaceAllEmptyDivsWithBrs(String text)
	{
		text = text.replaceAll("<div>\\s*</div>", "<br/>");
		return text;
	}
	
	public static int getLabelLineCount(String labelToUse)
	{
		String label = labelToUse + "AvoidSplitTrimmingTrailingNewlines";
		String[] lines = label.split(BR_TAG);
		return lines.length;
	}
	
	public static String getFirstLineWithTruncationIndicated(final String value)
	{
		String firstLine = value;
		int newlineAt = value.indexOf(BR_TAG);
		if (newlineAt >= 0)
			firstLine = value.substring(0, newlineAt) + "...";
		
		return firstLine;
	}

	public static String createHtmlBulletList(Vector<String> labels)
	{
		Collections.sort(labels, String.CASE_INSENSITIVE_ORDER);
	
		StringBuffer result = new StringBuffer();
		for(int index = 0; index < labels.size(); ++index)
		{
			if(result.length() == 0)
				result.append(UL_START_TAG);
			
			String label = labels.get(index);
			result.append(LI_START_TAG);
			result.append(label);
			result.append(LI_END_TAG);
		}
		
		if(result.length() > 0)
			result.append(UL_END_TAG);
		
		return result.toString();
	}
	
	public static String replaceHtmlBullets(String value)
	{
		value = value.replaceAll(LI_START_TAG, "- ");
		value = value.replaceAll(LI_END_TAG, BR_TAG);
		value = replaceHtmlTags(value, "ul", "");
		
		return value;
	}

	public static final String BR_TAG = "<br/>";
	public static final String UL_START_TAG = "<ul>";
	public static final String UL_END_TAG = "</ul>";
	public static final String LI_START_TAG = "<li>";
	public static final String LI_END_TAG = "</li>";
	private static final String DIV_TAG_NAME = "div";
	private static final String DIV_CLOSING_TAG = "</div>";
	private static final String DIV_EMPTY_TAG = "<div/>";
}
