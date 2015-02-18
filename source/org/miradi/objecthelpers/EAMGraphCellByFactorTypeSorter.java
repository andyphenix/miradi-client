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
package org.miradi.objecthelpers;

import java.util.Comparator;

import org.miradi.diagram.cells.EAMGraphCell;
import org.miradi.diagram.cells.FactorCell;

public class EAMGraphCellByFactorTypeSorter implements Comparator<EAMGraphCell>
{
	public int compare(EAMGraphCell o1, EAMGraphCell o2)
	{
		if (o1.isFactorLink() && o2.isFactorLink())
			return -1;
		
		if (o1.isFactorLink() && !o2.isFactorLink())
			return 1;
		
		if (!o1.isFactorLink() && o2.isFactorLink())
			return -1;
				
		FactorCell factorCell1 = (FactorCell) o1;
		FactorCell factorCell2 = (FactorCell) o2;
		
		Integer factorType1 = factorCell1.getWrappedFactorRef().getObjectType();
		Integer factorType2 = factorCell2.getWrappedFactorRef().getObjectType();
		
		return factorType1.compareTo(factorType2);
	}
}
