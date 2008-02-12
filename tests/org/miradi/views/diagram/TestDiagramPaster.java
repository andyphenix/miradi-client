/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.views.diagram;

import java.awt.Point;
import java.util.HashMap;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.EAMGraphCell;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.ids.IdList;
import org.miradi.main.EAMTestCase;
import org.miradi.main.TransferableMiradiList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.Factor;
import org.miradi.objects.Goal;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Objective;
import org.miradi.objects.Target;
import org.miradi.project.ProjectForTesting;
import org.miradi.views.diagram.DiagramCopyPaster;
import org.miradi.views.diagram.DiagramPaster;

public class TestDiagramPaster extends EAMTestCase
{
	public TestDiagramPaster(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
	}

	public void tearDown() throws Exception
	{
		project.close();
		project = null;
		super.tearDown();
	}

	public void testFixupAllIndicatorRefs() throws Exception
	{
		fixupRefs(Cause.getObjectType(), Indicator.getObjectType(), Factor.TAG_INDICATOR_IDS);
		fixupRefs(Cause.getObjectType(), Objective.getObjectType(), Factor.TAG_OBJECTIVE_IDS);
		fixupRefs(Target.getObjectType(), Goal.getObjectType(), Factor.TAG_GOAL_IDS);
		fixupRefs(Target.getObjectType(), KeyEcologicalAttribute.getObjectType(), Factor.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS);
	}
		
	public void fixupRefs(int factorType, int annotationType, String annotationFactorTag) throws Exception
	{
		DiagramFactor diagramFactor = project.createDiagramFactorAndAddToDiagram(factorType);
		ORef annotationRef1 = project.createFactorAndReturnRef(annotationType);
		ORef annotationRef2 = project.createFactorAndReturnRef(annotationType);
		
		IdList annotationIds = new IdList(annotationType);
		annotationIds.addRef(annotationRef1);
		annotationIds.addRef(annotationRef2);
		
		CommandSetObjectData setFactorAnnotationIds = new CommandSetObjectData(diagramFactor.getWrappedORef(), annotationFactorTag, annotationIds.toString());
		project.executeCommand(setFactorAnnotationIds);
		
		DiagramModel model = project.getDiagramModel();
		FactorCell factorCell = model.getFactorCellById(diagramFactor.getDiagramFactorId());
		EAMGraphCell dataCells[] = {factorCell};
		
		ORef diagramObjectRef = model.getDiagramObject().getRef();
		TransferableMiradiList transferableList = new TransferableMiradiList(project, diagramObjectRef);
		transferableList.storeData(dataCells);
		Factor factor = (Factor) project.findObject(diagramFactor.getWrappedORef());
		DiagramPaster paster = new DiagramCopyPaster(null, project.getDiagramModel(), transferableList);
		paster.pasteFactors(new Point(0, 0));
		
		HashMap oldToNewFactorRefMap = paster.getOldToNewObjectRefMap();
		ORef newRef = (ORef) oldToNewFactorRefMap.get(factor.getRef());
		Factor newFactor = (Factor) project.findObject(newRef);
		IdList newAnnotationIds = new IdList(annotationType, newFactor.getData(annotationFactorTag));
		assertFalse("contains wrong old id?", newAnnotationIds.contains(annotationRef1));
		assertFalse("contains wrong old id?", newAnnotationIds.contains(annotationRef2));
		
		ORef newAnnotation1 = (ORef) oldToNewFactorRefMap.get(annotationRef1);
		assertTrue("does not contain new id?", newAnnotationIds.contains(newAnnotation1));
		
		ORef newAnnotation2 = (ORef) oldToNewFactorRefMap.get(annotationRef2);
		assertTrue("does not contain new id?", newAnnotationIds.contains(newAnnotation2));
	}

	ProjectForTesting project;
}
