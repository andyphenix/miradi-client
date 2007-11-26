/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.threatstressrating.upperPanel;

import javax.swing.event.ListSelectionEvent;

import org.conservationmeasures.eam.dialogs.MultiTableUpperPanel;
import org.conservationmeasures.eam.dialogs.threatstressrating.properties.ThreatStressRatingPropertiesPanel;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.project.Project;

public class ThreatStressRatingListTablePanel extends MultiTableUpperPanel
{
	public static ThreatStressRatingListTablePanel createThreatStressRatingListTablePanel(Project projectToUse, ThreatStressRatingPropertiesPanel propertiesPanel) throws Exception
	{
		return new ThreatStressRatingListTablePanel(projectToUse, new ThreatStressRatingMultiTablePanel(projectToUse), propertiesPanel);
	}
	
	private ThreatStressRatingListTablePanel(Project projectToUse, ThreatStressRatingMultiTablePanel multiTablePanelToUse, ThreatStressRatingPropertiesPanel propertiesPanelToUse)
	{
		super(projectToUse, multiTablePanelToUse.getObjectPicker());

		multiTablePanel = multiTablePanelToUse;
		propertiesPanel = propertiesPanelToUse;
		
		add(multiTablePanelToUse);
	}

	public void valueChanged(ListSelectionEvent event)
	{
		super.valueChanged(event);
		ORefList[] selectedHierarcies = multiTablePanel.getSelectedHierarchies();
		propertiesPanel.setObjectRefs(selectedHierarcies);
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
	}

	public BaseObject getSelectedObject()
	{
		return null;
	}
	
	private ThreatStressRatingPropertiesPanel propertiesPanel;
	private ThreatStressRatingMultiTablePanel multiTablePanel;
}
