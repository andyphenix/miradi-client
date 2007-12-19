/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.diagram;

import java.awt.Color;


public class DiagramConstants
{
	public static final Color GROUP_BOX_COLOR = new Color(155, 155, 155);
	public static final Color TEXT_BOX_COLOR = new Color(230, 230, 230);
	public static final Color COLOR_STRESS = new Color(150, 150, 255);
	public static final Color COLOR_DRAFT_STRATEGY = new Color(255, 255, 190);
	public static final Color COLOR_FACTOR_CLUSTER = Color.LIGHT_GRAY;
	public static final Color DEFAULT_ACTIVITIES_COLOR = new Color(255, 255, 0);
	
	public static final Color[] strategyColorChoices = {
		new Color(255, 255, 0), 
		new Color(240, 240, 0), 
		new Color(255, 255, 128)};
	public static final Color[] directThreatColorChoices = {
		new Color(255, 150, 150), 
		new Color(255, 128, 128), 
		new Color(220, 150, 150), 
		new Color(255, 200, 200)};
	public static final Color[] contributingFactorColorChoices = {
		new Color(255, 190, 0), 
		new Color(255, 128, 0), 
		new Color(200, 128, 0), 
		new Color(255, 220, 0), 
		new Color(255, 190, 64), 
		new Color(255, 240, 200)};
	public static final Color[] targetColorChoices = {
		new Color(153, 255, 153), 
		new Color(200, 255, 200), 
		new Color(80, 255, 80), 
		new Color(64, 220, 64)};
	public static final Color[] scopeColorChoices = {
		new Color(0, 255, 0), 
		new Color(128, 255, 128), 
		new Color(0, 220, 0), 
		new Color(0, 180, 0), 
		new Color(0, 128, 0)};
	public static final Color[] intermediateResultChoices = {
		new Color(100, 222, 255),
		new Color(80, 200, 220), 
		new Color(80, 200, 255), 
		new Color(200, 240, 255), 
		new Color(150, 240, 255)};
	public static final Color[] threatReductionResultChoices = {
		new Color(222, 100, 255), 
		new Color(200, 80, 220), 
		new Color(200, 80, 255), 
		new Color(240, 200, 255), 
		new Color(240, 150, 255)};
	
	public static final Color DEFAULT_TARGET_COLOR = targetColorChoices[0];
	public static final Color DEFAULT_DIRECT_THREAT_COLOR = directThreatColorChoices[0];
	public static final Color DEFAULT_CONTRIBUTING_FACTOR_COLOR = contributingFactorColorChoices[0];
	public static final Color DEFAULT_STRATEGY_COLOR = strategyColorChoices[0];
	public static final Color DEFAULT_SCOPE_COLOR = scopeColorChoices[0];
	public static final Color DEFAULT_INTERMEDIATE_RESULT_COLOR = intermediateResultChoices[0];
	public static final Color DEFAULT_THREAT_REDUCTION_RESULT_COLOR = threatReductionResultChoices[0];

}
