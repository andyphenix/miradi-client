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
package org.miradi.commands;

import java.text.ParseException;
import java.util.HashMap;

import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.ids.TemporaryIdList;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.utils.PointList;

public class CommandSetObjectData extends Command
{
	static public CommandSetObjectData createNewPointList(BaseObject object, String listTag, PointList newList)
	{
		String newListAsString = newList.toJson().toString();
		
		return new CommandSetObjectData(object.getType(), object.getId(), listTag, newListAsString);
	}
	
	static public CommandSetObjectData createAppendIdsCommand(BaseObject object, String idListTag, IdList idsToAppend) throws ParseException
	{
		ensureIdList(object, idListTag);
		
		IdList newList = new IdList(idsToAppend.getObjectType(), object.getData(idListTag));
		newList.addAll(idsToAppend);
		
		return new CommandSetObjectData(object.getType(), object.getId(), idListTag, newList.toString());
	}
	
	public static CommandSetObjectData createAppendIdCommand(BaseObject object, String idListTag, ORef ref) throws ParseException
	{
		return createAppendIdCommand(object, idListTag, ref.getObjectId());
	}
	
	static public CommandSetObjectData createAppendIdCommand(BaseObject object, String idListTag, BaseId idToAppend) throws ParseException
	{
		ensureIdList(object, idListTag);
		
		TemporaryIdList newList = new TemporaryIdList(object.getData(idListTag));
		newList.add(idToAppend);
		return new CommandSetObjectData(object.getType(), object.getId(), idListTag, newList.toString());
	}

	static public CommandSetObjectData createInsertIdCommand(BaseObject object, String idListTag, BaseId idToInsert, int position) throws ParseException
	{
		ensureIdList(object, idListTag);
		
		TemporaryIdList newList = new TemporaryIdList(object.getData(idListTag));
		newList.insertAt(idToInsert, position);
		return new CommandSetObjectData(object.getType(), object.getId(), idListTag, newList.toString());
	}
	
	public static CommandSetObjectData createRemoveIdCommand(BaseObject object, String idListTag, ORef refToRemove) throws ParseException
	{
		return createRemoveIdCommand(object, idListTag, refToRemove.getObjectId());
	}

	static public CommandSetObjectData createRemoveIdCommand(BaseObject object, String idListTag, BaseId idToRemove) throws ParseException
	{
		ensureIdList(object, idListTag);
		
		TemporaryIdList newList = new TemporaryIdList(object.getData(idListTag));
		newList.removeId(idToRemove);
		return new CommandSetObjectData(object.getType(), object.getId(), idListTag, newList.toString());
	}
	
	static public CommandSetObjectData createRemoveORefCommand(BaseObject object, String oRefListTag, ORef oRefToRemove) throws ParseException
	{
		ensureRefList(object, oRefListTag);
		
		ORefList newList = new ORefList(object.getData(oRefListTag));
		newList.remove(oRefToRemove);
		return new CommandSetObjectData(object.getType(), object.getId(), oRefListTag, newList.toString());
	}

	//TODO find a better method name
	static public CommandSetObjectData createAppendListCommand(BaseObject object, String idListTag, IdList listToAppend) throws ParseException
	{
		ensureIdList(object, idListTag);
		
		IdList newList = new IdList(listToAppend.getObjectType(), object.getData(idListTag));
		newList.addAll(listToAppend);
		return new CommandSetObjectData(object.getType(), object.getId(), idListTag, newList.toString());
	}
	
	static public CommandSetObjectData createAppendORefCommand(BaseObject object, String oRefListTag, ORef oRefToAppend) throws ParseException
	{
		ensureRefList(object, oRefListTag);
		
		ORefList newList = new ORefList(object.getData(oRefListTag));
		newList.add(oRefToAppend);
		return new CommandSetObjectData(object.getType(), object.getId(), oRefListTag, newList.toString());
	}
	
	static public CommandSetObjectData createAppendORefListCommand(BaseObject object, String idListTag, ORefList refListToAppend) throws ParseException
	{
		ensureRefList(object, idListTag);
		
		ORefList newList = new ORefList(object.getData(idListTag));
		newList.addAll(refListToAppend);
		return new CommandSetObjectData(object.getType(), object.getId(), idListTag, newList.toString());
	}
	
	public CommandSetObjectData(ORef objectRef, String fieldTag, ORef oref)
	{
		this(objectRef.getObjectType(), objectRef.getObjectId(), fieldTag, oref.toString());
	}
	
	public CommandSetObjectData(BaseObject baseObject, String fieldTag, ORefList refList)
	{
		this(baseObject.getRef(), fieldTag, refList.toString());
	}
	
	public CommandSetObjectData(BaseObject baseObject, String fieldTag, ORefSet refSet)
	{
		this(baseObject, fieldTag, refSet.toRefList());
	}
	
	public CommandSetObjectData(BaseObject baseObject, String fieldTag, String dataValue)
	{
		this(baseObject.getRef(), fieldTag, dataValue);
	}
	
	public CommandSetObjectData(ORef objectRef, String fieldTag, String dataValue)
	{
		this(objectRef.getObjectType(), objectRef.getObjectId(), fieldTag, dataValue);
	}

	public CommandSetObjectData(int objectType, BaseId objectId, String fieldTag, String dataValue)
	{
		type = objectType;
		id = objectId;
		tag = fieldTag;
		newValue = dataValue;
	}
	
	public ORef getObjectORef()
	{
		return new ORef(getObjectType(), getObjectId());
	}
	
	public int getObjectType()
	{
		return type;
	}
	
	public BaseId getObjectId()
	{
		return id;
	}
	
	public String getFieldTag()
	{
		return tag;
	}
	
	public String getDataValue()
	{
		return newValue;
	}
	
	public String getPreviousDataValue()
	{
		return oldValue;
	}
	
	public void setPreviousDataValue(String forcedOldValue)
	{
		oldValue = forcedOldValue;
	}

	@Override
	public String getCommandName()
	{
		return COMMAND_NAME;
	}
	
	@Override
	public boolean isDoNothingCommand(Project project) throws CommandFailedException
	{
		try
		{
			String dataValue = getDataValue();
			String existingData = getExistingData(project);
			return dataValue.equals(existingData);
		}
		catch(RuntimeException e)
		{
			throw new CommandFailedException(e);
		}
	}
	
	public boolean isRefAndTag(ORef refToUse, String tagToUse)
	{
		if (!getObjectORef().equals(refToUse))
			return false;
		
		return isJustTagInAnyType(tagToUse);
	}

	public boolean isJustTagInAnyType(String tagToUse)
	{
		return getFieldTag().equals(tagToUse);
	}
	
	public boolean isTypeAndTag(int objectType, String tagToUse)
	{
		if (getObjectType() != objectType)
			return false;
		
		return isJustTagInAnyType(tagToUse);
	}
	
	@Override
	public String toString()
	{
		return getCommandName() + ": " + getObjectType() + ", " + getObjectId() + ", [" + getFieldTag() + "] = [" + getDataValue() + "]";
	}

	@Override
	public void execute(Project project) throws CommandFailedException
	{
		try
		{
			oldValue = getExistingData(project);
			project.setObjectData(type, id, tag, newValue);		
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
	}

	private String getExistingData(Project target)
	{
		return target.getObjectData(type, id, tag);
	}

	@Override
	public Command getReverseCommand() throws CommandFailedException
	{
		CommandSetObjectData commandSetObjectData = new CommandSetObjectData(type, id, tag, oldValue);
		commandSetObjectData.setPreviousDataValue(newValue);
		
		return commandSetObjectData;
	}

	@Override
	public HashMap getLogData()
	{
		HashMap<String, Comparable> dataPairs = new HashMap<String, Comparable>();
		dataPairs.put("OBJECT_TYPE", new Integer(type));
		dataPairs.put(BaseId.class.getSimpleName(), id);
		dataPairs.put("TAG", tag);
		dataPairs.put("NEW_VALUE", newValue);
		dataPairs.put("PREVIOUS_VALUE", oldValue);
		
		return dataPairs;
	}
	
	private static void ensureIdList(BaseObject object, String idListTag)
	{
		if (!object.isIdListTag(idListTag))
			throw new RuntimeException("Tag is not a idList tag:" + idListTag + " for object type:" + object.getType());
	}
	
	private static void ensureRefList(BaseObject object, String refListTag)
	{
		if (!object.isRefList(refListTag))
			throw new RuntimeException("Tag is not a refList tag:" + refListTag + " for object type:" + object.getType());
	}
	
	public static final String COMMAND_NAME = "SetObjectData";

	private int type;
	private BaseId id;
	private String tag;
	private String newValue;
	private String oldValue;
}
