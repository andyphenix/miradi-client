/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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

import java.util.Vector;

import org.miradi.main.EAM;
import org.miradi.utils.StringUtilities;

public class TaxonomyFileLoader extends TwoLevelFileLoader
{
	public TaxonomyFileLoader(String fileNameToUse)
	{
		super(fileNameToUse);
	}

	@Override
	protected Vector<TwoLevelEntry> processVector(Vector<Vector<String>> fileVector)
	{
		Vector<TwoLevelEntry> taxonomyItems = new Vector<TwoLevelEntry>();
		taxonomyItems.add(new TwoLevelEntry("", EAM.text("Not Specified")));

		String prevLevel1Code = "";
		int level1Index = 0;
		int level2Index = 0;
		for(int i = 0; i < fileVector.size(); ++i)
		{
			Vector row = fileVector.get(i);
			if(row.size() < 6)
				throw new RuntimeException("Not enough elements in: " + row);
			String code = (String) row.get(0);
			String level1Descriptor = (String) row.get(1);
			String level2Descriptor = (String) row.get(2);
			String longDescription = (String) row.get(3) + StringUtilities.NEW_LINE + (String) row.get(4) + StringUtilities.NEW_LINE + (String) row.get(5);

			if(!getLevel1Code(code).equals(prevLevel1Code))
			{
				level2Index = 0;
				String taxonomyLevelText = ++level1Index + "   "+ level1Descriptor;
				taxonomyItems.add(new TwoLevelEntry(getLevel1Code(code), taxonomyLevelText, "", longDescription));
			}
			
			++level2Index;
			String taxonomyLevel2Text = "    " + level1Index + "." + level2Index + "    " + level2Descriptor;
			TwoLevelEntry entry = new TwoLevelEntry(code, taxonomyLevel2Text, "", longDescription);
			taxonomyItems.add(entry);

			prevLevel1Code = getLevel1Code(code);
		}
		return taxonomyItems;
	}

	private String getLevel1Code(String code)
	{
		return code.substring(0, code.indexOf("."));
	}

	@Override
	protected TwoLevelEntry createEntry(Vector row)
	{
		throw new RuntimeException("TaxonomyFileLoader overrides processVector and does not call super.processVector");
	}
}
