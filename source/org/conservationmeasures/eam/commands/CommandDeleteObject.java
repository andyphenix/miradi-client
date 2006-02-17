/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.project.Project;

public class CommandDeleteObject extends Command
{
	public CommandDeleteObject(int objectType, int objectId)
	{
		type = objectType;
		id = objectId;
	}
	
	public CommandDeleteObject(DataInputStream dataIn) throws IOException
	{
		type = dataIn.readInt();
		id = dataIn.readInt();
	}
	
	public int getObjectType()
	{
		return type;
	}
	
	public int getObjectId()
	{
		return id;
	}

	public String getCommandName()
	{
		return COMMAND_NAME;
	}

	public void execute(Project target) throws CommandFailedException
	{
		try
		{
			target.deleteObject(type, id);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}

	public void undo(Project target) throws CommandFailedException
	{
		try
		{
			target.createObject(type, id);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
	
	public void writeDataTo(DataOutputStream dataOut) throws IOException
	{
		dataOut.writeInt(type);
		dataOut.writeInt(id);
	}

	public final static String COMMAND_NAME = "ComnandDeleteObject";

	int type;
	int id;
}
