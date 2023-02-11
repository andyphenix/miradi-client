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

package org.miradi.xml.xmpz2.objectImporters;

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.AbstractAssumption;
import org.miradi.objects.SubAssumption;
import org.miradi.project.Project;
import org.miradi.schemas.SubAssumptionSchema;
import org.miradi.xml.xmpz2.Xmpz2XmlImporter;

public class SubAssumptionImporter extends AbstractAssumptionImporter
{
    public SubAssumptionImporter(Xmpz2XmlImporter importerToUse)
    {
        super(importerToUse, new SubAssumptionSchema());
    }

    @Override
    AbstractAssumption findAssumptionOrSubAssumption(Project project, ORef destinationRef)
    {
        return SubAssumption.find(project, destinationRef);
    }
}