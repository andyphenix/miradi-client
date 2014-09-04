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
package org.miradi.utils;

import org.miradi.main.TestCaseWithProject;
import org.miradi.project.Project;

public class TestColumnSequenceSaver extends TestCaseWithProject
{
	public TestColumnSequenceSaver(String name)
	{
		super(name);
	}
	
	public void testRestoreColumnSequence() throws Exception
	{
		CodeList storedCodeList = new CodeList(new String[]{"Item","Viability Mode","Status","Type","Poor","Fair","Good","Very Good","Source","Progress"});
		CodeList defaultCodeList = new CodeList(new String[]{"Status", "Lebensf\u00e4higkeits Modus", "Posten","Typ","Mangelhaft","Ausreichend","Gut","Sehr gut","Quelle","Fortschritt"});
		ColumnSequenceSaver columnSequenceSaver = new ColumnSequenceSaver(getProject(), null, getName());
		CodeList calculatedList1 = columnSequenceSaver.calculateColumnCodesToRestore(storedCodeList, defaultCodeList);
		assertEquals("Didn't use default sequence when columns added?", defaultCodeList, calculatedList1);

		CodeList calculatedList2 = columnSequenceSaver.calculateColumnCodesToRestore(defaultCodeList, storedCodeList);
		assertNotEquals("Used default sequence when columns added?", defaultCodeList, calculatedList2);
	}
	
	public void testCalculateDesiredSequenceCodes()
	{
		CodeList storedCodeList = new CodeList(new String[]{"A", "C", "B", });
		CodeList currentCodeList = new CodeList(new String[]{"B", "C", "D", "D","A"});
		ColumnSequenceSaver columnSequenceSaver = new ColumnSequenceSaver(getProject(), null, getName());
		CodeList desiredList = columnSequenceSaver.calculateColumnCodesToRestore(storedCodeList, currentCodeList);
		
		CodeList expectedCodeList = new CodeList(new String[]{"A","C","B","D"}); 
		assertEquals("new sequence list was calculated incorrectly?", expectedCodeList, desiredList);
	}

	public void testRestoreColumnSequences() throws Exception
	{
		CodeList expectedSequenceAfterMove0 = new CodeList(new String[]{"D", "D", "C", "B", "A"});
		CodeList currentCodeList = new CodeList(new String[]{"B", "C", "D", "D","A"});
		verifyRestoreSequenceChange(expectedSequenceAfterMove0, new CodeList(new String[]{"D", "C", "B", }), currentCodeList);
		
		CodeList expectedSequenceAfterMove1 = new CodeList(new String[]{"A","C","B","D","D"});
		verifyRestoreSequenceChange(expectedSequenceAfterMove1, new CodeList(new String[]{"A", "C", "B", }), currentCodeList);
		
		CodeList expectedSequenceAfterMove2 = new CodeList(new String[]{"A","D","D","C","B"});
		verifyRestoreSequenceChange(expectedSequenceAfterMove2, new CodeList(new String[]{"A", "D", "C", "B", }), currentCodeList);
	}

	private void verifyRestoreSequenceChange(CodeList expectedSequenceAfterMove, CodeList storedCodeList, CodeList currentCodeList) throws Exception
	{
		ColumnSequenceSaverForTesting columnSequenceSaver = new ColumnSequenceSaverForTesting(getProject(), storedCodeList , currentCodeList);
		columnSequenceSaver.restoreColumnSequences();
		CodeList afterMoveSequence = columnSequenceSaver.getDisplayColumnCodes();
				
		assertEquals("incorrect move?", expectedSequenceAfterMove, afterMoveSequence);
	}
	
	class ColumnSequenceSaverForTesting extends ColumnSequenceSaver
	{
		public ColumnSequenceSaverForTesting(Project projectToUse, CodeList storedCodeListToUse, CodeList currentCodeListToUse)
		{
			super(projectToUse, null, getName());
			
			storedCodeList = storedCodeListToUse;
			currentCodeList = currentCodeListToUse;
		}

		@Override
		protected String getColumnGroupCode(int tableColumn)
		{
			return currentCodeList.get(tableColumn);
		}

		@Override
		protected int getTableColumnCount()
		{
			return currentCodeList.size();
		}
		
		@Override
		protected CodeList getDisplayColumnCodes()
		{
			return new CodeList(currentCodeList);
		}
		
		@Override
		protected CodeList getSavedColumnCodes()
		{
			return new CodeList(storedCodeList);
		}
		
		@Override
		protected void moveColumn(int fromTableColumn, int destination)
		{
			String codeToMoveToDestination = currentCodeList.get(fromTableColumn);
			currentCodeList.removeCodeAt(fromTableColumn);
			currentCodeList.insertElementAt(codeToMoveToDestination, destination);
		}
		
		private CodeList storedCodeList;
		private CodeList currentCodeList;
	}	
}
