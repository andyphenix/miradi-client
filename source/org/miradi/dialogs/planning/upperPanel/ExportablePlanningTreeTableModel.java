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
package org.miradi.dialogs.planning.upperPanel;

import javax.swing.tree.TreePath;

import org.miradi.dialogs.planning.RowColumnProvider;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.utils.CodeList;

public class ExportablePlanningTreeTableModel extends PlanningTreeTableModel implements RowColumnBaseObjectProvider
{
	public ExportablePlanningTreeTableModel(Project projectToUse, CodeList visibleRowCodes, CodeList visibleColumnCodes, String uniqueTreeTableModelIdentifierToUse) throws Exception
	{
		super(projectToUse, visibleRowCodes, visibleColumnCodes);
		
		uniqueTreeTableModelIdentifier = uniqueTreeTableModelIdentifierToUse;
		setRowObjectRefs();
	}

	public ExportablePlanningTreeTableModel(Project projectToUse, TreeTableNode rootNode, CodeList visibleRowCodes, CodeList visibleColumnCodes, String uniqueTreeTableModelIdentifierToUse) throws Exception
	{
		super(projectToUse, rootNode, visibleRowCodes, visibleColumnCodes);
		
		uniqueTreeTableModelIdentifier = uniqueTreeTableModelIdentifierToUse;
		setRowObjectRefs();
	}
	
	public ExportablePlanningTreeTableModel(Project projectToUse, RowColumnProvider rowColumnProvider, String uniqueTreeTableModelIdentifierToUse) throws Exception
	{
		super(projectToUse, rowColumnProvider.getRowListToShow(), rowColumnProvider.getColumnListToShow());
		
		uniqueTreeTableModelIdentifier = uniqueTreeTableModelIdentifierToUse;
		setRowObjectRefs();
	}

	private void setRowObjectRefs() throws Exception
	{
		rowObjectRefs = getFullyExpandedRefList();
		removeRootNode();
	}
	
	private void removeRootNode()
	{
		rowObjectRefs.remove(0);
	}
		
	public BaseObject getBaseObjectForRowColumn(int row, int column)
	{
		ORef rowObjectRef = rowObjectRefs.get(row);
		if (rowObjectRef.isInvalid())
			return null;
		return getProject().findObject(rowObjectRef);
	}

	public int getRowCount()
	{
		return rowObjectRefs.size();
	}
	
	public int getProportionShares(int row)
	{
		TreeTableNode node = getNodeForRow(row);
		if(node == null)
			return 1;
		return node.getProportionShares();
	}
	
	public boolean areBudgetValuesAllocated(int row)
	{
		TreeTableNode node = getNodeForRow(row);
		if(node == null)
			return false;
		return node.areBudgetValuesAllocated();
	}

	private TreeTableNode getNodeForRow(int row)
	{
		ORef rowObjectRef = rowObjectRefs.get(row);
		if (rowObjectRef.isInvalid())
			return null;
		TreePath path = findTreePath(rowObjectRef);
		TreeTableNode node = (TreeTableNode) path.getLastPathComponent();
		return node;
	}
	
	@Override
	public void updateColumnsToShow() throws Exception
	{
	}
	
	@Override
	public String getUniqueTreeTableModelIdentifier()
	{
		return uniqueTreeTableModelIdentifier;
	}
	
	private String uniqueTreeTableModelIdentifier;

	private ORefList rowObjectRefs;
}
