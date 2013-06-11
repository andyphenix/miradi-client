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
package org.miradi.views.targetviability;


import org.miradi.actions.ActionCreateKeyEcologicalAttribute;
import org.miradi.actions.ActionCreateKeyEcologicalAttributeIndicator;
import org.miradi.actions.ActionCreateKeyEcologicalAttributeMeasurement;
import org.miradi.actions.ActionDeleteKeyEcologicalAttribute;
import org.miradi.actions.ActionDeleteKeyEcologicalAttributeIndicator;
import org.miradi.actions.ActionExpandToGoal;
import org.miradi.actions.ActionExpandToHumanWelfareTarget;
import org.miradi.actions.ActionExpandToIndicator;
import org.miradi.actions.ActionExpandToKeyEcologicalAttribute;
import org.miradi.actions.ActionExpandToMeasurement;
import org.miradi.actions.ActionExpandToMenu;
import org.miradi.actions.ActionExpandToTarget;
import org.miradi.dialogs.viability.TargetViabilityManagementPanel;
import org.miradi.main.MainWindow;
import org.miradi.main.MiradiToolBar;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.views.TabbedView;
import org.miradi.views.diagram.CreateViabilityIndicatorDoer;
import org.miradi.views.diagram.CreateViabilityKeyEcologicalAttributeDoer;
import org.miradi.views.diagram.DeleteKeyEcologicalAttributeDoer;
import org.miradi.views.diagram.DeleteViabilityIndicatorDoer;
import org.miradi.views.targetviability.doers.CreateKeyEcologicalAttributeMeasurementDoer;
import org.miradi.views.targetviability.doers.ExpandToGoalDoer;
import org.miradi.views.targetviability.doers.ExpandToHumanWelfareTargetDoer;
import org.miradi.views.targetviability.doers.ExpandToIndicatorDoer;
import org.miradi.views.targetviability.doers.ExpandToKeyEcologicalAttributeDoer;
import org.miradi.views.targetviability.doers.ExpandToMeasurementDoer;
import org.miradi.views.targetviability.doers.ExpandToMenuDoer;
import org.miradi.views.targetviability.doers.ExpandToTargetDoer;
import org.miradi.views.umbrella.UmbrellaView;

public class TargetViabilityView extends TabbedView
{
	public TargetViabilityView(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		addDoersToMap();
	}
	
	@Override
	public String cardName()
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return Project.TARGET_VIABILITY_NAME;
	}

	@Override
	public MiradiToolBar createToolBar()
	{
		return new TargetViabilityToolBar(getActions());
	}

	@Override
	public void createTabs() throws Exception
	{
		viabilityPanelNew = TargetViabilityManagementPanel.createManagementPanel(getMainWindow());
		addNonScrollingTab(viabilityPanelNew);
	}

	@Override
	public void deleteTabs() throws Exception
	{
		viabilityPanelNew.dispose();
		viabilityPanelNew = null;
		
		super.deleteTabs();
	}

	
	private void addDoersToMap()
	{
		addDoerToMap(ActionCreateKeyEcologicalAttribute.class, new CreateViabilityKeyEcologicalAttributeDoer());
		addDoerToMap(ActionDeleteKeyEcologicalAttribute.class, new DeleteKeyEcologicalAttributeDoer());
		addDoerToMap(ActionCreateKeyEcologicalAttributeIndicator.class, new CreateViabilityIndicatorDoer());
		addDoerToMap(ActionDeleteKeyEcologicalAttributeIndicator.class, new DeleteViabilityIndicatorDoer());
		addDoerToMap(ActionCreateKeyEcologicalAttributeMeasurement.class, new CreateKeyEcologicalAttributeMeasurementDoer());
		addDoerToMap(ActionExpandToMenu.class, new ExpandToMenuDoer());
		addDoerToMap(ActionExpandToTarget.class, new ExpandToTargetDoer());
		addDoerToMap(ActionExpandToHumanWelfareTarget.class, new ExpandToHumanWelfareTargetDoer());
		addDoerToMap(ActionExpandToKeyEcologicalAttribute.class, new ExpandToKeyEcologicalAttributeDoer());
		addDoerToMap(ActionExpandToIndicator.class, new ExpandToIndicatorDoer());
		addDoerToMap(ActionExpandToGoal.class, new ExpandToGoalDoer());
		addDoerToMap(ActionExpandToMeasurement.class, new ExpandToMeasurementDoer());
	}
	
	
	@Override
	public BaseObject getSelectedObject()
	{
		if (viabilityPanelNew != null)
			viabilityPanelNew.getObject();
		
		return null;
	}
	
	public static boolean is(UmbrellaView view)
	{
		return view.cardName().equals(getViewName());
	}
	
	private TargetViabilityManagementPanel viabilityPanelNew;
}
