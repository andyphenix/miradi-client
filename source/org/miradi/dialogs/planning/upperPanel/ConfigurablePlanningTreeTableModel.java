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
package org.miradi.dialogs.planning.upperPanel;

import org.miradi.dialogs.planning.ConfigurableRowColumnProvider;
import org.miradi.dialogs.planning.treenodes.HiddenConfigurableProjectRootNode;
import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.main.EAM;
import org.miradi.project.Project;
import org.miradi.utils.CodeList;

public class ConfigurablePlanningTreeTableModel extends ExportablePlanningTreeTableModel
{
	public ConfigurablePlanningTreeTableModel(Project project) throws Exception
	{
		super(project, createConfigurablePlanningTreeRootNode(project), new ConfigurableRowColumnProvider(project), UNIQUE_TREE_TABLE_IDENTIFIER);
	}

	public static CodeList getVisibleRowCodes(Project projectToUse) throws Exception
	{
		return new ConfigurableRowColumnProvider(projectToUse).getRowCodesToShow();
	}
	
	private static TreeTableNode createConfigurablePlanningTreeRootNode(Project projectToUse) throws Exception
	{
		return new HiddenConfigurableProjectRootNode(projectToUse, getVisibleRowCodes(projectToUse));
	}

	@Override
	public CodeList getRowCodesToShow()
	{
		try
		{
			return getVisibleRowCodes(getProject());
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return new CodeList();
		}
	}
	
	@Override
	public String getUniqueTreeTableModelIdentifier()
	{
		return UNIQUE_TREE_TABLE_IDENTIFIER;
	}
	
	private static final String UNIQUE_TREE_TABLE_IDENTIFIER = "ConfigurablePlanningTreeTableModel";
}
