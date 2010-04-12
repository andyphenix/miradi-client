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

package org.miradi.dialogs.base;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.miradi.dialogfields.FieldSaver;
import org.miradi.main.AppPreferences;


public class MiradiDialog extends JDialog
{
	public MiradiDialog(JFrame parent)
	{
		super(parent);
		
		intializeDialog();
	}

	public MiradiDialog(JDialog owner)
	{
		super(owner);
		
		intializeDialog();
	}
	
	private void intializeDialog()
	{
		getContentPane().setBackground(AppPreferences.getDarkPanelBackgroundColor());
		addWindowListener(new WindowEventHandler(this));
	}

	class WindowEventHandler implements WindowListener
	{
		public WindowEventHandler(Window windowToMonitor)
		{
			window = windowToMonitor;
		}
		
		public void windowActivated(WindowEvent arg0)
		{
		}

		public void windowClosed(WindowEvent arg0)
		{
			FieldSaver.savePendingEdits();
		}

		public void windowClosing(WindowEvent arg0)
		{
			window.dispose();
		}

		public void windowDeactivated(WindowEvent arg0)
		{
		}

		public void windowDeiconified(WindowEvent arg0)
		{
		}

		public void windowIconified(WindowEvent arg0)
		{
		}

		public void windowOpened(WindowEvent arg0)
		{
		}
		
		private Window window;
	}
}
