/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.dialogs.planning.treenodes;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.AccountingCode;
import org.miradi.objects.BudgetCategoryOne;
import org.miradi.objects.BudgetCategoryTwo;
import org.miradi.objects.Cause;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.ExpenseAssignment;
import org.miradi.objects.Factor;
import org.miradi.objects.FundingSource;
import org.miradi.objects.Goal;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.Indicator;
import org.miradi.objects.IntermediateResult;
import org.miradi.objects.Measurement;
import org.miradi.objects.Objective;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Strategy;
import org.miradi.objects.SubTarget;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.ThreatReductionResult;
import org.miradi.project.Project;
import org.miradi.utils.CodeList;

public abstract class AbstractPlanningTreeNode extends TreeTableNode
{
	public AbstractPlanningTreeNode(Project projectToUse, CodeList visibleRowsToUse)
	{
		project = projectToUse;
		visibleRows = visibleRowsToUse;
		children = new Vector<AbstractPlanningTreeNode>();
	}
	
	@Override
	public ORef getObjectReference()
	{
		if(getObject() == null)
			return ORef.createInvalidWithType(getType());
		
		return getObject().getRef();
	}
	
	public String getObjectTypeName()
	{
		if (getObject() != null)
			return getObject().getTypeName();
		
		EAM.logError("getObject() in getObjectTypeName is null");
		return "";
	}
	
	@Override
	public TreeTableNode getChild(int index)
	{
		return children.get(index);
	}

	@Override
	public int getChildCount()
	{
		return children.size();
	}
	
	protected void addProportionShares(TreeTableNode otherNode)
	{
	}

	@Override
	public Object getValueAt(int column)
	{
		return null;
	}
	
	@Override
	public boolean areBudgetValuesAllocated()
	{
		if(isAllocated)
			return true;
		
		if(getProportionShares() < getTotalShareCount())
			return true;
		
		return false;
	}

	@Override
	public void rebuild() throws Exception
	{
		throw new Exception("Can't call rebuild on " + getClass().getCanonicalName());
	}
	
	public void addChildren(Vector<AbstractPlanningTreeNode> nodesToAdd)
	{
		for(AbstractPlanningTreeNode node : nodesToAdd)
			addChild(node);
	}

	private void addChild(AbstractPlanningTreeNode node)
	{
		ORefSet existingGrandChildRefs = getGrandChildRefs();
		if(!existingGrandChildRefs.contains(node.getObjectReference()))
			children.add(node);
	}
	
	private ORefSet getGrandChildRefs()
	{
		ORefSet grandchildRefs = new ORefSet();
		for(int childIndex = 0; childIndex < getChildCount(); ++childIndex)
		{
			TreeTableNode child = getChild(childIndex);
			for(int grandchildIndex = 0; grandchildIndex < child.getChildCount(); ++grandchildIndex)
			{
				TreeTableNode grandchild = child.getChild(grandchildIndex);
				grandchildRefs.add(grandchild.getObjectReference());
			}
		}
		
		return grandchildRefs;
	}

	public void setVisibleRowCodes(CodeList visibleRowsToUse)
	{
		visibleRows = visibleRowsToUse;
		for (int i = 0; i < getChildCount(); ++i)
		{
			((AbstractPlanningTreeNode) getChild(i)).setVisibleRowCodes(visibleRowsToUse);
		}
	}
	
	@Override
	public String toRawString()
	{
		if (getObject() == null)
			return "";
		
		return getObject().getFullName();
	}

	public ORefSet getAllRefsInTree()
	{
		ORefSet refs = new ORefSet();
		for(int i = 0; i < children.size(); ++i)
		{
			AbstractPlanningTreeNode child = children.get(i);
			refs.addAll(child.getAllRefsInTree());
		}
		
		refs.add(getObjectReference());
		
		return refs;
	}
	
	protected void pruneUnwantedLayers(CodeList objectTypesToShow)
	{
		if(isAnyChildAllocated(children))
			isAllocated = true;
		
		Vector<AbstractPlanningTreeNode> newChildren = new Vector<AbstractPlanningTreeNode>();
		for(AbstractPlanningTreeNode child : children)
		{
			child.pruneUnwantedLayers(objectTypesToShow);
			
			boolean isChildVisible = objectTypesToShow.contains(child.getObjectTypeName());
			if(isChildVisible)
			{
				mergeChildIntoList(newChildren, child);
			}
			else
			{
				addChildrenOfNodeToList(newChildren, child);
			}
		}
		
		if(shouldSortChildren())
			Collections.sort(newChildren, createNodeSorter());
		children = newChildren;
	}
	
	private boolean isAnyChildAllocated(Vector<AbstractPlanningTreeNode> nodesToCheck)
	{
		for(AbstractPlanningTreeNode node : nodesToCheck)
		{
			if(node.areBudgetValuesAllocated())
				return true;
		}
		
		return false;
	}

	protected NodeSorter createNodeSorter()
	{
		return new NodeSorter();
	}

	public static void mergeChildIntoList(Vector<AbstractPlanningTreeNode> destination, AbstractPlanningTreeNode newChild)
	{
		AbstractPlanningTreeNode existingNode = findNodeWithRef(destination, newChild.getObjectReference());
		if(existingNode == null)
		{
			if (isChildOfAnyNodeInList(destination, newChild))
				return;

			destination.add(newChild);
			return;
		}
		
		destination = existingNode.getChildren();
		addChildrenOfNodeToList(destination, newChild);
		existingNode.addProportionShares(newChild);

		if(existingNode.shouldSortChildren())
			Collections.sort(destination, existingNode.createNodeSorter());
	}
	
	private static boolean isChildOfAnyNodeInList(Vector<AbstractPlanningTreeNode> destination, AbstractPlanningTreeNode newChild)
	{
		for(AbstractPlanningTreeNode parentNode : destination)
		{
			Vector<AbstractPlanningTreeNode> children = parentNode.getChildren();
			AbstractPlanningTreeNode foundMatchingChild = findNodeWithRef(children, newChild.getObjectReference());
			if (foundMatchingChild != null)
				return true;
		}
		
		return false;
	}
	
	private static void addChildrenOfNodeToList(Vector<AbstractPlanningTreeNode> destination, AbstractPlanningTreeNode otherNode)
	{
		for(AbstractPlanningTreeNode newChild : otherNode.getChildren())
			mergeChildIntoList(destination, newChild);
	}

	static AbstractPlanningTreeNode findNodeWithRef(Vector<AbstractPlanningTreeNode> list, ORef ref)
	{
		for(AbstractPlanningTreeNode node : list)
		{
			if(ref.equals(node.getObjectReference()))
				return node;
		}
		
		return null;
	}
	
	protected boolean shouldTargetsBeOnDiagramLevel()
	{
		return getProject().getMetadata().shouldPutTargetsAtTopLevelOfTree();
	}
	
	boolean shouldSortChildren()
	{
		return true;
	}

	public class NodeSorter implements Comparator<AbstractPlanningTreeNode>
	{
		public int compare(AbstractPlanningTreeNode nodeA, AbstractPlanningTreeNode nodeB)
		{

			int typeSortLocationA = getTypeSortLocation(nodeA.getType());
			int typeSortLocationB = getTypeSortLocation(nodeB.getType());
			int diff = typeSortLocationA - typeSortLocationB;
			if(diff != 0)
				return diff;

			ORef refA = nodeA.getObjectReference();
			ORef refB = nodeB.getObjectReference();
			if(refA.isValid() && refB.isInvalid())
				return -1;
			if(refA.isInvalid() && refB.isValid())
				return 1;
			
			String labelA = nodeA.toString();
			String labelB = nodeB.toString();
			return labelA.compareToIgnoreCase(labelB);
		}
		
		private int getTypeSortLocation(int type)
		{
			int[] sortOrder = getNodeSortOrder();
			
			for(int i = 0; i < sortOrder.length; ++i)
				if(type == sortOrder[i])
					return i;
			EAM.logError("NodeSorter unknown type: " + type + " in " + AbstractPlanningTreeNode.this.getClass());
			return sortOrder.length;
		}

	}
	
	protected int[] getNodeSortOrder()
	{
		return new int[] {
			Target.getObjectType(),
			HumanWelfareTarget.getObjectType(),
			ResultsChainDiagram.getObjectType(),
			ConceptualModelDiagram.getObjectType(),
			Goal.getObjectType(),
			SubTarget.getObjectType(),
			Cause.getObjectType(),
			ThreatReductionResult.getObjectType(),
			IntermediateResult.getObjectType(),
			Objective.getObjectType(),
			Strategy.getObjectType(),
			Indicator.getObjectType(),
			ProjectResource.getObjectType(),
			AccountingCode.getObjectType(),
			FundingSource.getObjectType(),
			BudgetCategoryOne.getObjectType(),
			BudgetCategoryTwo.getObjectType(),
			Task.getObjectType(),
			Measurement.getObjectType(),
			ResourceAssignment.getObjectType(),
			ExpenseAssignment.getObjectType(),
		};
	}
	
	private Vector<AbstractPlanningTreeNode> getChildren()
	{
		return children;
	}

	protected void addMissingChildren(ORefList potentialChildRefs, DiagramObject diagram) throws Exception
	{
		addMissingChildren(new ORefSet(potentialChildRefs), diagram);
	}
	
	protected void addMissingChildren(ORefSet potentialChildRefs, DiagramObject diagram) throws Exception
	{
		ORefSet everythingInTree = getAllRefsInTree();
		for(ORef ref : potentialChildRefs)
		{
			if(everythingInTree.contains(ref))
				continue;
			
			createAndAddChild(ref, diagram);			
		}
	}
	
	protected void createAndAddTaskNodes(ORefList taskRefs, ORef contextNodeRef) throws Exception
	{
		for(int i = 0; i < taskRefs.size(); ++i)
		{
			ORef taskRef = taskRefs.get(i);
			addChild(new PlanningTreeTaskNode(project, contextNodeRef, taskRef, visibleRows));
		}
	}
	
	protected Vector<AbstractPlanningTreeNode> buildExpenseAssignmentNodes(ORefList expenseAssignmentRefs) throws Exception
	{
		Vector<AbstractPlanningTreeNode> expenseAssignmentNodes = new Vector<AbstractPlanningTreeNode>();
		for (int index = 0; index < expenseAssignmentRefs.size(); ++index)
		{
			expenseAssignmentNodes.add(new PlanningTreeExpenseAssignmentNode(project, expenseAssignmentRefs.get(index), visibleRows));
		}
	
		return expenseAssignmentNodes;
	}

	protected Vector<AbstractPlanningTreeNode> buildResourceAssignmentNodes(ORefList assignmentRefs) throws Exception
	{
		Vector<AbstractPlanningTreeNode> resourceAssignmentNodes = new Vector<AbstractPlanningTreeNode>();
		for (int index = 0; index < assignmentRefs.size(); ++index)
		{
			resourceAssignmentNodes.add(new PlanningTreeResourceAssignmentNode(project, assignmentRefs.get(index), visibleRows));
		}
		return resourceAssignmentNodes;
	}
	
	protected void createAndAddChildren(ORefList refsToAdd, DiagramObject diagram) throws Exception
	{
		for(int i = 0; i < refsToAdd.size(); ++i)
			createAndAddChild(refsToAdd.get(i), diagram);
	}

	protected void createAndAddChild(ORef refToAdd, DiagramObject diagram) throws Exception
	{
		AbstractPlanningTreeNode childNode = createChildNode(refToAdd, diagram);
		addChild(childNode);
	}

	protected AbstractPlanningTreeNode createChildNode(ORef refToAdd, DiagramObject diagram) throws Exception
	{
		int type = refToAdd.getObjectType();
		try
		{
			if(type == ConceptualModelDiagram.getObjectType())
				return new PlanningTreeConceptualModelPageNode(project, refToAdd, visibleRows);
			if(type == ResultsChainDiagram.getObjectType())
				return new PlanningTreeResultsChainNode(project, refToAdd, visibleRows);
			if(AbstractTarget.isAbstractTarget(type))
				return new PlanningTreeTargetNode(project, diagram, refToAdd, visibleRows);
			if(type == Goal.getObjectType())
				return new PlanningTreeGoalNode(project, diagram, refToAdd, visibleRows);
			if(type == Objective.getObjectType())
				return new PlanningTreeObjectiveNode(project, diagram, refToAdd, visibleRows);
			if(type == Cause.getObjectType())
				return new PlanningTreeDirectThreatNode(project, diagram, refToAdd, visibleRows);
			if(type == ThreatReductionResult.getObjectType())
				return new PlanningTreeThreatReductionResultNode(project, diagram, refToAdd, visibleRows);
			if(type == IntermediateResult.getObjectType())
				return new PlanningTreeIntermediateResultsNode(project, diagram, refToAdd, visibleRows);
			if(type == Strategy.getObjectType())
				return new PlanningTreeStrategyNode(project, refToAdd, visibleRows);
			if(type == Indicator.getObjectType())
				return new PlanningTreeIndicatorNode(project, refToAdd, visibleRows);
			if (type == Measurement.getObjectType())
				return new PlanningTreeMeasurementNode(project, refToAdd, visibleRows);
			if (type == Task.getObjectType())
				throw new RuntimeException(EAM.text("This method is not responsible for creating task nodes."));
			if (type == ResourceAssignment.getObjectType())
				return new PlanningTreeResourceAssignmentNode(project, refToAdd, visibleRows);
			if (type == ExpenseAssignment.getObjectType())
				return new PlanningTreeExpenseAssignmentNode(project, refToAdd, visibleRows);
			if (SubTarget.is(type))
				return new SubTargetNode(project, refToAdd, visibleRows);
			
			throw new Exception("Attempted to create node of unknown type: " + refToAdd);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return new PlanningTreeErrorNode(getProject(), refToAdd);
		}
	}

	protected ORefSet extractNonDraftStrategyRefs(Factor[] factors)
	{
		ORefSet upstreamStrategyRefs = new ORefSet();
		for(int i = 0; i < factors.length; ++i)
		{
			Factor factor = factors[i];
			if(!factor.isStrategy())
				continue;
			
			if(factor.isStatusDraft())
				continue;
			
			upstreamStrategyRefs.add(factor.getRef());
		}
		
		return upstreamStrategyRefs;
	}

	protected ORefSet extractIndicatorRefs(Factor[] upstreamFactors)
	{
		ORefSet potentialChildIndicatorRefs = new ORefSet();
		for(int i = 0; i < upstreamFactors.length; ++i)
		{
			Factor factor = upstreamFactors[i];
			ORefList indicatorRefs = new ORefList(Indicator.getObjectType(), factor.getDirectOrIndirectIndicators());
			potentialChildIndicatorRefs.addAll(new ORefSet(indicatorRefs));
		}
		
		return potentialChildIndicatorRefs;
	}
	
	public CodeList getVisibleRows()
	{
		return visibleRows;
	}
	
	protected Project getProject()
	{
		return project;
	}

	protected Project project;
	protected CodeList visibleRows;
	protected Vector<AbstractPlanningTreeNode> children;
	private boolean isAllocated;
}
