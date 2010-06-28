/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.objectdata.DateUnitEffortListData;
import org.miradi.objectdata.ORefData;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.TimePeriodCosts;
import org.miradi.objecthelpers.TimePeriodCostsMap;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.project.ProjectCalendar;
import org.miradi.utils.DateRange;
import org.miradi.utils.DateUnitEffort;
import org.miradi.utils.DateUnitEffortList;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.OptionalDouble;

abstract public class Assignment extends BaseObject
{
	public Assignment(ObjectManager objectManagerToUse, BaseId idToUse)
	{
		super(objectManagerToUse, idToUse);
	}
	
	public Assignment(ObjectManager objectManager, BaseId idToUse, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, idToUse, json);
	}
	
	public DateUnitEffortList getDateUnitEffortList() throws Exception
	{
		return new DateUnitEffortList(getData(TAG_DATEUNIT_EFFORTS));
	}
	
	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return new int[] {
			Strategy.getObjectType(), 
			Indicator.getObjectType(),
			Task.getObjectType(),
			};
	}
	
	@Override
	protected TimePeriodCostsMap getTimePeriodCostsMap(String tag) throws Exception
	{
		return convertDateUnitEffortList();
	}
		
	protected TimePeriodCostsMap convertDateUnitEffortList() throws Exception
	{
		DateUnitEffortList duel = getDateUnitEffortList();
		TimePeriodCostsMap tpcm = new TimePeriodCostsMap();
		if (duel.size() == 0 && !isEmpty())
			addTimePeriodCostsInPlaceForNoData(duel, tpcm);
			
		for (int index = 0; index < duel.size(); ++index)
		{
			DateUnitEffort dateUnitEffort = duel.getDateUnitEffort(index);
			TimePeriodCosts timePeriodCosts = createTimePeriodCosts(new OptionalDouble(dateUnitEffort.getQuantity()));
			DateUnit dateUnit = dateUnitEffort.getDateUnit();
			if (shouldIncludeEffort(dateUnit))
			{
				tpcm.add(dateUnit, timePeriodCosts);
			}
		}
		
		return tpcm;
	}

	private boolean shouldIncludeEffort(DateUnit dateUnit) throws Exception
	{
		if (matchesCurrentFiscalYearStartMonth(dateUnit))
			return false;

		return isWithinProjectDateRange(dateUnit);
	}

	private boolean matchesCurrentFiscalYearStartMonth(DateUnit dateUnit)
	{
		if (dateUnit.isYear())
		{
			int dateUnitStartMonth = dateUnit.getYearStartMonth();
			return dateUnitStartMonth != getProjectCalendar().getFiscalYearFirstMonth();
		}
		
		return false;
	}
	
	public boolean hasAnyYearDateUnitWithWrongStartMonth() throws Exception
	{	
		DateUnitEffortList duel = getDateUnitEffortList();
		for (int index = 0; index < duel.size(); ++index)
		{
			DateUnitEffort dateUnitEffort = duel.getDateUnitEffort(index);
			DateUnit dateUnit = dateUnitEffort.getDateUnit();
			if (matchesCurrentFiscalYearStartMonth(dateUnit))
				return true;
		}
		
		return false;
	}
	
	public TimePeriodCostsMap convertAllDateUnitEffortList() throws Exception
	{
		return createTimePeriodCostsMap(getDateUnitEffortList());
	}

	private TimePeriodCostsMap createTimePeriodCostsMap(DateUnitEffortList duel)
	{
		TimePeriodCostsMap tpcm = new TimePeriodCostsMap();
		for (int index = 0; index < duel.size(); ++index)
		{
			DateUnitEffort dateUnitEffort = duel.getDateUnitEffort(index);
			TimePeriodCosts timePeriodCosts = createTimePeriodCosts(new OptionalDouble(dateUnitEffort.getQuantity()));
			DateUnit dateUnit = dateUnitEffort.getDateUnit();
			tpcm.add(dateUnit, timePeriodCosts);
		}

		return tpcm;
	}
	
	private void addTimePeriodCostsInPlaceForNoData(DateUnitEffortList duel, TimePeriodCostsMap tpcm)
	{
		tpcm.add(new DateUnit(), createTimePeriodCosts(new OptionalDouble()));
	}

	private boolean isWithinProjectDateRange(final DateUnit dateUnit) throws Exception
	{
		DateRange projectPlanningDateRange = getProjectCalendar().getProjectPlanningDateRange();
		DateRange dateRange = getProjectCalendar().convertToDateRange(dateUnit);

		return projectPlanningDateRange.containsAtleastSome(dateRange);
	}

	private ProjectCalendar getProjectCalendar()
	{
		return getProject().getProjectCalendar();
	}
	
	public boolean hasCategoryData()
	{
		if (getFundingSourceRef().isValid())
			return true;
		
		if (getAccountingCodeRef().isValid())
			return true;
		
		return false;
	}
	
	public ORef getResourceRef()
	{
		return ORef.INVALID;
	}
	
	public ORef getCategoryOneRef()
	{
		return getRef(TAG_CATEGORY_ONE_REF);
	}
	
	public ORef getCategoryTwoRef()
	{
		return getRef(TAG_CATEGORY_TWO_REF);
	}
	
	public static boolean isAssignment(ORef ref)
	{
		return isAssignment(ref.getObjectType());
	}
	
	public static boolean is(ORef ref)
	{
		return isAssignment(ref.getObjectType());
	}
	
	public static boolean isAssignment(BaseObject baseObject)
	{
		return isAssignment(baseObject.getType());
	}
	
	public static boolean isAssignment(int objectType)
	{
		if (ResourceAssignment.is(objectType))
			return true;
		
		return ExpenseAssignment.is(objectType);
	}
	
	public static Assignment findAssignment(ObjectManager objectManager, ORef assignmentRef)
	{
		return (Assignment) find(objectManager, assignmentRef);
	}
	
	public static Assignment findAssignment(Project project, ORef assignmentRef)
	{
		return findAssignment(project.getObjectManager(), assignmentRef);
	}
	
	@Override
	public void clear()
	{
		super.clear();
		
		addField(new DateUnitEffortListData(TAG_DATEUNIT_EFFORTS));
		addField(new ORefData(TAG_CATEGORY_ONE_REF));
		addField(new ORefData(TAG_CATEGORY_TWO_REF));
	}
	
	abstract protected TimePeriodCosts createTimePeriodCosts(OptionalDouble quantity);
	
	abstract public ORef getFundingSourceRef();
	
	abstract public ORef getAccountingCodeRef();
	
	public static final String TAG_DATEUNIT_EFFORTS = "Details";
	public static final String TAG_CATEGORY_ONE_REF = "CategoryOneRef";
	public static final String TAG_CATEGORY_TWO_REF = "CategoryTwoRef";
}
