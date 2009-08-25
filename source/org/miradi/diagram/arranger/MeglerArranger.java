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
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.objects.FactorLink;
import org.miradi.objects.GroupBox;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.project.FactorCommandHelper;
import org.miradi.project.Project;
import org.miradi.views.diagram.LinkCreator;

public class MeglerArranger
{
	public MeglerArranger(DiagramObject diagramToArrange)
	{
		diagram = diagramToArrange;
		
	}

	public void arrange() throws Exception
	{
		extractFactorsOfInterest();
		segregateUnlinkedFactors();
		createGroupBoxes();
		setLocations();
	}

	private void segregateUnlinkedFactors()
	{
		unlinked = new Vector<DiagramFactor>();
		
		unlinked.addAll(extractUnlinkedDiagramFactors(strategies));
		unlinked.addAll(extractUnlinkedDiagramFactors(threats));
		unlinked.addAll(extractUnlinkedDiagramFactors(targets));
	}
	
	private void createGroupBoxes() throws Exception
	{
		createGroupBoxes(targets, FactorLink.FROM, Cause.getObjectType());
		createGroupBoxes(threats, FactorLink.TO, Target.getObjectType());
		createGroupBoxes(threats, FactorLink.FROM, Strategy.getObjectType());
		createGroupBoxes(strategies, FactorLink.TO, Cause.getObjectType());
	}

	private void createGroupBoxes(Vector<DiagramFactor> diagramFactorsToGroup, int direction, int objectTypeInThatDirection) throws Exception
	{
		Vector<DiagramFactor> groupCandidates = new Vector<DiagramFactor>();
		groupCandidates.addAll(diagramFactorsToGroup);
		groupCandidates.removeAll(findAllThatAreLinkedToAGroup(groupCandidates, direction));
		groupCandidates.removeAll(findAllThatAreAlreadyGrouped(groupCandidates));
		
		sortGroupCandidatesByLinkCount(groupCandidates);

		while(groupCandidates.size() > 1)
		{
			Vector<DiagramFactor> groupedTargets = createBiggestPossibleGroup(new Vector<DiagramFactor>(groupCandidates), direction, objectTypeInThatDirection);
			if(groupedTargets.size() == 0)
				break;
			groupCandidates.removeAll(groupedTargets);
		}
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

	class LinkCountComparator implements Comparator
	{
		public int compare(Object arg1, Object arg2)
		{
			if(!(arg1 instanceof DiagramFactor))
				throw new RuntimeException("Unexpected type: " + arg1.getClass().getCanonicalName());
			if(!(arg2 instanceof DiagramFactor))
				throw new RuntimeException("Unexpected type: " + arg2.getClass().getCanonicalName());
			
			DiagramFactor diagramFactor1 = (DiagramFactor)arg1;
			DiagramFactor diagramFactor2 = (DiagramFactor)arg2;
			
			int linkCount1 = getRefsOfFactorsThatLink(diagramFactor1).size();
			int linkCount2 = getRefsOfFactorsThatLink(diagramFactor2).size();

			return Math.abs(linkCount2 - linkCount1);
		}

		private ORefSet getRefsOfFactorsThatLink(DiagramFactor diagramFactor)
		{
			return new ORefSet(diagramFactor.findObjectsThatReferToUs(DiagramLink.getObjectType()));
		}
		
	}

	private void sortGroupCandidatesByLinkCount(Vector<DiagramFactor> groupCandidates)
	{
		Collections.sort(groupCandidates, new LinkCountComparator());
	}

	private Set<DiagramFactor> findAllThatAreLinkedToAGroup(Vector<DiagramFactor> groupCandidates, int direction)
	{
		HashSet<DiagramFactor> linkedToGroup = new HashSet<DiagramFactor>();
		for(DiagramFactor diagramFactor : groupCandidates)
		{
			ORefList ourLinks = diagramFactor.findObjectsThatReferToUs(DiagramLink.getObjectType());
			for(int i = 0; i < ourLinks.size(); ++i)
			{
				DiagramLink diagramLink = DiagramLink.find(getProject(), ourLinks.get(i));
				if(diagramLink.getDiagramFactor(direction).getWrappedType() == GroupBox.getObjectType())
					linkedToGroup.add(diagramFactor);
			}
		}
		
		return linkedToGroup;
	}

	private Vector<DiagramFactor> createBiggestPossibleGroup(Vector<DiagramFactor> groupCandidates, int direction, int objectTypeInThatDirection) throws Exception, UnexpectedNonSideEffectException, CommandFailedException
	{
		while(groupCandidates.size() > 1)
		{
			int wouldRemoveLinkCount = 0;
			ORefSet fromDiagramFactorRefs = getRefsOfFactorsThatLink(groupCandidates, direction, objectTypeInThatDirection);
			ORefSet fromDiagramFactorRefsThatLinkToAll = new ORefSet();
			for(ORef fromRef : fromDiagramFactorRefs)
			{
				if(isLinkedToAll(fromRef, groupCandidates, direction))
				{
					wouldRemoveLinkCount += groupCandidates.size();
					fromDiagramFactorRefsThatLinkToAll.add(fromRef);
				}
			}
			
			if(wouldRemoveLinkCount > 1)
			{
				createAndLinkToGroupBox(fromDiagramFactorRefsThatLinkToAll, groupCandidates, direction);
				break;
			}
			
			groupCandidates.remove(groupCandidates.size() - 1);
		}
		
		return groupCandidates;
	}

	private void createAndLinkToGroupBox(ORefSet nonGroupedDiagramFactorRefs, Vector<DiagramFactor> groupCandidates, int direction) throws Exception, UnexpectedNonSideEffectException, CommandFailedException
	{
		ORefList childRefs = new ORefList(groupCandidates.toArray(new DiagramFactor[0]));
		FactorCommandHelper helper = new FactorCommandHelper(getProject(), diagram);
		ORef newGroupDiagramFactorRef = new ORef(DiagramFactor.getObjectType(), helper.createFactorAndDiagramFactor(GroupBox.getObjectType()).getCreatedId());
		CommandSetObjectData addChildren = new CommandSetObjectData(newGroupDiagramFactorRef, DiagramFactor.TAG_GROUP_BOX_CHILDREN_REFS, childRefs.toString());
		getProject().executeCommand(addChildren);
		LinkCreator linkCreator = new LinkCreator(getProject());
		for(ORef nonGroupedRef : nonGroupedDiagramFactorRefs)
		{
			DiagramFactor fromDiagramFactor = DiagramFactor.find(getProject(), nonGroupedRef);
			DiagramFactor toDiagramFactor = DiagramFactor.find(getProject(), newGroupDiagramFactorRef);
			if(direction == FactorLink.TO)
			{
				DiagramFactor temp = fromDiagramFactor;
				fromDiagramFactor = toDiagramFactor;
				toDiagramFactor = temp;
			}
			linkCreator.createFactorLinkAndDiagramLink(diagram, fromDiagramFactor, toDiagramFactor);
		}
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
		ORefList linkRefs = factor.findObjectsThatReferToUs(DiagramLink.getObjectType());
		ORefSet froms = new ORefSet();
		for(int i = 0; i < linkRefs.size(); ++i)
		{
			DiagramLink link = DiagramLink.find(getProject(), linkRefs.get(i));
			if(link.getDiagramFactor(direction).getWrappedORef().getObjectType() == objectTypeInThatDirection)
				froms.add(link.getDiagramFactor(direction).getRef());
		}
		return froms;
	}

	private Vector<DiagramFactor> extractUnlinkedDiagramFactors(Vector<DiagramFactor> candidates)
	{
		Vector<DiagramFactor> unlinkedDiagramFactors = new Vector<DiagramFactor>();
		for(DiagramFactor diagramFactor : candidates)
		{
			ORefList linkRefs = diagramFactor.findObjectsThatReferToUs(DiagramLink.getObjectType());
			if(linkRefs.size() == 0)
				unlinkedDiagramFactors.add(diagramFactor);
		}
		candidates.removeAll(unlinkedDiagramFactors);
		return unlinkedDiagramFactors;
	}

	private void setLocations() throws Exception
	{
		Vector<DiagramFactorClump> strategyClumps = buildClumps(strategies);
		Vector<DiagramFactorClump> threatClumps = buildClumps(threats);
		Vector<DiagramFactorClump> targetClumps = buildClumps(targets);
		
		rearrangeClumps(strategyClumps, threatClumps, targetClumps);
		
		moveFactorsToFinalLocations(unlinked, UNLINKED_COLUMN_X, TOP_Y);
		moveFactorClumpsToFinalLocations(strategyClumps, STRATEGY_COLUMN_X, TOP_Y);
		moveFactorClumpsToFinalLocations(threatClumps, THREAT_COLUMN_X, TOP_Y);
		moveFactorClumpsToFinalLocations(targetClumps, TARGET_COLUMN_X, TOP_Y);
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
		ORefList referringDiagramFactorRefs = diagramFactor.findObjectsThatReferToUs(DiagramFactor.getObjectType());
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
	
	private void rearrangeClumps(Vector<DiagramFactorClump> strategyClumps, Vector<DiagramFactorClump> threatClumps, Vector<DiagramFactorClump> targetClumps)
	{
		Vector<DiagramFactorClump> arrangedStrategyClumps = new Vector<DiagramFactorClump>();
		Vector<DiagramFactorClump> arrangedThreatClumps = new Vector<DiagramFactorClump>();
		Vector<DiagramFactorClump> arrangedTargetClumps = new Vector<DiagramFactorClump>();
		
		while(threatClumps.size() > 0)
		{
			DiagramFactorClump mostActiveThreatClump = findMostActiveClump(threatClumps);
			
			arrangedThreatClumps.add(mostActiveThreatClump);
			threatClumps.remove(mostActiveThreatClump);

			addRelatedToArrangedList(arrangedStrategyClumps, strategyClumps, mostActiveThreatClump, FactorLink.FROM);
			addRelatedToArrangedList(arrangedTargetClumps, targetClumps, mostActiveThreatClump, FactorLink.TO);
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

	private void extractFactorsOfInterest()
	{
		strategies = new Vector<DiagramFactor>();
		threats = new Vector<DiagramFactor>();
		targets = new Vector<DiagramFactor>();
		
		Project project = diagram.getProject();
		ORefList diagramFactorRefs = diagram.getAllDiagramFactorRefs();
		for(int i = 0; i < diagramFactorRefs.size(); ++i)
		{
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
		ORefList likelyGroupRefs = diagramFactor.findObjectsThatReferToUs(DiagramFactor.getObjectType());
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

	private DiagramObject diagram;
	private Vector<DiagramFactor> strategies;
	private Vector<DiagramFactor> threats;
	private Vector<DiagramFactor> targets;
	private Vector<DiagramFactor> unlinked;
	
}
