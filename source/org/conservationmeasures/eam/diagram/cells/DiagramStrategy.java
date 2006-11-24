/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram.cells;

import java.awt.Color;

import org.conservationmeasures.eam.diagram.DiagramConstants;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.main.AppPreferences;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.Strategy;

public class DiagramStrategy extends DiagramFactor
{
	public DiagramStrategy(DiagramFactorId idToUse, Strategy cmIntervention)
	{
		super(idToUse, cmIntervention);
	}

	public Color getColor()
	{
		if(isStatusDraft())
			return DiagramConstants.COLOR_DRAFT_STRATEGY;
		
		return EAM.mainWindow.getColorPreference(AppPreferences.TAG_COLOR_INTERVENTION);
	}

}
