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
package org.miradi.views;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.miradi.dialogs.base.DisposablePanelWithDescription;
import org.miradi.rtf.RtfWriter;
import org.miradi.utils.TableExporter;

public interface MiradiTabContentsPanelInterface
{
	public String getTabName();
	public Icon getIcon();
	public DisposablePanelWithDescription getTabContentsComponent();
	public boolean isImageAvailable();
	public BufferedImage getImage(int scale) throws Exception;
	public boolean isExportableTableAvailable();
	public TableExporter getTableExporter() throws Exception;
	public JComponent getPrintableComponent() throws Exception;
	public boolean isPrintable();
	public Class getJumpActionClass();
	public boolean isRtfExportable();
	public void exportRtf(RtfWriter writer) throws Exception;
	public void becomeActive();
	public void becomeInactive();
}
