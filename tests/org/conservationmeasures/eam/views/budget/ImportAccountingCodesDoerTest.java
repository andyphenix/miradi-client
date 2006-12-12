/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.budget;

import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.AccountingCode;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.project.ProjectForTesting;

public class ImportAccountingCodesDoerTest extends EAMTestCase
{

	public ImportAccountingCodesDoerTest(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		project = new ProjectForTesting(getName());
		super.setUp();
	}
	
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
	}
	
	
	public void testTwoRows() throws Exception
	{
		String data = "code1A \t label1A \n code1B \t label1B";
		AccountingCode[] accountingCodes = ImportAccountingCodesDoer.importCodes(data, project);
		assertEquals(2, accountingCodes.length);
		EAMObject object = project.findObject(ObjectType.ACCOUNTING_CODE, accountingCodes[0].getId());
		assertNotNull(object);
		AccountingCode accountingCode = (AccountingCode) object; 
		assertEquals("label1A", accountingCode.getLabel());
		assertEquals("code1A", accountingCode.getData(AccountingCode.TAG_CODE));
	}

	public void testRejectionOfDuplicates() throws Exception
	{
		testTwoRows();
		String data = "code1A \t label1A \n code1B \t label1B";
		ImportAccountingCodesDoer.importCodes(data, project);
		assertEquals(2, project.getPool(ObjectType.ACCOUNTING_CODE).size());
	}
	
	Project project;
}