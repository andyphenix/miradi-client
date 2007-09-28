/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramContentsId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.objectdata.IdListData;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

abstract public class DiagramObject extends BaseObject
{
	public DiagramObject(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager,idToUse);
	}
	
	public DiagramObject(BaseId idToUse)
	{
		super(idToUse);
	}
	
	public DiagramObject(ObjectManager objectManager, int idToUse, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new DiagramContentsId(idToUse), json);
	}
	
	
	public DiagramObject(int idToUse, EnhancedJsonObject json) throws Exception
	{
		super(new DiagramContentsId(idToUse), json);
	}
	
	public DiagramLink getDiagramFactorLink(FactorLinkId factorLinkId)
	{
		IdList diagramFactorLinkIds = getAllDiagramFactorLinkIds();
		for (int i = 0; i < diagramFactorLinkIds.size(); i++)
		{
			DiagramLink diagramFactorLink = (DiagramLink) getObjectManager().findObject(new ORef(ObjectType.DIAGRAM_LINK, diagramFactorLinkIds.get(i)));
			if (diagramFactorLink.getWrappedId().equals(factorLinkId))
				return diagramFactorLink;
		}
		
		return null;
	}
	
	public DiagramFactor getDiagramFactor(FactorId factorId)
	{
		IdList diagramFactorIds = getAllDiagramFactorIds();
		for (int i = 0; i < diagramFactorIds.size(); i++)
		{
			DiagramFactor diagramFactor = (DiagramFactor) getObjectManager().findObject(new ORef(ObjectType.DIAGRAM_FACTOR, diagramFactorIds.get(i)));
			if (diagramFactor.getWrappedId().equals(factorId))
				return diagramFactor;
		}
		
		return null;
	}
	
	public DiagramFactor getDiagramFactor(ORef factorRef)
	{
		ORefList diagramFactorRefs = getAllDiagramFactorRefs();
		for (int i = 0; i < diagramFactorRefs.size(); ++i)
		{
			ORef diagramFactorRef = diagramFactorRefs.get(i);
			DiagramFactor diagramFactor = (DiagramFactor) getObjectManager().findObject(diagramFactorRef);
			if (diagramFactor.getWrappedORef().equals(factorRef))
				return diagramFactor;
		}
		
		return null;
	}
	
	// TODO: This really should have a test
	public ORefList getAllGoalRefs()
	{
		ORefList allGoalIds = objectManager.getGoalPool().getORefList();
		return getAnnotationInThisDiagram(allGoalIds);
	}

	// TODO: This really should have a test
	public ORefList getAllObjectiveRefs()
	{
		ORefList allObjectiveIds = objectManager.getObjectivePool().getORefList();
		return getAnnotationInThisDiagram(allObjectiveIds);
	}

	// TODO: This really should have a test
	private ORefList getAnnotationInThisDiagram(ORefList allAnnotationIds)
	{
		ORefList ourAnnotations = new ORefList();
		for (int i = 0; i < allAnnotationIds.size(); ++i)
		{
			ORef goalRef = allAnnotationIds.get(i);
			if(isAnnotationInThisDiagram(goalRef))
				ourAnnotations.add(goalRef);
		}
		return ourAnnotations;
	}

	// TODO: This really should have a test
	private boolean isAnnotationInThisDiagram(ORef annotationRef)
	{
		ORefList diagramFactorRefs = getAllDiagramFactorRefs();
		for(int dfr = 0; dfr < diagramFactorRefs.size(); ++dfr)
		{
			DiagramFactor diagramFactor = (DiagramFactor) objectManager.findObject(diagramFactorRefs.get(dfr));
			ORef factorRef = diagramFactor.getWrappedORef();
			Factor factor = objectManager.findFactor(factorRef);
			if(factor.getAllOwnedObjects().contains(annotationRef))
				return true;
		}
		
		return false;
	}
	
	public boolean containsWrappedFactorRef(ORef factorRef)
	{
		if (! Factor.isFactor(factorRef))
			return false;
		
		if (getDiagramFactor(factorRef) == null)
			return false;
		
		return true;
	}
	
	public boolean containsWrappedFactor(FactorId factorId)
	{
		if (getDiagramFactor(factorId) != null)
			return true;
		
		return false;
	}
	
	public boolean isResultsChain()
	{
		return (getType() == ObjectType.RESULTS_CHAIN_DIAGRAM);
	}
	
	//TODO the majority of this method was copied form DiagramModel.  this also has a test, so everyone should start using this method.
	public boolean areDiagramFactorsLinked(DiagramFactorId fromDiagramFactorId, DiagramFactorId toDiagramFactorId) throws Exception
	{
		ORefList diagramLinkRefs = getAllDiagramLinkRefs();
		for (int i  = 0; i < diagramLinkRefs.size(); ++i)
		{
			DiagramLink diagramLink = (DiagramLink) getObjectManager().findObject(diagramLinkRefs.get(i));
			if (diagramLink.getFromDiagramFactorId().equals(fromDiagramFactorId) && diagramLink.getToDiagramFactorId().equals(toDiagramFactorId))
				return true;
			
			if (diagramLink.getFromDiagramFactorId().equals(toDiagramFactorId) && diagramLink.getToDiagramFactorId().equals(fromDiagramFactorId))
				return true;
		}
		
		return false;
	}
	
	public IdList getAllDiagramFactorIds()
	{
		return allDiagramFactorIds.getIdList();
	}
	
	public ORefList getAllDiagramFactorRefs()
	{
		return new ORefList(DiagramFactor.getObjectType(), getAllDiagramFactorIds());
	}
	
	public ORefList getAllDiagramLinkRefs()
	{
		return new ORefList(DiagramLink.getObjectType(), getAllDiagramFactorLinkIds());
	}
	
	public IdList getAllDiagramFactorLinkIds()
	{
		return allDiagramFactorLinkIds.getIdList();
	}
	
	//TODO nima write test for this method
	public boolean containsDiagramFactor(DiagramFactorId diagramFactorId)
	{
		return allDiagramFactorIds.getIdList().contains(diagramFactorId);
	}
	
	public static boolean canOwnThisType(int type)
	{
		switch(type)
		{
			case ObjectType.DIAGRAM_FACTOR:
			case ObjectType.DIAGRAM_LINK:
				return true;
		}
		
		return false;
	}
	
	
	public static boolean canReferToThisType(int type)
	{
		return false;
	}
	

	public ORefList getOwnedObjects(int objectType)
	{
		ORefList list = super.getOwnedObjects(objectType);
		switch(objectType)
		{
			case ObjectType.DIAGRAM_FACTOR: 
				list.addAll(new ORefList(DiagramFactor.getObjectType(), getAllDiagramFactorIds()));
				break;
			case ObjectType.DIAGRAM_LINK: 
				list.addAll(new ORefList(DiagramLink.getObjectType(), getAllDiagramFactorLinkIds()));
				break;
		}
		return list;
	}
	
	//TODO not sure this the right place for this method
	public static ORefList getDiagramRefsContainingThisFactor(Project projectToUse, ORef ref)
	{
		ORefList diagramRefs = new ORefList();
		BaseObject foundObject = projectToUse.findObject(ref);
		ORefList referrerRefs = getObjectsThatRefferTo(foundObject);
		for(int i = 0; i < referrerRefs.size(); ++i)
		{
			BaseObject object = projectToUse.findObject(referrerRefs.get(i));
			diagramRefs.add(object.getOwnerRef());
		}
		
		return diagramRefs;
	}
	
	private static ORefList getObjectsThatRefferTo(BaseObject foundObject)
	{
		if (Factor.isFactor(foundObject.getRef()))
			return foundObject.findObjectsThatReferToUs(DiagramFactor.getObjectType());
		
		return foundObject.findObjectsThatReferToUs(DiagramLink.getObjectType());
	}

	public void clear()
	{
		super.clear();
		
		allDiagramFactorIds = new IdListData();
		allDiagramFactorLinkIds = new IdListData();
		
		addField(TAG_DIAGRAM_FACTOR_IDS, allDiagramFactorIds);
		addField(TAG_DIAGRAM_FACTOR_LINK_IDS, allDiagramFactorLinkIds);
	}
	
	public String toString()
	{
		return getLabel();
	}
	
	public static final String TAG_DIAGRAM_FACTOR_IDS = "DiagramFactorIds";
	public static final String TAG_DIAGRAM_FACTOR_LINK_IDS = "DiagramFactorLinkIds";
	
	IdListData allDiagramFactorIds;
	IdListData allDiagramFactorLinkIds;
}
