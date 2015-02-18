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
package org.miradi.xml;

import java.io.File;
import java.io.IOException;

import org.martus.util.UnicodeWriter;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.objects.FosProjectData;
import org.miradi.objects.RareProjectData;
import org.miradi.objects.TncProjectData;
import org.miradi.objects.WcpaProjectData;
import org.miradi.objects.WcsProjectData;
import org.miradi.objects.WwfProjectData;
import org.miradi.project.Project;
import org.miradi.schemas.FosProjectDataSchema;
import org.miradi.schemas.RareProjectDataSchema;
import org.miradi.schemas.TncProjectDataSchema;
import org.miradi.schemas.WcpaProjectDataSchema;
import org.miradi.schemas.WcsProjectDataSchema;
import org.miradi.schemas.WwfProjectDataSchema;
import org.miradi.utils.CodeList;
import org.miradi.utils.HtmlUtilities;
import org.miradi.utils.UnicodeXmlWriter;

public abstract class XmlExporter
{
	public XmlExporter(Project projectToExport)
	{
		project = projectToExport;
	}
	
	public void export(File destination) throws Exception
	{
		UnicodeXmlWriter out = new UnicodeXmlWriter(destination);
		try
		{
			exportProject(out);
		}
		finally
		{
			out.close();
		}
	}
	
	public static File getProjectDirectory(String[] commandLineArguments) throws Exception
	{
		return new File(EAM.getHomeDirectory(), commandLineArguments[0]);
	}
	
	public static File getXmlDestination(String[] commandLineArguments) throws Exception
	{
		return new File(commandLineArguments[1]);
	}

	public static boolean incorrectArgumentCount(String[] commandLineArguments)
	{
		return commandLineArguments.length != 2;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	protected void writeStartElementWithAttribute(UnicodeWriter out, String startElementName, String attributeName, int attributeValue) throws IOException
	{
		writeStartElementWithAttribute(out, startElementName, attributeName, Integer.toString(attributeValue));
	}
	
	public void writeStartElementWithAttribute(UnicodeWriter out, String startElementName, String attributeName, String attributeValue) throws IOException
	{
		out.write("<" + startElementName + " " + attributeName + "=\"" + attributeValue + "\">");
	}
	
	public void writeStartElementWithTwoAttributes(UnicodeWriter out, String startElementName, String attributeName1, int attributeValue1, String attributeName2, int attributeValue2) throws IOException
	{
		writeStartElementWithTwoAttributes(out, startElementName, attributeName1, Integer.toString(attributeValue1), attributeName2, Integer.toString(attributeValue2));
	}
	
	public void writeStartElementWithTwoAttributes(UnicodeWriter out, String startElementName, String attributeName1, String attributeValue1, String attributeName2, String attributeValue2) throws IOException
	{
		out.write("<" + startElementName + " " + attributeName1 + "=\"" + attributeValue1 + "\" " + attributeName2 + "=\"" + attributeValue2 + "\">");
	}
	
	protected void writeStartElement(UnicodeWriter out, String startElementName) throws IOException
	{
		out.writeln("<" + startElementName + ">");
	}
	
	public void writeEndElement(UnicodeWriter out, String endElementName) throws IOException
	{
		out.writeln("</" + endElementName + ">");
	}
	
	public void writeElement(UnicodeWriter out, String elementName, int data) throws Exception
	{
		writeElement(out, elementName, Integer.toString(data));
	}
	
	public void writeElement(UnicodeWriter out, String elementName, String data) throws Exception
	{
		out.write("<" + elementName + ">");
		writeXmlEncodedData(out, data);
		out.write("</" + elementName + ">");
		out.writeln();
	}

	public void writeOptionalElement(UnicodeWriter out, String elementName, String data) throws Exception
	{
		if (data == null || data.length() == 0)
			return;
		
		writeElement(out, elementName, data);
	}
	
	public void writeXmlEncodedData(UnicodeWriter out, String data) throws IOException
	{
		data = HtmlUtilities.replaceHtmlBrsWithNewlines(data);
		data = HtmlUtilities.stripAllHtmlTags(data);
		out.write(data);
	}
	
	protected void writeOptionalElement(UnicodeWriter out, String elementName, BaseObject object, String fieldTag) throws Exception
	{
		writeOptionalElement(out, elementName, getFieldDataForXmlExport(object, fieldTag));
	}
	
	protected void writeElement(UnicodeWriter out, String elementName, BaseObject object, String fieldTag) throws Exception
	{
		writeElement(out, elementName, getFieldDataForXmlExport(object, fieldTag));
	}

	private String getFieldDataForXmlExport(BaseObject object, String fieldTag)
	{
		return object.getData(fieldTag);
	}
	
	protected void writeCodeListElements(UnicodeWriter out, String parentElementName, String elementName, CodeList codeList) throws Exception
	{
		out.writeln("<" + parentElementName + ">");
		for (int codeIndex = 0; codeIndex < codeList.size(); ++codeIndex)
		{
			writeElement(out, elementName, codeList.get(codeIndex));
		}
		out.writeln("</" + parentElementName + ">");
	}
	
	protected WcpaProjectData getWcpaProjectData()
	{
		ORef wcpaProjectDataRef = getProject().getSingletonObjectRef(WcpaProjectDataSchema.getObjectType());
		return WcpaProjectData.find(getProject(), wcpaProjectDataRef);
	}
	
	protected TncProjectData getTncProjectData()
	{
		ORef tncProjectDataRef = getProject().getSingletonObjectRef(TncProjectDataSchema.getObjectType());
		return TncProjectData.find(getProject(), tncProjectDataRef);
	}
	
	protected WwfProjectData getWwfProjectData()
	{
		ORef wwfProjectDataRef = getProject().getSingletonObjectRef(WwfProjectDataSchema.getObjectType());
		return WwfProjectData.find(getProject(), wwfProjectDataRef);
	}

	protected WcsProjectData getWcsProjectData()
	{
		ORef wwfProjectDataRef = getProject().getSingletonObjectRef(WcsProjectDataSchema.getObjectType());
		return WcsProjectData.find(getProject(), wwfProjectDataRef);
	}
	
	protected RareProjectData getRareProjectData()
	{
		ORef rareProjectDataRef = getProject().getSingletonObjectRef(RareProjectDataSchema.getObjectType());
		return RareProjectData.find(getProject(), rareProjectDataRef);
	}
	
	protected FosProjectData getFosProjectData()
	{
		ORef fosProjectDataRef = getProject().getSingletonObjectRef(FosProjectDataSchema.getObjectType());
		return FosProjectData.find(getProject(), fosProjectDataRef);
	}
	
	abstract public void exportProject(UnicodeXmlWriter out) throws Exception;
	
	private Project project;
}
