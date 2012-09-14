/* 
Copyright 2005-2012, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.objects;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramFactorId;
import org.miradi.ids.DiagramLinkId;
import org.miradi.objectdata.BooleanData;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.schemas.ConceptualModelDiagramSchema;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.ResultsChainDiagramSchema;
import org.miradi.utils.CommandVector;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.PointList;

public class DiagramLink extends BaseObject
{
	public DiagramLink(ObjectManager objectManager, BaseId idToUse) throws Exception
	{
		super(objectManager, new DiagramLinkId(idToUse.asInt()), createSchema());
	}

	public static DiagramLinkSchema createSchema()
	{
		return new DiagramLinkSchema();
	}
	
	@Override
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject json = super.toJson();
		json.putId(TAG_WRAPPED_ID, getBaseIdData(TAG_WRAPPED_ID));
		json.putId(TAG_FROM_DIAGRAM_FACTOR_ID, getBaseIdData(TAG_FROM_DIAGRAM_FACTOR_ID));
		json.putId(TAG_TO_DIAGRAM_FACTOR_ID, getBaseIdData(TAG_TO_DIAGRAM_FACTOR_ID));
		
		return json;
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return new int[] {
			ConceptualModelDiagramSchema.getObjectType(), 
			ResultsChainDiagramSchema.getObjectType()
			};
	}
	
	@Override
	protected ORefList getNonOwnedObjectsToDeepCopy(ORefList deepCopiedFactorRefs)
	{
		ORefList deepObjectRefsToCopy = super.getNonOwnedObjectsToDeepCopy(deepCopiedFactorRefs);
		deepObjectRefsToCopy.addAll(getGroupedDiagramLinkRefs());
		
		return deepObjectRefsToCopy;
	}
	
	public ORef getDiagramFactorRef(int direction)
	{
		if(direction == DiagramLink.FROM)
			return getFromDiagramFactorRef();
		if(direction == DiagramLink.TO)
			return getToDiagramFactorRef();
		throw new RuntimeException("Unknown direction: " + direction);
	}
	
	public ORef getOppositeEndRef(ORef diagramFactorRef)
	{
		if (getFromDiagramFactorRef().equals(diagramFactorRef))
			return getToDiagramFactorRef();
		
		if (getToDiagramFactorRef().equals(diagramFactorRef))
			return getFromDiagramFactorRef();
		
		return ORef.INVALID;
	}
	
	public ORef getOppositeDiagramFactorRef(int direction)
	{
		if(direction == DiagramLink.FROM)
			return getToDiagramFactorRef();
		if(direction == DiagramLink.TO)
			return getFromDiagramFactorRef();
		throw new RuntimeException("Unknown direction: " + direction);
	}
	
	public DiagramFactor getDiagramFactor(int direction)
	{
		return DiagramFactor.find(objectManager, getDiagramFactorRef(direction));
	}
	
	public DiagramFactor getOppositeDiagramFactor(int direction)
	{
		return DiagramFactor.find(objectManager, getOppositeDiagramFactorRef(direction));
	}
	
	public ORef getFromWrappedRef()
	{
		return getFromDiagramFactor().getWrappedORef();
	}
	
	public ORef getFromDiagramFactorRef()
	{
		return new ORef(DiagramFactorSchema.getObjectType(), getFromDiagramFactorId());
	}
	
	public DiagramFactor getFromDiagramFactor()
	{
		return DiagramFactor.find(getProject(), getFromDiagramFactorRef());
	}
	
	public ORef getToWrappedRef()
	{
		return getToDiagramFactor().getWrappedORef();
	}
	
	public ORef getToDiagramFactorRef()
	{
		return new ORef(DiagramFactorSchema.getObjectType(), getToDiagramFactorId());
	}
	
	public DiagramFactor getToDiagramFactor()
	{
		return DiagramFactor.find(getProject(), getToDiagramFactorRef());
	}
	
	public DiagramFactorId getFromDiagramFactorId()
	{
		return new DiagramFactorId(getBaseIdData(TAG_FROM_DIAGRAM_FACTOR_ID).asInt());
	}
	
	public DiagramFactorId getToDiagramFactorId()
	{
		return new DiagramFactorId(getBaseIdData(TAG_TO_DIAGRAM_FACTOR_ID).asInt());
	}

	public DiagramLinkId getDiagramLinkId()
	{
		return (DiagramLinkId)getId(); 
	}
	
	public ORefList getGroupedDiagramLinkRefs()
	{
		return getSafeRefListData(TAG_GROUPED_DIAGRAM_LINK_REFS);
	}
	
	public ORef getWrappedRef()
	{
		return getRefData(TAG_WRAPPED_ID);
	}
	
	public BaseId getWrappedId()
	{
		return getBaseIdData(TAG_WRAPPED_ID);
	}
	
	public DiagramObject getDiagramObject()
	{
		ORefList cmPageRefs = findObjectsThatReferToUs(ConceptualModelDiagramSchema.getObjectType());
		if(cmPageRefs.size() > 0)
			return ConceptualModelDiagram.find(objectManager, cmPageRefs.get(0));

		ORefList rcRefs = findObjectsThatReferToUs(ResultsChainDiagramSchema.getObjectType());
		if(rcRefs.size() > 0)
			return ResultsChainDiagram.find(objectManager, rcRefs.get(0));
		
		return null;
	}
	
	@Override
	public int getAnnotationType(String tag)
	{
		if (tag.equals(TAG_GROUPED_DIAGRAM_LINK_REFS))
			return DiagramLinkSchema.getObjectType();
		
		return super.getAnnotationType(tag);
	}
	
	@Override
	public boolean isRefList(String tag)
	{
		if (tag.equals(TAG_GROUPED_DIAGRAM_LINK_REFS))
			return true;
		
		return super.isRefList(tag);
	}
	
	public boolean alsoLinksOurFromOrTo(DiagramLink otherDiagramLink)
	{
		if (isToOrFrom(otherDiagramLink.getFromDiagramFactorRef()))
			return true;
		
		if (isToOrFrom(otherDiagramLink.getToDiagramFactorRef()))
			return true;
		
		return false;
	}
	
	public boolean isToOrFrom(DiagramFactor diagramFactor)
	{
		return isToOrFrom(diagramFactor.getRef());
	}
	
	public boolean isToOrFrom(ORef diagramFactorRef)
	{
		if (getToDiagramFactorRef().equals(diagramFactorRef))
			return true;
		
		if (getFromDiagramFactorRef().equals(diagramFactorRef))
			return true;
		
		return false;
	}
	
	public boolean isBidirectional()
	{
		return getBooleanData(TAG_IS_BIDIRECTIONAL_LINK);
	}
	
	public FactorLink getWrappedFactorLink()
	{
		return FactorLink.find(getProject(), getWrappedRef());
	}
	
	public boolean bendPointAlreadyExists(Point location)
	{
		if (location == null)
			return false;
		
		return getBendPoints().contains(location);
	}

	public ORefList getSelfOrChildren()
	{
		if (isGroupBoxLink())
			return getGroupedDiagramLinkRefs();
		
		return new ORefList(getRef());
	}
	
	public boolean isGroupBoxLink()
	{
		if (DiagramFactor.find(getProject(), getFromDiagramFactorRef()).isGroupBoxFactor())
			return true;
		
		if (DiagramFactor.find(getProject(), getToDiagramFactorRef()).isGroupBoxFactor())
			return true;
		
		return false;
	}
	
	public String getToolTipString() 
	{
		DiagramFactor fromDiagramFactor = DiagramFactor.find(getProject(), getFromDiagramFactorRef());
		DiagramFactor toDiagramFactor = DiagramFactor.find(getProject(), getToDiagramFactorRef());
		Factor fromFactor = Factor.findFactor(getProject(), fromDiagramFactor.getWrappedORef());
		Factor toFactor = Factor.findFactor(getProject(), toDiagramFactor.getWrappedORef());
		String toolTipText = "<html><b>From : " + fromFactor.getLabel() + "</b><BR>" +
				           		   "<b>To : " + toFactor.getLabel() + "</b>";
		
		return toolTipText;
	}
	
	public PointList getBendPoints()
	{
		return getPointListData(TAG_BEND_POINTS);
	}
	
	public Rectangle getBendPointBounds()
	{
		return getBendPoints().getBounds();
	}
	
	public Color getColor()
	{
		return getColorChoiceItem().getColor();
	}

	public ChoiceItem getColorChoiceItem()
	{
		return getChoiceItemData(TAG_COLOR);
	}
	
	public boolean isCoveredByGroupBoxLink()
	{
		ORefList groupBoxLinks = findObjectsThatReferToUs(DiagramLinkSchema.getObjectType());
		return (groupBoxLinks.size() > 0);
	}
	
	public CommandVector createCommandsToEnableBidirectionalFlag()
	{
		return createCommandsToSetBidirectionalFlag(true);
	}
	
	public CommandVector createCommandsToSetBidirectionalFlag(boolean shouldBeBidirectional)
	{
		CommandVector commands = new CommandVector();
		String newBidirectionalValue = BooleanData.toString(shouldBeBidirectional);
		commands.add(new CommandSetObjectData(getRef(), DiagramLink.TAG_IS_BIDIRECTIONAL_LINK, newBidirectionalValue));
		return commands;
	}

	public static boolean isTo(int direction)
	{
		return direction == TO;
	}

	public static boolean isFrom(int direction)
	{
		return direction == FROM;
	}

	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == DiagramLinkSchema.getObjectType();
	}

	public static DiagramLink find(ObjectManager objectManager, ORef diagramLinkRef)
	{
		return (DiagramLink) objectManager.findObject(diagramLinkRef);
	}
	
	public static DiagramLink find(Project project, ORef diagramLinkRef)
	{
		return find(project.getObjectManager(), diagramLinkRef);
	}
		
	public static final String TAG_WRAPPED_ID = "WrappedLinkId";
	public static final String TAG_FROM_DIAGRAM_FACTOR_ID = "FromDiagramFactorId";
	public static final String TAG_TO_DIAGRAM_FACTOR_ID = "ToDiagramFactorId";
	public static final String TAG_BEND_POINTS = "BendPoints";
	public static final String TAG_GROUPED_DIAGRAM_LINK_REFS = "GroupedDiagramLinkRefs";
	public static final String TAG_COLOR = "Color";
	public static final String TAG_IS_BIDIRECTIONAL_LINK = "IsBidirectionalLink";
	
	public static final int FROM = 1;
	public static final int TO = 2;
	public static final String BIDIRECTIONAL_LINK = BooleanData.BOOLEAN_TRUE;
}
