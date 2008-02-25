/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.views.diagram;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogs.base.ObjectPoolTable;
import org.miradi.dialogs.base.ObjectPoolTableModel;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.ViewData;
import org.miradi.project.Project;

abstract public class DiagramPageList extends ObjectPoolTable
{
	public DiagramPageList(MainWindow mainWindowToUse, ObjectPoolTableModel objectPoolTableModel)
	{
		super(objectPoolTableModel, SORTABLE_COLUMN_INDEX);
		project = mainWindowToUse.getProject();
		
		getSelectionModel().addListSelectionListener(new DiagramObjectListSelectionListener(project));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setBorder(BorderFactory.createEtchedBorder());
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		resizeTable(6);
		
		ignoreKeystrokesThatShouldGoToDiagram();
	}

	private void ignoreKeystrokesThatShouldGoToDiagram()
	{
		InputMap im = getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		KeyStroke ctrlA = KeyStroke.getKeyStroke('A', KeyEvent.CTRL_DOWN_MASK);
		im.put(ctrlA, "none");
		KeyStroke ctrlX = KeyStroke.getKeyStroke('X', KeyEvent.CTRL_DOWN_MASK);
		im.put(ctrlX, "none");
		KeyStroke ctrlC = KeyStroke.getKeyStroke('C', KeyEvent.CTRL_DOWN_MASK);
		im.put(ctrlC, "none");
		KeyStroke ctrlV = KeyStroke.getKeyStroke('V', KeyEvent.CTRL_DOWN_MASK);
		im.put(ctrlV, "none");
	}
	
	@Override
	protected void addRowHeightSaver()
	{
		//NOTE: this is called from the base class constructor
		//By not calling super, we are disabling row hieght changing (saving)
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		Dimension preferredScrollableViewportSize = new Dimension(super.getPreferredScrollableViewportSize());
		Dimension preferredTableSize = getPreferredSize();
		preferredScrollableViewportSize.height = Math.max(2 * getRowHeight(), preferredTableSize.height);
		return preferredScrollableViewportSize;
	}
	
	public void listChanged()
	{
		getObjectPoolTableModel().rowsWereAddedOrRemoved();
	}

	private Vector buildCommandsToSetCurrentDiagramObjectRef(ORef selectedRef) throws Exception
	{
		ORef currentDiagramRef = getCurrentDiagramViewDataRef();
		if (currentDiagramRef.equals(selectedRef))
			return new Vector();
	
		Vector commandsVector = new Vector();
		ViewData viewData = project.getDiagramViewData();
		commandsVector.add(new CommandSetObjectData(viewData.getRef(), getCurrentDiagramViewDataTag(), selectedRef));
		return commandsVector;
	}

	public static String getCurrentDiagramViewDataTag(int objectType)
	{
		if (objectType == ObjectType.CONCEPTUAL_MODEL_DIAGRAM)
			return ViewData.TAG_CURRENT_CONCEPTUAL_MODEL_REF;
		
		if (objectType == ObjectType.RESULTS_CHAIN_DIAGRAM)
			return ViewData.TAG_CURRENT_RESULTS_CHAIN_REF;
		
		throw new RuntimeException("Could not find corrent tag for " + objectType);
	}
	
	public String getCurrentDiagramViewDataTag()
	{
		return getCurrentDiagramViewDataTag(getManagedDiagramType());
	}
	
	public ORef getCurrentDiagramViewDataRef() throws Exception
	{
		ViewData viewData = project.getDiagramViewData();
		return getCurrentDiagramViewDataRef(viewData, getManagedDiagramType());
	}
	
	public static ORef getCurrentDiagramViewDataRef(ViewData viewData, int objectType) throws Exception
	{
		String currrentDiagramViewDataTag = getCurrentDiagramViewDataTag(objectType);
		String orefAsJsonString = viewData.getData(currrentDiagramViewDataTag);
		
		return ORef.createFromString(orefAsJsonString);
	}
	
	public class DiagramObjectListSelectionListener  implements ListSelectionListener
	{
		public DiagramObjectListSelectionListener(Project projectToUse)
		{
			project = projectToUse;
		}

		public void valueChanged(ListSelectionEvent event)
		{
			try
			{
				setCurrentDiagram();
			}
			catch(Exception e)
			{
				EAM.panic(e);
			}
		}

		private void setCurrentDiagram() throws Exception
		{
			Vector commandsToExecute = new Vector();
			commandsToExecute.addAll(ensureDefaultMode());
			commandsToExecute.addAll(buildCommandsToSetCurrentDiagramObjectRef(getSelectedRef()));
			
			if (commandsToExecute.size() == 0)
				return;
			
			project.executeCommandsAsTransaction((Command[]) commandsToExecute.toArray(new Command[0]));		
		}

		private Vector ensureDefaultMode() throws Exception
		{
			ViewData viewData = project.getCurrentViewData();
			if (viewData.getData(ViewData.TAG_CURRENT_MODE).equals(ViewData.MODE_DEFAULT))
				return new Vector();
			
			Vector defaultCommandVector = new Vector();
			defaultCommandVector.add(new CommandSetObjectData(viewData.getRef(), ViewData.TAG_CURRENT_MODE, ViewData.MODE_DEFAULT));
			return defaultCommandVector;
		}

		private ORef getSelectedRef()
		{
			if (getSelectedHierarchies().length == 0)
				return ORef.INVALID;
			
			ORefList selectedDiagramObjectRefs = getSelectedHierarchies()[0];
			ORef selectedDiagramObjectRef = selectedDiagramObjectRefs.get(0);
			if (selectedDiagramObjectRef.isInvalid())
				return ORef.INVALID;
			
			return selectedDiagramObjectRef;
		}
		
		private Project project;
	}
	
	public boolean shouldSaveColumnWidth()
	{
		return false;
	}
	
	abstract public boolean isResultsChainPageList();
	
	abstract public boolean isConceptualModelPageList();
	
	abstract public int getManagedDiagramType();
	
	private Project project;

	private static final int SORTABLE_COLUMN_INDEX = 0;
}