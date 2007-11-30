/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.DiagramLink;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.objects.ThreatStressRating;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.project.Project;

public class LinkDeletor
{
	public LinkDeletor(Project projectToUse)
	{
		project = projectToUse;
	}

	public void deleteFactorLinkAndAllRefferers(FactorLinkId factorLinkId) throws Exception
	{
		FactorLink factorLink = (FactorLink) project.findObject(new ORef(ObjectType.FACTOR_LINK, factorLinkId));
		deleteAllReferrerDiagramLinks(factorLink);
		deleteFactorLinkIfOrphaned(factorLink);
	}
	
	public void deleteFactorLinkAndDiagramLink(ORefList factorsAboutToBeDeleted, DiagramLink diagramLink) throws Exception
	{
		deleteDiagramLink(diagramLink);
		FactorLink factorLink = diagramLink.getUnderlyingLink();
		if (!isToOrFromFactorBeingDeleted(factorsAboutToBeDeleted, factorLink))
			deleteAllReferrerDiagramLinks(factorLink);

		deleteFactorLinkIfOrphaned(factorLink);
	}

	private void deleteAllReferrerDiagramLinks(FactorLink factorLink) throws Exception
	{
		ObjectManager objectManager = project.getObjectManager();
		ORefList diagramLinkreferrers = factorLink.findObjectsThatReferToUs(objectManager, ObjectType.DIAGRAM_LINK, factorLink.getRef());
		deleteDiagramLinks(diagramLinkreferrers);
	}

	private void deleteDiagramLinks(ORefList diagramLinkORefs) throws Exception
	{
		for (int i = 0; i < diagramLinkORefs.size(); ++i)
		{
			DiagramLink diagramLink = (DiagramLink) project.findObject(diagramLinkORefs.get(i));
			deleteDiagramLink(diagramLink);
		}
	}
	
	private void deleteDiagramLink(DiagramLink diagramLink) throws Exception
	{
		BaseObject owner = diagramLink.getOwner();
		DiagramObject diagramObject = (DiagramObject) owner;
		CommandSetObjectData removeDiagramFactorLink = CommandSetObjectData.createRemoveIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, diagramLink.getDiagramLinkageId());
		project.executeCommand(removeDiagramFactorLink);

		Command[] commandsToClearDiagramLink = diagramLink.createCommandsToClear();
		project.executeCommandsWithoutTransaction(commandsToClearDiagramLink);

		CommandDeleteObject removeFactorLinkCommand = new CommandDeleteObject(ObjectType.DIAGRAM_LINK, diagramLink.getDiagramLinkageId());
		project.executeCommand(removeFactorLinkCommand);
	}
	
	private void deleteFactorLinkIfOrphaned(FactorLink factorLink) throws CommandFailedException
	{
		ObjectManager objectManager = project.getObjectManager();
		ORefList diagramLinkReferrers = factorLink.findObjectsThatReferToUs(objectManager, DiagramLink.getObjectType(), factorLink.getRef());
		
		if (diagramLinkReferrers.size() != 0)
			return;
		
		ORefList threatStressRatingRefs = factorLink.getThreatStressRatingRefs();
		
		Command[] commandsToClear = project.findObject(ObjectType.FACTOR_LINK, factorLink.getId()).createCommandsToClear();
		project.executeCommandsWithoutTransaction(commandsToClear);
		
		CommandDeleteObject deleteLinkage = new CommandDeleteObject(ObjectType.FACTOR_LINK, factorLink.getId());
		project.executeCommand(deleteLinkage);
		
		deleteOrphanedThreatStressRatings(threatStressRatingRefs);
	}
	
	private void deleteOrphanedThreatStressRatings(ORefList threatStressRatingRefs) throws CommandFailedException
	{
		for (int i = 0; i < threatStressRatingRefs.size(); ++i)
		{
			ORef threatStressRatingRef = threatStressRatingRefs.get(i);
			ThreatStressRating threatStressRating = (ThreatStressRating) project.findObject(threatStressRatingRef);
			ORefList allReferrers = threatStressRating.findObjectsThatReferToUs();
			if (allReferrers.size() != 0)
				continue;
			
			deleteThreatStressRating(threatStressRating);
		}
	}

	public void deleteThreatStressRating(ThreatStressRating threatStressRating) throws CommandFailedException
	{
		Command[] commandsToClear = threatStressRating.createCommandsToClear();
		project.executeCommandsWithoutTransaction(commandsToClear);
		
		CommandDeleteObject deleteThreatStressRating = new CommandDeleteObject(threatStressRating.getRef());
		project.executeCommand(deleteThreatStressRating);
	}

	private boolean isToOrFromFactorBeingDeleted(ORefList factorsAboutToBeDeleted, FactorLink factorLink)
	{
		for (int i = 0; i < factorsAboutToBeDeleted.size(); ++i)
		{
			ORef factorRefToBeDeleted = factorsAboutToBeDeleted.get(i);
			ORef toRef = factorLink.getToFactorRef();
			ORef fromRef = factorLink.getFromFactorRef();
			if (toRef.equals(factorRefToBeDeleted) || fromRef.equals(factorRefToBeDeleted))
				return true;
		}
		
		return false;
	}

	private Project project;
}
