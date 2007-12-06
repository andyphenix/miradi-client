/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.project;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objects.RatingCriterion;
import org.conservationmeasures.eam.objects.ValueOption;

public class SimpleThreatFormula extends ThreatFormula
{
	public SimpleThreatFormula()
	{
	}
	
	public SimpleThreatFormula(SimpleThreatRatingFramework frameworkToUse)
	{
		framework = frameworkToUse;
	}
	
	public int computeMagnitude(int scope, int severity)
	{		
		if(scope == 0 || severity == 0)
			return 0;
		if(scope == 1 || severity == 1)
			return 1;
		if(scope == 2 || severity == 2)
			return 2;
		if(scope == 3 || severity == 3)
			return 3;
		if(scope == 4 || severity == 4)
			return 4;
		
		throw new RuntimeException("unknown scope or severity" + scope + " " + severity);
	}
	
	public int computeSeriousness(int magnitude, int irreversibility)
	{
		if(magnitude == 0 || irreversibility == 0)
			return 0;
		if(irreversibility == 2 || irreversibility == 3)
			return magnitude;
		if(magnitude == 4 && irreversibility == 4)
			return 4;
		if(magnitude == 1 && irreversibility == 1)
			return 1;
		if(irreversibility == 4)
			return (magnitude + 1);
		if(irreversibility == 1)
			return (magnitude - 1);
		
		throw new RuntimeException("unknown magnitude or urgency" + magnitude + " " + irreversibility);
	}
	
	public int computeBundleValue(int scope, int severity, int irreversibility)
	{
		return computeSeriousness(computeMagnitude(scope, severity), irreversibility);
	}
	
	public int computeBundleValue(ThreatRatingBundle bundle)
	{
		
		int scope = getCriterionValue(bundle, "Scope");
		int severity = getCriterionValue(bundle, "Severity");
		int urgency = getCriterionValue(bundle, "Irreversibility");
		
		return computeBundleValue(scope, severity, urgency);
	}
	
	public int getHighestValue(int[] values)
	{
		int highestValue = 0;
		
		for(int i = 0; i < values.length; ++i)
		{
			if(values[i] > highestValue)
				highestValue = values[i];
		}
		
		return highestValue;
	}

	public int getMajority(int values[])
	{
		int numberOfLegalValues = 5;
		int rankTotals[] = new int[numberOfLegalValues];
		
		for(int i = 0; i < values.length; ++i)
		{
			++rankTotals[values[i]];
		}

		int total = values.length;
		int half = total/2;

		int cumulative = 0;
		for(int i = numberOfLegalValues -1; i >= 0; --i)
		{
			cumulative += rankTotals[i];
			if(cumulative > half)
				return i;
		}

		return 0;
	}
	
	private int getCriterionValue(ThreatRatingBundle bundle, String label)
	{
		RatingCriterion criterion = framework.findCriterionByLabel(label);
		if(criterion == null)
			return DEFAULT_VALUE;
		
		BaseId criterionId = criterion.getId();
		BaseId valueId = bundle.getValueId(criterionId);
		ValueOption valueOption = framework.getValueOption(valueId);
		return valueOption.getNumericValue();
	}
	
	static final int DEFAULT_VALUE = 0;
	
	SimpleThreatRatingFramework framework;
}
