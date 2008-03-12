/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.dialogs.base;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.miradi.dialogs.fieldComponents.ChoiceItemComboBox;
import org.miradi.dialogs.fieldComponents.PanelComboBox;
import org.miradi.dialogs.tablerenderers.ChoiceItemTableCellRenderer;
import org.miradi.dialogs.tablerenderers.DefaultFontProvider;
import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.StaticChoiceQuestion;
import org.miradi.utils.TableWithColumnWidthSaver;
import org.miradi.views.umbrella.ObjectPicker;

abstract public class EditableObjectTable extends TableWithColumnWidthSaver  implements ObjectPicker
{
	public EditableObjectTable(EditableObjectTableModel modelToUse)
	{
		super(modelToUse);
		
		model = modelToUse;
		selectionListeners = new Vector();
	}
	
	public Project getProject()
	{
		return model.getProject();
	}
	
	protected ObjectManager getObjectManager()
	{
		return getProject().getObjectManager();
	}
		
	public TreeTableNode[] getSelectedTreeNodes()
	{
		return null;
	}
		
	protected int getColumnWidth(int column)
	{
		return getColumnHeaderWidth(column);
	}
	
	protected String getColumnTag(int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		return model.getColumnTag(modelColumn);
	}
		
	protected void createComboColumn(BaseObject[] content, int col, BaseObject invalidObject)
	{
		Arrays.sort(content, new SorterByToString());
		BaseObject[] comboContent = addEmptySpaceAtStart(content, invalidObject);
		PanelComboBox comboBox = new PanelComboBox(comboContent);
		TableColumn tableColumn = getColumnModel().getColumn(col);
		tableColumn.setCellEditor(new DefaultCellEditor(comboBox));
		tableColumn.setCellRenderer(new ComboBoxRenderer(comboContent));
	}
	
	protected void createComboColumn(ChoiceItem[] choices, int tableColumn)
	{
		ChoiceItemComboBox comboBox = new ChoiceItemComboBox(choices);
		int modelColumn = convertColumnIndexToModel(tableColumn);
		TableColumn column = getColumnModel().getColumn(modelColumn);
		column.setCellEditor(new DefaultCellEditor(comboBox));
		column.setCellRenderer(new ChoiceItemComboBoxRenderer(choices));
	}
	
	protected void createReadonlyChoiceItemColumn(ChoiceItem[] choices, int tableColumn)
	{
		int modelColumn = convertColumnIndexToModel(tableColumn);
		TableColumn column = getColumnModel().getColumn(modelColumn);
		column.setCellRenderer(new ChoiceItemTableCellRenderer(model, new DefaultFontProvider()));
	}
	
	protected BaseObject[] addEmptySpaceAtStart(BaseObject[] content, BaseObject invalidObject)
	{
		final int EMPTY_SPACE = 0;
		BaseObject[]  comboContent = new BaseObject[content.length + 1];
		comboContent[EMPTY_SPACE] = invalidObject;

		try
		{
			invalidObject.setLabel(" ");
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	
		System.arraycopy(content, 0, comboContent, 1, content.length);	
		return comboContent;
	}
	
	public BaseObject[] getSelectedObjects()
	{
		int selectedRow = getSelectedRow();
		if (selectedRow < 0)
			return new BaseObject[0];
		
		if (selectedRow >=  model.getRowCount())
			return new BaseObject[0];
		
		BaseObject selectedObject = model.getBaseObjectForRowColumn(selectedRow, 0);
		if (selectedObject == null)
			return new BaseObject[0];
	
		return new BaseObject[] {selectedObject};
	}
	
	public ORefList getSelectionHierarchy()
	{
		return null;
	}
		
	public ORefList[] getSelectedHierarchies()
	{
		int[] rows = getSelectedRows();
		ORefList[] selectedHierarchies = new ORefList[rows.length];
		for(int i = 0; i < rows.length; ++i)
		{
			BaseObject objectFromRow = model.getBaseObjectForRowColumn(rows[i], 0);
			ORefList selectedObjectRefs = new ORefList();
			selectedObjectRefs.add(objectFromRow.getRef());
			selectedHierarchies[i] = selectedObjectRefs;
		}
		
		return selectedHierarchies;
	}

	public void ensureObjectVisible(ORef ref)
	{
		// TODO Auto-generated method stub
		// we should scroll the table as needed to make this 
		// probably-newly-created object visible
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
	
	protected void createComboQuestionColumn(StaticChoiceQuestion question, int tableColumn)
	{
		createComboColumn(question.getChoices(), tableColumn);
	}
	
	protected void createReadonlyComboQuestionColumn(StaticChoiceQuestion question, int tableColumn)
	{
		createReadonlyChoiceItemColumn(question.getChoices(), tableColumn);
	}

	public class SorterByToString implements Comparator<BaseObject>
	{
		public int compare(BaseObject o1, BaseObject o2)
		{
			return o1.toString().compareToIgnoreCase(o2.toString());
		}	
	}
	
	protected class ComboBoxRenderer extends PanelComboBox implements TableCellRenderer 
	{
	    public ComboBoxRenderer(Object[] items) 
	    {
	        super(items);
	    }

	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) 
	    {
	        if (!isSelected)  
	        	setColors(table.getBackground(), table.getForeground());

	        setSelectedItem(value);
	        return this;
	    }
	    
	    private void setColors(Color background, Color foreground)
	    {
	    	setBackground(background);
	        setForeground(foreground);
	    }
	}
	
	protected class ChoiceItemComboBoxRenderer extends ChoiceItemComboBox implements TableCellRenderer 
	{
	    public ChoiceItemComboBoxRenderer(ChoiceItem[] items) 
	    {
	        super(items);
	    }

	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) 
	    {
	        if (isSelected) 
	        	setColors(table.getSelectionBackground(), Color.BLACK);
	        else 
	        	setColors(table.getBackground(), table.getForeground());

	        setSelectedItem(value);
	        return this;
	    }
	    
	    private void setColors(Color background, Color foreground)
	    {
	        setForeground(foreground);
	        setBackground(background);
	    }
	}
	
	private Vector selectionListeners;
	private EditableObjectTableModel model;
}

