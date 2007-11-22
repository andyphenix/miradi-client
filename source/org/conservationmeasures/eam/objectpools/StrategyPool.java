/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objectpools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.ids.IdAssigner;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.project.ObjectManager;

public class StrategyPool extends EAMNormalObjectPool
{

	public StrategyPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.STRATEGY);
	}
	
	public void put(Strategy strategy)
	{
		put(strategy.getId(), strategy);
	}
	
	public Strategy find(BaseId id)
	{
		return (Strategy)getRawObject(id);
	}
	
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId, CreateObjectParameter extraInfo) throws Exception
	{
		return new Strategy(objectManager ,new FactorId(actualId.asInt()));
	}
	
	public Factor[] getDraftStrategies()
	{
		Vector draftStrategies = new Vector();
		Factor[] allStrategies = getDraftAndNonDraftStrategies();
		for(int i = 0; i < allStrategies.length; ++i)
		{
			Strategy strategy = (Strategy)allStrategies[i];
			if(strategy.isStatusDraft())
				draftStrategies.add(strategy);
		}
		return (Factor[])draftStrategies.toArray(new Factor[0]);
	}
	
	public Factor[] getDraftAndNonDraftStrategies()
	{
		return getNodesOfType(ObjectType.STRATEGY);
	}
	
	private Factor[] getNodesOfType(int type)
	{
		Vector cmNodes = new Vector();
		FactorId[] ids = getModelNodeIds();
		Arrays.sort(ids);
		for(int i = 0; i < ids.length; ++i)
		{
			Factor cmNode = (Factor)getRawObject(ids[i]);
			if(cmNode.getType() == type)
				cmNodes.add(cmNode);
		}
		return (Factor[])cmNodes.toArray(new Factor[0]);
	}

	public FactorId[] getModelNodeIds()
	{
		return (FactorId[])new HashSet(getRawIds()).toArray(new FactorId[0]);
	}
	
	public Factor[] getNonDraftStrategies()
	{
		Vector nonDraftStrategies = new Vector();
		Factor[] allStrategies = getDraftAndNonDraftStrategies();
		for(int i = 0; i < allStrategies.length; ++i)
		{
			Strategy strategy = (Strategy)allStrategies[i];
			if(!strategy.isStatusDraft())
				nonDraftStrategies.add(strategy);
		}
		return (Factor[])nonDraftStrategies.toArray(new Factor[0]);
	}
	
	public Strategy[] getAllStrategies()
	{
		return (Strategy[]) getValues().toArray(new Strategy[0]);
	}

}
