/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.rtf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;

import org.martus.util.UnicodeWriter;
import org.miradi.utils.AbstractTableExporter;
import org.miradi.utils.BufferedImageFactory;
import org.miradi.views.umbrella.SaveImageJPEGDoer;

public class RtfWriter
{
	public RtfWriter(File file) throws IOException
	{
		writer = new UnicodeWriter(file);
		rtfStyleManager = new RtfStyleManager();
	}
	
	public void close() throws Exception
	{
		getWriter().close();
	}

	public void writelnEncoded(String data) throws Exception
	{
		writelnRaw(encode(data));
	}
	
	public void writeEncoded(String data) throws Exception
	{
		writeRaw(encode(data));
	}
	
	public void writeRtfCommand(String rtfComand) throws Exception
	{
		writelnRaw(rtfComand);
	}
	
	public void writelnRaw(String textToWrite) throws Exception
	{
		getWriter().writeln(textToWrite);
	}
	
	public void writeRaw(String textToWrite) throws Exception
	{
		getWriter().write(textToWrite);
	}
	
	public void writeImage(BufferedImage bufferedImage) throws Exception
	{
		startBlock();

		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();
		String scaleRetainingAspectRatio = getScale(imageWidth, imageHeight);
		String jpegHeader = "\\pict\\picscalex" + scaleRetainingAspectRatio + "\\picscaley" + scaleRetainingAspectRatio + 
				"\\piccropl0\\piccropr0\\piccropt0\\piccropb0\\jpegblip ";
		getWriter().writeln(jpegHeader);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SaveImageJPEGDoer.saveJpeg(baos, bufferedImage);	
		byte[] imageAsBytes = baos.toByteArray();
		baos.close();
		final int BYTES_PER_LINE = 32;
		for (int i = 0; i < imageAsBytes.length; ++i)
		{
			byte b = imageAsBytes[i];
			getWriter().write(toHex(b));
			
			if(i % BYTES_PER_LINE == 0)
				  getWriter().writeln();
		}
		
		endBlock();
	}

	private String getScale(int imageWidth, int imageHeight)
	{
		return Integer.toString(calculateScale(imageWidth, imageHeight));
	}
	
	public static int calculateScale(int imageWidth, int imageHeight)
	{
		// NOTE: RTF inches are (pixels * 1.04) / 100 
		// so 11x8 inches minus margins and diagram title is about 800x600
		final int MAX_WIDTH = 800;
		final int MAX_HEIGHT = 600;
		double rawXScale = calculateSingleLenghtScale(imageWidth, MAX_WIDTH);
		double rawYScale = calculateSingleLenghtScale(imageHeight, MAX_HEIGHT);
		
		double scalePreservingAspectRatio = Math.min(rawXScale, rawYScale);
		
		return (int)scalePreservingAspectRatio;
	}

	private static double calculateSingleLenghtScale(int lenght, int maxLength)
	{
		final int FULL_SCALE = 100;
		
		if (lenght < maxLength)
			return FULL_SCALE;
		
		int scale = maxLength * 100 / lenght;
		return scale;
	}

	public void startBlock() throws Exception
	{
		writeRaw(START_BLOCK);
	}
	
	public void endBlock() throws Exception
	{
		writeRaw(END_BLOCK);
	}
	
	public void endBlockLn() throws Exception
	{
		writelnRaw(END_BLOCK);
	}

	static public String toHex(byte b)
	{
		int i = b & 0xFF;
		int firstNibble = i/16;
		String firstDigit = Integer.toHexString(firstNibble);
		
		int secondNibble = i%16;
		String secondDigit = Integer.toHexString(secondNibble);
		
		return firstDigit + secondDigit;
	}
	
	public void writeRtfTable(AbstractTableExporter exportableTable) throws Exception
	{
		writeTableHeader(exportableTable);
		writeTableBody(exportableTable);
		pageBreak();
	}

	private void writeTableBody(AbstractTableExporter exportableTable) throws Exception
	{
		for (int row = 0; row < exportableTable.getRowCount(); ++row)
		{
			writeRowData(exportableTable, row);
		}
	}

	private void writeRowData(AbstractTableExporter exportableTable, int row) throws Exception
	{
		writeRtfCommand(ROW_HEADER);
		writeCellCommands(exportableTable);
		for (int column = 0; column < exportableTable.getColumnCount(); ++column)
		{
			writeRaw(PRE_CELL_COMMAND);
			String cellStyleTag = exportableTable.getStyleTagAt(row, column);
			String styleFormattingCommand = getRtfStyleManager().getStyleFormatingCommand(cellStyleTag);
			writeRaw(styleFormattingCommand);
		
			Icon cellIcon = exportableTable.getIconAt(row, column);
			if (cellIcon != null)
			{
				int paddingCount = exportableTable.getDepth(row, column);
				insertIndents(paddingCount);
				writeImage(BufferedImageFactory.getImage(cellIcon));
				insertTab();
			}
			
			writeEncoded(exportableTable.getTextAt(row, column));
			
			writeRaw(CELL_COMMAND);
			newLine();
		}
		
		writeRaw(ROW_COMMAND);
		newLine();
		newLine();
	}

	private void writeCellCommands(AbstractTableExporter exportableTable) throws Exception
	{
		for (int column = 0; column < exportableTable.getColumnCount(); ++column)
		{
			writelnRaw(createCellxCommand(column));	
		}
	}

	public String createCellxCommand(final int column)
	{
		return CELL_BORDER + CELL_X_COMMAND + ((column  + 1) * ONE_INCH_IN_TWIPS );
	}

	private void writeTableHeader(AbstractTableExporter exportableTable) throws Exception
	{
		writelnRaw(TABLE_ROW_HEADER);
		writeCellCommands(exportableTable);
		String styleFormattingCommand = getRtfStyleManager().getStyleFormatingCommand(RtfStyleManager.COLUMN_HEADER_STYLE_TAG);
		for (int columnIndex = 0; columnIndex < exportableTable.getColumnCount(); ++columnIndex)
		{
			String header = exportableTable.getHeaderFor(columnIndex);
			writeRaw(PRE_TABLE_HEADER_CELL_COMMAND);
			writeRaw(styleFormattingCommand);
			startBlock();
			writeRaw(PRE_TABLE_HEADER_CELL_DATA_COMMAND);
			writeEncoded(header);
			endBlock();
			
			writeRaw(CELL_COMMAND);
			newLine();
		}
		
		writeRaw(ROW_COMMAND);
		newLine();
		newLine();
	}
	
	public static String encode(String stringToEncode)
	{	
		String encodedString = stringToEncode.replaceAll("\\\\", "\\\\\\\\");
		encodedString = encodedString.replaceAll("\\}", "\\\\}");
		encodedString = encodedString.replaceAll("\\{", "\\\\{");
		
		StringBuffer buffer = new StringBuffer(encodedString);
		for(int i = 0; i < buffer.length(); ++i)
		{
			char c = buffer.charAt(i);
			if (c >= 128)
			{
				String decimalValue = toDecimal(c);
				buffer.replace(i, i+1, "\\u" + decimalValue.toUpperCase());
			}
		}
		
		return buffer.toString();
	}
	
	static public String toDecimal(char c)
	{
		return Integer.toString(c);
	}
	
	public void insertIndents(int padCount) throws Exception
	{
		final int EIGHTH_OF_AN_INCH = 180;
		final int QUARTER_INCH = EIGHTH_OF_AN_INCH * 2;
		int indentInTwips = (padCount * EIGHTH_OF_AN_INCH) + QUARTER_INCH;
		writeRaw("\\fi-" + QUARTER_INCH+ "\\li" + indentInTwips + "\\tx" + indentInTwips + "\\ri0");
	}
	
	public void writeHeading1Style() throws Exception
	{
		String styleFormattingCommand = getRtfStyleManager().getStyleFormatingCommand(RtfStyleManager.HEADING_1_STYLE_TAG);
		writeRaw(styleFormattingCommand);
	}
	
	public void writeHeading2Style() throws Exception
	{
		String styleFormattingCommand = getRtfStyleManager().getStyleFormatingCommand(RtfStyleManager.HEADING_2_STYLE_TAG);
		writelnRaw(styleFormattingCommand);
	}

	public void writeHeading3Style() throws Exception
	{
		String styleFormattingCommand = getRtfStyleManager().getStyleFormatingCommand(RtfStyleManager.HEADING_3_STYLE_TAG);
		writelnRaw(styleFormattingCommand);
	}
	
	public void writeText(String text) throws Exception
	{
		writer.write(text);
		newParagraph();
	}
	
	public void startRtf() throws Exception
	{
		writeRtfHeader();
		writeFontTable();
		writeColorTable();
		writeInfo();
		getRtfStyleManager().exportRtfStyleTable(this);
	}

	private void writeRtfHeader() throws Exception
	{
		writelnRaw("{\\rtf1\\ansi\\deff0\\adeflang1025");
	}
	
	private void writeFontTable() throws Exception
	{
		newLine();
		writelnRaw("{\\fonttbl");
			writelnRaw("{\\f0\\froman\\fcharset0\\fprq2{\\*\\panose 02020603050405020304}Times New Roman;}");
			writelnRaw("{\\f1\\fswiss\\fcharset0\\fprq2{\\*\\panose 020b0604020202020204}Arial;}");
			writelnRaw("{\\f37\\froman\\fcharset238\\fprq2 Times New Roman CE;}");
			writelnRaw("{\\f38\\froman\\fcharset204\\fprq2 Times New Roman Cyr;}");
			writelnRaw("{\\f40\\froman\\fcharset161\\fprq2 Times New Roman Greek;}");
			writelnRaw("{\\f41\\froman\\fcharset162\\fprq2 Times New Roman Tur;}");
			writelnRaw("{\\f42\\fbidi \\froman\\fcharset177\\fprq2 Times New Roman (Hebrew);}");
			writelnRaw("{\\f43\\fbidi \\froman\\fcharset178\\fprq2 Times New Roman (Arabic);}");
			writelnRaw("{\\f44\\froman\\fcharset186\\fprq2 Times New Roman Baltic;}");
			writelnRaw("{\\f45\\froman\\fcharset163\\fprq2 Times New Roman (Vietnamese);}");
			writelnRaw("{\\f47\\fswiss\\fcharset238\\fprq2 Arial CE;}");
			writelnRaw("{\\f48\\fswiss\\fcharset204\\fprq2 Arial Cyr;}");
			writelnRaw("{\\f50\\fswiss\\fcharset161\\fprq2 Arial Greek;}");
			writelnRaw("{\\f51\\fswiss\\fcharset162\\fprq2 Arial Tur;}");
			writelnRaw("{\\f52\\fbidi \\fswiss\\fcharset177\\fprq2 Arial (Hebrew);}");
			writelnRaw("{\\f53\\fbidi \\fswiss\\fcharset178\\fprq2 Arial (Arabic);}");
			writelnRaw("{\\f54\\fswiss\\fcharset186\\fprq2 Arial Baltic;}");
			writelnRaw("{\\f55\\fswiss\\fcharset163\\fprq2 Arial (Vietnamese);}");
		writelnRaw("}");
	}

	private void writeColorTable() throws Exception
	{
		newLine();
		writelnRaw("{\\colortbl;" +
				"\\red0\\green0\\blue0;" +
				"\\red0\\green0\\blue255;" +
				"\\red0\\green255\\blue255;" +
				"\\red0\\green255\\blue0;" +
				"\\red255\\green0\\blue255;" +
				"\\red255\\green0\\blue0;" +
				"\\red255\\green255\\blue0;" +
				"\\red255\\green255\\blue255;" +
				"\\red0\\green0\\blue128;" +
				"\\red0\\green128\\blue128;" +
				"\\red0\\green128\\blue0;" +
				"\\red128\\green0\\blue128;" +
				"\\red128\\green0\\blue0;" +
				"\\red128\\green128\\blue0;" +
				"\\red128\\green128\\blue128;" +
				"\\red192\\green192\\blue192;}");
	}

	private void writeInfo() throws Exception
	{
		newLine();
		writelnRaw("{\\info");
			writelnRaw("{\\title Miradi}");
			writelnRaw("{\\author Miradi}");
			writelnRaw("{\\operator Miradi}");
			writelnRaw("{\\creatim\\yr2008\\mo8\\dy26\\hr15}");
			writelnRaw("{\\revtim\\yr2008\\mo8\\dy27\\hr10\\min24}");
			writelnRaw("{\\edmins140}");
			writelnRaw("{\\nofpages1}");
			writelnRaw("{\\nofwords4}");
			writelnRaw("{\\nofchars23}");
			writelnRaw("{\\nofcharsws26}");
			writelnRaw("{\\vern24613}");
		writelnRaw("}");
	}

	public void endRtf() throws Exception
	{
		endBlock();
	}	

	public void newParagraph() throws Exception
	{
		writeSingleCommand(PARAGRAPH_COMMAND);	
	}
	
	public void insertTab() throws Exception
	{
		writeRaw(TAB_COMMAND);	
	}
	
	public void pageBreak() throws Exception
	{
		writeRaw(PAGE_BREAK_COMMAND);
	}
	
	public void landscapeMode() throws Exception
	{
		writeRtfCommand(LANDSCAPE_COMMAND);
	}
	
	public void writeSingleCommand(String command) throws Exception
	{
		newLine();
		startBlock();
		writeRaw(command);
		endBlock();
		newLine();
	}
	
	public void newLine() throws Exception
	{
		writelnRaw("");
	}
	
	public UnicodeWriter getWriter()
	{
		return writer;
	}
	
	public RtfStyleManager getRtfStyleManager()
	{
		return rtfStyleManager;
	}
	
	private UnicodeWriter writer;
	private RtfStyleManager rtfStyleManager;

	public static final int ONE_INCH_IN_TWIPS = 1440;
	public static final String START_BLOCK = "{";
	public static final String END_BLOCK = "}";
	public static final String CELL_X_COMMAND = "\\cellx";
	public static final String CELL_COMMAND = "\\cell ";
	public static final String ROW_COMMAND = "\\row ";
	public static final String PARAGRAPH_COMMAND = "\\pard\\par ";
	public static final String ROW_HEADER = "\\pard \\trowd\\trql\\trpaddft3\\trpaddt55\\trpaddfl3\\trpaddl55\\trpaddfb3\\trpaddb55\\trpaddfr3\\trpaddr55 ";
	public static final String TABLE_ROW_HEADER = "\\trowd\\trql\\trhdr\\trpaddft3\\trpaddt55\\trpaddfl3\\trpaddl55\\trpaddfb3\\trpaddb55\\trpaddfr3\\trpaddr55 ";
	public static final String LANDSCAPE_COMMAND = "\\landscape\\paperh12240\\paperw15840\\margl1134\\margr1134\\margt1134\\margb1134\\sectd\\sbknone\\lndscpsxn ";
	public static final String PAGE_BREAK_COMMAND = "{\\page\\pard\\plain } ";
	public static final String BOLD_DIAGRAM_HEADER_FONT_COMMAND = "\\rtlch \\ltrch\\lang1033 ";
	public static final String TAB_COMMAND = "\\tab ";
	
	public static final String PRE_TABLE_HEADER_CELL_COMMAND = "\\intbl\\qc ";
	public static final String PRE_CELL_COMMAND  = "\\pard\\intbl\\plain ";
	public static final String PRE_TABLE_HEADER_CELL_DATA_COMMAND = "";
	public static final String CELL_BORDER = "\\clbrdrt\\brdrs\\brdrw1\\brdrcf1\\clbrdrl\\brdrs\\brdrw1\\brdrcf1\\clbrdrb\\brdrs\\brdrw1\\brdrcf1\\clbrdrr\\brdrs\\brdrw1\\brdrcf1 ";										  																	
}
