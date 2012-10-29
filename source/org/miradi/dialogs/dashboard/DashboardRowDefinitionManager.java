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

import java.util.Vector;

import org.miradi.main.EAM;
import org.miradi.objects.Dashboard;
import org.miradi.questions.OpenStandardsAnalyzeUseAndAdaptQuestion;
import org.miradi.questions.OpenStandardsCaptureAndShareLearningQuestion;
import org.miradi.questions.OpenStandardsConceptualizeQuestion;
import org.miradi.questions.OpenStandardsImplementActionsAndMonitoringQuestion;
import org.miradi.questions.OpenStandardsPlanActionsAndMonitoringQuestion;
import org.miradi.utils.CodeList;

public class DashboardRowDefinitionManager
{
	public DashboardRowDefinitionManager()
	{
		allCodes = new CodeList();
		allCodes.addAll(new OpenStandardsConceptualizeQuestion().getAllCodes());
		allCodes.addAll(new OpenStandardsPlanActionsAndMonitoringQuestion().getAllCodes());
		allCodes.addAll(new OpenStandardsImplementActionsAndMonitoringQuestion().getAllCodes());
		allCodes.addAll(new OpenStandardsAnalyzeUseAndAdaptQuestion().getAllCodes());
		allCodes.addAll(new OpenStandardsCaptureAndShareLearningQuestion().getAllCodes());
	}
	
	public CodeList getThirdLevelCodes()
	{
		return allCodes;
	}
	
	public Vector<DashboardRowDefinition> getRowDefinitions(String code) throws Exception
	{	
		Vector<DashboardRowDefinition> rowDefinitions = new Vector<DashboardRowDefinition>();
		rowDefinitions.addAll(addConceptualizeRowDefinitions(code));
		rowDefinitions.addAll(addPlanActionsAndMonitoringDefinitions(code));
		rowDefinitions.addAll(addImplementActionsAndMonitoringDefinitions(code));
		rowDefinitions.addAll(addAnalyzeUseAndAdaptDefinitions(code));
		
		return rowDefinitions;
	}

	private Vector<DashboardRowDefinition> addImplementActionsAndMonitoringDefinitions(String code)
	{
		Vector<DashboardRowDefinition> rowDefinitions = new Vector<DashboardRowDefinition>();
		
		if (code.equals(OpenStandardsImplementActionsAndMonitoringQuestion.DETAIL_ACTIVITIES_TASKS_AND_RESPONSIBILITIES_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 of %2 Strategies have at least 1 Activity."), Dashboard.PSEUDO_STRATEGIES_WITH_ACTIVITIES_COUNT, Dashboard.PSEUDO_STRATEGY_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Total activities created"), Dashboard.PSEUDO_ACTIVITIES_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%2 of %1 Activities and tasks have assignments"), Dashboard.PSEUDO_ACTIVITIES_AND_TASKS_COUNT, Dashboard.PSEUDO_ACTIVITIES_AND_TASKS_WITH_ASSIGNMENTS_COUNT));
		}
	
		if (code.equals(OpenStandardsImplementActionsAndMonitoringQuestion.DETAIL_METHODS_TASKS_AND_RESPONSIBILITIES_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 of %2 Indicators have Methods."), Dashboard.PSEUDO_INDICATORS_WITH_METHODS_COUNT, Dashboard.PSEUDO_INDICATORS_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Total methods created"), Dashboard.PSEUDO_METHODS_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 of %2 Methods and Tasks have assignments"), Dashboard.PSEUDO_METHODS_AND_TASKS_WITH_ASSIGNMENT_COUNT, Dashboard.PSEUDO_METHODS_AND_TASKS_COUNT));
		}
		
		if (code.equals(OpenStandardsImplementActionsAndMonitoringQuestion.DEVELOP_PROJECT_TIMELINE_OR_CALENDAR_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 - %2"), Dashboard.PSEUDO_WORK_PLAN_START_DATE, Dashboard.PSEUDO_WORK_PLAN_END_DATE));
		}
		
		if (code.equals(OpenStandardsImplementActionsAndMonitoringQuestion.ESTIMATE_COSTS_FOR_ACTIVITIES_AND_MONITORING_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("Total Action Budget: %1 %2"), Dashboard.PSEUDO_CURRENCY_SYMBOL, Dashboard.PSEUDO_TOTAL_ACTION_BUDGET));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("Total Monitoring Budget %1 %2"), Dashboard.PSEUDO_CURRENCY_SYMBOL, Dashboard.PSEUDO_TOTAL_MONITORING_BUDGET));
		}
		
		if (code.equals(OpenStandardsImplementActionsAndMonitoringQuestion.DEVELOP_AND_SUBMIT_FUNDING_PROPOSALS_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("Total Budget for Funding: %1 %2"), Dashboard.PSEUDO_CURRENCY_SYMBOL, Dashboard.PSEUDO_TOTAL_PROPOSED_BUDGET));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("Percent of Budget Secured:  %1 %"), Dashboard.PSEUDO_BUDGET_SECURED_PERCENT));
		}
		
		if (code.equals(OpenStandardsImplementActionsAndMonitoringQuestion.IMPLEMENT_STRATEGIC_AND_MONITORING_PLANS))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Strategies/activities (%2%) have progress reports"), Dashboard.PSEUDO_STRATEGIES_AND_ACTIVITIES_WITH_PROGRESS_REPORT_COUNT, Dashboard.PSEUDO_STRATEGIES_AND_ACTIVITIES_WITH_PROGRESS_REPORT_PERCENT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Indicators/methods (%2%) have progress reports"), Dashboard.PSEUDO_INDICATORS_AND_METHODS_WITH_PROGRESS_REPORT_COUNT, Dashboard.PSEUDO_INDICATORS_AND_METHODS_WITH_PROGRESS_REPORT_PERCENT));
		}
		
		if (code.equals(OpenStandardsImplementActionsAndMonitoringQuestion.IMPLEMENT_WORK_PLAN_CODE))
		{
			;
		}
		
		return rowDefinitions;
	}

	private Vector<DashboardRowDefinition> addPlanActionsAndMonitoringDefinitions(String code)
	{
		Vector<DashboardRowDefinition> rowDefinitions = new Vector<DashboardRowDefinition>();
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.DEVELOP_GOALS_FOR_EACH_TARGET_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 of %2 Targets have Goals"), Dashboard.PSEUDO_TARGETS_WITH_GOALS_COUNT, Dashboard.PSEUDO_TARGET_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 goals Created"), Dashboard.PSEUDO_GOAL_COUNT));
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.IDENTIFY_KEY_FACTORS_AND_DRAFT_STRATEGIES_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Draft Strategies Created"), Dashboard.PSEUDO_DRAFT_STRATEGY_COUNT));
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.RANK_DRAFT_STRATEGIES_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Draft Strategies Ranked"), Dashboard.PSEUDO_RANKED_DRAFT_STRATEGY_COUNT));
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.CREATE_RESULTS_CHAINS_SHOWING_ASSUMPTIONS_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Strategies Created"), Dashboard.PSEUDO_STRATEGY_COUNT));
			rowDefinitions.add(createTaxonomyRowDefinition(Dashboard.PSEUDO_STRATEGY__WITH_TAXONOMY_COUNT,	Dashboard.PSEUDO_STRATEGY_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Results Chains Created"), Dashboard.PSEUDO_RESULTS_CHAIN_COUNT));
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.DEVELOP_OBJECTIVES_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Objectives Created"), Dashboard.PSEUDO_OBJECTIVE_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%2 of %1 RCs have at least 1 objective"), Dashboard.PSEUDO_STRATEGY__WITH_TAXONOMY_COUNT, Dashboard.PSEUDO_RESULTS_CHAIN_WITH_OBJECTIVE_COUNT));
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.FINALIZE_STRATEGIC_PLAN_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 % of Objectives associated with 1 or more Strategies"), Dashboard.PSEUDO_OBJECTIVES_RELEVANT_TO_STRATEGIES_PERCENTAGE));		
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 % of Strategies associated with 1 or more Objectives"), Dashboard.PSEUDO_STRATEGIES_RELEVANT_TO_OBJECTIVES_PERCENTAGE));
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.FINALIZE_MONITORING_PLAN_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Objectives associated to 1 or more Indicators"), Dashboard.PSEUDO_OBJECTIVES_RELEVANT_TO_INDICATORS_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 % of indicators with Desired Future Status specified"), Dashboard.PSEUDO_INDICATORS_WITH_DESIRED_FUTURE_STATUS_SPECIFIED_PERCENTAGE));
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.DEFINE_AUDIENCES_AND_INFORMATION_NEEDS_CODE))
		{
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.DEFINE_INDICATORS_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Indicators associated to KEA's"), Dashboard.PSEUDO_KEA_INDICATORS_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 indicators associate to Factors"), Dashboard.PSEUDO_FACTOR_INDICATORS_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 % of indicators linked to objectives"), Dashboard.PSEUDO_INDICATORS_RELEVANT_TO_OBJECTIVES_PERCENTAGE));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 % of indicators not linked to objectives"), Dashboard.PSEUDO_INDICATORS_IRRELEVANT_TO_OBJECIVES_PERCENTAGE));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 KEA viability indicators associated with %2 targets"), Dashboard.PSEUDO_KEA_INDICATORS_COUNT, Dashboard.PSEUDO_TARGET_WITH_KEA_INDICATORS_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 simple viability indicators associated with %2 targets"), Dashboard.PSEUDO_SIMPLE_VIABILITY_INDICATORS_COUNT, Dashboard.PSEUDO_TARGET_WITH_SIMPLE_VIABILITY_INDICATORS_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Direct Threat Indicators"), Dashboard.PSEUDO_DIRECT_THREAT_INDICATORS_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Threat Reduction Result indicators"), Dashboard.PSEUDO_THREAT_REDUCTION_RESULT_INDICATORS_COUNT));
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.ASSESS_HUMAN_FINANCIAL_AND_OTHER_RESOURCES_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Team Members"), Dashboard.PSEUDO_TEAM_MEMBER_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Other Organizations"), Dashboard.PSEUDO_OTHER_ORGANIZATION_COUNT));
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.ASSESS_RISKS_CODE))
		{
		}
		
		if (code.equals(OpenStandardsPlanActionsAndMonitoringQuestion.PLAN_PROJECT_LIFESPAN_AND_EXIT_STRATEGY_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 - %2"), Dashboard.PSEUDO_PROJECT_PLANNING_START_DATE, Dashboard.PSEUDO_PROJECT_PLANNING_END_DATE));
		}
		
		return rowDefinitions;
	}

	private Vector<DashboardRowDefinition> addConceptualizeRowDefinitions(String code)
	{
		Vector<DashboardRowDefinition> rowDefinitions = new Vector<DashboardRowDefinition>();
		if (code.equals(OpenStandardsConceptualizeQuestion.SELECT_INTIAL_TEAM_MEMBERS_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("Team Members: %1"), Dashboard.PSEUDO_TEAM_MEMBER_COUNT));
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.AGREE_ON_ROLES_AND_RESPONSIBILITIES_CODE))
		{
			addAgreeOnRolesAndResponsibilities();
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.DEFINE_PROJECT_SCOPE_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("The scope currently consists of %1 words"), Dashboard.PSEUDO_PROJECT_SCOPE_WORD_COUNT));
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.DEVELOP_MAP_OF_PROJECT_AREA_CODE))
		{
			addDevelopMapOfProjectArea();
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.SELECT_CONSERVATION_TARGETS_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 created"),Dashboard.PSEUDO_TARGET_COUNT));
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.ADD_HUMAN_WELFARE_TARGETS_IF_DESIRED_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 created"), Dashboard.PSEUDO_HUMAN_WELFARE_TARGET_COUNT));
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.DESCRIBE_STATUS_OF_TARGETS_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 targets have KEA"), Dashboard.PSEUDO_TARGET_WITH_KEA_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 targets have simple viablity information"), Dashboard.PSEUDO_TARGET_WITH_SIMPLE_VIABILITY_COUNT));
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.IDENTIFY_DIRECT_THREATS_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Direct Threats created"), Dashboard.PSEUDO_THREAT_COUNT));
			rowDefinitions.add(createTaxonomyRowDefinition(Dashboard.PSEUDO_THREAT_WITH_TAXONOMY_COUNT, Dashboard.PSEUDO_THREAT_COUNT));
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.RANK_DIRECT_THREATS_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%2 of %1 threat/target ranks with simple ranking"), Dashboard.PSEUDO_THREAT_TARGET_LINK_COUNT, Dashboard.PSEUDO_THREAT_TARGET_LINK_WITH_SIMPLE_RATING_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%2 of %1 threat/target ranks with stress-based ranking"), Dashboard.PSEUDO_THREAT_TARGET_LINK_COUNT, Dashboard.PSEUDO_THREAT_TARGET_LINK_WITH_STRESS_BASED_RATING_COUNT));
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.IDENTIFY_INDIRECT_THREATS_AND_OPPORTUNITIES_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Contributing Factors Created"), Dashboard.PSEUDO_CONTRIBUTING_FACTOR_COUNT));
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.ASSESS_STAKEHOLDERS_CODE))
		{
			addAssessStakeholders();
		}

		if (code.equals(OpenStandardsConceptualizeQuestion.CREATE_INITIAL_CONCEPTUAL_MODEL_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 Conceptual Model Pages created"), Dashboard.PSEUDO_CONCEPTUAL_MODEL_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 total factors created"), Dashboard.PSEUDO_TOTAL_FACTOR_COUNT));
		}
		
		if (code.equals(OpenStandardsConceptualizeQuestion.GROUND_THRUTH_AND_REVISE_MODEL_CODE))
		{
			addGroundTruthAndReviseModel();
		}
		
		return rowDefinitions;
	}

	private DashboardRowDefinition createTaxonomyRowDefinition(final String pseudoWithTaxonomyCount, final String pseudoTaxonomyOwnerCount)
	{
		return new DashboardRowDefinition(EAM.text("%1 of %2 have taxonomy assignments"), pseudoWithTaxonomyCount, pseudoTaxonomyOwnerCount);
	}
	
	private Vector<DashboardRowDefinition> addAnalyzeUseAndAdaptDefinitions(String code)
	{
		Vector<DashboardRowDefinition> rowDefinitions = new Vector<DashboardRowDefinition>();
		if (code.equals(OpenStandardsAnalyzeUseAndAdaptQuestion.DEVELOP_SYSTEMS_FOR_RECORDING_STORING_PROCESSING_AND_BACKING_UP_DATA_CODE))
		{
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 indicators with no measurement"), Dashboard.PSEUDO_INDICATORS_WITH_NO_MEASUREMENT_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 indicators with 1 measurement"), Dashboard.PSEUDO_INDICATORS_WITH_ONE_MEASUREMENT_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 indicators with more than 1 measurement"), Dashboard.PSEUDO_INDICATORS_WITH_MORE_THAN_ONE_MEASUREMENT_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 objectives with no percent complete record"), Dashboard.PSEUDO_OBJECTIVES_WITH_NO_PERCENT_COMPLETE_RECORD_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 objectives with 1 percent complete record"), Dashboard.PSEUDO_OBJECTIVES_WITH_ONE_PERCENT_COMPLETE_RECORD_COUNT));
			rowDefinitions.add(new DashboardRowDefinition(EAM.text("%1 objectives with more than 1 percent complete record"), Dashboard.PSEUDO_OBJECTIVES_WITH_MORE_THAN_ONE_PERCENT_COMPLETE_RECORD_COUNT));
		}

		return rowDefinitions;
	}

	private void addAgreeOnRolesAndResponsibilities()
	{
	}

	private void addDevelopMapOfProjectArea()
	{
	}

	private void addAssessStakeholders()
	{
	}

	private void addGroundTruthAndReviseModel()
	{
	}	

	private CodeList allCodes;
}
