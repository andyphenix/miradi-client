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
package org.miradi.objectpools;

import java.util.Arrays;
import java.util.Vector;

import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdAssigner;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Factor;
import org.miradi.objects.Strategy;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.BaseObjectSchema;

public class StrategyPool extends FactorPool
{

	public StrategyPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.STRATEGY);
	}
	
	public void put(Strategy strategy) throws Exception
	{
		put(strategy.getId(), strategy);
	}
	
	public Strategy find(BaseId id)
	{
		return (Strategy)getRawObject(id);
	}
	
	@Override
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId) throws Exception
	{
		return new Strategy(objectManager ,new FactorId(actualId.asInt()));
	}
	
	public Factor[] getDraftStrategies()
	{
		return getDraftStrategiesAsVector().toArray(new Factor[0]);
	}

	public Vector<Strategy> getDraftStrategiesAsVector()
	{
		Vector<Strategy> draftStrategies = new Vector<Strategy>();
		Factor[] allStrategies = getDraftAndNonDraftStrategies();
		for(int i = 0; i < allStrategies.length; ++i)
		{
			Strategy strategy = (Strategy)allStrategies[i];
			if(strategy.isStatusDraft())
				draftStrategies.add(strategy);
		}
		
		return draftStrategies;
	}
	
	public Factor[] getDraftAndNonDraftStrategies()
	{
		return getNodesOfType(ObjectType.STRATEGY);
	}
	
	private Factor[] getNodesOfType(int type)
	{
		Vector<Factor> cmNodes = new Vector<Factor>();
		BaseId[] ids = getIds();
		Arrays.sort(ids);
		for(int i = 0; i < ids.length; ++i)
		{
			Factor cmNode = (Factor)getRawObject(ids[i]);
			if(cmNode.getType() == type)
				cmNodes.add(cmNode);
		}
		return cmNodes.toArray(new Factor[0]);
	}
	
	public ORefSet getDraftStrategyRefs()
	{
		return new ORefSet(getDraftStrategies());
	}

	public ORefList getNonDraftStrategyRefs()
	{
		return new ORefList(getNonDraftStrategies());
	}
	
	public Factor[] getNonDraftStrategies()
	{
		return getNonDraftStrategiesAsVector().toArray(new Factor[0]);
	}

	public Vector<Strategy> getNonDraftStrategiesAsVector()
	{
		Vector<Strategy> nonDraftStrategies = new Vector<Strategy>();
		Factor[] allStrategies = getDraftAndNonDraftStrategies();
		for(int i = 0; i < allStrategies.length; ++i)
		{
			Strategy strategy = (Strategy)allStrategies[i];
			if(!strategy.isStatusDraft())
				nonDraftStrategies.add(strategy);
		}
		return nonDraftStrategies;
	}
	
	@Override
	public BaseObjectSchema createBaseObjectSchema(Project projectToUse)
	{
		return Strategy.createSchema();
	}
}
