/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.threatstressrating.properties;

import java.awt.Component;

import org.conservationmeasures.eam.dialogs.base.ObjectDataInputPanel;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.objects.Stress;
import org.conservationmeasures.eam.views.umbrella.ObjectPicker;

import com.jhlabs.awt.BasicGridLayout;

public class ThreatStressRatingPropertiesPanel extends ObjectDataInputPanel
{
	public ThreatStressRatingPropertiesPanel(MainWindow mainWindowToUse, ObjectPicker objectPickerToUse) throws Exception
	{
		super(mainWindowToUse.getProject(), ObjectType.THREAT_STRESS_RATING, BaseId.INVALID);
		setLayout(new BasicGridLayout(2, 1));
		
		threatStressRatingFieldPanel = new ThreatStressRatingFieldPanel(mainWindowToUse.getProject(), ORef.INVALID); 
		editorComponent = new ThreatStressRatingEditorComponent(mainWindowToUse, objectPickerToUse);
		add(threatStressRatingFieldPanel);
		add(editorComponent);
		
		updateFieldsFromProject();
	}
	
	public void dispose()
	{
		super.dispose();
		if (editorComponent != null)
		{
			editorComponent.dispose();
			editorComponent = null;
		}
		
		if (threatStressRatingFieldPanel != null)
		{
			threatStressRatingFieldPanel.dispose();
			threatStressRatingFieldPanel = null;
		}
	}
	
	public void setObjectRefs(ORef[] hierarchyToSelectedRef)
	{
		threatStressRatingFieldPanel.setObjectRefs(hierarchyToSelectedRef);
		editorComponent.setObjectRefs(hierarchyToSelectedRef);
	}
	
	public void setObjectRefs(ORefList[] hierarchiesToSelectedRefs)
	{
		if (hierarchiesToSelectedRefs.length == 0)
			setObjectRefs(new ORef[0]);
		else
			setObjectRefs(hierarchiesToSelectedRefs[0].toArray());
	}
	
	public String getPanelDescription()
	{
		return EAM.text("Title|Stress-Based Threat Rating");
	}

	public void addFieldComponent(Component component)
	{
		add(component);
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		
		if (event.isSetDataCommandWithThisTypeAndTag(FactorLink.getObjectType(), FactorLink.TAG_THREAT_STRESS_RATING_REFS) || 
			event.isSetDataCommandWithThisType(Stress.getObjectType()))
			editorComponent.updateModelBasedOnPickerList();
	}
	
	private ThreatStressRatingEditorComponent editorComponent;
	private ThreatStressRatingFieldPanel threatStressRatingFieldPanel;
}
