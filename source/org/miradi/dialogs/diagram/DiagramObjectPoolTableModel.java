/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

package org.miradi.dialogs.diagram;

import org.miradi.dialogs.base.ObjectPoolTableModel;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.project.Project;

abstract public class DiagramObjectPoolTableModel extends ObjectPoolTableModel
{
	public DiagramObjectPoolTableModel(Project projectToUse, int listedItemType)
	{
		super(projectToUse, listedItemType, getTags());
	}
	
	@Override
	public boolean isPseudoFieldColumn(int column)
	{
		String columnTag = getColumnTag(column);
		if (columnTag.equals(ConceptualModelDiagram.PSEUDO_COMBINED_LABEL))
			return true;

		return super.isPseudoFieldColumn(column);
	}
	
	private static String[] getTags()
	{
		return new String[] {ConceptualModelDiagram.PSEUDO_COMBINED_LABEL};
	}
}
