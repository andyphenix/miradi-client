package org.conservationmeasures.eam.views;

import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.ColumnWidthSaver;

abstract public class TreeTableWithColumnWidthSaving extends TreeTableWithStateSaving
{
	public TreeTableWithColumnWidthSaving(Project projectToUse, GenericTreeTableModel treeTableModel)
	{
		super(projectToUse, treeTableModel);
		columnWidthSaver = new ColumnWidthSaver(this);
		getTableHeader().addMouseListener(columnWidthSaver);
	}
	
	public void rebuildTableCompletely()
	{
		super.rebuildTableCompletely();
		columnWidthSaver.restoreColumnWidths();
	}

	abstract public String getUniqueTableIdentifier();
	
	private ColumnWidthSaver columnWidthSaver;
}
