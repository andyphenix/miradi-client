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

package org.miradi.dialogs.tablerenderers;

import org.miradi.dialogfields.WhoCodeListEditorComponent;
import org.miradi.dialogs.base.DisposablePanel;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objects.BaseObject;
import org.miradi.questions.ProjectResourceQuestion;

public class WhoColumnTableCellEditorFactory extends AbstractPopupTableCellEditorFactory
{
	public WhoColumnTableCellEditorFactory(MainWindow mainWindowToUse, RowColumnSelectionProvider tableToUse)
	{
		super(mainWindowToUse, tableToUse);
	}	
	
	@Override
	protected DisposablePanel createEditorComponenet(BaseObject baseObjectForRow)
	{
		final ProjectResourceQuestion question = new ProjectResourceQuestion(getProject());

		return new WhoCodeListEditorComponent(baseObjectForRow, question);
	}
	
	@Override
	protected String getDialogTitle()
	{
		return EAM.text("Project Resource");
	}
}
