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

package org.miradi.main;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.util.Vector;

import org.miradi.commands.CommandCreateObject;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.EAMGraphCell;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorLinkId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.Factor;
import org.miradi.objects.FactorLink;
import org.miradi.project.FactorCommandHelper;
import org.miradi.project.ProjectForTesting;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.views.diagram.LinkCreator;

public class TestTransferableEamList extends EAMTestCase 
{

	public TestTransferableEamList(String name) 
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
		super.tearDown();
		project.close();
		project = null;
	}

	public void testGetTransferDataFlavors() throws Exception
	{
		EAMGraphCell emptyCells[] = {};
		ORef diagramObjectRef = project.getDiagramObject().getRef();
		TransferableMiradiList miradiList = new TransferableMiradiList(project, diagramObjectRef);
		miradiList.storeData(emptyCells);
		DataFlavor flavors[] = miradiList.getTransferDataFlavors();
		assertEquals("Should only support 1 flavor?", 1, flavors.length);
		assertEquals("MiradiListDataFlavor not found?", TransferableMiradiList.miradiListDataFlavor, flavors[0]);
	}
	
	public void testIsDataFlavorSupported() throws Exception
	{
		EAMGraphCell emptyCells[] = {};
		ORef diagramObjectRef = project.getDiagramObject().getRef();
		TransferableMiradiList miradiList = new TransferableMiradiList(project, diagramObjectRef);
		miradiList.storeData(emptyCells);
		assertTrue("MiradiListDataFlavor not supported?", miradiList.isDataFlavorSupported(TransferableMiradiList.miradiListDataFlavor));
	}
	
	public void testProjectFileName() throws Exception
	{
		String projectFileName = project.getFilename();
		ORef diagramObjectRef = project.getDiagramObject().getRef();
		TransferableMiradiList miradiList = new TransferableMiradiList(project, diagramObjectRef);
		miradiList.storeData(new EAMGraphCell[0]);
		assertEquals("wrong project filename?", projectFileName, miradiList.getProjectFileName());
	}

	public void testGetTransfeData() throws Exception
	{
		DiagramModel model = project.getDiagramModel();
		
		Point node1Location = new Point(1,2);
		FactorCommandHelper commandHelper = new FactorCommandHelper(project, model);
		CommandCreateObject createCommand1 = commandHelper.createFactorAndDiagramFactor(ObjectType.CAUSE);
		ORef diagramFactorRef1 = createCommand1.getObjectRef();
		DiagramFactor diagramFactor1 = (DiagramFactor) project.findObject(diagramFactorRef1);
		diagramFactor1.setLocation(node1Location);
		
		Point node2Location = new Point(2,3);
		CommandCreateObject createCommand2 = commandHelper.createFactorAndDiagramFactor(ObjectType.STRATEGY);
		ORef diagramFactorRef2 = createCommand2.getObjectRef();
		DiagramFactor diagramFactor2 = (DiagramFactor) project.findObject(diagramFactorRef2);
		diagramFactor2.setLocation(node2Location);
		
		LinkCreator linkCreator = new LinkCreator(project);
		FactorLinkId factorLinkId = linkCreator.createFactorLinkAndAddToDiagramUsingCommands(project.getDiagramModel(), diagramFactor1, diagramFactor2);
		DiagramLink diagramLink = project.getDiagramModel().getDiagramLinkbyWrappedId(factorLinkId);
		
		FactorCell factorCell1 = model.getFactorCellByRef(diagramFactorRef1);
		FactorCell factorCell2 = model.getFactorCellByRef(diagramFactorRef2);
		EAMGraphCell dataCells[] = {factorCell1, factorCell2, model.findLinkCell(diagramLink)};
		
		ORef diagramObjectRef = project.getDiagramObject().getRef();
		TransferableMiradiList miradiList = new TransferableMiradiList(project, diagramObjectRef);
		miradiList.storeData(dataCells);
		TransferableMiradiList miradiTransferData = (TransferableMiradiList)miradiList.getTransferData(TransferableMiradiList.miradiListDataFlavor);
		assertNotNull(miradiTransferData);
		
		Vector<String> factorDeepCopies = miradiTransferData.getFactorDeepCopies();
		assertEquals(2, factorDeepCopies.size());
		
		EnhancedJsonObject factor1Json = new EnhancedJsonObject(factorDeepCopies.get(0));
		int factor1Type = factor1Json.getInt("Type");
		assertEquals("wrong type for factor 1?", ObjectType.CAUSE, factor1Type);
		assertEquals("wrong factor id?", diagramFactor1.getWrappedId(), factor1Json.getId(Factor.TAG_ID));
		
		EnhancedJsonObject factor2Json = new EnhancedJsonObject(factorDeepCopies.get(1));
		int factor2Type = factor2Json.getInt("Type");
		assertEquals("wrong type for factor 1?", ObjectType.STRATEGY, factor2Type);
		assertEquals("wrong factor id?", diagramFactor2.getWrappedId(), factor2Json.getId(Factor.TAG_ID));
		
		Vector<String> diagramFactorDeepCopies = miradiTransferData.getDiagramFactorDeepCopies();
		assertEquals(2, diagramFactorDeepCopies.size());
		EnhancedJsonObject diagramFactor1Json = new EnhancedJsonObject(diagramFactorDeepCopies.get(0));
		int diagramFactor1Type = diagramFactor1Json.getInt("Type");
		assertEquals("wrong type for diagram factor 1?", ObjectType.DIAGRAM_FACTOR, diagramFactor1Type);
		Point diagramFactor1Location = diagramFactor1Json.getPoint(DiagramFactor.TAG_LOCATION);
		assertEquals("wrong diagram factor location?", node1Location, diagramFactor1Location);
		ORef wrappedRef1 = diagramFactor1Json.getRef(DiagramFactor.TAG_WRAPPED_REF);
		assertEquals("wrong diagram factor wrapped ref?", diagramFactor1.getWrappedORef(), wrappedRef1);
		
		EnhancedJsonObject diagramFactor2Json = new EnhancedJsonObject(diagramFactorDeepCopies.get(1));
		int diagramFactor2Type = diagramFactor2Json.getInt("Type");
		assertEquals("wrong type for diagram factor 2?", ObjectType.DIAGRAM_FACTOR, diagramFactor2Type);
		Point diagramFactor2Location = diagramFactor2Json.getPoint(DiagramFactor.TAG_LOCATION);
		assertEquals("wrong diagram factor location?", node2Location, diagramFactor2Location);
		ORef wrappedRef2 = diagramFactor2Json.getRef(DiagramFactor.TAG_WRAPPED_REF);
		assertEquals("wrong diagram factor wrapped ref?", diagramFactor2.getWrappedORef(), wrappedRef2);
		
		Vector<String> factorLinkDeepCopies = miradiTransferData.getFactorLinkDeepCopies();
		assertEquals(1, factorLinkDeepCopies.size());
		EnhancedJsonObject factorLinkJson = new EnhancedJsonObject(factorLinkDeepCopies.get(0));
		BaseId factorLinkIdFromJson = factorLinkJson.getId(FactorLink.TAG_ID);
		assertEquals("wrong factor link id?", factorLinkId, factorLinkIdFromJson);
		ORef fromRef = factorLinkJson.getRef(FactorLink.TAG_FROM_REF);
		assertEquals("wrong factor link from ref?", diagramFactor1.getWrappedORef(), fromRef);
		ORef toRef = factorLinkJson.getRef(FactorLink.TAG_TO_REF);
		assertEquals("wrong factor link to ref?", diagramFactor2.getWrappedORef(), toRef);
				
		Vector<String> diagramLinkDeepCopies = miradiTransferData.getDiagramLinkDeepCopies();
		assertEquals(1, diagramLinkDeepCopies.size()); 
		EnhancedJsonObject diagramLinkJson = new EnhancedJsonObject(diagramLinkDeepCopies.get(0));
		BaseId diagramLinkIdFromJson = diagramLinkJson.getId(DiagramLink.TAG_ID);
		assertEquals("wrong diagram link id?", diagramLink.getId(), diagramLinkIdFromJson);
		BaseId fromDiagramFactorId = diagramLinkJson.getId(DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID);
		assertEquals("wrong diagram link from id?", diagramFactor1.getDiagramFactorId(), fromDiagramFactorId);
		BaseId toDiagramFactorId = diagramLinkJson.getId(DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID);
		assertEquals("wrong diagram link to id?", diagramFactor2.getDiagramFactorId(), toDiagramFactorId);	
	}
	
	ProjectForTesting project;
}
