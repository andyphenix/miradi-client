/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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
package org.miradi.dialogs.planning;

import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.objects.ViewData;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.CustomPlanningColumnsQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.utils.CodeList;
import org.miradi.utils.StringList;

public class ConfigurableRowColumnProvider extends AbstractPlanningTreeRowColumnProvider
{
	public ConfigurableRowColumnProvider(Project projectToUse)
	{
		super(projectToUse);
	}

	public CodeList getColumnCodesToShow() throws Exception
	{
		return getVisibleColumnsForCustomization(getCurrentViewData());
	}

	public CodeList getRowCodesToShow() throws Exception
	{
		return getVisibleRowsForCustomization(getCurrentViewData());
	}

	private CodeList getVisibleRowsForCustomization(ViewData viewData) throws Exception
	{
		PlanningTreeRowColumnProvider customization = getCurrentCustomization();
		if(customization == null)
			return new CodeList();
		return customization.getRowCodesToShow();
	}

	private CodeList getVisibleColumnsForCustomization(ViewData viewData) throws Exception
	{
		PlanningTreeRowColumnProvider customization = getCurrentCustomization();
		if(customization == null)
			return new CodeList();
		CodeList columnCodes = customization.getColumnCodesToShow();
		omitUnknownColumnTagsInPlace(viewData.getProject(), columnCodes);
		
		return columnCodes;
	}

	public boolean shouldIncludeResultsChain() throws Exception
	{
		PlanningTreeRowColumnProvider customization = getCurrentCustomization();
		if(customization == null)
			return false;
		return customization.shouldIncludeResultsChain();
	}

	public boolean shouldIncludeConceptualModelPage() throws Exception
	{
		PlanningTreeRowColumnProvider customization = getCurrentCustomization();
		if(customization == null)
			return false;
		return customization.shouldIncludeConceptualModelPage();
	}
	
	@Override
	public boolean doObjectivesContainStrategies() throws Exception
	{
		PlanningTreeRowColumnProvider customization = getCurrentCustomization();
		if(customization == null)
			return true;
		
		return customization.doObjectivesContainStrategies();
	}

	@Override
	public boolean shouldPutTargetsAtTopLevelOfTree() throws Exception
	{
		return getCurrentCustomization().shouldPutTargetsAtTopLevelOfTree();
	}

	private PlanningTreeRowColumnProvider getCurrentCustomization() throws Exception
	{
		ViewData viewData = getCurrentViewData();
		ORef customizationRef = viewData.getORef(ViewData.TAG_TREE_CONFIGURATION_REF);
		if(customizationRef.isInvalid())
			return null;
		PlanningTreeRowColumnProvider customization = (PlanningTreeRowColumnProvider)viewData.getProject().findObject(customizationRef);
		return customization;
	}

	private static void omitUnknownColumnTagsInPlace(Project project, CodeList rawCodes)
	{
		ChoiceQuestion question = StaticQuestionManager.getQuestion(CustomPlanningColumnsQuestion.class);
		CodeList validColumnCodes = question.getAllCodes();
		validColumnCodes.addAll(getLegacyUselessButHarmlessColumnCodes());
		CodeList originalCodeList = new CodeList(rawCodes);
		rawCodes.retainAll(validColumnCodes);
		
		boolean wereCodesRemoved = originalCodeList.size() != rawCodes.size();
		originalCodeList.subtract(validColumnCodes);
		if (wereCodesRemoved)
			EAM.logDebug(("This customization uses codes that are no longer valid. Editing the rows or columns will prevent this message from appearing in the future. Unrecognized codes:" + originalCodeList));
	}

	private static StringList getLegacyUselessButHarmlessColumnCodes()
	{
		StringList legacyCodes = new StringList();
		legacyCodes.add("PseudoTaskBudgetTotal");
		legacyCodes.add("Who");
		return legacyCodes;
	}
}
