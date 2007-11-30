/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.text.ParseException;

import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.DiagramFactorLinkId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objectdata.BooleanData;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateThreatStressRatingParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.objects.ThreatStressRating;
import org.conservationmeasures.eam.project.Project;

public class LinkCreator
{
	public LinkCreator(Project projectToUse)
	{
		project = projectToUse;
	}
	
	public boolean linkWasRejected(DiagramModel model, ORef fromFactorRef, ORef toFactorRef) throws Exception
	{
		if (fromFactorRef.equals(toFactorRef))
			return true;
		
		if (! model.doesFactorExist(fromFactorRef) || ! model.doesFactorExist(toFactorRef))
			return true;

		if (model.areLinked(fromFactorRef, toFactorRef))
			return true;
		
		return false;
		
	}
	
	public boolean linkWasRejected(DiagramModel model, DiagramFactorId fromDiagramFactorId, DiagramFactorId toDiagramFactorId) throws Exception
	{
		DiagramFactor fromDiagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, fromDiagramFactorId));
		DiagramFactor toDiagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, toDiagramFactorId));
		
		return linkWasRejected(model, fromDiagramFactor, toDiagramFactor);
	}
	
	public boolean linkWasRejected(DiagramModel model, DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor) throws Exception
	{
		if (fromDiagramFactor == null || toDiagramFactor == null)
			return true;
		
		if(fromDiagramFactor.getDiagramFactorId().equals(toDiagramFactor.getDiagramFactorId()))
		{
			String[] body = {EAM.text("Can't link an item to itself"), };
			EAM.okDialog(EAM.text("Can't Create Link"), body);
			return true;
		}
		
		if(fromDiagramFactor.getDiagramFactorId().isInvalid() || toDiagramFactor.getDiagramFactorId().isInvalid())
		{
			EAM.logWarning("Unable to Paste Link : from " + fromDiagramFactor.getDiagramFactorId() + " to OriginalId:" + toDiagramFactor.getDiagramFactorId()+" node deleted?");	
			return true;
		}

		if (! model.containsDiagramFactor(fromDiagramFactor.getDiagramFactorId()) || ! model.containsDiagramFactor(toDiagramFactor.getDiagramFactorId()))
			return true;

		if (model.areDiagramFactorsLinked(fromDiagramFactor.getDiagramFactorId(), toDiagramFactor.getDiagramFactorId()))
			return true;
		
		return false;
	}

	public void createFactorLinkAndAddToDiagramUsingCommands(DiagramObject diagramObject, FactorId fromThreatId , FactorId toTargetId ) throws Exception
	{
		DiagramFactor fromDiagramFactor = diagramObject.getDiagramFactor(fromThreatId);
		DiagramFactor toDiagramFactor = diagramObject.getDiagramFactor(toTargetId);

		createFactorLinkAndAddToDiagramUsingCommands(diagramObject, fromDiagramFactor, toDiagramFactor);
	}
	
	public FactorLinkId createFactorLinkAndAddToDiagramUsingCommands(DiagramModel model, DiagramFactor diagramFactorFrom, DiagramFactor diagramFactorTo) throws Exception
	{
		DiagramObject diagramObject = model.getDiagramObject();
		return createFactorLinkAndAddToDiagramUsingCommands(diagramObject, diagramFactorFrom, diagramFactorTo);
	}
	
	private FactorLinkId createFactorLinkAndAddToDiagramUsingCommands(DiagramObject diagramObject, DiagramFactor diagramFactorFrom, DiagramFactor diagramFactorTo) throws Exception
	{
		FactorId fromFactorId = diagramFactorFrom.getWrappedId();
		FactorId toFactorId = diagramFactorTo.getWrappedId();
		FactorLinkId factorLinkId = project.getFactorLinkPool().getLinkedId(fromFactorId, toFactorId);
		
		if(factorLinkId != null)
			ensureLinkGoesOurWay(factorLinkId, fromFactorId, factorLinkId);
		else
			factorLinkId = createFactorLink(diagramFactorFrom, diagramFactorTo);
		
		createDiagramLinks(factorLinkId);
		return factorLinkId; 
	}

	private void ensureLinkGoesOurWay(FactorLinkId factorLinkId, FactorId fromFactorId, FactorLinkId factorlLinkId) throws CommandFailedException
	{
		FactorLink link = (FactorLink)project.findObject(FactorLink.getObjectType(), factorlLinkId);
		if (link.isBidirectional())
			return;
		
		if(link.getFromFactorRef().getObjectId().equals(fromFactorId))
			return;
		
		CommandSetObjectData command = new CommandSetObjectData(link.getRef(), FactorLink.TAG_BIDIRECTIONAL_LINK, BooleanData.BOOLEAN_TRUE);
		project.executeCommand(command);
	}

	public FactorLinkId createFactorLink(DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor) throws Exception
	{
		ORef factorLinkRef = createFactorLinkWithPossibleThreatStressRatings(fromDiagramFactor.getWrappedORef(), toDiagramFactor.getWrappedORef());
		return (FactorLinkId) factorLinkRef.getObjectId();
	}

	public ORef createFactorLinkWithPossibleThreatStressRatings(ORef fromFactorRef, ORef toFactorRef) throws Exception
	{
		ORef factorLinkRef = createFactorLink(fromFactorRef, toFactorRef);
		FactorLink factorLink = (FactorLink) project.findObject(factorLinkRef);
		if (factorLink.isThreatTargetLink())
			createAndAddThreatStressRatingsFromTarget(factorLinkRef, factorLink.getDownstreamTargetRef());
		
		return factorLinkRef;
	}
	
	public ORef createFactorLinkWithoutThreatStressRatings(ORef fromRef, ORef toRef) throws Exception
	{
		return createFactorLink(fromRef, toRef);
	}
	
	private ORef createFactorLink(ORef fromFactorRef, ORef toFactorRef) throws CommandFailedException
	{
		CreateFactorLinkParameter extraInfo = new CreateFactorLinkParameter(fromFactorRef, toFactorRef);
		CommandCreateObject createFactorLink = new CommandCreateObject(ObjectType.FACTOR_LINK, extraInfo);
		project.executeCommand(createFactorLink);
		
		return createFactorLink.getObjectRef();
	}
	
	public void createAndAddThreatStressRatingsFromTarget(ORef FactorLinkRef, ORef targetRef) throws Exception
	{
		ORefList threatStressRatingRefs = new ORefList();
		Target target = (Target) project.findObject(targetRef);
		ORefList stressRefs = target.getStressRefs();
		for (int i = 0; i < stressRefs.size(); ++i)
		{			
			CreateThreatStressRatingParameter extraInfo = new CreateThreatStressRatingParameter(stressRefs.get(i));
			CommandCreateObject createThreatStressRating = new CommandCreateObject(ThreatStressRating.getObjectType(), extraInfo);
			project.executeCommand(createThreatStressRating);
			
			threatStressRatingRefs.add(createThreatStressRating.getObjectRef());
		}
		
		CommandSetObjectData setThreatStressRatingRefs = new CommandSetObjectData(FactorLinkRef, FactorLink.TAG_THREAT_STRESS_RATING_REFS, threatStressRatingRefs.toString());
		project.executeCommand(setThreatStressRatingRefs);
	}

	public void createDiagramLinks(FactorLinkId factorLinkId) throws Exception
	{
		FactorLink factorLink = (FactorLink) project.findObject(new ORef(ObjectType.FACTOR_LINK, factorLinkId));
		Factor toFactor = getFactor(factorLink.getToFactorRef());
		Factor fromFactor = getFactor(factorLink.getFromFactorRef());
		
		ORefList toDiagramFactors = toFactor.findObjectsThatReferToUs(ObjectType.DIAGRAM_FACTOR);  
		ORefList fromDiagramFactors = fromFactor.findObjectsThatReferToUs(ObjectType.DIAGRAM_FACTOR);
		
		ORefList allDiagramObjects = project.getAllDiagramObjectRefs();
		for (int i = 0; i < allDiagramObjects.size(); ++i)
		{
			ORef diagramObjectORef = allDiagramObjects.get(i);
			DiagramObject diagramObject = (DiagramObject) project.findObject(diagramObjectORef);
			ORef toORef = findDiagramFactor(diagramObject, toDiagramFactors); 
			if (toORef == null)
				continue;
			
			ORef fromORef = findDiagramFactor(diagramObject, fromDiagramFactors);
			if (fromORef == null)
				continue;
			
			createDiagramLink(diagramObject, factorLinkId, (DiagramFactorId)fromORef.getObjectId(), (DiagramFactorId)toORef.getObjectId());
		}
	}

	//TODO nima check to see if this method occurs else where
	private ORef findDiagramFactor(DiagramObject diagramObject, ORefList diagramFactors)
	{
		for (int i = 0 ; i < diagramFactors.size(); ++i)
		{
			ORef diagramFactorORef = diagramFactors.get(i);
			if (diagramObject.containsDiagramFactor((DiagramFactorId) diagramFactorORef.getObjectId()))
				return diagramFactorORef;
		}
		
		return null;
	}

	private Factor getFactor(ORef factorRef)
	{
		return (Factor) project.findObject(factorRef);
	}
	
	private void createDiagramLink(DiagramObject diagramObject, FactorLinkId factorlLinkId, DiagramFactorId fromDiagramFactorId, DiagramFactorId toDiagramFactorId) throws CommandFailedException, ParseException
	{
		CreateDiagramFactorLinkParameter diagramLinkExtraInfo = createDiagramFactorLinkParameter(fromDiagramFactorId, toDiagramFactorId, factorlLinkId);
		CommandCreateObject createDiagramLinkCommand =  new CommandCreateObject(ObjectType.DIAGRAM_LINK, diagramLinkExtraInfo);
		project.executeCommand(createDiagramLinkCommand);
    	
    	BaseId rawId = createDiagramLinkCommand.getCreatedId();
		DiagramFactorLinkId createdDiagramLinkId = new DiagramFactorLinkId(rawId.asInt());
		
		CommandSetObjectData addDiagramLink = CommandSetObjectData.createAppendIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, createdDiagramLinkId);
		project.executeCommand(addDiagramLink);
	}

	private CreateDiagramFactorLinkParameter createDiagramFactorLinkParameter(DiagramFactorId fromId, DiagramFactorId toId, FactorLinkId factorlLinkId)
	{
		CreateDiagramFactorLinkParameter diagramLinkExtraInfo = new CreateDiagramFactorLinkParameter(factorlLinkId, fromId, toId);
		
		return diagramLinkExtraInfo;
	}
	
	private Project project;
}
