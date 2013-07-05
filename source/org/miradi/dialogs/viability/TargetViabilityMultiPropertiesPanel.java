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
package org.miradi.dialogs.viability;

import org.miradi.dialogs.base.AbstractMultiPropertiesPanel;
import org.miradi.dialogs.base.AbstractObjectDataInputPanel;
import org.miradi.dialogs.planning.MeasurementPropertiesPanel;
import org.miradi.dialogs.planning.propertiesPanel.BlankPropertiesPanel;
import org.miradi.dialogs.strategicPlan.IndicatorPropertiesPanelWithoutBudgetPanels;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.FutureStatus;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.Target;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.schemas.TargetSchema;

public class TargetViabilityMultiPropertiesPanel extends AbstractMultiPropertiesPanel
{
	public TargetViabilityMultiPropertiesPanel(MainWindow mainWindow) throws Exception
	{
		super(mainWindow, new ORef(ObjectType.TARGET, new FactorId(BaseId.INVALID.asInt())));		
				
		blankPropertiesPanel = new BlankPropertiesPanel(getProject());
		targetPropertiesPanel = new NonDiagramAbstractTargetPropertiesPanel(getProject(), TargetSchema.getObjectType());
		humanWelfareTargetPropertiesPanel = new NonDiagramAbstractTargetPropertiesPanel(getProject(), HumanWelfareTargetSchema.getObjectType());
		targetViabilityKeaPropertiesPanel = new TargetViabilityKeaPropertiesPanel(getProject(), mainWindow.getActions());
		targetViabilityIndicatorPropertiesPanel = new IndicatorPropertiesPanelWithoutBudgetPanels(getProject());
		targetViabilityMeasurementPropertiesPanel = new MeasurementPropertiesPanel(getProject());
		
		addPanel(blankPropertiesPanel);
		addPanel(targetPropertiesPanel);
		addPanel(humanWelfareTargetPropertiesPanel);
		addPanel(targetViabilityKeaPropertiesPanel);
		addPanel(targetViabilityIndicatorPropertiesPanel);
		addPanel(targetViabilityMeasurementPropertiesPanel);

		updateFieldsFromProject();
	}
	
	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Target Viability Properties");
	}

	@Override
	protected AbstractObjectDataInputPanel findPanel(ORef[] orefsToUse)
	{
		try
		{
			if(orefsToUse.length == 0)
				return blankPropertiesPanel;

			int objectType = orefsToUse[0].getObjectType();
			if(Target.is(objectType))
				return targetPropertiesPanel;
			if(HumanWelfareTarget.is(objectType))
				return humanWelfareTargetPropertiesPanel;
			if(objectType == KeyEcologicalAttributeSchema.getObjectType())
				return targetViabilityKeaPropertiesPanel;
			if(objectType == IndicatorSchema.getObjectType())
				return targetViabilityIndicatorPropertiesPanel;
			if(objectType == MeasurementSchema.getObjectType())
				return targetViabilityMeasurementPropertiesPanel;
			if(FutureStatus.is(objectType))
				return getFutureStatusForViabilityMode(new ORefList(orefsToUse));
		}
		catch (Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
		}
		return blankPropertiesPanel;
	}

	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		if (event.isSetDataCommandWithThisTypeAndTag(TargetSchema.getObjectType(), Target.TAG_VIABILITY_MODE))
			reloadSelectedRefs();		
	}

	private BlankPropertiesPanel blankPropertiesPanel;
	private NonDiagramAbstractTargetPropertiesPanel targetPropertiesPanel;
	private NonDiagramAbstractTargetPropertiesPanel humanWelfareTargetPropertiesPanel;
	private TargetViabilityKeaPropertiesPanel targetViabilityKeaPropertiesPanel;
	private AbstractIndicatorPropertiesPanel targetViabilityIndicatorPropertiesPanel;
	private MeasurementPropertiesPanel targetViabilityMeasurementPropertiesPanel;
}
