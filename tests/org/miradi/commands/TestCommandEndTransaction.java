/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.main.EAMTestCase;

public class TestCommandEndTransaction extends EAMTestCase
{
	public TestCommandEndTransaction(String name)
	{
		super(name);
	}

	public void testGetReverseCommand() throws Exception
	{
		CommandEndTransaction commandEndTrasaction = new CommandEndTransaction();
		CommandBeginTransaction reverseCommand = (CommandBeginTransaction) commandEndTrasaction.getReverseCommand();
		
		assertEquals("not same command?", new CommandBeginTransaction(), reverseCommand);	
	}
}
