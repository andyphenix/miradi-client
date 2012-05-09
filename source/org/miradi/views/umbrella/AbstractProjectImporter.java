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
package org.miradi.views.umbrella;

import java.io.File;
import java.util.zip.ZipFile;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.martus.swing.UiFileChooser;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;
import org.martus.util.inputstreamwithseek.ZipEntryInputStreamWithSeek;
import org.miradi.dialogs.base.ProgressDialog;
import org.miradi.exceptions.CpmzVersionTooOldException;
import org.miradi.exceptions.FutureSchemaVersionException;
import org.miradi.exceptions.UnsupportedNewVersionSchemaException;
import org.miradi.exceptions.UserCanceledException;
import org.miradi.exceptions.ValidationException;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.project.Project;
import org.miradi.utils.MiradiFileSaveChooser;
import org.miradi.utils.MiradiBackgroundWorkerThread;
import org.miradi.utils.ProgressInterface;
import org.miradi.views.noproject.NoProjectView;
import org.miradi.views.noproject.RenameProjectDoer;

public abstract class AbstractProjectImporter
{	
	public AbstractProjectImporter(MainWindow mainWindowToUse)
	{
		mainWindow = mainWindowToUse;
	}
	
	public void importProject() throws Exception 
	{
		try
		{
			JFileChooser fileChooser = new JFileChooser(currentDirectory);
			fileChooser.setDialogTitle(EAM.text("Import Project"));
			addFileFilters(fileChooser);
			
			fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
			fileChooser.setApproveButtonToolTipText(EAM.text(getApproveButtonToolTipText()));
			if (fileChooser.showDialog(getMainWindow(), getDialogApprovelButtonText()) != JFileChooser.APPROVE_OPTION)
				return;
			
			File fileToImport = fileChooser.getSelectedFile();
			fileToImport = MiradiFileSaveChooser.getFileWithExtension(fileChooser, fileToImport);
			
			importProject(fileToImport);
		}
		catch (UserCanceledException e)
		{
			EAM.notifyDialog(EAM.text("Import was canceled!"));
		}
		catch (UnsupportedNewVersionSchemaException e)
		{
			EAM.logException(e);
			showImportFailedErrorDialog(IMPORT_FAILED_MESSAGE);
		}
		catch (CpmzVersionTooOldException e)
		{
			EAM.logException(e);
			EAM.errorDialog(EAM.text("This project cannot be imported by this version of Miradi because it \n" +
									 "is in a newer data format. Please upgrade to the latest version of Miradi."));
		}
		catch (ValidationException e)
		{
			EAM.logException(e);
			showImportFailedErrorDialog(e.getMessage());
		}
		catch (FutureSchemaVersionException e)
		{
			EAM.logException(e);
			showImportFailedErrorDialog("This project cannot be imported by this version of Miradi because it <BR>" +
										"is in a newer data format. Please upgrade to the latest version of Miradi.");
		}
		catch(Exception e)
		{
			EAM.logException(e);
			String message = e.getMessage();
			if(message == null)
				message = "";
			showImportFailedErrorDialog(message);
		}
	}

	public void importProject(File fileToImport) throws Exception
	{
		String projectName = RenameProjectDoer.getValidatedUserProjectName(getMainWindow(), fileToImport);
		if (projectName == null)
			return;
		
		ProgressDialog progressDialog = new ProgressDialog(getMainWindow(), EAM.text("Importing..."));
		Worker worker = new Worker(progressDialog, fileToImport, projectName);
		progressDialog.doWorkInBackgroundWhileShowingProgress(worker);
		
		refreshNoProjectPanel();
		currentDirectory = fileToImport.getParent();
		userConfirmOpenImportedProject(worker.getImportedFile());
	}
	
	private class Worker extends MiradiBackgroundWorkerThread
	{
		public Worker(ProgressInterface progressInterfaceToUse, File fileToImportToUse, String projectNameToUse) throws Exception
		{
			super(progressInterfaceToUse);
			
			fileToImport = fileToImportToUse;
			projectName = projectNameToUse;
			newProjectFile = new File(EAM.getHomeDirectory(), projectName);

			if(!Project.isValidProjectFilename(projectName))
				throw new Exception("Illegal project name: " + projectName);
		}
		
		public File getImportedFile()
		{
			return newProjectFile;
		}

		@Override
		protected void doRealWork() throws Exception
		{
			createProject(fileToImport, EAM.getHomeDirectory(), newProjectFile, getProgressIndicator());
			getProgressIndicator().finished();
		}
		
		private File fileToImport;
		private String projectName;
		private File newProjectFile;
	}

	protected void userConfirmOpenImportedProject(File projectFile) throws Exception
	{
		String openProjectMessage = EAM.substitute(EAM.text("Import Completed.  Would you like to open %s?"), projectFile.getName());
		boolean shouldOpenProjectAfterImport = EAM.confirmOpenDialog(EAM.text("Open Project"), openProjectMessage);
		if (shouldOpenProjectAfterImport)
		{
			getMainWindow().createOrOpenProject(projectFile);
		}
	}

	private void addFileFilters(JFileChooser fileChooser)
	{
		FileFilter[] filters = getFileFilters();
		for (int i = 0; i < filters.length; ++i)
		{
			fileChooser.addChoosableFileFilter(filters[i]);
		}
	}

	private void showImportFailedErrorDialog(String message)
	{
		if(message == null)
			message = EAM.text("Unexpected error");
		String safeMessage = EAM.substitute(EAM.text("<html>Import failed: <br><p> %s </p></html>"), message);
		EAM.errorDialog(safeMessage);
	}

	private String getApproveButtonToolTipText()
	{
		return "Import";
	}
	
	private String getDialogApprovelButtonText()
	{
		return "Import";
	}
	
	private void refreshNoProjectPanel() throws Exception
	{
		NoProjectView noProjectView = (NoProjectView) getMainWindow().getView(NoProjectView.getViewName());
		noProjectView.refreshText();
	}
	
	protected InputStreamWithSeek getProjectAsInputStream(ZipFile zipFile) throws Exception
	{
		return new ZipEntryInputStreamWithSeek(zipFile, zipFile.getEntry(ExportCpmzDoer.PROJECT_XML_FILE_NAME));
	}

	protected MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	private static String currentDirectory = UiFileChooser.getHomeDirectoryFile().getPath();
	private static final String IMPORT_FAILED_MESSAGE = EAM.text("This file cannot be imported because it is a newer format than this version of Miradi supports. <br>" +
			  "Please make sure you are running the latest version of Miradi. If you are already <br>" +
			  "running the latest Miradi, either wait for a newer version that supports this format, <br>" +
			  "or re-export the project to an older (supported) format.");
	
	protected abstract void createProject(File importFile, File homeDirectory, File newProjectFile, ProgressInterface progressIndicator)  throws Exception;
	
	protected abstract FileFilter[] getFileFilters();
	
	private MainWindow mainWindow;
}
