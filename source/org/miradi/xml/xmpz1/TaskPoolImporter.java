/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml.xmpz1;

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Task;
import org.miradi.schemas.TaskSchema;
import org.miradi.xml.wcs.XmpzXmlConstants;
import org.w3c.dom.Node;

public class TaskPoolImporter extends FactorPoolImporter
{
	public TaskPoolImporter(XmpzXmlImporter importerToUse)
	{
		super(importerToUse, XmpzXmlConstants.TASK, TaskSchema.getObjectType());
	}
	
	@Override
	protected void importFields(Node node, ORef destinationRef)	throws Exception
	{
		super.importFields(node, destinationRef);
		
		importProgressReportRefs(node, destinationRef);
		importExpenseAssignmentRefs(node, destinationRef);
		importResourceAssignmentIds(node, destinationRef);
		importIds(node, destinationRef, Task.TAG_SUBTASK_IDS, TaskSchema.getObjectType(), XmpzXmlConstants.SUB_TASK);
	}
	
	@Override
	protected String getDetailsTag()
	{
		return Task.TAG_DETAILS;
	}
}
