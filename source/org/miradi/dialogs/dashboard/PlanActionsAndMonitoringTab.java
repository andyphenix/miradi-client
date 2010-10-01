/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.dashboard;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.BorderFactory;

import org.miradi.layout.TwoColumnPanel;
import org.miradi.main.EAM;
import org.miradi.objects.Dashboard;
import org.miradi.project.Project;

public class PlanActionsAndMonitoringTab extends AbstractDashboardTab
{
	public PlanActionsAndMonitoringTab(Project projectToUse) throws Exception
	{
		super(projectToUse);
	}

	@Override
	protected String getMainDescriptionFileName()
	{
		return "dashboard/2.html";
	}

	@Override
	protected TwoColumnPanel createLeftPanel()
	{
		TwoColumnPanel leftMainPanel = new TwoColumnPanel();
		leftMainPanel.setBorder(BorderFactory.createEtchedBorder(Color.red, Color.red));
		createHeaderRow(leftMainPanel, EAM.text("2. Plan Actions and Monitoring"), "", getMainDescriptionFileName());
		
		addDevelopStrategicPlanRow(leftMainPanel);
		
		return leftMainPanel;
	}

	private void addDevelopStrategicPlanRow(TwoColumnPanel leftMainPanel)
	{
		addSubHeaderRow(leftMainPanel, EAM.text("2A. Develop Strategic Plan"), DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME);
		
		createTargetsWithGoalRow(leftMainPanel);
		createGoalCountRow(leftMainPanel);
		createDraftStrategiesCountRow(leftMainPanel);
		createRankedDraftStrategiesCountRow(leftMainPanel);
		createStrategyCountRow(leftMainPanel);
		createStrategyWithTaxonomyCountRow(leftMainPanel);
		createResultsChainCountRow(leftMainPanel);
		createObjectivesCountRow(leftMainPanel);
		createFinalStrategicPlanRow(leftMainPanel);
	}

	private void createFinalStrategicPlanRow(TwoColumnPanel leftMainPanel)
	{
		String leftColumnTranslatedText = EAM.text("Finalize Strategic Plan");
		HashMap<String, String> tokenReplacementMap = new HashMap<String, String>();
		tokenReplacementMap.put("%objectivesRelevantToStrategiesPercentage", getDashboardData(Dashboard.PSEUDO_OBJECTIVES_RELEVANT_TO_STRATEGIES_PERCENTAGE));
		tokenReplacementMap.put("%strategiesIrrelevantToObjectivesCount", getDashboardData(Dashboard.PSEUDO_IRRELEVANT_STRATEGIES_TO_OBJECTIVES_COUNT));
		String rightColumnTranslatedText = EAM.substitute(EAM.text("%objectivesRelevantToStrategiesPercentage % of Objectives relevant to a Strategy. %strategiesIrrelevantToObjectivesCount Strategies that do not contribute to an Objective"), tokenReplacementMap);

		createDataRow(leftMainPanel, leftColumnTranslatedText, rightColumnTranslatedText, DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME);
	}

	private void createObjectivesCountRow(TwoColumnPanel leftMainPanel)
	{
		String leftColumnTranslatedText = EAM.text("Develop Objectives");
		HashMap<String, String> tokenReplacementMap = new HashMap<String, String>();
		tokenReplacementMap.put("%objectiveCount", getDashboardData(Dashboard.PSEUDO_OBJECTIVE_COUNT));
		tokenReplacementMap.put("%resultsChainCount", getDashboardData(Dashboard.PSEUDO_STRATEGY__WITH_TAXONOMY_COUNT));
		tokenReplacementMap.put("%resultsChainWithObjectiveCount", getDashboardData(Dashboard.PSEUDO_RESULTS_CHAIN_WITH_OBJECTIVE_COUNT));
		String rightColumnTranslatedText = EAM.substitute(EAM.text("%objectiveCount Objectives Created. %resultsChainWithObjectiveCount of %resultsChainCount RCs have at least 1 objective"), tokenReplacementMap);

		createDataRow(leftMainPanel, leftColumnTranslatedText, rightColumnTranslatedText, DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME);
	}

	private void createResultsChainCountRow(TwoColumnPanel leftMainPanel)
	{
		String leftColumnTranslatedText = EAM.text("Create Results Chains");
		HashMap<String, String> tokenReplacementMap = new HashMap<String, String>();
		tokenReplacementMap.put("%resultsChainCount", getDashboardData(Dashboard.PSEUDO_STRATEGY__WITH_TAXONOMY_COUNT));
		String rightColumnTranslatedText = EAM.substitute(EAM.text("%resultsChainCount Results Chains Created"), tokenReplacementMap);

		createDataRow(leftMainPanel, leftColumnTranslatedText, rightColumnTranslatedText, DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME);	
	}

	private void createStrategyWithTaxonomyCountRow(TwoColumnPanel leftMainPanel)
	{
		String leftColumnTranslatedText = EAM.text("Strategies with taxonomy assingments");
		HashMap<String, String> tokenReplacementMap = new HashMap<String, String>();
		tokenReplacementMap.put("%strategyWithTaxonomyCount", getDashboardData(Dashboard.PSEUDO_STRATEGY__WITH_TAXONOMY_COUNT));
		String rightColumnTranslatedText = EAM.substitute(EAM.text("%strategyWithTaxonomyCount with taxonomy assignments"), tokenReplacementMap);

		createDataRow(leftMainPanel, leftColumnTranslatedText, rightColumnTranslatedText, DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME);
	}

	private void createStrategyCountRow(TwoColumnPanel leftMainPanel)
	{
		String leftColumnTranslatedText = EAM.text("Create Strategies");
		HashMap<String, String> tokenReplacementMap = new HashMap<String, String>();
		tokenReplacementMap.put("%strategyCount", getDashboardData(Dashboard.PSEUDO_STRATEGY_COUNT));
		String rightColumnTranslatedText = EAM.substitute(EAM.text("%strategyCount Strategies Created"), tokenReplacementMap);

		createDataRow(leftMainPanel, leftColumnTranslatedText, rightColumnTranslatedText, DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME);
	}

	private void createRankedDraftStrategiesCountRow(TwoColumnPanel leftMainPanel)
	{
		String leftColumnTranslatedText = EAM.text("Rank Draft Strategies");
		HashMap<String, String> tokenReplacementMap = new HashMap<String, String>();
		tokenReplacementMap.put("%rankedDraftStrategiesCount", getDashboardData(Dashboard.PSEUDO_RANKED_DRAFT_STRATEGY_COUNT));
		String rightColumnTranslatedText = EAM.substitute(EAM.text("%rankedDraftStrategiesCount Draft Strategies Ranked"), tokenReplacementMap);

		createDataRow(leftMainPanel, leftColumnTranslatedText, rightColumnTranslatedText, DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME);
	}

	private void createDraftStrategiesCountRow(TwoColumnPanel leftMainPanel)
	{
		String leftColumnTranslatedText = EAM.text("Create Draft Strategies");
		HashMap<String, String> tokenReplacementMap = new HashMap<String, String>();
		tokenReplacementMap.put("%draftStrategiesCount", getDashboardData(Dashboard.PSEUDO_DRAFT_STRATEGY_COUNT));
		String rightColumnTranslatedText = EAM.substitute(EAM.text("%draftStrategiesCount Draft Strategies Created"), tokenReplacementMap);

		createDataRow(leftMainPanel, leftColumnTranslatedText, rightColumnTranslatedText, DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME);
	}

	private void createGoalCountRow(TwoColumnPanel leftMainPanel)
	{
		createDataRow(leftMainPanel, EAM.text("Total Goals Created:"), getDashboardData(Dashboard.PSEUDO_GOAL_COUNT), DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME);
	}

	protected void createTargetsWithGoalRow(TwoColumnPanel leftMainPanel)
	{
		String leftColumnTranslatedText = EAM.text("Develop Goals for Each Target:");
		HashMap<String, String> tokenReplacementMap = new HashMap<String, String>();
		tokenReplacementMap.put("%targetWithGoalCount", getDashboardData(Dashboard.PSEUDO_TARGET_COUNT));
		tokenReplacementMap.put("%targetCount", getDashboardData(Dashboard.PSEUDO_TARGET_COUNT));
		String rightColumnTranslatedText = EAM.substitute(EAM.text("%targetWithGoalCount of %targetCount Targets have Goals"), tokenReplacementMap);

		createDataRow(leftMainPanel, leftColumnTranslatedText, rightColumnTranslatedText, DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME);
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Plan Actions and Monitoring");
	}
	
	private static final String DEVELOP_STRATEGIC_PLAN_RIGHT_SIDE_FILENAME = "dashboard/2A.html";
}
