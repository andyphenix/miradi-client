/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.questions.ProjectSharingQuestion;
import org.miradi.schemas.TncProjectDataSchema;

public class TncProjectData extends BaseObject
{
	public TncProjectData(ObjectManager objectManager, BaseId id)
	{
		this(objectManager, id, createSchema());
	}

	public TncProjectData(ObjectManager objectManager, BaseId id, TncProjectDataSchema schema)
	{
		super(objectManager, id, schema);
	}

	public static TncProjectDataSchema createSchema()
	{
		return new TncProjectDataSchema();
	}
	
	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	public boolean canShareOutsideOfTnc()
	{
		return getData(TAG_PROJECT_SHARING_CODE).equals(ProjectSharingQuestion.SHARE_WITH_ANYONE);
	}
	
	public static boolean canReferToThisType(int type)
	{
		return false;
	}

	public static TncProjectData find(ObjectManager objectManager, ORef tncProjectDataRef)
	{
		return (TncProjectData) objectManager.findObject(tncProjectDataRef);
	}
	
	public static TncProjectData find(Project project, ORef tncProjectDataRef)
	{
		return find(project.getObjectManager(), tncProjectDataRef);
	}
		
	public final static String TAG_PROJECT_SHARING_CODE = "ProjectSharingCode";
	public final static String TAG_CON_PRO_PARENT_CHILD_PROJECT_TEXT = "ConProParentChildProjectText";
	public final static String TAG_PROJECT_RESOURCES_SCORECARD = "ProjectResourcesScorecard";
	public final static String TAG_PROJECT_LEVEL_COMMENTS = "ProjectLevelComments";
	public final static String TAG_PROJECT_CITATIONS = "ProjectCitations";
	public final static String TAG_CAP_STANDARDS_SCORECARD = "CapStandardsScorecard";
	public final static String TAG_MAKING_THE_CASE = "OverallProjectGoal";
	public final static String TAG_RISKS = "Risks";
	public final static String TAG_CAPACITY_AND_FUNDING = "FinancialPlan";
	public final static String TAG_PROJECT_FOCUS = "ProjectFocus";
	public final static String TAG_PROJECT_SCALE = "ProjectScale";
	public final static String TAG_FUNDRAISING_PLAN = "FundraisingPlan";
}
