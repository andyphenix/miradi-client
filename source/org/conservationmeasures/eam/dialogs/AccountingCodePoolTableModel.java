/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs;

import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.AccountingCode;
import org.conservationmeasures.eam.project.Project;

public class AccountingCodePoolTableModel extends ObjectPoolTableModel
{
	public AccountingCodePoolTableModel(Project project)
	{
		super(project, ObjectType.ACCOUNTING_CODE, COLUMN_TAGS);
	}

	private static final String[] COLUMN_TAGS = new String[] {
		AccountingCode.TAG_CODE,
		AccountingCode.TAG_LABEL,
	};
}
