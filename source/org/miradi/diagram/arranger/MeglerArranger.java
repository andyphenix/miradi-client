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

package org.miradi.diagram.arranger;

import java.awt.Point;
import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.exceptions.UnexpectedNonSideEffectException;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.project.FactorCommandHelper;
import org.miradi.project.Project;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.GroupBoxSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.utils.NullProgressMeter;
import org.miradi.utils.ProgressInterface;
import org.miradi.views.diagram.LinkCreator;

public class MeglerArranger
{
	public MeglerArranger(DiagramObject diagramToArrange)
	{
		this(diagramToArrange, new NullProgressMeter());
	}
	
	public MeglerArranger(DiagramObject diagramToArrange, ProgressInterface progressMeterToUse)
	{
		diagram = diagramToArrange;
		progressMeter = progressMeterToUse;
	}

	public boolean arrange() throws Exception
	{
		if(hasUserRequestedStop())
			return false;
		extractFactorsOfInterest();
		if(hasUserRequestedStop())
			return false;
		segregateUnlinkedFactors();
		if(hasUserRequestedStop())
			return false;
		
		final int ITERATIONS = 3;
		progressMeter.setStatusMessage(EAM.text("Creating group boxes..."), ITERATIONS);
		for(int i = 0; i < ITERATIONS; ++i)
		{
			createGroupBoxes();
			progressMeter.incrementProgress();
		}
		createGroupLinks();
		
		if(hasUserRequestedStop())
			return false;
		setLocations();
		return true;
	}

	private boolean hasUserRequestedStop()
	{
		return progressMeter.shouldExit();
	}

	private void segregateUnlinkedFactors() throws Exception
	{
		final int steps = 3;
		progressMeter.setStatusMessage(EAM.text("Ignoring unlinked factors..."), steps);

		unlinked = new Vector<DiagramFactor>();

		unlinked.addAll(extractUnlinkedDiagramFactors(strategies));
		progressMeter.incrementProgress();
		unlinked.addAll(extractUnlinkedDiagramFactors(threats));
		progressMeter.incrementProgress();
		unlinked.addAll(extractUnlinkedDiagramFactors(targets));
		progressMeter.incrementProgress();
	}
	
	private void createGroupBoxes() throws Exception
	{
		createTargetGroups();

		if(targets.size() <= 1)
		{
			createThreatGroupsBasedOnStrategies();
			createThreatGroupsBasedOnTargets();
		}
		else
		{
			createThreatGroupsBasedOnTargets();
			createThreatGroupsBasedOnStrategies();
		}
		
		ceateStrategyGroups();
	}

	private void createThreatGroupsBasedOnStrategies() throws Exception
	{
		createGroupBoxes(threats, DiagramLink.FROM, StrategySchema.getObjectType());
	}

	private void createThreatGroupsBasedOnTargets() throws Exception
	{
		createGroupBoxes(threats, DiagramLink.TO, TargetSchema.getObjectType());
	}

	private void ceateStrategyGroups() throws Exception
	{
		createGroupBoxes(strategies, DiagramLink.TO, CauseSchema.getObjectType());
	}

	private void createTargetGroups() throws Exception
	{
		createGroupBoxes(targets, DiagramLink.FROM, CauseSchema.getObjectType());
	}

	private void createGroupBoxes(Vector<DiagramFactor> diagramFactorsToGroup, int direction, int objectTypeInThatDirection) throws Exception
	{
		Vector<Vector<DiagramFactor>> groupsToCreate = findBestGroups(diagramFactorsToGroup, direction, objectTypeInThatDirection);
		
		for(Vector<DiagramFactor> toGroup : groupsToCreate)
		{
			createAndLinkToGroupBox(toGroup, direction);
		}
	}

	private Vector<Vector<DiagramFactor>> findBestGroups(
			Vector<DiagramFactor> diagramFactorsToGroup, int direction,
			int objectTypeInThatDirection) throws Exception,
			UnexpectedNonSideEffectException, CommandFailedException
	{
		Vector<DiagramFactor> groupCandidates = new Vector<DiagramFactor>();
		groupCandidates.addAll(diagramFactorsToGroup);
		groupCandidates.removeAll(findAllThatAreLinkedToAGroup(groupCandidates, direction));
		groupCandidates.removeAll(findAllThatAreAlreadyGrouped(groupCandidates));
		
		sortGroupCandidatesByLinkCount(groupCandidates, direction);

		Vector<Vector<DiagramFactor>> groupsToCreate = new Vector<Vector<DiagramFactor>>();
		
		while(groupCandidates.size() > 1)
		{
			Vector<DiagramFactor> grouped = findBiggestPossibleGroup(groupCandidates, direction, objectTypeInThatDirection);
			if(grouped.size() == 0)
				break;
			if(grouped.size() > 1)
				groupsToCreate.add(new Vector<DiagramFactor>(grouped));
			groupCandidates.removeAll(grouped);
		}
		return groupsToCreate;
	}
	
	private HashSet<DiagramFactor> findAllThatAreAlreadyGrouped(Vector<DiagramFactor> groupCandidates)
	{
		HashSet<DiagramFactor> toRemove = new HashSet<DiagramFactor>();
		for(DiagramFactor diagramFactor : groupCandidates)
		{
			if(diagramFactor.isCoveredByGroupBox())
				toRemove.add(diagramFactor);
		}
		return toRemove;
	}

	class LinkCountComparator implements Comparator<DiagramFactor>
	{
		public LinkCountComparator(int directionToUse)
		{
			direction = directionToUse;
		}
		
		public int compare(DiagramFactor diagramFactor1, DiagramFactor diagramFactor2)
		{
			int linkCount1 = getRefsOfRelevantDiagramLinks(diagramFactor1).size();
			int linkCount2 = getRefsOfRelevantDiagramLinks(diagramFactor2).size();

			int diff = Math.abs(linkCount2 - linkCount1);
			if(diff > 0)
				return diff;
			
			return diagramFactor1.getRef().compareTo(diagramFactor2.getRef());
		}

		private ORefSet getRefsOfRelevantDiagramLinks(DiagramFactor diagramFactor)
		{
			ORefSet matchingDiagramLinkRefs = new ORefSet();
			ORef thisRef = diagramFactor.getRef();
			ORefList diagramLinkRefs = diagramFactor.findObjectsThatReferToUs(DiagramLinkSchema.getObjectType());
			for(int i = 0; i < diagramLinkRefs.size(); ++i)
			{
				ORef diagramLinkRef = diagramLinkRefs.get(i);
				DiagramLink diagramLink = DiagramLink.find(getProject(), diagramLinkRef);
				if(diagramLink.getOppositeDiagramFactorRef(direction).equals(thisRef))
					matchingDiagramLinkRefs.add(diagramLinkRef);
			}
			return matchingDiagramLinkRefs;
		}
		
		private int direction;
	}

	private void sortGroupCandidatesByLinkCount(Vector<DiagramFactor> groupCandidates, int direction)
	{
		Collections.sort(groupCandidates, new LinkCountComparator(direction));
	}

	private static Set<DiagramFactor> findAllThatAreLinkedToAGroup(Vector<DiagramFactor> groupCandidates, int direction)
	{
		HashSet<DiagramFactor> linkedToGroup = new HashSet<DiagramFactor>();
		for(DiagramFactor diagramFactor : groupCandidates)
		{
			ORefList ourLinks = diagramFactor.findObjectsThatReferToUs(DiagramLinkSchema.getObjectType());
			for(int i = 0; i < ourLinks.size(); ++i)
			{
				DiagramLink diagramLink = DiagramLink.find(diagramFactor.getProject(), ourLinks.get(i));
				if(diagramLink.getDiagramFactor(direction).getWrappedType() == GroupBoxSchema.getObjectType())
					linkedToGroup.add(diagramFactor);
			}
		}
		
		return linkedToGroup;
	}

	private Vector<DiagramFactor> findBiggestPossibleGroup(Vector<DiagramFactor> originalGroupCandidates, int direction, int objectTypeInThatDirection) throws Exception, UnexpectedNonSideEffectException, CommandFailedException
	{
		Vector<DiagramFactor> groupCandidates = new Vector<DiagramFactor>(originalGroupCandidates);
		while(groupCandidates.size() > 1)
		{
			int wouldRemoveLinkCount = 0;
			ORefSet fromDiagramFactorRefs = getRefsOfFactorsThatLink(groupCandidates, direction, objectTypeInThatDirection);
			for(ORef fromRef : fromDiagramFactorRefs)
			{
				if(isLinkedToAll(fromRef, groupCandidates, direction))
				{
					wouldRemoveLinkCount += groupCandidates.size();
				}
			}
			
			if(wouldRemoveLinkCount > 1)
			{
				break;
			}
			
			groupCandidates.remove(groupCandidates.size() - 1);
		}
		
		return groupCandidates;
	}

	private void createAndLinkToGroupBox(Vector<DiagramFactor> toBeGrouped, int direction) throws Exception, UnexpectedNonSideEffectException, CommandFailedException
	{
		if(toBeGrouped.size() == 0)
			throw new RuntimeException("Attempted to group zero factors");
		
		ORef newGroupDiagramFactorRef = createAndPopulateGroupBox(toBeGrouped);

		LinkCreator linkCreator = new LinkCreator(getProject());
		ORefSet refsOfDiagramFactorsThatLinkToEntireGroup = linkCreator.getRefsOfDiagramFactorsThatLinkToAllChildren(newGroupDiagramFactorRef, direction);

		for(ORef nonGroupedRef : refsOfDiagramFactorsThatLinkToEntireGroup)
		{
			DiagramFactor fromDiagramFactor = DiagramFactor.find(getProject(), nonGroupedRef);
			DiagramFactor toDiagramFactor = DiagramFactor.find(getProject(), newGroupDiagramFactorRef);
			if(direction == DiagramLink.TO)
			{
				DiagramFactor temp = fromDiagramFactor;
				fromDiagramFactor = toDiagramFactor;
				toDiagramFactor = temp;
			}
			linkCreator.createFactorLinkAndDiagramLinkVoid(diagram, fromDiagramFactor, toDiagramFactor);
		}
	}

	private ORef createAndPopulateGroupBox(Vector<DiagramFactor> toBeGrouped)
			throws Exception, UnexpectedNonSideEffectException,	CommandFailedException
	{
		ORefList childRefs = new ORefList(toBeGrouped.toArray(new DiagramFactor[0]));
		FactorCommandHelper helper = new FactorCommandHelper(getProject(), diagram);
		ORef newGroupDiagramFactorRef = new ORef(DiagramFactorSchema.getObjectType(), helper.createFactorAndDiagramFactor(GroupBoxSchema.getObjectType()).getCreatedId());
		CommandSetObjectData addChildren = new CommandSetObjectData(newGroupDiagramFactorRef, DiagramFactor.TAG_GROUP_BOX_CHILDREN_REFS, childRefs.toString());
		getProject().executeCommand(addChildren);
		return newGroupDiagramFactorRef;
	}

	private boolean isLinkedToAll(ORef fromRef, AbstractCollection<DiagramFactor> groupCandidates, int direction)
	{
		for(DiagramFactor factor : groupCandidates)
		{
			if(!diagram.areDiagramFactorsLinkedFromToNonBidirectional(fromRef, factor.getRef(), direction))
				return false;
		}
		
		return true;
	}

	private ORefSet getRefsOfFactorsThatLink(AbstractCollection<DiagramFactor> groupCandidates, int direction, int objectTypeInThatDirection)
	{
		ORefSet allFroms = new ORefSet();
		for(DiagramFactor factor : groupCandidates)
			allFroms.addAll(getRefsOfFactorsThatLink(factor, direction, objectTypeInThatDirection));
		
		return allFroms;
	}

	private ORefSet getRefsOfFactorsThatLink(DiagramFactor factor, int direction, int objectTypeInThatDirection)
	{
		ORefList linkRefs = factor.findObjectsThatReferToUs(DiagramLinkSchema.getObjectType());
		ORefSet froms = new ORefSet();
		for(int i = 0; i < linkRefs.size(); ++i)
		{
			DiagramLink link = DiagramLink.find(getProject(), linkRefs.get(i));
			if(link.getDiagramFactor(direction).getWrappedORef().getObjectType() == objectTypeInThatDirection)
				froms.add(link.getDiagramFactor(direction).getRef());
		}
		return froms;
	}
	
	private void createGroupLinks() throws Exception
	{
		LinkCreator linkCreator = new LinkCreator(getProject());
		Set<DiagramFactor> groupBoxDiagramFactors = diagram.getDiagramFactorsThatWrap(GroupBoxSchema.getObjectType());
		for(DiagramFactor groupBoxDiagramFactor : groupBoxDiagramFactors)
		{
			linkCreator.createAllPossibleGroupLinks(diagram, groupBoxDiagramFactor);
		}
	}

	private Vector<DiagramFactor> extractUnlinkedDiagramFactors(Vector<DiagramFactor> candidates)
	{
		Vector<DiagramFactor> unlinkedDiagramFactors = new Vector<DiagramFactor>();
		for(DiagramFactor diagramFactor : candidates)
		{
			ORefList linkRefs = diagramFactor.findObjectsThatReferToUs(DiagramLinkSchema.getObjectType());
			if(linkRefs.size() == 0)
				unlinkedDiagramFactors.add(diagramFactor);
		}
		candidates.removeAll(unlinkedDiagramFactors);
		return unlinkedDiagramFactors;
	}

	private void setLocations() throws Exception
	{
		final int buildSteps = 3;
		progressMeter.setStatusMessage(EAM.text("Preparing groups..."), buildSteps);

		Vector<DiagramFactorClump> strategyClumps = buildClumps(strategies);
		progressMeter.incrementProgress();
		Vector<DiagramFactorClump> threatClumps = buildClumps(threats);
		progressMeter.incrementProgress();
		Vector<DiagramFactorClump> targetClumps = buildClumps(targets);
		progressMeter.incrementProgress();

		rearrangeClumps(strategyClumps, threatClumps, targetClumps);
		
		final int moveSteps = 4;
		progressMeter.setStatusMessage(EAM.text("Updating locations..."), moveSteps);

		moveFactorsToFinalLocations(unlinked, UNLINKED_COLUMN_X, TOP_Y);
		progressMeter.incrementProgress();
		moveFactorClumpsToFinalLocations(targetClumps, TARGET_COLUMN_X, TOP_Y);
		progressMeter.incrementProgress();
		moveFactorClumpsToFinalLocations(threatClumps, THREAT_COLUMN_X, TOP_Y);
		progressMeter.incrementProgress();
		moveFactorClumpsToFinalLocations(strategyClumps, STRATEGY_COLUMN_X, TOP_Y);
		progressMeter.incrementProgress();
	}

	private Vector<DiagramFactorClump> buildClumps(Vector<DiagramFactor> diagramFactors)
	{
		Vector<DiagramFactorClump> clumps = new Vector<DiagramFactorClump>();
		
		for(DiagramFactor diagramFactor : diagramFactors)
		{
			DiagramFactor diagramFactorMaybeGroup = diagramFactor;
			DiagramFactor group = findGroup(diagramFactor);
			if(group != null)
				diagramFactorMaybeGroup = group;

			DiagramFactorClump newClump = new DiagramFactorClump(diagram, diagramFactorMaybeGroup);
			if(!clumps.contains(newClump))
				clumps.add(newClump);
		}
		return clumps;
	}

	private DiagramFactorClump findMostActiveClump(Vector<DiagramFactorClump> clumps)
	{
		DiagramFactorClump mostActive = null;
		for(DiagramFactorClump clump : clumps)
		{
			int thisLinkCount = clump.getTotalLinkCount();
			if(mostActive == null || thisLinkCount > mostActive.getTotalLinkCount())
				mostActive = clump;
		}
		
		return mostActive;
	}

	private DiagramFactor findGroup(DiagramFactor diagramFactor)
	{
		ORefList referringDiagramFactorRefs = diagramFactor.findObjectsThatReferToUs(DiagramFactorSchema.getObjectType());
		if(referringDiagramFactorRefs.size() < 1)
			return null;
		
		ORef groupRef = referringDiagramFactorRefs.get(0);
		DiagramFactor group = DiagramFactor.find(getProject(), groupRef);
		return group;
	}

	private void moveFactorsToFinalLocations(Vector<DiagramFactor> factors, int x, int initialY) throws Exception
	{
		int y = initialY;
		FactorCommandHelper helper = new FactorCommandHelper(getProject(), diagram);
		for(DiagramFactor diagramFactor : factors)
		{
			Point newLocation = new Point(x, y);
			helper.setDiagramFactorLocation(diagramFactor.getDiagramFactorId(), newLocation);
			
			int height = diagramFactor.getSize().height;
			y += Math.max(height + VERTICAL_CUSHION, DELTA_Y);
		}
	}
	
	private void rearrangeClumps(Vector<DiagramFactorClump> strategyClumps, Vector<DiagramFactorClump> threatClumps, Vector<DiagramFactorClump> targetClumps) throws Exception
	{

		Vector<DiagramFactorClump> arrangedStrategyClumps = new Vector<DiagramFactorClump>();
		Vector<DiagramFactorClump> arrangedThreatClumps = new Vector<DiagramFactorClump>();
		Vector<DiagramFactorClump> arrangedTargetClumps = new Vector<DiagramFactorClump>();
		
		progressMeter.setStatusMessage(EAM.text("Arranging Threats..."), threatClumps.size());
		while(threatClumps.size() > 0)
		{
			DiagramFactorClump mostActiveThreatClump = findMostActiveClump(threatClumps);
			
			arrangedThreatClumps.add(mostActiveThreatClump);
			threatClumps.remove(mostActiveThreatClump);

			progressMeter.incrementProgress();
		}
		
		progressMeter.setStatusMessage(EAM.text("Arranging Targets..."), arrangedThreatClumps.size());
		for(DiagramFactorClump diagramFactorClump : arrangedThreatClumps)
		{
			addRelatedToArrangedList(arrangedTargetClumps, targetClumps, diagramFactorClump, DiagramLink.TO);
			progressMeter.incrementProgress();
		}
		
		progressMeter.setStatusMessage(EAM.text("Arranging Strategies..."), arrangedThreatClumps.size());
		for(DiagramFactorClump diagramFactorClump : arrangedThreatClumps)
		{
			addRelatedToArrangedList(arrangedStrategyClumps, strategyClumps, diagramFactorClump, DiagramLink.FROM);
			progressMeter.incrementProgress();
		}
		
		arrangedStrategyClumps.addAll(strategyClumps);
		arrangedTargetClumps.addAll(targetClumps);
		
		strategyClumps.clear();
		strategyClumps.addAll(arrangedStrategyClumps);
		threatClumps.clear();
		threatClumps.addAll(arrangedThreatClumps);
		targetClumps.clear();
		targetClumps.addAll(arrangedTargetClumps);
	}
	
	private void addRelatedToArrangedList(Vector<DiagramFactorClump> arranged, Vector<DiagramFactorClump> candidatesToInsert, DiagramFactorClump relatedTo, int direction)
	{
		Set<DiagramLink> links = relatedTo.getLinks(direction);
		for(DiagramLink diagramLink : links)
		{
			DiagramFactor other = diagramLink.getDiagramFactor(direction);
			DiagramFactorClump clump = findClump(candidatesToInsert, other);
			if(clump == null)
				continue;
			arranged.add(clump);
			candidatesToInsert.remove(clump);
		}
		
	}
	
	private DiagramFactorClump findClump(Vector<DiagramFactorClump> clumpsToSearch, DiagramFactor diagramFactorOrGroupToFind)
	{
		for(DiagramFactorClump diagramFactorClump : clumpsToSearch)
		{
			for(int i = 0; i < diagramFactorClump.getRowCount(); ++i)
			{
				if(diagramFactorClump.getDiagramFactor(i).equals(diagramFactorOrGroupToFind))
					return diagramFactorClump;
			}
		}
		
		return null;
	}
	
	private void moveFactorClumpsToFinalLocations(Vector<DiagramFactorClump> threatClumps, int x, int topY) throws Exception
	{
		int y = topY;
		FactorCommandHelper helper = new FactorCommandHelper(getProject(), diagram);
		for(DiagramFactorClump threatClump : threatClumps)
		{
			for(int i = 0; i < threatClump.getRowCount(); ++i)
			{
				DiagramFactor diagramFactor = threatClump.getDiagramFactor(i);

				Point newLocation = new Point(x, y);
				
				helper.setDiagramFactorLocation(diagramFactor.getDiagramFactorId(), newLocation);
				
				int height = diagramFactor.getSize().height;
				y += Math.max(height + VERTICAL_CUSHION, DELTA_Y);
			}
		}
	}

	private Project getProject()
	{
		return diagram.getProject();
	}

	private void extractFactorsOfInterest() throws Exception
	{
		strategies = new Vector<DiagramFactor>();
		threats = new Vector<DiagramFactor>();
		targets = new Vector<DiagramFactor>();
		
		Project project = diagram.getProject();
		ORefList diagramFactorRefs = diagram.getAllDiagramFactorRefs();

		progressMeter.setStatusMessage(EAM.text("Extracting factors..."), diagramFactorRefs.size());
		for(int i = 0; i < diagramFactorRefs.size(); ++i)
		{
			progressMeter.incrementProgress();
			
			DiagramFactor diagramFactor = DiagramFactor.find(project, diagramFactorRefs.get(i));
			if(isAlreadyInGroup(diagramFactor))
				continue;
			Factor factor = diagramFactor.getWrappedFactor();
			if(Strategy.is(factor))
				strategies.add(diagramFactor);
			if(Target.is(factor))
				targets.add(diagramFactor);
			if(Cause.isDirectThreat(factor))
				threats.add(diagramFactor);
		}
	}

	private boolean isAlreadyInGroup(DiagramFactor diagramFactor)
	{
		ORef childRef = diagramFactor.getRef();
		ORefList likelyGroupRefs = diagramFactor.findObjectsThatReferToUs(DiagramFactorSchema.getObjectType());
		for(int i = 0; i < likelyGroupRefs.size(); ++i)
		{
			DiagramFactor possibleGroup = DiagramFactor.find(getProject(), likelyGroupRefs.get(i));
			if(possibleGroup.getGroupBoxChildrenRefs().contains(childRef))
				return true;
		}
		
		return false;
	}

	private static final int UNLINKED_COLUMN_X = 30;
	private static final int STRATEGY_COLUMN_X = 330;
	private static final int THREAT_COLUMN_X = 630;
	private static final int TARGET_COLUMN_X = 930;
	
	private static final int VERTICAL_CUSHION = 45;
	private static final int TOP_Y = VERTICAL_CUSHION * 2;
	private static final int DELTA_Y = 60 + VERTICAL_CUSHION;

	private ProgressInterface progressMeter;
	private DiagramObject diagram;
	private Vector<DiagramFactor> strategies;
	private Vector<DiagramFactor> threats;
	private Vector<DiagramFactor> targets;
	private Vector<DiagramFactor> unlinked;
	
}
