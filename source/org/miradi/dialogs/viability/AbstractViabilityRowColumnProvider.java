/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.viability;

import org.miradi.dialogs.planning.AbstractPlanningTreeRowColumnProvider;
import org.miradi.dialogs.viability.nodes.ViabilityMeasurementNode;
import org.miradi.objects.BaseObject;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.Target;
import org.miradi.project.Project;
import org.miradi.utils.CodeList;

abstract public class AbstractViabilityRowColumnProvider extends AbstractPlanningTreeRowColumnProvider
{
	public AbstractViabilityRowColumnProvider(Project projectToUse)
	{
		super(projectToUse);
	}
	
	public CodeList getColumnCodesToShow() throws Exception
	{
		return new CodeList(new String[] {
				 Target.TAG_VIABILITY_MODE,
				 ViabilityTreeModel.VIRTUAL_TAG_STATUS,
				 KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE,
				 ViabilityMeasurementNode.POOR,
				 ViabilityMeasurementNode.FAIR,
				 ViabilityMeasurementNode.GOOD,
				 ViabilityMeasurementNode.VERY_GOOD,
				 Measurement.TAG_STATUS_CONFIDENCE,
				 BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE,
		});
	}

	public boolean shouldIncludeResultsChain() throws Exception
	{
		return true;
	}

	public boolean shouldIncludeConceptualModelPage() throws Exception
	{
		return true;
	}

	@Override
	public boolean shouldPutTargetsAtTopLevelOfTree() throws Exception
	{
		return true;
	}
}
