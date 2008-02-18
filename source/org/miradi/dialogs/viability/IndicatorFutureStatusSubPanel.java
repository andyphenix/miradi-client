/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.dialogs.viability;

import org.miradi.dialogfields.ObjectDataInputField;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.fieldComponents.PanelFieldLabel;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.icons.GoalIcon;
import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.project.Project;
import org.miradi.questions.StatusQuestion;

public class IndicatorFutureStatusSubPanel extends ObjectDataInputPanel
{
	public IndicatorFutureStatusSubPanel(Project project)
	{
		this(project, new ORef(Indicator.getObjectType(), BaseId.INVALID));
	}
	
	public IndicatorFutureStatusSubPanel(Project projectToUse, ORef orefToUse)
	{
		super(projectToUse, orefToUse);

		ObjectDataInputField futureStatusDateField = createDateChooserField(ObjectType.INDICATOR,  Indicator.TAG_FUTURE_STATUS_DATE);
		ObjectDataInputField futureStatusSummaryField = createMediumStringField(ObjectType.INDICATOR,  Indicator.TAG_FUTURE_STATUS_SUMMARY);

		futureStatusRatingLabelField = new PanelFieldLabel(ObjectType.INDICATOR, Indicator.TAG_FUTURE_STATUS_RATING);
		futureStatusRatingField = createRatingChoiceField(ObjectType.INDICATOR, Indicator.TAG_FUTURE_STATUS_RATING, new StatusQuestion());
		
		PanelTitleLabel futureStatusLabel = new PanelTitleLabel(EAM.text("Desired Future Status"), new GoalIcon());
		addFieldsOnOneLine(futureStatusLabel, new ObjectDataInputField[]{futureStatusDateField, futureStatusSummaryField, futureStatusRatingField, });

		addField(createMultilineField(Indicator.getObjectType(), Indicator.TAG_FUTURE_STATUS_DETAIL));
		addField(createMultilineField(Indicator.getObjectType(), Indicator.TAG_FUTURE_STATUS_COMMENT));
		
		updateFieldsFromProject();
	}
	
	public void setObjectRefs(ORef[] orefsToUse)
	{
		super.setObjectRefs(orefsToUse);
	
		boolean isVisible = true;
		ORef foundRef = new ORefList(orefsToUse).getRefForType(KeyEcologicalAttribute.getObjectType());
		if (foundRef.isInvalid())
			isVisible = false;
			
		setVisibilityOfRatingField(isVisible);
	}

	private void setVisibilityOfRatingField(boolean isVisible)
	{
		futureStatusRatingLabelField.setVisible(isVisible);
		futureStatusRatingField.setVisible(isVisible);
	}

	public String getPanelDescription()
	{
		return EAM.text("Desired Status");
	}
	
	private ObjectDataInputField futureStatusRatingField;
	private PanelTitleLabel futureStatusRatingLabelField ;
}
