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
package org.miradi.diagram;

import java.util.HashSet;

import org.miradi.objecthelpers.FactorSet;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.objects.FactorLink;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.project.Project;

public class ChainWalker
{
	public HashSet<DiagramLink> buildNormalChainAndGetDiagramLinks(DiagramObject diagramObjectToUse, DiagramFactor diagramFactor)
	{
		buildNormalChain(diagramObjectToUse, diagramFactor);
		return processedLinks;
	}
	
	public FactorSet buildNormalChainAndGetFactors(DiagramObject diagramObjectToUse, DiagramFactor diagramFactor)
	{
		buildNormalChain(diagramObjectToUse, diagramFactor);
		return getFactors();
	}
	
	public HashSet<DiagramFactor> buildNormalChainAndGetDiagramFactors(DiagramObject diagramObjectToUse, DiagramFactor diagramFactor)
	{
		buildNormalChain(diagramObjectToUse, diagramFactor);
		return getDiagramFactors();
	}
	
	public FactorSet buildUpstreamChainAndGetFactors(DiagramObject diagramObjectToUse, DiagramFactor diagramFactor)
	{
		buildUpstreamChain(diagramObjectToUse, diagramFactor);
		return getFactors();
	}
	
	public FactorSet buildDownstreamChainAndGetFactors(DiagramObject diagramObjectToUse, DiagramFactor diagramFactor)
	{
		buildDownstreamChain(diagramObjectToUse, diagramFactor);
		return getFactors();
	}
	
	public FactorSet buildDirectlyLinkedUpstreamChainAndGetFactors(DiagramObject diagram, DiagramFactor diagramFactor)
	{
		buildDirectlyLinkedUpstreamChain(diagram, diagramFactor);
		return getFactors();
	}
	
	public FactorSet buildNormalChainAndGetFactors(Factor factor)
	{
		Project project = factor.getProject();
		FactorSet factorsOnAllDiagrams = new FactorSet();
		ChainWalker realWalker = new ChainWalker();
		ORefList diagramFactorRefs = factor.findObjectsThatReferToUs(DiagramFactor.getObjectType());
		for(int i = 0; i < diagramFactorRefs.size(); ++i)
		{
			DiagramFactor df = DiagramFactor.find(project, diagramFactorRefs.get(i));
			ORefList diagramRefs = df.findDiagramsThatReferToUs();
			if(diagramRefs.size() != 1)
				throw new RuntimeException("DF " + df.getRef() + " is in multiple diagrams: " + diagramRefs);
			
			DiagramObject diagram = DiagramObject.findDiagramObject(project, diagramRefs.getFirstElement());
			FactorSet factorsOnThisDiagram = realWalker.buildNormalChainAndGetFactors(diagram, df);
			factorsOnAllDiagrams.attemptToAddAll(factorsOnThisDiagram);
		}
		
		return factorsOnAllDiagrams;
	}

	private void buildNormalChain(DiagramObject diagramObjectToUse , DiagramFactor diagramFactor)
	{
		initializeChain(diagramFactor);
		buildUpstreamDownstreamChain(diagramObjectToUse, diagramFactor);
	}
	
	private void buildUpstreamDownstreamChain(DiagramObject diagramObjectToUse, DiagramFactor diagramFactor)
	{
		initializeChain(diagramFactor);
		resultingFactors.addAll(getAllDownstreamFactors());
		resultingFactors.addAll(getAllUpstreamFactors());
	}
	
	private void buildUpstreamChain(DiagramObject diagramObjectToUse, DiagramFactor diagramFactor)
	{
		initializeChain(diagramFactor);
		resultingFactors.addAll(getAllUpstreamFactors());
	}
	
	private void buildDownstreamChain(DiagramObject diagram, DiagramFactor diagramFactor)
	{
		initializeChain(diagramFactor);
		resultingFactors.addAll(getAllDownstreamFactors());
	}
	
	private void buildDirectlyLinkedUpstreamChain(DiagramObject diagram, DiagramFactor diagramFactor)
	{
		initializeChain(diagramFactor);
		resultingFactors.addAll(getDirectlyLinkedUpstreamFactors());
	}
	
	private HashSet<Factor> getDirectlyLinkedFactors(int direction)
	{
		HashSet<Factor> results = new HashSet<Factor>();
		results.add(getStartingFactor());
		
		ORefList allDiagramLinkRefs = getAllDiagramLinkRefs();
		results.addAll(getFactorsToProcess(direction, allDiagramLinkRefs, getStartingFactor()));

		return results;
	}
		
	private HashSet<Factor> getAllLinkedFactors(int direction)
	{
		HashSet<Factor> linkedFactors = new HashSet<Factor>();
		HashSet<Factor> unprocessedFactors = new HashSet<Factor>();
		linkedFactors.add(getStartingFactor());

		ORefList allDiagramLinkRefs = getAllDiagramLinkRefs();
		unprocessedFactors.addAll(getFactorsToProcess(direction, allDiagramLinkRefs, getStartingFactor()));
		
		while(unprocessedFactors.size() > 0)
		{
			Factor thisFactor = (Factor)unprocessedFactors.toArray()[0];
			if (!linkedFactors.contains(thisFactor))
			{
				linkedFactors.add(thisFactor);
				unprocessedFactors.addAll(getFactorsToProcess(direction, allDiagramLinkRefs, thisFactor));
			}
			unprocessedFactors.remove(thisFactor);
		}
		
		return linkedFactors;
	}

	private HashSet<Factor> getFactorsToProcess(int direction, ORefList allDiagramLinkRefs, Factor factorToProcess)
	{
		HashSet<Factor> unprocessedFactors = new HashSet<Factor>();
		for(int index = 0; index < allDiagramLinkRefs.size(); ++index)
		{
			DiagramLink link = DiagramLink.find(getProject(), allDiagramLinkRefs.get(index));	
			Factor unprocessedFactor = processLink(factorToProcess, link, direction);
			if (unprocessedFactor != null)
				unprocessedFactors.add(unprocessedFactor);
		}
		
		return unprocessedFactors;
	}

	private ORefList getAllDiagramLinkRefs()
	{
		return diagramObject.getAllDiagramLinkRefs();
	}
		
	private void initializeChain(DiagramFactor diagramFactor)
	{
		ORefList diagramReferrers = diagramFactor.findObjectsThatReferToUs(new int[]{ResultsChainDiagram.getObjectType(), ConceptualModelDiagram.getObjectType(), });
		if (diagramReferrers.isEmpty() || diagramReferrers.size() > 1)
			throw new RuntimeException("DiagramFactor (ref= " + diagramFactor.getRef() + ") has incorrect number of diagram object referrers");
		
		diagramObject = DiagramObject.findDiagramObject(getProject(), diagramReferrers.getFirstElement());
		setStartingFactor(diagramFactor);
		resultingFactors = new HashSet<Factor>();
		processedLinks = new HashSet<DiagramLink>();
	}
	
	private Project getProject()
	{
		return getStartingFactor().getProject();
	}
	
	private Factor processLink(Factor thisFactor, DiagramLink diagramLink, int direction)
	{
		if(diagramLink.getDiagramFactor(direction).getWrappedORef().equals(thisFactor.getRef()))
		{
			processedLinks.add(diagramLink);
			Factor linkedNode = diagramLink.getOppositeDiagramFactor(direction).getWrappedFactor();
			return linkedNode;
		}
		
		if (!diagramLink.isBidirectional())
			return null;
		
		if(diagramLink.getOppositeDiagramFactor(direction).getWrappedORef().equals(thisFactor.getRef()))
		{
			processedLinks.add(diagramLink);
			Factor linkedNode = diagramLink.getDiagramFactor(direction).getWrappedFactor();
			return linkedNode;
		}
		
		return null;
	}
	
	private FactorSet getFactors()
	{
		FactorSet factorSet = new FactorSet();
		for(Factor factor : resultingFactors)
		{
			factorSet.attemptToAdd(factor);
		}
		return factorSet;
	}
	
	private HashSet<DiagramFactor> getDiagramFactors()
	{
		HashSet<DiagramFactor> diagramFactors = new HashSet<DiagramFactor>();
 		FactorSet processedFactors = getFactors();
 		for(Factor factor : processedFactors)
		{
			DiagramFactor diagramFactor = diagramObject.getDiagramFactor(factor.getRef());
			diagramFactors.add(diagramFactor);
		}
 		
 		return diagramFactors;
 		
	}

	private HashSet<Factor> getDirectlyLinkedUpstreamFactors()
	{
		return getDirectlyLinkedFactors(FactorLink.TO);
	}
	
	private HashSet<Factor> getAllUpstreamFactors()
	{
		return getAllLinkedFactors(FactorLink.TO);
	}
	
	private HashSet<Factor> getAllDownstreamFactors()
	{
		return getAllLinkedFactors(FactorLink.FROM);
	}
	
	private void setStartingFactor(DiagramFactor startingFactor)
	{
		this.startingFactor = startingFactor;
	}

	private Factor getStartingFactor()
	{
		return startingFactor.getWrappedFactor();
	}

	private DiagramObject diagramObject;
	private HashSet<Factor> resultingFactors;
	private HashSet<DiagramLink> processedLinks;
	private DiagramFactor startingFactor;
}
