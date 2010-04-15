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

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.miradi.actions.ActionCollapseAllRows;
import org.miradi.actions.ActionExpandAllRows;
import org.miradi.actions.ActionTreeNodeDown;
import org.miradi.actions.ActionTreeNodeUp;
import org.miradi.actions.Actions;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.views.umbrella.ObjectPicker;

import com.java.sun.jtreetable.TreeTableModelAdapter;

abstract public class ObjectTreeTable extends TreeTableWithColumnWidthSaving implements ObjectPicker, RowColumnBaseObjectProvider
{
	public ObjectTreeTable(MainWindow mainWindowToUse, GenericTreeTableModel treeTableModelToUse)
	{
		super(mainWindowToUse, treeTableModelToUse);
		
		project = mainWindowToUse.getProject();
		selectionListeners = new Vector<ListSelectionListener>();

		setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		getTree().setShowsRootHandles(true);
		getTree().setRootVisible(false);
		getTree().setCellRenderer(new ObjectTreeCellRenderer(this));
		getTree().setEditable(false);
		getColumnModel().getColumn(0).setPreferredWidth(200);
		if (getRowCount()>0)
			setRowSelectionInterval(0,0);
		
		final int CUSTOM_HEIGHT_TO_AVOID_ICON_CROPPING = getRowHeight() + 1;
		setRowHeight(CUSTOM_HEIGHT_TO_AVOID_ICON_CROPPING);
	}

	public Project getProject()
	{
		return project;
	}
	
	public int getProportionShares(int row)
	{
		return getNodeForRow(row).getProportionShares();
	}

	public boolean areBudgetValuesAllocated(int row)
	{
		return getNodeForRow(row).areBudgetValuesAllocated();
	}

	@Override
	public String getToolTipText(MouseEvent event)
	{
		Point at = new Point(event.getX(), event.getY());
		int row = rowAtPoint(at);
		TreeTableNode node = getNodeForRow(row);
		if(node == null)
			return null;

		BaseObject object = node.getObject();
		if(object == null)
			return null;
		
		return getToolTipString(object);
	}

	public static String getToolTipString(BaseObject object)
	{
		String typeName = EAM.fieldLabel(object.getType(), object.getTypeName());
		String tooltip = "<html><b>" + typeName + "</b><br>";
		
		return tooltip + object.getFullName();
	}
	
	public static Font createFristLevelFont(Font defaultFontToUse)
	{
		Map map = defaultFontToUse.getAttributes();
	    map.put(TextAttribute.SIZE, new Float(defaultFontToUse.getSize2D() + 2));
	    map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
	    Font customFont = new Font(map);
		return customFont;
	}
	
	public TreeTableNode[] getSelectedTreeNodes()
	{
		return new TreeTableNode[] {(TreeTableNode)getTree().getLastSelectedPathComponent()};
	}

	 //TODO: This method needs review as it seems a bit complex
	public BaseObject[] getSelectedObjects()
	{
		TreeTableNode selectedNode = (TreeTableNode)getTree().getLastSelectedPathComponent();
		
		if (selectedNode == null)
			return new BaseObject[0];
		
		ORef oRef = selectedNode.getObjectReference();
		EAMObjectPool pool = project.getPool(oRef.getObjectType());
		
		if (pool == null)
			return new BaseObject[0];
		
		BaseObject foundObject = pool.findObject(oRef.getObjectId());
		
		if (foundObject == null)
			return new BaseObject[0];
		
		return new BaseObject[] {foundObject};
	}
	
	public ORefList getSelectionHierarchy()
	{
		TreePath selectionPath = getTree().getSelectionModel().getSelectionPath();
		if (selectionPath == null)
			return new ORefList();
		
		ORefList selectionHierarchyNodeRefs = new ORefList();
		for(int i = selectionPath.getPathCount() - 1; i >=0 ; --i)
		{			
			TreeTableNode node = (TreeTableNode) selectionPath.getPathComponent(i);
			selectionHierarchyNodeRefs.add(node.getObjectReference());
		}
		
		return selectionHierarchyNodeRefs;
	}

	public ORefList[] getSelectedHierarchies()
	{
		TreePath[] selectionPaths = getTree().getSelectionModel().getSelectionPaths();
		if (selectionPaths == null)
			return new ORefList[] {new ORefList(getRootNodeRef())};
		
		ORefList[] selectionHierarchies = new ORefList[selectionPaths.length];
		for (int i = 0; i < selectionPaths.length; ++i)
		{
			selectionHierarchies[i] = getTreeTableModel().convertTreePathToRefList(selectionPaths[i]);
		}
		
		return selectionHierarchies;
	}

	public boolean isActive()
	{
		return isActive;
	}
	
	public void becomeActive()
	{
		Actions actions = getMainWindow().getActions();
		for(Class actionClass : getRelevantActions())
		{
			actions.getObjectsAction(actionClass).addPicker(this);
		}
		isActive = true;
	}

	public void becomeInactive()
	{
		isActive = false;
		Actions actions = getMainWindow().getActions();
		for(Class actionClass : getRelevantActions())
		{
			actions.getObjectsAction(actionClass).removePicker(this);
		}
	}

	private ORef getRootNodeRef()
	{
		return getTreeTableModel().getRootNode().getObjectReference();
	}
	
	public void ensureObjectVisible(ORef ref)
	{
		// NOTE: This code generally must be called from inside invokeLater 
		TreePath path = getTreeTableModel().findTreePath(ref);
		getTree().scrollPathToVisible(path);
	}

	public void addSelectionChangeListener(ListSelectionListener listener)
	{
		selectionListeners.add(listener);
	}

	public void removeSelectionChangeListener(ListSelectionListener listener)
	{
		selectionListeners.remove(listener);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		super.valueChanged(e);
		
		if(selectionListeners == null)
			return;
		
		for(int i = 0; i < selectionListeners.size(); ++i)
		{
			ListSelectionListener listener = selectionListeners.get(i);
			listener.valueChanged(e);
		}
	}

	public TreeTableModelAdapter getTreeTableAdapter()
	{
		return (TreeTableModelAdapter)getModel();
	}
	
	@Override
	public TreeTableNode getNodeForRow(int row)
	{
		return (TreeTableNode)getTreeTableAdapter().nodeForRow(row);
	}
	
	public BaseObject getBaseObjectForRowColumn(int row, int column)
	{
		return getNodeForRow(row).getObject();
	}
	
	private void selectObject(ORefList selectedHierarchy, int fallbackRow)
	{
		if (selectedHierarchy.isEmpty())
			return;
		
		TreePath path = convertToTreePath(selectedHierarchy);
		if(path == null)
		{
			getSelectionModel().setSelectionInterval(fallbackRow, fallbackRow);
			return;
		}
		
		tree.setSelectionPath(path);
	}
	
	private TreePath convertToTreePath(ORefList hierarchy)
	{
		ORef leafNodeRef = hierarchy.get(0);
		Vector<TreePath> treePaths = getTreeTableModel().findTreePaths(leafNodeRef);
		for(TreePath treePath : treePaths)
		{
			ORefList selectionHierarchy = getTreeTableModel().convertTreePathToRefList(treePath);
			if (hierarchy.equals(selectionHierarchy))
				return treePath;
		}
		
		return null;
	}

	public void selectObjectAfterSwingClearsItDueToTreeStructureChange(ORefList selectedHierarchy, int fallbackRow)
	{
		clearSelection();
		tree.clearSelection();
		if(selectedHierarchy == null || selectedHierarchy.isEmpty())
			return;
		
		SwingUtilities.invokeLater(new Reselecter(this, selectedHierarchy, fallbackRow));
	}
	
	static class Reselecter implements Runnable
	{
		public Reselecter(ObjectTreeTable treeTableToUse, ORefList hierarchyToSelect, int rowToSelect)
		{
			treeTable = treeTableToUse;
			selectedHierachy = hierarchyToSelect;
			row = rowToSelect;
		}
		
		public void run()
		{
			treeTable.selectObject(selectedHierachy, row);
			treeTable.ensureSelectedRowVisible();
		}
		
		private ObjectTreeTable treeTable;
		private ORefList selectedHierachy;
		private int row;
	}

	protected Set<Class> getRelevantActions()
	{
		HashSet<Class> set = new HashSet<Class>();
		set.add(ActionCollapseAllRows.class);
		set.add(ActionExpandAllRows.class);
		set.add(ActionTreeNodeUp.class);
		set.add(ActionTreeNodeDown.class);
		return set;
	}

	protected Project project;
	private Vector<ListSelectionListener> selectionListeners;
	private boolean isActive;
}
