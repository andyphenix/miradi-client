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

import org.miradi.commands.CommandSetObjectData;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.Factor;
import org.miradi.objects.TaggedObjectSet;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.TaggedObjectSetQuestion;

public class FactorTagListEditor extends AbstractListComponent implements CommandExecutedListener
{
	public FactorTagListEditor(Project projectToUse, Factor selectedFactorToUse)
	{
		super(createQuestion(projectToUse));
		
		project = projectToUse;
		selectedFactor = selectedFactorToUse;
		project.addCommandExecutedListener(this);
	
		updateCheckboxesToMatchDatabase();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		getProject().removeCommandExecutedListener(this);
	}
	
	protected void valueChanged(ChoiceItem choiceItem, boolean isSelected) throws Exception
	{
		String refAsCode = choiceItem.getCode();
		ORef taggedObjectSetRef = ORef.createFromString(refAsCode);
		ORefList taggedSet = getTaggedObjectRefs(taggedObjectSetRef);
		taggedSet.remove(getFactorToTag().getRef());
		if (isSelected)
			taggedSet.add(getFactorToTag().getRef());
		
		taggedSet.sort();
		CommandSetObjectData tagFactorCommand = new CommandSetObjectData(taggedObjectSetRef, TaggedObjectSet.TAG_TAGGED_OBJECT_REFS, taggedSet.toString());
		getProject().executeCommand(tagFactorCommand);
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		if (isTaggedObjectRelatedCommand(event))
			updateCheckboxesToMatchDatabase();		
	}
	
	private boolean isTaggedObjectRelatedCommand(CommandExecutedEvent event)
	{
		if (event.isCreateCommandForThisType(TaggedObjectSet.getObjectType()))
			return true;

		if (event.isSetDataCommandWithThisTypeAndTag(TaggedObjectSet.getObjectType(), TaggedObjectSet.TAG_LABEL))
			return true;
		
		if (event.isSetDataCommandWithThisTypeAndTag(TaggedObjectSet.getObjectType(), TaggedObjectSet.TAG_TAGGED_OBJECT_REFS))
			return true;
		
		return false;
	}

	public void updateCheckboxesToMatchDatabase()
	{
		reloadQuestion();
		rebuildCheckBoxes();
		for (int checkBoxIndex = 0; checkBoxIndex < checkBoxes.length; ++checkBoxIndex)
		{
			String label = checkBoxes[checkBoxIndex].getText();
			ChoiceItem choiceItem = getQuestion().findChoiceByLabel(label);
			ORef taggedObjectSetRef = ORef.createFromString(choiceItem.getCode());
			ORefList taggedSet = getTaggedObjectRefs(taggedObjectSetRef);
			boolean isSelected = taggedSet.contains(getFactorToTag().getRef());
			checkBoxes[checkBoxIndex].setSelected(isSelected);
		}
	}

	private void reloadQuestion()
	{
		((TaggedObjectSetQuestion)getQuestion()).reloadQuestion();
	}

	private ORefList getTaggedObjectRefs(ORef taggedObjectSetRef)
	{
		TaggedObjectSet taggedObjectSet = TaggedObjectSet.find(getProject(), taggedObjectSetRef);
		return new ORefList(taggedObjectSet.getTaggedObjectRefs());
	}
		
	private Project getProject()
	{
		return project;
	}
	
	private Factor getFactorToTag()
	{
		return selectedFactor;
	}
	
	private static TaggedObjectSetQuestion createQuestion(Project projectToUse)
	{
		return new TaggedObjectSetQuestion(projectToUse);
	}
	
	private Project project;
	private Factor selectedFactor;
}
