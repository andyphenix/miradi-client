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

package org.miradi.questions;

import org.miradi.main.EAM;

public class IrreversibilityThreatRatingQuestion extends ThreatRatingQuestion
{
	public IrreversibilityThreatRatingQuestion()
	{
		super(getDescription());
	}

	@Override
	protected String getLowRatingChoiceItemDescription()
	{
		return EAM.text("<html><b>Low:</b> The effects of the threat <b>are easily reversible</b> and the target can be <b>easily</b> restored at a relatively <b>low cost and/or within 0-5 years</b> (e.g., off-road vehicles trespassing in wetland).</html>");
	}
	
	@Override
	protected String getMediumRatingChoiceItemDescription()
	{
		return EAM.text("<html><b>Medium:</b> The effects of the threat <b>can be reversed</b> and the target restored with a <b>reasonable commitment</b> of resources and/or within <b>6-20 years</b> (e.g., ditching and draining of wetland).</html>");
	}

	@Override
	protected String getHighRatingChoiceItemDescription()
	{
		return EAM.text("<html><b>High:</b> The effects of the threat <b>can technically be reversed</b> and the target restored, but it is <b>not practically affordable</b> and/or it would take <b>21-100 years</b> to achieve this (e.g., wetland converted to agriculture).</html>");
	}

	@Override
	protected String getVeryHighRatingChoiceItemDescription()
	{
		return EAM.text("<html><b>Very High:</b> The effects of the threat <b>cannot be reversed</b> and it is <b>very unlikely</b> the target can be restored, and/or it would take <b>more than 100 years</b> to achieve this (e.g., wetlands converted to a shopping center).</html>");
	}
	
	private static String getDescription()
	{
		return EAM.text("<html><strong>Irreversibility (Permanence) - </strong>The degree to which the effects of a " +
						"threat can be reversed and the target affected by the threat restored.</html>");
	}
}
