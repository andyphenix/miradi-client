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
package org.miradi.dialogs.threatrating.upperPanel;

import javax.swing.table.AbstractTableModel;

import org.miradi.ids.FactorId;
import org.miradi.objects.Factor;
import org.miradi.objects.Target;
import org.miradi.project.Project;

abstract public class AbstractThreatTargetTableModel extends AbstractTableModel
{
	public AbstractThreatTargetTableModel(Project projectToUse)
	{
		project = projectToUse;
		
		resetTargetAndThreats();
	}
	
	public void resetTargetAndThreats()
	{
		threatRows =  getProject().getCausePool().getDirectThreats();
		targetColumns =  getProject().getTargetPool().getTargets();
	}
	
	public boolean isPopupSupportableCell(int row, int modelColumn)
	{
		return true;
	}
    	
	public Project getProject()
	{
		return project;
	}
	
	public boolean isActiveCell(int threatIndex, int targetIndex)
	{
		if(threatIndex < 0 || targetIndex < 0)
			return false;
		
		Factor threat = getDirectThreats()[threatIndex];
		Factor target = getTargets()[targetIndex];
		return getProject().areLinked(threat, target);
	}
	
	protected Factor[] getTargets()
	{
		return targetColumns;
	}
	
	protected Factor[] getDirectThreats()
	{
		return threatRows;
	}
	
	public Factor getDirectThreat(int row)
	{
		return threatRows[row];
	}
	
	public Target getTarget(int modelColumn)
	{
		return targetColumns[modelColumn];
	}

	public int getTargetCount()
	{
		return getTargets().length;
	}

	public int getThreatCount()
	{
		return getDirectThreats().length;
	}
	
	public String getThreatName(int threatIndex)
	{
		return getDirectThreats()[threatIndex].getLabel();
	}
	
	public String getTargetName(int targetIndex)
	{
		return getTargets()[targetIndex].getLabel();
	}

	public FactorId getThreatId(int threatIndex)
	{
		return getDirectThreats()[threatIndex].getFactorId();
	}

	public FactorId getTargetId(int targetIndex)
	{
		return getTargets()[targetIndex].getFactorId();
	}
	
	private Project project;
	protected Factor[] threatRows;
	protected Target[] targetColumns;	
}