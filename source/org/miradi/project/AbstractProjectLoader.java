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

package org.miradi.project;

import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.martus.util.UnicodeReader;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.threatrating.RatingValueSet;
import org.miradi.project.threatrating.ThreatRatingBundle;
import org.miradi.utils.StringUtilities;

abstract public class AbstractProjectLoader 
{
	protected AbstractProjectLoader(final UnicodeReader readerToUse, Project projectToUse) throws Exception
	{
		reader = readerToUse;
		project = projectToUse;
		
		bundleNameToBundleMap = new HashMap<String, ThreatRatingBundle>();
	}

	protected void load() throws Exception
	{
		prepareToLoad();
		
		boolean foundEnd = false;
		
		String fileHeaderLine = reader.readLine();
		validateHeaderLine(fileHeaderLine);
		
		while(true)
		{
			String line = reader.readLine();
			if(line == null)
				break;
			
			if (line.startsWith(AbstractMiradiProjectSaver.STOP_MARKER))
			{
				foundEnd = true;
				long lastModified = processStopLine(line);
				setLastModifiedTime(lastModified);
				continue;
			}
			else if(foundEnd)
			{
				throw new IOException("Project file is corrupted (data after end marker)");
			}

			processLine(line);
		}
		
		if(!foundEnd)
			throw new IOException("Project file is corrupted (no end marker found)");
	}

	protected long getLastModified() throws Exception
	{
		while(true)
		{
			String line = reader.readLine();
			if(line == null)
				break;

			if (line.startsWith(AbstractMiradiProjectSaver.STOP_MARKER))
			{
				long lastModified = processStopLine(line);
				return lastModified;
			}
		}

		return 0;
	}

	private void validateHeaderLine(String fileHeaderLine) throws Exception
	{
		if(fileHeaderLine == null || !fileHeaderLine.startsWith(AbstractMiradiProjectSaver.getBasicFileHeader()))
			throw new NotMiradiProjectFileException();
		
		final String WHITESPACE_REGEXP = "\\s+";
		String[] parts = fileHeaderLine.split(WHITESPACE_REGEXP);
		/*String baseFileHeader = parts[0];*/
		int lowVersion = Integer.parseInt(parts[1]);
		if(lowVersion > AbstractMiradiProjectSaver.VERSION_HIGH)
			throw new ProjectFileTooNewException(lowVersion, AbstractMiradiProjectSaver.VERSION_HIGH);
		int highVersion = Integer.parseInt(parts[1]);
		if(highVersion < AbstractMiradiProjectSaver.VERSION_LOW)
			throw new ProjectFileTooOldException(highVersion, AbstractMiradiProjectSaver.VERSION_LOW);
	}

	private void processLine(String line) throws Exception
	{
		if (line.startsWith(AbstractMiradiProjectSaver.UPDATE_PROJECT_VERSION_CODE))
			loadProjectVersionLine(line);

		else if (line.startsWith(AbstractMiradiProjectSaver.UPDATE_PROJECT_INFO_CODE))
			loadProjectInfoLine(line);
		
		else if (line.startsWith(AbstractMiradiProjectSaver.UPDATE_LAST_MODIFIED_TIME_CODE))
			loadLastModified(line);
		
		else if (line.startsWith(AbstractMiradiProjectSaver.CREATE_OBJECT_CODE))
			loadCreateObjectLine(line);
		
		else if (line.startsWith(AbstractMiradiProjectSaver.UPDATE_OBJECT_CODE))
			loadUpdateObjectline(line);
		
		else if (line.startsWith(AbstractMiradiProjectSaver.CREATE_SIMPLE_THREAT_RATING_BUNDLE_CODE))
			loadCreateSimpleThreatRatingLine(line);
		
		else if (line.startsWith(AbstractMiradiProjectSaver.UPDATE_SIMPLE_THREAT_RATING_BUNDLE_CODE))
			loadUpdateSimpleThreatRatingLine(line);
		
		else if (line.startsWith(AbstractMiradiProjectSaver.UPDATE_QUARANTINE_CODE))
			loadQuarantine(line);
		
		else if(line.startsWith(AbstractMiradiProjectSaver.UPDATE_EXCEPTIONS_CODE))
			loadExceptions(line);
		
		else
			throw new IOException("Unexpected action: " + line);
	}
	
	private long processStopLine(String stopLine) throws Exception
	{
		final String WHITESPACE_REGEXP = "\\s+";
		String[] parts = stopLine.split(WHITESPACE_REGEXP);
		if(parts.length < 2)
			return 0;
		
		/*String stopMarker = parts[0];*/
		String forComputers = parts[1];
		return Long.parseLong(forComputers);
	}

	private void loadExceptions(String line) throws Exception
	{
		String[] tagValue = parseTagValueLine(line);
		String tag = tagValue[0];
		String value = tagValue[1];
		if(!tag.equals(AbstractMiradiProjectSaver.EXCEPTIONS_DATA_TAG))
			throw new Exception("Unknown Exceptions field: " + tag);

		getProject().appendToExceptionLog(value);
	}
	
	private void loadQuarantine(String line) throws Exception
	{
		String[] tagValue = parseTagValueLine(line);
		String tag = tagValue[0];
		String value = tagValue[1];
		if(!tag.equals(AbstractMiradiProjectSaver.QUARANTINE_DATA_TAG))
			throw new Exception("Unknown Quarantine field: " + tag);

		getProject().appendToQuarantineFile(value);
	}

	private String[] parseTagValueLine(String line) throws Exception
	{
		final String tag = StringUtilities.substringBetween(line, AbstractMiradiProjectSaver.TAB, AbstractMiradiProjectSaver.EQUALS);
		final String value = StringUtilities.substringAfter(line, AbstractMiradiProjectSaver.EQUALS);;

		return new String[] {tag, value};
	}

	private void loadProjectVersionLine(String line)
	{
	}
	
	private void loadProjectInfoLine(final String line)
	{
		String[] splitLine = line.split(AbstractMiradiProjectSaver.TAB);
		String[] tagValue = splitLine[1].split(AbstractMiradiProjectSaver.EQUALS);
		String tag = tagValue[0];
		String value = tagValue[1];
		if (tag.equals(ProjectInfo.TAG_PROJECT_METADATA_ID))
			getProject().getProjectInfo().setMetadataId(new BaseId(value));
		if (tag.equals(ProjectInfo.TAG_HIGHEST_OBJECT_ID))
			getProject().getProjectInfo().getNormalIdAssigner().idTaken(new BaseId(value));
	}

	private void loadLastModified(String line)
	{
	}
	
	private void loadCreateSimpleThreatRatingLine(String line) throws Exception
	{
		StringTokenizer tokenizer = new StringTokenizer(line);
		/*String command =*/ tokenizer.nextToken();
		String threatIdTargetIdString = tokenizer.nextToken();
		String[] threatIdTargetIdParts = threatIdTargetIdString.split("-");
		FactorId threatId = new FactorId(Integer.parseInt(threatIdTargetIdParts[0]));
		FactorId targetId = new FactorId(Integer.parseInt(threatIdTargetIdParts[1]));
		ThreatRatingBundle bundle = new ThreatRatingBundle(threatId, targetId, BaseId.INVALID);
		bundleNameToBundleMap.put(threatIdTargetIdString, bundle);
		getProject().getSimpleThreatRatingFramework().saveBundle(bundle);
	}

	private void loadUpdateSimpleThreatRatingLine(String line) throws Exception
	{
		StringTokenizer tokenizer = new StringTokenizer(line);
		/*String command =*/ tokenizer.nextToken();
		String threatIdTargetIdString = tokenizer.nextToken();
		ThreatRatingBundle bundleToUpdate = bundleNameToBundleMap.get(threatIdTargetIdString);
		String tag = tokenizer.nextToken(EQUALS_DELIMITER_TAB_PREFIXED);
		String value = tokenizer.nextToken(EQUALS_DELIMITER_NEWLINE_POSTFIXED);
		if (tag.equals(ThreatRatingBundle.TAG_VALUES))
		{
			RatingValueSet ratings = new RatingValueSet();
			ratings.fillFrom(value);
			bundleToUpdate.setRating(ratings);
		}
		if (tag.equals(ThreatRatingBundle.TAG_DEFAULT_VALUE_ID))
		{
			bundleToUpdate.setDefaultValueId(new BaseId(Integer.parseInt(value)));
		}
	}

	private void loadCreateObjectLine(String line) throws Exception
	{
		ORef ref = extractRefFromLine(line);
		createObject(ref);
	}

	public static ORef extractRefFromLine(String line)
	{
		StringTokenizer tokenizer = new StringTokenizer(line);
		/*String command =*/ tokenizer.nextToken();
		String refString = tokenizer.nextToken();
		
		return extractRef(refString);
	}

	private void loadUpdateObjectline(String line) throws Exception
	{
		StringTokenizer tokenizer = new StringTokenizer(line);
		/*String command =*/ tokenizer.nextToken();
		String refString = tokenizer.nextToken();
		ORef ref = extractRef(refString);
		String tag = tokenizer.nextToken(EQUALS_DELIMITER_TAB_PREFIXED);
		final boolean hasData = tokenizer.hasMoreTokens();
		if (hasData)
		{
			String value = StringUtilities.substringAfter(line, EQUALS_DELIMITER);
			updateObjectWithData(ref, tag, value);
		}
	}

	public static ORef extractRef(String refString)
	{
		String[] refParts = refString.split(":");
		int objectType = Integer.parseInt(refParts[0]);
		BaseId objectId = new BaseId(Integer.parseInt(refParts[1]));
		
		return new ORef(objectType, objectId);
	}

	protected Project getProject()
	{
		return project;
	}
	
	public static class NotMiradiProjectFileException extends Exception
	{
	}
	
	public static class ProjectFileTooNewException extends Exception
	{
		public ProjectFileTooNewException(int thisVersionToUse, int highestAllowedVersionToUse)
		{
			thisVersion = thisVersionToUse;
			highestAllowedVersion = highestAllowedVersionToUse;
		}
		
		public int getThisVersion()
		{
			return thisVersion;
		}
		
		public int highestMaxAllowedVersion()
		{
			return highestAllowedVersion;
		}

		private int thisVersion;
		private int highestAllowedVersion;
	}
	
	public static class ProjectFileTooOldException extends Exception
	{
		public ProjectFileTooOldException(int thisVersionToUse, int lowestAllowedVersionToUse)
		{
			thisVersion = thisVersionToUse;
			lowestAllowedVersion = lowestAllowedVersionToUse;
		}
		
		public int getThisVersion()
		{
			return thisVersion;
		}
		
		public int getLowestAllowedVersion()
		{
			return lowestAllowedVersion;
		}

		private int thisVersion;
		private int lowestAllowedVersion;
	}
	
	protected void setLastModifiedTime(long lastModified)
	{
	}
	
	abstract protected void updateObjectWithData(ORef ref, String tag, String value)	throws Exception;
	
	abstract protected void createObject(ORef ref) throws Exception;
	
	abstract protected void prepareToLoad() throws Exception;

	private HashMap<String, ThreatRatingBundle> bundleNameToBundleMap;
	private UnicodeReader reader;
	private Project project;
	
	public static final String EQUALS_DELIMITER_TAB_PREFIXED = " \t=";
	private static final String EQUALS_DELIMITER_NEWLINE_POSTFIXED = "=\n";
	public static final String EQUALS_DELIMITER = "=";
}
