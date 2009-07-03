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
package org.miradi.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class OptionalDouble
{	
	public OptionalDouble()
	{
		this(null);
	}
	
	public OptionalDouble(Double doubleToUse)
	{
		optionalDouble = doubleToUse;
		
		resetHasValue(optionalDouble);
	}
	
	private void resetHasValue(Double valueToUse)
	{
		if (valueToUse != null)
			hasValue = true;
	}
	
	public OptionalDouble addValue(Double doubleToAdd)
	{
		return add(new OptionalDouble(doubleToAdd));
	}
	
	public OptionalDouble add(OptionalDouble optionalDoubleToAdd)
	{ 
		if (isValidValue(optionalDoubleToAdd) && hasValue())
			return new OptionalDouble(optionalDouble + optionalDoubleToAdd.getRawValue()); 

		if (isValidValue(optionalDoubleToAdd))
			return new OptionalDouble(optionalDoubleToAdd.getRawValue());
		
		return new OptionalDouble(getRawValue());
	}
	
	public OptionalDouble multiplyValue(Double doubleToMultiply)
	{
		return multiply(new OptionalDouble(doubleToMultiply));
	}
	
	public OptionalDouble multiply(OptionalDouble optionalDoubleToMultiply)
	{
		if (isValidValue(optionalDoubleToMultiply) && hasValue())
			return new OptionalDouble(optionalDoubleToMultiply.getRawValue() * getRawValue());
		
		return new OptionalDouble();
	}

	private boolean isValidValue(OptionalDouble optionalDoubleToUse)
	{
		if (optionalDoubleToUse == null)
			return false;
		
		return optionalDoubleToUse.hasValue();
	}
	
	public boolean hasValue()
	{
		return hasValue;
	}
	
	public boolean hasNoValue()
	{
		return !hasValue();
	}
	
	private Double getRawValue()
	{
		return optionalDouble;
	}
	
	public double getValue()
	{
		return getRawValue().doubleValue();
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if (! (rawOther instanceof OptionalDouble))
			return false;
	
		OptionalDouble other = (OptionalDouble) rawOther;
		if (!other.hasValue() && !hasValue())
			return true;
		
		if (!other.hasValue())
			return false;
		
		return other.getRawValue().equals(getRawValue());
	}
	
	@Override
	public int hashCode()
	{
		if (hasValue())
			return optionalDouble.hashCode();
		
		return 0;
	}
	
	@Override
	public String toString()
	{
		if(hasValue())
		{
			NumberFormat formatter = new DecimalFormat();
			formatter.setGroupingUsed(false);
			formatter.setMinimumFractionDigits(0);
			return formatter.format(getRawValue());
		}
		
		return "";
	}
	
	private boolean hasValue;
	private Double optionalDouble;
}
