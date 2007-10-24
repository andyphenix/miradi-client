/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning.propertiesPanel;

import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.ColumnTagProvider;

abstract public class PlanningViewAbstractTotalsTableModel extends PlanningViewAbstractAssignmentTabelModel implements ColumnTagProvider
{
	public PlanningViewAbstractTotalsTableModel(Project projectToUse)
	{
		super(projectToUse);
	}
	
	public int getColumnCount()
	{
		return 1;
	}
	
	public String getColumnTag(int column)
	{
		return getColumnName(column);
	}
}
