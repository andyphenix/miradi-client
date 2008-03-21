/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.dialogfields;

import org.miradi.ids.BaseId;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;

public class ResourcesOverridenListField extends RelevancyOverrideListField
{
	public ResourcesOverridenListField(Project projectToUse, int objectTypeToUse, BaseId objectIdToUse, ChoiceQuestion questionToUse)
	{
		super(projectToUse, objectTypeToUse, objectIdToUse, questionToUse, BaseObject.TAG_WHO_OVERRIDE_REFS);
	}
	
	public String getText()
	{
		try
		{
			return new ORefList(refListEditor.getText()).toString();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return "";
		}
	}

	public void setText(String codes)
	{
		try
		{
			BaseObject foundObject = getProject().findObject(getORef());
			ORefSet overridenWho = foundObject.getOverridenWho();
			refListEditor.setText(new ORefList(overridenWho).toString());
		}
		catch(Exception e)
		{
			//FIXME do something else with this exception
			EAM.logException(e);
		}	
	}
}
