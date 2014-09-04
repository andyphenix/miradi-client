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
package org.miradi.forms;

import org.miradi.main.MiradiTestCase;
import org.miradi.objects.Target;
import org.miradi.schemas.TargetSchema;

public class TestFormFieldData extends MiradiTestCase
{
	public TestFormFieldData(String name)
	{
		super(name);
	}
	
	public void testBasics()
	{
		FormFieldData formFieldData = new FormFieldData(TargetSchema.getObjectType(), Target.TAG_LABEL);
		assertTrue("is not form field Data?", formFieldData.isFormFieldData());
		assertEquals("wrong form field data type?", TargetSchema.getObjectType(), formFieldData.getObjectType());
		assertEquals("wrong form field data tag?", Target.TAG_LABEL, formFieldData.getObjectTag());
	}
}
