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
package org.miradi.views;

import org.miradi.views.diagram.DiagramView;
import org.miradi.views.planning.PlanningView;
import org.miradi.views.umbrella.UmbrellaView;

abstract public class ViewDoer extends MainWindowDoer
{
	@Override
	public boolean isAvailable()
	{
		return true;
	}
	
	@Override
	public void setView(UmbrellaView view)
	{
		this.view = view;
	}

	public UmbrellaView getView()
	{
		return view;
	}
	
	public DiagramView getDiagramView() throws RuntimeException
	{
		if (DiagramView.is(view))
			return (DiagramView)getView();
		
		throw new RuntimeException("Not in DiagramView");
	}
	
	public PlanningView getPlanningView() throws RuntimeException
	{
		if (isPlanningView())
			return (PlanningView) getView();
		
		throw new RuntimeException("Not in PlanningView");
	}

	public boolean isPlanningView()
	{
		if (! getView().cardName().equals(PlanningView.getViewName()))
			return false;
		
		return true;
	}
	
	public boolean isInDiagram()
	{
		if (! DiagramView.is(getView()))
			return false;
		
		if (getDiagramView().getCurrentDiagramComponent() == null)
			return false;
		
		if (getDiagramView().getDiagramModel() == null)
			return false;
		
		return true;
	}

	private UmbrellaView view;
}
