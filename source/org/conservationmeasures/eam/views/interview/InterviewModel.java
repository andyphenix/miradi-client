/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.interview;

import java.io.IOException;
import java.util.Vector;

import org.conservationmeasures.eam.main.EAM;
import org.martus.util.UnicodeStringReader;

public class InterviewModel
{
	public InterviewModel() throws IOException
	{
		steps = new Vector();
	}
	
	public void loadSteps() throws IOException
	{
		addStep(StepLoader.load(new UnicodeStringReader(templateWelcome)));
		addStep(StepLoader.load(new UnicodeStringReader(templateP1aT2S1)));
		addStep(StepLoader.load(new UnicodeStringReader(templateP1aT2S2)));
	}
	
	public void addStep(InterviewStepModel stepModel)
	{
		steps.add(stepModel);
		if(currentStep == null)
			currentStep = stepModel;
	}
	
	public int getStepCount()
	{
		return steps.size();
	}
	
	public InterviewStepModel getStep(int n)
	{
		return (InterviewStepModel)steps.get(n);
	}
	
	public InterviewStepModel getStep(String stepName)
	{
		for(int i=0; i < steps.size(); ++i)
		{
			InterviewStepModel step = (InterviewStepModel)steps.get(i);
			if(step.getStepName().equals(stepName))
				return step;
		}
		return null;
	}
	
	public String getCurrentStepName()
	{
		return currentStep.getStepName();
	}
	
	public void setCurrentStepName(String newStepName)
	{
		InterviewStepModel destination = getStep(newStepName);
		if(destination == null)
		{
			EAM.logError("Attempted to set step to: " + newStepName);
			return;
		}
		
		currentStep = destination;
	}
	
	public InterviewStepModel getCurrentStep()
	{
		for(int i=0; i < getStepCount(); ++i)
		{
			InterviewStepModel step = getStep(i);
			if(step.getStepName().equals(getCurrentStepName()))
				return step;
		}
		return null;
	}
	
	private InterviewStepModel currentStep;
	private Vector steps;

	// TODO: Extract out to text file(s)
	static public final String templateWelcome = 
		"welcome\n" +
		"P1aT2S1\n" +
		"\n" + 
		":html:\n" +
		"<h1>Interview</h1>" +
		"<p>This view will walk the user through a series of questions.</p>";

	static public final String templateP1aT2S1 = 	
		"P1aT2S1\n" +
		"P1aT2S2\n" +
		"welcome\n" +
		":html:\n" +
			"<p><font size='6'>Step 1.  Conceptualize</font></p>\n" + 
		":html:\n" +
			"<font size='5'>&nbsp;&nbsp;" +
			"Principle 1A.  Be clear and specific about the issue or problem</font></p>" +
			"<hr></hr>\n" +
		":html:\n" +
			"<p><strong>Task 2. Define the scope of the area or theme</strong></p>" +
			"<br></br>" +
			"<p>Most conservation projects will focus on a defined geographic <u><em>project area</em></u> " + 
			"that contains the biodiversity that is of interest.  " + 
			"In a few cases, a conservation project may not focus on biodiversity in a specific area, " + 
			"but instead will have a <u><em>theme</em></u> that focuses on a population of wide-ranging animals, " + 
			"such as migratory birds.</p>" +
			"<br></br>\n" +
		":html:\n" +
			"<p>Describe in a few sentences the project area or theme for your project:</p>\n" + 
		":input:\n" +
			"ProjectScope\n";
	
	private static final String templateP1aT2S2=
		"P1aT2S2\n" +
		"\n" +
		"P1aT2S1\n" +
		":html:\n" +
			"<p><font size='6'>Step 2.1.  Plan Your Actions</font></p>\n" +
		":html:\n" +
			"<font size='5'>&nbsp;&nbsp;Principle 2.1 A.  Develop clear goal and objectives</font></p>" +
			"<hr></hr>" +
			"<p><strong>Task 3. Develop Objectives</strong></p>" +
			"<br></br>" + 
			"An <u><em>objective</em></u> is a specific statement detailing the desired accomplishments, " + 
			"milestones or outcomes of a project.  To develop a good objective, " + 
			"select one of your high ranked threats:\n" +
		":list:ThreatChoosen\n" +
		"Cutting Trees\n" +
		"International Trawling\n" +
		"Local Fishing\n"+
		":html:\n";

	//private static final String dataPrinciple1ATask2Step1 = "Our community's traditional fishing grounds and adjacent shore areas in Our Bay.";
}
