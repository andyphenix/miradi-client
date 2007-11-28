/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.threatstressrating.upperPanel;


public class ThreatSummaryColumnTable extends TableWithSetPreferredScrollableViewportHeight
{
	public ThreatSummaryColumnTable(ThreatSummaryColumnTableModel model)
	{
		super(model);
		setForcedPreferredScrollableViewportWidth(100);
	}
	
	public String getUniqueTableIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}

	public static final String UNIQUE_IDENTIFIER = "ThreatSummaryColumnTable";	
}
