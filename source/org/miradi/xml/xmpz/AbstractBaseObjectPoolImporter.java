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

package org.miradi.xml.xmpz;

import java.awt.Point;

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Desire;
import org.miradi.objects.Factor;
import org.miradi.objects.ProgressPercent;
import org.miradi.objects.ProgressReport;
import org.miradi.schemas.ExpenseAssignmentSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.ResourceAssignmentSchema;
import org.miradi.xml.AbstractXmpzObjectImporter;
import org.miradi.xml.wcs.XmpzXmlConstants;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

abstract public class AbstractBaseObjectPoolImporter extends AbstractXmpzObjectImporter
{
	public AbstractBaseObjectPoolImporter(XmpzXmlImporter importerToUse, String poolNameToUse, int objectTypeToImportToUse)
	{
		super(importerToUse, poolNameToUse);
		
		objectTypeToImport = objectTypeToImportToUse;
	}
	
	@Override
	public void importElement() throws Exception
	{
		importObjects();
	}
	
	private void importObjects() throws Exception
	{
		NodeList nodes = getImporter().getNodes(getImporter().getRootNode(), new String[]{getPoolName() + XmpzXmlConstants.POOL_ELEMENT_TAG, getPoolName(), });
		for (int index = 0; index < nodes.getLength(); ++index)
		{
			Node node = nodes.item(index);
			String intIdAsString = getImporter().getAttributeValue(node, XmpzXmlConstants.ID);
			ORef ref = getProject().createObject(getObjectTypeToImport(), new BaseId(intIdAsString));
			postCreateFix(ref, node);
			
			importFields(node, ref);
		}
	}
	
	protected void postCreateFix(ORef ref, Node node) throws Exception
	{
	}

	private int getObjectTypeToImport()
	{
		return objectTypeToImport;
	}
	
	protected void importFields(Node node, ORef destinationRef) throws Exception
	{
		importField(node, destinationRef, BaseObject.TAG_LABEL);	
	}
	
	protected void importProgressReportRefs(Node node, ORef destinationRef) throws Exception
	{
		importRefs(node, XmpzXmlConstants.PROGRESS_REPORT_IDS, destinationRef, BaseObject.TAG_PROGRESS_REPORT_REFS, ProgressReport.getObjectType(), XmpzXmlConstants.PROGRESS_REPORT);
	}
	
	protected void importProgressPercentRefs(Node node, ORef destinationRef) throws Exception
	{
		importRefs(node, XmpzXmlConstants.PROGRESS_PERCENT_IDS, destinationRef, Desire.TAG_PROGRESS_PERCENT_REFS, ProgressPercent.getObjectType(), XmpzXmlConstants.PROGRESS_PERCENT );
	}
	
	protected void importExpenseAssignmentRefs(Node node, ORef destinationRef) throws Exception
	{
		importRefs(node, XmpzXmlConstants.EXPENSE_IDS, destinationRef, BaseObject.TAG_EXPENSE_ASSIGNMENT_REFS, ExpenseAssignmentSchema.getObjectType(), XmpzXmlConstants.EXPENSE_ASSIGNMENT);
	}

	protected void importResourceAssignmentIds(Node node, ORef destinationRef) throws Exception
	{
		importIds(node, destinationRef, BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS, ResourceAssignmentSchema.getObjectType(), XmpzXmlConstants.RESOURCE_ASSIGNMENT);
	}
	
	protected void importIndicatorIds(Node node, ORef destinationRef) throws Exception
	{
		importIds(node, destinationRef, Factor.TAG_INDICATOR_IDS, IndicatorSchema.getObjectType(), XmpzXmlConstants.INDICATOR);
	}
	
	protected Point extractPointFromNode(Node pointNode) throws Exception
	{
		Node xNode = getImporter().getNode(pointNode, XmpzXmlConstants.X_ELEMENT_NAME);
		Node yNode = getImporter().getNode(pointNode, XmpzXmlConstants.Y_ELEMENT_NAME);
		int x = extractNodeTextContentAsInt(xNode);
		int y = extractNodeTextContentAsInt(yNode);
		
		return new Point(x, y);
	}
	
	private int objectTypeToImport;
}