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
package org.miradi.objects;

import java.text.ParseException;

import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.ids.BaseId;
import org.miradi.objectdata.CodeListData;
import org.miradi.objectdata.IntegerData;
import org.miradi.objectdata.ORefData;
import org.miradi.objectdata.ORefListData;
import org.miradi.objectdata.StringData;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.questions.InternalQuestionWithoutValues;
import org.miradi.questions.WorkPlanCategoryTypesQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.EnhancedJsonObject;

public class ViewData extends BaseObject
{
	public ViewData(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse);

		clear();
	}

	public ViewData(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new BaseId(idAsInt), json);
	}

	public Command[] buildCommandsToAddNode(ORef oRefToAdd) throws ParseException
	{
		if(getCurrentMode().equals(MODE_DEFAULT))
			return new Command[0];
		
		CommandSetObjectData cmd = CommandSetObjectData.createAppendORefCommand(this, TAG_CHAIN_MODE_FACTOR_REFS, oRefToAdd);
		return new Command[] {cmd};
	}

	public Command[] buildCommandsToRemoveNode(ORef oRefToRemove) throws ParseException
	{
		if(getCurrentMode().equals(MODE_DEFAULT))
			return new Command[0];
		
		ORefList currentORefs = new ORefList(getData(TAG_CHAIN_MODE_FACTOR_REFS));
		if(!currentORefs.contains(oRefToRemove))
			return new Command[0];
		
		CommandSetObjectData cmd = CommandSetObjectData.createRemoveORefCommand(this, TAG_CHAIN_MODE_FACTOR_REFS, oRefToRemove);
		return new Command[] {cmd};
	}
	
	@Override
	public boolean isRefList(String tag)
	{
		if (tag.equals(TAG_CHAIN_MODE_FACTOR_REFS))
			return true;
		
		return super.isRefList(tag);
	}
	
	public CodeList getBudgetRollupReportLevelTypes()
	{
		return budgetRollupReportLevelTypes.getCodeList();
	}
	
	public ORef getCurrentConceptualModelRef()
	{
		return currentConceptualModelRef.getRef();
	}
	
	public ORef getCurrentResultsChainRef()
	{
		return currentResultsChainRef.getRef();
	}
	
	public ORef getTreeConfigurationRef()
	{
		return treeConfigurationRef.getRef();
	}
	
	private PlanningTreeConfiguration getTreeConfiguration()
	{
		return ObjectTreeTableConfiguration.find(getProject(), getTreeConfigurationRef());
	}
	
	public boolean shouldIncludeResultsChain() throws Exception
	{
		return getTreeConfiguration().shouldIncludeResultsChain();
	}

	public boolean shouldIncludeConceptualModelPage() throws Exception
	{
		return getTreeConfiguration().shouldIncludeConceptualModelPage();
	}
	
	public void setCurrentTab(int newTab) throws Exception
	{
		currentTab.set(Integer.toString(newTab));
	}
	
	public int getCurrentTab()
	{
		return currentTab.asInt();
	}

	public String getCurrentMode()
	{
		return currentMode.get();
	}

	@Override
	public int getType()
	{
		return getObjectType();
	}
	
	@Override
	public String getTypeName()
	{
		return OBJECT_NAME;
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	public static int getObjectType()
	{
		return ObjectType.VIEW_DATA;
	}
	
	
	public static ViewData find(ObjectManager objectManager, ORef viewDataRef)
	{
		return (ViewData) objectManager.findObject(viewDataRef);
	}
	
	public static ViewData find(Project project, ORef viewDataRef)
	{
		return find(project.getObjectManager(), viewDataRef);
	}
	
	public static boolean is(ORef ref)
	{
		return ref.getObjectType() == getObjectType();
	}

	@Override
	void clear()
	{
		super.clear();

		currentMode = new StringData(TAG_CURRENT_MODE);
		chainModeFactorRefs = new ORefListData(TAG_CHAIN_MODE_FACTOR_REFS);
		currentTab = new IntegerData(TAG_CURRENT_TAB);
		currentResultsChainRef = new ORefData(TAG_CURRENT_RESULTS_CHAIN_REF);
		currentConceptualModelRef = new ORefData(TAG_CURRENT_CONCEPTUAL_MODEL_REF);
		diagramHiddenTypes = new CodeListData(TAG_DIAGRAM_HIDDEN_TYPES, getQuestion(InternalQuestionWithoutValues.class));
		budgetRollupReportLevelTypes = new CodeListData(TAG_BUDGET_ROLLUP_REPORT_TYPES, getQuestion(WorkPlanCategoryTypesQuestion.class));
		planningSingleLevelChoice = new StringData(TAG_PLANNING_SINGLE_LEVEL_CHOICE);
		treeConfigurationRef = new ORefData(TAG_TREE_CONFIGURATION_REF);
		currentWizardStep = new StringData(TAG_CURRENT_WIZARD_STEP);
		
		addPresentationDataField(TAG_CURRENT_CONCEPTUAL_MODEL_REF, currentConceptualModelRef);
		addPresentationDataField(TAG_CURRENT_RESULTS_CHAIN_REF, currentResultsChainRef);
		addPresentationDataField(TAG_CURRENT_MODE, currentMode);
		addPresentationDataField(TAG_CHAIN_MODE_FACTOR_REFS, chainModeFactorRefs);
		addPresentationDataField(TAG_CURRENT_TAB, currentTab);
		addPresentationDataField(TAG_DIAGRAM_HIDDEN_TYPES, diagramHiddenTypes);
		addField(TAG_BUDGET_ROLLUP_REPORT_TYPES, budgetRollupReportLevelTypes);
		
		addPresentationDataField(TAG_PLANNING_SINGLE_LEVEL_CHOICE, planningSingleLevelChoice);
		addPresentationDataField(TAG_TREE_CONFIGURATION_REF, treeConfigurationRef);
		addPresentationDataField(TAG_CURRENT_WIZARD_STEP, currentWizardStep);
	}

	public static final String TAG_CURRENT_CONCEPTUAL_MODEL_REF = "CurrentConceptualModelRef";
	public static final String TAG_CURRENT_RESULTS_CHAIN_REF = "CurrentResultsChainRef";
	public static final String TAG_CURRENT_MODE = "CurrentMode";
	public static final String TAG_CHAIN_MODE_FACTOR_REFS = "ChainModeFactorRefs";
	public static final String TAG_CURRENT_TAB = "CurrentTab";
	public static final String TAG_DIAGRAM_HIDDEN_TYPES = "DiagramHiddenTypes";
	public static final String TAG_BUDGET_ROLLUP_REPORT_TYPES = "BudgetRollupReportTypes";
	
	public static final String TAG_PLANNING_SINGLE_LEVEL_CHOICE = "SingleLevelChoice";
	public static final String TAG_TREE_CONFIGURATION_REF = "CustomPlanRef";
	public static final String TAG_CURRENT_WIZARD_STEP = "CurrentWizardStep";
	
	public static final String MODE_DEFAULT = "";
	public static final String MODE_STRATEGY_BRAINSTORM = "StrategyBrainstorm";

	public static final String OBJECT_NAME = "ViewData";
	
	private IntegerData currentTab;
	private StringData currentMode;
	private ORefListData chainModeFactorRefs;
	private ORefData currentResultsChainRef;
	private ORefData currentConceptualModelRef;
	private CodeListData diagramHiddenTypes;
	private CodeListData budgetRollupReportLevelTypes;
	private StringData planningSingleLevelChoice;
	private ORefData treeConfigurationRef;
	private StringData currentWizardStep;
}
