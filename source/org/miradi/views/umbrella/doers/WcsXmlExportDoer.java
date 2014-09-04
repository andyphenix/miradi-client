/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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

package org.miradi.views.umbrella.doers;

import org.miradi.utils.MiradiFileSaveChooser;
import org.miradi.utils.WcsZipFileChooser;
import org.miradi.xml.wcs.WcsMiradiXmlValidator;

public class WcsXmlExportDoer extends AbstractExportProjectXmlZipDoer
{
	@Override
	protected MiradiFileSaveChooser createFileChooser()
	{
		return new WcsZipFileChooser(getMainWindow());
	}
	
	@Override
	protected String getSchemaRelativeFilePath()
	{
		return WcsMiradiXmlValidator.WCS_MIRADI_SCHEMA_FILE_RELATIVE_PATH;
	}
	
	@Override
	protected boolean doesUserConfirm() throws Exception
	{
		return ExportXmpzDoer.doesUserConfirmXmpz1Export();
	}
}
