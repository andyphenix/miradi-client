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

import org.miradi.actions.ActionEditStrategyGoalRelevancyList;
import org.miradi.actions.ActionEditStrategyObjectiveRelevancyList;
import org.miradi.actions.ActionManageFactorTags;
import org.miradi.actions.Actions;
import org.miradi.diagram.factortypes.FactorType;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.icons.IconManager;
import org.miradi.main.EAM;
import org.miradi.objectdata.BooleanData;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.Factor;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.project.Project;
import org.miradi.questions.DiagramFactorFontColorQuestion;
import org.miradi.questions.DiagramFactorFontSizeQuestion;
import org.miradi.questions.DiagramFactorFontStyleQuestion;
import org.miradi.questions.HabitatAssociationQuestion;
import org.miradi.questions.StrategyClassificationQuestion;
import org.miradi.questions.StrategyFeasibilityQuestion;
import org.miradi.questions.StrategyImpactQuestion;
import org.miradi.questions.StrategyRatingSummaryQuestion;
import org.miradi.questions.ThreatClassificationQuestion;
import org.miradi.utils.ObjectsActionButton;

public class FactorSummaryCorePanel extends ObjectDataInputPanel
{
	public FactorSummaryCorePanel(Project project, Actions actions, DiagramFactor diagramFactorToEdit) throws Exception
	{
		super(project, diagramFactorToEdit.getRef());
		Factor factorToEdit = diagramFactorToEdit.getWrappedFactor();
		setObjectRefs(new ORef[] {factorToEdit.getRef(), diagramFactorToEdit.getRef(),});

		ObjectDataInputField shortLabelField = createShortStringField(Factor.TAG_SHORT_LABEL);
		labelField = createExpandableField(Factor.TAG_LABEL);
		
		addFieldsOnOneLine(FactorType.getFactorTypeLabel(factorToEdit), 
				IconManager.getImage(factorToEdit), 
				new ObjectDataInputField[]{shortLabelField, labelField});
		addField(createMultilineField(Factor.TAG_TEXT));

		ObjectDataInputField fontField = createChoiceField(DiagramFactor.getObjectType(), DiagramFactor.TAG_FONT_SIZE, new DiagramFactorFontSizeQuestion());
		ObjectDataInputField colorField = createChoiceField(DiagramFactor.getObjectType(), DiagramFactor.TAG_FOREGROUND_COLOR, new DiagramFactorFontColorQuestion());
		ObjectDataInputField styleField = createChoiceField(DiagramFactor.getObjectType(), DiagramFactor.TAG_FONT_STYLE, new DiagramFactorFontStyleQuestion());
		addFieldsOnOneLine(EAM.text("Font"), new ObjectDataInputField[]{fontField, colorField, styleField});
		
		if (factorToEdit.isCause())
		{
			addField(createCheckBoxField(Cause.getObjectType(), Cause.TAG_IS_DIRECT_THREAT, BooleanData.BOOLEAN_TRUE, BooleanData.BOOLEAN_FALSE));
		}
		if (factorToEdit.isDirectThreat())
		{
			addField(createRadioButtonEditorField(Cause.getObjectType(), Cause.TAG_TAXONOMY_CODE, new ThreatClassificationQuestion()));
		}
		
		if(factorToEdit.isStrategy())
		{
			addOptionalDraftStatusCheckBox(Strategy.TAG_STATUS);
			addField(createRadioButtonEditorField(Strategy.getObjectType(), Strategy.TAG_TAXONOMY_CODE, new StrategyClassificationQuestion()));

			ObjectDataInputField impactField = createRadioButtonEditorField(Strategy.getObjectType(), Strategy.TAG_IMPACT_RATING, getQuestion(StrategyImpactQuestion.class));
			ObjectDataInputField feasibilityField = createRadioButtonEditorField(Strategy.getObjectType(), Strategy.TAG_FEASIBILITY_RATING, getQuestion(StrategyFeasibilityQuestion.class));
			ObjectDataInputField prioritySummaryField = createReadOnlyChoiceField(Strategy.PSEUDO_TAG_RATING_SUMMARY, getQuestion(StrategyRatingSummaryQuestion.class));
			addFieldsOnOneLine(EAM.text("Priority"), new ObjectDataInputField[] {impactField, feasibilityField, prioritySummaryField});
			addLabeledSubPanelWithoutBorder(new LegacyTncStrategyRankingEditorPropertiesSubPanel(getProject(), factorToEdit.getRef(), actions), EAM.text("Legacy TNC Ratings"));
			addFieldWithEditButton(EAM.text("Objectives"), createReadOnlyObjectList(Strategy.getObjectType(), Strategy.PSEUDO_TAG_RELEVANT_OBJECTIVE_REFS), createObjectsActionButton(actions.getObjectsAction(ActionEditStrategyObjectiveRelevancyList.class), getPicker()));
			addFieldWithEditButton(EAM.text("Goals"), createReadOnlyObjectList(Strategy.getObjectType(), Strategy.PSEUDO_TAG_RELEVANT_GOAL_REFS), createObjectsActionButton(actions.getObjectsAction(ActionEditStrategyGoalRelevancyList.class), getPicker()));
		}

		if(factorToEdit.isTarget())
		{
			addField(createMultilineField(Target.TAG_CURRENT_STATUS_JUSTIFICATION));
			addField(createStringField(Target.TAG_SPECIES_LATIN_NAME));
			addField(createQuestionFieldWithDescriptionPanel(Target.getObjectType(), Target.TAG_HABITAT_ASSOCIATION, new HabitatAssociationQuestion()));
		}
		
		addField(createReadOnlyObjectList(factorToEdit.getType(), Factor.PSEUDO_TAG_CONCEPTUAL_DIAGRAM_REFS));
		addField(createReadOnlyObjectList(factorToEdit.getType(), Factor.PSEUDO_TAG_RESULTS_CHAIN_REFS));

		ObjectsActionButton chooseTagForFactorButton = createObjectsActionButton(actions.getObjectsAction(ActionManageFactorTags.class), getPicker());
		ObjectDataInputField readOnlyTaggedObjects = createReadOnlyObjectList(factorToEdit.getRef().getObjectType(), Factor.PSEUDO_TAG_REFERRING_TAG_REFS);
		addFieldWithEditButton(getTagsLabel(), readOnlyTaggedObjects, chooseTagForFactorButton);
	}
	
	public static String getTagsLabel()
	{
		return EAM.text("Tags");
	}

	private void addOptionalDraftStatusCheckBox(String tag)
	{
		if (!getProject().isChainMode())
			return;
		
		ObjectDataInputField field = createCheckBoxField(tag, Strategy.STATUS_DRAFT, Strategy.STATUS_REAL);
		addField(field);
	}

	@Override
	public void setFocusOnFirstField()
	{
		labelField.getComponent().requestFocusInWindow();
	}
	
	@Override
	public String getPanelDescription()
	{
		return EAM.text("Summary");
	}

	private ObjectDataInputField labelField;
}
