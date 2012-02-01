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
package org.miradi.dialogs.summary;

import org.miradi.dialogs.base.ObjectPoolTable;
import org.miradi.dialogs.base.ObjectPoolTableModel;
import org.miradi.main.MainWindow;
import org.miradi.questions.ResourceRoleQuestion;
import org.miradi.utils.CodeList;

public class TeamPoolTable extends ObjectPoolTable
{
	public TeamPoolTable(MainWindow mainWindowToUse, ObjectPoolTableModel modelToUse)
	{
		super(mainWindowToUse, modelToUse);
	}
	
	@Override
	protected CodeList getCodesToDisable()
	{
		return new CodeList(new String[] {ResourceRoleQuestion.TEAM_MEMBER_ROLE_CODE});
	}
}
