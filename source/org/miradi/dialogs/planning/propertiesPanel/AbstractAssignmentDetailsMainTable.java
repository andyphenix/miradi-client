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
package org.miradi.dialogs.planning.propertiesPanel;

import java.awt.Color;

import org.miradi.main.AppPreferences;
import org.miradi.main.MainWindow;
import org.miradi.objects.AccountingCode;
import org.miradi.objects.BaseObject;
import org.miradi.objects.CategoryOne;
import org.miradi.objects.CategoryTwo;
import org.miradi.objects.FundingSource;

public class AbstractAssignmentDetailsMainTable extends AbstractComponentTable
{
	public AbstractAssignmentDetailsMainTable(MainWindow mainWindowToUse, AbstractSummaryTableModel modelToUse, String uniqueIdentifier) throws Exception
	{
		super(mainWindowToUse, modelToUse, uniqueIdentifier);
		
		setBackground(getColumnBackGroundColor(0));
		rebuildColumnEditorsAndRenderers();
	}
	
	@Override
	public Color getColumnBackGroundColor(int column)
	{
		return AppPreferences.RESOURCE_TABLE_BACKGROUND;
	}

	@Override
	public void rebuildColumnEditorsAndRenderers() throws Exception
	{
		for (int tableColumn = 0; tableColumn < getColumnCount(); ++tableColumn)
		{
			createFundingSourceColumn(tableColumn);
			createAccountingCodeColumn(tableColumn);
			createCategoryOneColumn(tableColumn);
			createCategoryTwoColumn(tableColumn);
		}
	}
	
	private void createAccountingCodeColumn(int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		if (! getAbstractSummaryTableModel().isAccountingCodeColumn(modelColumn))
			return;
		
		createComboColumnWithInvalidObject(tableColumn, AccountingCode.getObjectType());
	}
	
	private void createFundingSourceColumn(int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		if (! getAbstractSummaryTableModel().isFundingSourceColumn(modelColumn))
			return;

		createComboColumnWithInvalidObject(tableColumn, FundingSource.getObjectType());
	}
	
	private void createCategoryOneColumn(int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		if (! getAbstractSummaryTableModel().isCategoryOneColumn(modelColumn))
			return;

		createComboColumnWithInvalidObject(tableColumn, CategoryOne.getObjectType());
	}

	private void createCategoryTwoColumn(int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		if (! getAbstractSummaryTableModel().isCategoryTwoColumn(modelColumn))
			return;

		createComboColumnWithInvalidObject(tableColumn, CategoryTwo.getObjectType());
	}
	
	private void createComboColumnWithInvalidObject(int tableColumn, int objectType)
	{
		BaseObject[] baseObjects = getObjectManager().getPool(objectType).getAllObjectsAsArray();
		BaseObject invalidObject = ResourceAssignmentMainTableModel.createInvalidObject(getObjectManager(), objectType);
		createComboColumn(baseObjects, tableColumn, invalidObject);
	}
	
	protected AbstractSummaryTableModel getAbstractSummaryTableModel()
	{
		return (AbstractSummaryTableModel) getModel();
	}
	
	@Override
	public boolean shouldSaveColumnSequence()
	{
		return false;
	}

	@Override
	public boolean shouldSaveColumnWidth()
	{
		return false;
	}
}
