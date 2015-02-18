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
package org.miradi.views.summary;

import javax.swing.Icon;

import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.forms.summary.FosTabForm;
import org.miradi.icons.FosIcon;
import org.miradi.main.EAM;
import org.miradi.objects.FosProjectData;
import org.miradi.project.Project;
import org.miradi.questions.FosTrainingTypeQuestion;
import org.miradi.rtf.RtfFormExporter;
import org.miradi.rtf.RtfWriter;
import org.miradi.schemas.FosProjectDataSchema;

public class FOSSummaryPanel extends ObjectDataInputPanel
{
	public FOSSummaryPanel(Project projectToUse) throws Exception
	{
		super(projectToUse, projectToUse.getSingletonObjectRef(FosProjectDataSchema.getObjectType()));
		
		addField(createChoiceField(FosProjectDataSchema.getObjectType(), FosProjectData.TAG_TRAINING_TYPE, new FosTrainingTypeQuestion()));
		addField(createStringField(FosProjectData.TAG_TRAINING_DATES));
		addField(createStringField(FosProjectData.TAG_TRAINERS));
		addField(createStringField(FosProjectData.TAG_COACHES));
		
		updateFieldsFromProject();
	}

	@Override
	public String getPanelDescription()
	{
		return getFosPanelDescription();
	}

	public static String getFosPanelDescription()
	{
		return EAM.text("Label|FOS");
	}
	
	@Override
	public Icon getIcon()
	{
		return new FosIcon();
	}
	
	@Override
	public boolean isRtfExportable()
	{
		return true;
	}
	
	@Override
	public void exportRtf(RtfWriter writer) throws Exception
	{
		RtfFormExporter rtfFormExporter = new RtfFormExporter(getProject(), writer, getSelectedRefs());
		rtfFormExporter.exportForm(new FosTabForm());
	}
}
