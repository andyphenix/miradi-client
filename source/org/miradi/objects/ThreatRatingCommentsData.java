/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objecthelpers.CodeStringMap;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.utils.EnhancedJsonObject;

public class ThreatRatingCommentsData extends BaseObject
{
	public ThreatRatingCommentsData(ObjectManager objectManager, BaseId id)
	{
		super(objectManager, id);
		clear();
	}

	public ThreatRatingCommentsData(ObjectManager objectManager, int idAsInt, EnhancedJsonObject jsonObject) throws Exception 
	{
		super(objectManager, new BaseId(idAsInt), jsonObject);
	}

	@Override
	public int getType()
	{
		return getObjectType();
	}

	@Override
	public String getTypeName()
	{
		return OBJECT_NAME;
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	public static int getObjectType()
	{
		return ObjectType.THREAT_RATING_COMMENTS_DATA;
	}
	
	public String findComment(ORef threatRef, ORef targetRef)
	{
		String threatTargetRefsAsKey = createKey(threatRef, targetRef);
		if (getProject().isSimpleThreatRatingMode())
			return getSimpleThreatRatingCommentsMap().get(threatTargetRefsAsKey);
		
		return getStressBasedThreatRatingCommentsMap().get(threatTargetRefsAsKey);
	}
	
	public static String createKey(ORef threatRef, ORef targetRef)
	{
		return threatRef.toString() + targetRef.toString();
	}
	
	public CodeStringMap getThreatRatingCommentsMap()
	{
		if (getProject().isSimpleThreatRatingMode())
			return getSimpleThreatRatingCommentsMap();
		
		return getStressBasedThreatRatingCommentsMap();
	}
	
	public String getThreatRatingCommentsMapTag()
	{
		if (getProject().isSimpleThreatRatingMode())
			return TAG_SIMPLE_THREAT_RATING_COMMENTS_MAP;
		
		return TAG_STRESS_BASED_THREAT_RATING_COMMENTS_MAP;
	}
	
	public CodeStringMap getThreatRatingCommentsMap(String tag)
	{
		if (tag.equals(TAG_SIMPLE_THREAT_RATING_COMMENTS_MAP))
			return getSimpleThreatRatingCommentsMap();
		
		if (tag.equals(TAG_STRESS_BASED_THREAT_RATING_COMMENTS_MAP))
			return getStressBasedThreatRatingCommentsMap();
		
		throw new RuntimeException("Trying to get threat rating comments map with unknown tag:" + tag);
	}

	public CodeStringMap getStressBasedThreatRatingCommentsMap()
	{
		return getStringStringMapData(TAG_STRESS_BASED_THREAT_RATING_COMMENTS_MAP);
	}

	public CodeStringMap getSimpleThreatRatingCommentsMap()
	{
		return getStringStringMapData(TAG_SIMPLE_THREAT_RATING_COMMENTS_MAP);
	}
	
	public static ThreatRatingCommentsData find(ObjectManager objectManager, ORef threatRatingCommentsDataRef)
	{
		return (ThreatRatingCommentsData) objectManager.findObject(threatRatingCommentsDataRef);
	}
	
	public static ThreatRatingCommentsData find(Project project, ORef threatRatingCommentsDataRef)
	{
		return find(project.getObjectManager(), threatRatingCommentsDataRef);
	}

	@Override
	void clear()
	{
		super.clear();
		
		createStringStringMapField(TAG_SIMPLE_THREAT_RATING_COMMENTS_MAP);
		createStringStringMapField(TAG_STRESS_BASED_THREAT_RATING_COMMENTS_MAP);
	}

	public static final String OBJECT_NAME = "ThreatRatingCommentsData";
	
	public static final String TAG_SIMPLE_THREAT_RATING_COMMENTS_MAP = "SimpleThreatRatingCommentsMap";
	public static final String TAG_STRESS_BASED_THREAT_RATING_COMMENTS_MAP = "StressBasedThreatRatingCommentsMap";
}
