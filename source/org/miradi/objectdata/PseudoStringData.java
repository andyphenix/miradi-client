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

package org.miradi.objectdata;

import org.miradi.objects.BaseObject;

public class PseudoStringData  extends StringData
{
	public PseudoStringData(BaseObject owningObject, String tag)
	{
		super(tag);
		object = owningObject;
	}

	@Override
	public void set(String newValue) throws Exception
	{
		if (newValue.length()!=0)
			throw new RuntimeException("Set not allowed in a pseudo field");
	}

	@Override
	public String get()
	{
		return object.getPseudoData(getTag());
	}
	
	private BaseObject object;
}