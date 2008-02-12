package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.ids.FactorLinkId;
import org.miradi.objecthelpers.CreateFactorLinkParameter;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.FactorLink;
import org.miradi.project.ProjectForTesting;
import org.miradi.utils.EnhancedJsonObject;

public class TestFactorLink extends ObjectTestCase
{
	public TestFactorLink(String name) 
	{
		super(name);
	}

	public void testBasics() throws Exception
	{
		FactorLink linkageData = new FactorLink(getObjectManager(), id, nodeA.getWrappedORef(), nodeB.getWrappedORef());
		assertEquals("Id not the same?", id, linkageData.getId());
		assertEquals("From Node refs don't match", nodeA.getWrappedORef(), linkageData.getFromFactorRef());
		assertEquals("To Node refs don't match", nodeB.getWrappedORef(), linkageData.getToFactorRef());
	}

	public void testFields() throws Exception
	{
		CreateFactorLinkParameter extraInfo = new CreateFactorLinkParameter(nodeA.getWrappedORef(), nodeB.getWrappedORef());
		verifyFields(ObjectType.FACTOR_LINK, extraInfo);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
		nodeA = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
		nodeB = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
	}

	public void tearDown() throws Exception
	{
		project.close();
		project = null;
		super.tearDown();
	}

	public void testToJson() throws Exception
	{
		FactorLink original = new FactorLink(getObjectManager(), id, nodeA.getWrappedORef(), nodeB.getWrappedORef());
		EnhancedJsonObject json = original.toJson();
		FactorLink gotBack = (FactorLink)BaseObject.createFromJson(project.getObjectManager(), original.getType(), json);
		assertEquals("wrong id?", original.getId(), gotBack.getId());
		assertEquals("wrong from?", original.getFromFactorRef(), gotBack.getFromFactorRef());
		assertEquals("wrong to?", original.getToFactorRef(), gotBack.getToFactorRef());
	}
	
	public void testExtraInfo() throws Exception
	{
		BaseId diagramLinkId = project.createDiagramFactorLink(nodeA, nodeB);
		DiagramLink diagramLink = (DiagramLink) project.findObject(new ORef(ObjectType.DIAGRAM_LINK, diagramLinkId));
		FactorLink linkage = (FactorLink) project.findObject(new ORef(ObjectType.FACTOR_LINK, diagramLink.getWrappedId()));
		
		CreateFactorLinkParameter extraInfo = new CreateFactorLinkParameter(nodeA.getWrappedORef(), nodeB.getWrappedORef());
		CreateFactorLinkParameter gotExtraInfo = (CreateFactorLinkParameter)linkage.getCreationExtraInfo();
		assertEquals(extraInfo.getFromRef(), gotExtraInfo.getFromRef());
		assertEquals(extraInfo.getToRef(), gotExtraInfo.getToRef());
	}

	static final FactorLinkId id = new FactorLinkId(1);
	DiagramFactor nodeA;
	DiagramFactor nodeB;
	ProjectForTesting project;
}
