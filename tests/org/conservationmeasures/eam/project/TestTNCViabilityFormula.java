/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 

package org.conservationmeasures.eam.project;

import org.conservationmeasures.eam.questions.StatusQuestion;
import org.conservationmeasures.eam.utils.CodeList;
import org.martus.util.TestCaseEnhanced;

public class TestTNCViabilityFormula extends TestCaseEnhanced
{
	public TestTNCViabilityFormula(String name)
	{
		super(name);
	}

	public void testGetValueFromRatingCode() throws Exception
	{
		assertEquals(0, TNCViabilityFormula.getValueFromRatingCode(StatusQuestion.UNSPECIFIED), 0.0001);
		assertEquals(1, TNCViabilityFormula.getValueFromRatingCode(StatusQuestion.POOR), 0.0001);
		assertEquals(2.5, TNCViabilityFormula.getValueFromRatingCode(StatusQuestion.FAIR), 0.0001);
		assertEquals(3.5, TNCViabilityFormula.getValueFromRatingCode(StatusQuestion.GOOD), 0.0001);
		assertEquals(4.0, TNCViabilityFormula.getValueFromRatingCode(StatusQuestion.VERY_GOOD), 0.0001);
		try
		{
			TNCViabilityFormula.getValueFromRatingCode("8");
			fail("Should have thrown for unknown value");
		}
		catch(TNCViabilityFormula.UnexpectedValueException ignoreExpected)
		{
		}
	}

	public void testGetRatingCodeFromValue() throws Exception
	{
		assertEquals(StatusQuestion.POOR, TNCViabilityFormula.getRatingCodeFromValue(-0.01));
		assertEquals(StatusQuestion.POOR, TNCViabilityFormula.getRatingCodeFromValue(0.0));
		assertEquals(StatusQuestion.POOR, TNCViabilityFormula.getRatingCodeFromValue(1.74));
		assertEquals(StatusQuestion.FAIR, TNCViabilityFormula.getRatingCodeFromValue(1.75));
		assertEquals(StatusQuestion.FAIR, TNCViabilityFormula.getRatingCodeFromValue(2.99));
		assertEquals(StatusQuestion.GOOD, TNCViabilityFormula.getRatingCodeFromValue(3.0));
		assertEquals(StatusQuestion.GOOD, TNCViabilityFormula.getRatingCodeFromValue(3.74));
		assertEquals(StatusQuestion.VERY_GOOD, TNCViabilityFormula.getRatingCodeFromValue(3.75));
		assertEquals(StatusQuestion.VERY_GOOD, TNCViabilityFormula.getRatingCodeFromValue(4.5));
	}
	
	public void testGetAverageRatingCode() throws Exception
	{
		assertEquals(StatusQuestion.UNSPECIFIED, TNCViabilityFormula.getAverageRatingCode(
				new CodeList()));
		assertEquals(StatusQuestion.UNSPECIFIED, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.UNSPECIFIED})));
		assertEquals(StatusQuestion.POOR, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.POOR})));
		assertEquals(StatusQuestion.POOR, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.POOR, StatusQuestion.UNSPECIFIED})));
		assertEquals(StatusQuestion.FAIR, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.POOR, StatusQuestion.FAIR})));
		assertEquals(StatusQuestion.FAIR, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.POOR, StatusQuestion.FAIR, 
						StatusQuestion.FAIR, StatusQuestion.FAIR})));
		assertEquals(StatusQuestion.FAIR, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.FAIR})));
		assertEquals(StatusQuestion.GOOD, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.FAIR, StatusQuestion.GOOD})));
		assertEquals(StatusQuestion.GOOD, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.GOOD, StatusQuestion.GOOD, 
						StatusQuestion.GOOD, StatusQuestion.FAIR})));
		assertEquals(StatusQuestion.GOOD, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.GOOD})));
		assertEquals(StatusQuestion.VERY_GOOD, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.GOOD, StatusQuestion.VERY_GOOD})));
		assertEquals(StatusQuestion.VERY_GOOD, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.GOOD, StatusQuestion.VERY_GOOD, 
						StatusQuestion.VERY_GOOD, StatusQuestion.VERY_GOOD})));
		assertEquals(StatusQuestion.VERY_GOOD, TNCViabilityFormula.getAverageRatingCode(
				new CodeList(new String[] {StatusQuestion.VERY_GOOD})));
		try
		{
			TNCViabilityFormula.getAverageRatingCode(new CodeList(new String[] {"8"}));
			fail("Should have thrown for unknown value");
		}
		catch(TNCViabilityFormula.UnexpectedValueException ignoreExpected)
		{
		}

	}
	
	public void testGetTotalCategoryRatingCode() throws Exception
	{
		assertEquals(StatusQuestion.UNSPECIFIED, TNCViabilityFormula.getTotalCategoryRatingCode(
				new CodeList(new String[] {})));
		assertEquals(StatusQuestion.UNSPECIFIED, TNCViabilityFormula.getTotalCategoryRatingCode(
				new CodeList(new String[] {StatusQuestion.UNSPECIFIED})));
		assertEquals(StatusQuestion.VERY_GOOD, TNCViabilityFormula.getTotalCategoryRatingCode(
				new CodeList(new String[] {StatusQuestion.VERY_GOOD, StatusQuestion.UNSPECIFIED, 
						StatusQuestion.VERY_GOOD})));
		assertEquals(StatusQuestion.POOR, TNCViabilityFormula.getTotalCategoryRatingCode(
				new CodeList(new String[] {StatusQuestion.VERY_GOOD, StatusQuestion.POOR, 
						StatusQuestion.VERY_GOOD})));
		assertEquals(StatusQuestion.FAIR, TNCViabilityFormula.getTotalCategoryRatingCode(
				new CodeList(new String[] {StatusQuestion.VERY_GOOD, StatusQuestion.FAIR, 
						StatusQuestion.VERY_GOOD})));
		assertEquals(StatusQuestion.GOOD, TNCViabilityFormula.getTotalCategoryRatingCode(
				new CodeList(new String[] {StatusQuestion.GOOD})));
		assertEquals(StatusQuestion.GOOD, TNCViabilityFormula.getTotalCategoryRatingCode(
				new CodeList(new String[] {StatusQuestion.GOOD, StatusQuestion.VERY_GOOD})));
		assertEquals(StatusQuestion.VERY_GOOD, TNCViabilityFormula.getTotalCategoryRatingCode(
				new CodeList(new String[] {StatusQuestion.VERY_GOOD})));
		try
		{
			TNCViabilityFormula.getTotalCategoryRatingCode(new CodeList(new String[] {"8"}));
			fail("Should have thrown for unknown value");
		}
		catch(TNCViabilityFormula.UnexpectedValueException ignoreExpected)
		{
		}
		
	}

}
