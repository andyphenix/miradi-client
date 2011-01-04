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
package org.miradi.dialogs.threatrating.properties;

import java.awt.CardLayout;
import java.awt.Rectangle;

import org.miradi.dialogs.base.AbstractObjectDataInputPanel;
import org.miradi.dialogs.base.DisposablePanelWithDescription;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.planning.propertiesPanel.BlankPropertiesPanel;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ThreatTargetVirtualLinkHelper;
import org.miradi.objects.Cause;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.Target;
import org.miradi.views.umbrella.ObjectPicker;

public class ThreatRatingMultiPropertiesPanel extends ObjectDataInputPanel
{
	public ThreatRatingMultiPropertiesPanel(MainWindow mainWindowToUse, ObjectPicker objectPickerToUse) throws Exception
	{
		super(mainWindowToUse.getProject(), ORef.INVALID);
		
		objectPicker = objectPickerToUse;
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		createPropertiesPanels();
		addPropertiesPanels();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		simplePropertiesPanel.dispose();
		stressBasedPropertiesPanel.dispose();
		blankPropertiesPanel.dispose();
	}
	
	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		if(event.isSetDataCommandWithThisTypeAndTag(ProjectMetadata.getObjectType(), ProjectMetadata.TAG_THREAT_RATING_MODE))
			showCorrectPanel();
	}
	
	private void createPropertiesPanels() throws Exception
	{
		simplePropertiesPanel = new SimpleThreatRatingPropertiesPanel(getMainWindow(), objectPicker);
		stressBasedPropertiesPanel = new StressBasedThreatRatingPropertiesPanel(getMainWindow(), objectPicker);
		blankPropertiesPanel = new BlankPropertiesPanel(getProject());
	}

	private void addPropertiesPanels()
	{
		add(simplePropertiesPanel);
		add(stressBasedPropertiesPanel);
		add(blankPropertiesPanel);
	}
	
	private void add(DisposablePanelWithDescription panelToAdd)
	{
		add(panelToAdd, panelToAdd.getPanelDescription());
	}
	
	@Override
	public String getPanelDescription()
	{
		return EAM.text("Threat Rating Properties Panel");
	}

	@Override
	public void setObjectRefs(ORef[] orefsToUse)
	{
		super.setObjectRefs(orefsToUse);
		showCorrectPanel();
	
		simplePropertiesPanel.setObjectRefs(orefsToUse);
		stressBasedPropertiesPanel.setObjectRefs(orefsToUse);
		blankPropertiesPanel.setObjectRefs(orefsToUse);
		
		scrollRectToVisible(new Rectangle(0,0,0,0));
		
		// NOTE: The following are an attempt to fix a reported problem 
		// where the screen was not fully repainted when switching objects
		// This code is duplicated in TargetViabilityTreePropertiesPanel.java
		// and DirectIndicatorPropertiesPanel.java
		validate();
		repaint();
	}

	private void showCorrectPanel()
	{
		cardLayout.show(this, findPanel().getPanelDescription());
	}
	
	private DisposablePanelWithDescription findPanel()
	{
		ORef causeRef = getRefForType(Cause.getObjectType());
		if(causeRef.isInvalid())
			return blankPropertiesPanel;
		
		Cause cause = Cause.find(getProject(), causeRef);
		if (cause.isContributingFactor())
			return blankPropertiesPanel;
		
		ORef targetRef = getRefForType(Target.getObjectType());
		if (targetRef.isInvalid())
			return blankPropertiesPanel;
		
		if (!ThreatTargetVirtualLinkHelper.canSupportThreatRatings(getProject(), cause, targetRef))
			return blankPropertiesPanel;
		
		if (getProject().isStressBaseMode())
			return stressBasedPropertiesPanel;

		return simplePropertiesPanel;
	}
	
	private ObjectPicker objectPicker;
	private CardLayout cardLayout;
	
	private AbstractObjectDataInputPanel simplePropertiesPanel;
	private AbstractObjectDataInputPanel stressBasedPropertiesPanel;
	private BlankPropertiesPanel blankPropertiesPanel;
}
