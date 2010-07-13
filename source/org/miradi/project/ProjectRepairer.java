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
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.ThreatStressRatingEnsurer;
import org.miradi.objectpools.ObjectPool;
import org.miradi.objectpools.PoolWithIdAssigner;
import org.miradi.objects.BaseObject;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.TableSettings;
import org.miradi.objects.ThreatStressRating;
import org.miradi.objects.ViewData;
import org.miradi.utils.EnhancedJsonObject;

public class ProjectRepairer
{
	public static void repairProblemsWherePossible(Project project) throws Exception
	{
		ProjectRepairer repairer = new ProjectRepairer(project);
		repairer.repairProblemsWherePossible();
	}
	
	public ProjectRepairer(Project projectToRepair)
	{
		project = projectToRepair;
	}
	
	public void repairProblemsWherePossible() throws Exception
	{
		fixAnyProblemsWithThreatStressRatings();
		repairUnsnappedNodes();
		removeInvalidDiagramLinkRefs();
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

	public Vector<ORef> findOrphans()
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

	public ORefSet getFactorsWithoutDiagramFactors(int factorType)
	{
		ORefSet possibleOrphanRefs = getProject().getPool(factorType).getRefSet();
		int referringObjectType = DiagramFactor.getObjectType();

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
			
			filteredReferrers.add(ref);
		}
		
		return filteredReferrers;
	}

	public ORefList findAllMissingObjects() throws Exception
	{
		ORefList missingObjectRefs = new ORefList();
		for (int objectType = 0; objectType < ObjectType.OBJECT_TYPE_COUNT; ++objectType)
		{
			PoolWithIdAssigner pool = (PoolWithIdAssigner) project.getPool(objectType);
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
	
	public Vector<ORef> deleteEmptyOrphans(Vector<ORef> orphanRefs) throws Exception
	{
		Vector<ORef> deletedRefs = new Vector<ORef>();
		for(ORef orphanRef : orphanRefs)
		{
			BaseObject object = BaseObject.find(getProject(), orphanRef);
			if(object.isEmpty())
			{
				getProject().deleteObject(object);
				deletedRefs.add(orphanRef);
			}
		}
		
		return deletedRefs;
	}

	private Project getProject()
	{
		return project;
	}

	private Project project;
}
