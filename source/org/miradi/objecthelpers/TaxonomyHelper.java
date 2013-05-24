/* 
Copyright 2005-2013, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.objecthelpers;

import java.util.Vector;

import org.miradi.objectpools.TaxonomyAssociationPool;
import org.miradi.objects.Cause;
import org.miradi.objects.Goal;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.MiradiShareProjectData;
import org.miradi.objects.MiradiShareTaxonomy;
import org.miradi.objects.Objective;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Strategy;
import org.miradi.objects.Stress;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.TaxonomyAssociation;
import org.miradi.objects.ThreatReductionResult;
import org.miradi.project.Project;
import org.miradi.schemas.MiradiShareTaxonomySchema;
import org.miradi.utils.Utility;
import org.miradi.xml.xmpz2.Xmpz2XmlConstants;

public class TaxonomyHelper implements Xmpz2XmlConstants
{
	public static TaxonomyAssociation findTaxonomyAssociation(Project project, String taxonomyAssociationCode)
	{
		TaxonomyAssociationPool taxonomyAssociationPool = project.getTaxonomyAssociationPool();
		ORefList taxonomyAssociationsForType = taxonomyAssociationPool.getRefList();
		for(ORef taxonomyAssociationRef : taxonomyAssociationsForType)
		{
			TaxonomyAssociation taxonomyAssociation = TaxonomyAssociation.find(project, taxonomyAssociationRef);
			if (taxonomyAssociation.getTaxonomyAssociationCode().equals(taxonomyAssociationCode))
				return taxonomyAssociation;
		}

		return null;
	}
	
	public static MiradiShareTaxonomy getTaxonomyElementList(final TaxonomyAssociation taxonomyAssociation) throws Exception
	{
		String taxonomyCode = taxonomyAssociation.getTaxonomyCode();
		final Project projectToUse = taxonomyAssociation.getProject();
		ORefList miradiShareTaxonomyRefs = projectToUse.getPool(MiradiShareTaxonomySchema.getObjectType()).getORefList();
		for(ORef miradiShareTaxonomyRef : miradiShareTaxonomyRefs)
		{
			MiradiShareTaxonomy miradiShareTaxonomy = MiradiShareTaxonomy.find(projectToUse, miradiShareTaxonomyRef);
			final String thisTaxonomyCode = miradiShareTaxonomy.getData(MiradiShareTaxonomySchema.TAG_TAXONOMY_CODE);
			if (thisTaxonomyCode.equals(taxonomyCode))
				return miradiShareTaxonomy;
		}
		
		throw new Exception("Taxonomy object could not be found for code:" + taxonomyCode);
	}
	
	public static Vector<String> getTaxonomyAssociationPoolNamesForType(final int objectType)
	{
		if (MiradiShareProjectData.is(objectType))
			return convertToSingleItemVector(MIRADI_SHARE__PROJECT_DATA_TAXONOMY_ASSOCIATION_POOL);
		
		if (Target.is(objectType))
			return convertToSingleItemVector(BIODIVERSITY_TARGET_TAXONOMY_ASSOCIATION_POOL);
		
		if (HumanWelfareTarget.is(objectType))
			return convertToSingleItemVector(HUMAN_WELLBEING_TARGET_TAXONOMY_ASSOCIATION_POOL);
		
		if (Cause.is(objectType))
			return convertToSingleItemVector(CONTRIBUTING_FACTOR_TAXONOMY_ASSOCIATION_POOL, DIRECT_THREAT_TAXONOMY_ASSOCIATION_POOL);
		
		if (Strategy.is(objectType))
			return convertToSingleItemVector(STRATEGY_TAXONOMY_ASSOCIATION_POOL);
		
		if (ResultsChainDiagram.is(objectType))
			return convertToSingleItemVector(RESULTS_CHAIN_TAXONOMY_ASSOCIATION_POOL);
		
		if (ThreatReductionResult.is(objectType))
			return convertToSingleItemVector(THREAT_REDUCTION_RESULT_TAXONOMY_ASSOCIATION_POOL);
		
		if (Goal.is(objectType))
			return convertToSingleItemVector(GOAL_TAXONOMY_ASSOCIATION_POOL);
		
		if (KeyEcologicalAttribute.is(objectType))
			return convertToSingleItemVector(KEY_ECOLOGICAL_ATTRIBUTE_TAXONOMY_ASSOCIATION_POOL);
		
		if (Indicator.is(objectType))
			return convertToSingleItemVector(INDICATOR_TAXONOMY_ASSOCIATION_POOL);
		
		if (Objective.is(objectType))
			return convertToSingleItemVector(OBJECTIVE_TAXONOMY_ASSOCIATION_POOL);
		
		if (Stress.is(objectType))
			return convertToSingleItemVector(STRESS_TAXONOMY_ASSOCIATION_POOL);
		
		if (Task.is(objectType))
			return convertToSingleItemVector(TASK_TAXONOMY_ASSOCIATION_POOL);

		return new Vector<String>();
	}

	private static Vector<String> convertToSingleItemVector(String item)
	{
		return Utility.convertToVector(item);
	}
	
	private static Vector<String> convertToSingleItemVector(String item1, String item2)
	{
		return Utility.convertToVector(new String[]{item1, item2, });
	}
	
	public static boolean isTaxonomyAssociationForCause(String taxonomyAssociationPoolName, Cause cause)
	{
		if (cause.isContributingFactor()  && isContributingFactorPoolName(taxonomyAssociationPoolName))
			return true;
		
		if (cause.isDirectThreat() && isDirectThreatPoolName(taxonomyAssociationPoolName))
			return true;
		
		return false;
	}
	
	private static boolean isDirectThreatPoolName(String taxonomyAssociationPoolName)
	{
		return taxonomyAssociationPoolName.equals(DIRECT_THREAT_TAXONOMY_ASSOCIATION_POOL);
	}

	private static boolean isContributingFactorPoolName(String taxonomyAssociationPoolName)
	{
		return taxonomyAssociationPoolName.equals(CONTRIBUTING_FACTOR_TAXONOMY_ASSOCIATION_POOL);
	}
}
