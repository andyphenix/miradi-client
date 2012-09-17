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
package org.miradi.wizard.noproject.projectlist;

import java.io.File;

import org.miradi.dialogs.treetables.GenericTreeTableModel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.FileSystemProjectSorter;
import org.miradi.wizard.noproject.FileSystemDirectoryNode;
import org.miradi.wizard.noproject.FileSystemProjectOrDirectoryNode;
import org.miradi.wizard.noproject.FileSystemTreeNode;

public class ProjectListTreeTableModel extends GenericTreeTableModel
{
	public static ProjectListTreeTableModel createDirectoryListTreeTableModel(File homeDirectory) throws Exception
	{
		FileSystemProjectSorter nodeSorter = new FileSystemProjectSorter();
		FileSystemDirectoryNode rootNode = new FileSystemDirectoryNode(homeDirectory, nodeSorter);
			
		return new ProjectListTreeTableModel(rootNode, nodeSorter);
	}
	
	public static ProjectListTreeTableModel createProjectListTreeTableModel(File dataDirectory) throws Exception
	{
		FileSystemProjectSorter nodeSorter = new FileSystemProjectSorter();
		FileSystemProjectOrDirectoryNode rootNode = new FileSystemProjectOrDirectoryNode(dataDirectory, nodeSorter);
			
		return new ProjectListTreeTableModel(rootNode, nodeSorter);
	}
	
	private ProjectListTreeTableModel(FileSystemTreeNode root, FileSystemProjectSorter nodeSorterToUse)
	{
		super(root);
		
		nodeSorter = nodeSorterToUse;

		root.recursivelySort();
	}

	public String getColumnTag(int modelColumn)
	{
		return COLUMN_NAMES[modelColumn];
	}

	public int getColumnCount()
	{
		return COLUMN_NAMES.length;
	}

	public String getColumnName(int column)
	{
		return COLUMN_NAMES[column];
	}
	
	public void sort(int modelColumn)
	{
		FileSystemTreeNode fileSystemNode = getFileSystemRootNode();
		String columnTag = getColumnTag(modelColumn);
		nodeSorter.resortBy(columnTag);
		fileSystemNode.recursivelySort();
		
		reloadNodesWithouRebuildingNodes();
	}

	private FileSystemTreeNode getFileSystemRootNode()
	{
		return (FileSystemTreeNode) getRootNode();
	}
	
	public void rebuildEntireTree(File homeDir)
	{
		getFileSystemRootNode().setFile(homeDir);
		super.rebuildEntireTree();
	}
	
	@Override
	public String getUniqueTreeTableModelIdentifier()
	{
		return UNIQUE_TREE_TABLE_IDENTIFIER;
	}
	
	private static final String UNIQUE_TREE_TABLE_IDENTIFIER = "ProjectListTreeTableModel";
	
	private String[] COLUMN_NAMES = {EAM.text("Project"), EAM.text("Last Modified"), };
	private FileSystemProjectSorter nodeSorter;
}
