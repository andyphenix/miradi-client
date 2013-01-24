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

package org.miradi.utils;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.WysiwygHTMLEditorKit;

import org.gnu.classpath.javax.swing.text.html.GnuHtmlWriter;
import org.miradi.dialogfields.DocumentEventHandler;
import org.miradi.dialogfields.ObjectScrollingMultilineInputField;
import org.miradi.dialogs.base.AbstractObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objectdata.AbstractUserTextDataWithHtmlFormatting;

abstract public class AbstractHtmlPane extends MiradiTextPane
{
	public AbstractHtmlPane(MainWindow mainWindow)
	{
		this(mainWindow, AbstractObjectDataInputPanel.DEFAULT_TEXT_COLUM_COUNT, ObjectScrollingMultilineInputField.INITIAL_MULTI_LINE_TEXT_AREA_ROW_COUNT);
	}

	public AbstractHtmlPane(MainWindow mainWindow, int fixedApproximateColumnCount, int initialApproximateRowCount)
	{
		super(mainWindow, fixedApproximateColumnCount, initialApproximateRowCount);

		handler = new HyperlinkHandler();
		final HTMLEditorKitWithCustomLinkController htmlEditorKit = new HTMLEditorKitWithCustomLinkController();
		setEditorKitForContentType(htmlEditorKit.getContentType(), htmlEditorKit);
		setContentType(htmlEditorKit.getContentType()); 
		initializeEditorComponent();
		addHyperlinkListener(new HyperlinkOpenHandler());
		updateStyleSheet();
	}

	protected void initializeEditorComponent()
	{
		insertHtml("<div></div>", 0);
	}

	private void insertHtml(String html, int location) 
	{       
		try 
		{
			// NOTE: The Java HTML parser compresses all whitespace to a single space
			// (http://java.sun.com/products/jfc/tsc/articles/bookmarks/)
			HTMLEditorKit htmlEditorKit = (HTMLEditorKit) getEditorKit();
			Document document = getDocument();
			final String jEditorPaneizeHtml = HTMLUtils.jEditorPaneizeHTML(html);
			StringReader stringReader = new StringReader(jEditorPaneizeHtml);
			htmlEditorKit.read(stringReader, document, location);
		} 
		catch (Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
		}
	}

	public void setSaverListener(DocumentEventHandler saverListenerToUse)
	{
		saverListener = saverListenerToUse;
	}

	private DocumentEventHandler getSaverListener()
	{
		return saverListener;
	}

	@Override
	public void setText(String text)
	{
		clearDocumentToAvoidFormattingLeak();
		updateStyleSheet();
		String topText = HtmlUtilities.removeAllExcept(text, AbstractUserTextDataWithHtmlFormatting.getAllowedHtmlTags());
		// NOTE: Shef does not encode/decode apostrophes as we need for proper XML
		topText = XmlUtilities2.convertXmlTextToHtmlWithoutHtmlTags(topText);
		insertHtml(topText, 0);            
		CompoundUndoManager.discardAllEdits(getDocument());
	}

	private void clearDocumentToAvoidFormattingLeak()
	{
		//NOTE: per java instructions found here:
		//http://docs.oracle.com/javase/1.5.0/docs/api/javax/swing/JEditorPane.html#setText%28java.lang.String%29
		//we are creating a new Document to avoid any cross document leaks.
		HTMLEditorKit htmlEditorKit = (HTMLEditorKit) getEditorKit();
		final Document replacementDocument = htmlEditorKit.createDefaultDocument();

		if (getSaverListener() != null)
			getSaverListener().stopSaverListener();

		setDocument(replacementDocument);
		if (getSaverListener() != null)
			getSaverListener().startSaverListener();
	}

	@Override
	public String getText()
	{
		String text = super.getText();

		String normalizedAndSanitizedHtmlText = stripEntireHeadAndStyle(text);
		normalizedAndSanitizedHtmlText = getNormalizedAndSanitizedHtmlText(normalizedAndSanitizedHtmlText);

		return normalizedAndSanitizedHtmlText;
	}

	public static String getNormalizedAndSanitizedHtmlText(final String text)
	{
		return HtmlUtilities.getNormalizedAndSanitizedHtmlText(text, AbstractUserTextDataWithHtmlFormatting.getAllowedHtmlTags());
	}

	private void updateStyleSheet()
	{
		HTMLEditorKit htmlKit = (HTMLEditorKit)getEditorKit();
		StyleSheet style = htmlKit.getStyleSheet();
		customizeStyleSheet(style);
		htmlKit.setStyleSheet(style);
	}

	public void handleOpenLink(Point mousePosition)
	{
		handler.activateHyperlink(this, mousePosition);
	}

	public static String stripEntireHeadAndStyle(String htmlText)
	{
		return HtmlUtilities.removeStartToEndTagAndItsContent(htmlText, new String[]{"style", "head"});
	}

	abstract protected void customizeStyleSheet(StyleSheet style);

	private class HyperlinkOpenHandler implements HyperlinkListener 
	{
		public void hyperlinkUpdate(HyperlinkEvent e) 
		{
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) 
			{
				final URL url = e.getURL();
				if (url != null)
				{
					getMainWindow().mainLinkFunction(url.toString());
				}
			}
		}
	}

	public class HtmlEditorKitWithNonSharedStyleSheet extends WysiwygHTMLEditorKit
	{	
		@Override
		public StyleSheet getStyleSheet()
		{
			if (styleSheet == null)
			{
				styleSheet = new StyleSheet();
				styleSheet.addStyleSheet(super.getStyleSheet());
			}

			return styleSheet;
		}

		@Override
		public void setStyleSheet(StyleSheet styleSheetToUse)
		{
			styleSheet = styleSheetToUse;
		}

		private StyleSheet styleSheet;
	}

	public class HTMLEditorKitWithCtrlVFixed extends HtmlEditorKitWithNonSharedStyleSheet
	{
		@Override
		public void install(JEditorPane ed)
		{
			super.install(ed);
			removeCtrlVHandlerThatDoesTheWrongThing();
		}

		// NOTE: We are not sure why, but Shef installs a Ctrl-V binding 
		// that maps to its PasteAction class, which throws away any 
		// formatting on the clipboard and pastes plain text. Our default 
		// paste handler calls editor.paste which does the right thing
		private void removeCtrlVHandlerThatDoesTheWrongThing()
		{
			InputMap inputMap = getInputMap();
			if(inputMap == null)
				return;

			ActionMap actionMap = getActionMap();
			if(actionMap == null)
				return;

			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
			Object binding = inputMap.get(ks);
			if(binding == null)
				return;

			Action action = actionMap.get(binding);
			if(action == null)
				return;

			inputMap.put(ks, null);
			actionMap.put(binding, null);
		}
	}

	public class HTMLEditorKitWithCustomLinkController extends HTMLEditorKitWithCtrlVFixed 
	{
		@Override
		public void install(JEditorPane editorPane) 
		{
			MouseListener[] oldMouseListeners = editorPane.getMouseListeners();
			MouseMotionListener[] oldMouseMotionListeners = editorPane.getMouseMotionListeners();

			super.install(editorPane);
			removeMouseAndMotionListeners(editorPane);
			restoreMouseAndMotionListeners(editorPane, oldMouseListeners,	oldMouseMotionListeners);

			editorPane.addMouseListener(handler);
			editorPane.addMouseMotionListener(handler);
		}

		private void removeMouseAndMotionListeners(JEditorPane editorPane)
		{
			for (MouseListener l: editorPane.getMouseListeners()) 
			{
				editorPane.removeMouseListener(l);
			}

			for (MouseMotionListener l: editorPane.getMouseMotionListeners()) 
			{
				editorPane.removeMouseMotionListener(l);
			}
		}

		private void restoreMouseAndMotionListeners(JEditorPane editorPane, MouseListener[] oldMouseListeners, MouseMotionListener[] oldMouseMotionListeners)
		{
			for (MouseListener l: oldMouseListeners) 
			{
				editorPane.addMouseListener(l);
			}

			for (MouseMotionListener l: oldMouseMotionListeners) 
			{
				editorPane.addMouseMotionListener(l);
			}
		}

		@Override
		public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException
		{
			GnuHtmlWriter w = new HtmlWriterWithoutIndenting(out, (HTMLDocument)doc, pos, len);
			w.write();
		}
	}

	private class HtmlWriterThatFixesIllegalNesting extends GnuHtmlWriter
	{
		public HtmlWriterThatFixesIllegalNesting(Writer out, HTMLDocument doc, int pos, int len)
		{
			super(out, doc, pos, len);
		}

		@Override
		protected void closeOutUnwantedEmbeddedTags(AttributeSet attrSet) throws IOException
		{
			AttributeSet forceAllTagsToBeClosed = new SimpleAttributeSet();
			super.closeOutUnwantedEmbeddedTags(forceAllTagsToBeClosed);
		}
	}

	private class HtmlWriterWithoutIndenting extends HtmlWriterThatFixesIllegalNesting
	{
		public HtmlWriterWithoutIndenting(Writer out, HTMLDocument doc, int pos, int len)
		{
			super(out, doc, pos, len);
		}

		@Override
		protected void indent() throws IOException
		{
			// never indent
		}

		@Override
		protected void writeLineSeparator() throws IOException
		{
			// never write newlines
		}

		@Override
		protected void setCanWrapLines(boolean newValue)
		{
			// never wrap
			super.setCanWrapLines(false);
		}
	}

	private HyperlinkHandler handler;
	private DocumentEventHandler saverListener;

}
