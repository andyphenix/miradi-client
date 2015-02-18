/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.dialogs.taggedObjectSet;

import org.miradi.dialogs.base.EditableBaseObjectTable;
import org.miradi.dialogs.base.SingleBooleanColumnEditableModel;
import org.miradi.dialogs.treeRelevancy.AbstractEditableTreeTablePanel;
import org.miradi.dialogs.treetables.TreeTableWithStateSaving;
import org.miradi.main.MainWindow;
import org.miradi.objects.TaggedObjectSet;

public class TaggedObjectSetTreeTablePanel extends AbstractEditableTreeTablePanel
{
	public static TaggedObjectSetTreeTablePanel createTaggedItemTreeTablePanel(MainWindow mainWindowToUse, TaggedObjectSet taggedObjectSet) throws Exception
	{
		TaggedObjectSetRootProjectNode rootNode = new TaggedObjectSetRootProjectNode(mainWindowToUse.getProject());
		TaggedObjectSetTreeTableModel treeTableModel = new TaggedObjectSetTreeTableModel(rootNode); 
		TaggedObjectSetTreeTable treeTable = new TaggedObjectSetTreeTable(mainWindowToUse, treeTableModel);
		
		return new TaggedObjectSetTreeTablePanel(mainWindowToUse, treeTableModel, treeTable, taggedObjectSet);
	}
	
	private TaggedObjectSetTreeTablePanel(MainWindow mainWindowToUse, TaggedObjectSetTreeTableModel modelToUse, TreeTableWithStateSaving treeTable, TaggedObjectSet taggedObjectSet) throws Exception
	{
		super(mainWindowToUse, modelToUse, treeTable, taggedObjectSet);		
	}
	
	@Override
	protected SingleBooleanColumnEditableModel createEditableTableModel()
	{
		return new TaggedObjectSetEditableTableModel(getProject(), getTree(),  (TaggedObjectSet)getBaseObjectForPanel());
	}
	
	@Override
	protected EditableBaseObjectTable createEditableTable()
	{
		return new TaggedObjectSetEditableTable(getMainWindow(), getEditableSingleBooleanColumnTableModel());
	}	
}
