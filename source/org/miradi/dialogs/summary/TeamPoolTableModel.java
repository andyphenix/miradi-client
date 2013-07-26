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
package org.miradi.dialogs.summary;

import org.miradi.dialogs.base.ObjectPoolTableModel;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.ProjectResource;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.ResourceRoleQuestion;
import org.miradi.questions.StaticQuestionManager;

public class TeamPoolTableModel extends ObjectPoolTableModel
{
	public TeamPoolTableModel(Project project)
	{
		super(project, ObjectType.PROJECT_RESOURCE, COLUMN_TAGS);
	}

	@Override
	public ORefList getLatestRefListFromProject()
	{
		return getProject().getResourcePool().getTeamMemberRefs();
	}
	
	@Override
	public ChoiceQuestion getColumnQuestion(int column)
	{
		if (isRolesColumn(column))
			return StaticQuestionManager.getQuestion(ResourceRoleQuestion.class);
		
		return super.getColumnQuestion(column);
	}

	@Override
	public boolean isCodeListColumn(int column)
	{
		if (isRolesColumn(column))
			return true;
		
		return false;
	}
	
	@Override
	public boolean isChoiceItemColumn(int column)
	{
		if (isRolesColumn(column))
			return false;
		
		return super.isChoiceItemColumn(column);
	}
	
	private boolean isRolesColumn(int column)
	{
		return getColumnTag(column).equals(ProjectResource.TAG_ROLE_CODES);
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return UNIQUE_MODEL_IDENTIFIER;
	}
	
	private static final String UNIQUE_MODEL_IDENTIFIER = "TeamPoolTableModel";
		
	private static final String[] COLUMN_TAGS = new String[] {
		ProjectResource.TAG_GIVEN_NAME,
		ProjectResource.TAG_SUR_NAME,
		ProjectResource.TAG_INITIALS,
		ProjectResource.TAG_ORGANIZATION,
		ProjectResource.TAG_POSITION,
		ProjectResource.TAG_ROLE_CODES,
		ProjectResource.TAG_EMAIL,
		ProjectResource.TAG_PHONE_NUMBER,
	};
}
