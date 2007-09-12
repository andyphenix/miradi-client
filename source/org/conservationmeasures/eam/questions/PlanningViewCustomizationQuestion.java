/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import java.util.Vector;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objectpools.PlanningViewConfigurationPool;
import org.conservationmeasures.eam.objects.PlanningViewConfiguration;
import org.conservationmeasures.eam.project.Project;

public class PlanningViewCustomizationQuestion extends ChoiceQuestion
{
	public PlanningViewCustomizationQuestion(Project projectToUse)
	{
		super(getEmptyTag(), EAM.text("Customization Style"), getCustomizationChoices(projectToUse));
	}

	private static String getEmptyTag()
	{
		return "";
	}
	
	private static ChoiceItem[] getCustomizationChoices(Project project)
	{
		PlanningViewConfigurationPool configurationPool = (PlanningViewConfigurationPool) project.getPool(PlanningViewConfiguration.getObjectType());
		ORefList allConfigurationRefs = configurationPool.getORefList();

		Vector allCustomizations = new Vector();
		for (int i = 0; i < allConfigurationRefs.size(); ++i)
		{
			ChoiceItem choiceItem = createChoiceItem(project, allConfigurationRefs.get(i));
			allCustomizations.add(choiceItem);
		}

		return (ChoiceItem[]) allCustomizations.toArray(new ChoiceItem[0]);	
	}

	private static ChoiceItem createChoiceItem(Project project, ORef configurationRef)
	{
		return new ObjectChoiceItem(project, configurationRef);
	}
	
}
