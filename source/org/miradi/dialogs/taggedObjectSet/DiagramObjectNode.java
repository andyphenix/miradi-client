/* 
Copyright 2005-2021, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.dialogs.taggedObjectSet;

import java.util.Arrays;

import org.miradi.dialogs.planning.upperPanel.rebuilder.FactorNodeSorter;
import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;

public class DiagramObjectNode extends TreeTableNode
{
	public DiagramObjectNode(DiagramObject currentDiagramObjectToUse, TreeTableNode parentNodeToUse) throws Exception
	{
		currentDiagramObject = currentDiagramObjectToUse;
		parentNode = parentNodeToUse;
		rebuild();
	}

	@Override
	public TreeTableNode getParentNode() throws Exception
	{
		return parentNode;
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
	public BaseObject getObject()
	{
		return currentDiagramObject;
	}

	@Override
	public ORef getObjectReference()
	{
		return getObject().getRef();
	}

	@Override
	public Object getValueAt(int column)
	{
		return currentDiagramObject.toString();
	}

	@Override
	public void rebuild() throws Exception
	{
		Factor[] allDiagramObjectFactors = currentDiagramObject.getAllWrappedFactors();
		children = new TreeTableNode[allDiagramObjectFactors.length];
		for (int index = 0; index < allDiagramObjectFactors.length; ++index)
		{
			FactorTreeTableNode factorNode = new FactorTreeTableNode(allDiagramObjectFactors[index], this);
			children[index] = factorNode;
		}
		
		Arrays.sort(children, new FactorNodeSorter());
	}
	
	@Override
	public String getNodeLabel()
	{
		return currentDiagramObject.toString();
	}
	
	private DiagramObject currentDiagramObject;
	private TreeTableNode parentNode;
	private TreeTableNode[] children;
}
