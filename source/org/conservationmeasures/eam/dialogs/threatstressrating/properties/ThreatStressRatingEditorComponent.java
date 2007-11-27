/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.threatstressrating.properties;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JScrollPane;

import org.conservationmeasures.eam.dialogs.base.MultiTablePanel;
import org.conservationmeasures.eam.dialogs.threatstressrating.ThreatStressRatingTableModel;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.project.Project;

public class ThreatStressRatingEditorComponent extends MultiTablePanel
{
	public ThreatStressRatingEditorComponent(Project projectToUse) throws Exception
	{
		super(projectToUse);
		
		createTables();
		addTables();
	}
	
	private void createTables() throws Exception
	{
		threatStressRatingTableModel = new ThreatStressRatingTableModel(getProject(), getSelectedThreatStressRatingRef());
		threatStressRatingTable = new ThreatStressRatingTable(threatStressRatingTableModel);
	}
	
	private void addTables()
	{
		Box horizontalBox = Box.createHorizontalBox();
		JScrollPane resourceScroller = new ScrollPaneWithInvisibleVerticalScrollBar(threatStressRatingTable);
		addVerticalScrollableControlledTable(horizontalBox, resourceScroller);

		add(horizontalBox, BorderLayout.CENTER);
	}

	public void setObjectRefs(ORef[] hierarchyToSelectedRef)
	{
		threatStressRatingTableModel.setObjectRefs(hierarchyToSelectedRef);
		threatStressRatingTableModel.fireTableDataChanged();
		threatStressRatingTable.repaint();
	}
	
	public ORefList[] getSelectedHierarchies()
	{
		return new ORefList[0];
	}
	
	private ORef getSelectedThreatStressRatingRef()
	{
		return ORef.INVALID;
	}
	
	private ThreatStressRatingTableModel threatStressRatingTableModel;
	private ThreatStressRatingTable threatStressRatingTable;
}
