/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.diagram.cells.DiagramTargetCell;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;

public class InsertTargetDoer extends InsertFactorDoer
{
	public boolean isAvailable()
	{
		if (!super.isAvailable())
			return false;
		
		return !getDiagramView().isResultsChainTab();
	}
	
	public int getTypeToInsert()
	{
		return ObjectType.TARGET;
	}

	public String getInitialText()
	{
		return EAM.text("Label|New Target");
	}

	public void forceVisibleInLayerManager()
	{
		getProject().getLayerManager().setVisibility(DiagramTargetCell.class, true);
	}
}
