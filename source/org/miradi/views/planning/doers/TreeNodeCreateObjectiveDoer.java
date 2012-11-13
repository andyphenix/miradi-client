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
package org.miradi.views.planning.doers;

import org.miradi.objects.BaseObject;
import org.miradi.objects.Factor;
import org.miradi.schemas.ObjectiveSchema;

public class TreeNodeCreateObjectiveDoer extends AbstractTreeCreateAnnotationDoer
{
	@Override
	protected int getAnnotationType()
	{
		return ObjectiveSchema.getObjectType();
	}

	@Override
	protected String getAnnotationTag()
	{
		return Factor.TAG_OBJECTIVE_IDS;
	}

	protected boolean isAvailableForFactor(Factor factor)
	{
		return factor.canHaveObjectives();
	}

	@Override
	protected boolean isCorrectOwner(BaseObject selectedObject)
	{
		if (!Factor.isFactor(selectedObject))
			return false;
		
		return isAvailableForFactor((Factor) selectedObject);
	}

	@Override
	protected String getObjectName()
	{
		return ObjectiveSchema.OBJECT_NAME;
	}
}
