/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.views.diagram.doers;

import java.util.HashSet;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.cells.DiagramGroupBoxCell;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.GroupBox;
import org.miradi.views.diagram.InsertFactorDoer;

public class InsertGroupBoxDoer extends InsertFactorDoer
{
	public boolean isAvailable()
	{
		if (!super.isAvailable())
			return false;
				
		return true;
	}
	
	protected void doExtraSetup(DiagramFactor groupBoxDiagramFactor, FactorCell[] selectedFactorCells) throws Exception
	{
		super.doExtraSetup(groupBoxDiagramFactor, selectedFactorCells);
		if (!containsAllAcceptableDiagramFactors(selectedFactorCells))
			return;
		
		if (containsDifferentType(selectedFactorCells))
		{
			EAM.notifyDialog(EAM.text(GroupBoxAddDiagramFactorDoer.WARNING_TEXT));
			return;
		}
		
		ORefList selectedDiagramFactorRefs = extractDiagramFactorRefs(selectedFactorCells);
		if (GroupBoxAddDiagramFactorDoer.hasOwnedSelectedDiagramFactors(getProject(), selectedDiagramFactorRefs))
			return;
		
		CommandSetObjectData appendCommand = CommandSetObjectData.createAppendORefListCommand(groupBoxDiagramFactor, DiagramFactor.TAG_GROUP_BOX_CHILDREN_REFS, selectedDiagramFactorRefs);
		getProject().executeCommand(appendCommand);
	}
	
	private ORefList extractDiagramFactorRefs(FactorCell[] factorCells)
	{
		ORefList diagramFactorRefs = new ORefList();
		for (int i = 0; i < factorCells.length; ++i)
		{
			diagramFactorRefs.add(factorCells[i].getDiagramFactorRef());
		}
		
		return diagramFactorRefs;
	}
	
	private boolean containsAllAcceptableDiagramFactors(FactorCell[] selectedFactorCells)
	{
		for (int i = 0; i < selectedFactorCells.length; ++i)
		{
			if (!AbstractGroupBoxDoer.isAcceptableFactor(selectedFactorCells[i].getWrappedType()))
					return false;
		}
		
		return true;
	}
	
	public static boolean containsDifferentType(FactorCell[] selectedFactorCells)
	{
		HashSet<Integer> differentTypes = new HashSet();
		for (int i = 0; i < selectedFactorCells.length; ++i)
		{
			int wrappedType = selectedFactorCells[i].getWrappedType();
			if (!GroupBox.is(wrappedType))
				differentTypes.add(wrappedType);
		}
		
		return differentTypes.size() > 1;
	}
	
	public void forceVisibleInLayerManager()
	{
		getProject().getLayerManager().setVisibility(DiagramGroupBoxCell.class, true);
	}

	public String getInitialText()
	{
		return EAM.text("Label|New Group Box");
	}

	public int getTypeToInsert()
	{
		return GroupBox.getObjectType();
	}
}
