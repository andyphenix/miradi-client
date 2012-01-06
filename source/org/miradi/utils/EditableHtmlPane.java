/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

import java.io.StringReader;

import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.WysiwygHTMLEditorKit;

import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

public class EditableHtmlPane extends MiradiTextPane
{
	public EditableHtmlPane(MainWindow mainWindow, int fixedApproximateColumnCount, int initialApproximateRowCount) throws Exception
	{
		super(mainWindow, fixedApproximateColumnCount, initialApproximateRowCount);
		
		setEditorKitForContentType("text/html", new WysiwygHTMLEditorKit());
		setContentType("text/html"); 
		insertHtml("", 0);        
	}

	private void insertHtml(String html, int location) 
	{       
		try 
		{
			HTMLEditorKit htmlEditorKit = (HTMLEditorKit) getEditorKit();
			Document document = getDocument();
			final String jEditorPaneizeHtml = HTMLUtils.jEditorPaneizeHTML(html);
			StringReader stringReader = new StringReader(jEditorPaneizeHtml);
			htmlEditorKit.read(stringReader, document, location);
		} 
		catch (Exception e)
		{
			EAM.unexpectedErrorDialog(e);
			EAM.logException(e);
		}
	}

	@Override
	public void setText(String text)
	{
		String topText = removeInvalidTags(text);
		super.setText("");
		insertHtml(topText, 0);            
		CompoundUndoManager.discardAllEdits(getDocument());
	}

	@Override
	public String getText()
	{
		return removeInvalidTags(super.getText());          
	}

	private String removeInvalidTags(String html)
	{
		final String INVALID_TAGS[] = {"html", "head", "body", "title"};
		for(int i = 0; i < INVALID_TAGS.length; i++)
		{
			html = deleteOccurance(html, '<' + INVALID_TAGS[i] + '>');
			html = deleteOccurance(html, "</" + INVALID_TAGS[i] + '>');
		}

		return html.trim();
	}

	private String deleteOccurance(String text, String word)
	{
		StringBuffer stringBuffer = new StringBuffer(text);       
		int index;
		while((index = stringBuffer.toString().toLowerCase().indexOf(word.toLowerCase())) != -1)
		{           
			stringBuffer.delete(index, index + word.length());            
		}
		
		return stringBuffer.toString();
	}
}
