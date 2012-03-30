/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.objects;

import org.miradi.ids.FactorId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.project.threatrating.StressBasedThreatFormula;
import org.miradi.questions.ChoiceItem;
import org.miradi.schemas.StressSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.utils.EnhancedJsonObject;

public class Stress extends Factor
{
	public Stress(ObjectManager objectManager, FactorId idToUse)
	{
		super(objectManager, idToUse, new StressSchema());
	}
	
	public Stress(ObjectManager objectManager, FactorId idToUse, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, idToUse, json, new StressSchema());
	}
	
	@Override
	public int getType()
	{
		return getObjectType();
	}
	
	public static int getObjectType()
	{
		return ObjectType.STRESS;
	}
	
	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return new int[] {
			TargetSchema.getObjectType(),
			};
	}
	
	@Override
	public ORefList getAllObjectsToDeepCopy(ORefList deepCopiedFactorRefs)
	{
		ORefList objectRefsToDeepCopy = super.getAllObjectsToDeepCopy(deepCopiedFactorRefs);
		objectRefsToDeepCopy.addAll(getThreatStressRatingsToDeepCopy(deepCopiedFactorRefs));
		
		return objectRefsToDeepCopy;
	}

	private ORefList getThreatStressRatingsToDeepCopy(ORefList deepCopiedFactorRefs)
	{
		ORefList threatStressRatingReferrerRefs = findObjectsThatReferToUs(ThreatStressRating.getObjectType());
		ORefList threatStressRatingsWithThreatInList = new ORefList();
		for (int index = 0; index < threatStressRatingReferrerRefs.size(); ++index)
		{
			ORef threatStressRatingRef = threatStressRatingReferrerRefs.get(index);
			ThreatStressRating threatStressRating = ThreatStressRating.find(getProject(), threatStressRatingRef);
			ORef threatRef = threatStressRating.getThreatRef();
			if (deepCopiedFactorRefs.contains(threatRef))
				threatStressRatingsWithThreatInList.add(threatStressRatingRef);
		}
		
		return threatStressRatingsWithThreatInList;
	}
	
	@Override
	public boolean isStress()
	{
		return true;
	}
	
	@Override
	public boolean mustBeDeletedBecauseParentIsGone()
	{
		boolean isSuperShared = super.mustBeDeletedBecauseParentIsGone();
		if (isSuperShared)
			return true;
		
		ORefList referrers = findObjectsThatReferToUs(TargetSchema.getObjectType());
		
		return referrers.size() > 0;
	}
	
	@Override
	public String getTypeName()
	{
		return OBJECT_NAME;
	}
	
	@Override
	public String getPseudoData(String fieldTag)
	{
		if (fieldTag.equals(PSEUDO_STRESS_RATING))
			return getCalculatedStressRating();
		
		return super.getPseudoData(fieldTag);
	}

	public String getCalculatedStressRating()
	{
		int calculatedStressRating = calculateStressRating();
		if (calculatedStressRating == 0)
			return "";
		
		return Integer.toString(calculatedStressRating);
	}
	
	public int calculateStressRating()
	{
		ChoiceItem scopeChoice = getChoiceItemData(TAG_SCOPE);
		if (scopeChoice.getCode().length() == 0)
			return 0;

		ChoiceItem severityChoice = getChoiceItemData(TAG_SEVERITY);
		if (severityChoice.getCode().length() == 0)
			return 0;
		
		int scopeRating = Integer.parseInt(scopeChoice.getCode());
		int severityRating = Integer.parseInt(severityChoice.getCode());
		StressBasedThreatFormula formula = new StressBasedThreatFormula();
		return formula.computeSeverityByScope(scopeRating, severityRating);
	}
	
	@Override
	public String getShortLabel()
	{
		return getData(TAG_SHORT_LABEL);
	}
	
	public static Stress find(ObjectManager objectManager, ORef stressRef)
	{
		return (Stress) objectManager.findObject(stressRef);
	}
	
	public static Stress find(Project project, ORef stressRef)
	{
		return find(project.getObjectManager(), stressRef);
	}
	
	public static boolean is(BaseObject baseObject)
	{
		return is(baseObject.getType());
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == getObjectType();
	}
			
	@Override
	public String toString()
	{
		return getLabel();
	}
	
	public static final String TAG_DETAIL = "Detail";
	public static final String TAG_SCOPE = "Scope";
	public static final String TAG_SEVERITY = "Severity";
	public static final String PSEUDO_STRESS_RATING = "PseudoStressRating";
	
	public static final String OBJECT_NAME = "Stress";
}
