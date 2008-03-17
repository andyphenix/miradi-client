/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.dialogs.base;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.martus.swing.UiLabel;
import org.miradi.actions.EAMAction;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.fieldComponents.PanelButton;
import org.miradi.dialogs.fieldComponents.PanelFieldLabel;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.ids.BaseId;
import org.miradi.layout.OneRowPanel;
import org.miradi.main.AppPreferences;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.Project;

import com.jhlabs.awt.Alignment;
import com.jhlabs.awt.GridLayoutPlus;

abstract public class ObjectDataInputPanel extends AbstractObjectDataInputPanel
{
	public ObjectDataInputPanel(Project projectToUse, int objectType)
	{
		this(projectToUse, objectType, BaseId.INVALID);
	}
	
	public ObjectDataInputPanel(Project projectToUse, int objectType, BaseId idToUse)
	{
		this(projectToUse, new ORef[] {new ORef(objectType, idToUse)});
	}
	
	
	public ObjectDataInputPanel(Project projectToUse, ORef orefToUse)
	{
		this(projectToUse, new ORef[] {orefToUse});
	}
	
	
	public ObjectDataInputPanel(Project projectToUse, ORef[] orefsToUse)
	{
		super(projectToUse, orefsToUse);
		GridLayoutPlus layout = new GridLayoutPlus(0, 2, HGAP, VGAP);
		layout.setColAlignment(0, Alignment.NORTHEAST);
		setLayout(layout);
	}
	
	public void addLine(Component field1, Component field2)
	{
		add(field1);
		add(field2);
	}
	
	public void addBlankHorizontalLine()
	{
		addLabel(new UiLabel(" "));
		addFieldComponent(new UiLabel(" "));
	}
	
	public ObjectDataInputField addField(ObjectDataInputField field)
	{
		super.addField(field);
		addLabel(field.getObjectType(), field.getTag());
		addFieldComponent(field.getComponent());
		return field;
	}
	
	public ObjectDataInputField addFieldWithCustomLabel(ObjectDataInputField field, UiLabel label)
	{
		super.addField(field);
		addLabel(label);
		addFieldComponent(field.getComponent());
		return field;
	}
	
	public ObjectDataInputField addFieldWithCustomLabelAndHint(ObjectDataInputField field, String hint)
	{
		super.addField(field);
		addLabel(field.getObjectType(), field.getTag());
		Box box = Box.createHorizontalBox();
		box.setBackground(AppPreferences.getDataPanelBackgroundColor());
		box.add(field.getComponent());
		box.add(Box.createHorizontalStrut(20));
		box.add(new PanelTitleLabel(hint));
		addFieldComponent(box);
		return field;
	}
	
	protected void addFieldsOnOneLine(String fieldTag, Icon icon, ObjectDataInputField[] fields)
	{
		JPanel fieldPanel = createFieldPanel(fields);		
		fieldPanel.setBackground(AppPreferences.getDataPanelBackgroundColor());
		addLabelWithIcon(fieldTag, icon);
		add(fieldPanel);
	}
	
	protected void addFieldsOnOneLine(String fieldTag, Icon icon, String[] fieldLabelTexts, ObjectDataInputField[] fields)
	{
		JPanel fieldPanel = createFieldPanel(fieldLabelTexts, fields);		
		fieldPanel.setBackground(AppPreferences.getDataPanelBackgroundColor());
		addLabelWithIcon(fieldTag, icon);
		add(fieldPanel);
	}
	
	//TODO,  come up with a better solution for this.  the reason this exists is becuase
	// the label visibilty is changed outside of here.  
	protected void addFieldsOnOneLine(PanelTitleLabel label, Object[] labelsAndFields)
	{
		OneRowPanel fieldPanel = new OneRowPanel();
		fieldPanel.setGaps(3);
		fieldPanel.setBackground(AppPreferences.getDataPanelBackgroundColor());
		for(int i = 0; i < labelsAndFields.length; i+=2)
		{
			PanelTitleLabel fieldLabel = (PanelTitleLabel) labelsAndFields[i];
			ObjectDataInputField objectDataInputField = (ObjectDataInputField) labelsAndFields[i+1];
			super.addField(objectDataInputField);
			fieldPanel.add(fieldLabel);
			fieldPanel.add((objectDataInputField).getComponent());
		}
		
		addLabel(label);
		add(fieldPanel);
	}
	
	protected void addFieldsOnOneLine(String label, ObjectDataInputField[] fields)
	{
		JPanel fieldPanel = createFieldPanel(fields);
		addLabel(label);
		add(fieldPanel);
	}
	
	protected void addFieldsOnOneLine(PanelTitleLabel label, ObjectDataInputField[] fields)
	{
		JPanel fieldPanel = createFieldPanel(fields);
		add(label);
		add(fieldPanel);
	}
	
	protected void addFieldsOnOneLine(PanelTitleLabel label, String[] fieldLabelTexts, ObjectDataInputField[] fields)
	{
		JPanel fieldPanel = createFieldPanel(fieldLabelTexts, fields);
		add(label);
		add(fieldPanel);
	}
	
	protected void addFieldsOnOneLine(ObjectDataInputField[] fields)
	{
		addFieldsOnOneLine("", fields);
	}
	
	private JPanel createFieldPanel(ObjectDataInputField[] fields)
	{
		OneRowPanel fieldPanel = new OneRowPanel();
		fieldPanel.setGaps(3);
		fieldPanel.setBackground(AppPreferences.getDataPanelBackgroundColor());
		for(int i = 0; i < fields.length; ++i)
		{
			super.addField(fields[i]);
			fieldPanel.add(new PanelFieldLabel(fields[i].getObjectType(), fields[i].getTag()));
			fieldPanel.add(fields[i].getComponent());
			fieldPanel.add(new JLabel(" "));
		}
		
		return fieldPanel;
	}

	private JPanel createFieldPanel(String labelTexts[], ObjectDataInputField[] fields)
	{
		OneRowPanel fieldPanel = new OneRowPanel();
		fieldPanel.setGaps(3);
		fieldPanel.setBackground(AppPreferences.getDataPanelBackgroundColor());
		for(int i = 0; i < fields.length; ++i)
		{
			super.addField(fields[i]);
			fieldPanel.add(new PanelTitleLabel(labelTexts[i]));
			fieldPanel.add(fields[i].getComponent());
			fieldPanel.add(new JLabel(" "));
		}
		
		return fieldPanel;
	}

	protected void addFieldWithEditButton(String label, ObjectDataInputField field, EAMAction action)
	{
		addFieldWithEditButton(label, field, new PanelButton(action));
	}

	protected void addFieldWithEditButton(PanelTitleLabel label, ObjectDataInputField field, PanelButton button)
	{
		super.addField(field);
		OneRowPanel fieldPanel = new OneRowPanel();
		fieldPanel.setGaps(3);
		fieldPanel.setBackground(AppPreferences.getDataPanelBackgroundColor());
		fieldPanel.add(field.getComponent());
		fieldPanel.add(button);
		addLabel(label);
		add(fieldPanel);
	}

	protected void addFieldWithEditButton(String label, ObjectDataInputField field, PanelButton button)
	{
		super.addField(field);
		OneRowPanel fieldPanel = new OneRowPanel();
		fieldPanel.setGaps(3);
		fieldPanel.setBackground(AppPreferences.getDataPanelBackgroundColor());
		fieldPanel.add(field.getComponent());
		fieldPanel.add(button);
		addLabel(label);
		add(fieldPanel);
	}

	public void addLabelWithIcon(String fieldTag, Icon icon)
	{
		addLabel(getORef(0).getObjectType(), fieldTag, icon);
	}
	
	public void addLabel(String fieldTag)
	{
		addLabel(getORef(0).getObjectType(), fieldTag);
	}
	
	public void addLabel(int objectType, String fieldTag)
	{
		UiLabel label = new PanelFieldLabel(objectType, fieldTag);
		addLabel(label);
	}
	
	public void addLabel(int objectType, String fieldTag, Icon icon)
	{
		UiLabel label = new PanelFieldLabel(objectType, fieldTag);
		label.setIcon(icon);
		addLabel(label);
	}

	public void addLabel(UiLabel label)
	{
		label.setVerticalAlignment(SwingConstants.TOP);
		add(label);
	}
	
	public void addFieldComponent(Component component)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(AppPreferences.getDataPanelBackgroundColor());
		panel.add(component, BorderLayout.BEFORE_LINE_BEGINS);
		add(panel);
	}
	
	static final int HGAP = 6;
	static final int VGAP = 6;
}
