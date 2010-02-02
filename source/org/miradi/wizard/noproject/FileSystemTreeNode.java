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
package org.miradi.wizard.noproject;

import java.io.File;
import java.util.Collections;
import java.util.Vector;

import org.miradi.database.ProjectServer;
import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.FileSystemProjectSorter;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;

abstract public class FileSystemTreeNode extends TreeTableNode
{
	public FileSystemTreeNode(File file, FileSystemProjectSorter sorterToUse) throws Exception
	{
		thisFile = file;
		children = new Vector<FileSystemTreeNode>();
		sorter =  sorterToUse;

		rebuild();
	}
	
	public TreeTableNode getChild(int index)
	{
		return children.get(index);
	}

	public int getChildCount()
	{
		return children.size();
	}

	public BaseObject getObject()
	{
		throw new RuntimeException("Not an object-based tree");
	}

	public ORef getObjectReference()
	{
		throw new RuntimeException("Not an object-based tree");
	}

	public Object getValueAt(int column)
	{
		try
		{
			if(column == 0)
			{
				return thisFile;
			}
			if(column == 1)
			{
				if(!isProjectDirectory())
					return null;
				
				return getLastModifiedDate();
			}
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
		
		EAM.logError("FileSystemTreeNode has no value for column: " + column);
		
		return "";
	}

	public String getLastModifiedDate() throws Exception
	{
		return ProjectServer.readLocalLastModifiedProjectTime(thisFile);
	}

	public void rebuild() throws Exception
	{
		children.clear();
		if(isProjectDirectory())
			return;
		
		File[] files = thisFile.listFiles();
		if(files == null)
			return;
		
		for(int i = 0; i < files.length; ++i)
		{
			File file = files[i];
			if(shouldBeIncluded(file))
			{
				children.add(createNode(file, sorter));
			}
		}
		
		sortChildren();
	}

	public void recursivelySort()
	{
		sortChildren();	
		for (int index = 0; index < children.size(); ++index)
		{
			FileSystemTreeNode childNode = children.get(index);
			childNode.recursivelySort();
		}
	}
	
	private void sortChildren()
	{
		Collections.sort(children, sorter);
	}
	
	protected boolean isExternalReportsDirectory(File file)
	{	
		return file.getName().equals(OLD_JASPER_EXTERNAL_REPORTS_DIR_NAME);
	}

	protected boolean isCustomReportsDirectory(File file)
	{
		return file.getName().equals(OLD_JASPER_CUSTOM_REPORTS_DIR_NAME);
	}
	
	protected boolean isExternalResourceDirectory(File file)
	{
		return file.getName().equals(EAM.EXTERNAL_RESOURCE_DIRECTORY_NAME);
	}
	
	public boolean isProjectDirectory() throws Exception
	{
		return ProjectServer.isExistingLocalProject(thisFile);
	}

	@Override
	public String toRawString()
	{
		return thisFile.getName();
	}

	public File getFile()
	{
		return thisFile;
	}
	
	public void setFile(File file)
	{
		thisFile = file;
	}
	
	abstract protected FileSystemTreeNode createNode(File file, FileSystemProjectSorter sorterToUse) throws Exception;
	
	abstract protected boolean shouldBeIncluded(File file);
	
	private static final String OLD_JASPER_EXTERNAL_REPORTS_DIR_NAME = "ExternalReports";	
	private static final String OLD_JASPER_CUSTOM_REPORTS_DIR_NAME = "CustomReports";
	
	protected File thisFile;
	private Vector<FileSystemTreeNode> children;
	private FileSystemProjectSorter sorter;
}
