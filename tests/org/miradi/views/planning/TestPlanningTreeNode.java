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
package org.miradi.views.planning;

import org.miradi.dialogs.planning.treenodes.AbstractProjectNode;
import org.miradi.dialogs.planning.treenodes.HiddenConfigurableProjectRootNode;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.EAMTestCase;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.Goal;
import org.miradi.objects.Indicator;
import org.miradi.objects.Objective;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ResourceAssignment;
import org.miradi.objects.Strategy;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.project.ProjectForTesting;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.CustomPlanningRowsQuestion;

abstract public class TestPlanningTreeNode extends EAMTestCase
{
	public TestPlanningTreeNode(String name)
	{
		super(name);
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
		setupFactors();
	}

	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
		project = null;
	}
	
	private void setupFactors() throws Exception
	{
		projectMetadata = project.getMetadata();
		diagramStrategy1 = project.createDiagramFactorAndAddToDiagram(Strategy.getObjectType());		
		diagramStrategy2 = project.createDiagramFactorAndAddToDiagram(Strategy.getObjectType());		
		diagramCause = project.createDiagramFactorAndAddToDiagram(Cause.getObjectType());
		diagramTarget = project.createDiagramFactorAndAddToDiagram(Target.getObjectType());
		
		stratToCauseLinkId = project.createDiagramLinkAndAddToDiagram(diagramStrategy1, diagramCause).getObjectId();		
		causeToTargetLinkId = project.createDiagramLinkAndAddToDiagram(diagramCause, diagramTarget).getObjectId();
		
		objectiveId = project.addItemToObjectiveList(diagramCause.getWrappedORef(), Cause.TAG_OBJECTIVE_IDS);
		indicatorId = project.addItemToIndicatorList(diagramCause.getWrappedORef(), Cause.TAG_INDICATOR_IDS);
		goalId = project.addItemToGoalList(diagramTarget.getWrappedORef(), Target.TAG_GOAL_IDS);
		taskId = project.addItemToIndicatorList(indicatorId, Task.getObjectType(), Indicator.TAG_METHOD_IDS);
		activityId = project.addActivityToStrateyList(diagramStrategy1.getWrappedORef(), Strategy.TAG_ACTIVITY_IDS);
		subtaskId = project.addSubtaskToActivity(getTask().getRef(), Task.TAG_SUBTASK_IDS);
		
		IdList activityIds = new IdList(Task.getObjectType(), new BaseId[] {activityId});
		project.setObjectData(diagramStrategy2.getWrappedORef(), Strategy.TAG_ACTIVITY_IDS, activityIds.toString());
		
		strategyResourceAssignmentRef = project.addResourceAssignment(getStrategy(), 1, 2001, 2001).getRef();
		indicatorResourceAssignmentRef = project.addResourceAssignment(getIndicator(), 2, 2002, 2002).getRef();
		subtaskResourceAssignmentRef = project.addResourceAssignment(getSubtask(), 4, 2004, 2004).getRef();
	}
	
	public AbstractProjectNode createCompleteTree() throws Exception
	{
		ChoiceQuestion rowChoiceQuestion= new CustomPlanningRowsQuestion(project);
		HiddenConfigurableProjectRootNode root = new HiddenConfigurableProjectRootNode(project, rowChoiceQuestion.getAllCodes());
		return root;
	}
	
	public Goal getGoal()
	{
		return (Goal) project.findObject(new ORef(Goal.getObjectType(), goalId));
	}
	
	public Objective getObjective()
	{
		return (Objective) project.findObject(new ORef(Objective.getObjectType(), objectiveId));
	}
	
	public Strategy getStrategy()
	{
		return (Strategy) project.findObject(diagramStrategy1.getWrappedORef());
	}
	
	public Strategy getStrategy2()
	{
		return (Strategy) project.findObject(diagramStrategy2.getWrappedORef());
	}
	
	public Task getActivity()
	{
		return Task.find(project, new ORef(Task.getObjectType(), activityId));
	}
	
	public Indicator getIndicator()
	{
		return (Indicator) project.findObject(new ORef(Indicator.getObjectType(), indicatorId));
	}
	
	public Task getTask()
	{
		return (Task) project.findObject(new ORef(Task.getObjectType(), taskId));
	}
	
	public Task getSubtask()
	{
		return (Task) project.findObject(new ORef(Task.getObjectType(), subtaskId));
	}
	
	public Target getTarget()
	{
		return (Target) project.findObject(diagramTarget.getWrappedORef());
	}
	
	public Cause getThreat()
	{
		return (Cause) project.findObject(diagramCause.getWrappedORef());
	}
	
	public ProjectMetadata getProjectMetadata()
	{
		return projectMetadata;
	}
	
	public ResourceAssignment getStrategyResourceAssignment()
	{
		return ResourceAssignment.find(project, strategyResourceAssignmentRef);
	}
	
	public ResourceAssignment getIndicatorResourceAssignment()
	{
		return ResourceAssignment.find(project, indicatorResourceAssignmentRef);
	}
	
	public ResourceAssignment getSubtaskResourceAssignment()
	{
		return ResourceAssignment.find(project, subtaskResourceAssignmentRef);
	}
	
	public ProjectForTesting getProject()
	{
		return project;
	}
	
	ProjectForTesting project;
	ProjectMetadata projectMetadata;
	DiagramFactor diagramStrategy1;		
	DiagramFactor diagramStrategy2;		
	DiagramFactor diagramCause;
	DiagramFactor diagramTarget;
	
	BaseId stratToCauseLinkId;
	BaseId causeToTargetLinkId;
	
	BaseId indicatorId;
	BaseId objectiveId;
	BaseId goalId;
	BaseId taskId;
	BaseId activityId;
	BaseId subtaskId;
	
	private ORef strategyResourceAssignmentRef;
	private ORef indicatorResourceAssignmentRef;
	private ORef subtaskResourceAssignmentRef;
}
