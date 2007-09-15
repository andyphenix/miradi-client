/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.util.Arrays;
import java.util.Vector;

import org.conservationmeasures.eam.diagram.DiagramChainObject;
import org.conservationmeasures.eam.diagram.DiagramComponent;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.diagram.cells.LinkCell;
import org.conservationmeasures.eam.dialogs.DiagramPanel;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.DiagramLink;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.views.ViewDoer;

public class SelectChain extends ViewDoer
{
	public boolean isAvailable()
	{
		if (! isDiagramView())
			return false;
		
		FactorCell[] selectedFactors = getDiagramView().getDiagramPanel().getOnlySelectedFactorCells();
		DiagramLink[] selectedLinks = getDiagramView().getDiagramPanel().getOnlySelectedLinks();
		int combinedLengths = selectedLinks.length + selectedFactors.length;
		
		if (combinedLengths == 0)
			return false;
	    
		return true;
	}
	
	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		try
		{
			DiagramView view = (DiagramView)getView();
			DiagramPanel diagram = view.getDiagramPanel();
			selectChainsRelatedToSelectedFactorsAndLinks(diagram);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Unknown error");
		}
	}

	public static void selectChainsRelatedToSelectedFactorsAndLinks(DiagramPanel diagramPanel) throws Exception
	{
		FactorCell[] selectedFactors = diagramPanel.getOnlySelectedFactorCells();
		DiagramLink[] selectedLinks = diagramPanel.getOnlySelectedLinks();
		getChainsBasedOnFactorsAndLinks(diagramPanel.getdiagramComponent(), diagramPanel.getDiagramModel(), selectedFactors, selectedLinks);
	}
	
	public static void getChainsBasedOnFactorsAndLinks(DiagramComponent diagramComponent, DiagramModel model, FactorCell[] factorCells, DiagramLink[] linkageCells) throws Exception
	{
		Factor[] factorReleatedFactors = getChainsBasedOnFactors(model, factorCells);
		Factor[] linkRelatedFactors = getChainsBasedOnLinks(model, linkageCells);
		Vector nodes = new Vector();
		nodes.addAll(Arrays.asList(factorReleatedFactors));
		nodes.addAll(Arrays.asList(linkRelatedFactors));
		selectFactors(diagramComponent, model, (Factor[])nodes.toArray(new Factor[0]));
	}

	private static Factor[] getChainsBasedOnFactors(DiagramModel diagramModel, FactorCell[] factors) throws Exception
	{
		Vector nodes = new Vector();
		for(int i = 0; i < factors.length; ++i)
		{
			FactorCell selectedFactor = factors[i];
			DiagramChainObject chainObject = new DiagramChainObject();
			chainObject.buildNormalChain(diagramModel, selectedFactor.getUnderlyingObject());
			Factor[] chainNodes = chainObject.getFactors().toNodeArray();
			nodes.addAll(Arrays.asList(chainNodes));
		}
		return (Factor[])nodes.toArray(new Factor[0]);
	}
	
	private static Factor[] getChainsBasedOnLinks(DiagramModel diagramModel, DiagramLink[] linkages) throws Exception
	{
		Vector nodes = new Vector();
		for(int i = 0; i < linkages.length; ++i)
		{
			DiagramLink selectedLinkage = linkages[i];
			LinkCell cell = diagramModel.findLinkCell(selectedLinkage);
			
			DiagramChainObject upstreamChain = new DiagramChainObject();
			Factor from = diagramModel.getProject().findNode(cell.getFrom().getWrappedId());
			upstreamChain.buildUpstreamChain(diagramModel, from);
			Factor[] upstreamFactors = upstreamChain.getFactorsArray();
			nodes.addAll(Arrays.asList(upstreamFactors));
			
			DiagramChainObject downstreamChain = new DiagramChainObject();
			Factor to = diagramModel.getProject().findNode(cell.getTo().getWrappedId());
			downstreamChain.buildDownstreamChain(diagramModel, to);
			Factor[] downstreamFactors = downstreamChain.getFactorsArray();
			nodes.addAll(Arrays.asList(downstreamFactors));
		}
		return (Factor[])nodes.toArray(new Factor[0]);
	}

	private static void selectFactors(DiagramComponent diagaramComponent, DiagramModel model, Factor[] chainNodes) throws Exception
	{
		for(int i = 0; i < chainNodes.length; ++i)
		{
			// convert CMNode to DiagramNode
			FactorCell nodeToSelect = model.getFactorCellByWrappedId((FactorId)chainNodes[i].getId());
			diagaramComponent.addSelectionCell(nodeToSelect);
		}
	}
}
