/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.project;

import java.util.Vector;

import org.conservationmeasures.eam.objecthelpers.FactorSet;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.objects.FactorLink;


abstract public class ChainObject
{	
	abstract FactorSet getAllLinkedFactors(int direction);
	abstract FactorSet getDirectlyLinkedFactors(int direction);


	public FactorSet getFactors()
	{
		return factorSet;
	}

	public Factor[] getFactorsArray()
	{	
		return (Factor[])factorSet.toArray(new Factor[0]);
	}

	public FactorLink[] getFactorLinksArray()
	{
		return (FactorLink[])processedLinks.toArray(new FactorLink[0]);
	}
	
	protected FactorSet getDirectlyLinkedDownstreamFactors()
	{
		return getDirectlyLinkedFactors(FactorLink.FROM);
	}
	
	protected FactorSet getDirectlyLinkedUpstreamFactors()
	{
		return getDirectlyLinkedFactors(FactorLink.TO);
	}
	
	protected FactorSet getAllUpstreamFactors()
	{
		return getAllLinkedFactors(FactorLink.TO);
	}
	
	protected FactorSet getAllDownstreamFactors()
	{
		return getAllLinkedFactors(FactorLink.FROM);
	}
	
	protected Project getProject()
	{
		return startingFactor.getProject();
	}
	
	protected void attempToAdd(FactorLink thisLinkage)
	{
		if (!processedLinks.contains(thisLinkage))
			processedLinks.add(thisLinkage);
	}
	
	protected void processLink(FactorSet unprocessedFactors, Factor thisFactor, FactorLink thisLink, int direction)
	{
		if(thisLink.getFactorRef(direction).equals(thisFactor.getRef()))
		{
			attempToAdd(thisLink);
			Factor linkedNode = (Factor) getProject().findObject(thisLink.getOppositeFactorRef(direction));
			unprocessedFactors.attemptToAdd(linkedNode);
			return;
		}
		
		if (!thisLink.isBidirectional())
			return;
		
		if(thisLink.getOppositeFactorRef(direction).equals(thisFactor.getRef()))
		{
			attempToAdd(thisLink);
			Factor linkedNode = (Factor) getProject().findObject(thisLink.getFactorRef(direction));
			unprocessedFactors.attemptToAdd(linkedNode);
		}
	}
	
	protected FactorSet factorSet;
	protected Vector processedLinks;
	protected Factor startingFactor;
}
