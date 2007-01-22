/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.threatmatrix;

import java.util.Comparator;

import org.conservationmeasures.eam.objects.ViewData;


public class BundleColumnSortHandler extends ColumnSortHandler
{
	public BundleColumnSortHandler(ThreatGridPanel threatGirdPanelInUse)
	{
		super(threatGirdPanelInUse);
		sortToggle = false;
	}


	public void saveState(int sortColumn)
	{
		if(isSummaryColumn(sortColumn, mainTableModel))
		{
			saveSortState(sortToggle, ViewData.SORT_SUMMARY);
			return;
		}
		String columnBaseIdToSort = mainTableModel.getTargets()[sortColumn].getId().toString();
		saveSortState(sortToggle, columnBaseIdToSort);
	}

	
	public Comparator getComparator(int sortColumn)
	{
		if(isSummaryColumn(sortColumn, mainTableModel))
			return new SummaryColumnComparator(mainTableModel);
		return new FactorComparator(sortColumn,mainTableModel);
	}

	
	private boolean isSummaryColumn(int sortColumn, NonEditableThreatMatrixTableModel modelToSort) 
	{
		return (sortColumn == threatGirdPanel.getThreatMatrixTable().getSummaryColumn());
	}
	
	
	public void setToggle(boolean sortOrder) 
	{
		sortToggle = !sortOrder;
	}
	
	
	public  boolean getToggle() 
	{
		sortToggle = !sortToggle;
		return sortToggle;
	}
	
	
	boolean sortToggle;

}
