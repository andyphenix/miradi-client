/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.commands.CommandBeginTransaction;
import org.conservationmeasures.eam.commands.CommandEndTransaction;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.DiagramLink;
import org.conservationmeasures.eam.project.Project;

public class CreateBendPointDoer extends LocationDoer
{
	public boolean isAvailable()
	{
		if(!getProject().isOpen())
			return false;
		
		if (! isDiagramView())
			return false;
		
		DiagramLink[] selectedLinks = getDiagramView().getDiagramPanel().getOnlySelectedLinks();
		if (selectedLinks.length != 1)
			return false;
		
		if (selectedLinks[0].bendPointAlreadyExists(getLocation()))
			return false;
		
		return true;
	}
	
	public void doIt() throws CommandFailedException
	{
		if (!isAvailable())
			return;
		
		Project project = getProject();
		
		DiagramLink selectedLink = getDiagramView().getDiagramPanel().getOnlySelectedLinks()[0];
		BendPointCreator bendPointCreator = new BendPointCreator(getDiagramView().getDiagramComponent());

		// NOTE: We want separate transactions for the initial insert, 
		// and for the creation of points on nearby links
		project.executeCommand(new CommandBeginTransaction());
		try
		{
			bendPointCreator.createBendPoint(getLocation(), selectedLink);
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

		project.executeCommand(new CommandBeginTransaction());
		try
		{
			bendPointCreator.createBendPointOnNearbyLinks(selectedLink, getLocation());
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
	}
}
