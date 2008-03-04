/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.dialogs.planning.upperPanel;

import java.text.DecimalFormat;
import java.util.Vector;

import org.miradi.dialogs.planning.propertiesPanel.PlanningViewAbstractTreeTableSyncedTableModel;
import org.miradi.dialogs.planning.treenodes.AbstractPlanningTreeNode;
import org.miradi.main.EAM;
import org.miradi.project.Project;
import org.miradi.project.ProjectCalendar;
import org.miradi.utils.DateRange;

import com.java.sun.jtreetable.TreeTableModelAdapter;

public class PlanningViewBudgetAnnualTotalTableModel extends PlanningViewAbstractTreeTableSyncedTableModel
{
	public PlanningViewBudgetAnnualTotalTableModel(Project projectToUse, TreeTableModelAdapter adapterToUse) throws Exception
	{
		super(projectToUse, adapterToUse);

		yearlyDateRanges = getProjectCalendar().getYearlyDateRanges();
		combinedDataRange = getProjectCalendar().combineStartToEndProjectRange();
		currencyFormatter = project.getCurrencyFormatterWithCommas();
	}

	private ProjectCalendar getProjectCalendar() throws Exception
	{
		return project.getProjectCalendar();
	}

	public String getColumnName(int column)
	{
		if(isGrandTotalColumn(column))
			return GRAND_TOTAL_COLUMN_NAME;
		
		try
		{
			return getProjectCalendar().getDateRangeName((DateRange)yearlyDateRanges.get(column));
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return EAM.text("(Error)");
		}
	}
		
	public int getColumnCount()
	{
		return yearlyDateRanges.size() + 1;
	}
	
	public Object getValueAt(int row, int column)
	{
		Object rawTreeNode = getNodeForRow(row);
		return getValueAt(rawTreeNode, column);
	}

	public Object getValueAt(Object rawNode, int column)
	{
		try
		{
			AbstractPlanningTreeNode node = (AbstractPlanningTreeNode)rawNode;
			if (node.getObject() == null)
				return "";
			
			if (isGrandTotalColumn(column))
				return getGrandTotalCost(node);
		
			return getYearlyTotalCost(node, column);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return EAM.text("[ERROR]");
		}
	}
	
	private Object getYearlyTotalCost(AbstractPlanningTreeNode node, int column) throws Exception
	{	
		return getBudgetCost(node, (DateRange)yearlyDateRanges.get(column));
	}

	private Object getGrandTotalCost(AbstractPlanningTreeNode node) throws Exception
	{
		return getBudgetCost(node, combinedDataRange);		
	}
	
	private Object getBudgetCost(AbstractPlanningTreeNode node, DateRange dateRange) throws Exception
	{
		double totalCost = node.getObject().getProportionalBudgetCost(dateRange);      
        if (totalCost == 0)
        	return "";
        
		return  currencyFormatter.format(totalCost);
	}

	private boolean isGrandTotalColumn(int column)
	{
		return column == getColumnCount() - 1;
	}
	
	private DecimalFormat currencyFormatter;
	
	private DateRange combinedDataRange;
	private Vector yearlyDateRanges;
	
	public static final String GRAND_TOTAL_COLUMN_NAME = EAM.text("Budget Total");
}
