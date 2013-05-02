/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.base;

import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.forms.FieldPanelSpec;
import org.miradi.forms.FormConstant;
import org.miradi.forms.FormFieldData;
import org.miradi.forms.FormFieldLabel;
import org.miradi.forms.FormItem;
import org.miradi.forms.FormRow;
import org.miradi.forms.PropertiesPanelSpec;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.Project;
import org.miradi.utils.DataPanelFlexibleWidthHtmlViewer;
import org.miradi.utils.FlexibleWidthHtmlViewer;

abstract public class AbstractObjectDataInputPanelWithCreationFromForms extends	AbstractObjectDataInputPanelWithActivation
{
	public AbstractObjectDataInputPanelWithCreationFromForms(Project projectToUse, ORef[] orefsToUse)
	{
		super(projectToUse, orefsToUse);
	}

	protected void createFieldsFromForm(FieldPanelSpec form) throws Exception
	{
		for(int row = 0; row < form.getFieldRowCount(); ++row)
		{
			FormRow formRow = form.getFormRow(row);
			addLabelFromForm(formRow);
			addFieldFromForm(formRow);
		}
	}
	
	protected void addLabelAndFieldFromForm(FormRow formRow) throws Exception
	{
		addLabelFromForm(formRow);
		addFieldFromForm(formRow);
	}

	protected void addLabelFromForm(FormRow formRow)
	{
		if(formRow.getLeftFormItemsCount() != 1)
			throw new RuntimeException(getClass().getName()	+ ": Don't know how to add multiple labels");

		FormItem formItem = formRow.getLeftFormItem(0);
		if(formItem.isFormConstant())
		{
			FormConstant fc = (FormConstant) formItem;
			addHtmlWrappedLabel(fc.getConstant());
			return;
		}
		
		if(!formItem.isFormFieldLabel())
			throw new RuntimeException(getClass().getName()	+ ": Don't know how to add non-label " + formItem);
		
		FormFieldLabel label = (FormFieldLabel) formItem;
		int objectType = label.getObjectType();
		String fieldTag = label.getObjectTag();
		addLabel(objectType, fieldTag);
	}

	protected void addFieldFromForm(FormRow formRow) throws Exception
	{
		if(formRow.getRightFormItemsCount() != 1)
			throw new RuntimeException(getClass().getName()	+ ": Don't know how to add multiple fields");

		FormItem formItem = formRow.getRightFormItem(0);
		if(formItem.isFormConstant())
		{
			FormConstant fc = (FormConstant) formItem;
			String text = fc.getConstant();
			FlexibleWidthHtmlViewer viewer = new DataPanelFlexibleWidthHtmlViewer(getMainWindow(), text);
			addFieldComponent(viewer);
			return;
		}
		
		if(!formItem.isFormFieldData())
			throw new RuntimeException(getClass().getName()	+ ": Don't know how to add non-field " + formItem);
		
		FormFieldData data = (FormFieldData) formItem;
		int objectType = data.getObjectType();
		String fieldTag = data.getObjectTag();
		int dataEntryType = data.getDataEntryType();
		addFieldWithoutLabel(createDataField(objectType, fieldTag, dataEntryType));
	}

	private ObjectDataInputField createDataField(int objectType, String fieldTag, int dataEntryType) throws Exception
	{
		if(dataEntryType == PropertiesPanelSpec.TYPE_VERY_SHORT_STRING)
			return createShortStringField(objectType, fieldTag);
		
		if(dataEntryType == PropertiesPanelSpec.TYPE_FAIRLY_SHORT_STRING)
			return createMediumStringField(objectType, fieldTag);
		
		if(dataEntryType == PropertiesPanelSpec.TYPE_SINGLE_LINE_STRING)
			return createStringField(objectType, fieldTag);
		
		if(dataEntryType == PropertiesPanelSpec.TYPE_EXPANDING_STRING)
			return createExpandableField(objectType, fieldTag);
		
		if(dataEntryType == PropertiesPanelSpec.TYPE_MULTILINE_STRING)
			return createMultilineField(objectType, fieldTag);

		if(dataEntryType == PropertiesPanelSpec.TYPE_SINGLE_LINE_READONLY_STRING)
			return createReadonlyTextField(objectType, fieldTag);

		throw new RuntimeException(getClass().getName()	+ ": Cannot yet create data entry field of type: " + dataEntryType);
	}
}
