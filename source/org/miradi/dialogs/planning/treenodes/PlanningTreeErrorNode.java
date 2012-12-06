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

package org.miradi.dialogs.planning.treenodes;

import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;

public class PlanningTreeErrorNode extends AbstractPlanningTreeNode
{
	public PlanningTreeErrorNode(Project project, TreeTableNode parentNodeToUse, ORef refToAdd)
	{
		super(project, parentNodeToUse);
		
		ref = refToAdd;
	}

	@Override
	public BaseObject getObject()
	{
		return BaseObject.find(getProject(), getObjectReference());
	}
	
	@Override
	public ORef getObjectReference()
	{
		return ref;
	}
	
	@Override
	public int getType()
	{
		return getObjectReference().getObjectType();
	}
	
	@Override
	public String getNodeLabel()
	{
		return EAM.substitute(EAM.text("Error Creating: %s"), getObject().getLabel()) ;
	}
	
	@Override
	public void rebuild() throws Exception
	{
		// NOTE: Avoid a call to super rebuild because it always throws
	}
	
	private ORef ref;
}
