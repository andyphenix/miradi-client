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
import org.miradi.ids.ResourceAssignmentId;
import org.miradi.objectdata.BaseIdData;
import org.miradi.objectdata.ObjectData;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.TimePeriodCosts;
import org.miradi.objecthelpers.TimePeriodCostsMap;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.utils.DateRange;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.OptionalDouble;

public class ResourceAssignment extends Assignment
{
	public ResourceAssignment(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, new ResourceAssignmentId(idToUse.asInt()));
		clear();
	}
	
	public ResourceAssignment(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new ResourceAssignmentId(idAsInt), json);
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

	public static int getObjectType()
	{
		return ObjectType.RESOURCE_ASSIGNMENT;
	}
	
	@Override
	public String getPseudoData(String fieldTag)
	{
		if (fieldTag.equals(PSEUDO_TAG_PROJECT_RESOURCE_LABEL))
			return getProjectResourceLabel();
		
		if (fieldTag.equals(PSEUDO_TAG_OWNING_FACTOR_NAME))
			return getOwningFactorName();
		
		return super.getPseudoData(fieldTag);
	}
	
	@Override
	public TimePeriodCostsMap getTotalTimePeriodCostsMap() throws Exception
	{
		return getTimePeriodCostsMap(TAG_RESOURCE_ASSIGNMENT_IDS);
	}
	
	private String getOwningFactorName()
	{
		Factor owningFactor = getDirectOrIndirectOwningFactor();
		if (owningFactor == null)
			return "";
		
		return owningFactor.toString();
	}

	private String getProjectResourceLabel()
	{
		ProjectResource projectResource = getProjectResource();
		if (projectResource == null)
			return "";
		
		return projectResource.getInitials();
	}

	private ProjectResource getProjectResource()
	{
		return ProjectResource.find(getProject(), getResourceRef());
	}
	
	@Override
	protected TimePeriodCosts createTimePeriodCosts(OptionalDouble quantity)
	{
		return new TimePeriodCosts(getResourceRef(), getFundingSourceRef(), getAccountingCodeRef(), quantity); 
	}
	
	public DateRange getCombinedTimePeriodCostsMapDateRange() throws Exception
	{
		DateRange projectDateRange = getProject().getProjectCalendar().getProjectPlanningDateRange();
		return convertDateUnitEffortList().getRolledUpDateRange(projectDateRange);
	}
	
	@Override
	public ORef getFundingSourceRef()
	{
		return fundingIdData.getRef();
	}
	
	@Override
	public ORef getAccountingCodeRef()
	{
		return accountingIdData.getRef();
	}
	
	public ORef getResourceRef()
	{
		return resourceIdData.getRef();
	}
	
	@Override
	public boolean isAssignmentSuperseded(DateUnit dateUnit) throws Exception
	{
		return getOwner().hasAnySubtaskResourceData(dateUnit);
	}
	
	@Override
	public String toString()
	{
		ProjectResource projectResource = getProjectResource();
		if (projectResource == null)
			return "";
		
		return projectResource.getFullName();
	}

	public static boolean is(BaseObject baseObject)
	{
		return is(baseObject.getType());
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == getObjectType();
	}
	
	public static ResourceAssignment find(ObjectManager objectManager, ORef assignmentRef)
	{
		return (ResourceAssignment) objectManager.findObject(assignmentRef);
	}
	
	public static ResourceAssignment find(Project project, ORef assignmentRef)
	{
		return find(project.getObjectManager(), assignmentRef);
	}
	
	@Override
	public void clear()
	{
		super.clear();
		resourceIdData = new BaseIdData(TAG_RESOURCE_ID, ProjectResource.getObjectType());
		accountingIdData = new BaseIdData(TAG_ACCOUNTING_CODE, AccountingCode.getObjectType());
		fundingIdData = new BaseIdData(TAG_FUNDING_SOURCE, FundingSource.getObjectType());
		pseudoProjectResourceLabel = new PseudoStringData(PSEUDO_TAG_PROJECT_RESOURCE_LABEL);
		pseudoOwningFactorNameLabel = new PseudoStringData(PSEUDO_TAG_OWNING_FACTOR_NAME);
		
		addField(TAG_RESOURCE_ID, resourceIdData);
		addField(TAG_ACCOUNTING_CODE, accountingIdData);
		addField(TAG_FUNDING_SOURCE, fundingIdData);
		addField(PSEUDO_TAG_PROJECT_RESOURCE_LABEL, pseudoProjectResourceLabel);
		addField(PSEUDO_TAG_OWNING_FACTOR_NAME, pseudoOwningFactorNameLabel);
	}
	
	public static final String TAG_RESOURCE_ID = "ResourceId";
	public static final String TAG_ACCOUNTING_CODE = "AccountingCode";
	public static final String TAG_FUNDING_SOURCE = "FundingSource";
	public static final String PSEUDO_TAG_PROJECT_RESOURCE_LABEL = "PseudoTagProjectResourceLabel";
	public static final String PSEUDO_TAG_OWNING_FACTOR_NAME = "PseudoTagOwningFactorName";
	
	
	public static final String OBJECT_NAME = "ResourceAssignment";
	
	private ObjectData resourceIdData;
	private ObjectData accountingIdData;
	private ObjectData fundingIdData;
	private ObjectData pseudoProjectResourceLabel;
	private ObjectData pseudoOwningFactorNameLabel;
}
