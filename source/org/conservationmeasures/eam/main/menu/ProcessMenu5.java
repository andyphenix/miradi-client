/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.main.menu;

import java.awt.event.KeyEvent;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.main.EAM;

public class ProcessMenu5 extends MiradiMenu
{
	public ProcessMenu5(Actions actions)
	{
		super(EAM.text("5. Capture and Share Learning"), actions);
		setMnemonic(KeyEvent.VK_C);
		
		add(new ProcessMenu5a(actions));
		add(new ProcessMenu5b(actions));
		add(new ProcessMenu5c(actions));
//		addMenuItem(ActionJumpAnalyzeData.class, KeyEvent.VK_A);
//		addMenuItem(ActionJumpAnalyzeStrategies.class, KeyEvent.VK_S);
//		addMenuItem(ActionJumpCommunicateResults.class, KeyEvent.VK_C);
	}
}
