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
import org.conservationmeasures.eam.objects.PlanningViewConfiguration;
import org.conservationmeasures.eam.project.ObjectManager;

public class PlanningViewConfiguraionPool extends EAMNormalObjectPool
{
	public PlanningViewConfiguraionPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.PLANNING_VIEW_CONFIGURATION);
	}
	
	public PlanningViewConfiguration find(BaseId id)
	{
		return (PlanningViewConfiguration) findObject(id);
	}

	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId, CreateObjectParameter extraInfo)
	{
		return new PlanningViewConfiguration(objectManager, actualId);
	}
}
