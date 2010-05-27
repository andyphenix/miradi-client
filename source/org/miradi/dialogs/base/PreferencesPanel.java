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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;

import org.martus.swing.UiCheckBox;
import org.martus.swing.UiComboBox;
import org.martus.swing.UiLabel;
import org.miradi.diagram.DiagramConstants;
import org.miradi.dialogs.diagram.DiagramProjectPreferencesPanel;
import org.miradi.dialogs.fieldComponents.PanelCheckBox;
import org.miradi.dialogs.fieldComponents.PanelComboBox;
import org.miradi.dialogs.fieldComponents.PanelTabbedPane;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.dialogs.threatrating.ThreatRatingPreferencesPanel;
import org.miradi.layout.TwoColumnPanel;
import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.FontFamiliyQuestion;
import org.miradi.questions.FontSizeQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.TableRowHeightModeQuestion;
import org.miradi.utils.HyperlinkLabel;
import org.miradi.views.ProjectSettingsPanel;
import org.miradi.views.summary.SummaryPlanningPanel;

import com.jhlabs.awt.BasicGridLayout;

public class PreferencesPanel extends DataInputPanel implements ActionListener
{
	public PreferencesPanel(MainWindow mainWindowToUse) throws Exception
	{
		super(mainWindowToUse.getProject());
		mainWindow = mainWindowToUse;
		project = getMainWindow().getProject();
		add(createTabs(), BorderLayout.CENTER);
		
		setBackground(AppPreferences.getDarkPanelBackgroundColor());
		setBorder(BorderFactory.createEmptyBorder(0,3,3,3));
		
	}

	@Override
	public void dispose()
	{
		if(diagramProjectPreferencesPanel != null)
			diagramProjectPreferencesPanel.dispose();
		diagramProjectPreferencesPanel = null;
		
		if(summaryPlanningPanel != null)
			summaryPlanningPanel.dispose();
		summaryPlanningPanel = null;
		
		if (projectSettingsPanel != null)
			projectSettingsPanel.dispose();
		projectSettingsPanel = null;
		
		if(threatRatingPreferencesPanel != null)
			threatRatingPreferencesPanel.dispose();
		threatRatingPreferencesPanel = null;

		super.dispose();
	}
	
	JTabbedPane createTabs() throws Exception
	{
		JTabbedPane tabPane = new PanelTabbedPane();
		
		if(project.isOpen())
		{
			tabPane.addTab(EAM.text("Systemwide"), createSystemwideTab());
			tabPane.addTab(EAM.text("Diagram"), createDiagramTab());

			summaryPlanningPanel = new SummaryPlanningPanel(getMainWindow(), project.getMetadata().getRef());
			tabPane.addTab(EAM.text("Threat Ratings"), createThreatRatingTab());
			tabPane.addTab(EAM.text("Planning"), summaryPlanningPanel);
			
			projectSettingsPanel = new ProjectSettingsPanel(project);
			tabPane.addTab(EAM.text("Project Settings"), projectSettingsPanel);
		}
		
		tabPane.addTab(EAM.text("Data Location"), createDataLocationTab());
		
		return tabPane;
	}
	
	private JPanel createDataLocationTab()
	{
		return new DataLocationChooserPanel(getMainWindow());
	}
	
	private JPanel createSystemwideTab()
	{
		JPanel htmlTab = new JPanel(new BasicGridLayout(0,2));
		htmlTab.setBackground(AppPreferences.getDataPanelBackgroundColor());

		int panelFontSize = getMainWindow().getDataPanelFontSize();
		String panelSizeAsString = Integer.toString(panelFontSize);
		panelFontSizeCombo = createAndAddLabelAndCombo(htmlTab, EAM.text("Font Size"), new FontSizeQuestion(), panelSizeAsString);
			
		String panelFontFamily = getMainWindow().getDataPanelFontFamily();
		panelFontFamilyCombo = createAndAddLabelAndCombo(htmlTab, EAM.text("Font Family"), new FontFamiliyQuestion(), panelFontFamily);
		
		createAndAddBlankRow(htmlTab);
		String rowHeightMode = getMainWindow().getRowHeightModeString();
		ChoiceQuestion rowHeightModeQuestion = StaticQuestionManager.getQuestion(TableRowHeightModeQuestion.class);
		panelRowHeightModeCombo = createAndAddLabelAndCombo(htmlTab, EAM.text("Table Row Height Mode"), rowHeightModeQuestion, rowHeightMode);
		
		htmlTab.add(new PanelTitleLabel(EAM.text("Enable Spell Checking")));
		enableSpellCheckingCheckBox = new PanelCheckBox();
		enableSpellCheckingCheckBox.setBackground(AppPreferences.getDataPanelBackgroundColor());
		enableSpellCheckingCheckBox.setSelected(getMainWindow().getBooleanPreference(AppPreferences.TAG_CELL_RATINGS_VISIBLE));
		enableSpellCheckingCheckBox.addActionListener(this);
		htmlTab.add(enableSpellCheckingCheckBox);

		return htmlTab;
	}

	private void createAndAddBlankRow(JPanel htmlTab)
	{
		htmlTab.add(new JLabel(" "));
		htmlTab.add(new JLabel(" "));
	}

	private UiComboBox createAndAddLabelAndCombo(JPanel htmlTab, String label, ChoiceQuestion question, String sizeAsString)
	{
		UiComboBox combo = new PanelComboBox(question.getChoices());
		setSelectedItemQuestionBox(combo, sizeAsString);
		combo.addActionListener(this);
		htmlTab.add(new PanelTitleLabel(label));
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
		threatTab.setBackground(AppPreferences.getDataPanelBackgroundColor());

		threatTab.add(new PanelTitleLabel(EAM.text("Show Ratings in Cell")));
		cellRatingsVisibleCheckBox = new PanelCheckBox();
		cellRatingsVisibleCheckBox.setBackground(AppPreferences.getDataPanelBackgroundColor());
		cellRatingsVisibleCheckBox.setSelected(getMainWindow().getBooleanPreference(AppPreferences.TAG_CELL_RATINGS_VISIBLE));
		cellRatingsVisibleCheckBox.addActionListener(this);
		threatTab.add(cellRatingsVisibleCheckBox);
		
		threatRatingPreferencesPanel = new ThreatRatingPreferencesPanel(project); 
		threatTab.add(threatRatingPreferencesPanel);
		
		return threatTab;
	}

	private JPanel createDiagramTab()
	{
		JPanel diagramTab = new JPanel(new BasicGridLayout(1,1));
		diagramTab.setBackground(AppPreferences.getDataPanelBackgroundColor());

		JPanel diagramSystemPreferencesTab = new JPanel(new BasicGridLayout(0,2));
		diagramSystemPreferencesTab.setBackground(AppPreferences.getDataPanelBackgroundColor());
		
		diagramTab.add(new UiLabel(" "));
		diagramTab.add(new PanelTitleLabel(EAM.text("Choose the colors that look best on your system:")));
		interventionDropdown = createAndAddColorDropdown(diagramSystemPreferencesTab, EAM.text("Strategy (Yellow)"), DiagramConstants.strategyColorChoices, AppPreferences.TAG_COLOR_STRATEGY);
		directThreatDropdown = createAndAddColorDropdown(diagramSystemPreferencesTab, EAM.text("Direct Threat (Pink)"), DiagramConstants.directThreatColorChoices, AppPreferences.TAG_COLOR_DIRECT_THREAT);
		indirectFactorDropdown = createAndAddColorDropdown(diagramSystemPreferencesTab, EAM.text("Contributing Factor (Orange)"), DiagramConstants.contributingFactorColorChoices, AppPreferences.TAG_COLOR_CONTRIBUTING_FACTOR);
		biodiversityTargetDropdown = createAndAddColorDropdown(diagramSystemPreferencesTab, EAM.text("Biodiversity Target (Lighter Green)"), DiagramConstants.targetColorChoices, AppPreferences.TAG_COLOR_TARGET);
		humanWelfareTargetDropdown = createAndAddColorDropdown(diagramSystemPreferencesTab, EAM.text("Human Welfare Target (Lighter Brown)"), DiagramConstants.humanWelfareTargetColorChoices, AppPreferences.TAG_COLOR_HUMAN_WELFARE_TARGET);
		biodiversityTargetScopeDropdown = createAndAddColorDropdown(diagramSystemPreferencesTab, EAM.text("Biodiversity Target Scope (Darker Green)"), DiagramConstants.biodiversityTargetScopeColorChoices, AppPreferences.TAG_COLOR_SCOPE_BOX);
		humanWelfareScopeDropDown = createAndAddColorDropdown(diagramSystemPreferencesTab, EAM.text("Human Welfare Target Scope (Darker Brown)"), DiagramConstants.humanWelfareScopeColorChoices, AppPreferences.TAG_COLOR_HUMAN_WELFARE_SCOPE_BOX);
		intermediateResultDropDown = createAndAddColorDropdown(diagramSystemPreferencesTab, EAM.text("Intermediate Result (Light Blue)"), DiagramConstants.intermediateResultChoices, AppPreferences.TAG_COLOR_INTERMEDIATE_RESULT);
		threatReductionResultDropDown = createAndAddColorDropdown(diagramSystemPreferencesTab, EAM.text("Threat Reduction Result (Light Purple)"), DiagramConstants.threatReductionResultChoices, AppPreferences.TAG_COLOR_THREAT_REDUCTION_RESULT);

		
		diagramTab.add(new UiLabel(" "));
		diagramTab.add(new UiLabel(" "));
		
		JPanel bottomText = new JPanel();
		bottomText.setBackground(AppPreferences.getDataPanelBackgroundColor());
		bottomText.add(new HyperlinkLabel( 
				EAM.text("<div class='DataPanel'><p>Why are my choices limited to one color family for each type of factor?</p>"),
				EAM.text("We are trying to create a standard set of symbols that can be recognized " +
				"globally. Just like people the world over recognize a red octagon as a " +
				"stop sign, we hope that they will recognize a green oval as a target or " +
				"a yellow hexagon as a strategy")), BorderLayout.AFTER_LAST_LINE);
		bottomText.setBorder(BorderFactory.createEmptyBorder(25, 5, 25, 5));
		
		diagramTab.add(diagramSystemPreferencesTab);
		diagramTab.add(bottomText);
		
		TwoColumnPanel gridChoicePanel = new TwoColumnPanel();
		gridChoicePanel.setBackground(AppPreferences.getDataPanelBackgroundColor());
		gridVisibleCheckBox = new PanelCheckBox();
		gridVisibleCheckBox.setSelected(getMainWindow().getBooleanPreference(AppPreferences.TAG_GRID_VISIBLE));
		gridVisibleCheckBox.addActionListener(this);
		gridChoicePanel.add(new PanelTitleLabel(EAM.text("Show Diagram Grid")));
		gridChoicePanel.add(gridVisibleCheckBox);
		diagramTab.add(gridChoicePanel);

		diagramProjectPreferencesPanel = new DiagramProjectPreferencesPanel(getMainWindow());
		diagramTab.add(diagramProjectPreferencesPanel);
		
		return diagramTab;
	}

	private UiComboBox createAndAddColorDropdown(JPanel diagramSystemPreferencesTab, String label, Color[] colorChoices, String colorTag)
	{
		diagramSystemPreferencesTab.add(new PanelTitleLabel(label));
		UiComboBox dropdown = new PanelComboBox(colorChoices);
		dropdown.setRenderer(new ColorItemRenderer());
		dropdown.setSelectedItem(getMainWindow().getColorPreference(colorTag));
		dropdown.addActionListener(this);
		diagramSystemPreferencesTab.add(dropdown);
		
		return dropdown;
	}

	private void update()
	{
		setColorPreference(interventionDropdown, AppPreferences.TAG_COLOR_STRATEGY);
		setColorPreference(indirectFactorDropdown, AppPreferences.TAG_COLOR_CONTRIBUTING_FACTOR);
		setColorPreference(directThreatDropdown, AppPreferences.TAG_COLOR_DIRECT_THREAT);
		setColorPreference(biodiversityTargetDropdown, AppPreferences.TAG_COLOR_TARGET);
		setColorPreference(humanWelfareTargetDropdown, AppPreferences.TAG_COLOR_HUMAN_WELFARE_TARGET);
		setColorPreference(biodiversityTargetScopeDropdown, AppPreferences.TAG_COLOR_SCOPE_BOX);
		setColorPreference(humanWelfareScopeDropDown, AppPreferences.TAG_COLOR_HUMAN_WELFARE_SCOPE_BOX);
		setColorPreference(intermediateResultDropDown, AppPreferences.TAG_COLOR_INTERMEDIATE_RESULT);
		setColorPreference(threatReductionResultDropDown, AppPreferences.TAG_COLOR_THREAT_REDUCTION_RESULT);
		
		getMainWindow().setBooleanPreference(AppPreferences.TAG_GRID_VISIBLE, gridVisibleCheckBox.isSelected());
		
		if(cellRatingsVisibleCheckBox != null)
			getMainWindow().setBooleanPreference(AppPreferences.TAG_CELL_RATINGS_VISIBLE, cellRatingsVisibleCheckBox.isSelected());

		String panelFontSizeValue = getSelectedItemQuestionBox(panelFontSizeCombo);
		getMainWindow().setDataPanelFontSize(Integer.parseInt(panelFontSizeValue));
		
		String panelFontFamilyValue = getSelectedItemQuestionBox(panelFontFamilyCombo);
		getMainWindow().setDataPanelFontFamily(panelFontFamilyValue);
		
		String rowHeightMode = getSelectedItemQuestionBox(panelRowHeightModeCombo);
		getMainWindow().setRowHeightMode(rowHeightMode);
		
		getMainWindow().setBooleanPreference(AppPreferences.TAG_IS_SPELL_CHECK_ENABLED, enableSpellCheckingCheckBox.isSelected());

		getMainWindow().safelySavePreferences();
	}

	private void setColorPreference(UiComboBox colorDropDown, String tagColorStrategy)
	{
		Color interventionColor = (Color)colorDropDown.getSelectedItem();
		getMainWindow().setColorPreference(tagColorStrategy, interventionColor);
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

		@Override
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

		@Override
		public Dimension getSize()
		{
			return new Dimension(48, 16);
		}

		@Override
		public Dimension getPreferredSize()
		{
			return getSize();
		}

		@Override
		public Dimension getMinimumSize()
		{
			return getSize();
		}

		@Override
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
	
	private MainWindow getMainWindow()
	{
		return mainWindow;
	}

	private Project project;
	private MainWindow mainWindow;
	private DiagramProjectPreferencesPanel diagramProjectPreferencesPanel;
	private ThreatRatingPreferencesPanel threatRatingPreferencesPanel;
	private SummaryPlanningPanel summaryPlanningPanel;
	private ProjectSettingsPanel projectSettingsPanel;
	
	private UiComboBox interventionDropdown;
	private UiComboBox directThreatDropdown;
	private UiComboBox indirectFactorDropdown;
	private UiComboBox biodiversityTargetDropdown;
	private UiComboBox humanWelfareTargetDropdown;
	private UiComboBox biodiversityTargetScopeDropdown;
	private UiComboBox humanWelfareScopeDropDown;
	private UiComboBox intermediateResultDropDown;
	private UiComboBox threatReductionResultDropDown;
	private UiCheckBox gridVisibleCheckBox; 
	private UiCheckBox cellRatingsVisibleCheckBox;
	private UiCheckBox enableSpellCheckingCheckBox;
	
	private UiComboBox panelFontSizeCombo;
	private UiComboBox panelFontFamilyCombo;
	private UiComboBox panelRowHeightModeCombo;
}
