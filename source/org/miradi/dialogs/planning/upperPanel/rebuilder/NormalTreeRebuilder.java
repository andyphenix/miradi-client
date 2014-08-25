/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.planning.upperPanel.rebuilder;

import org.miradi.diagram.ChainWalker;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.FactorSet;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.Desire;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.Factor;
import org.miradi.objects.FutureStatus;
import org.miradi.objects.Indicator;
import org.miradi.objects.IntermediateResult;
import org.miradi.objects.Measurement;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.Strategy;
import org.miradi.objects.SubTarget;
import org.miradi.objects.Task;
import org.miradi.objects.ThreatReductionResult;
import org.miradi.project.Project;
import org.miradi.schemas.*;
import org.miradi.utils.CodeList;

public class NormalTreeRebuilder extends AbstractTreeRebuilder
{
	public NormalTreeRebuilder(Project projectToUse, PlanningTreeRowColumnProvider rowColumnProviderToUse)
	{
		super(projectToUse, rowColumnProviderToUse);
	}
	
	@Override
	public ORefList getChildRefs(ORef grandparentRef, ORef parentRef, DiagramObject diagram) throws Exception
	{
		final ORefList noChildren = new ORefList();
		if(ProjectMetadata.is(parentRef))
			return getChildrenOfProjectNode(parentRef);

		if(DiagramObject.isDiagramObject(parentRef))
			return getChildrenOfDiagramNode(parentRef);

		if(AbstractTarget.isAbstractTarget(parentRef))
			return getChildrenOfAbstractTarget(parentRef, diagram);

		if(Cause.is(parentRef))
			return getChildrenOfBasicFactor(parentRef, diagram);

		if(Strategy.is(parentRef))
			return getChildrenOfStrategy(grandparentRef, parentRef, diagram);

		if(ThreatReductionResult.is(parentRef))
			return getChildrenOfBasicFactor(parentRef, diagram);

		if(IntermediateResult.is(parentRef))
			return getChildrenOfBasicFactor(parentRef, diagram);

		if(Desire.isDesire(parentRef))
            return getChildrenOfDesire(parentRef, diagram);

		if(Indicator.is(parentRef))
			return getChildrenOfIndicator(parentRef, diagram);

		if(Task.is(parentRef))
			return getChildrenOfTask(parentRef, diagram);

		if(Measurement.is(parentRef))
			return noChildren;

		if(ResourceAssignment.is(parentRef))
			return noChildren;

		if(ExpenseAssignment.is(parentRef))
			return noChildren;

		if(SubTarget.is(parentRef))
			return noChildren;

		if (FutureStatus.is(parentRef))
			return noChildren;

		if(parentRef.isInvalid())
		{
			EAM.logDebug("NormalTreeRebuilder.getChildRefs called for INVALID, type=" + parentRef.getObjectType());
			return noChildren;
		}

		EAM.logDebug("Don't know how to get children of " + parentRef);
		return new ORefList();
	}
	
	private ORefList getChildrenOfProjectNode(ORef parentRef) throws Exception
	{
		ORefList childRefs = new ORefList();
		if(getRowColumnProvider().shouldIncludeConceptualModelPage())
		{
			ORefList conceptualModelRefs = getProject().getConceptualModelDiagramPool().getORefList();
			childRefs.addAll(conceptualModelRefs);
		}
		if(getRowColumnProvider().shouldIncludeResultsChain())
		{
			ORefList resultsChainRefs = getProject().getResultsChainDiagramPool().getORefList();
			childRefs.addAll(resultsChainRefs);
		}
		if(shouldTargetsBeAtSameLevelAsDiagrams())
		{
			childRefs.addAll(getProject().getTargetPool().getRefList());
			if(getProject().getMetadata().isHumanWelfareTargetMode())
				childRefs.addAll(getProject().getHumanWelfareTargetPool().getRefList());
		}
		return childRefs;
	}
	
	private ORefList getChildrenOfDiagramNode(ORef diagramRef) throws Exception
	{
		ORefList childRefs = new ORefList();
		DiagramObject diagramObject = DiagramObject.findDiagramObject(getProject(), diagramRef); 
		ORefList diagramFactorRefs = diagramObject.getAllDiagramFactorRefs();
		for(int i = 0; i < diagramFactorRefs.size(); ++i)
		{
			DiagramFactor diagramFactor = (DiagramFactor)getProject().findObject(diagramFactorRefs.get(i));
			Factor factor = diagramFactor.getWrappedFactor();
			if(shouldIncludeFactorWithinDiagram(factor))
			{
				childRefs.add(factor.getRef());
			}
		}
		
		childRefs.addAll(diagramObject.getAllObjectiveRefs());
		childRefs.addAll(diagramObject.getAllGoalRefs());
		
		return childRefs;
	}
	
	private ORefList getChildrenOfAbstractTarget(ORef targetRef, DiagramObject diagram) throws Exception
	{
		ORefList childRefs = new ORefList();
		AbstractTarget target = AbstractTarget.findTarget(getProject(), targetRef);
		childRefs.addAll(target.getSubTargetRefs());
		childRefs.addAll(target.getOwnedObjectRefs().getFilteredBy(GoalSchema.getObjectType()));
		childRefs.addAll(new ORefList(IndicatorSchema.getObjectType(), target.getDirectOrIndirectIndicators()));
		childRefs.addAll(getDirectlyLinkedNonDraftStrategies(target, diagram));
		
		return childRefs;
	}
	
	private ORefList getDirectlyLinkedNonDraftStrategies(AbstractTarget target, DiagramObject diagram)
	{
		if(diagram == null)
			return new ORefList();
		
		ORefList strategyRefs = new ORefList();
		ChainWalker chain = diagram.getDiagramChainWalker();
		DiagramFactor targetDiagramFactor = diagram.getDiagramFactor(target.getRef());
		FactorSet factors = chain.buildDirectlyLinkedUpstreamChainAndGetFactors(targetDiagramFactor);
		for(Factor factor : factors)
		{
			if(factor.isStrategy() && !factor.isStatusDraft())
				strategyRefs.add(factor.getRef());
		}
		
		return strategyRefs;
	}
	
	private ORefList getChildrenOfBasicFactor(ORef parentRef, DiagramObject diagram)
	{
		ORefList childRefs = new ORefList();
		Factor factor = Factor.findFactor(getProject(), parentRef);
		childRefs.addAll(factor.getObjectiveRefs());
		childRefs.addAll(factor.getDirectOrIndirectIndicatorRefs());
		return childRefs;
	}

	private ORefList getChildrenOfStrategy(ORef grandparentRef, ORef parentRef, DiagramObject diagram) throws Exception
	{
		ORefList childRefs = new ORefList();

        CodeList visibleRowTypes = getRowColumnProvider().getRowCodesToShow();
        boolean includeStrategies = visibleRowTypes.contains(StrategySchema.OBJECT_NAME);

        if (!includeStrategies && Desire.isDesire(grandparentRef))
            return childRefs;

		Strategy strategy = Strategy.find(getProject(), parentRef);
		childRefs.addAll(strategy.getActivityRefs());
		if (doStrategiesContainObjectives())
			childRefs.addAll(getRelevantObjectivesAndGoalsOnDiagram(diagram, parentRef));

		childRefs.addAll(strategy.getOwnedObjectRefs().getFilteredBy(IndicatorSchema.getObjectType()));
		return childRefs;
	}
	
	private ORefList getChildrenOfDesire(ORef parentRef, DiagramObject diagram) throws Exception
	{
		ORefList childRefs = new ORefList();
		Desire desire = Desire.findDesire(getProject(), parentRef);

        ORefList relevantStrategyAndActivityRefs = getRelevantStrategyAndActivityRefsInDiagram(diagram, desire);

        if (doObjectivesContainStrategies())
        {
            childRefs.addAll(relevantStrategyAndActivityRefs);
        }
        else
        {
            childRefs.addAll(relevantStrategyAndActivityRefs.getFilteredBy(TaskSchema.getObjectType()));
        }

		childRefs.addAll(getRelevantIndicatorsInDiagram(diagram, desire));
		return childRefs;
	}

	public ORefList getRelevantIndicatorsInDiagram(DiagramObject diagram, Desire desire) throws Exception
	{
		return keepObjectsThatAreInDiagram(diagram, desire.getRelevantIndicatorRefList());
	}

    public ORefList getRelevantStrategiesInDiagram(DiagramObject diagram, Desire desire) throws Exception
    {
        return keepObjectsThatAreInDiagram(diagram, desire.getRelevantStrategyRefs());
    }

    private ORefList getRelevantStrategyAndActivityRefsInDiagram(DiagramObject diagram, Desire desire) throws Exception
	{
        ORefList strategyAndActivityRefs = new ORefList();
        strategyAndActivityRefs.addAll(getRelevantStrategiesInDiagram(diagram, desire));
        strategyAndActivityRefs.addAll(desire.getRelevantActivityRefs());
        return strategyAndActivityRefs;
	}

    private ORefList getChildrenOfIndicator(ORef parentRef, DiagramObject diagram) throws Exception
	{
		ORefList childRefs = new ORefList();
		Indicator indicator = Indicator.find(getProject(), parentRef);
		childRefs.addAll(indicator.getMethodRefs());
		childRefs.addAll(indicator.getFutureStatusRefs());
		if(getRowColumnProvider().getRowCodesToShow().contains(MeasurementSchema.OBJECT_NAME))
		{
			childRefs.addAll(getSortedByDateMeasurementRefs(indicator));
		}
		
		return childRefs;
	}

	private ORefList getChildrenOfTask(ORef parentTaskRef, DiagramObject diagram) throws Exception
	{
		ORefList childRefs = new ORefList();

		Task parentTask = Task.find(getProject(), parentTaskRef);
		if(willThisTaskEndUpInTheTree(parentTask))
			childRefs.addAll(parentTask.getSubTaskRefs());
		
		return childRefs;
	}

	private boolean willThisTaskEndUpInTheTree(Task task) throws Exception
	{
		String taskTypeCode = task.getTypeName();
		CodeList visibleRowTypes = getRowColumnProvider().getRowCodesToShow();
		return visibleRowTypes.contains(taskTypeCode);
	}
	
	private boolean doStrategiesContainObjectives() throws Exception
	{
		return !doObjectivesContainStrategies();
	}

	private ORefList getRelevantObjectivesAndGoalsOnDiagram(DiagramObject diagram, ORef strategyRef) throws Exception
	{
		ORefList relevant = new ORefList();
		relevant.addAll(findRelevantObjectives(getProject(), strategyRef));
		relevant.addAll(findRelevantGoals(getProject(), strategyRef));
		
		return keepObjectsThatAreInDiagram(diagram, relevant);
	}
	
	private ORefList keepObjectsThatAreInDiagram(DiagramObject diagram, final ORefList baseObjectRefs)
	{
		if (diagram == null)
			return baseObjectRefs;
		
		ORefList itemRefsInDiagram = new ORefList();
		for(ORef candidateRef : baseObjectRefs)
		{
			BaseObject baseObject = BaseObject.find(getProject(), candidateRef);
			Factor factorOwner = baseObject.getDirectOrIndirectOwningFactor();
			if (diagram.containsWrappedFactorRef(factorOwner.getRef()))
				itemRefsInDiagram.add(candidateRef);
		}
		
		return itemRefsInDiagram;
	}
	
	private boolean doObjectivesContainStrategies() throws Exception
	{
		return getRowColumnProvider().doObjectivesContainStrategies();
	}
	
	private boolean shouldIncludeFactorWithinDiagram(Factor factor) throws Exception
	{
		if (AbstractTarget.isAbstractTarget(factor) && !shouldTargetsBeAtSameLevelAsDiagrams())
			return true;

		if (factor.isStrategy() && !factor.isStatusDraft())
			return true;
		
		if (factor.isDirectThreat())
			return true;
		
		if (factor.isContributingFactor())
			return true;
		
		if (factor.isThreatReductionResult())
			return true;
		
		if (factor.isIntermediateResult())
			return true;
		
		return false;
	}
}
