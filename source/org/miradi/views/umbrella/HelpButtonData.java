/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.views.umbrella;

public class HelpButtonData
{
	
	public HelpButtonData(Class resourceClassToUse, String titleToUse, String htmlFileToUse)
	{
		title = titleToUse;
		htmlFile = htmlFileToUse;
		resourceClass = resourceClassToUse;
	}
	
	public HelpButtonData(String titleToUse, String htmlFileToUse)
	{
		this(null, titleToUse, htmlFileToUse);
	}
	
	public String toString()
	{
		return "Title:" + title + " File:" + htmlFile ;
	}
	
	public static final String MORE_INFO_HTML = "MoreInfo.html";
	public static final String MORE_INFO = "More Info";
	public static final String EXAMPLES_HTML = "Examples.html";
	public static final String EXAMPLES = "Examples";
	public static final String WORKSHOP_HTML = "Workshop.html";
	public static final String WORKSHOP = "Workshop";
	
	public static final String COMING_ATTACTIONS = "Coming Attactions";
	public static final String COMING_ATTRACTIONS_HTML = "help/ComingAttractions.html";
	public static final String CREDITS = "Credits";
	public static final String CREDITS_HTML = "help/Credits.html";
	public static final String ABOUT_BENETECH = "About Benetech";
	public static final String ABOUT_BENETECH_HTML = "help/AboutBenetech.html";
	public static final String ABOUT_CMP = "About the CMP";
	public static final String ABOUT_CMP_HTML = "help/AboutCMP.html";
	public static final String AGILE_SOFTWARE = "Agile Software";
	public static final String AGILE_SOFTWARE_HTML = "help/AgileSoftware.html";
	public static final String CMP_STANDARDS = "CMP Standards";
	public static final String CMP_STANDARDS_HTML = "help/CMPStandards.html";
	public static final String ADAPTIVE_MANAGEMENT = "Adaptive Management";
	public static final String ADAPTIVE_MANAGEMENT_HTML = "help/AdaptiveManagement.html";
	public static final String SUPPORT = "Support";
	public static final String SUPPORT_HTML = "help/Support.html";
	
	public static final String IMPORT_AND_EXPORT_HTML = "help/DemoExportAndImport.html";
	public static final String CONFIGURE_EXPORT = "Configure Export";
	public static final String DEMO = "Demo";
	public static final String DEMO_AND_DATABASES = "Demo and Databases";
	
	public String title;
	public String htmlFile;
	public Class resourceClass;
}
