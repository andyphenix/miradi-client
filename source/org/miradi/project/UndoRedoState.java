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
package org.miradi.project;

import java.io.IOException;

import org.miradi.commands.Command;
import org.miradi.exceptions.NothingToRedoException;
import org.miradi.exceptions.NothingToUndoException;
import org.miradi.utils.CommandVector;

public class UndoRedoState
{
	public UndoRedoState()
	{
		undoableCommands = new CommandVector();
		redoableCommands = new CommandVector();
	}
	
	public int size()
	{
		return undoableCommands.size();
	}

	public boolean canUndo()
	{
		return (undoableCommands.size() > 0);
	}
	
	public boolean canRedo()
	{
		return (redoableCommands.size() > 0);
	}
	
	public Command popCommandToUndo() throws NothingToUndoException
	{
		if(!canUndo())
			throw new NothingToUndoException();
		Command cmd = undoableCommands.remove(0);
		redoableCommands.insertElementAt(cmd, 0);
		return cmd;
	}
	
	public Command popCommandToRedo() throws NothingToRedoException
	{
		if(!canRedo())
			throw new NothingToRedoException();
		Command cmd = redoableCommands.remove(0);
		undoableCommands.insertElementAt(cmd, 0);
		return cmd;
	}

	public void pushUndoableCommand(Command command) throws IOException
	{
		redoableCommands.clear();
		undoableCommands.insertElementAt(command, 0);
	}

	public Command getLastRecordedCommand()
	{
		if(!canUndo())
			return null;
		return undoableCommands.get(0);
	}

	public void discardLastUndoableCommand()
	{
		undoableCommands.remove(0);
	}

	CommandVector undoableCommands;
	CommandVector redoableCommands;
}
