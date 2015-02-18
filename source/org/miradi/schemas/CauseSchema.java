/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
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

package org.miradi.schemas;

import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Cause;
import org.miradi.questions.ThreatClassificationQuestion;

public class CauseSchema extends FactorSchema
{
	public CauseSchema()
	{
		super();
	}
	
	@Override
	protected void fillFieldSchemas()
	{
		super.fillFieldSchemas();
		
		createFieldSchemaChoice(Cause.TAG_TAXONOMY_CODE, new ThreatClassificationQuestion());
		createFieldSchemaBoolean(Cause.TAG_IS_DIRECT_THREAT);
	    writeIndicatorIds();
		writeObjectiveIds();
		createTaxonomyClassificationSchemaField();
	
		createPseudoFieldSchemaQuestion(Cause.PSEUDO_TAG_TAXONOMY_CODE_VALUE);
	}

	public static int getObjectType()
	{
		return ObjectType.CAUSE;
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
	
	public static final String OBJECT_NAME = "Cause";
}
