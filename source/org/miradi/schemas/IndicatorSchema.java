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
import org.miradi.objects.Indicator;
import org.miradi.questions.PriorityRatingQuestion;
import org.miradi.questions.RatingSourceQuestion;

public class IndicatorSchema extends BaseObjectSchema
{
	public IndicatorSchema()
	{
		super();
	}
	
	@Override
	protected void fillFieldSchemas()
	{
		super.fillFieldSchemas();
		
		createFieldSchemaSingleLineUserText(Indicator.TAG_SHORT_LABEL);
		createFieldSchemaChoice(Indicator.TAG_PRIORITY, PriorityRatingQuestion.class);
		createFieldSchemaIdList(Indicator.TAG_METHOD_IDS, TaskSchema.getObjectType());
		createFieldSchemaCodeToUserStringMap(Indicator.TAG_THRESHOLDS_MAP);
		createFieldSchemaCodeToUserStringMap(Indicator.TAG_THRESHOLD_DETAILS_MAP);
		createFieldSchemaChoice(Indicator.TAG_RATING_SOURCE, RatingSourceQuestion.class);
		createOwnedFieldSchemaReflist(Indicator.TAG_MEASUREMENT_REFS, MEASUREMENT);
		createFieldSchemaMultiLineUserText(Indicator.TAG_DETAIL);
		createFieldSchemaMultiLineUserText(Indicator.TAG_COMMENTS);
		createFieldSchemaMultiLineUserText(Indicator.TAG_VIABILITY_RATINGS_COMMENTS);
		createFieldSchemaOptionalRef(Indicator.TAG_LEADER_RESOURCE);
		createFieldSchemaSingleLineUserText(Indicator.TAG_UNIT);
        createOwnedFieldSchemaReflist(Indicator.TAG_FUTURE_STATUS_REFS, FUTURE_STATUS);
		createBudgetSchemas();
		createProgressReportSchema();
		createTaxonomyClassificationSchemaField();
		
		
		createPseudoFieldSchemaString(Indicator.PSEUDO_TAG_TARGETS);
		createPseudoFieldSchemaString(Indicator.PSEUDO_TAG_DIRECT_THREATS);
		createPseudoFieldSchemaString(Indicator.PSEUDO_TAG_STRATEGIES);
		createPseudoFieldSchemaString(Indicator.PSEUDO_TAG_FACTOR);
		createPseudoFieldSchemaString(Indicator.PSEUDO_TAG_METHODS);
		createPseudoFieldSchemaQuestion(Indicator.PSEUDO_TAG_PRIORITY_VALUE);
		createPseudoFieldSchemaQuestion(Indicator.PSEUDO_TAG_STATUS_VALUE);
		createPseudoFieldSchemaQuestion(Indicator.PSEUDO_TAG_RATING_SOURCE_VALUE);
		createPseudoFieldSchemaQuestion(Indicator.PSEUDO_TAG_LATEST_MEASUREMENT_REF);
		createPseudoFieldSchemaQuestion(Indicator.PSEUDO_TAG_FUTURE_STATUS_RATING_VALUE);
	}

	public static int getObjectType()
	{
		return ObjectType.INDICATOR;
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
	
	public static final String OBJECT_NAME = "Indicator";
}
