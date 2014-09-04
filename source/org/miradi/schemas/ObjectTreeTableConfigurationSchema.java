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

package org.miradi.schemas;

import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.ObjectTreeTableConfiguration;
import org.miradi.project.Project;
import org.miradi.questions.CustomPlanningColumnsQuestion;
import org.miradi.questions.CustomPlanningRowsQuestion;
import org.miradi.questions.DiagramObjectDataInclusionQuestion;
import org.miradi.questions.PlanningTreeTargetPositionQuestion;
import org.miradi.questions.StrategyObjectiveTreeOrderQuestion;

public class ObjectTreeTableConfigurationSchema extends BaseObjectSchema
{
	public ObjectTreeTableConfigurationSchema(final Project projectToUse)
	{
		super();
		
		project = projectToUse;
	}
	
	@Override
	protected void fillFieldSchemas()
	{
		super.fillFieldSchemas();
		
		createFieldSchemaCodeList(ObjectTreeTableConfiguration.TAG_ROW_CONFIGURATION, new CustomPlanningRowsQuestion(getProject()));
		createFieldSchemaCodeList(ObjectTreeTableConfiguration.TAG_COL_CONFIGURATION, getQuestion(CustomPlanningColumnsQuestion.class));
		createFieldSchemaRequiredChoice(ObjectTreeTableConfiguration.TAG_DIAGRAM_DATA_INCLUSION, DiagramObjectDataInclusionQuestion.class);
		createFieldSchemaRequiredChoice(ObjectTreeTableConfiguration.TAG_STRATEGY_OBJECTIVE_ORDER, StrategyObjectiveTreeOrderQuestion.class);
		createFieldSchemaRequiredChoice(ObjectTreeTableConfiguration.TAG_TARGET_NODE_POSITION, PlanningTreeTargetPositionQuestion.class);
	}
	
	private Project getProject()
	{
		return project;
	}
	
	public static int getObjectType()
	{
		return ObjectType.OBJECT_TREE_TABLE_CONFIGURATION;
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

	private Project project;
	public static final String OBJECT_NAME = "PlanningViewConfiguration";
}
