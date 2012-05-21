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
package org.miradi.objectpools;

import java.util.Arrays;

import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdAssigner;
import org.miradi.objecthelpers.BaseObjectByFullNameSorter;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Target;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.BaseObjectSchema;

public class TargetPool extends AbstractTargetPool
{

	public TargetPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.TARGET);
	}
	
	public void put(Target target) throws Exception
	{
		put(target.getId(), target);
	}
	
	public Target find(BaseId id)
	{
		return (Target)getRawObject(id);
	}
	
	@Override
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId) throws Exception
	{
		return new Target(objectManager ,new FactorId(actualId.asInt()));
	}
	
	public Target[] getSortedTargets()
	{
		Target[] targets = getValues().toArray(new Target[0]);
		Arrays.sort(targets, new BaseObjectByFullNameSorter());
		return targets;
	}
	
	@Override
	public BaseObjectSchema createBaseObjectSchema(Project projectToUse)
	{
		return Target.createSchema();
	}
}
