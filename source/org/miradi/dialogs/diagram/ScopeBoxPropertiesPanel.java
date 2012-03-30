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
package org.miradi.dialogs.diagram;

import org.miradi.actions.jump.ActionJumpDiagramWizardProjectScopeStep;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.ScopeBox;
import org.miradi.project.Project;
import org.miradi.questions.DiagramFactorFontColorQuestion;
import org.miradi.questions.DiagramFactorFontSizeQuestion;
import org.miradi.questions.DiagramFactorFontStyleQuestion;
import org.miradi.questions.ScopeBoxTypeQuestion;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.ScopeBoxSchema;

public class ScopeBoxPropertiesPanel extends ObjectDataInputPanel
{
	public ScopeBoxPropertiesPanel(Project projectToUse, DiagramFactor diagramFactor) throws Exception
	{
		super(projectToUse, ObjectType.SCOPE_BOX, diagramFactor.getWrappedId());

		setObjectRefs(new ORef[] {diagramFactor.getWrappedORef(), diagramFactor.getRef()});

		addField(createExpandableField(ScopeBox.TAG_LABEL));
		addField(createMultilineField(ScopeBox.TAG_TEXT));
		addField(createChoiceField(ScopeBoxSchema.getObjectType(), ScopeBox.TAG_SCOPE_BOX_TYPE_CODE, ScopeBoxTypeQuestion.createScopeBoxTypeQuestion()));
		
		ObjectDataInputField fontField = createChoiceField(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_FONT_SIZE, new DiagramFactorFontSizeQuestion());
		ObjectDataInputField colorField = createChoiceField(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_FOREGROUND_COLOR, new DiagramFactorFontColorQuestion());
		ObjectDataInputField styleField = createChoiceField(DiagramFactorSchema.getObjectType(), DiagramFactor.TAG_FONT_STYLE, new DiagramFactorFontStyleQuestion());
		addFieldsOnOneLine(EAM.text("Font"), new ObjectDataInputField[]{fontField, colorField, styleField});

		updateFieldsFromProject();
	}
	
	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Scope Box Properties");
	}

	@Override
	public Class getJumpActionClass()
	{
		return ActionJumpDiagramWizardProjectScopeStep.class;
	}
}
