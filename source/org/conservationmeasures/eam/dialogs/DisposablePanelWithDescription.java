/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import java.awt.BorderLayout;
import java.awt.LayoutManager2;

abstract public class DisposablePanelWithDescription extends DisposablePanel
{
	public DisposablePanelWithDescription()
	{
		this(new BorderLayout());
	}
	
	public DisposablePanelWithDescription(LayoutManager2 layoutToUse)
	{
		super(layoutToUse);
	}

	public abstract String getPanelDescription();
}
