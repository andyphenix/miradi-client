/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
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

package org.miradi.xml.wcs;

import java.awt.Point;
import java.util.Set;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.TimePeriodCosts;
import org.miradi.objecthelpers.TimePeriodCostsMap;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.Factor;
import org.miradi.objects.HumanWelfareTarget;
import org.miradi.objects.Target;
import org.miradi.schemas.ProjectResourceSchema;
import org.miradi.utils.DateRange;
import org.miradi.utils.OptionalDouble;

abstract public class BaseObjectPoolExporter extends ObjectPoolExporter
{
	public BaseObjectPoolExporter(Xmpz1XmlExporter wcsXmlExporterToUse, String containerNameToUse, int objectTypeToUse)
	{
		super(wcsXmlExporterToUse, containerNameToUse, objectTypeToUse);
	}

	@Override
	protected void writeObjectStartElement(BaseObject baseObject) throws Exception
	{
		getWcsXmlExporter().writeStartElementWithAttribute(getWriter(), getPoolName(), Xmpz1XmlConstants.ID, baseObject.getId().toString());
	}
	
	protected void writeProgressReportIds(BaseObject baseObject) throws Exception
	{
		writeOptionalIds(Xmpz1XmlConstants.PROGRESS_REPORT_IDS, Xmpz1XmlConstants.PROGRESS_REPORT, baseObject.getSafeRefListData(BaseObject.TAG_PROGRESS_REPORT_REFS));
	}
	
	protected void writeProgressPercetIds(ORefList progressPercentRefs) throws Exception
	{
		writeOptionalIds(Xmpz1XmlConstants.PROGRESS_PERCENT_IDS, Xmpz1XmlConstants.PROGRESS_PERCENT, progressPercentRefs);
	}
	
	protected void writeExpenseAssignmentIds(BaseObject baseObject) throws Exception
	{
		writeOptionalIds(Xmpz1XmlConstants.EXPENSE_IDS, Xmpz1XmlConstants.EXPENSE_ASSIGNMENT, baseObject.getSafeRefListData(BaseObject.TAG_EXPENSE_ASSIGNMENT_REFS));
	}
	
	protected void writeResourceAssignmentIds(BaseObject baseObject) throws Exception
	{
		writeOptionalIds(BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS, Xmpz1XmlConstants.RESOURCE_ASSIGNMENT, baseObject.getResourceAssignmentRefs());
	}
	
	protected void writeOptionalIndicatorIds(ORefList indicatorRefs) throws Exception
	{
		writeOptionalIndicatorIds("IndicatorIds", indicatorRefs);
	}

	protected void writeOptionalIndicatorIds(String idsElementName, ORefList indicatorRefs) throws Exception
	{
		writeOptionalIds(idsElementName, Xmpz1XmlConstants.INDICATOR, indicatorRefs);
	}
	
	protected String getFactorTypeName(Factor wrappedFactor)
	{
		if (Target.is(wrappedFactor))
			return Xmpz1XmlConstants.BIODIVERSITY_TARGET;
		
		if (HumanWelfareTarget.is(wrappedFactor))
			return Xmpz1XmlConstants.HUMAN_WELFARE_TARGET;
		
		if (Cause.is(wrappedFactor))
			return Xmpz1XmlConstants.CAUSE;
		
		return wrappedFactor.getTypeName();
	}
	
	protected void writeWrappedFactorId(Factor wrappedFactor) throws Exception
	{
		getWcsXmlExporter().writeStartElement(DIAGRAM_FACTOR + WRAPPED_FACTOR_ID_ELEMENT_NAME);
		writeWrappedFactorIdElement(wrappedFactor);
		getWcsXmlExporter().writeEndElement(DIAGRAM_FACTOR + WRAPPED_FACTOR_ID_ELEMENT_NAME);
	}

	private void writeWrappedFactorIdElement(Factor wrappedFactor) throws Exception
	{
		getWcsXmlExporter().writeStartElement(WRAPPED_BY_DIAGRAM_FACTOR_ID_ELEMENT_NAME);
		
		String factorTypeName = getFactorTypeName(wrappedFactor);
		getWcsXmlExporter().writeElement(factorTypeName, ID_ELEMENT_NAME, wrappedFactor.getFactorId().toString());
		
		getWcsXmlExporter().writeEndElement(WRAPPED_BY_DIAGRAM_FACTOR_ID_ELEMENT_NAME);
	}
	
	protected void writeFactorIds(String idsElementName, ORefList refs) throws Exception
	{
		writeFactorIds(getPoolName(), idsElementName, refs);
	}
	
	private void writeFactorIds(String parentElementName, String childElementName, ORefList refList) throws Exception
	{
		getWcsXmlExporter().writeStartElement(getWriter(), getWcsXmlExporter().createParentAndChildElementName(parentElementName, childElementName));
		for (int index = 0; index < refList.size(); ++index)
		{
			Factor factor = Factor.findFactor(getProject(), refList.get(index));
			writeWrappedFactorIdElement(factor);
		}
		
		getWcsXmlExporter().writeEndElement(getWriter(), getWcsXmlExporter().createParentAndChildElementName(parentElementName, childElementName));
	}
	
	protected void writeDiagramPoint(Point point) throws Exception
	{
		getWcsXmlExporter().writeStartElement(DIAGRAM_POINT_ELEMENT_NAME);
		getWcsXmlExporter().writeElement(getWriter(), X_ELEMENT_NAME, point.x);
		getWcsXmlExporter().writeElement(getWriter(), Y_ELEMENT_NAME, point.y);
		getWcsXmlExporter().writeEndElement(DIAGRAM_POINT_ELEMENT_NAME);
	}
	
	protected void writeOptionalCalculatedTimePeriodCosts(BaseObject baseObject) throws Exception
	{
		TimePeriodCostsMap totalBudgetCostsTimePeriodCostsMap = baseObject.getTotalTimePeriodCostsMap();
		TimePeriodCosts totalBudgetCost = totalBudgetCostsTimePeriodCostsMap.calculateTotalBudgetCost();
		
		final OptionalDouble totalCostValue = totalBudgetCost.calculateTotalCost(getProject());
		if (totalCostValue.hasValue())
		{
			final DateRange projectPlanningDateRange = getProject().getProjectCalendar().getProjectPlanningDateRange();
			DateRange totalDateRange = totalBudgetCostsTimePeriodCostsMap.getRolledUpDateRange(projectPlanningDateRange);
			getWcsXmlExporter().writeStartElement(getPoolName() + TIME_PERIOD_COSTS);

			getWcsXmlExporter().writeStartElement(TIME_PERIOD_COSTS);
			getWcsXmlExporter().writeElement(getWriter(), CALCULATED_START_DATE, totalDateRange.getStartDate().toIsoDateString());
			getWcsXmlExporter().writeElement(getWriter(), CALCULATED_END_DATE, totalDateRange.getEndDate().toIsoDateString());
			getWcsXmlExporter().writeElement(getWriter(),	CALCULATED_TOTAL_BUDGET_COST, totalCostValue.toString());

			writeResourceIds(CALCULATED_WHO, totalBudgetCost.getWorkUnitsRefSetForType(ProjectResourceSchema.getObjectType()));
			writeOptionalTotalCost(CALCULATED_EXPENSE_TOTAL, totalBudgetCost.getTotalExpense());

			writeOptionalTotalCost(CALCULATED_WORK_UNITS_TOTAL, totalBudgetCost.getTotalWorkUnits());			

			TimePeriodCostsMap expenseAssignmentTimePeriodCostsMap = baseObject.getExpenseAssignmentsTimePeriodCostsMap();
			TimePeriodCostsMap resourceAssignmentTimePeriodCostsMap = baseObject.getResourceAssignmentsTimePeriodCostsMap();
			new ExpenseTimePeriodCostsWriter(getWcsXmlExporter()).writeTimePeriodCosts(expenseAssignmentTimePeriodCostsMap.getDateUnitTimePeriodCostsMap());
			new WorkUnitsTimePeriodCostsWriter(getWcsXmlExporter()).writeTimePeriodCosts(resourceAssignmentTimePeriodCostsMap.getDateUnitTimePeriodCostsMap());

			getWcsXmlExporter().writeEndElement(TIME_PERIOD_COSTS);
			getWcsXmlExporter().writeEndElement(getPoolName() + TIME_PERIOD_COSTS);
		}
	}

	private void writeResourceIds(String elementName, Set<ORef> resourceRefs) throws Exception
	{
		if(resourceRefs.isEmpty())
			return;
		
		getWcsXmlExporter().writeStartElement(elementName);
		for(ORef resourceRef : resourceRefs)
		{
			getWcsXmlExporter().writeElement("", RESOURCE_ID, resourceRef.getObjectId().toString());
		}
		getWcsXmlExporter().writeEndElement(elementName);
	}

	private void writeOptionalTotalCost(final String totalCostElementName, final OptionalDouble totalCost) throws Exception
	{
		if (totalCost.hasValue())
		{
			getWcsXmlExporter().writeElement(getWriter(), totalCostElementName, totalCost.toString());
		}
	}
}
