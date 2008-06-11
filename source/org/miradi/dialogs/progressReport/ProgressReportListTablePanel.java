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
package org.miradi.dialogs.progressReport;

import org.miradi.actions.Actions;
import org.miradi.dialogs.base.ObjectListTablePanel;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.Project;

public class ProgressReportListTablePanel extends ObjectListTablePanel
{
	public ProgressReportListTablePanel(Project projectToUse, Actions actions, ORef nodeRef, String annotationTag, Class[] editButtonClasses)
	{
		super(projectToUse, new ProgressReportListTableModel(projectToUse, nodeRef, annotationTag), actions, editButtonClasses, DEFAULT_SORT_COLUMN);
	}
	
	private static final int DEFAULT_SORT_COLUMN = 0;
}
