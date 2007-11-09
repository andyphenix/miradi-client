/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.base; 

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.conservationmeasures.eam.actions.EAMAction;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelButton;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.utils.FastScrollPane;
import org.martus.swing.UiButton;
import org.martus.swing.Utilities;

public class ModelessDialogWithClose extends EAMDialog implements WindowListener
{
	public ModelessDialogWithClose(MainWindow parent, DisposablePanel panel, String headingText)
	{
		super(parent);
		setModal(false);
		setTitle(headingText);
		mainWindow = parent;
		wrappedPanel = panel;
	
		getContentPane().add(createMainPanel());
		getContentPane().add(createButtonBar(), BorderLayout.AFTER_LAST_LINE);
		pack();
		Utilities.fitInScreen(this);
		addWindowListener(this);
	}

	protected JComponent createMainPanel()
	{
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(wrappedPanel, BorderLayout.CENTER);
		FastScrollPane mainScrollPane = new FastScrollPane(mainPanel);
		return mainScrollPane;
	}
	
	public JPanel getWrappedPanel()
	{
		return wrappedPanel;
	}
	
	private Box createButtonBar()
	{
		UiButton closeButton = new PanelButton(EAM.text("Button|Close"));
		closeButton.setSelected(true);
		closeButton.addActionListener(new DialogCloseListener());
		
		getRootPane().setDefaultButton(closeButton);
		
		Box buttonBar = Box.createHorizontalBox();
		Component[] components = new Component[] {Box.createHorizontalGlue(), closeButton};
		addAdditionalButtons(buttonBar);
		Utilities.addComponentsRespectingOrientation(buttonBar, components);
		return buttonBar;
	}
	
	public void addAdditionalButtons(Box buttonBar)
	{
	}
	
	
	private final class DialogCloseListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}

	public void dispose()
	{
		if(wrappedPanel == null)
			return;
		
		wrappedPanel.dispose();
		wrappedPanel = null;
		super.dispose();
	}
	
	protected void createDirectionsButton(Box buttonBar)
	{
		UiButton  help = new PanelButton(new ActionDirections(EAM.text("Directions")));
		Component[] components = new Component[] {help};
		Utilities.addComponentsRespectingOrientation(buttonBar, components);
	}
	
	protected Class getJumpAction()
	{
		return null;
	}
	
	
	
	protected class ActionDirections extends EAMAction
	{

		public ActionDirections(String label)
		{
			super(label, "icons/directions.png");
		}
		
		public void doAction() throws CommandFailedException
		{
			if (getJumpAction()!=null)
				mainWindow.getActions().get(getJumpAction()).doAction();
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				doAction();
			}
			catch(CommandFailedException e1)
			{
				EAM.logException(e1);
			}
		}
	}

	public void windowActivated(WindowEvent arg0)
	{
	}

	public void windowClosed(WindowEvent arg0)
	{
	}

	public void windowClosing(WindowEvent arg0)
	{
		dispose();
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
	

	MainWindow mainWindow;
	DisposablePanel wrappedPanel;
}
