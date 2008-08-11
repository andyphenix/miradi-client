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
package org.miradi.dialogs.viability;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.miradi.dialogs.base.ObjectListManagementPanel;
import org.miradi.dialogs.indicator.DirectIndicatorPropertiesPanel;
import org.miradi.dialogs.planning.propertiesPanel.PlanningTreePropertiesPanel;
import org.miradi.dialogs.viability.nodes.ViabilityRoot;
import org.miradi.icons.IndicatorIcon;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.rtf.RtfManagementExporter;
import org.miradi.rtf.RtfWriter;
import org.miradi.utils.AbstractTableExporter;
import org.miradi.utils.BufferedImageFactory;
import org.miradi.utils.SplitterPositionSaverAndGetter;
import org.miradi.utils.TreeTableExporter;

abstract public class TargetViabilityTreeManagementPanel extends ObjectListManagementPanel
{
	protected TargetViabilityTreeManagementPanel(MainWindow mainWindow, SplitterPositionSaverAndGetter splitPositionSaverToUse, ORef factorRef) throws Exception
	{
		super(mainWindow, TargetViabililtyTreePanel.createTargetViabilityPanel(mainWindow, mainWindow.getProject(), factorRef),
				new TargetViabilityTreePropertiesPanel(mainWindow));
	}
	
	protected TargetViabilityTreeManagementPanel(MainWindow mainWindow, SplitterPositionSaverAndGetter splitPositionSaverToUse) throws Exception
	{
		super(mainWindow, TargetViabililtyTreePanel.createTargetViabilityPoolPanel(mainWindow),
				new TargetViabilityTreePropertiesPanel(mainWindow));
	}
	
	protected TargetViabilityTreeManagementPanel(MainWindow mainWindowToUse, ORef factorRef) throws Exception
	{
		super(mainWindowToUse, TargetViabililtyTreePanel.createFactorIndicatorPanel(mainWindowToUse, factorRef, mainWindowToUse.getProject()),
				new DirectIndicatorPropertiesPanel(mainWindowToUse, ORef.INVALID));
	}  
	
	//TODO should use this contructor instead of the constructor that creates DirectIndicatorPropertiesPanel, would be better to have a PlanningTreePropertiesPanel
	public TargetViabilityTreeManagementPanel(MainWindow mainWindowToUse, TargetViabililtyTreePanel treePanel, PlanningTreePropertiesPanel propertiesPanel) throws Exception
	{
		super(mainWindowToUse, treePanel, propertiesPanel);
		panelDescription = PANEL_DESCRIPTION_INDICATORS;
		icon = new IndicatorIcon();
	}
	
	
	public String getPanelDescription()
	{
		return panelDescription;
	}
	
	@Override
	public Icon getIcon()
	{
		return icon;
	}
	
	@Override
	public boolean isImageAvailable()
	{
		return true;
	}
	
	@Override
	public BufferedImage getImage() throws Exception
	{
		BufferedImage image = BufferedImageFactory.createImageFromTable(getTreeTable());
		return image;
	}
	
	@Override
	public boolean isExportableTableAvailable()
	{
		return true;
	}
	
	@Override
	public AbstractTableExporter getTableExporter() throws Exception
	{
		return new TreeTableExporter(getTreeTable());
	}

	private TargetViabilityTreeTable getTreeTable() throws Exception
	{
		ViabilityTreeModel model = new ViabilityTreeModel(new ViabilityRoot(getProject()));
		TargetViabilityTreeTable treeTable = new TargetViabilityTreeTable(getMainWindow(), model);
		treeTable.restoreTreeState();
		return treeTable;
	}
	
	@Override
	public boolean isPrintable()
	{
		return true;
	}
	
	@Override
	public JComponent getPrintableComponent() throws Exception
	{
		return getTreeTable();
	}
	
	public boolean isRtfExportable()
	{
		return true;
	}
		
	public void exportRtf(RtfWriter writer) throws Exception
	{
		new RtfManagementExporter(getProject()).writeManagement(getTableExporter(), writer);
	}
			
	protected String panelDescription;
	protected Icon icon;
	
	private static String PANEL_DESCRIPTION_INDICATORS = EAM.text("Tab|Indicators"); 
}
