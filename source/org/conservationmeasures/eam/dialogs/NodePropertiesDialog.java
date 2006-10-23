/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.conservationmeasures.eam.actions.ActionCreateIndicator;
import org.conservationmeasures.eam.actions.ActionCreateObjective;
import org.conservationmeasures.eam.actions.EAMAction;
import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.DiagramComponent;
import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.diagram.nodetypes.NodeType;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeDirectThreat;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeIndirectFactor;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.icons.DirectThreatIcon;
import org.conservationmeasures.eam.icons.IndirectFactorIcon;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.GoalIds;
import org.conservationmeasures.eam.ids.IndicatorId;
import org.conservationmeasures.eam.ids.ModelNodeId;
import org.conservationmeasures.eam.ids.ObjectiveIds;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.CommandExecutedListener;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objecthelpers.TaxonomyItem;
import org.conservationmeasures.eam.objecthelpers.TaxonomyLoader;
import org.conservationmeasures.eam.objectpools.GoalPool;
import org.conservationmeasures.eam.objectpools.IndicatorPool;
import org.conservationmeasures.eam.objectpools.ObjectivePool;
import org.conservationmeasures.eam.objects.ConceptualModelFactor;
import org.conservationmeasures.eam.objects.ConceptualModelIntervention;
import org.conservationmeasures.eam.objects.ConceptualModelNode;
import org.conservationmeasures.eam.objects.Goal;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.Objective;
import org.conservationmeasures.eam.project.NodeCommandHelper;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.DialogGridPanel;
import org.conservationmeasures.eam.utils.UiTextFieldWithLengthLimit;
import org.conservationmeasures.eam.views.strategicplan.StrategicPlanPanel;
import org.martus.swing.UiButton;
import org.martus.swing.UiCheckBox;
import org.martus.swing.UiComboBox;
import org.martus.swing.UiLabel;
import org.martus.swing.UiTextArea;
import org.martus.swing.UiTextField;

public class NodePropertiesDialog extends JDialog implements
		CommandExecutedListener
{
	public NodePropertiesDialog(MainWindow parent,
			DiagramComponent diagramToUse, String title)
			throws HeadlessException
	{
		super(parent, title);

		mainWindow = parent;
		diagram = diagramToUse;

		getProject().addCommandExecutedListener(this);
		addWindowListener(new WindowEventHandler());

		setResizable(true);
		setModal(false);
	}

	public void selectTab(int tabIdentifier)
	{
		switch(tabIdentifier)
		{
			case TAB_OBJECTIVES:
				tabs.setSelectedComponent(objectivesTab);
				break;
			case TAB_INDICATORS:
				tabs.setSelectedComponent(indicatorsTab);
				break;
			case TAB_GOALS:
				tabs.setSelectedComponent(goalsTab);
				break;
			default:
				tabs.setSelectedComponent(detailsTab);
				break;
		}
	}

	public void setCurrentNode(DiagramComponent diagram, DiagramNode node)
	{
		Container contents = getContentPane();
		contents.setLayout(new BorderLayout());
		contents.removeAll();
		try
		{
			currentNode = node;
			contents.add(createLabelBar(currentNode),
					BorderLayout.BEFORE_FIRST_LINE);
			contents.add(createTabbedPane(currentNode), BorderLayout.CENTER);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Error reading activity information");
		}
		pack();
	}

	public DiagramNode getCurrentNode()
	{
		return currentNode;
	}

	public ModelNodeId getNodeId()
	{
		return getCurrentNode().getWrappedId();
	}

	private Component createLabelBar(DiagramNode node)
	{
		createTextField(node.getLabel(), MAX_LABEL_LENGTH);

		DialogGridPanel grid = new DialogGridPanel();
		grid.add(new UiLabel(EAM.text("Label|Label")));
		grid.add(textField);

		if(node.isFactor())
		{
			grid.add(new UiLabel(EAM.text("Label|Type")));
			String subTypeString = new NodeTypeIndirectFactor().toString();
			if(node.isDirectThreat())
				subTypeString = new NodeTypeDirectThreat().toString();
			grid.add(new UiLabel(subTypeString));
		}

		grid.add(new UiLabel());
		return grid;
	}

	private Component createTabbedPane(DiagramNode node) throws Exception
	{
		tabs = new JTabbedPane();
		tabs.add(createMainGrid(node), EAM.text("Tab|Details"));
		tabs.add(createIndicatorsGrid(node), EAM.text("Tab|Indicators"));
		if(node.canHaveObjectives())
			tabs.add(createObjectivesGrid(node), EAM.text("Tab|Objectives"));
		if(node.canHaveGoal())
			tabs.add(createGoalsGrid(node), EAM.text("Tab|Goals"));
		if(node.isIntervention())
			tabs.add(createTasksGrid(node), EAM.text("Tab|Actions"));
		return tabs;
	}

	private Component createMainGrid(DiagramNode node)
	{
		detailsTab = new DialogGridPanel();
		statusCheckBox = new UiCheckBox(EAM.text("Label|Draft"));

		if(node.isDirectThreat())
		{
			detailsTab.add(new UiLabel(EAM
					.text("Label|IUCN-CMP Classification")));
			detailsTab.add(createThreatClassificationDropdown());
		}

		if(node.isIntervention())
		{
			detailsTab.add(new UiLabel(EAM.text("Label|Status")));
			statusCheckBox.setSelected(node.isStatusDraft());
			statusCheckBox.addItemListener(new StatusChangeHandler());
			detailsTab.add(statusCheckBox);

			detailsTab.add(new UiLabel(EAM
					.text("Label|IUCN-CMP Classification")));
			detailsTab.add(createInterventionClassificationDropdown());
		}

		detailsTab.add(new UiLabel(EAM.text("Label|Comments")));
		detailsTab.add(createComment(node.getComment()));

		return detailsTab;
	}

	class StatusChangeHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent event)
		{
			try
			{
				getProject().executeCommand(buildStatusCommand());
				getProject().updateVisibilityOfSingleNode(getCurrentNode());
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	private Component createIndicatorsGrid(DiagramNode node)
	{
		indicatorsTab = new DialogGridPanel();

		indicatorsTab.add(new UiLabel(EAM.text("Label|Indicator")));
		indicatorsTab.add(createIndicatorDropdown(node.getIndicatorId()));

		indicatorsTab.add(new UiLabel(""));
		EAMAction action = mainWindow.getActions().get(
				ActionCreateIndicator.class);
		UiButton buttonCreate = new UiButton(action);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(buttonCreate, BorderLayout.BEFORE_LINE_BEGINS);
		indicatorsTab.add(panel);

		return indicatorsTab;
	}

	private Component createObjectivesGrid(DiagramNode node)
	{
		objectivesTab = new DialogGridPanel();

		objectivesTab.add(new UiLabel(EAM.text("Label|Objective")));
		objectivesTab.add(createObjectiveDropdown());

		objectivesTab.add(new UiLabel(""));
		EAMAction action = mainWindow.getActions().get(
				ActionCreateObjective.class);
		UiButton buttonCreate = new UiButton(action);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(buttonCreate, BorderLayout.BEFORE_LINE_BEGINS);
		objectivesTab.add(panel);

		return objectivesTab;
	}

	private Component createGoalsGrid(DiagramNode node)
	{
		goalsTab = new DialogGridPanel();

		goalsTab.add(new UiLabel(EAM.text("Label|Goal")));
		goalsTab.add(createTargetGoal(getProject().getGoalPool(), node
				.getGoals()));

		return goalsTab;
	}

	private Component createTasksGrid(DiagramNode node) throws Exception
	{
		ConceptualModelIntervention intervention = (ConceptualModelIntervention) node
				.getUnderlyingObject();
		return StrategicPlanPanel.createForStrategy(mainWindow, intervention);
	}

	private Component createTextField(String initialText, int maxLength)
	{
		textField = new UiTextFieldWithLengthLimit(maxLength);
		textField.addFocusListener(new LabelFocusHandler());
		textField.requestFocus(true);

		textField.setText(initialText);
		textField.selectAll();

		JPanel component = new JPanel(new BorderLayout());
		component.add(textField, BorderLayout.LINE_START);
		return component;
	}

	class LabelFocusHandler implements FocusListener
	{
		public void focusGained(FocusEvent event)
		{
		}

		public void focusLost(FocusEvent event)
		{
			String newText = getText();
			if(newText.equals(getCurrentNode().getLabel()))
				return;
			try
			{
				CommandSetObjectData cmd = NodeCommandHelper
						.createSetLabelCommand(getNodeId(), newText);
				getProject().executeCommand(cmd);
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}
	}

	public Component createObjectiveDropdown()
	{
		dropdownObjective = new UiComboBox();
		populateObjectives();
		selectCurrentObjectives();
		dropdownObjective.addActionListener(new ObjectiveChangeHandler());

		JPanel component = new JPanel(new BorderLayout());
		component.add(dropdownObjective, BorderLayout.LINE_START);
		return component;
	}

	class ObjectiveChangeHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(ignoreObjectiveChanges)
				return;

			try
			{
				int type = ObjectType.MODEL_NODE;
				String tag = ConceptualModelNode.TAG_OBJECTIVE_IDS;
				String goals = getObjectives().toString();
				CommandSetObjectData cmd = new CommandSetObjectData(type,
						getNodeId(), tag, goals);
				getProject().executeCommand(cmd);
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	private void selectCurrentObjectives()
	{
		ObjectivePool allAvailableObjectives = getProject().getObjectivePool();
		ObjectiveIds currentObjectives = currentNode.getObjectives();
		Object nullObjective = dropdownObjective.getItemAt(0);
		Object selected = nullObjective;
		if(currentObjectives.size() > 0)
			selected = allAvailableObjectives.find(currentObjectives.getId(0));
		if(selected == null)
			selected = nullObjective;
		dropdownObjective.setSelectedItem(selected);
	}

	private void populateObjectives()
	{
		ignoreObjectiveChanges = true;
		dropdownObjective.removeAllItems();
		Objective nullObjective = new Objective(BaseId.INVALID);
		dropdownObjective.addItem(nullObjective);

		ObjectivePool allAvailableObjectives = getProject().getObjectivePool();
		BaseId[] objectiveIds = allAvailableObjectives.getIds();
		for(int i = 0; i < objectiveIds.length; ++i)
		{
			dropdownObjective.addItem(allAvailableObjectives
					.find(objectiveIds[i]));
		}
		ignoreObjectiveChanges = false;
	}

	public Component createTargetGoal(GoalPool allAvailableGoals,
			GoalIds currentGoals)
	{
		dropdownGoal = new UiComboBox();
		BaseId[] goalIds = allAvailableGoals.getIds();
		for(int i = 0; i < goalIds.length; ++i)
		{
			dropdownGoal.addItem(allAvailableGoals.find(goalIds[i]));
		}

		if(currentGoals.size() == 0)
		{
			dropdownGoal
					.setSelectedItem(allAvailableGoals.find(BaseId.INVALID));
		}
		else
		{
			BaseId id = currentGoals.getId(0);
			Goal goal = allAvailableGoals.find(id);
			dropdownGoal.setSelectedItem(goal);
		}
		dropdownGoal.addActionListener(new GoalChangeHandler());

		JPanel component = new JPanel(new BorderLayout());
		component.add(dropdownGoal, BorderLayout.LINE_START);
		return component;
	}

	class GoalChangeHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			try
			{
				int type = ObjectType.MODEL_NODE;
				String tag = ConceptualModelNode.TAG_GOAL_IDS;
				String goals = getGoals().toString();
				CommandSetObjectData cmd = new CommandSetObjectData(type,
						getNodeId(), tag, goals);
				getProject().executeCommand(cmd);
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	public Component createIndicatorDropdown(BaseId indicatorId)
	{
		dropdownIndicator = new UiComboBox();
		populateIndicators();
		selectCurrentIndicator(indicatorId);
		dropdownIndicator.addActionListener(new IndicatorChangeHandler());

		JPanel component = new JPanel(new BorderLayout());
		component.add(dropdownIndicator, BorderLayout.LINE_START);
		return component;
	}

	private void selectCurrentIndicator(BaseId indicatorId)
	{
		IndicatorPool allAvailableIndicators = getProject().getIndicatorPool();
		Object nullIndicator = dropdownIndicator.getItemAt(0);
		Object selected = allAvailableIndicators.find(indicatorId);
		if(selected == null)
			selected = nullIndicator;
		dropdownIndicator.setSelectedItem(selected);
	}

	private Indicator populateIndicators()
	{
		ignoreIndicatorChanges = true;
		dropdownIndicator.removeAllItems();
		Indicator nullIndicator = new Indicator(new IndicatorId(BaseId.INVALID
				.asInt()));
		dropdownIndicator.addItem(nullIndicator);

		IndicatorPool allAvailableIndicators = getProject().getIndicatorPool();
		BaseId[] availableIds = allAvailableIndicators.getIds();
		for(int i = 0; i < availableIds.length; ++i)
		{
			dropdownIndicator.addItem(allAvailableIndicators
					.find(availableIds[i]));
		}
		ignoreIndicatorChanges = false;
		return nullIndicator;
	}

	class IndicatorChangeHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(ignoreIndicatorChanges)
				return;

			try
			{
				int type = ObjectType.MODEL_NODE;
				String tag = ConceptualModelNode.TAG_INDICATOR_ID;
				String indicator = getIndicator().getId().toString();
				CommandSetObjectData cmd = new CommandSetObjectData(type,
						getNodeId(), tag, indicator);
				getProject().executeCommand(cmd);
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	class ThreatClassificationChangeHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(ignoreThreatClassificationChanges)
				return;

			try
			{
				int type = ObjectType.MODEL_NODE;
				String tag = ConceptualModelFactor.TAG_TAXONOMY_CODE;
				String taxonomyCode = getThreatTaxonomyItem().getTaxonomyCode();
				CommandSetObjectData cmd = new CommandSetObjectData(type,
						getNodeId(), tag, taxonomyCode);
				getProject().executeCommand(cmd);
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	
	class InterventionClassificationChangeHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(ignoreThreatClassificationChanges)
				return;

			try
			{
				int type = ObjectType.MODEL_NODE;
				String tag = ConceptualModelFactor.TAG_TAXONOMY_CODE;
				String taxonomyCode = getInterventionTaxonomyItem().getTaxonomyCode();
				CommandSetObjectData cmd = new CommandSetObjectData(type,
						getNodeId(), tag, taxonomyCode);
				getProject().executeCommand(cmd);
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}
	
	
	public JComponent createComment(String comment)
	{
		commentField = new UiTextArea(4, 25);
		commentField.setWrapStyleWord(true);
		commentField.setLineWrap(true);
		commentField.setText(comment);
		commentField.addFocusListener(new CommentFocusHandler());

		JScrollPane component = new JScrollPane(commentField);
		return component;
	}

	class CommentFocusHandler implements FocusListener
	{
		public void focusGained(FocusEvent event)
		{
		}

		public void focusLost(FocusEvent event)
		{
			String newComment = getComment();
			if(newComment.equals(getCurrentNode().getComment()))
				return;
			try
			{
				int type = ObjectType.MODEL_NODE;
				String tag = ConceptualModelNode.TAG_COMMENT;
				CommandSetObjectData cmd = new CommandSetObjectData(type,
						getNodeId(), tag, newComment);
				getProject().executeCommand(cmd);
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	JComponent createThreatClassificationDropdown()
	{
		String[] choices = { "error processing classifications", };
		try
		{
			TaxonomyItem[] taxonomyItems = TaxonomyLoader
					.load("ThreatTaxonomies.txt");
			dropdownThreatClassification = new UiComboBox(taxonomyItems);

			String taxonomyCode = getCurrentNode().getUnderlyingObject()
					.getData(ConceptualModelFactor.TAG_TAXONOMY_CODE);

			TaxonomyItem foundTaxonomyItem = SearchTaxonomyClassificationsForCode(
					taxonomyItems, taxonomyCode);

			if(foundTaxonomyItem == null)
			{
				String errorMessage = "Threat not found in table ; please make another selection";
				EAM.errorDialog(EAM.text(errorMessage));
				foundTaxonomyItem = taxonomyItems[0];
			}

			dropdownThreatClassification.setSelectedItem(foundTaxonomyItem);

			dropdownThreatClassification
					.addActionListener(new ThreatClassificationChangeHandler());

			return dropdownThreatClassification;

		}
		catch(Exception e)
		{
			EAM.logException(e);
			return new UiComboBox(choices);
		}
	}



	JComponent createInterventionClassificationDropdown()
	{
		String[] choices = { "error processing classifications", };
		try
		{
			TaxonomyItem[] taxonomyItems = TaxonomyLoader
					.load("InterventionTaxonomies.txt");
			dropdownInterventionClassification = new UiComboBox(taxonomyItems);
			
			String taxonomyCode = getCurrentNode().getUnderlyingObject()
			.getData(ConceptualModelFactor.TAG_TAXONOMY_CODE);

			TaxonomyItem foundTaxonomyItem = SearchTaxonomyClassificationsForCode(
					taxonomyItems, taxonomyCode);

			if(foundTaxonomyItem == null)
			{
				String errorMessage = "Intervention not found in table ; please make another selection";
				EAM.errorDialog(EAM.text(errorMessage));
				foundTaxonomyItem = taxonomyItems[0];
			}

			dropdownInterventionClassification.setSelectedItem(foundTaxonomyItem);

			dropdownInterventionClassification
				.addActionListener(new InterventionClassificationChangeHandler());

			return dropdownInterventionClassification;
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return new UiComboBox(choices);
		}

	}
	
	TaxonomyItem SearchTaxonomyClassificationsForCode(
			TaxonomyItem[] taxonomyItems, String taxonomyCode)
	{
		for(int i = 0; i < taxonomyItems.length; i++)
		{
			if(taxonomyItems[i].getTaxonomyCode().equals(taxonomyCode))
				return taxonomyItems[i];
		}
		return null;
	}

	class FactorTypeRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			Component cell = super.getListCellRendererComponent(list, value,
					index, isSelected, cellHasFocus);
			if(((NodeType) value).isDirectThreat())
				setIcon(new DirectThreatIcon());
			if(((NodeType) value).isIndirectFactor())
				setIcon(new IndirectFactorIcon());
			return cell;
		}
	}

	Command buildStatusCommand()
	{
		String newValue = ConceptualModelIntervention.STATUS_REAL;
		if(statusCheckBox.isSelected())
			newValue = ConceptualModelIntervention.STATUS_DRAFT;
		return new CommandSetObjectData(ObjectType.MODEL_NODE, currentNode
				.getDiagramNodeId(), ConceptualModelIntervention.TAG_STATUS,
				newValue);
	}

	Project getProject()
	{
		return getDiagram().getProject();
	}

	DiagramComponent getDiagram()
	{
		return diagram;
	}

	public String getText()
	{
		return textField.getText();
	}

	public String getComment()
	{
		return commentField.getText();
	}

	public Indicator getIndicator()
	{
		return (Indicator) dropdownIndicator.getSelectedItem();
	}

	public TaxonomyItem getThreatTaxonomyItem()
	{
		return (TaxonomyItem) dropdownThreatClassification.getSelectedItem();
	}

	public TaxonomyItem getInterventionTaxonomyItem()
	{
		return (TaxonomyItem) dropdownInterventionClassification.getSelectedItem();
	}
	
	public NodeType getType()
	{
		return (NodeType) dropdownFactorType.getSelectedItem();
	}

	public ObjectiveIds getObjectives()
	{
		ObjectiveIds objectives = new ObjectiveIds();
		Objective oneObjective = (Objective) dropdownObjective
				.getSelectedItem();
		if(!oneObjective.getId().isInvalid())
			objectives.setObjectives(oneObjective);
		return objectives;
	}

	public GoalIds getGoals()
	{
		Goal oneGoal = (Goal) dropdownGoal.getSelectedItem();
		GoalIds goals = new GoalIds();
		if(oneGoal != null)
			goals.addId(oneGoal.getId());
		return goals;
	}

	public void commandExecuted(CommandExecutedEvent event)
	{
		refreshObjectiveListIfNecessary(event);
		selectNewlyCreatedObjectiveIfNecessary(event);

		refreshIndicatorListIfNecessary(event);
		selectNewlyCreatedIndicatorIfNecessary(event);

	}

	public void commandUndone(CommandExecutedEvent event)
	{
		refreshObjectiveListIfNecessary(event);

		refreshIndicatorListIfNecessary(event);
	}

	public void commandFailed(Command command, CommandFailedException e)
	{
	}

	void refreshObjectiveListIfNecessary(CommandExecutedEvent event)
	{
		if(dropdownObjective == null)
			return;
		Command rawCommand = event.getCommand();
		if(rawCommand.getCommandName().equals(CommandCreateObject.COMMAND_NAME))
		{
			CommandCreateObject cmd = (CommandCreateObject) rawCommand;
			if(cmd.getObjectType() == ObjectType.OBJECTIVE)
			{
				populateObjectives();
			}
		}
		if(rawCommand.getCommandName()
				.equals(CommandSetObjectData.COMMAND_NAME))
		{
			CommandSetObjectData cmd = (CommandSetObjectData) rawCommand;
			if(cmd.getObjectType() == ObjectType.OBJECTIVE)
			{
				Object selected = dropdownObjective.getSelectedItem();
				populateObjectives();
				dropdownObjective.setSelectedItem(selected);
			}
		}

	}

	void selectNewlyCreatedObjectiveIfNecessary(CommandExecutedEvent event)
	{
		if(dropdownObjective == null)
			return;

		Command rawCommand = event.getCommand();
		if(rawCommand.getCommandName().equals(CommandCreateObject.COMMAND_NAME))
		{
			CommandCreateObject cmd = (CommandCreateObject) rawCommand;
			if(cmd.getObjectType() == ObjectType.OBJECTIVE)
			{
				Objective newObjective = getProject().getObjectivePool().find(
						cmd.getCreatedId());
				dropdownObjective.setSelectedItem(newObjective);
			}
		}
	}

	void refreshIndicatorListIfNecessary(CommandExecutedEvent event)
	{
		if(dropdownIndicator == null)
			return;
		Command rawCommand = event.getCommand();
		if(rawCommand.getCommandName().equals(CommandCreateObject.COMMAND_NAME))
		{
			CommandCreateObject cmd = (CommandCreateObject) rawCommand;
			if(cmd.getObjectType() == ObjectType.INDICATOR)
			{
				populateIndicators();
			}
		}
		if(rawCommand.getCommandName()
				.equals(CommandSetObjectData.COMMAND_NAME))
		{
			CommandSetObjectData cmd = (CommandSetObjectData) rawCommand;
			if(cmd.getObjectType() == ObjectType.INDICATOR)
			{
				Object selected = dropdownIndicator.getSelectedItem();
				populateIndicators();
				dropdownIndicator.setSelectedItem(selected);
			}
		}
	}

	void selectNewlyCreatedIndicatorIfNecessary(CommandExecutedEvent event)
	{
		if(dropdownIndicator == null)
			return;

		Command rawCommand = event.getCommand();
		if(rawCommand.getCommandName().equals(CommandCreateObject.COMMAND_NAME))
		{
			CommandCreateObject cmd = (CommandCreateObject) rawCommand;
			if(cmd.getObjectType() == ObjectType.INDICATOR)
			{
				Indicator newIndicator = getProject().getIndicatorPool().find(
						cmd.getCreatedId());
				dropdownIndicator.setSelectedItem(newIndicator);
			}
		}
	}

	class WindowEventHandler extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			stopListening();
		}

	}

	void stopListening()
	{
		getProject().removeCommandExecutedListener(this);
	}

	public void dispose()
	{
		stopListening();
		super.dispose();
	}

	static final int MAX_LABEL_LENGTH = 40;

	public static final int TAB_DETAILS = 0;

	public static final int TAB_INDICATORS = 1;

	public static final int TAB_OBJECTIVES = 2;

	public static final int TAB_GOALS = 3;

	JTabbedPane tabs;

	DialogGridPanel detailsTab;

	DialogGridPanel indicatorsTab;

	DialogGridPanel objectivesTab;

	DialogGridPanel goalsTab;

	MainWindow mainWindow;

	DiagramComponent diagram;

	DiagramNode currentNode;

	UiTextField textField;

	UiTextArea commentField;

	UiComboBox dropdownFactorType;

	UiComboBox dropdownThreatPriority;

	UiComboBox dropdownIndicator;

	UiComboBox dropdownObjective;

	UiComboBox dropdownGoal;

	UiCheckBox statusCheckBox;

	UiComboBox dropdownThreatClassification;
	
	UiComboBox dropdownInterventionClassification;

	boolean ignoreObjectiveChanges;

	boolean ignoreIndicatorChanges;

	boolean ignoreThreatClassificationChanges;
}
