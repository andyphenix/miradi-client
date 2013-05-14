/* 
Copyright 2005-2013, Foundations of Success, Bethesda, Maryland 
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

import java.util.Vector;

import org.miradi.main.EAM;
import org.miradi.objecthelpers.TaxonomyClassificationMap;
import org.miradi.objecthelpers.TaxonomyHelper;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.DynamicChoiceWithRootChoiceItem;
import org.miradi.utils.CodeList;

public class ReadonlyTaxonomyMultiChoiceComponent extends AbstractReadonlyChoiceComponent
{
	public ReadonlyTaxonomyMultiChoiceComponent(Project projectToUse, ChoiceQuestion questionToUse, String taxonomyAssociationCodeToUse)
	{
		super(questionToUse);
		
		project = projectToUse;
		taxonomyAssociationCode = taxonomyAssociationCodeToUse;
		taxonomyElementCodes = new CodeList();
	}

	@Override
	public String getText()
	{
		return taxonomyElementCodes.toJsonString();
	}
	
	@Override
	public void setText(String newValue)
	{
		try
		{
			taxonomyElementCodes = TaxonomyHelper.getTaxonomyElementCodes(getProject(), new TaxonomyClassificationMap(newValue), taxonomyAssociationCode);
			createAndAddReadonlyLabels(taxonomyElementCodes);
		}
		catch(Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
		}
	}
	
	@Override
	protected void createAndAddReadonlyLabels(final CodeList codeList)
	{
		try
		{
			ChoiceItem headerChoiceItem = ((DynamicChoiceWithRootChoiceItem)getQuestion()).getHeaderChoiceItem();
			Vector<ChoiceItem> childrenChoices = headerChoiceItem.getChildren();
			createAndAddReadonlyLabels(childrenChoices, codeList);
		}
		catch (Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
		}
	}

	private Project getProject()
	{
		return project;
	}
	
	private Project project;
	private CodeList taxonomyElementCodes;
	private String taxonomyAssociationCode;
}