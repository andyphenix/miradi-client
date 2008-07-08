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
package org.miradi.main;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.LanguageQuestion;
import org.miradi.questions.QuestionManager;
import org.miradi.utils.Translation;


public class Miradi
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			addThirdPartyJarsToClasspath();
			setBestLookAndFeel();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error initializing Miradi");
			System.exit(1);
		}
		
		Translation.initialize();
		
		List<String> argsAsList = Arrays.asList(args);

		EAM.setLogLevel(EAM.LOG_DEBUG);
		for(String arg : argsAsList)
		{
			if(arg.equals("--verbose"))
			{
				EAM.setLogLevel(EAM.LOG_VERBOSE);
			}
		}

		EAM.setExceptionLoggingDestination();
		EAM.possiblyLogTooLowInitialMemory();

		Miradi.start(args);
	}

	public static void switchToLanguage(String languageCode) throws Exception
	{
		if(languageCode == null)
		{
			EAM.restoreDefaultLocalization();
			return;
		}
		
		String jarName = "MiradiContent-2.1-" + languageCode + ".jar";
		File jarFile = findLanguageJar(jarName);
		EAM.setLocalization(jarFile.toURI().toURL(), languageCode);
	}
	
	public static HashSet<ChoiceItem> getAvailableLanguageChoices() throws Exception
	{
		HashSet<ChoiceItem> results = new HashSet();
		results.addAll(getAvailableLanguageChoices(EAM.getHomeDirectory()));
		results.addAll(getAvailableLanguageChoices(getAppCodeDirectory()));
		return results;
	}
	
	private static Vector<ChoiceItem> getAvailableLanguageChoices(File directory) throws Exception
	{
		class LanguageJarFilter implements FilenameFilter
		{
			public boolean accept(File dir, String name)
			{
				if(name.matches("MiradiContent-2\\.1-..\\.jar"))
					return true;
				
				return false;
			}
		}
		EAM.logWarning("Looking for content jars in: " + directory.getAbsolutePath());
		
		ChoiceQuestion languages = QuestionManager.getQuestion(LanguageQuestion.class);
		Vector<ChoiceItem> results = new Vector();
		String[] jarNames = directory.list(new LanguageJarFilter());
		if(jarNames == null)
			return results;
		
		for(int i = 0; i < jarNames.length; ++i)
		{
			String[] parts = jarNames[i].split("-");
			String languageCode = parts[2].split("\\.")[0];
			String languageName = languages.getValue(languageCode);
			results.add(new ChoiceItem(languageCode, languageName));
		}
		return results;
	}

	private static File findLanguageJar(String jarName) throws URISyntaxException
	{
		File jarFile = new File(EAM.getHomeDirectory(), jarName);
		if(jarFile.exists())
			return jarFile;

		jarFile = new File(getAppCodeDirectory(), jarName);
		if(jarFile.exists())
			return jarFile;

		EAM.logError("Unable to find content file: " + jarFile.getAbsolutePath());
		System.exit(2);
		return null;
	}
	
	private static void addThirdPartyJarsToClasspath() throws Exception
	{
		String jarSubdirectoryName = "ThirdParty";
		File miradiDirectory = getAppCodeDirectory();
		EAM.logVerbose("Miradi code running from: " + miradiDirectory.getAbsolutePath());
		File thirdPartyDirectory = new File(miradiDirectory, jarSubdirectoryName);
		EAM.logVerbose("Adding jars to classpath: " + thirdPartyDirectory.getAbsolutePath());
		RuntimeJarLoader.addJarsInSubdirectoryToClasspath(thirdPartyDirectory);
	}
	
	private static File getAppCodeDirectory() throws URISyntaxException
	{
		String imagesURIString = Miradi.class.getResource("/resources/images").toURI().toString();
		String imagesPathString = stripPrefix(imagesURIString);
	
		int bangAt = imagesPathString.indexOf('!');
		if(bangAt < 0)
		{
			File imagesDirectory = new File(stripPrefix(imagesURIString));
			return imagesDirectory.getParentFile();
		}
		
		String jarURIString = imagesPathString.substring(0, bangAt);
		File jarFile = new File(stripPrefix(jarURIString));
		return jarFile.getParentFile();
	}

	private static String stripPrefix(String uri)
	{
		int startOfRealPath = uri.indexOf(':') + 1;
		return uri.substring(startOfRealPath);
	}

	public static void start(String[] args)
	{
		try
		{
			VersionConstants.setVersionString();
			SwingUtilities.invokeAndWait(new MainWindowRunner(args));
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog(e.getMessage());
		}
	}

	static void setBestLookAndFeel() throws Exception
	{
		if(isLinux())
			return;
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}

	public static boolean isLinux()
	{
		return System.getProperty("os.name").equals("Linux");
	}

	public static boolean isWindows()
	{
		return System.getProperty("os.name").startsWith("Windows");
	}
	
	private static final class MainWindowRunner implements Runnable
	{
		MainWindowRunner(String[] argsToUse)
		{
			args = argsToUse;
		}
		
		public void run()
		{
			try
			{
				EAM.setMainWindow(new MainWindow());
				EAM.getMainWindow().start(args);
			}
			catch(Exception e)
			{
				EAM.logException(e);
				EAM.errorDialog("Unexpected error: " + e.getMessage());
				System.exit(1);
			}
		}
		
		String[] args;
	}

}
