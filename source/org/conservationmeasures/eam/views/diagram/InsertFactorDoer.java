/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.awt.Point;
import java.awt.Rectangle;

import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.diagram.DiagramComponent;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.cells.FactorCell;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.project.FactorCommandHelper;
import org.conservationmeasures.eam.project.Project;

abstract public class InsertFactorDoer extends LocationDoer
{
	abstract public int getTypeToInsert();
	abstract public String getInitialText();
	abstract public void forceVisibleInLayerManager();

	public boolean isAvailable()
	{
		if (! getProject().isOpen())
			return false;
		
		if (!isDiagramView())
			return false;
		
		if (getDiagramModel() == null)
			return false;
		
		return true;
	}

	public void doIt() throws CommandFailedException
	{
		if (!isAvailable())
			return;
		
		Project project = getProject();
		FactorCell[] selectedFactors = getDiagramView().getDiagramPanel().getOnlySelectedFactorCells();
		DiagramFactor diagramFactor = null;
		project.executeCommand(new CommandBeginTransaction());
		try
		{
			diagramFactor = insertFactorItself();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
		finally 
		{
			project.executeCommand(new CommandEndTransaction());
		}

		try
		{
			FactorId id = diagramFactor.getWrappedId();
			if((selectedFactors.length > 0) && (getTypeToInsert()!= ObjectType.TARGET))
			{
				// NOTE: Set up a second transaction, so the link creation is independently undoable
				project.executeCommand(new CommandBeginTransaction());
				try
				{
					linkToPreviouslySelectedFactors(diagramFactor, selectedFactors);
				}
				finally
				{
					project.executeCommand(new CommandEndTransaction());
				}
			}
			else
			{
				notLinkingToAnyFactors();
			}

			selectNewFactor(id);
			launchPropertiesEditor(diagramFactor);
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
	}
	
	protected void selectNewFactor(FactorId idToUse)
	{
		getDiagramView().getDiagramPanel().selectFactor(idToUse);
	}
	
	void launchPropertiesEditor(DiagramFactor diagramFactor) throws Exception, CommandFailedException
	{
		getDiagramView().getPropertiesDoer().doFactorProperties(diagramFactor, 0);
	}

	private DiagramFactor insertFactorItself() throws Exception
	{
		Point createAt = getLocation();
		Project project = getProject();
		int factorType = getTypeToInsert();
		FactorCell[] selectedNodes = getDiagramView().getDiagramPanel().getOnlySelectedFactorCells();
		Point deltaPoint = getDeltaPoint(createAt, selectedNodes, factorType, DiagramFactor.getDefaultSize(factorType).width);
		Point snappedPoint  = project.getSnapped(deltaPoint);
		
		FactorCommandHelper factorCommandHelper = new FactorCommandHelper(project, getDiagramModel());
		CommandCreateObject createCommand = factorCommandHelper.createFactorAndDiagramFactor(factorType, snappedPoint, DiagramFactor.getDefaultSize(factorType), getInitialText());
		DiagramFactorId id = (DiagramFactorId) createCommand.getCreatedId();
				
		DiagramFactor diagramFactor = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, id);
		FactorId factorId = diagramFactor.getWrappedId();
		doExtraSetup(factorId);

		forceVisibleInLayerManager();
		getDiagramView().updateVisibilityOfFactorsAndClearSelectionModel();
		
		return diagramFactor;
	}
	
	private Point getDeltaPoint(Point createAt, FactorCell[] selectedFactors, int factorType, int factorWidth) throws Exception
	{
		if (createAt != null)
			return createAt;
		
		if (factorType == ObjectType.TARGET)
			return getTargetLocation(getDiagramModel(), getDiagramVisibleRect(), factorWidth);
		
		return getNonTargetDeltaPoint(selectedFactors, factorType, factorWidth);
	}
	
	private Point getNonTargetDeltaPoint(FactorCell[] selectedFactors, int factorType, int factorWidth)
	{
		if (selectedFactors.length > 0 && !(factorType == ObjectType.TARGET))
			return getLocationSelectedNonTargetNode(selectedFactors, factorWidth);
		
		return getCenterLocation(getDiagramVisibleRect());
	}
	
	private Rectangle getDiagramVisibleRect()
	{
		DiagramComponent diagramComponent = getDiagramView().getDiagramComponent();
		Rectangle visibleRectangle = diagramComponent.getVisibleRect();
		return visibleRectangle;
	}
	
	public Point getCenterLocation(Rectangle visibleRectangle)
	{
		Point deltaPoint = new Point();
		int centeredWidth = visibleRectangle.width / 2;
		int centeredHeight = visibleRectangle.height / 2;
		
		deltaPoint.x = visibleRectangle.x + centeredWidth;
		deltaPoint.y = visibleRectangle.y + centeredHeight;
		
		return deltaPoint;
	}
	
	public Point getTargetLocation(DiagramModel diagramModel, Rectangle visibleRectangle, int factorWidth) throws Exception
	{
		Point deltaPoint = new Point();
		FactorCell[] allTargets = diagramModel.getAllDiagramTargets();

		if (allTargets.length == 0)
		{
			deltaPoint.x = visibleRectangle.width - TARGET_RIGHT_SPACING - factorWidth;
			deltaPoint.y = TARGET_TOP_LOCATION;
		}
		else
		{
			int highestYIndex = 0;
			int highestY = 0;
			
			for (int i = 0; i < allTargets.length; i++)
			{
				double y = allTargets[i].getBounds().getY();
				if (highestY < y)
				{
					highestY = (int) y;
					highestYIndex = i;
				}
			}
			
			FactorCell targetCell = allTargets[highestYIndex];
			deltaPoint.x = (int)targetCell.getBounds().getX();
			deltaPoint.y = highestY + (int)targetCell.getBounds().getHeight() + TARGET_BETWEEN_SPACING;
		}
		
		return deltaPoint;
	}
	
	public Point getLocationSelectedNonTargetNode(FactorCell[] selectedNodes, int nodeWidth)
	{
		Point nodeLocation = selectedNodes[0].getLocation();
		int x = Math.max(0, nodeLocation.x - DEFAULT_MOVE - nodeWidth);
		
		return new Point(x, nodeLocation.y);
	}
	
	protected void linkToPreviouslySelectedFactors(DiagramFactor newlyInserted, FactorCell[] nodesToLinkTo) throws Exception
	{
		if (! linkableType(newlyInserted.getWrappedType()))
			return;
		
		if (! containsLikableType(nodesToLinkTo))
			return;
		
		for(int i = 0; i < nodesToLinkTo.length; ++i)
		{
			DiagramFactor toDiagramFactor = nodesToLinkTo[i].getDiagramFactor();
			LinkCreator linkCreator = new LinkCreator(getProject());
			linkCreator.createFactorLinkAndAddToDiagramUsingCommands(getDiagramModel(), newlyInserted, toDiagramFactor);
		}
	}

	private boolean containsLikableType(FactorCell[] nodesToLinkTo)
	{
		for (int i = 0; i < nodesToLinkTo.length; ++i)
		{
			if (!linkableType(nodesToLinkTo[i].getWrappedType()))
					return false;
		}
		return true;
	}
	
	private boolean linkableType(int type)
	{
		if (type == ObjectType.TEXT_BOX)
			return false;
		
		return true; 
	}
	
	protected void notLinkingToAnyFactors() throws CommandFailedException
	{
	}

	protected void doExtraSetup(FactorId id) throws CommandFailedException
	{
	}
	
	private DiagramModel getDiagramModel()
	{
		return getDiagramView().getDiagramModel();
	}
	
	public static final int TARGET_TOP_LOCATION = 150;
	public static final int TARGET_BETWEEN_SPACING = 20;
	public static final int TARGET_RIGHT_SPACING = 10;
	public static final int DEFAULT_MOVE = 150;
}
