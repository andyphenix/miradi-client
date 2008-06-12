/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
import org.miradi.objectdata.ChoiceData;
import org.miradi.objectdata.StringData;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.project.StressBasedThreatFormula;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.StressRatingChoiceQuestion;
import org.miradi.questions.StressScopeChoiceQuestion;
import org.miradi.questions.StressSeverityChoiceQuestion;
import org.miradi.utils.EnhancedJsonObject;

public class Stress extends Factor
{
	public Stress(ObjectManager objectManager, FactorId idToUse)
	{
		super(objectManager, idToUse, Factor.TYPE_STRESS);
		clear();
	}
	
	public Stress(ObjectManager objectManager, FactorId idToUse, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, idToUse, Factor.TYPE_STRESS, json);
	}
	
	public int getType()
	{
		return getObjectType();
	}
	
	public static int getObjectType()
	{
		return ObjectType.STRESS;
	}
	
	public boolean isStress()
	{
		return true;
	}
	
	public String getTypeName()
	{
		return OBJECT_NAME;
	}
	
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
		ChoiceItem scopeChoice = new StressScopeChoiceQuestion().findChoiceByCode(scope.get());
		if (scopeChoice.getCode().length() == 0)
			return 0;

		ChoiceItem severityChoice = new StressSeverityChoiceQuestion().findChoiceByCode(severity.get());
		if (severityChoice.getCode().length() == 0)
			return 0;
		
		int scopeRating = Integer.parseInt(scopeChoice.getCode());
		int severityRating = Integer.parseInt(severityChoice.getCode());
		StressBasedThreatFormula formula = new StressBasedThreatFormula();
		return formula.computeSeverityByScope(scopeRating, severityRating);
	}
	
	public String getShortLabel()
	{
		return shortLabel.get();
	}
	
	public static Stress find(ObjectManager objectManager, ORef stressRef)
	{
		return (Stress) objectManager.findObject(stressRef);
	}
	
	public static Stress find(Project project, ORef stressRef)
	{
		return find(project.getObjectManager(), stressRef);
	}
		
	public String toString()
	{
		return getLabel();
	}
	
	public void clear()
	{
		super.clear();
		
		shortLabel = new StringData(TAG_SHORT_LABEL);
		detail = new StringData(TAG_DETAIL);
		comments = new StringData(TAG_COMMENTS);
		scope = new ChoiceData(TAG_SCOPE, getQuestion(StressScopeChoiceQuestion.class));
		severity = new ChoiceData(TAG_SEVERITY, getQuestion(StressSeverityChoiceQuestion.class));
		pseudoStressRating = new PseudoQuestionData(PSEUDO_STRESS_RATING, new StressRatingChoiceQuestion());
		
		addField(TAG_SHORT_LABEL, shortLabel);
		addField(TAG_DETAIL, detail);
		addField(TAG_COMMENTS, comments);
		addField(TAG_SCOPE, scope);
		addField(TAG_SEVERITY, severity);
		addField(PSEUDO_STRESS_RATING, pseudoStressRating);
	}
	
	public static final String TAG_SHORT_LABEL = "ShortLabel";
	public static final String TAG_DETAIL = "Detail";
	public static final String TAG_COMMENTS = "Comments";
	public static final String TAG_SCOPE = "Scope";
	public static final String TAG_SEVERITY = "Severity";
	public static final String PSEUDO_STRESS_RATING = "PseudoStressRating";
	
	private StringData shortLabel;
	private StringData detail;
	private StringData comments;
	private ChoiceData scope;
	private ChoiceData severity;
	
	private PseudoQuestionData pseudoStressRating;
	public static final String OBJECT_NAME = "Stress";
}
