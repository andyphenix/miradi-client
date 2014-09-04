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
package org.miradi.dialogs.threatrating.upperPanel;

import java.util.Comparator;

import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Factor;
import org.miradi.project.Project;
import org.miradi.utils.Translation;

public class ThreatSummaryColumnTableModel extends AbstractThreatPerRowTableModel
{
	public ThreatSummaryColumnTableModel(Project projectToUse)
	{
		super(projectToUse);
	}

	@Override
	public String getUniqueTableModelIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}

	@Override
	public String getColumnGroupCode(int column)
	{
		return getColumnName(column);
	}
	
	@Override
	public String getColumnName(int column)
	{
		return getThreatSummartRatingLabel();
	}

	public static String getThreatSummartRatingLabel()
	{
		return EAM.text("Summary Threat Rating");
	}
	
	public int getColumnCount()
	{
		return 1;
	}

	public Object getValueAt(int row, int column)
	{
		String valueToConvert = getCalculatedThreatSummaryRatingValue(row);
		return convertThreatRatingCodeToChoiceItem(valueToConvert);
	}
	
	private String getCalculatedThreatSummaryRatingValue(int row)
	{
		try
		{
			int calculatedValue = calculateThreatSummaryRatingValue(getDirectThreat(row));
			return convertIntToString(calculatedValue);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return Translation.getCellTextWhenException();
		}
	}
	
	public int calculateThreatSummaryRatingValue(Factor directThreat) throws Exception
	{
		return getFramework().get2PrimeSummaryRatingValue(directThreat);
	}
	
	@Override
	public Comparator<ORef> createComparator(int columnToSortOn)
	{
		return new TableModelChoiceItemComparator(this, columnToSortOn, getThreatRatingQuestion());
	}

	private static final String UNIQUE_IDENTIFIER = "ThreatSummaryColumnTableModel";
}
