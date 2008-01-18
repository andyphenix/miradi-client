/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.base;

import org.conservationmeasures.eam.main.MainWindow;

public class ModalDialogWithClose extends AbstractDialogWithClose
{
	public ModalDialogWithClose(MainWindow parent, DisposablePanel panel, String headingText)
	{
		super(parent, panel, headingText);
		setModal(true);
	}
}
