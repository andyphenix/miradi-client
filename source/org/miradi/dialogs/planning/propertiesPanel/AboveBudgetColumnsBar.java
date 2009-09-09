/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.planning.propertiesPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Vector;

import javax.swing.JScrollPane;

import org.miradi.dialogs.planning.TableWithExpandableColumnsInterface;
import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.questions.CustomPlanningColumnsQuestion;

public class AboveBudgetColumnsBar extends AbstractFixedHeightDirectlyAboveTreeTablePanel implements AdjustmentListener
{
	public AboveBudgetColumnsBar(TableWithExpandableColumnsInterface tableToSitAbove)
	{
		table = tableToSitAbove;
	}
	
	public void setTableScrollPane(JScrollPane tableScrollPaneToUse)
	{
		tableScrollPane = tableScrollPaneToUse;
		tableScrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		validate();
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor(getBackground());
		g.fillRect(getX(), getY(), getWidth(), getHeight());
		DateUnit forever = new DateUnit();
		drawColumnGroupHeader(g, findColumnGroupBounds(getWorkUnitsColumnGroups()), EAM.text("Label|Work Units"), AppPreferences.getWorkUnitsBackgroundColor(forever));
		drawColumnGroupHeader(g, findColumnGroupBounds(getExpensesColumnGroups()), EAM.text("Label|Expenses"), AppPreferences.getExpenseAmountBackgroundColor(forever));
		drawColumnGroupHeader(g, findColumnGroupBounds(getBudgetTotalsColumnGroups()), EAM.text("Label|Budget Totals"), AppPreferences.getBudgetDetailsBackgroundColor(forever));
	}

	private void drawColumnGroupHeader(Graphics g, Rectangle groupHeaderArea, String text, Color backgroundColor)
	{
		if(groupHeaderArea == null)
			return;
		
		g.setColor(backgroundColor);
		g.fillRect(groupHeaderArea.x, groupHeaderArea.y, groupHeaderArea.width, groupHeaderArea.height);
		g.setColor(Color.BLACK);
		g.drawRect(groupHeaderArea.x, groupHeaderArea.y, groupHeaderArea.width, groupHeaderArea.height);
		
		Shape oldClip = g.getClip();
		try
		{
			g.clipRect(groupHeaderArea.x, groupHeaderArea.y, groupHeaderArea.width, groupHeaderArea.height);
			Graphics2D g2 = (Graphics2D) g;
			Rectangle fontBounds = g.getFont().getStringBounds(text, g2.getFontRenderContext()).getBounds();
			int textX = groupHeaderArea.x + groupHeaderArea.width/2 - fontBounds.width/2;
			int textY = groupHeaderArea.y + groupHeaderArea.height/2 + fontBounds.height/2 - ARBITRARY_MARGIN;
			g.drawString(text, textX, textY);
		}
		finally
		{
			g.setClip(oldClip);
		}
	}

	private Rectangle findColumnGroupBounds(Vector<String> columnGroups)
	{
		int startColumn = -1;
		int startX = 0;
		for(int tableColumn = 0; tableColumn < table.getColumnCount(); ++tableColumn)
		{
			if(columnGroups.contains(table.getColumnGroupCode(tableColumn)))
			{
				startColumn = tableColumn;
				break;
			}
			startX += table.getColumnWidth(tableColumn);
		}
		
		if(startColumn < 0)
			return null;

		Rectangle rect = new Rectangle(new Point(startX, getY()), new Dimension(0, getHeight()));
		while(startColumn < table.getColumnCount() && columnGroups.contains(table.getColumnGroupCode(startColumn)))
		{
			int thisColumnWidth = table.getColumnWidth(startColumn);
			rect.width += thisColumnWidth;
			++startColumn;
		}
		
		rect.x -= tableScrollPane.getHorizontalScrollBar().getValue();
		return rect;
	}

	private Vector<String> getWorkUnitsColumnGroups()
	{
		Vector<String> columnGroups = new Vector();
		columnGroups.add(CustomPlanningColumnsQuestion.META_RESOURCE_ASSIGNMENT_COLUMN_CODE);
		columnGroups.add(CustomPlanningColumnsQuestion.META_PROJECT_RESOURCE_WORK_UNITS_COLUMN_CODE);
		
		return columnGroups;
	}

	private Vector<String> getExpensesColumnGroups()
	{
		Vector<String> columnGroups = new Vector();
		columnGroups.add(CustomPlanningColumnsQuestion.META_EXPENSE_ASSIGNMENT_COLUMN_CODE);
		columnGroups.add(CustomPlanningColumnsQuestion.META_ACCOUNTING_CODE_EXPENSE_COLUMN_CODE);
		columnGroups.add(CustomPlanningColumnsQuestion.META_FUNDING_SOURCE_EXPENSE_COLUMN_CODE);
		
		return columnGroups;
	}

	private Vector<String> getBudgetTotalsColumnGroups()
	{
		Vector<String> columnGroups = new Vector();
		columnGroups.add(CustomPlanningColumnsQuestion.META_BUDGET_DETAIL_COLUMN_CODE);
		columnGroups.add(CustomPlanningColumnsQuestion.META_ACCOUNTING_CODE_BUDGET_DETAILS_COLUMN_CODE);
		columnGroups.add(CustomPlanningColumnsQuestion.META_FUNDING_SOURCE_BUDGET_DETAILS_COLUMN_CODE);
		
		return columnGroups;
	}

	private TableWithExpandableColumnsInterface table;
	private JScrollPane tableScrollPane;
}
