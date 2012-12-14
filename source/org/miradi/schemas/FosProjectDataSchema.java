/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.schemas;

import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.FosProjectData;
import org.miradi.questions.FosTrainingTypeQuestion;

public class FosProjectDataSchema extends BaseObjectSchema
{
	public FosProjectDataSchema()
	{
		super();
	}
	
	@Override
	protected void fillFieldSchemas()
	{
		super.fillFieldSchemas();
		
		createFieldSchemaChoice(FosProjectData.TAG_TRAINING_TYPE, getQuestion(FosTrainingTypeQuestion.class));
		createFieldSchemaSingleLineUserText(FosProjectData.TAG_TRAINING_DATES);
		createFieldSchemaSingleLineUserText(FosProjectData.TAG_TRAINERS);
		createFieldSchemaSingleLineUserText(FosProjectData.TAG_COACHES);
	}
	
	public static int getObjectType()
	{
		return ObjectType.FOS_PROJECT_DATA;
	}
	
	@Override
	public int getType()
	{
		return getObjectType();
	}

	@Override
	public String getObjectName()
	{
		return OBJECT_NAME;
	}
	
	@Override
	public String getXmpz2ElementName()
	{
		return FOS_PROJECT_DATA;
	}
	
	public static final String OBJECT_NAME = "FosProjectData";
}
