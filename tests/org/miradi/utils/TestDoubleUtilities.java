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

package org.miradi.utils;

import java.util.Locale;

import org.miradi.main.MiradiTestCase;

public class TestDoubleUtilities extends MiradiTestCase
{
	public TestDoubleUtilities(String name)
	{
		super(name);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		previousLocale = Locale.getDefault();
	}
	
	@Override
	public void tearDown() throws Exception
	{
		setLocale(previousLocale);
		
		super.tearDown();
	}
	
	public void testToDoubleForDataUnderGermanLocale() throws Exception
	{
		try
		{
			switchToGermanLocale();
			assertEquals("incorrect value?", 5.0, DoubleUtilities.toDoubleFromDataFormat("5.0"));
			assertEquals("incorrect value?", 1000.5, DoubleUtilities.toDoubleFromDataFormat("1,000.5"));
			assertEquals("incorrect value?", -1000.5, DoubleUtilities.toDoubleFromDataFormat("-1,000.5"));
		}
		finally 
		{
			setLocale(previousLocale);
		}
	}
	
	public void testToDoubleForData() throws Exception
	{
		try
		{
			switchToGermanLocale();
			assertEquals("incorrect value?", 0.5, DoubleUtilities.toDoubleFromDataFormat("0.5"));
			assertEquals("incorrect value?", 1000.5, DoubleUtilities.toDoubleFromDataFormat("1,000.5"));
			assertEquals("incorrect value?", -1000.5, DoubleUtilities.toDoubleFromDataFormat("-1,000.5"));
		}
		finally 
		{
			setLocale(previousLocale);
		}
	}

	public void testToDoubleForHumans() throws Exception
	{
		try
		{
			switchToGermanLocale();
			assertEquals("incorrect value?", 0.5, DoubleUtilities.toDoubleFromHumanFormat("0,5"));
			assertEquals("incorrect value?", 1000.5, DoubleUtilities.toDoubleFromHumanFormat("1.000,5"));
			assertEquals("incorrect value?", -1000.5, DoubleUtilities.toDoubleFromHumanFormat("-1.000,5"));
		}
		finally 
		{
			setLocale(previousLocale);
		}
		
		switchToUsLocale();
		assertEquals("incorrect value?", 0.5, DoubleUtilities.toDoubleFromHumanFormat("0.5"));
		assertEquals("incorrect value?", 1000.5, DoubleUtilities.toDoubleFromHumanFormat("1000.5"));
		assertEquals("incorrect value?", -1000.5, DoubleUtilities.toDoubleFromHumanFormat("-1000.5"));
	}
	
	public void testToStringForData()
	{
		try
		{
			switchToGermanLocale();
			assertEquals("incorrect value?", "0.5", DoubleUtilities.toStringForData(0.5));
			assertEquals("incorrect value?", "1000.5", DoubleUtilities.toStringForData(1000.5));
			assertEquals("incorrect value?", "-1000.5", DoubleUtilities.toStringForData(-1000.5));
		}
		finally 
		{
			setLocale(previousLocale);
		}
		
		switchToUsLocale();
		assertEquals("incorrect value", "0.5", DoubleUtilities.toStringForData(0.5));
		assertEquals("incorrect value", "1000.5", DoubleUtilities.toStringForData(1000.5));
		assertEquals("incorrect value", "-1000.5", DoubleUtilities.toStringForData(-1000.5));
	}
	
	public void testToStringForHumans()
	{
		try
		{
			switchToGermanLocale();
			assertEquals("incorrect value?", "0,5", DoubleUtilities.toStringForHumans(0.5));
			assertEquals("incorrect value?", "1.000,5", DoubleUtilities.toStringForHumans(1000.5));
			assertEquals("incorrect value?", "-1.000,5", DoubleUtilities.toStringForHumans(-1000.5));
		}
		finally 
		{
			setLocale(previousLocale);
		}
		
		switchToUsLocale();
		assertEquals("incorrect value?", "0.5", DoubleUtilities.toStringForHumans(0.5));
		assertEquals("incorrect value?", "1,000.5", DoubleUtilities.toStringForHumans(1000.5));
		assertEquals("incorrect value?", "-1,000.5", DoubleUtilities.toStringForHumans(-1000.5));
	}
	
	private void switchToGermanLocale()
	{
		setLocale(Locale.GERMANY);
		assertEquals("didnt change to german locale", "Deutschland", Locale.getDefault().getDisplayCountry());
	}

	private void switchToUsLocale()
	{
		setLocale(Locale.US);
	}

	private void setLocale(Locale locale)
	{
		Locale.setDefault(locale);
	}
	
	private Locale previousLocale;
}
