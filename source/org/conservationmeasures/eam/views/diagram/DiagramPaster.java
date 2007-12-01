/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.cells.EAMGraphCell;
import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.dialogs.diagram.DiagramPanel;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.TransferableMiradiList;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorParameter;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.CreateThreatStressRatingParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.AccountingCode;
import org.conservationmeasures.eam.objects.Assignment;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.DiagramLink;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.objects.FundingSource;
import org.conservationmeasures.eam.objects.ProjectResource;
import org.conservationmeasures.eam.objects.Stress;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.objects.ThreatStressRating;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;
import org.conservationmeasures.eam.utils.PointList;

abstract public class DiagramPaster
{
	public DiagramPaster(DiagramPanel diagramPanelToUse, DiagramModel modelToUse, TransferableMiradiList transferableListToUse)
	{
		diagramPanel = diagramPanelToUse;
		currentModel = modelToUse;
		project = currentModel.getProject();
		transferableList = transferableListToUse;
		
		factorDeepCopies = transferableList.getFactorDeepCopies();
		diagramFactorDeepCopies = transferableList.getDiagramFactorDeepCopies();
		factorLinkDeepCopies = transferableList.getFactorLinkDeepCopies();
		diagramLinkDeepCopies = transferableList.getDiagramLinkDeepCopies();
		pastedCellsToSelect = new Vector();
	}

	protected void selectNewlyPastedItems()
	{
		//NOTE if-test only exists for tests
		if (diagramPanel == null)
			return;

		EAMGraphCell[] cellsToSelect = (EAMGraphCell[]) pastedCellsToSelect.toArray(new EAMGraphCell[0]);  
		diagramPanel.selectCells(cellsToSelect);
	}

	protected void createNewFactors() throws Exception
	{
		factorRelatedPastedObjectMap = new HashMap();
		for (int i = factorDeepCopies.size() - 1; i >= 0; --i)
		{			
			String jsonAsString = factorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			int type = json.getInt("Type");

			BaseObject newObject = createObject(type);
			loadNewObjectFromOldJson(newObject, json);
			
			BaseId oldId = json.getId(BaseObject.TAG_ID);
			ORef oldObjectRef = new ORef(type, oldId);
			factorRelatedPastedObjectMap.put(oldObjectRef, newObject.getRef());
			fixupRefs(factorRelatedPastedObjectMap,newObject);
		}
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
		String[] fields = newObject.getFieldTags();
		for (int i = 0; i < fields.length; ++i)
		{
			String tag = fields[i];
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

	protected void createNewDiagramFactors() throws Exception
	{
		oldToNewDiagramFactorRefMap = new HashMap();
		for (int i = diagramFactorDeepCopies.size() - 1; i >= 0; --i)
		{
			String jsonAsString = diagramFactorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			ORef oldWrappedRef = json.getRef(DiagramFactor.TAG_WRAPPED_REF);
			ORef newWrappedRef = getDiagramFactorWrappedRef(oldWrappedRef);
			DiagramFactorId diagramFactorId = new DiagramFactorId(json.getId(DiagramFactor.TAG_ID).asInt());

			if (diagramAlreadyContainsAlias(newWrappedRef))
				continue;
			
			String newLocationAsJsonString = offsetLocation(json, diagramFactorId);
			json.put(DiagramFactor.TAG_LOCATION, newLocationAsJsonString);
			
			ORef newDiagramFactorRef = createDiagramFactor(oldWrappedRef, newWrappedRef);
			DiagramFactor newDiagramFactor = (DiagramFactor) getProject().findObject(newDiagramFactorRef);
			Command[]  commandsToLoadFromJson = newDiagramFactor.loadDataFromJson(json);
			getProject().executeCommandsWithoutTransaction(commandsToLoadFromJson);

			addToCurrentDiagram(newDiagramFactorRef, DiagramObject.TAG_DIAGRAM_FACTOR_IDS);
			
			BaseId oldDiagramFactorId = json.getId(DiagramFactor.TAG_ID);
			int type = json.getInt("Type");
			oldToNewDiagramFactorRefMap.put(new ORef(type, oldDiagramFactorId), newDiagramFactorRef);
			addDiagramFactorToSelection(newDiagramFactorRef);
		}
	}

	private boolean diagramAlreadyContainsAlias(ORef oldWrappedRef)
	{
		DiagramObject diagramObject = getDiagramObject();
		return diagramObject.containsWrappedFactorRef(oldWrappedRef);
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


	private ORef createDiagramFactor(ORef oldWrappedRef, ORef newWrappedRef) throws CommandFailedException
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
	
	protected void createNewFactorLinks() throws Exception
	{
		linkRelatedPastedObjectMap = new HashMap();

		for (int i = factorLinkDeepCopies.size() - 1; i >= 0; --i)
		{
			String jsonAsString = factorLinkDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			BaseObject newObject = null;	
			int type = json.getInt("Type");
			if (type == FactorLink.getObjectType())
				newObject = createFactorLink(json);
			if (type == ThreatStressRating.getObjectType())
				newObject = createThreatStressRatings(json);
			
			if (newObject != null)
				fixObjectRefs(linkRelatedPastedObjectMap, newObject, json);
		}
		
		Vector newFactorLinks = new Vector(linkRelatedPastedObjectMap.values());
		ensureRatingListMatchesStressList(newFactorLinks);
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
			
			deleteThreatStressRefsWithoutAStress(factorLink);
			createMissingThreatStressRatingsForStresses(factorLink);
		}
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
		
		ORef newFromRef = getFixedupFactorRef(factorRelatedPastedObjectMap, json, FactorLink.TAG_FROM_REF);
		ORef newToRef = getFixedupFactorRef(factorRelatedPastedObjectMap, json, FactorLink.TAG_TO_REF);	
		
		LinkCreator linkCreator = new LinkCreator(project);
		if (linkCreator.linkWasRejected(currentModel, newFromRef, newToRef))
			return null;
					
		ORef factorLinkRef = linkCreator.createFactorLinkWithoutThreatStressRatings(newFromRef, newToRef);
		FactorLink newFactorLink = (FactorLink) getProject().findObject(factorLinkRef);
		
		Command[]  commandsToLoadFromJson = newFactorLink.createCommandsToLoadFromJson(json);
		getProject().executeCommandsWithoutTransaction(commandsToLoadFromJson);

		BaseId oldFactorLinkId = json.getId(FactorLink.TAG_ID);
		linkRelatedPastedObjectMap.put(new ORef(FactorLink.getObjectType(), oldFactorLinkId), newFactorLink.getRef());
		
		return newFactorLink;
	}

	private BaseObject createThreatStressRatings(EnhancedJsonObject json) throws Exception
	{
		BaseId oldThreatStressRatingId = json.getId(ThreatStressRating.TAG_ID);
		ORef oldStressRef = json.getRef(ThreatStressRating.TAG_STRESS_REF);
		ORef newStressRef = (ORef) factorRelatedPastedObjectMap.get(oldStressRef);
		CreateThreatStressRatingParameter extraInfo = new CreateThreatStressRatingParameter(newStressRef);
		ThreatStressRating newThreatStressRating = (ThreatStressRating) createObject(ThreatStressRating.getObjectType(), extraInfo);
		linkRelatedPastedObjectMap.put(new ORef(ThreatStressRating.getObjectType(), oldThreatStressRatingId), newThreatStressRating.getRef());
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
		return (factorRelatedPastedObjectMap.get(oldFromRef) == null || factorRelatedPastedObjectMap.get(oldToRef) == null);
	}
	
	public boolean wasAnyDataLost() throws Exception
	{
		if (! isInBetweenProjectPaste())
			return false;
		
		for (int i = 0; i < factorDeepCopies.size(); ++i)
		{
			String jsonAsString = factorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			int type = json.getInt("Type");
			if (Assignment.getObjectType() == type)
				return true;
		}
		
		return false;
	}
	
	protected void createNewDiagramLinks() throws Exception
	{	
		int offsetToAvoidOverlaying = dataHelper.getOffset(getProject());
		for (int i = diagramLinkDeepCopies.size() - 1; i >= 0; --i)
		{
			String jsonAsString = diagramLinkDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			
			PointList originalBendPoints = new PointList(json.getString(DiagramLink.TAG_BEND_POINTS));
			String movedBendPointsAsString = movePoints(originalBendPoints, offsetToAvoidOverlaying);
			json.put(DiagramLink.TAG_BEND_POINTS, movedBendPointsAsString);
			
			ORef oldWrappedFactorLinkRef = new ORef(FactorLink.getObjectType(), json.getId(DiagramLink.TAG_WRAPPED_ID));
			ORef newFactorLinkRef = getFactorLinkRef(oldWrappedFactorLinkRef);
			if (newFactorLinkRef == null)
				continue;
			
			FactorLinkId newFactorLinkId = new FactorLinkId(newFactorLinkRef.getObjectId().asInt());
			DiagramFactorId fromDiagramFactorId = getDiagramFactorId(json, DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID);
			DiagramFactorId toDiagramFactorId = getDiagramFactorId(json, DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID);
			LinkCreator linkCreator = new LinkCreator(project);
			if (linkCreator.linkWasRejected(currentModel, fromDiagramFactorId, toDiagramFactorId))
				continue;
			
			int type = json.getInt("Type");
			CreateDiagramFactorLinkParameter extraInfo = new CreateDiagramFactorLinkParameter(newFactorLinkId, fromDiagramFactorId, toDiagramFactorId);
			DiagramLink newDiagramLink = (DiagramLink) createObject(type, extraInfo);
			
			Command[]  commandsToLoadFromJson = newDiagramLink.createCommandsToLoadFromJson(json);
			getProject().executeCommandsWithoutTransaction(commandsToLoadFromJson);
	
			ORef newDiagramLinkRef = newDiagramLink.getRef();
			addToCurrentDiagram(newDiagramLinkRef, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS);
			addDiagramLinkToSelection(newDiagramLinkRef);
		}
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
		ORef newRef = (ORef) oldToNewDiagramFactorRefMap.get(new ORef(ObjectType.DIAGRAM_FACTOR, oldId));
		if (newRef == null)
			return new DiagramFactorId(oldId.asInt()); 
			 
		return new DiagramFactorId(newRef.getObjectId().asInt());
	}
	
	public boolean canPaste() throws Exception
	{
		for (int i = 0; i < factorDeepCopies.size(); i++) 
		{
			String jsonAsString = factorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			int type = json.getInt("Type");
			if (! canPasteTypeInCurrentTab(type))
			{
				EAM.logDebug("Cannot paste type " + type);
				return false;
			}
		}
		
		return true;
	}
	
	private boolean canPasteTypeInCurrentTab(int type)
	{
		if (isResultsChain() && containsType(getResultsChainPastableTypes(), type))
			return true;
		
		if (! isResultsChain() && containsType(getConceptualDiagramPastableTypes(), type))
			return true;
		
		return false;
	}

	private boolean isResultsChain()
	{
		DiagramObject diagramObject = getDiagramObject();
		if (diagramObject.getType() == ObjectType.RESULTS_CHAIN_DIAGRAM)
			return true;
		
		return false;
	}

	private boolean containsType(int[] listOfTypes, int type)
	{
		for (int i = 0 ; i < listOfTypes.length; ++i)
		{
			if (listOfTypes[i] == type)
				return true;
		}
		
		return false;
	}
	
	//TODO simplify the two below methods,  duplicate code
	private int[] getResultsChainPastableTypes()
	{
		return new int[] {
				ObjectType.THREAT_REDUCTION_RESULT,
				ObjectType.INTERMEDIATE_RESULT, 
				ObjectType.STRATEGY, 
				ObjectType.TARGET, 
				ObjectType.TEXT_BOX, 
				ObjectType.INDICATOR,
				ObjectType.OBJECTIVE,
				ObjectType.TASK,
				ObjectType.GOAL,
				ObjectType.KEY_ECOLOGICAL_ATTRIBUTE,
				ObjectType.ASSIGNMENT,
				ObjectType.ACCOUNTING_CODE,
				ObjectType.FUNDING_SOURCE,
				ObjectType.STRESS,
				ObjectType.THREAT_STRESS_RATING,
				};
	}
	
	private int[] getConceptualDiagramPastableTypes()
	{
		return new int[] {
				ObjectType.CAUSE, 
				ObjectType.STRATEGY, 
				ObjectType.TARGET,
				ObjectType.TEXT_BOX,
				ObjectType.INDICATOR,
				ObjectType.OBJECTIVE,
				ObjectType.TASK,
				ObjectType.GOAL,
				ObjectType.KEY_ECOLOGICAL_ATTRIBUTE,
				ObjectType.ASSIGNMENT,
				ObjectType.ACCOUNTING_CODE,
				ObjectType.FUNDING_SOURCE,
				ObjectType.STRESS,
				ObjectType.THREAT_STRESS_RATING,
				};
	}
	
	private Project getProject()
	{
		return project;
	}

	public HashMap getOldToNewFactorRefMap()
	{
		return factorRelatedPastedObjectMap;
	}
	
	private DiagramObject getDiagramObject()
	{
		return currentModel.getDiagramObject();
	}
	
	abstract public ORef getFactorLinkRef(ORef oldWrappedFactorLinkRef);	

	abstract public void pasteFactors(Point startPoint) throws Exception;
	
	abstract public void pasteFactorsAndLinks(Point startPoint) throws Exception;
	
	abstract public ORef getDiagramFactorWrappedRef(ORef oldWrappedRef) throws Exception;	
	
	Project project;
	DiagramModel currentModel;
	DiagramPanel diagramPanel;
	
	Vector<String> factorDeepCopies;
	Vector<String> diagramFactorDeepCopies;
	Vector<String> factorLinkDeepCopies;
	Vector<String> diagramLinkDeepCopies;
	
	HashMap factorRelatedPastedObjectMap;
	HashMap oldToNewDiagramFactorRefMap;
	HashMap linkRelatedPastedObjectMap;
	
	PointManipulater dataHelper;
	TransferableMiradiList transferableList;
	Vector pastedCellsToSelect;
}
