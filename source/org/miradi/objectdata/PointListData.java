/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
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
package org.miradi.objectdata;

import java.awt.Point;
import java.util.List;

import org.miradi.objecthelpers.ORef;
import org.miradi.schemas.AbstractFieldSchema;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.utils.PointList;
import org.miradi.xml.xmpz2.Xmpz2XmlImporter;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;
import org.miradi.xml.xmpz2.xmpz2schema.Xmpz2XmlSchemaCreator;
import org.w3c.dom.Node;

public class PointListData extends ObjectData
{
	public PointListData(String tagToUse)
	{
		super(tagToUse);
		points = new PointList();
	}
	
	@Override
	public void set(String newValue) throws Exception
	{
		set(new PointList(newValue));
	}
	
	@Override
	public String get()
	{
		return points.toString();
	}
	
	public void set(PointList newPoints)
	{
		points = newPoints;
	}
	
	public PointList getPointList()
	{
		return points;
	}
	
	public int size()
	{
		return points.size();
	}
	
	public Point get(int index)
	{
		return points.get(index);
	}
	
	public void add(Point point)
	{
		points.add(point);
	}
	
	public void addAll(List<Point> listToAdd)
	{
		points.addAll(listToAdd);
	}
	
	public void removePoint(Point point)
	{
		points.removePoint(point);
	}
	
	@Override
	public boolean equals(Object rawOther)
	{
		if(!(rawOther instanceof PointListData))
			return false;
		
		PointListData other = (PointListData)rawOther;
		return points.equals(other.points);
	}

	@Override
	public int hashCode()
	{
		return points.hashCode();
	}
	
	@Override
	public void writeAsXmpz2XmlData(Xmpz2XmlWriter writer, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		writer.writeBendPointListData(baseObjectSchema, fieldSchema, points);
	}
	
	@Override
	public void readAsXmpz2XmlData(Xmpz2XmlImporter importer, Node node, ORef destinationRefToUse, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		importer.importPointListField(node, destinationRefToUse, baseObjectSchema, fieldSchema);
	}
	
	@Override
	public String createXmpz2SchemaElementString(Xmpz2XmlSchemaCreator creator, BaseObjectSchema baseObjectSchema, AbstractFieldSchema fieldSchema) throws Exception
	{
		return creator.createPointListElement(baseObjectSchema, fieldSchema);
	}
	
	PointList points;
}
