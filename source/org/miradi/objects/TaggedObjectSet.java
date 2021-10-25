/* 
Copyright 2005-2021, Foundations of Success, Bethesda, Maryland
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
package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.TaggedObjectSetSchema;

public class TaggedObjectSet extends BaseObject
{
	public TaggedObjectSet(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse, createSchema(objectManager));
	}

	public static TaggedObjectSetSchema createSchema(Project projectToUse)
	{
		return createSchema(projectToUse.getObjectManager());
	}

	public static TaggedObjectSetSchema createSchema(ObjectManager objectManager)
	{
		return (TaggedObjectSetSchema) objectManager.getSchemas().get(ObjectType.TAGGED_OBJECT_SET);
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	@Override
	public String getShortLabel()
	{
		return getData(TAG_SHORT_LABEL);
	}

	@Override
	public String getPseudoData(String fieldTag)
	{
		try
		{
			if(fieldTag.equals(PSEUDO_TAG_REFERRING_DIAGRAM_FACTOR_REFS))
				return getReferringDiagramFactorRefs().toString();

			return super.getPseudoData(fieldTag);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}

	private ORefList getReferringDiagramFactorRefs()
	{
		return findObjectsThatReferToUs(DiagramFactorSchema.getObjectType());
	}

	public static boolean is(BaseObject object)
	{
		return is(object.getRef());
	}

	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == TaggedObjectSetSchema.getObjectType();
	}
	
	public static TaggedObjectSet find(ObjectManager objectManager, ORef objectTagRef)
	{
		return (TaggedObjectSet) objectManager.findObject(objectTagRef);
	}
	
	public static TaggedObjectSet find(Project project, ORef reportTemplateRef)
	{
		return find(project.getObjectManager(), reportTemplateRef);
	}

	public static final String TAG_SHORT_LABEL = "ShortLabel";
	public static final String TAG_TAGGED_OBJECT_REFS = "TaggedObjectRefs";
	public static final String TAG_COMMENTS = "Comments";
	public static final String PSEUDO_TAG_REFERRING_DIAGRAM_FACTOR_REFS = "PseudoTagReferringDiagramFactorRefs";
}
