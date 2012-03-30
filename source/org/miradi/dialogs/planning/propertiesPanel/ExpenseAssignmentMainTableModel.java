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

import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.AccountingCode;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.FundingSource;
import org.miradi.project.Project;
import org.miradi.schemas.AccountingCodeSchema;
import org.miradi.schemas.ExpenseAssignmentSchema;
import org.miradi.schemas.FundingSourceSchema;

public class ExpenseAssignmentMainTableModel extends AbstractSummaryTableModel
{
	public ExpenseAssignmentMainTableModel(Project projectToUse)
	{
		super(projectToUse);
	}
	
	@Override
	public String getColumnName(int column)
	{
		if (isExpenseNameColumn(column))
			return EAM.text("Name");
		
		return super.getColumnName(column);
	}
	
	@Override
	public int getColumnCount()
	{
		return COLUMN_COUNT;
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		return getCellValue(row, column);
	}
	
	@Override
	protected Object getCellValue(int row, int column)
	{
		if (isExpenseNameColumn(column))
			return getBaseObjectForRowColumn(row, column).getLabel();
		
		return super.getCellValue(row, column);
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		if (value == null)
			return;
		
		if (row < 0 || row >= getRowCount())
		{
			EAM.errorDialog(EAM.text("An error has occured while writing assignment data."));
			EAM.logWarning("Row out of bounds in PlanningViewResourceTableModel.setValueAt value = "+ value + " row = " + row + " column = " + column);
			return;
		}
		
		ORef assignmentRefForRow = getRefForRow(row);
		setExpenseNameCell(value, assignmentRefForRow, column);
		
		super.setValueAt(value, row, column);
	}
	
	private void setExpenseNameCell(Object value, ORef refForRow, int column)
	{
		if (! isExpenseNameColumn(column))
			return;

		String expenseName = value.toString();
		setValueUsingCommand(refForRow, ExpenseAssignment.TAG_LABEL, expenseName);
	}
	
	@Override
	public boolean isResourceColumn(int column)
	{
		return false;
	}
	
	public boolean isExpenseNameColumn(int column)
	{
		return getExpenseNameColumn() == column;
	}

	@Override
	public boolean isFundingSourceColumn(int column)
	{
		return getFundingSourceColumn() == column;
	}

	@Override
	public boolean isAccountingCodeColumn(int column)
	{
		return getAccountingCodeColumn() == column;
	}
	
	@Override
	public boolean isBudgetCategoryOneColumn(int column)
	{
		return CATEGORY_ONE_COLUMN == column;
	}
	
	@Override
	public boolean isBudgetCategoryTwoColumn(int column)
	{
		return CATEGORY_TWO_COLUMN == column;
	}

	private int getAccountingCodeColumn()
	{
		return ACCOUNTING_CODE_COLUMN;
	}
	
	private int getFundingSourceColumn()
	{
		return FUNDING_SOURCE_COLUMN;
	}
	
	private int getExpenseNameColumn()
	{
		return EXPENSE_NAME_COLUMN;
	}
	
	@Override
	protected String getListTag()
	{
		return BaseObject.TAG_EXPENSE_ASSIGNMENT_REFS;
	}

	@Override
	protected int getListType()
	{
		return ExpenseAssignmentSchema.getObjectType();
	}
	
	@Override
	protected BaseObject getFundingSource(BaseObject baseObjectToUse)
	{
		ExpenseAssignment expense = (ExpenseAssignment) baseObjectToUse;
		ORef fundingSourceRef = expense.getFundingSourceRef();
		if (fundingSourceRef.isInvalid())
			return createInvalidObject(getObjectManager(), FundingSourceSchema.getObjectType(), FundingSourceSchema.OBJECT_NAME);
		
		return FundingSource.find(getProject(), fundingSourceRef);
	}
	
	@Override
	protected BaseObject getAccountingCode(BaseObject baseObjectToUse)
	{
		ExpenseAssignment expense = (ExpenseAssignment) baseObjectToUse;
		ORef accountingCodeRef = expense.getAccountingCodeRef();
		if (accountingCodeRef.isInvalid())
			return createInvalidObject(getObjectManager(), AccountingCodeSchema.getObjectType(), AccountingCodeSchema.OBJECT_NAME);
		
		return AccountingCode.find(getProject(), accountingCodeRef);
	}

	@Override
	protected String getAccountingCodeTag()
	{
		return ExpenseAssignment.TAG_ACCOUNTING_CODE_REF;
	}
	
	@Override
	protected String getFundingSourceTag()
	{
		return ExpenseAssignment.TAG_FUNDING_SOURCE_REF;
	}
	
	@Override
	protected void setAccountingCode(ORef accountingCode, ORef assignmentRefForRow, int column)
	{
		setValueUsingCommand(assignmentRefForRow, getAccountingCodeTag(), accountingCode);
	}

	@Override
	protected void setFundingSource(ORef fundingSourceRef, ORef assignmentRefForRow, int column)
	{
		setValueUsingCommand(assignmentRefForRow, getFundingSourceTag(), fundingSourceRef);
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return UNIQUE_MODEL_IDENTIFIER;
	}
				
	private static final String UNIQUE_MODEL_IDENTIFIER = "ExpenseAssignmentMainTableModel";
	
	private static final int COLUMN_COUNT = 5;
	
	private static final int EXPENSE_NAME_COLUMN = 0;
	private static final int ACCOUNTING_CODE_COLUMN = 1;
	private static final int FUNDING_SOURCE_COLUMN = 2;
	private static final int CATEGORY_ONE_COLUMN = 3;
	private static final int CATEGORY_TWO_COLUMN = 4;
}
