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
package org.miradi.dialogs.organization;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.miradi.actions.jump.ActionJumpSummaryWizardRolesAndResponsibilities;
import org.miradi.dialogs.base.ObjectPoolManagementPanel;
import org.miradi.icons.OrganizationIcon;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.utils.BufferedImageFactory;
import org.miradi.utils.ImageTooLargeException;
import org.miradi.utils.ObjectTableModelExporter;
import org.miradi.utils.TableExporter;

public class OrganizationManagementPanel extends ObjectPoolManagementPanel
{
	public OrganizationManagementPanel(MainWindow mainWindowToUse) throws Exception
	{
		super(mainWindowToUse, new OrganizationPoolTablePanel(mainWindowToUse),
				new OrganizationPropertiesPanel(mainWindowToUse.getProject()));

	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Other Orgs");
	}
	
	@Override
	public Icon getIcon()
	{
		return new OrganizationIcon();
	}
	
	@Override
	public boolean isExportableTableAvailable()
	{
		return true;
	}
	
	@Override
	public TableExporter getTableExporter() throws Exception
	{
		return new ObjectTableModelExporter(createTableModel());
	}

	private OrganizationPoolTable createTable()
	{
		return new OrganizationPoolTable(getMainWindow(), createTableModel());
	}

	private OrganizationPoolTableModel createTableModel()
	{
		return new OrganizationPoolTableModel(getProject());
	}
	
	@Override
	public boolean isPrintable()
	{
		return true;
	}
	
	@Override
	public JComponent getPrintableComponent() throws Exception
	{
		return createTable();
	}
	
	@Override
	public boolean isImageAvailable()
	{
		return true;
	}
	
	@Override
	public BufferedImage getImage(int scale) throws ImageTooLargeException
	{
		return BufferedImageFactory.createImageFromTable(createTable());
	}
		
	@Override
	public Class getJumpActionClass()
	{
		return ActionJumpSummaryWizardRolesAndResponsibilities.class;
	}
}
