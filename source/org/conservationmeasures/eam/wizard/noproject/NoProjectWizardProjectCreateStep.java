/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.wizard.noproject;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.wizard.WizardManager;
import org.conservationmeasures.eam.wizard.WizardPanel;

public class NoProjectWizardProjectCreateStep extends NoProjectWizardStep
{

	public NoProjectWizardProjectCreateStep(WizardPanel wizardToUse) throws Exception
	{
		super(wizardToUse);
		
		String html = EAM.loadResourceFile(getClass(), "WelcomeProjectCreate.html");
		left = new LeftSideTextPanel(getMainWindow(), html, this);
		JPanel panel = new JPanel(new GridLayout(1, 2));
		panel.add(left);
		panel.add(projectList);
		
		add(panel, BorderLayout.CENTER);
	}
	
	
	public void refresh() throws Exception
	{
		left.refresh();
		super.refresh();
	}
	
	public void setComponent(String name, JComponent component)
	{
		if (name.equals(NEW_PROJECT_NAME))
		{
			newProjectNameField = (JTextComponent)component;
			newProjectNameField.addKeyListener(this);
		}
	}
	
	public Class getControl(String controlName)
	{
		if(controlName.equals(WizardManager.CONTROL_NEXT))
			return getClass();
		return super.getControl(controlName);
	}


	public void buttonPressed(String buttonName)
	{
		try
		{
			if(buttonName.equals(WizardManager.CONTROL_NEXT))
			{
				createProject();
			}
			else 
			{
				super.buttonPressed(buttonName);
			}
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog(EAM.text("Unable to process request: ") + e);
		}
	}

	private void createProject()
	{
		String newName = getValue(NEW_PROJECT_NAME);
		if (newName.length()<=0)
			return;
		try 
		{
			Project.validateNewProject(newName);
			File newFile = new File(EAM.getHomeDirectory(),newName);
			getMainWindow().createOrOpenProject(newFile);
		}
		catch (Exception e)
		{
			EAM.notifyDialog(EAM.text("Create Failed:") +e.getMessage());
		}
	}

	public String getValue(String name)
	{
		return newProjectNameField.getText();
	}

	private static final String NEW_PROJECT_NAME = "NewProjectName";

	LeftSideTextPanel left;
	JTextComponent newProjectNameField;
	

}
