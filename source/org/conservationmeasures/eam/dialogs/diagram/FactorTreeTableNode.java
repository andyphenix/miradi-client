/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.diagram;

import org.conservationmeasures.eam.dialogs.viability.KeyEcologicalAttributeIndicatorNode;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.TreeTableNode;

public class FactorTreeTableNode extends TreeTableNode
{
	public FactorTreeTableNode(Project projectToUse, ORef factorRef) throws Exception
	{
		project = projectToUse;
		factor = (Factor) project.findObject(factorRef);
		rebuild();
	}
	
	public BaseObject getObject()
	{
		return factor;
	}

	public TreeTableNode getChild(int index)
	{
		return indicators[index];
	}

	public int getChildCount()
	{
		return indicators.length;
	}

	public ORef getObjectReference()
	{
		return factor.getRef();
	}
	
	public int getType()
	{
		return factor.getType();
	}
	
	public boolean isAlwaysExpanded()
	{
		return true;
	}

	public Object getValueAt(int column)
	{
		return "";
	}

	public String toString()
	{
		return "";
	}
	
	public BaseId getId()
	{
		return factor.getId();
	}
	public void rebuild() throws Exception
	{
		ORefList indicatorRefs = factor.getIndicatorRefs();
		indicators = new KeyEcologicalAttributeIndicatorNode[indicatorRefs.size()];
		for (int i = 0; i < indicatorRefs.size(); ++i)
		{
			Indicator thisIndicator = (Indicator) project.findObject(indicatorRefs.get(i));
			indicators[i] = new KeyEcologicalAttributeIndicatorNode(project, this, thisIndicator);
		}
	}
	
	private KeyEcologicalAttributeIndicatorNode[] indicators;
	private Project project;
	private Factor factor;
}
