/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.objecthelpers;

import org.conservationmeasures.eam.objects.Factor;

public class NonDraftInterventionSet extends ConceptualModelNodeSet
{
	public NonDraftInterventionSet(ConceptualModelNodeSet nodesToAttemptToAdd)
	{
		attemptToAddAll(nodesToAttemptToAdd);
	}

	public boolean isLegal(Factor node)
	{
		return (node.isIntervention() && !node.isStatusDraft());
	}
	

}
