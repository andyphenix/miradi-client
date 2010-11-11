/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogfields;

import javax.swing.JToggleButton;

import org.miradi.layout.TwoColumnPanel;
import org.miradi.main.MainWindow;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceItemWithRowDescriptionProvider;
import org.miradi.questions.ChoiceQuestion;

public class SingleLevelQuestionEditor extends QuestionEditorWithHierarchichalRows
{
	public SingleLevelQuestionEditor(MainWindow mainWindowToUse, ChoiceQuestion questionToUse)
	{
		super(mainWindowToUse, questionToUse);
	}
	
	@Override
	protected void createRowPanel(TwoColumnPanel mainRowsPanel,	JToggleButton toggleButton, ChoiceItem choiceItem) throws Exception
	{
		createSelectableRow(mainRowsPanel, toggleButton, (ChoiceItemWithRowDescriptionProvider) choiceItem, 0, getRawFont());
	}
}
