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

package org.miradi.xml.generic;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

public class SchemaWriter extends PrintWriter
{
	public SchemaWriter(StringWriter writer)
	{
		super(writer);
	}
	
	public SchemaWriter(PrintStream out)
	{
		super(out);
	}

	public void defineAlias(String alias, String elementName)
	{
		println(alias + " = " + elementName);
	}

	public void startBlock()
	{
		printlnIndented("{");
		++indentLevel;
	}

	public void endBlock()
	{
		--indentLevel;
		printlnIndented("}");
		println();
	}
	
	public void printIndented(String text)
	{
		for(int i = 0; i < indentLevel; ++i)
			print("  ");
		print(text);
	}
	
	public void printlnIndented(String text)
	{
		printIndented(text);
		println();
	}

	public void startElementDefinition(String name)
	{
		println(name + ".element = element " + name);
		startBlock();
	}

	public void endElementDefinition(String name)
	{
		endBlock();
	}
	
	public void writeContentsList(Vector<String> contents)
	{
		for(int element = 0; element < contents.size(); ++element)
		{
			String item = contents.get(element);
			printIndented(item);
			if(element+1 < contents.size())
				print(" &");
			println();
		}
		
	}

	private int indentLevel;
}
