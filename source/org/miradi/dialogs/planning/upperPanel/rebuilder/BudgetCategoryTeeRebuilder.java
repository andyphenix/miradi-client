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

import java.util.HashMap;

import org.miradi.dialogs.planning.AbstractBudgetCategoryRowColumnProvider;
import org.miradi.dialogs.planning.WorkPlanCategoryTreeRowColumnProvider;
import org.miradi.dialogs.planning.treenodes.NewAbstractPlanningTreeNode;
import org.miradi.dialogs.planning.treenodes.NewPlanningTreeBaseObjectNode;
import org.miradi.dialogs.planning.treenodes.UnspecifiedBaseObject;
import org.miradi.dialogs.planning.treenodes.UnspecifiedBaseObjectNode;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.Assignment;
import org.miradi.objects.BaseObject;
import org.miradi.objects.DiagramObject;
import org.miradi.project.Project;
import org.miradi.utils.CodeList;

public class BudgetCategoryTeeRebuilder extends AbstractTreeRebuilder
{
	public BudgetCategoryTeeRebuilder(Project projectToUse, AbstractBudgetCategoryRowColumnProvider rowColumnProviderToUse)
	{
		super(projectToUse, rowColumnProviderToUse);
	}
	
	@Override
	protected ORefList getChildRefs(ORef parentRef, DiagramObject diagram) throws Exception
	{
		return new ORefList();
	}
	
	@Override
	public void rebuildTree(NewAbstractPlanningTreeNode rootNode) throws Exception
	{
		rootNode.clearChildren();
		ORefList allAsignmentRefs = new ORefList();
		allAsignmentRefs.addAll(getProject().getAssignmentPool().getRefList());
		allAsignmentRefs.addAll(getProject().getExpenseAssignmentPool().getRefList());

		rebuild(rootNode, allAsignmentRefs);
	}
	
	public void rebuild(NewAbstractPlanningTreeNode rootNode, ORefList allAsignmentRefs) throws Exception
	{
		if (!getLevelObjectTypes().hasData())
			return;
		
		rootNode.addChild(new UnspecifiedBaseObjectNode(getProject(), rootNode, getChildType()));
		int typeOfChildren = getChildType();
		HashMap<ORef, ORefList> categoryRefToAssignmentRefsMap = createCategoryRefToAssignmentRefsMap(typeOfChildren);
		ORefList childRefs = getProject().getPool(typeOfChildren).getRefList();
		addUnspecifiedRowInPlace(childRefs);
		for (int index = 0; index < childRefs.size(); ++index)
		{	
			ORef possibleChildRef = childRefs.get(index);
			BaseObject possibleChildObject = createOrFindChildObject(possibleChildRef, typeOfChildren);
			ORefList assignmentRefsThatMatchPossibleChild = getAssignmentsReferringToRow(categoryRefToAssignmentRefsMap, possibleChildObject);
			ORefList assignmentRefsThatMatchPossibleChildHierarchy = assignmentRefsThatMatchPossibleChild.getOverlappingRefs(getAssignmentRefsThatMatchThisNodeHierarchy());
			if (shouldIncludeChildNode(assignmentRefsThatMatchPossibleChildHierarchy))
			{
				if (possibleChildRef.isValid())
					rootNode.addChild(new NewPlanningTreeBaseObjectNode(getProject(), rootNode, possibleChildRef));
			}
		}
	}
	
	private boolean shouldIncludeChildNode(ORefList overlappingAssignmentRefs)
	{
		if (overlappingAssignmentRefs.hasRefs())
			return true;
		
		return getCastedRowColumnProvider().shouldIncludeEmptyRows();
	}

	private ORefList getAssignmentsReferringToRow(HashMap<ORef, ORefList> categoryRefToAssignmentRefsMap, BaseObject possibleChildObject)
	{
		ORefList referringAssignments = categoryRefToAssignmentRefsMap.get(possibleChildObject.getRef());
		if (referringAssignments == null)
			return new ORefList();
		
		return referringAssignments;
	}

	private void addUnspecifiedRowInPlace(ORefList childRefs)
	{
		childRefs.add(ORef.INVALID);
	}

	private HashMap<ORef, ORefList> createCategoryRefToAssignmentRefsMap(int levelObjectType)
	{
		HashMap<ORef, ORefList> categoryRefToAssignmentRefsMap = new HashMap<ORef, ORefList>();
		for (int index = 0; index < getAssignmentRefsThatMatchThisNodeHierarchy().size(); ++index)
		{
			Assignment assignment = Assignment.findAssignment(getProject(), getAssignmentRefsThatMatchThisNodeHierarchy().get(index));
			ORefList refList = new ORefList(assignment);
			ORef categoryRef = assignment.getCategoryRef(levelObjectType);
			if (!categoryRefToAssignmentRefsMap.containsKey(categoryRef))
				categoryRefToAssignmentRefsMap.put(categoryRef, new ORefList());
				
			categoryRefToAssignmentRefsMap.get(categoryRef).addAll(refList);
		}
		
		return categoryRefToAssignmentRefsMap;
	}

	private BaseObject createOrFindChildObject(ORef ref, int levelObjectType)
	{
		if (ref.isValid())
			return BaseObject.find(getProject(), ref);
		
		String objectNameForType = getProject().getObjectManager().getInternalObjectTypeName(levelObjectType);
		return new UnspecifiedBaseObject(getProject().getObjectManager(), levelObjectType, objectNameForType);
	}

	private ORefList getAssignmentRefsThatMatchThisNodeHierarchy()
	{
		ORefList allAsignmentRefs = new ORefList();
		allAsignmentRefs.addAll(getProject().getAssignmentPool().getRefList());
		allAsignmentRefs.addAll(getProject().getExpenseAssignmentPool().getRefList());

		return allAsignmentRefs;
	}

	private CodeList getLevelObjectTypes() throws Exception
	{
		return getCastedRowColumnProvider().getLevelTypeCodes();
	}

	private WorkPlanCategoryTreeRowColumnProvider getCastedRowColumnProvider()
	{
		return ((WorkPlanCategoryTreeRowColumnProvider) getRowColumnProvider());
	}
	
	private int getChildType()
	{
		return ((AbstractBudgetCategoryRowColumnProvider)getRowColumnProvider()).getObjectType();
	}
}
