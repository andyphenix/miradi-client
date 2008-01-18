/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import java.util.Arrays;
import java.util.Vector;

import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.project.Project;

abstract public class ObjectQuestion extends StaticChoiceQuestion
{
	public ObjectQuestion(Project project, BaseObject[] objects, String questionLabel)
	{
		super("", questionLabel, createChoiceItems(project, objects));
	}

	private static ChoiceItem[] createChoiceItems(Project project, BaseObject[] objects)
	{
		Vector choiceItems = new Vector();
		for (int i = 0; i < objects.length; ++i)
		{
			BaseObject thisObject = objects[i];
			choiceItems.add(new ChoiceItem(thisObject.getRef().toString(), thisObject.combineShortLabelAndLabel()));
		}
		
		ChoiceItem[] sortedChoiceItems = (ChoiceItem[]) choiceItems.toArray(new ChoiceItem[0]);
		Arrays.sort(sortedChoiceItems);
		
		return sortedChoiceItems;
	}
}
