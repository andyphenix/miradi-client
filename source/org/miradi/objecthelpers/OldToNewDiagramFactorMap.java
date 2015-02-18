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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.miradi.objects.DiagramFactor;

public class OldToNewDiagramFactorMap extends HashMap<DiagramFactor, DiagramFactor>
{
	@Override
	public DiagramFactor put(DiagramFactor oldDiagramFactor, DiagramFactor newDiagramFactor)
	{
		if (containsKey(oldDiagramFactor))
			throw new RuntimeException("Key DiagramFactor already exists in map. DF ref = " + oldDiagramFactor.getRef());
		
		return super.put(oldDiagramFactor, newDiagramFactor);
	}
	
	@Override
	public void putAll(Map<? extends DiagramFactor, ? extends DiagramFactor> otherMap)
	{
		HashSet<DiagramFactor> keys = new HashSet<DiagramFactor>(keySet());
		HashSet<DiagramFactor> otherKeys = new HashSet<DiagramFactor>(otherMap.keySet());
		keys.retainAll(otherKeys);
		if (keys.size() > 0)
			throw new RuntimeException("Keys exist in both maps.");
		
		super.putAll(otherMap);
	}
}
