/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.views.diagram.doers;

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Factor;
import org.miradi.objects.Task;

public class HideActivityBubbleDoer extends AbstractActivityVisibilityDoer
{

	@Override
	protected void doWork() throws Exception
	{
	}

	@Override
	protected boolean isAvailable(ORef selectedFactorRef)
	{
		return !isShowing(selectedFactorRef);
	}

	protected Factor getFactor(ORef factorRef)
	{
		return Task.find(getProject(), factorRef);
	}
	
	@Override
	protected ORef getSelectedAnnotationRef()
	{
		return getSelectedActivityRef();
	}
}
