/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.diagram.cells.DiagramStrategyCell;
import org.conservationmeasures.eam.diagram.cells.DiagramTargetCell;
import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Cause;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Target;
import org.conservationmeasures.eam.project.ProjectForTesting;

public class TestLayerManager extends EAMTestCase
{
	public TestLayerManager(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		
		project = new ProjectForTesting(getName());
		ORef targetRef = project.createObject(Target.getObjectType());
		cmTarget = (Target) Factor.findFactor(project, targetRef);
		cmTarget.setLabel("Target");
		ORef causeRef = project.createObject(Cause.getObjectType());
		cmFactor = (Cause) Factor.findFactor(project, causeRef);
		cmFactor.setLabel("Factor");
		ORef strategyRef = project.createObject(Strategy.getObjectType());
		cmIntervention = (Strategy) Factor.findFactor(project, strategyRef);
		cmIntervention.setLabel("Strategy");
		
		target = project.createFactorCell(ObjectType.TARGET);
		factor = project.createFactorCell(ObjectType.CAUSE);
		intervention = project.createFactorCell(ObjectType.STRATEGY);
	}
	
	public void tearDown() throws Exception
	{
		project.close();
		super.tearDown();
	}

	public void testDefaultAllVisible() throws Exception
	{
		LayerManager manager = new LayerManager();
		verifyVisibility("default visible", true, intervention, manager);
		verifyVisibility("default visible", true, factor, manager);
		verifyVisibility("default visible", true, target, manager);
		
		assertTrue("All layers not visible by default?", manager.areAllNodesVisible());
	}
	
	public void testHide() throws Exception
	{
		LayerManager manager = new LayerManager();
		manager.setVisibility(DiagramStrategyCell.class, false);
		
		DiagramFactor strategyDiagramFactor = getDiagramFactor(45);
		DiagramFactor targetDiagramFactor = getDiagramFactor(67);
		DiagramFactor targetDiagramFactor2 = getDiagramFactor(98);
		
		verifyVisibility("hidden type", false, new DiagramStrategyCell(cmIntervention, strategyDiagramFactor), manager);
		verifyVisibility("non-hidden type", true, new DiagramTargetCell(cmTarget, targetDiagramFactor), manager);
		assertFalse("All layers still visible?", manager.areAllNodesVisible());
		
		manager.setVisibility(DiagramStrategyCell.class, true);
		verifyVisibility("unhidden type", true, new DiagramTargetCell(cmTarget, targetDiagramFactor2), manager);
		assertTrue("All layers not visible again?", manager.areAllNodesVisible());
	}

	private DiagramFactor getDiagramFactor(int id)
	{
		final int SOME_RANDOM_NUMBER = 5;
		DiagramFactorId diagramFactorId = new DiagramFactorId(id + SOME_RANDOM_NUMBER);
		FactorId strategyFactorId = new FactorId(id);
		CreateDiagramFactorParameter strategyExtraInfo = new CreateDiagramFactorParameter(new ORef(ObjectType.STRATEGY, strategyFactorId));
		DiagramFactor strategyDiagramFactor = new DiagramFactor(diagramFactorId, strategyExtraInfo);
		
		return strategyDiagramFactor;
	}
	
	public void testHideIds() throws Exception
	{
		LayerManager manager = new LayerManager();
		assertTrue("all nodes not visible to start?", manager.areAllNodesVisible());
		ORefList ORefsToHide = new ORefList();
		ORefsToHide.add(target.getWrappedORef());
		ORefsToHide.add(factor.getWrappedORef());
		manager.setHiddenORefs(ORefsToHide);
		assertFalse("thinks all nodes are visible?", manager.areAllNodesVisible());
		for(int i = 0; i < ORefsToHide.size(); ++i)
		{
			verifyNodeVisibility("hide ids", false, target, manager);
			verifyNodeVisibility("hide ids", false, factor, manager);
			verifyNodeVisibility("hide ids", true, intervention, manager);
		}
	}
	
	public void testGoals() throws Exception
	{
		LayerManager manager = new LayerManager();
		assertTrue("Goals not visible by default?", manager.areGoalsVisible());
		manager.setGoalsVisible(false);
		assertFalse("Didn't set invisible?", manager.areGoalsVisible());
		manager.setGoalsVisible(true);
		assertTrue("Didn't set visible?", manager.areGoalsVisible());
	}
	
	
	public void testObjectives() throws Exception
	{
		LayerManager manager = new LayerManager();
		assertTrue("Objectives not visible by default?", manager.areObjectivesVisible());
		manager.setObjectivesVisible(false);
		assertFalse("Didn't set invisible?", manager.areObjectivesVisible());
		manager.setObjectivesVisible(true);
		assertTrue("Didn't set visible?", manager.areObjectivesVisible());
	}

	public void testIndicators() throws Exception
	{
		LayerManager manager = new LayerManager();
		assertTrue("Indicators not visible by default?", manager.areIndicatorsVisible());
		manager.setIndicatorsVisible(false);
		assertFalse("Didn't set invisible?", manager.areIndicatorsVisible());
		manager.setIndicatorsVisible(true);
		assertTrue("Didn't set visible?", manager.areIndicatorsVisible());
	}

	private void verifyVisibility(String text, boolean expected, FactorCell node, LayerManager manager)
	{
		assertEquals("type: " + text + " (" + node + ") ",expected, manager.isTypeVisible(node.getClass()));
		verifyNodeVisibility(text, expected, node, manager);
	}

	private void verifyNodeVisibility(String text, boolean expected, FactorCell node, LayerManager manager)
	{
		assertEquals("node: " + text + " (" + node.getLabel() + ") ",expected, manager.isVisible(project.getDiagramObject(), node));
	}
	
	private Target cmTarget;
	private Cause cmFactor;
	private Strategy cmIntervention;
	
	private ProjectForTesting project;
	private FactorCell target;
	private FactorCell factor;
	private FactorCell intervention;
}
