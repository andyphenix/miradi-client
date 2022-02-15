/*
Copyright 2005-2021, Foundations of Success, Bethesda, Maryland
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

package org.miradi.xml.xmpz2.objectImporters;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.objects.AbstractAnalyticalQuestion;
import org.miradi.project.Project;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.xml.xmpz2.Xmpz2XmlConstants;
import org.miradi.xml.xmpz2.Xmpz2XmlImporter;
import org.w3c.dom.Node;

abstract public class AbstractAnalyticalQuestionImporter extends BaseObjectImporter
{
    public AbstractAnalyticalQuestionImporter(Xmpz2XmlImporter importerToUse, BaseObjectSchema baseObjectSchemaToUse)
    {
        super(importerToUse, baseObjectSchemaToUse);
    }

	@Override
	public void importFields(Node baseObjectNode, ORef destinationRef) throws Exception
	{
		super.importFields(baseObjectNode, destinationRef);
		importRelevantIndicatorIds(baseObjectNode, destinationRef);
		importRelevantDiagramFactorIds(baseObjectNode, destinationRef);
	}

	@Override
	protected boolean isCustomImportField(String tag)
	{
		if (tag.equals(AbstractAnalyticalQuestion.TAG_INDICATOR_IDS))
			return true;

		if (tag.equals(AbstractAnalyticalQuestion.TAG_DIAGRAM_FACTOR_IDS))
			return true;

		return super.isCustomImportField(tag);
	}

	private void importRelevantIndicatorIds(Node node, ORef destinationRef) throws Exception
	{
		ORefList importedRelevantRefs = getImporter().extractRefs(node, getXmpz2ElementName(), RELEVANT_INDICATOR_IDS, INDICATOR);
		AbstractAnalyticalQuestion analyticalQuestionOrAssumption = findAnalyticalQuestionOrAssumption(getProject(), destinationRef);
		RelevancyOverrideSet set = analyticalQuestionOrAssumption.getCalculatedRelevantIndicatorOverrides(importedRelevantRefs);
		getImporter().setData(destinationRef, AbstractAnalyticalQuestion.TAG_INDICATOR_IDS, set.toString());
	}

	private void importRelevantDiagramFactorIds(Node node, ORef destinationRef) throws Exception
	{
		ORefList importedRelevantRefs = getImporter().extractRefs(node, getXmpz2ElementName(), RELEVANT_DIAGRAM_FACTOR_IDS, DIAGRAM_FACTOR);
		AbstractAnalyticalQuestion analyticalQuestionOrAssumption = findAnalyticalQuestionOrAssumption(getProject(), destinationRef);
		RelevancyOverrideSet set = analyticalQuestionOrAssumption.getCalculatedRelevantDiagramFactorOverrides(importedRelevantRefs);
		getImporter().setData(destinationRef, AbstractAnalyticalQuestion.TAG_DIAGRAM_FACTOR_IDS, set.toString());
	}

	abstract AbstractAnalyticalQuestion findAnalyticalQuestionOrAssumption(Project project, ORef destinationRef);
}