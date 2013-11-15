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
package org.miradi.main;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.martus.swing.UiNotifyDlg;
import org.martus.util.xml.XmlUtilities;
import org.miradi.project.Project;
import org.miradi.utils.FileUtilities;
import org.miradi.utils.HtmlViewPanel;
import org.miradi.utils.HtmlViewPanelWithMargins;
import org.miradi.utils.MiradiLogger;
import org.miradi.utils.StringUtilities;
import org.miradi.utils.Translation;

public class EAM
{
	// NOTE: This MUST be the first thing in the class so it is initialized first!
	private static MiradiLogger logger = new MiradiLogger();

	public static boolean initializeHomeDirectory()
	{
		if (Miradi.isWindows())
			alertIfHomeIsNotOnC();
		
		File preferredHomeDir = getHomeDirectory();
		
		preferredHomeDir.mkdirs();
		if (!preferredHomeDir.exists() || !preferredHomeDir.isDirectory())
		{	
			displayHtmlWarningDialog("NoHomeDirectoryFoundMessage.html","@DIRECTORY_NAME@", preferredHomeDir.getAbsolutePath());
			return true;
		}

		if(!EAM.handleEamToMiradiMigration())
			return false;
		
		if(!EAM.handleMigrationToDocumentsDirectory())
			return false;
		
		return true;
	}

	public static String getJavaVersion()
	{
		return System.getProperty("java.version");
	}
	
	public static File getHomeDirectory()
	{
		File preferredHomeDir = getPreferredHomeDirectory();
		if (preferredHomeDir != null)
			return preferredHomeDir;
		
		File defaultHomeDirectory = getDefaultHomeDirectory();
		Preferences.userNodeForPackage(Miradi.class).put(EAM.MIRADI_DATA_DIRECTORY_KEY, defaultHomeDirectory.getAbsolutePath());
		
		return defaultHomeDirectory;
	}
	
	public static boolean isOneFileInsideTheOther(File file1, File file2)
	{
		String file1AsString = file1.getAbsolutePath();
		String file2AsString = file2.getAbsolutePath();
		
		if (file1AsString.startsWith(file2AsString))
			return true;
		
		if (file2AsString.startsWith(file1AsString))
			return true;
		
		return false;
	}

	private static void alertIfHomeIsNotOnC()
	{
		String homeDir = getHomeDirectory().getAbsolutePath();
		if (homeDir.startsWith("C:\\"))
			return;
		
		displayHtmlWarningDialog("NoWindowsDataLocalDataLocationMessage.html", "@DIRECTORY_NAME@", homeDir);
	}
	
	public static boolean isValidProjectNameCharacter(char c)
	{
		if(LEGAL_NON_ALPHA_NUMERIC_CHARACTERS.indexOf(c) >= 0)
			return true;
	
		if(c >= 128)
			return true;
		
		return Character.isLetterOrDigit(c);
	}
	
	private static void displayHtmlWarningDialog(String htmlFileName, String findToReplace,  String replacementForStr1)
	{
		try
		{
			String html = Translation.getHtmlContent(htmlFileName);
			html = html.replace(findToReplace, replacementForStr1);
			displayHtmlWarningDialog(html);
		}
		catch (Exception e)
		{
			logException(e);
		}
	}

	public static void displayHtmlWarningDialog(String messageAsHtml)
	{
		HtmlViewPanel htmlViwer = new HtmlViewPanel(getMainWindow(), MiradiStrings.getWarningLabel(), messageAsHtml, null);
		htmlViwer.showAsOkDialog();
	}

	public static void showSafeHtmlOkMessageDialog(String messageFileName, String title)
	{
		try
		{
			EAM.showHtmlMessageOkDialog(messageFileName, title);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.unexpectedErrorDialog();
		}
	}

	public static void showHtmlMessageOkDialog(String messageFileName, String translatedTitle) throws Exception
	{
		HtmlViewPanelWithMargins.createFromHtmlFileName(getMainWindow(), translatedTitle, messageFileName).showAsOkDialog();
	}
	
	public static void showHtmlInfoMessageOkDialog(String messageFileName) throws Exception
	{
		showHtmlMessageOkDialog(messageFileName, MiradiStrings.getInformationDialogTitle());
	}

	private static File getPreferredHomeDirectory()
	{
		String preferredHomeDirAsString = Preferences.userNodeForPackage(Miradi.class).get(MIRADI_DATA_DIRECTORY_KEY, "");
		if (preferredHomeDirAsString == null || preferredHomeDirAsString.length() == 0)
			return null;
		
		return new File(preferredHomeDirAsString);
	}

	private static File getDefaultHomeDirectory()
	{
		return new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "Miradi");
	}
	
	public static File getOldMiradiHomeDirectory()
	{
		File home = new File(System.getProperty("user.home"), "Miradi");
		return home;
	}
	
	public static File getOldEamHomeDirectory()
	{
		File home = new File(System.getProperty("user.home"), "eAM");
		return home;
	}
	
	public static void setExceptionLoggingDestination()
	{
		setExceptionLoggingDestination(getDefaultExceptionsLogFile());
	}

	private static void setExceptionLoggingDestination(File destination)
	{
		try
		{
			if (getExceptionLoggingDestination() != null)
				getExceptionLoggingDestination().close();
			
			FileOutputStream outputStream = new FileOutputStream(destination, true);
			setExceptionLoggingDestination(outputStream);
		}
		catch(FileNotFoundException e)
		{
			System.out.println("Unable to create exception logging file: " + e.getLocalizedMessage());
		}
	}

	static File getDefaultExceptionsLogFile()
	{
		return new File(getHomeDirectory(), EXCEPTIONS_LOG_FILE_NAME);
	}

	public static void setExceptionLoggingDestination(OutputStream outputStream)
	{
		PrintStream printStream = new PrintStream(outputStream);
		setExceptionLoggingDestination(printStream);
	}


	public static void setExceptionLoggingDestination(PrintStream printStream)
	{
		logger.setExceptionLoggingDestination(printStream);
	}
	
	public static PrintStream getExceptionLoggingDestination()
	{
		return logger.getExceptionLoggingDestination();
	}
	
	///////////////////////////////////////////////////////////////////
	// Logging
	public static void setLogToString()
	{
		logger.setLogToString();
	}
	
	public static void setLogToConsole()
	{
		logger.setLogToConsole();
	}
	
	public static String getLoggedString()
	{
		return logger.getLoggedString();
	}
	
	public static void setLogLevel(int level)
	{
		logger.setLogLevel(level);
	}
	
	public static void logException(Exception e)
	{
		if (Miradi.isAlphaTesterMode())
			Toolkit.getDefaultToolkit().beep();
		
		logger.logException(e);
		appendToProjectExceptionLog(e);
	}

	private static void appendToProjectExceptionLog(Exception exceptionToAppend)
	{
		if (getMainWindow() == null)
			return;
		
		Project project = getMainWindow().getProject();
		if (project == null)
			return;
		
		try
		{	
			project.appendToExceptionLog(convertExceptionToString(exceptionToAppend));
			mainWindow.getProjectSaver().safeSave();
		}
		catch (Exception e)
		{
			logger.logException(e);
		}
	}
	
	public static void logError(String text)
	{
		logger.logError(text);

		if (Miradi.isAlphaTesterMode())
		{
			String safeText = XmlUtilities.getXmlEncoded(text);
			if(safeText.length() > 200)
				safeText = safeText.substring(0, 200);
			errorDialog("<HTML>There is a console error: <BR>" + safeText);
		}
	}
	
	public static void logWarning(String text)
	{
		logger.logWarning(text);

		if (Miradi.isAlphaTesterMode())
		{
			if(text.length() > 200)
				text = text.substring(0, 200);
			errorDialog("<HTML>There is a console warning: <BR>" + text);
		}
	}
	
	public static void logDebug(String text)
	{
		logger.logDebug(text);
	}
	
	public static void logStackTrace()
	{
		final String NO_MESSAGE = "";
		logStackTrace(NO_MESSAGE);
	}
	
	public static void logStackTrace(String errorMessage)
	{
		try
		{
			throw new Exception(errorMessage);
		}
		catch(Exception e)
		{
			logger.logException(e);
		}
	}

	public static void logVerbose(String text)
	{
		logger.logVerbose(text);
	}

	public static String convertExceptionToString(Exception exceptionToConvert)
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
		exceptionToConvert.printStackTrace(printWriter);
		printWriter.close();

		return byteArrayOutputStream.toString();
	}
	
	public static final int LOG_QUIET = MiradiLogger.LOG_QUIET;
	public static final int LOG_NORMAL = MiradiLogger.LOG_NORMAL;
	public static final int LOG_DEBUG = MiradiLogger.LOG_DEBUG;
	public static final int LOG_VERBOSE = MiradiLogger.LOG_VERBOSE;
	

	///////////////////////////////////////////////////////////////////
	// Translations
	public static String text(String key)
	{
		return Translation.text(key);
	}
	
	public static String emptyText()
	{
		return "";
	}
	
	public static String substitute(String text, HashMap<String, String> tokenReplacementMap)
	{
		Set<String> tokens = tokenReplacementMap.keySet();
		for(String token : tokens)
		{
			String replacement = tokenReplacementMap.get(token);
			text = substitute(text, token, replacement);
		}
		
		return text;
	}
	
	public static String substituteSingleInteger(String text, int replacement)
	{
		return substituteSingleString(text, Integer.toString(replacement));
	}
	
	public static String substituteSingleString(String text, String replacement)
	{
		return substitute(text, STRING_TO_SUBSTITUTE, replacement);
	}
	
	public static String substitute(String text, String token, String replacement)
	{
		if (text == null)
			EAM.logError("Substitute called with null value");
		
		if (replacement == null)
			replacement = "";
		
		return text.replace(token, replacement);
	}

	public static String fieldLabel(int objectType, String fieldTag)
	{
		return Translation.fieldLabel(objectType, fieldTag);
	}
	
	///////////////////////////////////////////////////////////////////
	// Dialogs

	public static void panic(Exception e)
	{
		logException(e);
		
		errorDialog(MiradiStrings.getErrorMessage(e));
		
		exitMiradiNowDueToFatalError();
	}

	public static void handleWriteFailure(File file, Exception e)
	{
		logError("write failure happend for: " + file.getAbsolutePath());
		logException(e);
		showSafeHtmlOkMessageDialog(FILEINUSE_ERROR_MESSAGE_FILE_NAME, Translation.getCellTextWhenException());
		exitMiradiNowDueToFatalError();
	}
	
	public static void exitMiradiNowDueToFatalError()
	{
		System.exit(0);
	}

	public static void unexpectedErrorDialog()
	{
		unexpectedErrorDialog("");
	}
	
	public static void alertUserOfNonFatalException(Exception e)
	{
		logException(e);
		String extraText = "";
		if(e.getMessage() != null)
			extraText += (e.getMessage());

		unexpectedErrorDialog(extraText);
	}
	
	private static void unexpectedErrorDialog(String extraText)
	{
		String errorMessage = MiradiStrings.getUnexpectedErrorMessage();
		if(extraText.length() > 0)
			errorMessage += ":" + StringUtilities.NEW_LINE + extraText;

		EAM.errorDialog(errorMessage);
	}

	public static void errorDialog(String errorMessage)
	{
		JOptionPane.showMessageDialog(getMainWindow(), errorMessage, MiradiStrings.getErrorMessage(), JOptionPane.ERROR_MESSAGE);
	}

	public static void notifyDialog(String text)
	{
		JOptionPane.showMessageDialog(getMainWindow(), text, MiradiStrings.getInformationDialogTitle(), JOptionPane.INFORMATION_MESSAGE);
	}

	public static int confirmDialog(String title, String text, String[] buttonLabels)
	{
		return JOptionPane.showOptionDialog(getMainWindow(), text, title, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttonLabels, null);
	}

	
	public static void okDialog(String title, String[] body)
	{
		new UiNotifyDlg(getMainWindow(), title, body, new String[] {MiradiStrings.getOkButtonText()});
	}
	
	public static boolean confirmOpenDialog(String title, String body)
	{
		String[] buttons = { MiradiStrings.getOpenLabel(), MiradiStrings.getCancelButtonText() };
		return confirmDialog(title, new String[]{body, }, buttons);
	}

	public static boolean confirmOverwriteDialog(String title, String body)
	{
		String[] buttons = { MiradiStrings.getOverwriteLabel(), MiradiStrings.getCancelButtonText() };
		return confirmDialog(title, new String[]{body, }, buttons);
	}

	public static boolean confirmDialog(String title, String[] body, String[] buttons)
	{
		UiNotifyDlg dlg = new UiNotifyDlg(getMainWindow(), title, body, buttons);
		if (wasWindowClosed(dlg))
			return false;
		
		return (dlg.getResult().equals(buttons[0]));
	}
	
	public static boolean confirmDeletRetainDialog(String[] body)
	{
		String[] buttons = {MiradiStrings.getDeleteLabel(), MiradiStrings.getRetainLabel(), };
		return EAM.confirmDialog(MiradiStrings.getDeleteLabel(), body, buttons);
	}

	public static String choiceDialog(String title, String[] body, String[] buttons)
	{
		UiNotifyDlg dlg = new UiNotifyDlg(getMainWindow(), title, body, buttons);
		if (wasWindowClosed(dlg))
			return "";
		
		return (dlg.getResult());		
	}

	private static boolean wasWindowClosed(UiNotifyDlg dlg)
	{
		return dlg.getResult() == null;
	}



	public static String convertToPath(String path)
	{
		return path.replace('.', File.separatorChar);
	}
	
	public static void setMainWindow(MainWindow mainWindow)
	{
		EAM.mainWindow = mainWindow;
	}


	public static MainWindow getMainWindow()
	{
		return mainWindow;
	}

	static boolean handleMigrationToDocumentsDirectory()
	{
		File oldDirectory = getOldMiradiHomeDirectory();
		File newDirectory = getHomeDirectory();
		if(oldDirectory.equals(newDirectory))
			return true;
		
		if(!oldDirectory.exists())
			return true;
		
		if(!oldDirectory.isDirectory())
			return true;
		
		if(newDirectory.exists())
		{
			if(newDirectory.isDirectory())
				return true;
			
			EAM.errorDialog("<html>" +
					"Miradi cannot run because there a file exists where " +
					"its data folder should be:" +
					"<br>" + newDirectory.getAbsolutePath());
			return false;
		}
		
		EAM.logWarning("Migrating " + oldDirectory.getAbsolutePath() + " to " + newDirectory.getAbsolutePath());
		try
		{
			boolean worked = oldDirectory.renameTo(newDirectory);
			if(worked)
				worked = !(oldDirectory.exists());
			if(worked)
				worked = (newDirectory.exists());
			
			if(worked)
				return true;
			
			EAM.errorDialog("<html>" +
					"Miradi was unable to move existing projects from " +
					"<br>" + oldDirectory.getAbsolutePath() + 
					"<br> to " +
					"<br>" + newDirectory.getAbsolutePath() + 
					"<br>Please contact Miradi support for assistance in resolving this problem.");
			return false;
		}
		catch(Exception e)
		{
			EAM.panic(e);
			return false;
		}
		
	}
	
	static boolean handleEamToMiradiMigration()
	{
		File miradiDirectory = getHomeDirectory();
		if(miradiDirectory.exists())
			return true;
		
		File oldEamDirectory = getOldEamHomeDirectory();
		if(!oldEamDirectory.exists())
			return true;
		
		String[] miradiMigrationText = {
			"Miradi has detected some e-Adaptive Management ",
			"projects and settings on this computer, which can ",
			"automatically be imported into Miradi.",
			"",
			"If you want to run Miradi without performing this migration, ",
			"delete the e-Adaptive Management project folder ",
			"(" + oldEamDirectory + "), or rename it to something else",
			"",
			"Do you want to Import the old data, or Exit Miradi?",
			"",
		};
		if(!confirmDialog("e-Adaptive Management Data Import", miradiMigrationText, new String[] {"Import", "Exit"}))
			return false;
		
		try
		{
			FileUtilities.renameExistingWithRetries(oldEamDirectory, miradiDirectory);
		}
		catch (Exception e)
		{
			panic(e);
		}
		
		if(oldEamDirectory.exists() || !miradiDirectory.exists())
		{
			errorDialog("Import failed. Be sure no projects are open, and that you " +
					"have permission to create " + miradiDirectory.getAbsolutePath());
			return false;
		}
		
		String[] importCompleteText = {
			"Import complete.",
			"",
			"We strongly recommend that you uninstall e-Adaptive Management, ",
			"if you have not already done so. It is now obsolete, having been ",
			"replaced by Miradi.",
		};
		okDialog("Import Complete", importCompleteText);
		return true;
	}
	
	public static void possiblyLogTooLowInitialMemory()
	{
		long maxMemory = Runtime.getRuntime().maxMemory();
		if (maxMemory < 100000000)
			logWarning(EAM.text("It appears that Miradi was launched without the -Xmx512m switch. As a result, certain operations like Reports may run out of memory."));    
	}
	
	public final static String EXTERNAL_RESOURCE_DIRECTORY_NAME = "ExternalResourceDirectory";
	
	public static int STANDARD_SCROLL_INCREMENT = 12;

	public static String NEWLINE = System.getProperty("line.separator");
	private static MainWindow mainWindow;
	public static String PROJECT_EXTENSION = ".eam";
	public static final Color READONLY_BACKGROUND_COLOR = new Color(217, 217, 217);
	public static final Color READONLY_FOREGROUND_COLOR = Color.black;
	public static final Color EDITABLE_BACKGROUND_COLOR = Color.WHITE;
	public static final Color EDITABLE_FOREGROUND_COLOR = Color.BLUE;
	
	public static final String MIRADI_DATA_DIRECTORY_KEY = "MiradiDataDirectory";
	public static final String STRING_TO_SUBSTITUTE = "%s";
	public static final char DASH = '-';
	public static final String LEGAL_NON_ALPHA_NUMERIC_CHARACTERS = "_. " + DASH;
	
	public static final String EXCEPTIONS_LOG_FILE_NAME = "exceptions.log";
	private final static String FILEINUSE_ERROR_MESSAGE_FILE_NAME = "FileInUseErrorMessage.html";
}



