/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.dialogs.planning.propertiesPanel;

import java.awt.Color;

import javax.swing.JLabel;

import org.miradi.ids.BaseId;
import org.miradi.main.AppPreferences;
import org.miradi.objectpools.ResourcePool;
import org.miradi.objects.AccountingCode;
import org.miradi.objects.FundingSource;
import org.miradi.objects.ProjectResource;

public class PlanningViewResourceTable extends PlanningViewAbstractTableWithPreferredScrollableViewportSize
{
	public PlanningViewResourceTable(PlanningViewResourceTableModel modelToUse)
	{
		super(modelToUse);
		model = modelToUse;
		
		setBackground(getColumnBackGroundColor(0));
		rebuildColumnEditorsAndRenderers();
	}
	
	public Color getColumnBackGroundColor(int column)
	{
		return AppPreferences.RESOURCE_TABLE_BACKGROUND;
	}

	public void rebuildColumnEditorsAndRenderers()
	{
		for (int tableColumn = 0; tableColumn < getColumnCount(); ++tableColumn)
		{
			createResourceCombo(tableColumn);
			createFundingSourceColumn(tableColumn);
			createAccountingCodeColumn(tableColumn);
		}
	}
	
	private void createAccountingCodeColumn(int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		if (! model.isAccountingCodeColumn(modelColumn))
			return;
		
		AccountingCode[] accountingCodes = getObjectManager().getAccountingCodePool().getAllAccountingCodes();
		AccountingCode invalidAccountingCode = new AccountingCode(getObjectManager(), BaseId.INVALID);
		createComboColumn(accountingCodes, tableColumn, invalidAccountingCode);
	}
	
	private void createResourceCombo(int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		if (! model.isResourceColumn(modelColumn))
			return;
		
		ProjectResource[] resources = getAllProjectResources();
		ProjectResource invalidResource = new ProjectResource(getObjectManager(), BaseId.INVALID);
		createComboColumn(resources, tableColumn, invalidResource);
	}
	
	private void createFundingSourceColumn(int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		if (! model.isFundingSourceColumn(modelColumn))
			return;

		FundingSource[] fundingSources = getObjectManager().getFundingSourcePool().getAllFundingSources();
		FundingSource invalidFundintSource = new FundingSource(getObjectManager(), BaseId.INVALID);
		createComboColumn(fundingSources, tableColumn, invalidFundintSource);
	}
	
	public int getColumnAlignment()
	{
		return JLabel.RIGHT;
	}
	
	public String getUniqueTableIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}
	
	public ProjectResource[] getAllProjectResources()
	{
		return  getResourcePool().getAllProjectResources();
	}
	
	public ResourcePool getResourcePool()
	{
		return getObjectManager().getResourcePool();
	}

	private PlanningViewResourceTableModel model;
    public static final String UNIQUE_IDENTIFIER = "PlanningViewResourceTable";
}
