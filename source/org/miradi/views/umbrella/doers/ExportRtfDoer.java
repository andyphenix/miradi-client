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
package org.miradi.views.umbrella.doers;

import java.io.File;

import org.miradi.main.EAM;
import org.miradi.rtf.RtfWriter;
import org.miradi.utils.RtfFileChooser;
import org.miradi.views.ViewDoer;

public class ExportRtfDoer extends ViewDoer
{
	@Override
	public boolean isAvailable()
	{
		if (!isProjectOpen())
			return false;
		
		return getView().isRtfExportable();
	}

	@Override
	protected void doIt() throws Exception
	{
		if (!isAvailable())
			return;
		
		RtfFileChooser rtfFileChooser = new RtfFileChooser(getMainWindow());
		File destination = rtfFileChooser.displayChooser();
		if (destination == null) 
			return;

		try
		{
			writeRtf(destination);
			EAM.notifyDialog(EAM.text("Current page was exported as RTF."));
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog(EAM.text("Error occurred while trying to export current page as RTF.\n") + e.getMessage());
		}
	}
	
	private void writeRtf(File destination) throws Exception
	{
		RtfWriter rtfWriter = new RtfWriter(destination);
		try
		{
			rtfWriter.startRtf();
			rtfWriter.landscapeMode();
			getView().exportRtf(rtfWriter);
			rtfWriter.endRtf();
		}
		finally
		{
			rtfWriter.close();
		}
	}
}
