/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.treetables;

import org.miradi.dialogs.planning.upperPanel.TreeTableModelWithRebuilder;
import org.miradi.dialogs.planning.upperPanel.rebuilder.AbstractTreeRebuilder;
import org.miradi.dialogs.planning.upperPanel.rebuilder.AnalysisTreeRebuilder;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.project.Project;

public class NewBudgetCategoryTreeModel extends TreeTableModelWithRebuilder
{
	public NewBudgetCategoryTreeModel(Project projectToUse,	TreeTableNode rootNode,	PlanningTreeRowColumnProvider rowColumnProvider, String uniqueTreeTableModelIdentifierToUse) throws Exception
	{
		super(projectToUse, rootNode, rowColumnProvider, uniqueTreeTableModelIdentifierToUse);
	}

	@Override
	protected AbstractTreeRebuilder createTreeRebuilder()
	{
		return new AnalysisTreeRebuilder(getProject(), getRowColumnProvider());
	}
}
