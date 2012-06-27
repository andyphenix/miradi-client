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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;

public class FileUtilities
{
	public static HashSet<File> getAllRecursiveFilePaths(File startDirectory)
	{
		HashSet<File> allFilePaths = new HashSet<File>();
		File[] currentDirChildren = startDirectory.listFiles();
		for (int index = 0; index < currentDirChildren.length; ++index)
		{
			File childFile = currentDirChildren[index];
			if (childFile.isDirectory())
				allFilePaths.addAll(getAllRecursiveFilePaths(childFile));
			else
				allFilePaths.add(childFile);
		}
		
		return allFilePaths;
	}
	
	public static void copyStream(InputStream inputStream, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
		int got = -1;
		while( (got = inputStream.read(buffer)) > 0)
		{
			out.write(buffer, 0, got);
		}
	}

	public static void copyStreamToFile(InputStream inputStream, File destinationFile) throws IOException
	{
		FileOutputStream out = new FileOutputStream(destinationFile);
		try
		{
			copyStream(inputStream, out);
		}
		finally
		{
			out.close();
		}
	}

	public static File createTempDirectory(String nameHint) throws IOException
	{
		File tempDirectory = File.createTempFile("$$$" + nameHint, null);
		tempDirectory.deleteOnExit();
		tempDirectory.delete();
		tempDirectory.mkdir();
		return tempDirectory;
	}
	
	public static void deleteIfExists(File file) throws IOException
	{
		if(!file.exists())
			return;
		
		if(!file.delete())
			throw new IOException("Delete failed: " + file.getAbsolutePath());
	}
	
	public static void renameIfExists(File fromFile, File toFile) throws IOException
	{
		if(!fromFile.exists())
			return;
		
		rename(fromFile, toFile);
	}

	public static void rename(File fromFile, File toFile) throws IOException
	{
		if(!fromFile.renameTo(toFile))
			throw new IOException("Rename failed: " + fromFile.getAbsolutePath() + "->" + toFile.getAbsolutePath());
	}

	public static File getFileWithSuffix(File currentFile, String suffix)
	{
		return new File(currentFile.getAbsolutePath() + suffix);
	}

	public static File createTempFileCopyOf(InputStream mpzInputStream) throws IOException
	{
		File temporaryFile = File.createTempFile("$$$tempFileCopy", null);
		temporaryFile.deleteOnExit();
		copyStreamToFile(mpzInputStream, temporaryFile);
		
		return temporaryFile;
	}
}
