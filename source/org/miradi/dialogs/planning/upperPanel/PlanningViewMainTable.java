package org.miradi.dialogs.planning.upperPanel;

import java.awt.Color;

import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableColumn;

import org.miradi.dialogs.tablerenderers.BasicTableCellRenderer;
import org.miradi.dialogs.tablerenderers.BudgetCostTreeTableCellRenderer;
import org.miradi.dialogs.tablerenderers.ChoiceItemTableCellRenderer;
import org.miradi.dialogs.tablerenderers.FontForObjectTypeProvider;
import org.miradi.dialogs.tablerenderers.TableCellRendererForObjects;
import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Indicator;
import org.miradi.objects.Strategy;
import org.miradi.objects.Task;
import org.miradi.utils.TableWithTreeTableNodes;

public class PlanningViewMainTable extends TableWithTreeTableNodes
{
	public PlanningViewMainTable(PlanningViewMainTableModel modelToUse, FontForObjectTypeProvider fontProviderToUse)
	{
		super(modelToUse);
		fontProvider = fontProviderToUse;
		setTableColumnRenderers();
	}

	private void setTableColumnRenderers()
	{
		for (int i  = 0; i < getColumnModel().getColumnCount(); ++i)
			setColumnRenderer(i);
	}

	private void setColumnRenderer(int column)
	{
		String columnTag = getColumnTag(column);
		BasicTableCellRenderer renderer = createRendererForColumn(columnTag);
		renderer.setCellBackgroundColor(getBackgroundColor(columnTag));

		TableColumn tableColumn = getColumnModel().getColumn(column);
		tableColumn.setCellRenderer(renderer);
	}

	private BasicTableCellRenderer createRendererForColumn(String columnTag)
	{
		if(columnTag.equals(Task.PSEUDO_TAG_BUDGET_TOTAL))
			return new BudgetCostTreeTableCellRenderer(this, fontProvider);
		if(isQuestionColumn(columnTag))
			return new ChoiceItemTableCellRenderer(this, fontProvider);
		return new TableCellRendererForObjects(this, fontProvider);
	}
	
	protected Color getBackgroundColor(String columnTag)
	{
		if (columnTag.equals(BaseObject.PSEUDO_TAG_WHO_TOTAL))
			return AppPreferences.RESOURCE_TABLE_BACKGROUND;
		
		if (columnTag.equals(Indicator.PSEUDO_TAG_METHODS))
			return AppPreferences.INDICATOR_COLOR;
		
		if(columnTag.equals(BaseObject.PSEUDO_TAG_WHEN_TOTAL))
			return AppPreferences.WORKPLAN_TABLE_BACKGROUND;
		
		if(columnTag.equals(Task.PSEUDO_TAG_BUDGET_TOTAL))
			return AppPreferences.BUDGET_TOTAL_TABLE_BACKGROUND;
			
		return Color.white;
	}
	
	public boolean isQuestionColumn(String columnTag)
	{
		if(columnTag.equals(Strategy.PSEUDO_TAG_RATING_SUMMARY))
			return true;
		
		if(columnTag.equals(Indicator.TAG_PRIORITY))
			return true;
		
		if(columnTag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
			return true;
		
		return false;
	}
	
	@Override
	public void columnAdded(TableColumnModelEvent event)
	{
		super.columnAdded(event);
		int column = event.getToIndex();
		setColumnRenderer(column);
		try
		{
			restoreWidthsAndSequence();
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}

	@Override
	public String getUniqueTableIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}
	
	public static final String UNIQUE_IDENTIFIER = "PlanningViewMainTable";

	private FontForObjectTypeProvider fontProvider;
}
