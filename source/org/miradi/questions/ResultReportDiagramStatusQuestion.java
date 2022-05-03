/*
Copyright 2005-2022, Foundations of Success, Bethesda, Maryland
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

package org.miradi.questions;

public class ResultReportDiagramStatusQuestion extends AbstractResultReportStatusQuestion
{
    @Override
    protected String getNotSpecifiedLabel()
    {
        return "";
    }

    @Override
    protected String getNotKnownLabel()
    {
        return "?";
    }

    @Override
    protected String getNotYetLabel()
    {
        return "&gt;";
    }

    @Override
    protected String getNotAchievedLabel()
    {
        return "-";
    }

    @Override
    protected String getPartiallyAchievedLabel()
    {
        return "\u00B1";
    }

    @Override
    protected String getOnTrackLabel()
    {
        return "+";
    }

    @Override
    protected String getAchievedLabel()
    {
        return "++";
    }

    @Override
    protected String getNoLongerRelevantLabel()
    {
        return "x";
    }
}
