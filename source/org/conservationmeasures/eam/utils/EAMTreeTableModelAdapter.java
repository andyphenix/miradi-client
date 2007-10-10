/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.utils;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

import org.conservationmeasures.eam.project.Project;

import com.java.sun.jtreetable.TreeTableModel;

public class EAMTreeTableModelAdapter extends AbstractTableModel
{
	public EAMTreeTableModelAdapter(Project projectToUse, TreeTableModel treeTableModelToUse, JTree treeToUse)
	{
		tree = treeToUse;
		treeTableModel = treeTableModelToUse;
		project = projectToUse;
		setModelListeners(treeTableModel);
	}

	private void setModelListeners(TreeTableModel treeTableModel)
	{
		treeTableModel.addTreeModelListener(new TreeModelHandler());
	}

	public int getColumnCount() 
	{
		return treeTableModel.getColumnCount();
	}

	public String getColumnName(int column) 
	{
		return treeTableModel.getColumnName(column);
	}

	public Class getColumnClass(int column) 
	{
		return treeTableModel.getColumnClass(column);
	}

	public int getRowCount() 
	{
		return tree.getRowCount();
	}

	public Object nodeForRow(int row) 
	{
		TreePath treePath = tree.getPathForRow(row);
		return treePath.getLastPathComponent();         
	}

	public Object getValueAt(int row, int column) 
	{
		return treeTableModel.getValueAt(nodeForRow(row), column);
	}

	public boolean isCellEditable(int row, int column) 
	{
		return treeTableModel.isCellEditable(nodeForRow(row), column); 
	}

	public void setValueAt(Object value, int row, int column) 
	{
		treeTableModel.setValueAt(value, nodeForRow(row), column);
	}

	protected void delayedFireTableRowsUpdated() 
	{
		SwingUtilities.invokeLater(new DelayedTableDataUpdatedFirer()); 
	}

	protected void delayedFireTableRowsRemoved() 
	{
		SwingUtilities.invokeLater(new DelayedTableDataChangedFirer());	}

	protected void delayedFireTableRowsInserted() 
	{
		SwingUtilities.invokeLater(new DelayedTableDataChangedFirer());
	}

	protected void delayedFireTableDataChanged() 
	{
		SwingUtilities.invokeLater(new DelayedTableDataUpdatedFirer());
	}

	private final class TreeModelHandler implements TreeModelListener
	{
		public void treeNodesChanged(TreeModelEvent e) 
		{
			delayedFireTableRowsUpdated();
		}

		public void treeNodesInserted(TreeModelEvent e) 
		{
			delayedFireTableRowsInserted();
		}

		public void treeNodesRemoved(TreeModelEvent e) 
		{
			delayedFireTableRowsRemoved();
		}

		public void treeStructureChanged(TreeModelEvent e) 
		{
			delayedFireTableDataChanged();
		}
	}

	private final class DelayedTableDataChangedFirer implements Runnable
	{
		public void run() 
		{
			fireTableDataChanged();
		}
	}

	private final class DelayedTableDataUpdatedFirer implements Runnable
	{
		public void run() 
		{
			fireTableRowsUpdated(0, getRowCount() - 1);
		}
	}
	
	Project project;
	JTree tree;
	TreeTableModel treeTableModel;
}
