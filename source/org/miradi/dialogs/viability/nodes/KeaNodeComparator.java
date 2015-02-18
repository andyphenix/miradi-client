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
package org.miradi.dialogs.viability.nodes;

import java.text.Collator;
import java.util.Comparator;

import org.miradi.objects.KeyEcologicalAttribute;

public class KeaNodeComparator implements Comparator<KeyEcologicalAttributeNode>
{
	public int compare(KeyEcologicalAttributeNode node1, KeyEcologicalAttributeNode node2)
	{
		KeyEcologicalAttribute kea1 =(KeyEcologicalAttribute)node1.getObject();
		String type1 =kea1.getKeyEcologicalAttributeType();
		KeyEcologicalAttribute kea2 =(KeyEcologicalAttribute)node2.getObject();
		String type2 =kea2.getKeyEcologicalAttributeType();
		Collator myCollator = Collator.getInstance();
		return myCollator.compare(type1, type2);
	}
}
