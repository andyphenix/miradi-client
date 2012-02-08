/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.objects;

import org.miradi.main.TestCaseWithProject;
import org.miradi.objecthelpers.AbstractStringToStringMap;
import org.miradi.objecthelpers.DashboardStatusMapsCache;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.CodeToChoiceMap;
import org.miradi.questions.OpenStandardsConceptualizeQuestion;
import org.miradi.questions.OpenStandardsProgressStatusQuestion;
import org.miradi.questions.OpenStandardsImplementActionsAndMonitoringQuestion;
import org.miradi.utils.CodeList;

public class TestDashboardStatusMapsCache extends TestCaseWithProject
{
	public TestDashboardStatusMapsCache(String name)
	{
		super(name);
	}
	
	public void testValidCalculatedStatusCacheWithNoData() throws Exception
	{
		CodeToChoiceMap createEmptyStringChoiceMap = createStringChoiceMapForEmptyProject();
		getDashboardStatusMapsCache().invalidateAllCachedMaps();
		CodeToChoiceMap calculatedStatusMap = getDashboardStatusMapsCache().getCalculatedStatusMap();
		
		assertEquals("empty dashboard should not have status?",createEmptyStringChoiceMap , calculatedStatusMap);		
	}
	
	public void testValidCalculatedStatusCacheWithData() throws Exception
	{
		verifyCalculatedStatusCode(OpenStandardsConceptualizeQuestion.IDENTIFY_DIRECT_THREATS_CODE, OpenStandardsProgressStatusQuestion.NOT_STARTED_CODE);
		putUserChoice(OpenStandardsConceptualizeQuestion.IDENTIFY_DIRECT_THREATS_CODE, OpenStandardsProgressStatusQuestion.NOT_APPLICABLE_CODE);
		getProject().createThreat();
		verifyCalculatedStatusCode(OpenStandardsConceptualizeQuestion.IDENTIFY_DIRECT_THREATS_CODE, OpenStandardsProgressStatusQuestion.IN_PROGRESS_CODE);
	}
	
	public void testValidCacheAfterProjectOpen() throws Exception
	{
		initializeCache();
		getProject().createAndAddFactorToDiagram(Cause.getObjectType());
		AbstractStringToStringMap cachedMap = getProject().getCachedDashboardEffectiveMap();
		
		getDashboardStatusMapsCache().invalidateAllCachedMaps();
		AbstractStringToStringMap nonCachedMap = getDashboardStatusMapsCache().getEffectiveMap();
		assertEquals("Cached map is not in sync with dashbard's effective status map?", nonCachedMap, cachedMap);
	}

	public void testGetEffectiveStatusMapWithNoData() throws Exception
	{
		CodeToChoiceMap createEmptyStringChoiceMap = createStringChoiceMapForEmptyProject();
		CodeToChoiceMap effectiveStatusMap = getEffectiveStatusMap();
		
		assertEquals("empty dashboard should not have status?",createEmptyStringChoiceMap , effectiveStatusMap);
	}
	
	public void testGetEffectiveStatusMapWithData() throws Exception
	{
		verifyTeamMemberEffectiveStatus(OpenStandardsProgressStatusQuestion.NOT_STARTED_CODE);
		
		getProject().createProjectResource();
		verifyTeamMemberEffectiveStatus(OpenStandardsProgressStatusQuestion.IN_PROGRESS_CODE);
	}
	
	public void testGetEffectiveStatusMapWithOneOfTwoRowsHavingData() throws Exception
	{
		verifyEffectiveStatus(OpenStandardsConceptualizeQuestion.IDENTIFY_DIRECT_THREATS_CODE, OpenStandardsProgressStatusQuestion.NOT_STARTED_CODE);
		
		getProject().createThreat();
		verifyEffectiveStatus(OpenStandardsConceptualizeQuestion.IDENTIFY_DIRECT_THREATS_CODE, OpenStandardsProgressStatusQuestion.IN_PROGRESS_CODE);
	}
	
	public void testGetEffectiveStatusMapWithUserData() throws Exception
	{
		getProject().createProjectResource();
		putUserChoice(OpenStandardsConceptualizeQuestion.SELECT_INTIAL_TEAM_MEMBERS_CODE, OpenStandardsProgressStatusQuestion.COMPLETE_CODE);
		verifyTeamMemberEffectiveStatus(OpenStandardsProgressStatusQuestion.COMPLETE_CODE);
	}

	private void verifyTeamMemberEffectiveStatus(String expectedCode) throws Exception
	{
		verifyEffectiveStatus(OpenStandardsConceptualizeQuestion.SELECT_INTIAL_TEAM_MEMBERS_CODE, expectedCode);
	}

	private void verifyEffectiveStatus(final String thirdLevelRowCode, String expectedCode) throws Exception
	{
		CodeToChoiceMap mapAsString = getEffectiveStatusMap();
		verifyCodeInMap(mapAsString, thirdLevelRowCode, expectedCode);
	}
	
	private void verifyCalculatedStatusCode(final String thirdLevelRowCode, String expectedProgressCode) throws Exception
	{
		CodeToChoiceMap mapAsString = getDashboardStatusMapsCache().getCalculatedStatusMap();
		verifyCodeInMap(mapAsString, thirdLevelRowCode, expectedProgressCode);
	}

	private void verifyCodeInMap(CodeToChoiceMap mapAsString, final String thirdLevelRowCode, String expectedProgressCode)
	{
		CodeToChoiceMap map = new CodeToChoiceMap(mapAsString);
		String calculatedStatusCode = map.get(thirdLevelRowCode);

		assertEquals("Incorrect status code?", expectedProgressCode, calculatedStatusCode);
	}
	
	private CodeToChoiceMap getEffectiveStatusMap() throws Exception
	{
		getDashboardStatusMapsCache().invalidateAllCachedMaps();

		return getDashboardStatusMapsCache().getEffectiveMap();
	}
	
	private CodeToChoiceMap createStringChoiceMapForEmptyProject()
	{
		CodeList allThirdLEvelCodes = getDashboard().getDashboardRowDefinitionManager().getThirdLevelCodes();
		CodeToChoiceMap emptyMap = new CodeToChoiceMap();
		for (int index = 0; index < allThirdLEvelCodes.size(); ++index)
		{
			String thirdLevelCode = allThirdLEvelCodes.get(index);
			String progressCode = getProgressCodeWithSpecialCases(thirdLevelCode);
			
			emptyMap.put(thirdLevelCode, progressCode);
		}
		
		return emptyMap;
	}

	private String getProgressCodeWithSpecialCases(String thirdLevelCode)
	{
		// NOTE: In a perfect world, an empty project would have all NOT STARTED values
		// but for now, there are a few cases that are already IN PROGRESS
		String progressCode = OpenStandardsProgressStatusQuestion.NOT_STARTED_CODE;
		if (thirdLevelCode.equals(OpenStandardsImplementActionsAndMonitoringQuestion.ESTIMATE_COSTS_FOR_ACTIVITIES_AND_MONITORING_CODE))
			progressCode = OpenStandardsProgressStatusQuestion.IN_PROGRESS_CODE;
		
		if (thirdLevelCode.equals(OpenStandardsImplementActionsAndMonitoringQuestion.DEVELOP_AND_SUBMIT_FUNDING_PROPOSALS_CODE))
			progressCode = OpenStandardsProgressStatusQuestion.IN_PROGRESS_CODE;
		
		if (thirdLevelCode.equals(OpenStandardsConceptualizeQuestion.CREATE_INITIAL_CONCEPTUAL_MODEL_CODE))
			progressCode = OpenStandardsProgressStatusQuestion.IN_PROGRESS_CODE;
		
		return progressCode;
	}
	
	private void putUserChoice(final String thirdLevelCode, final String progressCode) throws Exception
	{
		CodeToChoiceMap userMap = new CodeToChoiceMap();
		userMap.put(thirdLevelCode, progressCode);
		getDashboard().setData(Dashboard.TAG_PROGRESS_CHOICE_MAP, userMap.toString());
	}
	
	private Dashboard getDashboard()
	{
		ORef dashboardRef = getProject().getSingletonObjectRef(Dashboard.getObjectType());
		return Dashboard.find(getProject(), dashboardRef);
	}

	private void initializeCache() throws Exception
	{
		getProject().getCachedDashboardEffectiveMap();
	}
	
	private DashboardStatusMapsCache getDashboardStatusMapsCache()
	{
		return getProject().getDashboardStatusMapsCache();
	}
}
