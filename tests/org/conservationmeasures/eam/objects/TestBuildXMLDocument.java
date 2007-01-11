/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objects;

import java.io.File;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objectdata.IdListData;
import org.conservationmeasures.eam.objectdata.ObjectData;
import org.conservationmeasures.eam.objectdata.RatingData;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objectpools.EAMObjectPool;
import org.conservationmeasures.eam.project.Project;

public class TestBuildXMLDocument extends EAMTestCase
{
	public TestBuildXMLDocument(String name)
	{
		super(name);
	}

	public void testBuild() throws Exception
	{
		try
		{
			File projectFile = new File(EAM.getHomeDirectory(),"exportedProject");
			Project project = new Project();
			project.createOrOpen(projectFile);
			
			
			processObjectPool(project, ObjectType.FACTOR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void processObjectPool(Project project , int objectType) throws Exception
	{
			EAMObjectPool pool = project.getPool(objectType);
			BaseId[] baseIds = pool.getIds();
			for(int i = 0; i < baseIds.length; ++i)
			{
				EAMBaseObject object = (EAMBaseObject) project.findObject(objectType, baseIds[i]);
				String[] tags = object.getFieldTags();
				for(int tagIdx = 0; tagIdx < tags.length; ++tagIdx)
				{
					String sampleData = getSampleData(object, tags[tagIdx]);
					System.out.println(sampleData);
				}
			}
	}
	
	
	private String getSampleData(EAMBaseObject object, String tag)
	{
		ObjectData field = object.getField(tag);
		if(field instanceof IdListData)
		{
			IdList list = new IdList();
			list.add(new BaseId(7));
			return list.toString();
		}
		
		if(field instanceof RatingData)
		{
			return "3";
		}
		
		return tag + tag;
	}
}

