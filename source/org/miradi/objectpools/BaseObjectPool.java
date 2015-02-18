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
package org.miradi.objectpools;

import org.miradi.ids.BaseId;
import org.miradi.ids.IdAssigner;
import org.miradi.objects.BaseObject;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.schemas.BaseObjectSchema;

abstract public class BaseObjectPool extends PoolWithIdAssigner
{
	public BaseObjectPool(IdAssigner idAssignerToUse, int objectTypeToStore)
	{
		super(objectTypeToStore, idAssignerToUse);
	}
	
	public BaseObject createObject(ObjectManager objectManager, BaseId objectId) throws Exception
	{
		BaseId actualId = idAssigner.obtainRealId(objectId);
		BaseObject created = createRawObject(objectManager, actualId);
		put(created.getId(), created);
		
		return created;
	}
	
	protected ChoiceQuestion getQuestion(final Class questionClass)
	{
		return StaticQuestionManager.getQuestion(questionClass);
	}
	
	abstract BaseObject createRawObject(ObjectManager objectManager, BaseId actualId) throws Exception;
	
	abstract public BaseObjectSchema createBaseObjectSchema(final Project projectToUse);
}
