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
package org.miradi.views.diagram;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.Box;

import org.miradi.diagram.DiagramComponent;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.EAMGraphCell;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.diagram.cells.LinkCell;
import org.miradi.dialogs.base.AbstractObjectDataInputPanel;
import org.miradi.dialogs.base.ModelessDialogPanel;
import org.miradi.dialogs.base.ModelessDialogWithClose;
import org.miradi.dialogs.diagram.FactorLinkPropertiesDialog;
import org.miradi.dialogs.diagram.FactorLinkPropertiesPanel;
import org.miradi.dialogs.diagram.FactorPropertiesPanel;
import org.miradi.dialogs.diagram.GroupBoxPropertiesPanel;
import org.miradi.dialogs.diagram.ProjectScopePanel;
import org.miradi.dialogs.diagram.TextBoxPropertiesPanel;
import org.miradi.dialogs.groupboxLink.GroupBoxLinkListTablePanel;
import org.miradi.dialogs.groupboxLink.GroupBoxLinkManagementPanel;
import org.miradi.dialogs.groupboxLink.GroupBoxLinkTableModel;
import org.miradi.dialogs.stress.StressPropertiesPanel;
import org.miradi.dialogs.task.TaskPropertiesInputPanel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.GroupBox;
import org.miradi.objects.Stress;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.objects.TextBox;
import org.miradi.questions.ThreatRatingModeChoiceQuestion;
import org.miradi.views.umbrella.StaticPicker;

public class PropertiesDoer extends LocationDoer
{
	public boolean isAvailable()
	{
		if(!getProject().isOpen())
			return false;
		
		if (! isInDiagram())
			return false;
		
		EAMGraphCell[] selected = getSelectedCellsWithoutGroupBoxCoveredLinks();
		if(selected.length != 1)
			return false;
		
		if(selected[0].isFactor() || selected[0].isProjectScope())
			return true;
		
		if(selected[0].isFactorLink())
			return true;
		
		return false;
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;

		try
		{
			EAMGraphCell topCellAtClickPoint = getCorrectCellToShowPropertiesFor();
			if(topCellAtClickPoint.isFactor())
				doFactorProperties((FactorCell)topCellAtClickPoint, getLocation());

			else if(topCellAtClickPoint.isProjectScope())
				doProjectScopeProperties();

			else if(topCellAtClickPoint.isFactorLink())
				doFactorLinkProperties(topCellAtClickPoint.getDiagramLink());
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}

	private EAMGraphCell getCorrectCellToShowPropertiesFor() throws Exception
	{
		EAMGraphCell selected = getSelectedCellsWithoutGroupBoxCoveredLinks()[0];
		if (!selected.isFactor())
			return selected;
		
		EAMGraphCell topCellAtClickPoint = (EAMGraphCell) getDiagramComponent().getFirstCellForLocation(getLocation().x, getLocation().y);
		HashSet<FactorCell> children = getChildrenIfAny(selected);
		if (children.contains(topCellAtClickPoint))
			return topCellAtClickPoint;
		
		return selected;
	}

	private HashSet<FactorCell> getChildrenIfAny(EAMGraphCell selected) throws Exception
	{
		DiagramModel model = getDiagramView().getDiagramPanel().getDiagramModel();
		if (selected.isProjectScope())
			return new HashSet(model.getAllDiagramTargets());
		
		if (selected.getDiagramFactor().isGroupBoxFactor())
			return  model.getGroupBoxFactorChildren(selected);			
		
		return new HashSet();
	}
	
	class ScopePropertiesDialog extends ModelessDialogWithClose
	{
		public ScopePropertiesDialog(MainWindow parent, ProjectScopePanel panel)
		{
			super(parent, panel, panel.getPanelDescription());
		}
		
		@Override
		public void addAdditionalButtons(Box buttonBarToUse)
		{
			super.addAdditionalButtons(buttonBarToUse);
		}
	}

	void doProjectScopeProperties() throws CommandFailedException
	{
		ProjectScopePanel projectScopePanel = new ProjectScopePanel(getProject(), getProject().getMetadata());
		ScopePropertiesDialog dlg = new ScopePropertiesDialog(getMainWindow(), projectScopePanel); 
		getView().showFloatingPropertiesDialog(dlg);
	}
	
	void doFactorLinkProperties(DiagramLink diagramLink) throws Exception
	{
		if (diagramLink.isTargetLink() && diagramLink.isGroupBoxLink())
		{
			GroupBoxLinkManagementPanel dialogPanel = createDialogPanel(diagramLink);
			showPropertiesPanel(dialogPanel);
			dialogPanel.updateSplitterLocation();
		}
		else
		{
			showPropertiesPanel(getFactorLinkPropertiesPanel(diagramLink));
		}
	}

	private void showPropertiesPanel(ModelessDialogPanel dialogPanel)
	{
		FactorLinkPropertiesDialog dlg = new FactorLinkPropertiesDialog(getMainWindow(), dialogPanel, dialogPanel.getPanelDescription()); 
		getView().showFloatingPropertiesDialog(dlg);
	}

	private GroupBoxLinkManagementPanel createDialogPanel(DiagramLink diagramLink) throws Exception
	{
		ORefList children = diagramLink.getSelfOrChildren();
		ORef firstChildRef = children.get(0);
		DiagramLink diagramLinkChild = DiagramLink.find(getProject(), firstChildRef);
		
		GroupBoxLinkTableModel model = new GroupBoxLinkTableModel(getProject(), diagramLink.getRef(), DiagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS);
		GroupBoxLinkListTablePanel tablePanel = new GroupBoxLinkListTablePanel(getMainWindow(), model);
		FactorLinkPropertiesPanel factorLinkPropertiesPanel = FactorLinkPropertiesPanel.createGroupBoxedTargetLinkPropertiesPanel(getMainWindow(), diagramLinkChild.getWrappedRef(), tablePanel.getPicker());
		
		return new GroupBoxLinkManagementPanel(getMainWindow(), diagramLink.getRef(), DiagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS, getMainWindow().getActions(), tablePanel, factorLinkPropertiesPanel);
	}
	
	private FactorLinkPropertiesPanel getFactorLinkPropertiesPanel(DiagramLink diagramLink) throws Exception
	{
		boolean isTargetLink = diagramLink.isTargetLink();
		boolean isStressBasedMode = getProject().getMetadata().getThreatRatingMode().equals(ThreatRatingModeChoiceQuestion.STRESS_BASED_CODE);
		if (!isTargetLink || !isStressBasedMode)
			return FactorLinkPropertiesPanel.createWithOnlyBidirectionalAndColorPropertiesPanel(getProject(), diagramLink);
		
		ORef fromRef = diagramLink.getUnderlyingLink().getFromFactorRef();
		ORef toRef = diagramLink.getUnderlyingLink().getToFactorRef();
		ORefList hierarchyRefs = new ORefList();
		hierarchyRefs.add(diagramLink.getRef());
		hierarchyRefs.add(diagramLink.getWrappedRef());
		if (Target.is(fromRef))
		{
			hierarchyRefs.add(fromRef);
			hierarchyRefs.add(toRef);
		}
		else 
		{
			hierarchyRefs.add(toRef);
			hierarchyRefs.add(fromRef);
		}
	
		StaticPicker picker = new StaticPicker(hierarchyRefs);
		
		return FactorLinkPropertiesPanel.createTargetLinkPropertiesPanel(getMainWindow(), diagramLink, picker);
	}
	
	private void doFactorProperties(FactorCell selectedFactorCell, Point at) throws Exception
	{
		int tabToStartOn = getTabToStartOn(selectedFactorCell, at);
		DiagramFactor diagramFactor = selectedFactorCell.getDiagramFactor();
		doFactorProperties(diagramFactor, tabToStartOn);
	}

	public void doFactorProperties(DiagramFactor diagramFactor, int tabToStartOn) throws Exception
	{
		int wrappedType = diagramFactor.getWrappedType();
		
		if (TextBox.is(wrappedType))
			doTextBoxProperties(diagramFactor);
		else if (GroupBox.is(wrappedType))
			doGroupBoxProperties(diagramFactor);
		else if (Stress.is(wrappedType))
			doStressProperties(diagramFactor);
		else if (Task.is(wrappedType))
			doActivityProperties(diagramFactor);
		else
			doNormalFactorProperties(diagramFactor, tabToStartOn);
	}

	private void doNormalFactorProperties(DiagramFactor diagramFactor, int tabToStartOn)
	{
		DiagramView view = (DiagramView)getView();
		view.showNodeProperties(diagramFactor, tabToStartOn);
	}
	
	private void doTextBoxProperties(DiagramFactor diagramFactor)
	{
		TextBoxPropertiesPanel panel = new TextBoxPropertiesPanel(getProject(), diagramFactor);
		ModelessDialogWithClose propertiesDialog = new ModelessDialogWithClose(getMainWindow(), panel, panel.getPanelDescription()); 
		getView().showFloatingPropertiesDialog(propertiesDialog);
	}

	private void doGroupBoxProperties(DiagramFactor diagramFactor)
	{
		GroupBoxPropertiesPanel panel = new GroupBoxPropertiesPanel(getProject(), diagramFactor);
		ModelessDialogWithClose propertiesDialog = new ModelessDialogWithClose(getMainWindow(), panel, panel.getPanelDescription()); 
		getView().showFloatingPropertiesDialog(propertiesDialog);
	}
	
	private void doStressProperties(DiagramFactor diagramFactor) throws Exception
	{
		StressPropertiesPanel panel = StressPropertiesPanel.createWithoutVisibilityPanel(getMainWindow());
		
		addDiagramWrappedRefToHierarchyAndShowPanel(diagramFactor, panel);
	}
	
	private void doActivityProperties(DiagramFactor diagramFactor) throws Exception
	{
		TaskPropertiesInputPanel panel = TaskPropertiesInputPanel.createWithoutVisibilityPanel(getMainWindow());

		addDiagramWrappedRefToHierarchyAndShowPanel(diagramFactor, panel);
	}

	private void addDiagramWrappedRefToHierarchyAndShowPanel(DiagramFactor diagramFactor, AbstractObjectDataInputPanel propertiesPanel)
	{
		ORefList selectedHierarchy = new ORefList(diagramFactor.getRef());
		selectedHierarchy.add(diagramFactor.getWrappedORef());
		propertiesPanel.setObjectRefs(selectedHierarchy);
		ModelessDialogWithClose propertiesDialog = new ModelessDialogWithClose(getMainWindow(), propertiesPanel, propertiesPanel.getPanelDescription()); 
		getView().showFloatingPropertiesDialog(propertiesDialog);
	}

	// TODO: The tab should probably be computed elsewhere?
	private int getTabToStartOn(FactorCell factorCell, Point screenPoint)
	{
		if(screenPoint == null)
			return FactorPropertiesPanel.TAB_DETAILS;
		
		Point pointRelativeToCellOrigin = getDiagramComponent().convertScreenPointToCellRelativePoint(screenPoint, factorCell);

		EAM.logVerbose(screenPoint.toString() + "->" + pointRelativeToCellOrigin.toString());
		if(factorCell.isPointInObjective(pointRelativeToCellOrigin))
		{
			EAM.logVerbose("Objective");
			return FactorPropertiesPanel.TAB_OBJECTIVES;
		}
		if (factorCell.isPointInViability(pointRelativeToCellOrigin))
		{
			EAM.logVerbose("ViabilityModeTNC");
			return FactorPropertiesPanel.TAB_VIABILITY;
		}
		if(factorCell.isPointInIndicator(pointRelativeToCellOrigin))
		{
			EAM.logVerbose("Indicator");
			return FactorPropertiesPanel.TAB_INDICATORS;
		}
		if(factorCell.isPointInGoal(pointRelativeToCellOrigin))
		{
			EAM.logVerbose("Goal");
			return FactorPropertiesPanel.TAB_GOALS;
		}
		
		return FactorPropertiesPanel.TAB_DETAILS;
	}
	
	private EAMGraphCell[] getSelectedCellsWithoutGroupBoxCoveredLinks()
	{
		try
		{
			EAMGraphCell[] selectedCells = getDiagramComponent().getOnlySelectedCells();
			HashSet<FactorCell> groupBoxesAndChildren = getDiagramComponent().getOnlySelectedFactorAndGroupChildCells();
			HashSet<LinkCell> linkInsideGroupBox = getDiagramComponent().getAllLinksInsideGroupBox(groupBoxesAndChildren);
			HashSet<EAMGraphCell> selectedFactorsWithoutGroupBoxLinks = new HashSet();
			selectedFactorsWithoutGroupBoxLinks.addAll(Arrays.asList(selectedCells));
			selectedFactorsWithoutGroupBoxLinks.removeAll(linkInsideGroupBox);
			
			return selectedFactorsWithoutGroupBoxLinks.toArray(new FactorCell[0]);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return new EAMGraphCell[0];
		}
	}

	private DiagramComponent getDiagramComponent()
	{
		return getDiagramView().getDiagramComponent();
	}
}
