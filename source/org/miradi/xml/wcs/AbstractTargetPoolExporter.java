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

package org.miradi.xml.wcs;

import org.martus.util.UnicodeWriter;
import org.miradi.dialogs.threatrating.upperPanel.AbstractThreatPerRowTableModel;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Factor;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.ThreatRatingQuestion;
import org.miradi.xml.generic.XmlSchemaCreator;

public abstract class AbstractTargetPoolExporter extends FactorPoolExporter
{
	public AbstractTargetPoolExporter(Xmpz1XmlExporter wcsXmlExporterToUse, String containerNameToUse, int objectTypeToUse)
	{
		super(wcsXmlExporterToUse, containerNameToUse, objectTypeToUse);
	}

	@Override
	protected void exportFields(UnicodeWriter writer, BaseObject baseObject) throws Exception
	{
		super.exportFields(writer, baseObject);
		
		AbstractTarget abstractTarget = (AbstractTarget) baseObject;
		writeCodeElement(XmlSchemaCreator.TARGET_STATUS_ELEMENT_NAME, StaticQuestionManager.getQuestion(StatusQuestion.class), abstractTarget.getTargetViability());
		writeElementWithSameTag(baseObject, AbstractTarget.TAG_VIABILITY_MODE);
		writeOptionalElementWithSameTag(baseObject, AbstractTarget.TAG_CURRENT_STATUS_JUSTIFICATION);
		writeOptionalIds(Xmpz1XmlConstants.SUB_TARGET_IDS_ELEMENT, Xmpz1XmlConstants.SUB_TARGET, abstractTarget.getSubTargetRefs());
		writeOptionalIds(AbstractTarget.TAG_GOAL_IDS, Xmpz1XmlConstants.GOAL, abstractTarget.getGoalRefs());
		writeOptionalIds(AbstractTarget.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS, Xmpz1XmlConstants.KEY_ECOLOGICAL_ATTRIBUTE, abstractTarget.getKeyEcologicalAttributeRefs());
		writeIndicatorIds(abstractTarget);
		exportThreatRatingThreatTargetRating(abstractTarget);
	}
	
	@Override
	protected void writeIndicatorIds(Factor factor) throws Exception
	{
		writeOptionalIndicatorIds(factor.getActiveAndInactiveDirectIndicatorRefs());
	}
	
	private void exportThreatRatingThreatTargetRating(AbstractTarget abstractTarget) throws Exception
	{
		if (getProject().isStressBaseMode())
			exportStressBasedThreatRatingThreatTargetRating(abstractTarget);
		else
			exportSimpleThreatRatingThreatTargetRating(abstractTarget.getRef());
	}

	private void exportStressBasedThreatRatingThreatTargetRating(AbstractTarget target) throws Exception
	{
		int rawTargetRatingValue = getProject().getStressBasedThreatRatingFramework().get2PrimeSummaryRatingValue(target);
		ChoiceItem targetThreatRating = AbstractThreatPerRowTableModel.convertThreatRatingCodeToChoiceItem(rawTargetRatingValue);
		writeOptionalCodeElement(TARGET_THREAT_RATING, new ThreatRatingQuestion(), targetThreatRating.getCode());
	}
	
	private void exportSimpleThreatRatingThreatTargetRating(ORef targetRef) throws Exception
	{
		ChoiceItem threatTargetRating = getProject().getSimpleThreatRatingFramework().getTargetThreatRatingValue(targetRef);
		writeOptionalCodeElement(TARGET_THREAT_RATING, new ThreatRatingQuestion(), threatTargetRating.getCode());
	}
}
