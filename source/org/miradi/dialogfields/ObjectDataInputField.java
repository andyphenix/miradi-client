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
package org.miradi.dialogfields;


import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.objectdata.ObjectData;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.utils.InvalidDateException;
import org.miradi.utils.InvalidNumberException;

abstract public class ObjectDataInputField extends ObjectDataField
{
	public ObjectDataInputField(Project projectToUse, ORef refToUse, String tagToUse)
	{
		super(projectToUse, refToUse);
		
		tag = tagToUse;
		allowEdits = true;
	}
	
	public void forceSave()
	{
		setNeedsSave();
		saveIfNeeded();
	}
	
	public void setTag(String tagToUse)
	{
		tag = tagToUse;
	}
	
	@Override
	public String getTag()
	{
		return tag;
	}
	
	@Override
	public void updateFromObject()
	{
		saveIfNeeded();
		updateEditableState();
		String text = "";
		if(isValidObject())
			text = getProject().getObjectData(getORef(), getTag());
		if (text.equals(getText()))
			return;
		setText(text);
	}
	
	public boolean allowEdits()
	{
		return allowEdits;
	}

	public void setEditable(boolean newState)
	{
		allowEdits = newState;
		updateEditableState();
	}

	public boolean needsToBeSaved()
	{
		return needsSave;
	}
	
	public void setNeedsSave()
	{
		if(getORef().isInvalid())
			return;
		
		needsSave = hasDataChanged();
	}

	private boolean hasDataChanged()
	{
		try
		{
			BaseObject object = BaseObject.find(getProject(), getORef());
			ObjectData field = object.getField(getTag());
			return !field.isCurrentValue(getText());
		}
		catch (Exception e)
		{
			EAM.panic(e);
			return false;
		}
	}
	
	public void clearNeedsSave()
	{
		needsSave = false;
	}
	
	@Override
	public void saveIfNeeded()
	{
		if(!needsToBeSaved())
			return;
		
		clearNeedsSave();

		if(!isValidObject())
			return;
		
		if(!hasDataChanged())
			return;
		
		String newValue = getText();
		String existingValue = getProject().getObjectData(getORef(), getTag());

		CommandSetObjectData cmd = new CommandSetObjectData(getORef(), getTag(), newValue);
		try
		{
			getProject().executeCommand(cmd);
			updateFromObject();
		}
		catch(CommandFailedException e)
		{
			EAM.logException(e);
			notifyUserOfFailure(e);
			setText(existingValue);
			getComponent().requestFocus();
		}
	}

	private void notifyUserOfFailure(CommandFailedException cfe)
	{
		try
		{
			throw(cfe.getCause());
		}
		catch (InvalidDateException ide)
		{
			EAM.errorDialog(EAM.text("Text|Dates must be in YYYY-MM-DD format"));
		}
		catch (InvalidNumberException ine)
		{
			EAM.errorDialog(getNumericErrorMessage());
		}
		catch(NumberFormatException nfe)
		{
			EAM.errorDialog(getNumericErrorMessage());
		}
		catch(Throwable e)
		{
			EAM.errorDialog(EAM.text("Text|Unknown error prevented saving this data"));
		}
	}
	
	private String getNumericErrorMessage()
	{
		return "<html>" + EAM.text("This value must be numeric<br><br>" +
		"Currency symbols and percent signs are not allowed");
	}

	abstract public String getText();

	abstract public void setText(String newValue);
	
	private String tag;
	private boolean allowEdits;
	private boolean needsSave;
}
