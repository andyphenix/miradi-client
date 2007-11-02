/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.diagram;

import org.conservationmeasures.eam.dialogs.base.ObjectDataInputPanel;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.ProjectMetadata;
import org.conservationmeasures.eam.project.Project;

public class ProjectScopePanel extends ObjectDataInputPanel
{
	public ProjectScopePanel(Project projectToUse, ProjectMetadata metadata)
	{
		super(projectToUse, metadata.getType(), metadata.getId());

		addField(createStringField(metadata.TAG_SHORT_PROJECT_SCOPE));
		addField(createMultilineField(metadata.TAG_PROJECT_SCOPE));
		addField(createStringField(metadata.TAG_SHORT_PROJECT_VISION));
		addField(createMultilineField(metadata.TAG_PROJECT_VISION));
		
		updateFieldsFromProject();
	}
	
	public String getPanelDescription()
	{
		return EAM.text("Title|Project Scope Properties");
	}

}
