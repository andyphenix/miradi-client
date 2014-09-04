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
package org.miradi.dialogs.summary;

import org.miradi.actions.ActionDeleteTeamMember;
import org.miradi.actions.ActionTeamCreateMember;
import org.miradi.dialogs.base.ObjectPoolTablePanel;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.MainWindow;
import org.miradi.objects.ProjectResource;
import org.miradi.schemas.ProjectResourceSchema;

public class TeamPoolTablePanel extends ObjectPoolTablePanel
{
	public TeamPoolTablePanel(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, new TeamPoolTable(mainWindowToUse, new TeamPoolTableModel(mainWindowToUse.getProject())));
		
		addUnknownTypeOfButton(ActionTeamCreateMember.class);
		addUnknownTypeOfButton(ActionDeleteTeamMember.class);
	}
	
	@Override
	public void handleCommandEventImmediately(CommandExecutedEvent event)
	{
		super.handleCommandEventImmediately(event);
		if (event.isSetDataCommandWithThisTypeAndTag(ProjectResourceSchema.getObjectType(), ProjectResource.TAG_ROLE_CODES))
			getTable().getObjectTableModel().rowsWereAddedOrRemoved();
	}
}
