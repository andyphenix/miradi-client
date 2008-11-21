/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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

import org.miradi.ids.AssignmentId;
import org.miradi.ids.BaseId;
import org.miradi.ids.TaskId;
import org.miradi.objectdata.BaseIdData;
import org.miradi.objectdata.DateRangeEffortListData;
import org.miradi.objecthelpers.DateRangeEffortList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.utils.DateRange;
import org.miradi.utils.EnhancedJsonObject;

public class Assignment extends BaseObject
{
	public Assignment(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, new AssignmentId(idToUse.asInt()));
		clear();
	}
	
	public Assignment(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager,new TaskId(idAsInt), json);
	}
	
	public int getType()
	{
		return getObjectType();
	}

	public String getTypeName()
	{
		return OBJECT_NAME;
	}

	public static int getObjectType()
	{
		return ObjectType.ASSIGNMENT;
	}
	
	public static boolean canOwnThisType(int type)
	{
		return false;
	}
	
	public DateRangeEffortList getDetails()
	{
		return detailListData.getDateRangeEffortList();
	}
	
	@Override
	public String getPseudoData(String fieldTag)
	{
		if (fieldTag.equals(PSEUDO_TAG_PROJECT_RESOURCE_LABEL))
			return getProjectResourceLabel();
		
		return super.getPseudoData(fieldTag);
	}
	
	private String getProjectResourceLabel()
	{
		ProjectResource projectResource = ProjectResource.find(getProject(), getResourceRef());
		if (projectResource == null)
			return "";
		
		return projectResource.getInitials();
	}

	public DateRange getCombinedEffortListDateRange() throws Exception
	{
		return getDetails().getCombinedDateRange();
	}
	
	public void setResourceId(BaseId resourceIdToUse)
	{
		resourceIdData.setId(resourceIdToUse);
	}
	
	public ORef getFundingSourceRef()
	{
		return fundingIdData.getRef();
	}
	
	public ORef getAccountingCodeRef()
	{
		return accountingIdData.getRef();
	}
	
	public ORef getResourceRef()
	{
		return resourceIdData.getRef();
	}
	
	public DateRangeEffortList getDateRangeEffortList() throws Exception
	{
		String dREffortListAsString = getData(Assignment.TAG_DATERANGE_EFFORTS);
		return new DateRangeEffortList(dREffortListAsString);
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == getObjectType();
	}
	
	public static Assignment find(ObjectManager objectManager, ORef assignmentRef)
	{
		return (Assignment) objectManager.findObject(assignmentRef);
	}
	
	public static Assignment find(Project project, ORef assignmentRef)
	{
		return find(project.getObjectManager(), assignmentRef);
	}
	
	public void clear()
	{
		super.clear();
		resourceIdData = new BaseIdData(TAG_ASSIGNMENT_RESOURCE_ID, ProjectResource.getObjectType());
		detailListData = new DateRangeEffortListData(TAG_DATERANGE_EFFORTS);
		accountingIdData = new BaseIdData(TAG_ACCOUNTING_CODE, AccountingCode.getObjectType());
		fundingIdData = new BaseIdData(TAG_FUNDING_SOURCE, FundingSource.getObjectType());
		pseudoProjectResourceLabel = new PseudoStringData(PSEUDO_TAG_PROJECT_RESOURCE_LABEL);
		
		addField(TAG_ASSIGNMENT_RESOURCE_ID, resourceIdData);
		addField(TAG_DATERANGE_EFFORTS, detailListData);
		addField(TAG_ACCOUNTING_CODE, accountingIdData);
		addField(TAG_FUNDING_SOURCE, fundingIdData);
		addField(PSEUDO_TAG_PROJECT_RESOURCE_LABEL, pseudoProjectResourceLabel);
	}
	
	public static final String TAG_ASSIGNMENT_RESOURCE_ID = "ResourceId";
	public static final String TAG_DATERANGE_EFFORTS = "Details";
	public static final String TAG_ACCOUNTING_CODE = "AccountingCode";
	public static final String TAG_FUNDING_SOURCE = "FundingSource";
	public static final String PSEUDO_TAG_PROJECT_RESOURCE_LABEL = "PseudoTagProjectResourceLabel";
	
	public static final String OBJECT_NAME = "Assignment";
	
	private BaseIdData resourceIdData;
	private DateRangeEffortListData detailListData;
	private BaseIdData accountingIdData;
	private BaseIdData fundingIdData;
	private PseudoStringData pseudoProjectResourceLabel;
}
