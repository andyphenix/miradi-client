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
import org.miradi.objects.Audience;
import org.miradi.objects.BaseObject;
import org.miradi.objects.OtherNotableSpecies;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.AudienceSchema;
import org.miradi.schemas.BaseObjectSchema;

public class AudiencePool extends BaseObjectPool
{
	public AudiencePool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, AudienceSchema.getObjectType());
	}
	
	public void put(OtherNotableSpecies audience) throws Exception
	{
		put(audience.getId(), audience);
	}
	
	public Audience find(BaseId id)
	{
		return (Audience) getRawObject(id);
	}
	
	@Override
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId)
	{
		return new Audience(objectManager, actualId);
	}
	
	@Override
	public BaseObjectSchema createBaseObjectSchema(Project projectToUse)
	{
		return Audience.createSchema();
	}
}
