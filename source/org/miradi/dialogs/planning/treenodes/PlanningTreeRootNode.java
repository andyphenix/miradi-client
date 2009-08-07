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

import java.util.Vector;

import org.miradi.icons.MiradiApplicationIcon;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.utils.CodeList;

public class PlanningTreeRootNode extends AbstractPlanningTreeNode
{
	public PlanningTreeRootNode(Project projectToUse, CodeList visibleRowsToUse) throws Exception
	{
		super(projectToUse, visibleRowsToUse);
		rebuild();
	}
	
	@Override
	public int getType()
	{
		return ProjectMetadata.getObjectType();
	}

	public BaseObject getObject()
	{
		return project.getMetadata();
	}

	@Override
	public Object getValueAt(int column)
	{
		if (column == 0)
			return getObject().toString();
		
		return new ChoiceItem("", "", new MiradiApplicationIcon());
	}

	public boolean attemptToAdd(ORef refToAdd)
	{
		return false;
	}

	@Override
	public void rebuild() throws Exception
	{
		children = new Vector();

		boolean isResultsChainVisible = visibleRows.contains(ResultsChainDiagram.OBJECT_NAME);
		boolean isConceptualModelVisible = visibleRows.contains(ConceptualModelDiagram.OBJECT_NAME);
		
		boolean includeResultsChainItems = isResultsChainVisible || !isConceptualModelVisible;
		boolean includeConceptualModelItems = isConceptualModelVisible || !isResultsChainVisible;
		
		if(includeResultsChainItems)
			addResultsChainDiagrams();
		if(includeConceptualModelItems)
			addConceptualModel();
		
		pruneUnwantedLayers(visibleRows);
	}
	
	@Override
	public String toRawString()
	{
		return getProject().getFilename();
	}

	private void addConceptualModel() throws Exception
	{
		ORefList conceptualModelRefs = project.getConceptualModelDiagramPool().getORefList();
		createAndAddChildren(conceptualModelRefs, null);
	}

	private void addResultsChainDiagrams() throws Exception
	{
		ORefList resultsChainRefs = project.getResultsChainDiagramPool().getORefList();
		createAndAddChildren(resultsChainRefs, null);
	}
}
