/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import java.util.Vector;

import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.DiagramObject;
import org.conservationmeasures.eam.project.Project;

public class EntireDiagrams
{
	public static DiagramFactor[] getAllDiagramFactorsInAllDiagrams(Project project)
	{
		ORefList allDiagramRefs = getAllDiagramRefs(project);
		Vector diagramFactors = new Vector();
		for (int i = 0; i < allDiagramRefs.size(); ++i)
		{
			DiagramObject diagramObject = (DiagramObject) project.findObject(allDiagramRefs.get(i));
			diagramFactors.addAll(getAllDiagramFactors(project, diagramObject));
		}
		
		return (DiagramFactor[]) diagramFactors.toArray(new DiagramFactor[0]);
	}

	private static ORefList getAllDiagramRefs(Project project)
	{
		ORefList allDiagramRefs = new ORefList();
		allDiagramRefs.addAll(project.getConceptualModelDiagramPool().getORefList());
		allDiagramRefs.addAll(project.getResultsChainDiagramPool().getORefList());

		return allDiagramRefs;
	}
	
	public static Vector getAllDiagramFactors(Project project, DiagramObject diagramObject)
	{
		Vector diagramFactors = new Vector();
		ORefList diagramFactorRefs = diagramObject.getAllDiagramFactorRefs();
		for (int i = 0; i < diagramFactorRefs.size(); ++i)
		{
			DiagramFactor diagramFactor = (DiagramFactor) project.findObject(diagramFactorRefs.get(i));
			diagramFactors.add(diagramFactor);
		}
		
		return diagramFactors;
	}
}
