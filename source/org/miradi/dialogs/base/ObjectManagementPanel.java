/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.dialogs.base;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.miradi.actions.ObjectsAction;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.utils.ExportableTableInterface;
import org.miradi.utils.SplitterPositionSaverAndGetter;
import org.miradi.views.MiradiTabContentsPanelInterface;

abstract public class ObjectManagementPanel extends VerticalSplitPanel implements MiradiTabContentsPanelInterface
{
	public ObjectManagementPanel(Project projectToUse, SplitterPositionSaverAndGetter splitPositionSaverToUse, ObjectCollectionPanel tablePanelToUse, AbstractObjectDataInputPanel propertiesPanelToUse) throws Exception
	{
		this(splitPositionSaverToUse, tablePanelToUse, propertiesPanelToUse);
		project = projectToUse;
	}
	
	public ObjectManagementPanel(SplitterPositionSaverAndGetter splitPositionSaverToUse, ObjectCollectionPanel tablePanelToUse, AbstractObjectDataInputPanel propertiesPanelToUse) throws Exception
	{
		super(splitPositionSaverToUse, tablePanelToUse, propertiesPanelToUse);
		listComponent = tablePanelToUse;
		
		propertiesPanel = propertiesPanelToUse;
		listComponent.setPropertiesPanel(propertiesPanel);
	}
	
	public void dispose()
	{
		listComponent.dispose();
		listComponent = null;
		
		propertiesPanel.dispose();
		propertiesPanel = null;
		
		super.dispose();
	}

	public void addTablePanelButton(ObjectsAction action)
	{
		listComponent.addButton(action);
	}
	
	public Project getProject()
	{
		return project;
	}

	public BaseObject getObject()
	{
		if(listComponent == null)
			return null;
		return listComponent.getSelectedObject();
	}
	
	public DisposablePanel getTabContentsComponent()
	{
		return this;
	}

	public Icon getIcon()
	{
		return null;
	}

	public String getTabName()
	{
		return getPanelDescription();
	}
	
	public boolean isImageAvailable()
	{
		return false;
	}
	
	public BufferedImage getImage() throws Exception
	{
		return null;
	}
	
	public boolean isExportableTableAvailable()
	{
		return false;
	}
	
	public ExportableTableInterface getExportableTable() throws Exception
	{
		return null;
	}
	
	public JComponent getPrintableComponent() throws Exception
	{
		return null;
	}
	
	public boolean isPrintable()
	{
		return false;
	}
	
	private Project project;
	private ObjectCollectionPanel listComponent;
	public AbstractObjectDataInputPanel propertiesPanel;
}
