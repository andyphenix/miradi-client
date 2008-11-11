/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.views.diagram.doers;

import org.miradi.dialogs.base.DisposablePanel;
import org.miradi.dialogs.taggedObjectSet.TaggedObjectSetTreeTablePanel;
import org.miradi.main.EAM;
import org.miradi.objects.TaggedObjectSet;
import org.miradi.views.umbrella.doers.AbstractEditLisDoer;

public class EditTaggedObjectSetDoer extends AbstractEditLisDoer
{
	protected DisposablePanel getEditPanel() throws Exception
	{
		TaggedObjectSet taggedObjectSet = (TaggedObjectSet) getSelectedObject();
		return TaggedObjectSetTreeTablePanel.createTaggedItemTreeTablePanel(getMainWindow(), taggedObjectSet);
	}

	protected int getObjectType()
	{
		return TaggedObjectSet.getObjectType();
	}
	
	protected String getDialogTitle()
	{
		return EAM.text("Tagged Items");
	}
}
