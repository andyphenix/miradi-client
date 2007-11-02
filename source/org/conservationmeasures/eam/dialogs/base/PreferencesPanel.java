/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;

import org.conservationmeasures.eam.diagram.DiagramConstants;
import org.conservationmeasures.eam.dialogs.diagram.DiagramProjectPreferencesPanel;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelCheckBox;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelComboBox;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelTabbedPane;
import org.conservationmeasures.eam.dialogs.fieldComponents.PanelTitleLabel;
import org.conservationmeasures.eam.main.AppPreferences;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.questions.ChoiceItem;
import org.conservationmeasures.eam.questions.ChoiceQuestion;
import org.conservationmeasures.eam.questions.FontFamiliyQuestion;
import org.conservationmeasures.eam.questions.FontSizeQuestion;
import org.conservationmeasures.eam.utils.HyperlinkLabel;
import org.martus.swing.UiCheckBox;
import org.martus.swing.UiComboBox;
import org.martus.swing.UiLabel;

import com.jhlabs.awt.BasicGridLayout;

public class PreferencesPanel extends DataInputPanel implements ActionListener
{
	public PreferencesPanel(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse.getProject());
		mainWindow = mainWindowToUse;
		project = mainWindow.getProject();
		add(createTabs(), BorderLayout.CENTER);
	}

	public void dispose()
	{
		diagramProjectPreferencesPanel.dispose();
		diagramProjectPreferencesPanel = null;
		super.dispose();
	}
	
	JTabbedPane createTabs()
	{
		JTabbedPane tabPane = new PanelTabbedPane();
		tabPane.addTab("Systemwide", createSystemwideTab());
		tabPane.addTab("Diagram View", createDiagramTab());
		tabPane.addTab("Threat Rating View", createThreatRatingTab());
		return tabPane;
	}
	
	private JPanel createSystemwideTab()
	{
		JPanel htmlTab = new JPanel(new BasicGridLayout(0,2));

		int panelFontSize = mainWindow.getDataPanelFontSize();
		String panelSizeAsString = Integer.toString(panelFontSize);
		panelFontSizeCombo = createAndAddLabelAndCombo(htmlTab,new FontSizeQuestion(""), panelSizeAsString);
			
		String panelFontFamily = mainWindow.getDataPanelFontFamily();
		panelFontFamilyCombo = createAndAddLabelAndCombo(htmlTab,new FontFamiliyQuestion(""), panelFontFamily);
		
		return htmlTab;
	}

	private UiComboBox createAndAddLabelAndCombo(JPanel htmlTab, ChoiceQuestion question, String sizeAsString)
	{
		UiComboBox combo = new PanelComboBox(question.getChoices());
		setSelectedItemQuestionBox(combo, sizeAsString);
		combo.addActionListener(this);
		htmlTab.add(new PanelTitleLabel(question.getTag() + question.getLabel()));
		htmlTab.add(combo);
		return combo;
	}

	public void setSelectedItemQuestionBox(UiComboBox combo, String code)
	{
		for(int i = 0; i < combo.getItemCount(); ++i)
		{
			ChoiceItem choice = (ChoiceItem)combo.getItemAt(i);
			if(choice.getCode().equals(code))
			{
				combo.setSelectedIndex(i);
				return;
			}
		}
		combo.setSelectedIndex(-1);
	}
	

	private JPanel createThreatRatingTab()
	{
		JPanel threatTab = new JPanel(new BasicGridLayout(0,2));
		threatTab.add(new PanelTitleLabel(EAM.text("Show Ratings in Cell")));
		cellRatingsVisibleCheckBox = new PanelCheckBox();
		cellRatingsVisibleCheckBox.setSelected(mainWindow.getBooleanPreference(AppPreferences.TAG_CELL_RATINGS_VISIBLE));
		cellRatingsVisibleCheckBox.addActionListener(this);
		threatTab.add(cellRatingsVisibleCheckBox);
		return threatTab;
	}

	private JPanel createDiagramTab()
	{
		JPanel diagramTab = new JPanel(new BasicGridLayout(1,1));
		JPanel diagramSystemPreferencesTab = new JPanel(new BasicGridLayout(0,2));
		
		diagramTab.add(new UiLabel(" "));
		diagramTab.add(new PanelTitleLabel(EAM.text("Choose the colors that look best on your system:")));

		diagramSystemPreferencesTab.add(new PanelTitleLabel(EAM.text("Strategy (Yellow)")));
		interventionDropdown = createColorsDropdown(DiagramConstants.strategyColorChoices);
		interventionDropdown.setSelectedItem(mainWindow.getColorPreference(AppPreferences.TAG_COLOR_STRATEGY));
		interventionDropdown.addActionListener(this);
		diagramSystemPreferencesTab.add(interventionDropdown);

		diagramSystemPreferencesTab.add(new PanelTitleLabel(EAM.text("Direct Threat (Pink)")));
		directThreatDropdown = createColorsDropdown(DiagramConstants.directThreatColorChoices);
		directThreatDropdown.setSelectedItem(mainWindow.getColorPreference(AppPreferences.TAG_COLOR_DIRECT_THREAT));
		directThreatDropdown.addActionListener(this);
		diagramSystemPreferencesTab.add(directThreatDropdown);

		diagramSystemPreferencesTab.add(new PanelTitleLabel(EAM.text("Contributing Factor (Orange)")));
		indirectFactorDropdown = createColorsDropdown(DiagramConstants.contributingFactorColorChoices);
		indirectFactorDropdown.setSelectedItem(mainWindow.getColorPreference(AppPreferences.TAG_COLOR_CONTRIBUTING_FACTOR));
		indirectFactorDropdown.addActionListener(this);
		diagramSystemPreferencesTab.add(indirectFactorDropdown);

		diagramSystemPreferencesTab.add(new PanelTitleLabel(EAM.text("Target (Light Green)")));
		targetDropdown = createColorsDropdown(DiagramConstants.targetColorChoices);
		targetDropdown.setSelectedItem(mainWindow.getColorPreference(AppPreferences.TAG_COLOR_TARGET));
		targetDropdown.addActionListener(this);
		diagramSystemPreferencesTab.add(targetDropdown);

		diagramSystemPreferencesTab.add(new PanelTitleLabel(EAM.text("Project Scope (Dark Green)")));
		scopeDropdown = createColorsDropdown(DiagramConstants.scopeColorChoices);
		scopeDropdown.setSelectedItem(mainWindow.getColorPreference(AppPreferences.TAG_COLOR_SCOPE));
		scopeDropdown.addActionListener(this);
		diagramSystemPreferencesTab.add(scopeDropdown);

		diagramSystemPreferencesTab.add(new PanelTitleLabel(EAM.text("Intermediate Result (Light Blue)")));
		intermediateResultDropDown = createColorsDropdown(DiagramConstants.intermediateResultChoices);
		intermediateResultDropDown.setSelectedItem(mainWindow.getColorPreference(AppPreferences.TAG_COLOR_INTERMEDIATE_RESULT));
		intermediateResultDropDown.addActionListener(this);
		diagramSystemPreferencesTab.add(intermediateResultDropDown);

		diagramSystemPreferencesTab.add(new PanelTitleLabel(EAM.text("Threat Reduction Result (Light Purple)")));
		threatReductionResultDropDown = createColorsDropdown(DiagramConstants.threatReductionResultChoices);
		threatReductionResultDropDown.setSelectedItem(mainWindow.getColorPreference(AppPreferences.TAG_COLOR_THREAT_REDUCTION_RESULT));
		threatReductionResultDropDown.addActionListener(this);
		diagramSystemPreferencesTab.add(threatReductionResultDropDown);

		diagramSystemPreferencesTab.add(new PanelTitleLabel(EAM.text("Show Diagram Grid")));
		gridVisibleCheckBox = new PanelCheckBox();
		gridVisibleCheckBox.setSelected(mainWindow.getBooleanPreference(AppPreferences.TAG_GRID_VISIBLE));
		gridVisibleCheckBox.addActionListener(this);
		diagramSystemPreferencesTab.add(gridVisibleCheckBox);
		
		diagramTab.add(new UiLabel(" "));
		diagramTab.add(new UiLabel(" "));
		
		JPanel bottomText = new JPanel();
		bottomText.add(new HyperlinkLabel( 
				"<p>Why are my choices limited to one color family for each type of factor?</p>",
				"We are trying to create a standard set of symbols that can be recognized " +
				"globally. Just like people the world over recognize a red octagon as a " +
				"stop sign, we hope that they will recognize a green oval as a target or " +
				"a yellow hexagon as a strategy"), BorderLayout.AFTER_LAST_LINE);
		bottomText.setBorder(BorderFactory.createEmptyBorder(25, 5, 25, 5));
		
		diagramTab.add(diagramSystemPreferencesTab);
		diagramTab.add(bottomText);

		diagramProjectPreferencesPanel = new DiagramProjectPreferencesPanel(mainWindow, project, project.getProjectInfo());
		diagramTab.add(diagramProjectPreferencesPanel);
		
		return diagramTab;
	}

	private UiComboBox createColorsDropdown(Color[] colorChoices)
	{
		UiComboBox dropdown = new PanelComboBox(colorChoices);
		dropdown.setRenderer(new ColorItemRenderer());
		return dropdown;
	}

	void update()
	{
		Color interventionColor = (Color)interventionDropdown.getSelectedItem();
		mainWindow.setColorPreference(AppPreferences.TAG_COLOR_STRATEGY, interventionColor);

		Color indirectFactorColor = (Color)indirectFactorDropdown.getSelectedItem();
		mainWindow.setColorPreference(AppPreferences.TAG_COLOR_CONTRIBUTING_FACTOR, indirectFactorColor);

		Color directThreatColor = (Color)directThreatDropdown.getSelectedItem();
		mainWindow.setColorPreference(AppPreferences.TAG_COLOR_DIRECT_THREAT, directThreatColor);

		Color targetColor = (Color)targetDropdown.getSelectedItem();
		mainWindow.setColorPreference(AppPreferences.TAG_COLOR_TARGET, targetColor);

		Color scopeColor = (Color)scopeDropdown.getSelectedItem();
		mainWindow.setColorPreference(AppPreferences.TAG_COLOR_SCOPE, scopeColor);

		Color intermediateResultColor = (Color) intermediateResultDropDown.getSelectedItem();
		mainWindow.setColorPreference(AppPreferences.TAG_COLOR_INTERMEDIATE_RESULT, intermediateResultColor);
		
		Color threatReductionResultColor = (Color) threatReductionResultDropDown.getSelectedItem();
		mainWindow.setColorPreference(AppPreferences.TAG_COLOR_THREAT_REDUCTION_RESULT, threatReductionResultColor);
		
		mainWindow.setBooleanPreference(AppPreferences.TAG_GRID_VISIBLE, gridVisibleCheckBox.isSelected());
		
		mainWindow.setBooleanPreference(AppPreferences.TAG_CELL_RATINGS_VISIBLE, cellRatingsVisibleCheckBox.isSelected());

		String panelFontSizeValue = getSelectedItemQuestionBox(panelFontSizeCombo);
		mainWindow.setDataPanelFontSize(Integer.parseInt(panelFontSizeValue));
		
		String panelFontFamilyValue = getSelectedItemQuestionBox(panelFontFamilyCombo);
		mainWindow.setDataPanelFontFamily(panelFontFamilyValue);

		try
		{
			mainWindow.savePreferences();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog(EAM.text("Unable to save preferences"));
		}
	}
	
	public String getSelectedItemQuestionBox(UiComboBox combo)
	{
		ChoiceItem selected = (ChoiceItem)combo.getSelectedItem();
		if(selected == null)
			return "";
		return selected.getCode();
	}
	

	static class ColorItemRenderer extends Component implements ListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			color = (Color)value;
			selected = isSelected;
			return this;
		}

		public void paint(Graphics g)
		{
			super.paint(g);
			g.setColor(color);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth(), getHeight());			
			if(selected)
				g.drawRect(1, 1, getWidth()-2, getHeight()-2);
		}

		public Dimension getSize()
		{
			return new Dimension(48, 16);
		}

		public Dimension getPreferredSize()
		{
			return getSize();
		}

		public Dimension getMinimumSize()
		{
			return getSize();
		}

		public Dimension getMaximumSize()
		{
			return getSize();
		}

		boolean selected;
		Color color;
	}

	public String getPanelDescription()
	{
		return EAM.text("Preferences");
	}

	public void actionPerformed(ActionEvent e)
	{
		update();
	}

	
	Project project;
	MainWindow mainWindow;
	DiagramProjectPreferencesPanel diagramProjectPreferencesPanel;
	
	UiComboBox interventionDropdown;
	UiComboBox directThreatDropdown;
	UiComboBox indirectFactorDropdown;
	UiComboBox targetDropdown;
	UiComboBox scopeDropdown;
	UiComboBox intermediateResultDropDown;
	UiComboBox threatReductionResultDropDown;
	UiCheckBox gridVisibleCheckBox; 
	UiCheckBox cellRatingsVisibleCheckBox;
	
	UiComboBox panelFontSizeCombo;
	UiComboBox panelFontFamilyCombo;
}
