/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.objectpools;

import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramContentsId;
import org.miradi.ids.IdAssigner;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.schemas.BaseObjectSchema;

public class ConceptualModelDiagramPool extends DiagramObjectPool
{
	public ConceptualModelDiagramPool(IdAssigner idAssignerToUse)
	{
		super(idAssignerToUse, ObjectType.CONCEPTUAL_MODEL_DIAGRAM);
	}
	
	public void put(ConceptualModelDiagram conceptualModelDiagram) throws Exception
	{
		put(conceptualModelDiagram.getId(), conceptualModelDiagram);
	}
	
	public ConceptualModelDiagramPool find(BaseId id)
	{
		return (ConceptualModelDiagramPool)getRawObject(id);
	}
	
	@Override
	BaseObject createRawObject(ObjectManager objectManager, BaseId actualId) throws Exception
	{
		return new ConceptualModelDiagram(objectManager ,new DiagramContentsId(actualId.asInt()));
	}
	
	@Override
	public BaseObjectSchema createBaseObjectSchema(Project projectToUse)
	{
		return ConceptualModelDiagram.createSchema();
	}
}
