/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.objects;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Vector;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.cells.FactorDataMap;
import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.objectdata.DimensionData;
import org.conservationmeasures.eam.objectdata.ORefData;
import org.conservationmeasures.eam.objectdata.PointData;
import org.conservationmeasures.eam.objecthelpers.CreateDiagramFactorParameter;
import org.conservationmeasures.eam.objecthelpers.CreateObjectParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ObjectManager;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class DiagramFactor extends BaseObject
{
	public DiagramFactor(ObjectManager objectManager, DiagramFactorId diagramFactorIdToUse, CreateDiagramFactorParameter extraInfo)
	{
		super(objectManager, diagramFactorIdToUse);
		
		clear();
		underlyingObjectRef.set(extraInfo.getFactorRef());
		size.setDimension(getDefaultSize());
	}
	
	public DiagramFactor(DiagramFactorId diagramFactorIdToUse, CreateDiagramFactorParameter extraInfo)
	{
		super(diagramFactorIdToUse);
		
		clear();
		underlyingObjectRef.set(extraInfo.getFactorRef());
		size.setDimension(getDefaultSize());
	}
	
	public DiagramFactor(ObjectManager objectManager, int idToUse, EnhancedJsonObject json) throws Exception
	{
		super(objectManager, new DiagramFactorId(idToUse), json);
		
		ORef wrappedRef = ORef.createFromString(json.getString(TAG_WRAPPED_REF));
		underlyingObjectRef.set(wrappedRef);
	}
	
	public DiagramFactor(int idToUse, EnhancedJsonObject json) throws Exception
	{
		super(new DiagramFactorId(idToUse), json);
		
		ORef wrappedRef = ORef.createFromString(json.getString(TAG_WRAPPED_REF));
		underlyingObjectRef.set(wrappedRef);
	}
	
	public EnhancedJsonObject toJson()
	{
		EnhancedJsonObject jsonObject = super.toJson();
		jsonObject.put(TAG_WRAPPED_REF, underlyingObjectRef.toString());
		
		return jsonObject;
	}
	
	public static Dimension getDefaultSize(int type)
	{
		if (type == ObjectType.TEXT_BOX)
			return new Dimension(180, 30);
		
		return getDefaultSize();
	}
	
	public static Dimension getDefaultSize()
	{
		return new Dimension(120, 60);
	}
	
	public int getType()
	{
		return getObjectType();
	}

	public static int getObjectType()
	{
		return ObjectType.DIAGRAM_FACTOR;
	}
	
	
	public static boolean canOwnThisType(int type)
	{
		return false;
	}
	
	
	public static boolean canReferToThisType(int type)
	{
		switch(type)
		{
			case ObjectType.CAUSE: 
				return true;
			case ObjectType.STRATEGY: 
				return true;
			case ObjectType.TARGET: 
				return true;
			case ObjectType.INTERMEDIATE_RESULT:
				return true;
			case ObjectType.THREAT_REDUCTION_RESULT:
				return true;
				
			default:
				return false;
		}
	}
		
	public ORefList getReferencedObjects(int objectType)
	{
		ORefList list = super.getReferencedObjects(objectType);
		
		switch(objectType)
		{
			case ObjectType.TARGET:
			case ObjectType.STRATEGY:
			case ObjectType.CAUSE:
			case ObjectType.INTERMEDIATE_RESULT:
			case ObjectType.THREAT_REDUCTION_RESULT:
			{
				Factor factor = objectManager.findNode(new FactorId(underlyingObjectRef.getRawRef().getObjectId().asInt()));
				if (factor.getType() == objectType)
					list.add(factor.getRef());
			}
		}
		return list;
	}
	
	public DiagramFactorId getDiagramFactorId()
	{
		return (DiagramFactorId)getId(); 
	}
	
	public int getWrappedType()
	{
		return getWrappedORef().getObjectType();
	}
	
	public FactorId getWrappedId()
	{
		return new FactorId(getWrappedORef().getObjectId().asInt());
	}
	
	public ORef getWrappedORef()
	{
		return underlyingObjectRef.getRawRef();
	}

	public Dimension getSize()
	{
		return size.getDimension();
	}
	
	public Point getLocation()
	{
		return location.getPoint();
	}
	
	public void setLocation(Point pointToUse)
	{
		location.setPoint(pointToUse);
	}
	
	public void setSize(Dimension dimensionToUse)
	{
		size.setDimension(dimensionToUse);
	}
	
	public FactorDataMap createFactorDataMap(String factorType, String factorLabel)
	{
		FactorDataMap dataMap = new FactorDataMap();
		dataMap.putId(TAG_ID, getDiagramFactorId());
		dataMap.putString(TAG_WRAPPED_REF, underlyingObjectRef.get());
		dataMap.putString(TAG_LOCATION, location.get());
		dataMap.putString(TAG_SIZE, size.get());
		
		
		// FIXME: Kevin: This is a crude hack, so we can re-create the node if it gets pasted. (Kevin)
		// Really, for each node copied to the clipboard, we should copy 
		// the json for both the ConceptualModelNode and for the DiagramNode.
		// That will also fix the current bug that objectives and goals are not copied
		dataMap.put(Factor.TAG_NODE_TYPE, factorType);
		dataMap.put(Factor.TAG_LABEL, factorLabel);
		
		return dataMap;
	}

	//FIXME do we still use this method
	public Command[] loadDataFromJson(EnhancedJsonObject json, HashMap newOldRefMap) throws Exception
	{
		Vector commands = new Vector();
		String dimensionAsString = json.getString(DiagramFactor.TAG_SIZE);
		CommandSetObjectData setSizeCommand = new CommandSetObjectData(getRef(), DiagramFactor.TAG_SIZE, dimensionAsString);
		commands.add(setSizeCommand);
		
		String locationAsString = json.getString(DiagramFactor.TAG_LOCATION);
		CommandSetObjectData setLocationCommand = new CommandSetObjectData(getRef(), DiagramFactor.TAG_LOCATION, locationAsString);
		commands.add(setLocationCommand);
		
		return (Command[]) commands.toArray(new Command[0]);
	}
	
	public Command[] createCommandsToMirror(DiagramFactorId newlyCreatedId)
	{
		Vector commands = new Vector();
		String sizeAsString = EnhancedJsonObject.convertFromDimension(getSize());
		CommandSetObjectData setSizeCommand = new CommandSetObjectData(ObjectType.DIAGRAM_FACTOR, newlyCreatedId, DiagramFactor.TAG_SIZE, sizeAsString);
		commands.add(setSizeCommand);
		
		String locationAsString = EnhancedJsonObject.convertFromPoint(getLocation());
		CommandSetObjectData setLocationCommand = new CommandSetObjectData(ObjectType.DIAGRAM_FACTOR, newlyCreatedId, DiagramFactor.TAG_LOCATION, locationAsString);
		commands.add(setLocationCommand);
		
		return (Command[]) commands.toArray(new Command[0]);
	}
	
	public CreateObjectParameter getCreationExtraInfo()
	{
		return new CreateDiagramFactorParameter(getWrappedORef());
	}
	
	void clear()
	{
		super.clear();

		size = new DimensionData();
		location = new PointData();
		underlyingObjectRef = new ORefData();
		
		addField(TAG_SIZE, size);
		addField(TAG_LOCATION, location);
		addField(TAG_WRAPPED_REF, underlyingObjectRef);
	}
	
	public static final String TAG_LOCATION = "Location";
	public static final String TAG_SIZE = "Size";
	public static final String TAG_WRAPPED_REF = "WrappedFactorRef";
	
	private DimensionData size;
	private PointData location;
	private ORefData underlyingObjectRef;
}
