/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
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

package org.miradi.xml.generic;

import org.miradi.main.EAM;
import org.miradi.main.Miradi;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Measurement;
import org.miradi.objects.ProgressReport;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.ProjectResource;
import org.miradi.objects.Strategy;
import org.miradi.objects.Stress;
import org.miradi.objects.Target;
import org.miradi.objects.ThreatStressRating;
import org.miradi.objects.WwfProjectData;
import org.miradi.questions.BudgetTimePeriodQuestion;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.CountriesQuestion;
import org.miradi.questions.DashboardFlagsQuestion;
import org.miradi.questions.DiagramFactorBackgroundQuestion;
import org.miradi.questions.DiagramFactorFontColorQuestion;
import org.miradi.questions.DiagramFactorFontSizeQuestion;
import org.miradi.questions.DiagramLinkColorQuestion;
import org.miradi.questions.DiagramObjectDataInclusionQuestion;
import org.miradi.questions.FiscalYearStartQuestion;
import org.miradi.questions.FosTrainingTypeQuestion;
import org.miradi.questions.HabitatAssociationQuestion;
import org.miradi.questions.KeyEcologicalAttributeTypeQuestion;
import org.miradi.questions.OpenStandardsProgressStatusQuestion;
import org.miradi.questions.PlanningTreeTargetPositionQuestion;
import org.miradi.questions.PriorityRatingQuestion;
import org.miradi.questions.ProgressReportLongStatusQuestion;
import org.miradi.questions.ProtectedAreaCategoryQuestion;
import org.miradi.questions.QuarterColumnsVisibilityQuestion;
import org.miradi.questions.RatingSourceQuestion;
import org.miradi.questions.ResourceRoleQuestion;
import org.miradi.questions.ResourceTypeQuestion;
import org.miradi.questions.StaticChoiceQuestion;
import org.miradi.questions.StatusConfidenceQuestion;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.StrategyFeasibilityQuestion;
import org.miradi.questions.StrategyImpactQuestion;
import org.miradi.questions.StrategyObjectiveTreeOrderQuestion;
import org.miradi.questions.StrategyTaxonomyQuestion;
import org.miradi.questions.StressContributionQuestion;
import org.miradi.questions.StressIrreversibilityQuestion;
import org.miradi.questions.StressScopeChoiceQuestion;
import org.miradi.questions.StressSeverityChoiceQuestion;
import org.miradi.questions.TextBoxZOrderQuestion;
import org.miradi.questions.ThreatClassificationQuestion;
import org.miradi.questions.ThreatRatingModeChoiceQuestion;
import org.miradi.questions.ThreatRatingQuestion;
import org.miradi.questions.TncFreshwaterEcoRegionQuestion;
import org.miradi.questions.TncMarineEcoRegionQuestion;
import org.miradi.questions.TncOperatingUnitsQuestion;
import org.miradi.questions.TncOrganizationalPrioritiesQuestion;
import org.miradi.questions.TncProjectPlaceTypeQuestion;
import org.miradi.questions.TncTerrestrialEcoRegionQuestion;
import org.miradi.questions.TrendQuestion;
import org.miradi.questions.ViabilityModeQuestion;
import org.miradi.questions.WwfEcoRegionsQuestion;
import org.miradi.questions.WwfManagingOfficesQuestion;
import org.miradi.questions.WwfRegionsQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.Translation;
import org.miradi.xml.wcs.Xmpz1XmlConstants;

public class XmlSchemaCreator implements Xmpz1XmlConstants
{
	public static void main(String[] args) throws Exception
	{
		new XmlSchemaCreator().printXmlRncSchema(new SchemaWriter(System.out));
	}

	public XmlSchemaCreator() throws Exception
	{
		Miradi.addThirdPartyJarsToClasspath();
		Translation.initialize();
	}

	public void printXmlRncSchema(SchemaWriter writer) throws Exception
	{
		ProjectSchemaElement rootElement = new ProjectSchemaElement();
		writer.println("namespace miradi = '" + Xmpz1XmlConstants.NAME_SPACE + "'");
		writer.defineAlias("start", rootElement.getProjectElementName() + ".element");
		rootElement.output(writer);
		
		writer.println("vocabulary_full_project_timespan = xsd:NMTOKEN { pattern = 'Total' } ");
		writer.println("vocabulary_year = xsd:NMTOKEN { pattern = '[0-9]{4}' } ");
		writer.println("vocabulary_month = xsd:integer { minInclusive='1' maxInclusive='12' } ");
		writer.println("vocabulary_date = xsd:NMTOKEN { pattern = '[0-9]{4}-[0-9]{2}-[0-9]{2}' }");
		writer.println(VOCABULARY_TEXT_BOX_Z_ORDER + " = '" + Z_ORDER_BACK_CODE + "' | '" + TextBoxZOrderQuestion.FRONT_CODE + "'");
		defineLanguagesVocabulary(writer);
		defineVocabulary(writer, VOCABULARY_FISCAL_YEAR_START, new FiscalYearStartQuestion());
		defineVocabulary(writer, VOCABULARY_PROTECTED_AREA_CATEGORIES, new ProtectedAreaCategoryQuestion());
		defineVocabulary(writer, VOCABULARY_RESOURCE_TYPE, new ResourceTypeQuestion());
		defineVocabulary(writer, VOCABULARY_RESOURCE_ROLE_CODES, new ResourceRoleQuestion());
		defineVocabulary(writer, VOCABULARY_HIDDEN_TYPES, new LegacyDiagramLegendQuestion());
		defineVocabulary(writer, VOCABULARY_DIAGRAM_FACTOR_FONT_SIZE, new DiagramFactorFontSizeQuestion());
		defineVocabulary(writer, VOCABULARY_DIAGRAM_FACTOR_FONT_STYLE, new LegacyDiagramFactorFontStyleQuestion());
		defineVocabulary(writer, VOCABULARY_DIAGRAM_FACTOR_BACKGROUND_COLOR, new DiagramFactorBackgroundQuestion());
		defineVocabulary(writer, VOCABULARY_DIAGRAM_FACTOR_FOREGROUND_COLOR, new DiagramFactorFontColorQuestion());
		defineVocabulary(writer, VOCABULARY_DIAGRAM_LINK_COLOR, new DiagramLinkColorQuestion());
		defineVocabulary(writer, VOCABULARY_BIODIVERSITY_TARGET_HABITAT_ASSICIATION, new HabitatAssociationQuestion());
		defineVocabulary(writer, VOCABULARY_TARGET_STATUS, new StatusQuestion());
		defineVocabulary(writer, VOCABULARY_TARGET_VIABILITY_MODE, new LegacyViabilityModeQuestion());
		defineVocabulary(writer, VOCABULARY_THREAT_TAXONOMY_CODE, new ThreatClassificationQuestion());
		defineVocabulary(writer, VOCABULARY_SCOPE_BOX_TYPE, new LegacyScopeBoxTypeQuestion(null));
		defineVocabulary(writer, VOCABULARY_STRESS_SEVERITY, new StressSeverityChoiceQuestion());
		defineVocabulary(writer, VOCABULARY_STRESS_SCOPE, new StressScopeChoiceQuestion());
		defineVocabulary(writer, VOCABULARY_STRATEGY_TAXONOMY_CODE, new StrategyTaxonomyQuestion());
		defineVocabulary(writer, VOCABULARY_STRATEGY_IMAPACT_RATING_CODE, new StrategyImpactQuestion());
		defineVocabulary(writer, VOCABULARY_STRATEGY_FEASIBILITY_RATING_CODE, new StrategyFeasibilityQuestion());
		defineVocabulary(writer, VOCABULARY_PRIORITY_RATING_CODE, new PriorityRatingQuestion());
		defineVocabulary(writer, VOCABULARY_KEA_TYPE, new KeyEcologicalAttributeTypeQuestion());
		defineVocabulary(writer, VOCABULARY_THREAT_STRESS_RATING_CONTRIBUTION_CODE, new StressContributionQuestion());
		defineVocabulary(writer, VOCABULARY_THREAT_STRESS_RATING_IRREVERSIBILITY_CODE, new StressIrreversibilityQuestion());
		defineVocabulary(writer, VOCABULARY_SIMPLE_THREAT_RATING_SCOPE_CODE, new ThreatRatingQuestion());
		defineVocabulary(writer, VOCABULARY_SIMPLE_THREAT_RATING_SEVERITY_CODE, new ThreatRatingQuestion());
		defineVocabulary(writer, VOCABULARY_SIMPLE_THREAT_RATING_IRREVERSIBILITY_CODE, new ThreatRatingQuestion());
		defineVocabulary(writer, VOCABULARY_TNC_PROJECT_PLACE_TYPES, new TncProjectPlaceTypeQuestion());
		defineVocabulary(writer, VOCABULARY_TNC_ORGANIZATIONAL_PRIORITIES, new TncOrganizationalPrioritiesQuestion());
		defineVocabulary(writer, VOCABULARY_TNC_OPERATING_UNTIS, new TncOperatingUnitsQuestion());
		defineVocabulary(writer, VOCABULARY_TNC_TERRESTRIAL_ECO_REGION, new TncTerrestrialEcoRegionQuestion());
		defineVocabulary(writer, VOCABULARY_TNC_MARINE_ECO_REGION, new TncMarineEcoRegionQuestion());
		defineVocabulary(writer, VOCABULARY_TNC_FRESHWATER_ECO_REGION, new TncFreshwaterEcoRegionQuestion());
		defineVocabulary(writer, VOCABULARY_WWF_MANAGING_OFFICES, new WwfManagingOfficesQuestion());
		defineVocabulary(writer, VOCABULARY_WWF_REGIONS, new WwfRegionsQuestion());
		defineVocabulary(writer, VOCABULARY_WWF_ECOREGIONS, new WwfEcoRegionsQuestion());
		defineVocabulary(writer, VOCABULARY_FOS_TRAINING_TYPE, new FosTrainingTypeQuestion());
		defineVocabulary(writer, VOCABULARY_PROGRESS_REPORT_STATUS, new ProgressReportLongStatusQuestion());
		defineVocabulary(writer, VOCABULARY_MEASUREMENT_TREND, new TrendQuestion());
		defineVocabulary(writer, VOCABULARY_MEASUREMENT_STATUS, new StatusQuestion());
		defineVocabulary(writer, VOCABULARY_MEASUREMENT_STATUS_CONFIDENCE, new LegacyStatusConfidenceQuestion());
		defineVocabulary(writer, VOCABULARY_COUNTRIES, new CountriesQuestion());
		defineVocabulary(writer, VOCABULARY_THREAT_RATING, new ThreatRatingQuestion());
		defineVocabulary(writer, VOCABULARY_DIAGRAM_OBJECT_DATA_INCLUSION, new DiagramObjectDataInclusionQuestion());
		defineVocabulary(writer, VOCABULARY_PLANNING_TREE_OBJECTIVE_STRATEGY_NODE_ORDER, new StrategyObjectiveTreeOrderQuestion());
		defineVocabulary(writer, VOCABULARY_PLANNING_TREE_TARGET_NODE_POSITION, new PlanningTreeTargetPositionQuestion());
		defineVocabulary(writer, VOCABULARY_THREAT_RATING_MODE, new ThreatRatingModeChoiceQuestion());
		defineVocabulary(writer, VOCABULARY_DASHBOARD_ROW_FLAGS, new DashboardFlagsQuestion());
		defineVocabulary(writer, VOCABULARY_QUARTER_COLUMNS_VISIBILITY, new QuarterColumnsVisibilityQuestion());
		defineVocabulary(writer, VOCABULARY_WORK_PLAN_TIME_UNIT, new BudgetTimePeriodQuestion());
		defineVocabulary(writer, VOCABULARY_RATING_SOURCE, new RatingSourceQuestion());
		defineDashboardStatusesVocabulary(writer);
		
		defineIdElement(writer, "ConceptualModel");
		defineIdElement(writer, "ResultsChain");

		defineIdElement(writer, "DiagramFactor");
		defineIdElement(writer, BIODIVERSITY_TARGET_ID_ELEMENT_NAME);
		defineIdElement(writer, "HumanWelfareTarget");
		defineIdElement(writer, CAUSE_ID_ELEMENT_NAME);
		defineIdElement(writer, "Strategy");
		defineIdElement(writer, "ThreatReductionResult");
		defineIdElement(writer, "IntermediateResult");
		defineIdElement(writer, "GroupBox");
		defineIdElement(writer, "TextBox");
		defineIdElement(writer, "ScopeBox");
		
		defineIdElement(writer, ACITIVTY_ID_ELEMENT_NAME);
		defineIdElement(writer, STRESS_ID_ELEMENT_NAME);
		
		defineIdElement(writer, "DiagramLink");
		 
		defineIdElement(writer, GOAL_ELEMENT_NAME);
		defineIdElement(writer, OBJECTIVE_ID_ELEMENT_NAME);
		defineIdElement(writer, INDICATOR_ID_ELEMENT_NAME);
		defineIdElement(writer, KEA_ID_ELEMENT_NAME);
		defineIdElement(writer, TAGGED_OBJECT_SET_ELEMENT_NAME);
		defineIdElement(writer, SUB_TARGET_ID_ELEMENT_NAME);
		defineIdElement(writer, THREAT_ID_ELEMENT_NAME);
		defineIdElement(writer, ACCOUNTING_CODE_ID_ELEMENT_NAME);
		defineIdElement(writer, FUNDING_SOURCE_ID_ELEMENT_NAME);
		defineIdElement(writer, BUDGET_CATEGORY_ONE);
		defineIdElement(writer, BUDGET_CATEGORY_TWO);
		defineIdElement(writer, PROGRESS_REPORT_ID_ELEMENT_NAME);
		defineIdElement(writer, PROGRESS_PERCENT_ID_ELEMENT_NAME);
		defineIdElement(writer, EXPENSE_ASSIGNMENT_ID_ELEMENT_NAME);
		defineIdElement(writer, RESOURCE_ASSIGNMENT_ID_ELEMENT_NAME);
		defineIdElement(writer, RESOURCE_ID_ELEMENT_NAME);
		defineIdElement(writer, ACTIVITY_ID_ELEMENT_NAME);
		defineIdElement(writer, MEASUREMENT_ID_ELEMENT_NAME);
		defineIdElement(writer, METHOD);
		defineIdElement(writer, SUB_TASK);
		
		writer.defineAlias(Xmpz1XmlConstants.WRAPPED_BY_DIAGRAM_FACTOR_ID_ELEMENT_NAME + ".element", "element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.WRAPPED_BY_DIAGRAM_FACTOR_ID_ELEMENT_NAME);
		writer.startBlock();
		writer.printlnIndented("BiodiversityTargetId.element |");
		writer.printlnIndented("HumanWelfareTargetId.element |");
		writer.printlnIndented("CauseId.element |");
		writer.printlnIndented("StrategyId.element |");
		writer.printlnIndented("ThreatReductionResultId.element |");
		writer.printlnIndented("IntermediateResultId.element |");
		writer.printlnIndented("GroupBoxId.element |");
		writer.printlnIndented("TextBoxId.element |");
		writer.printlnIndented("ScopeBoxId.element |");
		writer.printlnIndented("ActivityId.element |");
		writer.printlnIndented("StressId.element ");
		writer.endBlock();
		
		
		writer.defineAlias("ThreatReductionResultThreatId.element", "element " + Xmpz1XmlConstants.PREFIX + "ThreatReductionResultThreatId");
		writer.startBlock();
		writer.printlnIndented("ThreatId.element");
		writer.endBlock();
		
		writer.defineAlias(Xmpz1XmlConstants.LINKABLE_FACTOR_ID + ".element", "element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.LINKABLE_FACTOR_ID);
		writer.startBlock();
		writer.printlnIndented("BiodiversityTargetId.element |");
		writer.printlnIndented("HumanWelfareTargetId.element |");
		writer.printlnIndented("CauseId.element |");
		writer.printlnIndented("StrategyId.element |");
		writer.printlnIndented("ThreatReductionResultId.element |");
		writer.printlnIndented("IntermediateResultId.element |");
		writer.printlnIndented("GroupBoxId.element ");
		writer.endBlock();
		
		writer.defineAlias("GeospatialLocation.element", "element miradi:GeospatialLocation");
		writer.startBlock();
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + "latitude { xsd:decimal } &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + "longitude { xsd:decimal } ");
		writer.endBlock();
		
		writer.defineAlias(Xmpz1XmlConstants.EXTERNAL_PROJECT_ID_ELEMENT_NAME + ".element", "element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.EXTERNAL_PROJECT_ID_ELEMENT_NAME);
		writer.startBlock();
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.EXTERNAL_APP_ELEMENT_NAME + " { text } &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.PROJECT_ID + " { text } ");
		writer.endBlock();
		
		writer.defineAlias(Xmpz1XmlConstants.DIAGRAM_POINT_ELEMENT_NAME + ".element", "element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.DIAGRAM_POINT_ELEMENT_NAME);
		writer.startBlock();
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.X_ELEMENT_NAME + " { xsd:integer } &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.Y_ELEMENT_NAME + " { xsd:integer } ");
		writer.endBlock();
		
		writer.defineAlias(Xmpz1XmlConstants.DIAGRAM_SIZE_ELEMENT_NAME + ".element", "element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.DIAGRAM_SIZE_ELEMENT_NAME);
		writer.startBlock();
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.WIDTH_ELEMENT_NAME + " { xsd:integer } &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.HEIGHT_ELEMENT_NAME + " { xsd:integer } ");
		writer.endBlock();
		
		defineDiagramFactorUiSettings(writer);
		defineSimpleThreatRatingElement(writer);
		defineStressBasedThreatRatingElement(writer);
		defineDateUnitEfforts(writer);
		defineWorkUnitsFullProjectTimeSpanElement(writer);
		defineWorkUnitsYearElement(writer);
		defineWorkUnitsQuarterElement(writer);
		defineWorkUnitsMonthElement(writer);
		defineWorkUnitsDayElement(writer);
		defineThresholdsElement(writer);
		defineTimePeriodCostsElement(writer);
		defineExpenseEntryElement(writer);
		defineWorkUnitsEntryElement(writer);
		
		defineDateUnitExpense(writer);
		defineExpenseFullProjectTimeSpanElement(writer);
		defineExpenseYearElement(writer);
		defineExpenseQuarterElement(writer);
		defineExpenseMonthElement(writer);
		defineExpenseDayElement(writer);
		
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.PROJECT_SUMMARY_PLANNING, VOCABULARY_FISCAL_YEAR_START, "FiscalYearStartMonth");
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.PROJECT_SUMMARY_SCOPE, VOCABULARY_PROTECTED_AREA_CATEGORIES, PROTECTED_AREA_CATEGORIES_ELEMENT_NAME);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.PROJECT_RESOURCE, VOCABULARY_RESOURCE_ROLE_CODES, RESOURCE_ROLE_CODES_ELEMENT_NAME);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.CONCEPTUAL_MODEL, VOCABULARY_HIDDEN_TYPES, HIDDEN_TYPES_ELEMENT_NAME);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.RESULTS_CHAIN, VOCABULARY_HIDDEN_TYPES, HIDDEN_TYPES_ELEMENT_NAME);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.BIODIVERSITY_TARGET, VOCABULARY_BIODIVERSITY_TARGET_HABITAT_ASSICIATION, BIODIVERSITY_TARGET_HABITAT_ASSOCIATION_ELEMENT_NAME);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.HUMAN_WELFARE_TARGET, VOCABULARY_BIODIVERSITY_TARGET_HABITAT_ASSICIATION, BIODIVERSITY_TARGET_HABITAT_ASSOCIATION_ELEMENT_NAME);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.HUMAN_WELFARE_TARGET, VOCABULARY_TARGET_STATUS, TARGET_STATUS_ELEMENT_NAME);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.HUMAN_WELFARE_TARGET, VOCABULARY_TARGET_VIABILITY_MODE, TARGET_VIABILITY_MODE_ELEMENT_NAME);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.STRESS, VOCABULARY_STRESS_SEVERITY, STRESS_SEVERITY_ELEMENT_NAME);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.STRESS, VOCABULARY_STRESS_SCOPE, STRESS_SCOPE_ELEMENT_NAME);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.TNC_PROJECT_DATA, VOCABULARY_TNC_PROJECT_PLACE_TYPES, TNC_PROJECT_PLACE_TYPES);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.TNC_PROJECT_DATA, VOCABULARY_TNC_ORGANIZATIONAL_PRIORITIES, TNC_ORGANIZATIONAL_PRIORITIES);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.TNC_PROJECT_DATA, VOCABULARY_TNC_OPERATING_UNTIS, TNC_OPERATING_UNITS);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.TNC_PROJECT_DATA, VOCABULARY_TNC_TERRESTRIAL_ECO_REGION, TNC_TERRESTRIAL_ECO_REGION);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.TNC_PROJECT_DATA, VOCABULARY_TNC_MARINE_ECO_REGION, TNC_MARINE_ECO_REGION);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.TNC_PROJECT_DATA, VOCABULARY_TNC_FRESHWATER_ECO_REGION, TNC_FRESHWATER_ECO_REGION);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.WWF_PROJECT_DATA, VOCABULARY_WWF_MANAGING_OFFICES, WwfProjectData.TAG_MANAGING_OFFICES);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.WWF_PROJECT_DATA, VOCABULARY_WWF_REGIONS, WwfProjectData.TAG_REGIONS);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.WWF_PROJECT_DATA, VOCABULARY_WWF_ECOREGIONS, WwfProjectData.TAG_ECOREGIONS);
		defineVocabularyDefinedAlias(writer, Xmpz1XmlConstants.PROJECT_SUMMARY_LOCATION, VOCABULARY_COUNTRIES, COUNTRIES);
		defineVocabularyDefinedAlias(writer, DASHBOARD, VOCABULARY_DASHBOARD_ROW_FLAGS, DASHBOARD_FLAGS);
		
		defineDashboardUserChoiceMap(writer);
		defineExtraDataSectionElement(writer);
		
		writer.flush();
	}
	
	private void defineExtraDataSectionElement(SchemaWriter writer)
	{
		writer.defineAlias(EXTRA_DATA_SECTION + ".element", "element " + Xmpz1XmlConstants.PREFIX + EXTRA_DATA_SECTION);
		writer.startBlock();
		writer.printlnIndented("attribute " + EXTRA_DATA_SECTION_OWNER_ATTRIBUTE + " { text } &");
		writer.printlnIndented(EXTRA_DATA_ITEM + ".element *");
		writer.endBlock();
		
		defineExtraDataItemElement(writer);
	}

	private void defineExtraDataItemElement(SchemaWriter writer)
	{
		writer.defineAlias(EXTRA_DATA_ITEM + ".element", "element " + Xmpz1XmlConstants.PREFIX + EXTRA_DATA_ITEM);
		writer.startBlock();
		writer.printlnIndented("attribute " + EXTRA_DATA_ITEM_NAME + " { text } &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + EXTRA_DATA_ITEM_VALUE + " { text }?");
		writer.endBlock();
	}

	private void defineDashboardStatusesVocabulary(SchemaWriter writer)
	{
		final String[] allCodesFromDynamicQuestion = new String[]{
			OpenStandardsProgressStatusQuestion.NOT_SPECIFIED_CODE,
			OpenStandardsProgressStatusQuestion.NOT_STARTED_CODE,
			OpenStandardsProgressStatusQuestion.IN_PROGRESS_CODE,
			OpenStandardsProgressStatusQuestion.COMPLETE_CODE,
			OpenStandardsProgressStatusQuestion.NOT_APPLICABLE_CODE,
		};
		
		writer.print(VOCABULARY_DASHBOARD_ROW_PROGRESS + " = ");
		for(int index = 0; index < allCodesFromDynamicQuestion.length; ++index)
		{
			String code = allCodesFromDynamicQuestion[index];
			writer.write("'" + code + "'");
			if (index < allCodesFromDynamicQuestion.length - 1)
				writer.print("|");
		}
		
		writer.println();
	}	
	
	private void defineDashboardUserChoiceMap(SchemaWriter writer)
	{
		writer.defineAlias(DASHBOARD_STATUS_ENTRY + ".element", "element " + Xmpz1XmlConstants.PREFIX + DASHBOARD_STATUS_ENTRY);
		writer.startBlock();
		writer.printlnIndented("attribute " + KEY_ATTRIBUTE_NAME + " { text } &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + DASHBOARD_PROGRESS + " { " + VOCABULARY_DASHBOARD_ROW_PROGRESS + " }? &");
		writer.printlnIndented(DASHBOARD + DASHBOARD_FLAGS + CONTAINER_ELEMENT_TAG + ".element? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + DASHBOARD_COMMENTS + " { text }?");
		writer.endBlock();
	}

	private void defineDiagramFactorUiSettings(SchemaWriter writer)
	{
		writer.defineAlias(STYLING + ".element"	, ELEMENT_NAME + PREFIX + STYLING);
		writer.startBlock();
		writer.printlnIndented(ELEMENT_NAME + PREFIX + DIAGRAM_FACTOR + XmlSchemaCreator.DIAGRAM_FACTOR_FONT_SIZE_ELEMENT_NAME + " { " + XmlSchemaCreator.VOCABULARY_DIAGRAM_FACTOR_FONT_SIZE + " }? &");
		writer.printlnIndented(ELEMENT_NAME + PREFIX + DIAGRAM_FACTOR + XmlSchemaCreator.DIAGRAM_FACTOR_FONT_STYLE_ELEMENT_NAME + " { " + XmlSchemaCreator.VOCABULARY_DIAGRAM_FACTOR_FONT_STYLE + " }? &");
		writer.printlnIndented(ELEMENT_NAME + PREFIX + DIAGRAM_FACTOR + XmlSchemaCreator.DIAGRAM_FACTOR_FOREGROUND_COLOR_ELEMENT_NAME + " { " + XmlSchemaCreator.VOCABULARY_DIAGRAM_FACTOR_FOREGROUND_COLOR + " }? &");
		writer.printlnIndented(ELEMENT_NAME + PREFIX + DIAGRAM_FACTOR + XmlSchemaCreator.DIAGRAM_FACTOR_BACKGROUND_COLOR_ELEMENT_NAME + " { " + XmlSchemaCreator.VOCABULARY_DIAGRAM_FACTOR_BACKGROUND_COLOR + " }?");
		writer.endBlock();		
	}
		
	private void defineSimpleThreatRatingElement(SchemaWriter writer)
	{
		writer.defineAlias(SIMPLE_BASED_THREAT_RATING + ".element"	, ELEMENT_NAME + PREFIX + SIMPLE_BASED_THREAT_RATING);
		writer.startBlock();
		writer.printlnIndented(ELEMENT_NAME + PREFIX + SIMPLE_BASED_THREAT_RATING + SCOPE + " { " + XmlSchemaCreator.VOCABULARY_SIMPLE_THREAT_RATING_SCOPE_CODE + " }? &");
		writer.printlnIndented(ELEMENT_NAME + PREFIX + SIMPLE_BASED_THREAT_RATING + SEVERITY + " { " + XmlSchemaCreator.VOCABULARY_SIMPLE_THREAT_RATING_SEVERITY_CODE +" }? &");
		writer.printlnIndented(ELEMENT_NAME + PREFIX + SIMPLE_BASED_THREAT_RATING + IRREVERSIBILITY + " { " + XmlSchemaCreator.VOCABULARY_SIMPLE_THREAT_RATING_IRREVERSIBILITY_CODE + " }?");
		writer.endBlock();
    }

	private void defineStressBasedThreatRatingElement(SchemaWriter writer)
	{
		writer.defineAlias(STRESS_BASED_THREAT_RATING + ".element", ELEMENT_NAME + PREFIX + STRESS_BASED_THREAT_RATING);
		writer.startBlock();
		writer.printlnIndented(ELEMENT_NAME + PREFIX + STRESS_BASED_THREAT_RATING + "StressId{ StressId.element } &");
		writer.printlnIndented(ELEMENT_NAME + PREFIX + STRESS_BASED_THREAT_RATING + CONTRIBUTION + " { " + XmlSchemaCreator.VOCABULARY_THREAT_STRESS_RATING_CONTRIBUTION_CODE + " }? &");
		writer.printlnIndented(ELEMENT_NAME + PREFIX + STRESS_BASED_THREAT_RATING + IRREVERSIBILITY + " { " + XmlSchemaCreator.VOCABULARY_THREAT_STRESS_RATING_IRREVERSIBILITY_CODE + " }? &");
		writer.printlnIndented(ELEMENT_NAME + PREFIX + STRESS_BASED_THREAT_RATING + IS_ACTIVE + " { xsd:boolean }? &");
		writer.printlnIndented(ELEMENT_NAME + PREFIX + STRESS_BASED_THREAT_RATING + STRESS_RATING + " { " + XmlSchemaCreator.VOCABULARY_THREAT_RATING + " }? &");
		writer.printlnIndented(ELEMENT_NAME + PREFIX + STRESS_BASED_THREAT_RATING + THREAT_STRESS_RATING + " { " + XmlSchemaCreator.VOCABULARY_THREAT_RATING + " }? ");
		writer.endBlock();
	}

	private void defineDateUnitEfforts(SchemaWriter writer)
	{
		writer.defineAlias("DateUnitWorkUnits.element", "element " + Xmpz1XmlConstants.PREFIX + "DateUnitWorkUnits");
		writer.startBlock();
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + "WorkUnitsDateUnit{WorkUnitsDay.element | WorkUnitsMonth.element | WorkUnitsQuarter.element | WorkUnitsYear.element | WorkUnitsFullProjectTimespan.element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + "NumberOfUnits { xsd:decimal }?");
		writer.endBlock();
	}

	private void defineDateUnitExpense(SchemaWriter writer)
	{
		writer.defineAlias("DateUnitExpense.element", "element " + Xmpz1XmlConstants.PREFIX + "DateUnitExpense");
		writer.startBlock();
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.EXPENSES_DATE_UNIT + "{ExpensesDay.element | ExpensesMonth.element | ExpensesQuarter.element | ExpensesYear.element | ExpensesFullProjectTimespan.element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + Xmpz1XmlConstants.EXPENSE + " { xsd:decimal }?");
		writer.endBlock();
	}
	
	private void defineExpenseFullProjectTimeSpanElement(SchemaWriter writer)
	{
		defineFullProjectTimeSpanElement(writer, "ExpensesFullProjectTimespan");
	}
	
	private void defineExpenseYearElement(SchemaWriter writer)
	{
		defineYearElement(writer, "ExpensesYear");
	}

	private void defineExpenseQuarterElement(SchemaWriter writer)
	{
		defineQuarterElement(writer, "ExpensesQuarter");
	}
	
	private void defineExpenseMonthElement(SchemaWriter writer)
	{
		defineMonthElement(writer, "ExpensesMonth");
	}
	
	private void defineExpenseDayElement(SchemaWriter writer)
	{
		defineDayElement(writer, "ExpensesDay");
	}
	
	private void defineWorkUnitsFullProjectTimeSpanElement(SchemaWriter writer)
	{
		defineFullProjectTimeSpanElement(writer, "WorkUnitsFullProjectTimespan");
	}
		
	private void defineWorkUnitsYearElement(SchemaWriter writer)
	{
		defineYearElement(writer, "WorkUnitsYear");
	}

	private void defineWorkUnitsQuarterElement(SchemaWriter writer)
	{
		defineQuarterElement(writer, "WorkUnitsQuarter");
	}

	private void defineWorkUnitsMonthElement(SchemaWriter writer)
	{
		defineMonthElement(writer, "WorkUnitsMonth");
	}
	
	private void defineWorkUnitsDayElement(SchemaWriter writer)
	{
		defineDayElement(writer, "WorkUnitsDay");
	}
	
	private void defineTimePeriodCostsElement(SchemaWriter writer)
	{
		writer.defineAlias(TIME_PERIOD_COSTS + ".element", "element " + Xmpz1XmlConstants.PREFIX + TIME_PERIOD_COSTS);
		writer.startBlock();
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + CALCULATED_START_DATE + "{ vocabulary_date } &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + CALCULATED_END_DATE + "{ vocabulary_date } &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + CALCULATED_EXPENSE_TOTAL + "{ xsd:decimal }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + CALCULATED_WORK_UNITS_TOTAL + "{ xsd:decimal }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + CALCULATED_TOTAL_BUDGET_COST + "{ xsd:decimal }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + CALCULATED_WHO + "{ ResourceId.element* }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + CALCULATED_EXPENSE_ENTRIES + "{ " + EXPENSE_ENTRY + ".element* }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + CALCULATED_WORK_UNITS_ENTRIES + "{ " + WORK_UNITS_ENTRY + ".element* }?");
		writer.endBlock();
	}
	
	private void defineExpenseEntryElement(SchemaWriter writer)
	{
		writer.defineAlias(EXPENSE_ENTRY + ".element", "element " + Xmpz1XmlConstants.PREFIX + EXPENSE_ENTRY);
		writer.startBlock();
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + EXPENSE_ENTRY + FUNDING_SOURCE_ID + "{ " + FUNDING_SOURCE_ID + ".element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + EXPENSE_ENTRY + ACCOUNTING_CODE_ID + "{ " + ACCOUNTING_CODE_ID + ".element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + EXPENSE_ENTRY + BUDGET_CATEGORY_ONE_ID + "{ " + BUDGET_CATEGORY_ONE_ID + ".element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + EXPENSE_ENTRY + BUDGET_CATEGORY_TWO_ID + "{ " + BUDGET_CATEGORY_TWO_ID + ".element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + EXPENSE_ENTRY + DETAILS + "{ " + DATE_UNITS_EXPENSE + ".element* }?");
		writer.endBlock();
	}
	
	private void defineWorkUnitsEntryElement(SchemaWriter writer)
	{
		writer.defineAlias(WORK_UNITS_ENTRY + ".element", "element " + Xmpz1XmlConstants.PREFIX + WORK_UNITS_ENTRY);
		writer.startBlock();
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + WORK_UNITS_ENTRY + RESOURCE_ID + "{ " + RESOURCE_ID + ".element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + WORK_UNITS_ENTRY + FUNDING_SOURCE_ID + "{ " + FUNDING_SOURCE_ID + ".element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + WORK_UNITS_ENTRY + ACCOUNTING_CODE_ID + "{ " + ACCOUNTING_CODE_ID + ".element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + WORK_UNITS_ENTRY + BUDGET_CATEGORY_ONE_ID + "{ " + BUDGET_CATEGORY_ONE_ID + ".element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + WORK_UNITS_ENTRY + BUDGET_CATEGORY_TWO_ID + "{ " + BUDGET_CATEGORY_TWO_ID + ".element }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + WORK_UNITS_ENTRY + DETAILS + "{ " + DATE_UNIT_WORK_UNITS + ".element* }?");
		writer.endBlock();
	}
	
	private void defineThresholdsElement(SchemaWriter writer)
	{
		writer.defineAlias(THRESHOLD + ".element", "element " + Xmpz1XmlConstants.PREFIX + THRESHOLD);
		writer.startBlock();
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + STATUS_CODE + "{" + VOCABULARY_MEASUREMENT_STATUS + "}? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + THRESHOLD_VALUE + " { text }? &");
		writer.printlnIndented("element " + Xmpz1XmlConstants.PREFIX + THRESHOLD_DETAILS + " { text }?");
		writer.endBlock();
	}
	
	private void defineFullProjectTimeSpanElement(SchemaWriter writer, String fullProjectTimeSpanElementName)
	{
		String[] subElements = new String[]{"attribute " + FULL_PROJECT_TIMESPAN + " { vocabulary_full_project_timespan }"};
		defineElement(writer, fullProjectTimeSpanElementName, subElements);
	}
	
	private void defineYearElement(SchemaWriter writer, String yearElementName)
	{
		String[] subElements = new String[]{"attribute StartYear {vocabulary_year}", "attribute StartMonth {vocabulary_month}"};
		defineElement(writer, yearElementName, subElements);
	}
		
	private void defineQuarterElement(SchemaWriter writer, String quarterElementName)
	{
		String[] subElements = new String[]{"attribute Year {vocabulary_year}", "attribute StartMonth {vocabulary_month}"};
		defineElement(writer, quarterElementName, subElements);
	}
	
	private void defineMonthElement(SchemaWriter writer, String monthElementName)
	{
		String[] subElements = new String[]{"attribute Year {vocabulary_year}", "attribute Month {vocabulary_month}"};
		defineElement(writer, monthElementName, subElements);
	}
	
	private void defineDayElement(SchemaWriter writer, String dayElementName)
	{
		String[] subElements = new String[]{"attribute Date {vocabulary_date}"};
		defineElement(writer, dayElementName, subElements);
	}
	
	private void defineElement(SchemaWriter writer, String elementName, String[] subElements)
	{
		writer.defineAlias(elementName + ".element", "element miradi:" + elementName);
		writer.startBlock();
		for (int index = 0; index < subElements.length; ++index)
		{
			if (index > 0)
				writer.println(" &");
			
			writer.printIndented(subElements[index]);
		}
		
		writer.println();
		writer.endBlock();
	}
	
	
	private void defineVocabularyDefinedAlias(SchemaWriter writer, String parentElementName, String vocabularyElementName, String elementName)
	{
		writer.defineAlias(parentElementName + elementName+Xmpz1XmlConstants.CONTAINER_ELEMENT_TAG + ".element", "element miradi:" + parentElementName + elementName+Xmpz1XmlConstants.CONTAINER_ELEMENT_TAG);
		writer.startBlock();
		writer.printlnIndented("element miradi:" + CODE_ELEMENT_NAME + " { " + vocabularyElementName + " } * ");
		writer.endBlock();
	}
	
	private void defineIdElement(SchemaWriter writer, String baseName)
	{
		writer.println(baseName + "Id.element = element " + Xmpz1XmlConstants.PREFIX + baseName + "Id { xsd:integer }");
	}
	
	private void defineVocabulary(SchemaWriter writer, String vocabularyName, ChoiceQuestion question)
	{
		CodeList codes = question.getAllCodes();
		writer.print(vocabularyName + " = ");
		for(int index = 0; index < codes.size(); ++index)
		{
			String code = codes.get(index);
			code = question.convertToReadableCode(code);
			writer.write("'" + code + "'");
			if (index < codes.size() - 1)
				writer.print("|");
		}
		
		writer.println();
	}
	
	private static class LegacyViabilityModeQuestion extends ViabilityModeQuestion
	{
		
		@Override
		public String convertToReadableCode(String code)
		{
			return code;
		}
		
		@Override
		protected boolean hasReadableAlternativeDefaultCode()
		{
			return false;
		}
	}
	
	private static class LegacyDiagramLegendQuestion extends StaticChoiceQuestion
	{
		public LegacyDiagramLegendQuestion()
		{
			super(getLegendChoices());
		}

		private static ChoiceItem[] getLegendChoices()
		{
			return  new ChoiceItem[] {
					new ChoiceItem("Strategy", "Strategy"),
					new ChoiceItem("Draft" + "Strategy", "Draft" + "Strategy"),
					new ChoiceItem("ContributingFactor", "ContributingFactor"),
					new ChoiceItem("DirectThreat", "DirectThreat"),
					new ChoiceItem("Target", "Target"),
					new ChoiceItem("HumanWelfareTarget", "HumanWelfareTarget"),
					new ChoiceItem("Link", "Link"),
					new ChoiceItem("Goal", "Goal"),
					new ChoiceItem("Objective", "Objective"),
					new ChoiceItem("Indicator", "Indicator"),
					new ChoiceItem("TextBox", "TextBox"),
					new ChoiceItem("ScopeBox", "ScopeBox"),
					new ChoiceItem("Stress", "Stress"),
					new ChoiceItem("Activity", "Activity"),
					new ChoiceItem("IntermediateResult", "IntermediateResult"),
					new ChoiceItem("ThreatReductionResult", "ThreatReductionResult"),
					new ChoiceItem("GroupBox", "GroupBox"),
			};
		}
	}
	
	private static class LegacyStatusConfidenceQuestion extends StaticChoiceQuestion
	{
		public LegacyStatusConfidenceQuestion()
		{
			super(getStatusConfidences());
		}
		
		private static ChoiceItem[] getStatusConfidences()
		{
			return new ChoiceItem[] {
				new ChoiceItem(StatusConfidenceQuestion.NOT_SPECIFIED, EAM.text("Not Specified")),
				new ChoiceItem(StatusConfidenceQuestion.ROUGH_GUESS_CODE, EAM.text("Rough Guess")),
				new ChoiceItem(StatusConfidenceQuestion.EXPERT_KNOWLEDGE_CODE, EAM.text("Expert Knowledge")),
				new ChoiceItem(StatusConfidenceQuestion.RAPID_ASSESSMENT_CODE, EAM.text("Rapid Assessment")),
				new ChoiceItem(StatusConfidenceQuestion.INTENSIVE_ASSESSMENT_CODE, EAM.text("Intensive Assessment")),
			};
		}
	}
	
	private void defineLanguagesVocabulary(SchemaWriter writer)
	{
		writer.print(VOCABULARY_LANGUAGE_CODE + " = " + "'ab'|'aa'|'af'|'ak'|'sq'|'am'|'ar'|'an'|'hy'|'as'|'av'|'ae'|'ay'|'az'|'bm'|'ba'|'eu'|'be'|'bn'|'bh'|'bi'|'nb'|'bs'|'br'|'bg'|'my'|'ca'|'km'|'ch'|'ce'|'ny'|'zh'|'cu'|'cv'|'kw'|'co'|'cr'|'hr'|'cs'|'da'|'dv'|'nl'|'dz'|'en'|'eo'|'et'|'ee'|'fo'|'fj'|'fi'|'fr'|'ff'|'gd'|'gl'|'lg'|'ka'|'de'|'el'|'gn'|'gu'|'ht'|'ha'|'he'|'hz'|'hi'|'ho'|'hu'|'is'|'io'|'ig'|'id'|'ia'|'ie'|'iu'|'ik'|'ga'|'it'|'ja'|'jv'|'kl'|'kn'|'kr'|'ks'|'kk'|'ki'|'rw'|'ky'|'kv'|'kg'|'ko'|'kj'|'ku'|'lo'|'la'|'lv'|'li'|'ln'|'lt'|'lu'|'lb'|'mk'|'mg'|'ms'|'ml'|'mt'|'gv'|'mi'|'mr'|'mh'|'mo'|'mn'|'na'|'nv'|'nd'|'nr'|'ng'|'ne'|'se'|'no'|'nn'|'oc'|'oj'|'or'|'om'|'os'|'pi'|'pa'|'fa'|'pl'|'pt'|'ps'|'qu'|'ro'|'rm'|'rn'|'ru'|'sm'|'sg'|'sa'|'sc'|'sr'|'sn'|'ii'|'sd'|'si'|'sk'|'sl'|'so'|'st'|'es'|'su'|'sw'|'ss'|'sv'|'tl'|'ty'|'tg'|'ta'|'tt'|'te'|'th'|'bo'|'ti'|'to'|'ts'|'tn'|'tr'|'tk'|'tw'|'ug'|'uk'|''|'ur'|'uz'|'ve'|'vi'|'vo'|'wa'|'cy'|'fy'|'wo'|'xh'|'yi'|'yo'|'za'|'zu'");
		writer.println();
	}
	
	public static final String VOCABULARY_LANGUAGE_CODE = "vocabulary_language_code";
	private static final String VOCABULARY_FISCAL_YEAR_START = "vocabulary_fiscal_year_start_month";
	private static final String VOCABULARY_PROTECTED_AREA_CATEGORIES = "vocabulary_protected_area_categories";
	public static final String VOCABULARY_RESOURCE_TYPE = "vocabulary_resource_type";
	private static final String VOCABULARY_RESOURCE_ROLE_CODES = "vocabulary_resource_role_codes";
	private static final String VOCABULARY_HIDDEN_TYPES = "vocabulary_hidden_types";
	static final String VOCABULARY_DIAGRAM_FACTOR_FONT_SIZE = "vocabulary_diagram_factor_font_size";
	public static final String VOCABULARY_DIAGRAM_FACTOR_FONT_STYLE = "vocabulary_diagram_factor_font_style";
	public static final String VOCABULARY_DIAGRAM_FACTOR_BACKGROUND_COLOR = "vocabulary_diagram_factor_background_color";
	public static final String VOCABULARY_DIAGRAM_FACTOR_FOREGROUND_COLOR = "vocabulary_diagram_factor_foreground_color";
	public static final String VOCABULARY_DIAGRAM_LINK_COLOR = "vocabulary_diagram_link_color";
	private static final String VOCABULARY_BIODIVERSITY_TARGET_HABITAT_ASSICIATION = "vocabulary_biodiversity_target_habitat_association";
	public static final String VOCABULARY_TARGET_STATUS = "vocabulary_target_status";
	static final String VOCABULARY_TARGET_VIABILITY_MODE = "vocabulary_target_viability_mode";
	public static final String VOCABULARY_THREAT_TAXONOMY_CODE = "vocabulary_cause_taxonomy_code";
	public static final String VOCABULARY_STRATEGY_TAXONOMY_CODE = "vocabulary_strategy_taxonomy_code";
	public static final String VOCABULARY_SCOPE_BOX_TYPE = "vocabulary_scope_box_type";
	public static final String VOCABULARY_STRESS_SEVERITY = "vocabulary_stress_severity";
	public static final String VOCABULARY_STRESS_SCOPE = "vocabulary_stress_scope";
	public static final String VOCABULARY_STRATEGY_IMAPACT_RATING_CODE = "vocabulary_strategy_impact_rating_code";
	public static final String VOCABULARY_STRATEGY_FEASIBILITY_RATING_CODE = "vocabulary_strategy_feasibility_rating_code";
	public static final String VOCABULARY_PRIORITY_RATING_CODE = "vocabulary_priority_rating_code";
	public static final String VOCABULARY_KEA_TYPE = "vocabulary_key_ecological_attribute_type";
	public static final String VOCABULARY_THREAT_STRESS_RATING_IRREVERSIBILITY_CODE = "vocabulary_irreversibility_code";
	public static final String VOCABULARY_THREAT_STRESS_RATING_CONTRIBUTION_CODE = "vocabulary_contribution_code";
	public static final String VOCABULARY_SIMPLE_THREAT_RATING_SCOPE_CODE = "vocabulary_simple_threat_rating_scope_code";
	public static final String VOCABULARY_SIMPLE_THREAT_RATING_SEVERITY_CODE = "vocabulary_simple_threat_rating_severitiy_code";
	public static final String VOCABULARY_SIMPLE_THREAT_RATING_IRREVERSIBILITY_CODE = "vocabulary_simple_threat_rating_irreversibility_code";
	public static final String VOCABULARY_MONTH = "vocabulary_month";
	public static final String VOCABULARY_START_MONTH = "vocabulary_start_month";
	public static final String VOCABULARY_YEAR = "vocabulary_year";
	public static final String VOCABULARY_START_YEAR = "vocabulary_start_year";
	public static final String VOCABULARY_TNC_PROJECT_PLACE_TYPES = "vocabulary_tnc_project_place_types";
	public static final String VOCABULARY_TNC_ORGANIZATIONAL_PRIORITIES = "vocabulary_tnc_organizational_priorities";
	public static final String VOCABULARY_TNC_OPERATING_UNTIS = "vocabulary_tnc_operating_units";
	public static final String VOCABULARY_TNC_TERRESTRIAL_ECO_REGION = "vocabulary_tnc_terrestrial_eco_region";
	public static final String VOCABULARY_TNC_MARINE_ECO_REGION = "vocabulary_tnc_marine_eco_region";
	public static final String VOCABULARY_TNC_FRESHWATER_ECO_REGION = "vocabulary_tnc_freshwater_eco_region";
	public static final String VOCABULARY_SHARE_OUTSIDE_TNC = "vocabulary_share_outside_tnc";
	public static final String VOCABULARY_WWF_MANAGING_OFFICES = "vocabulary_wwf_managing_offices";
	public static final String VOCABULARY_WWF_REGIONS = "vocabulary_wwf_regions";
	public static final String VOCABULARY_WWF_ECOREGIONS = "vocabulary_wwf_ecoregions";
	public static final String VOCABULARY_FOS_TRAINING_TYPE = "vocabulary_fos_training_type";
	public static final String VOCABULARY_PROGRESS_REPORT_STATUS = "vocabulary_progress_report_status";
	public static final String VOCABULARY_MEASUREMENT_TREND = "vocabulary_measurement_trend";
	public static final String VOCABULARY_MEASUREMENT_STATUS = "vocabulary_measurement_status";
	public static final String VOCABULARY_MEASUREMENT_STATUS_CONFIDENCE = "vocabulary_measurement_status_confidence";
	public static final String VOCABULARY_COUNTRIES = "vocabulary_countries";
	public static final String VOCABULARY_TEXT_BOX_Z_ORDER = "vocabulary_text_box_z_order";
	public static final String VOCABULARY_THREAT_RATING = "vocabulary_threat_rating";
	public static final String VOCABULARY_DIAGRAM_OBJECT_DATA_INCLUSION = "vocabulary_included_diagram_types";
	public static final String VOCABULARY_PLANNING_TREE_TARGET_NODE_POSITION = "vocabulary_planning_tree_target_node_position";
	public static final String VOCABULARY_THREAT_RATING_MODE = "vocabulary_threat_rating_mode";
	public static final String VOCABULARY_DASHBOARD_ROW_PROGRESS = "vocabulary_dashboard_row_progress";
	public static final String VOCABULARY_DASHBOARD_ROW_FLAGS = "vocabulary_dashboard_row_flags";
	public static final String VOCABULARY_DASHBOARD_ROW_FLAG = "vocabulary_dashboard_row_flag";
	public static final String VOCABULARY_PLANNING_TREE_OBJECTIVE_STRATEGY_NODE_ORDER = "vocabulary_planning_tree_objective_strategy_node_order";
	public static final String VOCABULARY_QUARTER_COLUMNS_VISIBILITY = "vocabulary_quarter_columns_visibility";
	public static final String VOCABULARY_WORK_PLAN_TIME_UNIT = "vocabulary_work_plan_time_unit";
	public static final String VOCABULARY_RATING_SOURCE = "vocabulary_rating_source";
	
	public static final String PROTECTED_AREA_CATEGORIES_ELEMENT_NAME = ProjectMetadata.TAG_PROTECTED_AREA_CATEGORIES;
	public static final String RESOURCE_TYPE_ELEMENT_NAME = ProjectResource.TAG_RESOURCE_TYPE;
	public static final String RESOURCE_ROLE_CODES_ELEMENT_NAME = ProjectResource.TAG_ROLE_CODES;
	public static final String HIDDEN_TYPES_ELEMENT_NAME = DiagramObject.TAG_HIDDEN_TYPES;
	public static final String TAGGED_OBJECT_SET_ELEMENT_NAME = "TaggedObjectSet";
	public static final String KEA_ID_ELEMENT_NAME = "KeyEcologicalAttribute";
	public static final String SUB_TARGET_ID_ELEMENT_NAME = "SubTarget";
	public static final String THREAT_ID_ELEMENT_NAME = "Threat";
	private static final String FUNDING_SOURCE_ID_ELEMENT_NAME = "FundingSource";
	private static final String ACCOUNTING_CODE_ID_ELEMENT_NAME = "AccountingCode";
	public static final String ACITIVTY_ID_ELEMENT_NAME = "Activity";
	public static final String OBJECTIVE_ID_ELEMENT_NAME = "Objective";
	public static final String GOAL_ELEMENT_NAME = "Goal";
	public static final String PROGRESS_REPORT_ID_ELEMENT_NAME = "ProgressReport";
	public static final String PROGRESS_PERCENT_ID_ELEMENT_NAME = "ProgressPercent";
	public static final String EXPENSE_ASSIGNMENT_ID_ELEMENT_NAME = "ExpenseAssignment";
	public static final String RESOURCE_ASSIGNMENT_ID_ELEMENT_NAME = "ResourceAssignment";
	public static final String RESOURCE_ID_ELEMENT_NAME = "Resource";
	public static final String INDICATOR_ID_ELEMENT_NAME = "Indicator";
	public static final String BIODIVERSITY_TARGET_ID_ELEMENT_NAME = "BiodiversityTarget";
	public static final String ACTIVITY_ID_ELEMENT_NAME = "Acitivity";
	public static final String MEASUREMENT_ID_ELEMENT_NAME = "Measurement";
	public static final String DIAGRAM_FACTOR_FONT_SIZE_ELEMENT_NAME = DiagramFactor.TAG_FONT_SIZE;
	public static final String DIAGRAM_FACTOR_FONT_STYLE_ELEMENT_NAME = DiagramFactor.TAG_FONT_STYLE;
	public static final String DIAGRAM_FACTOR_BACKGROUND_COLOR_ELEMENT_NAME = DiagramFactor.TAG_BACKGROUND_COLOR;
	public static final String DIAGRAM_FACTOR_FOREGROUND_COLOR_ELEMENT_NAME = DiagramFactor.TAG_FOREGROUND_COLOR;
	public static final String DIAGRAM_LINK_COLOR_ELEMENT_NAME = DiagramLink.TAG_COLOR;
	public static final String BIODIVERSITY_TARGET_HABITAT_ASSOCIATION_ELEMENT_NAME = Target.TAG_HABITAT_ASSOCIATION;
	public static final String TARGET_STATUS_ELEMENT_NAME = "ViabilityStatus";
	public static final String TARGET_VIABILITY_MODE_ELEMENT_NAME = AbstractTarget.TAG_VIABILITY_MODE;
	public static final String STRATEGY_TAXONOMY_ELEMENT_NAME = Strategy.TAG_TAXONOMY_CODE;
	public static final String STRATEGY_IMPACT_RATING_ELEMENT_NAME = Strategy.TAG_IMPACT_RATING;
	public static final String STRATEGY_FEASIBILITY_RATING_ELEMENT_NAME = Strategy.TAG_FEASIBILITY_RATING;
	public static final String SCOPE_BOX_COLOR_ELEMENT_NAME = "ScopeBoxTypeCode";
	public static final String STRESS_SEVERITY_ELEMENT_NAME = Stress.TAG_SEVERITY;
	public static final String STRESS_SCOPE_ELEMENT_NAME = Stress.TAG_SCOPE;
	public static final String STRESS_ID_ELEMENT_NAME = "Stress";
	private static final String CAUSE_ID_ELEMENT_NAME = "Cause";
	public static final String THREAT_STRESS_RATING_IRREVERSIBILITY_CODE = ThreatStressRating.TAG_IRREVERSIBILITY;
	public static final String THREAT_STRESS_RATING_CONTRIBUTION_CODE = ThreatStressRating.TAG_CONTRIBUTION;
	public static final String TNC_DATABASE_DOWNLOAD_DATE = "DatabaseDownloadDate";
	public static final String TNC_PLANNING_TEAM_COMMENTS = "PlanningTeamComments";
	public static final String TNC_LESSONS_LEARNED = "LessonsLearned";
	public static final String TNC_PROJECT_PLACE_TYPES = "TNCProjectPlaceTypes";
	public static final String TNC_ORGANIZATIONAL_PRIORITIES = "TNCOrganizationalPriorities";
	public static final String TNC_OPERATING_UNITS = "TNCOperatingUnits";
	public static final String TNC_TERRESTRIAL_ECO_REGION = "TNCTerrestrialEcoRegion";
	public static final String TNC_MARINE_ECO_REGION = "TNCMarineEcoRegion";
	public static final String TNC_FRESHWATER_ECO_REGION = "TNCFreshwaterEcoRegion";
	public static final String PROJECT_SHARE_OUTSIDE_ORGANIZATION = "ShareOutsideOrganization";
	public static final String PROGRESS_REPORT_STATUS = ProgressReport.TAG_PROGRESS_STATUS;
	public static final String MEASUREMENT_TREND = Measurement.TAG_TREND;
	public static final String MEASUREMENT_STATUS = Measurement.TAG_STATUS;	
	public static final String MEASUREMENT_STATUS_CONFIDENCE = Measurement.TAG_STATUS_CONFIDENCE;
	public static final String COUNTRIES = ProjectMetadata.TAG_COUNTRIES;
	public static final String CODE_ELEMENT_NAME = "code";
	public static final String METHOD = "Method";
}

