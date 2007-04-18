package org.conservationmeasures.eam.reports;

import java.util.Vector;

import net.sf.jasperreports.engine.JRDataSource;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Goal;
import org.conservationmeasures.eam.views.strategicplan.StratPlanGoal;
import org.conservationmeasures.eam.views.strategicplan.StratPlanObjective;

public class RelatedFactorsToThisGoalDataSource extends CommonDataSource
{
	public RelatedFactorsToThisGoalDataSource(Goal goal)
	{
		super(goal.getObjectManager().getProject());
		try
		{
			ORefList list = new ORefList();
			Vector objectiveVector = StratPlanGoal.getObjectiveNodes(project, goal);
			for (int i=0; i<objectiveVector.size(); ++i)
			{
				BaseObject ss = ((StratPlanObjective)objectiveVector.get(i)).getObject();
				list.add(ss.getRef());
			}

			setObjectList(list);
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}
	
	public JRDataSource getRelatedObjectDataSource(int relatedObjectTypeToFind)
	{
		return new RelatedObjectDataSource(getCurrentObject(), relatedObjectTypeToFind);
	}
} 
