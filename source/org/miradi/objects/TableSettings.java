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

import java.util.HashMap;
import java.util.Vector;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.CodeStringMap;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.questions.SortDirectionQuestion;
import org.miradi.questions.WorkPlanVisibleRowsQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.EnhancedJsonObject;

public class TableSettings extends BaseObject
{
	public TableSettings(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse);
		clear();
	}
		
	public TableSettings(ObjectManager objectManager, int idAsInt, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new BaseId(idAsInt), json);
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
	
	public CodeStringMap getColumnWidthMap()
	{
		return getStringStringMapData(TAG_COLUMN_WIDTHS);
	}
	
	public int getRowHeight()
	{
		return getIntegerData(TAG_ROW_HEIGHT);
	}
	
	public CodeList getColumnSequenceCodes()
	{
		return getTagListData(TAG_COLUMN_SEQUENCE_CODES);
	}
	
	public Vector<DateUnit> getDateUnitList()
	{
		return getDateUnitListData(TAG_DATE_UNIT_LIST_DATA);
	}
	
	public Vector<ORefList> getExpandedRefListList() throws Exception
	{
		return getRefListListData(TAG_TREE_EXPANSION_LIST);
	}
	
	public CodeStringMap getTableSettingsMap()
	{
		return getStringStringMapData(TAG_TABLE_SETTINGS_MAP);
	}
	
	public CodeList getCodeListFromTableSettingsMap(String codeListKey) throws Exception
	{
		HashMap<String, String> settingsMap = getTableSettingsMap().toHashMap();
		if (settingsMap.containsKey(codeListKey))
			return new CodeList(settingsMap.get(codeListKey));

		return new CodeList();
	}
	
	public CommandSetObjectData createCommandToUpdateDateUnitList(CodeList dateUnits)
	{
		return new CommandSetObjectData(this, TAG_DATE_UNIT_LIST_DATA, dateUnits.toString());
	}

	public static int getObjectType()
	{
		return ObjectType.TABLE_SETTINGS;
	}
	
	public String getUniqueIdentifier()
	{
		return getData(TAG_TABLE_IDENTIFIER);
	}
	
	public static boolean exists(Project projectToUse, String uniqueTableIdentifier)
	{
		TableSettings foundTableSettings = find(projectToUse, uniqueTableIdentifier);
		return foundTableSettings != null;
	}
	
	public static TableSettings findOrCreate(Project projectToUse, String uniqueTableIdentifier) throws Exception
	{
		TableSettings foundTableSettings = find(projectToUse, uniqueTableIdentifier);
		if (foundTableSettings != null)
			return foundTableSettings;
		
		ORef newlyCreateTableSettings = projectToUse.createObject(TableSettings.getObjectType());
		projectToUse.setObjectData(newlyCreateTableSettings, TableSettings.TAG_TABLE_IDENTIFIER, uniqueTableIdentifier);
		
		return TableSettings.find(projectToUse, newlyCreateTableSettings);
	}

	public static TableSettings find(Project projectToUse,	String uniqueTableIdentifier)
	{
		ORefList tableSettingsRefs = projectToUse.getTableSettingsPool().getORefList();
		for(int index = 0; index < tableSettingsRefs.size(); ++index)
		{
			TableSettings tableSettings = find(projectToUse, tableSettingsRefs.get(index));
			String thisTableIdentifier = tableSettings.getData(TableSettings.TAG_TABLE_IDENTIFIER);
			if (uniqueTableIdentifier.equals(thisTableIdentifier))
				return tableSettings;
		}
		
		return null;
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == getObjectType();
	}
	
	public static TableSettings find(ObjectManager objectManager, ORef tableSettingsRef)
	{
		return (TableSettings) objectManager.findObject(tableSettingsRef);
	}
	
	public static TableSettings find(Project project, ORef tableSettingsRef)
	{
		return find(project.getObjectManager(), tableSettingsRef);
	}
	
	@Override
	void clear()
	{
		super.clear();

		createUserTextField(TAG_TABLE_IDENTIFIER);
		createIntegerField(TAG_ROW_HEIGHT);
		createDateUnitListField(TAG_DATE_UNIT_LIST_DATA);
		createStringStringMapField(TAG_TABLE_SETTINGS_MAP);
		createChoiceField(TAG_WORK_PLAN_VISIBLE_NODES_CODE, getQuestion(WorkPlanVisibleRowsQuestion.class));
		
		createRefListListField(TAG_TREE_EXPANSION_LIST);
		setNonUserField(TAG_TREE_EXPANSION_LIST);
		createTagListField(TAG_COLUMN_SEQUENCE_CODES);
		setNonUserField(TAG_COLUMN_SEQUENCE_CODES);
		createStringStringMapField(TAG_COLUMN_WIDTHS);
		setNonUserField(TAG_COLUMN_WIDTHS);
		createCodeField(TAG_COLUMN_SORT_TAG);
		setNonUserField(TAG_COLUMN_SORT_TAG);
		createChoiceField(TAG_COLUMN_SORT_DIRECTION, getQuestion(SortDirectionQuestion.class));
		setNonUserField(TAG_COLUMN_SORT_DIRECTION);
		
	}
	
	public static final String OBJECT_NAME = "TableSettings";
	
	public static final String WORK_PLAN_PROJECT_RESOURCE_FILTER_CODELIST_KEY = "WorkPlanProjectResourceFilterCodeListKey";
	public static final String WORK_PLAN_BUDGET_COLUMNS_CODELIST_KEY = "WorkPlanBudgetColumnCodeListKey";
	
	public static final String TAG_TABLE_IDENTIFIER = "TableIdentifier";
	public static final String TAG_COLUMN_SEQUENCE_CODES = "ColumnSequenceCodes";
	public static final String TAG_COLUMN_WIDTHS = "ColumnWidths";
	public static final String TAG_ROW_HEIGHT = "RowHeight";
	public static final String TAG_TREE_EXPANSION_LIST  = "TreeExpansionList";
	public static final String TAG_DATE_UNIT_LIST_DATA = "DateUnitListData";
	public static final String TAG_TABLE_SETTINGS_MAP = "TagTableSettingsMap";
	public static final String TAG_COLUMN_SORT_TAG = "ColumnSortTag";
	public static final String TAG_COLUMN_SORT_DIRECTION = "ColumnSortDirection";
	public static final String TAG_WORK_PLAN_VISIBLE_NODES_CODE = "WorkPlanVisibleNodesCode";
}
