/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.views.diagram;

import java.util.HashSet;
import java.util.Vector;

import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.DiagramModel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramLinkId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.objects.FactorLink;
import org.miradi.project.Project;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.GroupBoxSchema;
import org.miradi.schemas.HumanWelfareTargetSchema;
import org.miradi.schemas.IntermediateResultSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.ThreatReductionResultSchema;


//FIXME low: Examine all the methods and try to make it more uniform, simpler, etc....
//seems like a ton of very similar methods, among other things
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
	
	private boolean isGroupThatContains(ORef potentialGroupBoxDiagramFactorRef, ORef potentialChildDiagramFactorRef)
	{
		DiagramFactor from = DiagramFactor.find(project, potentialGroupBoxDiagramFactorRef);
		if(!from.isGroupBoxFactor())
			return false;
		
		return(from.getGroupBoxChildrenRefs().contains(potentialChildDiagramFactorRef));
	}

	public boolean linkToBePastedWasRejected(DiagramModel model, ORef fromDiagramFactorRef, ORef toDiagramFactorRef) throws Exception
	{
		DiagramFactor fromDiagramFactor = (DiagramFactor) project.findObject(fromDiagramFactorRef);
		DiagramFactor toDiagramFactor = (DiagramFactor) project.findObject(toDiagramFactorRef);
		
		return linkWasRejected(model, fromDiagramFactor, toDiagramFactor);
	}
	
	public boolean linkToBeCreatedWasRejected(DiagramModel model, DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor) throws Exception
	{
		boolean linkWasRejected = linkWasRejected(model, fromDiagramFactor, toDiagramFactor);
		if (linkWasRejected)
			return true;
		
		return !canBeLinked(model.getDiagramObject(), fromDiagramFactor, toDiagramFactor);  
	}
	
	private boolean linkWasRejected(DiagramModel model, DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor) throws Exception
	{
		if (fromDiagramFactor == null || toDiagramFactor == null)
			return true;
		
		if(fromDiagramFactor.getDiagramFactorId().equals(toDiagramFactor.getDiagramFactorId()))
		{
			String[] body = {EAM.text("Can't link an item to itself"), };
			EAM.okDialog(EAM.text("Can't Create Link"), body);
			return true;
		}
		
		ORef fromRef = fromDiagramFactor.getRef();
		ORef toRef = toDiagramFactor.getRef();
		if (isGroupThatContains(fromRef, toRef) || isGroupThatContains(toRef, fromRef))
		{
			String[] body = {EAM.text("Can't link a group to an item it contains"), };
			EAM.okDialog(EAM.text("Can't Create Link"), body);
			return true;
		}
		
		if(fromDiagramFactor.getDiagramFactorId().isInvalid() || toDiagramFactor.getDiagramFactorId().isInvalid())
		{
			EAM.logWarning("Unable to Paste Link : from " + fromDiagramFactor.getDiagramFactorId() + " to OriginalId:" + toDiagramFactor.getDiagramFactorId()+" node deleted?");	
			return true;
		}

		if (! model.containsDiagramFactor(fromDiagramFactor.getRef()) || ! model.containsDiagramFactor(toDiagramFactor.getRef()))
			return true;

		//TODO this method is called twice when inserting a link (that is linking a GB).  Since we are in frozen
		//state we dont want to change this.  This class in general needs cleaning up after frozen.
		if (areDiagramFactorsLinked(fromDiagramFactor, toDiagramFactor))
			return true;
			
		return false;		
	}
	
	public boolean canBeLinked(DiagramObject diagramObject, DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor) throws Exception
	{
		if (areDiagramFactorsLinked(fromDiagramFactor, toDiagramFactor))
			return false;
	
		if (areWrappedFactorsLinkedInDiagram(diagramObject, fromDiagramFactor, toDiagramFactor))
			return false;
		
		boolean isFromGroupBox = fromDiagramFactor.isGroupBoxFactor();
		boolean isToGroupBoxChild = toDiagramFactor.isCoveredByGroupBox();
		if (isFromGroupBox && isToGroupBoxChild)
			return canLinkBetweenGroupAndChild(fromDiagramFactor, toDiagramFactor);
		
		boolean isFromGroupBoxChild = fromDiagramFactor.isCoveredByGroupBox();
		boolean isToGroupBox = toDiagramFactor.isGroupBoxFactor();
		if (isFromGroupBoxChild && isToGroupBox)
			return canLinkBetweenGroupAndChild(toDiagramFactor, fromDiagramFactor);

		return true;
	}

	private boolean canLinkBetweenGroupAndChild(DiagramFactor groupBox, DiagramFactor childDiagramFactor) throws Exception
	{
		ORef owningGroupBoxRef = childDiagramFactor.getOwningGroupBoxRef();
		DiagramFactor owningGroupBox = DiagramFactor.find(getProject(), owningGroupBoxRef);
		if (isLinkedToAnyGroupBoxChildren(owningGroupBox, groupBox))
			return false;

		boolean isOwningAlreadyLinkedToGroupBox = getProject().areDiagramFactorsLinked(owningGroupBoxRef, groupBox.getRef());
		if (isOwningAlreadyLinkedToGroupBox)
			return false;
		
		return true;
	}

	private boolean areDiagramFactorsLinked(DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor) throws Exception
	{
		return getProject().areDiagramFactorsLinked(fromDiagramFactor.getRef(), toDiagramFactor.getRef());
	}

	private boolean areWrappedFactorsLinkedInDiagram(DiagramObject diagramObject, DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor)
	{
		ORef factorLinkRef = getProject().getFactorLinkPool().getLinkedRef(fromDiagramFactor.getWrappedFactor(), toDiagramFactor.getWrappedFactor());
		if (factorLinkRef.isInvalid())
			return false;
		
		DiagramLink diagramLink = diagramObject.getDiagramFactorLink(factorLinkRef);
		return diagramLink != null;
	}
	
	private boolean isLinkedToAnyGroupBoxChildren(DiagramFactor from, DiagramFactor toGroupBox) throws Exception
	{
		ORefList childrenRefs = toGroupBox.getGroupBoxChildrenRefs();
		for (int index = 0; index < childrenRefs.size(); ++index)
		{
			if (getProject().areDiagramFactorsLinked(from.getRef(), childrenRefs.get(index)))
				return true;
		}
		
		return false;
	}
	
	public boolean areGroupBoxOwnedFactorsLinked(DiagramModel diagramModel, DiagramFactor from, DiagramFactor to) throws Exception
	{
		DiagramObject diagramObject = diagramModel.getDiagramObject();
		return areGroupBoxOwnedFactorsLinked(diagramObject, from, to);
	}

	private boolean areGroupBoxOwnedFactorsLinked(DiagramObject diagramObject,
			DiagramFactor from, DiagramFactor to)
	{
		ORefList fromOwningGroupBoxAndChildren = getOwningGroupBoxAndChildren(from);
		ORefList toOwningGroupBoxAndChildren = getOwningGroupBoxAndChildren(to);		
		for (int fromIndex = 0; fromIndex < fromOwningGroupBoxAndChildren.size(); ++fromIndex)
		{
			for (int toIndex = 0; toIndex < toOwningGroupBoxAndChildren.size(); ++toIndex)
			{
				DiagramFactor thisFrom = DiagramFactor.find(getProject(), fromOwningGroupBoxAndChildren.get(fromIndex));
				DiagramFactor thisTo = DiagramFactor.find(getProject(), toOwningGroupBoxAndChildren.get(toIndex));
				if (diagramObject.areLinkedEitherDirection(thisFrom.getWrappedORef(), thisTo.getWrappedORef()))
					return true;
			}
		}
		
		return false;
	}

	private ORefList getOwningGroupBoxAndChildren(DiagramFactor diagramFactor)
	{
		if (diagramFactor.isCoveredByGroupBox())
		{
			DiagramFactor owningGroupBox = DiagramFactor.find(getProject(), diagramFactor.getOwningGroupBoxRef());
			return owningGroupBox.getSelfAndChildren();
		}

		return diagramFactor.getSelfAndChildren();
	}

	public DiagramLink createFactorLinkAndAddToDiagramUsingCommands(DiagramObject diagramObject, DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor) throws Exception
	{
		Factor fromFactor = Factor.findFactor(getProject(), fromDiagramFactor.getWrappedORef());
		Factor toFactor = Factor.findFactor(getProject(), toDiagramFactor.getWrappedORef());
		ORef factorLinkRef = project.getFactorLinkPool().getLinkedRef(fromFactor, toFactor);
		
		if(factorLinkRef.isInvalid())
			factorLinkRef = createFactorLink(fromDiagramFactor, toDiagramFactor);

		ORef diagramLinkRef = createDiagramLink(diagramObject, factorLinkRef, fromDiagramFactor.getRef(), toDiagramFactor.getRef());
		ensureLinkGoesOurWay(DiagramLink.find(getProject(), diagramLinkRef), fromDiagramFactor);

		return DiagramLink.find(getProject(), diagramLinkRef); 
	}

	private void ensureLinkGoesOurWay(DiagramLink diagramLink, DiagramFactor fromDiagramFactor) throws CommandFailedException
	{
		if (diagramLink.isBidirectional())
			return;
		
		if(diagramLink.getWrappedFactorLink().getFromFactorRef().equals(fromDiagramFactor.getWrappedORef()))
			return;
		
		enableBidirectional(diagramLink);
	}

	private void enableBidirectional(DiagramLink diagramLink) throws CommandFailedException
	{
		project.executeCommands(diagramLink.createCommandsToEnableBidirectionalFlag());
	}

	public ORef createFactorLink(DiagramFactor fromDiagramFactor, DiagramFactor toDiagramFactor) throws Exception
	{
		ORef fromFactorRef = fromDiagramFactor.getWrappedORef();
		ORef toFactorRef = toDiagramFactor.getWrappedORef();
		
		return createFactorLink(fromFactorRef, toFactorRef);
	}

	public ORef createFactorLink(ORef fromFactorRef, ORef toFactorRef) throws CommandFailedException
	{
		CommandCreateObject createFactorLink = new CommandCreateObject(ObjectType.FACTOR_LINK);
		project.executeCommand(createFactorLink);
		final ORef factorLinkRef = createFactorLink.getObjectRef();
		project.executeCommand(new CommandSetObjectData(factorLinkRef, FactorLink.TAG_FROM_REF, fromFactorRef));
		project.executeCommand(new CommandSetObjectData(factorLinkRef, FactorLink.TAG_TO_REF, toFactorRef));
		
		return factorLinkRef;
	}
	
	private void createDiagramLinkWithChildren(DiagramObject diagramObject, ORefList allLinkRefs, ORef fromDiagramFactorRef, ORef toDiagramFactorRef) throws Exception
	{
		ORef newGroupBoxDiagramLinkRef = createDiagramLink(diagramObject, ORef.INVALID, fromDiagramFactorRef, toDiagramFactorRef);
	
		updateGroupBoxChildrenRefs(allLinkRefs, newGroupBoxDiagramLinkRef);
	}

	public void updateGroupBoxChildrenRefs(ORefList allLinkRefs, ORef newGroupBoxDiagramLinkRef) throws CommandFailedException
	{
		CommandSetObjectData setChildrenRefs = new CommandSetObjectData(newGroupBoxDiagramLinkRef, DiagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS, allLinkRefs.toString());
		getProject().executeCommand(setChildrenRefs);
	}
	
	public ORef createGroupDiagramLink(DiagramObject diagramObject, ORef fromDiagramFactorRef, ORef toDiagramFactorRef) throws Exception
	{
		return createDiagramLink(diagramObject, ORef.INVALID, fromDiagramFactorRef, toDiagramFactorRef);
	}
	
	public ORef createDiagramLink(DiagramObject diagramObject, ORef factorLinkRef, ORef fromDiagramFactorRef, ORef toDiagramFactorRef) throws Exception
	{
		CommandCreateObject createDiagramLinkCommand =  new CommandCreateObject(ObjectType.DIAGRAM_LINK);
		project.executeCommand(createDiagramLinkCommand);
    	
    	BaseId rawId = createDiagramLinkCommand.getCreatedId();
    	
    	CommandSetObjectData setWrappedId = new CommandSetObjectData(createDiagramLinkCommand.getObjectRef(), DiagramLink.TAG_WRAPPED_ID, factorLinkRef.getObjectId().toString());
    	getProject().executeCommand(setWrappedId);
    	
    	CommandSetObjectData setFromDiagramFactorId = new CommandSetObjectData(createDiagramLinkCommand.getObjectRef(), DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID, fromDiagramFactorRef.getObjectId().toString());
    	getProject().executeCommand(setFromDiagramFactorId);
    	
    	CommandSetObjectData setToDiagramFactorId = new CommandSetObjectData(createDiagramLinkCommand.getObjectRef(), DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID, toDiagramFactorRef.getObjectId().toString());
    	getProject().executeCommand(setToDiagramFactorId);
    	
		DiagramLinkId createdDiagramLinkId = new DiagramLinkId(rawId.asInt());
		
		CommandSetObjectData addDiagramLink = CommandSetObjectData.createAppendIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, createdDiagramLinkId);
		project.executeCommand(addDiagramLink);
		
		return createDiagramLinkCommand.getObjectRef();
	}

	public ORefList createGroupBoxChildrenDiagramLinks(DiagramObject diagramObject, DiagramFactor fromDiagramFactorToUse, DiagramFactor toDiagramFactorToUse) throws Exception
	{
		if (fromDiagramFactorToUse.isGroupBoxFactor() && toDiagramFactorToUse.isGroupBoxFactor())
		{
			deleteRelatedGroupBoxLinks(diagramObject, fromDiagramFactorToUse, toDiagramFactorToUse.getGroupBoxChildrenRefs());
			deleteRelatedGroupBoxLinks(diagramObject, toDiagramFactorToUse, fromDiagramFactorToUse.getGroupBoxChildrenRefs());
		}
		
		ORefList allNonGroupBoxDiagramLinkRefs = new ORefList();
		ORefList fromDiagramFactorRefs = fromDiagramFactorToUse.getSelfOrChildren();
		ORefList toDiagramFactorRefs = toDiagramFactorToUse.getSelfOrChildren();
		for (int from = 0; from < fromDiagramFactorRefs.size(); ++from)
		{
			for (int to = 0; to < toDiagramFactorRefs.size(); ++to)
			{
				DiagramFactor fromDiagramFactor = DiagramFactor.find(getProject(), fromDiagramFactorRefs.get(from));
				DiagramFactor toDiagramFactor = DiagramFactor.find(getProject(), toDiagramFactorRefs.get(to));
				if (diagramObject.areLinkedEitherDirection(fromDiagramFactor.getWrappedORef(), toDiagramFactor.getWrappedORef()))
				{
					DiagramLink diagramLink = diagramObject.getDiagramLink(fromDiagramFactor.getWrappedORef(), toDiagramFactor.getWrappedORef());
					allNonGroupBoxDiagramLinkRefs.add(diagramLink.getRef());
					continue;
				}
				
				DiagramLink diagramLink = createFactorLinkAndAddToDiagramUsingCommands(diagramObject, fromDiagramFactor, toDiagramFactor);
				allNonGroupBoxDiagramLinkRefs.add(diagramLink.getRef());
			}
		}
		
		if (diagramObject.areLinkedEitherDirection(fromDiagramFactorToUse.getWrappedORef(), toDiagramFactorToUse.getWrappedORef()))
		{
			DiagramLink groupBoxDiagramLink = diagramObject.getDiagramLink(fromDiagramFactorToUse.getWrappedORef(), toDiagramFactorToUse.getWrappedORef());
			updateGroupBoxChildrenRefs(allNonGroupBoxDiagramLinkRefs, groupBoxDiagramLink.getRef());
		}
		else
		{
			createDiagramLinkWithChildren(diagramObject, allNonGroupBoxDiagramLinkRefs, fromDiagramFactorToUse.getRef(), toDiagramFactorToUse.getRef());
		}
		
		if (anyOppositeLinks(allNonGroupBoxDiagramLinkRefs, fromDiagramFactorRefs, toDiagramFactorRefs))
			enableBidirectional(allNonGroupBoxDiagramLinkRefs);
		
		return allNonGroupBoxDiagramLinkRefs;
	}
	
	public void enableBidirectional(ORefList createdDiagramLinkRefs) throws Exception
	{
		for (int i = 0; i < createdDiagramLinkRefs.size(); ++i)
		{
			DiagramLink diagramLink = DiagramLink.find(getProject(), createdDiagramLinkRefs.get(i));
			enableBidirectional(diagramLink);
		}
	}
	
	private boolean anyOppositeLinks(ORefList createdDiagramLinkRefs, ORefList fromDiagramFactorRefs, ORefList toDiagramFactorRefs)
	{
		for (int i = 0; i < createdDiagramLinkRefs.size(); ++i)
		{
			DiagramLink diagramLink = DiagramLink.find(getProject(), createdDiagramLinkRefs.get(i));
			if (diagramLink.isBidirectional())
				return true;
			
			ORef toDiagramFactorRef = diagramLink.getToDiagramFactorRef();
			if (fromDiagramFactorRefs.contains(toDiagramFactorRef))
				return true;
			
			ORef fromDiagramFactorRef = diagramLink.getFromDiagramFactorRef();
			if (toDiagramFactorRefs.contains(fromDiagramFactorRef))
				return true;
		}
		
		return false;
	}
	
	private void deleteRelatedGroupBoxLinks(DiagramObject diagramObject, DiagramFactor groupBoxDiagramFactor, ORefList groupBoxChildren) throws Exception
	{
		LinkDeletor linkDeletor = new LinkDeletor(getProject());
		for (int childRef = 0; childRef < groupBoxChildren.size(); ++childRef)
		{
			ORefList diagramLinkRefs = diagramObject.getDiagramLinkFromDiagramFactors(groupBoxDiagramFactor.getRef(), groupBoxChildren.get(childRef));
			for (int refIndex = 0; refIndex < diagramLinkRefs.size(); ++refIndex)
			{
				DiagramLink diagramLink = DiagramLink.find(getProject(), diagramLinkRefs.get(refIndex));
				linkDeletor.deleteDiagramLink(diagramLink);
			}
		}
	}
	
	public static boolean isValidLinkableType(int wrappedType)
	{
		return getLinkableTypes().contains(wrappedType);
	}

	private static HashSet getLinkableTypes()
	{
		int[] linkableTypesArray = {StrategySchema.getObjectType(), 
							   CauseSchema.getObjectType(), 
							   IntermediateResultSchema.getObjectType(), 
							   ThreatReductionResultSchema.getObjectType(), 
							   TargetSchema.getObjectType(),
							   HumanWelfareTargetSchema.getObjectType(),
							   GroupBoxSchema.getObjectType(), };  
		
		HashSet<Integer> linkableTypes = new HashSet<Integer>();
		for (int i = 0; i < linkableTypesArray.length; ++i)
		{
			linkableTypes.add(linkableTypesArray[i]);
		}
		
		return linkableTypes;
	}
	
	public void splitSelectedLinkToIncludeFactor(DiagramModel diagramModel, DiagramLink diagramLink, DiagramFactor newlyInsertedDiagramFactor) throws Exception
	{
		boolean isBidirectional = diagramLink.isBidirectional();
		DiagramFactor fromDiagramFactor = diagramLink.getFromDiagramFactor();
		DiagramFactor toDiagramFactor = diagramLink.getToDiagramFactor();
		
		LinkDeletor linkDeletor = new LinkDeletor(getProject());
		linkDeletor.deleteDiagramLinkAndOrphandFactorLink(diagramLink);
	

		ORefList diagramLinkRefs1 = createFactorLinkAndDiagramLink(diagramModel.getDiagramObject(), fromDiagramFactor, newlyInsertedDiagramFactor);
		if (isBidirectional)
			enableBidirectionality(diagramLinkRefs1);
		
		ORefList diagramLinkRefs2 = createFactorLinkAndDiagramLink(diagramModel.getDiagramObject(), newlyInsertedDiagramFactor, toDiagramFactor);
		if (isBidirectional)
			enableBidirectionality(diagramLinkRefs2);
	}
	
	private void enableBidirectionality(ORefList diagramLinkRefs) throws Exception
	{
		for (int index = 0; index < diagramLinkRefs.size(); ++index)
		{
			enableBidirectional(DiagramLink.find(getProject(), diagramLinkRefs.get(index)));
		}
	}

	public void createFactorLinkAndDiagramLinkVoid(DiagramObject diagramObject, DiagramFactor from, DiagramFactor to) throws Exception
	{
		createFactorLinkAndDiagramLink(diagramObject, from, to);
	}
	
	private ORefList createFactorLinkAndDiagramLink(DiagramObject diagramObject, DiagramFactor from, DiagramFactor to) throws Exception
	{
		if (!from.isGroupBoxFactor() && !to.isGroupBoxFactor())
		{
			DiagramLink created = createFactorLinkAndAddToDiagramUsingCommands(diagramObject, from, to);
			return new ORefList(created.getRef());
		}
		
		return createGroupBoxChildrenDiagramLinks(diagramObject, from, to);
	}
	
	public void createAllPossibleGroupLinks(DiagramObject diagramObject, DiagramFactor groupBoxDiagramFactor) throws Exception
	{
		ORef groupBoxDiagramFactorRef = groupBoxDiagramFactor.getRef();

		{
			ORefSet fromDiagramFactorRefs = getRefsOfDiagramFactorsThatLinkToAllChildren(groupBoxDiagramFactorRef, DiagramLink.FROM);
			ORefSet toDiagramFactorRefs = new ORefSet(groupBoxDiagramFactorRef);
			createAllPossibleGroupLinks(diagramObject, fromDiagramFactorRefs, toDiagramFactorRefs);
		}

		{
			ORefSet fromDiagramFactorRefs = new ORefSet(groupBoxDiagramFactorRef);
			ORefSet toDiagramFactorRefs = getRefsOfDiagramFactorsThatLinkToAllChildren(groupBoxDiagramFactorRef, DiagramLink.TO);
			createAllPossibleGroupLinks(diagramObject, fromDiagramFactorRefs, toDiagramFactorRefs);
		}
	}

	private void createAllPossibleGroupLinks(DiagramObject diagramObject, ORefSet fromDiagramFactorRefs, ORefSet toDiagramFactorRefs) throws Exception
	{
		if(fromDiagramFactorRefs.size() > 1 && toDiagramFactorRefs.size() > 1)
			EAM.logError("createAllPossibleGroupLinks was expecting one-to-many or many-to-one");
		
		for(ORef fromDiagramFactorRef : fromDiagramFactorRefs)
		{
			for(ORef toDiagramFactorRef : toDiagramFactorRefs)
			{
				DiagramFactor from = DiagramFactor.find(getProject(), fromDiagramFactorRef);
				DiagramFactor to = DiagramFactor.find(getProject(), toDiagramFactorRef);
				boolean areAlreadyLinked = areFactorsOrTheirGroupsAlreadyLinked(diagramObject, from, to);

				if(!areAlreadyLinked)
					createGroupBoxChildrenDiagramLinks(diagramObject, from, to);
			}
		}
	}

	private boolean areFactorsOrTheirGroupsAlreadyLinked(DiagramObject diagramObject, DiagramFactor from, DiagramFactor to)
			throws Exception
	{
		ORef fromFactorRef = from.getWrappedORef();
		ORef toFactorRef = to.getWrappedORef();
		if(diagramObject.areLinkedEitherDirection(fromFactorRef, toFactorRef))
			return true;

		DiagramFactor fromGroup = DiagramFactor.find(project, from.getOwningGroupBoxRef());
		DiagramFactor toGroup = DiagramFactor.find(project, to.getOwningGroupBoxRef());

		if(toGroup != null && diagramObject.areLinkedEitherDirection(fromFactorRef, toGroup.getWrappedORef()))
			return true;
		
		if(fromGroup != null && diagramObject.areLinkedEitherDirection(fromGroup.getWrappedORef(), toFactorRef))
			return true;
		
		if(fromGroup != null && toGroup != null && diagramObject.areLinkedEitherDirection(fromGroup.getWrappedORef(), toGroup.getWrappedORef()))
			return true;

		return false;
	}

	public ORefSet getRefsOfDiagramFactorsThatLinkToAllChildren(ORef groupBoxDiagramfactorRef, int direction)
	{
		DiagramFactor groupBoxDiagramFactor = DiagramFactor.find(getProject(), groupBoxDiagramfactorRef);
		ORefSet childRefs = groupBoxDiagramFactor.getGroupBoxChildrenSet();

		Vector<ORefSet> linkedFactorsForEachGroupedFactor = new Vector<ORefSet>();
		for(ORef childRef : childRefs)
		{
			ORefSet diagramFactorsThatLinkToThis = new ORefSet();
			
			ORef thisDiagramFactorRef = childRef;
			DiagramFactor df = DiagramFactor.find(project, childRef);
	
			ORefList diagramLinkRefs = df.findObjectsThatReferToUs(DiagramLinkSchema.getObjectType());
			for(int diagramLinkIndex = 0; diagramLinkIndex < diagramLinkRefs.size(); ++diagramLinkIndex)
			{
				DiagramLink diagramLink = DiagramLink.find(project, diagramLinkRefs.get(diagramLinkIndex));
				ORef maybeThisDiagramFactorRef = diagramLink.getOppositeDiagramFactorRef(direction);
				ORef otherDiagramFactorRef = diagramLink.getDiagramFactorRef(direction);
				if(maybeThisDiagramFactorRef.equals(thisDiagramFactorRef))
					diagramFactorsThatLinkToThis.add(otherDiagramFactorRef);
			}
			
			linkedFactorsForEachGroupedFactor.add(diagramFactorsThatLinkToThis);
		}
		
		if(linkedFactorsForEachGroupedFactor.size() == 0)
			return new ORefSet();
		
		ORefSet result = linkedFactorsForEachGroupedFactor.firstElement();
		for(ORefSet set : linkedFactorsForEachGroupedFactor)
			result.retainAll(set);
	
		return result;
	}

	private Project getProject()
	{
		return project;
	}
	
	private Project project;
}
