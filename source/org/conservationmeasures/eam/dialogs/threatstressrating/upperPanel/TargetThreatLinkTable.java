/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.threatstressrating.upperPanel;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.Target;

public class TargetThreatLinkTable extends TableWithSetPreferredScrollableViewportHeight
{
	public TargetThreatLinkTable(TargetThreatLinkTableModel tableModel)
	{
		super(tableModel);
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setForcedPreferredScrollableViewportWidth(TargetThreatLinkTable.PREFERRED_VIEWPORT_WIDTH);
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public TargetThreatLinkTableModel getTargetThreatLinkTableModel()
	{
		return (TargetThreatLinkTableModel) getModel();
	}
	
	public ORefList[] getSelectedHierarchies()
	{
		int threatIndex = getSelectedRow();
		Factor directThreat = getTargetThreatLinkTableModel().getDirectThreat(threatIndex);
		
		int tableColumn = getSelectedColumn();
		int modelColumn = convertColumnIndexToModel(tableColumn);
		ORefList hierarchyRefs = new ORefList();
		if (modelColumn < 0)
			return new ORefList[0];
		
		Target target = getTargetThreatLinkTableModel().getTarget(modelColumn);
		ORef targetRef = target.getRef();
		if (getTargetThreatLinkTableModel().isLinked(directThreat, target))
		{
			ORef linkRef = getTargetThreatLinkTableModel().getLinkRef(directThreat, target);
			hierarchyRefs.add(linkRef);
		}
		hierarchyRefs.add(targetRef);
		hierarchyRefs.add(directThreat.getRef());
		
		return new ORefList[]{hierarchyRefs};
	}
	
	public String getUniqueTableIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}

	public static final String UNIQUE_IDENTIFIER = "TargetThreatLinkTable";
	public static final int PREFERRED_VIEWPORT_WIDTH = 400;
	public static final int PREFERRED_VIEWPORT_HEIGHT = 100;
}
