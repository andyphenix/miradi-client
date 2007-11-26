/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram.doers;

import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.views.diagram.DeleteAnnotationDoer;

public class DeleteStressDoer extends DeleteAnnotationDoer
{
	public boolean isAvailable()
	{
//FIXME temporarly disabled the creation of stresses due to migration of threatStressRatings and new rules
		return false;

//		if (!isDiagramView())
//			return false;
//		
//		if (getObjects().length == 0)
//			return false;
//		
//		if (getSelectedObjectType() != Stress.getObjectType())
//			return false;
//		
//		return true;
	}
	
	protected BaseObject getParent(BaseObject annotationToDelete)
	{
		return getSingleSelected(Target.getObjectType());  
	}

	public String[] getDialogText()
	{
		return new String[] { "Are you sure you want to delete this Stress?",};
	}

	public String getAnnotationIdListTag()
	{
		return Target.TAG_STRESS_REFS;
	}
}
