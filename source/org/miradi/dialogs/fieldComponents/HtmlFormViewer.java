/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.dialogs.fieldComponents;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.StyleSheet;

import org.martus.swing.HyperlinkHandler;
import org.martus.swing.UiEditorPane;
import org.miradi.actions.ActionCopy;
import org.miradi.actions.ActionCut;
import org.miradi.actions.ActionDelete;
import org.miradi.actions.ActionPaste;
import org.miradi.actions.Actions;
import org.miradi.main.EAM;
import org.miradi.main.EAMResourceImageIcon;
import org.miradi.main.MainWindow;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.FontFamiliyQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.utils.HtmlFormEventHandler;
import org.miradi.utils.HtmlUtilities;
import org.miradi.utils.XmlUtilities2;


public class HtmlFormViewer extends UiEditorPane implements HyperlinkListener, MouseListener
{
	public HtmlFormViewer(MainWindow mainWindowToUse, String htmlSource, HyperlinkHandler hyperLinkHandler)
	{
		mainWindow = mainWindowToUse;
		linkHandler = hyperLinkHandler;
		createEditor();

		setEditable(false);
		setText(htmlSource);
		addHyperlinkListener(this);
		addMouseListener(this);
		copyAction = new EditorActionCopy(mainWindow);
		
		setBorder(null);
	}
	
	public int getFontSize()
	{
		return getMainWindow().getWizardFontSize();
	}
	
	public String getFontFamilyCode()
	{
		return getMainWindow().getWizardFontFamily();
	}
	
	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	@Override
	public void setText(String text)
	{
		updateStyleSheet();
		
		super.setText(text);
		setCaretPosition(0);
	}

	public void setTextWithoutScrollingToMakeFieldVisible(String newValue)
	{
		DefaultCaret caret = (DefaultCaret)getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		setText(newValue);
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

	private void updateStyleSheet()
	{
		HTMLEditorKit htmlKit = (HTMLEditorKit)getEditorKit();
		StyleSheet style = htmlKit.getStyleSheet();
		customizeStyleSheet(style);
		htmlKit.setStyleSheet(style);
	}

	private void createEditor()
	{
		HTMLEditorKit htmlKit = new OurHtmlEditorKit(linkHandler);
		Document doc = htmlKit.createDefaultDocument();
		setDocument(doc);
		setEditorKit(htmlKit);
	}

	public static void setFixedWidth( Component component, int width )
	{
		component.setSize( new Dimension( width, Short.MAX_VALUE ) );
		Dimension preferredSize = component.getPreferredSize();
		component.setPreferredSize( new Dimension( width, preferredSize.height ) );
	}
	
	public int getPreferredHeight(int width)
	{
		setSize(new Dimension(width, Short.MAX_VALUE));
		return getPreferredSize().height;
	}
	
	protected void customizeStyleSheet(StyleSheet style)
	{
		addRuleBackground(style);
		addRuleFontFamily(style);
		addRuleFontSize(style);
	}

	protected void addRuleBackground(StyleSheet style)
	{
		style.addRule("body {background: #ffffff;}");
	}

	public void addRuleFontSize(StyleSheet style)
	{
		HtmlUtilities.addRuleFontSize(style, getFont().getSize(), getFontSize());		
	}

	public void addRuleFontFamily(StyleSheet style)
	{
		FontFamiliyQuestion question = (FontFamiliyQuestion)StaticQuestionManager.getQuestion(FontFamiliyQuestion.class);
		ChoiceItem selectedFontFamily = question.findChoiceByCode(getFontFamilyCode());
		HtmlUtilities.addRuleFontFamily(style, question.getFontsString(selectedFontFamily));
	}
	
	public void hyperlinkUpdate(HyperlinkEvent e)
	{
		if(e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
		{
			String clicked = e.getDescription();
			linkHandler.linkClicked(clicked);
		}

	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{	
	}

	public void mousePressed(MouseEvent e)
	{
		if(e.isPopupTrigger())
			fireRightClick(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		if(e.isPopupTrigger())
			fireRightClick(e);
	}
	
	void fireRightClick(MouseEvent e)
	{
		getRightClickMenu(getMainWindow().getActions()).show(this, e.getX(), e.getY());
	}
	
	public JPopupMenu getRightClickMenu(Actions actions)
	{
		JPopupMenu menu = new JPopupMenu();
		
		JMenuItem menuItemCopy = new JMenuItem(copyAction);
		menu.add(menuItemCopy);
		
		JMenuItem menuItemCut = new JMenuItem(actions.get(ActionCut.class));
		menuItemCut.setEnabled(false);
		menu.add(menuItemCut);
		
		JMenuItem menuItemPaste = new JMenuItem(actions.get(ActionPaste.class));
		menuItemPaste.setEnabled(false);
		menu.add(menuItemPaste);
		
		JMenuItem menuItemDelete = new JMenuItem(actions.get(ActionDelete.class));
		menuItemDelete.setEnabled(false);
		menu.add(menuItemDelete);
		
		return menu;
	}
	
	class EditorActionCopy extends ActionCopy
	{
		public EditorActionCopy(MainWindow mainWindow)
		{
			super(mainWindow);
		}
		
		@Override
		public void doAction(EventObject event) throws Exception
		{
			copy();
		}
	}
	

	class OurHtmlEditorKit extends HTMLEditorKit
	{
		public OurHtmlEditorKit(HyperlinkHandler handler)
		{
			factory = new OurHtmlViewFactory(handler);
			ourStyleSheet = new StyleSheet();
			ourStyleSheet.addStyleSheet(super.getStyleSheet());
		}
		
		@Override
		public ViewFactory getViewFactory()
		{
			return factory;
		}

		@Override
		public StyleSheet getStyleSheet()
		{
			return ourStyleSheet;
		}

		@Override
		public void setStyleSheet(StyleSheet s)
		{
			ourStyleSheet = s;
		}

		ViewFactory factory;
		StyleSheet ourStyleSheet;
	}
	
	class OurHtmlViewFactory extends HTMLEditorKit.HTMLFactory
	{
		public OurHtmlViewFactory(HyperlinkHandler handlerToUse)
		{
			handler = handlerToUse;
		}
		
		@Override
		public View create(Element elem)
		{
			if(elem.getName().equals("select"))
			{
				return new OurSelectView(elem, handler);
			}
			if(elem.getName().equals("input"))
			{
				AttributeSet attributes = elem.getAttributes();
				Object typeAttribute = attributes.getAttribute(HTML.Attribute.TYPE);
				if(typeAttribute.equals("submit"))
				{
					return new OurButtonView(elem, handler);
				}
				if(typeAttribute.equals("text"))
				{
					return new OurTextView(elem, handler);
				}
				if(typeAttribute.equals("textarea"))
				{
					return new OurTextView(elem, handler);
				}
				if(typeAttribute.equals("label"))
				{
					return new OurLabelView(elem, handler);
				}
			}
			else if(elem.getName().equals("img"))
			{
				return new OurImageView(elem);
			}
			return super.create(elem);
		}
		
		HyperlinkHandler handler;
	}
	
	class OurButtonView extends FormView
	{
		public OurButtonView(Element elem, HyperlinkHandler handlerToUse)
		{
			super(elem);
			handler = handlerToUse;
		}

		@Override
		protected Component createComponent()
		{
			JButton buttonName = ((JButton)super.createComponent());
			buttonName.setFont(new PanelButton("").getFont());
			return buttonName;
		}
		
		@Override
		protected void submitData(String data)
		{
			String buttonName = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			handler.buttonPressed(buttonName);
		}
		
		HyperlinkHandler handler;
	}
	
	class OurSelectView extends FormView implements ItemListener
	{
		public OurSelectView(Element elem, HyperlinkHandler handlerToUse)
		{
			super(elem);
			handler = handlerToUse;
		}

		@Override
		protected Component createComponent()
		{
			comboBox = new PanelComboBox();
			comboBox.addItemListener(this);
			String fieldName = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			((HtmlFormEventHandler)handler).setComponent(fieldName, comboBox);
			return comboBox;
		}

		public void itemStateChanged(ItemEvent e)
		{
			String name = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			handler.valueChanged(name, comboBox.getSelectedItem().toString());
		}
		
		HyperlinkHandler handler;
		JComboBox comboBox;
	}
	
	
	class OurTextView extends FormView implements DocumentListener
	{
		public OurTextView(Element elem, HyperlinkHandler handlerToUse)
		{
			super(elem);
			handler = handlerToUse;
		}

		@Override
		protected Component createComponent()
		{
			String fieldName = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			textField = (JTextComponent)super.createComponent();
			textField.setFont(new PanelTextArea("").getFont());
			textField.getDocument().addDocumentListener(this);
			((HtmlFormEventHandler)handler).setComponent(fieldName, textField);
			return textField;
		}

		public void changedUpdate(DocumentEvent event) 
		{
			notifyHandler();
		}


		public void insertUpdate(DocumentEvent event) 
		{
			notifyHandler();
		}

		public void removeUpdate(DocumentEvent event) 
		{
			notifyHandler();
		}
		
		private void notifyHandler() 
		{
			String name = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			handler.valueChanged(name, textField.getText());
		}
		
		@Override
		protected void submitData(String data)
		{
		}
		
		HyperlinkHandler handler;
		JTextComponent textField;

	}

	
	class OurLabelView extends FormView
	{
		public OurLabelView(Element elem, HyperlinkHandler handlerToUse)
		{
			super(elem);
			handler = handlerToUse;
		}

		@Override
		protected Component createComponent()
		{
			String fieldName = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			JLabel label = new PanelTitleLabel("");
			label.setBorder(new EmptyBorder(new Insets(0,0,10,0)));
			((HtmlFormEventHandler)handler).setComponent(fieldName, label);
			return label;
		}

		HyperlinkHandler handler;
	}
	
	class OurImageView extends ImageView
	{
		public OurImageView(Element elem)
		{
			super(elem);
			name = (String)elem.getAttributes().getAttribute(HTML.Attribute.SRC);
			if(name == null)
				EAM.logError("Image without name at " + elem.getStartOffset());
		}

		@Override
		public Image getImage()
		{
			if(image == null)
			{
				try
				{
					EAMResourceImageIcon icon = new EAMResourceImageIcon(name);
					image = icon.getImage();
				}
				catch(NullPointerException e)
				{
					EAM.logError("Missing image: " + name);
					throw new RuntimeException(name, e);
				}
			}
			return image;
		}
		
		String name;
		Image image;
	}
	
	MainWindow mainWindow;
	HyperlinkHandler linkHandler;
	EditorActionCopy copyAction;
	
}

