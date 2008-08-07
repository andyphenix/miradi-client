/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.dialogs.threatstressrating.properties;

import org.miradi.dialogs.base.EditableObjectTable;
import org.miradi.dialogs.threatstressrating.ThreatStressRatingTableModel;
import org.miradi.main.MainWindow;
import org.miradi.utils.RowHeightListener;

public class ThreatStressRatingTable extends EditableObjectTable
{
	public ThreatStressRatingTable(MainWindow mainWindowToUse, ThreatStressRatingTableModel threatStressRatingTableModel)
	{
		super(mainWindowToUse, threatStressRatingTableModel);
		rebuildColumnEditorsAndRenderers();
		//TODO shouldn't set row height to constant value
		setRowHeight(25);
		setForcedPreferredScrollableViewportWidth(PREFERRED_VIEWPORT_WIDTH);
		setForcedPreferredScrollableViewportHeight(PREFERRED_VIEWPORT_HEIGHT);
	}
	
	@Override
	public boolean allowUserToSetRowHeight()
	{
		return false;
	}
	
	
	@Override
	public void addRowHeightListener(RowHeightListener listener)
	{
		// NOTE: Override and do nothing to prevent user from changing row height
	}
	
	public ThreatStressRatingTableModel getThreatStressRatingTableModel()
	{
		return (ThreatStressRatingTableModel) getModel();
	}
	
	public void rebuildColumnEditorsAndRenderers()
	{
		ThreatStressRatingTableModel threatStressRatingTableModel = getThreatStressRatingTableModel();
		for (int tableColumn = 0; tableColumn < threatStressRatingTableModel.getColumnCount(); ++tableColumn)
		{
			int modelColumn = convertColumnIndexToModel(tableColumn);


			if (threatStressRatingTableModel.isStressRatingColumn(modelColumn))
				createReadonlyComboQuestionColumn(threatStressRatingTableModel.createStressRatingQuestion(modelColumn), tableColumn);
			
			if (threatStressRatingTableModel.isContributionColumn(modelColumn))
				createComboQuestionColumn(threatStressRatingTableModel.createContributionQuestion(modelColumn), tableColumn);
			
			if (threatStressRatingTableModel.isIrreversibilityColumn(modelColumn))
				createComboQuestionColumn(threatStressRatingTableModel.createIrreversibilityQuestion(modelColumn), tableColumn);
			
			if (threatStressRatingTableModel.isThreatRatingColumn(modelColumn))
				createReadonlyComboQuestionColumn(threatStressRatingTableModel.createThreatStressRatingQuestion(modelColumn), tableColumn);
		}
	}

	public String getUniqueTableIdentifier()
	{
		return UNIQUE_IDENTIFIER;
	}

	public static final String UNIQUE_IDENTIFIER = "ThreatStressRatingTable";
	public static final int PREFERRED_VIEWPORT_WIDTH = 700;
	public static final int PREFERRED_VIEWPORT_HEIGHT = 100;
}
