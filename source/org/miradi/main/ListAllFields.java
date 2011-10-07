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
package org.miradi.main;

import java.io.IOException;
import java.util.Vector;

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.CreateDiagramFactorLinkParameter;
import org.miradi.objecthelpers.CreateDiagramFactorParameter;
import org.miradi.objecthelpers.CreateFactorLinkParameter;
import org.miradi.objecthelpers.CreateObjectParameter;
import org.miradi.objecthelpers.CreateThreatStressRatingParameter;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.FactorLink;
import org.miradi.objects.Task;
import org.miradi.objects.ThreatStressRating;
import org.miradi.project.Project;
import org.miradi.utils.NullProgressMeter;
import org.miradi.utils.Translation;

public class ListAllFields
{
	public static void main(String[] args) throws Exception
	{
		Miradi.addThirdPartyJarsToClasspath();
		Translation.initialize();
		
		Project project = new Project();
		project.getDatabase().setMemoryDataLocation("Memory");

		project.createOrOpenWithDefaultObjectsAndDiagramHelp("ListAllFields", new NullProgressMeter());
		listFieldsToConsole(project);
		project.close();
	}

	private static void listFieldsToConsole(Project project) throws IOException, Exception
	{
		for(int type = 0; type < ObjectType.OBJECT_TYPE_COUNT; ++type)
		{
			if(project.getPool(type) == null)
				continue;
			BaseObject object = createObject(project, type);
			
			// NOTE: We create a task with no owner, so it doesn't have a type
			if(type == Task.getObjectType())
				System.out.println("Task/Activity/Method");
			else
				showObjectName(object);
			
			Vector<String> fieldTags = object.getStoredFieldTags();
			for(int field = 0; field < fieldTags.size(); ++field)
			{
				String tag = fieldTags.get(field);
				showField(object, tag);
			}
		}
	}

	private static BaseObject createObject(Project project, int type) throws Exception
	{
		CreateObjectParameter extraInfo = createExtraInfo(type);
		ORef ref = new ORef(type, project.createObjectAndReturnId(type, extraInfo));
		BaseObject object = project.findObject(ref);
		return object;
	}
	
	

	private static CreateObjectParameter createExtraInfo(int type)
	{
		if(type == DiagramFactor.getObjectType())
			return new CreateDiagramFactorParameter(ORef.INVALID); 
		
		if(type == FactorLink.getObjectType())
			return new CreateFactorLinkParameter(ORef.INVALID, ORef.INVALID);
		
		if(type == DiagramLink.getObjectType())
		{
			BaseId factorLinkId = BaseId.INVALID;
			BaseId fromId = BaseId.INVALID;
			BaseId toId = BaseId.INVALID;
			return new CreateDiagramFactorLinkParameter(factorLinkId, fromId, toId);
		}
		
		if(type == ThreatStressRating.getObjectType())
		{
			return new CreateThreatStressRatingParameter(ORef.INVALID, ORef.INVALID);
		}
		
		return null;
	}

	private static void showField(BaseObject object, String tag)
	{
		// NOTE: Hack to avoid exceptions without removing printStackTrace from EAM.fieldLabel
		EAM.setLogToString();
		try
		{
			String fieldLabel = EAM.fieldLabel(object.getType(), tag);
			if(fieldLabel.equals(tag))
				System.out.println("  " + fieldLabel);
			else
				System.out.println("  " + fieldLabel + " (" + tag + ")");
		}
		finally
		{
			EAM.setLogToConsole();
		}
	}

	private static void showObjectName(BaseObject object)
	{
		String typeName = object.getTypeName();
		if(object.getType() == Task.getObjectType())
			typeName = "Activity/Method/Task";
		System.out.println(typeName);
	}
}
