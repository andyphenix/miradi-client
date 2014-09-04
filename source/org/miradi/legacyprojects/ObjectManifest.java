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
package org.miradi.legacyprojects;

import org.json.JSONObject;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objectpools.ObjectPool;

public class ObjectManifest extends Manifest
{
	public ObjectManifest()
	{
		super(ObjectManifest.OBJECT_MANIFEST);
	}
	
	public ObjectManifest(JSONObject copyFrom)
	{
		super(copyFrom);
		// TODO: Fail if wrong type
	}

	public ObjectManifest(ObjectPool pool)
	{
		this();
		ORefList refsToAdd = pool.getRefList();
		for(int i = 0; i < refsToAdd.size(); ++i)
		{
			put(refsToAdd.get(i).getObjectId());
		}
	}
	
	private static String OBJECT_MANIFEST = "ObjectManifest";
}
