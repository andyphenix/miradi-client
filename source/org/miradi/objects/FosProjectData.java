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
import org.miradi.questions.FosTrainingTypeQuestion;
import org.miradi.utils.EnhancedJsonObject;

public class FosProjectData extends BaseObject
{
	public FosProjectData(ObjectManager objectManager, BaseId id)
	{
		super(objectManager, id);
		clear();
	}
	
	public FosProjectData(ObjectManager objectManager, int idAsInt, EnhancedJsonObject jsonObject) throws Exception 
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
		return ObjectType.FOS_PROJECT_DATA;
	}
	
	public static boolean canOwnThisType(int type)
	{
		return false;
	}
	
	public static boolean canReferToThisType(int type)
	{
		return false;
	}
	
	public static FosProjectData find(ObjectManager objectManager, ORef fosProjectDataRef)
	{
		return (FosProjectData) objectManager.findObject(fosProjectDataRef);
	}
	
	public static FosProjectData find(Project project, ORef fosProjectDataRef)
	{
		return find(project.getObjectManager(), fosProjectDataRef);
	}
	
	@Override
	void clear()
	{
		super.clear();
		
		createChoiceField(TAG_TRAINING_TYPE, FosTrainingTypeQuestion.class);
		createSingleLineUserTextField(TAG_TRAINING_DATES);
		createSingleLineUserTextField(TAG_TRAINERS);
		createSingleLineUserTextField(TAG_COACHES);
	}
	
	public static final String OBJECT_NAME = "FosProjectData";
	
	public static final String TAG_TRAINING_TYPE = "TrainingType";
	public static final String TAG_TRAINING_DATES = "TrainingDates";
	public static final String TAG_TRAINERS = "Trainers";
	public static final String TAG_COACHES = "Coaches";
}
