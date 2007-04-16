/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objectdata.ChoiceData;
import org.conservationmeasures.eam.objectdata.StringData;
import org.conservationmeasures.eam.objecthelpers.DirectThreatSet;
import org.conservationmeasures.eam.objecthelpers.FactorSet;
import org.conservationmeasures.eam.objecthelpers.NonDraftStrategySet;
import org.conservationmeasures.eam.objecthelpers.TargetSet;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.questions.StatusQuestion;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

abstract public class Desire extends BaseObject
{
	public Desire(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse);
		clear();
	}

	public Desire(BaseId idToUse)
	{
		super(idToUse);
		clear();
	}
	
	public Desire(ObjectManager objectManager, BaseId idToUse, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, idToUse, json);
	}

	
	public Desire(BaseId idToUse, EnhancedJsonObject json) throws Exception
	{
		super(idToUse, json);
	}

	abstract public int getType();

	public String getShortLabel()
	{
		return shortLabel.get();
	}

	public String toString()
	{
		if(getId().isInvalid())
			return "(None)";
		return combineShortLabelAndLabel(shortLabel.toString(), getLabel());
	}

	public String getPseudoData(String fieldTag)
	{
		if(fieldTag.equals(PSEUDO_TAG_TARGETS))
			return getRelatedLabelsAsMultiLine(new TargetSet());
		
		if(fieldTag.equals(PSEUDO_TAG_DIRECT_THREATS))
			return getRelatedLabelsAsMultiLine(new DirectThreatSet());;
		
		if(fieldTag.equals(PSEUDO_TAG_STRATEGIES))
			return getRelatedLabelsAsMultiLine(new NonDraftStrategySet());
		
		if(fieldTag.equals(PSEUDO_TAG_FACTOR))
			return getRelatedLabelsAsMultiLine(new FactorSet());
		
		return super.getPseudoData(fieldTag);
	}
	
	void clear()
	{
		super.clear();
		shortLabel = new StringData();
		fullText = new StringData();	
		desiredStatus = new ChoiceData();;
		byWhen = new StringData();
		desiredSummary = new StringData();
		desiredDetail = new StringData();
		desiredStatusLabel = new PseudoQuestionData(new StatusQuestion(Goal.TAG_DESIRED_STATUS));
		multiLineTargets = new PseudoStringData(PSEUDO_TAG_TARGETS);
		multiLineDirectThreats = new PseudoStringData(PSEUDO_TAG_DIRECT_THREATS);
		multiLineStrategies = new PseudoStringData(PSEUDO_TAG_STRATEGIES);
		multiLineFactor = new PseudoStringData(PSEUDO_TAG_FACTOR);
		
		addField(TAG_SHORT_LABEL, shortLabel);
		addField(TAG_FULL_TEXT, fullText);
		addField(TAG_DESIRED_STATUS, desiredStatus);
		addField(TAG_BY_WHEN, byWhen);
		addField(TAG_DESIRED_SUMMARY, desiredSummary);
		addField(TAG_DESIRED_DETAIL, desiredDetail);
		addField(PSEUDO_TAG_DESIRED_STATUS_VALUE, desiredStatusLabel);
		addField(PSEUDO_TAG_TARGETS, multiLineTargets);
		addField(PSEUDO_TAG_DIRECT_THREATS, multiLineDirectThreats);
		addField(PSEUDO_TAG_STRATEGIES, multiLineStrategies);
		addField(PSEUDO_TAG_FACTOR, multiLineFactor);
	}
	
	public final static String TAG_SHORT_LABEL = "ShortLabel";
	public final static String TAG_FULL_TEXT = "FullText";
	public final static String TAG_DESIRED_STATUS = "DesiredStatus";
	public final static String TAG_BY_WHEN = "ByWhen";
	public final static String TAG_DESIRED_SUMMARY = "DesiredSummary";
	public final static String TAG_DESIRED_DETAIL = "DesiredDetail";
	public final static String PSEUDO_TAG_DESIRED_STATUS_VALUE = "DesiredStatusValue";
	public final static String PSEUDO_TAG_TARGETS = "PseudoTagTargets";
	public final static String PSEUDO_TAG_DIRECT_THREATS = "PseudoTagDirectThreats";
	public final static String PSEUDO_TAG_STRATEGIES = "PseudoTagStrategies";
	public final static String PSEUDO_TAG_FACTOR = "PseudoTagFactor";
	
	
	public static final String OBJECT_NAME = "Desire";

	StringData shortLabel;
	StringData fullText;
	ChoiceData desiredStatus;
	StringData byWhen;
	StringData desiredSummary;
	StringData desiredDetail;
	PseudoQuestionData desiredStatusLabel;
	PseudoStringData multiLineTargets;
	PseudoStringData multiLineDirectThreats;
	PseudoStringData multiLineStrategies;
	PseudoStringData multiLineFactor;
}
