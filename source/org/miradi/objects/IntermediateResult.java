/* 
Copyright 2005-2022, Foundations of Success, Bethesda, Maryland
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
package org.miradi.objects;

import org.miradi.ids.FactorId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.IntermediateResultSchema;
import org.miradi.utils.CommandVector;

public class IntermediateResult extends Factor
{
	public IntermediateResult(ObjectManager objectManager, FactorId idToUse)
	{
		super(objectManager, idToUse, createSchema(objectManager));
	}

	public static IntermediateResultSchema createSchema(Project projectToUse)
	{
		return createSchema(projectToUse.getObjectManager());
	}

	public static IntermediateResultSchema createSchema(ObjectManager objectManager)
	{
		return (IntermediateResultSchema) objectManager.getSchemas().get(ObjectType.INTERMEDIATE_RESULT);
	}

	@Override
	public boolean isIntermediateResult()
	{
		return true;
	}
	
	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	@Override
	public boolean canHaveObjectives()
	{
		return true;
	}

	@Override
	public CommandVector createCommandsToDeleteChildren() throws Exception
	{
		CommandVector commandsToDeleteChildren  = super.createCommandsToDeleteChildren();
		commandsToDeleteChildren.addAll(createCommandsToDeleteRefs(TAG_RESULT_REPORT_REFS));

		return commandsToDeleteChildren;
	}

	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == IntermediateResultSchema.getObjectType();
	}

	public static boolean is(BaseObject baseObject)
	{
		return is(baseObject.getType());
	}

	public static IntermediateResult find(ObjectManager objectManager, ORef intermediateResultRef)
	{
		return (IntermediateResult) objectManager.findObject(intermediateResultRef);
	}
	
	public static IntermediateResult find(Project project, ORef intermediateResultRef)
	{
		return find(project.getObjectManager(), intermediateResultRef);
	}
}
