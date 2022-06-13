/* 
Copyright 2005-2022, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

public class TaxonomyTwoLevelFileLoader extends TwoLevelFileLoader
{
	public TaxonomyTwoLevelFileLoader(String fileNameToUse)
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
			if(row.size() < columnCount)
				throw new RuntimeException("Not enough elements in: " + row);

			String code = (String) row.get(indexCode);
			String level1Descriptor = (String) row.get(indexLevel1);
			String level2Descriptor = (String) row.get(indexLevel2);
			String longDescription = buildLongDescription(row);

			if(!getLevel1Code(code).equals(prevLevel1Code))
			{
				level2Index = 0;
				prevLevel1Code = getLevel1Code(code);

				String taxonomyLevelText = ++level1Index + " " + level1Descriptor;
				taxonomyItems.add(new TwoLevelEntry(getLevel1Code(code), taxonomyLevelText, "", longDescription, 1, ""));
			}
			
			++level2Index;
			String taxonomyLevel2Text = " " + level1Index + "." + level2Index + " " + level2Descriptor;
			TwoLevelEntry entry = new TwoLevelEntry(code, taxonomyLevel2Text, "", longDescription, 2, prevLevel1Code);
			taxonomyItems.add(entry);
		}
		return taxonomyItems;
	}

	private String getLevel1Code(String code)
	{
		return code.substring(0, code.indexOf("."));
	}

	private String buildLongDescription(Vector row)
	{
		return row.get(indexLevel1Def) + StringUtilities.NEW_LINE +
				row.get(indexLevel2Def) + StringUtilities.NEW_LINE +
				EAM.text("Examples") + ": " + row.get(indexExamples);
	}

	@Override
	protected TwoLevelEntry createEntry(Vector row)
	{
		throw new RuntimeException("TaxonomyTwoLevelFileLoader overrides processVector and does not call super.processVector");
	}

	private final int indexCode = 0;
	private final int indexLevel1 = 1;
	private final int indexLevel2 = 2;
	private final int indexLevel1Def = 3;
	private final int indexLevel2Def = 4;
	private final int indexExamples = 5;
	private final int columnCount = 6;
}
