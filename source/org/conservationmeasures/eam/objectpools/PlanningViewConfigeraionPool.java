/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objectpools;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.PlanningViewConfigeration;
import org.conservationmeasures.eam.project.ObjectManager;

public class PlanningViewConfigeraionPool extends EAMNormalObjectPool
{
	public PlanningViewConfigeraionPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.PLANNING_VIEW_CONFIGERATION);
	}
	
	public PlanningViewConfigeration find(BaseId id)
	{
		return (PlanningViewConfigeration) findObject(id);
	}

	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId, CreateObjectParameter extraInfo)
	{
		return new PlanningViewConfigeration(objectManager, actualId);
	}
}
