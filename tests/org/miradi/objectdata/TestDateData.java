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
package org.miradi.objectdata;

import org.miradi.main.MiradiTestCase;
import org.miradi.objectdata.DateData;


public class TestDateData extends MiradiTestCase
{

	public TestDateData(String name)
	{
		super(name);
	}
	
	public void testBasics() throws Exception
	{
		DateData date = new DateData("tag");
		String sampleDate = "2006-01-01"; 
		date.set(sampleDate);
		assertEquals("Simple get/set failed?", sampleDate, date.get());
	}
}
