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


import org.miradi.utils.DoubleUtilities;
import org.miradi.utils.FloatingPointFormatter;
import org.miradi.utils.InvalidNumberException;

public class NumberData extends ObjectData
{
	public NumberData(String tagToUse)
	{
		super(tagToUse);

		value = Double.NaN;
	}
	
	@Override
	public void set(String newValue) throws Exception
	{
		if(newValue.length() == 0)
		{
			value = Double.NaN;
			return;
		}
		
		try
		{
			value = DoubleUtilities.toDoubleForData(newValue);
		}
		catch (NumberFormatException e)
		{
			throw new InvalidNumberException(e);
		}
	}

	@Override
	public String get()
	{
		if(new Double(value).isNaN())
			return "";

		FloatingPointFormatter format = new FloatingPointFormatter();
		return format.format(value);
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof NumberData))
			return false;
		
		NumberData other = (NumberData)rawOther;
		return new Double(value).equals(new Double(other.value));
	}

	@Override
	public int hashCode()
	{
		return (int)value;
	}
	
	public double getSafeValue()
	{
		if (new Double(value).isNaN())
			return 0;

		return value;
	}
	
	double value;
}
