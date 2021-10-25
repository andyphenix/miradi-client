/* 
Copyright 2005-2021, Foundations of Success, Bethesda, Maryland
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
package org.miradi.diagram;

import org.martus.swing.UiPopupMenu;
import org.miradi.actions.*;
import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.icons.TaggedObjectSetIcon;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.main.menu.MiradiUIMenu;
import org.miradi.objecthelpers.BaseObjectByFullNameSorter;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objectpools.TaggedObjectSetPool;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.TaggedObjectSet;
import org.miradi.project.DiagramFactorTaggedObjectSetHelper;
import org.miradi.project.Project;
import org.miradi.utils.CommandVector;
import org.miradi.utils.HtmlUtilities;
import org.miradi.utils.LocationHolder;
import org.miradi.utils.MenuItemWithoutLocation;
import org.miradi.views.diagram.DiagramView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Vector;

public class DiagramContextMenuHandler
{
	public DiagramContextMenuHandler(MainWindow  mainWindowToUse, DiagramComponent diagramComponentToUse, Actions actionsToUse)
	{
		mainWindow = mainWindowToUse;
		diagramComponent = diagramComponentToUse;
		actions = actionsToUse;
	}

	private UiPopupMenu createPopupMenu(Point menuInvokedAt)
	{
		UiPopupMenu menu = new UiPopupMenu();

		JMenuItem propMenuItem = createMenuItem(ActionProperties.class, menuInvokedAt);
		propMenuItem.setFont(propMenuItem.getFont().deriveFont(Font.BOLD));
		menu.add(propMenuItem);
		
		menu.addSeparator();
		menu.add(getInsertMenu(menuInvokedAt));
		menu.add(getGroupBoxMenu(menuInvokedAt));
		menu.add(createMenuItem(ActionCreateBendPoint.class, menuInvokedAt));
		
		MainWindowAction objectsAction = actions.getMainWindowAction(ActionDeleteBendPoint.class);
		if (objectsAction.isEnabled())
			menu.add(createMenuItem(ActionDeleteBendPoint.class, menuInvokedAt));
		
		menu.add(createMenuItem(ActionCreateIncomingJunction.class, menuInvokedAt));
		menu.add(createMenuItem(ActionCreateOutgoingJunction.class, menuInvokedAt));

        int selectedFactorCount = diagramComponent.getOnlySelectedFactors().length;
		if(selectedFactorCount == 1)
			menu.add(createMenuItem(ActionManageFactorTagsFromMenu.class, menuInvokedAt));
		if(selectedFactorCount > 0)
		{
			menu.add(createTagFactorsMenu());
			menu.add(createUntagFactorsMenu());
            menu.add(createUntagAllFactorsMenuItem());
		}
		
		menu.addSeparator();
		menu.add(new MenuItemWithoutLocation(actions.get(ActionUndo.class)));
		menu.add(new MenuItemWithoutLocation(actions.get(ActionRedo.class)));
		menu.addSeparator();
		menu.add(new MenuItemWithoutLocation(actions.get(ActionCut.class)));
		menu.add(new MenuItemWithoutLocation(actions.get(ActionCopy.class)));
		menu.add(createMenuItem(ActionPaste.class, menuInvokedAt));
		menu.add(createMenuItem(ActionPasteFactorContent.class, menuInvokedAt));
		menu.add(createMenuItem(ActionPasteWithoutLinks.class, menuInvokedAt));
		menu.addSeparator();
		menu.add(new MenuItemWithoutLocation(actions.get(ActionCopyDiagramFactorFormat.class)));
		menu.add(new MenuItemWithoutLocation(actions.get(ActionPasteDiagramFactorFormat.class)));
		menu.addSeparator();
		menu.add(new MenuItemWithoutLocation(actions.get(ActionDelete.class)));
		menu.add(new MenuItemWithoutLocation(actions.get(ActionSelectAll.class)));
		menu.add(new MenuItemWithoutLocation(actions.get(ActionSelectChain.class)));
        menu.add(new MenuItemWithoutLocation(actions.get(ActionClearAll.class)));
		menu.add(getModeSwitchMenuItem());
		DiagramView diagramView = (DiagramView) mainWindow.getCurrentView();
		menu.add(getDiagramModeSwitchItem(diagramView, actions));
		menu.add(new MenuItemWithoutLocation(actions.get(ActionCreateResultsChain.class)));
		menu.addSeparator();
		menu.add(new MenuItemWithoutLocation(actions.get(ActionDiagramProperties.class)));
		return menu;
	}

	abstract class AbstractActionTagUntagFactor extends AbstractAction
	{
		public AbstractActionTagUntagFactor(boolean isTagActionToUse, ORefSet DiagramFactorRefsToTag, TaggedObjectSet tagSetToApply)
		{
			super(tagSetToApply.getLabel(), new TaggedObjectSetIcon());

			isTagAction = isTagActionToUse;
			diagramFactorTaggedObjectSetHelper = new DiagramFactorTaggedObjectSetHelper(getProject());
			diagramFactorRefs = DiagramFactorRefsToTag;
			tagSet = tagSetToApply;
		}

		public void actionPerformed(ActionEvent event)
		{
			try
			{
				applyTagToDiagramFactors();
			}
			catch(Exception e)
			{
				EAM.logException(e);
				EAM.errorDialog(EAM.text("Unexpected error prevented the tag operation from succeeding"));
			}
		}

		private void applyTagToDiagramFactors() throws Exception
		{
			getProject().executeCommand(new CommandBeginTransaction());
			try
			{
				CommandVector commandsToApplyTag = new CommandVector();
				DiagramFactor[] diagramFactors = getDiagramFactors();

				if (isTagAction)
				{
					for (DiagramFactor diagramFactor : diagramFactors)
					{
						Vector<CommandSetObjectData> commandsToUntagDiagramFactors = diagramFactorTaggedObjectSetHelper.createCommandsToTagDiagramFactor(diagramFactor, getTaggedObjectSet());
						commandsToApplyTag.addAll(commandsToUntagDiagramFactors);
					}
				}
				else
				{
					Vector<CommandSetObjectData> commandsToUntagDiagramFactors = diagramFactorTaggedObjectSetHelper.createCommandsToUntagDiagramFactors(diagramFactors, getTaggedObjectSet());
					commandsToApplyTag.addAll(commandsToUntagDiagramFactors);
				}

				getProject().executeCommands(commandsToApplyTag);
			}
			finally
			{
				getProject().executeCommand(new CommandEndTransaction());
			}
		}

		protected ORefSet getDiagramFactorRefs()
		{
			return diagramFactorRefs;
		}

		protected DiagramFactor[] getDiagramFactors()
		{
			Vector<DiagramFactor> diagramFactors = new Vector<DiagramFactor>();

			for (ORef diagramFactorRef : diagramFactorRefs)
			{
				DiagramFactor diagramFactor = DiagramFactor.find(getProject(), diagramFactorRef);
				diagramFactors.add(diagramFactor);
			}

			return diagramFactors.toArray(new DiagramFactor[0]);
		}

		protected TaggedObjectSet getTaggedObjectSet()
		{
			return tagSet;
		}

		private boolean isTagAction;
		private DiagramFactorTaggedObjectSetHelper diagramFactorTaggedObjectSetHelper;
		private ORefSet diagramFactorRefs;
		private TaggedObjectSet tagSet;
	}
	
	class ActionTagFactors extends AbstractActionTagUntagFactor
	{
		public ActionTagFactors(ORefSet diagramFactorRefsToTag, TaggedObjectSet tagSetToApply)
		{
			super(true, diagramFactorRefsToTag, tagSetToApply);
		}
	}

	class ActionUntagFactors extends AbstractActionTagUntagFactor
	{
		public ActionUntagFactors(ORefSet diagramFactorRefsToTag, TaggedObjectSet tagSetToApply)
		{
			super(false, diagramFactorRefsToTag, tagSetToApply);
		}
	}
	
	interface ActionCreator
	{
		AbstractActionTagUntagFactor createAction(ORefSet DiagramFactorRefs, TaggedObjectSet tag);
	}
	
	class TagActionCreator implements ActionCreator
	{
		public ActionTagFactors createAction(ORefSet DiagramFactorRefs, TaggedObjectSet tag)
		{
			return new ActionTagFactors(DiagramFactorRefs, tag);
		}
	}

	class UntagActionCreator implements ActionCreator
	{
		public ActionUntagFactors createAction(ORefSet DiagramFactorRefs, TaggedObjectSet tag)
		{
			return new ActionUntagFactors(DiagramFactorRefs, tag);
		}
	}

	private MiradiUIMenu createTagFactorsMenu()
	{
		String template = EAM.text("Menu|Add Tag to %n Selected Item(s)");

		TaggedObjectSetPool pool = getProject().getTaggedObjectSetPool();
		Vector<TaggedObjectSet> tags = pool.getAllTaggedObjectSets();

		return createTagUntagFactorsMenu(getDiagramComponent(), template, new TagActionCreator(), tags);
	}

	private MiradiUIMenu createUntagFactorsMenu()
	{
		String template = EAM.text("Menu|Remove Tag from %n Selected Item(s)");

		ORefSet taggedObjectSetRefSet = new ORefSet();
		for (DiagramFactor diagramFactor : getDiagramComponent().getOnlySelectedDiagramFactors())
		{
			taggedObjectSetRefSet.addAllRefs(diagramFactor.getTaggedObjectSetRefs());
		}

		Vector<TaggedObjectSet> tags = new Vector<TaggedObjectSet>();
		for (ORef taggedObjectSetRef : taggedObjectSetRefSet)
		{
			TaggedObjectSet taggedObjectSet = TaggedObjectSet.find(getProject(), taggedObjectSetRef);
			tags.add(taggedObjectSet);
		}

		return createTagUntagFactorsMenu(getDiagramComponent(), template, new UntagActionCreator(), tags);
	}

	private MiradiUIMenu createTagUntagFactorsMenu(DiagramComponent diagramComponent, String template, ActionCreator actionCreator, Vector<TaggedObjectSet> tags)
	{
		ORefSet diagramFactorRefs = new ORefSet(new ORefList(diagramComponent.getOnlySelectedDiagramFactors()));

		String label = EAM.substitute(template, "%n", Integer.toString(diagramFactorRefs.size()));
		MiradiUIMenu menu = new MiradiUIMenu(label);

		Collections.sort(tags, new BaseObjectByFullNameSorter());
		for(TaggedObjectSet set : tags)
		{
			Action action = actionCreator.createAction(diagramFactorRefs, set);
			MenuItemWithoutLocation menuItem = new MenuItemWithoutLocation(action);
			menuItem.setText(HtmlUtilities.wrapInHtmlTags(set.getFullName()));
			menu.add(menuItem);
		}
		
		menu.setEnabled(!tags.isEmpty());
		
		return menu;
	}

	private class ActionUntagAllFactors extends AbstractAction
    {
        public ActionUntagAllFactors(DiagramFactor[] diagramFactorsToUntag)
        {
        	diagramFactorTaggedObjectSetHelper = new DiagramFactorTaggedObjectSetHelper(getProject());
            diagramFactors = diagramFactorsToUntag;
        }

        @Override
        public void actionPerformed(ActionEvent event)
        {
            try
            {
                removeAllTagsFromDiagramFactors();
            }
            catch(Exception e)
            {
                EAM.logException(e);
                EAM.errorDialog(EAM.text("Unexpected error prevented the tag operation from succeeding"));
            }
        }

        private void removeAllTagsFromDiagramFactors() throws Exception
        {
            getProject().executeCommand(new CommandBeginTransaction());
            try
            {
                CommandVector commandsToRemoveTags = new CommandVector();
				ORefSet taggedObjectRefsToRemove = new ORefSet();
				DiagramFactor[] diagramFactors = getDiagramFactors();

				for (DiagramFactor diagramFactor : diagramFactors)
                {
                	taggedObjectRefsToRemove.addAllRefs(diagramFactor.getTaggedObjectSetRefs());
                    commandsToRemoveTags.add(new CommandSetObjectData(diagramFactor, DiagramFactor.TAG_TAGGED_OBJECT_SET_REFS, new ORefList()));
                }

                Vector<CommandSetObjectData> commandsToRemoveSelectedTaggedObjectSetsFromDiagramObjects =
						diagramFactorTaggedObjectSetHelper.createCommandsToRemoveSelectedTaggedObjectSetsFromDiagramObjects(diagramFactors, taggedObjectRefsToRemove);
				commandsToRemoveTags.addAll(commandsToRemoveSelectedTaggedObjectSetsFromDiagramObjects);

                getProject().executeCommands(commandsToRemoveTags);
            }
            finally
            {
                getProject().executeCommand(new CommandEndTransaction());
            }
        }

        protected DiagramFactor[] getDiagramFactors()
        {
            return diagramFactors;
        }

        private DiagramFactorTaggedObjectSetHelper diagramFactorTaggedObjectSetHelper;
        private DiagramFactor[] diagramFactors;
    }

    private JMenuItem createUntagAllFactorsMenuItem()
    {
        String template = EAM.text("Menu|Remove All Tags from %n Selected Item(s)");

        DiagramFactor[] selectedDiagramFactors = getDiagramComponent().getOnlySelectedDiagramFactors();

        String label = EAM.substitute(template, "%n", Integer.toString(selectedDiagramFactors.length));

        Action action = new ActionUntagAllFactors(selectedDiagramFactors);
        MenuItemWithoutLocation menuItem = new MenuItemWithoutLocation(action);
        menuItem.setText(label);
        menuItem.setEnabled(shouldEnableUntagAllFactorsMenuItem(selectedDiagramFactors));

        return  menuItem;
    }

    private boolean shouldEnableUntagAllFactorsMenuItem(DiagramFactor[] selectedDiagramFactors)
    {
        for (DiagramFactor diagramFactor : selectedDiagramFactors)
        {
            if (!diagramFactor.getTaggedObjectSetRefs().isEmpty())
                return true;
        }

        return false;
    }

	private Project getProject()
	{
		return mainWindow.getProject();
	}

	private DiagramComponent getDiagramComponent()
	{
		return diagramComponent;
	}

	private MiradiUIMenu getGroupBoxMenu(Point menuInvokedAt)
	{
		MiradiUIMenu groupBoxMenu = new MiradiUIMenu(EAM.text("Menu|Group Box"));
		groupBoxMenu.add(createMenuItem(ActionInsertGroupBox.class, menuInvokedAt));
		groupBoxMenu.add(createMenuItem(ActionGroupBoxAddFactor.class, menuInvokedAt));
		groupBoxMenu.add(createMenuItem(ActionGroupBoxRemoveFactor.class, menuInvokedAt));
		groupBoxMenu.add(createMenuItem(ActionDeleteGroupBox.class, menuInvokedAt));
		
		return groupBoxMenu;
	}	
	
	private MiradiUIMenu getInsertMenu(Point menuInvokedAt)
	{
		MiradiUIMenu insertMenu = new MiradiUIMenu(EAM.text("Menu|Insert"));

		insertMenu.add(createMenuItem(ActionInsertDraftStrategy.class, menuInvokedAt));
		insertMenu.add(createMenuItem(ActionInsertStrategy.class, menuInvokedAt));
		insertMenu.add(createMenuItem(ActionInsertContributingFactor.class, menuInvokedAt));
		insertMenu.add(createMenuItem(ActionInsertDirectThreat.class, menuInvokedAt));
		if (getProject().getMetadata().isBiophysicalFactorMode())
		    insertMenu.add(createMenuItem(ActionInsertBiophysicalFactor.class, menuInvokedAt));
		insertMenu.add(createMenuItem(ActionInsertTarget.class, menuInvokedAt));
		if (getProject().getMetadata().isHumanWelfareTargetMode())
			insertMenu.add(createMenuItem(ActionInsertHumanWelfareTarget.class, menuInvokedAt));

		insertMenu.add(createMenuItem(ActionInsertIntermediateResult.class, menuInvokedAt));
		insertMenu.add(createMenuItem(ActionInsertThreatReductionResult.class, menuInvokedAt));
        if (getProject().getMetadata().isBiophysicalFactorMode())
		    insertMenu.add(createMenuItem(ActionInsertBiophysicalResult.class, menuInvokedAt));
		insertMenu.add(createMenuItem(ActionInsertTextBox.class, menuInvokedAt));
		insertMenu.add(createMenuItem(ActionInsertScopeBox.class, menuInvokedAt));
		
		insertMenu.addSeparator();
		insertMenu.add(actions.get(ActionInsertLink.class));
		
		return insertMenu;
	}

	private JMenuItem createMenuItem(Class c, Point menuInvokedAt)
	{
		LocationAction action = (LocationAction)actions.get(c);
		action.setInvocationPoint(menuInvokedAt);
		return new ContextMenuItemAtLocation(action);
	}

	public void showContextMenu(MouseEvent e)
	{
		Point pointWithinDiagram = e.getPoint();
		JPopupMenu menu = createPopupMenu(pointWithinDiagram);
		mainWindow.updateActionsAndStatusBar();
		menu.show(diagramComponent, e.getX(), e.getY());
	}
	
	private static MiradiAction getDiagramModeSwitchItem(DiagramView diagramView, Actions actions)
	{
		if (diagramView.isResultsChainTab())
			return actions.get(ActionShowConceptualModel.class);
		
		return actions.get(ActionShowResultsChain.class);
	}
	
	private MenuItemWithoutLocation getModeSwitchMenuItem()
	{
		if (((DiagramView)mainWindow.getCurrentView()).isStrategyBrainstormMode())
			return new MenuItemWithoutLocation(actions.get(ActionShowFullModelMode.class));
		
		return new MenuItemWithoutLocation(actions.get(ActionShowSelectedChainMode.class));
	}
	
	static class ContextMenuItemAtLocation extends JMenuItem implements LocationHolder
	{
		public ContextMenuItemAtLocation(Action action)
		{
			super(action);
		}
		
		public boolean hasLocation()
		{
			return true;
		}
	}

	MainWindow mainWindow;
	DiagramComponent diagramComponent;
	Actions actions;
}
