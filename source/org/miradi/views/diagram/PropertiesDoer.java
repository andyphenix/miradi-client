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

import javax.swing.Box;

import org.miradi.diagram.DiagramComponent;
import org.miradi.diagram.cells.EAMGraphCell;
import org.miradi.diagram.cells.FactorCell;
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
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.Target;
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
		
		EAMGraphCell[] selected = getDiagramView().getDiagramPanel().getOnlySelectedCells();
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
			EAMGraphCell topCellAtClickPoint = (EAMGraphCell) getDiagramView().getDiagramComponent().getFirstCellForLocation(getLocation().x, getLocation().y);
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
	
	private boolean isTextBoxFactor(DiagramFactor selected)
	{
		if (selected.getWrappedType() == ObjectType.TEXT_BOX)
			return true;
		
		return false;
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
		if (diagramLink.isGroupBoxLink())
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
		GroupBoxLinkListTablePanel tablePanel = new GroupBoxLinkListTablePanel(getProject(), model);
		FactorLinkPropertiesPanel panel = new FactorLinkPropertiesPanel(getMainWindow(), diagramLinkChild.getWrappedRef(), tablePanel.getPicker());
		
		return new GroupBoxLinkManagementPanel(getProject(), getMainWindow(), diagramLink.getRef(), DiagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS, getMainWindow().getActions(), tablePanel, panel);
	}
	
	private FactorLinkPropertiesPanel getFactorLinkPropertiesPanel(DiagramLink diagramLink) throws Exception
	{
		boolean isTargetLink = diagramLink.isTargetLink();
		boolean isStressBasedMode = getProject().getMetadata().getThreatRatingMode().equals(ThreatRatingModeChoiceQuestion.STRESS_BASED_CODE);
		if (!isTargetLink || !isStressBasedMode)
			return new FactorLinkPropertiesPanel(getProject(), diagramLink);
		
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
		
		return new FactorLinkPropertiesPanel(getMainWindow(), diagramLink, picker);
	}
	
	void doFactorProperties(FactorCell selectedFactorCell, Point at) throws CommandFailedException
	{
		int tabToStartOn = getTabToStartOn(selectedFactorCell, at);
		DiagramFactor diagramFactor = selectedFactorCell.getDiagramFactor();
		doFactorProperties(diagramFactor, tabToStartOn);
	}

	void doFactorProperties(DiagramFactor diagramFactor, int tabToStartOn)
	{
		DiagramView view = (DiagramView)getView();
		if (isTextBoxFactor(diagramFactor))
			doTextBoxProperties(diagramFactor);
		else if (diagramFactor.getWrappedType() == ObjectType.GROUP_BOX)
			doGroupBoxProperties(diagramFactor);
		else
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
	
	// TODO: The tab should probably be computed elsewhere?
	private int getTabToStartOn(FactorCell factorCell, Point screenPoint)
	{
		if(screenPoint == null)
			return FactorPropertiesPanel.TAB_DETAILS;
		
		DiagramComponent diagramComponent = getDiagramView().getDiagramComponent();
		Point pointRelativeToCellOrigin = diagramComponent.convertScreenPointToCellRelativePoint(screenPoint, factorCell);

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
}
