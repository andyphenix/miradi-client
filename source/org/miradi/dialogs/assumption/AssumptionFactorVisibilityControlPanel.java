/*
Copyright 2005-2021, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.dialogs.assumption;

import org.miradi.actions.ActionHideAssumptionBubble;
import org.miradi.actions.ActionShowAssumptionBubble;
import org.miradi.dialogs.base.AbstractFactorBubbleVisibilityPanel;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ObjectType;

public class AssumptionFactorVisibilityControlPanel extends AbstractFactorBubbleVisibilityPanel
{
    public AssumptionFactorVisibilityControlPanel(MainWindow mainWindow) throws Exception
    {
        super(mainWindow, ObjectType.ASSUMPTION);
    }

    @Override
    protected Class getHideButtonClass()
    {
        return ActionHideAssumptionBubble.class;
    }

    @Override
    protected Class getShowButtonClass()
    {
        return ActionShowAssumptionBubble.class;
    }

    @Override
    protected String getExplanationMessage()
    {
        return EAM.text("Assumptions can be shown on both Conceptual Model and Results Chain pages");
    }

    @Override
    protected boolean shouldShowButtonPanel()
    {
        return true;
    }

    @Override
    public String getPanelDescription()
    {
        return EAM.text("Title|Assumption Visibility");
    }
}