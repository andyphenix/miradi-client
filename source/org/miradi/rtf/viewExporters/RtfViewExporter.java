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
package org.miradi.rtf.viewExporters;

import org.miradi.dialogs.base.AbstractObjectTableModel;
import org.miradi.main.MainWindow;
import org.miradi.project.Project;
import org.miradi.rtf.RtfManagementExporter;
import org.miradi.rtf.RtfWriter;
import org.miradi.utils.CodeList;
import org.miradi.utils.ObjectTableModelExporter;
import org.miradi.utils.TableExporter;

abstract public class RtfViewExporter
{
	public RtfViewExporter(MainWindow mainWindowToUse)
	{
		mainWindow = mainWindowToUse;
	}
	
	protected void exportObjectTableModel(RtfWriter writer, AbstractObjectTableModel objectTableModel, String translatedTableName) throws Exception
	{
		exportTableWithPageBreak(writer, new ObjectTableModelExporter(objectTableModel), translatedTableName);
	}
	
	protected void exportTable(RtfWriter writer, TableExporter tableExporter, String translatedTableName) throws Exception
	{
		writer.startBlock();
		writer.writeHeading2Style();
		writer.writelnEncoded(translatedTableName);
		writer.writeParCommand();
		writer.endBlock();
		writer.newParagraph();
		
		createRtfManagementRtfExporter().writeManagement(tableExporter, writer);
		writer.newParagraph();
	}
	
	protected void writeHeader(RtfWriter writer, String headerText) throws Exception
	{
		writer.startBlock();
		writer.writeHeading1Style();
		writer.writeEncoded(headerText);
		writer.writeParCommand();
		writer.endBlock();
		writer.newParagraph();
	}

	protected void exportTableWithPageBreak(RtfWriter writer, TableExporter tableExporter, String translatedTableName) throws Exception
	{
		exportTable(writer, tableExporter, translatedTableName);
		writer.pageBreak();
	}
	
	private RtfManagementExporter createRtfManagementRtfExporter()
	{
		return new RtfManagementExporter(getProject());
	}
		
	protected Project getProject()
	{
		return getMainWindow().getProject();
	}
	
	protected MainWindow getMainWindow()
	{
		return mainWindow;
	}

	abstract public void exportView(RtfWriter writer, CodeList reportTemplateContent) throws Exception;
	
	private MainWindow mainWindow;
}
