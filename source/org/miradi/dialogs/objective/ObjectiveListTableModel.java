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
package org.miradi.dialogs.objective;

import org.miradi.dialogs.base.ObjectListTableModel;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Factor;
import org.miradi.objects.Objective;
import org.miradi.project.Project;

public class ObjectiveListTableModel extends ObjectListTableModel
{
	public ObjectiveListTableModel(Project projectToUse, ORef nodeRef)
	{
		super(projectToUse, nodeRef, Factor.TAG_OBJECTIVE_IDS, ObjectType.OBJECTIVE, getColumnTags());
	}

	private static String[] getColumnTags()
	{
		return new String[] {
			Objective.TAG_SHORT_LABEL,
			Objective.TAG_LABEL,
			Objective.TAG_FULL_TEXT,
		};
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return UNIQUE_MODEL_IDENTIFIER;
	}
	
	private static final String UNIQUE_MODEL_IDENTIFIER = "ObjectiveListTableModel";
}
