/* 
Copyright 2005-2021, Foundations of Success, Bethesda, Maryland
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

package org.miradi.objecthelpers;

import org.miradi.objecthelpers.AbstractStringToStringMap;
import org.miradi.objecthelpers.CodeToChoiceMap;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.TestAbstractStringMap;

public class TestCodeToChoiceMap extends TestAbstractStringMap
{
	public TestCodeToChoiceMap(String name)
	{
		super(name);
	}

	@Override
	protected AbstractStringToStringMap createAbstractMap()
	{
		return new CodeToChoiceMap();
	}

	@Override
	protected AbstractStringToStringMap createAbstractMap(EnhancedJsonObject json)
	{
		return new CodeToChoiceMap(json);
	}
	
	public void testToString() throws Exception
	{
		CodeToChoiceMap list = (CodeToChoiceMap) createMapWithSampleData();
		assertEquals("Can't rount trip?", list, new CodeToChoiceMap(list));
	}
}
