/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.viability;

import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.TreeTableNode;

public class KeyEcologicalAttributeIndicatorNode extends TreeTableNode
{
	public KeyEcologicalAttributeIndicatorNode(Project projectToUse, KeyEcologicalAttributeNode parent, Indicator indicatorToUse)
	{
		project = projectToUse;
		indicator = indicatorToUse;
		keyEcologicalAttributesNode = parent;
	}
	
	public BaseObject getObject()
	{
		return indicator;
	}

	public ORef getObjectReference()
	{
		return indicator.getRef();
	}
	
	public int getType()
	{
		return indicator.getType();
	}

	public String toString()
	{
		return indicator.toString();
	}

	public int getChildCount()
	{
		return 0;
	}

	public TreeTableNode getChild(int index)
	{
		return null;
	}
	
	public TreeTableNode getParentNode()
	{
		return keyEcologicalAttributesNode;
	}

	//TODO: this method could be pulled up to the supper
	public Object getValueAt(int column)
	{
		String tag = COLUMN_TAGS[column];
		return getObject().getData(tag);
	}

	public void rebuild() throws Exception
	{
	}
	
	public static final String[] COLUMN_TAGS = {
		Indicator.TAG_LABEL,
		Indicator.PSEUDO_TAG_MEASUREMENT_STATUS_VALUE, 
		Indicator.TAG_EMPTY,
		};
	
	Project project;
	Indicator indicator;
	KeyEcologicalAttributeNode keyEcologicalAttributesNode;
}