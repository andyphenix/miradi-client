/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.utils;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

public class PointList
{
	public PointList()
	{
		this(new Vector());
	}
	
	public PointList(PointList copyFrom)
	{
		this(new Vector(copyFrom.data));
	}
	
	public PointList(EnhancedJsonObject json) throws Exception
	{
		this();
		EnhancedJsonArray array = json.optJsonArray(TAG_POINTS);
		if(array == null)
			array = new EnhancedJsonArray();
		
		for(int i = 0; i < array.length(); ++i)
		{
			Point point = EnhancedJsonObject.convertToPoint(array.getString(i));
			add(point);
		}
	}
	
	public PointList(String listAsJsonString) throws Exception
	{
		this(new EnhancedJsonObject(listAsJsonString));
	}
	
	private PointList(List dataToUse)
	{
		data = new Vector(dataToUse);
	}
	
	public int size()
	{
		return data.size();
	}
	
	public void insertAt(Point point, int index)
	{
		data.insertElementAt(point, index);
	}
	
	public void add(Point point)
	{
		data.add(point);
	}
	
	public void addAll(List listToAdd)
	{
		data.addAll(listToAdd);
	}
		
	public Point get(int index)
	{
		return (Point)data.get(index);
	}
	
	public Vector getAllPoints()
	{
		return data;
	}
	
	public boolean contains(Point point)
	{
		return data.contains(point);
	}
	
	public int find(Point point)
	{
		return data.indexOf(point);
	}
	
	public void removePoint(int index)
	{
		data.remove(index);
	}
	
	public void removePoint(Point point)
	{
		if(!data.contains(point))
			throw new RuntimeException("Attempted to remove non-existant point: " + point + " from: " + toString());
		
		data.remove(point);
	}
	
	public void subtract(PointList other)
	{
		for(int i = 0; i < other.size(); ++i)
		{
			Point point = other.get(i);
			if (contains(point))
				removePoint(point);
		}
	}
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = new EnhancedJsonObject();
		EnhancedJsonArray array = new EnhancedJsonArray();
		for(int i = 0; i < size(); ++i)
		{
			String pointAsString = EnhancedJsonObject.convertFromPoint(get(i));
			array.put(pointAsString);
		}
		json.put(TAG_POINTS, array);

		return json;
	}
	
	public String toString()
	{
		if(size() == 0)
			return "";
		
		return toJson().toString();
	}
	
	public boolean equals(Object rawOther)
	{
		if(! (rawOther instanceof PointList))
			return false;
		
		PointList other = (PointList)rawOther;
		return data.equals(other.data);
	}
	
	public int hashCode()
	{
		return data.hashCode();
	}
	
	public PointList createClone()
	{
		PointList clonedList = new PointList();
		for (int i = 0; i < size(); ++i)
		{
			Point pointToClone = get(i);
			Point clonedPoint = new Point(pointToClone);
			
			clonedList.add(clonedPoint);
		}
		return clonedList;
	}
	
	public Point getClosestPoint(Point point)
	{
		if (size() == 0)
			return new Point(0, 0);
		
		Point closestPoint = (Point) data.get(0);
		for (int i = 0; i < data.size(); ++i)
		{
			Point currentPoint = (Point) data.get(i);
			double currentDistance2Point = currentPoint.distance(point);
			double closestDistance2Point = closestPoint.distance(point);
			if (currentDistance2Point < closestDistance2Point)
				closestPoint = currentPoint;
		}
		return closestPoint;
	}
	
	public Line2D.Double createLineSegment(Point2D fromBendPoint, Point2D toBendPoint)
	{
		Point point1 = Utility.convertPoint2DToPoint(fromBendPoint);
		Point point2 = Utility.convertPoint2DToPoint(toBendPoint);
		
		return new Line2D.Double(point1, point2);
	}
	
	public Line2D.Double[] convertToLineSegments()
	{
		if (size() <= 0)
			return new Line2D.Double[0];
		
		Line2D.Double[] allLineSegments = new Line2D.Double[size() - 1];
		for (int i = 0 ; i < size() - 1; ++i)
		{
			Point fromPoint = get(i);
			Point toPoint = get(i + 1);
			allLineSegments[i] = createLineSegment(fromPoint, toPoint);
		}
		
		return allLineSegments;
	}
	
	protected static final String TAG_POINTS = "Points";
	Vector data;

}
