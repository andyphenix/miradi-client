/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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

package org.miradi.xml.xmpz1;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.objects.Desire;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.xml.wcs.Xmpz1XmlConstants;
import org.w3c.dom.Node;

abstract public class DesirePoolImporter extends AbstractBaseObjectPoolImporter
{
	public DesirePoolImporter(Xmpz1XmlImporter importerToUse, String poolNameToUse, int objectTypeToImportToUse)
	{
		super(importerToUse, poolNameToUse, objectTypeToImportToUse);
	}
	
	@Override
	protected void importFields(Node node, ORef destinationRef) throws Exception
	{
		super.importFields(node, destinationRef);
	
		importField(node, destinationRef, Desire.TAG_SHORT_LABEL);
		importField(node, destinationRef, Desire.TAG_FULL_TEXT);
		importField(node, destinationRef, Desire.TAG_COMMENTS);
		importProgressPercentRefs(node, destinationRef);

		importRelevantIndicatorIds(node, destinationRef);
		importRelevantStrategyAndActivityIds(node, destinationRef);
	}

	private void importRelevantIndicatorIds(Node node, ORef destinationDesireRef) throws Exception
	{
		ORefList importedRelevantRefs = extractRefs(node, Xmpz1XmlConstants.RELEVANT_INDICATOR_IDS, IndicatorSchema.getObjectType(), Xmpz1XmlConstants.INDICATOR + Xmpz1XmlConstants.ID);
		Desire desire = Desire.findDesire(getProject(), destinationDesireRef);
		RelevancyOverrideSet set = desire.getCalculatedRelevantIndicatorOverrides(importedRelevantRefs);		
		getImporter().setData(destinationDesireRef, Desire.TAG_RELEVANT_INDICATOR_SET, set.toString());
	}

	private void importRelevantStrategyAndActivityIds(Node node, ORef destinationDesireRef) throws Exception
	{
		ORefList importedStrategyAndActivityRefs = new ORefList();
		importedStrategyAndActivityRefs.addAll(extractRefs(node, Xmpz1XmlConstants.RELEVANT_STRATEGY_IDS, StrategySchema.getObjectType(), Xmpz1XmlConstants.STRATEGY + Xmpz1XmlConstants.ID));
		importedStrategyAndActivityRefs.addAll(extractRefs(node, Xmpz1XmlConstants.RELEVANT_ACTIVITY_IDS, TaskSchema.getObjectType(), Xmpz1XmlConstants.ACTIVITY + Xmpz1XmlConstants.ID));
		
		Desire desire = Desire.findDesire(getProject(), destinationDesireRef);
		RelevancyOverrideSet set = desire.getCalculatedRelevantStrategyActivityOverrides(importedStrategyAndActivityRefs);
		getImporter().setData(destinationDesireRef, Desire.TAG_RELEVANT_STRATEGY_ACTIVITY_SET, set.toString());
	}	
}
