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
package org.miradi.project;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.main.Miradi;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.ThreatStressRatingEnsurer;
import org.miradi.objectpools.ObjectPool;
import org.miradi.objectpools.PoolWithIdAssigner;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.Assignment;
import org.miradi.objects.BaseObject;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.FactorLink;
import org.miradi.objects.GroupBox;
import org.miradi.objects.Indicator;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.TableSettings;
import org.miradi.objects.TaggedObjectSet;
import org.miradi.objects.ThreatStressRating;
import org.miradi.objects.ViewData;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.ExpenseAssignmentSchema;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.ResourceAssignmentSchema;
import org.miradi.schemas.TaggedObjectSetSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.utils.BaseObjectDeepCopier;
import org.miradi.utils.BaseObjectDeepCopierNotUsingCommands;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.HtmlUtilities;

public class ProjectRepairer
{
	public ProjectRepairer(Project projectToRepair)
	{
		project = projectToRepair;
		messages = new TreeSet<String>();
	}
	
	public int quarantineOrphans() throws Exception
	{
		int quarantinedCount = 0;
		while(true)
		{
			untagOrphans();
			Vector<ORef> orphanRefs = findOrphans();
			if(orphanRefs.size() == 0)
				break;
			
			quarantinedCount += quarantineObjects(orphanRefs);
		}
		
		return quarantinedCount;
	}

	private int quarantineObjects(Vector<ORef> orphanRefs) throws Exception
	{
		for(ORef orphanRef : orphanRefs)
		{
			BaseObject object = BaseObject.find(getProject(), orphanRef);
			if(!object.isEmpty())
				addToQuarantine(object);
			getProject().deleteObject(object);
		}
		if(orphanRefs.size() > 0)
			EAM.logDebug("Quarantined orphans: " + orphanRefs);
		
		return orphanRefs.size();
	}

	private void addToQuarantine(BaseObject object) throws Exception
	{
		String typeName = object.getObjectManager().getInternalObjectTypeName(object.getType());
		StringBuffer quarantineText = new StringBuffer();
		quarantineText.append("# " + new Date() + ", Version " + Miradi.MAIN_VERSION + HtmlUtilities.BR_TAG);
		quarantineText.append("ORef: " + object.getRef() + " # " + typeName + HtmlUtilities.BR_TAG);
		quarantineText.append(HtmlUtilities.replaceNonHtmlNewlines(object.toJson().toString(2)));
		quarantineText.append(HtmlUtilities.BR_TAG);
		
		getProject().appendToQuarantineFile(quarantineText.toString());
	}

	public void repairProblemsWherePossible() throws Exception
	{
		repairUnsnappedNodes();
		removeInvalidDiagramLinkRefs();
		quarantineGroupBoxFactorLinks();
		fixAnyProblemsWithThreatStressRatings();
		fixAssignmentsReferringToMissingObjects();
		repairKeaModeTargetsReferringToMissingSimpleModeIndicators(TargetSchema.getObjectType());
		repairKeaModeTargetsReferringToMissingSimpleModeIndicators(HumanWelfareTargetSchema.getObjectType());
		repairTargetsReferringToSameSimpleModeIndicator(TargetSchema.getObjectType());
		repairTargetsReferringToSameSimpleModeIndicator(HumanWelfareTargetSchema.getObjectType());
	}
	
	private void repairKeaModeTargetsReferringToMissingSimpleModeIndicators(final int abstractTargetType) throws Exception
	{
		ORefList abstractTargetRefs = getProject().getPool(abstractTargetType).getORefList();
		for(ORef abstractTargetRef : abstractTargetRefs)
		{
			AbstractTarget target = AbstractTarget.findTarget(getProject(), abstractTargetRef);
			if (target.isViabilityModeTNC())
			{
				deleteMissingIndicatorsFromTarget(target);
			}
		}
	}

	private void deleteMissingIndicatorsFromTarget(AbstractTarget target) throws Exception
	{
		final ORefSet allPoolIndicators = getProject().getIndicatorPool().getRefSet();
		ORefList targetIndicatorRefs = target.getSafeRefListData(AbstractTarget.TAG_INDICATOR_IDS);
		ORefList indicatorsToDelete = new ORefList();
		for(ORef indicatorRef : targetIndicatorRefs)
		{
			if (!allPoolIndicators.contains(indicatorRef))
			{
				indicatorsToDelete.add(indicatorRef);
				messages.add(EAM.text("Missing Indicator(s) was removed!"));
			}
		}

		targetIndicatorRefs.removeAll(indicatorsToDelete);
		setIndicatorRefs(target, targetIndicatorRefs);
	}

	private void repairTargetsReferringToSameSimpleModeIndicator(final int abstractTargetType) throws Exception
	{
		final ORefSet allIndicators = getProject().getIndicatorPool().getRefSet();
		for(ORef indicatorRef : allIndicators)
		{
			Indicator indicator = Indicator.find(getProject(), indicatorRef);
			ORefList abstractTargetReferrerRefs = indicator.findObjectsThatReferToUs(abstractTargetType);
			attachDeepCopiesOfIndicator(abstractTargetReferrerRefs, indicator);
		}
	}

	private void attachDeepCopiesOfIndicator(ORefList abstractTargetReferrerRefs, Indicator indicator) throws Exception
	{
		final int IGNORE_FIRST_REFERRER = 1;
		for (int index = IGNORE_FIRST_REFERRER; index < abstractTargetReferrerRefs.size(); ++index)
		{
			AbstractTarget abstractTarget = AbstractTarget.findTarget(getProject(), abstractTargetReferrerRefs.get(index));
			ORefList indicatorRefs = abstractTarget.getSafeRefListData(AbstractTarget.TAG_INDICATOR_IDS);
			indicatorRefs.remove(indicator.getRef());
			indicatorRefs.add(deepCopyIndicator(indicator));
			setIndicatorRefs(abstractTarget, indicatorRefs);
		}
	}

	public ORef deepCopyIndicator(Indicator indicator) throws Exception
	{
		BaseObjectDeepCopier deepCopier = new BaseObjectDeepCopierNotUsingCommands(getProject());
		BaseObject indicatorCopy = deepCopier.createDeepCopier(indicator);
		
		return indicatorCopy.getRef();
	}
	
	private void setIndicatorRefs(AbstractTarget target, ORefList indicatorRefs) throws Exception
	{
		final IdList indicatorIds = indicatorRefs.convertToIdList(IndicatorSchema.getObjectType());
		getProject().setObjectData(target, AbstractTarget.TAG_INDICATOR_IDS, indicatorIds.toString());
	}

	private void fixAssignmentsReferringToMissingObjects() throws Exception
	{
		Vector<String> resourceAssignmentTagsToFix = new Vector<String>();
		resourceAssignmentTagsToFix.add(ResourceAssignment.TAG_ACCOUNTING_CODE_ID);
		resourceAssignmentTagsToFix.add(ResourceAssignment.TAG_FUNDING_SOURCE_ID);
		resourceAssignmentTagsToFix.add(ResourceAssignment.TAG_CATEGORY_ONE_REF);
		resourceAssignmentTagsToFix.add(ResourceAssignment.TAG_CATEGORY_TWO_REF);
		fixResourceAssignment(ResourceAssignmentSchema.getObjectType(), resourceAssignmentTagsToFix);
		
		Vector<String> expenseAssignmentTagsToFix = new Vector<String>();
		expenseAssignmentTagsToFix.add(ExpenseAssignment.TAG_ACCOUNTING_CODE_REF);
		expenseAssignmentTagsToFix.add(ExpenseAssignment.TAG_FUNDING_SOURCE_REF);
		expenseAssignmentTagsToFix.add(ExpenseAssignment.TAG_CATEGORY_ONE_REF);
		expenseAssignmentTagsToFix.add(ExpenseAssignment.TAG_CATEGORY_TWO_REF);
		fixResourceAssignment(ExpenseAssignmentSchema.getObjectType(), expenseAssignmentTagsToFix);
	}

	private void fixResourceAssignment(int assignmentType, Vector<String> assignmentTagsToFix) throws Exception
	{
		ORefSet assignmentRefs = getProject().getPool(assignmentType).getRefSet();
		for(ORef assignmentRef : assignmentRefs)
		{
			Assignment assignment = Assignment.findAssignment(getProject(), assignmentRef);
			clearFieldReferringToMissingObject(assignment, assignmentTagsToFix);
		}
	}

	private void clearFieldReferringToMissingObject(Assignment assignment, Vector<String> assignmentTagsToFix) throws Exception
	{
		for(String tagToFix : assignmentTagsToFix)
		{
			ORef ref = assignment.getRefData(tagToFix);
			if (shouldClearDataField(ref))
				getProject().setObjectData(assignment, tagToFix, "");
		}
	}

	private boolean shouldClearDataField(ORef ref)
	{
		if (ref.isInvalid())
			return true;
		
		return BaseObject.find(getProject(), ref) == null;
	}

	private void quarantineGroupBoxFactorLinks() throws Exception
	{
		Vector<ORef> groupBoxFactorLinkRefs = new Vector<ORef>();
		ORefList allFactorLinkRefs = project.getFactorLinkPool().getFactorLinkRefs();
		for(int i = 0; i < allFactorLinkRefs.size(); ++i)
		{
			ORef factorLinkRef = allFactorLinkRefs.get(i);
			FactorLink link = FactorLink.find(project, factorLinkRef);
			if(GroupBox.is(link.getFromFactorRef()) || GroupBox.is(link.getToFactorRef()))
				groupBoxFactorLinkRefs.add(factorLinkRef);
		}
		quarantineObjects(groupBoxFactorLinkRefs);
	}

	private void removeInvalidDiagramLinkRefs() throws Exception
	{
		removeInvalidDiagramLinkRefs(project.getConceptualModelDiagramPool());
		removeInvalidDiagramLinkRefs(project.getResultsChainDiagramPool());
	}

	private void removeInvalidDiagramLinkRefs(ObjectPool diagramPool) throws Exception
	{
		ORefList diagramRefs = diagramPool.getRefList();
		for(int i = 0; i < diagramRefs.size(); ++i)
		{
			DiagramObject diagram = DiagramObject.findDiagramObject(getProject(), diagramRefs.get(i));
			removeInvalidDiagramLinkRefs(diagram);
		}
	}

	private void removeInvalidDiagramLinkRefs(DiagramObject diagram) throws Exception
	{
		IdList diagramLinkIds = new IdList(diagram.getAllDiagramFactorLinkIds());
		while(diagramLinkIds.contains(BaseId.INVALID))
			diagramLinkIds.removeId(BaseId.INVALID);
		
		if(diagramLinkIds.size() < diagram.getAllDiagramFactorLinkIds().size())
		{
			EAM.logWarning("Deleting -1 diagram link ids from " + diagram.getRef());
			getProject().setObjectData(diagram, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, diagramLinkIds.toString());
		}
	}

	private Vector<ORef> findOrphans()
	{
		Vector<ORef> orphanRefs = new Vector<ORef>();
		Set<Integer> topLevelTypes = ObjectType.getTopLevelObjectTypes();
		for(int objectType = ObjectType.FIRST_OBJECT_TYPE; objectType < ObjectType.OBJECT_TYPE_COUNT; ++objectType)
		{
			if(topLevelTypes.contains(objectType))
				continue;
			
			ObjectPool pool = getProject().getPool(objectType);
			if(pool == null)
				continue;
			
			orphanRefs.addAll(findOrphans(pool.getRefSet()));
		}
		
		Collections.sort(orphanRefs);
		return orphanRefs;
	}

	private ORefSet findOrphans(ORefSet possibleOrphanRefs)
	{
		ORefSet orphanRefs = new ORefSet();
		for(ORef ref : possibleOrphanRefs)
		{
			ORefSet referrers = getProject().getObjectManager().getReferringObjects(ref);
			if(referrers.isEmpty())
				orphanRefs.add(ref);
		}
		
		return orphanRefs;
	}
	
	private void untagOrphans() throws Exception
	{
		ORefList taggedObjectSetRefs = getProject().getPool(TaggedObjectSetSchema.getObjectType()).getORefList();
		for(ORef taggedObjectSetRef : taggedObjectSetRefs)
		{
			TaggedObjectSet taggedObjectSet = TaggedObjectSet.find(getProject(), taggedObjectSetRef);
			ORefList taggedObjectRefs = taggedObjectSet.getTaggedObjectRefs();
			ORefList taggedOrphanRefs = findTaggedOrphans(taggedObjectRefs);
			untagOrphans(taggedOrphanRefs);
		}
	}

	private void untagOrphans(ORefList taggedObjectRefs) throws Exception
	{
		for(ORef refToUntag : taggedObjectRefs)
		{
			untagOrphan(refToUntag);
		}
	}

	private void untagOrphan(ORef refToUntag) throws Exception
	{
		Vector<TaggedObjectSet> taggedObjectSetsWithFactor = getProject().getTaggedObjectSetPool().findTaggedObjectSetsWithFactor(refToUntag);
		for (int index = 0; index < taggedObjectSetsWithFactor.size(); ++index)
		{
			TaggedObjectSet taggedObjectSet = taggedObjectSetsWithFactor.get(index);
			ORefList currentTaggedObjectRefs = taggedObjectSet.getTaggedObjectRefs();
			currentTaggedObjectRefs.remove(refToUntag);
			getProject().setObjectData(taggedObjectSet, TaggedObjectSet.TAG_TAGGED_OBJECT_REFS, currentTaggedObjectRefs.toString());
		}
	}

	private ORefList findTaggedOrphans(ORefList taggedObjectRefs)
	{
		ORefList allTaggedObjectSetRefs = getProject().getPool(TaggedObjectSetSchema.getObjectType()).getORefList();
		ORefList orphandRefsToUntag = new ORefList();
		for(ORef ref : taggedObjectRefs)
		{
			BaseObject baseObject = BaseObject.find(getProject(), ref);
			ORefList allReferrers = baseObject.findObjectsThatReferToUs();
			allReferrers.removeAll(allTaggedObjectSetRefs);
			if (allReferrers.isEmpty())
				orphandRefsToUntag.add(ref);
		}
		
		return orphandRefsToUntag;
	}

	public ORefSet getFactorsWithoutDiagramFactors(int factorType)
	{
		ORefSet possibleOrphanRefs = getProject().getPool(factorType).getRefSet();
		int referringObjectType = DiagramFactorSchema.getObjectType();

		return getActualOrphanRefs(possibleOrphanRefs, new int[] {referringObjectType});
	}

	private ORefSet getActualOrphanRefs(ORefSet possibleOrphanRefs,	int[] referringObjectTypes)
	{
		ORefSet orphanRefs = new ORefSet();
		for(ORef possibleOrphanRef : possibleOrphanRefs)
		{
			BaseObject possibleOrphan = BaseObject.find(getProject(), possibleOrphanRef);
			ORefList referrerRefs = new ORefList(); 
			for(int typeIndex = 0; typeIndex < referringObjectTypes.length; ++typeIndex)
				referrerRefs.addAll(possibleOrphan.findObjectsThatReferToUs(referringObjectTypes[typeIndex]));
			
			if (referrerRefs.isEmpty())
				orphanRefs.addRef(possibleOrphan);
		}
		
		return orphanRefs;
	}

	private void repairUnsnappedNodes()
	{
		DiagramFactor[] diagramFactors = project.getAllDiagramFactors();
		for (int i = 0; i < diagramFactors.length; ++i)
		{
			fixLocation(diagramFactors[i]);
			fixSize(diagramFactors[i]);
		}
	}

	private void fixSize(DiagramFactor diagramFactor)
	{
		Dimension currentSize = diagramFactor.getSize();
		int snappedEvenWidth = project.forceNonZeroEvenSnap(currentSize.width);
		int snappedEvenHeight = project.forceNonZeroEvenSnap(currentSize.height);
		Dimension snappedEvenSize = new Dimension(snappedEvenWidth, snappedEvenHeight);
		
		if (currentSize.equals(snappedEvenSize))
			return;
		
		try
		{
			String newSizeAsString = EnhancedJsonObject.convertFromDimension(snappedEvenSize);
			project.setObjectData(diagramFactor.getType(), diagramFactor.getId(), DiagramFactor.TAG_SIZE, newSizeAsString);
		}
		catch(Exception e)
		{
			logAndContinue(e);
		}
	}

	private void fixLocation(DiagramFactor diagramFactor)
	{
		Point currentLocation = diagramFactor.getLocation();
		Point expectedLocation  = project.getSnapped(currentLocation);
		int deltaX = expectedLocation.x - currentLocation.x;
		int deltaY = expectedLocation.y - currentLocation.y;

		if(deltaX == 0 && deltaY == 0)
			return;
			
		try
		{
			String moveToLocation = EnhancedJsonObject.convertFromPoint(expectedLocation);
			project.setObjectData(diagramFactor.getType(), diagramFactor.getId(), DiagramFactor.TAG_LOCATION, moveToLocation);
		}
		catch(Exception e)
		{
			logAndContinue(e);
		}
	}
	
	private void logAndContinue(Exception e)
	{
		EAM.logException(e);
	}
	
	private void fixAnyProblemsWithThreatStressRatings() throws Exception
	{
		deleteThreatStressRatingsWithInvalidRefs();
		
		ThreatStressRatingEnsurer ensurer = new ThreatStressRatingEnsurer(getProject());
		ensurer.createOrDeleteThreatStressRatingsAsNeeded();
	}

	private void deleteThreatStressRatingsWithInvalidRefs() throws Exception
	{
		ORefSet deletedRefs = new ORefSet();
		ORefList ratingObjectRefs = getProject().getThreatStressRatingPool().getRefList();
		for(int i = 0; i < ratingObjectRefs.size(); ++i)
		{
			ThreatStressRating rating = ThreatStressRating.find(getProject(), ratingObjectRefs.get(i));
			if(rating.getThreatRef().isInvalid() || rating.getStressRef().isInvalid())
			{
				deletedRefs.add(rating.getRef());
				getProject().deleteObject(rating);
			}
		}
		
		if(deletedRefs.size() > 0)
			EAM.logWarning("Deleted " + deletedRefs.size() + " TSR's with invalid refs");
	}

	public HashMap<ORef, ORefSet> getListOfMissingObjects() throws Exception
	{
		HashMap<ORef, ORefSet> missingObjectsAndReferrers = new HashMap<ORef, ORefSet>();

		ORefList missingObjectRefs = findAllMissingObjects();
		for (int i = 0; i < missingObjectRefs.size(); ++i)
		{
			ORef missingRef = missingObjectRefs.get(i);
			ORefSet referrers = project.getObjectManager().getReferringObjects(missingRef);
			referrers = getOnlyReferrersThatMatter(referrers);
			
			if(referrers.size() > 0)
				missingObjectsAndReferrers.put(missingRef, referrers);
		}
		
		return missingObjectsAndReferrers;
	}

	private ORefSet getOnlyReferrersThatMatter(ORefSet referrers)
	{
		ORefSet filteredReferrers = new ORefSet();
		for(ORef ref : referrers)
		{
			if(TableSettings.is(ref))
				continue;
			if(ViewData.is(ref))
				continue;
			if(TaggedObjectSet.is(ref))
				continue;
			
			filteredReferrers.add(ref);
		}
		
		return filteredReferrers;
	}

	public ORefList findAllMissingObjects() throws Exception
	{
		ORefList missingObjectRefs = new ORefList();
		for (int objectType = 0; objectType < ObjectType.OBJECT_TYPE_COUNT; ++objectType)
		{
			PoolWithIdAssigner pool = project.getPool(objectType);
			if (pool == null)
				continue;
			
			missingObjectRefs.addAll(getMissingObjectsReferredToBy(pool.getORefList()));
		}
		
		return missingObjectRefs;
	}
	
	private ORefList getMissingObjectsReferredToBy(ORefList refList) throws Exception
	{
		ORefList missingObjectRefs = new ORefList();
		for (int i = 0; i < refList.size(); ++i)
		{
			BaseObject foundObject = project.findObject(refList.get(i));
			ORefSet referredRefs = foundObject.getAllReferencedObjects();
			missingObjectRefs.addAll(extractMissingObjectRefs(referredRefs));
		}
		
		return missingObjectRefs;
	}

	private ORefList extractMissingObjectRefs(ORefSet ownedAndReferredRefs)
	{
		ORefList missingObjectRefs = new ORefList();
		for(ORef ref : ownedAndReferredRefs)
		{
			if (ref.isInvalid())
				continue;
			
			if(ref.getObjectType() == ObjectType.FAKE)
			{
				EAM.logDebug("WARNING: Ref with fake type but non-invalid id: " + ref.getObjectId());
				continue;
			}
			
			if (ref.getObjectType() == ObjectType.FACTOR)
			{
				EAM.logDebug("WARNING: Ref with factor type with id:" + ref.getObjectId());
				continue;
			}
			
			BaseObject foundObject = project.findObject(ref);
			if (foundObject != null)
				continue;
			
			missingObjectRefs.add(ref);
		}
		
		return missingObjectRefs;
	}
	
	public TreeSet<String> getMessages()
	{
		return messages;
	}
	
	private Project getProject()
	{
		return project;
	}

	private Project project;
	private TreeSet<String> messages;
}
