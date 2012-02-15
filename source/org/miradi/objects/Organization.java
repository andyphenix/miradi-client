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
package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.utils.EnhancedJsonObject;

public class Organization extends BaseObject
{
	public Organization(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse);
		clear();
	}
		
	public Organization(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new BaseId(idAsInt), json);
	}
	
	@Override
	public int getType()
	{
		return getObjectType();
	}
	
	@Override
	public String getTypeName()
	{
		return OBJECT_NAME;
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	public static int getObjectType()
	{
		return ObjectType.ORGANIZATION;
	}
	
	public boolean canHaveIndicators()
	{
		return false;
	}

	@Override
	public ORefList getOwnedObjects(int objectType)
	{
		return new ORefList();
	}
	
	@Override
	public String getShortLabel()
	{
		return getData(TAG_SHORT_LABEL);
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
		return objectType == getObjectType();
	}

	public static Organization find(ObjectManager objectManager, ORef organizationRef)
	{
		return (Organization) objectManager.findObject(organizationRef);
	}
	
	public static Organization find(Project project, ORef organizationRef)
	{
		return find(project.getObjectManager(), organizationRef);
	}
	
	@Override
	void clear()
	{
		super.clear();
		
		createSingleLineUserTextField(TAG_SHORT_LABEL);
		createSingleLineUserTextField(TAG_ROLES_DESCRIPTION);
		createSingleLineUserTextField(TAG_CONTACT_FIRST_NAME);
		createSingleLineUserTextField(TAG_CONTACT_LAST_NAME);
		createSingleLineUserTextField(TAG_EMAIL);
		createSingleLineUserTextField(TAG_PHONE_NUMBER);
		createMultiLineUserTextField(TAG_COMMENTS);
	}
	
	public static final String OBJECT_NAME = "Organization";
	
	public static final String TAG_SHORT_LABEL = "ShortLabel";
	public static final String TAG_ROLES_DESCRIPTION = "RolesDescription";
	public static final String TAG_CONTACT_FIRST_NAME = "ContactFirstName";
	public static final String TAG_CONTACT_LAST_NAME = "ContactLastName";
	public static final String TAG_EMAIL = "Email";
	public static final String TAG_PHONE_NUMBER = "PhoneNumber";
	public static final String TAG_COMMENTS = "Comments";
}
