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
import org.miradi.exceptions.FutureVersionException;
import org.miradi.exceptions.UnsupportedNewVersionSchemaException;
import org.miradi.exceptions.UserCanceledException;
import org.miradi.exceptions.ValidationException;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.utils.EAMFileSaveChooser;
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
			fileToImport = EAMFileSaveChooser.getFileWithExtension(fileChooser, fileToImport);
			
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
		catch (FutureVersionException e)
		{
			EAM.logException(e);
			showImportFailedErrorDialog("This project cannot be imported by this version of Miradi because it <BR>" +
										"is in a newer data format. Please upgrade to the latest version of Miradi.");
		}
		catch(Exception e)
		{
			EAM.logException(e);
			showImportFailedErrorDialog(e.getMessage());
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
		userConfirmOpenImportedProject(projectName);
	}
	
	private class Worker extends MiradiBackgroundWorkerThread
	{
		public Worker(ProgressInterface progressInterfaceToUse, File fileToImportToUse, String projectNameToUse)
		{
			super(progressInterfaceToUse);
			
			fileToImport = fileToImportToUse;
			projectName = projectNameToUse;
		}
		
		@Override
		protected void doRealWork() throws Exception
		{
			createProject(fileToImport, EAM.getHomeDirectory(), projectName, getProgressIndicator());
			getProgressIndicator().finished();
		}
		
		private File fileToImport;
		private String projectName;
	}

	protected void userConfirmOpenImportedProject(String projectName) throws Exception
	{
		String openProjectMessage = EAM.substitute(EAM.text("Import Completed.  Would you like to open %s?"), projectName);
		boolean shouldOpenProjectAfterImport = EAM.confirmOpenDialog(EAM.text("Open Project"), openProjectMessage);
		if (shouldOpenProjectAfterImport)
		{
			getMainWindow().setLocalDataLocation(EAM.getHomeDirectory());
			getMainWindow().createOrOpenProject(projectName);
		}
	}

	private void addFileFilters(JFileChooser fileChooser)
	{
		FileFilter[] filters = getFileFilters();
		addFileFilters(fileChooser, filters);
	}

	public static void addFileFilters(JFileChooser fileChooser, FileFilter[] filters)
	{
		for (int i = 0; i < filters.length; ++i)
		{
			fileChooser.addChoosableFileFilter(filters[i]);
		}
	}

	private void showImportFailedErrorDialog(String message)
	{
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

	private MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	private static String currentDirectory = UiFileChooser.getHomeDirectoryFile().getPath();
	private static final String IMPORT_FAILED_MESSAGE = EAM.text("This file cannot be imported because it is a newer format than this version of Miradi supports. <br>" +
			  "Please make sure you are running the latest version of Miradi. If you are already <br>" +
			  "running the latest Miradi, either wait for a newer version that supports this format, <br>" +
			  "or re-export the project to an older (supported) format.");
	
	protected abstract void createProject(File importFile, File homeDirectory, String newProjectFilename, ProgressInterface progressIndicator)  throws Exception;
	
	protected abstract FileFilter[] getFileFilters();
	
	private MainWindow mainWindow;
}
