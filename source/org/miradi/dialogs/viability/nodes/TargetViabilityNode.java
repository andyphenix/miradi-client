/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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
package org.miradi.dialogs.viability.nodes;

import java.util.Arrays;
import java.util.Vector;

import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Target;
import org.miradi.project.Project;
import org.miradi.questions.EmptyChoiceItem;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.ViabilityModeQuestion;
import org.miradi.schemas.KeyEcologicalAttributeSchema;

public class TargetViabilityNode extends TreeTableNode
{
	public TargetViabilityNode(Project projectToUse, ORef targetRef) throws Exception
	{
		project = projectToUse;
		target =  AbstractTarget.findTarget(project, targetRef);
		rebuild();
	}
	
	@Override
	public BaseObject getObject()
	{
		return target;
	}

	@Override
	public TreeTableNode getChild(int index)
	{
		return children[index];
	}

	@Override
	public int getChildCount()
	{
		return children.length;
	}

	@Override
	public ORef getObjectReference()
	{
		return target.getRef();
	}
	
	@Override
	public int getType()
	{
		return target.getType();
	}

	@Override
	public Object getValueAt(int column)
	{
		String tag = COLUMN_TAGS[column];
		String rawValue = target.getData(tag);

		if(tag.equals(Target.TAG_VIABILITY_MODE))
			return new ViabilityModeQuestion().findChoiceByCode(rawValue);
		
		if (tag.equals(Target.PSEUDO_TAG_TARGET_VIABILITY))
			return new StatusQuestion().findChoiceByCode(rawValue);
		
		if(tag.equals(Target.TAG_EMPTY))
			return new EmptyChoiceItem();
		
		return rawValue;
	}

	@Override
	public String getNodeLabel()
	{
		return target.getLabel();
	}
	
	public BaseId getId()
	{
		return target.getId();
	}
	
	@Override
	public void rebuild() throws Exception
	{
		children = buildChildrenNodes();
	}

	private TreeTableNode[] buildChildrenNodes() throws Exception
	{
		if (target.isViabilityModeTNC())
			return getKeaNodes(target);
		
		return getTargetIndicatorNodes();
	}

	private ViabilityIndicatorNode[] getTargetIndicatorNodes() throws Exception
	{
		Vector<ViabilityIndicatorNode> viabilityIndicatorNodes = new Vector<ViabilityIndicatorNode>();
		ORefList indicatorRefs = target.getOnlyDirectIndicatorRefs();
		for (int index = 0; index < indicatorRefs.size(); ++index)
		{
			Indicator indicator = Indicator.find(getProject(), indicatorRefs.get(index));
			ViabilityIndicatorNode viabilityIndicatorNode = new ViabilityIndicatorNode(getProject(), this, indicator);
			viabilityIndicatorNodes.add(viabilityIndicatorNode);
		}
		
		return viabilityIndicatorNodes.toArray(new ViabilityIndicatorNode[0]);
	}

	static public KeyEcologicalAttributeNode[] getKeaNodes(AbstractTarget target) throws Exception
	{
		if (!target.isViabilityModeTNC())
			return new KeyEcologicalAttributeNode[0];
		
		Project project = target.getProject();
		IdList keas = target.getKeyEcologicalAttributes();
		Vector<KeyEcologicalAttributeNode> keyEcologicalAttributesVector = new Vector<KeyEcologicalAttributeNode>();
		for(int i = 0; i < keas.size(); ++i)
		{
			KeyEcologicalAttribute kea = (KeyEcologicalAttribute)project.findObject(new ORef(KeyEcologicalAttributeSchema.getObjectType(),keas.get(i)));
			keyEcologicalAttributesVector.add(new KeyEcologicalAttributeNode(project, kea));
		}
		
		KeyEcologicalAttributeNode[] keaNodes = keyEcologicalAttributesVector.toArray(new KeyEcologicalAttributeNode[0]);
		sortObjectList(keaNodes, new KeaNodeComparator());
		return keaNodes;
	}
	
	static public void sortObjectList(KeyEcologicalAttributeNode[] objectList, KeaNodeComparator comparator)
	{
		Arrays.sort(objectList, comparator);
	}
	
	private Project getProject()
	{
		return project;
	}
	
	public static final String[] COLUMN_TAGS = {
		Target.TAG_EMPTY, 
		Target.TAG_VIABILITY_MODE, 
		Target.PSEUDO_TAG_TARGET_VIABILITY,
		Target.TAG_EMPTY,
		Target.TAG_EMPTY,
		Target.TAG_EMPTY,
		Target.TAG_EMPTY,
		Target.TAG_EMPTY,
		Target.TAG_EMPTY,
		Target.TAG_EMPTY,
		};
	
	private Project project;
	private AbstractTarget target;
	private TreeTableNode[] children;
}
