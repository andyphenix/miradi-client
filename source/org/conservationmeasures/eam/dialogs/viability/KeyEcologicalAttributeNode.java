/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.viability;

import java.util.Vector;

import org.conservationmeasures.eam.dialogs.treetables.TreeTableNode;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.KeyEcologicalAttribute;
import org.conservationmeasures.eam.project.Project;

public class KeyEcologicalAttributeNode extends ViabilityTreeTableNode
{
	public KeyEcologicalAttributeNode(Project projectToUse, KeyEcologicalAttribute keaToUse) throws Exception
	{
		project = projectToUse;
		kea = keaToUse;
		rebuild();
	}
	
	public BaseObject getObject()
	{
		return kea;
	}

	public int getChildCount()
	{
		return indicators.length;
	}

	public TreeTableNode getChild(int index)
	{
		return indicators[index];
	}

	public ORef getObjectReference()
	{
		return kea.getRef();
	}
	
	public int getType()
	{
		return ObjectType.KEY_ECOLOGICAL_ATTRIBUTE;
	}

	public Object getValueAt(int column)
	{
		String tag = COLUMN_TAGS[column];
		if (tag.equals(KeyEcologicalAttribute.PSEUDO_TAG_VIABILITY_STATUS_VALUE))
			return kea.getViabilityStatusValue();
		
		return kea.getData(tag);
	}


	public String toString()
	{
		return (String)getValueAt(0);
	}
	
	public BaseId getId()
	{
		return kea.getId();
	}

	public void rebuild() throws Exception
	{
		int childCount = kea.getIndicatorIds().size();
		Vector indicatorVector = new Vector();
		for(int i = 0; i < childCount; ++i)
		{
			BaseId indicatorId = kea.getIndicatorIds().get(i);
			Indicator indicator = project.getIndicatorPool().find(indicatorId);
			indicatorVector.add(new ViabilityIndicatorNode(project, this, indicator));
		}
		indicators = (ViabilityIndicatorNode[])indicatorVector.toArray(new ViabilityIndicatorNode[0]);
	}
	
	public static final String[] COLUMN_TAGS = {
		KeyEcologicalAttribute.TAG_LABEL, 
		KeyEcologicalAttribute.TAG_EMPTY,
		KeyEcologicalAttribute.PSEUDO_TAG_VIABILITY_STATUS_VALUE, 
		KeyEcologicalAttribute.PSEUDO_TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE_VALUE,
		KeyEcologicalAttribute.TAG_EMPTY,
		KeyEcologicalAttribute.TAG_EMPTY,
		KeyEcologicalAttribute.TAG_EMPTY,
		KeyEcologicalAttribute.TAG_EMPTY,
		KeyEcologicalAttribute.TAG_EMPTY,
		KeyEcologicalAttribute.TAG_EMPTY,
		};
	
	private Project project;
	private KeyEcologicalAttribute kea;
	private ViabilityIndicatorNode[] indicators;
}

