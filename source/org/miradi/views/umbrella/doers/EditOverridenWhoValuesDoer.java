/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.views.umbrella.doers;

import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.diagram.OverridingResourcePanel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.Indicator;
import org.miradi.objects.Strategy;
import org.miradi.objects.Task;
import org.miradi.views.umbrella.AbstractEditListDoer;

public class EditOverridenWhoValuesDoer extends AbstractEditListDoer
{
	protected ObjectDataInputPanel getPickListPanel(ORef ref)
	{
		return new OverridingResourcePanel(getProject(), ref);
	}

	protected String getDialogTitle()
	{
		return EAM.text("Choose Resource(s)");
	}
	
	protected ORef getSelectionRef()
	{
		ORefList refList = getSelectedHierarchies()[0];
		return refList.getRefForTypes(new int[]{Task.getObjectType(), Strategy.getObjectType(), Indicator.getObjectType(),});
	}
	
	@Override
	protected boolean isInvalidSelection()
	{
		ORefList refList = getSelectedHierarchies()[0];
		boolean strategyIsInvalid = refList.getRefForType(Strategy.getObjectType()).isInvalid();
 		boolean taskIsInvalid = refList.getRefForType(Task.getObjectType()).isInvalid();
 		boolean indicatorIsInvalid = refList.getRefForType(Indicator.getObjectType()).isInvalid();
	
		return (strategyIsInvalid && taskIsInvalid && indicatorIsInvalid);
	}
}
