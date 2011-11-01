/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.views.diagram;

import org.miradi.dialogs.base.ModelessDialogPanel;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objects.BaseObject;
import org.miradi.views.umbrella.LegendPanel;

public class LayerPanel extends ModelessDialogPanel
{
	public LayerPanel(MainWindow mainWindowToUse) throws Exception
	{
		legendPanel = createLegendPanel(mainWindowToUse);
		add(legendPanel);
	}

	@Override
	public void dispose()
	{
		if (legendPanel != null)
		{
			legendPanel.dispose();
			legendPanel = null;
		}
		
		super.dispose();
	}
	
	private LegendPanel createLegendPanel(MainWindow mainWindowToUse) throws Exception
	{
		if (mainWindowToUse.getDiagramView().isResultsChainTab())
			return new ResultsChainDiagramLegendPanel(mainWindowToUse);

		return new ConceptualModelDiagramLegendPanel(mainWindowToUse);
	}

	@Override
	public BaseObject getObject()
	{
		return null;
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Layers");
	}
	
	private LegendPanel legendPanel;
}
