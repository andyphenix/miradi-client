/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.main.menu;

import java.awt.event.KeyEvent;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.main.EAM;

public class ProcessMenu2 extends MiradiMenu
{
	public ProcessMenu2(Actions actionsToUse)
	{
		super(EAM.text("2. Plan Actions and Monitoring"), actionsToUse);
		setMnemonic(KeyEvent.VK_A);
		
		add(new ProcessMenu2a(actions));
		add(new ProcessMenu2b(actions));
		add(new ProcessMenu2c(actions));
	}

}
