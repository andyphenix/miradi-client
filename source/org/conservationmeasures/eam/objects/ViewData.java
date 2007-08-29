/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import java.text.ParseException;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.objectdata.CodeListData;
import org.conservationmeasures.eam.objectdata.IntegerData;
import org.conservationmeasures.eam.objectdata.ORefData;
import org.conservationmeasures.eam.objectdata.ORefListData;
import org.conservationmeasures.eam.objectdata.StringData;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class ViewData extends BaseObject
{
	public ViewData(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse);
		clear();
	}

	public ViewData(BaseId idToUse)
	{
		super(idToUse);
		clear();
	}
	
	public ViewData(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new BaseId(idAsInt), json);
	}

	public ViewData(int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(new BaseId(idAsInt), json);
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
	
	public ORef getCurrentConceptualModelRef()
	{
		return currentConceptualModelRef.getRawRef();
	}
	
	public ORef getCurrentResutlstChainRef()
	{
		return currentResultsChainRef.getRawRef();
	}
	
	public void setCurrentTab(int newTab) throws Exception
	{
		currentTab.set(Integer.toString(newTab));
	}
	
	public int getCurrentTab()
	{
		return currentTab.asInt();
	}

	private String getCurrentMode()
	{
		return currentMode.get();
	}

	public int getType()
	{
		return getObjectType();
	}
	
	public String getTypeName()
	{
		return OBJECT_NAME;
	}

	public static int getObjectType()
	{
		return ObjectType.VIEW_DATA;
	}
	
	
	public static boolean canOwnThisType(int type)
	{
		return false;
	}
	
	
	public static boolean canReferToThisType(int type)
	{
		return Factor.isFactor(type);
	}
	
	
	public ORefList getReferencedObjects(int objectType)
	{
		ORefList list = super.getReferencedObjects(objectType);
		list.addAll(chainModeFactorRefs.getORefList(objectType));
		return list;
	}
	
	void clear()
	{
		super.clear();
		currentMode = new StringData();
		chainModeFactorRefs = new ORefListData();
		currentTab = new IntegerData();
		currentSortBy = new StringData();
		currentSortDirecton = new StringData();
		expandedNodesList = new ORefListData();
		currentResultsChainRef = new ORefData();
		currentConceptualModelRef = new ORefData();
		diagramHiddenTypes = new CodeListData();
		planningHiddenRowTypes = new CodeListData();
		planningHiddenColumnTypes = new CodeListData();
		
		addField(TAG_CURRENT_CONCEPTUAL_MODEL_REF, currentConceptualModelRef);
		addField(TAG_CURRENT_RESULTS_CHAIN_REF, currentResultsChainRef);
		addField(TAG_CURRENT_MODE, currentMode);
		addField(TAG_CHAIN_MODE_FACTOR_REFS, chainModeFactorRefs);
		addField(TAG_CURRENT_TAB, currentTab);
		addField(TAG_CURRENT_SORT_BY, currentSortBy);
		addField(TAG_CURRENT_SORT_DIRECTION, currentSortDirecton);
		addField(TAG_CURRENT_EXPANSION_LIST, expandedNodesList);
		addField(TAG_DIAGRAM_HIDDEN_TYPES, diagramHiddenTypes);
		addField(TAG_PLANNING_HIDDEN_ROW_TYPES, planningHiddenRowTypes);
		addField(TAG_PLANNING_HIDDEN_COL_TYPES, planningHiddenColumnTypes);
	}

	public static final String TAG_CURRENT_CONCEPTUAL_MODEL_REF = "CurrentConceptualModelRef";
	public static final String TAG_CURRENT_RESULTS_CHAIN_REF = "CurrentResultsChainRef";
	public static final String TAG_CURRENT_MODE = "CurrentMode";
	public static final String TAG_CHAIN_MODE_FACTOR_REFS = "ChainModeFactorRefs";
	public static final String TAG_CURRENT_TAB = "CurrentTab";
	public static final String TAG_CURRENT_SORT_BY = "CurrentSortBy";
	public static final String TAG_CURRENT_SORT_DIRECTION = "CurrentSortDirecton";
	public static final String TAG_CURRENT_EXPANSION_LIST  = "CurrentExpansionList";
	public static final String TAG_DIAGRAM_HIDDEN_TYPES = "DiagramHiddenTypes";
	public static final String TAG_PLANNING_HIDDEN_ROW_TYPES = "PlanningHiddenRowTypes";
	public static final String TAG_PLANNING_HIDDEN_COL_TYPES = "PlanningHiddenColumnTypes"; 
	
	public static final String MODE_DEFAULT = "";
	public static final String MODE_STRATEGY_BRAINSTORM = "StrategyBrainstorm";

	public static final String SORT_ASCENDING = "ASCENDING";
	public static final String SORT_DESCENDING = "DESCENDING";
	public static final String SORT_SUMMARY = "SUMMARY";
	public static final String SORT_TARGETS = "TARGETS";
	public static final String SORT_THREATS = "THREATS";
	
	public static final String OBJECT_NAME = "ViewData";
	
	private IntegerData currentTab;
	private StringData currentMode;
	private ORefListData chainModeFactorRefs;
	private StringData currentSortBy;
	private StringData currentSortDirecton;
	private ORefData currentResultsChainRef;
	private ORefData currentConceptualModelRef;
	private ORefListData expandedNodesList;
	CodeListData diagramHiddenTypes;
	CodeListData planningHiddenRowTypes;
	CodeListData planningHiddenColumnTypes;
}
