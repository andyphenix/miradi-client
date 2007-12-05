/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.project;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.CreateFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.FactorLink;
import org.conservationmeasures.eam.objects.RatingCriterion;
import org.conservationmeasures.eam.objects.ValueOption;
import org.conservationmeasures.eam.questions.ChoiceQuestion;
import org.json.JSONArray;
import org.json.JSONObject;
import org.martus.util.DirectoryUtils;

public class TestThreatRatingFramework extends EAMTestCase
{
	public TestThreatRatingFramework(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
		framework = project.getThreatRatingFramework();
	}
	
	public void tearDown() throws Exception
	{
		project.close();
		super.tearDown();
	}
	
	public void testJson()
	{
		JSONObject json = framework.toJson();
		JSONArray bundleKeys = json.getJSONArray(SimpleThreatRatingFramework.TAG_BUNDLE_KEYS);
		assertEquals("didn't jsonize bundle keys?", framework.getBundleCount(), bundleKeys.length());
	}
	
	public void testWriteAndRead() throws Exception
	{
		File tempDir = createTempDirectory();
		try
		{
			Project realProject = new Project();
			realProject.createOrOpen(tempDir);
			BaseId createdId = realProject.createObjectAndReturnId(ObjectType.RATING_CRITERION);
			
			FactorId threatId = new FactorId(283);
			FactorId targetId = new FactorId(983);
			ThreatRatingBundle bundle = realProject.getThreatRatingFramework().getBundle(threatId, targetId);
			bundle.setValueId(createdId, new BaseId(838));
			IdList realOptionIds = realProject.getThreatRatingFramework().getValueOptionIds();
			realProject.getThreatRatingFramework().saveBundle(bundle);
			realProject.getDatabase().writeThreatRatingFramework(realProject.getThreatRatingFramework());
			realProject.close();

			Project loadedProject = new Project();
			loadedProject.createOrOpen(tempDir);
			IdList loadedOptionIds = loadedProject.getThreatRatingFramework().getValueOptionIds();
			SimpleThreatRatingFramework loadedFramework = loadedProject.getThreatRatingFramework();
			assertEquals("didn't reload framework?", createdId, loadedFramework.getCriterion(createdId).getId());
			ThreatRatingBundle gotBundle = loadedProject.getThreatRatingFramework().getBundle(threatId, targetId);
			assertEquals("didn't load bundles?", bundle.getValueId(createdId), gotBundle.getValueId(createdId));
			assertEquals("didn't load options?", realOptionIds, loadedOptionIds);
			loadedProject.close();
		}
		finally
		{
			DirectoryUtils.deleteEntireDirectoryTree(tempDir);
		}
	}
	
	public void testGetBundleValue()
	{
		BaseId noneId = framework.findValueOptionByNumericValue(0).getId();
		ThreatRatingBundle bundle = new ThreatRatingBundle(new FactorId(1), new FactorId(2), noneId);
		ValueOption result = framework.getBundleValue(bundle);
		assertEquals("didn't default correctly? ", 0, result.getNumericValue());
	}

	public void testRatingValueOptions() throws Exception
	{
		ValueOption[] options = framework.getValueOptions();
		assertEquals("wrong number of default options?", 5, options.length);
		// NOTE: options are: [0]:1/VH, [1]:2/H, [2]:3/M, [3]:4/L, [4]:0/NONE 
		assertEquals("wrong order or label?", "Very High", options[0].getLabel());
		assertEquals("wrong numeric value? ", 3, options[1].getNumericValue());
		assertEquals("bad color?", ChoiceQuestion.LIGHT_GREEN, options[2].getColor());
	}
	
	public void testFindValueOptionByNumericValue()
	{
		ValueOption optionNone = framework.findValueOptionByNumericValue(0);
		assertEquals(Color.WHITE, optionNone.getColor());
	}

	public void testDefaultValue() throws Exception
	{
		BaseId id = framework.getDefaultValueId();
		ValueOption option = framework.getValueOption(id);
		assertEquals("Didn't default to zero?", 0, option.getNumericValue());
	}
	
	public void testThreatRatingCriteria() throws Exception
	{
		RatingCriterion[] criteria = framework.getCriteria();
		assertEquals("wrong number of default criteria?", 3, criteria.length);
		List expectedLabels = Arrays.asList(new String[] {"Scope", "Severity", "Irreversibility", });
		for(int i = 0; i < criteria.length; ++i)
		{
			String actual = criteria[i].getLabel();
			assertContains("Missing a criterion?", actual, expectedLabels);
		}
	}
	
	public void testIdAssignment() throws Exception
	{
		assertNotEquals("reused ids?", framework.getCriteria()[0].getId(), framework.getValueOptions()[0].getId());
	}
	
	public void testBundlesForDeletedNodes() throws Exception
	{
		ThreatRatingBundle bundle1 = createThreatTargetAndBundle();
		BaseObject object1 = project.findObject(new ORef(ObjectType.FACTOR, bundle1.getThreatId()));
		project.deleteObject(object1);
		assertFalse("deleted threatId case failed?", framework.isBundleForLinkedThreatAndTarget(bundle1));
		
		ThreatRatingBundle bundle2 = createThreatTargetAndBundle();
		BaseObject object2 = project.findObject(new ORef(ObjectType.TARGET, bundle2.getTargetId()));
		project.deleteObject(object2);
		assertFalse("deleted targetId case failed?", framework.isBundleForLinkedThreatAndTarget(bundle2));
		
	}

	private ThreatRatingBundle createThreatTargetAndBundle() throws Exception
	{
		FactorId threatId = createThreat(project).getWrappedId();
		FactorId targetId = createTarget(project).getWrappedId();
		populateBundle(framework, threatId, targetId, framework.getValueOptions()[0]);
		ThreatRatingBundle bundle = framework.getBundle(threatId, targetId);
		assertFalse("normal case failed?", framework.isBundleForLinkedThreatAndTarget(bundle));
		return bundle;
	}
	
	public void testBundles() throws Exception
	{
		FactorId threatId = new FactorId(77);
		FactorId targetId = new FactorId(292);
		BaseId criterionId = new BaseId(22);
		BaseId valueId = new BaseId(639);
		
		ThreatRatingBundle bundle = framework.getBundle(threatId, targetId);
		assertNotNull("Didn't write bundle?", project.getDatabase().readThreatRatingBundle(threatId, targetId));
		
		
		bundle.setValueId(criterionId, valueId);
		ThreatRatingBundle reGot = framework.getBundle(threatId, targetId);
		assertEquals("did't get same bundle?", bundle.getValueId(criterionId), reGot.getValueId(criterionId));
		
	}
	
	public void testGetThreatRatingSummary() throws Exception
	{
		framework.createDefaultObjectsIfNeeded();
		
		DiagramFactor threat1 = createThreat(project);
		DiagramFactor threat2 = createThreat(project);
		DiagramFactor target1 = createTarget(project);
		DiagramFactor target2 = createTarget(project);

		ValueOption none = framework.findValueOptionByNumericValue(0);
		ValueOption high = framework.findValueOptionByNumericValue(3);
		ValueOption veryHigh = framework.findValueOptionByNumericValue(4);
		
		assertEquals("threat1 not none?", none, framework.getThreatThreatRatingValue(threat1.getWrappedId()));
		assertEquals("threat2 not none?", none, framework.getThreatThreatRatingValue(threat2.getWrappedId()));
		assertEquals("target1 not none?", none, framework.getTargetThreatRatingValue(target1.getWrappedId()));
		assertEquals("target2 not none?", none, framework.getTargetThreatRatingValue(target2.getWrappedId()));

		createLinkageAndBundle(project, threat1, target1, veryHigh);
		createLinkageAndBundle(project, threat1, target2, veryHigh);
		assertEquals("target1 not high?", high, framework.getTargetThreatRatingValue(target1.getWrappedId()));
		createLinkageAndBundle(project, threat2, target1, veryHigh);
		assertEquals("threat2 not high?", high, framework.getThreatThreatRatingValue(threat2.getWrappedId()));
		createLinkageAndBundle(project, threat2, target2, veryHigh);
		
		assertEquals("threat1 not very high?", veryHigh, framework.getThreatThreatRatingValue(threat1.getWrappedId()));
		assertEquals("threat2 not very high?", veryHigh, framework.getThreatThreatRatingValue(threat2.getWrappedId()));
		assertEquals("target1 not very high?", veryHigh, framework.getTargetThreatRatingValue(target1.getWrappedId()));
		assertEquals("target2 not very high?", veryHigh, framework.getTargetThreatRatingValue(target2.getWrappedId()));
	}
	
	void createLinkageAndBundle(Project projectToUse, DiagramFactor threat, DiagramFactor target, ValueOption value) throws Exception
	{
		CreateFactorLinkParameter parameter = new CreateFactorLinkParameter(threat.getWrappedORef(), target.getWrappedORef());
		projectToUse.createObject(ObjectType.FACTOR_LINK, BaseId.INVALID, parameter);
		populateBundle(projectToUse.getThreatRatingFramework(), threat.getWrappedId(), target.getWrappedId(), value);
	}
	
	public void testGetThreatRatingSummaryUnlinked() throws Exception
	{
		DiagramFactor threat = createThreat(project);
		DiagramFactor target = createTarget(project);
		FactorId threatId = threat.getWrappedId();
		FactorId targetId = target.getWrappedId();
		ValueOption none = framework.findValueOptionByNumericValue(0);
		ValueOption high = framework.findValueOptionByNumericValue(3);
		ValueOption veryHigh = framework.findValueOptionByNumericValue(4);

		populateBundle(framework, threatId, targetId, veryHigh);
		assertEquals("included unlinked bundle in threat value?", none, framework.getThreatThreatRatingValue(threatId));
		assertEquals("included unlinked bundle in target value?", none, framework.getTargetThreatRatingValue(targetId));
		CreateFactorLinkParameter parameter = new CreateFactorLinkParameter(threat.getWrappedORef(), target.getWrappedORef());
		BaseId linkId = project.createObject(ObjectType.FACTOR_LINK, BaseId.INVALID, parameter);
		
		assertEquals("linking didn't include value for threat?", high, framework.getThreatThreatRatingValue(threatId));
		assertEquals("linking didn't include value for target?", high, framework.getTargetThreatRatingValue(targetId));

		FactorLink factorLink = (FactorLink) project.findObject(new ORef(ObjectType.FACTOR_LINK, linkId));
		project.deleteObject(factorLink);
		assertEquals("threat value included contributing factor?", none, framework.getThreatThreatRatingValue(threatId));
		assertEquals("target value included contributing factor?", none, framework.getTargetThreatRatingValue(targetId));
	}
	
	public void testGetHighestValueForTarget() throws Exception
	{
		int[][] bundleValues = { {3,}, {4,}, };
		SimpleThreatRatingFramework trf = createFramework(bundleValues);
		try
		{
			FactorId targetId = trf.getProject().getTargetPool().getTargets()[0].getFactorId();
			assertEquals(4, trf.getHighestValueForTarget(targetId).getNumericValue());
		}
		finally
		{
			trf.getProject().close();
		}
	}
	
	public void testGetPureMajorityProjectRating() throws Exception
	{
		int[][] bundlesEmpty = { {-1} };
		verifyPureMajority("Empty", 0, bundlesEmpty);
		
		int[][] bundlesPlurality = { {2, 3, 4, 2, 3, 4, 2},	};
		verifyPureMajority("Plurality", 3, bundlesPlurality);
		
		int[][] bundlesMajority = { {2, 2, 1},	};
		verifyPureMajority("Majority", 2, bundlesMajority);

		int[][] bundlesTwoRows = {
			{1, 4, 1, },
			{4, 1, 4, },
		};
		verifyPureMajority("TwoRows", 4, bundlesTwoRows);
	}
	
	public void testGetProjectRating() throws Exception
	{
		int[][] bundlesEmpty = { {-1} };
		verifyOverallProjectRating("Empty", 0, bundlesEmpty);
		
		int[][] bundlesPlurality = { {4, 4, 1, 1},	};
		verifyOverallProjectRating("Plurality", 3, bundlesPlurality);
		
		int[][] bundlesMajority = { {3, 3, 1},	};
		verifyOverallProjectRating("Majority", 3, bundlesMajority);
		
	}
	
	private void verifyPureMajority(String message, int expected, int[][] bundleValues) throws Exception
	{
		SimpleThreatRatingFramework trf = createFramework(bundleValues);
		try
		{
			assertEquals(message, expected, trf.getProjectMajorityRating().getNumericValue());
		}
		finally
		{
			trf.getProject().close();
		}
	}

	private void verifyOverallProjectRating(String message, int expected, int[][] bundleValues) throws Exception
	{
		SimpleThreatRatingFramework trf = createFramework(bundleValues);
		try
		{
			assertEquals(message, expected, trf.getOverallProjectRating().getNumericValue());
		}
		finally
		{
			trf.getProject().close();
		}
	}

	private DiagramFactor createTarget(ProjectForTesting projectToUse) throws Exception
	{
		return projectToUse.createDiagramFactorAndAddToDiagram(ObjectType.TARGET);
		//FactorId targetId = (FactorId)projectToUse.createObject(ObjectType.TARGET);
		//return targetId;
	}

	private DiagramFactor createThreat(ProjectForTesting projectToUse) throws Exception
	{
		return projectToUse.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
//		FactorId threatId = (FactorId)projectToUse.createObject(ObjectType.CAUSE);
//		return threatId;
	}
	
	void populateBundle(SimpleThreatRatingFramework frameworkToUse, FactorId threatId, FactorId targetId, ValueOption value) throws Exception
	{
		ThreatRatingBundle bundle = frameworkToUse.getBundle(threatId, targetId);
		RatingCriterion criteria[] = frameworkToUse.getCriteria();
		for(int i = 0; i < criteria.length; ++i)
			bundle.setValueId(criteria[i].getId(), value.getId());
	}
	
	private SimpleThreatRatingFramework createFramework(int[][] bundleValues) throws Exception
	{
		ProjectForTesting tempProject = new ProjectForTesting(getName());
		SimpleThreatRatingFramework trf = tempProject.getThreatRatingFramework();
		
		int threatCount = bundleValues.length;
		DiagramFactor[] threats = new DiagramFactor[threatCount];
		for(int i = 0; i < threatCount; ++i)
			threats[i] = createThreat(tempProject);
		
		int targetCount = bundleValues[0].length;
		DiagramFactor[] targets = new DiagramFactor[targetCount];
		for(int i = 0; i < targetCount; ++i)
			targets[i] = createTarget(tempProject);

		for(int threatIndex = 0; threatIndex < threatCount; ++threatIndex)
		{
			int[] valuesForThreat = bundleValues[threatIndex];
			for(int targetIndex = 0; targetIndex < targetCount; ++targetIndex)
			{
				int numericValue = valuesForThreat[targetIndex];
				if(numericValue < 0)
					continue;
				ValueOption valueOption = trf.findValueOptionByNumericValue(numericValue);
				createLinkageAndBundle(tempProject, threats[threatIndex], targets[targetIndex], valueOption);				
			}
		}
		return trf;
	}
	
	SimpleThreatRatingFramework framework;
	private ProjectForTesting project;
}
