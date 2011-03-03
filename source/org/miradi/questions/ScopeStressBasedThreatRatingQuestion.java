/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.questions;

import org.miradi.main.EAM;

public class ScopeStressBasedThreatRatingQuestion extends ThreatRatingQuestion
{
	public ScopeStressBasedThreatRatingQuestion()
	{
		super(getDescription());
	}

	@Override
	protected String getLowRatingChoiceItemDescription()
	{
		return EAM.text("<html><b>Low:</b> The stress is likely to be <b>very narrow</b> in its scope, affecting the target across a <b>small proportion (1-10%)</b> of its occurrence/population.</html>");
	}
	
	@Override
	protected String getMediumRatingChoiceItemDescription()
	{
		return EAM.text("<html><b>Medium:</b> The stress is likely to be <b>restricted</b> in its scope, affecting the target across <b>some (11-30%)</b> of its occurrence/population.</html>");
	}

	@Override
	protected String getHighRatingChoiceItemDescription()
	{
		return EAM.text("<html><b>High:</b> The stress is likely to be <b>widespread</b> in its scope, affecting the target across <b>much (31-70%)</b> of its occurrence/population.</html>");
	}

	@Override
	protected String getVeryHighRatingChoiceItemDescription()
	{
		return EAM.text("<html><b>Very High:</b> The stress is likely to be <b>pervasive</b> in its scope, affecting the target across <b>all or most (71-100%)</b> of its occurrence/population.</html>");
	}
	
	private static String getDescription()
	{
		return EAM.text("<html><strong>Scope - </strong>Most commonly defined spatially as the proportion of the target " +
				"that can reasonably be expected to be affected by the stress within ten years given " +
				"the continuation of current circumstances and trends. For ecosystems and ecological communities, " +
				"measured as the proportion of the target's occurrence. For species, measured as the proportion " +
				"of the target's population.</html>");
	}
}
