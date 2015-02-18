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
package org.miradi.objects;

import java.util.HashSet;
import java.util.Vector;

import org.miradi.diagram.ChainWalker;
import org.miradi.main.TestCaseWithProject;
import org.miradi.objecthelpers.ORef;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TargetSchema;

public class TestDiagramChainObject extends TestCaseWithProject
{
	public TestDiagramChainObject(String name)
	{
		super(name);
	}
	
	public void testThreatToThreatToTargetChain() throws Exception
	{
		DiagramFactor threat1 = getProject().createDiagramFactorAndAddToDiagram(CauseSchema.getObjectType());
		getProject().enableAsThreat(threat1.getWrappedORef());
		
		DiagramFactor threat2 = getProject().createDiagramFactorAndAddToDiagram(CauseSchema.getObjectType());
		getProject().enableAsThreat(threat2.getWrappedORef());
		
		DiagramFactor target = getProject().createDiagramFactorAndAddToDiagram(TargetSchema.getObjectType());
		
		getProject().createDiagramLinkAndAddToDiagram(threat1, threat2);
		getProject().createDiagramLinkAndAddToDiagram(threat2, target);
		
		Vector<DiagramFactor> allDiagramFactors = new Vector<DiagramFactor>();
		allDiagramFactors.add(threat1);
		allDiagramFactors.add(threat2);
		allDiagramFactors.add(target);
		
		verifyChain(threat1, allDiagramFactors);
		verifyChain(threat2, allDiagramFactors);
		verifyChain(target, allDiagramFactors);
	}
	
	public void testBasics() throws Exception
	{
		DiagramFactor strategy = getProject().createDiagramFactorAndAddToDiagram(StrategySchema.getObjectType());
		DiagramFactor causeLinkedTo = getProject().createDiagramFactorAndAddToDiagram(CauseSchema.getObjectType());
		DiagramFactor causeLinkedToFrom = getProject().createDiagramFactorAndAddToDiagram(CauseSchema.getObjectType());
		
		getProject().createDiagramLinkAndAddToDiagram(strategy, causeLinkedTo);
		ORef diagramLinkRef = getProject().createDiagramLinkAndAddToDiagram(strategy, causeLinkedToFrom);
		DiagramLink diagramLink = DiagramLink.find(getProject(), diagramLinkRef);
		diagramLink.setData(DiagramLink.TAG_IS_BIDIRECTIONAL_LINK, DiagramLink.BIDIRECTIONAL_LINK.toString());
		
		assertTrue("link is not bidirectional?", diagramLink.isBidirectional());
		
		Vector<DiagramFactor> allDiagramFactors = new Vector<DiagramFactor>();
		allDiagramFactors.add(strategy);
		allDiagramFactors.add(causeLinkedTo);
		allDiagramFactors.add(causeLinkedToFrom);
		
		verifyChain(strategy, allDiagramFactors);
		verifyChain(causeLinkedTo, allDiagramFactors);
		verifyChain(causeLinkedToFrom, allDiagramFactors);
	}

	private void verifyChain(DiagramFactor strartingDiagramFactor, Vector<DiagramFactor> allDiagramFactors)
	{
		ChainWalker chainObject = new ChainWalker();
		HashSet<DiagramFactor> diagramFactorsInChain = chainObject.buildNormalChainAndGetDiagramFactors(strartingDiagramFactor);
		assertEquals("wrong diagram factor in chain count?", 3, diagramFactorsInChain.size());
		
		for (int index = 0; index < allDiagramFactors.size(); ++index)
		{
			verifyDiagramFactorInChain(allDiagramFactors.get(index), diagramFactorsInChain);
		}
	}

	private void verifyDiagramFactorInChain(DiagramFactor strategy,	HashSet<DiagramFactor> diagramFactorsInChain)
	{
		assertTrue("diagramFactor not in chain?", diagramFactorsInChain.contains(strategy));
	}
}
