/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.project.Project;

abstract public class ObjectPoolTablePanel extends ObjectTablePanel
{
	public ObjectPoolTablePanel(Project projectToUse, int objectTypeToUse, ObjectPoolTableModel model)
	{
		super(projectToUse, objectTypeToUse, new ObjectPoolTable(model));
	}

}
