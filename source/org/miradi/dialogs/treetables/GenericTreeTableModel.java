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
package org.miradi.dialogs.treetables;

import java.util.Vector;

import javax.swing.tree.TreePath;

import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.utils.ColumnTagProvider;

import com.java.sun.jtreetable.AbstractTreeTableModel;
import com.java.sun.jtreetable.TreeTableModel;

public abstract class GenericTreeTableModel extends AbstractTreeTableModel implements ColumnTagProvider
{
	public GenericTreeTableModel(Object root)
	{
		super(root);
	}

	public TreePath getPathToRoot()
	{
		return new TreePath(getRoot());
	}
	
	protected void rebuildNode()
	{
		try
		{
			getRootNode().rebuild();
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}

	public TreePath getPathOfNode(ORef ref)
	{
		return findObject(getPathToRoot(), ref.getObjectType(), ref.getObjectId());
	}
	
	protected TreeTableNode getRootNode()
	{
		return (TreeTableNode)getRoot();
	}

	public void rebuildEntireTree()
	{
		rebuildNode();
		reloadNodesWithouRebuildingNodes();
	}

	public void reloadNodesWithouRebuildingNodes()
	{
		fireTreeStructureChanged(getRoot(), new Object[] {getPathToRoot()}, null, null);
	}
	
	public void rebuildObjectRow(ORef ref)
	{
		TreePath pathToRoot = getPathToRoot();
		TreePath pathToRepaint = findObject(pathToRoot, ref.getObjectType(), ref.getObjectId());
		if(pathToRepaint == null)
			return;
		
		rebuildRow(pathToRepaint);
	}

	public void rebuildRow(TreePath pathToRepaint)
	{
		TreeTableNode nodeToRepaint = (TreeTableNode)pathToRepaint.getLastPathComponent();
		TreePath pathToParent = pathToRepaint.getParentPath();
		TreeTableNode parentNode = (TreeTableNode)pathToParent.getLastPathComponent();
		int[] childIndex = new int[] {parentNode.getIndex(nodeToRepaint)};
		Object[] childObject = new Object[] {nodeToRepaint};
		fireTreeNodesChanged(nodeToRepaint, pathToParent.getPath(), childIndex, childObject);
	}
	
	public TreePath findObject(TreePath pathToStartSearch, ORef ref)
	{
		return findObject(pathToStartSearch, ref.getObjectType(), ref.getObjectId());
	}

	public TreePath findObject(TreePath pathToStartSearch, int objectType, BaseId objectId)
	{
		TreeTableNode nodeToSearch = (TreeTableNode)pathToStartSearch.getLastPathComponent();

		if(nodeToSearch.getType() == objectType)
		{
			if (nodeToSearch.getObjectReference()==null)
				return pathToStartSearch;
			if (nodeToSearch.getObjectReference().getObjectId().equals(objectId))
				return pathToStartSearch;
		}

		for(int i = 0; i < nodeToSearch.getChildCount(); ++i)
		{
			TreeTableNode thisChild = nodeToSearch.getChild(i);
			TreePath childPath = pathToStartSearch.pathByAddingChild(thisChild);
			TreePath found = findObject(childPath, objectType, objectId);
			if(found != null)
				return found;
		}
		return null;
	}
	
	//FIXME this is duplicated from above method findObject.  Also this method needs to be diagnosed since
	// it seems like it can be refactored 
	public Vector<TreePath> findTreePaths(ORef ref)
	{
		TreePath pathToStartSearch = getPathToRoot();
		TreeTableNode nodeToSearch = (TreeTableNode)pathToStartSearch.getLastPathComponent();
		
		Vector<TreePath> treePaths = new Vector();
		if(nodeToSearch.getType() == ref.getObjectType())
		{
			if (nodeToSearch.getObjectReference()==null)
			{
				treePaths.add(pathToStartSearch);
				return treePaths;
			}
			if (nodeToSearch.getObjectReference().equals(ref))
			{
				treePaths.add(pathToStartSearch);
				return treePaths;
			}
		}

		for(int i = 0; i < nodeToSearch.getChildCount(); ++i)
		{
			TreeTableNode thisChild = nodeToSearch.getChild(i);
			TreePath childPath = pathToStartSearch.pathByAddingChild(thisChild);
			TreePath found = findObject(childPath, ref);
			if(found != null)
				treePaths.add(found);
		}
		
		return treePaths;
	}
	
	public Class getColumnClass(int column)
	{
		if(column == PanelTreeTable.TREE_COLUMN_INDEX)
			return TreeTableModel.class;
		return String.class;
	}
	
	public Object getValueAt(Object rawNode, int column)
	{
		TreeTableNode node = (TreeTableNode)rawNode;
		return node.getValueAt(column);
	}
	
	public Object getChild(Object rawNode, int index)
	{
		TreeTableNode node = (TreeTableNode)rawNode;
		return node.getChild(index);
	}

	public int getChildCount(Object rawNode)
	{
		TreeTableNode node = (TreeTableNode)rawNode;
		return node.getChildCount();
	}
	
	public ORefList getFullyExpandedRefList() throws Exception
	{
		ORefList fullyExpandedObjectRefs = new ORefList();
		Vector<TreePath> fullExpandedNodeList = getFullyExpandedTreePathList();
		for(TreePath treePath : fullExpandedNodeList)
		{
			TreeTableNode node = (TreeTableNode) treePath.getLastPathComponent();
			ORef ref = node.getObjectReference();
			fullyExpandedObjectRefs.add(ref);
		}
		
		return fullyExpandedObjectRefs;
	}
	
		public Vector<ORefList> getFullyExpandedHierarchyRefListList() throws Exception
	{
		Vector<ORefList> fullyExpandedObjectRefs = new Vector();
		Vector<TreePath> fullExpandedNodeList = getFullyExpandedTreePathList();
		for(TreePath treePath : fullExpandedNodeList)
		{
			fullyExpandedObjectRefs.add(convertPath(treePath));
		}
		
		return fullyExpandedObjectRefs;
	}
	
	public ORefList convertPath(TreePath treePath)
	{
		ORefList selectionHierarchyNodeRefs = new ORefList();
		for(int i = treePath.getPathCount() - 1; i >=0 ; --i)
		{			
			TreeTableNode node = (TreeTableNode) treePath.getPathComponent(i);
			selectionHierarchyNodeRefs.add(node.getObjectReference());
		}
		
		return selectionHierarchyNodeRefs;	
	}
	
	public Vector<TreePath> getFullyExpandedTreePathList() throws Exception
	{
		TreePath pathToRoot = getPathToRoot();
		Vector<TreePath> fullyExpandedTreePathList = new Vector();
		recursivelyGetFullyExpandedTreePaths(fullyExpandedTreePathList, pathToRoot);
		
		return fullyExpandedTreePathList;
	}
	
	private void recursivelyGetFullyExpandedTreePaths(Vector<TreePath> fullyExpandedTreePathList, TreePath treePath)
	{
		fullyExpandedTreePathList.add(treePath);
		TreeTableNode node = (TreeTableNode) treePath.getLastPathComponent();
		for(int childIndex = 0; childIndex < node.getChildCount(); ++childIndex)
		{
			TreeTableNode childNode = node.getChild(childIndex);
			TreePath thisTreePath = treePath.pathByAddingChild(childNode);
			recursivelyGetFullyExpandedTreePaths(fullyExpandedTreePathList, thisTreePath);
		}
	}
	
	abstract public String getUniqueTreeTableModelIdentifier();

	public static final String DEFAULT_COLUMN = "Item";
	public static final int SINGLE_COLUMN_COUNT = 1;
}
