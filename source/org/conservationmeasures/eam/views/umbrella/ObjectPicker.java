/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.umbrella;

import org.conservationmeasures.eam.dialogs.treetables.TreeTableNode;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.BaseObject;

public interface ObjectPicker
{
	public ORefList[] getSelectedHierarchies();
	
	//NOTE: No longer use the below methods they are deprecated.  Use getSelectedHierarchies instead
	public ORefList getSelectionHierarchy();
	public BaseObject[] getSelectedObjects();
	//TODO: to be extracted to its own interface (TreeObjectPicker) later
	public TreeTableNode[] getSelectedTreeNodes();

	public void clearSelection();
	public void ensureObjectVisible(ORef ref);
	
	public void addSelectionChangeListener(SelectionChangeListener listener);
	public void removeSelectionChangeListener(SelectionChangeListener listener);
	
	public interface SelectionChangeListener
	{
		public void selectionHasChanged();
	}
}
