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

package org.miradi.xml.xmpz2;

import java.util.Collections;
import java.util.Vector;

import org.miradi.diagram.ThreatTargetChainWalker;
import org.miradi.dialogs.threatrating.upperPanel.AbstractThreatTargetTableModel;
import org.miradi.dialogs.threatrating.upperPanel.TargetThreatLinkTableModel;
import org.miradi.objecthelpers.BaseObjectByRefSorter;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ThreatTargetVirtualLinkHelper;
import org.miradi.objects.Cause;
import org.miradi.objects.Stress;
import org.miradi.objects.Target;
import org.miradi.objects.ThreatRatingCommentsData;
import org.miradi.objects.ThreatStressRating;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.ThreatRatingModeChoiceQuestion;
import org.miradi.questions.ThreatRatingQuestion;
import org.miradi.utils.ThreatStressRatingDetailsTableExporter;

public class StressBasedThreatRatingExporter implements Xmpz2XmlConstants
{
	public StressBasedThreatRatingExporter(Xmpz2XmlWriter writerToUse)
	{
		writer = writerToUse;
	}

	public void writeThreatRatings() throws Exception
	{
		getWriter().writeStartPoolElement(getParentElementName());
		exportStressBasedThreatRating();
		getWriter().writeEndPoolElement(getParentElementName());
	}

	private Vector<Target> getSortedTargetsInConceptualModelDiagrams()
	{
		Vector<Target> targets = new Vector<Target>(TargetThreatLinkTableModel.getOnlyTargetsInConceptualModelDiagrams(getProject()));
		Collections.sort(targets, new BaseObjectByRefSorter());

		return targets;
	}

	private void exportStressBasedThreatRating() throws Exception
	{
		Vector<Target> targets = getSortedTargetsInConceptualModelDiagrams();
		for(Target target : targets)
		{
			if (target.getStressRefs().hasRefs())
				exportStressBasedThreatRatingDetailsRow(target);
		}
	}
	
	private void exportStressBasedThreatRatingDetailsRow(Target target) throws Exception
	{
		ORefList stressRefs = target.getStressRefs();
		for (int index = 0; index < stressRefs.size(); ++index)
		{
			Stress stress = Stress.find(getProject(), stressRefs.get(index));
			ThreatTargetChainWalker chainWalker = new ThreatTargetChainWalker(getProject());
			ORefSet upstreamThreatsFromTarget = chainWalker.getUpstreamThreatRefsFromTarget(target);
			ORefList sortedThreatRefs = new ORefList(upstreamThreatsFromTarget);
			sortedThreatRefs.sort();
			for(int threatIndex = 0; threatIndex < sortedThreatRefs.size(); ++threatIndex)
			{
				Cause threat = Cause.find(getProject(), sortedThreatRefs.get(threatIndex));
				getWriter().writeStartElement(getParentElementName());
				ORef targetRef = target.getRef();
				ORef threatRef = threat.getRef();
				exportThreatId(threatRef);
				exportStressId(stress.getRef());
				exportTargetId(targetRef);
				exportThreatRating(ThreatRatingModeChoiceQuestion.STRESS_BASED_CODE, targetRef, threatRef);
				exportStressBasedRatingComment(threatRef, targetRef);
				exportStressBasedThreatRatingDetails(target, stress, threat);
				getWriter().writeEndElement(getParentElementName());
			}
		}
	}

	private void exportThreatRating(String threatRatingMode, ORef targetRef, ORef threatRef) throws Exception
	{
		ThreatTargetVirtualLinkHelper threatTargetVirtualLink = new ThreatTargetVirtualLinkHelper(getProject());
		int calculatedValue = threatTargetVirtualLink.calculateThreatRatingBundleValue(threatRatingMode, threatRef, targetRef);
		String threatRatingCode = AbstractThreatTargetTableModel.convertIntToString(calculatedValue);
		final ChoiceQuestion threatRatingQuestion = getProject().getQuestion(ThreatRatingQuestion.class);
		getWriter().writeNonOptionalCodeElement(getParentElementName(), THREAT_TARGET_RATING, threatRatingQuestion, threatRatingCode);
	}

	private void exportStressBasedThreatRatingDetails(Target target, Stress stress, Cause threat) throws Exception
	{
		ChoiceItem irreversibility = ThreatStressRatingDetailsTableExporter.getIrreversibility(getProject(), target.getRef(), threat.getRef(), stress);
		getWriter().writeElement(getParentElementName() + IRREVERSIBILITY, irreversibility.getCode());
		
		ChoiceItem contribution = ThreatStressRatingDetailsTableExporter.getContribution(getProject(), target.getRef(), threat.getRef(), stress);
		getWriter().writeElement(getParentElementName() + CONTRIBUTION, contribution.getCode());
		
		ThreatStressRating threatStressRating = ThreatStressRatingDetailsTableExporter.findThreatStressRating(getProject(), target.getRef(), threat.getRef(), stress);
		if (threatStressRating != null)
			getWriter().writeElement(getParentElementName(), threatStressRating, ThreatStressRating.TAG_IS_ACTIVE);
		
		//FIXME do we want these two calculated elements exported? if yes, then add to schema
		//exportStressBasedStressRating(stress.getCalculatedStressRating());
		//exportStressBasedThreatStressRating(target.getRef(), threat.getRef());
	}
	
//	private void exportStressBasedThreatStressRating(ORef targetRef, ORef threatRef) throws Exception
//	{
//		ThreatTargetVirtualLinkHelper virtualLink = new ThreatTargetVirtualLinkHelper(getProject());
//		int rawThreatStressRating = virtualLink.calculateStressBasedThreatRating(threatRef, targetRef);
//		String safeThreatRatingCode = ThreatRatingFramework.getSafeThreatRatingCode(rawThreatStressRating);
//		ChoiceQuestion question = getProject().getQuestion(ThreatStressRatingChoiceQuestion.class);
//		exportStressBasedThreatRatingCode(THREAT_STRESS_RATING, question.findChoiceByCode(safeThreatRatingCode));
//	}
//
//	private void exportStressBasedStressRating(String stressRating) throws Exception
//	{
//		ChoiceQuestion question = getProject().getQuestion(StressRatingChoiceQuestion.class);
//		ChoiceItem stressRatingChoiceItem = question.findChoiceByCode(stressRating);
//		exportStressBasedThreatRatingCode(STRESS_RATING, stressRatingChoiceItem);
//	}
//	
//	private void exportStressBasedThreatRatingCode(String elementName, ChoiceItem rating) throws Exception
//	{
//		getWriter().writeStartElement(STRESS_BASED_THREAT_RATING + elementName);
//		getWriter().writeXmlText(rating.getCode());
//		getWriter().writeEndElement(STRESS_BASED_THREAT_RATING + elementName);
//	}

	private void exportStressBasedRatingComment(ORef threatRef, ORef targetRef) throws Exception
	{
		exportThreatRatingComment(threatRef, targetRef, ThreatRatingCommentsData.TAG_STRESS_BASED_THREAT_RATING_COMMENTS_MAP);
	}
	
	private void exportThreatRatingComment(ORef threatRef, ORef targetRef, String threatRatingCommentsMapTag) throws Exception
	{
		ThreatRatingCommentsData threatRatingCommentsData = getProject().getSingletonThreatRatingCommentsData();
		String threatTargetRefsAsKey = threatRatingCommentsData.createKey(threatRef, targetRef);
		String threatRatingComments = threatRatingCommentsData.getThreatRatingCommentsMap(threatRatingCommentsMapTag).getUserString(threatTargetRefsAsKey);
		getWriter().writeElement(getParentElementName() + COMMENTS, threatRatingComments);
	}
	
	private void exportThreatId(ORef threatRef) throws Exception
	{
		exportId(getParentElementName() + THREAT, THREAT, threatRef);
	}

	private void exportTargetId(ORef targetRef) throws Exception
	{
		exportId(getParentElementName() + TARGET, BIODIVERSITY_TARGET, targetRef);
	}
	
	private void exportStressId(ORef stressRef) throws Exception
	{
		exportId(getParentElementName() + STRESS, STRESS, stressRef);
	}

	private void exportId(String parentElementName, String idElementName, ORef ref) throws Exception
	{
		getWriter().writeStartElement(parentElementName + ID);
		
		getWriter().writeStartElement(idElementName + ID);
		getWriter().writeXmlText(ref.getObjectId().toString());
		getWriter().writeEndElement(idElementName + ID);
		
		getWriter().writeEndElement(parentElementName + ID);
	}
	
	private String getParentElementName()
	{
		return STRESS_BASED_THREAT_RATING;
	}
	
	private Xmpz2XmlWriter getWriter()
	{
		return writer;
	}
	
	private Project getProject()
	{
		return getWriter().getProject();
	}

	private Xmpz2XmlWriter writer;
}
