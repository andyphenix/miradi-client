/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.views.diagram;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.miradi.commands.Command;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.EAMGraphCell;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.diagram.cells.LinkCell;
import org.miradi.dialogs.diagram.DiagramPanel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramFactorId;
import org.miradi.ids.FactorLinkId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.main.TransferableMiradiList;
import org.miradi.objecthelpers.CreateDiagramFactorLinkParameter;
import org.miradi.objecthelpers.CreateDiagramFactorParameter;
import org.miradi.objecthelpers.CreateObjectParameter;
import org.miradi.objecthelpers.CreateThreatStressRatingParameter;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.AccountingCode;
import org.miradi.objects.Assignment;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.objects.FactorLink;
import org.miradi.objects.FundingSource;
import org.miradi.objects.IntermediateResult;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Strategy;
import org.miradi.objects.Stress;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.ThreatReductionResult;
import org.miradi.objects.ThreatStressRating;
import org.miradi.project.Project;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.PointList;

abstract public class DiagramPaster
{
	public DiagramPaster(DiagramPanel diagramPanelToUse, DiagramModel modelToUse, TransferableMiradiList transferableListToUse)
	{
		diagramPanel = diagramPanelToUse;
		currentModel = modelToUse;
		project = currentModel.getProject();
		transferableList = transferableListToUse;
		oldToNewPastedObjectMap = new HashMap<ORef, ORef>();
		
		factorDeepCopies = transferableList.getFactorDeepCopies();
		diagramFactorDeepCopies = transferableList.getDiagramFactorDeepCopies();
		factorLinkDeepCopies = transferableList.getFactorLinkDeepCopies();
		diagramLinkDeepCopies = transferableList.getDiagramLinkDeepCopies();
		pastedCellsToSelect = new Vector();
	}
	
	protected Vector getFactorDeepCopies()
	{
		return factorDeepCopies;
	}
	
	protected DiagramModel getDiagramModel()
	{
		return currentModel;
	}
	
	protected void selectNewlyPastedItems()
	{
		//NOTE if-test only exists for tests
		if (diagramPanel == null)
			return;

		EAMGraphCell[] cellsToSelect = (EAMGraphCell[]) pastedCellsToSelect.toArray(new EAMGraphCell[0]);  
		diagramPanel.selectCells(cellsToSelect);
	}

	private String getClipboardProjectFileName()
	{
		return transferableList.getProjectFileName();
	}

	private void fixupRefs(HashMap pastedObjectMap, BaseObject newObject) throws Exception
	{
		Command[] commandsToFixRefs = createCommandToFixupRefLists(pastedObjectMap, newObject);
		getProject().executeCommandsWithoutTransaction(commandsToFixRefs);
	}
	
	public Command[] createCommandToFixupRefLists(HashMap pastedObjectMap, BaseObject newObject) throws Exception
	{
		Vector commands = new Vector();
		String[] fieldTags = newObject.getFieldTags();
		for (int i = 0; i < fieldTags.length; ++i)
		{
			String tag = fieldTags[i];
			commands.addAll(Arrays.asList(getCommandsToFixUpIdListRefs(pastedObjectMap, newObject, tag)));
			commands.addAll(Arrays.asList(getCommandsToFixUpORefList(pastedObjectMap, newObject, tag)));
			commands.addAll(Arrays.asList(getCommandToFixUpIdRefs(pastedObjectMap,newObject, tag)));
		}
		
		return (Command[]) commands.toArray(new Command[0]);
	}
	
	private Command[] getCommandsToFixUpIdListRefs(HashMap pastedObjectMap, BaseObject newObject, String tag) throws Exception
	{
		if (!newObject.isIdListTag(tag))
			return new Command[0];
		
		Command commandToFixRefs = fixUpIdList(pastedObjectMap, newObject, tag, newObject.getAnnotationType(tag));
		return new Command[] {commandToFixRefs};
	}
	
	private Command[] getCommandsToFixUpORefList(HashMap pastedObjectMap, BaseObject newObject, String tag) throws Exception
	{
		if (!newObject.isRefList(tag))
			return new Command[0];
		
		Command commandToFixRefs = fixUpRefList(pastedObjectMap, newObject, tag, newObject.getAnnotationType(tag));
		return new Command[] {commandToFixRefs};
	}
		
	private Command[] getCommandToFixUpIdRefs(HashMap pastedObjectMap, BaseObject newObject, String tag) throws Exception
	{
		if (Assignment.getObjectType() == newObject.getType())
		{
			if (Assignment.TAG_ACCOUNTING_CODE.equals(tag))
				return getCommandToFixId(pastedObjectMap, newObject, AccountingCode.getObjectType(), tag);

			if (Assignment.TAG_FUNDING_SOURCE.equals(tag))
				return getCommandToFixId(pastedObjectMap, newObject, FundingSource.getObjectType(), tag);

			if (Assignment.TAG_ASSIGNMENT_RESOURCE_ID.equals(tag))
				return getCommandToFixId(pastedObjectMap, newObject, ProjectResource.getObjectType(), tag);
		}
		
		if (ThreatStressRating.getObjectType() == newObject.getType())
		{
			if (ThreatStressRating.TAG_STRESS_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
		}
		
		if (DiagramFactor.getObjectType() == newObject.getType())
		{
			if (DiagramFactor.TAG_WRAPPED_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
		}
					
		return new Command[0];
	}

	private Command[] getCommandToFixId(HashMap pastedObjectMap, BaseObject newObject, int annotationType, String tag) throws Exception
	{
		BaseId baseId = new BaseId(newObject.getData(tag));
		ORef refToFix = new ORef(annotationType, baseId);
		ORef fixedRef = fixupSingleRef(pastedObjectMap, refToFix);
		
		return new Command[] {new CommandSetObjectData(newObject.getRef(), tag, fixedRef.getObjectId().toString())};
	}

	private Command[] getCommandToFixRef(HashMap pastedObjectMap, BaseObject newObject, String tag) throws Exception
	{
		ORef refToFix = ORef.createFromString(newObject.getData(tag));
		ORef fixedRef = fixupSingleRef(pastedObjectMap, refToFix);
		
		return new Command[] {new CommandSetObjectData(newObject.getRef(), tag, fixedRef.toString())};
	}

	private Command fixUpIdList(HashMap pastedObjectMap, BaseObject newObject, String annotationTag, int annotationType) throws Exception
	{
		//FIXME currently items ids found in list but not in map are not added to new list
		IdList oldList = new IdList(annotationType, newObject.getData(annotationTag));
		IdList newList = new IdList(annotationType);
		for (int i = 0; i < oldList.size(); ++i)
		{
			ORef oldRef = oldList.getRef(i);
			ORef refToAdd = fixupSingleRef(pastedObjectMap, oldRef);
			if (!refToAdd.isInvalid())
				newList.addRef(refToAdd);
		}
		
		return new CommandSetObjectData(newObject.getRef(), annotationTag, newList.toString());
	}

	//TODO this is duplicate code as above exceot it deals in RefList,  
	private Command fixUpRefList(HashMap pastedObjectMap, BaseObject newObject, String annotationTag, int annotationType) throws Exception
	{
		//FIXME currently items ids found in list but not in map are not added to new list
		ORefList oldList = new ORefList(newObject.getData(annotationTag));
		ORefList newList = new ORefList();
		for (int i = 0; i < oldList.size(); ++i)
		{
			ORef oldRef = oldList.get(i);
			ORef refToAdd = fixupSingleRef(pastedObjectMap, oldRef);
			if (!refToAdd.isInvalid())
				newList.add(refToAdd);
		}
		
		return new CommandSetObjectData(newObject.getRef(), annotationTag, newList.toString());
	}

	
	private ORef fixupSingleRef(HashMap pastedObjectMap, ORef oldRef) throws Exception
	{
		if (pastedObjectMap.containsKey(oldRef))
			return  (ORef) pastedObjectMap.get(oldRef);
		
		if (!isInBetweenProjectPaste())
			return oldRef;
		
		return ORef.INVALID;
	}

	private ORef getFixedupFactorRef(HashMap pastedObjectMap, EnhancedJsonObject json, String tag) throws Exception
	{
		ORef oldRef = json.getRef(tag);
		return fixupSingleRef(pastedObjectMap, oldRef);
	}	
	
	private void loadNewObjectFromOldJson(BaseObject newObject, EnhancedJsonObject json) throws Exception, CommandFailedException
	{
		Command[] commandsToLoadFromJson = newObject.createCommandsToLoadFromJson(json);
		getProject().executeCommandsWithoutTransaction(commandsToLoadFromJson);
	}
	
	private BaseObject createObject(int type) throws Exception
	{
		return createObject(type, null);
	}
	
	private BaseObject createObject(int type, CreateObjectParameter extraInfo) throws CommandFailedException
	{
		CommandCreateObject createObject = new CommandCreateObject(type, extraInfo);
		getProject().executeCommand(createObject);
		
		ORef newObjectRef = createObject.getObjectRef();
		BaseObject newObject = getProject().findObject(newObjectRef);
		
		return newObject;
	}

	private boolean diagramAlreadyContainsAlias(ORef wrappedRef)
	{
		DiagramObject diagramObject = getDiagramObject();
		return diagramObject.containsWrappedFactorRef(wrappedRef);
	}

	private String offsetLocation(EnhancedJsonObject json, DiagramFactorId diagramFactorId) throws Exception
	{
		Point originalLocation = json.getPoint(DiagramFactor.TAG_LOCATION);
		int offsetToAvoidOverlaying = dataHelper.getOffset(getProject());
		Point transLatedPoint = dataHelper.getSnappedTranslatedPoint(getProject(), originalLocation, offsetToAvoidOverlaying);
		
		return EnhancedJsonObject.convertFromPoint(transLatedPoint);
	}

	private String movePoints(PointList originalBendPoints, int offsetToAvoidOverlaying) throws Exception
	{
		PointList movedPoints = new PointList();
		for (int i = 0; i < originalBendPoints.size(); ++i)
		{
			Point originalPoint = originalBendPoints.get(i);
			Point translatedPoint = dataHelper.getSnappedTranslatedPoint(getProject(), originalPoint, offsetToAvoidOverlaying);			
			movedPoints.add(translatedPoint);
		}
		
		return movedPoints.toString();
	}

	private void addDiagramFactorToSelection(ORef diagramFactorRefToSelect) throws Exception
	{
		DiagramFactorId diagramFactorId = new DiagramFactorId(diagramFactorRefToSelect.getObjectId().asInt());
		FactorCell cell = currentModel.getFactorCellById(diagramFactorId);
		pastedCellsToSelect.add(cell);
	}
	
	private void addDiagramLinkToSelection(ORef diagramLinkRefToSelect) throws Exception
	{
		DiagramLink diagramLink = (DiagramLink) project.findObject(diagramLinkRefToSelect);
		LinkCell linkCell = currentModel.getDiagramFactorLink(diagramLink);
		pastedCellsToSelect.add(linkCell);	
	}


	private ORef createDiagramFactor(ORef newWrappedRef) throws CommandFailedException
	{
		CreateDiagramFactorParameter extraInfo = new CreateDiagramFactorParameter(newWrappedRef);
		CommandCreateObject createDiagramFactor = new CommandCreateObject(DiagramFactor.getObjectType(), extraInfo);
		getProject().executeCommand(createDiagramFactor);
		
		return createDiagramFactor.getObjectRef();
	}

	private void addToDiagramObject(DiagramObject diagramObjectToAddTo, ORef refToAppend, String tag) throws Exception
	{
		CommandSetObjectData addDiagramFactor = CommandSetObjectData.createAppendIdCommand(diagramObjectToAddTo, tag, refToAppend.getObjectId());
		getProject().executeCommand(addDiagramFactor);
	}
	
	private void addToCurrentDiagram(ORef refToAppend, String tag) throws Exception
	{
		addToDiagramObject(getDiagramObject(), refToAppend, tag);
	}

	protected void createNewFactorsAndContents() throws Exception
	{
		for (int i = factorDeepCopies.size() - 1; i >= 0; --i)
		{			
			String jsonAsString = factorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			int type = getTypeFromJson(json);

			BaseId oldId = json.getId(BaseObject.TAG_ID);
			ORef oldObjectRef = new ORef(type, oldId);
			
			if (!shouldCreateObject(oldObjectRef))
				continue;
			
			int convertedType = convertType(oldObjectRef);
			BaseObject newObject = createObject(convertedType);
			loadNewObjectFromOldJson(newObject, json);
			
			getOldToNewObjectRefMap().put(oldObjectRef, newObject.getRef());
			fixupRefs(getOldToNewObjectRefMap(),newObject);
		}
	}

	protected void createNewDiagramFactors() throws Exception
	{
		for (int i = diagramFactorDeepCopies.size() - 1; i >= 0; --i)
		{
			String jsonAsString = diagramFactorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			createNewDiagramFactor(json);
		}
	}
	
	private void createNewDiagramFactor(EnhancedJsonObject json) throws Exception
	{
		DiagramFactorId diagramFactorId = new DiagramFactorId(json.getId(DiagramFactor.TAG_ID).asInt());
		ORef diagramFactorRef = new ORef(DiagramFactor.getObjectType(), diagramFactorId);
		if (oldToNewPastedObjectMap.containsKey(diagramFactorRef))
			return;
		
		ORef oldWrappedRef = json.getRef(DiagramFactor.TAG_WRAPPED_REF);
		ORef newWrappedRef = getDiagramFactorWrappedRef(oldWrappedRef);
		if (diagramAlreadyContainsAlias(newWrappedRef))
			return;
		
		if (ignorePastingDiagramFactorForFactor(newWrappedRef))
			return;
		
		String newLocationAsJsonString = offsetLocation(json, diagramFactorId);
		json.put(DiagramFactor.TAG_LOCATION, newLocationAsJsonString);
		
		ORef newDiagramFactorRef = createDiagramFactor(newWrappedRef);
		DiagramFactor newDiagramFactor = (DiagramFactor) getProject().findObject(newDiagramFactorRef);
		loadNewObjectFromOldJson(newDiagramFactor, json);

		BaseId oldDiagramFactorId = json.getId(DiagramFactor.TAG_ID);
		int type = getTypeFromJson(json);
		getOldToNewObjectRefMap().put(new ORef(type, oldDiagramFactorId), newDiagramFactorRef);
		fixupRefs(getOldToNewObjectRefMap(), newDiagramFactor);
		addToCurrentDiagram(newDiagramFactorRef, DiagramObject.TAG_DIAGRAM_FACTOR_IDS);
		addDiagramFactorToSelection(newDiagramFactorRef);
	}

	protected void createNewFactorLinks() throws Exception
	{
		for (int i = factorLinkDeepCopies.size() - 1; i >= 0; --i)
		{
			String jsonAsString = factorLinkDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			BaseObject newObject = null;	
			int type = getTypeFromJson(json);
			if (type == FactorLink.getObjectType())
				newObject = createFactorLink(json);
			if (type == ThreatStressRating.getObjectType() && isPastingIntoConceptualModel())
				newObject = createThreatStressRatings(json);
			
			if (newObject != null)
			{
				fixObjectRefs(getOldToNewObjectRefMap(), newObject, json);
				clearThreatStressRatingFieldForResultsChainPastes(newObject);
			}
		}
		
		Vector newFactorLinks = new Vector(getOldToNewObjectRefMap().values());
		ensureRatingListMatchesStressList(newFactorLinks);
	}

	private void clearThreatStressRatingFieldForResultsChainPastes(BaseObject newObject) throws CommandFailedException
	{
		if (isPastingIntoConceptualModel())
			return;
		
		if (!FactorLink.is(newObject.getType()))
			return;
		
		CommandSetObjectData clearThreatStressRatingList = new CommandSetObjectData(newObject.getRef(), FactorLink.TAG_THREAT_STRESS_RATING_REFS, "");
		getProject().executeCommand(clearThreatStressRatingList);
	}
	
	protected void createNewDiagramLinks() throws Exception
	{	
		int offsetToAvoidOverlaying = dataHelper.getOffset(getProject());
		for (int i = diagramLinkDeepCopies.size() - 1; i >= 0; --i)
		{
			String jsonAsString = diagramLinkDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			BaseId diagramLinkId = json.getId(DiagramLink.TAG_ID);
			ORef diagramLinkRef = new ORef(DiagramLink.getObjectType(), diagramLinkId);
			if (oldToNewPastedObjectMap.containsKey(diagramLinkRef))
				continue;
	
			PointList originalBendPoints = new PointList(json.getString(DiagramLink.TAG_BEND_POINTS));
			String movedBendPointsAsString = movePoints(originalBendPoints, offsetToAvoidOverlaying);
			json.put(DiagramLink.TAG_BEND_POINTS, movedBendPointsAsString);
			
			ORef oldWrappedFactorLinkRef = new ORef(FactorLink.getObjectType(), json.getId(DiagramLink.TAG_WRAPPED_ID));
			ORef newFactorLinkRef = getFactorLinkRef(oldWrappedFactorLinkRef);
			if(newFactorLinkRef == null)
			{
				EAM.logVerbose("Skipping link because Factor Link " + oldWrappedFactorLinkRef + " for Diagram Link " + diagramLinkRef + " is null");
				continue;
			}
			if(newFactorLinkRef.isInvalid())
			{
				EAM.logError("Skipping link because Factor Link " + oldWrappedFactorLinkRef + " for Diagram Link " + diagramLinkRef + " is invalid");
				continue;
			}
			
			DiagramFactorId fromDiagramFactorId = getDiagramFactorId(json, DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID);
			DiagramFactorId toDiagramFactorId = getDiagramFactorId(json, DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID);
			LinkCreator linkCreator = new LinkCreator(project);
			if (linkCreator.linkWasRejected(currentModel, fromDiagramFactorId, toDiagramFactorId))
				continue;
			
			CreateDiagramFactorLinkParameter extraInfo = createFactorLinkExtraInfo(fromDiagramFactorId, toDiagramFactorId, newFactorLinkRef);
			int type = getTypeFromJson(json);
			DiagramLink newDiagramLink = (DiagramLink) createObject(type, extraInfo);
			
			Command[]  commandsToLoadFromJson = newDiagramLink.createCommandsToLoadFromJson(json);
			getProject().executeCommandsWithoutTransaction(commandsToLoadFromJson);
	
			ORef newDiagramLinkRef = newDiagramLink.getRef();
			getOldToNewObjectRefMap().put(diagramLinkRef, newDiagramLinkRef);
			fixupRefs(getOldToNewObjectRefMap(), newDiagramLink);
			addToCurrentDiagram(newDiagramLinkRef, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS);
			addDiagramLinkToSelection(newDiagramLinkRef);
		}
	}

	private CreateDiagramFactorLinkParameter createFactorLinkExtraInfo(DiagramFactorId fromDiagramFactorId, DiagramFactorId toDiagramFactorId, ORef newFactorLinkRef)
	{
		if (newFactorLinkRef == null)
			return new CreateDiagramFactorLinkParameter(fromDiagramFactorId, toDiagramFactorId);
		
		FactorLinkId newFactorLinkId = new FactorLinkId(newFactorLinkRef.getObjectId().asInt());
		return new CreateDiagramFactorLinkParameter(newFactorLinkId, fromDiagramFactorId, toDiagramFactorId);
	}

	private void ensureRatingListMatchesStressList(Vector newFactorLinks) throws Exception
	{
		for (int i = 0; i < newFactorLinks.size(); ++i)
		{
			ORef newFactorLinkRef = (ORef) newFactorLinks.get(i);
			if (newFactorLinkRef.getObjectType() != FactorLink.getObjectType())
				continue;
	
			FactorLink factorLink = FactorLink.find(getProject(), newFactorLinkRef);
			if (!factorLink.isThreatTargetLink())
				continue;
			
			if (!isPastingIntoConceptualModel())
				continue;
			
			deleteThreatStressRefsWithoutAStress(factorLink);
			createMissingThreatStressRatingsForStresses(factorLink);
		}
	}

	private boolean isPastingIntoResultsChain()
	{
		return ResultsChainDiagram.is(getDiagramObject().getType());
	}
	
	private boolean isPastingIntoConceptualModel()
	{
		return ConceptualModelDiagram.is(getDiagramObject().getType());
	}

	private void createMissingThreatStressRatingsForStresses(FactorLink factorLink) throws Exception
	{
		ORefList stressRefsWithoutRating = computeStressRefsWithoutThreatStressRating(factorLink);
		for (int i = 0; i < stressRefsWithoutRating.size(); ++i)
		{
			ORef newThreatStressRatingRef = new LinkCreator(getProject()).createThreatStressRating(stressRefsWithoutRating.get(i));
			CommandSetObjectData appendThreatStressRating = CommandSetObjectData.createAppendORefCommand(factorLink, FactorLink.TAG_THREAT_STRESS_RATING_REFS, newThreatStressRatingRef);
			getProject().executeCommand(appendThreatStressRating);
		}
	}

	private ORefList computeStressRefsWithoutThreatStressRating(FactorLink factorLink) throws Exception
	{
		ORefList extractedStresses = extractThreatStressRatingStresses(factorLink);
		ORef targetRef = factorLink.getDownstreamTargetRef();
		Target target = Target.find(getProject(), targetRef);
		ORefList stresses = target.getStressRefs();
		ORefList stressRefsWithoutRating = ORefList.subtract(stresses, extractedStresses);
		return stressRefsWithoutRating;
	}		

	private ORefList extractThreatStressRatingStresses(FactorLink factorLink)
	{
		ORefList threatStressRatingRefs = factorLink.getThreatStressRatingRefs();
		ORefList extractedStressRefs = new ORefList();
		for (int i = 0; i < threatStressRatingRefs.size();++i)
		{
			ThreatStressRating threatStressRating = ThreatStressRating.find(getProject(), threatStressRatingRefs.get(i));
			extractedStressRefs.add(threatStressRating.getStressRef());
		}
		
		return extractedStressRefs;
	}

	private void deleteThreatStressRefsWithoutAStress(FactorLink factorLink) throws Exception
	{
		ORefList threatStressRefs = factorLink.getThreatStressRatingRefs();
		for(int i = 0; i < threatStressRefs.size(); ++i)
		{
			ThreatStressRating threatStressRating = ThreatStressRating.find(getProject(), threatStressRefs.get(i));
			Stress stress = Stress.find(getProject(), threatStressRating.getStressRef());
			if (stress == null)
				deleteThreatStressRating(factorLink, threatStressRating);
		}
	}

	private void deleteThreatStressRating(FactorLink factorLink, ThreatStressRating threatStressRating) throws Exception
	{
		CommandSetObjectData removeThreatStressRating = CommandSetObjectData.createRemoveORefCommand(factorLink, FactorLink.TAG_THREAT_STRESS_RATING_REFS, threatStressRating.getRef());
		getProject().executeCommand(removeThreatStressRating);
		
		new LinkDeletor(getProject()).deleteThreatStressRating(threatStressRating);
	}

	private void fixObjectRefs(HashMap pastedObjectMap, BaseObject newObject, EnhancedJsonObject json) throws Exception, CommandFailedException
	{
		loadNewObjectFromOldJson(newObject, json);
		fixupRefs(pastedObjectMap, newObject);
	}
	
	private FactorLink createFactorLink(EnhancedJsonObject json) throws Exception
	{
		if (cannotCreateNewFactorLinkFromAnotherProject(json))
			return null;
		
		ORef newFromRef = getFixedupFactorRef(getOldToNewObjectRefMap(), json, FactorLink.TAG_FROM_REF);
		ORef newToRef = getFixedupFactorRef(getOldToNewObjectRefMap(), json, FactorLink.TAG_TO_REF);	
		
		LinkCreator linkCreator = new LinkCreator(project);
		if (linkCreator.linkWasRejected(currentModel, newFromRef, newToRef))
			return null;
					
		ORef factorLinkRef = linkCreator.createFactorLinkWithoutThreatStressRatings(newFromRef, newToRef);
		FactorLink newFactorLink = (FactorLink) getProject().findObject(factorLinkRef);
		
		Command[]  commandsToLoadFromJson = newFactorLink.createCommandsToLoadFromJson(json);
		getProject().executeCommandsWithoutTransaction(commandsToLoadFromJson);

		BaseId oldFactorLinkId = json.getId(FactorLink.TAG_ID);
		getOldToNewObjectRefMap().put(new ORef(FactorLink.getObjectType(), oldFactorLinkId), newFactorLink.getRef());
		
		return newFactorLink;
	}

	private BaseObject createThreatStressRatings(EnhancedJsonObject json) throws Exception
	{
		BaseId oldThreatStressRatingId = json.getId(ThreatStressRating.TAG_ID);
		ORef newStressRef = json.getRef(ThreatStressRating.TAG_STRESS_REF);
		CreateThreatStressRatingParameter extraInfo = new CreateThreatStressRatingParameter(newStressRef);
		ThreatStressRating newThreatStressRating = (ThreatStressRating) createObject(ThreatStressRating.getObjectType(), extraInfo);
		getOldToNewObjectRefMap().put(new ORef(ThreatStressRating.getObjectType(), oldThreatStressRatingId), newThreatStressRating.getRef());
		return newThreatStressRating;
	}	

	private boolean isInBetweenProjectPaste()
	{
		return ! getProject().getFilename().equals(getClipboardProjectFileName());
	}
	
	private boolean cannotCreateNewFactorLinkFromAnotherProject(EnhancedJsonObject json)
	{
		ORef oldFromRef = json.getRef(FactorLink.TAG_FROM_REF);
		ORef oldToRef = json.getRef(FactorLink.TAG_TO_REF);
		boolean haveBothFactorsBeenCopied = haveBothFactorsBeenCopied(oldFromRef, oldToRef);
		boolean isInBetweenProjectPaste = isInBetweenProjectPaste();
		
		return (haveBothFactorsBeenCopied && isInBetweenProjectPaste);
	}

	private boolean haveBothFactorsBeenCopied(ORef oldFromRef, ORef oldToRef)
	{
		return (getOldToNewObjectRefMap().get(oldFromRef) == null || getOldToNewObjectRefMap().get(oldToRef) == null);
	}
	
	public boolean wasAnyDataLost() throws Exception
	{
		if (! isInBetweenProjectPaste())
			return false;
		
		for (int i = 0; i < factorDeepCopies.size(); ++i)
		{
			String jsonAsString = factorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			int type = getTypeFromJson(json);
			if (Assignment.getObjectType() == type)
				return true;
		}
		
		return false;
	}
	
	public void wrapExistingLinksForDiagramFactorsInAllDiagramObjects() throws Exception
	{
		ORefList allDiagramObjects = GroupOfDiagrams.getAllDiagramObjects(getProject());
		for (int i = 0; i < allDiagramObjects.size(); ++i)
		{
			DiagramObject diagramObject = (DiagramObject) getProject().findObject(allDiagramObjects.get(i));
			wrapExistingLinksForDiagramFactors(diagramObject);
		}
	}
	
	private void wrapExistingLinksForDiagramFactors(DiagramObject diagramObjectToUse) throws Exception
	{
		ORefList diagramFactorRefs = diagramObjectToUse.getAllDiagramFactorRefs();
		for (int i= 0; i < diagramFactorRefs.size(); ++i)
		{
			DiagramFactor diagramFactor = (DiagramFactor) getProject().findObject(diagramFactorRefs.get(i));			
			wrapExistingLinksForThisFactor(diagramObjectToUse, diagramFactor.getWrappedORef());
		}
	}
	
	private void wrapExistingLinksForThisFactor(DiagramObject diagramObjectToUse, ORef factorRef) throws Exception
	{
		Factor factor = (Factor) getProject().findObject(factorRef);
		ORefList factorLinks = factor.findObjectsThatReferToUs(FactorLink.getObjectType());
		for (int i = 0; i < factorLinks.size(); ++i)
		{
			FactorLink factorLink = (FactorLink) getProject().findObject(factorLinks.get(i));
			DiagramFactor fromDiagramFactor = diagramObjectToUse.getDiagramFactor(factorLink.getFromFactorRef());
			DiagramFactor toDiagramFactor = diagramObjectToUse.getDiagramFactor(factorLink.getToFactorRef());
			if (fromDiagramFactor == null || toDiagramFactor == null)
				continue;
			
			if (diagramObjectToUse.areDiagramFactorsLinked(fromDiagramFactor.getDiagramFactorId(), toDiagramFactor.getDiagramFactorId()))
				continue;
			
			CreateDiagramFactorLinkParameter extraInfo = new CreateDiagramFactorLinkParameter(factorLink.getFactorLinkId(), fromDiagramFactor.getDiagramFactorId(), toDiagramFactor.getDiagramFactorId());
			DiagramLink newDiagramLink = (DiagramLink) createObject(DiagramLink.getObjectType(), extraInfo);	
			ORef newDiagramLinkRef = newDiagramLink.getRef();
			addToDiagramObject(diagramObjectToUse, newDiagramLinkRef, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS);
			addDiagramLinkToSelection(newDiagramLinkRef);		
		}
	}

	private DiagramFactorId getDiagramFactorId(EnhancedJsonObject json, String tag)
	{
		BaseId oldId = json.getId(tag);
		ORef newRef = getOldToNewObjectRefMap().get(new ORef(ObjectType.DIAGRAM_FACTOR, oldId));
		if (newRef == null)
			return new DiagramFactorId(oldId.asInt()); 
			 
		return new DiagramFactorId(newRef.getObjectId().asInt());
	}
	
	private int convertType(ORef oldObjectRef)
	{
		if (isPastingInSameDiagramType())
			return oldObjectRef.getObjectType();

		if (!Factor.isFactor(oldObjectRef))
			return oldObjectRef.getObjectType();
		
		Factor factor = Factor.findFactor(getProject(), oldObjectRef);
		if (isFromConceptualModel())
		{
			if (factor.isContributingFactor())
				return IntermediateResult.getObjectType();
			
			if (factor.isDirectThreat())
				return ThreatReductionResult.getObjectType();
		}
		
		if (isFromResultsChain())
		{
			if (factor.isIntermediateResult() || factor.isThreatReductionResult())
				return Cause.getObjectType();
		}
		
		return oldObjectRef.getObjectType();
	}

	public boolean isPastingInSameDiagramType()
	{
		int fromType = transferableList.getDiagramObjectRefCopiedFrom().getObjectType();
		int toType = getDiagramObject().getType();
		
		return (fromType == toType);
	}
	
	private boolean isFromConceptualModel()
	{
		return ConceptualModelDiagram.is(transferableList.getDiagramObjectRefCopiedFrom());
	}
	
	private boolean isFromResultsChain()
	{
		return ResultsChainDiagram.is(transferableList.getDiagramObjectRefCopiedFrom());
	}

	public Project getProject()
	{
		return project;
	}

	public HashMap<ORef, ORef> getOldToNewObjectRefMap()
	{
		return oldToNewPastedObjectMap;
	}
	
	protected DiagramObject getDiagramObject()
	{
		return currentModel.getDiagramObject();
	}
	
	public boolean canPaste() throws Exception
	{
		for (int i = 0; i < factorDeepCopies.size(); i++) 
		{
			String jsonAsString = factorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			int type = getTypeFromJson(json);
			if (!canPasteTypeInDiagram(type))
			{
				EAM.logDebug("Cannot paste type " + type);
				return false;
			}
		}
		
		return true;
	}

	protected int getTypeFromJson(EnhancedJsonObject json)
	{
		return json.getInt("Type");
	}

	protected boolean containsType(int[] types, int type)
	{
		for (int i = 0; i < types.length; ++i)
		{
			if (types[i] == type)
				return true;
		}
		
		return false;
	}
	
	private boolean ignorePastingDiagramFactorForFactor(ORef factorRef)
	{
		if (!Task.is(factorRef))
			return true;
		
		if (!Stress.is(factorRef))
			return true;
		
		if (!canPasteStress(factorRef))
			return true;
		
		if (!canPasteActivity(factorRef))
			return true;

		return false;
	}
	
	private boolean canPasteStress(ORef newWrappedRef)
	{
		if (doesObjectExist(newWrappedRef))
			return true;
		
		if (isPastingIntoResultsChain())
			return false;
		
		return hasReferrersInDiagram(newWrappedRef, Target.getObjectType());
	}
	
	private boolean canPasteActivity(ORef newWrappedRef)
	{
		if (doesObjectExist(newWrappedRef))
			return true;
		
		if (isPastingIntoConceptualModel())
			return false;
		
		return hasReferrersInDiagram(newWrappedRef, Strategy.getObjectType());
	}
	
	private boolean doesObjectExist(ORef factorRef)
	{
		BaseObject found = getProject().findObject(factorRef);
		if (found == null)
			return false;
		
		return true;
	}
	
	private boolean hasReferrersInDiagram(ORef referredObjectRef, int referrerType)
	{
		ORefList referrerRefs = getDiagramObject().findReferrersOnSameDiagram(referredObjectRef, referrerType);
		return referrerRefs.size() > 0;
	}
	
	abstract public ORef getFactorLinkRef(ORef oldWrappedFactorLinkRef);	

	abstract public void pasteFactors(Point startPoint) throws Exception;
	
	abstract public void pasteFactorsAndLinks(Point startPoint) throws Exception;
	
	abstract public ORef getDiagramFactorWrappedRef(ORef oldWrappedRef) throws Exception;
	
	abstract protected boolean canPasteTypeInDiagram(int type);
	
	abstract protected boolean shouldCreateObject(ORef ref);
	
	private Project project;
	private DiagramModel currentModel;
	private DiagramPanel diagramPanel;
	
	private Vector<String> factorDeepCopies;
	private Vector<String> diagramFactorDeepCopies;
	private Vector<String> factorLinkDeepCopies;
	private Vector<String> diagramLinkDeepCopies;
	
	protected HashMap<ORef, ORef> oldToNewPastedObjectMap;	
	protected PointManipulater dataHelper;
	protected TransferableMiradiList transferableList;
	private Vector pastedCellsToSelect;
}
