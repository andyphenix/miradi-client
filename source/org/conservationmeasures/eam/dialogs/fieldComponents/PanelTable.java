/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.fieldComponents;

import javax.swing.table.TableModel;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.utils.TableWithHelperMethods;

public class PanelTable extends TableWithHelperMethods
{
	public PanelTable()
	{
		super();
		setFontData();
	}

	public PanelTable(TableModel model)
	{
		super(model);
		setFontData();
	}

	private void setFontData()
	{
		setFont(getMainWindow().getUserDataPanelFont());
		getTableHeader().setFont(getMainWindow().getUserDataPanelFont());
		setRowHeight(getFontMetrics(getFont()).getHeight());
	}
	
	//TODO: Richard: should not use static ref here
	private MainWindow getMainWindow()
	{
		return EAM.mainWindow;
	}
}
