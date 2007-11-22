/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.ids;

import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objects.Cause;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class TestIdList extends EAMTestCase
{
	public TestIdList(String name)
	{
		super(name);
	}

	public void testBasics()
	{
		IdList list = new IdList();
		assertEquals("wrong initial size?", 0, list.size());
		BaseId id1 = new BaseId(7);
		BaseId id2 = new BaseId(19);
		list.add(id1);
		list.add(id2);
		assertEquals("wrong size?", 2, list.size());
		assertEquals("bad get 1?", id1, list.get(0));
		assertEquals("bad get 2?", id2, list.get(1));
	}
	
	public void testJson()
	{
		IdList list = createSampleIdList();
		EnhancedJsonObject json = list.toJson();
		
		IdList loaded = new IdList(0, json);
		assertEquals("wrong size?", list.size(), loaded.size());
		for(int i =0; i < list.size(); ++i)
			assertEquals("wrong member?", list.get(i), loaded.get(i));
	}
	
	public void testRemove()
	{
		IdList list = createSampleIdList();
		list.removeId(list.get(1));
		assertEquals(2, list.size());
		assertEquals(new BaseId(9998), list.get(1));
		
		try
		{
			list.removeId(new BaseId(3333333));
			fail("Should have thrown removing non-existant id");
		}
		catch (RuntimeException ignoreExpected)
		{
		}
		
	}
	
	public void testToString() throws Exception
	{
		IdList list = createSampleIdList();
		assertEquals("Can't rount trip?", list, new IdList(list.getObjectType(), list.toString()));
	}

	private IdList createSampleIdList()
	{
		IdList list = new IdList();
		list.add(25);
		list.add(13);
		list.add(9998);
		return list;
	}
	
	public void testEquals()
	{
		IdList list = createSampleIdList();
		IdList identical = createSampleIdList();
		assertEquals(list, identical);
		assertEquals(list.hashCode(), identical.hashCode());
		
		IdList different = new IdList();
		different.add(list.get(0));
		different.add(list.get(2));
		different.add(list.get(1));
		assertNotEquals("didn't compare order?", list, different);
		assertNotEquals("didn't hash everything?", list.hashCode(), different.hashCode());
		
		assertNotEquals("didn't check type?", list, new Object());
	}
	
	public void testSubtract()
	{
		IdList list12345 = new IdList();
		list12345.add(1);
		list12345.add(2);
		list12345.add(3);
		list12345.add(4);
		list12345.add(5);
		
		IdList list654 = new IdList();
		list654.add(6);
		list654.add(5);
		list654.add(4);
		
		IdList list123 = new IdList(list12345);
		list123.subtract(list654);
		assertEquals(3, list123.size());
		assertEquals(new BaseId(1), list123.get(0));
		assertEquals(new BaseId(2), list123.get(1));
		assertEquals(new BaseId(3), list123.get(2));
		
		IdList list6 = new IdList(list654);
		list6.subtract(list12345);
		assertEquals(1, list6.size());
		assertEquals(new BaseId(6), list6.get(0));
	}
	
	public void testFind()
	{
		BaseId[] ids = new BaseId[] { new BaseId(1), new BaseId(19), new BaseId(3), };
		IdList list = new IdList();
		for(int i = 0; i < ids.length; ++i)
			list.add(ids[i]);
		for(int i = 0; i < ids.length; ++i)
			assertEquals("Couldn't find " + i + "?", i, list.find(ids[i]));
		assertEquals("Found non-existant?", -1, list.find(new BaseId(27)));

	}
	
	public void testIdListWithType() throws Exception
	{
		IdList idListWithStrategyType = new IdList(Strategy.getObjectType());
		Strategy strategy = new Strategy(new FactorId(1));	
		Cause cause = new Cause(null, new FactorId(2));
		idListWithStrategyType.addRef(strategy.getRef());
		
		try 
		{
			idListWithStrategyType.addRef(cause.getRef());
			fail();
		}
		catch(Exception ignoreExpected)
		{
			
		}
		
		assertTrue("does not contain strategy?", idListWithStrategyType.contains(strategy.getRef()));
		assertFalse("does contain cause?", idListWithStrategyType.contains(cause.getRef()));
		
		assertEquals("wrong size?", 1, idListWithStrategyType.size());
		assertEquals("wrong ref?", strategy.getRef(), idListWithStrategyType.getRef(0));
	}
}
