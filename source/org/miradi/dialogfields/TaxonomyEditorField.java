/* 
Copyright 2005-2013, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogfields;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.TaxonomyClassification;
import org.miradi.objecthelpers.TaxonomyClassificationList;
import org.miradi.objecthelpers.TaxonomyHelper;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.CodeList;

public class TaxonomyEditorField extends ObjectDataInputField implements ListSelectionListener
{
	public TaxonomyEditorField(Project projectToUse, ORef refToUse, String tagToUse, ChoiceQuestion questionToUse, String taxonomyAssociationCodeToUse)
	{
		super(projectToUse, refToUse, tagToUse);
		
		taxonomyAssociationCode = taxonomyAssociationCodeToUse;
		component = new TaxonomyEditorComponent(questionToUse);
		component.addListSelectionListener(this);
	}
	

	//FIXME urgent, getText and setText are still under construction
	@Override
	public String getText()
	{
		try
		{
			final String text = component.getText();
			CodeList selectedTaxonomyCodes = new CodeList(text);
			TaxonomyClassificationList taxonomyClassificationList = TaxonomyHelper.createTaxonomyClassificationList(getProject(), getObjectType(), taxonomyAssociationCode, selectedTaxonomyCodes);

			return taxonomyClassificationList.toJsonString();
		}
		catch (Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
			return "";
		}
	}

	@Override
	public void setText(String newValue)
	{
		try
		{
			CodeList taxonomyCodes = new CodeList();
			TaxonomyClassificationList taxononyClassificationList = new TaxonomyClassificationList(newValue);
			for(TaxonomyClassification taxonomyClassification : taxononyClassificationList)
			{
				final Vector<String> taxonomyElementCodes = taxonomyClassification.getTaxonomyElementCodes();
				taxonomyCodes.addAll(new CodeList(taxonomyElementCodes));
			}
			
			component.setText(taxonomyCodes.toString());
		}
		catch (Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
		}
	}

	@Override
	public JComponent getComponent()
	{
		return component;
	}
	
	@Override
	protected boolean shouldBeEditable()
	{
		return isValidObject();
	}
	
	public void valueChanged(ListSelectionEvent arg0)
	{
		forceSave();
	}
	
	private String taxonomyAssociationCode;
	private TaxonomyEditorComponent component;
}
