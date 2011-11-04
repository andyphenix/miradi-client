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
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.utils.EnhancedJsonObject;

public class WcpaProjectData extends BaseObject
{
	public WcpaProjectData(ObjectManager objectManager, BaseId id)
	{
		super(objectManager, id);
		clear();
	}
	
	public WcpaProjectData(ObjectManager objectManager, int idAsInt, EnhancedJsonObject jsonObject) throws Exception 
	{
		super(objectManager, new BaseId(idAsInt), jsonObject);
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
		return ObjectType.WCPA_PROJECT_DATA;
	}
	
	public static boolean canReferToThisType(int type)
	{
		return false;
	}
	
	public static WcpaProjectData find(ObjectManager objectManager, ORef wcpsProjectDataRef)
	{
		return (WcpaProjectData) objectManager.findObject(wcpsProjectDataRef);
	}
	
	public static WcpaProjectData find(Project project, ORef wcpaProjectDataRef)
	{
		return find(project.getObjectManager(), wcpaProjectDataRef);
	}
	
	@Override
	void clear()
	{
		super.clear();
		
		createUserTextField(TAG_LEGAL_STATUS);
		createUserTextField(TAG_LEGISLATIVE);
		createUserTextField(TAG_PHYSICAL_DESCRIPTION);
		createUserTextField(TAG_BIOLOGICAL_DESCRIPTION);
		createUserTextField(TAG_SOCIO_ECONOMIC_INFORMATION);
		createUserTextField(TAG_HISTORICAL_DESCRIPTION);
		createUserTextField(TAG_CULTURAL_DESCRIPTION);
		createUserTextField(TAG_ACCESS_INFORMATION);
		createUserTextField(TAG_VISITATION_INFORMATION);
		createUserTextField(TAG_CURRENT_LAND_USES);
		createUserTextField(TAG_MANAGEMENT_RESOURCES);
	}

	public final static String TAG_LEGAL_STATUS = "LegalStatus";
	public final static String TAG_LEGISLATIVE = "LegislativeContext";
	public final static String TAG_PHYSICAL_DESCRIPTION = "PhysicalDescription";
	public final static String TAG_BIOLOGICAL_DESCRIPTION = "BiologicalDescription";
	public final static String TAG_SOCIO_ECONOMIC_INFORMATION = "SocioEconomicInformation";
	public final static String TAG_HISTORICAL_DESCRIPTION = "HistoricalDescription";
	public final static String TAG_CULTURAL_DESCRIPTION = "CulturalDescription";
	public final static String TAG_ACCESS_INFORMATION = "AccessInformation";
	public final static String TAG_VISITATION_INFORMATION = "VisitationInformation";
	public final static String TAG_CURRENT_LAND_USES = "CurrentLandUses";
	public final static String TAG_MANAGEMENT_RESOURCES = "ManagementResources";
		
	public static final String OBJECT_NAME = "WCPAProjectData";
}
