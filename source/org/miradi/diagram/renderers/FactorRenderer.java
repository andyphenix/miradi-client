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

/*
 * Portions of this file are based on work with this copyright:
 * Copyright (c) 2001-2004, Gaudenz Alder
 * All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of JGraph nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package org.miradi.diagram.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.miradi.diagram.DiagramComponent;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.EAMGraphCell;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.icons.AbstractMiradiIcon;
import org.miradi.icons.ResultsChainIcon;
import org.miradi.ids.IdList;
import org.miradi.main.AppPreferences;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.Desire;
import org.miradi.objects.Factor;
import org.miradi.objects.Goal;
import org.miradi.objects.Objective;
import org.miradi.objects.Strategy;
import org.miradi.objects.Stress;
import org.miradi.objects.Target;
import org.miradi.objects.Task;
import org.miradi.project.Project;
import org.miradi.project.threatrating.ThreatRatingFramework;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.StatusQuestion;
import org.miradi.schemas.GoalSchema;
import org.miradi.schemas.ObjectiveSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.utils.Utility;
import org.miradi.utils.XmlUtilities2;


public abstract class FactorRenderer extends MultilineCellRenderer implements CellViewRenderer
{
	@Override
	public Component getRendererComponent(JGraph graphToUse, CellView view,
			boolean sel, boolean focus, boolean previewMode)
	{
		super.getRendererComponent(graphToUse, view, sel, focus, previewMode);
		
		try
		{
			node = (FactorCell)view.getCell();
			DiagramComponent diagram = (DiagramComponent)graphToUse;
			DiagramModel model = diagram.getDiagramModel();
			ThreatRatingFramework framework = model.getThreatRatingFramework();
			priority = null;
			if(getFactorCell().isDirectThreat())
			{
				priority = framework.getThreatThreatRatingValue(getFactorCell().getWrappedFactorRef());
			}
			if(getFactorCell().isTarget() || getFactorCell().isHumanWelfareTarget())
			{
				AbstractTarget target = (AbstractTarget)getFactorCell().getWrappedFactor();
				String ratingCode = model.getProject().getObjectData(target.getRef(), Target.PSEUDO_TAG_TARGET_VIABILITY);
				StatusQuestion question = new StatusQuestion();
				rating = question.findChoiceByCode(ratingCode);
			}
			
			if(getFactorCell().isStrategy())
			{
				Strategy strategy = (Strategy)getFactorCell().getWrappedFactor();
				rating = strategy.getStrategyRating();
				stragetyInResultsChain = shouldDisplayResultsChainIcon(model, strategy);
			}
			
			isAliased = shouldMarkAsShared(model);
			isOwnedByGroup = getFactorCell().getDiagramFactor().isCoveredByGroupBox();
			
			EAMGraphCell cell = (EAMGraphCell)view.getCell();
			setHtmlFormViewerText(getAdditionalHtmlFontTags() + cell.toString());
			
			indicatorText = null;
			if(diagram.areIndicatorsVisible())
			{
				IdList indicators = getFactorCell().getWrappedFactor().getDirectOrIndirectIndicators();
				if(indicators.size() == 1)
					indicatorText = "";
				else if(indicators.size() > 1)
					indicatorText = "+";
			}
			
			setObjectiveText(diagram);
			
			setGoalText(diagram);
			
			isRelatedToSelectedFactor = checkIfRelatedToSelectedFactor(diagram, getFactorCell());
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
		
		return this;
	}

	private void setGoalText(DiagramComponent diagram) throws Exception
	{
		ORefList annotationRefs = getFactorCell().getGoalRefs();
		final String computedText = getAnnotationText(diagram, GoalSchema.getObjectType(), annotationRefs);
		goalsText = computedText;
	}

	private void setObjectiveText(DiagramComponent diagram) throws Exception
	{
		ORefList annotationRefs = getFactorCell().getObjectiveRefs();
		final String computedText = getAnnotationText(diagram, ObjectiveSchema.getObjectType(), annotationRefs);
		objectivesText = computedText;
	}

	private String getAnnotationText(final DiagramComponent diagram, int annotationType, ORefList annotationRefs) throws Exception
	{
		final Project project = diagram.getProject();
		final String objectName = project.getObjectManager().getInternalObjectTypeName(annotationType);
		if (!diagram.isTypeVisible(objectName))
		{
			return null;
		}	
		if (!getFactorCell().canHaveType(annotationType))
		{
			return null;
		}
		
		final String annotationName = getAnnotationName(annotationType, annotationRefs.size());
		if(annotationRefs.size() == 1)
		{
			String shortLabel = project.getObjectData(annotationRefs.get(0), Desire.TAG_SHORT_LABEL);
			if (shortLabel.length() > 0)
			{
				//NOTE: not formatting as html since the label cannot contain html styling
				return XmlUtilities2.convertXmlTextToPlainText(shortLabel);
			}
		}
		
		return annotationName;
	}
	
	private String getAnnotationName(int annotationType, int size)
	{
		if (Goal.is(annotationType))
		{
			if (size == 1)
				return EAM.text("1 Goal");
			
			if (size > 1)
				return EAM.substitute(EAM.text("%s Goals"), Integer.toString(size));
		}
		if (Objective.is(annotationType))
		{
			if (size == 1)
				return EAM.text("1 Obj");
			
			if (size > 1)
				return EAM.substitute(EAM.text("%s Objs"), Integer.toString(size));
		}
			
		return null;
	}
	
	private boolean checkIfRelatedToSelectedFactor(DiagramComponent diagram, FactorCell thisCell)
	{
		Factor underlyingFactor = thisCell.getWrappedFactor();
		
		if(thisCell.isStress())
			return checkIfOwningTargetIsSelected(diagram, (Stress)underlyingFactor);
		
		if(thisCell.isTarget())
			return checkIfOwnedStressIsSelected(diagram, (Target)underlyingFactor);
		
		if(thisCell.isActivity())
			return checkIfOwningStrategyIsSelected(diagram, (Task)underlyingFactor);
		
		if(thisCell.isStrategy())
			return checkIfOwnedActivityIsSelected(diagram, (Strategy)underlyingFactor);
		
		return false;
	}

	private boolean checkIfOwningStrategyIsSelected(DiagramComponent diagram, Task activity)
	{
		return areAnyOfTheseFactorsSelected(diagram, activity.findObjectsThatReferToUs(StrategySchema.getObjectType()));
	}
	
	private boolean checkIfOwningTargetIsSelected(DiagramComponent diagram, Stress stress)
	{
		return areAnyOfTheseFactorsSelected(diagram, stress.findObjectsThatReferToUs(TargetSchema.getObjectType()));
	}

	private boolean checkIfOwnedActivityIsSelected(DiagramComponent diagram, Strategy strategy)
	{
		return areAnyOfTheseFactorsSelected(diagram, strategy.getActivityRefs());
	}

	private boolean checkIfOwnedStressIsSelected(DiagramComponent diagram, Target target)
	{
		return areAnyOfTheseFactorsSelected(diagram, target.getStressRefs());
	}

	private boolean areAnyOfTheseFactorsSelected(DiagramComponent diagram, ORefList relatedRefs)
	{
		EAMGraphCell[] selectedCells = diagram.getSelectedAndRelatedCells();
		for(int i = 0; i < selectedCells.length; i++)
		{
			if(!selectedCells[i].isFactor())
				continue;
			FactorCell cell = (FactorCell)selectedCells[i];
			if(relatedRefs.contains(cell.getWrappedFactorRef()))
				return true;
		}
		return false;
	}

	private boolean shouldMarkAsShared(DiagramModel model)
	{
		boolean isSharedInCoceptualModel = model.isSharedInConceptualModel(getFactorCell().getDiagramFactor());
		if(isSharedInCoceptualModel)
			   return true;

		boolean isResultsChain = model.isResultsChain();
		if(! isResultsChain)
			 return false;
		
		return model.isSharedInResultsChain(getFactorCell().getDiagramFactor());
	}
	protected String getAdditionalHtmlFontTags()
	{
		String formatted = "<Font "+ getDiagramFactorFontColor() + ">";
		formatted += getDiagramFactorFontStyle();
		if (isAliased)
			return formatted += "<i>";
	
		return formatted;
	}
	
	private String getDiagramFactorFontColor()
	{
		String fontColor = getFactorCell().getDiagramFactor().getFontColor();
		if (fontColor.length() != 0)
			return fontColor = "color=" + fontColor;
		
		return fontColor;
	}
	
	private String getDiagramFactorFontStyle()
	{
		String fontStyle = getFactorCell().getDiagramFactor().getFontStyle();
		if (fontStyle.length() != 0)
			return fontStyle;
		
		return fontStyle;
	}

	private boolean shouldDisplayResultsChainIcon(DiagramModel model, Strategy strategy)
	{
		ORefList resultsChains = strategy.getResultsChains();
		if (model.isResultsChain())
			return  false;
		return resultsChains.size() > 0;
	}
	
	public void setRatingBubbleFont(Graphics2D g2)
	{
		g2.setFont(g2.getFont().deriveFont(9.0f).deriveFont(Font.BOLD));
	}
	
	@Override
	public void paint(Graphics g1)
	{
		int originalHeight = getSize().height;
		int originalWidth = getSize().width; 
		setSize(getSizeWithoutAnnotations(getSize()));
		super.paint(g1);
		setSize(originalWidth, originalHeight);

		Rectangle rect = getNonBorderBounds();
		Graphics2D g2 = (Graphics2D) g1;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if(objectivesText != null)
			drawAnnotationCellRect(rect, g2, new RectangleRenderer(), objectivesText);
		
		if(goalsText != null)
			drawAnnotationCellRect(rect, g2, new EllipseRenderer(), goalsText);
		
		if(indicatorText != null)
			drawIndicator(rect, g2);
		
		if(stragetyInResultsChain)
			drawChainIcon(rect, g2);
		
		drawCommentTriangle(g2, new Point(rect.width, 0));
	}
	
	public static Dimension getSizeWithoutAnnotations(Dimension size)
	{
		return new Dimension(size.width, size.height);
	}
	
	private void drawIndicator(Rectangle rect, Graphics2D g2) 
	{
		if(indicatorText == null)
			return;

		TriangleRenderer indicatorRenderer = new TriangleRenderer();
		Rectangle smallTriangle = getIndicatorRectWithinNode();
		smallTriangle.translate(rect.x, rect.y);
		setPaint(g2, smallTriangle, AppPreferences.INDICATOR_COLOR);
		indicatorRenderer.fillShape(g2, smallTriangle, AppPreferences.INDICATOR_COLOR);
		drawAnnotationBorder(g2, smallTriangle, indicatorRenderer);
		smallTriangle.setLocation(smallTriangle.x, smallTriangle.y + (INDICATOR_HEIGHT / 4));
		
		drawLabel(g2, smallTriangle, indicatorText, smallTriangle.getSize());
	}
	
	private void drawChainIcon(Rectangle rect, Graphics2D g2) 
	{
		AbstractMiradiIcon icon = new ResultsChainIcon();
		Rectangle rectangle = getResultChainRectWithinNode();
		icon.paintIcon(null, g2,rectangle.x, rectangle.y);
	}
	
	private Rectangle getResultChainRectWithinNode()
	{
		return getFactorCell().getResultChainRectWithinNode();
	}
	
	private Rectangle getIndicatorRectWithinNode()
	{
		return getFactorCell().getIndicatorRectWithinNode();
	}

	@Override
	Color getFillColor()
	{
		return getFactorCell().getColor();
	}
	
	@Override
	Dimension getInsetDimension()
	{
		return getFactorCell().getInsetDimension();
	}
	
	@Override
	Stroke getStroke()
	{
		if(!selected)
		{
			if(isRelatedToSelectedFactor)
				return getRelatedIsSelectedStroke();
			if(isOwnedByGroup)
				return getGroupMemberStroke();
		}

		return super.getStroke();
	}

	private Stroke getRelatedIsSelectedStroke()
	{
		return new BasicStroke(getSelectedStrokeWidth(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	}

	private Stroke getGroupMemberStroke()
	{
		float[] dashes = {5.0f, 5.0f};
		return new BasicStroke(borderThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dashes, 0.0f);
	}

	protected Rectangle getBubbleRect(Rectangle rect)
	{
		Rectangle smallRect = new Rectangle();
		smallRect.x = rect.x;
		if (getFactorCell().isCause())
			smallRect.y  = rect.y;
		else
			smallRect.y = getSize().height/2 - PRIORITY_HEIGHT/2;
		smallRect.width = PRIORITY_WIDTH;
		smallRect.height = PRIORITY_HEIGHT;
		return smallRect;
	}

	protected void drawRatingBubble(Graphics2D g2, Rectangle rect, Color ratingColor, String ratingText)
	{
		Rectangle smallRect = getBubbleRect(rect);
		
		Paint oldPaint = g2.getPaint();
		setPaint(g2, smallRect, ratingColor);
		g2.fill(getShape(smallRect));
		g2.setPaint(oldPaint);

		drawBorder(g2, smallRect, Color.BLACK);
		setRatingBubbleFont(g2);
		g2.setColor(Color.BLACK);
		Utility.drawStringCentered(g2, ratingText, smallRect);
	}
	
	protected void drawCommentTriangle(Graphics2D g2, Point upperRight)
	{
		if(!shouldShowCommentTriangle())
			return;
		
		final int triangleInset = 15;
		Polygon triangle = new Polygon();
		triangle.addPoint(upperRight.x, upperRight.y);
		triangle.addPoint(upperRight.x - triangleInset, upperRight.y);
		triangle.addPoint(upperRight.x, upperRight.y + triangleInset);
		triangle.addPoint(upperRight.x, upperRight.y);
		setPaint(g2, getBounds(), Color.CYAN);
		g2.fill(triangle);
		
		//Temporarly turning off border
		//setPaint(g2, rect, Color.BLACK);
		//g2.drawPolygon(triangle);
	}
	
	private boolean shouldShowCommentTriangle()
	{
		// NOTE: We have disabled comment triangles for this release
		return false;
	}
	
	protected FactorCell getFactorCell()
	{
		return node;
	}

	protected static final int PRIORITY_WIDTH = 16;
	protected static final int PRIORITY_HEIGHT = 8;
	protected ChoiceItem priority;
	private FactorCell node;
	protected ChoiceItem rating;
	private String indicatorText;
	private String objectivesText;
	private String goalsText;
	boolean stragetyInResultsChain;
	boolean isAliased;
	boolean isOwnedByGroup;
	private boolean isRelatedToSelectedFactor;
}
