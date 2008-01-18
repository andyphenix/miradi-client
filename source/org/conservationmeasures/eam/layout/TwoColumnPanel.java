/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.layout;

import javax.swing.JPanel;

public class TwoColumnPanel extends JPanel
{
	public TwoColumnPanel()
	{
		setLayout(new TwoColumnGridLayout());
	}
}
