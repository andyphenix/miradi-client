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
package org.miradi.dialogs.treetables;

import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.views.umbrella.ObjectPicker;

import com.java.sun.jtreetable.TreeTableModel;
import com.java.sun.jtreetable.TreeTableModelAdapter;

abstract public class ObjectTreeTable extends TreeTableWithColumnWidthSaving implements ObjectPicker, RowColumnBaseObjectProvider
{
	public ObjectTreeTable(MainWindow mainWindowToUse, GenericTreeTableModel treeTableModelToUse)
	{
		super(mainWindowToUse, treeTableModelToUse);
		treeTableModel = treeTableModelToUse;
		project = mainWindowToUse.getProject();
		selectionListeners = new Vector();

		setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		getTree().setShowsRootHandles(true);
		getTree().setRootVisible(false);
		getTree().setCellRenderer(new ObjectTreeCellRenderer(getMainWindow()));
		getTree().setEditable(false);
		getColumnModel().getColumn(0).setPreferredWidth(200);
		TableCellEditor ce = new NonEditableTreeTableCellEditor();
		setDefaultEditor(TreeTableModel.class, ce);
		if (getRowCount()>0)
			setRowSelectionInterval(0,0);
		
		final int CUSTOM_HEIGHT_TO_AVOID_ICON_CROPPING = getRowHeight() + 1;
		setRowHeight(CUSTOM_HEIGHT_TO_AVOID_ICON_CROPPING);
	}

	public Project getProject()
	{
		return project;
	}

	public GenericTreeTableModel getTreeTableModel()
	{
		return treeTableModel;
	}
	
	public static Font createFristLevelFont(Font defaultFontToUse)
	{
		Map map = defaultFontToUse.getAttributes();
	    map.put(TextAttribute.SIZE, new Float(defaultFontToUse.getSize2D() + 2));
	    map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
	    Font customFont = new Font(map);
		return customFont;
	}
	
	class NonEditableTreeTableCellEditor extends TreeTableCellEditor
	{
		public NonEditableTreeTableCellEditor() 
		{
		    super();
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c)
		{
			JTextField textField = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, r, c);
			textField.setEditable(false);
			return textField;
		}
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
			selectionHierarchies[i] = convertPath(selectionPaths[i]);
		}
		
		return selectionHierarchies;
	}

	private ORef getRootNodeRef()
	{
		return getTreeTableModel().getRootNode().getObjectReference();
	}
	
	private ORefList convertPath(TreePath treePath)
	{
		ORefList selectionHierarchyNodeRefs = new ORefList();
		for(int i = treePath.getPathCount() - 1; i >=0 ; --i)
		{			
			TreeTableNode node = (TreeTableNode) treePath.getPathComponent(i);
			selectionHierarchyNodeRefs.add(node.getObjectReference());
		}
		
		return selectionHierarchyNodeRefs;	
	}

	public void ensureObjectVisible(ORef ref)
	{
		// NOTE: This code hasn't been proven to work...we believe it needs to be called
		// from inside invokeLater and that it will work if we do that
		TreePath path = getTreeTableModel().findObject(getTreeTableModel().getPathToRoot(), ref);
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

	public void valueChanged(ListSelectionEvent e)
	{
		super.valueChanged(e);
		
		if(selectionListeners == null)
			return;
		
		for(int i = 0; i < selectionListeners.size(); ++i)
		{
			ListSelectionListener listener = (ListSelectionListener)selectionListeners.get(i);
			listener.valueChanged(null);
		}
	}

	public String getColumnTag(int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		return getTreeTableModel().getColumnTag(modelColumn);
	}
	
	public TreeTableModelAdapter getTreeTableAdapter()
	{
		return (TreeTableModelAdapter)getModel();
	}
	
	public TreeTableNode getNodeForRow(int row)
	{
		return (TreeTableNode)getTreeTableAdapter().nodeForRow(row);
	}
	
	public BaseObject getBaseObjectForRowColumn(int row, int column)
	{
		return getNodeForRow(row).getObject();
	}
	
	public void selectObject(ORef ref, int fallbackRow)
	{
		TreePath path = getTreeTableModel().getPathOfNode(ref.getObjectType(), ref.getObjectId());
		if(path == null)
		{
			getSelectionModel().setSelectionInterval(fallbackRow, fallbackRow);
			return;
		}
		
		tree.setSelectionPath(path);
	}

	public void selectObjectAfterSwingClearsItDueToTreeStructureChange(ORef selectedRef, int fallbackRow)
	{
		clearSelection();
		tree.clearSelection();
		if(selectedRef == null || selectedRef.isInvalid())
			return;
		
		SwingUtilities.invokeLater(new Reselecter(this, selectedRef, fallbackRow));
	}
	
	static class Reselecter implements Runnable
	{
		public Reselecter(ObjectTreeTable treeTableToUse, ORef refToSelect, int rowToSelect)
		{
			treeTable = treeTableToUse;
			ref = refToSelect;
			row = rowToSelect;
		}
		
		public void run()
		{
			treeTable.selectObject(ref, row);
		}
		
		private ObjectTreeTable treeTable;
		private ORef ref;
		private int row;
	}

	private GenericTreeTableModel treeTableModel;
	Project project;
	Vector selectionListeners;
}
