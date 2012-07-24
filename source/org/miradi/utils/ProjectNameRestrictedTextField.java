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
package org.miradi.utils;


import org.miradi.dialogs.fieldComponents.PanelTextFieldWithSelectAllOnFocusGained;
import org.miradi.project.Project;

//FIXME medium - Since this text field is already using ProjectNameRestrictedDocument, why is it still
//passing max value to super.  Seems like duplicate effort. 
public class ProjectNameRestrictedTextField extends PanelTextFieldWithSelectAllOnFocusGained
{
	public ProjectNameRestrictedTextField()
	{
		this("");
	}

	public ProjectNameRestrictedTextField(String initialValue)
	{
		super(Project.getMaximumProjectNameLength());
		
		initialize(initialValue);
	}

	private void initialize(String initialValue)
	{
		setDocument(new ProjectNameRestrictedDocument());
		setText(initialValue);
	}
}
