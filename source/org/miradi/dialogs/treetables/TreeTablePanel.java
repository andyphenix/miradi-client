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
package org.miradi.dialogs.treetables;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.martus.swing.UiButton;
import org.miradi.actions.Actions;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogs.base.ColumnMarginResizeListenerValidator;
import org.miradi.dialogs.base.ObjectCollectionPanel;
import org.miradi.dialogs.treetables.MultiTreeTablePanel.ScrollPaneWithHideableScrollBar;
import org.miradi.main.AppPreferences;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.objects.TableSettings;
import org.miradi.schemas.TableSettingsSchema;
import org.miradi.utils.FastScrollPane;
import org.miradi.utils.FillerLabel;

import com.jhlabs.awt.GridLayoutPlus;

abstract public class TreeTablePanel extends ObjectCollectionPanel  implements TreeSelectionListener
{
	public TreeTablePanel(MainWindow mainWindowToUse, TreeTableWithStateSaving treeToUse, Class[] buttonActionClasses) throws Exception
	{
		super(mainWindowToUse, treeToUse);
		
		mainWindow = mainWindowToUse;
		tree = treeToUse;
		
		buttonBox = createButtonBox(buttonActionClasses);
		add(buttonBox, BorderLayout.BEFORE_FIRST_LINE);

		restoreTreeExpansionState();
		treeTableScrollPane = new ScrollPaneWithHideableScrollBar(tree);
		add(treeTableScrollPane, BorderLayout.CENTER);
		
		tree.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tree.getTree().addSelectionRow(0);
		tree.getTree().addTreeSelectionListener(this);
	}
	
	@Override
	public void dispose()
	{
		tree.dispose();
		super.dispose();
	}
	
	protected void addToButtonBox(Component componentToAdd)
	{
		buttonBox.add(componentToAdd); 
	}
	
	protected void addComponentAsFirst(Component componentToAdd)
	{
		final int FIRST_BUTTON_IN_PANEL_INDEX = 0;
		buttonBox.add(componentToAdd, FIRST_BUTTON_IN_PANEL_INDEX);
	}
	
	private JPanel createButtonBox(Class[] buttonActionClasses) throws Exception
	{
		GridLayoutPlus layout = createButtonLayout();
		JPanel box = new JPanel(layout);
		box.setBackground(AppPreferences.getDataPanelBackgroundColor());
		addButtonsToBox(buttonActionClasses, box, mainWindow.getActions());
		
		return box;
	}

	public TreeTableWithStateSaving getTree()
	{
		return tree;
	}
	
	protected FastScrollPane getTreeTableScrollPane()
	{
		return treeTableScrollPane;
	}
	
	public JPanel getButtonBox()
	{
		return buttonBox;
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

	@Override
	public BaseObject getSelectedObject()
	{
		TreeTableNode selectedTreeNode = getSelectedTreeNode();
		if(selectedTreeNode == null)
			return null;
		return selectedTreeNode.getObject();
	}

	public TreeTableNode getSelectedTreeNode()
	{
		return (TreeTableNode)tree.getTree().getLastSelectedPathComponent();
	}

	public GenericTreeTableModel getTreeTableModel()
	{
		return getTree().getTreeTableModel();
	}
	
	public void addButtonsToBox(Class[] classes, JPanel box, Actions actions)
	{
		for (int index = 0; index < classes.length; ++index)
		{
			addCreateButtonAndAddToBox(classes[index], box, actions);
		}
	}
	
	private void addCreateButtonAndAddToBox(Class actionClass, JPanel box, Actions actions)
	{
		if (actionClass == null)
		{
			box.add(new FillerLabel());
		}
		else
		{
			UiButton button = createObjectsActionButton(actions.getObjectsAction(actionClass), tree);
			box.add(button);
		}
	}

	public void valueChanged(TreeSelectionEvent e)
	{	
		if (getPropertiesPanel() == null)
			return;
		
		TreePath[] treePaths = tree.getTree().getSelectionPaths();
		if (treePaths==null || treePaths.length == 0)
			return;
		
		Object[] selectedObjects = treePaths[0].getPath();
		
		Vector<ORef> objects = new Vector<ORef>();
		for (int i=0; i< selectedObjects.length; ++i)
		{
			TreeTableNode node = (TreeTableNode)selectedObjects[i];
			objects.insertElementAt(node.getObjectReference(), 0);
		}
		
		getPropertiesPanel().setObjectRefs(objects.toArray(new ORef[0]));
		mainWindow.updateActionStates();
		if (shouldSetFocusOnFirstField())
			getPropertiesPanel().setFocusOnFirstField();
	}
	
	protected boolean shouldSetFocusOnFirstField()
	{
		return true;
	}

	//TODO:Is this needed? Is it the right place/mechanism? 
	public void setSelectedObject(ORef ref)
	{
	}
	
	// TODO: We think this method is not necessary
	// Currently this method returns true if the modified object is of the passed type,
	// or if the modified object is of the type that is selected in the tree, which is confusing
	protected boolean isSelectedObjectModification(CommandExecutedEvent event, int typeToCheck)
	{
		if (! event.isSetDataCommand())
			return false;
		
		TreeTableNode node = getSelectedTreeNode();
		if (node == null)
			return false;
		
		BaseObject selectedObject = node.getObject(); 
		if (selectedObject == null)
			return false;
		
		CommandSetObjectData setCommand = (CommandSetObjectData) event.getCommand();
		int setType = setCommand.getObjectType();
		if(setType == typeToCheck)
			return true;
		
		String setField = setCommand.getFieldTag();
		
		String[] fieldTags = selectedObject.getFieldTags();
		Vector<String> fields = new Vector<String>(Arrays.asList(fieldTags));
	
		boolean sameType = (selectedObject.getType() == setType);
		boolean containsField = (fields.contains(setField));
		return (sameType && containsField);
	}

	protected void repaintToGrowIfTreeIsTaller()
	{
		if (getTopLevelAncestor() != null)
			getTopLevelAncestor().repaint();
	}

	protected void listenForColumnWidthChanges(JTable table)
	{
		table.getColumnModel().addColumnModelListener(new ColumnMarginResizeListenerValidator(this));
	}

	protected boolean isTreeExpansionCommand(CommandExecutedEvent event)
	{
		return event.isSetDataCommandWithThisTypeAndTag(TableSettingsSchema.getObjectType(), TableSettings.TAG_TREE_EXPANSION_LIST);
	}
	
	public static class ScrollPaneNoExtraWidth extends ScrollPaneWithHideableScrollBar
	{
		public ScrollPaneNoExtraWidth(Component component)
		{
			super(component);
		}

		@Override
		public Dimension getMaximumSize()
		{
			Dimension max = super.getMaximumSize();
			max.width = getPreferredSize().width;
			return max;
		}
	}
	
	abstract protected GridLayoutPlus createButtonLayout();
	
	private MainWindow mainWindow;
	protected TreeTableWithStateSaving tree;
	protected JPanel buttonBox;
	protected FastScrollPane treeTableScrollPane;
}
