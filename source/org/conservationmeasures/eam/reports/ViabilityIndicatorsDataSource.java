/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.reports;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.KeyEcologicalAttribute;
import org.conservationmeasures.eam.project.Project;

public class ViabilityIndicatorsDataSource extends CommonDataSource
{
	public ViabilityIndicatorsDataSource(KeyEcologicalAttribute kea)
	{
		super();
		project = kea.getObjectManager().getProject();
		list = new ORefList(Indicator.getObjectType(), kea.getIndicatorIds());
		rowCount = list.size();
	}

	public JRDataSource getViabilityGoalsDataSourceDataSource()
	{
		return new ViabilityGoalsDataSource(currentIndicator);
	}
	
	public Object getFieldValue(JRField field)
	{
		return currentIndicator.getData(field.getName());
	}

	public boolean next() throws JRException 
	{
		if (super.next())
		{
			currentIndicator = (Indicator)project.findObject(list.get(rowCount));
			return true;
		}
		return false;
	}

	ORefList list;
	Indicator currentIndicator;
	Project project;
} 
