/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.treeViews.TaskTreeTableModel;

public class TargetViabilityTreeTableModel extends TaskTreeTableModel
{
	public TargetViabilityTreeTableModel(Project projectToUse)
	{
		super(new TargetViabilityRoot(projectToUse));
		project = projectToUse;
	}

	public int getColumnCount()
	{
		return columnTags.length;
	}

	public String getColumnName(int column)
	{
		return EAM.fieldLabel(ObjectType.TASK, columnTags[column]);
	}

	public static String[] columnTags = {"Item", };
	Project project;
}
