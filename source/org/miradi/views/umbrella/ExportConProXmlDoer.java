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
package org.miradi.views.umbrella;

import java.io.File;
import java.io.FileInputStream;

import org.miradi.xml.conpro.exporter.ConProMiradiXmlValidator;
import org.miradi.xml.conpro.exporter.ConproXmlExporter;

public class ExportConProXmlDoer extends XmlExporter
{
	@Override
	protected void export(File chosen) throws Exception
	{
		new ConproXmlExporter(getProject()).export(chosen);
		if (!new ConProMiradiXmlValidator().isValid(new FileInputStream(chosen)))
			throw new Exception("Could not validate file for importing.");
	}
}
