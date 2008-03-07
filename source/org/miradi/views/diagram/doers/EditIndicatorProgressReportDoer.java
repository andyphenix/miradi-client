/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.views.diagram.doers;

import org.miradi.actions.ActionDeleteIndicatorProgressReport;
import org.miradi.dialogs.base.ObjectListManagementPanel;
import org.miradi.dialogs.progressReport.ProgressReportManagementPanel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Indicator;
import org.miradi.views.umbrella.ActionCreateIndicatorProgressReport;
import org.miradi.views.umbrella.doers.AbstractPopUpEditDoer;

public class EditIndicatorProgressReportDoer extends AbstractPopUpEditDoer
{
	public EditIndicatorProgressReportDoer()
	{
		super(Indicator.getObjectType(), EAM.text("Edit Progress Reports"));
	}

	protected ObjectListManagementPanel getManagementPanel() throws Exception
	{
		ORef indicatorRef = getSelectedHierarchies()[0].getRefForType(getTypeToFilterOn());
		
		
		return new ProgressReportManagementPanel(getProject(), getMainWindow(), indicatorRef, Indicator.TAG_PROGRESS_REPORT_REFS, getMainWindow().getActions(), buttonActionClasses);
	}
	
	static Class[] buttonActionClasses = new Class[] {
		ActionCreateIndicatorProgressReport.class,
		ActionDeleteIndicatorProgressReport.class,
	};
}
