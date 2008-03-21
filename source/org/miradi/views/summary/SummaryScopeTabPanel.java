/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.views.summary;

import javax.swing.Icon;

import org.miradi.actions.jump.ActionJumpSummaryWizardDefineProjecScope;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.icons.ProjectScopeIcon;
import org.miradi.layout.OneColumnGridLayout;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.ProjectMetadata;
import org.miradi.project.Project;

public class SummaryScopeTabPanel extends ObjectDataInputPanel
{
	public SummaryScopeTabPanel(Project projectToUse, ORef[] orefsToUse)
	{
		super(projectToUse, orefsToUse);
		setLayout(new OneColumnGridLayout());
		
		ScopeAndVisionPanel scopeVision = new ScopeAndVisionPanel(getProject(), getRefForType(ProjectMetadata.getObjectType()));
		addSubPanelWithTitledBorder(scopeVision);

		BiodiversityPanel biodiversity = new BiodiversityPanel(getProject(), getRefForType(ProjectMetadata.getObjectType()));
		addSubPanelWithTitledBorder(biodiversity);
		
		HumanStakeholderPanel humans = new HumanStakeholderPanel(getProject(), getRefForType(ProjectMetadata.getObjectType()));
		addSubPanelWithTitledBorder(humans);
		
		ProtectedAreaPanel protectedAreas = new ProtectedAreaPanel(getProject(), getSelectedRefs().toArray());
		addSubPanelWithTitledBorder(protectedAreas);

		updateFieldsFromProject();
	}

	public String getPanelDescription()
	{
		return EAM.text("Scope");
	}
	
	@Override
	public Icon getIcon()
	{
		return new ProjectScopeIcon();
	}
	
	@Override
	public Class getJumpActionClass()
	{
		return ActionJumpSummaryWizardDefineProjecScope.class;
	}
	

}
