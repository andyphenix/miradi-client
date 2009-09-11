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
package org.miradi.dialogs.threatrating.upperPanel;

import java.util.Comparator;

import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ThreatTargetVirtualLinkHelper;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.project.Project;

public class TargetThreatLinkTableModel extends MainThreatTableModel
{
	public TargetThreatLinkTableModel(Project projectToUse)
	{
		super(projectToUse);
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}

	public int getColumnCount()
	{
		return targetColumns.length;
	}
	
	public String getColumnGroupCode(int column)
	{
		return targetColumns[column].getRef().toString();
	}

	public String getColumnName(int column)
	{
		return targetColumns[column].toString();
	}
	
	public Object getValueAt(int row, int column)
	{
		String valueToConvert = getFactorLinkThreatRatingBundle(row, column);
		return convertThreatRatingCodeToChoiceItem(valueToConvert);	
	}

	private String getFactorLinkThreatRatingBundle(int row, int column)
	{
		try
		{
			Cause threat = (Cause) getDirectThreat(row);
			ORef targetRef = getTarget(column).getRef();
			if (!ThreatTargetVirtualLinkHelper.canSupportThreatRatings(getProject(), threat, targetRef))
				return null;
			
			ThreatTargetVirtualLinkHelper threatTargetVirtualLink = new ThreatTargetVirtualLinkHelper(getProject());
			int calculatedValue = threatTargetVirtualLink.calculateThreatRatingBundleValue(threat.getRef(), targetRef);
			return convertIntToString(calculatedValue);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return EAM.text("Error");
		}
	}

	public String getColumnTag(int column)
	{
		return "";
	}

	public BaseObject getBaseObjectForRowColumn(int row, int column)
	{
		return null;
	}
	
	public Comparator getComparator(int columnToSortOn)
	{
		return new TableModelChoiceItemComparator(this, columnToSortOn);
	}

	private static final String UNIQUE_IDENTIFIER = "TargetThreatLinkTableModel";
}
