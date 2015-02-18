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
package org.miradi.questions;

import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;

public class ObjectPoolChoiceQuestion extends ObjectQuestion
{
	public ObjectPoolChoiceQuestion(Project projectToUse, int typeToUse)
	{
		super(getAllObjects(projectToUse, typeToUse));
		
		project = projectToUse;
		type = typeToUse;
	}
	
	private static BaseObject[] getAllObjects(Project project, int type)
	{
		ORefList refList = project.getPool(type).getRefList();
		BaseObject[] objectList = new BaseObject[refList.size()];
		for (int i = 0; i < refList.size(); ++i)
		{
			objectList[i] = project.findObject(refList.get(i));
		}
		
		return objectList;
	}
	
	@Override
	public void reloadQuestion()
	{
		super.reloadQuestion();
		
		setObjects(getAllObjects(project, type));
	}
	
	protected Project getProject()
	{
		return project;
	}
	
	private Project project;
	private int type;
}
