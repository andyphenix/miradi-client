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
package org.miradi.dialogs.treeRelevancy;

import javax.swing.JPanel;

import org.miradi.objects.Objective;
import org.miradi.project.Project;

public class StrategyActivityRelevancyTreeTablePanel extends JPanel
{
	public StrategyActivityRelevancyTreeTablePanel(Project project, Objective objective) throws Exception
	{
		//FIXME temporarly disabled - deprioritized
		//RootTreeTableNode rootNode = new RootTreeTableNode(project, objective.getRelevantStrategyRefList());
		//StrategyActivityTreeTableModel model = new StrategyActivityTreeTableModel(rootNode); 
		//StrategyActivityRelevancyTreeTable treeTable = new StrategyActivityRelevancyTreeTable(project, model);	
	}
}
