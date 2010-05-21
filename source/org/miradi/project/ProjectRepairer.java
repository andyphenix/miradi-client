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
import java.util.LinkedHashSet;

import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.ThreatStressRatingEnsurer;
import org.miradi.objectpools.PoolWithIdAssigner;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.Factor;
import org.miradi.objects.FactorLink;
import org.miradi.objects.GroupBox;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.IntermediateResult;
import org.miradi.objects.ScopeBox;
import org.miradi.objects.Strategy;
import org.miradi.objects.TableSettings;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.TextBox;
import org.miradi.objects.ThreatReductionResult;
import org.miradi.utils.EnhancedJsonObject;

public class ProjectRepairer
{
	public static void scanForCorruptedObjects(Project project) throws Exception
	{
		ProjectRepairer repairer = new ProjectRepairer(project);
		repairer.possiblyShowMissingObjectsWarningDialog();
	}
	
	public static void repairAnyProblems(Project project) throws Exception
	{
		ProjectRepairer repairer = new ProjectRepairer(project);
		repairer.repair();
	}
	
	public ProjectRepairer(Project projectToRepair)
	{
		project = projectToRepair;
	}
	
	void repair() throws Exception
	{
		fixAnyProblemsWithThreatStressRatings();
		repairUnsnappedNodes();
		logOrphansAndSimilarProblems();
	}

	public void logOrphansAndSimilarProblems()
	{
		warnOfOrphanAnnotations();	
		warnOfOrphanTasks();
		warnOfFactorsWithoutReferringDiagramFactors();
	}
	 
	private void warnOfFactorsWithoutReferringDiagramFactors()
	{
		ORefSet factorsWithoutDiagramFactors = getFactorsWithoutDiagramFactors();
		if (factorsWithoutDiagramFactors.hasData())
			EAM.logError("Factors are not covered by a diagramFactor:" + factorsWithoutDiagramFactors.toRefList());
	}

	private ORefSet getFactorsWithoutDiagramFactors()
	{
		ORefSet factorsWithoutDiagramFactors = new ORefSet();
		factorsWithoutDiagramFactors.addAll(getFactorsWithoutDiagramFactors(ScopeBox.getObjectType()));
		factorsWithoutDiagramFactors.addAll(getFactorsWithoutDiagramFactors(Target.getObjectType()));
		factorsWithoutDiagramFactors.addAll(getFactorsWithoutDiagramFactors(HumanWelfareTarget.getObjectType()));
		factorsWithoutDiagramFactors.addAll(getFactorsWithoutDiagramFactors(Cause.getObjectType()));
		factorsWithoutDiagramFactors.addAll(getFactorsWithoutDiagramFactors(Strategy.getObjectType()));
		factorsWithoutDiagramFactors.addAll(getFactorsWithoutDiagramFactors(ThreatReductionResult.getObjectType()));
		factorsWithoutDiagramFactors.addAll(getFactorsWithoutDiagramFactors(IntermediateResult.getObjectType()));
		factorsWithoutDiagramFactors.addAll(getFactorsWithoutDiagramFactors(GroupBox.getObjectType()));
		factorsWithoutDiagramFactors.addAll(getFactorsWithoutDiagramFactors(TextBox.getObjectType()));
		
		return factorsWithoutDiagramFactors;
	}

	public ORefSet getFactorsWithoutDiagramFactors(int factorType)
	{
		ORefSet possibleOrphanRefs = getProject().getPool(factorType).getRefSet();
		int referringObjectType = DiagramFactor.getObjectType();

		ORefSet factorsWithoutDiagramFactors = new ORefSet();
		for(ORef possibleOrphanRef : possibleOrphanRefs)
		{
			Factor possibleOrphan = Factor.findFactor(getProject(), possibleOrphanRef);
			ORefList referrerRefs = possibleOrphan.findObjectsThatReferToUs(referringObjectType);
			if (referrerRefs.isEmpty())
				factorsWithoutDiagramFactors.addRef(possibleOrphan);
		}
		
		return factorsWithoutDiagramFactors;
	}

	private void warnOfOrphanTasks()
	{
		int type = Task.getObjectType();
		ORefList refs = getProject().getPool(type).getRefList();
		for(int i = 0; i < refs.size(); ++i)
		{
			BaseObject object = BaseObject.find(getProject(), refs.get(i));
			if(object.getOwnerRef().isInvalid())
				EAM.logWarning("Object without owner! " + object.getRef());
		}
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
	
	private void warnOfOrphanAnnotations()
	{
		warnOfOrphanAnnotations(ObjectType.OBJECTIVE);
		warnOfOrphanAnnotations(ObjectType.GOAL);
		warnOfOrphanAnnotations(ObjectType.INDICATOR);
		warnOfOrphanAnnotations(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE);
	}

	private void warnOfOrphanAnnotations(int annotationType)
	{
		IdList allIds = project.getPool(annotationType).getIdList();
		for(int i = 0; i < allIds.size(); ++i)
		{
			BaseId annotationId = allIds.get(i);
			try
			{
				BaseObject object = project.getObjectManager().findObject(annotationType, annotationId);
				BaseObject owner = object.getOwner();
				if(owner == null)
				{
					EAM.logWarning("Found orphan " + annotationType + ":" + annotationId);
				}
			}
			catch(Exception e)
			{
				logAndContinue(e);
			}
		}
	}

	private void logAndContinue(Exception e)
	{
		EAM.logException(e);
	}
	
	private void fixAnyProblemsWithThreatStressRatings() throws Exception
	{
		ThreatStressRatingEnsurer ensurer = new ThreatStressRatingEnsurer(getProject());
		ensurer.createOrDeleteThreatStressRatingsAsNeeded();
	}

	private void possiblyShowMissingObjectsWarningDialog() throws Exception
	{
		ORefList missingObjectRefs = findAllMissingObjects();
		if (missingObjectRefs.size() == 0 )
			return;
		
		LinkedHashSet<String> orderedErrorMesseges = new LinkedHashSet<String>();
		for (int i = 0; i < missingObjectRefs.size(); ++i)
		{
			ORef missingRef = missingObjectRefs.get(i);
			ORefSet referrers = project.getObjectManager().getReferringObjects(missingRef);
			if (hasOnlyTableSettingReferrers(referrers))
				continue;
			
			String errorMessage = "Missing object: " + missingRef + " referred to by: " + referrers;
			orderedErrorMesseges.add(errorMessage);
		}
		
		for(String errorMessage : orderedErrorMesseges)
		{
			EAM.logError(errorMessage);
		}
		
		detectAndReportOrphans(FactorLink.getObjectType(), DiagramLink.getObjectType());
		detectAndReportOrphans(Cause.getObjectType(), DiagramFactor.getObjectType());

// NOTE: This is appropriate for testing, but not for production
//		EAM.notifyDialog("<html>This project has some data corruption, " +
//						 "which may cause error messages or unexpected results within Miradi. <br>" +
//						 "Please contact the Miradi team to report this problem, " +
//						 "and/or to have them repair this project.");
	}

	private boolean hasOnlyTableSettingReferrers(ORefSet referrers)
	{
		for(ORef ref : referrers)
		{
			if (!TableSettings.is(ref))
				return false;
		}
		
		return true;
	}

	private void detectAndReportOrphans(int possibleOrphanType,	final int custodianType)
	{
		ORefList possibleOrphanRefs = getProject().getObjectManager().getPool(possibleOrphanType).getORefList();
		for(int i = 0; i < possibleOrphanRefs.size(); ++i)
		{
			final ORef possibleOrphanRef = possibleOrphanRefs.get(i);
			BaseObject possibleOrphan = BaseObject.find(getProject(), possibleOrphanRef);
			ORefList custodianRefs = possibleOrphan.findObjectsThatReferToUs(custodianType);
			if(custodianRefs.size() == 0)
				EAM.logError(possibleOrphan.getTypeName() + " without custodian: " + possibleOrphanRef);
		}
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
				EAM.logDebug("Ref with fake type but non-invalid id: " + ref.getObjectId());
				continue;
			}
			
			if (ref.getObjectType() == ObjectType.FACTOR)
			{
				EAM.logDebug("Ref with factor type with id:" + ref.getObjectId());
				continue;
			}
			
			BaseObject foundObject = project.findObject(ref);
			if (foundObject != null)
				continue;
			
			missingObjectRefs.add(ref);
		}
		
		return missingObjectRefs;
	}
	
	private Project getProject()
	{
		return project;
	}

	private Project project;
}
