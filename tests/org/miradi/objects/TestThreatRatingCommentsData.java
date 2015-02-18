/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

import org.miradi.objecthelpers.ORef;
import org.miradi.project.ProjectForTesting;
import org.miradi.schemas.ThreatRatingCommentsDataSchema;


public class TestThreatRatingCommentsData extends ObjectTestCase
{
	public TestThreatRatingCommentsData(String name)
	{
		super(name);
	}
	
	public void testFields() throws Exception
	{
		verifyFields(ThreatRatingCommentsDataSchema.getObjectType());
	}
	
	public void testFindComment() throws Exception
	{
		ORef threatRatingCommentsDataRef = getProject().getSingletonObjectRef(ThreatRatingCommentsDataSchema.getObjectType());
		assertTrue("singlton threat rating comments data does not exist?", !threatRatingCommentsDataRef.isInvalid());
		ThreatRatingCommentsData threatRatingCommentsData = ThreatRatingCommentsData.find(getProject(), threatRatingCommentsDataRef);
		Cause cause = getProject().createCause();
		Target target = getProject().createTarget();
		getProject().populateThreatRatingCommentsData(threatRatingCommentsData, cause.getRef(), target.getRef());
		
		assertTrue("project is not in simple threat rating mode?", getProject().isSimpleThreatRatingMode());
		String simpleThreatRatingComment = threatRatingCommentsData.findComment(cause.getRef(), target.getRef());
		assertEquals("wrong simple based threat rating comment?", ProjectForTesting.SIMPLE_THREAT_RATING_COMMENT, simpleThreatRatingComment);
		
		getProject().switchToStressBaseMode();
		assertTrue("project is not in stress based threat rating mode?", getProject().isStressBaseMode());
		String stressBasedThreatRatingComment = threatRatingCommentsData.findComment(cause.getRef(), target.getRef());
		assertEquals("wrong simple based threat rating comment?", ProjectForTesting.STRESS_BASED_THREAT_RATING_COMMENT, stressBasedThreatRatingComment);		
	}
}
