/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.treetables;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.dialogs.base.ObjectCollectionPanel;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.utils.FastScrollPane;
import org.conservationmeasures.eam.utils.HideableScrollBar;
import org.martus.swing.UiButton;

import com.jhlabs.awt.GridLayoutPlus;

abstract public class TreeTablePanel extends ObjectCollectionPanel  implements TreeSelectionListener
{
	public TreeTablePanel(MainWindow mainWindowToUse, TreeTableWithStateSaving treeToUse, Class[] classes)
	{
		super(mainWindowToUse.getProject(), treeToUse);
		mainWindow = mainWindowToUse;
		tree = treeToUse;
		
		restoreTreeExpansionState();
		treeTableScrollPane = new ScrollPaneWithHideableScrollBar(tree);
		add(treeTableScrollPane, BorderLayout.CENTER);
		
		GridLayoutPlus layout = new GridLayoutPlus(1, 0);
		JPanel buttonBox = new JPanel(layout);
		add(buttonBox,BorderLayout.AFTER_LAST_LINE);
		addButtonsToBox(classes, buttonBox, mainWindow.getActions());

		tree.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tree.getTree().addSelectionRow(0);
		tree.getTree().addTreeSelectionListener(this);
	}
	
	public TreeTableWithStateSaving getTree()
	{
		return tree;
	}

	protected void restoreTreeExpansionState() 
	{
		try
		{
			tree.restoreTreeState();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog(EAM.text("Error restoring tree state"));
		}
	}

	public void dispose()
	{
		tree.dispose();
		super.dispose();
	}
	
	public BaseObject getSelectedObject()
	{
		return getSelectedTreeNode().getObject();
	}

	public TreeTableNode getSelectedTreeNode()
	{
		return (TreeTableNode)tree.getTree().getLastSelectedPathComponent();
	}

	public void selectObject(BaseObject objectToSelect)
	{
		tree.selectObject(objectToSelect.getRef());
	}

	public GenericTreeTableModel getModel()
	{
		return model;
	}
	
	public void addButtonsToBox(Class[] classes, JPanel buttonBox, Actions actions)
	{
		for (int i=0; i<classes.length; ++i)
		addCreateButtonAndAddToBox(classes[i], buttonBox, actions);
	}
	
	private void addCreateButtonAndAddToBox(Class actionClass, JPanel buttonBox, Actions actions)
	{
		UiButton button = createObjectsActionButton(actions.getObjectsAction(actionClass), tree);
		buttonBox.add(button);
	}
	

	public void valueChanged(TreeSelectionEvent e)
	{	
		if (getPropertiesPanel() == null)
			return;
		
		TreePath[] treePaths = tree.getTree().getSelectionPaths();
		if (treePaths==null)
			return;
		
		Object[] selectedObjects = treePaths[0].getPath();
		
		Vector objects = new Vector();
		for (int i=0; i< selectedObjects.length; ++i)
		{
			TreeTableNode node = (TreeTableNode)selectedObjects[i];
			objects.insertElementAt(node.getObjectReference(), 0);
		}
		
		getPropertiesPanel().setObjectRefs((ORef[])objects.toArray(new ORef[0]));
		mainWindow.updateActionStates();
	}
	
	
	//TODO:Is this needed? Is it the right place/mechanism? 
	public void setSelectedObject(ORef ref)
	{
	}
	
	protected ScrollPaneWithHideableScrollBar getTreeTableScrollPane()
	{
		return treeTableScrollPane;
	}
	
	public static class ScrollPaneWithHideableScrollBar extends FastScrollPane
	{
		public ScrollPaneWithHideableScrollBar(JComponent component)
		{
			super(component);
			hideableScrollBar = new HideableScrollBar();
			setVerticalScrollBar(hideableScrollBar);
		}
		
		public void showVerticalScrollBar()
		{
			hideableScrollBar.visible = true;
		}
		
		public void hideVerticalScrollBar()
		{
			hideableScrollBar.visible = false;
		}

		private HideableScrollBar hideableScrollBar;
	}

	private MainWindow mainWindow;
	protected TreeTableWithStateSaving tree;
	protected GenericTreeTableModel model;
	protected ScrollPaneWithHideableScrollBar treeTableScrollPane;
}
