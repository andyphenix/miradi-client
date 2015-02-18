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

import org.miradi.diagram.ThreatTargetChainWalker;
import org.miradi.main.TestCaseWithProject;
import org.miradi.objectdata.BooleanData;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TargetSchema;

public class TestThreatTargetChainObject extends TestCaseWithProject
{
	public TestThreatTargetChainObject(String name)
	{
		super(name);
	}
	
	public void testBasics() throws Exception
	{
		verifyThreatThreatStrategyTarget();
		verifyThreatTargetTarget();
		verifyThreatCauseTarget();
		veriftThreat1TargetThreat2Target();
		verifyThreatTarget1ThreatTarget2();		
	}

	private void verifyThreatThreatStrategyTarget() throws Exception
	{
		DiagramFactor threat1 = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(CauseSchema.getObjectType());
		getProject().enableAsThreat(threat1.getWrappedORef());
				
		DiagramFactor threat2 = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(CauseSchema.getObjectType());
		getProject().enableAsThreat(threat2.getWrappedORef());
		
		DiagramFactor target = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(TargetSchema.getObjectType());
		DiagramFactor strategy = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(StrategySchema.getObjectType());
		
		//threat1 -> threat2 -> strategy -> target
		getProject().createDiagramLinkAndAddToDiagram(threat1, threat2); 
		getProject().createDiagramLinkAndAddToDiagram(threat2, strategy);			
		getProject().createDiagramLinkAndAddToDiagram(strategy, target);
		
		ThreatTargetChainWalker chainObject = new ThreatTargetChainWalker(getProject());
		verifySingleUpstreamThreat(chainObject, threat1.getWrappedFactor(), target.getWrappedFactor());
		verifySingleUpstreamThreat(chainObject, threat2.getWrappedFactor(), target.getWrappedFactor());
		verifySingleDownstreamTarget(chainObject, threat1.getWrappedFactor(), target.getWrappedFactor());
		verifySingleDownstreamTarget(chainObject, threat2.getWrappedFactor(), target.getWrappedFactor());
		verifyDoubleUpstreamThreats(chainObject, threat1.getWrappedFactor(), threat2.getWrappedFactor(), target.getWrappedFactor());
	}
	
	private void verifyThreatTargetTarget() throws Exception
	{
		DiagramFactor threat1 = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(CauseSchema.getObjectType());
		getProject().enableAsThreat(threat1.getWrappedORef());
				
		DiagramFactor target1 = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(TargetSchema.getObjectType());
		DiagramFactor target2 = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(TargetSchema.getObjectType());
		
		//threat1 -> target1 -> target2
		getProject().createDiagramLinkAndAddToDiagram(threat1, target1); 
		getProject().createDiagramLinkAndAddToDiagram(target1, target2);			
		
		ThreatTargetChainWalker chainObject = new ThreatTargetChainWalker(getProject());
		verifySingleUpstreamThreat(chainObject, threat1.getWrappedFactor(), target1.getWrappedFactor());	
		verifyDoubleDownstreamTargets(chainObject, threat1.getWrappedFactor(), target1.getWrappedFactor(), target2.getWrappedFactor());
	}
	
	private void verifyThreatCauseTarget() throws Exception
	{
		//threat1 -> cause -> target
		verifyThreatCauseTargetWithOptionalBidi(BooleanData.BOOLEAN_FALSE);
		
		//threat1 <-> cause <-> target
		verifyThreatCauseTargetWithOptionalBidi(BooleanData.BOOLEAN_TRUE);
	}
	
	private void veriftThreat1TargetThreat2Target() throws Exception
	{
		DiagramFactor threat1 = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(CauseSchema.getObjectType());
		getProject().enableAsThreat(threat1.getWrappedORef());
				
		DiagramFactor threat2 = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(CauseSchema.getObjectType());
		getProject().enableAsThreat(threat2.getWrappedORef());
		
		DiagramFactor target = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(TargetSchema.getObjectType());
		
		//threat1 -> target
		//threat2 -> target
		getProject().createDiagramLinkAndAddToDiagram(threat1, target); 
		getProject().createDiagramLinkAndAddToDiagram(threat2, target);			
		
		ThreatTargetChainWalker chainObject = new ThreatTargetChainWalker(getProject());
		verifyDoubleUpstreamThreats(chainObject, threat1.getWrappedFactor(), threat2.getWrappedFactor(), target.getWrappedFactor());	
		verifySingleDownstreamTarget(chainObject, threat1.getWrappedFactor(), target.getWrappedFactor());	
		verifySingleDownstreamTarget(chainObject, threat2.getWrappedFactor(), target.getWrappedFactor());
	}
	
	private void verifyThreatTarget1ThreatTarget2() throws Exception
	{
		DiagramFactor threat1 = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(CauseSchema.getObjectType());
		getProject().enableAsThreat(threat1.getWrappedORef());
				
		DiagramFactor target1 = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(TargetSchema.getObjectType());
		DiagramFactor target2 = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(TargetSchema.getObjectType());
		
		//threat1 -> target1
		//threat1 -> target2
		getProject().createDiagramLinkAndAddToDiagram(threat1, target1); 
		getProject().createDiagramLinkAndAddToDiagram(threat1, target2);			
		
		ThreatTargetChainWalker chainObject = new ThreatTargetChainWalker(getProject());
		verifySingleUpstreamThreat(chainObject, threat1.getWrappedFactor(), target1.getWrappedFactor());	
		verifySingleUpstreamThreat(chainObject, threat1.getWrappedFactor(), target2.getWrappedFactor());
		verifyDoubleDownstreamTargets(chainObject, threat1.getWrappedFactor(), target1.getWrappedFactor(), target2.getWrappedFactor());	
	}

	private void verifyThreatCauseTargetWithOptionalBidi(String isBidirectionalTag) throws Exception
	{
		DiagramFactor threat = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(CauseSchema.getObjectType());
		getProject().enableAsThreat(threat.getWrappedORef());
				
		DiagramFactor cause = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(CauseSchema.getObjectType());
		DiagramFactor target = getProject().createDiagramFactorWithWrappedRefLabelAndAddToDiagram(TargetSchema.getObjectType());
		
		getProject().createDiagramLinkAndAddToDiagram(threat, cause, isBidirectionalTag);
		getProject().createDiagramLinkAndAddToDiagram(cause, target, isBidirectionalTag);			
		
		ThreatTargetChainWalker chainObject = new ThreatTargetChainWalker(getProject());
		verifySingleUpstreamThreat(chainObject, threat.getWrappedFactor(), target.getWrappedFactor());
		verifySingleDownstreamTarget(chainObject, threat.getWrappedFactor(), target.getWrappedFactor());
	}

	private void verifySingleDownstreamTarget(ThreatTargetChainWalker chainObject, Factor threat, Factor target)
	{
		HashSet<Factor> downStreamTargetsFromThreat1 = chainObject.getDownstreamTargetsFromThreat(threat);
		assertEquals("wrong target count?", 1, downStreamTargetsFromThreat1.size());
		assertTrue("wrong threat in list?", downStreamTargetsFromThreat1.contains(target));
	}
	
	private void verifyDoubleDownstreamTargets(ThreatTargetChainWalker chainObject, Factor threat, Factor target1, Factor target2)
	{
		HashSet<Factor> downStreamTargetsFromThreat1 = chainObject.getDownstreamTargetsFromThreat(threat);
		assertEquals("wrong target count?", 2, downStreamTargetsFromThreat1.size());
		assertTrue("wrong threat in list?", downStreamTargetsFromThreat1.contains(target1));
		assertTrue("wrong threat in list?", downStreamTargetsFromThreat1.contains(target2));
	}
		
	private void verifySingleUpstreamThreat(ThreatTargetChainWalker chainObject, Factor threat, Factor target)
	{
		HashSet<Cause> upstreamThreats = chainObject.getUpstreamThreatsFromTarget(target);
		assertTrue("threat is not in chain?", upstreamThreats.contains(threat));
		assertTrue("wrong threat in list?", upstreamThreats.contains(threat));
	}
	
	private void verifyDoubleUpstreamThreats(ThreatTargetChainWalker chainObject, Factor threat1, Factor threat2, Factor target)
	{
		HashSet<Cause> upstreamThreats = chainObject.getUpstreamThreatsFromTarget(target);
		assertEquals("wrong threat count?", 2, upstreamThreats.size());
		assertTrue("wrong threat in list?", upstreamThreats.contains(threat1));
		assertTrue("wrong threat in list?", upstreamThreats.contains(threat2));
	}
}
