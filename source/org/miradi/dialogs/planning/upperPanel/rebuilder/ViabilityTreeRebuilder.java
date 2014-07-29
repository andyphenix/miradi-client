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

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.miradi.dialogs.planning.treenodes.AbstractPlanningTreeNode;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.objects.FutureStatus;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.objects.ProjectMetadata;
import org.miradi.project.Project;
import org.miradi.schemas.IndicatorSchema;

public class ViabilityTreeRebuilder extends AbstractTreeRebuilder
{
	public ViabilityTreeRebuilder(Project projectToUse,	PlanningTreeRowColumnProvider rowColumnProviderToUse)
	{
		super(projectToUse, rowColumnProviderToUse);
	}
	
	@Override
	protected ORefList getChildRefs(ORef grandparentRef, ORef parentRef, DiagramObject diagram) throws Exception
	{
		final ORefList noChildren = new ORefList();
		if(ProjectMetadata.is(parentRef))
			return getChildrenOfProjectNode(parentRef);
		
		if(AbstractTarget.isAbstractTarget(parentRef))
			return getChildrenOfAbstractTarget(parentRef, diagram);
		
		if (Factor.isFactor(parentRef))
			return getIndicatorChildren(parentRef);
		
		if(Indicator.is(parentRef))
			return getChildrenOfIndicator(parentRef, diagram);
		
		if(KeyEcologicalAttribute.is(parentRef))
			return getChildrenOfKea(parentRef);
		
		if(Measurement.is(parentRef))
			return noChildren;
		
		if (FutureStatus.is(parentRef))
			return noChildren;
		
		if(parentRef.isInvalid())
			throw new RuntimeException("Attempted to getChildRefs for null parent: " + parentRef.getObjectType());
		
		EAM.logDebug("Don't know how to get children of " + parentRef);
		return new ORefList();
	}
	
	private ORefList getIndicatorChildren(ORef parentRef)
	{
		Factor factor = Factor.findFactor(getProject(), parentRef);

		return factor.getDirectOrIndirectIndicatorRefs();
	}

	private ORefList getChildrenOfKea(ORef parentRef)
	{
		ORefList childRefs = new ORefList();
		KeyEcologicalAttribute kea = KeyEcologicalAttribute.find(getProject(), parentRef);
		childRefs.addAll(kea.getIndicatorRefs());
		
		return childRefs;
	}
	
	private ORefList getChildrenOfIndicator(ORef parentRef, DiagramObject diagram) throws Exception
	{
		ORefList childRefs = new ORefList();
		Indicator indicator = Indicator.find(getProject(), parentRef);
		childRefs.addAll(getSortedByDateMeasurementRefs(indicator));
		childRefs.addAll(indicator.getFutureStatusRefs());
		
		return childRefs;
	}
	
	private ORefList getChildrenOfAbstractTarget(ORef targetRef, DiagramObject diagram) throws Exception
	{
		ORefList childRefs = new ORefList();
		AbstractTarget target = AbstractTarget.findTarget(getProject(), targetRef);
		childRefs.addAll(new ORefList(IndicatorSchema.getObjectType(), target.getDirectOrIndirectIndicators()));
		
		if (target.isViabilityModeTNC()) 
			childRefs.addAll(getSortedKeaRefs(target));
		
		return childRefs;
	}
	
	private ORefList getSortedKeaRefs(AbstractTarget target) throws Exception
	{
		Project project = target.getProject();
		ORefList keaRefs = target.getKeyEcologicalAttributeRefs();
		Vector<KeyEcologicalAttribute> keyEcologicalAttributesVector = new Vector<KeyEcologicalAttribute>();
		for(int index = 0; index < keaRefs.size(); ++index)
		{
			KeyEcologicalAttribute kea = (KeyEcologicalAttribute)project.findObject(keaRefs.get(index));
			keyEcologicalAttributesVector.add(kea);
		}
		
		Collections.sort(keyEcologicalAttributesVector, new KeaComparator());
		
		return new ORefList(keyEcologicalAttributesVector);
	}
	
	private ORefList getChildrenOfProjectNode(ORef parentRef) throws Exception
	{
		ORefList childRefs = new ORefList();
		childRefs.addAll(getProject().getTargetPool().getRefList());
		if(getProject().getMetadata().isHumanWelfareTargetMode())
			childRefs.addAll(getProject().getHumanWelfareTargetPool().getRefList());

		return childRefs;
	}

	@Override
	protected void addChildrenOfNodeToList(Vector<AbstractPlanningTreeNode> destination, AbstractPlanningTreeNode otherNode)
	{
	}
	
	private class KeaComparator implements Comparator<KeyEcologicalAttribute>
	{
		public int compare(KeyEcologicalAttribute kea1, KeyEcologicalAttribute kea2)
		{
			String type1 =kea1.getKeyEcologicalAttributeType();
			String type2 =kea2.getKeyEcologicalAttributeType();
			Collator myCollator = Collator.getInstance();

			return myCollator.compare(type1, type2);
		}
	}
}
