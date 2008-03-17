/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.dialogs.diagram;

import org.miradi.actions.ActionEditStrategyProgressReports;
import org.miradi.actions.Actions;
import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.icons.StrategyIcon;
import org.miradi.main.EAM;
import org.miradi.objects.Factor;
import org.miradi.objects.Strategy;
import org.miradi.project.Project;
import org.miradi.questions.StrategyFeasibilityQuestion;
import org.miradi.questions.StrategyImpactQuestion;
import org.miradi.questions.StrategyRatingSummaryQuestion;
import org.miradi.questions.StrategyTaxonomyQuestion;
import org.miradi.utils.ObjectsActionButton;


public class StrategyCoreSubpanel extends ObjectDataInputPanel
{
	public StrategyCoreSubpanel(Project projectToUse, Actions actions, int objectType)
	{
		super(projectToUse, objectType);

		ObjectDataInputField shortLabelField = createStringField(Strategy.getObjectType(), Strategy.TAG_SHORT_LABEL,10);
		ObjectDataInputField labelField = createExpandableField(Strategy.getObjectType(), Strategy.TAG_LABEL);
		addFieldsOnOneLine(EAM.text("Strategy"), new StrategyIcon(), new ObjectDataInputField[]{shortLabelField, labelField,});
		addField(createMultilineField(Strategy.getObjectType(), Factor.TAG_TEXT));

		addField(createChoiceField(Strategy.getObjectType(), Strategy.TAG_TAXONOMY_CODE, new StrategyTaxonomyQuestion()));
		
		ObjectDataInputField impactField = createRatingChoiceField(Strategy.TAG_IMPACT_RATING, new StrategyImpactQuestion());
		ObjectDataInputField feasibilityField = createRatingChoiceField(Strategy.TAG_FEASIBILITY_RATING, new StrategyFeasibilityQuestion());
		ObjectDataInputField prioritySummaryField = createReadOnlyChoiceField(Strategy.PSEUDO_TAG_RATING_SUMMARY, new StrategyRatingSummaryQuestion());

		addFieldsOnOneLine(EAM.text("Priority"), new ObjectDataInputField[] {impactField, feasibilityField, prioritySummaryField});
		
		ObjectsActionButton editProgressReportButton = createObjectsActionButton(actions.getObjectsAction(ActionEditStrategyProgressReports.class), getPicker());
		ObjectDataInputField readOnlyProgressReportsList = createReadOnlyObjectList(Strategy.getObjectType(), Strategy.TAG_PROGRESS_REPORT_REFS);
		addFieldWithEditButton(EAM.text("Progress"), readOnlyProgressReportsList, editProgressReportButton);
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("");
	}

}
