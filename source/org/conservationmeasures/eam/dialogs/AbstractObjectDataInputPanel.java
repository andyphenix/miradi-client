/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.border.EmptyBorder;

import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.dialogfields.ObjectAdjustableStringInputField;
import org.conservationmeasures.eam.dialogfields.ObjectCheckBoxField;
import org.conservationmeasures.eam.dialogfields.ObjectChoiceField;
import org.conservationmeasures.eam.dialogfields.ObjectClassificationChoiceField;
import org.conservationmeasures.eam.dialogfields.ObjectCodeListField;
import org.conservationmeasures.eam.dialogfields.ObjectCurrencyInputField;
import org.conservationmeasures.eam.dialogfields.ObjectDataInputField;
import org.conservationmeasures.eam.dialogfields.ObjectDateChooserInputField;
import org.conservationmeasures.eam.dialogfields.ObjectIconChoiceField;
import org.conservationmeasures.eam.dialogfields.ObjectMultilineDisplayField;
import org.conservationmeasures.eam.dialogfields.ObjectMultilineInputField;
import org.conservationmeasures.eam.dialogfields.ObjectNumericInputField;
import org.conservationmeasures.eam.dialogfields.ObjectRaitingChoiceField;
import org.conservationmeasures.eam.dialogfields.ObjectReadonlyChoiceField;
import org.conservationmeasures.eam.dialogfields.ObjectReadonlyObjectList;
import org.conservationmeasures.eam.dialogfields.ObjectStringInputField;
import org.conservationmeasures.eam.dialogfields.ObjectStringMapTableField;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.CommandExecutedListener;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.questions.ChoiceQuestion;

abstract public class AbstractObjectDataInputPanel extends ModelessDialogPanel implements CommandExecutedListener
{
	
	public AbstractObjectDataInputPanel(Project projectToUse, int objectType, BaseId idToUse)
	{
		this(projectToUse,new ORef(objectType, idToUse));
	}
	
	
	public AbstractObjectDataInputPanel(Project projectToUse, ORef orefToUse)
	{
		this(projectToUse, new ORef[] {orefToUse});
	}
	
	
	public AbstractObjectDataInputPanel(Project projectToUse, ORef[] orefsToUse)
	{
		project = projectToUse;
		orefs = orefsToUse;
		fields = new Vector();
		project.addCommandExecutedListener(this);
		setBorder(new EmptyBorder(5,5,5,5));
	}
	
	public void dispose()
	{
		project.removeCommandExecutedListener(this);
		super.dispose();
	}

	public Project getProject()
	{
		return project;
	}
	
	public void setObjectRef(ORef oref)
	{
		setObjectRefs(new ORef[] {oref});
	}
	
	public void setObjectRefs(ORef[] orefsToUse)
	{
		saveModifiedFields();
		orefs = orefsToUse;
		updateFieldsFromProject();
	}
	
	public void setFocusOnFirstField()
	{
		//TODO: should be first non read only field.
		if (getFields().size() > 0)
		{
			((ObjectDataInputField)getFields().get(0)).getComponent().requestFocusInWindow();
			Rectangle rect = ((ObjectDataInputField) getFields().get(0)).getComponent().getBounds();
			scrollRectToVisible(rect);
		}
	}
		
	public ObjectDataInputField addField(ObjectDataInputField field)
	{
		getFields().add(field);
		return field;
	}

	abstract public void addFieldComponent(Component component);
	
	
	public ObjectDataInputField createCheckBoxField(String tag, String on, String off)
	{
		return new ObjectCheckBoxField(project, getORef(0).getObjectType(), getObjectIdForType(getORef(0).getObjectType()), tag, on, off);
	}
	
	public ObjectDataInputField createCheckBoxField(int objectType, String tag, String on, String off)
	{
		return new ObjectCheckBoxField(project, objectType, getObjectIdForType(objectType), tag, on, off);
	}
	
	
	public ObjectDataInputField createStringField(String tag)
	{
		return new ObjectStringInputField(project, getORef(0).getObjectType(), getObjectIdForType(getORef(0).getObjectType()), tag, 50);
	}
	
	public ObjectDataInputField createStringField(int objectType, String tag)
	{
		return new ObjectStringInputField(project, objectType, getObjectIdForType(objectType), tag, 50);
	}
	
	
	public ObjectDataInputField createStringField(String tag, int column)
	{
		return new ObjectAdjustableStringInputField(project, getORef(0).getObjectType(), getObjectIdForType(getORef(0).getObjectType()), tag, column);
	}
	
	public ObjectDataInputField createStringField(int objectType, String tag, int column)
	{
		return new ObjectAdjustableStringInputField(project, objectType, getObjectIdForType(objectType), tag, column);
	}
	
	
	public ObjectDataInputField createDateChooserField(String tag)
	{
		return new ObjectDateChooserInputField(project,  getORef(0).getObjectType(), getObjectIdForType( getORef(0).getObjectType()), tag);
	}
	
	public ObjectDataInputField createDateChooserField(int objectType, String tag)
	{
		return new ObjectDateChooserInputField(project, objectType, getObjectIdForType(objectType), tag);
	}
	
	
	public ObjectDataInputField createNumericField(String tag, int column)
	{
		return new ObjectNumericInputField(project, getORef(0).getObjectType(), getObjectIdForType(getORef(0).getObjectType()), tag, column);
	}
	
	public ObjectDataInputField createNumericField(int objectType, String tag, int column)
	{
		return new ObjectNumericInputField(project, objectType, getObjectIdForType(objectType), tag, column);
	}

	public ObjectDataInputField createCurrencyField(int objectType, String tag)
	{
		return new ObjectCurrencyInputField(project, objectType, getObjectIdForType(objectType), tag, 10);
	}
	
	public ObjectDataInputField createCurrencyField(String tag)
	{
		return new ObjectCurrencyInputField(project, getORef(0).getObjectType(), getObjectIdForType(getORef(0).getObjectType()), tag, 10);
	}

	public ObjectDataInputField createNumericField(String tag)
	{
		return new ObjectNumericInputField(project, getORef(0).getObjectType(), getObjectIdForType(getORef(0).getObjectType()), tag);
	}
	
	public ObjectDataInputField createNumericField(int objectType, String tag)
	{
		return new ObjectNumericInputField(project, objectType, getObjectIdForType(objectType), tag);
	}

	public ObjectDataInputField createMultilineField(String tag)
	{
		return new ObjectMultilineInputField(project, getORef(0).getObjectType(), getObjectIdForType(getORef(0).getObjectType()), tag, 50);
	}
	
	public ObjectDataInputField createMultilineField(int objectType, String tag)
	{
		return new ObjectMultilineInputField(project, objectType, getObjectIdForType(objectType), tag, 50);
	}
	
	public ObjectDataInputField createMultilineField(int objectType, String tag, int columns)
	{
		return new ObjectMultilineInputField(project, objectType, getObjectIdForType(objectType), tag, columns);
	}
	
	
	public ObjectDataInputField createMultiCodeField(ChoiceQuestion question)
	{
		return new ObjectCodeListField(project, getORef(0).getObjectType(), getObjectIdForType(getORef(0).getObjectType()), question);
	}
	
	public ObjectDataInputField createMultiCodeField(int objectType, ChoiceQuestion question)
	{
		return new ObjectCodeListField(project, objectType, getObjectIdForType(objectType), question);
	}

	public ObjectDataInputField createReadonlyTextField(String tag)
	{
		return new ObjectMultilineDisplayField(project, getORef(0).getObjectType(), getObjectIdForType(getORef(0).getObjectType()), tag);
	}
	
	public ObjectDataInputField createReadonlyTextField(int objectType, String tag)
	{
		return new ObjectMultilineDisplayField(project, objectType, getObjectIdForType(objectType), tag);
	}
	
	public ObjectDataInputField createChoiceField(int objectType, ChoiceQuestion question)
	{
		return new ObjectChoiceField(project, objectType, getObjectIdForType(objectType), question);
	}
	
	
	public ObjectDataInputField createStringMapTableField(int objectType, ChoiceQuestion question)
	{
		return new ObjectStringMapTableField(project, objectType, getObjectIdForType(objectType), question);
	}
	
	public ObjectDataInputField createClassificationChoiceField(ChoiceQuestion question)
	{
		return new ObjectClassificationChoiceField(project, getORef(0).getObjectType(), getObjectIdForType(getORef(0).getObjectType()), question);
	}
	
	public ObjectDataInputField createClassificationChoiceField(int objectType, ChoiceQuestion question)
	{
		return new ObjectClassificationChoiceField(project, objectType, getObjectIdForType(objectType), question);
	}
	
	public ObjectDataInputField createRatingChoiceField(ChoiceQuestion question)
	{
		return new ObjectRaitingChoiceField(project,  getORef(0).getObjectType(), getObjectIdForType( getORef(0).getObjectType()), question);
	}
	
	public ObjectDataInputField createRatingChoiceField(int objectType, ChoiceQuestion question)
	{
		return new ObjectRaitingChoiceField(project, objectType, getObjectIdForType(objectType), question);
	}

	public ObjectDataInputField createIconChoiceField(int objectType, ChoiceQuestion question)
	{
		return new ObjectIconChoiceField(project, objectType, getObjectIdForType(objectType), question);
	}
	
	public ObjectDataInputField createReadOnlyChoiceField(ChoiceQuestion question)
	{
		return new ObjectReadonlyChoiceField(project,  getORef(0).getObjectType(), getObjectIdForType( getORef(0).getObjectType()), question);
	}
	
	public ObjectDataInputField createReadOnlyChoiceField(int objectType, ChoiceQuestion question)
	{
		return new ObjectReadonlyChoiceField(project, objectType, getObjectIdForType(objectType), question);
	}
	
	public ObjectDataInputField createReadOnlyObjectList(int objectType, String tag)
	{
		return new ObjectReadonlyObjectList(project, objectType, getObjectIdForType(objectType), tag); 
	}
	
	public BaseId getObjectIdForType(int objectType)
	{
		for (int i=0; i<orefs.length; ++i)
		{
			int type = getORef(i).getObjectType();
			if (objectType == type)
				return  getORef(i).getObjectId();
		}
		return BaseId.INVALID;
	}
	
	
	public static BaseId getObjectIdForTypeInThisList(int objectType, ORef[] orefsToUse)
	{
		for (int i=0; i<orefsToUse.length; ++i)
		{
			ORef oref = orefsToUse[i];
			int type = oref.getObjectType();
			if (objectType == type)
				return  oref.getObjectId();
		}
		return BaseId.INVALID;
	}
	
	
	public ORef getORef(int index)
	{
		return orefs[index];
	}
	
	
	public void saveModifiedFields()
	{
		for(int i = 0; i < getFields().size(); ++i)
		{
			ObjectDataInputField field = (ObjectDataInputField) getFields().get(i);
			field.saveIfNeeded();
		}
	}
	
	public void updateFieldsFromProject()
	{
		setFieldObjectIds();
		for(int i = 0; i < getFields().size(); ++i)
		{
			ObjectDataInputField field = (ObjectDataInputField) getFields().get(i);
			field.updateFromObject();
		}
		
	}

	public Vector getFields()
	{
		return fields;
	}
	
	public void setFieldObjectIds()
	{
		for(int i = 0; i < getFields().size(); ++i)
		{
			ObjectDataInputField field = (ObjectDataInputField) getFields().get(i);
			field.setObjectId(getObjectIdForType(field.getObjectType()));
		}
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		if(wasOurObjectJustDeleted(event))
		{
			CommandDeleteObject cmd = (CommandDeleteObject)event.getCommand();
			deleteObjectFromList(cmd.getObjectId());
			setFieldObjectIds();
			return;
		}
		updateFieldsFromProject();
	}
	

	public void deleteObjectFromList(BaseId baseId)
	{
		Vector orefList = new Vector(Arrays.asList(orefs));
		for (int i=0; i<orefs.length; ++i)
		{
			BaseId objectId = getORef(i).getObjectId();
			if (objectId.equals(baseId))
				orefList.remove(i);
		}
		orefs = (ORef[])orefList.toArray(new ORef[0]);
	}

	boolean wasOurObjectJustDeleted(CommandExecutedEvent event)
	{
		if(!event.isDeleteObjectCommand())
			return false;
		
		CommandDeleteObject cmd = (CommandDeleteObject)event.getCommand();
		if(!cmd.getObjectId().equals(getObjectIdForType(cmd.getObjectType())))
			return false;
		return true;
	}

	public BaseObject getObject()
	{
		return null;
	}

	public BaseId getObjectId()
	{
		return getORef(orefs.length-1).getObjectId();
	}

	public static int STD_SHORT = 5;
	
	private Project project;
	private ORef[] orefs;
	private Vector fields;
}

