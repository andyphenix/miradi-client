/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning.treenodes;

import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Cause;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.Objective;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;

public class PlanningTreeDirectThreatNode extends AbstractPlanningTreeNode
{
	public PlanningTreeDirectThreatNode(Project projectToUse, DiagramObject diagramToUse, ORef threatRef) throws Exception
	{
		super(projectToUse);
		diagramObject = diagramToUse;
		threat = (Cause)project.findObject(threatRef);
		rebuild();
	}
	
	public void rebuild() throws Exception
	{
		DiagramObject diagram = diagramObject;
		createAndAddChildren(threat.getUpstreamObjectives(diagram), diagram);
		
		addMissingUpstreamNonDraftStrategies(diagram);
		addMissingUpstreamIndicators(diagram);
	}

	int[] getNodeSortOrder()
	{
		return new int[] {
				Strategy.getObjectType(),
				Indicator.getObjectType(),
				Objective.getObjectType(),
				Task.getObjectType(),
			};
	}

	public BaseObject getObject()
	{
		return threat;
	}

	DiagramObject diagramObject;
	Cause threat;
}
