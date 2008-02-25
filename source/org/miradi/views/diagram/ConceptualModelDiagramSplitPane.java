/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.views.diagram;

import org.miradi.actions.ActionCreateConceptualModel;
import org.miradi.actions.ActionDeleteConceptualModel;
import org.miradi.actions.ActionDiagramProperties;
import org.miradi.actions.ActionRenameConceptualModel;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ObjectType;

public class ConceptualModelDiagramSplitPane extends DiagramSplitPane
{
	public ConceptualModelDiagramSplitPane(MainWindow mainWindow) throws Exception
	{
		super(mainWindow, ObjectType.CONCEPTUAL_MODEL_DIAGRAM, "ConceptualModelDiagramSplitPane");
	}

	public DiagramLegendPanel createLegendPanel(MainWindow mainWindow)
	{
		return new ConceptualModelDiagramLegendPanel(mainWindow);
	}
	
	public DiagramPageList createPageList(MainWindow mainWindowToUse)
	{
		return new ConceptualModelPageList(mainWindowToUse);
	}
	
	public Class[] getPopUpMenuActions()
	{
		return  new Class[] {
				ActionDiagramProperties.class,
				null,
				ActionCreateConceptualModel.class,
				ActionRenameConceptualModel.class,
				ActionDeleteConceptualModel.class,
		};
	}
}
