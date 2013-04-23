/* 
Copyright 2005-2013, Foundations of Success, Bethesda, Maryland 
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

import org.miradi.ids.BaseId;
import org.miradi.ids.IdAssigner;
import org.miradi.objects.BaseObject;
import org.miradi.objects.MiradiShareTaxonomyAssociation;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.schemas.MiradiShareProjectDataSchema;

public class MiradiShareTaxonomyAssociationPool extends BaseObjectPool
{
	public MiradiShareTaxonomyAssociationPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, MiradiShareProjectDataSchema.getObjectType());
	}
	
	public void put(MiradiShareTaxonomyAssociation miradiShareTaxonomyAssociation) throws Exception
	{
		put(miradiShareTaxonomyAssociation.getId(), miradiShareTaxonomyAssociation);
	}
	
	public MiradiShareTaxonomyAssociation find(BaseId id)
	{
		return (MiradiShareTaxonomyAssociation) getRawObject(id);
	}

	@Override
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId)
	{
		return new MiradiShareTaxonomyAssociation(objectManager, new BaseId(actualId.asInt()));
	}
	
	@Override
	public BaseObjectSchema createBaseObjectSchema(Project projectToUse)
	{
		return MiradiShareTaxonomyAssociation.createSchema();
	}
}
