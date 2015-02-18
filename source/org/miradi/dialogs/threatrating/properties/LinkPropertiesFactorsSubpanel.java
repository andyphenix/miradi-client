/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

import org.miradi.actions.Actions;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.icons.IconManager;
import org.miradi.layout.OneRowGridLayout;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Factor;
import org.miradi.objects.ProjectMetadata;
import org.miradi.project.Project;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.schemas.TargetSchema;

//TODO need to rename and remove link from name
public class LinkPropertiesFactorsSubpanel extends ObjectDataInputPanel
{
	public LinkPropertiesFactorsSubpanel(Project projectToUse, Actions actions) throws Exception
	{
		super(projectToUse, ORef.INVALID);
		final OneRowGridLayout layout = new OneRowGridLayout();
		layout.setGaps(10);
		setLayout(layout);
		
		threatLabel = new PanelTitleLabel();
		threatNameField = createExpandableField(ObjectType.FAKE, Factor.TAG_LABEL);
		threatNameField.setEditable(false);
		addFieldWithCustomLabel(threatNameField, threatLabel);

		targetLabel = new PanelTitleLabel();
		targetNameField = createExpandableField(ObjectType.FAKE, Factor.TAG_LABEL);
		targetNameField.setEditable(false);
		addFieldWithCustomLabel(targetNameField, targetLabel);

		updateFieldsFromProject();
	}
	
	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		if(event.isSetDataCommandWithThisTypeAndTag(ProjectMetadataSchema.getObjectType(), ProjectMetadata.TAG_THREAT_RATING_MODE))
		{
			setObjectRefs(getSelectedRefs());
		}
	}
	
	@Override
	public void setObjectRefs(ORef[] orefsToUse)
	{
		updateFieldLabels(orefsToUse);
		super.setObjectRefs(orefsToUse);
	}

	private void updateFieldLabels(ORef[] orefsToUse)
	{
		ORefList refs = new ORefList(orefsToUse);
		ORef threatRef = refs.getRefForType(CauseSchema.getObjectType());
		ORef targetRef = refs.getRefForType(TargetSchema.getObjectType());		
		if(threatRef.isInvalid() || targetRef.isInvalid())
		{
			threatLabel.setText("");
			threatLabel.setIcon(null);
			threatNameField.setObjectRef(ORef.INVALID);
			targetLabel.setText("");
			targetLabel.setIcon(null);
			targetNameField.setObjectRef(ORef.INVALID);
			return;
		}
		
		try
		{
			Factor threat = Factor.findFactor(getProject(), threatRef);
			threatLabel.setText(EAM.fieldLabel(threat.getType(), threat.getTypeName()));
			threatLabel.setIcon(IconManager.getImage(threat));
			threatNameField.setObjectRef(threat.getRef());

			Factor target = Factor.findFactor(getProject(), targetRef);
			targetLabel.setText(EAM.fieldLabel(target.getType(), target.getTypeName()));
			targetLabel.setIcon(IconManager.getImage(target));
			targetNameField.setObjectRef(target.getRef());
		}
		catch(Exception e)
		{
			EAM.panic(e);
		}
	}

	@Override
	public String getPanelDescription()
	{
		return "LinkPropertiesFactorsSubpanel";
	}
	
	private PanelTitleLabel threatLabel;
	private PanelTitleLabel targetLabel;
	private ObjectDataInputField threatNameField;
	private ObjectDataInputField targetNameField;
}
