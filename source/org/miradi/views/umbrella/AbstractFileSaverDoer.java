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
package org.miradi.views.umbrella;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import org.miradi.dialogs.base.ProgressDialog;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.exceptions.UserCanceledException;
import org.miradi.main.EAM;
import org.miradi.utils.MiradiFileSaveChooser;
import org.miradi.utils.ImageTooLargeException;
import org.miradi.utils.MiradiBackgroundWorkerThread;
import org.miradi.utils.ProgressInterface;
import org.miradi.views.ViewDoer;

abstract public class AbstractFileSaverDoer extends ViewDoer
{
	@Override
	public boolean isAvailable()
	{
		return (getProject().isOpen());
	}
	
	@Override
	protected void doIt() throws Exception
	{
		if (!isAvailable())
			return;
		
		try 
		{
			//FIXME medium - this needs to be cleaned up.  Push down into sub classes.  
			if (!doesUserConfirm())
				return;
			
			MiradiFileSaveChooser eamFileChooser = createFileChooser();
			File chosen = eamFileChooser.displayChooser();
			if (chosen==null) 
				return;

			ProgressDialog progressDialog = new ProgressDialog(getMainWindow(), getProgressTitle());
			ExportWorker worker = new ExportWorker(progressDialog, chosen);
			progressDialog.doWorkInBackgroundWhileShowingProgress(worker);
			worker.cleanup();
		}
		catch (UserCanceledException e)
		{
			EAM.notifyDialog(EAM.text("Operation was canceled!"));
		}
		catch(ImageTooLargeException e)
		{
			String errorMessage = EAM.text("The image is too large to be exported.\n" +
					"Please use the <Zoom Out> feature to make it smaller, and try again.");
			EAM.errorDialog(errorMessage);
		}
		catch (ZipException e)
		{
			throw new CommandFailedException(e);
		}
		catch (IOException e)
		{
			EAM.logException(e);
			EAM.errorDialog(getIOExceptionErrorMessage());
			tryAgain();
		}
		catch (Exception e) 
		{
			throw new CommandFailedException(e);
		} 
	}

	private void doWorkWithProgressDialog(ProgressInterface progressInterface, File chosen) throws Exception
	{
		boolean workWasCompleted = doWork(chosen, progressInterface);
		if (workWasCompleted)
			EAM.notifyDialog(EAM.text("Export complete"));
	}

	protected String getIOExceptionErrorMessage()
	{
		return EAM.text("<html>An error occurred<br/><br/>Perhaps the destination is full or readonly.");
	}
	
	protected boolean doesUserConfirm() throws Exception
	{
		return true;
	}

	protected void tryAgain() throws Exception
	{
		safeDoIt();
	}
	
	protected void initializeSingleStepSaveProgressInterface(ProgressInterface progressInterface)
	{
		progressInterface.setStatusMessage(EAM.text("save..."), 1);
	}
	
	private class ExportWorker extends MiradiBackgroundWorkerThread
	{
		public ExportWorker(ProgressInterface progressToNotify, File destinationFileToUse)
		{
			super(progressToNotify);
			
			destinationFile = destinationFileToUse;
		}
		
		@Override
		protected void doRealWork() throws Exception
		{
			doWorkWithProgressDialog(getProgressIndicator(), destinationFile);
		}
		
		private File destinationFile;
	}
	
	abstract protected MiradiFileSaveChooser createFileChooser();
	
	abstract protected boolean doWork(File destinationFile, ProgressInterface progressInterface) throws Exception;
	
	abstract protected String getProgressTitle();
}
