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
package org.miradi.questions;

import java.util.Vector;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objectpools.ObjectTreeTableConfigurationPool;
import org.miradi.objects.ObjectTreeTableConfiguration;
import org.miradi.project.Project;

public class ObjectTreeTableConfigurationQuestion extends DynamicChoiceQuestion
{
	public ObjectTreeTableConfigurationQuestion(Project projectToUse)
	{
		super();
		
		project = projectToUse;
	}

	@Override
	public ChoiceItem[] getChoices()
	{
		ObjectTreeTableConfigurationPool configurationPool = (ObjectTreeTableConfigurationPool) getProject().getPool(ObjectTreeTableConfiguration.getObjectType());
		ORefList allConfigurationRefs = configurationPool.getORefList();

		Vector<ChoiceItem> allCustomizations = new Vector<ChoiceItem>();
		for (int i = 0; i < allConfigurationRefs.size(); ++i)
		{
			ChoiceItem choiceItem = createChoiceItem(getProject(), allConfigurationRefs.get(i));
			allCustomizations.add(choiceItem);
		}

		return allCustomizations.toArray(new ChoiceItem[0]);	
	}

	private static ChoiceItem createChoiceItem(Project project, ORef configurationRef)
	{
		return new ObjectChoiceItem(project, configurationRef);
	}
	
	public Project getProject()
	{
		return project;
	}
	
	private Project project;
}
