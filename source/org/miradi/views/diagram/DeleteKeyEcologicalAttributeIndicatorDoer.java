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

import org.miradi.commands.Command;
import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.dialogs.viability.KeyEcologicalAttributeNode;
import org.miradi.dialogs.viability.ViabilityIndicatorNode;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.project.Project;
import org.miradi.views.targetviability.doers.AbstractKeyEcologicalAttributeDoer;

public class DeleteKeyEcologicalAttributeIndicatorDoer extends AbstractKeyEcologicalAttributeDoer
{	
	public int getRequiredObjectType()
	{
		return ObjectType.INDICATOR;
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
	
		ViabilityIndicatorNode selectedIndicatorNode = (ViabilityIndicatorNode)getSelectedTreeNodes()[0];
		deleteIndicator(getProject(), selectedIndicatorNode);
	}

	public static void deleteIndicator(Project project, ViabilityIndicatorNode selectedIndicatorNode) throws CommandFailedException
	{

		project.executeCommand(new CommandBeginTransaction());
		try
		{
			Command[] commands = createDeleteCommands(project, selectedIndicatorNode); 
			project.executeCommandsWithoutTransaction(commands);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			throw new CommandFailedException(e);
		}
		finally
		{
			project.executeCommand(new CommandEndTransaction());
		}
	}

	public static Command[] createDeleteCommands(Project project, ViabilityIndicatorNode selectedIndicatorNode) throws Exception
	{
		KeyEcologicalAttributeNode  keaNode = (KeyEcologicalAttributeNode)selectedIndicatorNode.getParentNode();
		BaseObject thisAnnotation = project.findObject(ObjectType.INDICATOR, selectedIndicatorNode.getObject().getId());
		return DeleteIndicator.buildCommandsToAnnotation(project, keaNode.getObject(), KeyEcologicalAttribute.TAG_INDICATOR_IDS, thisAnnotation);
	}
}