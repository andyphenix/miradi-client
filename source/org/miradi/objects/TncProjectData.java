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
import org.miradi.questions.ProjectSharingQuestion;
import org.miradi.questions.TncOrganizationalPrioritiesQuestion;
import org.miradi.questions.TncProjectPlaceTypeQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.EnhancedJsonObject;

public class TncProjectData extends BaseObject
{
	public TncProjectData(ObjectManager objectManager, BaseId id)
	{
		super(objectManager, id);
		clear();
	}
	
	public TncProjectData(ObjectManager objectManager, int idAsInt, EnhancedJsonObject jsonObject) throws Exception 
	{
		super(objectManager, new BaseId(idAsInt), jsonObject);
	}
	
	public CodeList getOrganizationalPriorityCodes()
	{
		return getCodeListData(TAG_ORGANIZATIONAL_PRIORITIES);
	}
	
	public CodeList getProjectPlaceTypeCodes()
	{
		return getCodeListData(TAG_PROJECT_PLACE_TYPES);
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
		return ObjectType.TNC_PROJECT_DATA;
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
		
	@Override
	void clear()
	{
		super.clear();
		
		createCodeField(TAG_PROJECT_SHARING_CODE);
		createCodeListField(TAG_PROJECT_PLACE_TYPES, getProject().getQuestion(TncProjectPlaceTypeQuestion.class));
		createCodeListField(TAG_ORGANIZATIONAL_PRIORITIES, getProject().getQuestion(TncOrganizationalPrioritiesQuestion.class));
		createUserTextField(TAG_CON_PRO_PARENT_CHILD_PROJECT_TEXT);
		createUserTextField(TAG_PROJECT_RESOURCES_SCORECARD);
		createUserTextField(TAG_PROJECT_LEVEL_COMMENTS);
		createUserTextField(TAG_PROJECT_CITATIONS);
		createUserTextField(TAG_CAP_STANDARDS_SCORECARD);
	}
	
	public static final String OBJECT_NAME = "TncProjectData";

	public final static String TAG_PROJECT_SHARING_CODE = "ProjectSharingCode";
	public final static String TAG_PROJECT_PLACE_TYPES = "ProjectPlaceTypes";
	public final static String TAG_ORGANIZATIONAL_PRIORITIES = "OrganizationalPriorities";
	public final static String TAG_CON_PRO_PARENT_CHILD_PROJECT_TEXT = "ConProParentChildProjectText";
	public final static String TAG_PROJECT_RESOURCES_SCORECARD = "ProjectResourcesScorecard";
	public final static String TAG_PROJECT_LEVEL_COMMENTS = "ProjectLevelComments";
	public final static String TAG_PROJECT_CITATIONS = "ProjectCitations";
	public final static String TAG_CAP_STANDARDS_SCORECARD = "CapStandardsScorecard";
}
