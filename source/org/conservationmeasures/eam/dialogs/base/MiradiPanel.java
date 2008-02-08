/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.base;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.conservationmeasures.eam.main.AppPreferences;

public class MiradiPanel extends JPanel
{
	public MiradiPanel()
	{
		super();
	}
	
	public MiradiPanel(LayoutManager layout)
	{
		super(layout);
		
		setBackground(AppPreferences.DARK_PANEL_BACKGROUND);
	}	
}
