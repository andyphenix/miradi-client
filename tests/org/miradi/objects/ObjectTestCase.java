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

import java.awt.Point;
import java.util.Vector;

import org.martus.util.MultiCalendar;
import org.martus.util.UnicodeStringReader;
import org.martus.util.UnicodeStringWriter;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.ids.TaskId;
import org.miradi.main.EAM;
import org.miradi.main.TestCaseWithProject;
import org.miradi.objectdata.AbstractUserTextDataWithHtmlFormatting;
import org.miradi.objectdata.BaseIdData;
import org.miradi.objectdata.BooleanData;
import org.miradi.objectdata.ChoiceData;
import org.miradi.objectdata.CodeData;
import org.miradi.objectdata.CodeListData;
import org.miradi.objectdata.CodeToChoiceMapData;
import org.miradi.objectdata.CodeToCodeListMapData;
import org.miradi.objectdata.CodeToCodeMapData;
import org.miradi.objectdata.CodeToUserStringMapData;
import org.miradi.objectdata.DateData;
import org.miradi.objectdata.DateUnitEffortListData;
import org.miradi.objectdata.DateUnitListData;
import org.miradi.objectdata.IdListData;
import org.miradi.objectdata.IntegerData;
import org.miradi.objectdata.NumberData;
import org.miradi.objectdata.ORefData;
import org.miradi.objectdata.ObjectData;
import org.miradi.objectdata.PointListData;
import org.miradi.objectdata.PseudoQuestionData;
import org.miradi.objectdata.PseudoStringData;
import org.miradi.objectdata.RefListData;
import org.miradi.objectdata.RefListListData;
import org.miradi.objectdata.RelevancyOverrideSetData;
import org.miradi.objectdata.SingleLineUserTextData;
import org.miradi.objectdata.TagListData;
import org.miradi.objectdata.TaxonomyClassificationListData;
import org.miradi.objecthelpers.CodeToChoiceMap;
import org.miradi.objecthelpers.CodeToCodeListMap;
import org.miradi.objecthelpers.CodeToCodeMap;
import org.miradi.objecthelpers.CodeToUserStringMap;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.RelevancyOverride;
import org.miradi.objecthelpers.RelevancyOverrideSet;
import org.miradi.objecthelpers.TaxonomyClassification;
import org.miradi.objecthelpers.TaxonomyClassificationList;
import org.miradi.project.Project;
import org.miradi.project.ProjectForTesting;
import org.miradi.project.ProjectLoader;
import org.miradi.project.ProjectSaver;
import org.miradi.project.TestDateUnit;
import org.miradi.questions.InternalQuestionWithoutValues;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.schemas.CauseSchema;
import org.miradi.utils.CodeList;
import org.miradi.utils.DateUnitEffort;
import org.miradi.utils.DateUnitEffortList;

public class ObjectTestCase extends TestCaseWithProject
{
	public ObjectTestCase(String name)
	{
		super(name);
	}

	public void verifyFields(int objectType) throws Exception
	{
		verifyObjectCount(objectType);
		BaseObject object = createObject(objectType);
		verifyTypeName(object);
		
		Vector<String> fieldTags = object.getStoredFieldTags();
		for(int i = 0; i < fieldTags.size(); ++i)
		{
			verifyShortLabelField(object, fieldTags.get(i));
			verifyFieldLifecycle(getProject(), object, fieldTags.get(i));		
			object.clear();
			assertTrue("object is not empty?", object.isEmpty());
		}
		
		verifyLoadPool(objectType);
	}
	
	private void verifyObjectCount(int objectType)
	{
		boolean isLessThanObjectTypeCount = objectType < ObjectType.OBJECT_TYPE_COUNT;
		assertTrue("object id not less than count", isLessThanObjectTypeCount);
	}

	protected BaseObject createObject(int objectType) throws Exception
	{
		BaseId id = getProject().createObjectAndReturnId(objectType, BaseId.INVALID);
		
		return getProject().findObject(objectType, id);
	}

	private void verifyTypeName(BaseObject object)
	{
		if(isObjectWhoseTypeNameVaries(object))
			return;
		
		int objectType = object.getType();
		String internalTypeName = object.getObjectManager().getInternalObjectTypeName(objectType);
		assertEquals(internalTypeName, object.getTypeName());
		EAM.setLogToString();
		try
		{
			EAM.fieldLabel(objectType, internalTypeName);
			assertEquals("Logged a problem?", 0, EAM.getLoggedString().length());
		}
		finally
		{
			EAM.setLogToConsole();
		}
	}

	private boolean isObjectWhoseTypeNameVaries(BaseObject object)
	{
		if(Task.is(object))
			return true;
		if(Cause.is(object))
			return true;
		
		return false;
	}

	public ProjectForTesting createAndOpenProject() throws Exception
	{
		ProjectForTesting project = ProjectForTesting.createProjectWithDefaultObjects(getName());
		return project;
	}
	 
	private void verifyLoadPool(int objectType) throws Exception
	{
		BaseId id = BaseId.INVALID;
		id = getProject().createObjectAndReturnId(objectType, BaseId.INVALID);
		UnicodeStringWriter writer = UnicodeStringWriter.create();
		ProjectSaver.saveProject(getProject(), writer);
		getProject().clear();
		ProjectLoader.loadProject(new UnicodeStringReader(writer.toString()), getProject());
		BaseObject object = getProject().findObject(objectType, id);
		assertNotNull("Didn't load pool?", object);
	}
	
	private void verifyFieldLifecycle(Project project, BaseObject object, String tag) throws Exception
	{
		if(tag.equals(BaseObject.TAG_ID))
			return;
		
		if (object.isPseudoField(tag))
			return;
		
		String sampleData = getSampleData(object, tag);
		String emptyData = getEmptyData(object, tag);

		assertTrue("field didn't start out empty?", object.getField(tag).isEmpty());
		assertTrue("current value is not empty?", object.getField(tag).isCurrentValue(""));
		assertEquals("didn't default " + tag + " empty?", emptyData, object.getData(tag));
		try
		{
			object.setData(tag, sampleData);	
		}
		catch(Exception e)
		{
			System.out.println("need sample data for " + object.getField(tag).getClass().getSimpleName());
			throw e;
		}
		assertFalse("claims to be empty?", object.getField(tag).isEmpty());
		assertTrue("current contents mismatch?", object.getField(tag).isCurrentValue(sampleData));
		
		assertEquals("did't set " + tag + "?", sampleData, object.getData(tag));
		
		Vector<CommandSetObjectData> commandsToClear = object.createCommandsToClear();
		for(int i = 0; i < commandsToClear.size(); ++i)
		{
			assertNotEquals("Tried to clear Id?", BaseObject.TAG_ID, commandsToClear.get(i).getFieldTag());
			project.executeCommand(commandsToClear.get(i));
		}
		assertEquals("Didn't clear " + tag + "?", emptyData, object.getData(tag));
		project.undo();
		assertEquals("Didn't restore " + tag + "?", sampleData, object.getData(tag));
	}

	private void verifyShortLabelField(BaseObject object, String tag) throws Exception
	{
		if (!tag.equals("ShortLabel"))
			return;
		
		object.setData(tag, "someShortLabelValue");
		assertEquals("didnt return correct value for field " + tag + ":?", "someShortLabelValue", object.getShortLabel());
		
		object.setData(tag, "");
	}

	private String getEmptyData(BaseObject object, String tag)
	{
		ObjectData field = object.getField(tag);
		if(field instanceof ORefData)
			return ORef.INVALID.toString();
		
		if (field instanceof IntegerData)
			return new IntegerData("tag").toString();
		
		return "";
	}
	
	private String getSampleData(BaseObject object, String tag) throws Exception
	{
		ObjectData field = object.getField(tag);
		if(field instanceof IdListData)
		{
			IdList list = new IdList(0);
			list.add(new BaseId(7));
			return list.toString();
		}
		else if(field instanceof CodeToUserStringMapData)
		{
			CodeToUserStringMap list = new CodeToUserStringMap();
			list.putUserString("A","RolaA");
			return list.toJsonString();
		}
		else if (field instanceof CodeToChoiceMapData)
		{
			CodeToChoiceMap map = new CodeToChoiceMap();
			map.putChoiceCode("a", "code");
			return map.toJsonString();
		}
		else if (field instanceof CodeToCodeListMapData)
		{
			CodeToCodeListMap map = new CodeToCodeListMap();
			CodeList randomCodes = new CodeList();
			randomCodes.add("SomeCodeA");
			randomCodes.add("SomeCodeB");
			map.putCodeList("SomeKey", randomCodes);
			
			return map.toJsonString();
		}
		else if (field instanceof CodeToCodeMapData)
		{
			CodeToCodeMap map = new CodeToCodeMap();
			map.putCode("SomeKey", "SomeCode");
			map.putCode("AnotherKey", "AnotherCode");
			
			return map.toJsonString();
		}
		else if(field instanceof BaseIdData)
		{
			return new BaseId(15).toString();
		}
		else if(field instanceof DateData)
		{
			return MultiCalendar.createFromGregorianYearMonthDay(1953, 10, 21).toString();
		}
		else if (field instanceof PointListData)
		{
			PointListData pointList = new PointListData("tag");
			pointList.add(new Point(-1, 55));
			
			return pointList.toString();
		}
		else if(field instanceof ChoiceData)
		{
			final CodeList allCodes = field.getChoiceQuestion().getAllCodes();
			return allCodes.get(allCodes.size() - 1);
		}
		else if(field instanceof CodeData)
		{
			return tag + tag;
		}
		else if(field instanceof SingleLineUserTextData)
		{
			return tag + tag;
		}
		else if(field instanceof AbstractUserTextDataWithHtmlFormatting)
		{
			return "<b>Testing HTML</b>";
		}
		else if(field instanceof PseudoQuestionData)
		{
			return "";
		}
		else if(field instanceof PseudoStringData)
		{
			return "";
		}
		else if(field instanceof ORefData)
		{
			return new ORef(ObjectType.TASK, new TaskId(283)).toString();
		}
		else if(field instanceof RefListData)
		{
			ORef test = new ORef(ObjectType.TASK, new TaskId(283));
			ORefList list = new ORefList(new ORef[] {test});
			RefListData listData = new RefListData("tag");
			listData.set(list.toString());
			return listData.toString();
		}
		else if(field instanceof CodeListData)
		{
			CodeListData codeList = new CodeListData("tag", StaticQuestionManager.getQuestion(InternalQuestionWithoutValues.class));
			codeList.add("A1");
			codeList.add("B1");
			return codeList.toString();
			
		}
		else if (field instanceof TagListData)
		{
			TagListData tagList = new TagListData("tag");
			tagList.add("Code1A");
			tagList.add("Code2A");
			
			return tagList.toString();
		}
		else if (field instanceof BooleanData)
		{
			return "1";
		}
		else if (field instanceof IntegerData)
		{
			return "3";
		}
		else if (field instanceof RelevancyOverrideSetData)
		{
			RelevancyOverride test = new RelevancyOverride(new ORef(CauseSchema.getObjectType(), new BaseId(44)), true);
			RelevancyOverrideSet overrideSet = new RelevancyOverrideSet();
			overrideSet.add(test);
			
			RelevancyOverrideSetData overrideSetData = new RelevancyOverrideSetData("tag");
			overrideSetData.set(overrideSet.toString());
			return overrideSetData.toString();
		}
		else if (field instanceof NumberData)
		{
			return "27.65";
		}
		else if (field instanceof DateUnitListData)
		{
			DateUnitListData dateUnitListData = new DateUnitListData("");
			dateUnitListData.add("2009");
			
			return dateUnitListData.toString();
		}
		else if (field instanceof DateUnitEffortListData)
		{
			DateUnitEffortList list = new DateUnitEffortList();
			DateUnit dateUnit = TestDateUnit.month12;
			list.add(new DateUnitEffort(dateUnit, 5.0));
			return list.toString();
		}
		else if (field instanceof RefListListData)
		{
			ORefList refList = new ORefList(new ORef(CauseSchema.getObjectType(), new BaseId(9999)));
			RefListListData refListListData = new RefListListData("");
			refListListData.addList(refList);
			
			return refListListData.toString();
			
		}
		else if (field instanceof TaxonomyClassificationListData)
		{
			TaxonomyClassificationList taxonomyClassificationList = new TaxonomyClassificationList();
			TaxonomyClassification taxonomyClassification = new TaxonomyClassification();
			taxonomyClassification.setTaxonomyClassificationCode("RandomCode");
			taxonomyClassification.addElementCode("elementCode1");
			taxonomyClassification.addElementCode("elementCode2");
			taxonomyClassificationList.add(taxonomyClassification);
			
			return taxonomyClassificationList.toJsonString();
		}
		else
		{
			throw new RuntimeException("Need to add sample data for " + object.getType() + ":" + tag + " type: " + field.getClass().getSimpleName());
		}
	}
}

