/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.questions;

import org.conservationmeasures.eam.main.EAMTestCase;

public class TestChoiceItem extends EAMTestCase
{
	public TestChoiceItem(String name)
	{
		super(name);
	}
	
	public void testEquals()
	{
		String label1 = "some label";
		String code1 = "SomeCode";
		ChoiceItem choice1 = new ChoiceItem(code1, label1);
		ChoiceItem flippedCodeLabelChoice = new ChoiceItem(label1, code1);
		ChoiceItem nonFlippedCodeLabelChoice = new ChoiceItem(code1, label1);
		
		assertFalse("should not be the same?", choice1.equals(flippedCodeLabelChoice));
		assertTrue("should be the same?", choice1.equals(nonFlippedCodeLabelChoice));
	}
}
