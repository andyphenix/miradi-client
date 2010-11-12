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
package org.miradi.objecthelpers;

import java.io.Reader;
import java.net.URL;
import java.util.Vector;

import org.martus.util.UnicodeReader;
import org.miradi.main.ResourcesHandler;
import org.miradi.utils.DelimitedFileLoader;
import org.miradi.utils.Translation;

abstract public class TwoLevelFileLoader extends DelimitedFileLoader
{
	public TwoLevelFileLoader(String fileNameToUse)
	{
		fileName = fileNameToUse;
	}
	
	public TwoLevelEntry[] load() throws Exception
	{
		URL english = ResourcesHandler.getEnglishResourceURL(getPath());
		Reader reader = new UnicodeReader(english.openStream());
		try
		{
			TwoLevelEntry[] table = load(reader);
			return table;
		}
		finally
		{
			reader.close();
		}
	}

	public TwoLevelEntry[] load(Reader reader) throws Exception
	{
		Vector<Vector<String>> fileVector = getDelimitedContents(reader);
		Vector<TwoLevelEntry> taxonomyItems = processVector(fileVector);
		return taxonomyItems.toArray(new TwoLevelEntry[0]);
	}
	
	@Override
	protected String translateLine(String line)
	{
		final String prefix = "choice|" + getPath() + "|";
		return Translation.translateTabDelimited(prefix, line);
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	private String getPath()
	{
		String path = ResourcesHandler.RESOURCES_PATH + "fieldoptions/" + getFileName();
		return path;
	}
	
	//FIXME medium - there is a lot of duplication in the overriding processVector methods.
	//The only real difference is the index the code name and description within a line tht is read.
	//Maybe have each subclass return the index for code, name, description, or a map.  
	abstract protected Vector<TwoLevelEntry> processVector(Vector<Vector<String>> fileVector);

	private String fileName;
	public final static String COUNTRIES_FILE = "Countries.tsv";
	public final static String WWF_ECO_REGIONS_FILE = "EcoRegions.tsv";
	public final static String STRATEGY_TAXONOMIES_FILE = "StrategyTaxonomies.tsv";
	public final static String THREAT_TAXONOMIES_FILE = "ThreatTaxonomies.tsv";
	
	public final static String TNC_FRESHWATER_ECO_REGION_FILE = "TncFreshwaterEcoRegions.tsv";
	public final static String TNC_MARINE_ECO_REGION_FILE = "TncMarineEcoRegions.tsv";
	public final static String TNC_OPERATING_UNITS_FILE = "TncOperatingUnits.tsv";
	public final static String TNC_TERRESTRIAL_ECO_REGION_FILE = "TncTerrestrialEcoRegions.tsv";

	public final static String WWF_LINK_TO_GLOBAL_TARGETS = "WwfLinkToGlobalTargets.tsv";
	public final static String WWF_MANAGING_OFFICES_FILE = "WwfManagingOffices.tsv";
	public final static String WWF_REGIONS_FILE = "WwfRegions.tsv";
}
