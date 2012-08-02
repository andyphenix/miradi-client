/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class MiradiZipFile extends ZipFile
{
	public MiradiZipFile(File file) throws ZipException, IOException
	{
		super(file);
	}
	
	@Override
	public ZipEntry getEntry(String name)
	{
		ZipEntry entry = super.getEntry(name);
		if (entry != null)
			return entry;
		
		String reversedFirstCharName = replaceWithOtherPossibleLeadingChar(name, FileUtilities.SEPARATOR);
		entry = super.getEntry(reversedFirstCharName);
		if (entry != null)
			return entry;
		
		String reversedPathSeparator = attemptUsingReversedPathSeparator(name);
		if (reversedPathSeparator != null)
		{
			entry = super.getEntry(reversedPathSeparator);
			if (entry != null)
				return entry;
			
			reversedFirstCharName = replaceWithOtherPossibleLeadingChar(reversedPathSeparator, getPathSeparatorUsedInZipFile());
			return super.getEntry(reversedFirstCharName);
		}
		
		return null;
	}
	
	private String getPathSeparatorUsedInZipFile()
	{
		Enumeration<? extends ZipEntry> entries = entries();
		while (entries.hasMoreElements())
		{
			ZipEntry entry = entries.nextElement();
			if (entry.getName().contains(FileUtilities.SEPARATOR))
				return FileUtilities.SEPARATOR;
			
			if (entry.getName().contains(FileUtilities.BACKWARD_SLASH))
				return FileUtilities.BACKWARD_SLASH;
		}
		
		return FileUtilities.SEPARATOR;
	}

	public static String attemptUsingReversedPathSeparator(String name)
	{
		if (name.contains(FileUtilities.SEPARATOR))
			return name.replaceAll(FileUtilities.SEPARATOR, "\\\\");
		
		if (name.contains(FileUtilities.BACKWARD_SLASH))
			return name.replaceAll("\\\\", FileUtilities.SEPARATOR);
		
		return null;
	}

	public static String replaceWithOtherPossibleLeadingChar(String name, final String separator)
	{
			if (name.startsWith(separator))
				return replaceFirst(separator, name, "");
		
		return separator + name;		
	}
	
	private static String replaceFirst(String regex, String text, String replacement)
	{
		final Pattern compiledRegex = Pattern.compile(regex, Pattern.LITERAL);
		
		return compiledRegex.matcher(text).replaceFirst(replacement);
	}
}
