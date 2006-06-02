/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.annotations.GoalIds;
import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.martus.util.TestCaseEnhanced;

public class TestConceptualModelNode extends TestCaseEnhanced
{
	public TestConceptualModelNode(String name)
	{
		super(name);
	}
	
	public void testComments()
	{
		int id = 35;
		ConceptualModelFactor factor = new ConceptualModelFactor(id, DiagramNode.TYPE_DIRECT_THREAT);
		assertEquals("started with a comment?", "", factor.getComment());
		String sampleComment = "yowza";
		factor.setComment(sampleComment);
		assertEquals("couldn't getComment?", sampleComment, factor.getComment());
	}
	
	public void testPriority()
	{
		int id = 35;
		ConceptualModelFactor factor = new ConceptualModelFactor(id, DiagramNode.TYPE_DIRECT_THREAT);
		assertEquals("didn't default to priority none?", null, factor.getThreatPriority());
	}

	public void testJson() throws Exception
	{
		GoalIds goals = new GoalIds();
		goals.addId(2);
		goals.addId(5);
		
		ObjectiveIds objectives = new ObjectiveIds();
		objectives.addId(7);
		objectives.addId(9);

		int factorId = 2342;
		ConceptualModelFactor factor = new ConceptualModelFactor(factorId, DiagramNode.TYPE_DIRECT_THREAT);
		factor.setLabel("JustAName");
		factor.setComment("This is a great comment");
		factor.setIndicatorId(99);
		factor.setGoals(goals);
		factor.setObjectives(objectives);
		ConceptualModelFactor got = (ConceptualModelFactor)ConceptualModelNode.createFrom(factor.toJson());
		assertEquals("wrong type?", factor.getNodeType(), got.getNodeType());
		assertEquals("wrong id?", factor.getId(), got.getId());
		assertEquals("wrong name?", factor.getLabel(), got.getLabel());
		assertEquals("wrong comment?", factor.getComment(), got.getComment());
		assertEquals("wrong priority?", factor.getThreatPriority(), got.getThreatPriority());
		assertEquals("wrong indicator?", factor.getIndicatorId(), got.getIndicatorId());
		assertEquals("wrong goals count?", factor.getGoals().size(), got.getGoals().size());
		assertEquals("wrong goals?", factor.getGoals(), got.getGoals());
		assertEquals("wrong objectives count?", factor.getObjectives().size(), got.getObjectives().size());
		assertEquals("wrong objectives?", factor.getObjectives(), got.getObjectives());
	}
}
